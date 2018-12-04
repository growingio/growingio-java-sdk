package io.growing.sdk.java.store;

import io.growing.sdk.java.dto.GIOMessage;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:22 PM
 */
public interface StoreStrategy {
    void push(GIOMessage msgList);

}
