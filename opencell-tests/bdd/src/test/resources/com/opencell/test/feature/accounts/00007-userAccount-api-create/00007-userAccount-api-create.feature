@customers @ignore
Feature: Create a User Account by API

  Background: The classic offer is already executed
    Create a BillingAccount is already executed


  @admin @superadmin
  Scenario Outline: Create a User Account by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The User account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                         | dto                | api                                     | statusCode | status  | errorCode                        | message                                                                                |
      | customers/00004-userAccount-api-create/SuccessTest.json          | UserAccountDto     | /account/userAccount/createOrUpdate     |     200    | SUCCESS |                                  |                                                                                        |
      | customers/00004-userAccount-api-create/SuccessTest.json          | UserAccountDto     | /account/userAccount/createOrUpdate     |     200    | SUCCESS |                                  |                                                                                        |
      | customers/00004-userAccount-api-create/SuccessTest.json          | UserAccountDto     | /account/userAccount/createOrUpdate     |     403    | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | UserAccount with code=TEST already exists.                                             |
      | customers/00004-userAccount-api-create/MISSING_PARAMETER.json    | UserAccountDto     | /account/userAccount/createOrUpdate     |     400    | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: billingAccount.       |
      | customers/00004-userAccount-api-create/INVALID_PARAMETER.json    | UserAccountDto     | /account/userAccount/createOrUpdate     |     400    | FAIL    | INVALID_PARAMETER                | Can not deserialize value of type org.meveo.model.billing.AccountStatusEnum from String|
      | customers/00004-userAccount-api-create/ENTITY_DOES_NOT_EXIST.json| UserAccountDto     | /account/userAccount/createOrUpdate     |     404    | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingAccount with code=billingAccount does not exists.                                            |
