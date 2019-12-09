Feature: Creation of a new subscriber-prefix

   
   Background:  System is configured.
   
Scenario: Create a new subscriber-prefix 
   
Given  Field "Code " is filled in with a  Code
And    Field " Description" is filled in with a  description
When   I Press  "Save" button 
Then   The subscriber-prefix is created 
And    I see success  message 


Scenario Outline: Create a new subscriber-prefix


Given   Field "Code " is filled in  with <Code>
And     Field " Description"  is filled in with   <Description>
When    I Press "Save" button 
Then    The subscriber-prefix is created 
And     This "<Message>" is displaying  


Examples:
| Code   |  Description      |  Message       |
| DSL   | DSL Product            |Element created |
