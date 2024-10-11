package io.growing.sdk.java.sender.retry;

public class RetryerBuilder<V> {

    private AttemptTimeLimiter<V> attemptTimeLimiter;
    private StopStrategy stopStrategy;
    private WaitStrategy waitStrategy;
    private BlockStrategy blockStrategy;
    private Predicate<Attempt<V>> rejectionPredicate = new Predicate<Attempt<V>>() {
        @Override
        public boolean apply(Attempt<V> input) {
            return false;
        }
    };

    private RetryerBuilder() {
    }

    public static <V> RetryerBuilder<V> newBuilder() {
        return new RetryerBuilder<V>();
    }

    public RetryerBuilder<V> withWaitStrategy(WaitStrategy waitStrategy) throws IllegalStateException {
        if (waitStrategy == null) {
            throw new IllegalArgumentException("waitStrategy must not be null");
        }
        if (this.waitStrategy != null) {
            throw new IllegalStateException("waitStrategy already set");
        }
        this.waitStrategy = waitStrategy;
        return this;
    }

    public RetryerBuilder<V> withStopStrategy(StopStrategy stopStrategy) throws IllegalStateException {
        if (stopStrategy == null) {
            throw new IllegalArgumentException("stopStrategy must not be null");
        }
        if (this.stopStrategy != null) {
            throw new IllegalStateException("stopStrategy already set");
        }
        this.stopStrategy = stopStrategy;
        return this;
    }

    public RetryerBuilder<V> withBlockStrategy(BlockStrategy blockStrategy) throws IllegalStateException {
        if (blockStrategy == null) {
            throw new IllegalArgumentException("blockStrategy must not be null");
        }
        if (this.blockStrategy != null) {
            throw new IllegalStateException("blockStrategy already set");
        }
        this.blockStrategy = blockStrategy;
        return this;
    }

    public RetryerBuilder<V> withAttemptTimeLimiter(AttemptTimeLimiter<V> attemptTimeLimiter) {
        if (attemptTimeLimiter == null) {
            throw new IllegalArgumentException("attemptTimeLimiter must not be null");
        }
        this.attemptTimeLimiter = attemptTimeLimiter;
        return this;
    }

    public RetryerBuilder<V> retryIfException() {
        rejectionPredicate = Predicates.or(rejectionPredicate, new ExceptionClassPredicate<V>(Exception.class));
        return this;
    }

    public RetryerBuilder<V> retryIfRuntimeException() {
        rejectionPredicate = Predicates.or(rejectionPredicate, new ExceptionClassPredicate<V>(RuntimeException.class));
        return this;
    }

    public RetryerBuilder<V> retryIfExceptionOfType(Class<? extends Throwable> exceptionClass) {
       if (exceptionClass == null) {
           throw new IllegalArgumentException("exceptionClass must not be null");
       }
        rejectionPredicate = Predicates.or(rejectionPredicate, new ExceptionClassPredicate<V>(exceptionClass));
        return this;
    }

    public RetryerBuilder<V> retryIfException(Predicate<Throwable> exceptionPredicate) {
        if (exceptionPredicate == null) {
            throw new IllegalArgumentException("exceptionPredicate must not be null");
        }
        rejectionPredicate = Predicates.or(rejectionPredicate, new ExceptionPredicate<V>(exceptionPredicate));
        return this;
    }

    public RetryerBuilder<V> retryIfResult(Predicate<V> resultPredicate) {
        if (resultPredicate == null) {
            throw new IllegalArgumentException("resultPredicate must not be null");
        }
        rejectionPredicate = Predicates.or(rejectionPredicate, new ResultPredicate<V>(resultPredicate));
        return this;
    }

    public Retryer<V> build() {
        AttemptTimeLimiter<V> theAttemptTimeLimiter = attemptTimeLimiter == null ? AttemptTimeLimiters.<V>noTimeLimit() : attemptTimeLimiter;
        StopStrategy theStopStrategy = stopStrategy == null ? StopStrategies.neverStop() : stopStrategy;
        WaitStrategy theWaitStrategy = waitStrategy == null ? WaitStrategies.noWait() : waitStrategy;
        BlockStrategy theBlockStrategy = blockStrategy == null ? BlockStrategies.threadSleepStrategy() : blockStrategy;

        return new Retryer<V>(theAttemptTimeLimiter, theStopStrategy, theWaitStrategy, theBlockStrategy, rejectionPredicate);
    }

    private static final class ExceptionClassPredicate<V> implements Predicate<Attempt<V>> {

        private Class<? extends Throwable> exceptionClass;

        public ExceptionClassPredicate(Class<? extends Throwable> exceptionClass) {
            this.exceptionClass = exceptionClass;
        }

        @Override
        public boolean apply(Attempt<V> attempt) {
            if (!attempt.hasException()) {
                return false;
            }
            return exceptionClass.isAssignableFrom(attempt.getExceptionCause().getClass());
        }
    }

    private static final class ExceptionPredicate<V> implements Predicate<Attempt<V>> {

        private Predicate<Throwable> delegate;

        public ExceptionPredicate(Predicate<Throwable> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean apply(Attempt<V> attempt) {
            if (!attempt.hasException()) {
                return false;
            }
            return delegate.apply(attempt.getExceptionCause());
        }
    }

    private static final class ResultPredicate<V> implements Predicate<Attempt<V>> {

        private Predicate<V> delegate;

        public ResultPredicate(Predicate<V> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean apply(Attempt<V> attempt) {
            if (!attempt.hasResult()) {
                return false;
            }
            V result = attempt.getResult();
            return delegate.apply(result);
        }
    }
}
