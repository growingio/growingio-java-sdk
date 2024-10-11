package io.growing.sdk.java.sender.retry;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class AttemptTimeLimiters {

    private AttemptTimeLimiters() {
    }

    public static <V> AttemptTimeLimiter<V> noTimeLimit() {
        return new NoAttemptTimeLimit<V>();
    }

    public static <V> AttemptTimeLimiter<V> fixedTimeLimit(long duration, TimeUnit timeUnit) {
        if (timeUnit == null) {
            timeUnit = TimeUnit.MILLISECONDS;
        }
        return new FixedAttemptTimeLimit<V>(duration, timeUnit);
    }

    public static <V> AttemptTimeLimiter<V> fixedTimeLimit(long duration, TimeUnit timeUnit, ExecutorService executorService) {
        if (timeUnit == null) {
            timeUnit = TimeUnit.MILLISECONDS;
        }
        return new FixedAttemptTimeLimit<V>(duration, timeUnit, executorService);
    }

    // 没有时间限制的单次调用策略
    private static final class NoAttemptTimeLimit<V> implements AttemptTimeLimiter<V> {
        @Override
        public V call(Callable<V> callable) throws Exception {
            return callable.call();
        }
    }

    // 固定时间限制的单次调用策略
    private static final class FixedAttemptTimeLimit<V> implements AttemptTimeLimiter<V> {

        private final SimpleTimeLimiter timeLimiter;
        private final long duration;
        private final TimeUnit timeUnit;

        public FixedAttemptTimeLimit(long duration, TimeUnit timeUnit) {
            this(new SimpleTimeLimiter(), duration, timeUnit);
        }

        public FixedAttemptTimeLimit(long duration, TimeUnit timeUnit, ExecutorService executorService) {
            this(new SimpleTimeLimiter(executorService), duration, timeUnit);
        }

        private FixedAttemptTimeLimit(SimpleTimeLimiter timeLimiter, long duration, TimeUnit timeUnit) {
            if (timeLimiter == null) {
                throw new IllegalArgumentException("timeLimiter must not be null");
            }
            if (timeUnit == null) {
                timeUnit = TimeUnit.MILLISECONDS;
            }
            this.timeLimiter = timeLimiter;
            this.duration = duration;
            this.timeUnit = timeUnit;
        }

        @Override
        public V call(Callable<V> callable) throws Exception {
            return timeLimiter.callWithTimeout(callable, duration, timeUnit, true);
        }
    }
}
