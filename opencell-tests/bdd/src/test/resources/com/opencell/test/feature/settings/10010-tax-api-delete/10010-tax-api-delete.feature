@settings
Feature: Delete Tax by API

  Background: The classic offer is already executed
              Create Tax by API is already executed


  @admin @superadmin
  Scenario Outline: <action> a Tax Plan by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                        | dto    | api   | action | statusCode | status | errorCode                        | message                                  |
      | settings/00010-tax-api-create/SuccessTest.json  | TaxDto | /tax/ | Delete |        500 | FAIL   | GENERIC_API_EXCEPTION            | ERROR: update or delete on table         |
      | settings/00010-tax-api-create/DO_NOT_EXIST.json | TaxDto | /tax/ | Delete |        404 | FAIL   | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Tax with code=NOT_EXIST does not exists. |
