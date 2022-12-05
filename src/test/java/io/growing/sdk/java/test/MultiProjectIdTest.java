package io.growing.sdk.java.test;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.constants.APIConstants;
import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.test.stub.StubStreamHandlerFactory;
import io.growing.sdk.java.utils.ConfigUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xerial.snappy.Snappy;

import java.net.URL;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@RunWith(JUnit4.class)
public class MultiProjectIdTest {
    private static StubStreamHandlerFactory factory;
    private volatile Exception mException;

    @BeforeClass
    public static void before() {
        factory = StubStreamHandlerFactory.get();
        try {
            URL.setURLStreamHandlerFactory(factory);
        } catch (Error ignored) {
        }

        Properties properties = new Properties();
        properties.setProperty("run.mode", "production");
        ConfigUtils.init(properties);
    }

    @Before
    public void beforeTest() {
        mException = null;
    }

    @After
    public void afterTest() {
        if (mException != null) {
            Assert.fail(mException.getMessage());
        }
    }

    @Test
    public void sendMultiProjectMsg() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(3);

        final String AI1_PROJECT_ID = "ai1";
        final String AI1_EVENT_NAME = "ai1-event";
        final String AI2_PROJECT_ID = "ai2";
        final String AI2_EVENT_NAME = "ai2-event";
        final String DEFAULT_PROJECT_ID = ConfigUtils.getStringValue("project.id", "");
        final String DEFAULT_EVENT_NAME = "default-event";

        factory.setStubHttpURLConnectionListener(new StubStreamHandlerFactory.StubHttpURLConnectionListener() {
            @Override
            public void onSend(URL url, byte[] msg) {
                try {
                    String originMessage = new String(Snappy.uncompress(msg));
                    System.out.println(url + "接收到的数据为: " + originMessage);
                    if (url.toString().contains(APIConstants.buildUploadEventAPI(AI1_PROJECT_ID))) {
                        check(originMessage, AI1_EVENT_NAME);
                    } else if (url.toString().contains(APIConstants.buildUploadEventAPI(AI2_PROJECT_ID))) {
                        check(originMessage, AI2_EVENT_NAME);
                    } else if (url.toString().contains(APIConstants.buildUploadEventAPI(DEFAULT_PROJECT_ID))) {
                        check(originMessage, DEFAULT_EVENT_NAME);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mException = e;
                }
                countDownLatch.countDown();
            }

            private void check(String originMessage, String name) {
                JSONArray jsonArray = new JSONArray(originMessage);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                Assert.assertEquals("cstm", jsonObject.get("t"));
                Assert.assertEquals(name, jsonObject.get("n"));
            }
        });
        GrowingAPI.send(new GIOEventMessage.Builder().eventKey("default-event").build());
        GrowingAPI.send(new GIOEventMessage.Builder().eventKey("ai1-event").build(), "ai1");
        GrowingAPI.send(new GIOEventMessage.Builder().eventKey("ai2-event").build(), "ai2");

        countDownLatch.await();
    }
}
