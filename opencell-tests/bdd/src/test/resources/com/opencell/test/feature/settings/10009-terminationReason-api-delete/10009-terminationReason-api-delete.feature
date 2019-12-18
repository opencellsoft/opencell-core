@settings
Feature: Delete termination Reason by API

  Background: System is configured.
    Create termination Reason by API already executed.


  @admin @superadmin
  Scenario Outline: Delete termination Reason by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>" with identifier "code"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                               | dto                  | api                 | statusCode | status  | errorCode                        | message                                       |
      | settings/00009-terminationReason-api-create/SuccessTest.json           | terminationReasonDto | /terminationReason/ |        200 | SUCCESS |                                  |                                               |
      | settings/10009-terminationReason-api-delete/ENTITY_DOES_NOT_EXIST.json | terminationReasonDto | /terminationReason/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | SubscriptionTerminationReason with code=NOT_EXIST does not exists. |
