@sample
Feature: Example tests for the user login journey

@sample
  @OnlyOneUI
  Scenario:S1-As a registered user, user should be able to logged in
    Given I am in App main site
    And I enter the value "standard_user" into the "usernameInput" on the "LoginPage"
    And I enter the value "secret_sauce" into the "passwordInput" on the "LoginPage"
    When I click the "loginButton" on the "LoginPage"
    And I verify that the text: "Swag Labs" exactly matches the current page title

@sample
  Scenario:S2-As a locked user, user should not be logged in
    Given I am in App main site
    And I enter the value "locked_out_user" into the "usernameInput" on the "LoginPage"
    And I enter the value "secret_sauce" into the "passwordInput" on the "LoginPage"
    When I click the "loginButton" on the "LoginPage"
    And I verify that the following text should exactly matches the text of the loginButtonError field on the LoginPage
      | Epic sadface: Sorry, this user has been locked out. |

@sample
  Scenario:S3-As an user with invalid credentials, user should not be logged in
    Given I am in App main site
    And I enter the value "standard_user" into the "usernameInput" on the "LoginPage"
    And I enter the value "secret_sauce" into the "passwordInput" on the "LoginPage"
    When I click the "loginButton" on the "LoginPage"
    And I verify that the text: "Swag Labs" exactly matches the current page title

  #    Demo site URL - https://demoqa.com/checkbox
  @checkbox
  Scenario:S3-Checkbox test
    Given I am in App main site
    Then  I wait "2" seconds to synchronize the things on app
    When "true" checkbox "checkBox" on "LoginPage" page
    Then  I wait "2" seconds to synchronize the things on app
    When I verify checkbox selected state "true" for "checkBox" on "LoginPage"
    Then  I wait "2" seconds to synchronize the things on app
    When "false" checkbox "checkBox" on "LoginPage" page
    Then  I wait "2" seconds to synchronize the things on app
    When I verify checkbox selected state "false" for "checkBox" on "LoginPage"
    Then  I wait "2" seconds to synchronize the things on app
    When "true" checkbox "checkBox" on "LoginPage" page
    Then  I wait "2" seconds to synchronize the things on app
    When I verify checkbox selected state "true" for "checkBox" on "LoginPage"

    #    Demo site URL - https://www.globalsqa.com/demo-site/draganddrop/
  @dragdrop
  Scenario:S4-Drag and drop test
    Given I am in App main site
    Then  I wait "10" seconds to synchronize the things on app
    Then I switch to the "iframeForDragAndDrop" frame on the "LoginPage"
    When Perform drag "dragSource" and drop "dragTarget" on "LoginPage" page

