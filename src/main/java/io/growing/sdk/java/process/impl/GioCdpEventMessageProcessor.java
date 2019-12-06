package io.growing.sdk.java.process.impl;

import io.growing.collector.tunnel.protocol.EventDto;
import io.growing.collector.tunnel.protocol.EventList;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCdpEventMessage;
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
public class GioCdpEventMessageProcessor extends AbstractMessageProcessor implements MessageProcessor {

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
        EventList.Builder listBuilder = EventList.newBuilder();
        listBuilder.addAllValues(getEvents(msgList));
        return listBuilder.build().toByteArray();
    }

    private List<EventDto> getEvents(List<GIOMessage> msgList) {
        List<EventDto> events = new ArrayList<EventDto>(msgList.size());

        for (GIOMessage msg : msgList) {
            GioCdpEventMessage cdp = (GioCdpEventMessage) msg;
            events.add(cdp.getEvent());
        }

        return events;
    }

    @Override
    protected String debugMessage(List<GIOMessage> msgList) {
        List<EventDto> events = getEvents(msgList);

        StringBuilder message = new StringBuilder();
        message.append("[");
        int size = events.size();
        for (int i = 0; i < size; i++) {
            EventDto event = events.get(i);
            if (i == size - 1) {
                message.append(event.toString());
            } else {
                message.append(event.toString()).append(",");
            }
        }
        message.append("]");

        return message.toString();
    }

}