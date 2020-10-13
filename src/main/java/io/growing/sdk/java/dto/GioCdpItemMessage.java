package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.ItemDto;

import java.io.Serializable;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:33 PM
 */
public class GioCdpItemMessage extends GioCDPMessage<ItemDto> implements Serializable {

    private static final long serialVersionUID = -5228910337644290100L;

    private final ItemDto event;

    private GioCdpItemMessage(ItemDto.Builder builder) {
        event = builder.build();
    }

    @Override
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Override
    public ItemDto getMessage() {
        return event.toBuilder().setProjectKey(projectKey).setDataSourceId(dataSourceId).build();
    }

    @Override
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public static final class Builder {
        private final ItemDto.Builder builder = ItemDto.newBuilder();

        public GioCdpItemMessage build() {
            return new GioCdpItemMessage(builder);
        }

        public Builder id(String id) {
            builder.setId(id);
            return this;
        }

        public Builder key(String key) {
            builder.setKey(key);
            return this;
        }

        public Builder addItemVariable(String key, String value) {
            builder.putAttributes(key, value);
            return this;
        }

    }

}