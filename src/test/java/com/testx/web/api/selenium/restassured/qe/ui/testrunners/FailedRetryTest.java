
package com.testx.web.api.selenium.restassured.qe.ui.testrunners;

import com.testx.web.api.selenium.restassured.qe.ui.testrunners.retry.Retry;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
@CucumberOptions
        (plugin = {"io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm",
                "pretty",
                "html:target/cucumber-report/report.html",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
                ,"json:target/cucumber-report/cucumber.json",
                "pretty",
                "html:target/cucumber-report/cucumber-pretty",
                "json:target/cucumber-report/CucumberTestReport.json"},
                monochrome = true,
                features = "src/test/resources/features",
                glue = {"com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions",
                        "com.testx.web.api.selenium.restassured.qe.api.stepdefs",
                        "com.testx.web.api.selenium.restassured.qe.backend.stepdefs"}
                ,tags = ""
                )

public class FailedRetryTest extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider()
    public Object[][] scenarios() {
        return super.scenarios();
    }
    @Test(
            groups = {"cucumber"},
            description = "Run Cucumber Scenarios",
            dataProvider = "scenarios",
            retryAnalyzer = Retry.class
    )
    @Override
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper){
        super.runScenario(pickleWrapper, featureWrapper);
    }

}
