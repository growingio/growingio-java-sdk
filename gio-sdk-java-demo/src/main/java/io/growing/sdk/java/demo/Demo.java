package io.growing.sdk.java.demo;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-24 22:46
 */
public class Demo {

    private static GrowingAPI projectA = new GrowingAPI.Builder().setProjectKey("test-debug-a").setDataSourceId("debug-a").build();
    private static GrowingAPI projectB = new GrowingAPI.Builder().setProjectKey("test-debug-b").setDataSourceId("debug-b").build();

    /**
     * 配置文件内容参考 gio.properties
     */
    public static void main(String[] args) {
        sendCdpCustomEvent();
        sendCdpUser();
    }

    /**
     * send cdp custom event message
     */
    public static void sendCdpCustomEvent() {
        Map<String, Object> map = new HashMap<String, Object>();
        //事件行为消息体
        GioCdpEventMessage msg = new GioCdpEventMessage.Builder()
                .eventTime(System.currentTimeMillis())      // 默认为系统当前时间,选填
                .eventKey("test")                           // 事件标识 (必填)
                .loginUserId("test")                        // 带用登陆用户ID的 (必填)
                .addEventVariable("product_name", "cdp苹果") // 事件级变量 (选填)
                .addEventVariables(map)                     // 事件级变量集合 (选填)
                .build();

        projectA.send(msg);

    }

    /**
     * send cdp user message
     */
    public static void sendCdpUser() {
        Map<String, Object> map = new HashMap<String, Object>();
        //事件行为消息体
        GioCdpUserMessage msg = new GioCdpUserMessage.Builder()
                .time(System.currentTimeMillis())      // 默认为系统当前时间,选填
                .loginUserId("417abcabcabcbac")        // 带用登陆用户ID的 (必填)
                .addUserVariable("user", "test")       // 用户变量 (选填)
                .addUserVariables(map)                 // 用户变量集合 (选填)
                .build();
        projectB.send(msg);
    }

    /**
     * send saas custom event
     */
    public static void sendSaas() {
        //事件行为消息体
        GIOEventMessage eventMessage = new GIOEventMessage.Builder()
                .eventTime(System.currentTimeMillis()) // 默认为系统当前时间,选填
                .eventKey("3")                         // 事件标识 (必填)
                .loginUserId("417abcabcabcbac")        // 带用登陆用户ID的 (选填)
                .addEventVariable("product_name", "苹果")         // 事件级变量 (选填)
                .addEventVariable("product_classify", "水果")     // 事件级变量 (选填)
                .addEventVariable("product_price", 14)           // 事件级变量 (选填)
                .build();

        //上传事件行为消息到服务器
        projectA.send(eventMessage);
    }
}