Feature: Create, modify and delete Tax classes

   
   Background:  System is configured.
   
Scenario: Create a new tax class 

Given  Field "code" is filled in with a code
And    Field "description" is filled in with a description
When   I Press  "Save" button 
Then   The tax class  is created 
And    I see success  message 

Scenario: Update a tax class 

Given  Field "description" is updated with a new description
When   I Press  "Save" button 
Then   The tax class  is updated
And    I see success  message 

Scenario: Delete a tax class 

Given  a tax class already created
When   I Press  "Delete" button 
And    I Press  "Confirm" button 
Then   The tax class  is deleted
And    I see success  message 

Scenario Outline: Create a new tax class 

Given   Field "code" is filled in  with <Code>
And     Field "description"  is filled in with <description>
When    I Press  "Save" button 
Then    The tax class is created  
And     This "<Message>" is displaying  

Scenario Outline: Update a tax class 

Given   Field "description" is updated with a new <description>
When    I Press  "Save" button 
Then    The tax class is updated  
And     This "<Message>" is displaying  

Scenario Outline: Delete a tax class 

Given   a tax class already created
When    I Press  "Delete" button 
And     I Press  "Confirm" button 
Then    The tax class is deleted   
And     This "<Message>" is displaying  

Examples:
| code         | description      | Message          |
| Taxclass_Test  | Taxclass_Test      | Element created  |

Examples:
| description | Message          | 
| Taxclass_Test | Element updated  | 

Examples:
| Message          | 
| Element deleted  |