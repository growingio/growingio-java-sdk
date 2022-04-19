package io.growing.sdk.java.test;

import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpItemMessage;
import io.growing.sdk.java.dto.GioCdpUserMappingMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EventMessageTest {

    @Test
    public void checkCustomMessage() {
        GioCdpEventMessage eventMessage = new GioCdpEventMessage.Builder()
                .eventKey("event_name")
                .loginUserId("user_id")
                .build();
        Assert.assertFalse("legal data: event_name and user_id ", eventMessage.isIllegal());

        eventMessage = new GioCdpEventMessage.Builder()
                .eventKey("event_name")
                .anonymousId("device_id")
                .build();
        Assert.assertFalse("legal data: event_name and anonymousId ", eventMessage.isIllegal());

        eventMessage = new GioCdpEventMessage.Builder()
                .eventKey("event_name")
                .build();
        Assert.assertTrue("illegal data: event_name", eventMessage.isIllegal());

        eventMessage = new GioCdpEventMessage.Builder()
                .loginUserId("user_id")
                .build();
        Assert.assertTrue("illegal data: user_id", eventMessage.isIllegal());

        eventMessage = new GioCdpEventMessage.Builder()
                .anonymousId("device_id")
                .build();
        Assert.assertTrue("illegal data: device_id", eventMessage.isIllegal());
    }

    @Test
    public void checkUserMessage() {
        GioCdpUserMessage userMessage = new GioCdpUserMessage.Builder()
                .loginUserId("user_id")
                .build();
        Assert.assertFalse("legal data: user_id", userMessage.isIllegal());

        userMessage = new GioCdpUserMessage.Builder()
                .build();
        Assert.assertTrue("illegal data: empty", userMessage.isIllegal());
    }

    @Test
    public void checkUserMappingMessage() {
        GioCdpUserMappingMessage userMappingMessage = new GioCdpUserMappingMessage.Builder()
                .addIdentities("user_key", "user_id")
                .build();
        Assert.assertFalse("legal data: identity", userMappingMessage.isIllegal());

        userMappingMessage = new GioCdpUserMappingMessage.Builder()
                .build();
        Assert.assertTrue("illegal data: empty", userMappingMessage.isIllegal());
    }

    @Test
    public void checkItemMessage() {
        GioCdpItemMessage itemMessage = new GioCdpItemMessage.Builder()
                .id("item_id")
                .key("item_key")
                .build();
        Assert.assertFalse("legal data: item_id and item_key", itemMessage.isIllegal());

        itemMessage = new GioCdpItemMessage.Builder()
                .id("item_id")
                .build();
        Assert.assertTrue("illegal data: item_id", itemMessage.isIllegal());

        itemMessage = new GioCdpItemMessage.Builder()
                .key("item_key")
                .build();
        Assert.assertTrue("illegal data: item_key", itemMessage.isIllegal());
    }
}
