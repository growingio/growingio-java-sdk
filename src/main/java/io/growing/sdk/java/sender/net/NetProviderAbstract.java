package io.growing.sdk.java.sender.net;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.constants.APIConstants;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.utils.ConfigUtils;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import static java.net.Proxy.Type.HTTP;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-22 13:36
 */
public abstract class NetProviderAbstract {
    protected final static String CHECK_NET_HEALTH_URL = APIConstants.getApihost();

    protected final static Map<String, String> httpHeaders = new HashMap<String, String>();

    protected final static int connectionTimeout = ConfigUtils.getIntValue("connection.timeout", 2000);
    protected final static int readTimeout = ConfigUtils.getIntValue("read.timeout", 2000);

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

    protected static class ProxyInfo {
        private static Proxy proxy;

        static {
            proxy = setProxy();
            setAuthenticator();
        }

        private static Proxy setProxy() {
            String proxyHost = ConfigUtils.getStringValue("proxy.host", null);
            Integer proxyPort = ConfigUtils.getIntValue("proxy.port", 80);

            if (proxyHost == null || proxyHost.isEmpty()) {
                return null;
            }

            return new Proxy(HTTP, new InetSocketAddress(proxyHost, proxyPort));
        }

        private static void setAuthenticator() {
            final String proxyUser = ConfigUtils.getStringValue("proxy.user", null);
            final String proxyPassword = ConfigUtils.getStringValue("proxy.password", null);

            if (proxyUser != null && proxyPassword != null) {
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
                    }
                });
            }
        }

        public static Proxy getProxy() {
            return proxy;
        }
    }
}