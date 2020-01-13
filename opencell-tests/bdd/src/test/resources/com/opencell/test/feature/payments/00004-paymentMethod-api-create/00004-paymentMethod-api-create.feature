@payments @ignore
Feature: Create Payment Method by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: Create Payment Method by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The payment method is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                       | dto              | api                                   | statusCode | status  | errorCode                       | message                                                                                         |
      | payments/00004-paymentMethod-api-create/SuccessTest.json       | PaymentMethodDto | /payment/paymentMethod/               |        200 | SUCCESS |                                 |                                                                                                 |
      | payments/00004-paymentMethod-api-create/SuccessTest.json       | PaymentMethodDto | /payment/paymentMethod/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | PaymentGateway with code=TEST already exists.                                                   |
      | payments/00004-paymentMethod-api-create/MISSING_PARAMETER.json | PaymentMethodDto | /payment/paymentMethod/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values: code, type, paymentMethodType. |
      | payments/00004-paymentMethod-api-create/INVALID_PARAMETER.json | PaymentMethodDto | /payment/paymentMethod/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER               | Cannot deserialize value of type `org.meveo.model.payments.PaymentGatewayTypeEnum` from String   |

  #@admin @superadmin
  #Scenario Outline: Update Payment Method by API
    #Given The entity has the following information "<jsonFile>" as "<dto>"
    #When I call the put "<api>"
    #Then The payment method is created
    #And Validate that the statusCode is "<statusCode>"
    #And The status is "<status>"
    #And The message  is "<message>"
    #And The errorCode  is "<errorCode>"
#
    #Examples: 
      #| payments/00004-paymentMethod-api-create/SuccessTest1.json | PaymentMethodDto | /payment/paymentMethod/ | 200 | SUCCESS |  |  |
