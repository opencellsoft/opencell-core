@full
Feature: Subscribe to disable offer

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
      | jsonFile                                                                                          | title               | dto             | api                                           | action         | statusCode | status  | errorCode | message                            | entity | expected |
      | scenarios/full/00004-subscription-workflow/07-subscribe-to-disable-offer/disable-offer_3.json     | Disable Offer3      |                 | /catalog/offerTemplate/RS_BASE_OFFER3/disable | POST           |        200 | SUCCESS |           |                                    |        |          |
      | scenarios/full/00004-subscription-workflow/07-subscribe-to-disable-offer/create-subscription.json | Create subscription | SubscriptionDto | /billing/subscription/createOrUpdate          | CreateOrUpdate |        500 | FAIL    |           | Cannot subscribe to disabled offer |        |          |
      | scenarios/full/00004-subscription-workflow/07-subscribe-to-disable-offer/enable_offer_3.json      | Enable Offer3       |                 | /catalog/offerTemplate/RS_BASE_OFFER3/enable  | POST           |        200 | SUCCESS |           |                                    |        |          |
