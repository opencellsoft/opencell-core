@full
Feature: InvoiceType - EL AO

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
      | jsonFile                                                                                         | title                           | dto                        | api                                                                    | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/create-service_1.json              | Create Service1                 | ServiceTemplateDto         | /catalog/serviceTemplate/createOrUpdate                                | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/create-offer_1.json                | Create Offer1                   | OfferTemplateDto           | /catalog/offerTemplate/createOrUpdate                                  | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/create-invoiceType-el-ao.json      | Create InvoiceType - EL AO      | InvoiceTypeDto             | /invoiceType/createOrUpdate                                            | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/create-billingCycle-el-ao.json     | Create BillingCycle - EL AO     | BillingCycleDto            | /billingCycle/createOrUpdate                                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/create-accountHierarchy-el-ao.json | Create AccountHierarchy - EL AO |                            | /account/accountHierarchy/createOrUpdate                               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/create-subscription-el-ao.json     | Create subscription - EL AO     | SubscriptionDto            | /billing/subscription/createOrUpdate                                   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/create-access-el-ao.json           | Create access - EL AO           | AccessDto                  | /account/access/createOrUpdate                                         | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/activate-services-el-ao.json       | Activate service - EL AO        | ActivateServicesRequestDto | /billing/subscription/activateServices                                 | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/charge-cdr-el-ao.json              | Charge CDR - EL AO              | String                     | /billing/mediation/chargeCdr                                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/generate-invoice-el-ao.json        | Generate invoice - EL AO        |                            | /invoice/generateInvoice                                               | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/create-ddrequestLotOp-el-ao.json   | Create DDRequestLotOp - EL AO   |                            | /                                                                      | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/ao-job-el-ao.json                  | AO Job - EL AO                  |                            | /job/execute                                                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/03-invoiceType-el-ao/list-account-operations-el-ao.json | List Account operations - EL AO |                            | /accountOperation/list?customerAccountCode=CA_RS_FULL_43_INV_CUST_ElAO | GET            |        200 | SUCCESS |           |         |        |          |
