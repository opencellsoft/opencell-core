@custom @ignore
 Feature: Delete Custom  Entity Instance by API

  Background: The classic offer is already executed
              Create Custom  Entity Instance  by API is already executed


  @admin @superadmin
  Scenario Outline: Delete Custom  Entity Instance by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The custom entity instance  is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                              | dto                     | api                          | statusCode | status  | errorCode                        | message                                                   |
      | api/custom/customentityinstance-api-delete/SuccessDelete.json         | CustomEntityTemplateDto | /customEntityInstance/test   |        200 | SUCCESS |                                  |                                                           |
      | api/custom/customentityinstance-api-delete/ENTITY_DOES_NOT_EXIST.json | CustomEntityTemplateDto | /entityCustomization/entity/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomEntityInstance with code=NOT_EXIST does not exists. |
