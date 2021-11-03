package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.UserMappingDto;
import io.growing.sdk.java.logger.GioLogger;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lijh
 * @version : 1.0.9
 * @since : 2021-08-16 11:15 PM
 */
public class GioCdpUserMappingMessage extends GioCDPMessage<UserMappingDto> implements Serializable {

    private static final long serialVersionUID = 8319050884751121301L;

    private final UserMappingDto event;

    private GioCdpUserMappingMessage(UserMappingDto.Builder builder) {
        event = builder.build();
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
    public UserMappingDto getMessage() {
        return event.toBuilder().setProjectKey(projectKey).setDataSourceId(dataSourceId).build();
    }

    @Override
    public boolean isIllegal() {
        if (event.getIdentifiesMap().isEmpty()) {
            GioLogger.error("GioCdpUserMappingMessage: identifies is empty");
            return true;
        }

        return false;
    }

    public static final class Builder {
        private final UserMappingDto.Builder builder = UserMappingDto.newBuilder();

        public GioCdpUserMappingMessage build() {
            return new GioCdpUserMappingMessage(builder);
        }

        public GioCdpUserMappingMessage.Builder addIdentities(String userKey, String userId) {
            if (userKey != null && userId != null) {
                builder.putIdentifies(userKey, userId);
            }
            return this;
        }

        public GioCdpUserMappingMessage.Builder addIdentities(Map<String, String> identifies) {
            if (identifies != null && !identifies.isEmpty()) {
                builder.putAllIdentifies(identifies);
            }
            return this;
        }

    }

}