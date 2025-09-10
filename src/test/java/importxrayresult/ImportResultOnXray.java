package importxrayresult;

import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import org.testng.annotations.Test;
import xray.xrayhelper.ImportFeaturesAndTestResults;

public class ImportResultOnXray {

    @Test
    public void importXrayResult(){
        ImportFeaturesAndTestResults test = new ImportFeaturesAndTestResults();
        String token = test.createTokenWithAPIKey(ConfigurationManager.getConfiguration().xrayClientId(),ConfigurationManager.getConfiguration().xrayClientSecretId());
        System.out.println("************CREATING TEST EXECUTION IN JIRA******************");
//        String testExecutionKey = test.importJsonReportToJira(token,"target/cucumber-report/cucumber.json","src/test/resources/testExecutionDetails.json");
        String testExecutionKey = test.importJsonReportToJira(token,"target/cucumber-report/cucumber.json","src/test/resources/testExecutionDetails_OnPrem.json");
        System.out.println("TestX_Xray - CREATED TEST EXECUTION IS:::::"+ testExecutionKey);
    }
}
