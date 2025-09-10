// This is a Java Class

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Home_Page { 
@FindBy(id = "search_field_element")
public static WebElement SEARCH_FIELD_ELEMENT;

@FindBy(id = "search_icon_element")
public static WebElement SEARCH_ICON_ELEMENT;

