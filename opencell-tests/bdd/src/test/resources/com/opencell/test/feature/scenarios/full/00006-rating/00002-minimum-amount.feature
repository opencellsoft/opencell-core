@full
Feature: Rating - Minimum Amount

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
      | jsonFile                                                                                 | title                                   | dto                                  | api                                                     | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00006-rating/02-minimum-amount/create-oneshot-subscription.json           | Create OneShot - Subscription           | OneShotChargeTemplateDto             | /catalog/oneShotChargeTemplate/createOrUpdate           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00006-rating/02-minimum-amount/create-pricePlan-oneshot-subscription.json | Create priceplan OneShot - Subscription | PricePlanMatrixDto                   | /catalog/pricePlan/createOrUpdate                       | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00006-rating/02-minimum-amount/create-product-charge_1.json               | Create Product charge1                  | ProductChargeTemplateDto             | /catalogManagement/productChargeTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00006-rating/02-minimum-amount/create-product_1.json                      | Create Product1                         | ProductTemplateDto                   | /catalogManagement/productTemplate/createOrUpdate       | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00006-rating/02-minimum-amount/create-pricePlan-product_1.json            | Create priceplan product1               | PricePlanMatrixDto                   | /catalog/pricePlan/createOrUpdate                       | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00006-rating/02-minimum-amount/create-service_1.json                      | Create Service1                         | ServiceTemplateDto                   | /catalog/serviceTemplate/createOrUpdate                 | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00006-rating/02-minimum-amount/create-offer_1.json                        | Create Offer1                           | OfferTemplateDto                     | /catalog/offerTemplate/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00006-rating/02-minimum-amount/create-accountHierarchy.json               | Create AccountHierarchy                 |                                      | /account/accountHierarchy/createOrUpdate                | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00006-rating/02-minimum-amount/create-subscription.json                   | Create Subscription                     | SubscriptionDto                      | /billing/subscription/createOrUpdate                    | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00006-rating/02-minimum-amount/apply-oneshot.json                         | Apply one shot                          | ApplyOneShotChargeInstanceRequestDto | /billing/subscription/applyOneShotChargeInstance        | POST           |        200 | SUCCESS |           |         |        |          |
