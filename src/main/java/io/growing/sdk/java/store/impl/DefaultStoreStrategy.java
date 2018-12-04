package io.growing.sdk.java.store.impl;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.sender.MessageSender;
import io.growing.sdk.java.store.StoreStrategyAbstract;
import io.growing.sdk.java.thread.GioThreadNamedFactory;
import io.growing.sdk.java.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:24 PM
 */
public class DefaultStoreStrategy extends StoreStrategyAbstract {
    private static final int limit = ConfigUtils.getIntValue("msg.store.queue.size", 500);

    private static final ArrayBlockingQueue<GIOMessage> queue = new ArrayBlockingQueue<GIOMessage>(limit);

    private static final ScheduledExecutorService sendMsgSchedule = Executors.newScheduledThreadPool(1, new GioThreadNamedFactory("gio-send-msg-schedule"));
    private static final int sendInterval = ConfigUtils.getIntValue("send.msg.interval", 100);
    private static final MessageSender sender = new FixThreadPoolSender();
    private static final int sendMsgBatchSize = 100;
    private static AtomicInteger offered = new AtomicInteger(0);

    static {
        sendMsgSchedule.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                List<GIOMessage> msg = new ArrayList<GIOMessage>(sendMsgBatchSize);
                while (!queue.isEmpty()) {
                    if (msg.size() < sendMsgBatchSize) {
                        GIOMessage gioMessage = queue.poll();
                        if (gioMessage != null) {
                            msg.add(gioMessage);
                        }
                    } else {
                        break;
                    }
                }

                if (!msg.isEmpty()) {
                    sender.sendMsg(msg);
                }

            }
        }, sendInterval, sendInterval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void doPush(GIOMessage msg) {
        if (!queue.offer(msg)) {
            GioLogger.error("msg queue is full, suggest greater size for [msg.store.queue.size] or shorten the interval of [send.msg.interval]");
        }
    }

}