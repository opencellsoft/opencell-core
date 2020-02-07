@offers
Feature: Create/Update recurring charge by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: <status> <action> recurring charge by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The recurring charge is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                           | dto                        | api                                             | action         | statusCode | status  | errorCode                        | message                                                          |
      | api/offers/00002-recurringCharge-api-create/SuccessTest.json       | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                  |
      | api/offers/00002-recurringCharge-api-create/SuccessTest.json       | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | RecurringChargeTemplate with code=TEST already exists.           |
      | api/offers/00002-recurringCharge-api-create/DO_NOT_EXIST.json      | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | RecurringChargeTemplate with code=NOT_EXIST does not exists.     |
      | api/offers/00002-recurringCharge-api-create/SuccessTest1.json      | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/               | Update         |        200 | SUCCESS |                                  |                                                                  |
      | api/offers/00002-recurringCharge-api-create/SuccessTest1.json      | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                  |
      | api/offers/00002-recurringCharge-api-create/MISSING_PARAMETER.json | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values  |
      | api/offers/00002-recurringCharge-api-create/INVALID_PARAMETER.json | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.lang.Boolean` from String |
