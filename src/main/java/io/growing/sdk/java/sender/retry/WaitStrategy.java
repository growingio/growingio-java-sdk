package io.growing.sdk.java.sender.retry;

public interface WaitStrategy {

    long computeSleepTime(Attempt failedAttempt);
}
