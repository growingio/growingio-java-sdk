package io.growing.sdk.java.test;

import io.growing.collector.tunnel.protocol.EventV3Dto;
import io.growing.collector.tunnel.protocol.EventV3List;
import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.ab.ABExperiment;
import io.growing.sdk.java.ab.ABTestCallback;
import io.growing.sdk.java.test.stub.StubStreamHandlerFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RunWith(JUnit4.class)
public class Case2ABTest {

    private static final String PROJECT_KEY = "91eaf9b283361032";
    private static final String DATASOURCE_ID = "a390a68c7b25638c";
    private static final String AB_DATASOURCE_ID = "ab90a68c7b25638c";

    private static final String LAYER_ID = "JmjoD6pL";
    private static final String DISTINCT_ID = "deviceId-187********";

    private static GrowingAPI sender;
    private static StubStreamHandlerFactory factory;

    private volatile Exception mException;

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

    @BeforeClass
    public static void before() {
        sender = new GrowingAPI.Builder().setDataSourceId(DATASOURCE_ID).setProjectKey(PROJECT_KEY).build();
        factory = StubStreamHandlerFactory.getInstance();
    }

    @Test
    public void getAbTestSuccess() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        factory.setResponseHandler(new StubStreamHandlerFactory.ResponseHandler() {
            @Override
            public String getResponse(URL url) {
                if (url.getPath().contains("/diversion")) {
                    return "{\n" +
                            "    \"code\": 0,\n" +
                            "    \"errorMsg\": null,\n" +
                            "    \"layerId\": 13,\n" +
                            "    \"layerName\": \"首次访问引导\",\n" +
                            "    \"experimentId\": 20,\n" +
                            "    \"experimentName\": \"新用户首次访问引导弹窗pc\",\n" +
                            "    \"strategyId\": 49,\n" +
                            "    \"strategyName\": \"实验组1\",\n" +
                            "    \"variables\": {\n" +
                            "        \"isShow\": \"true\"\n" +
                            "    }\n" +
                            "}";
                } else {
                    return "OK";
                }
            }
        });

        factory.setStubHttpURLConnectionListener(new StubStreamHandlerFactory.StubHttpURLConnectionListener() {
            @Override
            public void onSend(URL url, byte[] msg) {
                if (url.getPath().contains("/collect")) {
                    try {
                        EventV3List eventList = EventV3List.parseFrom(msg);
                        EventV3Dto customEvent = eventList.getValues(0);
                        Assert.assertEquals(AB_DATASOURCE_ID, customEvent.getDataSourceId());
                        Assert.assertEquals(PROJECT_KEY, customEvent.getProjectKey());

                        Assert.assertEquals("CUSTOM", customEvent.getEventType().name());
                        Assert.assertEquals("$exp_hit", customEvent.getEventName());
                        Assert.assertEquals(DISTINCT_ID, customEvent.getDeviceId());

                        Map<String, String> attributes = customEvent.getAttributesMap();
                        Assert.assertEquals(String.valueOf(20L), attributes.get("$exp_id"));
                        Assert.assertEquals(String.valueOf(49L), attributes.get("$exp_strategy_id"));
                        Assert.assertEquals(LAYER_ID, attributes.get("$exp_layer_id"));
                        Assert.assertEquals("首次访问引导", attributes.get("$exp_layer_name"));
                        Assert.assertEquals("新用户首次访问引导弹窗pc", attributes.get("$exp_name"));
                        Assert.assertEquals("实验组1", attributes.get("$exp_strategy_name"));
                    } catch (Exception e) {
                        mException = e;
                    }
                    countDownLatch.countDown();
                } else if (url.getPath().contains("/diversion")) {
                    String body = "accountId=91eaf9b283361032&datasourceId=ab90a68c7b25638c&distinctId=deviceId-187********&layerId=JmjoD6pL&newDevice=true";
                    Assert.assertEquals(body, new String(msg));
                    countDownLatch.countDown();
                }
            }
        });

        sender.getABTestSync(LAYER_ID, AB_DATASOURCE_ID, DISTINCT_ID, new ABTestCallback() {

            @Override
            public void onABExperimentReceived(ABExperiment experiment) {
                try {
                    Assert.assertEquals(LAYER_ID, experiment.getLayerId());
                    Assert.assertEquals(20L, experiment.getExperimentId());
                    Assert.assertEquals(49L, experiment.getStrategyId());
                    Assert.assertEquals("新用户首次访问引导弹窗pc", experiment.getExpName());
                    Assert.assertEquals("实验组1", experiment.getExpStrategyName());
                    Assert.assertEquals("首次访问引导", experiment.getExpLayerName());
                    Map<String, String> variables = experiment.getVariables();
                    Assert.assertEquals(variables.get("isShow"), "true");
                } catch (Exception e) {
                    mException = e;
                }
                countDownLatch.countDown();
            }

            @Override
            public void onABExperimentFailed(Exception error) {
            }
        }, true);

        countDownLatch.await();
    }

    @Test
    public void getABTestFailed() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        factory.setResponseHandler(new StubStreamHandlerFactory.ResponseHandler() {
            @Override
            public String getResponse(URL url) {
                if (url.getPath().contains("/diversion")) {
                    return "{\n" +
                            "    \"code\": 999,\n" +
                            "    \"errorMsg\": \"系统异常\",\n" +
                            "    \"layerId\": null,\n" +
                            "    \"layerName\": null,\n" +
                            "    \"experimentId\": null,\n" +
                            "    \"experimentName\": null,\n" +
                            "    \"strategyId\": null,\n" +
                            "    \"strategyName\": null,\n" +
                            "    \"variables\": null\n" +
                            "}";
                } else {
                    return "OK";
                }
            }
        });

        sender.getABTestSync(LAYER_ID, AB_DATASOURCE_ID, DISTINCT_ID, new ABTestCallback() {
            @Override
            public void onABExperimentReceived(ABExperiment experiment) {
                System.out.println(experiment);
            }

            @Override
            public void onABExperimentFailed(Exception error) {
                try {
                    Assert.assertEquals(error.getLocalizedMessage(), "ABExperiment data failed with: 系统异常");
                } catch (Exception e) {
                    mException = e;
                }
                countDownLatch.countDown();
            }
        }, true);

        countDownLatch.await();
    }

    @Test
    public void getABTestIllegal() {
        try {
            ABTestCallback callback = new ABTestCallback() {
                @Override
                public void onABExperimentReceived(ABExperiment experiment) {
                }

                @Override
                public void onABExperimentFailed(Exception error) {
                }
            };
            sender.getABTestSync(null, AB_DATASOURCE_ID, DISTINCT_ID, callback);
            sender.getABTestSync(LAYER_ID, null, DISTINCT_ID, callback);
            sender.getABTestSync(LAYER_ID, AB_DATASOURCE_ID, null, callback);
            sender.getABTestSync(LAYER_ID, AB_DATASOURCE_ID, DISTINCT_ID, null);
            sender.getABTestSync(null, null, null, null);
        } catch (Exception e) {
            Assert.fail(e.getLocalizedMessage());
        }
    }
}
