@full
Feature: Subscription workflow - Apply Oneshot chargeInstance

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
      | jsonFile                                                                                                     | title                      | dto                                  | api                                              | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00004-subscription-workflow/03-apply-oneshot-chargeInstance/crearte-subscription.json         | Create subscription        | SubscriptionDto                      | /billing/subscription/createOrUpdate             | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/03-apply-oneshot-chargeInstance/activate-services.json            | Activate services          | ActivateServicesRequestDto           | /billing/subscription/activateServices           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/03-apply-oneshot-chargeInstance/apply-oneshot-chargeInstance.json | applyOneShotChargeInstance | ApplyOneShotChargeInstanceRequestDto | /billing/subscription/applyOneShotChargeInstance | POST           |        200 | SUCCESS |           |         |        |          |