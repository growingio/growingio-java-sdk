package io.growing.sdk.java.store.impl;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.exception.GIOSendBeRejectedException;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.sender.MessageSender;
import io.growing.sdk.java.store.StoreStrategyAbstract;
import io.growing.sdk.java.thread.GioThreadNamedFactory;
import io.growing.sdk.java.utils.ConfigUtils;
import io.growing.sdk.java.utils.ExecutorServiceUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * while speed is too fast, msg will be rejected.
 *
 * @author liguobin@growingio.com
 * @version 1.0, 2020/10/29
 */
public class AbortPolicyStoreStrategy extends StoreStrategyAbstract {

    private static final int THREADS = ConfigUtils.getIntValue("send.msg.thread", 3); //网络 提交线程
    private static final int LIMIT = ConfigUtils.getIntValue("msg.store.queue.size", 500);
    private static double loadfactor = ConfigUtils.getDoubleValue("msg.store.queue.load_factor", 0.5);
    private static final int SEND_INTERVAL = ConfigUtils.getIntValue("send.msg.interval", 100);
    protected static final int SPEED_THREAD_POOL_TIMEOUT = ConfigUtils.getIntValue("speed.thread_pool.timeout", 1000);
    private static final int SEND_MSG_BATCH_SIZE = 100;

    private static final AtomicBoolean queueWillFull = new AtomicBoolean(false);
    private static final ScheduledThreadPoolExecutor speedSendScheduler = new ScheduledThreadPoolExecutor(THREADS, new GioThreadNamedFactory("gio-speed-send-msg-schedule"));
    private static final ScheduledExecutorService sendMsgSchedule = new ScheduledThreadPoolExecutor(1, new GioThreadNamedFactory("gio-send-msg-schedule"));
    private static final Map<String, List<GIOMessage>> BATCH_MSG_MAP = new ConcurrentHashMap<String, List<GIOMessage>>();

    private static final MessageSender SENDER = new FixThreadPoolSender();

    static {
        messageBlockingQueue = new ArrayBlockingQueue<GIOMessage>(LIMIT);
        sendMsgSchedule.scheduleWithFixedDelay(new SendRunnable(), SEND_INTERVAL, SEND_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * 使用新线程池加速提交，但不保证消费queue的速度足够（可能仍会抛出GIOSendBeRejectedException）.
     */
    private static void beforeFull() {
        if (loadfactor < 0 || loadfactor > 1) {
            GioLogger.error("msg.store.queue.load_factor cannot be less than 0 or greater than 1, use default value: 0.5");
            loadfactor = 0.5D;
        }
        if (messageBlockingQueue.size() > LIMIT * loadfactor) { //负载到达x%，提醒快满了，并标记为将满
            queueWillFull.compareAndSet(false, true);
            GioLogger.debug("msg queue is almost full");
        } else {
            queueWillFull.compareAndSet(true, false);
        }
        if (queueWillFull.get()) {
            //一次性定时任务，等待 (1 - DEFAULT_LOAD_FACTOR) * SEND_INTERVAL) / 10 ms (此等待必须远小于SEND_INTERVAL，否则无效果)
            //这是因为定时器创建后就无法修改delay的值，所以这里使用第二个一次性定时器加速
            speedSendScheduler.schedule(new SendRunnable(), (long) ((1 - loadfactor) * SEND_INTERVAL) / 10, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void doPush(GIOMessage msg) {
        if (messageBlockingQueue.offer(msg)) {
            beforeFull();
        } else {
            throw new GIOSendBeRejectedException("push was rejected, because msg queue is full, suggest greater size for [msg.store.queue.size] or shorten the interval of [send.msg.interval]");
        }
    }

    @Override
    public void awaitTerminationAfterShutdown() {
        ExecutorServiceUtils.awaitTerminationAfterShutdown(pushMsgThreadPool, PUSH_THREAD_POOL_TIMEOUT);

        if (!messageBlockingQueue.isEmpty()) {
            GioLogger.error("awaitTerminationAfterShutdown was executed, msg queue size: " + messageBlockingQueue.size() + " is not empty, will wait it " + AWAIT + "s");
            try {
                TimeUnit.SECONDS.sleep(AWAIT);
            } catch (InterruptedException e) {
                GioLogger.error(e.getLocalizedMessage());
            }
        }

        ExecutorServiceUtils.awaitTerminationAfterShutdown(sendMsgSchedule, SEND_THREAD_POOL_TIMEOUT);
        ExecutorServiceUtils.awaitTerminationAfterShutdown(speedSendScheduler, SPEED_THREAD_POOL_TIMEOUT);
        SENDER.awaitTermination(SENDER_THREAD_POOL_TIMEOUT);
    }

    @Override
    public void shutDownNow() {
        pushMsgThreadPool.shutdownNow();
        sendMsgSchedule.shutdownNow();
        speedSendScheduler.shutdownNow();
        SENDER.shutdownNow();
    }

    static class SendRunnable implements Runnable {

        private int currentBatchMsgSize() {
            int size = 0;
            Collection<List<GIOMessage>> values = BATCH_MSG_MAP.values();
            for (List<GIOMessage> msgList : values) {
                size += msgList.size();
            }
            return size;
        }

        @Override
        public void run() {
            while (!messageBlockingQueue.isEmpty()) {
                if (currentBatchMsgSize() < SEND_MSG_BATCH_SIZE) {
                    GIOMessage gioMessage = messageBlockingQueue.poll();
                    if (gioMessage != null) {
                        String projectKey = gioMessage.getProjectKey();
                        if (BATCH_MSG_MAP.containsKey(projectKey)) {
                            List<GIOMessage> list = BATCH_MSG_MAP.get(projectKey);
                            list.add(gioMessage);
                        } else {
                            List<GIOMessage> list = new ArrayList<GIOMessage>();
                            list.add(gioMessage);
                            BATCH_MSG_MAP.put(projectKey, list);
                        }
                    }
                } else {
                    break;
                }
            }

            for (Map.Entry<String, List<GIOMessage>> entry : BATCH_MSG_MAP.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    SENDER.sendMsg(entry.getKey(), BATCH_MSG_MAP.remove(entry.getKey()));
                }
            }
        }
    }
}