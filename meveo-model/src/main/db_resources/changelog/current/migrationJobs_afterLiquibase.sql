INSERT INTO meveo_timer
(id ,version ,sc_d_o_month ,sc_d_o_week ,sc_end ,sc_hour ,sc_min ,sc_month ,
  sc_sec ,sc_start ,sc_year ,provider_id ,code ,created ,creator_id ,description ,
  disabled ,updated ,updater_id 
)
SELECT id,version,sc_d_o_month ,sc_d_o_week ,sc_end ,sc_hour ,sc_min ,sc_month ,
  sc_sec ,sc_start ,sc_year ,provider_id, 'code_'||id,current_timestamp,1,'',FALSE,null,null
FROM temp_meveo_timer;



INSERT INTO meveo_job_instance
(id ,version ,disabled ,created ,updated ,code ,description ,
  active,job_category ,job_template ,parametres ,user_id ,
  provider_id ,creator_id ,updater_id ,timerentity_id 
)
SELECT id,version,FALSE ,current_timestamp ,null ,name ,'' ,
active , job_category ,job_name ,parametres ,userid, provider_id,1,null,id
FROM temp_meveo_timer;



INSERT INTO  crm_custom_field_inst (
   id, version ,disabled ,created ,updated ,code ,description ,
  date_value ,double_value ,long_value,string_value ,provider_id,
  creator_id,updater_id,access_id,account_id,charge_template_id,
  offer_template_id,service_template_id,subscription_id,
  versionable ,job_instance_id)
SELECT   id, version ,disabled ,created ,updated ,code ,description ,
  date_value ,double_value ,long_value,string_value ,provider_id,
  creator_id,updater_id,access_id,account_id,charge_template_id,
  offer_template_id,service_template_id,subscription_id,
  FALSE ,meveo_timer_id
  FROM temp_crm_custom_field_inst;
  
  drop table temp_meveo_timer;
  drop table temp_crm_custom_field_inst;


SELECT setval('meveo_timer_seq', max(id)+1) FROM meveo_timer;
SELECT setval('meveo_job_instance_seq', max(id)+1) FROM meveo_job_instance;
SELECT setval('CRM_CUSTOM_FIELD_INST_SEQ', max(id)+1) FROM CRM_CUSTOM_FIELD_INST;
--ALTER SEQUENCE meveo_timer_seq RESTART WITH 20;
--ALTER SEQUENCE meveo_job_instance_seq RESTART WITH 30;
--ALTER SEQUENCE CRM_CUSTOM_FIELD_INST_SEQ RESTART WITH 30;
 
  
  --ALTER SEQUENCE meveo_timer_seq RESTART WITH select max(id)+1 from meveo_timer ;
  --ALTER SEQUENCE meveo_instance_job_seq RESTART WITH (select max(id)+1 from meveo_job_instance);
 -- ALTER SEQUENCE CRM_CUSTOM_FIELD_INST_SEQ RESTART WITH select max(id)+1 from crm_custom_field_inst ;

