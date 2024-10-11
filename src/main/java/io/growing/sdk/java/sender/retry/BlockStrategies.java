package io.growing.sdk.java.sender.retry;

public class BlockStrategies {
    private static final BlockStrategy THREAD_SLEEP_STRATEGY = new ThreadSleepStrategy();

    private BlockStrategies() {
    }

    public static BlockStrategy threadSleepStrategy() {
        return THREAD_SLEEP_STRATEGY;
    }

    private static class ThreadSleepStrategy implements BlockStrategy {

        @Override
        public void block(long sleepTime) throws InterruptedException {
            Thread.sleep(sleepTime);
        }
    }
}
