package io.growing.sdk.java.test;

import io.growing.sdk.java.sender.retry.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.*;

public class Case5RetryPolicyTest {

    @Test
    public void AttemptTest() throws InterruptedException {
        int attemptCount = 3;
        final CountDownLatch countDownLatch = new CountDownLatch(attemptCount);
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfException()
                .retryIfResult(new Predicate<Boolean>() {
                    @Override
                    public boolean apply(Boolean input) {
                        return input;
                    }
                })
                // 最多调用3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(attemptCount))
                .build();

        try {
            retryer.call(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    countDownLatch.countDown();
                    // 触发重试
                    return true;
                }
            });
        } catch (ExecutionException e) {
        } catch (RetryException e) {
        }

        countDownLatch.await();
    }

    @Test
    public void ExceptionTest() throws InterruptedException {
        int attemptCount = 3;
        final CountDownLatch countDownLatch = new CountDownLatch(attemptCount);
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfExceptionOfType(IOException.class)
                // 最多调用3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(attemptCount))
                .build();

        try {
            retryer.call(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    countDownLatch.countDown();
                    // 触发重试
                    throw new IOException();
                }
            });
        } catch (ExecutionException e) {
        } catch (RetryException e) {
        }

        countDownLatch.await();
    }

    @Test
    public void TimeLimitTest() throws InterruptedException {
        int attemptCount = 3;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .withAttemptTimeLimiter(AttemptTimeLimiters.<Boolean>fixedTimeLimit(1000, TimeUnit.MILLISECONDS))
                .retryIfExceptionOfType(IOException.class)
                // 最多调用3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(attemptCount))
                .build();

        try {
            retryer.call(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Thread.sleep(2000);
                    // 触发重试
                    throw new IOException();
                }
            });
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getCause().getClass().isAssignableFrom(TimeoutException.class));
            countDownLatch.countDown();
        } catch (RetryException e) {
        }
        countDownLatch.await();
    }
}
