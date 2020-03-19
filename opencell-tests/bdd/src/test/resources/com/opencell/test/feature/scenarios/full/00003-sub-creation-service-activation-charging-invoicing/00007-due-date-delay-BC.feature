@full
Feature: Sub creation, Service Activation, Charging and invoicing - Due date delay BC

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
      | jsonFile                                                                                                                | title                 | dto                        | api                                    | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/07-due-date-delay-BC/create-subscription.json   | Create subscription   | SubscriptionDto            | /billing/subscription/createOrUpdate   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/07-due-date-delay-BC/activate-services.json     | Activate services     | ActivateServicesRequestDto | /billing/subscription/activateServices | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/07-due-date-delay-BC/create-BR.json             | Create BR             | CreateBillingRunDto        | /billing/invoicing/createBillingRun    | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/07-due-date-delay-BC/recurring-rating-job.json  | Recurring Rating Job  |                            | /job/execute                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/07-due-date-delay-BC/rated-transaction-job.json | Rated Transaction Job |                            | /job/execute                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/07-due-date-delay-BC/invoicing-job_1.json       | Invoicing Job         |                            | /job/execute                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/07-due-date-delay-BC/validate-BR.json           | Validate BR           |                            | /billing/invoicing/validateBillingRun  | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/07-due-date-delay-BC/invoicing-job_2.json       | Invoicing Job 2       |                            | /job/execute                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/07-due-date-delay-BC/XML-job.json               | XML Job               |                            | /job/execute                           | POST           |        200 | SUCCESS |           |         |        |          |
