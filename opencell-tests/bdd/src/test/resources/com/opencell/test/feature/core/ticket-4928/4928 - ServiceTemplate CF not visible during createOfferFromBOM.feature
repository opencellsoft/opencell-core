@#4928-BDD @test
Feature: Check that ServiceTemplate CF are visible during createOfferFromBOM

  Background: 4928-BDD.json is executed

  @admin @superadmin
  Scenario Outline: Check that ServiceTemplate CF are visible during createOfferFromBOM
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then I get the Service template  with a custom fields
    And Service template contains the following CF "<Val_aString_attendue>"
    And Service template contains the following CF "<Val_aStringFiltered_attendue>"

    Examples: 
      | jsonFile                           | api                           | action | statusCode | status  | Val_aString_attendue                         | Val_aStringFiltered_attendue                         |
      | core/ticket-4928/SuccessTest1.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_1.json | core/ticket-4928/Val_aStringFiltered_attendue_1.json |
      | core/ticket-4928/SuccessTest2.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_2.json | core/ticket-4928/Val_aStringFiltered_attendue_2.json |
      | core/ticket-4928/SuccessTest3.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_3.json | core/ticket-4928/Val_aStringFiltered_attendue_3.json |
      | core/ticket-4928/SuccessTest5.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_4.json | core/ticket-4928/Val_aStringFiltered_attendue_4.json |
      | core/ticket-4928/SuccessTest5.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_5.json | core/ticket-4928/Val_aStringFiltered_attendue_5.json |
      | core/ticket-4928/SuccessTest6.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_6.json | core/ticket-4928/Val_aStringFiltered_attendue_6.json |
      | core/ticket-4928/SuccessTest7.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_7.json | core/ticket-4928/Val_aStringFiltered_attendue_7.json |
      | core/ticket-4928/SuccessTest8.json | /catalog/serviceTemplate/list | Get    |        200 | SUCCESS | core/ticket-4928/Val_aString_attendue_8.json | core/ticket-4928/Val_aStringFiltered_attendue_8.json |
