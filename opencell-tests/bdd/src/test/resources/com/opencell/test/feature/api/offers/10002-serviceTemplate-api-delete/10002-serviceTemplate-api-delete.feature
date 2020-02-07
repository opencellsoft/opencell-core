@offers
Feature: Delete service template by API

  Background: The classic offer is already executed
              Create/Update service template by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> service template by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                      | dto                | api                       | action | statusCode | status  | errorCode                        | message                                              |
      | api/offers/00004-serviceTemplate-api-create/SuccessTest.json  | ServiceTemplateDto | /catalog/serviceTemplate/ | Delete |        200 | SUCCESS |                                  |                                                      |
      | api/offers/00004-serviceTemplate-api-create/DO_NOT_EXIST.json | ServiceTemplateDto | /catalog/serviceTemplate/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | ServiceTemplate with code=NOT_EXIST does not exists. |
