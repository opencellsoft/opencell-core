@administration @ignore
Feature: Delete script instance by API

  Background: The classic offer is already executed
              Create script instance by API is already executed


  @admin @superadmin
  Scenario Outline: Delete script instance by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                  | dto               | api              | statusCode | status  | errorCode                        | message                                                                      |
      | administration/00005-scriptInstance-api-create/Success.json               | ScriptInstanceDto | /scriptInstance/ |        200 | SUCCESS |                                  |                                                                              |
      | administration/10004-scriptInstance-api-delete/ENTITY_DOES_NOT_EXIST.json | ScriptInstanceDto | /scriptInstance/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | ScriptInstance with code=org.meveo.service.script.NOT_EXIST does not exists. |
