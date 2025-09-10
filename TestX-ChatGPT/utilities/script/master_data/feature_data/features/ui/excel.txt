@excel
Feature: Excel example tests for the login journey

  Scenario:S1-Login to the application using excel data
    Given I am in App main site
    When I enter the data for the following fields from the excel file TestData:LoginPage:TID1
      | FieldName     | PageName  |
      | usernameInput | LoginPage |
      | passwordInput | LoginPage |
    When I click the "loginButton" on the "LoginPage"
    And I verify that the text: "Swag Labs" exactly matches the current page title

