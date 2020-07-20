package io.growing.sdk.java.sender;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.process.EventProcessorClient;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.sender.net.HttpUrlProvider;
import io.growing.sdk.java.sender.net.NetProviderAbstract;
import io.growing.sdk.java.thread.GioThreadNamedFactory;
import io.growing.sdk.java.utils.ConfigUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 4:46 PM
 */
public class FixThreadPoolSender implements MessageSender {
    private static final ExecutorService sendThread = Executors.newFixedThreadPool(ConfigUtils.getIntValue("send.msg.thread", 3), new GioThreadNamedFactory("gio-sender"));

    private static final NetProviderAbstract netProvider = new HttpUrlProvider();

    @Override
    public void sendMsg(final String projectKey, final List<GIOMessage> msg) {
        doSend(projectKey, msg);
    }

    public static NetProviderAbstract getNetProvider() {
        return netProvider;
    }

    public void doSend(final String projectKey, final List<GIOMessage> msgList) {
        if (null != msgList && !msgList.isEmpty()) {
            sendThread.execute(new Runnable() {
                @Override
                public void run() {
                    for (MessageProcessor processor : EventProcessorClient.getProcessors()) {
                        byte[] processed = processor.process(msgList);

                        if (processed != null && processed.length > 0) {
                            RequestDto requestDto = new RequestDto.Builder()
                                    .setUrl(processor.apiHost(projectKey))
                                    .setContentType(processor.contentType())
                                    .setBytes(processed)
                                    .setHeaders(processor.headers())
                                    .build();

                            getNetProvider().toSend(requestDto);
                        }

                    }
                }
            });
        }
    }

}