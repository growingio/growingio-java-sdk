package io.growing.sdk.java.test;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import io.growing.sdk.java.utils.ConfigUtils;
import io.growing.sdk.java.utils.VersionInfo;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 3:35 PM
 */
@RunWith(JUnit4.class)
public class GrowingAPITest {

    private static GrowingAPI testDebug1;
    private static GrowingAPI testDebug2;
    private final int msgSize = 500;

    @BeforeClass
    public static void before() {
        System.setProperty("java.util.logging.config.file", "src/test/resources/logging.properties");
        testDebug1 = new GrowingAPI.Builder().setDataSourceId("test-debug-1-datasource").setProjectKey("test-debug-1").build();
        testDebug2 = new GrowingAPI.Builder().setDataSourceId("test-debug-2-datasource").setProjectKey("test-debug-2").build();
    }

    @Test
    public void sendCdpEventMsg() {
        GrowingAPI.initConfig("gio-test.properties");
        List<GioCdpEventMessage> list = new ArrayList<GioCdpEventMessage>(msgSize);
        for (int i = 0; i < msgSize; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("a" + i, i);
            GioCdpEventMessage msg = new GioCdpEventMessage.Builder()
                    .eventKey("" + i)
                    .loginUserId(i + "")
                    .addEventVariable("product_name", "cdp苹果")
                    .addEventVariables(map)
                    .build();

            list.add(msg);
        }

        sendMsg(list);
    }

    @Test
    public void sendEventMsg() {
        List<GIOEventMessage> list = new ArrayList<GIOEventMessage>(msgSize);
        for (int i = 0; i < msgSize; i++) {
            GIOEventMessage msg = new GIOEventMessage.Builder()
                    .eventKey("" + i)
                    .eventNumValue(i)
                    .loginUserId(i + "")
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
        List<GioCdpUserMessage> list = new ArrayList<GioCdpUserMessage>(msgSize);
        for (int i = 0; i < msgSize; i++) {
            GioCdpUserMessage msg = new GioCdpUserMessage.Builder()
                    .loginUserId(String.valueOf(i))
                    .addUserVariable("user", i)
                    .build();


            list.add(msg);
        }

        sendMsg(list);
    }

    @Test
    public void sendMultipleMsg() {
        List<GIOMessage> list = new ArrayList<GIOMessage>(msgSize);
        for (int i = 0; i < msgSize; i++) {
            if (i % 2 == 0) {
                GioCdpUserMessage msg = new GioCdpUserMessage.Builder()
                        .loginUserId(String.valueOf(i))
                        .addUserVariable("user", i)
                        .build();

                list.add(msg);
            } else {
                GioCdpEventMessage msg = new GioCdpEventMessage.Builder()
                        .eventKey(String.valueOf(i))
                        .loginUserId(String.valueOf(i))
                        .addEventVariable("product_name", "cdp苹果")
                        .build();

                list.add(msg);
            }
        }

        sendMsg(list);
    }

    private <T extends GIOMessage> void sendMsg(List<T> msgList) {

        for (int i = 0, size = msgList.size(); i < size; i++) {
            if (i % 3 == 0) {
                testDebug2.send(msgList.get(i));
            } else {
                testDebug1.send(msgList.get(i));
            }
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readConfig() {
        assert (ConfigUtils.getStringValue("api.host", "").equals("http://cdp0:1598"));
        GrowingAPI.initConfig("gio-test.properties");
        assert (ConfigUtils.getStringValue("api.host", "").equals("http://gio-test:1598"));
        GrowingAPI.initConfig("gio.properties");
        assert (ConfigUtils.getStringValue("api.host", "").equals("http://gio-test:1598"));
    }
}