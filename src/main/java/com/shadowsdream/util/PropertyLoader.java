package com.shadowsdream.util;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.io.FileReader;

public final class PropertyLoader {

    private static final String CONFIGURATION_FILE = "/home/shadows-dream/IdeaProjects/contact-list-app/src/main/resources/configuration.properties";
    private static final String SMTP_PROPERTIES_FILE = "/home/shadows-dream/IdeaProjects/contact-list-app/src/main/resources/smtp.properties";


    private PropertyLoader(){}


    public static String getDelimiter() throws IOException {
        return getConfigurationProperties().getProperty("delimiter");
    }


    public static Properties getSmtpProtrties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(SMTP_PROPERTIES_FILE));
        return properties;
    }


    private static Properties getConfigurationProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(CONFIGURATION_FILE));
        return properties;
    }
}
