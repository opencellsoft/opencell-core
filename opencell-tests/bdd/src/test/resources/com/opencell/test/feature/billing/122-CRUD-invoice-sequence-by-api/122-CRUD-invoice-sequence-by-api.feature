@All
Feature: CRUD invoice sequence by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create invoice sequence by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The invoice sequence is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                        | dto                | api                             | statusCode | status  | errorCode         | message                                                                |
      | billing/122-CRUD-invoice-sequence-by-api/SuccessTest.json       | InvoiceSequenceDto | /invoiceSequence/createOrUpdate |        200 | SUCCESS |                   |                                                                        |
      | billing/122-CRUD-invoice-sequence-by-api/SuccessTest1.json      | InvoiceSequenceDto | /invoiceSequence/createOrUpdate |        200 | SUCCESS |                   |                                                                        |
      | billing/122-CRUD-invoice-sequence-by-api/MISSING_PARAMETER.json | InvoiceSequenceDto | /invoiceSequence/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER | The following parameters are required or contain invalid values: code. |
      | billing/122-CRUD-invoice-sequence-by-api/INVALID_PARAMETER.json | InvoiceSequenceDto | /invoiceSequence/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER | Can not deserialize value of type java.lang.Integer from String        |

  #@admin @superadmin
  #Scenario Outline: Delete invoice sequence by API
    #Given The entity has the following information "<jsonFile>" as "<dto>"
    #When I call the delete "<api>"
    #Then The entity is deleted
    #And Validate that the statusCode is "<statusCode>"
    #And The status is "<status>"
    #And The message  is "<message>"
    #And The errorCode  is "<errorCode>"
#
    #Examples: 
      #| jsonFile                                                            | dto                | api               | statusCode | status  | errorCode                        | message                                              |
      #| billing/122-CRUD-invoice-sequence-by-api/SuccessTest.json           | InvoiceSequenceDto | /invoiceSequence/ |        200 | SUCCESS |                                  |                                                      |
      #| billing/122-CRUD-invoice-sequence-by-api/ENTITY_DOES_NOT_EXIST.json | InvoiceSequenceDto | /invoiceSequence/ |        400 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceSequence with code=NOT_EXIST does not exists. |
