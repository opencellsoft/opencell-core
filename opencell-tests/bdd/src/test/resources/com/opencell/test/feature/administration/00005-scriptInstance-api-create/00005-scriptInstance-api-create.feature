@administration
Feature: Create/Update script instance by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> script instance by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The script instance is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                   | dto               | api                            | action         | statusCode | status  | errorCode                       | message                                                                                     |
      | administration/00005-scriptInstance-api-create/Success.json                | ScriptInstanceDto | /scriptInstance/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                 |                                                                                             |
      | administration/00005-scriptInstance-api-create/Success.json                | ScriptInstanceDto | /scriptInstance/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | ScriptInstance with code=org.meveo.service.script.Test3604Script already exists.            |
      | administration/00005-scriptInstance-api-create/Success1.json               | ScriptInstanceDto | /scriptInstance/               | Update         |        200 | SUCCESS |                                 |                                                                                             |
      | administration/00005-scriptInstance-api-create/Success1.json               | ScriptInstanceDto | /scriptInstance/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                 |                                                                                             |
      | administration/00005-scriptInstance-api-create/MISSING_PARAMETER.json      | ScriptInstanceDto | /scriptInstance/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values: script.                    |
      | administration/00005-scriptInstance-api-create/INVALID_PARAMETER.json      | ScriptInstanceDto | /scriptInstance/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER               | Cannot deserialize value of type `org.meveo.model.scripts.ScriptSourceTypeEnum` from String |
      | administration/00005-scriptInstance-api-create/BUSINESS_API_EXCEPTION.json | ScriptInstanceDto | /scriptInstance/createOrUpdate | CreateOrUpdate |        500 | FAIL    | BUSINESS_API_EXCEPTION          | The code and the canonical script class name must be identical                              |
