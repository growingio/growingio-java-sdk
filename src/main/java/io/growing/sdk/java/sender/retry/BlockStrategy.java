package io.growing.sdk.java.sender.retry;

public interface BlockStrategy {
    void block(long sleepTime) throws InterruptedException;
}
