package com.shadowsdream.util;

import java.io.IOException;
import java.util.Properties;
import java.io.FileReader;

public class PropertyLoader {

    private PropertyLoader(){}

    public static String getDelimiter() throws IOException {
        Properties p = new Properties();
        p.load(new FileReader("/home/shadows-dream/IdeaProjects/contact-list-app/src/main/resources/configuration.properties"));
        return p.getProperty("delimiter");
    }
}
