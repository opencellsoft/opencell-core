@full @ignore
Feature: Invoicing - Setup Data

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
      | jsonFile                                                                       | title                         | dto                        | api                                      | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00001-invoicing-api/setup-data/rated_transaction_job_clear.json | Rated Transaction Job - Clear |                            | /job/execute                             | POST           |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_invoiceType_eng.json      | Create InvoiceType - ENG      | InvoiceTypeDto             | /invoiceType/createOrUpdate              | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_billingCycle.json         | Create BillingCycle           | BillingCycleDto            | /billingCycle/createOrUpdate             | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_accountHierarchy_eng.json | Create AccountHierarchy - ENG |                            | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_service_1.json            | Create Service1               | ServiceTemplateDto         | /catalog/serviceTemplate/createOrUpdate  | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_offer_1.json              | Create Offer 1                | OfferTemplateDto           | /catalog/offerTemplate/createOrUpdate    | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_subscription_1.json       | Create subscription           | SubscriptionDto            | /billing/subscription/createOrUpdate     | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_access_1.json             | Create access                 | AccessDto                  | /account/access/createOrUpdate           | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/activate_service_1.json          | Activate service              | ActivateServicesRequestDto | /billing/subscription/activateServices   | POST           |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_accountHierarchy_fra.json | Create AccountHierarchy - FRA |                            | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_subscription_2.json       | Create subscription 2         | SubscriptionDto            | /billing/subscription/createOrUpdate     | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/create_access_2.json             | Create access 2               | AccessDto                  | /account/access/createOrUpdate           | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/setup-data/activate_service_2.json          | Activate service 2            | ActivateServicesRequestDto | /billing/subscription/activateServices   | POST           |        200 | SUCCESS |           |         |
