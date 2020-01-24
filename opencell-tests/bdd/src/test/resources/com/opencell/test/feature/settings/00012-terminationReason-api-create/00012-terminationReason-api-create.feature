@settings @ignore @review
Feature: Create Termination reason by API

  Background: The system is configured.

  @admin @superadmin
  Scenario Outline: Create Termination reason by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The Termination reason is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                               | dto                    | api                                    | statusCode | status  | errorCode                       | message                                                                |
      | settings/00012-terminationReason-api-create/SuccessTest.json           | 	TerminationReasonDto  | /	TerminationReasonDto /createOrUpdate |        200 | SUCCESS |                                 |                                                                        |
      | settings/00012-terminationReason-api-create/SuccessTest1.json          | 	TerminationReasonDto  | /	TerminationReasonDto /createOrUpdate |        200 | SUCCESS |                                 |                                                                        |
     