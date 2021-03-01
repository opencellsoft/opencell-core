1. Add the script to the server
2. Modify the email receiver 
3. Save & compile the script 
4. Add the job : 
	4.1. Manually : using the gui 
	4.2. using this postgresql request : 
INSERT INTO "meveo_job_instance" ("id", "version", "disabled", "created", "updated", "code", "description", "job_category", "job_template", "parametres", "creator", "updater", "timerentity_id", "uuid", "run_on_nodes", "single_node", "cf_values") VALUES
(-31,    38,     '0',    '2018-03-25 09:53:00.489',      '2018-03-25 14:27:28.227',      'SendRresults',   NULL,   'MEDIATION',    'ScriptingJob', NULL,   'opencell.superadmin',  'opencell.superadmin',  NULL,   '48091cb7-eebe-4acc-ae59-57842ab5e220', NULL,   1,      '{"ScriptingJob_script":[{"entity":{"classname":"org.meveo.model.scripts.ScriptInstance","code":"org.meveo.service.script.MailNotificationScript"}}]}');
