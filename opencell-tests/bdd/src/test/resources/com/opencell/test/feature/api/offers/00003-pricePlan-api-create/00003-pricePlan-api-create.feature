@offers
Feature: Create/Update price plan by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: <status> <action> price plan by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The price plan is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                     | dto                | api                               | action         | statusCode | status  | errorCode                        | message                                                                                       |
      | api/offers/00003-pricePlan-api-create/SuccessTest.json       | PricePlanMatrixDto | /catalog/pricePlan/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                               |
      | api/offers/00003-pricePlan-api-create/SuccessTest.json       | PricePlanMatrixDto | /catalog/pricePlan/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | PricePlanMatrix with code=TEST already exists.                                                |
      | api/offers/00003-pricePlan-api-create/DO_NOT_EXIST.json      | PricePlanMatrixDto | /catalog/pricePlan/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | PricePlanMatrix with code=NOT_EXIST does not exists.                                          |
      | api/offers/00003-pricePlan-api-create/SuccessTest1.json      | PricePlanMatrixDto | /catalog/pricePlan/               | Update         |        200 | SUCCESS |                                  |                                                                                               |
      | api/offers/00003-pricePlan-api-create/SuccessTest1.json      | PricePlanMatrixDto | /catalog/pricePlan/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                               |
      | api/offers/00003-pricePlan-api-create/MISSING_PARAMETER.json | PricePlanMatrixDto | /catalog/pricePlan/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: eventCode, amountWithoutTax. |
      | api/offers/00003-pricePlan-api-create/INVALID_PARAMETER.json | PricePlanMatrixDto | /catalog/pricePlan/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.math.BigDecimal` from String                           |
