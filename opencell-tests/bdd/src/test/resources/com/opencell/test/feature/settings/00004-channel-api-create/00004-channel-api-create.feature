@settings
Feature: Create/Update Channel by API

  Background: System is configured.

  @admin @superadmin
  Scenario Outline: <action> Channel by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The channel is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                 | dto        | api                             | action         | statusCode | status  | errorCode                        | message                                                                |
      | settings/00004-channel-api-create/SuccessTest.json       | ChannelDto | /catalog/channel/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | settings/00004-channel-api-create/SuccessTest.json       | ChannelDto | /catalog/channel/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Channel with code=TEST already exists.                                 |
      | settings/00004-channel-api-create/DO_NOT_EXIST.json      | ChannelDto | /catalog/channel/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Channel with code=NOT_EXIST does not exists.                           |
      | settings/00004-channel-api-create/SuccessTest1.json      | ChannelDto | /catalog/channel/               | Update         |        200 | SUCCESS |                                  |                                                                        |
      | settings/00004-channel-api-create/MISSING_PARAMETER.json | ChannelDto | /catalog/channel/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | settings/00004-channel-api-create/INVALID_PARAMETER.json | ChannelDto | /catalog/channel/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.lang.Boolean` from String       |
