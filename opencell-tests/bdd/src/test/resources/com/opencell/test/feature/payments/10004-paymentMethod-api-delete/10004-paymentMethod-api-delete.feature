@payments @test
Feature: Delete Payment Method by API

  Background: The classic offer is already executed
              Create Payment Method is already executed


  @admin @superadmin
  Scenario Outline: Delete Payment Method by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                           | dto              | api                          | statusCode | status  | errorCode                        | message                                            |
      | payments/00004-paymentMethod-api-create/SuccessTest.json           | PaymentMethodDto | /payment/paymentMethod?code= |        200 | SUCCESS |                                  |                                                    |
      | payments/10004-paymentMethod-api-delete/ENTITY_DOES_NOT_EXIST.json | PaymentMethodDto | /payment/paymentMethod?code= |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | PaymentMethod with code=NOT_EXIST does not exists. |
