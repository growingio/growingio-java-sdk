package io.growing.sdk.java.test;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.store.StoreStrategyClient;
import io.growing.sdk.java.store.impl.AbortPolicyStoreStrategy;
import io.growing.sdk.java.utils.ConfigUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(JUnit4.class)
public class Case0PropertiesTest {
    private static final String PROJECT_KEY = "91eaf9b283361032";
    private static final String DATASOURCE_ID = "a390a68c7b25638c";

    private static GrowingAPI sender;

    @Test
    public void checkProperties() {
        setStaticField(ConfigUtils.class, "inited", new AtomicBoolean(false));
        setStaticField(RunMode.class, "currentMode", null);

        // set properties
        final String MAGINC_NUMBER = "666";

        Properties properties = new Properties();

        properties.setProperty("api.host", "http://localhost:8080");
        properties.setProperty("project.id", "123456654321");
        properties.setProperty("msg.store.strategy", "abortPolicy");
        properties.setProperty("run.mode", "production");
        properties.setProperty("logger.level", "error");

        properties.setProperty("send.msg.thread", MAGINC_NUMBER);
        properties.setProperty("send.msg.interval", MAGINC_NUMBER + 1);
        properties.setProperty("msg.store.queue.size", MAGINC_NUMBER + 2);

        // init properties
        GrowingAPI.initConfig(properties);
        sender = new GrowingAPI.Builder().setDataSourceId(DATASOURCE_ID).setProjectKey(PROJECT_KEY).build();

        // check properties
        Assert.assertEquals(ConfigUtils.getStringValue("api.host", ""), "http://localhost:8080");
        Assert.assertEquals(ConfigUtils.getStringValue("project.id", ""), "123456654321");
        Assert.assertEquals(StoreStrategyClient.CURRENT_STRATEGY.name(), "ABORT_POLICY");
        Assert.assertTrue(RunMode.isProductionMode());
        Assert.assertEquals(getStaticField(GioLogger.class, "loggerLevel"), "error");

        Assert.assertEquals(getStaticField(AbortPolicyStoreStrategy.class, "THREADS"), Integer.parseInt(MAGINC_NUMBER));
        Assert.assertEquals(getStaticField(AbortPolicyStoreStrategy.class, "SEND_INTERVAL"), Integer.parseInt(MAGINC_NUMBER + 1));
        Assert.assertEquals(getStaticField(AbortPolicyStoreStrategy.class, "LIMIT"), Integer.parseInt(MAGINC_NUMBER + 2));
    }

    private static void setStaticField(Class clazz, String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(null, value);
        } catch (Exception ignored) {
        }
    }

    private static Object getStaticField(Class clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception ignored) {
        }

        return null;
    }
}
