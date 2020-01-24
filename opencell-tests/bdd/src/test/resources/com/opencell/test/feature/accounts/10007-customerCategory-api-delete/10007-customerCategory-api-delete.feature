@accounts
Feature: Delete Customer Category by API

  Background: The system is configured
              Create Customer Category by API is already executed


  @admin @superadmin
  Scenario Outline: Delete Customer Category by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                              | dto                 | api                               | statusCode | status  | errorCode                        | message                                               |
      | accounts/00007-customerCategory-api-create/SuccessTest.json           | CustomerCategoryDto | /account/customer/removeCategory/ |        200 | SUCCESS |                                  |                                                       |
      | accounts/10007-customerCategory-api-delete/ENTITY_DOES_NOT_EXIST.json | CustomerCategoryDto | /account/customer/removeCategory/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerCategory with code=NOT_EXIST does not exists. |
