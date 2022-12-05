package io.growing.sdk.java.test;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.test.stub.StubStreamHandlerFactory;
import org.json.JSONArray;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xerial.snappy.Snappy;

import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 3:35 PM
 */
@RunWith(JUnit4.class)
public class GrowingAPITest {
    private static StubStreamHandlerFactory factory;
    private volatile Exception mException;

    @BeforeClass
    public static void before() {
        factory = StubStreamHandlerFactory.get();
        try {
            URL.setURLStreamHandlerFactory(factory);
        } catch (Error ignored) {
        }

        System.setProperty("java.util.logging.config.file", "src/test/resources/logging.properties");
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
    public void apiSendEventTest() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(498);
        factory.setStubHttpURLConnectionListener(new StubStreamHandlerFactory.StubHttpURLConnectionListener() {
            @Override
            public void onSend(URL url, byte[] msg) {
                try {
                    String originMessage = new String(Snappy.uncompress(msg));
                    JSONArray jsonArray = new JSONArray(originMessage);
                    int length = jsonArray.length();
                    while (length-- != 0) {
                        countDownLatch.countDown();
                    }
                } catch (Exception e) {
                    mException = e;
                }
            }
        });
        for (int i = 0; i < 500; i++) {
            GIOEventMessage msg = new GIOEventMessage.Builder()
                    .eventKey("" + i)
                    .eventNumValue(i)
                    .loginUserId(i + "")
                    .addEventVariable("product_name", "苹果")
                    .addEventVariable("product_classify", "水果")
                    .addEventVariable("product_classify", "水果")
                    .addEventVariable("product_price", 14)
                    .build();
            GrowingAPI.send(msg);
        }
        countDownLatch.await();
    }

}