package io.growing.sdk.java.ab;

import io.growing.sdk.java.sender.net.BaseNetProvider;
import io.growing.sdk.java.utils.ConfigUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class ABTestHttpUrlProvider extends BaseNetProvider {

    private static int getConnectionTimeout() {
        return ConfigUtils.getIntValue("ab.connection.timeout", 5000);
    }

    private static int getReadTimeout() {
        return ConfigUtils.getIntValue("ab.read.timeout", 5000);
    }

    public ABTestResponse requestABTestExperimentData(String projectId, String dataSourceId, String layerId, String distinctId, boolean isNewDevice) {
        ABTestResponse outABTestResponse = new ABTestResponse();
        DataOutputStream os = null;
        BufferedReader br = null;
        try {
            String body = "accountId=" + URLEncoder.encode(projectId, Charset.forName("UTF-8").toString()) +
                    "&datasourceId=" + URLEncoder.encode(dataSourceId, Charset.forName("UTF-8").toString()) +
                    "&distinctId=" + URLEncoder.encode(distinctId, Charset.forName("UTF-8").toString()) +
                    "&layerId=" + URLEncoder.encode(layerId, Charset.forName("UTF-8").toString());
            if (isNewDevice) {
                body += "&newDevice=true";
            }

            HttpURLConnection httpConn = getConnection(apiHost());
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(getConnectionTimeout());
            httpConn.setReadTimeout(getReadTimeout());
            httpConn.setRequestProperty("Content-Length", String.valueOf(body.getBytes().length));
            httpConn.setDoOutput(true);

            os = new DataOutputStream(httpConn.getOutputStream());
            os.write(body.getBytes());
            os.flush();

            int responseCode = httpConn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                ABTestResponse abTestResponse = ABTestResponse.parseHttpJson(layerId, response.toString());
                if (!abTestResponse.isSucceed()) {
                    abTestResponse.setErrorMsg("ABExperiment data failed with: " + abTestResponse.getErrorMsg());
                }
                return abTestResponse;
            } else {
                outABTestResponse.setErrorMsg("ABTest request failed: " + responseCode);
            }

        } catch (Exception e) {
            outABTestResponse.setErrorMsg("ABTest request failed: " + e.getLocalizedMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    outABTestResponse.setErrorMsg("ABTest request failed: " + e.getLocalizedMessage());
                }
                os = null;
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    outABTestResponse.setErrorMsg("ABTest request failed: " + e.getLocalizedMessage());
                }
                br = null;
            }
        }
        return outABTestResponse;
    }

    private String apiDomain() {
        String apiHost = ConfigUtils.getStringValue("ab.api.host", "");
        if (apiHost.endsWith("/")) {
            return apiHost.substring(0, apiHost.length() - 1);
        } else {
            return apiHost;
        }
    }

    private String apiHost() {
        return apiDomain() + "/diversion/specified-layer-variables";
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
