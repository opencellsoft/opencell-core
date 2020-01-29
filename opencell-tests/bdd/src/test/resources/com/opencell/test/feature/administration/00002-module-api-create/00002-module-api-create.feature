@administration
Feature: Create/Update a module by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: <action>  a module by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The module is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                      | dto            | api                    | action         | statusCode | status  | errorCode                        | message                                                                                 |
      | administration/00002-module-api-create/SuccessTest.json       | MeveoModuleDto | /module/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                         |
      | administration/00002-module-api-create/SuccessTest.json       | MeveoModuleDto | /module/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Module with code=TEST already exists.                                                   |
      | administration/00002-module-api-create/DO_NOT_EXIST.json      | MeveoModuleDto | /module/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Module with code=NOT_EXIST does not exists.                                             |
      | administration/00002-module-api-create/SuccessTest1.json      | MeveoModuleDto | /module/               | Update         |        200 | SUCCESS |                                  |                                                                                         |
      | administration/00002-module-api-create/SuccessTest1.json      | MeveoModuleDto | /module/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                         |
      | administration/00002-module-api-create/MISSING_PARAMETER.json | MeveoModuleDto | /module/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values                         |
      | administration/00002-module-api-create/INVALID_PARAMETER.json | MeveoModuleDto | /module/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.model.module.ModuleLicenseEnum` from String |
