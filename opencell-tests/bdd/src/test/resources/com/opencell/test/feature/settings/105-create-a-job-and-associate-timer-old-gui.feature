Feature: Creation of a new Job and associate timer  on opencell Admin

   Background: The system is configured 
  
Given    Filled in "Job category" with a Job category
And      Filled in "Job type" with a job type 
And      Filled in "Code" with a code 
And      Filled in "Timer" with a timer 
When     I Press  "Save" button 
Then     The job instance is created 
And      I see success  message 


Scenario Outline: Create a new job instance

Given   Field "Job category" is filled in  with <Job category>
And     Field "Job type " is filled in with <Job type> 
And     Field "code" is filled in with <code>
And     Field "Timer" is filled in with <Timer> 
When    I Press  "Save" button 
Then    The job instance  is created 
And     This "<Message>" is displaying

Examples:
| Job category         | Job type                      | code              | Timer   | Message                        |
| account receivables  | PaymentScheduleProcessingJob  | JobInstance_test  | Monthly |Entity was created successfully!|