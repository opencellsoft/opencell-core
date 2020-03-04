@full
Feature: Invoicing - invoicing

  @admin @superadmin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                             | title                                | dto            | api                                                                                                                                                                                                                                                                                                                                                                              | action | statusCode | status  | errorCode | message |
      | scenarios/full/00001-invoicing-api/invoicing/charge_cdr_in_range.json                | Charge CDR - in range                | String         | /billing/mediation/chargeCdr                                                                                                                                                                                                                                                                                                                                                     | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/charge_cdr_out_of_range_1.json          | Charge CDR - out of range 1          | String         | /billing/mediation/chargeCdr                                                                                                                                                                                                                                                                                                                                                     | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/charge_cdr_out_of_range_2.json          | Charge CDR - out of range 2          | String         | /billing/mediation/chargeCdr                                                                                                                                                                                                                                                                                                                                                     | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/rated_transaction_job.json              | Rated Transaction Job                |                | /job/execute                                                                                                                                                                                                                                                                                                                                                                     | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/generate_draft_invoice.json             | Generate draft invoice               |                | /invoice/generateDraftInvoice                                                                                                                                                                                                                                                                                                                                                    | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/generate_invoice.json                   | Generate invoice                     |                | /invoice/generateInvoice                                                                                                                                                                                                                                                                                                                                                         | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/charge_cdr_again.json                   | Charge CDR - again                   | String         | /billing/mediation/chargeCdr                                                                                                                                                                                                                                                                                                                                                     | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/update_invoiceType.json                 | Update InvoiceType                   | InvoiceTypeDto | /invoiceType                                                                                                                                                                                                                                                                                                                                                                     | Update |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/generate_invoice_again.json             | Generate invoice again               |                | /invoice/generateInvoice                                                                                                                                                                                                                                                                                                                                                         | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/list_invoice.json                       | List Invoices                        |                | /invoice/list                                                                                                                                                                                                                                                                                                                                                                    | GET    |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/list_invoice_with_filter.json           | List Invoices with filter            |                | /invoice/list                                                                                                                                                                                                                                                                                                                                                                    | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/list_invoice_rest_get.json              | List Invoices - Rest get             |                | /invoice/list?sortBy=id&sortOrder=ASCENDING                                                                                                                                                                                                                                                                                                                                      | GET    |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/list_invoice_with_filter_rest_get.json  | List Invoices with filter - Rest get |                | /invoice/list?query=billingAccount.customerAccount.customer.code:RS_FULL_286_INV_CUST1\|fromRange invoiceDate:2050-09-15\|toRange invoiceDate:2050-11-15\|fromRange dueDate:2050-09-15\|toRange dueDate:2050-11-15\|invoiceType.code:RS_FULL_286_IT_PDFXML\|fromRange amountWithoutTax:7.99\|toRange amountWithoutTax:10.5&fields=transactions,pdf&sortBy=id&sortOrder=ASCENDING | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/list_invoice_rest_post.json             | List Invoice - Rest post             |                | /invoice/list                                                                                                                                                                                                                                                                                                                                                                    | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/list_invoice_with_filter_rest_post.json | List Invoice with filter - Rest post |                | /invoice/list                                                                                                                                                                                                                                                                                                                                                                    | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/charge_cdr_in_range_2_en.json           | Charge CDR - in range 2 - EN         | String         | /billing/mediation/chargeCdr                                                                                                                                                                                                                                                                                                                                                     | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/generate_invoice_2_en.json              | Generate invoice 2 - EN              |                | /invoice/generateInvoice                                                                                                                                                                                                                                                                                                                                                         | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/charge_cdr_in_range_2_fr.json           | Charge CDR - in range 2 - FR         | String         | /billing/mediation/chargeCdr                                                                                                                                                                                                                                                                                                                                                     | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoicing/generate_invoice_2_fr.json              | Generate invoice 2 - FR              |                | /invoice/generateInvoice                                                                                                                                                                                                                                                                                                                                                         | POST   |        200 | SUCCESS |           |         |
