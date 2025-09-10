from selenium.webdriver.common.by import By


class Amazon:
    SEARCH_FIELD = (By.ID, "twotabsearchtextbox")
    SEARCH_ICON = (By.ID, "nav-search-submit-button")
    SEARCH_RESULT_ITEM = (By.XPATH, ".//img[@class='s-image']")
    BUY_NOW_BUTTON = (By.ID, "buy-now-button")
    USE_THIS_ADDRESS_BUTTON = (By.ID, "orderSummaryPrimaryActionBtn-announce")
    PAYMENT_OPTIONS_DROPDOWN = "id=payment-options"
    CARD_NUMBER_FIELD = "id=card-number"
    CVV_NUMBER_FIELD = "id=cvv-number"
    COMPLETE_ORDER_BUTTON = "id=complete-order"
    CONFIRMATION_TEXT = "id=confirmation-text"
