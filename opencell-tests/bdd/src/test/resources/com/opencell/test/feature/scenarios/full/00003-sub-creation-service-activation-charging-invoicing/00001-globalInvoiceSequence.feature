@full
Feature: Global Invoice Sequence

  @admin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    And The entity "<entity>" matches "<expected>"

    Examples: 
      | jsonFile                                                                                                                                     | title                                   | dto                    | api                                                         | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/01-GlobalInvoiceSequence/create-invoice-for-self-sequence.json       | create InvoiceType for self sequence    | InvoiceTypeDto         | /invoiceType/createOrUpdate                                 | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/01-GlobalInvoiceSequence/create-customerGlobal.json                  | Create  CustomerGlobal                  | CRMAccountHierarchyDto | /account/accountHierarchy/createOrUpdateCRMAccountHierarchy | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/01-GlobalInvoiceSequence/create-invoice-for-self-sequence_5001.json  | create Invoice for self sequence_5001   | InvoiceDto             | /invoice                                                    | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/01-GlobalInvoiceSequence/create-invoice-for-self-sequence_5002.json  | create Invoice for self sequence_5002   | InvoiceDto             | /invoice                                                    | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/01-GlobalInvoiceSequence/update-invoiceType-to-global-sequence.json  | update InvoiceType to global sequence   | InvoiceTypeDto         | /invoiceType                                                | Update         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/01-GlobalInvoiceSequence/set-global-to-sequence-to-100.json          | Set global to sequence to 100           | ProviderDto            | /provider                                                   | Update         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/01-GlobalInvoiceSequence/create-invoice-for-global-sequence_101.json | create Invoice  for global sequence_101 | InvoiceDto             | /invoice                                                    | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/01-GlobalInvoiceSequence/create-invoice-for-global-sequence_102.json | create Invoice  for global sequence_102 | InvoiceDto             | /invoice                                                    | Create         |        200 | SUCCESS |           |         |        |          |
