@settings @ignore
Feature: Delete Tax by API

  Background: The classic offer is already executed
              Create Tax by API is already executed


  @admin @superadmin
  Scenario Outline: Delete a Tax Plan by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                 | dto    | api   | statusCode | status  | errorCode                        | message                                  |
      | settings/00010-tax-api-create/SuccessTest.json           | TaxDto | /tax/ |        200 | SUCCESS |                                  |                                          |
      | settings/10010-tax-api-delete/ENTITY_DOES_NOT_EXIST.json | TaxDto | /tax/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Tax with code=NOT_EXIST does not exists. |
