Feature: Create, modify and delete Termination reasons

   
   Background:  System is configured.
   
Scenario: Create a new Termination reason

Given  Field "code" is filled in with a code
And    Field "description" is filled in with a description
When   I Press  "Save" button 
Then   The Termination reason is created 
And    I see success  message 

Scenario: Update a Termination reason

Given  Field "description" is updated with a new description
When   I Press  "Save" button 
Then   The Termination reason is updated
And    I see success  message 

Scenario: Delete a Termination reason 

Given  a Termination reason already created
When   I Press  "Delete" button 
And    I Press  "Confirm" button 
Then   The Termination reason is deleted
And    I see success  message 

Scenario Outline: Create a new Termination reason 

Given   Field "code" is filled in  with <Code>
And     Field "description"  is filled in with <description>
When    I Press  "Save" button 
Then    The  Termination reason is created  
And     This "<Message>" is displaying  

Scenario Outline: Update a Termination reason   

Given   Field "description" is updated with a new <description>
When    I Press  "Save" button 
Then    The  Termination reason is updated  
And     This "<Message>" is displaying  

Scenario Outline: Delete a Tilte and civility 

Given   a  Termination reason already created
When    I Press  "Delete" button 
And     I Press  "Confirm" button 
Then    The  Termination reason is deleted   
And     This "<Message>" is displaying  

Examples:
| code                      | description                   | Message          |
| TerminationReason _Test   | TerminationReason_Test        | Element created  |

Examples:
| description              | Message          | 
| TerminationReason _Test  | Element updated  | 

Examples:
| Message          | 
| Element deleted  |