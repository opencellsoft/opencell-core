@accounting
Feature: Delete an accounting code by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: Delete an accounting code by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                  | dto               | api                      | statusCode | status  | errorCode                        | message                                            |
      | accounting/00001-accountCode-api-create/SuccessTest.json  | AccountingCodeDto | /billing/accountingCode/ |        200 | SUCCESS |                                  |                                                    |
      | accounting/10002-accountCode-api-delete/DO_NOT_EXIST.json | AccountingCodeDto | /billing/accountingCode/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | AccountingCode with code=NOT_EXIST does not exists |
