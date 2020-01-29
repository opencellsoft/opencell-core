@payments
Feature: Delete Payment Schedule Template by API

  Background: The classic offer is already executed
              Create Payment Schedule Template is already executed


  @admin @superadmin
  Scenario Outline: Delete Payment Schedule Template by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                     | dto                        | api                                                           | statusCode | status  | errorCode                        | message                                                      |
      | payments/00005-paymentScheduleTemplate-api-create/SuccessTest.json           | PaymentScheduleTemplateDto | /payment/paymentScheduleTemplate?paymentScheduleTemplateCode= |        200 | SUCCESS |                                  |                                                              |
      | payments/10005-paymentScheduleTemplate-api-delete/ENTITY_DOES_NOT_EXIST.json | PaymentScheduleTemplateDto | /payment/paymentScheduleTemplate?paymentScheduleTemplateCode= |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | PaymentScheduleTemplate with code=NOT_EXIST does not exists. |
