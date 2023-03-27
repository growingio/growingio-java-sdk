package io.growing.sdk.java.test;

import io.growing.collector.tunnel.protocol.EventV3Dto;
import io.growing.collector.tunnel.protocol.EventV3List;
import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.constants.RunMode;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import io.growing.sdk.java.test.stub.StubStreamHandlerFactory;
import io.growing.sdk.java.utils.ConfigUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(JUnit4.class)
public class Case1MockHttpTest {
    private static final String PROJECT_KEY = "91eaf9b283361032";
    private static final String DATASOURCE_ID = "a390a68c7b25638c";

    private static GrowingAPI sender;
    private static StubStreamHandlerFactory factory;

    @BeforeClass
    public static void before() {
        setStaticField(ConfigUtils.class, "inited", new AtomicBoolean(false));
        setStaticField(RunMode.class, "currentMode", null);
        Properties properties = new Properties();
        properties.setProperty("run.mode", "production");
        properties.setProperty("api.host", "http://117.50.105.254:8080");
        GrowingAPI.initConfig(properties);
        sender = new GrowingAPI.Builder().setDataSourceId(DATASOURCE_ID).setProjectKey(PROJECT_KEY).build();
        factory = new StubStreamHandlerFactory();
        URL.setURLStreamHandlerFactory(factory);
    }

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

    private volatile Exception mException;
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
    public void sendCustomEvent() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        factory.setStubHttpURLConnectionListener(new StubStreamHandlerFactory.StubHttpURLConnectionListener() {
            @Override
            public void onSend(URL url, byte[] msg) {
                try {
                    EventV3List eventList = EventV3List.parseFrom(msg);
                    EventV3Dto customEvent = eventList.getValues(0);
                    Assert.assertEquals(DATASOURCE_ID, customEvent.getDataSourceId());
                    Assert.assertEquals(PROJECT_KEY, customEvent.getProjectKey());

                    Assert.assertEquals("CUSTOM", customEvent.getEventType().name());
                    Assert.assertEquals("list_attribute_test_event", customEvent.getEventName());

                    Map<String, String> attributes = customEvent.getAttributesMap();
                    Assert.assertEquals("1||2||3", attributes.get("list_attribute_normal"));
                    Assert.assertEquals("||1||", attributes.get("list_attribute_contains_null"));
                    Assert.assertNull(attributes.get("list_attribute_empty"));
                    Assert.assertEquals("", attributes.get("list_attribute_empty_string"));
                    Assert.assertEquals("||||", attributes.get("list_attribute_empty_string_list"));
                    Assert.assertEquals("", attributes.get(""));
                    Assert.assertEquals("中文||English||にほんご", attributes.get("列表属性中文"));
                    Assert.assertEquals(101, attributes.get("list_attribute_length").split("\\|\\|").length);
                } catch (Exception e) {
                    mException = e;
                }

                countDownLatch.countDown();
            }
        });

        sender.send(new GioCdpEventMessage.Builder()
                .eventKey("list_attribute_test_event")
                .loginUserId("list_attribute_test_user")
                .addEventVariable("list_attribute_normal", Arrays.asList("1", "2", "3"))
                .addEventVariable("list_attribute_contains_null", Arrays.asList("", "1", null))
                .addEventVariable("list_attribute_empty", Arrays.asList())
                .addEventVariable("list_attribute_empty_string", Arrays.asList(""))
                .addEventVariable("list_attribute_empty_string_list", Arrays.asList("", "", ""))
                .addEventVariable("", Arrays.asList(""))
                .addEventVariable("列表属性中文", Arrays.asList("中文", "English", "にほんご"))
                .addEventVariable("list_attribute_length", makeSequence(0, 100))
                .build());

       countDownLatch.await();
    }

    @Test
    public void sendUserEvent() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        factory.setStubHttpURLConnectionListener(new StubStreamHandlerFactory.StubHttpURLConnectionListener() {
            @Override
            public void onSend(URL url, byte[] msg) {
                try {
                    EventV3List eventList = EventV3List.parseFrom(msg);
                    EventV3Dto userEvent = eventList.getValues(0);
                    Assert.assertEquals(DATASOURCE_ID, userEvent.getDataSourceId());
                    Assert.assertEquals(PROJECT_KEY, userEvent.getProjectKey());
                    Assert.assertEquals("LOGIN_USER_ATTRIBUTES", userEvent.getEventType().name());
                    Assert.assertEquals("list_attribute_test_user", userEvent.getUserId());

                    Map<String, String> attributes = userEvent.getAttributesMap();
                    Assert.assertEquals("1||2||3", attributes.get("list_attribute_normal"));
                    Assert.assertEquals("||1||", attributes.get("list_attribute_contains_null"));
                    Assert.assertNull(attributes.get("list_attribute_empty"));
                    Assert.assertEquals("", attributes.get("list_attribute_empty_string"));
                    Assert.assertEquals("||||", attributes.get("list_attribute_empty_string_list"));
                    Assert.assertEquals("", attributes.get(""));
                    Assert.assertEquals("中文||English||にほんご", attributes.get("列表属性中文"));
                    Assert.assertEquals(101, attributes.get("list_attribute_length").split("\\|\\|").length);

                } catch (Exception e) {
                    mException = e;
                }

                countDownLatch.countDown();
            }
        });

        sender.send(new GioCdpUserMessage.Builder()
                .loginUserId("list_attribute_test_user")
                .addUserVariable("list_attribute_normal", Arrays.asList("1", "2", "3"))
                .addUserVariable("list_attribute_contains_null", Arrays.asList("", "1", null))
                .addUserVariable("list_attribute_empty", Arrays.asList())
                .addUserVariable("list_attribute_empty_string", Arrays.asList(""))
                .addUserVariable("list_attribute_empty_string_list", Arrays.asList("", "", ""))
                .addUserVariable("", Arrays.asList(""))
                .addUserVariable("列表属性中文", Arrays.asList("中文", "English", "にほんご"))
                .addUserVariable("list_attribute_length", makeSequence(0, 100))
                .build());

        countDownLatch.await();
    }

    // Java 8可以使用Stream API
    private List<Integer> makeSequence(int begin, int end) {
        List<Integer> ret = new ArrayList(end - begin + 1);

        for (int i = begin; i <= end; i++) {
            ret.add(i);
        }

        return ret;
    }
}
