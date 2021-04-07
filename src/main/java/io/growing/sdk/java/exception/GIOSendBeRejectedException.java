package io.growing.sdk.java.exception;

import java.io.Serializable;
import java.util.concurrent.RejectedExecutionException;

/**
 * will throw when msg queue is full.
 *
 * @author liguobin@growingio.com
 * @version 1.0, 2020/10/29
 */
public class GIOSendBeRejectedException extends RejectedExecutionException implements Serializable {

    public GIOSendBeRejectedException(String message) {
        super(message);
    }

    public GIOSendBeRejectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
