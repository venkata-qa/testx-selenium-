
package com.testx.web.api.selenium.restassured.qe.ui.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class HomePage extends AbstractPageObject {

    @FindBy(xpath = "//span[contains(text(),'Products')]")
    public static WebElement products;

    @FindBy(id = "shopping_cart_container")
    public static WebElement shoppingCartContainer;

    @FindBy(css = "span[class='title']")
    public static WebElement productsMain;




    @FindBy(xpath = "//h2[contains(@class,'tagline')][1]")
    public static WebElement uiData;

    @FindBy(xpath = "//b[contains(@class,'f12')]")
    public static List<WebElement> Sector_Indices;



    @FindBy(xpath = "//*[@id=\"content\"]/ul/li[17]/a")
    public static WebElement file_download_link;


    @FindBy(xpath = "//*[@id=\"content\"]/div/a[11]")
    public static WebElement test_file;












}