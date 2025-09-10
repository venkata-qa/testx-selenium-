
package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class PrebuiltExcelInputSteps extends BaseSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrebuiltBrowserSteps.class);
    TestContext testContext;

    public PrebuiltExcelInputSteps(TestContext context) {
        super(context);
        this.testContext = context;
    }

//    @When("I enter the value {string} into the {string} on the {string}")
//    public void iEnterTheValueInToTheFieldOnThePage(String valueToEnter, String elementName, String pageClassName) {
//        WebElement element = loadWebElement(elementName, pageClassName);
//        driverManagerUtils.enterText(driver, element, valueToEnter);
//    }

    @When("^I enter the data for the following fields from the excel file (.*)$")
    public void iEnterValuesInToTheFieldsOnThePage(String excelFileData, DataTable dataTable) throws FilloException {
        String ProjectWorkingDirectory = System.getProperty("user.dir");
        String fileDataPath = ProjectWorkingDirectory + "\\src\\test\\resources\\excel_data\\";

        String[] parts = excelFileData.split(":");
        String excelFileName = parts[0];
        String excelFileSheetName = parts[1];
        String excelFileRowID = parts[2];

        Fillo fillo=new Fillo();
        Connection connection=fillo.getConnection(fileDataPath + excelFileName + ".xlsx");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        Recordset recordset = null;

        for (Map<String, String> column : rows) {
            String fieldName = column.get("FieldName");
            String pageClassName = column.get("PageName");
            String strQuery = String.format("Select * from %s where TEST_ID='%s'", excelFileSheetName, excelFileRowID);
            recordset = connection.executeQuery(strQuery);
            while(recordset.next())
            {
                WebElement webElement = loadWebElement(fieldName, pageClassName);
                driverManagerUtils.enterText(driver, webElement, recordset.getField(fieldName));
            }
        }
        assert recordset != null;
        recordset.close();
        connection.close();
    }
}
