package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.EventType;
import io.growing.collector.tunnel.protocol.EventV3Dto;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.utils.StringUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:33 PM
 */
public class GioCdpUserMessage extends GioCDPMessage<EventV3Dto> implements Serializable {

    private static final long serialVersionUID = -3409119784875248302L;

    private EventV3Dto user;

    private GioCdpUserMessage(EventV3Dto.Builder builder) {
        user = builder.setTimestamp(getTimeStampOrDefault(builder.getTimestamp()))
                                .setEventType(EventType.LOGIN_USER_ATTRIBUTES)
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
        return user.toBuilder().setProjectKey(projectKey).setDataSourceId(dataSourceId).build();
    }

    @Override
    public boolean isIllegal() {
        if (user.getAttributesMap().isEmpty()) {
            GioLogger.error("GioCdpUserMessage: attributes is empty");
            return true;
        }

        if (StringUtils.isBlank(user.getUserId()) && StringUtils.isBlank(user.getDeviceId())) {
            GioLogger.error("GioCdpUserMessage: userId and anonymousId are empty");
            return true;
        }

        return false;
    }

    public static final class Builder {
        private EventV3Dto.Builder builder = EventV3Dto.newBuilder();
        private static final String LIST_SPLIT = "||";

        public GioCdpUserMessage build() {
            return new GioCdpUserMessage(builder);
        }

        public Builder time(long timestamp) {
            builder.setTimestamp(timestamp);
            builder.setSendTime(timestamp);
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

        public Builder addUserVariable(String key, String value) {
            this.addVariableObject(key, value);
            return this;
        }

        public Builder addUserVariable(String key, int value) {
            this.addVariableObject(key, value);
            return this;
        }

        public Builder addUserVariable(String key, double value) {
            this.addVariableObject(key, value);
            return this;
        }

        public <T> Builder addUserVariable(String key, List<T> value) {
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

        private Builder addVariableObject(String key, Object value) {
            if (key != null && value != null) {
                key = key.trim();
                builder.putAttributes(key, String.valueOf(value));
            }
            return this;
        }

        public Builder addUserVariables(Map<String, Object> variables) {
            if (variables != null && !variables.isEmpty()) {
                for (Map.Entry<String, Object> entry: variables.entrySet()) {
                    this.addVariableObject(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }
    }

}