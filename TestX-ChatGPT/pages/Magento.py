from selenium.webdriver.common.by import By


class Magento:
    MEN = (By.XPATH, ".//*[@role='menuitem']//span[text()='Men']")
    TOPS = (By.XPATH, "(.//*[@role='menuitem']//span[text()='Tops'])[2]")
    JACKETS = (By.XPATH, "(.//*[@role='menuitem']//span[text()='Jackets'])[2]")
    SEARCH_ITEM = (By.CLASS_NAME, "product-image-photo")
    M_SIZE = (By.XPATH, ".//*[@aria-label='M']")

    BLUE_COLOR = (By.XPATH, ".//*[@aria-label='Blue']")
    QTY = (By.ID, "qty")
    ADD_TO_CART = (By.ID, "product-addtocart-button")
    CART_COUNT = (By.XPATH, ".//*[@class='counter-number']")
    PROCEED_TO_CHECKOUT = (By.XPATH, ".//*[@title='Proceed to Checkout']")
    EMAIL = (By.XPATH, "(.//*[@type='email' and @name='username'])[2]")
    F_NAME = (By.XPATH, ".//*[@name='firstname']")
    L_NAME = (By.XPATH, ".//*[@name='lastname']")
    STREET = (By.NAME, "street[0]")
    CITY = (By.NAME, "city")
    STATE = (By.NAME, "region_id")
    ZIP = (By.NAME, "postcode")
    COUNTRY = (By.NAME, "country_id")
    TEL = (By.NAME, "telephone")
    NEXT = (By.XPATH, ".//*[text()='Next']")
    PLACE_ORDER = (By.XPATH, ".//*[@title='Place Order']")
    THANKS_MSG = (By.XPATH, ".//*[@data-ui-id='page-title-wrapper']")
    ORDER_ID = (By.XPATH, ".//*[@class='checkout-success']//span")
    FRAME = (By.CLASS_NAME, "form-login")







