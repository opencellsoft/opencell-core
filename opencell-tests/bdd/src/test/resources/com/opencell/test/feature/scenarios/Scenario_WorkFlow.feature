Feature: Create a wotkflow history
   
   Background:  System is configured.
   
Scenario: Create a generic workflow

Given  Field "Code" is filled in with a Code
And    Field "Target entity class" is filled in with a Target entity class
When   I Press "Save" button 
Then   The workflow is created   
And    I see success  message 

Scenario: Create Workflow Statuses List

Given  A workflow is created 
And    Field "status Code" is filled in with a ST_Code
When   I Press "Save" button 
Then   A line on the list is added   

Scenario: Create Transitions on WF 

Given  Field "From Status " is filled in with a From_Status
And    Field "To Status" is filled in with a To_Status
And    Field "Condition EL" is filled in with a Condition_EL
And    Field "Description" is filled in with a Description
When   I Press "Save" button 
And    I Press "save" button
Then   The Transitions is created   

Scenario: Create Job on the WF

Given  Field "Job category " is filled in with a Job category
And    Field "Job type" is filled in with a Job type
And    Field "Code" is filled in with a Code
And    Field "Generic workflow" is filled in with a Generic workflow
When   I Press "Save" button 
And    I Press "Subscribe" button
Then   The job is created   

Scenario: Run the job on the WF

Given  The job is created
When   I Press "Execute" button 
Then   The job is executed   

Scenario: Create a generic workflow

Given  Field "Code" is filled in with a <Code>
And    Field "Target entity class" is filled in with a <Target entity class>
When   I Press "Save" button 
Then   The workflow is created   
And    I see success <message>

Scenario: Create Workflow Statuses List

Given  A workflow is created 
And    Field "status Code" is filled in with a <ST_Code>
When   I Press "Save" button 
Then   A line on the list is added   

Scenario: Create Transitions on WF

Given  Field "From Status " is filled in with a <From_Status>
And    Field "To Status" is filled in with a <To_Status>
And    Field "Condition EL" is filled in with a <Condition_EL>
And    Field "Description" is filled in with a <Description>
When   I Press "Save" button 
And    I Press "save" button
Then   The Transitions is created   

Scenario: Create Job on the WF

Given  Field "Job category " is filled in with a <Job category>
And    Field "Job type" is filled in with a <Job type>
And    Field "Code" is filled in with a <Job_Code>
And    Field "Generic workflow" is filled in with a <Generic workflow>
When   I Press "Save" button 
And    I Press "Subscribe" button
Then   The job is created   

Scenario Outline: Run the job on the WF

Given  The job is created
When   I Press "Execute" button 
Then   The job is executed   

Examples: 
| Code   |Target entity class |            message           |ST_Code|From_Status| To_Status | Condition_EL |     Description        |Job category|    Job type      |Job_Code|Generic workflow|
| WF_TEST|     Sub_TEST_1     | Workflow created successfully|   T0  | T0        | T1        |  #{true}     |transition form T0 to T1|     Misc   |GenericWorkflowJob|Test_Job|    WF_TEST     |
