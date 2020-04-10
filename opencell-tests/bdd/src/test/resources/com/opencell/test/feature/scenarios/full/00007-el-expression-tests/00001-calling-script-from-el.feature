@full
Feature: Calling script from EL

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
      | jsonFile                                                                                           | title                     | dto                        | api                                                             | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00007-el-expression-tests/01-calling-script-from-el/create-script.json              | Create script             | ScriptInstanceDto          | /scriptInstance/createOrUpdate                                  | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/01-calling-script-from-el/create-cft-on-charge.json       | Create CFT on Charge      | CustomFieldTemplateDto     | /entityCustomization/field/createOrUpdate                       | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/01-calling-script-from-el/create-recurring-charge-ok.json | Create Recuring Charge OK | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate                 | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/01-calling-script-from-el/remove-cft.json                 | Remove CFT                |                            | /entityCustomization/field/RS_BASE_CFT_EL_SCRIPT/ChargeTemplate | DEL            |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/01-calling-script-from-el/remove-script-instance.json     | Remove Script Instance    |                            | /scriptInstance/org.meveo.service.script.TheRS_BASE_ScriptEL    | DEL            |        200 | SUCCESS |           |         |        |          |
