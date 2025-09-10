
package com.testx.web.api.selenium.restassured.qe.ui.testrunners.retry;

import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {

    int count = 0;

    @Override
    public boolean retry(ITestResult iTestResult) {
        int maxTry = Integer.parseInt(BaseSetup.configuration.retryCount());
        if (!iTestResult.isSuccess()){
            if (count<maxTry){
                count++;
                return true;
            }
        }
        return false;
    }
}
