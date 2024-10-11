package io.growing.sdk.java.sender;

import io.growing.sdk.java.com.googlecode.protobuf.format.JsonFormat;
import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.dto.GioCDPMessage;
import io.growing.sdk.java.logger.GioLogger;
import io.growing.sdk.java.process.EventProcessorClient;
import io.growing.sdk.java.process.MessageProcessor;
import io.growing.sdk.java.sender.net.HttpUrlProvider;

import java.util.Arrays;

public class SyncSender {

    private static final JsonFormat JSON_FORMAT = new JsonFormat();

    public SendResult sendMsg(final GIOMessage msg) {
        String projectKey = msg.getProjectKey();

        MessageProcessor processor = EventProcessorClient.getProcessor(msg.getMessageClass());
        byte[] processed = processor.process(Arrays.asList(msg));

        if (processed != null && processed.length > 0) {
            RequestDto requestDto = new RequestDto.Builder()
                    .setUrl(processor.apiHost(projectKey))
                    .setContentType(processor.contentType())
                    .setBytes(processed)
                    .setHeaders(processor.headers())
                    .build();

            SendResult result = HttpUrlProvider.getInstance().toSendSync(requestDto);
            if (result.getState() != SendResult.State.SUCCESS) {
                if (msg instanceof GioCDPMessage) {
                    GioLogger.file("send failed, result: " + result + ", data: " + JSON_FORMAT.printToString(((GioCDPMessage<?>) msg).getMessage()));
                }
            }
            return result;
        }

        return new SendResult(SendResult.State.UNKNOWN_ERROR, "unknown error");
    }
}
