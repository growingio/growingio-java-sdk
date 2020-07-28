package io.growing.sdk.java.sender;

import io.growing.sdk.java.sender.net.ContentTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 11:00 AM
 */
public class RequestDto {
    private final String url;
    private final ContentTypeEnum contentType;
    private final Map<String, String> headers;
    private final byte[] bytes;

    private RequestDto(Builder builder) {
       this.url = builder.url;
       this.contentType = builder.contentType;
       this.bytes = builder.bytes;
       this.headers = builder.headers;
    }

    @Override
    public String toString() {
        return "request: " + this.url + " with " + this.contentType + " data size : " + this.getBytes().length;
    }

    public static class Builder {
        private String url;
        private ContentTypeEnum contentType;
        private byte[] bytes;
        private Map<String, String> headers = new HashMap<String, String>();

        public RequestDto build() {
            return new RequestDto(this);
        }

        public Builder setContentType(ContentTypeEnum contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder setBytes(byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }
    }

    public String getUrl() {
        return url;
    }

    public ContentTypeEnum getContentType() {
        return contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBytes() {
        return bytes;
    }
}