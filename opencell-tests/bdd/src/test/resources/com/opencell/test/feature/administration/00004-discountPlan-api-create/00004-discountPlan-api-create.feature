@administration
Feature: Create/Update discount Plan by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> discount Plan by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The discount Plan is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto             | api                                  | action         | statusCode | status  | errorCode                        | message                                                       |
      | administration/00004-discountPlan-api-create/Success.json           | DiscountPlanDto | /catalog/discountPlan/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                               |
      | administration/00004-discountPlan-api-create/Success.json           | DiscountPlanDto | /catalog/discountPlan/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | DiscountPlan with code=TEST already exists.                   |
      | administration/00004-discountPlan-api-create/DO_NOT_EXIST.json      | DiscountPlanDto | /catalog/discountPlan/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | DiscountPlan with code=NOT_EXIST does not exists.             |
      | administration/00004-discountPlan-api-create/Success1.json          | DiscountPlanDto | /catalog/discountPlan/               | Update         |        200 | SUCCESS |                                  |                                                               |
      | administration/00004-discountPlan-api-create/Success1.json          | DiscountPlanDto | /catalog/discountPlan/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                               |
      | administration/00004-discountPlan-api-create/INVALID_PARAMETER.json | DiscountPlanDto | /catalog/discountPlan/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.util.Date` from String |
