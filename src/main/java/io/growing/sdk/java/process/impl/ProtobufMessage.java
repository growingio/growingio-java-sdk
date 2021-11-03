package io.growing.sdk.java.process.impl;

import com.google.protobuf.Message;
import io.growing.sdk.java.com.googlecode.protobuf.format.JsonFormat;
import io.growing.sdk.java.logger.GioLogger;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 11:40 PM
 */
public abstract class ProtobufMessage extends AbstractMessageProcessor {
    private static JsonFormat jsonFormat = new JsonFormat();

    protected String toJson(Message msg) {
        if (msg != null) {
            try {
                return jsonFormat.printToString(msg);
            } catch (Exception e) {
                GioLogger.error("failed to parse msg, " + e.toString());
            }
        }

        return null;
    }

 }