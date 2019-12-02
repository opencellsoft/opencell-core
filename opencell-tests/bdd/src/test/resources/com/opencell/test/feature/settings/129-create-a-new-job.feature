Feature: Creation of a new Job  

   
   Background:  System is configured.
   
Scenario: Create a new Job  
   
Given  Field "Code " is filled in with a  Code
And    Field "Job Category" is filled in with a  category
And    Field "Parameters" is filled in with a  Parameter
And    Field "Next job" is filled in with a  Next job
When   I Press  "Save" button 
Then   The Job   is created 
And    I see success  message 


Scenario Outline: Create a new Job


Given   Field "Code " is filled in  with <Code>
And     Field " Job Category"  is filled in with  <Job Category>
And     Field " Parameters"  is filled in with   <Parameters>
And     Field " Next job"  is filled in with   <Next job>
When    I Press "Save" button 
Then    The Job   is created 
And     This "<Message>" is displaying  


Examples:
| Code     |  Job Category |Parameters             | Next job    |  Message       |
| codeJob  | Rating        |MQ_CHURN_SUB_PER_MONTH | FF_Job      |Element created |
