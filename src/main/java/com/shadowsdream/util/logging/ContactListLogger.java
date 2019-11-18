package com.shadowsdream.util.logging;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class ContactListLogger {
    private static final Logger LOG = LoggerFactory.getLogger(Logger.class);

    public static Logger getLog() {
        return LOG;
    }
}
