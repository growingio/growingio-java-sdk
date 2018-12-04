package io.growing.sdk.java.sender;

import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.dto.GIOMessage;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:27 PM
 */
public interface MessageSender {

    void sendMsg(final List<GIOMessage> msg);

}