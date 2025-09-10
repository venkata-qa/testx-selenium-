```java
@Given("User is on the {string} page")
public void userOnPage(String pageName) {
    // Add code to navigate to the specific page
}

@When("I enter text {string} in {string} field on {string} page")
public void enterValue(String valueToEnter, String element, String pageName) {
    WebElement ele1 = load_and_find_page_element(element, pageName);
    ele1.clear();
    ele1.sendKeys(valueToEnter);
}

@And("I click on {string} on {string} page")
public void clickElement(String element, String pageName) {
    WebElement ele1 = load_and_find_page_element(element, pageName);
    ele1.click();
}

@And("I assert page title text with {string} on {string} page")
public void assertPageTitle(String expectedValue, String pageName) {
    String actualTitle = driver.getTitle();
    Assert.assertEquals(expectedValue, actualTitle);
}

@And("I click on {string} on {string} page")
public void clickOnItem(String element, String pageName) {
    WebElement ele1 = load_and_find_page_element(element, pageName);
    ele1.click();
}

@And("I assert element {string} text with {string} on {string} page")
public void assertElementText(String element, String expectedValue, String pageName) {
    WebElement ele1 = load_and_find_page_element(element, pageName);
    String actualText = ele1.getText();
    Assert.assertEquals(expectedValue, actualText);
}

@And("I click on {string} on {string} page")
public void clickToCompare(String element, String pageName) {
    WebElement ele1 = load_and_find_page_element(element, pageName);
    ele1.click();
}

@Then("I assert element {string} text with {string} on {string} page")
public void assertComparedView(String element, String expectedValue, String pageName) {
    WebElement ele1 = load_and_find_page_element(element, pageName);
    String actualText = ele1.getText();
    Assert.assertEquals(expectedValue, actualText);
}
```