@administration
Feature: Create discount Plan by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create discount Plan by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The discount Plan is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto             | api                                  | statusCode | status  | errorCode         | message                                                       |
      | administration/00004-discountPlan-api-create/Success.json           | DiscountPlanDto | /catalog/discountPlan/createOrUpdate |        200 | SUCCESS |                   |                                                               |
      | administration/00004-discountPlan-api-create/Success1.json          | DiscountPlanDto | /catalog/discountPlan/createOrUpdate |        200 | SUCCESS |                   |                                                               |
      | administration/00004-discountPlan-api-create/INVALID_PARAMETER.json | DiscountPlanDto | /catalog/discountPlan/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER | Cannot deserialize value of type `java.util.Date` from String |
