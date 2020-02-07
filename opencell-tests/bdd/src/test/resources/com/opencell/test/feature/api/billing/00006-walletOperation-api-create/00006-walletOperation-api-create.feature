@billing
Feature: Create/Update Wallet operation by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> Wallet operation by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The wallet operation is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto                | api                       | action | statusCode | status  | errorCode                       | message                                                         |
      | api/billing/00006-walletOperation-api-create/SuccessTest.json       | WalletOperationDto | /billing/wallet/operation | POST   |        200 | SUCCESS |                                 |                                                                 |
      | api/billing/00006-walletOperation-api-create/SuccessTest.json       | WalletOperationDto | /billing/wallet/operation | POST   |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | WalletOperation with code=TEST already exists.                  |
      | api/billing/00006-walletOperation-api-create/MISSING_PARAMETER.json | WalletOperationDto | /billing/wallet/operation | POST   |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values |
      | api/billing/00006-walletOperation-api-create/INVALID_PARAMETER.json | WalletOperationDto | /billing/wallet/operation | POST   |        400 | FAIL    | INVALID_PARAMETER               | Cannot deserialize value of type `java.util.Date` from String   |
