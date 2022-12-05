package io.growing.sdk.java.sender;

import io.growing.sdk.java.dto.GIOMessage;

import java.util.List;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:27 PM
 */
public interface MessageSender {

    void sendMsg(final String projectId, final List<GIOMessage> msg);

}