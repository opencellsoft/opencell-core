@settings
Feature: Delete Channel by API

  Background: System is configured.
    Create Channel by API already executed.


  @admin @superadmin
  Scenario Outline: <status> <action> Channel by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                            | dto        | api                           | action | statusCode | status  | errorCode                        | message                                      |
      | settings/00004-channel-api-create/SuccessTest.json  | ChannelDto | /catalog/channel?channelCode= | Delete |        200 | SUCCESS |                                  |                                              |
      | settings/00004-channel-api-create/DO_NOT_EXIST.json | ChannelDto | /catalog/channel?channelCode= | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Channel with code=NOT_EXIST does not exists. |
