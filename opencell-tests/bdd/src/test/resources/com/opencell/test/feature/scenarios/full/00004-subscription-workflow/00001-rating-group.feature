@full
Feature: Rating Group

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
      | jsonFile                                                                                                  | title                                       | dto                        | api                                     | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00004-subscription-workflow/01-rating-group/rated-transaction-job-clear.json               | Rated Transaction Job - Clear               |                            | /job/execute                            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-billingCycle.json                       | Create billing cycle                        | BillingCycleDto            | /billingCycle/createOrUpdate            | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-accountHierarchy.json                   | Create AccountHierarchy                     |                            | /account/accountHierarchy               | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-usage-charge.json                       | Create usage charge                         | UsageChargeTemplateDto     | /catalog/usageChargeTemplate            | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-pricePlan.json                          | Create priceplan                            | PricePlanMatrixDto         | /catalog/pricePlan/createOrUpdate       | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-service.json                            | Create service                              | ServiceTemplateDto         | /catalog/serviceTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-offer.json                              | Create offer                                | OfferTemplateDto           | /catalog/offerTemplate/createOrUpdate   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-subscription_1-with-ratingGroup.json    | Create Subscription - with ratingGroup      | SubscriptionDto            | /billing/subscription/createOrUpdate    | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/activate-subscription_1.json                   | Activate subscription 1                     | String                     | /billing/subscription/activate          | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/activate-services_1.json                       | Activate services                           | ActivateServicesRequestDto | /billing/subscription/activateServices  | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-access_1.json                           | Create access                               | AccessDto                  | /account/access/createOrUpdate          | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-subscription_2-without-ratingGroup.json | Create Subscription 2 - without ratingGroup | SubscriptionDto            | /billing/subscription/createOrUpdate    | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/activate-subscription_2.json                   | Activate subscription 2                     | String                     | /billing/subscription/activate          | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/activate-services_2.json                       | Activate services 2                         | ActivateServicesRequestDto | /billing/subscription/activateServices  | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/create-access_2.json                           | Create access 2                             | AccessDto                  | /account/access/createOrUpdate          | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/register-cdr.json                              | Register CDR                                | CdrListDto                 | /billing/mediation/registerCdrList      | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/run-ur.json                                    | Run UR                                      |                            | /job/execute                            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/run-rt.json                                    | Run RT                                      |                            | /job/execute                            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/01-rating-group/generate-invoice.json                          | GenerateInvoice                             |                            | /invoice/generateInvoice                | POST           |        200 | SUCCESS |           |         |        |          |
