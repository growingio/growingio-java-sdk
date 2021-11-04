package io.growing.sdk.java.process.impl;

import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.utils.ConfigUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 1:53 PM
 */
public abstract class AbstractMessageProcessor implements MessageProcessor {

    protected static final Map<String, String> HEADERS = new HashMap<String, String>();

    protected String apiDomain() {
        String apiHost = ConfigUtils.getStringValue("api.host", "");
        if (apiHost.endsWith("/")) {
            return apiHost.substring(0, apiHost.length() - 1);
        } else {
            return apiHost;
        }
    }

    @Override
    public byte[] process(List<GIOMessage> msgList) {
        if (RunMode.isTestMode()) {
            String debugMessage = debugMessage(msgList);
            if (debugMessage != null) {
                GioLogger.debug("gio message is " + debugMessage(msgList));
            }
        }

        return doProcess(msgList);
    }

    @Override
    public boolean skipIllegalMessage(GIOMessage gioMessage) {
        return gioMessage.isIllegal();
    }

    protected abstract byte[] doProcess(List<GIOMessage> msgList);

    protected abstract String debugMessage(List<GIOMessage> msgList);

}