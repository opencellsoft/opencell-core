@settings @ignore
Feature: Delete Language Iso by API

  Background: System is configured.
    Create Language Iso by API already executed.


  @admin @superadmin
  Scenario Outline: Delete Language Iso by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>" with identifier "code"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                         | dto            | api           | statusCode | status  | errorCode                        | message                                       |
      | settings/00007-languageIso-api-create/SuccessTest.json           | LanguageIsoDto | /languageIso/ |        200 | SUCCESS |                                  |                                               |
      | settings/10007-languageIso-api-delete/ENTITY_DOES_NOT_EXIST.json | LanguageIsoDto | /languageIso/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Language with code=NOT_EXIST does not exists. |
