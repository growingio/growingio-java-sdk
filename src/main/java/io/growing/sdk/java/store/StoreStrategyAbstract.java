package io.growing.sdk.java.store;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.thread.GioThreadNamedFactory;
import io.growing.sdk.java.utils.ConfigUtils;
import io.growing.sdk.java.validate.MsgValidate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
                GIOMessage validated = MsgValidate.validate(msg);
                if (validated != null) {
                    doPush(validated);
                }
            }
        });
    }

    public abstract void doPush(GIOMessage msg);
}