package io.growing.sdk.java.process.impl;

import io.growing.collector.tunnel.protocol.EventList;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.sender.net.ContentTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 11:36 AM
 */
public class GioCdpEventMessageProcessor extends ProtobufMessage implements MessageProcessor {

    @Override
    public String apiHost(String ai) {
        return apiDomain() + "/projects/" + ai + "/collect/cstm";
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
        return getEvents(msgList).toByteArray();
    }

    private EventList getEvents(List<GIOMessage> msgList) {
        EventList.Builder listBuilder = EventList.newBuilder();
        for (GIOMessage msg : msgList) {
            GioCdpEventMessage cdp = (GioCdpEventMessage) msg;
            listBuilder.addValues(cdp.getEvent());
        }

        return listBuilder.build();
    }

    @Override
    protected String debugMessage(List<GIOMessage> msgList) {
        return toJson(getEvents(msgList));
    }

}