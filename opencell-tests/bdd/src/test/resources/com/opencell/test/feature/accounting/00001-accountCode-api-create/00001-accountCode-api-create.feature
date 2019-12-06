@accounting
Feature: Create an accounting code by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: Create an accounting code by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The accounting code is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                       | dto               | api                                    | statusCode | status  | errorCode         | message                                                                                                             |
      | accounting/00001-accountCode-api-create/SuccessTest.json       | AccountingCodeDto | /billing/accountingCode/createOrUpdate |        200 | SUCCESS |                   |                                                                                                                     |
      | accounting/00001-accountCode-api-create/SuccessTest1.json      | AccountingCodeDto | /billing/accountingCode/createOrUpdate |        200 | SUCCESS |                   |                                                                                                                     |
      | accounting/00001-accountCode-api-create/MISSING_PARAMETER.json | AccountingCodeDto | /billing/accountingCode/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER | The following parameters are required or contain invalid values: chartOfAccountTypeEnum, chartOfAccountViewTypeEnum |
      | accounting/00001-accountCode-api-create/INVALID_PARAMETER.json | AccountingCodeDto | /billing/accountingCode/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER | Can not deserialize value of type org.meveo.model.billing.ChartOfAccountViewTypeEnum from String                    |
