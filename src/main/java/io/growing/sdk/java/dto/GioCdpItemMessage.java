package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.ItemDto;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:33 PM
 */
public class GioCdpItemMessage extends GioCDPMessage<ItemDto> implements Serializable {

    private static final long serialVersionUID = -2411957894363769098L;

    private final ItemDto event;

    private GioCdpItemMessage(ItemDto.Builder builder) {
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
    public ItemDto getMessage() {
        return event.toBuilder().setProjectKey(projectKey).setDataSourceId(dataSourceId).build();
    }

    @Override
    public boolean isIllegal() {
        if (event.getId().isEmpty() || event.getKey().isEmpty()) {
            GioLogger.error("GioCdpItemMessage: id or key is empty");
            return true;
        }

        return false;
    }

    public static final class Builder {
        private final ItemDto.Builder builder = ItemDto.newBuilder();

        public GioCdpItemMessage build() {
            return new GioCdpItemMessage(builder);
        }

        public Builder id(String id) {
            if (id != null) {
                builder.setId(id);
            }
            return this;
        }

        public Builder key(String key) {
            if (key != null) {
                builder.setKey(key);
            }
            return this;
        }

        public <T> Builder addItemVariable(String key, List<T> value) {
            if (key != null && value != null && !value.isEmpty()) {
                builder.putAttributes(key, StringUtils.list2Str(value));
            }

            return this;
        }

        public Builder addItemVariable(String key, String value) {
            if (key != null && value != null) {
                builder.putAttributes(key, value);
            }
            return this;
        }

    }

}