package io.growing.sdk.java.process.impl;

import io.growing.collector.tunnel.protocol.EventDto;
import io.growing.collector.tunnel.protocol.EventList;
import io.growing.collector.tunnel.protocol.UserDto;
import io.growing.collector.tunnel.protocol.UserList;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.sender.net.ContentTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 11:36 AM
 */
public class GioCdpUserMessageProcessor extends AbstractMessageProcessor implements MessageProcessor {

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
        UserList.Builder listBuilder = UserList.newBuilder();
        listBuilder.addAllValues(getUsers(msgList));
        return listBuilder.build().toByteArray();
    }

    private List<UserDto> getUsers(List<GIOMessage> msgList) {
        List<UserDto> users = new ArrayList<UserDto>(msgList.size());

        for (GIOMessage msg : msgList) {
            GioCdpUserMessage cdp = (GioCdpUserMessage) msg;
            users.add(cdp.getUser());
        }

        return users;
    }

    @Override
    protected String debugMessage(List<GIOMessage> msgList) {
        List<UserDto> users = getUsers(msgList);

        StringBuilder message = new StringBuilder();
        message.append("[");
        int size = users.size();
        for (int i = 0; i < size; i++) {
            UserDto user = users.get(i);
            if (i == size - 1) {
                message.append(user.toString());
            } else {
                message.append(user.toString()).append(",");
            }
        }
        message.append("]");

        return message.toString();
    }

}