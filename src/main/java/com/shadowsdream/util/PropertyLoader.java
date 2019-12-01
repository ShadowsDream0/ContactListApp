package com.shadowsdream.util;

import java.io.IOException;
import java.util.Properties;
import java.io.FileReader;

public class PropertyLoader {

    private PropertyLoader(){}

    public static String getProperties() throws IOException {
        Properties p = new Properties();
        p.load(new FileReader("C:\\Users\\Shadow'sDream\\IdeaProjects\\contact_list_app\\src\\main\\resources" +
                "\\configuration.properties"));
        return p.getProperty("delimiter");
    }
}
