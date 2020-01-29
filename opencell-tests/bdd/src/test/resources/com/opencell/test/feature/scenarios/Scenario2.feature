@ignore
Feature: Generate a pro-forma on CC module
   
   Background:  System is configured.
   Given customer hierarchy is created
   And   the customer is subscribed to the offer "Generic with multiple basic service"
   And   some services are activated
   

Scenario: Create a many subscriptions 

Given  Field "offer " is filled in with an offer
And    Field "code" is filled in with a SubscriptionCode
And    Field "subscription date" is filled in with a subscription date
And    Field "Quantity" is filled in with an Quantity  
And    Field "One-off price" is filled in with a One-off price
And    Field "Trigger commissioning" is filled in with a valueOfList
When   I Press "Activate" button 
And    I Press "Subscribe" button
Then   The Subscription is created   
And    I see success  message 

