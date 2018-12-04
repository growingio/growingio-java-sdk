package io.growing.sdk.java.process.compression;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-25 00:31
 */
public interface CompressionTool {
    byte[] compress(byte[] bytes);
    byte[] uncompress(byte[] bytes);
}
