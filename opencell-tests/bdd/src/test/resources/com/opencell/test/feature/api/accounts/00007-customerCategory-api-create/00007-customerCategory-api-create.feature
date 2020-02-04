@accounts
Feature: Create/Update Customer Category by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: <status> <action> Customer Category by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The customer category is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                  | dto                 | api                                      | action         | statusCode | status  | errorCode                        | message                                                                |
      | api/accounts/00007-customerCategory-api-create/SuccessTest.json           | CustomerCategoryDto | /account/customer/createCategory         | Create         |        200 | SUCCESS |                                  |                                                                        |
      | api/accounts/00007-customerCategory-api-create/SuccessTest.json           | CustomerCategoryDto | /account/customer/createCategory         | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | CustomerCategory with code=TEST already exists.                        |
      | api/accounts/00007-customerCategory-api-create/DO_NOT_EXIST.json          | CustomerCategoryDto | /account/customer/updateCategory         | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerCategory with code=NOT_EXIST does not exists.                  |
      | api/accounts/00007-customerCategory-api-create/SuccessTest1.json          | CustomerCategoryDto | /account/customer/updateCategory         | Update         |        200 | SUCCESS |                                  |                                                                        |
      | api/accounts/00007-customerCategory-api-create/SuccessTest1.json          | CustomerCategoryDto | /account/customer/createOrUpdateCategory | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/accounts/00007-customerCategory-api-create/SuccessTest2.json          | CustomerCategoryDto | /account/customer/updateCategory         | Update         |        200 | SUCCESS |                                  |                                                                        |
      | api/accounts/00007-customerCategory-api-create/SuccessTest2.json          | CustomerCategoryDto | /account/customer/createOrUpdateCategory | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/accounts/00007-customerCategory-api-create/MISSING_PARAMETER.json     | CustomerCategoryDto | /account/customer/createOrUpdateCategory | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | api/accounts/00007-customerCategory-api-create/ENTITY_DOES_NOT_EXIST.json | CustomerCategoryDto | /account/customer/createOrUpdateCategory | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | AccountingCode with code=TEST does not exists.                         |
      | api/accounts/00007-customerCategory-api-create/INVALID_PARAMETER.json     | CustomerCategoryDto | /account/customer/createOrUpdateCategory | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.lang.Boolean` from String       |
