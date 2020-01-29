@payments @ignore
Feature: Delete Payment Method by API

  Background: The classic offer is already executed
              Create Payment Method is already executed


  @admin @superadmin
  Scenario Outline: <action> Payment Method by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                  | dto              | api                          | action | statusCode | status  | errorCode                        | message                                            |
      | payments/00004-paymentMethod-api-create/SuccessTest.json  | PaymentMethodDto | /payment/paymentMethod?code= | Delete |        200 | SUCCESS |                                  |                                                    |
      | payments/00004-paymentMethod-api-create/DO_NOT_EXIST.json | PaymentMethodDto | /payment/paymentMethod?code= | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | PaymentMethod with code=NOT_EXIST does not exists. |
