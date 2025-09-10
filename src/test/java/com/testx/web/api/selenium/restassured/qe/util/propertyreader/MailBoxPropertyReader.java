
package com.testx.web.api.selenium.restassured.qe.util.propertyreader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MailBoxPropertyReader {

    public static String HOSTKEY;
    public static String HOSTVALUE;
    public static String PORTKEY;
    public static String PORTVALUE;
    public static String SSLKEY;
    public static String SSLVALUE;
    public static String PROTOCOL;
    public static String FOLDERNAME;
    public static String USERNAME;
    public static String PASSWORD;


    static
    {
        Properties properties=new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/mailbox.properties"));
            HOSTKEY = properties.get("host.key").toString();
           HOSTVALUE = properties.get("host.value").toString();
           PORTKEY = properties.get("port.key").toString();
           PORTVALUE = properties.get("port.value").toString();
           SSLKEY = properties.get("ssl.key").toString();
           SSLVALUE = properties.get("ssl.value").toString();
           PROTOCOL = properties.get("protocol.value").toString();
           FOLDERNAME = properties.get("folder.value").toString();
            USERNAME = properties.get("mail.username").toString();
            PASSWORD = properties.get("mail.password").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
