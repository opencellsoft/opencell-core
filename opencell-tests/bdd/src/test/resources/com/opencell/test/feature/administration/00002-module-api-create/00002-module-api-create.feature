@administration
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
      | jsonFile                                                      | dto            | api                    | statusCode | status  | errorCode         | message                                                                                 |
      | administration/00002-module-api-create/SuccessTest.json       | MeveoModuleDto | /module/createOrUpdate |        200 | SUCCESS |                   |                                                                                         |
      | administration/00002-module-api-create/SuccessTest1.json      | MeveoModuleDto | /module/createOrUpdate |        200 | SUCCESS |                   |                                                                                         |
      | administration/00002-module-api-create/MISSING_PARAMETER.json | MeveoModuleDto | /module/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER | The following parameters are required or contain invalid values                         |
      | administration/00002-module-api-create/INVALID_PARAMETER.json | MeveoModuleDto | /module/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER | Cannot deserialize value of type `org.meveo.model.module.ModuleLicenseEnum` from String |
