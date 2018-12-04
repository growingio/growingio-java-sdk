package io.growing.sdk.java.process.serialize;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.exception.GIOMessageException;
import io.growing.sdk.java.logger.GioLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:28 PM
 */
public class JsonSerialize implements SerializeTool {

    public byte[] serialize(List<GIOMessage> msgList) {
        String serialized = serializeToJson(msgList);

        if (GrowingAPI.isTestMode()) {
            GioLogger.debug("gio message is " + serialized);
        }

        return serialized.getBytes();
    }

    @Override
    public String deserialize(byte[] bytes) {
        return new String(bytes);
    }

    public static String serializeToJson(List<GIOMessage> msgList) {
        JSONArray jsonArray = new JSONArray();
        for (GIOMessage msg : msgList) {
            jsonArray.put(new JSONObject(msg.getMapResult()));
        }
        return jsonArray.toString();
    }

    public static String serializeToJson(GIOMessage msg) {
        return new JSONObject(msg).toString();
    }
}
