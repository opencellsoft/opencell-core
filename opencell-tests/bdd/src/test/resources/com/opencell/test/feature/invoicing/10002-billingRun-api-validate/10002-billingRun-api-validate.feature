@settings
Feature: Validate billing run by API

  Background: The classic offer is already executed
              Create a Billing run by API is already executed
              
  @admin @superadmin
  Scenario Outline: Validate billing run by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the Validate "<api>"
    Then The entity is validated 
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                      | dto | api                                   | statusCode | status  | errorCode                        | message                               |
      | invoicing/10002-billingRun-api-validate/Success.json                          |     | /billing/invoicing/validateBillingRun |    200     | SUCCESS |                                  |                                       |
      | invoicing/10002-billingRun-api-validate/BUSINESS_API_EXCEPTION_1.json         |     | /billing/invoicing/validateBillingRun |    500     | FAIL    | BUSINESS_API_EXCEPTION           | BillingRun with status VALIDATED cannot be validated |
      | invoicing/10002-billingRun-api-validate/BUSINESS_API_EXCEPTION_2.json         |     | /billing/invoicing/validateBillingRun |    500     | FAIL    | BUSINESS_API_EXCEPTION           | Cant find BillingRun with id:100|
