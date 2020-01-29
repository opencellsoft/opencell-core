@settings
Feature: Delete seller Plan by API

  Background: The classic offer is already executed
              Create seller Plan by API is already executed


  @admin @superadmin
  Scenario Outline: <action> a seller Plan by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                           | dto       | api      | action | statusCode | status  | errorCode                        | message                                     |
      | settings/00001-seller-api-create/Success.json      | SellerDto | /seller/ | Delete |        200 | SUCCESS |                                  |                                             |
      | settings/00001-seller-api-create/DO_NOT_EXIST.json | SellerDto | /seller/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Seller with code=NOT_EXIST does not exists. |
