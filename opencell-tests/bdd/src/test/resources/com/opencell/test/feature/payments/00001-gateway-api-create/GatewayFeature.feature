Feature: Create a Gateway by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create a Gateway by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The gateway is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                               | dto               | api                                    | statusCode | status  | errorCode                        | message                                                                            |
      | SUCCESS.json                           | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | SUCCESS1.json                          | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | SUCCESS2.json                          | PaymentGatewayDto | /payment/paymentGateway                |        200 | SUCCESS |                                  |                                                                                    |
      | ENTITY_ALREADY_EXISTS_EXCEPTION.json   | PaymentGatewayDto | /payment/paymentGateway                |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | "PaymentGateway with code=INGENICO_OGONE_CARD_RELA5 already exists                                           |
      | MISSING_PARAMETER.json                 | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: paymentMethodType. |
