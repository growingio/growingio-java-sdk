package io.growing.sdk.java.utils;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCDPMessage;
import io.growing.sdk.java.logger.GioLogger;

public class MessageUtils {
    public static boolean businessVerification(GIOMessage msg, String projectKey, String dataSourceId) {
        if (StringUtils.nonBlank(projectKey)) {
            msg.setProjectKey(projectKey);
        } else {
            GioLogger.error("projectKey cant be null or empty string");
            return false;
        }

        if (msg instanceof GioCDPMessage) {
            if (StringUtils.nonBlank(dataSourceId)) {
                ((GioCDPMessage<?>) msg).setDataSourceId(dataSourceId);
            } else {
                GioLogger.error("cdp message datasourceId cant be null or empty string");
                return false;
            }
        }
        return true;
    }
}
