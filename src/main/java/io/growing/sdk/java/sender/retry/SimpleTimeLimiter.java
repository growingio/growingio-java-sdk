package io.growing.sdk.java.sender.retry;

import java.lang.reflect.Array;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class SimpleTimeLimiter {

    private final ExecutorService executor;

    public SimpleTimeLimiter(ExecutorService executor) {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }
        this.executor = executor;
    }

    public SimpleTimeLimiter() {
        this(Executors.newSingleThreadExecutor());
    }

    public <T> T callWithTimeout(
            Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit, boolean amInterruptible)
            throws Exception {
        Future<T> future = executor.submit(callable);
        try {
            if (amInterruptible) {
                try {
                    return future.get(timeoutDuration, timeoutUnit);
                } catch (InterruptedException e) {
                    future.cancel(true);
                    throw e;
                }
            } else {
                return getUninterruptibly(future, timeoutDuration, timeoutUnit);
            }
        } catch (ExecutionException e) {
            throw throwCause(e, true);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        }
    }

    private static <V extends Object> V getUninterruptibly(
            Future<V> future, long timeout, TimeUnit unit) throws ExecutionException, TimeoutException {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;

            while (true) {
                try {
                    // Future treats negative timeouts just like zero.
                    return future.get(remainingNanos, NANOSECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }


    private static Exception throwCause(Exception e, boolean combineStackTraces) throws Exception {
        Throwable cause = e.getCause();
        if (cause == null) {
            throw e;
        }
        if (combineStackTraces) {
            StackTraceElement[] combined =
                    concat(cause.getStackTrace(), e.getStackTrace(), StackTraceElement.class);
            cause.setStackTrace(combined);
        }
        if (cause instanceof Exception) {
            throw (Exception) cause;
        }
        if (cause instanceof Error) {
            throw (Error) cause;
        }
        // The cause is a weird kind of Throwable, so throw the outer exception.
        throw e;
    }

    private static <T extends Object> T[] concat(
            T[] first, T[] second, Class<T> type) {
        T[] result = (T[]) Array.newInstance(type, first.length + second.length);
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
