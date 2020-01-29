@settings
Feature: Create Currency Iso by API

  Background: The system is configured.

  @admin @superadmin
  Scenario Outline: Create Currency Iso by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The currency iso is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                         | dto            | api                         | statusCode | status  | errorCode                       | message                                                                |
      | settings/00006-currencyIso-api-create/SuccessTest.json           | CurrencyIsoDto | /currencyIso/createOrUpdate |        200 | SUCCESS |                                 |                                                                        |
      | settings/00006-currencyIso-api-create/SuccessTest.json           | CurrencyIsoDto | /currencyIso/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | Currency with code=TST already exists.                                 |
      | settings/00006-currencyIso-api-create/SuccessTest1.json          | CurrencyIsoDto | /currencyIso/createOrUpdate |        200 | SUCCESS |                                 |                                                                        |
      | settings/00006-currencyIso-api-create/GENERIC_API_EXCEPTION.json | CurrencyIsoDto | /currencyIso/createOrUpdate |        500 | FAIL    | GENERIC_API_EXCEPTION           | ERROR: null value in column                                            |
      | settings/00006-currencyIso-api-create/MISSING_PARAMETER.json     | CurrencyIsoDto | /currencyIso/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values: code. |
      | settings/00006-currencyIso-api-create/INVALID_PARAMETER.json     | CurrencyIsoDto | /currencyIso/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER               | currencyCode la taille doit être comprise entre 0 et 3                 |
