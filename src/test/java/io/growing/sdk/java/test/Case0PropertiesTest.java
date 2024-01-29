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
        final String magicNumber = "666";

        Properties properties = new Properties();

        properties.setProperty("api.host", "https://www.growingio.com");
        properties.setProperty("project.id", "123456654321");
        properties.setProperty("msg.store.strategy", "abortPolicy");
        properties.setProperty("run.mode", "production");
        properties.setProperty("logger.level", "error");

        properties.setProperty("send.msg.thread", magicNumber);
        properties.setProperty("send.msg.interval", magicNumber + 1);
        properties.setProperty("msg.store.queue.size", magicNumber + 2);

        // init properties
        GrowingAPI.initConfig(properties);
        sender = new GrowingAPI.Builder().setDataSourceId(DATASOURCE_ID).setProjectKey(PROJECT_KEY).build();

        // check properties
        Assert.assertEquals("https://www.growingio.com", ConfigUtils.getStringValue("api.host", ""));
        Assert.assertEquals("123456654321", ConfigUtils.getStringValue("project.id", ""));
        Assert.assertEquals("ABORT_POLICY", StoreStrategyClient.CURRENT_STRATEGY.name());
        Assert.assertTrue(RunMode.isProductionMode());
        Assert.assertEquals("error", getStaticField(GioLogger.class, "loggerLevel"));

        Assert.assertEquals(Integer.parseInt(magicNumber), getStaticField(AbortPolicyStoreStrategy.class, "THREADS"));
        Assert.assertEquals(Integer.parseInt(magicNumber + 1), getStaticField(AbortPolicyStoreStrategy.class, "SEND_INTERVAL"));
        Assert.assertEquals(Integer.parseInt(magicNumber + 2), getStaticField(AbortPolicyStoreStrategy.class, "LIMIT"));
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
