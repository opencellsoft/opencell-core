@administration
Feature: Delete discount Plan by API

  Background: The classic offer is already executed
              Create discount Plan by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> discount Plan by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                       | dto             | api                                     | action | statusCode | status  | errorCode                        | message                                           |
      | administration/00004-discountPlan-api-create/Success.json      | DiscountPlanDto | /catalog/discountPlan?discountPlanCode= | Delete |        200 | SUCCESS |                                  |                                                   |
      | administration/00004-discountPlan-api-create/DO_NOT_EXIST.json | DiscountPlanDto | /catalog/discountPlan?discountPlanCode= | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | DiscountPlan with code=NOT_EXIST does not exists. |
