package io.growing.sdk.java.sender.retry;

import java.util.concurrent.TimeUnit;

public final class StopStrategies {

    private static final StopStrategy NEVER_STOP = new NeverStopStrategy();

    private StopStrategies() {
    }

    // 永不停止策略
    public static StopStrategy neverStop() {
        return NEVER_STOP;
    }

    // 重试次数停止策略
    public static StopStrategy stopAfterAttempt(int attemptNumber) {
        return new StopAfterAttemptStrategy(attemptNumber);
    }

    // 超时停止策略
    public static StopStrategy stopAfterDelay(long delayInMillis) {
        return stopAfterDelay(delayInMillis, TimeUnit.MILLISECONDS);
    }

    public static StopStrategy stopAfterDelay(long duration, TimeUnit timeUnit) {
        if (timeUnit == null) {
            timeUnit = TimeUnit.MILLISECONDS;
        }
        return new StopAfterDelayStrategy(timeUnit.toMillis(duration));
    }

    private static final class NeverStopStrategy implements StopStrategy {
        @Override
        public boolean shouldStop(Attempt failedAttempt) {
            return false;
        }
    }

    private static final class StopAfterAttemptStrategy implements StopStrategy {
        private final int maxAttemptNumber;

        public StopAfterAttemptStrategy(int maxAttemptNumber) {
            if (maxAttemptNumber < 1) {
                throw new IllegalArgumentException("maxAttemptNumber must be greater than 0");
            }
            this.maxAttemptNumber = maxAttemptNumber;
        }

        @Override
        public boolean shouldStop(Attempt failedAttempt) {
            return failedAttempt.getAttemptNumber() >= maxAttemptNumber;
        }
    }

    private static final class StopAfterDelayStrategy implements StopStrategy {
        private final long maxDelay;

        public StopAfterDelayStrategy(long maxDelay) {
            if (maxDelay < 0L) {
                throw new IllegalArgumentException("maxDelay must be greater than 0");
            }
            this.maxDelay = maxDelay;
        }

        @Override
        public boolean shouldStop(Attempt failedAttempt) {
            return failedAttempt.getDelaySinceFirstAttempt() >= maxDelay;
        }
    }
}
