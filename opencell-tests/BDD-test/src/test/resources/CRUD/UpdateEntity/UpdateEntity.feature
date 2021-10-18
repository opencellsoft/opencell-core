   # The objective of this scenario is to verify whether an entity
   # can be updated by API
   Feature: Testing method Update on an entity

      Background:  System is configured

      @UpdateEntity
      Scenario Outline: UpdateEntity

         Given  Actor wants to test on API version "<apiVer>" with business domain "<businessDomainPath>"
         When   Entity "<entity>" with code or id "<codeOrId>"
         And    Body request given by "<jsonFile>"
         Then   The test is "<status>"

         Examples:
            | apiVer | businessDomainPath | entity  | codeOrId          | jsonFile          | status |
            | v1     | accountManagement  | sellers | ben.ohara.seller1 | UpdateSeller.json | pass   |
