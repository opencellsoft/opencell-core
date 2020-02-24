@full
Feature: Setup base data - Setup administration

  @admin @superadmin
  Scenario Outline: Create <entity>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                    | entity                  | dto                   | api                                  | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/setup-administration/create_measurable_quantities.json | Measurable quantities   | MeasurableQuantityDto | /measurableQuantity                  | Create         |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_barChart.json              | Bar chart               | BarChartDto           | /chart/bar                           | Create         |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_pieChart.json              | Pie chart               | PieChartDto           | /chart/pie                           | Create         |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_lineChart.json             | Line chart              | LineChartDto          | /chart/line                          | Create         |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_scriptInstance.json        | ScriptInstance          | ScriptInstanceDto     | /scriptInstance/createOrUpdate       | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_notifications.json         | Notifications           | NotificationDto       | /notification/createOrUpdate         | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_webhook.json               | Webhook                 | WebHookDto            | /notification/webhook/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_emailNotifications.json    | EmailNotifications      | EmailNotificationDto  | /notification/email/createOrUpdate   | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_timer.json                 | Timer                   | TimerEntityDto        | /timerEntity/createOrUpdate          | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_jobInstance.json           | JobInstance             | JobInstanceDto        | /jobInstance/createOrUpdate          | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_filter.json                | Filter                  | FilterDto             | /filter/createOrUpdate               | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_timer_hourly.json          | Timer Hourly            | TimerEntityDto        | /timerEntity/createOrUpdate          | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_timer_daily_midnight.json  | Timer  Daily - Midnight | TimerEntityDto        | /timerEntity/createOrUpdate          | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-administration/create_paymentSchedule_job.json   | PaymentSchedule Job     | JobInstanceDto        | /jobInstance/createOrUpdate          | CreateOrUpdate |        200 | SUCCESS |           |         |
