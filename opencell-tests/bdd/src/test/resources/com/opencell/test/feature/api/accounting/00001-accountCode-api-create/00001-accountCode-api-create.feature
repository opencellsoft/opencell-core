@accounting
Feature: Create/Update an accounting code by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: <status> <action> an accounting code by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The accounting code is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                           | dto               | api                                    | action         | statusCode | status  | errorCode                        | message                                                                                                             |
      | api/accounting/00001-accountCode-api-create/SuccessTest.json       | AccountingCodeDto | /billing/accountingCode/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                                     |
      | api/accounting/00001-accountCode-api-create/SuccessTest.json       | AccountingCodeDto | /billing/accountingCode/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | AccountingCode with code=TEST already exists.                                                                       |
      | api/accounting/00001-accountCode-api-create/DO_NOT_EXIST.json      | AccountingCodeDto | /billing/accountingCode/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | AccountingCode with code=NOT_EXIST does not exists.                                                                 |
      | api/accounting/00001-accountCode-api-create/SuccessTest1.json      | AccountingCodeDto | /billing/accountingCode/               | Update         |        200 | SUCCESS |                                  |                                                                                                                     |
      | api/accounting/00001-accountCode-api-create/SuccessTest1.json      | AccountingCodeDto | /billing/accountingCode/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                                     |
      | api/accounting/00001-accountCode-api-create/MISSING_PARAMETER.json | AccountingCodeDto | /billing/accountingCode/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: chartOfAccountTypeEnum, chartOfAccountViewTypeEnum |
      | api/accounting/00001-accountCode-api-create/INVALID_PARAMETER.json | AccountingCodeDto | /billing/accountingCode/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.model.billing.ChartOfAccountViewTypeEnum` from String                   |
