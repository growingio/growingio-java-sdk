package io.growing.sdk.java.process.impl;

import io.growing.collector.tunnel.protocol.UserList;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.sender.net.ContentTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 11:36 AM
 */
public class GioCdpUserMessageProcessor extends ProtobufMessage implements MessageProcessor {

    @Override
    public String apiHost(String ai) {
        return apiDomain() + "/projects/" + ai + "/collect/user";
    }

    @Override
    public ContentTypeEnum contentType() {
        return ContentTypeEnum.PROTOBUF;
    }

    @Override
    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public GIOMessage skipIllegalMessage(GIOMessage gioMessage) {
        //todo validate
        return gioMessage;
    }

    @Override
    protected byte[] doProcess(List<GIOMessage> msgList) {
        return getUsers(msgList).toByteArray();
    }

    private UserList getUsers(List<GIOMessage> msgList) {
        UserList.Builder listBuilder = UserList.newBuilder();
        for (GIOMessage msg : msgList) {
            GioCdpUserMessage cdp = (GioCdpUserMessage) msg;
            listBuilder.addValues(cdp.getUser());
        }

        return listBuilder.build();
    }

    @Override
    protected String debugMessage(List<GIOMessage> msgList) {
        return toJson(getUsers(msgList));
    }

}