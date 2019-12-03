Feature:  Delete offer template by API

Background: The classic offer is already executed
            Create offer template by API is already executed

@admin @superadmin
Scenario Outline: Delete offer template by API
Given  The entity has the following information "<jsonFile>" as "<dto>"
When   I call the delete "<api>"
Then   The entity is deleted
And    Validate that the statusCode is "<statusCode>"
And    The status is "<status>"
And    The message  is "<message>"
And    The errorCode  is "<errorCode>"


Examples: 
    |jsonFile                                         |    dto          |   api                   |statusCode|status  |errorCode                        |message|
    |offers/offer-template/SucessTest.json             |OfferTemplateDto |/catalog/offerTemplate/  |200       |SUCCESS |                                 |       |
    |offers/offer-template/ENTITY_DOES_NOT_EXIST.json  |OfferTemplateDto |/catalog/offerTemplate/  |404       |FAIL    |ENTITY_DOES_NOT_EXISTS_EXCEPTION |OfferTemplate with code=NOT_EXIST /  /  does not exists.|