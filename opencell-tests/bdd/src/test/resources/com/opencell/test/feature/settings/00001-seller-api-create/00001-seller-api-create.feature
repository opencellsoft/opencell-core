@settings
Feature: Create seller Plan by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create seller Plan by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The seller is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                               | dto       | api                    | statusCode | status  | errorCode                        | message                               |
      | settings/00001-seller-api-create/Success.json                          | SellerDto | /seller/createOrUpdate |        200 | SUCCESS |                                  |                                       |
      | settings/00001-seller-api-create/Success1.json                         | SellerDto | /seller/createOrUpdate |        200 | SUCCESS |                                  |                                       |
      | settings/00001-seller-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | SellerDto | /seller/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Seller with code=XXX does not exists. |
