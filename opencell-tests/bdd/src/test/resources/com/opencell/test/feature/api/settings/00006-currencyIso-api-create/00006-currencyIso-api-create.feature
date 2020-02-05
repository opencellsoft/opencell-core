@settings
Feature: Create/Update Currency Iso by API

  Background: The system is configured.

  @admin @superadmin
  Scenario Outline: <status> <action> Currency Iso by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The currency iso is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                             | dto            | api                         | action         | statusCode | status  | errorCode                        | message                                                                |
      | api/settings/00006-currencyIso-api-create/SuccessTest.json           | CurrencyIsoDto | /currencyIso/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/settings/00006-currencyIso-api-create/SuccessTest.json           | CurrencyIsoDto | /currencyIso/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Currency with code=TST already exists.                                 |
      | api/settings/00006-currencyIso-api-create/DO_NOT_EXIST.json          | CurrencyIsoDto | /currencyIso/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Currency with code=NOT_EXIST does not exists.                          |
      | api/settings/00006-currencyIso-api-create/SuccessTest1.json          | CurrencyIsoDto | /currencyIso/               | Update         |        200 | SUCCESS |                                  |                                                                        |
      | api/settings/00006-currencyIso-api-create/SuccessTest1.json          | CurrencyIsoDto | /currencyIso/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/settings/00006-currencyIso-api-create/GENERIC_API_EXCEPTION.json | CurrencyIsoDto | /currencyIso/createOrUpdate | CreateOrUpdate |        500 | FAIL    | GENERIC_API_EXCEPTION            | ERROR: null value in column                                            |
      | api/settings/00006-currencyIso-api-create/MISSING_PARAMETER.json     | CurrencyIsoDto | /currencyIso/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | api/settings/00006-currencyIso-api-create/INVALID_PARAMETER.json     | CurrencyIsoDto | /currencyIso/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | currencyCode la taille doit être comprise entre 0 et 3                 |
