
package com.testx.web.api.selenium.restassured.qe.util.propertyreader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    public static String datasource_file;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/test/resources/properties/config.properties"));
            datasource_file = properties.get("datasource.file").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
