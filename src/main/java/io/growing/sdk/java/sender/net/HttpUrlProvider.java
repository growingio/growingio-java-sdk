package io.growing.sdk.java.sender.net;

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
                GioLogger.error("failed to send growingio data" + e.toString());
            }
        }
    }

    @Override
    protected void sendGet(String url) throws IOException {
        HttpURLConnection httpConn = getConnection(url);
        setHeaders(httpConn);
        httpConn.setRequestMethod("GET");
        httpConn.setConnectTimeout(1000);
        httpConn.setConnectTimeout(1000);


        httpConn.connect();

        InputStream inputStream = null;
        try {
            int responseCode = httpConn.getResponseCode();
            inputStream = httpConn.getInputStream();
            if (responseCode != 200) {
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
            GioLogger.error("failed to connect " + CHECK_NET_HEALTH_URL + ", cause " + e.toString());
            return false;
        }
    }

    private void doSend(String url, byte[] data) throws Exception {
        HttpURLConnection httpConn = getConnection(url);
        setHeaders(httpConn);
        httpConn.setUseCaches(false);
        httpConn.setRequestMethod("POST");
        httpConn.setConnectTimeout(2000);
        httpConn.setConnectTimeout(2000);
        httpConn.setRequestProperty("Content-Length", String.valueOf(data.length));
        httpConn.setDoOutput(true);

        httpConn.connect();

        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = new DataOutputStream(httpConn.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
            int responseCode = httpConn.getResponseCode();

            inputStream = httpConn.getInputStream();
            if (responseCode != 200) {
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