package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.UserMappingDto;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lijh
 * @version : 1.0.10
 * @since : 2021-08-16 11:15 PM
 */
public class GioCdpUserMappingMessage extends GioCDPMessage<UserMappingDto> implements Serializable {

    private static final long serialVersionUID = -2300267756742441937L;
    private final UserMappingDto event;

    private GioCdpUserMappingMessage(UserMappingDto.Builder builder) {
        event = builder.build();
    }

    @Override
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Override
    public UserMappingDto getMessage() {
        return event.toBuilder().setProjectKey(projectKey).setDataSourceId(dataSourceId).build();
    }

    @Override
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public static final class Builder {
        private final UserMappingDto.Builder builder = UserMappingDto.newBuilder();

        public GioCdpUserMappingMessage build() {
            return new GioCdpUserMappingMessage(builder);
        }

        public GioCdpUserMappingMessage.Builder addIdentities(String userKey, String userId) {
            builder.putIdentifies(userKey, userId);
            return this;
        }

        public GioCdpUserMappingMessage.Builder addIdentities(Map<String, String> identifies) {
            builder.putAllIdentifies(identifies);
            return this;
        }

    }

}