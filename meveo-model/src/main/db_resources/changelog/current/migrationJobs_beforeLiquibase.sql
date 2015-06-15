create table temp_meveo_timer as select *  from  meveo_timer;
create table temp_CRM_CUSTOM_FIELD_INST as select *  from  CRM_CUSTOM_FIELD_INST;
delete from CRM_CUSTOM_FIELD_INST;
delete  from meveo_timer;
