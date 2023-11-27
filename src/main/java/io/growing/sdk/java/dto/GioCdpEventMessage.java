package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.EventType;
import io.growing.collector.tunnel.protocol.EventV3Dto;
import io.growing.collector.tunnel.protocol.ResourceItem;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.utils.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:33 PM
 */
public class GioCdpEventMessage extends GioCDPMessage<EventV3Dto> implements Serializable {

    private static final long serialVersionUID = -2414503426226355459L;

    public static final String XEI_USER_KEY = "$notuser";

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
        if (StringUtils.isBlank(event.getEventName())) {
            GioLogger.error("GioCdpEventMessage: eventName is empty");
            return true;
        }

        if (!XEI_USER_KEY.equals(event.getUserKey())) {
            if (StringUtils.isBlank(event.getUserId()) && StringUtils.isBlank(event.getDeviceId())) {
                GioLogger.error("GioCdpEventMessage: userId and anonymousId are empty");
                return true;
            }
        }

        return false;
    }

    public static final class Builder {
        private final EventV3Dto.Builder builder = EventV3Dto.newBuilder();

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

        public Builder domain(String domain) {
            if (domain != null) {
                builder.setDomain(domain);
            }
            return this;
        }

        public Builder urlScheme(String urlScheme) {
            if (urlScheme != null) {
                builder.setUrlScheme(urlScheme);
            }
            return this;
        }

        public Builder deviceBrand(String deviceBrand) {
            if (deviceBrand != null) {
                builder.setDeviceBrand(deviceBrand);
            }
            return this;
        }

        public Builder deviceModel(String deviceModel) {
            if (deviceModel != null) {
                builder.setDeviceModel(deviceModel);
            }
            return this;
        }

        public Builder deviceType(String deviceType) {
            if (deviceType != null) {
                builder.setDeviceType(deviceType);
            }
            return this;
        }

        public Builder appVersion(String appVersion) {
            if (appVersion != null) {
                builder.setAppVersion(appVersion);
            }
            return this;
        }

        public Builder appName(String appName) {
            if (appName != null) {
                builder.setAppName(appName);
            }
            return this;
        }

        public Builder language(String language) {
            if (language != null) {
                builder.setLanguage(language);
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
                builder.putAttributes(key, StringUtils.list2Str(value));
            }

            return this;
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