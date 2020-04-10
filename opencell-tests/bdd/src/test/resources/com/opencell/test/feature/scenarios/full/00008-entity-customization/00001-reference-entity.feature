@full
Feature: Entity Customization - Reference Entity

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
      | jsonFile                                                                                                | title                                  | dto                    | api                                                                                     | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00008-entity-customization/01-reference-entity/create-cft-with-reference-entity.json     | Create CFT with reference entity       | CustomFieldTemplateDto | /entityCustomization/field/createOrUpdate                                               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-entity-customization/01-reference-entity/get-reference-entities-no-wildcard.json   | Get reference entities - no wildcard   |                        | /entityCustomization/listBusinessEntityForCFVByCode/?code=RS_FULL_443_RCF100            | GET            |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-entity-customization/01-reference-entity/get-reference-entities-with-wildcard.json | Get reference entities - with wildcard |                        | /entityCustomization/listBusinessEntityForCFVByCode/?code=RS_FULL_443_RCF100&wildcode=2 | GET            |        200 | SUCCESS |           |         |        |          |
