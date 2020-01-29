@settings @country
Feature: Delete CountryIso by API

  Background: System is configured.
    Create CountryIso by API already executed.


  @admin @superadmin
  Scenario Outline: <action> CountryIso by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>" with identifier "countryCode"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                               | dto           | api          | action | statusCode | status  | errorCode                        | message                                      |
      | settings/00005-countryIso-api-create/SuccessTest.json  | CountryIsoDto | /countryIso/ | Delete |        200 | SUCCESS |                                  |                                              |
      | settings/00005-countryIso-api-create/DO_NOT_EXIST.json | CountryIsoDto | /countryIso/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Country with code=NOT_EXIST does not exists. |
