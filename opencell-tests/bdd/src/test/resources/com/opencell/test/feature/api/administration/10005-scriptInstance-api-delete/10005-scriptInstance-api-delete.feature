@administration
Feature: Delete script instance by API

  Background: The classic offer is already executed
              Create script instance by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> script instance by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                             | dto               | api              | action | statusCode | status  | errorCode                        | message                                                                      |
      | api/administration/00005-scriptInstance-api-create/Success.json      | ScriptInstanceDto | /scriptInstance/ | Delete |        200 | SUCCESS |                                  |                                                                              |
      | api/administration/00005-scriptInstance-api-create/DO_NOT_EXIST.json | ScriptInstanceDto | /scriptInstance/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | ScriptInstance with code=NOT_EXIST does not exists. |
