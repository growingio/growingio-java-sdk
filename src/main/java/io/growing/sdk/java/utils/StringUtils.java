package io.growing.sdk.java.utils;

import java.util.Iterator;
import java.util.List;

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

    public static <T> String list2Str(List<T> value) {
        if (value != null && !value.isEmpty()) {
            final String listSplit = "||";
            StringBuilder valueBuilder = new StringBuilder();
            Iterator<T> iterator = value.iterator();
            if (iterator.hasNext()) {
                valueBuilder.append(toString(iterator.next()));
                while (iterator.hasNext()) {
                    valueBuilder.append(listSplit);
                    valueBuilder.append(toString(iterator.next()));
                }
            }
            return valueBuilder.toString();
        }

        return "";
    }

    private static String toString(Object value) {
        if (value == null) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }
}