@settings @ignore @review
Feature: Delete termination Reason by API

  Background: System is configured.
    Create termination Reason by API already executed.


  @admin @superadmin
  Scenario Outline: <status> <action> termination Reason by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                          | dto                  | api                 | action | statusCode | status  | errorCode                        | message                                                            |
      | api/settings/00012-terminationReason-api-create/SuccessTest.json  | terminationReasonDto | /terminationReason/ | Delete |        200 | SUCCESS |                                  |                                                                    |
      | api/settings/00012-terminationReason-api-create/DO_NOT_EXIST.json | terminationReasonDto | /terminationReason/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | SubscriptionTerminationReason with code=NOT_EXIST does not exists. |
