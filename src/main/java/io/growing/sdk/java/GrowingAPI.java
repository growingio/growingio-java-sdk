package io.growing.sdk.java;

import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.store.StoreStrategyClient;
import io.growing.sdk.java.utils.ConfigUtils;
import io.growing.sdk.java.utils.StringUtils;
import io.growing.sdk.java.utils.VersionInfo;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:31 PM
 */
public class GrowingAPI {

    private final static RunMode runMode;
    private final static String projectId;

    private final static boolean validDefaultConfig;

    static {
        ConfigUtils.initDefault();
        runMode = RunMode.getByValue(ConfigUtils.getStringValue("run.mode", "test"));
        projectId = ConfigUtils.getStringValue("project.id", "");
        validDefaultConfig = validDefaultConfig();
    }

    private static boolean validDefaultConfig() {
        GioLogger.debug("growingio-java-sdk version is " + VersionInfo.getVersion());
        if (StringUtils.isBlank(projectId) || "填写您项目的AccountID".equals(projectId)) {
            GioLogger.error("please set up your default project accountID to gio.properties for key [project.id]");
            return false;
        }

        return FixThreadPoolSender.getNetProvider().isConnectedToGrowingAPIHost();
    }

    /**
     * 添加埋点事件
     *
     * @param msg the event msg to upload
     */
    public static void send(GIOMessage msg) {
        send(msg, projectId);
    }

    public static void send(GIOMessage msg, String projectId) {
        try {
            if (validDefaultConfig) {
                if (StringUtils.nonBlank(projectId)) {
                    msg.setProjectId(projectId);
                } else {
                    GioLogger.error("projectId cant be null or empty string");
                    return;
                }
                StoreStrategyClient.getStoreInstance().push(msg);
            }
        } catch (Exception e) {
            GioLogger.error("failed to send msg, " + e.toString());
        }
    }

    public static RunMode getRunMode() {
        return runMode;
    }

    public static boolean isTestMode() {
        return getRunMode() == RunMode.TEST;
    }

    public static boolean isProductionMode() {
        return getRunMode() == RunMode.PRODUCTION;
    }
}