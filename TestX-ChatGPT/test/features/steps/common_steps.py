import time

from pytest_bdd import given, when, then, parsers
from selenium.webdriver import ActionChains
from selenium.webdriver.support.select import Select

from pages.BasePage import BasePage


@given('I open the login page')
def open_login_page(browser_setup):
    browser_setup.get('https://www.amazon.in')


@then('Login the application')
def login_app(browser_setup):
    BasePage(browser_setup).load_and_find_page_element("USERNAME_INPUT", "AmazonHomePage") \
        .send_keys('standard_user')
    BasePage(browser_setup).load_and_find_page_element("PASSWORD_INPUT", "AmazonHomePage") \
        .send_keys('secret_sauce')
    BasePage(browser_setup).load_and_find_page_element("LOGIN_BUTTON", "AmazonHomePage").click()


@when(parsers.parse('I enter text {text} in {element} field on {page} page'))
def enter_username(browser_setup, text, element, page):
    ele = BasePage(browser_setup).load_and_find_page_element(element, page)
    ele.clear()
    ele.send_keys(text)


@when(parsers.parse('I clicked {element} on {page} page'))
def click_login_button(browser_setup, element, page):
    ele = BasePage(browser_setup).load_and_find_page_element(element, page)
    ele.click()


@when(parsers.parse('I verify the label as {expected_text} of {element} on {page} page'))
def verify_label(browser_setup, expected_text, element, page):
    ele = BasePage(browser_setup).load_and_find_page_element(element, page)
    assert ele.text == expected_text


# Used
@then(parsers.parse('I verify that the text: "{expected_page_title}" {assertion_type} matches the current page title'))
def verify_the_page_title(browser_setup, expected_page_title, assertion_type):
    current_page_title = browser_setup.title
    print(current_page_title)
    # Perform the verification based on the assertion type
    if assertion_type == 'exactly':
        assert expected_page_title == current_page_title, "Page title does not match exactly"
    elif assertion_type == 'partially':
        assert expected_page_title in current_page_title, "Page title does not match partially"
    else:
        raise ValueError(f"Invalid assertion type: {assertion_type}")


@given(parsers.parse('I navigate to {url} page'))
def navigate_to(browser_setup, url):
    browser_setup.get(url)
# 
# 
# @when(parsers.parse('I enter text {text} in {locator} field on {page} page'))
# def enter_text(browser_setup, text, locator, page):
#     element = BasePage(browser_setup).load_and_find_page_element(locator, page)
#     element.clear()
#     element.send_keys(text)
# 
# 
# @when(parsers.parse('I click on {locator} on {page} page'))
# def click_element(browser_setup, locator, page):
#     element = BasePage(browser_setup).load_and_find_page_element(locator, page)
#     element.click()
# 
# 
# @when(parsers.parse('I select {option} from {locator} dropdown on {page} page'))
# def select_option(browser_setup, option, locator, page):
#     element = BasePage(browser_setup).load_and_find_page_element(locator, page)
#     select = Select(element)
#     select.select_by_visible_text(option)
# 
# 
# @when(parsers.parse('I get the {locator} text and assert with expected text on {page} page'))
# def assert_text(browser_setup, locator, page):
#     element = BasePage(browser_setup).load_and_find_page_element(locator, page)
#     actual_text = element.text
#     expected_text = "Confirmation"
#     assert actual_text == expected_text, f"Expected text: {expected_text}, Actual text: {actual_text}"


@when(parsers.parse('Switch to new window'))
def switch_new_window(browser_setup):
    print(browser_setup.title)
    BasePage(browser_setup).switch_to_new_window()


@when(parsers.parse('I hover on {element} on {page} page'))
def hover_element(browser_setup, element, page):
    driver = BasePage(browser_setup)
    element = driver.load_and_find_page_element(element, page)
    ActionChains(browser_setup).move_to_element(element).perform()


@when(parsers.parse('I click on {element} on {page} page'))
def click_element(browser_setup, element, page):
    driver = BasePage(browser_setup)
    element = driver.load_and_find_page_element(element, page)
    element.click()


@when(parsers.parse('I enter {text} in {field} field on {page} page'))
def enter_text(browser_setup, text, field, page):
    driver = BasePage(browser_setup)
    element = driver.load_and_find_page_element(field, page)
    element.clear()
    element.send_keys(text)


@then(parsers.parse('I verify {count} count on {page} page'))
def verify_count(browser_setup, count, page):
    driver = BasePage(browser_setup)
    element = driver.load_and_find_page_element(count, page)
    assert element.text == '2'
    print(element.text)
    print('Done')


@then(parsers.parse('I assert {message} message on {page} page'))
def assert_message(browser_setup, message, page):
    driver = BasePage(browser_setup)
    element = driver.load_and_find_page_element(message, page)
    assert element.text == 'Thank you for your purchase!'


@then(parsers.parse('I assert {order_id} should be numeric on {page} page'))
def assert_order_id(browser_setup, order_id, page):
    driver = BasePage(browser_setup)
    element = driver.load_and_find_page_element(order_id, page)
    assert element.text.isdigit()


@when('Wait to sync the app')
def wait_app(browser_setup):
    time.sleep(2)


@when(parsers.parse('I select {option}" from {locator} dropdown on {page} page'))
def select_option(browser_setup, option, locator, page):
    element = BasePage(browser_setup).load_and_find_page_element(locator, page)
    select = Select(element)
    select.select_by_visible_text(option)


@when(parsers.parse('I switch to frame {frame} on {page} page'))
def select_option(browser_setup, frame, page):
    base_page = BasePage(browser_setup)
    ele = base_page.load_and_find_page_element(frame, page)
    base_page.switch_to_frame(ele)
