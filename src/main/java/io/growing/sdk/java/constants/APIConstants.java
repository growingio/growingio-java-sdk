package io.growing.sdk.java.constants;

import io.growing.sdk.java.utils.ConfigUtils;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 3:17 PM
 */
public class APIConstants {
    private final static String apihost = ConfigUtils.getStringValue("api.host", "");
    private final static String UPLOAD_EVENT_API_VERSION = "v3";

    public static String buildUploadEventAPI(String projectId) {
        if (apihost.endsWith("/"))
            return apihost + UPLOAD_EVENT_API_VERSION + "/" + projectId + "/s2s/cstm";
        else
            return apihost + "/" + UPLOAD_EVENT_API_VERSION + "/" + projectId + "/s2s/cstm";
    }

    public static String getApihost() {
        return apihost;
    }
}