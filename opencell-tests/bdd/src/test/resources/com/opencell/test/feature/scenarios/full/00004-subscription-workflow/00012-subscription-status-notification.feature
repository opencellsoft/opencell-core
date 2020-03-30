@full
Feature: Subscription workflow - Subscription status notification

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
      | jsonFile                                                                                                  | title                 | dto                           | api                                                                        | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00004-subscription-workflow/12-subscription-status-notification/create-script.json         | Create script         | ScriptInstanceDto             | /scriptInstance/createOrUpdate                                             | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/12-subscription-status-notification/create-notifications.json  | Create notifications  | NotificationDto               | /notification/createOrUpdate                                               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/12-subscription-status-notification/create-subscription.json   | Create subscription   | SubscriptionDto               | /billing/subscription/createOrUpdate                                       | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/12-subscription-status-notification/instantiate-services.json  | Instanticate services | InstantiateServicesRequestDto | /billing/subscription/instantiateServices                                  | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/12-subscription-status-notification/activate-subscription.json | Activate subscription | String                        | /billing/subscription/activate                                             | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/12-subscription-status-notification/find-subscription.json     | Find subscription     |                               | /billing/subscription?subscriptionCode=RS_FULL_164_SUB_STATUS_NOTIFICATION | GET            |        200 | SUCCESS |           |         |        |          |
