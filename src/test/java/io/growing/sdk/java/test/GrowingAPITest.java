package io.growing.sdk.java.test;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpItemMessage;
import io.growing.sdk.java.dto.GioCdpUserMappingMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;


/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 3:35 PM
 */
@RunWith(JUnit4.class)
public class GrowingAPITest {
    private static GrowingAPI sender;

    @BeforeClass
    public static void before() {
        sender = new GrowingAPI.Builder().setDataSourceId("a390a68c7b25638c").setProjectKey("91eaf9b283361032").build();
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
                        .eventTime(System.currentTimeMillis())
                        .loginUserKey("login_user_key")
                        .loginUserId("login_user_id")
                        .anonymousId("anonymous_id")
                        .eventKey("event_name")
                        .addItem("resource_item_id", "resource_item_key", resourceAttributes)
                        .addEventVariable("attribute_key", "attribute_value")
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
                .time(System.currentTimeMillis())
                .loginUserKey("login_user_key")
                .loginUserId("login_user_id")
                .anonymousId("anonymous_id")
                .addUserVariable("attribute_key", "attribute_value")
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