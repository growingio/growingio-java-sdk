package io.growing.sdk.java.dto;

import io.growing.collector.tunnel.protocol.EventDto;
import io.growing.collector.tunnel.protocol.UserDto;

import java.io.Serializable;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 12:33 PM
 */
public class GioCdpUserMessage extends GIOMessage implements Serializable {

    private static final long serialVersionUID = -5228910337644290100L;

    private UserDto user;

    private GioCdpUserMessage(UserDto.Builder builder) {
        user = builder.build();
    }

    public UserDto getUser() {
        return user;
    }

    public static final class Builder {
        private UserDto.Builder builder = UserDto.newBuilder();

        public GioCdpUserMessage build() {
            return new GioCdpUserMessage(builder);
        }

        public Builder setLoginUserId(String loginUserId) {
            builder.setUserId(loginUserId);
            builder.setGioId(loginUserId);
            return this;
        }

        public Builder addAttribute(String key, String value) {
            builder.putAttributes(key, value);
            return this;
        }

        public Builder addAttribute(String key, int value) {
            builder.putAttributes(key, String.valueOf(value));
            return this;
        }
    }

}