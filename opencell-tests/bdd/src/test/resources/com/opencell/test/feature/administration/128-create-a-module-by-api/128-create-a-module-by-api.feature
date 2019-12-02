Feature: Create a module by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: Create an accounting code by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The module is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                         | dto            | api                    | statusCode | status  | errorCode         | message                                                                                |
      | administration/128-create-a-module-by-api/SuccessTest.json       | MeveoModuleDto | /module/createOrUpdate |        200 | SUCCESS |                   |                                                                                        |
      | administration/128-create-a-module-by-api/SuccessTest1.json      | MeveoModuleDto | /module/createOrUpdate |        200 | SUCCESS |                   |                                                                                        |
      | administration/128-create-a-module-by-api/MISSING_PARAMETER.json | MeveoModuleDto | /module/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER | The following parameters are required or contain invalid values                        |
      | administration/128-create-a-module-by-api/INVALID_PARAMETER.json | MeveoModuleDto | /module/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER | Can not deserialize value of type org.meveo.model.module.ModuleLicenseEnum from String |
