package com.shadowsdream.util.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContactListLogger {

    private static final Logger LOG = LoggerFactory.getLogger(Logger.class);

    private ContactListLogger(){}

    public static Logger getLog() {
        return LOG;
    }
}
