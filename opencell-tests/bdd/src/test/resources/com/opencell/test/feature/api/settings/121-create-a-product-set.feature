@ignore
Feature: Creation of a new Product-Set

   
   Background:  System is configured.
   
Scenario: Create a new Product-Set 
   
Given  Field "Code " is filled in with a  Code
And    Field " Description" is filled in with a  description
When   I Press  "Save" button 
Then   The Product-Set is created 
And    I see success  message 


Scenario Outline: Create a new Product-Set


Given   Field "Code " is filled in  with <Code>
And     Field " Description"  is filled in with   <Description>
When    I Press "Save" button 
Then    The Product-Set is created 
And     This "<Message>" is displaying  


Examples:
| Code   |  Description      |  Message       |
| DSL   | DSL Product            |Element created |
