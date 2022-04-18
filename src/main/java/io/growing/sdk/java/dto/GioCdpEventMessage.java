package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.EventType;
import io.growing.collector.tunnel.protocol.EventV3Dto;
import io.growing.collector.tunnel.protocol.ResourceItem;
import io.growing.sdk.java.logger.GioLogger;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:33 PM
 */
public class GioCdpEventMessage extends GioCDPMessage<EventV3Dto> implements Serializable {

    private static final long serialVersionUID = -2414503426226355459L;

    private final EventV3Dto event;

    private GioCdpEventMessage(EventV3Dto.Builder builder) {
        event = builder.setTimestamp(getTimeStampOrDefault(builder.getTimestamp()))
                        .setEventType(EventType.CUSTOM)
                        .build();
    }

    @Override
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    @Override
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Override
    public EventV3Dto getMessage() {
        return event.toBuilder().setProjectKey(projectKey).setDataSourceId(dataSourceId).build();
    }

    @Override
    public boolean isIllegal() {
        if (event.getEventName().isEmpty()) {
            GioLogger.error("GioCdpEventMessage: eventName is empty");
            return true;
        }

        return false;
    }

    public static final class Builder {
        private final EventV3Dto.Builder builder = EventV3Dto.newBuilder();
        private static final String LIST_SPLIT = "||";

        public GioCdpEventMessage build() {
            return new GioCdpEventMessage(builder);
        }

        public Builder eventTime(long eventTime) {
            builder.setTimestamp(eventTime);
            builder.setSendTime(eventTime);
            return this;
        }

        public Builder eventKey(String eventKey) {
            if (eventKey != null) {
                builder.setEventName(eventKey);
            }
            return this;
        }

        public Builder loginUserId(String loginUserId) {
            if (loginUserId != null) {
                builder.setUserId(loginUserId);
                builder.setGioId(loginUserId);
            }
            return this;
        }

        public Builder loginUserKey(String loginUserKey) {
            if (loginUserKey != null) {
                builder.setUserKey(loginUserKey);
            }
            return this;
        }

        public Builder anonymousId (String anonymousId) {
            if (anonymousId != null) {
                builder.setDeviceId(anonymousId);
            }
            return this;
        }

        @Deprecated
        public Builder eventNumValue(Number numValue) {
            // DO NOTHING
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
            if (variables != null && !variables.isEmpty()) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    addVariableObject(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        public <T> Builder addEventVariable(String key, List<T> value) {
            if (key != null && value != null && !value.isEmpty()) {
                StringBuilder valueBuilder = new StringBuilder();
                Iterator<T> iterator = value.iterator();
                if (iterator.hasNext()) {
                    valueBuilder.append(toString(iterator.next()));
                    while (iterator.hasNext()) {
                        valueBuilder.append(LIST_SPLIT);
                        valueBuilder.append(toString(iterator.next()));
                    }
                }
                builder.putAttributes(key, valueBuilder.toString());
            }

            return this;
        }

        private String toString(Object value) {
            if (value == null) {
                return "";
            } else {
                return String.valueOf(value);
            }
        }

        public Builder addItem(String id, String key) {
            addItem(id, key, null);
            return this;
        }

        public Builder addItem(String id, String key, Map<String, String> variables) {
            if (id != null && key != null) {
                ResourceItem.Builder resourceItemBuilder = ResourceItem.newBuilder();
                resourceItemBuilder.setId(id);
                resourceItemBuilder.setKey(key);
                if (variables != null) {
                    resourceItemBuilder.putAllAttributes(variables);
                }
                builder.setResourceItem(resourceItemBuilder.build());
            }
            return this;
        }

        private Builder addVariableObject(String key, Object value) {
            if (key != null && value != null) {
                key = key.trim();
                builder.putAttributes(key, String.valueOf(value));
            }

            return this;
        }

    }

}