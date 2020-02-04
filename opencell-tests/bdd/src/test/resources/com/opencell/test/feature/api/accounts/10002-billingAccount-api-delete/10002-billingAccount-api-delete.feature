@accounts
Feature: Delete a Billing Account by API

  Background: The classic offer is already executed
              Create Billing Account by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> Billing Accocunt by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                       | dto               | api                      | action | statusCode | status  | errorCode                        | message                                             |
      | api/accounts/00003-billingAccount-api-create/SuccessTest.json  | BillingAccountDto | /account/billingAccount/ | Delete |        200 | SUCCESS |                                  |                                                     |
      | api/accounts/00003-billingAccount-api-create/DO_NOT_EXIST.json | BillingAccountDto | /account/billingAccount/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingAccount with code=NOT_EXIST does not exists. |
