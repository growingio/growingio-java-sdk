package io.growing.sdk.java.sender.net;

import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.RequestDto;
import io.growing.sdk.java.utils.ConfigUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-22 13:36
 */
public abstract class NetProviderAbstract extends BaseNetProvider {
    protected static final Map<String, String> HTTP_HEADERS = new HashMap<String, String>();

    protected static int getConnectionTimeout() {
        return ConfigUtils.getIntValue("connection.timeout", 2000);
    }

    protected static int getReadTimeout() {
        return ConfigUtils.getIntValue("read.timeout", 2000);
    }

    public void toSend(RequestDto requestDto) {
        if (RunMode.isProductionMode()) {
            sendPost(requestDto);
        }
        GioLogger.debug(System.currentTimeMillis() + " message sent. " + requestDto.toString());
    }

    protected abstract int sendPost(RequestDto requestDto);

    public abstract boolean connectedToGrowingAPIHost();
}