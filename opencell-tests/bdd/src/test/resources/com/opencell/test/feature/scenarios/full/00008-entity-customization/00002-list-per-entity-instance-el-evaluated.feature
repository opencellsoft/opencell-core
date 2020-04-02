@full
Feature: Entity Customization - List per entity instance - el evaluated

  @admin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    And The entity "<entity>" matches "<expected>"

    Examples: 
      | jsonFile                                                                                                         | title                    | dto                    | api                                                                                          | action         | statusCode | status  | errorCode | message | entity                       | expected                                                                                                  |
      | scenarios/full/00008-entity-customization/02-list-per-entity-instance-el-evaluated/create-cft-offer-visible.json | Create CFT Offer visible | CustomFieldTemplateDto | /entityCustomization/field/createOrUpdate                                                    | CreateOrUpdate |        200 | SUCCESS |           |         |                              |                                                                                                           |
      | scenarios/full/00008-entity-customization/02-list-per-entity-instance-el-evaluated/create-cft-offer-hidden.json  | Create CFT Offer hidden  | CustomFieldTemplateDto | /entityCustomization/field/createOrUpdate                                                    | CreateOrUpdate |        200 | SUCCESS |           |         |                              |                                                                                                           |
      | scenarios/full/00008-entity-customization/02-list-per-entity-instance-el-evaluated/query-visible-cft.json        | Query visible CFT        |                        | /entityCustomization/entity/listELFiltered?appliesTo=OfferTemplate&entityCode=RS_BASE_OFFER1 | GET            |        200 | SUCCESS |           |         | entityCustomization.field[0] | scenarios/full/00008-entity-customization/02-list-per-entity-instance-el-evaluated/query-visible-cft.json |
