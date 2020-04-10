@offers
Feature: Create/Update tax class by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: <status> <action> taxclass by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The tax class is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                    | dto         | api                      | action         | statusCode | status  | errorCode                        | message                                              |
      | api/offers/00001-taxclass-api-create/SuccessTest.json       | TaxClassDto | /taxClass/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                      |
      | api/offers/00001-taxclass-api-create/SuccessTest.json       | TaxClassDto | /taxClass/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | TaxClass with code=TEST already exists.              |
      | api/offers/00001-taxclass-api-create/DO_NOT_EXIST.json      | TaxClassDto | /taxClass/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | TaxClass with code=NOT_EXIST                         |
      | api/offers/00001-taxclass-api-create/SuccessTest1.json      | TaxClassDto | /taxClass/               | Update         |        200 | SUCCESS |                                  |                                                      |
      | api/offers/00001-taxclass-api-create/SuccessTest1.json      | TaxClassDto | /taxClass/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                      |
      | api/offers/00001-taxclass-api-create/INVALID_PARAMETER.json | TaxClassDto | /taxClass/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize instance of `java.util.ArrayList` |
