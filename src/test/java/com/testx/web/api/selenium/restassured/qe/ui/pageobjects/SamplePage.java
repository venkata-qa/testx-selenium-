
package com.testx.web.api.selenium.restassured.qe.ui.pageobjects;

import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SamplePage extends AbstractPageObject {

    protected SamplePage(TestContext testContext) {
        super(testContext);
    }

    @FindBy(id = "datePickerMonthYearInput")
    public static WebElement datePickerInput;
}
