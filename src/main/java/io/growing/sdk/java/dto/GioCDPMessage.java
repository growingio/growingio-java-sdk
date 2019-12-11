package io.growing.sdk.java.dto;

import com.google.protobuf.Message;

import java.io.Serializable;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 5:20 PM
 */
public abstract class GioCDPMessage<M extends Message> extends GIOMessage implements Serializable {
    private static final long serialVersionUID = -5789315589035420840L;

    protected String dataSourceId;

    public abstract void setDataSourceId(String dataSourceId);

    public abstract M getMessage();

    protected long getTimeStampOrDefault(long timestamp) {
        return timestamp == 0 ? System.currentTimeMillis() : timestamp;
    }
}