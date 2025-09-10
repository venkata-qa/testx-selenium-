package com.testx.web.api.selenium.restassured.qe.ui.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;

@LoadPolicy(LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:properties/general.properties",
        "classpath:properties/db.properties",
        "classpath:properties/grid.properties"})
public interface Configuration extends Config {

    @Key("retryCount")
    String retryCount();

    @Key("baseurl")
    String baseUrl();

    @Key("browsername")
    String browserName();

    @Key("isheadless")
    boolean isHeadLess();

    @Key("gridURL")
    String gridURL();

    @Key("dbDriver")
    String dbDriver();

    @Key("dbUrl")
    String dbUrl();

    @Key("dbUrlWithDatabaseName")
    String dbUrlWithDatabaseName();

    @Key("dbSchema")
    String dbSchema();

    @Key("xrayClientId")
    String xrayClientId();

    @Key("xrayProjectKey")
    String xrayProjectKey();

    @Key("isXrayEnable")
    String isXrayEnable();

    @Key("OnPrem")
    String isOnPrem();

    @Key("jiraURL")
    String jiraURL();

    @Key("xrayClientSecretId")
    String xrayClientSecretId();

    @Key("jiraToken")
    String jiraToken();

    @Key("jiraUserName")
    String jiraUserName();

    @Key("target")
    String target();

    @Key("browser")
    String browser();

    @Key("launchBrowserViaExe")
    String launchBrowserViaExe();

    @Key("headless")
    Boolean headless();

    @Key("url.base")
    String url();

    @Key("timeout")
    int timeout();

    @Key("grid.url")
    String gridUrl();

    @Key("grid.port")
    String gridPort();

    @Key("db.host")
    String dbHost();

    @Key("db.port")
    String dbPort();

    @Key("db.name")
    String dbName();

    @Key("dbUser")
    String dbUser();

    @Key("dbPassword")
    String dbPassword();


}

