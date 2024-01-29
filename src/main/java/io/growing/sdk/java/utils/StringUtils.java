package io.growing.sdk.java.utils;

import io.growing.sdk.java.com.googlecode.protobuf.format.util.JsonUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public static String map2Str(Map<String, String> map) {
        try {
            if (map != null && !map.isEmpty()) {
                boolean printedComma = false;
                StringBuilder builder = new StringBuilder("{");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (key != null && value != null) {
                        if (printedComma) {
                            builder.append(",");
                        } else {
                            printedComma = true;
                        }
                        printField(builder, key);
                        builder.append(":");
                        printField(builder, toString(value));
                    }
                }
                builder.append("}");
                return builder.toString();
            }
        } catch (Exception ignored) {
        }

        return "{}";
    }

    private static void printField(StringBuilder builder, String value) {
        builder.append("\"");
        builder.append(JsonUtils.escapeText(value));
        builder.append("\"");
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