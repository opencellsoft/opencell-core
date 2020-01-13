@accounts
Feature: Create Customer Category by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: Create Customer Category by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The customer category is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                              | dto                 | api                                      | statusCode | status  | errorCode                        | message                                                                |
      | accounts/00006-customerCategory-api-create/SuccessTest.json           | CustomerCategoryDto | /account/customer/createOrUpdateCategory |        200 | SUCCESS |                                  |                                                                        |
      | accounts/00006-customerCategory-api-create/SuccessTest1.json          | CustomerCategoryDto | /account/customer/createOrUpdateCategory |        200 | SUCCESS |                                  |                                                                        |
      | accounts/00006-customerCategory-api-create/SuccessTest2.json          | CustomerCategoryDto | /account/customer/createOrUpdateCategory |        200 | SUCCESS |                                  |                                                                        |
      | accounts/00006-customerCategory-api-create/SuccessTest.json           | CustomerCategoryDto | /account/customer/createCategory         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | CustomerCategory with code=TEST already exists.                        |
      | accounts/00006-customerCategory-api-create/MISSING_PARAMETER.json     | CustomerCategoryDto | /account/customer/createOrUpdateCategory |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | accounts/00006-customerCategory-api-create/ENTITY_DOES_NOT_EXIST.json | CustomerCategoryDto | /account/customer/createOrUpdateCategory |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | AccountingCode with code=TEST does not exists.                         |
      | accounts/00006-customerCategory-api-create/INVALID_PARAMETER.json     | CustomerCategoryDto | /account/customer/createOrUpdateCategory |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.lang.Boolean` from String       |
