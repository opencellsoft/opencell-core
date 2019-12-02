Feature: Delete Billing Cycle by API

  Background: The classic offer is already executed
              Create invoicing cycle is already executed


  @admin @superadmin
  Scenario Outline: Delete Billing Cycle by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                | dto             | api            | statusCode | status  | errorCode                        | message                                           |
      | administration/119--invoicing--cycle--create-a--billing-cycle/Success.json              | BillingCycleDto | /billingCycle/ |        200 | SUCCESS |                                  |                                                   |
      | administration/530-invoicing--cycle--delete-a--billing-cycle/ENTITY_DOES_NOT_EXIST.json | BillingCycleDto | /billingCycle/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingCycle with code=NOT_EXIST does not exists. |
