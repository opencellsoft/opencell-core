@accounts
Feature: Delete a Customer Account by API

  Background: The classic offer is already executed
              Create a Customer Account by API is already executed


  @admin @superadmin
  Scenario Outline: <action> a Customer Account by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                    | dto                | api                       | action | statusCode | status  | errorCode                        | message                                              |
      | accounts/00002-customerAccount-api-create/SuccessTest.json  | CustomerAccountDto | /account/customerAccount/ | Delete |        200 | SUCCESS |                                  |                                                      |
      | accounts/00002-customerAccount-api-create/DO_NOT_EXIST.json | CustomerAccountDto | /account/customerAccount/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerAccount with code=NOT_EXIST does not exists. |
