/* Catalogs */
insert into CAT_CHARGE_TEMPLATE (id, version, provider_id, disabled, created, code, description, INVOICE_SUB_CATEGORY, unit_nb_decimal, INPUT_UNIT_DESCRIPTION, RATING_UNIT_DESCRIPTION, UNIT_MULTIPLICATOR) values (1, 0, 1, false, now(), 'REC1', 'REC1', 1, 2, 'xxx', 'yyy', 1.5);
insert into CAT_CHARGE_TEMPLATE (id, version, provider_id, disabled, created, code, description, INVOICE_SUB_CATEGORY, unit_nb_decimal, INPUT_UNIT_DESCRIPTION, RATING_UNIT_DESCRIPTION, UNIT_MULTIPLICATOR) values (2, 0, 1, false, now(), 'SUB1', 'SUB1', 1, 2, 'xxx', 'yyy', 1.5);
insert into CAT_CHARGE_TEMPLATE (id, version, provider_id, disabled, created, code, description, INVOICE_SUB_CATEGORY, unit_nb_decimal, INPUT_UNIT_DESCRIPTION, RATING_UNIT_DESCRIPTION, UNIT_MULTIPLICATOR) values (3, 0, 1, false, now(), 'USAGE1', 'USAGE1', 1, 2, 'xxx', 'yyy', 1.5);
insert into CAT_CHARGE_TEMPLATE (id, version, provider_id, disabled, created, code, description, INVOICE_SUB_CATEGORY, unit_nb_decimal, INPUT_UNIT_DESCRIPTION, RATING_UNIT_DESCRIPTION, UNIT_MULTIPLICATOR) values (4, 0, 1, false, now(), 'SUB2', 'SUB2', 1, 2, 'xxx', 'yyy', 1.5);

DROP SEQUENCE IF EXISTS CAT_CHARGE_TEMPLATE_SEQ;
CREATE SEQUENCE CAT_CHARGE_TEMPLATE_SEQ start with 5 increment by 1;

insert into CAT_RECURRING_CHARGE_TEMPL (id, CALENDAR_ID, APPLY_IN_ADVANCE, subscription_prorata, termination_prorata) values (1, 3, true, true, true);
insert into CAT_ONE_SHOT_CHARGE_TEMPL (id, type, immediate_invoicing) values (2, 'SUBSCRIPTION', false);
insert into CAT_USAGE_CHARGE_TEMPLATE (id) values (3);
insert into CAT_ONE_SHOT_CHARGE_TEMPL (id, type, immediate_invoicing) values (4, 'SUBSCRIPTION', false);

insert into CAT_SERVICE_TEMPLATE (id, version, disabled, created, code, description, provider_id) values (1, 0, false, now(), 'SERV1', 'SERV1', 1);

DROP SEQUENCE IF EXISTS CAT_SERVICE_TEMPLATE_SEQ;
CREATE SEQUENCE CAT_SERVICE_TEMPLATE_SEQ start with 2 increment by 1;

/*insert into CAT_SERV_RECCHARGE_TEMPLATES (service_template_id, charge_template_id) values (1, 1);
insert into CAT_SERV_ONECHARGE_S_TEMPLATES (service_template_id, charge_template_id) values (1, 2);
insert into CAT_SERV_ONECHARGE_S_TEMPLATES (service_template_id, charge_template_id) values (1, 4);*/

insert into CAT_offer_TEMPLATE (id, version, disabled, created, code, description, provider_id) values (1, 0, false, now(), 'OFF1', 'OFF1', 1);

DROP SEQUENCE IF EXISTS CAT_OFFER_TEMPLATE_SEQ;
CREATE SEQUENCE CAT_OFFER_TEMPLATE_SEQ start with 2 increment by 1;

insert into CAT_OFFER_SERV_TEMPLATES (offer_template_id, service_template_id) values (1, 1);

