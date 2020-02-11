@#4928-BDD @scenarios
Feature: Inherit or Override CustomField values when creating Offer from BOM

  Background: System is installed

  @admin @superadmin
  Scenario Outline: Create a CustomField <name> applied on service template
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The custom field template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | name             | jsonFile                                      | dto                    | action | api                                 | statusCode | status  | errorCode | message |
      | DEFAULT_CF1      | scenarios/ticket-4928-2/DEFAULT_CF1.json      | CustomFieldTemplateDto | POST   | /customFieldTemplate/createOrUpdate |        200 | SUCCESS |           |         |
      | DEFAULT_CF2      | scenarios/ticket-4928-2/DEFAULT_CF2.json      | CustomFieldTemplateDto | POST   | /customFieldTemplate/createOrUpdate |        200 | SUCCESS |           |         |
      | DEFAULT_CF11     | scenarios/ticket-4928-2/DEFAULT_CF11.json     | CustomFieldTemplateDto | POST   | /customFieldTemplate/createOrUpdate |        200 | SUCCESS |           |         |
      | DEFAULT_CF22     | scenarios/ticket-4928-2/DEFAULT_CF22.json     | CustomFieldTemplateDto | POST   | /customFieldTemplate/createOrUpdate |        200 | SUCCESS |           |         |
      | DEFAULT_CF_OF_1  | scenarios/ticket-4928-2/DEFAULT_CF_OF_1.json  | CustomFieldTemplateDto | POST   | /customFieldTemplate/createOrUpdate |        200 | SUCCESS |           |         |
      | DEFAULT_CF_OF_2  | scenarios/ticket-4928-2/DEFAULT_CF_OF_2.json  | CustomFieldTemplateDto | POST   | /customFieldTemplate/createOrUpdate |        200 | SUCCESS |           |         |
      | DEFAULT_CF_OF_11 | scenarios/ticket-4928-2/DEFAULT_CF_OF_11.json | CustomFieldTemplateDto | POST   | /customFieldTemplate/createOrUpdate |        200 | SUCCESS |           |         |
      | DEFAULT_CF_OF_22 | scenarios/ticket-4928-2/DEFAULT_CF_OF_22.json | CustomFieldTemplateDto | POST   | /customFieldTemplate/createOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create a service template "SE" and fill custom fields's values
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The service template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                    | dto                | action | api                                     | statusCode | status  | errorCode | message |
      | scenarios/ticket-4928-2/create_service.json | ServiceTemplateDto | POST   | /catalog/serviceTemplate/createOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create a Business Service Model
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The business service model is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                | dto                     | action | api                                          | statusCode | status  | errorCode | message |
      | scenarios/ticket-4928-2/create_BSM.json | BusinessServiceModelDto | POST   | /catalog/businessServiceModel/createOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create an offer template with custom field and empty service with custom field
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The offer template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                  | dto              | action | api                                   | statusCode | status  | errorCode | message |
      | scenarios/ticket-4928-2/create_offer.json | OfferTemplateDto | POST   | /catalog/offerTemplate/createOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create a BOM with BSM
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The business offer model is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                | dto                   | action | api                                        | statusCode | status  | errorCode | message |
      | scenarios/ticket-4928-2/create_BOM.json | BusinessOfferModelDto | POST   | /catalog/businessOfferModel/createOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Install BOM with BSM
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The business offer model is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                 | dto                   | action | api                                 | statusCode | status  | errorCode | message |
      | scenarios/ticket-4928-2/install_BOM.json | BusinessOfferModelDto | PUT    | /catalog/businessOfferModel/install |        200 | SUCCESS |           |         |

  Scenario Outline: Create offer <OF_Name>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the create offer from bom "<api>"
    Then The offer template is created
    And Validate that the statusCode is "<statusCode>"

    Examples: 
      | OF_Name                | jsonFile                                            | dto         | action | api                                   | statusCode | status | errorCode | message |
      | OF_SE_NO_OVERRIDE      | scenarios/ticket-4928-2/OF_SE_NO_OVERRIDE.json      | BomOfferDto | POST   | /catalogManagement/createOfferFromBOM |        200 |        |           |         |
      | OF_SE_OVERRIDE         | scenarios/ticket-4928-2/OF_SE_OVERRIDE.json         | BomOfferDto | POST   | /catalogManagement/createOfferFromBOM |        200 |        |           |         |
      | OF_BSM_NO_OVERRIDE     | scenarios/ticket-4928-2/OF_BSM_NO_OVERRIDE.json     | BomOfferDto | POST   | /catalogManagement/createOfferFromBOM |        200 |        |           |         |
      | OF_BSM_SINGLE_OVERRIDE | scenarios/ticket-4928-2/OF_BSM_SINGLE_OVERRIDE.json | BomOfferDto | POST   | /catalogManagement/createOfferFromBOM |        200 |        |           |         |
      | OF_BSM_MULTI_OVERRIDE  | scenarios/ticket-4928-2/OF_BSM_MULTI_OVERRIDE.json  | BomOfferDto | POST   | /catalogManagement/createOfferFromBOM |        200 |        |           |         |
      | OF_BSM_MULTI_OVERRIDE  | scenarios/ticket-4928-2/OF_BSM_MULTI_OVERRIDE.json  | BomOfferDto | POST   | /catalogManagement/createOfferFromBOM |        200 |        |           |         |

  Scenario Outline: Check that ServiceTemplate CF are visible during createOfferFromBOM
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then I get the Service template with a custom fields
    And Service template contains the following CF "<Val_aString_attendue>"
    And Service template contains the following CF "<Val_aStringFiltered_attendue>"

    Examples: 
      | jsonFile                                  | api                           | action | statusCode | status  | Val_aString_attendue                           | Val_aStringFiltered_attendue                           |
      | scenarios/ticket-4928-2/SuccessTest1.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928-2/Val_aString_attendue_1.json | core/ticket-4928-2/Val_aStringFiltered_attendue_1.json |
      | scenarios/ticket-4928-2/SuccessTest2.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928-2/Val_aString_attendue_2.json | core/ticket-4928-2/Val_aStringFiltered_attendue_2.json |
      | scenarios/ticket-4928-2/SuccessTest3.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928-2/Val_aString_attendue_3.json | core/ticket-4928-2/Val_aStringFiltered_attendue_3.json |
      | scenarios/ticket-4928-2/SuccessTest5.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928-2/Val_aString_attendue_4.json | core/ticket-4928-2/Val_aStringFiltered_attendue_4.json |
      | scenarios/ticket-4928-2/SuccessTest5.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928-2/Val_aString_attendue_5.json | core/ticket-4928-2/Val_aStringFiltered_attendue_5.json |
      | scenarios/ticket-4928-2/SuccessTest6.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928-2/Val_aString_attendue_6.json | core/ticket-4928-2/Val_aStringFiltered_attendue_6.json |
      | scenarios/ticket-4928-2/SuccessTest7.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928-2/Val_aString_attendue_7.json | core/ticket-4928-2/Val_aStringFiltered_attendue_7.json |
      | scenarios/ticket-4928-2/SuccessTest8.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928-2/Val_aString_attendue_8.json | core/ticket-4928-2/Val_aStringFiltered_attendue_8.json |
