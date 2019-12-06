package io.growing.sdk.java.store;

import io.growing.sdk.java.store.impl.DefaultStoreStrategy;
import io.growing.sdk.java.utils.ConfigUtils;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:20 PM
 */
public class StoreStrategyClient {

    private static class StoreInstance {
        static StoreStrategy defaultStoreStrategy = new DefaultStoreStrategy();
    }

    public static StoreStrategy getStoreInstance() {
        String strategy = ConfigUtils.getStringValue("msg.store.strategy", "default");

        if (!"default".equals(strategy)) {
            // TODO 暂仅支持 defaultStrategy
            return StoreInstance.defaultStoreStrategy;
        }

        return StoreInstance.defaultStoreStrategy;
    }
}