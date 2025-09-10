package com.testx.web.api.selenium.restassured.qe.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConfig {
    static String prefix;
    static String hostName;
    static String port;
    static String DBName;
    static String userName;
    static String password;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/test/resources/properties/db.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        prefix = properties.get("Prefix.db.In.Use").toString();
        hostName = properties.get(prefix + ".db.host").toString();
        port = properties.get(prefix + ".db.port").toString();
        DBName = properties.get(prefix + ".db.name").toString();
        userName = properties.get(prefix + ".db.user").toString();
        password = properties.get(prefix + ".db.password").toString();
    }
          public static Connection   getConnection(){
              String jdbcUrl = "jdbc:mysql://"+hostName+":"+port+"/"+DBName+"";
              Connection connection;
              try {
                  connection = DriverManager.getConnection(jdbcUrl, userName, password);
              } catch (SQLException e) {
                  throw new RuntimeException(e);
              }
              return connection;
          }
}