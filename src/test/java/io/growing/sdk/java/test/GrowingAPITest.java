package io.growing.sdk.java.test;

import io.growing.collector.tunnel.protocol.EventDto;
import io.growing.collector.tunnel.protocol.EventList;
import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.sender.MessageSender;
import io.growing.sdk.java.utils.VersionInfo;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 3:35 PM
 */
@RunWith(JUnit4.class)
public class GrowingAPITest {

    @BeforeClass
    public static void before() {
        System.setProperty("java.util.logging.config.file","src/test/resources/logging.properties");
    }

    @Test
    public void sendCdpEventMsg() {
        List<GioCdpEventMessage> list = new ArrayList<GioCdpEventMessage>(500);
        for (int i = 0; i < 500; i++) {
            GioCdpEventMessage msg = new GioCdpEventMessage.Builder()
                    .eventKey(""+i)
                    .eventNumValue(i)
                    .loginUserId(i+"")
                    .addEventVariable("product_name", "cdp苹果")
                    .addEventVariable("product_classify", "cdp水果")
                    .addEventVariable("product_classify", "cdp水果")
                    .addEventVariable("product_price", 14)
                    .build();

            list.add(msg);
        }

        sendMsg(list);
    }

    @Test
    public void sendEventMsg() {
        List<GIOEventMessage> list = new ArrayList<GIOEventMessage>(500);
        for (int i = 0; i < 500; i++) {
            GIOEventMessage msg = new GIOEventMessage.Builder()
                    .eventKey(""+i)
                    .eventNumValue(i)
                    .loginUserId(i+"")
                    .addEventVariable("product_name", "cdp苹果")
                    .addEventVariable("product_classify", "cdp水果")
                    .addEventVariable("product_classify", "cdp水果")
                    .addEventVariable("product_price", 14)
                    .addEventVariable("version", VersionInfo.getVersion())
                    .build();

            list.add(msg);
        }

        sendMsg(list);
    }

    @Test
    public void sendCdpUserMsg() {
        List<GioCdpUserMessage> list = new ArrayList<GioCdpUserMessage>(500);
        for (int i = 0; i < 500; i++) {
            GioCdpUserMessage msg = new GioCdpUserMessage.Builder()
                    .setLoginUserId(String.valueOf(i))
                    .addAttribute("user", i)
                    .build();


            list.add(msg);
        }

        sendMsg(list);
    }

    private <T extends GIOMessage> void sendMsg(List<T> msgList) {
        for (T t : msgList) {
            GrowingAPI.send(t);
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}