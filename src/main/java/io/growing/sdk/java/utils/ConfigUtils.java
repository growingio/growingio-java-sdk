package io.growing.sdk.java.utils;

import io.growing.sdk.java.exception.GIOMessageException;
import io.growing.sdk.java.logger.GioLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:00 PM
 */
public class ConfigUtils {
    private static final AtomicBoolean inited = new AtomicBoolean(false);
    private static Properties prop = new Properties();

    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream gioDefaultProps = classLoader.getResourceAsStream("gio_default.properties");
            prop.load(gioDefaultProps);

            InputStream gioProps = classLoader.getResourceAsStream("gio.properties");
            if (gioProps != null) {
                prop.load(gioProps);
            }
        } catch (Exception e) {
            throw new GIOMessageException("cant find gio sdk config", e);
        }
    }

    public static void initDefault() {
        inited.compareAndSet(false, true);
    }

    public static void init(String configFilePath) {
        if (inited.compareAndSet(false, true)) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            try {
                if (null != configFilePath && configFilePath.length() != 0) {
                    InputStream customProps = classLoader.getResourceAsStream(configFilePath);
                    if (customProps != null) {
                        prop.load(customProps);
                    } else {
                        InputStream runtimeConfigResource = new FileInputStream(new File(configFilePath));
                        prop.load(runtimeConfigResource);
                    }
                    GioLogger.debug("read gio config from " + configFilePath);
                }
            } catch (IOException e) {
                throw new GIOMessageException("cant find gio sdk config", e);
            }
        }
    }

    public static void init(Properties properties) {
        if (inited.compareAndSet(false, true)) {
            if (properties != null) {
                prop.putAll(properties);
            } else {
                GioLogger.debug("custom properties is null, use default config in gio.properties");
            }

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