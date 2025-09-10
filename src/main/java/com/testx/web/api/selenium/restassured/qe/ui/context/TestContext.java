package com.testx.web.api.selenium.restassured.qe.ui.context;

import com.testx.web.api.selenium.restassured.qe.ui.webdriver.DriverManager;

public class TestContext {

    DriverManager driverManager;

    public TestContext() {
        driverManager = new DriverManager();
    }

    public DriverManager getDriverManager() {
        return driverManager;
    }
}
