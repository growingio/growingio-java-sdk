package io.growing.sdk.java.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:33 PM
 */
public class GIOEventMessage extends GIOMessage implements Serializable {

    private static final long serialVersionUID = -5228910337644290100L;

    // 事件发生时间时间戳（毫秒）
    private static final String tm = "tm";
    // 事件标识
    private static final String n = "n";
    // 数值类型自定义事件使用
    private static final String num = "num";
    // 所有的自定义事件变量
    private static final String var = "var";
    // 用户登录ID
    private static final String cs1 = "cs1";
    private static final String t = "t";

    private Map<String, Object> mapResult;

    private GIOEventMessage(Builder builder) {
        this.mapResult = builder.builderMap;
        this.mapResult.put(t, "cstm");
        if (this.mapResult.get(tm) == null) {
            this.mapResult.put(tm, System.currentTimeMillis());
        }
    }

    public static Builder newMessage() {
        return new Builder();
    }

    public String getN() {
        Object nValue = this.mapResult.get(n);
        if (nValue instanceof String) {
            return nValue.toString();
        } else {
            return null;
        }
    }

    public String getCs1() {
        Object cs1Value = this.mapResult.get(cs1);
        if (cs1Value instanceof String) {
            return cs1Value.toString();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Object> getMapResult() {
        return this.mapResult;
    }

    public static final class Builder {
        private Map<String, Object> eventVar = new HashMap<String, Object>();
        private Map<String, Object> builderMap = new HashMap<String, Object>();

        public GIOEventMessage build() {
            return new GIOEventMessage(this);
        }

        public Builder eventTime(Long eventTime) {
            this.builderMap.put(tm, eventTime);
            return this;
        }

        public Builder eventKey(String eventKey) {
            this.builderMap.put(n, eventKey);
            return this;
        }

        public Builder loginUserId(String loginUserId) {
            this.builderMap.put(cs1, loginUserId);
            return this;
        }

        public Builder eventNumValue(Number numValue) {
            if (numValue != null) {
                double value = numValue.doubleValue();
                if (value >= 0) {
                    this.builderMap.put(num, value);
                }
            }

            return this;
        }

        public Builder addEventVariable(String key, Integer value) {
            addEventVariableObject(key, value);
            return this;
        }

        public Builder addEventVariable(String key, Double value) {
            addEventVariableObject(key, value);
            return this;
        }

        public Builder addEventVariable(String key, String value) {
            addEventVariableObject(key, value);
            return this;
        }

        private Builder addEventVariableObject(String key, Object value) {
            if (key != null) {
                key = key.trim();

                if (value instanceof String) {
                    String val = value.toString();
                    if (val.length() > 255) {
                        this.eventVar.put(key, val.substring(0, 255));
                    } else {
                        this.eventVar.put(key, value);
                    }
                } else {
                    this.eventVar.put(key, value);
                }

                this.builderMap.put(var, this.eventVar);
            }


            return this;
        }

    }
}