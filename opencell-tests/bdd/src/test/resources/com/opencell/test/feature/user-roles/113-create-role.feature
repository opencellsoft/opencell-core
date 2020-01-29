@ignore
Feature: Creation of a new Role

   
   Background:  System is configured.
   
Scenario: Create a new Role 
   
Given  Field "User role name " is filled in with a  name
And    Field "User role description" is filled in with a  description
When   I Press  "Save" button 
Then   The role is created 
And    I see success  message 


Scenario Outline: Create a new Role 


Given   Field "User role name   " is filled in  with <Name>
And     Field "User role description"  is filled in with   <Description>
When    I Press "Save" button 
Then    The role is created 
And     This "<Message>" is displaying  


Examples:
| User role name   | User role description      |  Message       |
| RoleName         | RoleDescription            |Element created |
