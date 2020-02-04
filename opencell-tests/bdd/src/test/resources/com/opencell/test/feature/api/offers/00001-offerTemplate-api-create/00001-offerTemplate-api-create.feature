@offers
Feature: Create/Update offer template by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: <status> <action> offer template by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The offer template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                   | dto              | api                                   | action         | statusCode | status  | errorCode                        | message                                                                                           |
      | api/offers/00001-offerTemplate-api-create/SuccessTest.json                 | OfferTemplateDto | /catalog/offerTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                   |
      | api/offers/00001-offerTemplate-api-create/SuccessTest.json                 | OfferTemplateDto | /catalog/offerTemplate/               | Create         |        400 | FAIL    | INVALID_PARAMETER                | An offer, valid on , already exists. Please change the validity dates of an existing offer first. |
      | api/offers/00001-offerTemplate-api-create/DO_NOT_EXIST.json                | OfferTemplateDto | /catalog/offerTemplate/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OfferTemplate with code=NOT_EXIST                                                                 |
      | api/offers/00001-offerTemplate-api-create/SuccessTest1.json                | OfferTemplateDto | /catalog/offerTemplate/               | Update         |        200 | SUCCESS |                                  |                                                                                                   |
      | api/offers/00001-offerTemplate-api-create/SuccessTest1.json                | OfferTemplateDto | /catalog/offerTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                   |
      | api/offers/00001-offerTemplate-api-create/INVALID_PARAMETER.json           | OfferTemplateDto | /catalog/offerTemplate/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.model.catalog.LifeCycleStatusEnum`                    |
      | api/offers/00001-offerTemplate-api-create/ServiceTemplateDoesNotExist.json | OfferTemplateDto | /catalog/offerTemplate/createOrUpdate | CreateOrUpdate |        500 | FAIL    | INVALID_PARAMETER                | ServiceTemplatecode SE_USG_UNIT does not exist.                                                   |
