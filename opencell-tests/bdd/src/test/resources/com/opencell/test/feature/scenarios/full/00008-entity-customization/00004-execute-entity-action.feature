@full
Feature: Entity Customization - Execute entity action

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
      | jsonFile                                                                                     | title                | dto                   | api                                                                                             | action         | statusCode | status  | errorCode | message | entity        | expected                                                                           |
      | scenarios/full/00008-entity-customization/04-execute-entity-action/create-script.json        | Create script        | ScriptInstanceDto     | /scriptInstance/createOrUpdate                                                                  | CreateOrUpdate |        200 | SUCCESS |           |         |               |                                                                                    |
      | scenarios/full/00008-entity-customization/04-execute-entity-action/create-custom-action.json | Create custom action | EntityCustomActionDto | /entityCustomization/action/createOrUpdate                                                      | CreateOrUpdate |        200 | SUCCESS |           |         |               |                                                                                    |
      | scenarios/full/00008-entity-customization/04-execute-entity-action/execute-action.json       | Execute action       |                       | /entityCustomization/entity/action/execute/ExecuteCustomAction_443/OfferTemplate/RS_BASE_OFFER3 | POST           |        200 | SUCCESS |           |         |               |                                                                                    |
      | scenarios/full/00008-entity-customization/04-execute-entity-action/find-offer.json           | Find offer           |                       | /catalog/offerTemplate?offerTemplateCode=RS_BASE_OFFER3                                         | GET            |        200 | SUCCESS |           |         | offerTemplate | scenarios/full/00008-entity-customization/04-execute-entity-action/find-offer.json |
      | scenarios/full/00008-entity-customization/04-execute-entity-action/delete-custom-action.json | Delete custom action |                       | /entityCustomization/action/ExecuteCustomAction_443/OfferTemplate                               | DEL            |        200 | SUCCESS |           |         |               |                                                                                    |
      | scenarios/full/00008-entity-customization/04-execute-entity-action/delete-script.json        | Delete script        | ScriptInstanceDto     | /scriptInstance/org.meveo.service.script.ExecuteCustomAction_443_Script                        | DEL            |        200 | SUCCESS |           |         |               |                                                                                    |
