package com.testx.web.api.selenium.restassured.qe.ui.testrunners;

import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import org.testng.annotations.Test;
import xray.xrayhelper.ImportFeaturesAndTestResults;

public class CreateXrayTestCases {

    //@Test
    //TODO:Un-comment the above line
    public void createXray() {
        ImportFeaturesAndTestResults test = new ImportFeaturesAndTestResults();
        String token = test.createTokenWithAPIKey(
                ConfigurationManager.getConfiguration().xrayClientId(),
                ConfigurationManager.getConfiguration().xrayClientSecretId());
        test.importFeatureJIRA(token
                , "src/test/resources/features/ui/sample.feature",
                ConfigurationManager.getConfiguration().xrayProjectKey(), "", "", "",
                "21", "", "", "");
    }
}
