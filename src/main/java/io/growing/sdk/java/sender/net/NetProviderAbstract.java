package io.growing.sdk.java.sender.net;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.constants.APIConstants;
import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.process.ProcessClient;
import io.growing.sdk.java.process.serialize.JsonSerialize;
import io.growing.sdk.java.utils.ConfigUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-22 13:36
 */
public abstract class NetProviderAbstract {
    protected final static String CHECK_NET_HEALTH_URL = APIConstants.getApihost();

    protected final static Map<String, String> httpHeaders = new HashMap<String, String>();

    private final static String COMPRESS_HEADER = "X-Compress-Codec";
    private final static Boolean compressConfig = ConfigUtils.getBooleanValue("compress", Boolean.TRUE);

    static {
        String compressCode = "0";
        if (compressConfig) {
            compressCode = "2";
        }

        httpHeaders.put(COMPRESS_HEADER, compressCode);//0 不压缩 2 Snappy压缩
    }

    public static boolean needCompress() {
        return compressConfig;
    }

    public void toSend(String url, byte[] data) {
        if (GrowingAPI.isProductionMode()) {                                                                            
            sendPost(url, data);
        } else {
            GioLogger.debug("apiHost: " + url + " data size: " + data.length);
        }

    }

    protected abstract void sendPost(String url, byte[] data);

    protected abstract void sendGet(String url) throws IOException;

    public abstract boolean isConnectedToGrowingAPIHost();
}