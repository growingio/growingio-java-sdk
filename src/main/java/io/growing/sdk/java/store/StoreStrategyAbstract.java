package io.growing.sdk.java.store;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.exception.GIOSendBeRejectedException;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.process.EventProcessorClient;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.thread.GioThreadNamedFactory;
import io.growing.sdk.java.utils.ConfigUtils;

import java.util.concurrent.*;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 5:26 PM
 */
public abstract class StoreStrategyAbstract implements StoreStrategy {

    protected static ExecutorService pushMsgThreadPool = Executors.newSingleThreadExecutor(new GioThreadNamedFactory("gio-push-msg"));
    protected static final int AWAIT = ConfigUtils.getIntValue("shutdown.await", 3);
    protected static final int PUSH_THREAD_POOL_TIMEOUT = ConfigUtils.getIntValue("push.thread_pool.timeout", 1000);
    protected static final int SEND_THREAD_POOL_TIMEOUT = ConfigUtils.getIntValue("send.thread_pool.timeout", 1000);
    protected static final int SENDER_THREAD_POOL_TIMEOUT = ConfigUtils.getIntValue("sender.thread_pool.timeout", 1000);

    protected static BlockingQueue<GIOMessage> messageBlockingQueue;

    @Override
    public void push(final GIOMessage msg) {
        Future<?> resultFuture = pushMsgThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                MessageProcessor processor = EventProcessorClient.getApiInstance(msg);
                if (!processor.skipIllegalMessage(msg)) {
                    doPush(msg);
                } else {
                    GioLogger.error("skip illegal message");
                }
            }
        });

        try {
            resultFuture.get();
        } catch (ExecutionException e) {
            //抛出来的是ExecutionException，转化为GIOSendBeRejectedException，并且不会捕获此异常
            if (e.getCause() instanceof GIOSendBeRejectedException) {
                throw (GIOSendBeRejectedException) e.getCause();
            } else {
                GioLogger.error(e.getLocalizedMessage());
            }
        } catch (Exception e) {
            GioLogger.error(e.getLocalizedMessage());
        }
    }

    public abstract void doPush(GIOMessage msg);

    /**
     * 最后在调用此方法，以避免关闭时，queue没有被消费完.
     */
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                awaitTerminationAfterShutdown();
            }
        }));
    }
}