package io.growing.sdk.java.process.impl;

import io.growing.collector.tunnel.protocol.UserMappingDtoList;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCdpUserMappingMessage;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.sender.net.ContentTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author lijh
 * @version : 1.0.9
 * @since : 2021-08-16 11:17 PM
 */
public class GioCdpUserMappingMessageProcessor extends ProtobufMessage implements MessageProcessor {
    @Override
    protected byte[] doProcess(List<GIOMessage> msgList) {
        UserMappingDtoList list = getIdMapping(msgList);
        return null == list ? null : list.toByteArray();
    }

    @Override
    protected String debugMessage(List<GIOMessage> msgList) {
        return toJson(getIdMapping(msgList));
    }

    private UserMappingDtoList getIdMapping(List<GIOMessage> msgList) {
        UserMappingDtoList.Builder listBuilder = UserMappingDtoList.newBuilder();

        for (GIOMessage msg : msgList) {
            if (msg instanceof GioCdpUserMappingMessage) {
                GioCdpUserMappingMessage cdp = (GioCdpUserMappingMessage) msg;
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
        return apiDomain() + "/projects/" + ai + "/collect/user-mapping";
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
