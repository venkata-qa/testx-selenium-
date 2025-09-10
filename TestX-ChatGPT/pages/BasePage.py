import importlib

from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException


class BasePage:
    def __init__(self, driver):
        self.driver = driver

    # Used
    def locate_element(self, locator, timeout=10):
        try:
            element = WebDriverWait(self.driver, timeout).until(
                EC.presence_of_element_located(locator)
            )
            return element
        except TimeoutException:
            raise AssertionError(f"Element {locator} not found within {timeout} seconds.")

    # Used
    def load_and_find_page_element(self, locator, page_class_name):
        try:
            page_module = importlib.import_module(f'pages.{page_class_name}')
            page_class = getattr(page_module, page_class_name)
            element = getattr(page_class, locator)

            ele = self.locate_element(element, 10)
            self.highlight_element(ele, 'red')
            return ele
        except Exception as e:
            print(f"Error: {str(e)}")
            return None

    # Used
    def highlight_element(self, web_element, color):
        try:
            print("Highlighting:", web_element)
            self.driver.execute_script("arguments[0].style.border='3px solid {}'".format(color), web_element)
        except NoSuchElementException as e:
            print("Element highlight method could not find the locator:", web_element)
        except Exception as e:
            print("Some error occurred while highlighting the locator:", web_element)

    def verify_page_loaded(self):
        WebDriverWait(self.driver, 10).until(
            lambda web_driver: web_driver.execute_script("return document.readyState") == "complete"
        )

    def switch_to_new_window(self):
        print(self.driver.window_handles)
        for win_handle in self.driver.window_handles:
            self.driver.switch_to.window(win_handle)
            print("Switched to the new window")

    def switch_to_frame(self, web_element):
        print("Switching to frame: " + web_element.tag_name)
        self.driver.switch_to.frame(web_element)

    def switch_to_parent_frame(self):
        print("Switching to parent frame")
        self.driver.switch_to.parent_frame()

    def switch_to_default_content(self):
        print("Switching to default content")
        self.driver.switch_to.default_content()