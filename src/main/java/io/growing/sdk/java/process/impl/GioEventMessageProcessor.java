package io.growing.sdk.java.process.impl;

import io.growing.sdk.java.constants.APIConstants;
import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.process.compression.CompressionTool;
import io.growing.sdk.java.process.compression.impl.GioSnappy;
import io.growing.sdk.java.sender.net.ContentTypeEnum;
import io.growing.sdk.java.utils.ConfigUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 11:36 AM
 */
public class GioEventMessageProcessor extends AbstractMessageProcessor implements MessageProcessor {

    private static final List<String> INVALID_CS1_VALUE = Arrays.asList("0", "1", "-1");
    private static final int STRING_VALUE_LENGTH_LIMIT = 255;
    private final static CompressionTool compress = new GioSnappy();

    private final static Boolean compressConfig = ConfigUtils.getBooleanValue("compress", Boolean.TRUE);

    @Override
    public String apiHost(String ai) {
        return apiDomain() + "/v3/" + ai + "/s2s/cstm?stm=" + System.currentTimeMillis();
    }

    @Override
    public ContentTypeEnum contentType() {
        return ContentTypeEnum.JSON;
    }

    @Override
    public Map<String, String> headers() {
        if (compressConfig) {
            headers.put(APIConstants.COMPRESS_HEADER, "2");
        } else {
            headers.put(APIConstants.COMPRESS_HEADER, "0");
        }

        return headers;
    }

    @Override
    public GIOMessage skipIllegalMessage(GIOMessage gioMessage) {
        GIOEventMessage eventMessage = (GIOEventMessage) gioMessage;
        if (eventMessage.getN() == null) {
            GioLogger.error("event key cant be null ");
            return null;
        } else {
            int nLength = eventMessage.getN().length();
            if (nLength == 0 || nLength > STRING_VALUE_LENGTH_LIMIT) {
                GioLogger.error("event key length must be between 1 and 255 ");
                return null;
            } else if (eventMessage.getCs1() != null) {
                int cs1Length = eventMessage.getCs1().length();
                if (cs1Length == 0 || cs1Length > STRING_VALUE_LENGTH_LIMIT) {
                    GioLogger.error("event loginUserId length must be between 1 and 255 ");
                    return null;
                } else if (INVALID_CS1_VALUE.contains(eventMessage.getCs1())) {
                    GioLogger.error("event loginUserId cant contains " + eventMessage.getCs1());
                    return null;
                } else if (eventMessage.getCs1().contains(":")) {
                    GioLogger.error("event loginUserId cant contains [:]" + eventMessage.getCs1());
                    return null;
                }
            }
        }

        return gioMessage;
    }

    @Override
    protected byte[] doProcess(List<GIOMessage> msgList) {
        String json = toJson(msgList);

        if (json != null) {
            byte[] bytes = json.getBytes();
            if (compressConfig) {
                return compress.compress(bytes);
            } else {
                return bytes;
            }
        } else {
            return null;
        }

    }

    private String toJson(List<GIOMessage> msgList) {
        JSONArray jsonArray = new JSONArray();
        for (GIOMessage msg : msgList) {
            if (msg instanceof GIOEventMessage) {
                GIOEventMessage event = (GIOEventMessage) msg;
                jsonArray.put(new JSONObject(event.getMapResult()));
            }
        }

        if (jsonArray.length() > 0) {
            return jsonArray.toString();
        } else {
            return null;
        }
    }

    @Override
    protected String debugMessage(List<GIOMessage> msgList) {
        return toJson(msgList);
    }

}