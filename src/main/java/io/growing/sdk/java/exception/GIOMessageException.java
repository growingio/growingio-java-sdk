package io.growing.sdk.java.exception;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 7:58 PM
 */
public class GIOMessageException extends RuntimeException {
    private static final long serialVersionUID = 5009965954277376931L;

    public GIOMessageException(String message) {
        super(message);
    }

    public GIOMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}