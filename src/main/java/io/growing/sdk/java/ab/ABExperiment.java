package io.growing.sdk.java.ab;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ABExperiment {

    private final String layerId;
    private final long strategyId;
    private final long experimentId;
    private String expLayerName;
    private String expName;
    private String expStrategyName;
    private final Map<String, String> variables;

    public ABExperiment(String layerId, long strategyId, long experimentId, Map<String, String> variables) {
        this.layerId = layerId;
        this.strategyId = strategyId;
        this.experimentId = experimentId;
        if (variables == null) {
            this.variables = Collections.emptyMap();
        } else {
            this.variables = variables;
        }
    }

    public void setExperimentNames(String expLayerName, String expName, String expStrategyName) {
        this.expLayerName = expLayerName;
        this.expName = expName;
        this.expStrategyName = expStrategyName;
    }

    public String getLayerId() {
        return layerId;
    }

    public long getStrategyId() {
        return strategyId;
    }

    public long getExperimentId() {
        return experimentId;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public String getExpLayerName() {
        return expLayerName;
    }

    public String getExpName() {
        return expName;
    }

    public String getExpStrategyName() {
        return expStrategyName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ABExperiment) {
            ABExperiment abExperiment = (ABExperiment) obj;
            return abExperiment.getLayerId().equals(layerId)
                    && abExperiment.getStrategyId() == strategyId
                    && abExperiment.getExperimentId() == experimentId
                    && abExperiment.getVariables().equals(variables);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(layerId).append("$").append(strategyId).append("$").append(experimentId);
        if (variables != null && !variables.isEmpty()) {
            Set<String> keySets = variables.keySet();
            sb.append("$");
            for (String key : keySets) {
                sb.append(key).append("=").append(variables.get(key));
            }
        }
        return sb.toString();
    }


}