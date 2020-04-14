@full @ignore
Feature: Sub creation, Service Activation, Charging and invoicing - Create BR and Invoicing

  @admin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    And The entity "<entity>" matches "<expected>"

    Examples: 
      | jsonFile                                                                                                                       | title                 | dto                 | api                                   | action | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/03-create-BR-and-invoicing/create-BR.json              | create BR             | CreateBillingRunDto | /billing/invoicing/createBillingRun   | Create |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/03-create-BR-and-invoicing/recurring-rating-job.json   | Recurring Rating Job  | JobInstanceInfoDto  | /job/execute                          | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/03-create-BR-and-invoicing/rated-transactiton-job.json | Rated Transaction Job | JobInstanceInfoDto  | /job/execute                          | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/03-create-BR-and-invoicing/invoicing-job_1.json        | Invoicing Job 1       | JobInstanceInfoDto  | /job/execute                          | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/03-create-BR-and-invoicing/validated-BR.json           | validate BR           |                     | /billing/invoicing/validateBillingRun | Create |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/03-create-BR-and-invoicing/invoicing-job_2.json        | Invoicing Job 2       | JobInstanceInfoDto  | /job/execute                          | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/03-create-BR-and-invoicing/XML-job.json                | XML Job               | JobInstanceInfoDto  | /job/execute                          | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/03-create-BR-and-invoicing/PDF-job.json                | PDF  Job              | JobInstanceInfoDto  | /job/execute                          | POST   |        200 | SUCCESS |           |         |        |          |
