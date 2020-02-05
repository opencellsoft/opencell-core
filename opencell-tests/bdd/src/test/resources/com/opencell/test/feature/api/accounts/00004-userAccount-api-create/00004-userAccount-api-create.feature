@accounts
Feature: Create/Update a User Account by API

  Background: The classic offer is already executed
    Create a BillingAccount is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> a User Account by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The user account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                             | dto            | api                                 | action         | statusCode | status  | errorCode                        | message                                                                                  |
      | api/accounts/00004-userAccount-api-create/SuccessTest.json           | UserAccountDto | /account/userAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                          |
      | api/accounts/00004-userAccount-api-create/SuccessTest.json           | UserAccountDto | /account/userAccount/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | UserAccount with code=TEST already exists.                                               |
      | api/accounts/00004-userAccount-api-create/DO_NOT_EXIST.json          | UserAccountDto | /account/userAccount/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | UserAccount with code=NOT_EXIST does not exists.                                         |
      | api/accounts/00004-userAccount-api-create/SuccessTest1.json          | UserAccountDto | /account/userAccount/               | Update         |        200 | SUCCESS |                                  |                                                                                          |
      | api/accounts/00004-userAccount-api-create/SuccessTest1.json          | UserAccountDto | /account/userAccount/createOrUpdate | Create         |        200 | SUCCESS |                                  |                                                                                          |
      | api/accounts/00004-userAccount-api-create/MISSING_PARAMETER.json     | UserAccountDto | /account/userAccount/createOrUpdate | Create         |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: billingAccount.         |
      | api/accounts/00004-userAccount-api-create/INVALID_PARAMETER.json     | UserAccountDto | /account/userAccount/createOrUpdate | Create         |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.model.billing.AccountStatusEnum` from String |
      | api/accounts/00004-userAccount-api-create/ENTITY_DOES_NOT_EXIST.json | UserAccountDto | /account/userAccount/createOrUpdate | Create         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingAccount with code=billingAccount does not exists.                                 |
