
package com.testx.web.api.selenium.restassured.qe.api.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.DataProvider;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

@Test
@CucumberOptions
        (plugin = {"io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm","pretty", "html:target/cucumber-report/report.html",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
                ,"json:target/cucumber-report/cucumber.json","pretty","html:target/cucumber-report/cucumber-pretty",
                "json:target/cucumber-report/CucumberTestReport.json"},
                monochrome = true,
                features = "src/test/resources/features",
                glue = {"com.testx.web.api.selenium.restassured.qe.api.stepdefs"}
               , tags = "@sample123")


public class RunAPITest extends AbstractTestNGCucumberTests {
        @Override
        @DataProvider
        public Object[][] scenarios() {
                return super.scenarios();
        }
}
