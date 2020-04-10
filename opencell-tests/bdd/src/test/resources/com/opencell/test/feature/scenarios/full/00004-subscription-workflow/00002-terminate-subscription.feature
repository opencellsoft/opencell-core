@full @ignore
Feature: Subscription workflow - Terminate Subscription

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
      | jsonFile                                                                                                                                                                | title                                                                                               | dto                             | api                                                                       | action                                 | statusCode | status  | errorCode | message | entity | expected |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/create-subscription.json                                                                           | Create subscription                                                                                 | SubscriptionDto                 | /billing/subscription/createOrUpdate                                      | CreateOrUpdate                         |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/activate-services.json                                                                             | Activate services                                                                                   | ActivateServicesRequestDto      | /billing/subscription/activateServices                                    | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_1.json                                                                           | Find subscription                                                                                   |                                 | /billing/subscription?subscriptionCode=RS_FULL_164_SUB_SUB_WFL            | GET                                    |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/terminate-subscription.json                                                                        | Terminate Subscription 1                                                                            | TerminateSubscriptionRequestDto | /billing/subscription/terminate                                           | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_2.json                                                                           | Find subscription 2                                                                                 |                                 | /billing/subscription?subscriptionCode=RS_FULL_164_SUB_SUB_WFL            | GET                                    |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/create-subscription_3-future-termination.json                                                      | Create subscription - future termination                                                            |                                 | /billing/subscription                                                     | Create                                 |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/activate-service_1-future-termination.json                                                         | Activate services - future termination                                                              |                                 | ActivateServicesRequestDto                                                | /billing/subscription/activateServices | POST       |     200 | SUCCESS   |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_1-future-termination-1.json                                                      | Find subscription - future termination                                                              |                                 | /billing/subscription?subscriptionCode=RS_FULL_164_SUB_FUTURE_TERMINATION | GET                                    |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/terminate-subscription_1-future-termination.json                                                   | Terminate Subscription - future termination                                                         |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_1-future-termination-2.json                                                      | Find subscription - future termination 2                                                            |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/create-subscription_1-in-between-days-termination.json                                             | Create subscription - in-between-days termination                                                   |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/activate-service_1-in-between-days-termination.json                                                | Activate services - in-between-days termination                                                     |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_1-in-between-days-termination-1.json                                             | Find subscription - in-between-days termination                                                     |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/terminate-subscription_1-in-between-days-termination-before-date-KO.json                           | Terminate Subscription - in-between-days termination - before Subscription's subscription date -KO  |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/terminate-subscription_1-in-between-days-termination-before-date-OK.json                           | Terminate Subscription - in-between-days termination - on Subscription's subscription date - OK     |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_1-in-between-days-termination-2.json                                             | Find subscription - in-between-days termination 2                                                   |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/create-subscription_2-in-between-days-termination.json                                             | Create subscription - in-between-days 2 termination                                                 |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/activate-service_2-in-between-days-termination.json                                                | Activate services - in-between-days 2 termination                                                   |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_1-in-between-days-termination-2.json                                             | Find subscription - in-between-days 2 termination                                                   |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/terminate-subscription_2-in-between-days-termination-in-between-services-subscription-date-OK.json | Terminate Subscription - in-between-days 2 termination - inbetween service's subscription date - OK |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_2-in-between-days-termination-2.json                                             | Find subscription - in-between-days 2 termination 2                                                 |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/create-subscription_3-future-termination.json                                                      | Create subscription - future termination 3                                                          |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_3-future-termination.json                                                        | Find subscription - future termination 3                                                            |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/create-subscription_4-future-termination.json                                                      | Create subscription - future termination 4                                                          |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/activate-service_4-future-termination.json                                                         | Activate services - future termination 4                                                            |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/update-subscription_4-future-termination.json                                                      | Update subscription - future termination 4                                                          |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/find-subscription_4-future-termination.json                                                        | Find subscription - future termination 4                                                            |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
      | scenarios/full/00004-subscription-workflow/02-terminate-subscription/                                                                                                   |                                                                                                     |                                 | /                                                                         | POST                                   |        200 | SUCCESS |           |         |        |          |  |
