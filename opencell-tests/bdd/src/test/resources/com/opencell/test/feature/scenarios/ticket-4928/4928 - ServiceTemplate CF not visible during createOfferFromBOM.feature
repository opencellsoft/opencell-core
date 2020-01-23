@#4928-BDD @test @ignore
Feature: Create a CustomField applied on service template 

     @admin @superadmin

  Scenario Outline: Create a CustomField applied on service template
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got a custom field on Service template 
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                          | dto                    | api                                | statusCode | status  | errorCode | message |
|scenario/ticket-4928/createCF.json | CustomFieldTemplateDto | /customFieldTemplate/createOrUpdate|        200 | SUCCESS |           |         |

#Feature: Create a CustomField applied on service template with applicableOnEL

Scenario Outline: Create a CustomField applied on service template with applicableOnEL
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got a custom field with applicableOnEL on Service template 
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                             | dto                    | api                                | statusCode | status  | errorCode | message |
| scenario/ticket-4928/createCF_EL.json| CustomFieldTemplateDto | /customFieldTemplate/createOrUpdate|        200 | SUCCESS |           |         |

#Feature: Create a CustomField applied on offer template

Scenario Outline: Create a CustomField applied on offer template
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got a custom field on offer template 
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                             | dto                    | api                                | statusCode | status  | errorCode | message |
| scenario/ticket-4928/createCF_OF.json| CustomFieldTemplateDto | /customFieldTemplate/createOrUpdate|        200 | SUCCESS |           |         |

#Feature: Create a CustomField applied on offer template with applicableOnEL

Scenario Outline: Create a CustomField applied on offer template with applicableOnEL
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got a custom field with applicableOnEL on offer template 
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                                | dto                    | api                                | statusCode | status  | errorCode | message |
| scenario/ticket-4928/createCF_EL_OF.json| CustomFieldTemplateDto | /customFieldTemplate/createOrUpdate|        200 | SUCCESS |           |         |

#Feature: Create an empty service template and fill custom fields's values

Scenario Outline: Create a service template and fill custom fields's values
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got a service template with custom fields filled in  
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                                | dto                    | api                                    | statusCode | status  | errorCode | message |
| scenario/ticket-4928/create_service.json| ServiceTemplateDto     | /catalog/serviceTemplate/createOrUpdate|        200 | SUCCESS |           |         |

#Feature: Create a Business Service Model

Scenario Outline: Create a Business Service Model
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the Business Service Model 
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                             | dto                    | api                                         | statusCode | status  | errorCode | message |
| scenario/ticket-4928/create_BSM.json | BusinessServiceModelDto| /catalog/businessServiceModel/createOrUpdate|        200 | SUCCESS |           |         |

#Feature: Create an offer template with custom field

Scenario Outline: Create an offer template with custom field and empty service with custom field
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the offer template with custom field 
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                              | dto                    | api                                         | statusCode | status  | errorCode | message |
| scenario/ticket-4928/create_offer.json| OfferTemplateDto       | /catalog/offerTemplate/createOrUpdate       |        200 | SUCCESS |           |         |

#Feature: Create a BOM with BSM

Scenario Outline: Create a BOM with BSM
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the offer template with custom field 
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                             | dto                    | api                                         | statusCode | status  | errorCode | message |
| scenario/ticket-4928/create_BOM.json | 	BusinessOfferModelDto | /catalog/businessOfferModel/createOrUpdate  |        200 | SUCCESS |           |         |

#Feature: Install BOM with BSM

Scenario Outline: Install BOM with BSM
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the offer template with custom field 
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                             | dto                    | api                                         | statusCode | status  | errorCode | message |
| scenario/ticket-4928/install_BOM.json| BusinessOfferModelDto  | /catalog/businessOfferModel/createOrUpdate  |        200 | SUCCESS |           |         |

#Feature: Create offer from BOM using the ServiceTemplate with no CF override 

Scenario Outline: Create offer from BOM using the ServiceTemplate with no CF override
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the offer template created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                                   | dto                    | api                                         | statusCode | status  | errorCode | message |
|scenario/ticket-4928/OF_SE_NO_OVERRIDE.json | BomOfferDto            | /catalogManagement/createOfferFromBOM       |        200 |         |           |         |

#Feature: Create offer from BOM using the ServiceTemplate and no CF override 

Scenario Outline: Create offer from BOM using the ServiceTemplate with CF override
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the offer template created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                                      | dto                    | api                                         | statusCode | status  | errorCode | message |
| scenario/ticket-4928/OF_SE_OVERRIDE.json.json | BomOfferDto            | /catalogManagement/createOfferFromBOM       |        200 |         |           |         |

#Feature: Create offer from BOM using BusinessServiceModel with no CF override 

Scenario Outline: Create offer from BOM using BusinessServiceModel with no CF override
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the offer template created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:

| jsonFile                                      | dto                    | api                                         | statusCode | status  | errorCode | message |
| scenario/ticket-4928/OF_BSM_NO_OVERRIDE.json  | BomOfferDto            | /catalogManagement/createOfferFromBOM       |        200 |         |           |         |

#Feature: Create offer from BOM using BusinessServiceModel instantiated once ,with CF override 

Scenario Outline: Create offer from BOM using BusinessServiceModel instantiated once, with CF override
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the offer template created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:
 
| jsonFile                                        | dto                    | api                                         | statusCode | status  | errorCode | message |
| scenario/ticket-4928/OF_BSM_SINGLE_OVERRIDE.json| BomOfferDto            | /catalogManagement/createOfferFromBOM       |        200 |         |           |         |

#Feature: Create offer from BOM using BusinessServiceModel instantiated several times, with CF override 

Scenario Outline: Create offer from BOM using BusinessServiceModel instantiated several times, with CF override
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the offer template created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    
Examples:
 
| jsonFile                                        | dto                    | api                                         | statusCode | status  | errorCode | message |
| scenario/ticket-4928/OF_BSM_MULTI_OVERRIDE.json | BomOfferDto            | /catalogManagement/createOfferFromBOM       |        200 |         |           |         |

#Feature: Check that ServiceTemplate CF are visible during createOfferFromBOM

  Scenario Outline: Check that ServiceTemplate CF are visible during createOfferFromBOM
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then I get the Service template  with a custom fields
    And Service template contains the following CF "<Val_aString_attendue>"
    And Service template contains the following CF "<Val_aStringFiltered_attendue>"
    Examples: 
      | jsonFile                               | api                           | action | statusCode | status  | Val_aString_attendue                         | Val_aStringFiltered_attendue                         |
      | scenario/ticket-4928/SuccessTest1.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_1.json | core/ticket-4928/Val_aStringFiltered_attendue_1.json |
      | scenario/ticket-4928/SuccessTest2.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_2.json | core/ticket-4928/Val_aStringFiltered_attendue_2.json |
      | scenario/ticket-4928/SuccessTest3.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_3.json | core/ticket-4928/Val_aStringFiltered_attendue_3.json |
      | scenario/ticket-4928/SuccessTest5.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_4.json | core/ticket-4928/Val_aStringFiltered_attendue_4.json |
      | scenario/ticket-4928/SuccessTest5.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_5.json | core/ticket-4928/Val_aStringFiltered_attendue_5.json |
      | scenario/ticket-4928/SuccessTest6.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_6.json | core/ticket-4928/Val_aStringFiltered_attendue_6.json |
      | scenario/ticket-4928/SuccessTest7.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_7.json | core/ticket-4928/Val_aStringFiltered_attendue_7.json |
      | scenario/ticket-4928/SuccessTest8.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_8.json | core/ticket-4928/Val_aStringFiltered_attendue_8.json |
