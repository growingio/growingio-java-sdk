package io.growing.sdk.java.test;

import io.growing.sdk.java.dto.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Case3EventMessageTest {

    @Test
    public void checkCustomMessage() {
        GioCdpEventMessage eventMessage = new GioCdpEventMessage.Builder()
                .eventKey("event_name")
                .loginUserId("user_id")
                .build();
        Assert.assertFalse(eventMessage.isIllegal());

        eventMessage = new GioCdpEventMessage.Builder()
                .eventKey("event_name")
                .anonymousId("device_id")
                .build();
        Assert.assertFalse(eventMessage.isIllegal());

        eventMessage = new GioCdpEventMessage.Builder()
                .eventKey("event_name")
                .loginUserKey(GioCdpEventMessage.XEI_USER_KEY)
                .build();
        Assert.assertFalse(eventMessage.isIllegal());

        eventMessage = new GioCdpEventMessage.Builder()
                .eventKey("event_name")
                .build();
        Assert.assertTrue(eventMessage.isIllegal());

        eventMessage = new GioCdpEventMessage.Builder()
                .loginUserId("user_id")
                .build();
        Assert.assertTrue(eventMessage.isIllegal());

        eventMessage = new GioCdpEventMessage.Builder()
                .anonymousId("device_id")
                .build();
        Assert.assertTrue(eventMessage.isIllegal());
    }

    @Test
    public void checkUserMessage() {
        GioCdpUserMessage userMessage = new GioCdpUserMessage.Builder()
                .loginUserId("user_id")
                .addUserVariable("attr_key", "attr_value")
                .build();
        Assert.assertFalse(userMessage.isIllegal());

        userMessage = new GioCdpUserMessage.Builder()
                .anonymousId("anonymous_id")
                .addUserVariable("attr_key", "attr_value")
                .build();
        Assert.assertFalse(userMessage.isIllegal());

        userMessage = new GioCdpUserMessage.Builder()
                .anonymousId("anonymous_id")
                .build();
        Assert.assertTrue(userMessage.isIllegal());

        userMessage = new GioCdpUserMessage.Builder()
                .loginUserId("user_id")
                .build();
        Assert.assertTrue(userMessage.isIllegal());

        userMessage = new GioCdpUserMessage.Builder()
                .addUserVariable("attr_key", "attr_value")
                .build();
        Assert.assertTrue(userMessage.isIllegal());
    }

    @Test
    public void checkUserMappingMessage() {
        GioCdpUserMappingMessage userMappingMessage = new GioCdpUserMappingMessage.Builder()
                .addIdentities("user_key", "user_id")
                .build();
        Assert.assertFalse(userMappingMessage.isIllegal());

        userMappingMessage = new GioCdpUserMappingMessage.Builder()
                .build();
        Assert.assertTrue(userMappingMessage.isIllegal());
    }

    @Test
    public void checkItemMessage() {
        GioCdpItemMessage itemMessage = new GioCdpItemMessage.Builder()
                .id("item_id")
                .key("item_key")
                .build();
        Assert.assertFalse(itemMessage.isIllegal());

        itemMessage = new GioCdpItemMessage.Builder()
                .id("item_id")
                .build();
        Assert.assertTrue(itemMessage.isIllegal());

        itemMessage = new GioCdpItemMessage.Builder()
                .key("item_key")
                .build();
        Assert.assertTrue(itemMessage.isIllegal());
    }
}
