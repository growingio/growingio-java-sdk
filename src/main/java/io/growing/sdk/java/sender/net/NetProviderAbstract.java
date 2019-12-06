package io.growing.sdk.java.sender.net;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.constants.APIConstants;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.RequestDto;
import io.growing.sdk.java.utils.ConfigUtils;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import static io.growing.sdk.java.constants.APIConstants.COMPRESS_HEADER;
import static java.net.Proxy.Type.HTTP;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-22 13:36
 */
public abstract class NetProviderAbstract {
    protected final static String CHECK_NET_HEALTH_URL = APIConstants.API_HOST;

    protected final static Map<String, String> httpHeaders = new HashMap<String, String>();

    protected final static int connectionTimeout = ConfigUtils.getIntValue("connection.timeout", 2000);
    protected final static int readTimeout = ConfigUtils.getIntValue("read.timeout", 2000);

    public void toSend(RequestDto requestDto) {
        if (GrowingAPI.isProductionMode()) {
            sendPost(requestDto);
        } else {
            GioLogger.debug(requestDto.toString());
        }
    }

    protected abstract void sendPost(RequestDto requestDto);

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

    public abstract boolean connectedToGrowingAPIHost();
}