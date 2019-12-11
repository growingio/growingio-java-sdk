package io.growing.sdk.java.process.impl;

import io.growing.sdk.java.constants.APIConstants;
import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.process.MessageProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 1:53 PM
 */
public abstract class AbstractMessageProcessor implements MessageProcessor {

    protected final static Map<String, String> headers = new HashMap<String, String>();

    protected String apiDomain() {
        String apiHost = APIConstants.API_HOST;
        if (apiHost.endsWith("/"))
            return apiHost.substring(0, apiHost.length()-1);
        else
            return apiHost;
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

    protected abstract byte[] doProcess(List<GIOMessage> msgList);

    protected abstract String debugMessage(List<GIOMessage> msgList);

}