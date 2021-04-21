README first :
To do performances tests on the 10.X version or Integration you need to follow these steps: 

1. Open postman (latest version better)
2. Import the collection "OPENCELL_PERF_TESTS_SUITE"
3. Import the env variables 
	3.1. "opencell-env-variables" : for the connexion variables
	3.2. "Workspace-variables" : for the env variables to add 
4. In "opencell-env-variables" update the url (opencell.base) with the server correct url 
5. In "Workspace-variables" update :
	5.1. NB_cust : the number of customers
	5.2.  NB_SUBS : the number of subscriptions
	5.3. CDRs_per_cust : the number of cdrs / 1 cust
	5.4. NBFiles : the number of files for the customers like the number of threads on the server
	5.5. FROM : if you want to start from a certain value $
	5.6. Add your email as EMAIL variable between "" (example : "mohamedali.hammal@opencellsoft.com"), to receive the test results on your email 
7. Lunch the collection and wait for the results in your email 