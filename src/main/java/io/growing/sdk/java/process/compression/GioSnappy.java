package io.growing.sdk.java.process.compression;

import io.growing.sdk.java.exception.GIOMessageException;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-22 18:52
 */
public class GioSnappy implements CompressionTool{

    public byte[] compress(byte[] message)  {
        try {
            return org.xerial.snappy.Snappy.compress(message);
        } catch (IOException e) {
            throw new GIOMessageException("failed to compress message", e);
        }
    }

    public byte[] uncompress(byte[] message)  {
        try {
            return org.xerial.snappy.Snappy.uncompress(message);
        } catch (IOException e) {
            throw new GIOMessageException("failed to compress message", e);
        }
    }
}