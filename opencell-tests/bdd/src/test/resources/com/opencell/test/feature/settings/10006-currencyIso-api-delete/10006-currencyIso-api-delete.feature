@settings
Feature: Delete Currency Iso by API

  Background: System is configured.
    Create Currency Iso by API already executed.


  @admin @superadmin
  Scenario Outline: <action> Currency Iso by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                | dto            | api           | action | statusCode | status  | errorCode                        | message                                       |
      | settings/00006-currencyIso-api-create/SuccessTest.json  | CurrencyIsoDto | /currencyIso/ | Delete |        200 | SUCCESS |                                  |                                               |
      | settings/00006-currencyIso-api-create/DO_NOT_EXIST.json | CurrencyIsoDto | /currencyIso/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Currency with code=NOT_EXIST does not exists. |
