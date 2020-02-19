@scenarios @ignore
Feature: Delete offer template created by scenario 4928 by API

  Background: Scenario 4928 is executed

  @admin @superadmin
  Scenario Outline: <status> <action> offer template by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | OF_Name                | jsonFile                                            | dto         | api                     | action | statusCode | status  | errorCode | message |
      | OF_SE_NO_OVERRIDE      | scenarios/ticket-4928-2/OF_SE_NO_OVERRIDE.json      | BomOfferDto | /catalog/offerTemplate/ | Delete |        200 | SUCCESS |           |         |
      | OF_SE_OVERRIDE         | scenarios/ticket-4928-2/OF_SE_OVERRIDE.json         | BomOfferDto | /catalog/offerTemplate/ | Delete |        200 | SUCCESS |           |         |
      | OF_BSM_NO_OVERRIDE     | scenarios/ticket-4928-2/OF_BSM_NO_OVERRIDE.json     | BomOfferDto | /catalog/offerTemplate/ | Delete |        200 | SUCCESS |           |         |
      | OF_BSM_SINGLE_OVERRIDE | scenarios/ticket-4928-2/OF_BSM_SINGLE_OVERRIDE.json | BomOfferDto | /catalog/offerTemplate/ | Delete |        200 | SUCCESS |           |         |
      | OF_BSM_MULTI_OVERRIDE  | scenarios/ticket-4928-2/OF_BSM_MULTI_OVERRIDE.json  | BomOfferDto | /catalog/offerTemplate/ | Delete |        200 | SUCCESS |           |         |
      | OF_BSM_MULTI_OVERRIDE  | scenarios/ticket-4928-2/OF_BSM_MULTI_OVERRIDE.json  | BomOfferDto | /catalog/offerTemplate/ | Delete |        200 | SUCCESS |           |         |
