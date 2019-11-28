Feature: Delete discount Plan by API

  Background: The classic offer is already executed
              Create discount Plan by API is already executed


  @admin @superadmin
  Scenario Outline: Delete discount Plan by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                    | dto             | api                                     | statusCode | status  | errorCode                        | message                                           |
      | administration/458-create-a-discount-plan-by-api/Success.json               | DiscountPlanDto | /catalog/discountPlan?discountPlanCode= |        200 | SUCCESS |                                  |                                                   |
      | administration/529-delete-a-discount-plan-by-api/ENTITY_DOES_NOT_EXIST.json | DiscountPlanDto | /catalog/discountPlan?discountPlanCode= |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | DiscountPlan with code=NOT_EXIST does not exists. |