insert into CAT_PRICE_PLAN_MATRIX (id, version, disabled, created, provider_id, PRIORITY, EVENT_CODE, AMOUNT_WITHOUT_TAX, AMOUNT_WITH_TAX, code, description) values (1, 0, false, now(), 1, 1, 'REC1', 5, 10, 'REC1', 'REC1');
insert into CAT_PRICE_PLAN_MATRIX (id, version, disabled, created, provider_id, PRIORITY, EVENT_CODE, AMOUNT_WITHOUT_TAX, AMOUNT_WITH_TAX, code, description) values (2, 0, false, now(), 1, 1, 'SUB1', 15, 20, 'SUB1', 'SUB1');
insert into CAT_PRICE_PLAN_MATRIX (id, version, disabled, created, provider_id, PRIORITY, EVENT_CODE, AMOUNT_WITHOUT_TAX, AMOUNT_WITH_TAX, code, description) values (3, 0, false, now(), 1, 1, 'USAGE1', 25, 30, 'USAGE1', 'USAGE1');
insert into CAT_PRICE_PLAN_MATRIX (id, version, disabled, created, provider_id, PRIORITY, EVENT_CODE, AMOUNT_WITHOUT_TAX, AMOUNT_WITH_TAX, code, description) values (4, 0, false, now(), 1, 1, 'SUB2', 15, 20, 'SUB2', 'SUB2');

DROP SEQUENCE IF EXISTS CAT_PRICE_PLAN_MATRIX_SEQ;
CREATE SEQUENCE CAT_PRICE_PLAN_MATRIX_SEQ start with 5 increment by 1;

insert into account_entity (id, version, disabled, created, provider_id, code, description, lastname, DEFAULT_LEVEL, account_type) values (1, 0, false, now(), 1, 'CUST1', 'CUST1', 'DEMO', TRUE, 'CUST');
insert into account_entity (id, version, disabled, created, provider_id, code, description, lastname, DEFAULT_LEVEL, account_type) values (2, 0, false, now(), 1, 'CA1', 'CA1', 'DEMO', TRUE, 'CA');
insert into account_entity (id, version, disabled, created, provider_id, code, description, lastname, DEFAULT_LEVEL, account_type) values (3, 0, false, now(), 1, 'BA1', 'BA1', 'DEMO', TRUE, 'BA');
insert into account_entity (id, version, disabled, created, provider_id, code, description, lastname, DEFAULT_LEVEL, account_type) values (4, 0, false, now(), 1, 'UA1', 'UA1', 'DEMO', TRUE, 'UA');

DROP SEQUENCE IF EXISTS ACCOUNT_ENTITY_SEQ;
CREATE SEQUENCE ACCOUNT_ENTITY_SEQ start with 5 increment by 1;

insert into crm_customer (id, CUSTOMER_CATEGORY_ID, CUSTOMER_BRAND_ID, SELLER_ID) values (1, 2, 1, 1);
insert into AR_CUSTOMER_ACCOUNT (id, CUSTOMER_ID, TRADING_CURRENCY_ID, STATUS) values (2, 1, 1, 'ACTIVE');
insert into BILLING_BILLING_ACCOUNT (id, CUSTOMER_ACCOUNT_ID, BILLING_CYCLE, TRADING_COUNTRY_ID, TRADING_language_ID, PAYMENT_METHOD, ELECTRONIC_BILLING) values (3, 2, 1, 1, 1, 'CHECK', false);
insert into BILLING_USER_ACCOUNT (id, BILLING_ACCOUNT_ID) values (4, 3);

insert into cat_wallet_template (id, version, disabled, created, code, description, FAST_RATING_LEVEL, wallet_type, provider_id, CONSUMPTION_ALERT_SET) values (1, 0, false, now(), 'POSTPAID_WALLET', 'Post Paid Wallet', 1, 'POSTPAID', 1, true);
insert into cat_wallet_template (id, version, disabled, created, code, description, FAST_RATING_LEVEL, wallet_type, provider_id, CONSUMPTION_ALERT_SET) values (2, 0, false, now(), 'PREPAID_WALLET', 'Prepaid Wallet', 1, 'PREPAID', 1, true);
insert into CAT_WALLET_TEMPLATE (id, version, disabled, created, code, description, consumption_alert_set, fast_rating_level, wallet_type, provider_id, creator_id) values (3, 0, false, now(), 'SOAP_WALLET10', 'SOAP_WALLET10', true, 1, 'PREPAID', 1, 1);
insert into CAT_WALLET_TEMPLATE (id, version, disabled, created, code, description, consumption_alert_set, fast_rating_level, wallet_type, provider_id, creator_id) values (4, 0, false, now(), 'SOAP_WALLET20', 'SOAP_WALLET20', true, 1, 'PREPAID', 1, 1);

