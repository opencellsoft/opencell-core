  -- crm customer procedure 
CREATE OR REPLACE PROCEDURE insert_crm_customer(param_max_customers integer, param_step_to_run varchar)
language plpgsql
AS $$
declare 
    counter integer := 0;
	var_seller_id integer := 15;
	var_customer_brand_id integer :=0;
	var_customer_category_id integer := 0;
	var_billing_cycle_id integer := 0;
	var_offer_id integer :=0;
	var_offer_code varchar :='OF_BASIC';
	var_oss_charge_id integer :=0;
	var_oss_charge_code varchar := 'ISCAT_OSS';
	var_ost_charge_id integer :=0;
	var_rec_charge_id integer :=0;
	var_rec_charge_code varchar := 'ISCAT_REC';
	var_usg_charge_id integer :=0;
	var_service_template_id integer :=0;
	var_trading_country_id integer :=0;
	var_trading_language_id integer :=0;
	var_trading_currency_id integer :=0;
	var_tax_id integer := 0;
	var_tax_class_id integer := 0;
	var_tax_cat_id integer := 0;
	var_currency_id integer := 0;
	var_invoice_subcat_id integer :=0;
	var_foo integer :=0;
	var_bc_id integer := 1;
	var_title_id integer := 0;
	var_rec_cal_id integer := 0;
	var_customer_iterations integer := 1;
	var_max_ae_id integer := 1;
    var_schema_name varchar := 'public';
    var_rec record;
	var_first_customer_id integer := null;

