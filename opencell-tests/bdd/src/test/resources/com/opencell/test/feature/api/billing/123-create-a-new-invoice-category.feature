@ignore
Feature: Creation of a new invoice category

   
   Background:  System is configured.
   
Scenario: Create a new invoice category 
   
Given  Field "Code " is filled in with a  Code
And    Field " Description" is filled in with a  description
When   I Press  "Save" button 
Then   The invoice category is created 
And    I see success  message 


Scenario Outline: Create a new invoice category


Given   Field "Code " is filled in  with <Code>
And     Field " Description"  is filled in with   <Description>
When    I Press "Save" button 
Then    The invoice category is created 
And     This "<Message>" is displaying  


Examples:
| Code         |  Description  |  Message       |
| SUBSCRIPTION | subscription  |Element created |
