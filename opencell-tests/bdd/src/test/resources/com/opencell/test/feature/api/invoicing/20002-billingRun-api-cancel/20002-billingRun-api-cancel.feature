@invoicing
Feature: Cancel billing run by API

  Background: The classic offer is already executed
              Create a Billing run by API is already executed


  @admin @superadmin
  Scenario Outline: Cancel billing run by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I cancell billing run
    Then The entity is cancelled
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                        | dto                | api                                 | statusCode | status  | errorCode                        | message                                 |
      | api/invoicing/20002-billingRun-api-cancel/Success.json                          | AuditableEntityDto | /billing/invoicing/cancelBillingRun |        200 | SUCCESS |                                  |                                         |
      | api/invoicing/20002-billingRun-api-cancel/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | AuditableEntityDto | /billing/invoicing/cancelBillingRun |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingRun with id=100 does not exists. |
