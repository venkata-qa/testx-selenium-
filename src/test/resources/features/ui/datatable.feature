@datatable
Feature: Data table example tests for the login journey

  Scenario:S1-As a registered user, user should be able to logged in
    Given I am in App main site
    And I enter the data for the following fields on the "LoginPage"
      | fieldName     | data          |
      | usernameInput | standard_user |
      | passwordInput | secret_sauce  |
    When I click the following elements on the "LoginPage"
      | fieldName   |
      | loginButton |
    And I verify the text of the following fields on the "HomePage"
      | fieldName    | fieldText |
      | productsMain | Products  |

    Then I verify that the text: "Swag" partially matches the current page title
    And I verify that the text: "Swag Labs" exactly matches the current page title
