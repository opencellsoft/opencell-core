@settings
Feature: Create/Update Tax by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <action> Tax by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The tax is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                      | dto    | api                 | action         | statusCode | status  | errorCode                        | message                                                                   |
      | settings/00010-tax-api-create/SuccessTest.json                | TaxDto | /tax/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                           |
      | settings/00010-tax-api-create/SuccessTest.json                | TaxDto | /tax/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Tax with code=TEST already exists.                                        |
      | settings/00010-tax-api-create/DO_NOT_EXIST.json               | TaxDto | /tax/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Tax with code=NOT_EXIST does not exists.                                  |
      | settings/00010-tax-api-create/SuccessTest1.json               | TaxDto | /tax/               | Update         |        200 | SUCCESS |                                  |                                                                           |
      | settings/00010-tax-api-create/SuccessTest1.json               | TaxDto | /tax/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                           |
      | settings/00010-tax-api-create/MISSING_PARAMETER.json          | TaxDto | /tax/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: percent. |
      | settings/00010-tax-api-create/INVALID_PARAMETER.json          | TaxDto | /tax/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.math.BigDecimal` from String       |
      | settings/00010-tax-api-create/INVALID_PARAMETER_Language.json | TaxDto | /tax/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Language 100 is not supported by the provider.                            |
      | settings/00010-tax-api-create/ENTITY_DOES_NOT_EXISTS.json     | TaxDto | /tax/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | AccountingCode with code=22 does not exists.                              |
