package io.growing.sdk.java.logger;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.utils.ConfigUtils;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-24 17:38
 */
public class GioLogger {
    private static GioLoggerInterface logger;
    private static String loggerLevel;

    static {
        String loggerImplName = ConfigUtils.getStringValue("logger.implemention", "io.growing.sdk.java.logger.GioLoggerImpl");

        loggerLevel = ConfigUtils.getStringValue("logger.level", "error");

        try {
            final Class<?> loggerImplClass = Class.forName(loggerImplName);
            logger = (GioLoggerInterface) loggerImplClass.newInstance();
        } catch (ClassNotFoundException e) {
            logger = getDefaultLogger();
            logger.error("use gio logger, cause failed to find logger implemention class " + loggerImplName);
        } catch (IllegalAccessException e) {
            logger = getDefaultLogger();
            logger.error("use gio logger, cause failed to construct logger implemention class " + loggerImplName);
        } catch (InstantiationException e) {
            logger = getDefaultLogger();
            logger.error("use gio logger, cause failed to instantiate logger implemention class " + loggerImplName);
        }
    }

    public static GioLoggerInterface getDefaultLogger() {
        return new GioLoggerImpl();
    }

    public static void debug(String msg) {
        if (!loggerLevel.equals("error")) {
            logger.debug(msg);
        }
    }

    public static void error(String msg) {
        logger.error(msg);
    }
}