package io.growing.sdk.java.utils;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2019-05-07 12:50
 */
public class VersionInfo {
    private static String version = null;

    private static final String defaultVersion = "1.0.15-cdp";

    public static String getVersion() {
        if (version == null) {
            Class<?> cls = VersionInfo.class;
            version = cls.getPackage().getImplementationVersion();
            if (version == null || version.length() == 0) {
                version = cls.getPackage().getSpecificationVersion();
            }
            return version == null ? defaultVersion : version;
        } else {
            return version;
        }

    }
}