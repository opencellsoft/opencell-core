@payments
Feature: Create Account Operation by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create Account Operation by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The account operation is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                         | dto                 | api                              | statusCode | status  | errorCode                        | message                                                          |
      | payments/00001-accountOperation-api-create/Success.json                          | AccountOperationDto | /accountOperation/createOrUpdate |        200 | SUCCESS |                                  |                                                                  |
      | payments/00001-accountOperation-api-create/Success1.json                         | AccountOperationDto | /accountOperation/createOrUpdate |        200 | SUCCESS |                                  |                                                                  |
      | payments/00001-accountOperation-api-create/MISSING_PARAMETER.json                | AccountOperationDto | /accountOperation/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: |
      | payments/00001-accountOperation-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | AccountOperationDto | /accountOperation/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | AccountOperation with code=NOT_EXIST does not exists.            |
