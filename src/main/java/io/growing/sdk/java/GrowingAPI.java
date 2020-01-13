package io.growing.sdk.java;

import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCDPMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.store.StoreStrategyClient;
import io.growing.sdk.java.utils.StringUtils;
import io.growing.sdk.java.utils.VersionInfo;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:31 PM
 */
public class GrowingAPI {

    private static boolean validDefaultConfig;
    private String projectKey;
    private String dataSourceId;

    private GrowingAPI(Builder builder) {
        this.dataSourceId = builder.dataSourceId;
        this.projectKey = builder.projectKey;
    }

    static {
        validDefaultConfig = validDefaultConfig();
    }

    private static boolean validDefaultConfig() {
        GioLogger.debug("growingio-java-sdk version is " + VersionInfo.getVersion() + ", running in mode: " + RunMode.getCurrentMode());

        return RunMode.isTestMode() || FixThreadPoolSender.getNetProvider().connectedToGrowingAPIHost();
    }

    /**
     * 添加埋点事件.
     * @param msg the event message to upload
     */
    public void send(GIOMessage msg) {
        try {
            if (validDefaultConfig && businessVerification(msg)) {
                StoreStrategyClient.getStoreInstance().push(msg);
            }
        } catch (Exception e) {
            GioLogger.error("failed to send msg, " + e.toString());
        }
    }

    private boolean businessVerification(GIOMessage msg){
        if (StringUtils.nonBlank(this.projectKey)) {
            msg.setProjectKey(this.projectKey);
        } else {
            GioLogger.error("projectKey cant be null or empty string");
            return false;
        }

        if (msg instanceof GioCDPMessage) {
            if (StringUtils.nonBlank(this.dataSourceId)) {
                ((GioCDPMessage) msg).setDataSourceId(this.dataSourceId);
            } else {
                GioLogger.error("cdp message datasourceId cant be null or empty string");
                return false;
            }
        }
        return true;
    }

    public static class Builder {
        private String dataSourceId;
        private String projectKey;

        public Builder setDataSourceId(String dataSourceId) {
            this.dataSourceId = dataSourceId;
            return this;
        }

        public Builder setProjectKey(String projectKey) {
            this.projectKey = projectKey;
            return this;
        }

        public GrowingAPI build() {
            return new GrowingAPI(this);
        }
    }

}