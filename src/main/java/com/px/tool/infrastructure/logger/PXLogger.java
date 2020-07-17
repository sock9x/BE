package com.px.tool.infrastructure.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PXLogger {
    private static final Logger logger = LoggerFactory.getLogger("[Phuong_an_so_logger]");

    public static void info(String message, Object... params) {
        logger.info(message, params);
    }

    public static void error(String message, Object... params) {
        logger.info(message, params);
    }
}
