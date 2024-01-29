package io.growing.sdk.java.test;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpItemMessage;
import io.growing.sdk.java.dto.GioCdpUserMappingMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import io.growing.sdk.java.utils.ConfigUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 3:35 PM
 */
@RunWith(JUnit4.class)
public class Case2GrowingAPITest {
    private static GrowingAPI sender;

    @BeforeClass
    public static void before() {
        setStaticField(ConfigUtils.class, "inited", new AtomicBoolean(false));
        setStaticField(RunMode.class, "currentMode", null);
        Properties properties = new Properties();
        properties.setProperty("run.mode", "test");
        GrowingAPI.initConfig(properties);
        sender = new GrowingAPI.Builder().setDataSourceId("a390a68c7b25638c").setProjectKey("91eaf9b283361032").build();
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

    @Test
    public void sendCustomEvent() {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("int_attribute_key", 1);
        attributes.put("double_attribute_key", 1.0f);
        attributes.put("string_attribute_key", "string_attribute_value");

        HashMap<String, String> resourceAttributes = new HashMap<String, String>();
        resourceAttributes.put("resource_attribute_key", "resource_attribute_value");
        sender.send(new GioCdpEventMessage.Builder()
                        .eventTime(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                        .loginUserKey("login_user_key")
                        .loginUserId("login_user_id")
                        .anonymousId("anonymous_id")
                        .eventKey("event_name")
                        .addItem("resource_item_id", "resource_item_key", resourceAttributes)
                        .addEventVariable("attribute_key", "attribute_value")
                        .addEventVariable("attribute_list_key", Arrays.asList("", "1", null))
                        .addEventVariable("empty_list_key", Arrays.asList())
                        .addEventVariables(attributes)
                        .build());
    }

    @Test
    public void sendIllegalCustomEvent() {
        // eventName为必填字段
        sender.send(new GioCdpEventMessage.Builder()
                .build());
    }

    @Test
    public void sendUserEvent() {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("int_attribute_key", 1);
        attributes.put("double_attribute_key", 1.0f);
        attributes.put("string_attribute_key", "string_attribute_value");

        sender.send(new GioCdpUserMessage.Builder()
                .time(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                .loginUserKey("login_user_key")
                .loginUserId("login_user_id")
                .anonymousId("anonymous_id")
                .addUserVariable("attribute_key", "attribute_value")
                .addUserVariable("attribute_list_key", Arrays.asList("", "1", null))
                .addUserVariable("empty_list_key", Arrays.asList())
                .addUserVariables(attributes)
                .build());
    }

    @Test
    public void sendIllegalUserEvent() {
        sender.send(new GioCdpUserMessage.Builder()
                .build());
    }

    @Test
    public void sendItemEvent() {
        sender.send(new GioCdpItemMessage.Builder()
                .id("resource_item_id")
                .key("resource_item_key")
                .addItemVariable("resource_attribute_key_1", "resource_attribute_value_1")
                .addItemVariable("resource_attribute_key_2", "resource_attribute_value_2")
                .build());
    }

    @Test
    public void sendIllegalItemEvent() {
        sender.send(new GioCdpItemMessage.Builder()
                .build());
    }

    @Test
    public void sendUserMappingEvent() {
        HashMap<String, String> identities = new HashMap<String, String>();
        identities.put("email", "unit-test@growingio.com");
        identities.put("qq", "26******20");
        sender.send(new GioCdpUserMappingMessage.Builder()
                .eventTime(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                .addIdentities("phone", "1**********1")
                .addIdentities("login_user_key", "login_user_id")
                .addIdentities(identities)
                .build());
    }

    @Test
    public void sendIllegalUserMappingEvent() {
        sender.send(new GioCdpUserMappingMessage.Builder()
                .build());
    }
}