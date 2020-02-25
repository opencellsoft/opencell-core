@full
Feature: Invoicing - Invoice Sequence from Parent Seller

  @admin @superadmin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is cleared
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                | title                         | dto                        | api                                      | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00001-invoicing-api/invoice_sequence_from_parent_seller/create_invoiceType_eng.json      | Create InvoiceType - ENG      | String                     | /invoiceType/createOrUpdate              | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoice_sequence_from_parent_seller/create_billingCycle.json         | Create BillingCycle           | BillingCycleDto            | /billingCycle/createOrUpdate             | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoice_sequence_from_parent_seller/create_seller_parent.json        | Create seller - parent        | SellerDto                  | /seller/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoice_sequence_from_parent_seller/create_seller_child.json         | Create seller - child         | SellerDto                  | /seller/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoice_sequence_from_parent_seller/create_accountHierarchy_eng.json | Create AccountHierarchy - ENG |                            | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoice_sequence_from_parent_seller/create_subscription.json         | Create subscription           | SubscriptionDto            | /billing/subscription/createOrUpdate     | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoice_sequence_from_parent_seller/activate_service.json            | Activate service              | ActivateServicesRequestDto | /billing/subscription/activateServices   | POST           |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoice_sequence_from_parent_seller/generate_draft_invoice.json      | Generate draft invoice        |                            | /invoice/generateDraftInvoice            | POST           |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/invoice_sequence_from_parent_seller/generate_invoice.json            | Generate invoice              |                            | /invoice/generateInvoice                 | POST           |        200 | SUCCESS |           |         |
