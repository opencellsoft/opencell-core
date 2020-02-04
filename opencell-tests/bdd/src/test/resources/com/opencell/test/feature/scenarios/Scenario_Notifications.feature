@ignore
Feature: Create a wotkflow history
   
   Background:  System is configured.

Scenario: Create a script

Given  Field "Script source" is filled in with a Script_Source
When   I Press "Validate/Compile" button 
And    I Press "Save" button
Then   The script is compiled and saved   
And    I see success  message 

Scenario: Create a Notification

Given  Field "Code" is filled in with a Code
And    Field "Classname filter" is filled in with a Classname_Filter
And    Field "Event type filter" is filled in with an Event_type
And    Field "Script instance" is filled in with a Script instance
When   I Press "Save" button 
Then   The workflow is created   
And    I see success  message 

Scenario: Create a seller 

Given  Field "code" is filled in with a seller_code
When   I Press "Save" button 
Then   A seller is  created  

Scenario: Create a script

Given  Field "Script source" is filled in with a <Script_Source>
When   I Press "Validate/Compile" button 
And    I Press "Save" button
Then   The script is compiled and saved   
And    I see success  <message> 

Scenario: Create a Notification

Given  Field "Code" is filled in with a <Code>
And    Field "Classname filter" is filled in with a <Classname_Filter>
And    Field "Event type filter" is filled in with an <Event_type>
And    Field "Script instance" is filled in with a <Script instance>
When   I Press "Save" button 
Then   The workflow is created   
And    I see success  <message>

Scenario Outline: Create a seller

Given  Field "code" is filled in with a seller_code
When   I Press "Save" button 
Then   A seller is  created  
And    A notification is sent on logs    

Examples: 
| Script_Source |Code|Classname_Filter|Event_type |Script instance|message| 
#|
#package org.meveo.service.script;

#import java.util.Map;

#import org.meveo.admin.exception.BusinessException;
#import org.meveo.model.crm.Customer;
#import org.meveo.service.crm.impl.CustomFieldInstanceService;
#import org.meveo.service.crm.impl.CustomerService;
#import org.meveo.service.script.module.ModuleScript;
#import org.slf4j.Logger;
#import org.slf4j.LoggerFactory;

#public class Test4152 extends org.meveo.service.script.Script {

#    @Override
#    public void execute(Map<String, Object> methodContext) throws BusinessException {
 #       log.info("Test of notification");

#    }
#}
#|Test_Notif|org.meveo.model.admin.Seller|Created|org.meveo.dynamics.script.TestScript|Entity was created successfully|
