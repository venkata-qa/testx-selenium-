
package com.testx.web.api.selenium.restassured.qe.ui.pageobjects;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class LoginPage extends AbstractPageObject {

    @FindBy(xpath = ".//*[@class='download_xls']")
    public static WebElement excelDownloadButton;

    @FindBy(xpath = "(.//*[contains(@class,'rct-icon rct-icon-uncheck') or contains(@class,'rct-icon rct-icon-check')])[1]")
    public static WebElement checkBox;

    @FindBy(xpath = ".//iframe[@class='demo-frame lazyloaded']")
    public static WebElement iframeForDragAndDrop;
    @FindBy(xpath = "(.//*[contains(@class,'ui-draggable-handle')])[1]//img")
    public static WebElement dragSource;

    @FindBy(xpath = ".//*[@id='trash']")
    public static WebElement dragTarget;

    @FindBy(xpath = ".//*[@id='inventory_item_price']")
    public static List<WebElement> priceOfAllProducts;

    @FindBy(id = "user-name")
    public static WebElement usernameInput;

    @FindBy(id = "password")
    public static WebElement passwordInput;

    @FindBy(id = "login-button")
    public static WebElement loginButton;

    @FindBy(css = "h3[data-test='error']")
    public static WebElement loginButtonError;

}
