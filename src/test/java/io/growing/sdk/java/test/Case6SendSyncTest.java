package io.growing.sdk.java.test;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.sender.SendResult;
import io.growing.sdk.java.sender.retry.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class Case6SendSyncTest {
    private static final String PROJECT_KEY = "91eaf9b283361032";
    private static final String DATASOURCE_ID = "a390a68c7b25638c";
    private static GrowingAPI sender = new GrowingAPI.Builder().setDataSourceId(DATASOURCE_ID).setProjectKey(PROJECT_KEY).build();

    @Test
    public void SendSyncTest() throws InterruptedException {
        SendResult sendResult = sender.sendSync(new GioCdpEventMessage.Builder()
                .eventKey("simple")
                .anonymousId("anonymousId")
                .addEventVariable("key1", "value1")
                .build());
        Assert.assertEquals(sendResult.getState(), SendResult.State.SUCCESS);
    }

    @Test
    public void CompositeFeatureTest() {
        int attemptCount = 3;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
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
                    SendResult result = sender.sendSync(new GioCdpEventMessage.Builder()
                            .eventKey("simple")
                            .anonymousId("anonymousId")
                            .addEventVariable("key1", "value1")
                            .build());
                    if (result.getState() == SendResult.State.SUCCESS) {
                        return false;
                    }
                    return true;
                }
            });
        } catch (ExecutionException e) {
        } catch (RetryException e) {
        }
    }
}
