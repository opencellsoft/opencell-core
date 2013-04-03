/*----------TRUNCATE ALL TABLES-----------*/

truncate table account_entity,adm_country,adm_currency,adm_input_history,adm_language,adm_medina_configuration,
adm_messages,adm_role,adm_role_permission,adm_title,
adm_user,adm_user_log,adm_user_provider,adm_user_role,adm_vertina_configuration,
ar_account_operation,ar_action_dunning,ar_action_plan_item,ar_bank_operation,
ar_customer_account,ar_ddrequest_item,ar_ddrequest_lot,ar_ddrequest_lot_op,
ar_dunning_lot,ar_dunning_plan,ar_dunning_plan_transition,ar_matching_amount,ar_matching_code,
ar_occ_template,bi_job,bi_job_history,bi_report,bi_report_emails,billing_access_point,
billing_billing_account,billing_billing_run,billing_billing_run_list,
billing_charge_applic,billing_charge_instance,billing_cycle,billing_discountplan_instanciation,billing_invoice,billing_invoice_agregate,billing_invoice_cat,
billing_invoice_cat_country,billing_invoice_sub_cat,billing_invoice_cat_lang,
billing_invoice_sub_cat_country,billing_invoice_template,
billing_one_shot_charge_inst,billing_operation,billing_priceplan_instanciation,billing_rated_transaction,
billing_recurring_charge_inst,billing_serv_param_inst,billing_service_instance,billing_subscrip_termin_reason
,billing_subscription,billing_tax,billing_tax_language,
billing_trading_country,billing_trading_currency,
billing_trading_language,billing_user_account,billing_wallet,cat_calendar,cat_calendar_days,cat_charge_template,
cat_day_in_year,cat_discount_plan_matrix,cat_offer_serv_templates,cat_offer_template,
cat_one_shot_charge_templ,cat_price_plan_matrix,cat_recurring_charge_templ,cat_serv_onecharge_s_templates,
cat_serv_onecharge_t_templates,cat_serv_reccharge_templates,cat_service_template,
com_campaign,com_contact,com_contact_com_message,com_contact_coords,com_message,
com_message_template,com_msg_tmpl_variable,com_msg_var_value,com_provider_policy,com_sender_config,
crm_customer,crm_customer_brand,crm_customer_category,crm_email,crm_provider,crm_provider_config,
crm_provider_contact,dwh_account_operation,dwh_journal_entries,hibernate_sequences,mediation_magic_numbers,medina_access,
medina_number_plan,medina_time_plan,medina_zonning_plan,provider_titles,
rating_matrix_definition,rating_matrix_entry,rating_usage_type,report_emails,rm_line,rm_offer_instance,rm_usage_counter cascade;

/*------INSERTS OF TABLE LANGUAGE----*/

INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (1, 1, now(), NULL, 'DEU', 'allemand', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (2, 1, now(), NULL, 'DEU','allemand', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (3, 1, now(), NULL, 'ENG','anglais', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (4, 1, now(), NULL, 'ARA','arabe', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (5, 1, now(), NULL, 'BUL','bulgare', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (6, 1, now(), NULL, 'CAT','catalan', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (7, 1, now(), NULL, 'ZHO','chinois', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (8, 1, now(), NULL, 'DAN','danois', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (9, 1, now(), NULL, 'ESL','espagnol', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (10, 1, now(), NULL, 'EST','estonien', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (11, 1, now(), NULL, 'FAO','féroïen', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (12, 1, now(), NULL, 'FIN','finlandais', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (13, 1, now(), NULL, 'FRA','français', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (14, 1, now(), NULL, 'ELL','grec', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (15, 1, now(), NULL, 'HIN','hindi', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (16, 1, now(), NULL, 'HUN','hongrois', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (17, 1, now(), NULL, 'ISL','islandais', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (18, 1, now(), NULL, 'ITA','italien', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (19, 1, now(), NULL, 'JPN','japonais', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (20, 1, now(), NULL, 'LAV','letton', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (21, 1, now(), NULL, 'LIT','lituanien', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (22, 1, now(), NULL, 'NLD','néerlandais', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (23, 1, now(), NULL, 'NOR','norvégien', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (24, 1, now(), NULL, 'POL','polonais', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (25, 1, now(), NULL, 'POR','portugais', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (26, 1, now(), NULL, 'RON','roumain', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (27, 1, now(), NULL, 'RUS','russe', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (28, 1, now(), NULL, 'SRP','serbe', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (29, 1, now(), NULL, 'SLK','slovaque', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (30, 1, now(), NULL, 'SLV','slovène', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (31, 1, now(), NULL, 'SVE','suédois', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (32, 1, now(), NULL, 'CES','tchèque', NULL, NULL);	
INSERT INTO adm_language (id, version, created, updated, language_code, description_en, creator_id,  updater_id) VALUES (33, 1, now(), NULL, 'TUR','turc', NULL, NULL);	

/*------INSERTS OF TABLE CURRENCY----*/

INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (1,1,'AFA','Afghani',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (2,1,'ZAR','Rand',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (3,1,'ALL','Lek',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (4,1,'DZD','Dinar algérien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (5,1,'EUR','Euro',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (6,1,'AOA','Kwanza',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (7,1,'XCD','Dollar des Cara bes de lEst',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (8,1,'ANG','Florin des Antilles',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (9,1,'SAR','Riyal saoudien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (10,1,'ARS','Peso',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (11,1,'AMD','Dram',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (12,1,'AWG','Florin d Aruba',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (13,1,'AUD','Dollar australien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (14,1,'AZM','Manat azerbaïdjanais',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (15,1,'BSD','Dollar des Bahamas',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (16,1,'BHD','Dinar de Bahreïn',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (17,1,'BDT','Taka',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (18,1,'BBD','Dollar de Barbade',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (19,1,'BZD','Dollar de Belize',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (20,1,'XOF','Franc CFA - BCEAO',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (21,1,'BMD','Dollar des Bermudes',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (22,1,'BTN','Ngultrum',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (23,1,'BYR','Rouble biãlorussie',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (24,1,'BOB','Boliviano',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (25,1,'BAM','Mark bosniaque convertible',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (26,1,'BWP','Pula',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (27,1,'BRL','Real',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (28,1,'BND','Dollar de Brunéi',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (29,1,'BGN','Lev',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (30,1,'BIF','Franc du Burundi',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (31,1,'NOK','Couronne norvégienne',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (32,1,'KYD','Dollar des îles Caïmanes',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (33,1,'KHR','Riel',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (34,1,'XAF','Franc CFA - BEAC',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (35,1,'CAD','Dollar canadien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (36,1,'CVE','Escudo du Cap-Vert',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (37,1,'CFA','FRANC CFA-BEAC',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (38,1,'CLP','Peso chilien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (39,1,'CNY','Yuan Ren-Min-Bi',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (40,1,'CYP','Livre chypriote',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (41,1,'COP','Peso colombien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (42,1,'KMF','Franc des Comores',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (43,1,'CDF','FRANC DU CONGO DEMOCRATIQUE',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (44,1,'KRW','Won',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (45,1,'KPW','Won de la Corée du Nord',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (46,1,'CRC','Colon de Costa Rica',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (47,1,'HRK','Kuna',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (48,1,'CUP','Peso cubain',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (49,1,'USD','Dollar des Etats-unis',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (50,1,'DKK','Couronne danoise',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (51,1,'DJF','Franc de Djibouti',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (52,1,'DOP','Peso dominicain',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (53,1,'EGP','Livre égyptienne',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (54,1,'AED','Dirham des émirats arabes unis',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (55,1,'ERN','Nafka',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (56,1,'EEK','Couronne d Estonie',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (57,1,'ETB','Birr éthiopien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (58,1,'FKP','Livre de Falkland',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (59,1,'FJD','Dollar des Fidji',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (60,1,'GMD','Dalasie',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (61,1,'GEL','Lari',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (62,1,'GHC','Cedi ghanéen',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (63,1,'GIP','Livre de Gibraltar',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (64,1,'GTQ','Quetzal',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (65,1,'GBP','Livre sterling',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (66,1,'GNF','Franc guinéen',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (67,1,'GYD','Dollar de Guyane',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (68,1,'HTG','Gourde',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (69,1,'HNL','Lempira',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (70,1,'HKD','Dollar de Hong-Kong',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (71,1,'HUF','Forint',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (72,1,'NZD','Dollar néo-zélandais',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (73,1,'INR','Roupie indienne',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (74,1,'IDR','Rupiah',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (75,1,'IRR','Rial iranien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (76,1,'IQD','Dinar iraquien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (77,1,'ISK','Couronne islandaise',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (78,1,'ILS','Sheqel',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (79,1,'JMD','Dollar jamaïcain',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (80,1,'JPY','Yen',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (81,1,'JOD','Dinar jordanien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (82,1,'KZT','Tenge',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (83,1,'KES','Shilling du Kenya',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (84,1,'KGS','Som',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (85,1,'KWD','Dinar koweïtien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (86,1,'LAK','Kip',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (87,1,'LSL','Loti',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (88,1,'LVL','Lats letton',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (89,1,'LBP','Livre libanaise',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (90,1,'LRD','Dollar libérien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (91,1,'LYD','Dinar libyen',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (92,1,'CHF','Franc suisse',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (93,1,'LTL','Litas lituanien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (94,1,'MOP','Pataca',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (95,1,'MKD','Denar',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (96,1,'MGA','Ariary malgache',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (97,1,'MGF','Franc malgache',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (98,1,'MYR','Ringgit de Malaisie',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (99,1,'MWK','Kwacha',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (100,1,'MVR','Rufiyaa',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (101,1,'MTL','Livre maltaise',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (102,1,'MAD','Dirham marocain',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (103,1,'MUR','Roupie mauricienne',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (104,1,'MRO','Ouguija',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (105,1,'MXN','Peso mexicain',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (106,1,'MDL','Leu de Moldave',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (107,1,'MNT','Tugrik',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (108,1,'MZM','Metical',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (109,1,'MMK','Kyat',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (110,1,'NAD','Dollar namibien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (111,1,'NPR','Roupie Népalaise',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (112,1,'NIO','Cordoba oro',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (113,1,'NGN','Naira',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (114,1,'XPF','Franc CFP',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (115,1,'OMR','Rial Omani',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (116,1,'XAU','Opérations sur or',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (117,1,'UGX','Shilling ougandais',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (118,1,'UZS','Soum ouzbek',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (119,1,'PKR','Roupie pakistanaise',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (120,1,'PAB','Balboa',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (121,1,'PGK','Kina',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (122,1,'PYG','Guarani',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (123,1,'PEN','Nouveau sol',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (124,1,'PHP','Peso philippin',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (125,1,'PLN','Zloty',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (126,1,'QAR','Riyal du Qatar',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (127,1,'RON','LEI (Nouveau Leu)',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (128,1,'ROL','Leu',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (129,1,'RUB','Rouble russe (nouveau)',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (130,1,'RWF','Franc du Rwanda',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (131,1,'SBD','Dollar des îles Salomon',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (132,1,'SVC','Colon salvadorien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (133,1,'WST','Tala',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (134,1,'STD','Dobra',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (135,1,'CSD','Dinar Serbe',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (136,1,'SCR','Roupie des Seychelles',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (137,1,'SLL','Leone',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (138,1,'SGD','Dollar de Singapour',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (139,1,'SKK','Couronne slovaque',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (140,1,'SIT','Tolar',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (141,1,'SOS','Shilling Somalien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (142,1,'SDG','Livre soudanaise',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (143,1,'LKR','Roupie de Sri Lanka',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (144,1,'SHP','Livre de Sainte-Hélène',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (145,1,'SEK','Couronne suédoise',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (146,1,'SRD','Florin du suriname',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (147,1,'SZL','Lilangeni',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (148,1,'SYP','Livre syrienne',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (149,1,'TJS','Somoni',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (150,1,'TWD','Nouveau dollar de Taïwan',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (151,1,'TZS','Shilling tanzanien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (152,1,'CZK','Couronne tchèque',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (153,1,'THB','Baht',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (154,1,'TOP','Pa anga',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (155,1,'TTD','Dollar de Trinité et de Tobago',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (156,1,'TND','Dinar tunisien',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (157,1,'TMM','Manat turkmène',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (158,1,'TRY','Nouvelle Livre turque',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (159,1,'TRL','Livre turque',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (160,1,'UAH','HRYVNIA',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (161,1,'UYU','Nouveau Peso uruguayen',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (162,1,'VUV','Vatu',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (163,1,'VEF','Bolivar Fuerte',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (164,1,'VND','Dong',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (165,1,'YER','Riyal yéménite',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (166,1,'ZMK','Kwacha de Zambie',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (167,1,'ZWD','Dollar du Zimbabwe',true,now(),null,null,null);
INSERT INTO adm_currency (id, version, currency_code, description_en, system_currency, created, updated, creator_id,  updater_id) VALUES (168,1,'GHS','Cedi ghanéen',true,now(),null,null,null);

/*-------INSERTS OF TABLE COUNTRY------*/

INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(1,1,now(),null,'AD','Andorra',null,null,8,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(2,1,now(),null,'AE','United Arab Emirates',null,null,3,54);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(3,1,now(),null,'AF','Afghanistan',null,null,null,1);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(4,1,now(),null,'AG','Antigua and Barbuda',null,null,2,7);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(5,1,now(),null,'AI','Anguilla',null,null,null,7);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(6,1,now(),null,'AL','Albania',null,null,null,3);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(7,1,now(),null,'AM','Armenia',null,null,null,11);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(8,1,now(),null,'AN','Netherlands Antilles',null,null,21,8);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(9,1,now(),null,'AO','Angola',null,null,2,6);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(10,1,now(),null,'AR','Argentina',null,null,8,10);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(11,1,now(),null,'AS','American Samoa',null,null,1,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(12,1,now(),null,'AT','Austria',null,null,1,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(13,1,now(),null,'AU','Australia',null,null,2,13);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(14,1,now(),null,'AW','Aruba',null,null,null,12);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(15,1,now(),null,'AZ','Azerbaijan',null,null,null,6);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(16,1,now(),null,'BA','Bosnia and Herzegovina',null,null,2,25);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(17,1,now(),null,'BB','Barbados',null,null,null,18);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(18,1,now(),null,'BD','Bangladesh',null,null,null,17);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(19,1,now(),null,'BE','Belgium',null,null,12,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(20,1,now(),null,'BF','Burkina Faso',null,null,null,20);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(21,1,now(),null,'BG','Bulgaria',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(22,1,now(),null,'BH','Bahrain',null,null,null,16);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(23,1,now(),null,'BI','Burundi',null,null,null,30);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(24,1,now(),null,'BJ','Benin',null,null,null,20);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(25,1,now(),null,'BM','Bermuda',null,null,null,21);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(26,1,now(),null,'BN','Brunei Darussalam',null,null,null,28);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(27,1,now(),null,'BO','Bolivia',null,null,null,24);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(28,1,now(),null,'BR','Brazil',null,null,null,27);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(29,1,now(),null,'BS','The Bahamas',null,null,2,15);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(30,1,now(),null,'BT','Bhutan',null,null,null,16);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(31,1,now(),null,'BV','Bouvet Island',null,null,2,31);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(32,1,now(),null,'BW','Botswana',null,null,2,26);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(33,1,now(),null,'BY','Belarus',null,null,null,23);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(34,1,now(),null,'BZ','Belize',null,null,null,19);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(35,1,now(),null,'CA','Canada',null,null,2,35);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(36,1,now(),null,'CC','Cocos (Keeling) Islands',null,null,2,13);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(37,1,now(),null,'CD','Congo, Democratic Republic of th',null,null,12,43);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(38,1,now(),null,'CF','Central African Republic',null,null,12,34);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(39,1,now(),null,'CG','Congo, Republic of the',null,null,12,34);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(40,1,now(),null,'CH','Switzerland',null,null,null,92);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(41,1,now(),null,'CI','Cote Ivoire',null,null,12,20);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(42,1,now(),null,'CK','Cook Islands',null,null,null,72);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(43,1,now(),null,'CL','Chile',null,null,null,38);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(44,1,now(),null,'CM','Cameroon',null,null,12,34);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(45,1,now(),null,'CN','China',null,null,6,39);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(46,1,now(),null,'CO','Colombia',null,null,8,41);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(47,1,now(),null,'CR','Costa Rica',null,null,8,46);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(48,1,now(),null,'CU','Cuba',null,null,8,48);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(49,1,now(),null,'CV','Cape Verde',null,null,8,36);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(50,1,now(),null,'CX','Christmas Island',null,null,null,13);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(51,1,now(),null,'CY','Cyprus',null,null,null,40);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(52,1,now(),null,'CZ','Czech Republic',null,null,null,152);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(53,1,now(),null,'DE','Germany',null,null,1,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(54,1,now(),null,'DJ','Djibouti',null,null,12,51);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(55,1,now(),null,'DK','Denmark',null,null,null,50);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(56,1,now(),null,'DM','Dominica',null,null,null,7);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(57,1,now(),null,'DO','Dominican Republic',null,null,2,52);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(58,1,now(),null,'DZ','Algeria',null,null,8,4);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(59,1,now(),null,'EC','Ecuador',null,null,null,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(60,1,now(),null,'EE','Estonia',null,null,null,56);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(61,1,now(),null,'EG','Egypt',null,null,8,53);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(62,1,now(),null,'ER','Eritrea',null,null,null,55);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(63,1,now(),null,'ES','Spain',null,null,8,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(64,1,now(),null,'ET','Ethiopia',null,null,null,57);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(65,1,now(),null,'FI','Finland',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(66,1,now(),null,'FJ','Fiji',null,null,null,59);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(67,1,now(),null,'FK','Falkland Islands (Islas Malvinas',null,null,2,58);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(68,1,now(),null,'FM','Micronesia, Federated States of',null,null,2,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(69,1,now(),null,'FO','Faroe Islands',null,null,2,50);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(70,1,now(),null,'FR','France',null,null,12,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(71,1,now(),null,'GA','Gabon',null,null,12,34);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(72,1,now(),null,'GD','Grenada',null,null,null,7);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(73,1,now(),null,'GE','Georgia',null,null,null,61);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(74,1,now(),null,'GF','French Guiana',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(75,1,now(),null,'GH','Ghana',null,null,2,62);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(76,1,now(),null,'GI','Gibraltar',null,null,null,63);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(77,1,now(),null,'GL','Greenland',null,null,null,50);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(78,1,now(),null,'GM','The Gambia',null,null,null,60);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(79,1,now(),null,'GN','Guinea',null,null,null,66);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(80,1,now(),null,'GP','Guadeloupe',null,null,12,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(81,1,now(),null,'GQ','Equatorial Guinea',null,null,null,34);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(82,1,now(),null,'GR','Greece',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(83,1,now(),null,'GS','South Georgia and the South Sand',null,null,null,65);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(84,1,now(),null,'GT','Guatemala',null,null,null,64);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(85,1,now(),null,'GU','Guam',null,null,null,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(86,1,now(),null,'GW','Guinea-Bissau',null,null,null,20);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(87,1,now(),null,'GY','Guyana',null,null,null,67);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(88,1,now(),null,'HK','Hong Kong (SAR)',null,null,null,70);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(89,1,now(),null,'HM','Heard Island and McDonald Island',null,null,null,13);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(90,1,now(),null,'HN','Honduras',null,null,null,69);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(91,1,now(),null,'HR','Croatia',null,null,null,47);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(92,1,now(),null,'HT','Haiti',null,null,null,68);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(93,1,now(),null,'HU','Hungary',null,null,null,71);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(94,1,now(),null,'ID','Indonesia',null,null,null,74);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(95,1,now(),null,'IE','Ireland',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(96,1,now(),null,'IL','Israel',null,null,null,78);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(97,1,now(),null,'IN','India',null,null,null,73);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(98,1,now(),null,'IO','British Indian Ocean Territory',null,null,null,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(99,1,now(),null,'IQ','Iraq',null,null,8,76);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(100,1,now(),null,'IR','Iran',null,null,null,75);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(101,1,now(),null,'IS','Iceland',null,null,null,77);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(102,1,now(),null,'IT','Italy',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(103,1,now(),null,'JM','Jamaica',null,null,null,79);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(104,1,now(),null,'JO','Jordan',null,null,null,81);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(105,1,now(),null,'JP','Japan',null,null,null,80);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(106,1,now(),null,'KE','Kenya',null,null,null,83);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(107,1,now(),null,'KG','Kyrgyzstan',null,null,null,84);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(108,1,now(),null,'KH','Cambodia',null,null,null,33);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(109,1,now(),null,'KI','Kiribati',null,null,null,13);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(110,1,now(),null,'KM','Comoros',null,null,null,42);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(111,1,now(),null,'KN','Saint Kitts and Nevis',null,null,null,7);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(112,1,now(),null,'KP','Korea, North',null,null,null,45);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(113,1,now(),null,'KR','Korea, South',null,null,null,44);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(114,1,now(),null,'KW','Kuwait',null,null,null,85);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(115,1,now(),null,'KY','Cayman Islands',null,null,null,32);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(116,1,now(),null,'KZ','Kazakhstan',null,null,null,82);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(117,1,now(),null,'LA','Laos',null,null,null,86);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(118,1,now(),null,'LB','Lebanon',null,null,null,89);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(119,1,now(),null,'LC','Saint Lucia',null,null,null,7);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(120,1,now(),null,'LI','Liechtenstein',null,null,null,92);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(121,1,now(),null,'LK','Sri Lanka',null,null,null,143);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(122,1,now(),null,'LR','Liberia',null,null,null,90);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(123,1,now(),null,'LS','Lesotho',null,null,null,87);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(124,1,now(),null,'LT','Lithuania',null,null,null,93);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(125,1,now(),null,'LU','Luxembourg',null,null,12,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(126,1,now(),null,'LV','Latvia',null,null,null,88);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(127,1,now(),null,'LY','Libya',null,null,null,91);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(128,1,now(),null,'MA','Morocco',null,null,3,102);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(129,1,now(),null,'MC','Monaco',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(130,1,now(),null,'MD','Moldova',null,null,null,106);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(131,1,now(),null,'MG','Madagascar',null,null,null,97);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(132,1,now(),null,'MH','Marshall Islands',null,null,null,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(133,1,now(),null,'MK','Macedonia, The Former Yugoslav R',null,null,null,95);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(134,1,now(),null,'ML','Mali',null,null,12,20);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(135,1,now(),null,'MM','Burma',null,null,null,109);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(136,1,now(),null,'MN','Mongolia',null,null,null,107);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(137,1,now(),null,'MO','Macao',null,null,null,94);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(138,1,now(),null,'MP','Northern Mariana Islands',null,null,null,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(139,1,now(),null,'MQ','Martinique',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(140,1,now(),null,'MR','Mauritania',null,null,null,104);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(141,1,now(),null,'MS','Montserrat',null,null,null,7);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(142,1,now(),null,'MT','Malta',null,null,null,101);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(143,1,now(),null,'MU','Mauritius',null,null,null,103);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(144,1,now(),null,'MV','Maldives',null,null,null,100);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(145,1,now(),null,'MW','Malawi',null,null,null,99);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(146,1,now(),null,'MX','Mexico',null,null,null,105);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(147,1,now(),null,'MY','Malaysia',null,null,null,98);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(148,1,now(),null,'MZ','Mozambique',null,null,null,108);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(149,1,now(),null,'NA','Namibia',null,null,2,110);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(150,1,now(),null,'NC','New Caledonia',null,null,null,114);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(151,1,now(),null,'NE','Niger',null,null,12,20);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(152,1,now(),null,'NF','Norfolk Island',null,null,null,13);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(153,1,now(),null,'NG','Nigeria',null,null,null,113);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(154,1,now(),null,'NI','Nicaragua',null,null,8,112);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(155,1,now(),null,'NL','Netherlands',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(156,1,now(),null,'NO','Norway',null,null,null,31);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(157,1,now(),null,'NP','Nepal',null,null,null,111);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(158,1,now(),null,'NR','Nauru',null,null,null,13);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(159,1,now(),null,'NU','Niue',null,null,null,72);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(160,1,now(),null,'NZ','New Zealand',null,null,null,72);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(161,1,now(),null,'OM','Oman',null,null,null,115);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(162,1,now(),null,'PA','Panama',null,null,8,120);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(163,1,now(),null,'PE','Peru',null,null,8,123);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(164,1,now(),null,'PF','French Polynesia',null,null,12,114);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(165,1,now(),null,'PG','Papua New Guinea',null,null,12,121);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(166,1,now(),null,'PH','Philippines',null,null,null,124);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(167,1,now(),null,'PK','Pakistan',null,null,null,119);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(168,1,now(),null,'PL','Poland',null,null,null,125);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(169,1,now(),null,'PM','Saint Pierre and Miquelon',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(170,1,now(),null,'PN','Pitcairn Islands',null,null,null,72);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(171,1,now(),null,'PR','Puerto Rico',null,null,8,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(172,1,now(),null,'PS','Palestinian Territory, Occupied',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(173,1,now(),null,'PT','Portugal',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(174,1,now(),null,'PW','Palau',null,null,null,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(175,1,now(),null,'PY','Paraguay',null,null,null,122);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(176,1,now(),null,'QA','Qatar',null,null,null,126);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(177,1,now(),null,'RE','Réunion',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(178,1,now(),null,'RO','Romania',null,null,null,128);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(179,1,now(),null,'RU','Russia',null,null,null,129);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(180,1,now(),null,'RW','Rwanda',null,null,null,130);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(181,1,now(),null,'SA','Saudi Arabia',null,null,null,9);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(182,1,now(),null,'SB','Solomon Islands',null,null,null,131);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(183,1,now(),null,'SC','Seychelles',null,null,null,136);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(184,1,now(),null,'SD','Sudan',null,null,null,21);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(185,1,now(),null,'SE','Sweden',null,null,null,145);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(186,1,now(),null,'SG','Singapore',null,null,2,138);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(187,1,now(),null,'SH','Saint Helena',null,null,null,144);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(188,1,now(),null,'SI','Slovenia',null,null,null,140);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(189,1,now(),null,'SJ','Svalbard',null,null,null,31);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(190,1,now(),null,'SK','Slovakia',null,null,null,139);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(191,1,now(),null,'SL','Sierra Leone',null,null,null,137);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(192,1,now(),null,'SM','San Marino',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(193,1,now(),null,'SN','Senegal',null,null,null,20);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(194,1,now(),null,'SO','Somalia',null,null,null,141);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(195,1,now(),null,'SR','Suriname',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(196,1,now(),null,'ST','São Tomé and Príncipe',null,null,null,134);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(197,1,now(),null,'SV','El Salvador',null,null,null,132);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(198,1,now(),null,'SY','Syria',null,null,null,148);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(199,1,now(),null,'SZ','Swaziland',null,null,null,147);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(200,1,now(),null,'TC','Turks and Caicos Islands',null,null,null,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(201,1,now(),null,'TD','Chad',null,null,null,34);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(202,1,now(),null,'TF','French Southern and Antarctic La',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(203,1,now(),null,'TG','Togo',null,null,null,20);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(204,1,now(),null,'TH','Thailand',null,null,null,153);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(205,1,now(),null,'TJ','Tajikistan',null,null,null,149);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(206,1,now(),null,'TK','Tokelau',null,null,null,72);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(207,1,now(),null,'TM','Turkmenistan',null,null,null,157);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(208,1,now(),null,'TN','Tunisia',null,null,null,156);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(209,1,now(),null,'TO','Tonga',null,null,null,154);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(210,1,now(),null,'TL','East timor',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(211,1,now(),null,'TR','Turkey',null,null,null,159);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(212,1,now(),null,'TT','Trinidad and Tobago',null,null,null,155);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(213,1,now(),null,'TV','Tuvalu',null,null,null,13);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(214,1,now(),null,'TW','Taiwan',null,null,null,150);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(215,1,now(),null,'TZ','Tanzania',null,null,null,151);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(216,1,now(),null,'UA','Ukraine',null,null,null,160);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(217,1,now(),null,'UG','Uganda',null,null,null,117);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(218,1,now(),null,'GB','United Kingdom',null,null,2,65);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(219,1,now(),null,'UM','United States Minor Outlying Isl',null,null,2,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(220,1,now(),null,'US','United States',null,null,2,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(221,1,now(),null,'UY','Uruguay',null,null,null,161);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(222,1,now(),null,'UZ','Uzbekistan',null,null,null,118);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(223,1,now(),null,'VA','Holy See Vatican City',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(224,1,now(),null,'VC','Saint Vincent and the Grenadines',null,null,null,7);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(225,1,now(),null,'VE','Venezuela',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(226,1,now(),null,'VG','British Virgin Islands',null,null,2,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(227,1,now(),null,'VI','Virgin Islands',null,null,2,49);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(228,1,now(),null,'VN','Vietnam',null,null,null,164);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(229,1,now(),null,'VU','Vanuatu',null,null,null,162);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(230,1,now(),null,'WF','Wallis and Futuna',null,null,null,114);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(231,1,now(),null,'WS','Samoa',null,null,null,133);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(232,1,now(),null,'YE','Yemen',null,null,null,165);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(233,1,now(),null,'YT','Mayotte',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(234,1,now(),null,'YU','Yugoslavia',null,null,null,5);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(235,1,now(),null,'ZA','South Africa',null,null,2,2);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(236,1,now(),null,'ZM','Zambia',null,null,2,166);
INSERT INTO adm_country (id, version, created, updated, country_code, description_en,creator_id, updater_id, language_id,currency_id) VALUES(237,1,now(),null,'ZW','Zimbabwe',null,null,2,167);

/*------INSERTS OF TABLE CRM PROVIDER----*/

INSERT INTO crm_provider (id, version, disabled, created, updated, code, description, multicountry_flag, multicurrency_flag, multilanguage_flag, payment_methods, logo, invoice_prefix, current_invoice_nb, rating_rounding, bank_code, branch_code, account_number, hash_key, iban, bic, account_owner, bank_name, bank_id, issuer_number, issuer_name, entreprise, automatic_invoicing, code_creancier, code_etblissement_creancier, code_centre, nne, address_1, address_2, address_3, address_zipcode, address_city, address_country, address_state, amount_validation, level_duplication, email, country_id, provider_id, currency_id, updater_id,creator_id,language_id) VALUES (1, 1, false, now(), NULL, 'MYCOMPANY', NULL, true, true, true, NULL, NULL, NULL, NULL, NULL, 'SGMB', '12345', '33333333333', '11', '11', '11', 'owner', 'SGMB', '11', '1111', 'PROV1', false, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, false, false, NULL, 1, NULL, 1, NULL,NULL, 1);
INSERT INTO crm_provider (id, version, disabled, created, updated, code, description, multicountry_flag, multicurrency_flag, multilanguage_flag, payment_methods, logo, invoice_prefix, current_invoice_nb, rating_rounding, bank_code, branch_code, account_number, hash_key, iban, bic, account_owner, bank_name, bank_id, issuer_number, issuer_name, entreprise, automatic_invoicing, code_creancier, code_etblissement_creancier, code_centre, nne, address_1, address_2, address_3, address_zipcode, address_city, address_country, address_state, amount_validation, level_duplication, email, country_id, provider_id, currency_id, updater_id,creator_id,language_id) VALUES (2, 1, false,now(), NULL, 'OTHERCOPANY', NULL, true, true, true, NULL, NULL, NULL, NULL, NULL, 'CIC', '54321', '22222222222', '22', '22', '22', 'owner2', 'CIC', '12', '2222', 'PROV22', false, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, false, false, NULL, 2, NULL, 2, NULL,NULL, 2);

/*------INSERTS OF TABLE ADM_USER----*/

INSERT INTO ADM_USER (ID, VERSION, DISABLED, CREATED, USERNAME, PASSWORD, LAST_PASSWORD_MODIFICATION, CREATOR_ID, PROVIDER_ID) VALUES ('1', 0, false, now(),  'ADMIN', 'd033e22ae348aeb5660fc2140aec35850c4da997', now(), 1, 1);
INSERT INTO ADM_USER (ID, VERSION, DISABLED, CREATED, USERNAME, PASSWORD, LAST_PASSWORD_MODIFICATION, CREATOR_ID, PROVIDER_ID) VALUES ('6', 0, false, now(),  'MEVEO.ADMIN', 'fb93a3221422999ba49eb103977a6d736376505b', now(), 1, 1);

 /*------INSERTS OF TABLE ADM_ROLE----*/
 
INSERT INTO ADM_ROLE (ID, ROLE_NAME, ROLE_DESCRIPTION) VALUES (1, 'administrateur', 'Administrateur');
INSERT INTO ADM_ROLE (ID, ROLE_NAME, ROLE_DESCRIPTION) VALUES (2, 'adv', 'ADV');
INSERT INTO ADM_ROLE (ID, ROLE_NAME, ROLE_DESCRIPTION) VALUES (3, 'visualisationGenerale', 'Visualization générale');
INSERT INTO ADM_ROLE (ID, ROLE_NAME, ROLE_DESCRIPTION) VALUES (4, 'visualisationClient', 'Visualization client');
INSERT INTO ADM_ROLE (ID, ROLE_NAME, ROLE_DESCRIPTION) VALUES (5, 'encaissement', 'Encaissement');
INSERT INTO ADM_ROLE (ID, ROLE_NAME, ROLE_DESCRIPTION) VALUES (6, 'superAdministrateur', 'Super Administrateur');

  /*------INSERTS OF TABLE ADM_USER_ROLE----*/
  
INSERT INTO ADM_USER_ROLE (USER_ID, ROLE_ID) VALUES (6, 1);
INSERT INTO ADM_USER_ROLE (USER_ID, ROLE_ID) VALUES (6, 2);
INSERT INTO ADM_USER_ROLE (USER_ID, ROLE_ID) VALUES (6, 3);
INSERT INTO ADM_USER_ROLE (USER_ID, ROLE_ID) VALUES (6, 4);
INSERT INTO ADM_USER_ROLE (USER_ID, ROLE_ID) VALUES (6, 5);
INSERT INTO ADM_USER_ROLE (USER_ID, ROLE_ID) VALUES (6, 6);
 
INSERT INTO ADM_USER_PROVIDER (PROVIDER_ID, USER_ID) VALUES (1, 6);
INSERT INTO ADM_USER_PROVIDER (PROVIDER_ID, USER_ID) VALUES (2, 6);

/*------INSERTS OF TABLE ADM_ROLE_PERMISSION----*/

INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'administrateur','administration','administrationVisualization,administrationManagement', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'administrateur','catalog','catalogVisualization,catalogManagement', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'administrateur','account','accountVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'administrateur','reporting','reportingVisualization,reportingManagement', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'administrateur','customerSummary','customerSummaryVisualization', 'role');

INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'adv','administration','administrationVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'adv','catalog','catalogVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'adv','account','accountVisualization,accountManagement', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'adv','billing','billingVisualization,billingManagement', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'adv','reporting','reportingVisualization,reportingManagement', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'adv','customerSummary','customerSummaryVisualization', 'role');

INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'visualisationGenerale','catalog','catalogVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'visualisationGenerale','account','accountVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'visualisationGenerale','billing','billingVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'visualisationGenerale','customerSummary','customerSummaryVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'visualisationClient','customerSummary','customerSummaryVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'encaissement','catalog','catalogVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'encaissement','account','accountVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'encaissement','billing','billingVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'encaissement','customerSummary','customerSummaryVisualization', 'role');
INSERT INTO ADM_ROLE_PERMISSION (ID,VERSION,ROLE,TARGET,ACTION,DISCRIMINATOR) VALUES (nextval('ADM_ROLE_PERMISSION_SEQ'),0,'encaissement','reporting','reportingVisualization', 'role');




