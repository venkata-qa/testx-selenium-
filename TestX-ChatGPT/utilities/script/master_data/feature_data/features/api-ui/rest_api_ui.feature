@test88
Feature: To Validate the API data to UI data

  @apitest
  Scenario Outline: Get users Status and validate
    Given I have API "<API>"
    And I set content-type as JSON
    When I call method 'GET'
    Then I get the response
    Then I save the initial response for "<API>"
    Then I verify the data of below element to actual "<API>" data on the "HomePage" page
      | apiPath       |uiElement|
      | data[0].email | uiData   |
    Examples:
      | API           |
      | get_all_users |



     ## Note : The above scenario will give assertion error as UI and API both having different data