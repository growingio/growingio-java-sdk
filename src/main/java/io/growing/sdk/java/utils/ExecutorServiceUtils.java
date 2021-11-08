package io.growing.sdk.java.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceUtils {

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool, long timeout) {
        awaitTerminationAfterShutdown(threadPool, timeout, TimeUnit.MILLISECONDS);
    }

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool, long timeout, TimeUnit unit) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(timeout, unit)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
