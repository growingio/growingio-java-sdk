package io.growing.sdk.java.utils;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/11/19 2:10 PM
 */
public class StringUtils {

    public static boolean nonBlank(String value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isBlank(String value) {
        return value == null || value.isEmpty();
    }
}