BEGIN
	for var_rec IN (
		SELECT table_name FROM information_schema.tables WHERE table_schema = var_schema_name and table_type='BASE TABLE'
	
	)  LOOP
	       EXECUTE format ('ALTER TABLE %I DISABLE TRIGGER ALL',var_rec.table_name );
	
	  END LOOP;


    IF param_step_to_run IS NULL THEN
	 	--update billing_invoice set recorded_invoice_id = null;
		delete from ar_account_operation;
		delete from billing_rated_transaction;
		delete from billing_invoice_agregate;
		delete from billing_invoice;
		delete from billing_wallet_operation;
		delete from billing_chrginst_wallet;
		delete from BILLING_CHARGE_INSTANCE;
		delete from billing_service_instance;
		delete from medina_access;
		delete from rating_cdr;
		delete from rating_edr;
		delete from billing_subscription;
		--update billing_user_account set wallet_id=null;
		delete from billing_wallet;
		delete from billing_user_account;
		delete from billing_billing_account ;
		delete from ar_payment_token;
		delete from ar_customer_account;
		delete from crm_customer;	
		delete from job_execution;
		delete from billing_billing_run;
		delete from audit_log;
	END IF;
	
	select max(id) into var_seller_id from crm_seller;
	select id into var_offer_id from cat_offer_template where code='OF_BASIC';
	select id into var_title_id from adm_title where code='MR';
	select id into var_service_template_id from cat_service_template where code='SE_OSS_OST_USG_RECU';
	select id into var_oss_charge_id from cat_charge_template where code='CH_OSS';
	select id into var_ost_charge_id from cat_charge_template where code='CH_OST';
	select id into var_rec_charge_id from cat_charge_template where code='CH_REC_BUILD_RUN_ADV';
	select id into var_usg_charge_id from cat_charge_template where code='CH_USG_UNIT';
	select max(id) into var_trading_currency_id from billing_trading_currency;
	select max(id) into var_trading_country_id from billing_trading_country;
	select max(id) into var_trading_language_id from billing_trading_language;
	select id into var_tax_id from billing_tax where code='TAX_10';
	select id into var_tax_cat_id from billing_tax_category where code='REGULAR';
	select id into var_tax_class_id from billing_tax_class where code='NORMAL';
	select id into var_currency_id from adm_currency where currency_code='EUR';
	select id into var_bc_id from billing_cycle where code='INV_MT_1';
	select id into var_customer_category_id from crm_customer_category where code='CLIENT';
	select id into var_customer_brand_id from crm_customer_brand where code='DEFAULT';

	-- select max(id) into var_invoice_subcat_id from billing_invoice_sub_cat;
	select id into var_rec_cal_id from cat_calendar where code='CAL_MONTHLY_1ST';

    
    -- "Customer"
    
	IF param_step_to_run IS NULL or lower(param_step_to_run) ='customer' THEN

		select ceil(log(2,param_max_customers)) into var_customer_iterations;
	
		INSERT INTO crm_customer(id, version, created, updated, code, description, address_1, address_2, address_3, address_city, address_country_id, address_state, address_zipcode, default_level, external_ref_1, external_ref_2, firstname, lastname, provider_contact, creator, updater, title_id, primary_contact, uuid, bam_id, cf_values, job_title, email, fax, mobile, phone, vat_no, registration_no, minimum_amount_el, minimum_label_el, minimum_charge_template_id, customer_brand_id, customer_category_id, seller_id, additional_details_id, address_book_id,minimum_target_account_id,invoicing_threshold, check_threshold,threshold_per_entity)
		(select nextval('account_entity_seq'),0,now(),null,'cust_'||currval('account_entity_seq'),'Demo Distributor',null,null,null,null,null,null,null,1,null,null,'firstname_'||currval('account_entity_seq'),'lastname_'||currval('account_entity_seq'),null,null,null,var_title_id,null,'cust_'||currval('account_entity_seq')||currval('account_entity_seq'),null,null,null,null,null,null,null,null,null,null,null,null,var_customer_brand_id, var_customer_category_id, var_seller_id, null, null, null, null, null, 0);

        select currval('account_entity_seq') into var_first_customer_id;	   

		while counter < var_customer_iterations loop
	            INSERT INTO crm_customer(id, version, created, updated, code, description, address_1, address_2, address_3, address_city, address_country_id, address_state, address_zipcode, default_level, external_ref_1, external_ref_2, firstname, lastname, provider_contact, creator, updater, title_id, primary_contact, uuid, bam_id, cf_values, job_title, email, fax, mobile, phone, vat_no, registration_no, minimum_amount_el, minimum_label_el, minimum_charge_template_id, customer_brand_id, customer_category_id, seller_id, additional_details_id, address_book_id,minimum_target_account_id,invoicing_threshold, check_threshold,threshold_per_entity)
                        (select nextval('account_entity_seq'),0,now(),null,'cust_'||currval('account_entity_seq'),'Demo Distributor',null,null,null,null,null,null,null,1,null,null,'firstname_'||currval('account_entity_seq'),'lastname_'||currval('account_entity_seq'),null,null,null,var_title_id,null,'cust_'||currval('account_entity_seq')||currval('account_entity_seq'),null,null,null,null,null,null,null,null,null,null,null,null,var_customer_brand_id, var_customer_category_id, var_seller_id, null, null, null, null, null, 0 from crm_customer);
	        
	            counter := counter + 1;
		end loop;     
	
		-- Remove customers exceeding the number needed
	    delete from crm_customer where id >= (var_first_customer_id + param_max_customers);

	    select setval('account_entity_seq', (select max(id)+1 from crm_customer), false) into var_foo;
	  
	END IF;


	-- "Customer account"

	IF param_step_to_run IS NULL or lower(param_step_to_run) ='ca' THEN
		
		INSERT INTO ar_customer_account(id, version, created, updated, code, description, address_1, address_2, address_3, address_city, address_country_id, address_state, address_zipcode, default_level, external_ref_1, external_ref_2, firstname, lastname, provider_contact, creator, updater, title_id, primary_contact, uuid, bam_id, cf_values, job_title, email, fax, mobile, phone, vat_no, registration_no, minimum_amount_el, minimum_label_el, minimum_charge_template_id, date_dunning_level, date_status, dunning_level, pswd, status, customer_id, trading_currency_id, trading_language_id, credit_category_id, due_date_delay_el, excluded_from_payment, crm_address_book_id, minimum_target_account_id, invoicing_threshold, check_threshold, threshold_per_entity) 
		(select nextval('account_entity_seq'),0,now(),null,'ca_'||currval('account_entity_seq'),'Demo Distributor',null,null,null,null,null,null,null,1,null,null,'firstname_'||currval('account_entity_seq'),'lastname_'||currval('account_entity_seq'),null,null,null,var_title_id,null,'ca_'||currval('account_entity_seq')||currval('account_entity_seq'),null,null,null,null,null,null,null,null,null,null,null,null,NOW(),NOW(),'R0','password','ACTIVE',id,var_trading_currency_id,var_trading_language_id,null,null,0,null,null,null,null,0 from crm_customer);
	
	  	INSERT INTO ar_payment_token (id, version, created, updated, disabled, alias, is_default, bank_name, bank_code ,account_number,iban, bic,mandate_date,mandate_identification, customer_account_id, token_type)
		(select nextval('ar_payment_token_seq'), 0, now(), now(), 0, 'SEPA', 1,'Some Bank','12456','...','FR123456789123456789','BDNFR123456',TO_DATE('2020-01-10','YYYY-MM-DD'),'G', ca.id, 'DIRECTDEBIT' from ar_customer_account ca); 

	END IF;

	-- "Billing account"
	
	IF param_step_to_run IS NULL or lower(param_step_to_run) ='ba' THEN
			
		INSERT INTO billing_billing_account(id, version, created, updated, code, description, address_1, address_2, address_3, address_city, address_country_id, address_state, address_zipcode, default_level, external_ref_1, external_ref_2, firstname, lastname, provider_contact, creator, updater, title_id, primary_contact, uuid, bam_id, cf_values, job_title, email, fax, mobile, phone, vat_no, registration_no, minimum_amount_el, minimum_label_el, minimum_charge_template_id, br_amount_with_tax, br_amount_without_tax, discount_rate, electronic_billing, invoice_prefix, next_invoice_date, status, status_date, subscription_date, termination_date, billing_cycle, billing_run, customer_account_id, termin_reason_id, trading_country_id, trading_language_id, invoicing_threshold, mailing_type, cced_emails, email_template_id, minimum_invoice_sub_category_id, tax_category_id, check_threshold, payment_method_id, threshold_per_entity) 	
		(select nextval('account_entity_seq'),0,now(),null,'ba_'||currval('account_entity_seq'),'Demo Distributor',null,null,null,null,null,null,null,1,null,null,null,null,null,null,null,null,null,'ba_'||currval('account_entity_seq')||currval('account_entity_seq'),null,null,null,null,null,null,null,null,null,null,null,null, null, null, null, 0, 'test', now() ,'ACTIVE', NOW(), TO_DATE('2021-03-01','YYYY-MM-DD'), null, var_bc_id, null, id, null, var_trading_country_id, var_trading_language_id, null, null, null, null, null, var_tax_cat_id, null, null, 0 from ar_customer_account);

	END IF;

	-- "User account"
	
	IF param_step_to_run IS NULL or lower(param_step_to_run) ='ua' THEN
			
		
		INSERT INTO billing_user_account(id, version, created, updated, code, description, address_1, address_2, address_3, address_city, address_country_id, address_state, address_zipcode, default_level, external_ref_1, external_ref_2, firstname, lastname, provider_contact, creator, updater, title_id, primary_contact, uuid, bam_id, cf_values, job_title, email, fax, mobile, phone, vat_no, registration_no, minimum_amount_el, minimum_label_el, minimum_charge_template_id, status, status_date, subscription_date, termination_date, billing_account_id, termin_reason_id, wallet_id) 
		(select nextval('account_entity_seq'),0,now(),null,'ua_'||currval('account_entity_seq'),'Demo Distributor',null,null,null,null,null,null,null,1,null,null,null,null,null,null,null,null,null,'ua_'||currval('account_entity_seq')||currval('account_entity_seq'),null,null,null,null,null,null,null,null,null,null,null,null, 'ACTIVE', NOW(), NOW(), null, id, null, currval('account_entity_seq') from billing_billing_account);
		
		INSERT INTO billing_wallet(id,version,created, updated, code, description,user_account_id)
			(select id, 0, now(), now(), 'PRINCIPAL',null, id from billing_user_account);
	
		select setval('account_entity_seq', (select max(id)+1 from billing_wallet), false) into var_foo;

	END IF;
	
	-- "Subscriptions"

	IF param_step_to_run IS NULL or lower(param_step_to_run) ='subscription' THEN
	
		insert into billing_subscription (id, version, created, updated, code, description, subscription_date, user_account_id, uuid, seller_id, offer_id, auto_renew, initial_term_type, renewal_term_type, status, status_date)
		(select nextval('billing_subscription_seq'), 0, now(), now(), 'sub_'||currval('billing_subscription_seq'), null, date_trunc('month', now()) - interval '1 month', ua.id,'sub_'||currval('billing_subscription_seq'),var_seller_id,var_offer_id,0,'RECURRING','RECURRING','ACTIVE', now() from billing_user_account ua);

		insert into medina_access (id, version, disabled, acces_user_id, end_date, start_date, subscription_id, uuid, creator, updater, created, updated, cf_values)
		(select nextval('medina_access_seq'), 0, 0, 'acc_'||currval('medina_access_seq'), null, null, sub.id, 'acc_'||currval('medina_access_seq'), null, null, now(), null, null from billing_subscription sub );

	END IF;
	
	-- "Service instantiation" 

	IF param_step_to_run IS NULL or lower(param_step_to_run) ='service' THEN

		INSERT INTO BILLING_SERVICE_INSTANCE (id, version,description, created, code, quantity, status, status_date, subscription_date, service_template_id, subscription_id, uuid, cf_values) 
		(select nextval('billing_service_instance_seq'),0,null, NOW(),'SE_OSS_OST_USG_RECU', 2, 'ACTIVE', now(),date_trunc('month', now()) - interval '1 month',var_service_template_id,id,'serv'||currval('billing_service_instance_seq'),null from billing_subscription);

	END IF;

	-- For subscription charge:

	IF param_step_to_run IS NULL or lower(param_step_to_run) ='subscription_charge' THEN
		
		INSERT INTO BILLING_CHARGE_INSTANCE(id, version, created, code, description,  is_prepaid, status, status_date, charge_template_id, trading_country, trading_currency, seller_id, subscription_id, user_account_id, charge_type, quantity, service_instance_id, uuid, calendar_id, apply_in_advance, subscription_date, charge_date, next_charge_date, charged_to_date, priority)
		(select nextval('billing_charge_instance_seq'), 0, NOW(), 'CH_OSS', null,  0, 'CLOSED', NOW(), var_oss_charge_id,var_trading_country_id, var_trading_currency_id,var_seller_id,sub.id, sub.user_account_id,'S', 2,  serv.id, 'ch_'||currval('billing_charge_instance_seq'), null, 1,sub.subscription_date, sub.subscription_date, null, null, 1 from BILLING_SERVICE_INSTANCE serv join billing_subscription sub on serv.subscription_id=sub.id);
	
		-- For Subscription charge wallet operation
		select id into var_invoice_subcat_id from billing_invoice_sub_cat where code = var_oss_charge_code;
		
		insert into billing_wallet_operation (id, version, accounting_code_id, amount_tax, amount_with_tax, amount_without_tax, cf_values, charge_instance_id, code, counter_id, created, currency_id, description, edr_id, end_date, input_quantity, input_unit_description, input_unitofmeasure, invoice_sub_category_id, invoicing_date, offer_code, offer_id, operation_date, order_number, parameter_1, parameter_2, parameter_3, parameter_extra, priceplan_id, quantity, rated_transaction_id, rating_unit_description, rating_unitofmeasure, raw_amount_with_tax, raw_amount_without_tax, reject_reason, reratedWalletOperation_id, seller_id, service_instance_id, sort_index, start_date, status, subscription_id, subscription_date, tax_id, tax_class_id, tax_percent, credit_debit_flag, unit_amount_tax, unit_amount_with_tax, unit_amount_without_tax, updated, uuid, wallet_id, operation_type, user_account_id, billing_account_id) 
		(select nextval('billing_wallet_operation_seq'), 0, null, 2, 12, 10, null, ch.id, ch.code, null, now(), var_currency_id, ch.description, null, null, null, null, null, var_invoice_subcat_id, null, var_offer_code, var_offer_id, date_trunc('month', now()) - interval '1 month', null, null, null, null, null, null, ch.quantity, null, null, null, null, null, null, null, var_seller_id, ch.service_instance_id, 0, null, 'OPEN', ch.subscription_id, ch.subscription_date, var_tax_id, var_tax_class_id, 0, 'DEBIT', 1, 6, 5, null, 'wo_'||currval('billing_wallet_operation_seq'),ch.user_account_id, 'W', ch.user_account_id, ua.billing_account_id from billing_charge_instance ch join billing_user_account ua on ch.user_account_id=ua.id where ch.code = 'CH_OSS');


	END IF;

	-- For recurring charge: 

	IF param_step_to_run IS NULL or lower(param_step_to_run) ='recurring_charge' THEN
		
		INSERT INTO BILLING_CHARGE_INSTANCE(id, version, created, code, description,  is_prepaid, status, status_date, charge_template_id, trading_country, trading_currency, seller_id, subscription_id, user_account_id, charge_type, quantity, service_instance_id, uuid, calendar_id, apply_in_advance,  subscription_date, charge_date, next_charge_date, charged_to_date, priority)
		(select nextval('billing_charge_instance_seq'), 0, NOW(), 'CH_REC_BUILD_RUN_ADV', null,  0, 'ACTIVE', NOW(), var_rec_charge_id,var_trading_country_id, var_trading_currency_id,var_seller_id,sub.id, sub.user_account_id,'R', 2,  serv.id, 'ch_'||currval('billing_charge_instance_seq'), var_rec_cal_id, 1,sub.subscription_date, null, null, null, 1 from BILLING_SERVICE_INSTANCE serv join billing_subscription sub on serv.subscription_id=sub.id);
	
		-- For recurring charge wallet operation
		-- select id into var_invoice_subcat_id from billing_invoice_sub_cat where code = var_rec_charge_code;
			
		insert into billing_wallet_operation (id, version, accounting_code_id, amount_tax, amount_with_tax, amount_without_tax, cf_values, charge_instance_id, code, counter_id, created, currency_id, description, edr_id, end_date, input_quantity, input_unit_description, input_unitofmeasure, invoice_sub_category_id, invoicing_date, offer_code, offer_id, operation_date, order_number, parameter_1, parameter_2, parameter_3, parameter_extra, priceplan_id, quantity, rated_transaction_id, rating_unit_description, rating_unitofmeasure, raw_amount_with_tax, raw_amount_without_tax, reject_reason, reratedWalletOperation_id, seller_id, service_instance_id, sort_index, start_date, status, subscription_id, subscription_date, tax_id, tax_class_id, tax_percent, credit_debit_flag, unit_amount_tax, unit_amount_with_tax, unit_amount_without_tax, updated, uuid, wallet_id, operation_type, user_account_id, billing_account_id) 
		(select nextval('billing_wallet_operation_seq'), 0, null, 2, 12, 10, null, ch.id, ch.code, null, now(), var_currency_id, ch.description, null, null, null, null, null, var_invoice_subcat_id, null, var_offer_code, var_offer_id, date_trunc('month', now()) - interval '1 month', null, null, null, null, null, null, ch.quantity, null, null, null, null, null, null, null, var_seller_id, ch.service_instance_id, 0, null, 'OPEN', ch.subscription_id, ch.subscription_date, var_tax_id, var_tax_class_id, 0, 'DEBIT', 1, 6, 5, null, 'wo_'||currval('billing_wallet_operation_seq'),ch.user_account_id, 'W', ch.user_account_id, ua.billing_account_id from billing_charge_instance ch join billing_user_account ua on ch.user_account_id=ua.id  where ch.code = 'CH_REC_BUILD_RUN_ADV');
 
	END IF;

    -- For usage charge:

	IF param_step_to_run IS NULL or lower(param_step_to_run) ='usage_charge' THEN
	
		INSERT INTO BILLING_CHARGE_INSTANCE(id, version, created, code, description,  is_prepaid, status, status_date, charge_template_id, trading_country, trading_currency, seller_id, subscription_id, user_account_id, charge_type, quantity, service_instance_id, uuid, calendar_id, apply_in_advance,  subscription_date, charge_date, next_charge_date, charged_to_date, priority)
		(select nextval('billing_charge_instance_seq'), 0, NOW(), 'CH_USG_UNIT', null,  0, 'ACTIVE', NOW(), var_usg_charge_id,var_trading_country_id, var_trading_currency_id,var_seller_id,sub.id, sub.user_account_id,'U', 2,  serv.id, 'ch_'||currval('billing_charge_instance_seq'), null, 1,sub.subscription_date, sub.subscription_date, null, null, 1 from BILLING_SERVICE_INSTANCE serv join billing_subscription sub on serv.subscription_id=sub.id);

	END IF;
	
	-- For termination charge:
	
	IF param_step_to_run IS NULL or lower(param_step_to_run) ='termination_charge' THEN	

		INSERT INTO BILLING_CHARGE_INSTANCE(id, version, created, code, description,  is_prepaid, status, status_date, charge_template_id, trading_country, trading_currency, seller_id, subscription_id, user_account_id, charge_type, quantity, service_instance_id, uuid, calendar_id, apply_in_advance,  subscription_date, charge_date, next_charge_date, charged_to_date, priority)
		(select nextval('billing_charge_instance_seq'), 0, NOW(), 'CH_OST', null,  0, 'INACTIVE', NOW(), var_ost_charge_id,var_trading_country_id, var_trading_currency_id,var_seller_id,sub.id, sub.user_account_id,'T', 2,  serv.id, 'ch_'||currval('billing_charge_instance_seq'), null, 1, sub.subscription_date, sub.subscription_date, null, null, 1 from BILLING_SERVICE_INSTANCE serv join billing_subscription sub on serv.subscription_id=sub.id);
	
	END IF;


	for var_rec IN (
		SELECT table_name FROM information_schema.tables WHERE table_schema = var_schema_name and table_type='BASE TABLE'
	
	)  LOOP
	       EXECUTE format ('ALTER TABLE %I ENABLE TRIGGER ALL',var_rec.table_name );
	
	  END LOOP;


end$$;


  
