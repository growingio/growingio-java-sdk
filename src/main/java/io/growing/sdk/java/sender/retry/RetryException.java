package io.growing.sdk.java.sender.retry;

public final class RetryException extends Exception {

    private final int numberOfFailedAttempts;
    private final Attempt<?> lastFailedAttempt;

    public RetryException(int numberOfFailedAttempts, Attempt<?> lastFailedAttempt) {
        this("Retrying failed to complete successfully after " + numberOfFailedAttempts + " attempts.", numberOfFailedAttempts, lastFailedAttempt);
    }

    public RetryException(String message, int numberOfFailedAttempts, Attempt<?> lastFailedAttempt) {
        super(message, lastFailedAttempt != null && lastFailedAttempt.hasException() ? lastFailedAttempt.getExceptionCause() : null);
        this.numberOfFailedAttempts = numberOfFailedAttempts;
        this.lastFailedAttempt = lastFailedAttempt;
    }
    public int getNumberOfFailedAttempts() {
        return numberOfFailedAttempts;
    }

    public Attempt<?> getLastFailedAttempt() {
        return lastFailedAttempt;
    }
}