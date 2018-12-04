package io.growing.sdk.java.validate;

import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.process.serialize.JsonSerialize;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 5:21 PM
 */
public class MsgValidate {

    private static final List<String> INVALID_CS1_VALUE = Arrays.asList("0", "1", "-1");
    private static final int STRING_VALUE_LENGTH_LIMIT = 255;

    /**
     * validate return msg
     * invalidate return null
     * @param msg
     * @return
     */
    public static GIOMessage validate(GIOMessage msg) {
        if (msg instanceof GIOEventMessage) {
            GIOEventMessage eventMessage = (GIOEventMessage) msg;
            if (isGIOEventMessageInvalidate(eventMessage)) {
                return null;
            }
        }

        return msg;
    }

    private static boolean isGIOEventMessageInvalidate(GIOEventMessage gioEventMessage) {
        if (gioEventMessage.getN() == null) {
            GioLogger.error("event key cant be null ");
            return true;
        } else {
            int nLength = gioEventMessage.getN().length();
            if (nLength == 0 || nLength > STRING_VALUE_LENGTH_LIMIT) {
                GioLogger.error("event key length must be between 1 and 255 ");
                return true;
            } else if (gioEventMessage.getCs1() != null) {
                int cs1Length = gioEventMessage.getCs1().length();
                if (cs1Length == 0 || cs1Length > STRING_VALUE_LENGTH_LIMIT) {
                    GioLogger.error("event loginUserId length must be between 1 and 255 ");
                    return true;
                } else if (INVALID_CS1_VALUE.contains(gioEventMessage.getCs1())) {
                    GioLogger.error("event loginUserId cant contains " + gioEventMessage.getCs1());
                    return true;
                } else if (gioEventMessage.getCs1().contains(":")) {
                    GioLogger.error("event loginUserId cant contains [:]" + gioEventMessage.getCs1());
                    return true;
                }
            }
        }

        return false;
    }
}