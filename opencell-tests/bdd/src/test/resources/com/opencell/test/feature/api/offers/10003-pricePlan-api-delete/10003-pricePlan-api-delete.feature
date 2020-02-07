@offers
Feature: Delete price plan by API

  Background: The classic offer is already executed
              Create/Update price plan by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> price plan by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                | dto                | api                 | action | statusCode | status  | errorCode                        | message                                              |
      | api/offers/00003-pricePlan-api-create/SuccessTest.json  | PricePlanMatrixDto | /catalog/pricePlan/ | Delete |        200 | SUCCESS |                                  |                                                      |
      | api/offers/00003-pricePlan-api-create/DO_NOT_EXIST.json | PricePlanMatrixDto | /catalog/pricePlan/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | PricePlanMatrix with code=NOT_EXIST does not exists. |
