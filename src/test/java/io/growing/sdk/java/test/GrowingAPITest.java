package io.growing.sdk.java.test;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.sender.MessageSender;
import io.growing.sdk.java.utils.ConfigUtils;
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
    private final static String projectId = ConfigUtils.getStringValue("project.id", "");

    @BeforeClass
    public static void before() {
        System.setProperty("java.util.logging.config.file","src/test/resources/logging.properties");
    }

    @Test
    public void apiSendEventTest() {
        for (int i = 0; i < 500; i++) {
            GIOEventMessage msg = new GIOEventMessage.Builder()
                    .eventKey(""+i)
                    .eventNumValue(i)
                    .loginUserId(i+"")
                    .addEventVariable("product_name", "苹果")
                    .addEventVariable("product_classify", "水果")
                    .addEventVariable("product_classify", "水果")
                    .addEventVariable("product_price", 14)
                    .build();
            GrowingAPI.send(msg);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void senderTest() throws InterruptedException {
        MessageSender sender = new FixThreadPoolSender();
        for (int i = 0; i < 200; i++){
            List<GIOMessage> list = new ArrayList<GIOMessage>();
            GIOMessage msg = new GIOEventMessage.Builder()
                    .eventKey("3")
                    .eventNumValue(3)
                    .addEventVariable("product_name", "苹果")
                    .addEventVariable("product_classify", "水果")
                    .addEventVariable("product_price", 14)
                    .build();

            list.add(msg);
            sender.sendMsg(projectId, list);
        }
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void sendWithProxy() throws InterruptedException {
        MessageSender sender = new FixThreadPoolSender();

        GIOMessage msg = new GIOEventMessage.Builder()
                .eventKey("3")
                .eventNumValue(3)
                .addEventVariable("product_name", "苹果")
                .addEventVariable("product_classify", "水果")
                .addEventVariable("product_price", 14)
                .build();

        List list = new ArrayList();
        list.add(msg);

        sender.sendMsg(projectId, list);

        TimeUnit.SECONDS.sleep(15);
    }

}