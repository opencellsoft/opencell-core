@payments
Feature: Delete Payment Gateway by API

  Background: The classic offer is already executed
              Create Payment Gateway is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> Payment Gateway by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                   | dto               | api                           | action | statusCode | status  | errorCode                        | message                                             |
      | payments/00003-paymentGateway-api-create/SuccessTest.json  | PaymentGatewayDto | /payment/paymentGateway?code= | Delete |        200 | SUCCESS |                                  |                                                     |
      | payments/00003-paymentGateway-api-create/DO_NOT_EXIST.json | PaymentGatewayDto | /payment/paymentGateway?code= | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | PaymentGateway with code=NOT_EXIST does not exists. |
