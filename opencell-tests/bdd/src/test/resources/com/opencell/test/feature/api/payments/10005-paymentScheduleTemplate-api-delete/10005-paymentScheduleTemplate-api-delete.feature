@payments
Feature: Delete Payment Schedule Template by API

  Background: The classic offer is already executed
              Create Payment Schedule Template is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> Payment Schedule Template by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                | dto                        | api                                                           | action | statusCode | status  | errorCode                        | message                                                      |
      | api/payments/00005-paymentScheduleTemplate-api-create/SuccessTest.json  | PaymentScheduleTemplateDto | /payment/paymentScheduleTemplate?paymentScheduleTemplateCode= | Delete |        200 | SUCCESS |                                  |                                                              |
      | api/payments/00005-paymentScheduleTemplate-api-create/DO_NOT_EXIST.json | PaymentScheduleTemplateDto | /payment/paymentScheduleTemplate?paymentScheduleTemplateCode= | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | PaymentScheduleTemplate with code=NOT_EXIST does not exists. |
