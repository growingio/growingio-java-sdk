package io.growing.sdk.java;

import io.growing.sdk.java.constants.APIConstants;
import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.store.StoreStrategyClient;
import io.growing.sdk.java.utils.ConfigUtils;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:31 PM
 */
public class GrowingAPI {

    private final static RunMode runMode = RunMode.getByValue(ConfigUtils.getStringValue("run.mode", "test"));

    private static boolean netHealthyOk;

    static {
        netHealthyOk = FixThreadPoolSender.getNetProvider().isConnectedToGrowingAPIHost();

        if (!netHealthyOk) {
            GioLogger.error("cant connect to " + APIConstants.getApihost());
        }
    }

    /**
     * 添加埋点事件
     * @param msg
     */
    public static void send(GIOMessage msg){
        try{
            if (netHealthyOk) {
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