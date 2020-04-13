@full @ignore
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
      | jsonFile                                                                                                                    | title                      | dto                        | api                                     | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/create-billingCycle_4.json     | Create BillingCycle 4      | BillingCycleDto            | /billingCycle/createOrUpdate            | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/create-customerAccount_3A.json | Create Customer Account 3A | CustomerAccountDto         | /account/customerAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/create-billingAccount_3A.json  | Create Billing Account 3A  | BillingAccountDto          | /account/billingAccount/createOrUpdate  | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/create-userAccount_3A.json     | Create User Account 3A     | UserAccountDto             | /account/userAccount/createOrUpdate     | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/create-subscription.json       | Create subscription        | SubscriptionDto            | /billing/subscription/createOrUpdate   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/activate-services.json         | Activate services          | ActivateServicesRequestDto | /billing/subscription/activateServices  | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/create-BR.json                 | Create BR                  | CreateBillingRunDto        | /billing/invoicing/createBillingRun     | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/recurring-job.json             | Recurring Rating Job       |                            | /job/execute                            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/rated-transaction-job.json     | Rated Transaction Job      |                            | /job/execute                            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/invoicing-job_1.json           | Invoicing Job☺             |                            | /job/execute                            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/validate-BR.json               | Validate BR                |                            | /billing/invoicing/validateBillingRun   | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/invoicing-job_2.json           | Invoicing Job 2            |                            | /job/execute                            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/08-due-date-delay-CA/XML-job.json                   | XML Job                    |                            | /job/execute                            | POST           |        200 | SUCCESS |           |         |        |          |
