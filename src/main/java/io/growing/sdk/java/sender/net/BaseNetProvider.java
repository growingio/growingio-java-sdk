package io.growing.sdk.java.sender.net;

import io.growing.sdk.java.utils.ConfigUtils;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import static java.net.Proxy.Type.HTTP;

public class BaseNetProvider {

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
}
