Feature: Creation of a new billing Account

   
   Background:  System is configured.
   
   Scenario: Create a new Billing Account 
   
Given  Field "Customer Account   " is filled in with a  customer account 
And    Field "Billing cycle" is filled in with a  Billing cycle
 And    Field  "Email" is filled in with a Email  in tab Contact information
 And    Field "Adress 1" is filled in with a Adress  in Tab Adress
 And    Field "Postal code" is filled in with a Postal code  in  Tab Adress
 And    Field "City " is filled in with a City  in  Tab Adress
 And    Field "Country" is filled in with a Country  in Tab Adress
And    Field "Tax Category" is filled in with a Tax   in Tab Tax Category
When   I Press  "Save" button 
Then   The billing account  is created 
And    I see success  message 

Scenario Outline: Create a new Billing Account 




Given   Field "Customer Account  " is filled in  with <Customer Account>
And     Field "Billing cycle"  is filled in with   <Billing cycle>
And    Field "Email" is filled in  with <Email>  in tab Contact  information
And    Field "Adress 1" is filled in with <Adress 1>  in Tab Adress
And    Field "Postal code" is filled in with <Postal code>  in Tab Adress
And    Field "City" is filled in with  <City>  in Tab Adress
And    Field "Country" is filled in with  <Country>  in Tab Adress
And    Field "Tax Category" is filled in with  <Tax Category>  in Tab Tax Category
When    I Press "Save" button 
Then    The billing account is created 

And     This "<Message>" is displaying  


Examples:
| Customer Account | Billing cycle          |  Email                      |  Adress 1              | Postal code|City | Country| Tax Category| Message       |
|    Ben.Ohara     | BC_CLASSIC_MONTHLY_1ST |   ben.ohara@opencellsoft.com|  14 rue Crespin du Gast| 12345      |Paris| France | TAXCAT3     |Element created |

