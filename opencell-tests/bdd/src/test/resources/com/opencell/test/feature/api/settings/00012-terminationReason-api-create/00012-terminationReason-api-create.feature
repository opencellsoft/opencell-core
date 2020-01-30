@settings @ignore @review
Feature: Create/Update Termination reason by API

  Background: The system is configured.

  @admin @superadmin
  Scenario Outline: <status> <action> Termination reason by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The Termination reason is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                   | dto                    | api                                    |  action         |statusCode | status  | errorCode                       | message                                                                |
      | api/settings/00012-terminationReason-api-create/SuccessTest.json           | 	TerminationReasonDto  | /	TerminationReasonDto /createOrUpdate |  action         |       200 | SUCCESS |                                 |                                                                        |
      | api/settings/00012-terminationReason-api-create/SuccessTest1.json          | 	TerminationReasonDto  | /	TerminationReasonDto /createOrUpdate |  action         |       200 | SUCCESS |                                 |                                                                        |
     