package io.growing.sdk.java.process;

import io.growing.sdk.java.dto.GIOEventMessage;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCdpEventMessage;
import io.growing.sdk.java.dto.GioCdpUserMessage;
import io.growing.sdk.java.process.impl.GioCdpEventMessageProcessor;
import io.growing.sdk.java.process.impl.GioCdpUserMessageProcessor;
import io.growing.sdk.java.process.impl.GioEventMessageProcessor;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 10:44 AM
 */
public class EventProcessorClient {

    private static class SingletonInstances {
        private static final GioCdpEventMessageProcessor CDP_EVENT_MESSAGE_PROCESSOR = new GioCdpEventMessageProcessor();
        private static final GioCdpUserMessageProcessor CDP_USER_MESSAGE_PROCESSOR = new GioCdpUserMessageProcessor();
        private static final GioEventMessageProcessor EVENT_MESSAGE_PROCESSOR = new GioEventMessageProcessor();
    }

    public static MessageProcessor getApiInstance(GIOMessage msg) {
        if (msg instanceof GioCdpEventMessage) {
            return SingletonInstances.CDP_EVENT_MESSAGE_PROCESSOR;
        } else if (msg instanceof GIOEventMessage) {
            return SingletonInstances.EVENT_MESSAGE_PROCESSOR;
        } else if (msg instanceof GioCdpUserMessage){
            return SingletonInstances.CDP_USER_MESSAGE_PROCESSOR;
        } else {
            return null;
        }

    }

}
