  -- crm customer procedure 
CREATE OR REPLACE PROCEDURE insert_kc_authorization(param_max_users integer, param_max_roles integer, param_max_se integer, param_step_to_run varchar)
language plpgsql
AS $$
declare 
    counter integer := 0;
	var_resource_server_id varchar :='';
	var_resource_owner_id varchar :='';
	var_created_timestamp bigint :=0;
	var_user_secret_data varchar :='';
	var_user_credential_data varchar :='';
	var_role_to_user_ratio int :=1;
	var_role_to_se_ratio int :=1;
	
	var_user_iterations integer := 1;
	var_role_iterations integer := 1;
	var_se_iterations integer := 1;
	var_resource_iterations integer := 1;
	var_schema_name varchar := 'public';
    var_rec record;
	var_first_user_id integer := null;
	var_first_role_id integer := null;
	var_first_se_id integer := null;

BEGIN


    IF param_step_to_run IS NULL THEN
	    delete from policy_config where policy_id like 'policy_%';
		delete from associated_policy where policy_id like 'permission_%';
	    delete from resource_policy where resource_id like 'resource_%';
	    delete from resource_server_resource where id like 'resource_%';
    	delete from resource_server_policy where id like 'policy_%' or id like 'permission_%';
    	delete from credential where user_id like 'user_%';
	    delete from user_role_mapping where user_id like 'user_%';
		delete from user_entity where id like 'user_%';
		delete from keycloak_role where id like 'role_%';
	END IF;
	
	select created_timestamp into var_created_timestamp from user_entity limit 1;
	select secret_data, credential_data into var_user_secret_data, var_user_credential_data from credential where user_id in (select id from user_entity where username='opencell.admin');
	select id into var_resource_server_id from resource_server limit 1;
	select id into var_resource_owner_id from user_entity where username='opencell.admin';

    
    -- "Role"
    
	IF param_step_to_run IS NULL or lower(param_step_to_run) ='role' THEN

		drop sequence if exists perf_counter_seq;
		create sequence perf_counter_seq start with 1;
		
		select ceil(log(2,param_max_roles)) into var_role_iterations;
	
		INSERT INTO keycloak_role(id, client_realm_constraint, client_role, name, realm_id)
		(select 'role_'||nextval('perf_counter_seq'),'opencell', false, 'perfRole.'||currval('perf_counter_seq'),'opencell');

        select currval('perf_counter_seq') into var_first_role_id;	   

		while counter < var_role_iterations loop
	            INSERT INTO keycloak_role(id, client_realm_constraint, client_role, name, realm_id)
					(select 'role_'||nextval('perf_counter_seq'),'opencell', false, 'perfRole.'||currval('perf_counter_seq'),'opencell' from keycloak_role where id like 'role_%');
	            counter := counter + 1;
		end loop;     
	
		-- Remove roles exceeding the number needed
	    delete from keycloak_role where cast(substring(id from 6) as integer) >= (var_first_role_id + param_max_roles) and id like 'role_%';
	  
	END IF;

	-- "User"
	
	IF param_step_to_run IS NULL or lower(param_step_to_run) ='user' THEN

		drop sequence if exists perf_counter_seq;
		create sequence perf_counter_seq start with 1;
	
		select ceil(log(2,param_max_users)) into var_user_iterations;
	
		INSERT INTO user_entity(id, enabled, realm_id, username, created_timestamp)
		(select 'user_'||nextval('perf_counter_seq'),true, 'opencell', 'perfUser.'||currval('perf_counter_seq'), var_created_timestamp);

        select currval('perf_counter_seq') into var_first_user_id;	   
        
        counter := 0;
		while counter < var_user_iterations loop
		
			INSERT INTO user_entity(id, enabled, realm_id, username, created_timestamp)
				(select 'user_'||nextval('perf_counter_seq'),true, 'opencell', 'perfUser.'||currval('perf_counter_seq'), var_created_timestamp from user_entity where id like 'user_%');
	            counter := counter + 1;
		end loop;     
	
		-- Remove users exceeding the number needed
	    delete from user_entity where cast(substring(id from 6) as integer) >= (var_first_user_id + param_max_users) and id like 'user_%';

	    -- Insert passwords
	    insert into credential (id, type, user_id, created_date, secret_data, credential_data, priority) 
	    	(select 'credential'||nextval('perf_counter_seq'),'password', id, var_created_timestamp, var_user_secret_data, var_user_credential_data, 10 from user_entity where id like 'user_%');
	    	    
	    -- Assign roles to a user
	    
	    counter := 1;
        
	    
	   	IF param_max_users>param_max_roles THEN
	    	
		    select param_max_users/param_max_roles into var_role_to_user_ratio;
	
		    while counter <= var_role_to_user_ratio loop		    
		        insert into user_role_mapping (role_id, user_id) (select id, 'user_'||cast(substring(id from 6) as integer)*counter from keycloak_role where id like 'role_%');
		        counter := counter + 1;
			end loop;
	    	
	    ELSE
	    	
		    select param_max_roles/param_max_users into var_role_to_user_ratio;
	    			    
		    while counter <= var_role_to_user_ratio loop
		        insert into user_role_mapping (role_id, user_id) (select 'role_'||cast(substring(id from 6) as integer)*counter, id from user_entity where id like 'user_%');
	    	    counter := counter + 1;
				
            end loop;
		    
	    
	    END IF;
	END IF;	
	
	
	
    
    -- "Authorization policy"
    
	IF param_step_to_run IS NULL or lower(param_step_to_run) ='policy' THEN
	
		INSERT INTO resource_server_policy(id, name, type, decision_strategy, logic, resource_server_id)
		(select 'policy_'||substring(id from 6),'Role '|| name, 'role', 1, 0, var_resource_server_id from keycloak_role where id like 'role_%');
	  
		INSERT INTO policy_config(policy_id, name, value) (select 'policy_'||substring(id from 6), 'roles', '[{"id":"'||id||'","required":false}]' from keycloak_role where id like 'role_%');
	END IF;
	

    -- "Authorization resources"
    
	IF param_step_to_run IS NULL or lower(param_step_to_run) ='resource' THEN

		drop sequence if exists perf_counter_seq;
		create sequence perf_counter_seq start with 1;
		
		select ceil(log(2,param_max_se)) into var_se_iterations;
	
		INSERT INTO resource_server_resource(id, name, owner, resource_server_id, owner_managed_access)
		(select 'resource_'||nextval('perf_counter_seq'),'SE:Seller:-'||currval('perf_counter_seq')||':SELLER_'||currval('perf_counter_seq')||':READ', var_resource_owner_id, var_resource_server_id, false);

        select currval('perf_counter_seq') into var_first_se_id;	   
        
        counter := 0;
        
		while counter < var_se_iterations loop
	       	INSERT INTO resource_server_resource(id, name, owner, resource_server_id, owner_managed_access)
				(select 'resource_'||nextval('perf_counter_seq'),'SE:Seller:-'||currval('perf_counter_seq')||':SELLER_'||currval('perf_counter_seq')||':READ', var_resource_owner_id, var_resource_server_id, false from resource_server_resource where id like 'resource_%');
			counter := counter + 1;
		end loop;     
	
		-- Remove resources exceeding the number needed
	    delete from resource_server_resource where cast(substring(id from 10) as integer) >= (var_first_se_id + param_max_se) and id like 'resource_%';
	  
	END IF;

    -- "Authorization permission"
    
	IF param_step_to_run IS NULL or lower(param_step_to_run) ='permission' THEN
	
		INSERT INTO resource_server_policy(id, name, type, decision_strategy, logic, resource_server_id)
		(select 'permission_'||substring(id from 10),'Permission '|| name, 'resource', 0, 0, var_resource_server_id from resource_server_resource where id like 'resource_%');
	  
		INSERT INTO resource_policy (resource_id, policy_id) (select id, 'permission_'||substring(id from 10) from resource_server_resource where id like 'resource_%');
		
		
		counter := 1;
        	    	
	    select param_max_se/param_max_roles into var_role_to_se_ratio;
    			    
	    while counter <= var_role_to_se_ratio loop
	    	INSERT INTO associated_policy (policy_id, associated_policy_id) (select 'permission_'||cast(substring(id from 8) as integer)*counter,id from resource_server_policy where id like 'policy_%');
		    counter := counter + 1;
	    end loop;
		
	END IF;


end$$;

CALL insert_kc_authorization(100,300,6000, null);

--update billing_billing_account set billing_cycle=3+(id%3);
--update billing_subscription set rating_group= id%3 



  
