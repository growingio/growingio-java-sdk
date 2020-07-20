package io.growing.sdk.java.demo;

import io.growing.sdk.java.logger.GioLoggerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 7/10/20 10:55 PM
 */
public class DemoLogger implements GioLoggerInterface {
	private final Logger logger = LoggerFactory.getLogger(DemoLogger.class);

	public void debug(String msg) {
		logger.debug(msg);
	}

	public void error(String msg) {
		logger.error(msg);
	}
}