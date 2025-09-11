
package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions;

import com.testx.web.api.selenium.restassured.qe.ui.config.Configuration;
import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.webdriver.DriverManagerUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.util.List;

public class BaseSetup {
    
    private static final Logger log = LoggerFactory.getLogger(BaseSetup.class);

    public static Configuration configuration;
    private static final String UI_PAGE_OBJECTS_PACKAGE = "com.testx.web.api.selenium.restassured.qe.ui.pageobjects.";
    TestContext testContext;
    protected WebDriver driver;
    protected DriverManagerUtils driverManagerUtils;

    public BaseSetup(TestContext testContext) {
        this.testContext = testContext;
        driver = testContext.getDriverManager().getDriver();
        configuration =  ConfigurationManager.getConfiguration();
        driverManagerUtils = new DriverManagerUtils(driver);
    }

    public WebElement loadWebElement(String elementName, String pageClassName) {
        return loadAndFindPageElement(elementName, pageClassName);
    }

    public List<WebElement> loadWebElements(String elementName, String pageClassName) {
        return loadAndFindPageElements(elementName, pageClassName);
    }

    private WebElement loadAndFindPageElement(String locatorName, String pageClassName) {
       WebElement element = null;
        Class<?> loadClass = null;
        try {
            loadClass = Class.forName(UI_PAGE_OBJECTS_PACKAGE + pageClassName);
        } catch (ClassNotFoundException e) {
            log.error("Class not found when setting up page object: {}", e.getMessage(), e);
        }
        PageFactory.initElements(driver, loadClass);
        Field field = null;
        try {
            field = loadClass.getDeclaredField(locatorName);
        } catch (NoSuchFieldException e) {
            log.error("Field not found when setting up page object: {}", e.getMessage(), e);
        }
        try {
            element = (WebElement) field.get(null);
        } catch (IllegalAccessException e) {
            log.error("Illegal access when setting up page object: {}", e.getMessage(), e);
        }
        return element;
    }
    private List<WebElement> loadAndFindPageElements(String locatorName, String pageClassName) {
        List<WebElement> element = null;
        Class<?> loadClass = null;
        try {
            loadClass = Class.forName(UI_PAGE_OBJECTS_PACKAGE + pageClassName);
        } catch (ClassNotFoundException e) {
            log.error("Class not found when setting up page object: {}", e.getMessage(), e);
        }
        PageFactory.initElements(driver, loadClass);
        Field field = null;
        try {
            field = loadClass.getDeclaredField(locatorName);
        } catch (NoSuchFieldException e) {
            log.error("Field not found when setting up page object: {}", e.getMessage(), e);
        }
        try {
            element = (List<WebElement>) field.get(null);
        } catch (IllegalAccessException e) {
            log.error("Illegal access when setting up page object: {}", e.getMessage(), e);
        }
        return element;
    }

}
