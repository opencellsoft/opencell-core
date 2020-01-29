@accounts
Feature: Create/Update a Customer Account by API

  Background: The classic offer is already executed
    Create a Customer is already executed


  @admin @superadmin
  Scenario Outline: <action> a Customer Account by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The customer account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                             | dto                | api                                     | action         | statusCode | status  | errorCode                        | message                                                                              |
      | accounts/00002-customerAccount-api-create/SuccessTest.json           | CustomerAccountDto | /account/customerAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                      |
      | accounts/00002-customerAccount-api-create/SuccessTest.json           | CustomerAccountDto | /account/customerAccount/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | CustomerAccount with code=TEST already exists.                                       |
      | accounts/00002-customerAccount-api-create/DO_NOT_EXIST.json          | CustomerAccountDto | /account/customerAccount/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerAccount with code=NOT_EXIST does not exists.                                 |
      | accounts/00002-customerAccount-api-create/SuccessTest1.json          | CustomerAccountDto | /account/customerAccount/               | Update         |        200 | SUCCESS |                                  |                                                                                      |
      | accounts/00002-customerAccount-api-create/SuccessTest1.json          | CustomerAccountDto | /account/customerAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                      |
      | accounts/00002-customerAccount-api-create/MISSING_PARAMETER.json     | CustomerAccountDto | /account/customerAccount/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: currency, language. |
      | accounts/00002-customerAccount-api-create/INVALID_PARAMETER.json     | CustomerAccountDto | /account/customerAccount/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.util.Date` from String                        |
      | accounts/00002-customerAccount-api-create/ENTITY_DOES_NOT_EXIST.json | CustomerAccountDto | /account/customerAccount/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Customer with code=NOT_EXIST does not exists.                                        |
