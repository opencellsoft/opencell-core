Feature:  Create offer template by API

Background: The classic offer is executed

@admin @superadmin
Scenario Outline: Create offer template by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When  I call the "<api>"
    Then  The offer is created
    And   Validate that the statusCode is "<statusCode>"
    And   The status is "<status>"
    And   The message  is "<message>"
    And   The errorCode  is "<errorCode>"

Examples: 
    |jsonFile                                               |dto             |api                                  |statusCode|status |errorCode        |message|
    |offers/offer-template/SucessTest.json                  |OfferTemplateDto|/catalog/offerTemplate/createOrUpdate|200				|SUCCESS|                 |       |
    |offers/offer-template/SucessTest.json                  |OfferTemplateDto|/catalog/offerTemplate/createOrUpdate|200				|SUCCESS|                 |       |
    |offers/offer-template/INVALID_PARAMETER.json           |OfferTemplateDto|/catalog/offerTemplate/createOrUpdate|400			  |FAIL   |INVALID_PARAMETER|Can not deserialize value of type org.meveo.model.catalog.LifeCycleStatusEnum|
    |offers/offer-template/ServiceTemplateDoesNotExist.json |OfferTemplateDto|/catalog/offerTemplate/createOrUpdate|500			  |FAIL   |INVALID_PARAMETER|ServiceTemplatecode SE_USG_UNIT does not exist.|
