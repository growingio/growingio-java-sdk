package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.UserDto;

import java.io.Serializable;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:33 PM
 */
public class GioCdpUserMessage extends GioCDPMessage<UserDto> implements Serializable {

    private static final long serialVersionUID = -5228910337644290100L;

    private UserDto user;

    private GioCdpUserMessage(UserDto.Builder builder) {
        user = builder.setTimestamp(getTimeStampOrDefault(builder.getTimestamp())).build();
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
    public UserDto getMessage() {
        return user.toBuilder().setProjectKey(projectKey).setDataSourceId(dataSourceId).build();
    }

    public static final class Builder {
        private UserDto.Builder builder = UserDto.newBuilder();

        public GioCdpUserMessage build() {
            return new GioCdpUserMessage(builder);
        }

        public Builder time(long timestamp) {
            builder.setTimestamp(timestamp);
            return this;
        }

        public Builder loginUserId(String loginUserId) {
            builder.setUserId(loginUserId);
            builder.setGioId(loginUserId);
            return this;
        }

        public Builder anonymousId (String anonymousId) {
            builder.setAnonymousId(anonymousId);
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