
package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PrebuiltBrowserSteps extends BaseSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrebuiltBrowserSteps.class);
    TestContext testContext;

    public PrebuiltBrowserSteps(TestContext context) {
        super(context);
        this.testContext = context;
    }

    @Given("^I am in App main site")
    public void iAmInAppMainSite() {
        LOGGER.info("Navigate to: " + configuration.url());
        driver.get(configuration.url());
        driverManagerUtils.HandleMyWindows.put("Principal", driver.getWindowHandle());
        driverManagerUtils.verifyPageLoaded();
    }

    @Given("^I navigate to \"([^\"]*)\"$")
    public void navigate_to(String link) {
        driverManagerUtils.navigateTo(link);
    }

    @Given("^I navigate forward on the browser")
    public void navigate_forward() {
        driverManagerUtils.navigateForward();
    }

    @Given("^I navigate back on the browser")
    public void navigate_back() {
        driverManagerUtils.navigateBack();
    }

    @Given("^I refresh the current web page$")
    public void refresh_page() {
        driverManagerUtils.refreshPage();
    }

    @Given("^I switch to new window$")
    public void switchNewWindow() {
        driverManagerUtils.switchToNewWindow();
    }

    /**
     * Switch to a new windows by name
     */
    @Given("^I go to (.*?) window$")
    public void switchNewNamedWindow(String windowsName) {
        driverManagerUtils.windowHandle(windowsName);
    }

    /**
     * Switch to the previous windows
     */
    @Given("^I switch to previous window$")
    public void switchPreviousWindows() {
        LOGGER.info("Switching of previous windows");
        driver.switchTo().defaultContent();

    }

    /**
     * Close a windows by title
     */
    @Given("^I close window \"(.*?)\"$")
    public void closeNewWindows(String windowTitle) {
        driverManagerUtils.closeWindowByName(windowTitle);
    }

    @Given("^I maximize the windows")
    public void iMaximizeTheWindows() {
        driver.manage().window().maximize();
    }

    /**
     * Zoom out until the element is displayed
     */
    @Given("^I zoom out page till I see element \"(.*?)\"$")
    public void zoomTillElementDisplay(String elementName, String pageClassName) throws Exception {
        driverManagerUtils.zoomTillElementDisplay(loadWebElement(elementName, pageClassName));
    }

    @Given("^I open new tab with URL (.*)")
    public void OpenNewTabWithURL(String url) {
        driverManagerUtils.openNewTabWithURL(url);
    }


    @Given("I switch to parent frame")
    public void iSwitchToParentFrame() {
        driverManagerUtils.switchToParentFrame();
    }

    @Given("I switch to the {string} frame on the {string}")
    public void i_switch_to_the_frame_on_the(String elementName, String pageClassName) {
        driverManagerUtils.switchToFrame(loadWebElement(elementName, pageClassName));
    }

    // step to switch to main content
    @Given("^I switch to main content of the page$")
    public void switch_to_default_content() {
        driverManagerUtils.switchToDefaultContent();
    }

    /**
     * Scroll to the (top/end) of the page.
     */
    @Given("^I scroll to (top|end) of page$")
    public void scrollPage(String to) throws Exception {
        driverManagerUtils.scrollPage(to);
    }

    /**
     * Scroll to an element.
     */
    @Given("^I scroll to element (.+) on (.+)$")
    public void scrollToElement(String elementName, String pageClassName) throws Exception {
        driverManagerUtils.scrollToElement(loadWebElement(elementName, pageClassName));
    }

    //Tested
    @Then("I verify checkbox selected state {string} for {string} on {string}")
    public void isCheckBoxSelected(String expectedState, String element, String pageClassName) {
        boolean flag = false;
        WebElement ele = loadWebElement(element, pageClassName);
        if (!(ele.getAttribute("class").contains("uncheck"))) {
            flag = true;
        }
        Assert.assertEquals(String.valueOf(flag), expectedState);
    }

    //Tested
    @Then("{string} checkbox {string} on {string} page")
    public void checkBox(String expectedState, String element, String pageClassName) {
        WebElement ele = loadWebElement(element, pageClassName);
        if (expectedState.contains("true") && (ele.getAttribute("class").contains("uncheck"))) {
            ele.click();
        }
        if (expectedState.contains("false") && !(ele.getAttribute("class").contains("uncheck"))) {
            ele.click();
        }
    }
    public static String textToEnter = null;
    @When("I click on created element {string} on the {string}")
    public void clickOnCreatedReport(String elementName, String pageClassName) {
        List<WebElement> webElements = loadWebElements(elementName, pageClassName);
        for (WebElement ele : webElements) {
            if (ele.getText().contains(textToEnter)) {
                ele.click();
                break;
            }
        }
    }

    @When("I verify the created element option {string} on the {string}")
    public void verifyDropdown(String elementName, String pageClassName) {
        WebElement element = loadWebElement(elementName, pageClassName);
        element.click();
        WebElement createdElement = driver.findElement(
                By.xpath(".//*[text()='" + textToEnter + "']"));
        Assert.assertTrue(createdElement.isDisplayed());
    }

    //Tested
    @Then("Perform drag {string} and drop {string} on {string} page")
    public void dragAndDrop(String sourceElement, String targetElement, String pageClassName) {
        WebElement sourceEle = loadWebElement(sourceElement, pageClassName);
        WebElement targetEle = loadWebElement(targetElement, pageClassName);
        Actions actions = new Actions(driver);
        actions.dragAndDrop(sourceEle, targetEle).build().perform();
    }

    @When("I enter the value in {string} on the {string}")
    public void iEnterTheValueInToTheFieldOnThePage(String elementName, String pageClassName) {
        WebElement element = loadWebElement(elementName, pageClassName);
        textToEnter = RandomStringUtils.randomAlphabetic(10);
        driverManagerUtils.enterText(driver, element, textToEnter);
    }

    @When("I write email for schedule the report")
    public void email() throws InterruptedException {
        driver.switchTo().frame("");
        WebElement subject = driver.findElements(By.xpath(".//*[@contenteditable='true']")).get(0);
        subject.click();
        subject.sendKeys(RandomStringUtils.randomAlphabetic(10));
        driver.switchTo().defaultContent();
        driver.switchTo().frame("");
        WebElement body = driver.findElements(By.xpath(".//*[@contenteditable='true']")).get(0);
        body.click();
        body.sendKeys("https: www.test.com");
        driver.switchTo().defaultContent();
        driver.findElement(By.xpath(".//*[@class='keyword']//*[text()='Report library URL']")).click();
    }

    //Tested
    public String getLatestFile() {
        String path = System.getProperty("user.dir") + "\\src\\test\\resources\\external_downloads";
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length > 0 && files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                return files[0].getAbsolutePath();
            }
        }
        return null;
    }

    List<String> excelHeaders = new ArrayList<>();
    //Tested
