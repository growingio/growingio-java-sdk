package io.growing.sdk.java.store.impl;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.sender.MessageSender;
import io.growing.sdk.java.store.StoreStrategyAbstract;
import io.growing.sdk.java.thread.GioThreadNamedFactory;
import io.growing.sdk.java.utils.ConfigUtils;
import io.growing.sdk.java.utils.ExecutorServiceUtils;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:24 PM
 */
public class DefaultStoreStrategy extends StoreStrategyAbstract {

    private static final ScheduledExecutorService sendMsgSchedule = Executors.newScheduledThreadPool(1, new GioThreadNamedFactory("gio-send-msg-schedule"));
    private static final int sendInterval = ConfigUtils.getIntValue("send.msg.interval", 100);
    private static final MessageSender sender = new FixThreadPoolSender();
    private static final int sendMsgBatchSize = 100;
    private static final int LIMIT = ConfigUtils.getIntValue("msg.store.queue.size", 500);
    private static final Map<String, List<GIOMessage>> batchMsgMap = new HashMap<String, List<GIOMessage>>();

    static {
        messageBlockingQueue = new ArrayBlockingQueue<GIOMessage>(LIMIT);
        sendMsgSchedule.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                while (!messageBlockingQueue.isEmpty()) {
                    if (currentBatchMsgSize() < sendMsgBatchSize) {
                        GIOMessage gioMessage = messageBlockingQueue.poll();
                        if (gioMessage != null) {
                            String projectKey = gioMessage.getProjectKey();
                            if (batchMsgMap.containsKey(projectKey)) {
                                List<GIOMessage> list = batchMsgMap.get(projectKey);
                                list.add(gioMessage);
                            } else {
                                List<GIOMessage> list = new ArrayList<GIOMessage>();
                                list.add(gioMessage);
                                batchMsgMap.put(projectKey, list);
                            }
                        }
                    } else {
                        break;
                    }
                }

                for (Map.Entry<String, List<GIOMessage>> entry : batchMsgMap.entrySet()) {
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        sender.sendMsg(entry.getKey(), entry.getValue());
                    }
                }

                batchMsgMap.clear();

            }
        }, sendInterval, sendInterval, TimeUnit.MILLISECONDS);
    }

    private static int currentBatchMsgSize() {
        int size = 0;
        Collection<List<GIOMessage>> values = batchMsgMap.values();
        for (List<GIOMessage> msgList : values) {
            size += msgList.size();
        }
        return size;
    }

    @Override
    public void doPush(GIOMessage msg) {
        if (!messageBlockingQueue.offer(msg)) {
            GioLogger.error("msg queue is full, suggest greater size for [msg.store.queue.size] or shorten the interval of [send.msg.interval]");
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
        sender.awaitTermination(SENDER_THREAD_POOL_TIMEOUT);
    }

    @Override
    public void shutDownNow() {
        pushMsgThreadPool.shutdownNow();
        sendMsgSchedule.shutdownNow();
        sender.shutdownNow();
    }
}