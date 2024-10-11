package io.growing.sdk.java.sender.net;

import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.RequestDto;
import io.growing.sdk.java.sender.SendResult;
import io.growing.sdk.java.utils.ConfigUtils;

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
    protected static final Map<String, String> HTTP_HEADERS = new HashMap<String, String>();

    protected static int getConnectionTimeout() {
        return ConfigUtils.getIntValue("connection.timeout", 2000);
    }

    protected static int getReadTimeout() {
        return ConfigUtils.getIntValue("read.timeout", 2000);
    }

    protected static int getSyncConnectionTimeout() {
        return ConfigUtils.getIntValue("sync.connection.timeout", 1000);
    }

    protected static int getSyncReadTimeout() {
        return ConfigUtils.getIntValue("sync.read.timeout", 1000);
    }

    public void toSend(RequestDto requestDto) {
        GioLogger.debug(System.currentTimeMillis() + " message sent. " + requestDto.toString());
        if (RunMode.isProductionMode()) {
            sendPost(requestDto);
        }
    }

    public SendResult toSendSync(RequestDto requestDto) {
        GioLogger.debug(System.currentTimeMillis() + " message sent. " + requestDto.toString());
        if (RunMode.isProductionMode()) {
            return sendPostSync(requestDto);
        }
        return new SendResult(SendResult.State.SUCCESS, "running int mode: Test");
    }

    protected abstract int sendPost(RequestDto requestDto);

    protected abstract SendResult sendPostSync(RequestDto requestDto);

    protected static class ProxyInfo {
        private static final Proxy proxy;

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