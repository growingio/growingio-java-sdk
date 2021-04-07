package io.growing.sdk.java.store;

import io.growing.sdk.java.constants.StoreStrategyEnum;
import io.growing.sdk.java.store.impl.AbortPolicyStoreStrategy;
import io.growing.sdk.java.store.impl.DefaultStoreStrategy;
import io.growing.sdk.java.utils.ConfigUtils;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/20/18 7:20 PM
 */
public class StoreStrategyClient {

    public static final StoreStrategyEnum CURRENT_STRATEGY = StoreStrategyEnum.getByValue(ConfigUtils.getStringValue("msg.store.strategy", "default"));

    private static class StoreInstance {
        static StoreStrategy defaultStoreStrategy = new DefaultStoreStrategy();
        static StoreStrategy abortPolicyStoreStrategy = new AbortPolicyStoreStrategy();
    }

    public static StoreStrategy getStoreInstance(StoreStrategyEnum strategy) {
        StoreStrategy storeStrategy;
        if (strategy == StoreStrategyEnum.ABORT_POLICY) {
            storeStrategy = StoreInstance.abortPolicyStoreStrategy;
        } else {
            storeStrategy = StoreInstance.defaultStoreStrategy;
        }
        storeStrategy.shutdownAwait();
        return storeStrategy;
    }

}