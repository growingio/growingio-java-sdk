package io.growing.sdk.java.sender;

import io.growing.sdk.java.constants.APIConstants;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.process.ProcessClient;
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
    private final static NetProviderAbstract netProvider = new HttpUrlProvider();
    private static ExecutorService sendThread = Executors.newFixedThreadPool(ConfigUtils.getIntValue("send.msg.thread", 3), new GioThreadNamedFactory("gio-sender"));

    public static NetProviderAbstract getNetProvider() {
        return netProvider;
    }

    @Override
    public void sendMsg(final String projectId, final List<GIOMessage> msg) {
        doSend(APIConstants.buildUploadEventAPI(projectId), msg);
    }

    private String addStmQueryParams(String url) {
        return url + "?stm=" + System.currentTimeMillis();
    }

    public void doSend(final String url, final List<GIOMessage> msg) {
        if (null != msg) {
            sendThread.execute(new Runnable() {
                @Override
                public void run() {
                    getNetProvider().toSend(addStmQueryParams(url), ProcessClient.process(msg));
                }
            });
        }
    }
}