@accounts @test
Feature: Create/Update Account Hierarchy by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: <status> <action> Account Hierarchy by API <errorCode>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The account hierarchy is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                   | dto                 | api                                      | action         | statusCode | status  | errorCode                        | message                                                                                                                                                              |
      | api/accounts/00008-accountHierarchy-api-create/SuccessTest.json            | AccountHierarchyDto | /account/accountHierarchy/               | Create         |        200 | SUCCESS |                                  |                                                                                                                                                                      |
      | api/accounts/00008-accountHierarchy-api-create/SuccessTest.json            | AccountHierarchyDto | /account/accountHierarchy/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Customer with code=TEST_CH already exists.                                                                                                                           |
      | api/accounts/00008-accountHierarchy-api-create/DO_NOT_EXIST.json           | AccountHierarchyDto | /account/accountHierarchy/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Customer with code=NOT_EXIST does not exists.                                                                                                                        |
      | api/accounts/00008-accountHierarchy-api-create/SuccessTest1.json           | AccountHierarchyDto | /account/accountHierarchy/               | Update         |        200 | SUCCESS |                                  |                                                                                                                                                                      |
      | api/accounts/00008-accountHierarchy-api-create/SuccessTest1.json           | AccountHierarchyDto | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                                                                                      |
      | api/accounts/00008-accountHierarchy-api-create/MISSING_PARAMETER1.json     | AccountHierarchyDto | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: customerCode                                                                                        |
      | api/accounts/00008-accountHierarchy-api-create/MISSING_PARAMETER2.json     | AccountHierarchyDto | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: customerCategoryCode, sellerCode, currencyCode, countryCode, billingCycleCode, languageCode, email. |
      | api/accounts/00008-accountHierarchy-api-create/ENTITY_DOES_NOT_EXIST1.json | AccountHierarchyDto | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Currency with code=NOT_EXIST does not exists.                                                                                                                        |
      | api/accounts/00008-accountHierarchy-api-create/ENTITY_DOES_NOT_EXIST2.json | AccountHierarchyDto | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | SellerCode with code=NOT_EXIST does not exists.                                                                                                                      |
      | api/accounts/00008-accountHierarchy-api-create/INVALID_PARAMETER.json      | AccountHierarchyDto | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.util.Date` from String                                                                                                        |
