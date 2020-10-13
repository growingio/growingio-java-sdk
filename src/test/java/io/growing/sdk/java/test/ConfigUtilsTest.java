package io.growing.sdk.java.test;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.utils.ConfigUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 9/21/20 2:50 PM
 */
@RunWith(JUnit4.class)
public class ConfigUtilsTest {

    private void cleanConfigUtils() {
        try {
            Field field = ConfigUtils.class.getDeclaredField("inited");
            field.setAccessible(true);
            AtomicBoolean inited = (AtomicBoolean) field.get(null);
            inited.set(false);
            Field props = ConfigUtils.class.getDeclaredField("prop");
            props.setAccessible(true);
            Properties properties = (Properties) props.get(null);
            properties.clear();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read1DefaultConfig() {
        cleanConfigUtils();
        GrowingAPI obj = new GrowingAPI.Builder().build();
        assert (ConfigUtils.getStringValue("api.host", "").equals("http://cdp0:1598"));
        cleanConfigUtils();
        GrowingAPI.initConfig("gio-test.properties");
        assert (ConfigUtils.getStringValue("api.host", "").equals("http://gio-test:1598"));
        cleanConfigUtils();
        Properties properties = new Properties();
        properties.put("api.host", "http://custom-properties:1598");
        properties.put("run.mode", "test");
        GrowingAPI.initConfig(properties);
        assert (ConfigUtils.getStringValue("api.host", "").equals("http://custom-properties:1598"));
    }

}