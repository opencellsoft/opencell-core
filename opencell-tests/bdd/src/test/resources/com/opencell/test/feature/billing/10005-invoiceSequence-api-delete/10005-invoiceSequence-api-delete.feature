@billing
Feature: Delete invoice sequence by API

  Background: The classic offer is already executed
    Create invoice sequence by API

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
      #| billing/00009-invoiceSequence-api-CRUD/SuccessTest.json           | InvoiceSequenceDto | /invoiceSequence/ |        200 | SUCCESS |                                  |                                                      |
      #| billing/00009-invoiceSequence-api-CRUD/ENTITY_DOES_NOT_EXIST.json | InvoiceSequenceDto | /invoiceSequence/ |        400 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceSequence with code=NOT_EXIST does not exists. |
