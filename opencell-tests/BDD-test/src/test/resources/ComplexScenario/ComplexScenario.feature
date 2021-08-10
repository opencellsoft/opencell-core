@TestComplexScenario
   # The objective of this scenario is to verify a complex scenario of entity Seller
Feature: Testing complex scenario

   Background:  System is configured.

   Scenario Outline: Complex Scenario

      Given  complex scenario composed of "<scenarios>"
      Then   Execute a complex scenario

      Examples:
         | scenarios |
         | UpdateSeller DeleteSeller |
