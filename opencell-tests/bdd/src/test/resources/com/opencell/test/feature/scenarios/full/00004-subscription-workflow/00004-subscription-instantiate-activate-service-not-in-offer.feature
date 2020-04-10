@full
Feature: Subscription workflow - Subscription instantiate/activate service NOT in Offer and modify offer in subscription

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
      | jsonFile                                                                                                                                                 | title                                           | dto                           | api                                       | action         | statusCode | status  | errorCode              | message                                                                    | entity | expected |
      | scenarios/full/00004-subscription-workflow/04-subscription-instantiate-activate-service-not-in-offer/create-subscription.json                            | Create subscription                             | SubscriptionDto               | /billing/subscription/createOrUpdate      | CreateOrUpdate |        200 | SUCCESS |                        |                                                                            |        |          |
      | scenarios/full/00004-subscription-workflow/04-subscription-instantiate-activate-service-not-in-offer/create-service-with-min-req-vals.json               | Create service with min req Vals                | ServiceTemplateDto            | /catalog/serviceTemplate                  | Create         |        200 | SUCCESS |                        |                                                                            |        |          |
      | scenarios/full/00004-subscription-workflow/04-subscription-instantiate-activate-service-not-in-offer/activate-services-with-service-not-in-offer.json    | Activate services with service not in Offer     | ActivateServicesRequestDto    | /billing/subscription/activateServices    | POST           |        500 | FAIL    | BUSINESS_API_EXCEPTION | RS_FULL_164_SERVICE is not associated with Offer                           |        |          |
      | scenarios/full/00004-subscription-workflow/04-subscription-instantiate-activate-service-not-in-offer/instantiate-services-with-service-not-in-offer.json | Instanticate services with service not in Offer | InstantiateServicesRequestDto | /billing/subscription/instantiateServices | POST           |        500 | FAIL    | BUSINESS_API_EXCEPTION | No offerServiceTemplate corresponds to RS_FULL_164_SERVICE                 |        |          |
      | scenarios/full/00004-subscription-workflow/04-subscription-instantiate-activate-service-not-in-offer/update-subscription-with-another-offer-ok.json      | Update subscription with another offer - ok     | SubscriptionDto               | /billing/subscription                     | Update         |        200 | SUCCESS |                        |                                                                            |        |          |
      | scenarios/full/00004-subscription-workflow/04-subscription-instantiate-activate-service-not-in-offer/instantiate-services.json                           | Instanticate services                           | InstantiateServicesRequestDto | /billing/subscription/instantiateServices | POST           |        200 | SUCCESS |                        |                                                                            |        |          |
      | scenarios/full/00004-subscription-workflow/04-subscription-instantiate-activate-service-not-in-offer/update-subscription-with-another-offer-fail.json    | Update subscription with another offer - fail   | SubscriptionDto               | /billing/subscription                     | Update         |        400 | FAIL    | INVALID_PARAMETER      | Cannot change the offer of subscription once the services are instantiated |        |          |
