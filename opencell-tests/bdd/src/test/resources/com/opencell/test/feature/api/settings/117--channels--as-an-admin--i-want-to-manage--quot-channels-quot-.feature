@ignore
Feature: Create, modify and delete channels

   
   Background:  System is configured.
   
Scenario: Create a new channel 

Given  Field "code" is filled in with a code
And    Field "description" is filled in with a description
When   I Press  "Save" button 
Then   The channel  is created 
And    I see success  message 

Scenario: Update a channel 

Given  Field "description" is updated with a new description
When   I Press  "Save" button 
Then   The channel  is updated
And    I see success  message 

Scenario: Delete a channel 

Given  a a channel already created
When   I Press  "Delete" button 
And    I Press  "Confirm" button 
Then   The channel  is deleted
And    I see success  message 

Scenario Outline: Create a new channel

Given   Field "code" is filled in  with <Code>
And     Field "description"  is filled in with <description>
When    I Press  "Save" button 
Then    The channel is created  
And     This "<Message>" is displaying  

Scenario Outline: Update a channel 

Given   Field "description" is updated with a new <description>
When    I Press  "Save" button 
Then    The channel is updated  
And     This "<Message>" is displaying  

Scenario Outline: Delete a channel

Given   a channel already created
When    I Press  "Delete" button 
And     I Press  "Confirm" button 
Then    The channel is deleted   
And     This "<Message>" is displaying  

Examples:
| code         | description      | Message          |
| channel_Test  | channel_Test      | Element created  |

Examples:
| description | Message          | 
| channel_Test | Element updated  | 

Examples:
| Message          | 
| Element deleted  |