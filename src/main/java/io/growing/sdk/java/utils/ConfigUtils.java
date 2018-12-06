package io.growing.sdk.java.utils;

import io.growing.sdk.java.exception.GIOMessageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:00 PM
 */
public class ConfigUtils {
    private static Properties prop = new Properties();
    
    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream gioProps = classLoader.getResourceAsStream("gio_default.properties");
            prop.load(gioProps);
            
            InputStream customProps = classLoader.getResourceAsStream("gio.properties");
            if (customProps != null) {
                prop.load(customProps);
            }

        } catch (Exception e) {
            throw new GIOMessageException("cant find gio.properties", e);
        }
    }

    public static String getStringValue(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    public static Long getLongValue(String key, Long defaultValue) {
        String obj = prop.getProperty(key, defaultValue.toString());

        try {
            return Long.valueOf(obj);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Integer getIntValue(String key, Integer defaultValue) {
        String obj = prop.getProperty(key, defaultValue.toString());

        try {
            return Integer.valueOf(obj);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Boolean getBooleanValue(String key, Boolean defaultValue) {
        String obj = prop.getProperty(key, defaultValue.toString());

        try {
            return Boolean.valueOf(obj);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}