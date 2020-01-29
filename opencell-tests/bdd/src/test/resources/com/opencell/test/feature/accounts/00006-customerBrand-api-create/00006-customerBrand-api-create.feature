@accounts
Feature: Create/Update Customer Brand Contact by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: <status> <action> Customer Brand by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The customer brand is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                       | dto              | api                                   | action | statusCode | status  | errorCode                        | message                                                                |
      | accounts/00006-customerBrand-api-create/SuccessTest.json       | CustomerBrandDto | /account/customer/createBrand         | Create |        200 | SUCCESS |                                  |                                                                        |
      | accounts/00006-customerBrand-api-create/SuccessTest.json       | CustomerBrandDto | /account/customer/createBrand         | Create |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | CustomerBrand with code=TEST already exists.                           |
      | accounts/00006-customerBrand-api-create/DO_NOT_EXIST.json      | CustomerBrandDto | /account/customer/updateBrand         | Update |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerBrand with code=NOT_EXIST does not exists.                     |
      | accounts/00006-customerBrand-api-create/SuccessTest1.json      | CustomerBrandDto | /account/customer/updateBrand         | Update |        200 | SUCCESS |                                  |                                                                        |
      | accounts/00006-customerBrand-api-create/SuccessTest1.json      | CustomerBrandDto | /account/customer/createOrUpdateBrand | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | accounts/00006-customerBrand-api-create/MISSING_PARAMETER.json | CustomerBrandDto | /account/customer/createOrUpdateBrand | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
