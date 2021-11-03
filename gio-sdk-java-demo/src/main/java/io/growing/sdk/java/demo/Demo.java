package io.growing.sdk.java.demo;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpItemMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import io.growing.sdk.java.exception.GIOSendBeRejectedException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-24 22:46
 */
public class Demo {

    private static final GrowingAPI projectA = new GrowingAPI.Builder().setProjectKey("test-debug-a").setDataSourceId("debug-a").build();
    private static final GrowingAPI projectB = new GrowingAPI.Builder().setProjectKey("test-debug-b").setDataSourceId("debug-b").build();

    /**
     * 配置文件内容参考 gio.properties
     */
    public static void main(String[] args) {
        sendCdpItem();
        sendCdpCustomEvent();
        sendCdpUser();
    }

    /**
     * send cdp custom event message
     */
    public static void sendCdpCustomEventWithNewApi() {
        Map<String, Object> map = new HashMap<String, Object>();
        //事件行为消息体
        GioCdpEventMessage msg = new GioCdpEventMessage.Builder()
                .eventTime(System.currentTimeMillis())      // 默认为系统当前时间,选填
                .eventKey("test")                           // 事件标识 (必填)
                .loginUserId("test")                        // 带用登陆用户ID的 (必填)
                .addEventVariable("product_name", "cdp苹果") // 事件级变量 (选填)
                .addEventVariables(map)                     // 事件级变量集合 (选填)
                .build();
        try {
            //该接口在队列满时显式抛出异常(经过测试，在延迟100ms，队列500的情况下，出现的可能性已经很小)，调用方可选择是否处理。该方法还会在队列到达负载值时加快队列消费
            //过大的发送速度，即使消费队列的速度跟得上，也请确保api.host能抗的住。
            projectA.sendMaybeRejected(msg);
        } catch (GIOSendBeRejectedException e) {
            System.out.println("消息发送被拒绝：" + e.getLocalizedMessage());
            //retry
            projectA.sendMaybeRejected(msg);
        }

    }

    /**
     * send cdp item message
     */
    public static void sendCdpItem() {
        //事件行为消息体
        GioCdpItemMessage msg = new GioCdpItemMessage.Builder()
                .id("item-test")                        // 物品模型id
                .key("test")                            // 物品模型key
                .addItemVariable("item_name", "cdp苹果") // 物品模型变量 (选填)
                .build();

        projectA.send(msg);

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
                .addItem("item_id", "item_key")             // 物品模型id, key (选填)
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
}