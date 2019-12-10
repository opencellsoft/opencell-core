@settings
Feature: Create CountryIso by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: Create CountryIso by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The country iso is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                   | dto           | api                        | statusCode | status  | errorCode                        | message                                                                        |
      | settings/00005-countryIso-api-create/SuccessTest.json                      | CountryIsoDto | /countryIso/createOrUpdate |        200 | SUCCESS |                                  |                                                                                |
      | settings/00005-countryIso-api-create/SuccessTest1.json                     | CountryIsoDto | /countryIso/createOrUpdate |        200 | SUCCESS |                                  |                                                                                |
      | settings/00005-countryIso-api-create/MISSING_PARAMETER.json                | CountryIsoDto | /countryIso/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: currencyCode. |
      | settings/00005-countryIso-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | CountryIsoDto | /countryIso/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | with code=NOT_EXIST does not exists.                                           |
      | settings/00005-countryIso-api-create/INVALID_PARAMETER.json                | CountryIsoDto | /countryIso/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Can not deserialize instance of java.util.ArrayList out of VALUE_STRING        |
