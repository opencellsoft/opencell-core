@billing
Feature: Create/Update invoice sequence by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> invoice sequence by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoice sequence is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto                | api                             | action         | statusCode | status  | errorCode                        | message                                                                |
      | api/billing/00005-invoiceSequence-api-create/SuccessTest.json       | InvoiceSequenceDto | /invoiceSequence/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/billing/00005-invoiceSequence-api-create/SuccessTest.json       | InvoiceSequenceDto | /invoiceSequence/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | InvoiceSequence with code=TEST already exists.                         |
      | api/billing/00005-invoiceSequence-api-create/DO_NOT_EXIST.json      | InvoiceSequenceDto | /invoiceSequence/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceSequence with code=NOT_EXIST does not exists.                   |
      | api/billing/00005-invoiceSequence-api-create/SuccessTest1.json      | InvoiceSequenceDto | /invoiceSequence/               | Update         |        200 | SUCCESS |                                  |                                                                        |
      | api/billing/00005-invoiceSequence-api-create/SuccessTest1.json      | InvoiceSequenceDto | /invoiceSequence/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/billing/00005-invoiceSequence-api-create/MISSING_PARAMETER.json | InvoiceSequenceDto | /invoiceSequence/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | api/billing/00005-invoiceSequence-api-create/INVALID_PARAMETER.json | InvoiceSequenceDto | /invoiceSequence/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.lang.Integer` from String       |
