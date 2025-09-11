package com.testx.web.api.selenium.restassured.qe.ui.testrunners;

import com.testx.web.api.selenium.restassured.qe.util.FolderUtil;
import importxrayresult.ImportResultOnXray;
import io.cucumber.java.Scenario;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager.getConfiguration;

@Test
@CucumberOptions(
        plugin = {
                "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm", // Ensure you have the Allure plugin configured
                "pretty",
                "html:target/cucumber-report/report.html",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
                ,"json:target/cucumber-report/cucumber.json",
                "pretty",
                "html:target/cucumber-report/cucumber-pretty",
                "json:target/cucumber-report/CucumberTestReport.json",
                "rerun:target/failedRerun.txt"},
                monochrome = true,
                features = "src/test/resources/features",
                glue = {"com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions",
                        "com.testx.web.api.selenium.restassured.qe.api.stepdefs",
                        "com.testx.web.api.selenium.restassured.qe.database",
                        "com.testx.web.api.selenium.restassured.qe.backend.stepdefs"}
               ,tags = "@OnlyOneUI"
)
public class RunUITest extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(/*parallel = true*/) // Running scenarios in parallel if desired
    public Object[][] scenarios() {
        return super.scenarios();
    }


    @BeforeSuite(alwaysRun = true)
    public static void clearTargetScreenshotsFolder() throws IOException {
        FolderUtil.clearFolder(Paths.get("target/cucumber-report/screenshots/"));
    }
    @AfterSuite(alwaysRun = true)
    public static void afterAllTests() {
        if(Boolean.parseBoolean(getConfiguration().isXrayEnable())) {
//            ImportResultOnXray importer = new ImportResultOnXray();
//            importer.importXrayResult();
        }
        else{
            System.out.println("TestX_Xray - xray import found as "+getConfiguration().isXrayEnable());
        }
        FolderUtil.report();
    }
}