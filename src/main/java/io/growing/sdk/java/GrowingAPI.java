package io.growing.sdk.java;

import io.growing.sdk.java.constants.APIConstants;
import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.store.StoreStrategyClient;
import io.growing.sdk.java.utils.ConfigUtils;
import io.growing.sdk.java.utils.VersionInfo;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:31 PM
 */
public class GrowingAPI {

    private final static RunMode runMode = RunMode.getByValue(ConfigUtils.getStringValue("run.mode", "test"));

    private static boolean validDefaultConfig;

    static {
        validDefaultConfig = validDefaultConfig();
    }

    private static boolean validDefaultConfig(){
        GioLogger.debug("growingio-java-sdk version is " + VersionInfo.getVersion());

        String projectId = FixThreadPoolSender.getProjectId();
        if (projectId == null || projectId.isEmpty() || projectId.equals("填写您项目的AccountID")) {
            GioLogger.error("please set up your project accountID to gio.properties for key [project.id]");
            return false;
        }

        return FixThreadPoolSender.getNetProvider().connectedToGrowingAPIHost();
    }

    /**
     * 添加埋点事件
     * @param msg the event msg to upload
     */
    public static void send(GIOMessage msg){
        try{
            if (validDefaultConfig) {
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