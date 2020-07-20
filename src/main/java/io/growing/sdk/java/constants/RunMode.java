package io.growing.sdk.java.constants;

import io.growing.sdk.java.utils.ConfigUtils;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-24 23:32
 */
public enum RunMode {
    TEST("test"),
    PRODUCTION("production");

    private final String value;
    private static RunMode currentMode;

    RunMode(String value) {
        this.value = value;
    }

    public static RunMode getByValue(String value) {
        for (RunMode mode: RunMode.values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        return TEST;
    }

    public static RunMode getCurrentMode() {
        if (currentMode == null) {
            currentMode = RunMode.getByValue(ConfigUtils.getStringValue("run.mode", "test"));
        }
        return currentMode;
    }

    public static Boolean isTestMode() {
        return currentMode == TEST;
    }

    public static Boolean isProductionMode() {
        return currentMode == PRODUCTION;
    }
}
