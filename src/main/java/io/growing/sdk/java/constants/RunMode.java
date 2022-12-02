package io.growing.sdk.java.constants;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-24 23:32
 */
public enum RunMode {
    TEST("test"),
    PRODUCTION("production");

    private String value;

    RunMode(String value) {
        this.value = value;
    }

    public static RunMode getByValue(String value) {
        for (RunMode mode : RunMode.values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        return TEST;
    }
}
