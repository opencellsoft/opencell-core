@full @test
Feature: Service workflow - service multi activation

  @admin @superadmin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                         | title                            | dto                           | api                                       | action         | statusCode | status  | errorCode                       | message                                                                          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/create_subscription.json         | Create subscription              | SubscriptionDto               | /billing/subscription/createOrUpdate      | CreateOrUpdate |        200 | SUCCESS |                                 |                                                                                  |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/instantiate_service_1.json       | Instantiate Service 1            | InstantiateServicesRequestDto | /billing/subscription/instantiateServices | POST           |        200 | SUCCESS |                                 |                                                                                  |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/instantiate_service_1_again.json | Instantiate Service again - fail | InstantiateServicesRequestDto | /billing/subscription/instantiateServices | POST           |        500 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | ServiceInstance with code=RS_BASE_SERVICE1 is already instanciated or activated. |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>" with identifier "<identifier>"
    Then The entity "<entity>" matches
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                 | title               | api                                     | action | statusCode | status  | errorCode | message | identifier | entity       |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/find_subscription_1.json | Find subscription I | /billing/subscription?subscriptionCode= | Find   |        200 | SUCCESS |           |         | code       | subscription |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The subscription is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                         | title                                                            | dto | api                  | action         | statusCode | status  | errorCode | message | expected |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Activate services 1 and 2                                        |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Find subscription A1                                             |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Find service instance                                            |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Terminate Service1                                               |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Instantiate Service 1 again                                      |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Activate service 1 again - change quantity and subscription date |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Find subscription A2                                             |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Terminate Service1 A                                             |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Instantiate Service 1 again 2                                    |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Activate service 1 again - no changes                            |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Find subscription A3                                             |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Find service instance - fail                                     |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Find service instance by ID                                      |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Terminate Service by id1                                         |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Find subscription T1                                             |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Terminate Service2                                               |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Find subscription T2                                             |     | /fill/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |          |
