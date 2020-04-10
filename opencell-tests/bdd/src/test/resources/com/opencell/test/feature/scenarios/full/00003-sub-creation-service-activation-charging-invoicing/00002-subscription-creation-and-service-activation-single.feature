@full @ignore
Feature: Sub creation, Service Activation, Charging and invoicing - Subscription creation and Service activation - service single activation

  @admin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    And The entity "<entity>" matches "<expected>"

    Examples: 
      | jsonFile                                                                                                                                                                                     | title                                                                                   | dto                        | api                                                            | action         | statusCode | status  | errorCode | message                                                          | entity       | expected                                                                                                                                                |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-discountPlan.json                                      | Create discountPlan                                                                     | DiscountPlanDto            | /catalog/discountPlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-discountPlan-item.json                                 | Create discount plan item                                                               | DiscountPlanItemDto        | /catalog/discountPlanItem/createOrUpdate                       | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/rated-transaction-job-create-transaction-for-other-tests.json | Rated Transaction Job - create transactios for other tests, so this test wont take long | JobInstanceInfoDto         | /job/execute                                                   | POST           |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-invoiceType-indexed.json                               | Create InvoiceType indexed                                                              | InvoiceTypeDto             | /invoiceType/createOrUpdate                                    | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-billingCycle-indexed.json                              | Create BillingCycle indexed                                                             | BillingCycleDto            | /billingCycle/createOrUpdate                                   | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-billing-account-invoice.json                           | Create Billing Account Invoice                                                          | BillingAccountDto          | /account/billingAccount/createOrUpdate                         | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-user-account-invoice.json                              | Create User Account Invoice                                                             | UserAccountDto             | /account/userAccount/createOrUpdate                            | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-counter-invoice-usage.json                             | Create Counter Invoice Usage                                                            | CounterTemplateDto         | /catalog/counterTemplate/createOrUpdate                        | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-counter-usage_4.json                                   | Create Counter Usage4                                                                   | CounterTemplateDto         | /catalog/counterTemplate/createOrUpdate                        | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-service-invoice_3.json                                 | Create Service Invoice 3                                                                | ServiceTemplateDto         | /catalog/serviceTemplate/createOrUpdate                        | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-service-invoice_4.json                                 | Create Service Invoice 4                                                                | ServiceTemplateDto         | /catalog/serviceTemplate/createOrUpdate                        | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-offer-invocie_2.json                                   | Create Offer Invoice 2                                                                  | OfferTemplateDto           | /catalog/offerTemplate/createOrUpdate                          | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-subscription_1.json                                    | Create subscription 1                                                                   | SubscriptionDto            | /billing/subscription/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/activate-service_1-on-subscription_1.json                     | Activate service 1 on subscription 1                                                    | ActivateServicesRequestDto | /billing/subscription/activateServices                         | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-accessPoint_1.json                                     | Create AccessPoint                                                                      | AccessDto                  | /account/access/createOrUpdate                                 | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/find-subscription_1.json                                      | Find subscription 1                                                                     |                            | /billing/subscription?subscriptionCode=RS_FULL_200_SUB_INVOICE | GET            |        200 | SUCCESS |           |                                                                  | subscription | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/find-subscription_1.json |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-subscription_2.json                                    | Create subscription 2                                                                   | SubscriptionDto            | /billing/subscription/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/activate-service_3-on-subscription_2.json                     | Activate services 3 on subscription 2                                                   | ActivateServicesRequestDto | /billing/subscription/activateServices                         | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/create-accessPoint_2.json                                     | Create AccessPoint 2                                                                    | AccessDto                  | /account/access/createOrUpdate                                 | CreateOrUpdate |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/generate-invoice.json                                         | Generate invoice                                                                        | GenerateInvoiceRequestDto  | /invoice/generateInvoice                                       | POST           |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/get-invoice-by-number-and-type.json                           | Get Invoice by number and type                                                          |                            | /invoice?id=16&includeTransactions=true                        | GET            |        200 | SUCCESS |           |                                                                  |              |                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/02-subscription-creation-and-service-activation-single/activate-service_1-on-subscription_2-again-fail.json          | Activate service 1 on subscription 1 second time - fail                                 | ActivateServicesRequestDto | /billing/subscription/activateServices                         | CreateOrUpdate |        500 | FAIL    |           | ServiceInstance with code=RS_BASE_SERVICE1 is already activated. |              |                                                                                                                                                         |
