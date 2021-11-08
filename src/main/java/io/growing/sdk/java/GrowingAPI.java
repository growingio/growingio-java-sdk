package io.growing.sdk.java;

import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCDPMessage;
import io.growing.sdk.java.exception.GIOSendBeRejectedException;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.store.StoreStrategy;
import io.growing.sdk.java.store.StoreStrategyClient;
import io.growing.sdk.java.utils.ConfigUtils;
import io.growing.sdk.java.utils.StringUtils;
import io.growing.sdk.java.utils.VersionInfo;

import java.util.Properties;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:31 PM
 */
public class GrowingAPI {

    private static final boolean validDefaultConfig;
    private final String projectKey;
    private final String dataSourceId;
    private static final StoreStrategy strategy = StoreStrategyClient.getStoreInstance(StoreStrategyClient.CURRENT_STRATEGY);

    static {
        ConfigUtils.initDefault();
        validDefaultConfig = validDefaultConfig();
    }

    private GrowingAPI(Builder builder) {
        this.dataSourceId = builder.dataSourceId;
        this.projectKey = builder.projectKey;
    }

    private static boolean validDefaultConfig() {
        GioLogger.debug("growingio-java-sdk version is " + VersionInfo.getVersion() + ", running in mode: " + RunMode.getCurrentMode());

        return RunMode.isTestMode() || FixThreadPoolSender.getNetProvider().connectedToGrowingAPIHost();
    }

    /**
     * 添加埋点事件.
     *
     * @param msg the event message to upload
     */
    public void send(GIOMessage msg) {
        try {
            if (validDefaultConfig && businessVerification(msg)) {
                strategy.push(msg);
            }
        } catch (Exception e) {
            GioLogger.error("failed to send msg, " + e.toString());
        }
    }

    /**
     * 添加埋点事件，当队列满时，可以抛出拒绝异常.
     *
     * @param msg the event msg to upload
     */
    public void sendMaybeRejected(GIOMessage msg) throws GIOSendBeRejectedException {
        try {
            if (validDefaultConfig && businessVerification(msg)) {
                strategy.push(msg);
            }
        } catch (GIOSendBeRejectedException e) {
            throw e;
        } catch (Exception e) {
            GioLogger.error("failed to send msg, " + e.toString());
        }
    }

    private boolean businessVerification(GIOMessage msg) {
        if (StringUtils.nonBlank(this.projectKey)) {
            msg.setProjectKey(this.projectKey);
        } else {
            GioLogger.error("projectKey cant be null or empty string");
            return false;
        }

        if (msg instanceof GioCDPMessage) {
            if (StringUtils.nonBlank(this.dataSourceId)) {
                ((GioCDPMessage<?>) msg).setDataSourceId(this.dataSourceId);
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

    /**
     * 配置文件路径读取.
     * 如果不需要指定配置文件路径，则默认加载 gio.properties
     * 如果需要指定配置文件路径，则需要在 GrowingAPI 初始化之前调用 initConfig, 进行配置初始化
     *
     * @param configFilePath
     */
    public static void initConfig(String configFilePath) {
        ConfigUtils.init(configFilePath);
    }

    /**
     * 如果需要自定义 Properties 进行配置初始化，则需要在 GrowingAPI 初始化之前调用 initConfig, 进行配置初始化.
     * 自定义 properties key 参考 gio_default.properties 文件
     *
     * @param properties
     */
    public static void initConfig(Properties properties) {
        ConfigUtils.init(properties);
    }

    /**
     * showdownNow 将会直接调用sdk中线程池的shutdownNow.
     */
    public static void shutdownNow() {
        strategy.shutDownNow();
    }

    /**
     * shutdown 将会依照配置信息，允许执行之前提交的任务直到完成或超时.
     */
    public static void shutdown() {
        strategy.awaitTerminationAfterShutdown();
    }
}