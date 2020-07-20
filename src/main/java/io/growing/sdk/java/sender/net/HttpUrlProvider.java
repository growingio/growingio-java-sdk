package io.growing.sdk.java.sender.net;

import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.RequestDto;

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
    protected int sendPost(RequestDto requestDto) {
        try {
            return doSend(requestDto);
        } catch (Exception e) {
            if (e instanceof IOException) {
                return retry(requestDto);
            } else {
                return HttpURLConnection.HTTP_BAD_REQUEST;
            }
        }
    }

    @Override
    public boolean connectedToGrowingAPIHost() {
        InputStream inputStream = null;
        try {
            HttpURLConnection httpConn = getConnection(CHECK_NET_HEALTH_URL);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(CONNECTION_TIMEOUT);
            httpConn.setReadTimeout(READ_TIMEOUT);

            httpConn.connect();
            httpConn.getResponseCode();

            return true;
        } catch (IOException e) {
            GioLogger.error("failed to connect " + CHECK_NET_HEALTH_URL + ", cause " + e.getLocalizedMessage());
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private int doSend(RequestDto requestDto) throws Exception {
        HttpURLConnection httpConn = getConnection(requestDto.getUrl());
        setHttpConnHeaders(httpConn, requestDto.getHeaders());
        httpConn.setRequestProperty("Content-Type", requestDto.getContentType().toString());
        httpConn.setUseCaches(false);
        httpConn.setRequestMethod("POST");
        httpConn.setConnectTimeout(CONNECTION_TIMEOUT);
        httpConn.setReadTimeout(READ_TIMEOUT);
        httpConn.setRequestProperty("Content-Length", String.valueOf(requestDto.getBytes().length));
        httpConn.setDoOutput(true);

        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            httpConn.connect();
            outputStream = new DataOutputStream(httpConn.getOutputStream());
            outputStream.write(requestDto.getBytes());
            outputStream.flush();
            int responseCode = httpConn.getResponseCode();
            inputStream = httpConn.getInputStream();

            return responseOk(responseCode);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void setHttpConnHeaders(HttpURLConnection httpConn, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpConn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private int responseOk(int responseCode) {
        if (responseCode < 200 || responseCode > 300) {
            GioLogger.error("growingio server return error " + responseCode);
        }
        return responseCode;
    }

    private int retry(RequestDto requestDto) {
        int retryTimes = 3;
        int responseCode = 0;
        for (int i = 0; i < retryTimes; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
                responseCode = doSend(requestDto);
                break;
            } catch (Exception ignore) {

            }
        }
        return responseCode;
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