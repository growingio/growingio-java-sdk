package io.growing.sdk.java.process.serialize;

import io.growing.sdk.java.dto.GIOMessage;

import java.util.List;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-25 00:27
 */
public interface SerializeTool {

    byte[] serialize(List<GIOMessage> msgList);

    String deserialize(byte[] bytes);
}
