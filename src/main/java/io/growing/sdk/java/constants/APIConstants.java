package io.growing.sdk.java.constants;

import io.growing.sdk.java.utils.ConfigUtils;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 3:17 PM
 */
public class APIConstants {

    public static final  String API_HOST = ConfigUtils.getStringValue("api.host", "");

    public static final String COMPRESS_HEADER = "X-Compress-Codec";

}