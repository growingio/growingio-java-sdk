package io.growing.sdk.java.store;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.process.EventProcessorClient;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.thread.GioThreadNamedFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 5:26 PM
 */
public abstract class StoreStrategyAbstract implements StoreStrategy {

    protected static ExecutorService pushMsgThreadPool = Executors.newSingleThreadExecutor(new GioThreadNamedFactory("gio-push-msg"));

    @Override
    public void push(final GIOMessage msg) {
        pushMsgThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                MessageProcessor processor = EventProcessorClient.getApiInstance(msg);
                if (processor.skipIllegalMessage(msg) != null) {
                    doPush(msg);
                }
            }
        });
    }

    public abstract void doPush(GIOMessage msg);
}