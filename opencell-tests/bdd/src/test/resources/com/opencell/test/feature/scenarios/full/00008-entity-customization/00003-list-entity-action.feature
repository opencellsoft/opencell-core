@full
Feature: Entity Customization - List entity action

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
      | jsonFile                                                                                          | title                        | dto                   | api                                                                                          | action         | statusCode | status  | errorCode | message | entity                        | expected |
      | scenarios/full/00008-entity-customization/03-list-entity-action/create-script.json                | Create script                | ScriptInstanceDto     | /scriptInstance/createOrUpdate                                                               | CreateOrUpdate |        200 | SUCCESS |           |         |                               |          |
      | scenarios/full/00008-entity-customization/03-list-entity-action/create-custom-action-hidden.json  | Create custom action hidden  | EntityCustomActionDto | /entityCustomization/action/createOrUpdate                                                   | CreateOrUpdate |        200 | SUCCESS |           |         |                               |          |
      | scenarios/full/00008-entity-customization/03-list-entity-action/create-custom-action-visible.json | Create custom action visible | EntityCustomActionDto | /entityCustomization/action/createOrUpdate                                                   | CreateOrUpdate |        200 | SUCCESS |           |         |                               |          |
      | scenarios/full/00008-entity-customization/03-list-entity-action/list-visible-ca.json              | List visible CA              |                       | /entityCustomization/entity/listELFiltered?appliesTo=OfferTemplate&entityCode=RS_BASE_OFFER1 | GET            |        200 | SUCCESS |           |         | entityCustomization.action[0] |          |
      | scenarios/full/00008-entity-customization/03-list-entity-action/delete-custom-action-hidden.json  | Delete custom action hidden  |                       | /entityCustomization/action/ExecuteCustomAction_443_HIDDEN/OfferTemplate                     | DEL            |        200 | SUCCESS |           |         |                               |          |
      | scenarios/full/00008-entity-customization/03-list-entity-action/delete-custom-action-visible.json | Delete custom action visible |                       | /entityCustomization/action/ExecuteCustomAction_443_VISIBLE/OfferTemplate                    | DEL            |        200 | SUCCESS |           |         |                               |          |
      | scenarios/full/00008-entity-customization/03-list-entity-action/delete-script.json                | Delete script                |                       | /scriptInstance/org.meveo.service.script.ExecuteCustomAction_443_Script                      | DEL            |        200 | SUCCESS |           |         |                               |          |
