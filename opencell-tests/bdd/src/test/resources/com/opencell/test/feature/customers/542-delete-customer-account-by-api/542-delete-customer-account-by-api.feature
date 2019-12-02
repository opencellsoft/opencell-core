Feature: Delete a Customer Account by API

  Background: The classic offer is already executed
              Create a Customer Account by API is already executed


  @admin @superadmin
  Scenario Outline: Delete a Customer Account by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                | dto                | api                       | statusCode | status  | errorCode                        | message                                              |
      | customers/541-create-a-customer-account-by-api/SuccessTest.json         | CustomerAccountDto | /account/customerAccount/ |        200 | SUCCESS |                                  |                                                      |
      | customers/542-delete-customer-account-by-api/ENTITY_DOES_NOT_EXIST.json | CustomerAccountDto | /account/customerAccount/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerAccount with code=NOT_EXIST does not exists. |
