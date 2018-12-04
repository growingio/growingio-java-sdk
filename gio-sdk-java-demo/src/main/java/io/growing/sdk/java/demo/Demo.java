package io.growing.sdk.java.demo;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-24 22:46
 */
public class Demo {

    /**
     * 配置文件内容参考 gio.properties
     * @param args
     */
    public static void main(String[] args) {
        //事件行为消息体
        GIOEventMessage eventMessage = new GIOEventMessage.Builder()
                .eventTime(System.currentTimeMillis()) // 默认为系统当前时间,选填
                .eventKey("3")                         // 事件标识 (必填)
                .eventNumValue(1.0)                    // 打点事件数值 (选填)
                .loginUserId("417abcabcabcbac")        // 带用登陆用户ID的 (选填)
                .addEventVariable("product_name", "苹果")         // 事件级变量 (选填)
                .addEventVariable("product_classify", "水果")     // 事件级变量 (选填)
                .addEventVariable("product_price", 14)           // 事件级变量 (选填)
                .build();

        //上传事件行为消息到服务器
        GrowingAPI.send(eventMessage);
    }
}