//    @Then("Get headers from downloaded excel file")
//    public void  getHeaderFromFile() throws IOException {
//        String file = getLatestFile();
//        try (FileInputStream fis = new FileInputStream(file)) {
//            Workbook workbook = WorkbookFactory.create(fis);
//            Sheet sheet = workbook.getSheetAt(0);
//            Row row = sheet.getRow(0);
//            if (row != null) {
//                for (Cell cell : row) {
//                    excelHeaders.add(cell.getStringCellValue().trim());
//                }
//            }
//        }
//    }
    @Then("Get headers from downloaded file")
    public void getHeaderFromFile() throws IOException {
        String file = getLatestFile();
        String fileType = Files.probeContentType(Paths.get(file));

        if (fileType != null && fileType.equals("application/vnd.ms-excel")) {
            // Handle as Excel file
            try (FileInputStream fis = new FileInputStream(file)) {
                Workbook workbook = WorkbookFactory.create(fis);
                Sheet sheet = workbook.getSheetAt(0);
                Row row = sheet.getRow(0);
                if (row != null) {
                    for (Cell cell : row) {
                        excelHeaders.add(cell.getStringCellValue().trim());
                    }
                }
            }
        } else if (fileType != null && fileType.equals("application/json")) {
            // Handle as JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            try (FileReader reader = new FileReader(file)) {
                JsonNode jsonNode = objectMapper.readTree(reader);
                // Assuming that you want the field names (keys) as headers
                Iterator<String> fieldNames = jsonNode.fieldNames();
                while (fieldNames.hasNext()) {
                    excelHeaders.add(fieldNames.next().trim());
                }
            }
        } else if (fileType == null || fileType.equals("text/plain")) {
            // Handle as plain text file
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String headerLine = reader.readLine();
                if (headerLine != null) {
                    String[] headers = headerLine.split("\t");  // Adjust the delimiter if necessary
                    excelHeaders.addAll(Arrays.asList(headers));
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + fileType);
        }
    }

    //Tested

    //Tested
    List<String> list = new ArrayList<>();
    @Then("Get text of {string} all elements on {string}")
    public void getAllText(String element, String pageClassName) {
        List<WebElement> webElements = loadWebElements(element, pageClassName);
        for (WebElement webElement : webElements) {
            list.add(webElement.getText().trim());
        }
    }

    //Tested
    @Then("Verify dollar in all products price")
    public void verifyDollar(){
        boolean result = list.stream().allMatch(price -> price.contains("$"));
        Assert.assertTrue(result);
    }

    //Tested
    @Then("Verify the downloaded file name {string}")
    public void verifyFileName(String expectedFileName) {
        String file = getLatestFile();
        Assert.assertTrue(file.contains(expectedFileName));
    }
}