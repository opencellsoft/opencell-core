@full
Feature: Subscription workflow - Subscription age

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
      | jsonFile                                                                                         | title                        | dto                             | api                                             | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/create-recurring.json             | Create recurring             | RecurringChargeTemplateDto      | /catalog/recurringChargeTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/create-service.json               | Create Service               | ServiceTemplateDto              | /catalog/serviceTemplate/createOrUpdate         | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/create-offer.json                 | Create Offer                 | OfferTemplateDto                | /catalog/offerTemplate/createOrUpdate           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/create-pricePlan-recurring_1.json | Create priceplan recurring 1 | PricePlanMatrixDto              | /catalog/pricePlan/createOrUpdate               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/create-pricePlan-recurring_2.json | Create priceplan recurring 2 | PricePlanMatrixDto              | /catalog/pricePlan/createOrUpdate               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/create-pricePlan-recurring_3.json | Create priceplan recurring 3 | PricePlanMatrixDto              | /catalog/pricePlan/createOrUpdate               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/create-subscription_1.json        | Create subscription          | SubscriptionDto                 | /billing/subscription/createOrUpdate            | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/activate-services_1.json          | Activate services            | ActivateServicesRequestDto      | /billing/subscription/activateServices          | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/terminate-subscription_1.json     | Terminate subscription 1     | TerminateSubscriptionRequestDto | /billing/subscription/terminate                 | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/create-subscription_2.json        | Create subscription 2        | SubscriptionDto                 | /billing/subscription/createOrUpdate            | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/activate-services_2.json          | Activate services 2          | ActivateServicesRequestDto      | /billing/subscription/activateServices          | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/10-subscription-age/terminate-subscription_2.json     | Terminate subscription 2     | TerminateSubscriptionRequestDto | /billing/subscription/terminate                 | POST           |        200 | SUCCESS |           |         |        |          |
