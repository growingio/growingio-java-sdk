package io.growing.sdk.java.process;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.sender.net.ContentTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 10:44 AM
 */
public interface MessageProcessor {

    String apiHost(String ai);

    ContentTypeEnum contentType();

    byte[] process(List<GIOMessage> msgList);

    Map<String, String> headers();

    boolean skipIllegalMessage(GIOMessage gioMessage);
}
