package io.growing.sdk.java.sender.retry;

import java.util.concurrent.ExecutionException;

public interface Attempt<V> {

    int getAttemptNumber();

    long getDelaySinceFirstAttempt();

    boolean hasException();

    Throwable getExceptionCause() throws IllegalStateException;

    boolean hasResult();

    V getResult() throws IllegalStateException;

    V get() throws ExecutionException;
}
