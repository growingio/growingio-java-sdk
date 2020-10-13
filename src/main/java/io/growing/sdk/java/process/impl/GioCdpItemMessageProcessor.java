package io.growing.sdk.java.process.impl;

import io.growing.collector.tunnel.protocol.ItemDtoList;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCdpItemMessage;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.sender.net.ContentTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 9/21/20 11:47 AM
 */
public class GioCdpItemMessageProcessor extends ProtobufMessage implements MessageProcessor {
    @Override
    protected byte[] doProcess(List<GIOMessage> msgList) {
        ItemDtoList list = getItems(msgList);
        return null == list ? null : list.toByteArray();
    }

    @Override
    protected String debugMessage(List<GIOMessage> msgList) {
        ItemDtoList list = getItems(msgList);
        return toJson(getItems(msgList));
    }

    private ItemDtoList getItems(List<GIOMessage> msgList) {
        ItemDtoList.Builder listBuilder = ItemDtoList.newBuilder();

        for (GIOMessage msg : msgList) {
            if (msg instanceof GioCdpItemMessage) {
                GioCdpItemMessage cdp = (GioCdpItemMessage) msg;
                listBuilder.addValues(cdp.getMessage());
            }
        }

        if (listBuilder.getValuesCount() > 0) {
            return listBuilder.build();
        } else {
            return null;
        }
    }

    @Override
    public String apiHost(String ai) {
        return apiDomain() + "/projects/" + ai + "/collect/item";
    }

    @Override
    public ContentTypeEnum contentType() {
        return ContentTypeEnum.PROTOBUF;
    }

    @Override
    public Map<String, String> headers() {
        return HEADERS;
    }

    @Override
    public GIOMessage skipIllegalMessage(GIOMessage gioMessage) {
        return gioMessage;
    }
}