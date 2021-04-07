package io.growing.sdk.java.constants;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2020/10/29
 */
public enum StoreStrategyEnum {

    DEFAULT("default"),
    ABORT_POLICY("abortPolicy");

    private String value;

    StoreStrategyEnum(String value) {
        this.value = value;
    }

    public static StoreStrategyEnum getByValue(String value) {
        for (StoreStrategyEnum mode : StoreStrategyEnum.values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        return DEFAULT;
    }
}
