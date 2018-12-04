package io.growing.sdk.java.process;

import io.growing.sdk.java.dto.GIOMessage;
import io.growing.sdk.java.process.compression.CompressionTool;
import io.growing.sdk.java.process.compression.GioSnappy;
import io.growing.sdk.java.process.serialize.JsonSerialize;
import io.growing.sdk.java.process.serialize.SerializeTool;
import io.growing.sdk.java.sender.net.NetProviderAbstract;

import java.util.List;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-25 00:22
 */
public class ProcessClient {
    private final static SerializeTool serialize = new JsonSerialize();
    private final static CompressionTool compress = new GioSnappy();

    public static byte[] process(List<GIOMessage> msgList) {
        byte[] serialized = serialize.serialize(msgList);

        if (NetProviderAbstract.needCompress()) {
            return compress.compress(serialized);
        }

        return serialized;
    }

    public static String getOrigianlMsgJsonArray(byte[] bytes) {
        byte[] original = bytes;

        if (NetProviderAbstract.needCompress()) {
            original = compress.uncompress(bytes);
        }
        
        return serialize.deserialize(original);

    }
}