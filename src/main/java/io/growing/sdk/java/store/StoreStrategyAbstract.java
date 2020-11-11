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

    private static ExecutorService pushMsgThreadPool = Executors.newSingleThreadExecutor(new GioThreadNamedFactory("gio-push-msg"));
    private static final int AWAIT = ConfigUtils.getIntValue("shutdown.await", 3);

    protected static BlockingQueue<GIOMessage> messageBlockingQueue;

    @Override
    public void push(final GIOMessage msg) {
        Future<?> resultFuture = pushMsgThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                MessageProcessor processor = EventProcessorClient.getApiInstance(msg);
                if (processor.skipIllegalMessage(msg) != null) {
                    doPush(msg);
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
    public void shutdownAwait() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (!messageBlockingQueue.isEmpty()) {
                    GioLogger.error("JVM hook was executed, msg queue size: " + messageBlockingQueue.size() + " is not empty, will wait it " + AWAIT + "s");
                    try {
                        TimeUnit.SECONDS.sleep(AWAIT);
                    } catch (InterruptedException e) {
                        GioLogger.error(e.getLocalizedMessage());
                    }
                }
            }
        }));
    }
}