delete from ar_customer_account where id>200;
delete from crm_customer where id>200;
delete from account_entity where id>200;
delete from crm_seller where id>50;
ALTER SEQUENCE account_entity_seq  RESTART WITH 200;
ALTER SEQUENCE crm_seller_seq  RESTART WITH 200;