@ignore
Feature: Manipulation on MarketingManger module
   
   Background:  System is configured.
   
Scenario: Create an offer

Given  Field "Name" is updated with a new offerName
And    Field "Offer code" is updated with a new OfferCode 
And    Instantiate  "Services" as ServiceToInclude
When   I Press  "Save" button 
Then   A new offer is created 
And    I see success  message 

Scenario: Create a product

Given  Field "Name" is updated with a new ProductName
And    Field "Offer code" is updated with a new ProductCode 
When   I Press  "Save" button 
Then   A new product is created 
And    I see success  message 

Scenario: Create a bundle

Given  Field "Name" is filled in with a new BundletName
And    Field "Offer code" is updated with a new BundletCode 
And    Field "Status" is filled in with a status
When   I Press  "Add Product/Bundle to Bundle" button 
And    I select a Bundle
And    I Press  "Add Product/Bundle to Bundle" button on actions tab
When   I Press  "Save" button 
Then   A new bundle is created 
And    I see success  message

Scenario Outline: Create an offer

Given  Field "Name" is updated with a new <offerName>
And    Field "Offer code" is updated with a new <OfferCode>
And    Instantiate  "Services" as <ServiceToInclude>
When   I Press  "Save" button
Then   A new offer is created
And    I see success  <message>
   Examples:
      | offerName                  |  OfferCode                 |ServiceToInclude         |   message                  |ProductName|ProductCode|BundletName|BundletCode|status|Bundle         |
      |OF_CLASSIC_SUPER_COMMISSION1|OF_CLASSIC_SUPER_COMMISSION1|Commissioning for Generic|Element created successfully|PR_TEST1   | PR_TEST1  |BD_TEST    |BD_TEST    |Active|Default product|

   Scenario Outline: Create a product

Given  Field "Name" is updated with a new <ProductName>
And    Field "Offer code" is updated with a new <ProductCode>
When   I Press  "Save" button
Then   A new product is created
And    I see success  <message>
   Examples:
      | offerName                  |  OfferCode                 |ServiceToInclude         |   message                  |ProductName|ProductCode|BundletName|BundletCode|status|Bundle         |
      |OF_CLASSIC_SUPER_COMMISSION1|OF_CLASSIC_SUPER_COMMISSION1|Commissioning for Generic|Element created successfully|PR_TEST1   | PR_TEST1  |BD_TEST    |BD_TEST    |Active|Default product|

   Scenario Outline: Create a bundle

Given  Field "Name" is filled in with a new <BundletName>
And    Field "Offer code" is updated with a new <BundletCode>
And    Field "Status" is filled in with a <status>
When   I Press  "Add Product/Bundle to Bundle" button 
And    I select a <Bundle>
And    I Press  "Add Product/Bundle to Bundle" button on actions tab
When   I Press  "Save" button 
Then   A new bundle is created 
And    I see success  <message>

Examples:
| offerName                  |  OfferCode                 |ServiceToInclude         |   message                  |ProductName|ProductCode|BundletName|BundletCode|status|Bundle         |
|OF_CLASSIC_SUPER_COMMISSION1|OF_CLASSIC_SUPER_COMMISSION1|Commissioning for Generic|Element created successfully|PR_TEST1   | PR_TEST1  |BD_TEST    |BD_TEST    |Active|Default product|
