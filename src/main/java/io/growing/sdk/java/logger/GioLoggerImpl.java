package io.growing.sdk.java.logger;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-24 17:36
 */
public class GioLoggerImpl implements GioLoggerInterface {

    private String logTemplate(String msg) {
        return "[" + Thread.currentThread().getName() + "] " + msg;
    }

    @Override
    public void debug(String msg) {
        if (msg != null)
            System.out.println(logTemplate(msg));
    }

    @Override
    public void error(String msg) {
        if (msg != null)
            System.err.println(logTemplate(msg));
    }
}