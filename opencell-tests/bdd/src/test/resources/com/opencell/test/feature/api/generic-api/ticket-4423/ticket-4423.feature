@generic
Feature: Check if generic API return correct total for all API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Get all customers with filters by generic API and check if field <field> is equal to <value>
    Given The entity has the following information "<jsonFile>"
    When I call the generic "<action>" "<api>"
    Then I get a generic response
    And Validate that the statusCode is "<statusCode>"
    And The field "<field>" is equal to "<value>"

    Examples: 
      | jsonFile                                   | api                      | action | statusCode | field | value |
      | api/generic-api/ticket-4423/acct_cust.json | /v2/generic/all/customer | Post   |        200 | total |    97 |
      | api/generic-api/ticket-4423/one_cust.json  | /v2/generic/all/customer | Post   |        200 | total |     1 |

  Scenario Outline: Get a title and check if nested object auditable is readable
    Given The entity has the following information "<jsonFile>"
    When I call the generic "<action>" "<api>"
    Then I get a generic response
    And Validate that the statusCode is "<statusCode>"
    And The field "<field>" exists

    Examples: 
      | jsonFile                              | api                  | action | statusCode | field                  |
      | api/generic-api/ticket-4423/null.json | /v2/generic/title/-2 | Post   |        200 | data.auditable.creator |
