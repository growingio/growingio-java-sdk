package io.growing.sdk.java.process;

import io.growing.sdk.java.dto.*;
import io.growing.sdk.java.process.impl.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 10:44 AM
 */
public class EventProcessorClient {

    private static class SingletonInstances {
        private static final GioCdpEventMessageProcessor CDP_EVENT_MESSAGE_PROCESSOR = new GioCdpEventMessageProcessor();
        private static final GioCdpUserMessageProcessor CDP_USER_MESSAGE_PROCESSOR = new GioCdpUserMessageProcessor();
        private static final GioCdpItemMessageProcessor CDP_ITEM_MESSAGE_PROCESSOR = new GioCdpItemMessageProcessor();
        private static final GioEventMessageProcessor EVENT_MESSAGE_PROCESSOR = new GioEventMessageProcessor();
        private static final GioCdpUserMappingMessageProcessor CDP_ID_MAPPING_MESSAGE_PROCESSOR = new GioCdpUserMappingMessageProcessor();
    }

    private static final Map<Class<?>, MessageProcessor> processors = new HashMap<Class<?>, MessageProcessor>();

    static {
        processors.put(GioCdpEventMessage.class, new GioCdpEventMessageProcessor());
        processors.put(GIOEventMessage.class, new GioEventMessageProcessor());
        processors.put(GioCdpItemMessage.class, new GioCdpItemMessageProcessor());
        processors.put(GioCdpUserMessage.class, new GioCdpUserMessageProcessor());
        processors.put(GioCdpUserMappingMessage.class, new GioCdpUserMappingMessageProcessor());
    }

    public static Collection<MessageProcessor> getProcessors(){
        return processors.values();
    }

    public static MessageProcessor getApiInstance(GIOMessage msg) {
        return processors.get(msg.getClass());
    }

}
