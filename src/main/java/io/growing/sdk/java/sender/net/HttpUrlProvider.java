package io.growing.sdk.java.sender.net;

import io.growing.sdk.java.constants.HttpHeaderConstants;
import io.growing.sdk.java.exception.GIOMessageException;
import io.growing.sdk.java.logger.GioLogger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 2:59 PM
 */
public class HttpUrlProvider extends NetProviderAbstract {

    @Override
    protected void sendPost(String url, byte[] data) {
        try {
            doSend(url, data);
        } catch (Exception e) {
            if (e instanceof IOException) {
                retry(url, data);
            } else {
                GioLogger.error("failed to send growingio data" + e);
            }
        }
    }

    @Override
    protected void sendGet(String url) throws IOException {
        HttpURLConnection httpConn = getConnection(url);
        setHeaders(httpConn);
        httpConn.setRequestMethod("GET");
        httpConn.setConnectTimeout(connectionTimeout);
        httpConn.setReadTimeout(readTimeout);

        InputStream inputStream = null;
        try {
            httpConn.connect();
            int responseCode = httpConn.getResponseCode();
            inputStream = httpConn.getInputStream();
            if (responseCode < 200 || responseCode > 300) {
                throw new GIOMessageException("growingio server return error " + responseCode);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Override
    public boolean isConnectedToGrowingAPIHost() {
        try {
            sendGet(CHECK_NET_HEALTH_URL);
            return true;
        } catch (IOException e) {
            GioLogger.error("failed to connect " + CHECK_NET_HEALTH_URL + ", cause " + e);
            return false;
        }
    }

    private void doSend(String url, byte[] data) throws Exception {
        HttpURLConnection httpConn = getConnection(url);
        setHeaders(httpConn);
        httpConn.setUseCaches(false);
        httpConn.setRequestMethod("POST");
        httpConn.setConnectTimeout(connectionTimeout);
        httpConn.setReadTimeout(readTimeout);
        httpConn.setRequestProperty(HttpHeaderConstants.CONTENT_TYPE, HttpHeaderConstants.APPLICATION_JSON);
        httpConn.setRequestProperty(HttpHeaderConstants.CONTENT_LENGTH, String.valueOf(data.length));
        httpConn.setDoOutput(true);

        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            httpConn.connect();
            outputStream = new DataOutputStream(httpConn.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
            int responseCode = httpConn.getResponseCode();

            inputStream = httpConn.getInputStream();
            if (responseCode < 200 || responseCode > 300) {
                GioLogger.error("growingio server return error " + responseCode);
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void setHeaders(HttpURLConnection httpConn) {
        for (Map.Entry<String, String> entry : httpHeaders.entrySet())
            httpConn.setRequestProperty(entry.getKey(), entry.getValue());
    }

    private void retry(String url, byte[] data) {
        int retryTimes = 3;
        for (int i = 0; i < retryTimes; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
                doSend(url, data);
                break;
            } catch (Exception ignore) {

            }
        }
    }

    private HttpURLConnection getConnection(String url) throws IOException {
        Proxy proxy = ProxyInfo.getProxy();

        HttpURLConnection httpConn;
        if (proxy == null) {
            httpConn = (HttpURLConnection) new URL(url).openConnection();
        } else {
            httpConn = (HttpURLConnection) new URL(url).openConnection(proxy);
        }

        return httpConn;
    }
}