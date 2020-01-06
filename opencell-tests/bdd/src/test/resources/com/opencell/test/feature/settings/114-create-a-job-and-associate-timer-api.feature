Feature: Creation of a new Job on Admin 


    Background: The system is configured

Given  The API are up and running for  "https://api.opencellsoft.com/integration/"
When A user performs request to "/job/createOrUpdate"
And a valid Authorization token is "Basic Auth"
And request operation is "Post"
Then I get a "200" response
And i get a message


Scenario Outline: Create a new Job

Given  The API are up and running for  "https://api.opencellsoft.com/integration/"
When A user performs request to "/job/createOrUpdate"
And request operation is "Post"
And the "body" is filled in with a <body>
Then I get a "200" response
And the "<response>" is displaying 
    """
      {
    "status": "SUCCESS",
    "message": ""
      }
    """
Examples:
| body                      | response |
| {"jobTemplate" : "...", 	    "code" : "....",	    "timerCode" : "...",	    "jobCategory" : "...",	    "parameter" : "...",	    "active" : "true"      }                      |  """      {    "status": "SUCCESS",    "message": ""      }    """  |
  
