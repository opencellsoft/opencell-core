@payments @ignore
Feature: Create Payment Gateway by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: Create Payment Gateway by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The payment gateway is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                        | dto               | api                                    | statusCode | status  | errorCode                       | message                                                                                         |
      | payments/00003-paymentGateway-api-create/SuccessTest.json       | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate |        200 | SUCCESS |                                 |                                                                                                 |
      | payments/00003-paymentGateway-api-create/SuccessTest.json       | PaymentGatewayDto | /payment/paymentGateway/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | PaymentGateway with code=TEST already exists.                                                   |
      | payments/00003-paymentGateway-api-create/SuccessTest1.json      | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate |        200 | SUCCESS |                                 |                                                                                                 |
      | payments/00003-paymentGateway-api-create/MISSING_PARAMETER.json | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values: code, type, paymentMethodType. |
      | payments/00003-paymentGateway-api-create/INVALID_PARAMETER.json | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER               | Can not deserialize value of type org.meveo.model.payments.PaymentGatewayTypeEnum from String   |
