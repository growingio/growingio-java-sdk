import com.google.protobuf.Message;
import io.growing.collector.tunnel.protocol.EventType;
import io.growing.collector.tunnel.protocol.EventV3Dto;
import io.growing.collector.tunnel.protocol.EventV3List;
import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

import static constant.Constants.*;

public class Main {


    public static void main(String[] args) {
        // SDK在 设置请求头的场景下 仅支持单条数据发送，
        // 考虑到在现有发送策略中增加该场景对性能的影响
        // 建议直接走 OPENAPI 转发数据

        HttpUtils.request(createVisitMessage(), createHeader());
        HttpUtils.request(createCustomEvent(), createHeader());

        GrowingAPI project = new GrowingAPI.Builder().setProjectKey(ACCOUNT_ID).setDataSourceId(DATA_SOURCE_ID).build();
        project.send(new GioCdpEventMessage.Builder()
                .eventKey("order_sdk")
                .anonymousId("4d08c7a2-dfcc-48e6-9c86-8e6010bcc824")
                .loginUserId("styluo")
                .build());

//        System.exit(0);
    }

    /**
     * [{
     *     "eventType":"VISIT",
     *     "deviceId":"4d08c7a2-dfcc-48e6-9c86-8e6010bcc824",
     *     "sessionId":"f9345c77-9881-4a7e-a880-e0debc5111c9",
     *     "dataSourceId":"adba4a25533bd7a8",
     *     "timestamp":1655953292555,
     *     "domain":"proxy.growingio.cn:8080",
     *     "path":"/quick/",
     *     "platform":"web",
     *     "screenHeight":900,
     *     "screenWidth":1440,
     *     "sdkVersion":"3.3.11",
     *     "language":"zh-cn",
     *     "appVersion":"2.3.0",
     *     "title":"first html5 page",
     *     "globalSequenceId":1,
     *     "eventSequenceId":1
     * }]
     */
    public static Message createVisitMessage() {
        EventV3Dto eventV3Dto = EventV3Dto.newBuilder()
                .setEventType(EventType.VISIT)
                .setDeviceId("4d08c7a2-dfcc-48e6-9c86-8e6010bcc824")
                .setSessionId("f9345c77-9881-4a7e-a880-e0debc5111c9")
                .setDataSourceId(DATA_SOURCE_ID)
                // 方便 查数
                .setTimestamp(System.currentTimeMillis())
                .setDomain("proxy.growingio.cn:8080")
                .setPath("/quick/")
                .setPlatform("web")
                .setScreenHeight(900)
                .setScreenWidth(1440)
                .setSdkVersion("3.3.11")
                .setLanguage("zh-cn")
                .setAppVersion("2.3.0")
                .setTitle("first html5 page")
                .setGlobalSequenceId(1)
                .setEventSequenceId(1)
                .build();
        return EventV3List.newBuilder()
                .addValues(eventV3Dto)
                .build();
    }

    /**
     *[{
     *   "eventType": "CUSTOM",
     *   "pageShowTimestamp": 1655967844223,
     *   "eventName": "order_sdk",
     *   "deviceId": "4d08c7a2-dfcc-48e6-9c86-8e6010bcc824",
     *   "sessionId": "f9345c77-9881-4a7e-a880-e0debc5111c9",
     *   "dataSourceId": "adba4a25533bd7a8",
     *   "timestamp": 1655967844224,
     *   "domain": "proxy.growingio.cn:8080",
     *   "path": "/quick/",
     *   "platform": "web",
     *   "screenHeight": 1440,
     *   "screenWidth": 2560,
     *   "sdkVersion": "3.3.11",
     *   "language": "zh-cn",
     *   "appVersion": "2.3.0",
     *   "title": "first html5 page",
     *   "globalSequenceId": 9,
     *   "eventSequenceId": 3
     * }]
     */
    public static Message createCustomEvent() {
        EventV3Dto eventV3Dto = EventV3Dto.newBuilder()
                .setEventType(EventType.CUSTOM)
                .setPageShowTimestamp(Long.parseLong("1655967844223"))
                .setEventName("TTT")
                .setDeviceId("4d08c7a2-dfcc-48e6-9c86-8e6010bcc824")
                .setSessionId("f9345c77-9881-4a7e-a880-e0debc5111c9")
                .setDataSourceId(DATA_SOURCE_ID)
                // 方便 查数
                .setTimestamp(System.currentTimeMillis())
                .setUserId("styluo")
                .setDomain("proxy.growingio.cn:8080")
                .setPath("/quick/")
                .setPlatform("web")
                .setScreenHeight(1440)
                .setScreenWidth(2560)
                .setSdkVersion("3.3.11")
                .setLanguage("zh-cn")
                .setAppVersion("2.3.0")
                .setTitle("first html5 page")
                .setGlobalSequenceId(9)
                .setEventSequenceId(3)
                .build();
        return EventV3List.newBuilder()
                .addValues(eventV3Dto)
                .build();
    }

    // 转发客户端 真实 IP X-G-Real-IP
    // 转发客户端 真实 UA
    public static Map<String, String> createHeader() {
        HashMap<String, String> map = new HashMap<>();
        map.put(HEADER_IP, "218.108.153.19");
        map.put(HEADER_UA, "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Mobile Safari/537.36");
        return map;
    }
}
