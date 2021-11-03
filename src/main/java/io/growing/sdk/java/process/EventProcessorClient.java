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

    private static final Map<Class<?>, MessageProcessor> processors = new HashMap<Class<?>, MessageProcessor>();

    static {
        processors.put(GioCdpEventMessage.class, new GioCdpEventMessageProcessor());
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
