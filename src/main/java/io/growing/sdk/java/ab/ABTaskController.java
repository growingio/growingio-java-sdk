package io.growing.sdk.java.ab;

import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.store.StoreStrategy;
import io.growing.sdk.java.thread.GioThreadNamedFactory;
import io.growing.sdk.java.utils.MessageUtils;
import io.growing.sdk.java.utils.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ABTaskController {
    private static final ExecutorService abTaskThreadPool = Executors.newSingleThreadExecutor(new GioThreadNamedFactory("gio-ab-sender"));
    private static final ABTestHttpUrlProvider netProvider = new ABTestHttpUrlProvider();
    private StoreStrategy strategy;

    public ABTaskController(StoreStrategy strategy) {
        this.strategy = strategy;
    }

    public void submitABTask(final String projectId, final String dataSourceId, final String layerId, final String distinctId, final ABTestCallback callback) {
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(dataSourceId) || StringUtils.isBlank(distinctId) || callback == null) {
            GioLogger.error("submitABTask:params is illegal");
            return;
        }

        try {
            ABTestResponse response = netProvider.requestABTestExperimentData(projectId, dataSourceId, layerId, distinctId);
            if (response.isSucceed()) {
                callback.onABExperimentReceived(response.abExperiment);
                sendAbTestTrackEvent(response.abExperiment, projectId, dataSourceId, distinctId);
            } else {
                callback.onABExperimentFailed(new IllegalAccessException(response.getErrorMsg()));
            }
        } catch (Exception e) {
            GioLogger.error("submitABTask failed: " + e.getLocalizedMessage());
        }
    }

    public void submitABTaskSync(final String projectId, final String dataSourceId, final String layerId, final String distinctId, final ABTestCallback callback) {
        submitABTask(projectId, dataSourceId, layerId, distinctId, callback);
    }

    public void submitABTaskAsync(final String projectId, final String dataSourceId, final String layerId, final String distinctId, final ABTestCallback callback) {
        abTaskThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                submitABTask(projectId, dataSourceId, layerId, distinctId, callback);
            }
        });
    }

    private void sendAbTestTrackEvent(ABExperiment abExperiment, String projectId, String dataSourceId, String distinctId) {
        if (abExperiment.getExperimentId() == 0 && abExperiment.getStrategyId() == 0) {
            return;
        }
        GioCdpEventMessage eventMessage = new GioCdpEventMessage.Builder()
                .eventKey("$exp_hit")
                .anonymousId(distinctId)
                .addEventVariable("$exp_id", String.valueOf(abExperiment.getExperimentId()))
                .addEventVariable("$exp_strategy_id", String.valueOf(abExperiment.getStrategyId()))
                .addEventVariable("$exp_layer_id", abExperiment.getLayerId())
                .addEventVariable("$exp_layer_name", abExperiment.getExpLayerName())
                .addEventVariable("$exp_name", abExperiment.getExpName())
                .addEventVariable("$exp_strategy_name", abExperiment.getExpStrategyName())
                .build();

        if (MessageUtils.businessVerification(eventMessage, projectId, dataSourceId)) {
            strategy.push(eventMessage);
        }
    }
}
