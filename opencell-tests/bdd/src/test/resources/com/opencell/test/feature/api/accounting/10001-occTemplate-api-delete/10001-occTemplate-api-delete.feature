@accounting
Feature: Delete OCC Template code by API

  Background: The classic offer is executed
    Create OCC Template is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> OCC Template by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                      | dto            | api           | action | statusCode | status  | errorCode                        | message                                         |
      | api/accounting/00002-occTemplate-api-create/SuccessTest.json  | OccTemplateDto | /occTemplate/ | Delete |        200 | SUCCESS |                                  |                                                 |
      | api/accounting/00002-occTemplate-api-create/DO_NOT_EXIST.json | OccTemplateDto | /occTemplate/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OCCTemplate with code=NOT_EXIST does not exists |
