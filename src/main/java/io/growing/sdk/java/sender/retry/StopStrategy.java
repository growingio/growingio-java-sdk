package io.growing.sdk.java.sender.retry;

public interface StopStrategy {
    boolean shouldStop(Attempt failedAttempt);
}
