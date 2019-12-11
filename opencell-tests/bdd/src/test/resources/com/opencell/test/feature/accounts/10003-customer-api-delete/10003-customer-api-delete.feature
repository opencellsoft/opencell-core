@accounts
Feature: Delete a Customer by API

  Background: The classic offer is already executed
              Create a Customer by API is already executed


  @admin @superadmin
  Scenario Outline: Delete a Customer by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                      | dto         | api                | statusCode | status  | errorCode                        | message                                       |
      | accounts/00001-customer-api-create/SuccessTest.json           | CustomerDto | /account/customer/ |        200 | SUCCESS |                                  |                                               |
      | accounts/10003-customer-api-delete/ENTITY_DOES_NOT_EXIST.json | CustomerDto | /account/customer/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Customer with code=NOT_EXIST does not exists. |
