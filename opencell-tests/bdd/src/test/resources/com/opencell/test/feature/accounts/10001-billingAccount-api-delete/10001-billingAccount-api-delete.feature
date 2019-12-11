@accounts
Feature: Delete a Billing Account by API

  Background: The classic offer is already executed
              Create Billing Account by API is already executed


  @admin @superadmin
  Scenario Outline: Delete Billing Accocunt by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto               | api                      | statusCode | status  | errorCode                        | message                                             |
      | accounts/00003-billingAccount-api-create/SuccessTest.json           | BillingAccountDto | /account/billingAccount/ |        200 | SUCCESS |                                  |                                                     |
      | accounts/10001-billingAccount-api-delete/ENTITY_DOES_NOT_EXIST.json | BillingAccountDto | /account/billingAccount/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingAccount with code=NOT_EXIST does not exists. |
