@accounting
Feature: Delete OCC Template code by API

  Background: The classic offer is executed
    Create OCC Template is already executed


  @admin @superadmin
  Scenario Outline: Delete OCC Template by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                  | dto            | api           | statusCode | status  | errorCode                        | message                                         |
      | accounting/00002-occTemplate-api-create/SuccessTest.json  | OccTemplateDto | /occTemplate/ |        200 | SUCCESS |                                  |                                                 |
      | accounting/10001-occTemplate-api-delete/DO_NOT_EXIST.json | OccTemplateDto | /occTemplate/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OCCTemplate with code=NOT_EXIST does not exists |
