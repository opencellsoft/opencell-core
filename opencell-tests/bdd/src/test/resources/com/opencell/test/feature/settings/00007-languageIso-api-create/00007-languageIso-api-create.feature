@settings
Feature: Create Language Iso by API

  Background: The system is configured.

  @admin @superadmin
  Scenario Outline: Create Language Iso by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The language iso is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                         | dto            | api                         | statusCode | status  | errorCode                       | message                                                                |
      | settings/00007-languageIso-api-create/SuccessTest.json           | LanguageIsoDto | /languageIso/createOrUpdate |        200 | SUCCESS |                                 |                                                                        |
      | settings/00007-languageIso-api-create/SuccessTest.json           | LanguageIsoDto | /languageIso/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | Language with code=TST already exists.                                 |
      | settings/00007-languageIso-api-create/SuccessTest1.json          | LanguageIsoDto | /languageIso/createOrUpdate |        200 | SUCCESS |                                 |                                                                        |
      | settings/00007-languageIso-api-create/GENERIC_API_EXCEPTION.json | LanguageIsoDto | /languageIso/createOrUpdate |        500 | FAIL    | GENERIC_API_EXCEPTION           | ERROR: null value in column                                            |
      | settings/00007-languageIso-api-create/MISSING_PARAMETER.json     | LanguageIsoDto | /languageIso/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values: code. |
      | settings/00007-languageIso-api-create/INVALID_PARAMETER.json     | LanguageIsoDto | /languageIso/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER               | languageCode la taille doit être comprise entre 0 et 3                 |
