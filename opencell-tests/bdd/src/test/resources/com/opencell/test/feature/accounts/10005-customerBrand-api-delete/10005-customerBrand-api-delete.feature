@accounts
Feature: Delete Customer Brand by API

  Background: The system is configured
              Create Customer Brand by API is already executed


  @admin @superadmin
  Scenario Outline: Delete Customer Brand by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                           | dto              | api                            | statusCode | status  | errorCode                        | message                                              |
      | accounts/00005-customerBrand-api-create/SuccessTest.json           | CustomerBrandDto | /account/customer/removeBrand/ |        200 | SUCCESS |                                  |                                                      |
      | accounts/10005-customerBrand-api-delete/ENTITY_DOES_NOT_EXIST.json | CustomerBrandDto | /account/customer/removeBrand/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerBrand with code=NOT_EXIST does not exists. |
