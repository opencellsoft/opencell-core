@settings
Feature: Create/Update seller Plan by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> seller Plan by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The seller is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                               | dto       | api                    | action         | statusCode | status  | errorCode                        | message                                     |
      | settings/00001-seller-api-create/Success.json                          | SellerDto | /seller/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                             |
      | settings/00001-seller-api-create/Success.json                          | SellerDto | /seller/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Seller with code=TEST already exists.       |
      | settings/00001-seller-api-create/DO_NOT_EXIST.json                     | SellerDto | /seller/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Seller with code=NOT_EXIST does not exists. |
      | settings/00001-seller-api-create/Success1.json                         | SellerDto | /seller/               | Update         |        200 | SUCCESS |                                  |                                             |
      | settings/00001-seller-api-create/Success1.json                         | SellerDto | /seller/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                             |
      | settings/00001-seller-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | SellerDto | /seller/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Seller with code=XXX does not exists.       |
