package io.growing.sdk.java.ab;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ABTestResponse {

    private static final String LAYER_ID = "layerId";
    private static final String LAYER_NAME = "layerName";
    private static final String STRATEGY_ID = "strategyId";
    private static final String STRATEGY_NAME = "strategyName";
    private static final String EXPERIMENT_ID = "experimentId";
    private static final String EXPERIMENT_NAME = "experimentName";
    private static final String VARIABLES = "variables";

    private int code = -1;
    private String errorMsg;

    ABExperiment abExperiment;

    public int getCode() {
        return code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public static ABTestResponse parseHttpJson(String layerId, String json) {
        final ABTestResponse response = new ABTestResponse();
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            response.code = code;
            if (code == 0) {
                long experimentId = jsonObject.optLong(EXPERIMENT_ID);
                long strategyId = jsonObject.optLong(STRATEGY_ID);
                JSONObject variables = jsonObject.optJSONObject(VARIABLES);
                Map<String, String> variableMap = new HashMap<String, String>();
                if (variables != null) {
                    Iterator<String> keys = variables.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        variableMap.put(key, variables.get(key).toString());
                    }
                }
                response.abExperiment = new ABExperiment(layerId, strategyId, experimentId, variableMap);
                String expLayerName = jsonObject.optString(LAYER_NAME);
                String expName = jsonObject.optString(EXPERIMENT_NAME);
                String expStrategyName = jsonObject.optString(STRATEGY_NAME);
                response.abExperiment.setExperimentNames(expLayerName, expName, expStrategyName);
            } else {
                response.errorMsg = jsonObject.getString("errorMsg");
            }
        } catch (JSONException e) {
            response.code = -1;
            response.errorMsg = "parse error:Illegal ABExperiment";
        }
        return response;
    }

    public ABExperiment getABExperiment() {
        return abExperiment;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    boolean isSucceed() {
        return code == 0;
    }
}
