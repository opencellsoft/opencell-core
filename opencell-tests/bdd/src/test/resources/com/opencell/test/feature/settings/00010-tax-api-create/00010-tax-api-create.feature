@settings
@administration @ignore
Feature: Create Tax by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create Tax by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The tax is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                      | dto    | api                 | statusCode | status  | errorCode                        | message                                                                   |
      | settings/00010-tax-api-create/SuccessTest.json                | TaxDto | /tax/createOrUpdate |        200 | SUCCESS |                                  |                                                                           |
      | settings/00010-tax-api-create/SuccessTest.json                | TaxDto | /tax/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Tax with code=TEST already exists.                                        |
      | settings/00010-tax-api-create/SuccessTest1.json               | TaxDto | /tax/createOrUpdate |        200 | SUCCESS |                                  |                                                                           |
      | settings/00010-tax-api-create/MISSING_PARAMETER.json          | TaxDto | /tax/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: percent. |
      | settings/00010-tax-api-create/INVALID_PARAMETER.json          | TaxDto | /tax/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.math.BigDecimal` from String       |
      | settings/00010-tax-api-create/INVALID_PARAMETER_Language.json | TaxDto | /tax/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Language 100 is not supported by the provider.                            |
      | settings/00010-tax-api-create/ENTITY_DOES_NOT_EXISTS.json     | TaxDto | /tax/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | AccountingCode with code=22 does not exists.                              |
