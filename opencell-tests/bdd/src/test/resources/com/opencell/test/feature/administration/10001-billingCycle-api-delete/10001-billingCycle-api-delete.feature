@administration
Feature: Delete Billing Cycle by API

  Background: The classic offer is already executed
              Create invoicing cycle is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> Billing Cycle by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                       | dto             | api            | action | statusCode | status  | errorCode                        | message                                           |
      | administration/00001-billingCycle-api-create/Success.json      | BillingCycleDto | /billingCycle/ | Delete |        200 | SUCCESS |                                  |                                                   |
      | administration/00001-billingCycle-api-create/DO_NOT_EXIST.json | BillingCycleDto | /billingCycle/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingCycle with code=NOT_EXIST does not exists. |
