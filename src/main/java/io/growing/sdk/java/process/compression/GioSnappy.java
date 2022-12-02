package io.growing.sdk.java.process.compression;

import io.growing.sdk.java.snappy.Snappy;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-22 18:52
 */
public class GioSnappy implements CompressionTool {

    public byte[] compress(byte[] message) {
        return Snappy.compress(message);
    }
}