package io.growing.sdk.java.test;

import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.sender.FixThreadPoolSender;
import io.growing.sdk.java.sender.MessageSender;
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
 * @since : 12/6/19 4:46 PM
 */
@RunWith(JUnit4.class)
public class SenderTest {

    @BeforeClass
    public static void before() {
        System.setProperty("java.util.logging.config.file", "src/test/resources/logging.properties");
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

        sender.sendMsg("test-debug", list);

        TimeUnit.SECONDS.sleep(15);
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
            sender.sendMsg("test-debug", list);
        }
        TimeUnit.SECONDS.sleep(10);
    }
}