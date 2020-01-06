Feature: Creation of a new billing cycle

   Background:  System is configured.
   
   Scenario: Create a new Billing cycle 
   
Given  Field "Code" is filled in with a code  
And    Field "Calendar" is filled in with a calendar 
And    Field "Invoice date delay" is filled in with a invoice date delay in tab invoices 
And    Field "Due date delay" is filled in with a Due date delay in Tab invoices 
And    Field "Reference date" is filled with Reference date in tab invoices
When   I Press "Save" button 
Then   The billing cycle is created 
And    I see success message 

   Scenario Outline: Create a new Billing cycle 

Given   Field "Code" is filled in  with <Code>
And     Field "Calendar" is filled in  with <Calendar>  
And     Field "Invoice date delay" is filled in with <Invoice date delay> in tab invoices
And     Field "Due date delay" is filled in with <Due date delay> in tab invoices
And     Field "Reference date" is filled with <Reference date> in tab invoices
When    I Press "Save" button 
Then    This <Message> is displaying 


Examples:
|Code                   |Calendar                |Transaction date delay|Invoice date delay|Due date delay|Reference date|Message                                    |
|BC_CLASSIC_MONTHLY_1ST |CAL_CLASSIC_MONTHLY_1ST |2                     |2                 |30            |Today         |Element created                            |
|BC_CLASSIC_MONTHLY_1ST |CAL_CLASSIC_MONTHLY_1ST |2                     |5                 |1             |Today         |Invoice date delay must be < Due date delay|