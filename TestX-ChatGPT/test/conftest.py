import pytest
from selenium import webdriver


@pytest.fixture(scope="function")
def browser_setup(request):
    _driver = webdriver.Chrome()
    yield _driver
    _driver.quit()


@pytest.fixture(scope="function", autouse=True)
def browser_teardown(request, browser_setup):
    yield
