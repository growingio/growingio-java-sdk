package io.growing.sdk.java.process.impl;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 11:40 PM
 */
public abstract class ProtobufMessage extends AbstractMessageProcessor {
    private static JsonFormat jsonFormat = new JsonFormat();

    protected String toJson(Message msg) {
        return jsonFormat.printToString(msg);
    }

 }