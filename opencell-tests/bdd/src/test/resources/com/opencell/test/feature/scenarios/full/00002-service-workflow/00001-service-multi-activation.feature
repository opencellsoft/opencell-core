@full
Feature: Service workflow - service multi activation

  @admin
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
    When I call the "<action>" "<api>"
    Then The entity "<entity>" matches
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                 | title               | api                                                                  | action | statusCode | status  | errorCode | message | entity       |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/find_subscription_1.json | Find subscription I | /billing/subscription?subscriptionCode=RS_FULL_212_SUB_SERVICE_MULTI | GET    |        200 | SUCCESS |           |         | subscription |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                      | title                     | dto                        | api                                    | action | statusCode | status  | errorCode | message | expected |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/activate_service_1_and_2.json | Activate services 1 and 2 | ActivateServicesRequestDto | /billing/subscription/activateServices | POST   |        200 | SUCCESS |           |         |          |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The entity "<entity>" matches
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                   | title                 | api                                                                                                                       | action | statusCode | status  | errorCode | message | entity          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/find_subscription_A1.json  | Find subscription A1  | /billing/subscription?subscriptionCode=RS_FULL_212_SUB_SERVICE_MULTI                                                      | GET    |        200 | SUCCESS |           |         | subscription    |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/find_service_instance.json | Find service instance | /billing/subscription/serviceInstance?subscriptionCode=RS_FULL_212_SUB_SERVICE_MULTI&serviceInstanceCode=RS_BASE_SERVICE1 | GET    |        200 | SUCCESS |           |         | serviceInstance |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                                                            | title                                                            | dto                                     | api                                       | action | statusCode | status  | errorCode                | message                                                                          |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/terminate_service_1.json                                            | Terminate Service1                                               | TerminateSubscriptionServicesRequestDto | /billing/subscription/terminateServices   | POST   |        200 | SUCCESS |                          |                                                                                  |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/instantiate_service_1_again.json                                    | Instantiate Service 1 again                                      | InstantiateServicesRequestDto           | /billing/subscription/instantiateServices | POST   |        200 | SUCCESS |                          |                                                                                  |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/activate_service_1_again_change_quantity_and_subscription_date.json | Activate service 1 again - change quantity and subscription date | ActivateServicesRequestDto              | /billing/subscription/activateServices    | POST   |        200 | SUCCESS |                          |                                                                                  |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The entity "<entity>" matches
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                  | title                | api                                                                  | action | statusCode | status  | errorCode | message | entity       |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/find_subscription_A2.json | Find subscription A2 | /billing/subscription?subscriptionCode=RS_FULL_212_SUB_SERVICE_MULTI | GET    |        200 | SUCCESS |           |         | subscription |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                                 | title                                 | dto                                     | api                                       | action | statusCode | status  | errorCode | message |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/terminate_service_1_A.json               | Terminate Service1 A                  | TerminateSubscriptionServicesRequestDto | /billing/subscription/terminateServices   | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/instantiate_service_1_again_2.json       | Instantiate Service 1 again 2         | InstantiateServicesRequestDto           | /billing/subscription/instantiateServices | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/activate_service_1_again_no_changes.json | Activate service 1 again - no changes | ActivateServicesRequestDto              | /billing/subscription/activateServices    | POST   |        200 | SUCCESS |           |         |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The entity "<entity>" matches
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                  | title                | api                                                                  | action | statusCode | status  | errorCode | message | entity       |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/find_subscription_A3.json | Find subscription A3 | /billing/subscription?subscriptionCode=RS_FULL_212_SUB_SERVICE_MULTI | GET    |        200 | SUCCESS |           |         | subscription |

  #| scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/ | Find service instance by ID  | /billing/subscription/serviceInstance?subscriptionCode=RS_FULL_212_SUB_SERVICE_MULTI&serviceInstanceCode=RS_BASE_SERVICE1 | GET    |        200 | SUCCESS |                   |                                                          | serviceInstance |
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                        | title                        | dto | api                                                                                                                       | action | statusCode | status | errorCode         | message                                                  |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/find_service_instance_fail.json | Find service instance - fail |     | /billing/subscription/serviceInstance?subscriptionCode=RS_FULL_212_SUB_SERVICE_MULTI&serviceInstanceCode=RS_BASE_SERVICE1 | GET    |        400 | FAIL   | INVALID_PARAMETER | More than one service instance with status [] was found. |

  #| scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/terminate_service_by_id.json    | Terminate Service by id1     | TerminateSubscriptionServicesRequestDto | /billing/subscription/terminateServices                                                                                   | POST   |        200 | SUCCESS |                   |                                                          |
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The entity "<entity>" matches
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                  | title                | api                                                                  | action | statusCode | status  | errorCode | message | entity                                   |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/find_subscription_T1.json | Find subscription T1 | /billing/subscription?subscriptionCode=RS_FULL_212_SUB_SERVICE_MULTI | GET    |        200 | SUCCESS |           |         | subscription.services.serviceInstance[0] |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                 | title              | dto                                     | api                                     | action | statusCode | status  | errorCode | message |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/terminate_service_2.json | Terminate Service2 | TerminateSubscriptionServicesRequestDto | /billing/subscription/terminateServices | POST   |        200 | SUCCESS |           |         |

  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The entity "<entity>" matches
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                  | title                | api                                                                  | action | statusCode | status  | errorCode | message | entity                                   |
      | scenarios/full/00002-service-workflow/service-workflow-service-multi-activation/find_subscription_T2.json | Find subscription T2 | /billing/subscription?subscriptionCode=RS_FULL_212_SUB_SERVICE_MULTI | GET    |        200 | SUCCESS |           |         | subscription.services.serviceInstance[1] |
