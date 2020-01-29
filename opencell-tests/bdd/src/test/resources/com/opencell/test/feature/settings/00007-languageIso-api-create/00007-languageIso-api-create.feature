@settings
Feature: Create/Update Language Iso by API

  Background: The system is configured.

  @admin @superadmin
  Scenario Outline: <status> <action> Language Iso by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The language iso is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                         | dto            | api                         | action         | statusCode | status  | errorCode                        | message                                                                |
      | settings/00007-languageIso-api-create/SuccessTest.json           | LanguageIsoDto | /languageIso/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | settings/00007-languageIso-api-create/SuccessTest.json           | LanguageIsoDto | /languageIso/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Language with code=TST already exists.                                 |
      | settings/00007-languageIso-api-create/DO_NOT_EXIST.json          | LanguageIsoDto | /languageIso/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Language with code=NOT_EXIST does not exists.                          |
      | settings/00007-languageIso-api-create/SuccessTest1.json          | LanguageIsoDto | /languageIso/               | Update         |        200 | SUCCESS |                                  |                                                                        |
      | settings/00007-languageIso-api-create/SuccessTest1.json          | LanguageIsoDto | /languageIso/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | settings/00007-languageIso-api-create/GENERIC_API_EXCEPTION.json | LanguageIsoDto | /languageIso/createOrUpdate | CreateOrUpdate |        500 | FAIL    | GENERIC_API_EXCEPTION            | ERROR: null value in column                                            |
      | settings/00007-languageIso-api-create/MISSING_PARAMETER.json     | LanguageIsoDto | /languageIso/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | settings/00007-languageIso-api-create/INVALID_PARAMETER.json     | LanguageIsoDto | /languageIso/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | languageCode la taille doit être comprise entre 0 et 3                 |
