package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.EventDto;

import java.io.Serializable;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:33 PM
 */
public class GioCdpEventMessage extends GIOMessage implements Serializable {

    private static final long serialVersionUID = -5228910337644290100L;

    private EventDto event;

    private GioCdpEventMessage(EventDto.Builder builder) {
        event = builder.setType(EventDto.EventType.CUSTOM_EVENT).build();
    }

    public EventDto getEvent() {
        return event;
    }

    public static final class Builder {
        private EventDto.Builder builder = EventDto.newBuilder();

        public GioCdpEventMessage build() {
            return new GioCdpEventMessage(builder);
        }

        public Builder eventTime(Long eventTime) {
            builder.setTimestamp(eventTime);
            return this;
        }

        public Builder eventKey(String eventKey) {
            builder.setEventKey(eventKey);
            return this;
        }

        public Builder loginUserId(String loginUserId) {
            builder.setUserId(loginUserId);
            return this;
        }

        public Builder eventNumValue(Number numValue) {
            if (numValue != null) {
                double value = numValue.doubleValue();
                if (value >= 0) {
                    builder.setEventNum(value);
                }
            }

            return this;
        }

        public Builder addEventVariable(String key, Integer value) {
            addVariableObject(key, value);
            return this;
        }

        public Builder addEventVariable(String key, Double value) {
            addVariableObject(key, value);
            return this;
        }

        public Builder addEventVariable(String key, String value) {
            addVariableObject(key, value);
            return this;
        }

        public Builder addEventVariables(Map<String, Object> variables) {
            if (variables != null && !variables.isEmpty()){
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    addVariableObject(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        private Builder addVariableObject(String key, Object value) {
            if (key != null && value != null) {
                key = key.trim();

                if (value instanceof String) {
                    String val = value.toString();
                    if (val.length() > 255) {
                        builder.putAttributes(key, val.substring(0, 255));
                    } else {
                        builder.putAttributes(key, String.valueOf(value));
                    }
                } else {
                    builder.putAttributes(key, String.valueOf(value));
                }
            }


            return this;
        }

    }

}