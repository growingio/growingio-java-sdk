package io.growing.sdk.java.sender.retry;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WaitStrategies {

    private static final WaitStrategy NO_WAIT_STRATEGY = new FixedWaitStrategy(0L);

    private WaitStrategies() {
    }

    // 不等待，直接重试策略
    public static WaitStrategy noWait() {
        return NO_WAIT_STRATEGY;
    }

    public static WaitStrategy fixedWait(long sleepTime, TimeUnit timeUnit) throws IllegalStateException {
        if (timeUnit == null) {
            timeUnit = TimeUnit.MILLISECONDS;
        }
        return new FixedWaitStrategy(timeUnit.toMillis(sleepTime));
    }

    public static WaitStrategy randomWait(long maximumTime, TimeUnit timeUnit) {
        if (timeUnit == null) {
            timeUnit = TimeUnit.MILLISECONDS;
        }
        return new RandomWaitStrategy(0L, timeUnit.toMillis(maximumTime));
    }

    public static WaitStrategy randomWait(long minimumTime,
                                          TimeUnit minimumTimeUnit,
                                          long maximumTime,
                                          TimeUnit maximumTimeUnit) {
        if (minimumTimeUnit == null) {
            minimumTimeUnit = TimeUnit.MILLISECONDS;
        }
        if (maximumTimeUnit == null) {
            maximumTimeUnit = TimeUnit.MILLISECONDS;
        }
        return new RandomWaitStrategy(minimumTimeUnit.toMillis(minimumTime),
                maximumTimeUnit.toMillis(maximumTime));
    }

    public static WaitStrategy incrementingWait(long initialSleepTime,
                                                TimeUnit initialSleepTimeUnit,
                                                long increment,
                                                TimeUnit incrementTimeUnit) {
        if (initialSleepTimeUnit == null) {
            initialSleepTimeUnit = TimeUnit.MILLISECONDS;
        }
        if (incrementTimeUnit == null) {
            incrementTimeUnit = TimeUnit.MILLISECONDS;
        }
        return new IncrementingWaitStrategy(initialSleepTimeUnit.toMillis(initialSleepTime),
                incrementTimeUnit.toMillis(increment));
    }

    public static WaitStrategy exponentialWait() {
        return new ExponentialWaitStrategy(1, Long.MAX_VALUE);
    }

    public static WaitStrategy exponentialWait(long multiplier,
                                               long maximumTime,
                                               TimeUnit maximumTimeUnit) {
        if (maximumTimeUnit == null) {
            maximumTimeUnit = TimeUnit.MILLISECONDS;
        }
        return new ExponentialWaitStrategy(multiplier, maximumTimeUnit.toMillis(maximumTime));
    }

    public static WaitStrategy fibonacciWait() {
        return new FibonacciWaitStrategy(1, Long.MAX_VALUE);
    }

    public static WaitStrategy fibonacciWait(long maximumTime,
                                             TimeUnit maximumTimeUnit) {
        if (maximumTimeUnit == null) {
            maximumTimeUnit = TimeUnit.MILLISECONDS;
        }
        return new FibonacciWaitStrategy(1, maximumTimeUnit.toMillis(maximumTime));
    }

    public static WaitStrategy fibonacciWait(long multiplier,
                                             long maximumTime,
                                             TimeUnit maximumTimeUnit) {
        if (maximumTimeUnit == null) {
            maximumTimeUnit = TimeUnit.MILLISECONDS;
        }
        return new FibonacciWaitStrategy(multiplier, maximumTimeUnit.toMillis(maximumTime));
    }

    public static <T extends Throwable> WaitStrategy exceptionWait(Class<T> exceptionClass,
                                                                   ExceptionWaitStrategyFunction<T, Long> function) {
        if (exceptionClass == null) {
            throw new IllegalArgumentException("exceptionClass must not be null");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }
        return new ExceptionWaitStrategy<T>(exceptionClass, function);
    }

    public static WaitStrategy join(WaitStrategy... waitStrategies) {
        if (waitStrategies == null || waitStrategies.length == 0) {
            throw new IllegalArgumentException("waitStrategies must not be null");
        }
        List<WaitStrategy> waitStrategyList = convertToList(waitStrategies);
        if (waitStrategyList.contains(null)) {
            throw new IllegalArgumentException("waitStrategies must not contain null");
        }
        return new CompositeWaitStrategy(waitStrategyList);
    }

    private static <E> List<E> convertToList(E... elements) {
        List<E> list = new ArrayList<E>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    // 固定等待时间策略
    private static final class FixedWaitStrategy implements WaitStrategy {
        private final long sleepTime;

        public FixedWaitStrategy(long sleepTime) {
            if (sleepTime < 0) {
                throw new IllegalArgumentException("sleepTime must be greater than or equal to 0");
            }
            this.sleepTime = sleepTime;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            return sleepTime;
        }
    }

    // 随机区间等待时间策略
    private static final class RandomWaitStrategy implements WaitStrategy {
        private static final Random RANDOM = new Random();
        private final long minimum;
        private final long maximum;

        public RandomWaitStrategy(long minimum, long maximum) {
            if (minimum < 0) {
                throw new IllegalArgumentException("minimum must be greater than 0");
            }
            if (maximum <= minimum) {
                throw new IllegalArgumentException("minimum must be less than maximum");
            }

            this.minimum = minimum;
            this.maximum = maximum;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            long t = Math.abs(RANDOM.nextLong()) % (maximum - minimum);
            return t + minimum;
        }
    }

    // 指定步长增长等待时间策略
    private static final class IncrementingWaitStrategy implements WaitStrategy {
        private final long initialSleepTime;
        private final long increment;

        public IncrementingWaitStrategy(long initialSleepTime,
                                        long increment) {
            if (initialSleepTime < 0) {
                throw new IllegalArgumentException("initialSleepTime must be greater than or equal to 0");
            }
            this.initialSleepTime = initialSleepTime;
            this.increment = increment;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            long result = initialSleepTime + (increment * (failedAttempt.getAttemptNumber() - 1));
            return result >= 0L ? result : 0L;
        }
    }

    // 指数系数增长等待时长策略
    private static final class ExponentialWaitStrategy implements WaitStrategy {
        private final long multiplier;
        private final long maximumWait;

        public ExponentialWaitStrategy(long multiplier,
                                       long maximumWait) {
            if (multiplier <= 0) {
                throw new IllegalArgumentException("multiplier must be greater or equal to 0");
            }
            if (maximumWait < 0) {
                throw new IllegalArgumentException("maximumWait must be greater than 0");
            }
            if (multiplier >= maximumWait) {
                throw new IllegalArgumentException("multiplier must be less than maximumWait");
            }

            this.multiplier = multiplier;
            this.maximumWait = maximumWait;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            double exp = Math.pow(2, failedAttempt.getAttemptNumber());
            long result = Math.round(multiplier * exp);
            if (result > maximumWait) {
                result = maximumWait;
            }
            return result >= 0L ? result : 0L;
        }
    }

    // 斐波那契数列系数增长等待时长策略
    private static final class FibonacciWaitStrategy implements WaitStrategy {
        private final long multiplier;
        private final long maximumWait;

        public FibonacciWaitStrategy(long multiplier, long maximumWait) {
            if (multiplier <= 0) {
                throw new IllegalArgumentException("multiplier must be greater than 0");
            }
            if (maximumWait < 0) {
                throw new IllegalArgumentException("maximumWait must be greater than or equal to 0");
            }
            if (multiplier >= maximumWait) {
                throw new IllegalArgumentException("multiplier must be less than maximumWait");
            }

            this.multiplier = multiplier;
            this.maximumWait = maximumWait;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            long fib = fib(failedAttempt.getAttemptNumber());
            long result = multiplier * fib;

            if (result > maximumWait || result < 0L) {
                result = maximumWait;
            }

            return result >= 0L ? result : 0L;
        }

        private long fib(long n) {
            if (n == 0L) return 0L;
            if (n == 1L) return 1L;

            long prevPrev = 0L;
            long prev = 1L;
            long result = 0L;

            for (long i = 2L; i <= n; i++) {
                result = prev + prevPrev;
                prevPrev = prev;
                prev = result;
            }

            return result;
        }
    }

    // 组合 等待时长策略
    private static final class CompositeWaitStrategy implements WaitStrategy {
        private final List<WaitStrategy> waitStrategies;

        public CompositeWaitStrategy(List<WaitStrategy> waitStrategies) {
            if (waitStrategies == null || waitStrategies.isEmpty()) {
                throw new IllegalArgumentException("waitStrategies must not be null or empty");
            }
            this.waitStrategies = waitStrategies;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            long waitTime = 0L;
            for (WaitStrategy waitStrategy : waitStrategies) {
                waitTime += waitStrategy.computeSleepTime(failedAttempt);
            }
            return waitTime;
        }
    }

    // 根据触发的异常决定等待时长，未命中异常则返回0L作为等待时长
    private static final class ExceptionWaitStrategy<T extends Throwable> implements WaitStrategy {
        private final Class<T> exceptionClass;
        private final ExceptionWaitStrategyFunction<T, Long> function;

        public ExceptionWaitStrategy(Class<T> exceptionClass, ExceptionWaitStrategyFunction<T, Long> function) {
            this.exceptionClass = exceptionClass;
            this.function = function;
        }

        @Override
        public long computeSleepTime(Attempt lastAttempt) {
            if (lastAttempt.hasException()) {
                Throwable cause = lastAttempt.getExceptionCause();
                if (exceptionClass.isAssignableFrom(cause.getClass())) {
                    return function.apply((T) cause);
                }
            }
            return 0L;
        }
    }

    public interface ExceptionWaitStrategyFunction<T, R> {
        R apply(T t);
    }
}