DROP SEQUENCE IF EXISTS CAT_WALLET_TEMPLATE_SEQ;
CREATE SEQUENCE CAT_WALLET_TEMPLATE_SEQ start with 5 increment by 1;

insert into CAT_SERV_REC_CHARGE_TEMPLATE (id, version, provider_id, charge_template_id, service_template_id) values (1, 0, 1, 1, 1);
insert into CAT_SERV_SUB_CHARGE_TEMPLATE (id, version, provider_id, charge_template_id, service_template_id) values (1, 0, 1, 2, 1);
insert into CAT_SERV_USAGE_CHARGE_TEMPLATE (id, version, provider_id, charge_template_id, service_template_id) values (1, 0, 1, 3, 1);

DROP SEQUENCE IF EXISTS CAT_CHARGE_TEMPLATE_SEQ;
CREATE SEQUENCE CAT_CHARGE_TEMPLATE_SEQ start with 5 increment by 1;

insert into CAT_SERV_REC_WALLET_TEMPLATE (service_rec_templt_id, wallet_template_id, indx) values (1, 1, 0);
insert into CAT_SERV_SUB_WALLET_TEMPLATE (service_sub_templt_id, wallet_template_id, indx) values (1, 1, 0);
insert into CAT_SERV_USAGE_WALLET_TEMPLATE (service_usage_templt_id, wallet_template_id, indx) values (1, 1, 0);

DROP SEQUENCE IF EXISTS CAT_SERV_RECCHRG_TEMPLT_SEQ;
CREATE SEQUENCE CAT_SERV_RECCHRG_TEMPLT_SEQ start with 2 increment by 1;

DROP SEQUENCE IF EXISTS CAT_SERV_SUBCHRG_TEMPLT_SEQ;
CREATE SEQUENCE CAT_SERV_SUBCHRG_TEMPLT_SEQ start with 2 increment by 1;

DROP SEQUENCE IF EXISTS CAT_SERV_USAGECHRG_TEMPLT_SEQ;
CREATE SEQUENCE CAT_SERV_USAGECHRG_TEMPLT_SEQ start with 2 increment by 1;

/* Add wallet to userAccount=1 */
insert into BILLING_WALLET (id, version, disabled, created, code, description, provider_id, user_account_id) values (1, 0, false, now(), 'PRINCIPAL', 'Principal', 1, 4);
DROP SEQUENCE IF EXISTS BILLING_WALLET_SEQ;
CREATE SEQUENCE BILLING_WALLET_SEQ start with 2 increment by 1;

update BILLING_USER_ACCOUNT set wallet_id=1 where id=4;

/* Custom Fields */
insert into CRM_CUSTOM_FIELD_TMPL (id, version, disabled, created, code, description, account_type, field_type, value_required, provider_id, creator_id) values (1, 0, false, now(), 'SOAP_CUST10', 'SOAP_CUST10', 'CUST', 'STRING', false, 1, 1);
insert into CRM_CUSTOM_FIELD_TMPL (id, version, disabled, created, code, description, account_type, field_type, value_required, provider_id, creator_id) values (2, 0, false, now(), 'SOAP_CA10', 'SOAP_CA10', 'CA', 'DATE', false, 1, 1);
insert into CRM_CUSTOM_FIELD_TMPL (id, version, disabled, created, code, description, account_type, field_type, value_required, provider_id, creator_id) values (3, 0, false, now(), 'SOAP_BA10', 'SOAP_BA10', 'BA', 'LONG', false, 1, 1);
insert into CRM_CUSTOM_FIELD_TMPL (id, version, disabled, created, code, description, account_type, field_type, value_required, provider_id, creator_id) values (4, 0, false, now(), 'SOAP_UA10', 'SOAP_UA10', 'UA', 'DOUBLE', false, 1, 1);
insert into CRM_CUSTOM_FIELD_TMPL (id, version, disabled, created, code, description, account_type, field_type, value_required, provider_id, creator_id) values (5, 0, false, now(), 'SOAP_SUB10', 'SOAP_SUB10', 'SUB', 'STRING', false, 1, 1);

DROP SEQUENCE IF EXISTS CRM_CUSTOM_FLD_TMP_SEQ;
CREATE SEQUENCE CRM_CUSTOM_FLD_TMP_SEQ start with 6 increment by 1;