Feature: Create, modify and delete Tilte and civility 

   
   Background:  System is configured.
   
Scenario: Create a new Tilte and civility  

Given  Field "code" is filled in with a code
And    Field "description" is filled in with a description
When   I Press  "Save" button 
Then   The Tilte and civility is created 
And    I see success  message 

Scenario: Update a Tilte and civility 

Given  Field "description" is updated with a new description
When   I Press  "Save" button 
Then   The Tilte and civility   is updated
And    I see success  message 

Scenario: Delete a Tilte and civility  

Given  a Tilte and civility already created
When   I Press  "Delete" button 
And    I Press  "Confirm" button 
Then   The Tilte and civility   is deleted
And    I see success  message 

Scenario Outline: Create a new Tilte and civility 

Given   Field "code" is filled in  with <Code>
And     Field "description"  is filled in with <description>
When    I Press  "Save" button 
Then    The Tilte and civility  is created  
And     This "<Message>" is displaying  

Scenario Outline: Update a Tilte and civility  

Given   Field "description" is updated with a new <description>
When    I Press  "Save" button 
Then    The Tilte and civility  is updated  
And     This "<Message>" is displaying  

Scenario Outline: Delete a Tilte and civility 

Given   a Tilte and civility  already created
When    I Press  "Delete" button 
And     I Press  "Confirm" button 
Then    The Tilte and civility  is deleted   
And     This "<Message>" is displaying  

Examples:
| code                      | description                   | Message          |
| TilteCivility _Test       | TerminationReason _Test       | Element created  |

Examples:
| description              | Message          | 
| TilteCivility _Test      | Element updated  | 

Examples:
| Message          | 
| Element deleted  |