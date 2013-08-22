
--
-- Data for Name: account_entity; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO account_entity VALUES (125, 2, false, '2013-04-12 10:51:37.077', '2013-04-17 12:15:47.741', '55', 'hfbf', '', '', '', '', 'Australie', NULL, '', true, '52', '', 'ben', 'gbvdv', NULL, 1, 1, 1, 1, NULL);
INSERT INTO account_entity VALUES (1044, 0, false, '2013-04-23 20:34:31.578', NULL, 'CUST_TEST', 'Customer test', '', 'Street', '', 'PARIS', 'France', NULL, '00000', true, '', '', NULL, NULL, NULL, 1, 1, NULL, NULL, NULL);
INSERT INTO account_entity VALUES (1045, 0, false, '2013-04-23 20:35:10.083', NULL, 'CUST_ACC_TEST', '', '', 'Street', '', 'PARIS', 'France', NULL, '00000', true, '', '', '', 'Name', NULL, 1, 1, NULL, NULL, NULL);
INSERT INTO account_entity VALUES (1047, 2, false, '2013-04-23 20:36:26.117', '2013-04-23 20:37:12.853', 'USR_ACC_TEST', '', '', 'Street', '', 'PARIS', 'France', NULL, '00000', true, NULL, NULL, '', '', NULL, 1, 1, 1, NULL, NULL);
INSERT INTO account_entity VALUES (1046, 1, false, '2013-04-23 20:36:04.818', '2013-04-24 10:51:45.643', 'BIL_ACC_TEST', '', '', 'Street', '', 'PARIS', 'France', NULL, '00000', true, '', '', '', '', NULL, 1, 1, 1, NULL, NULL);






--
-- Data for Name: cat_offer_serv_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--




--
-- Data for Name: cat_one_shot_charge_templ; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_one_shot_charge_templ VALUES (true, 'OTHER', 1058);


--
-- Data for Name: cat_price_plan_matrix; Type: TABLE DATA; Schema: public; Owner: meveo
--


--
-- Data for Name: cat_serv_reccharge_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--




--
-- Data for Name: cat_usage_charge_template; Type: TABLE DATA; Schema: public; Owner: meveo
--








--
-- Data for Name: ar_customer_account; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO ar_customer_account VALUES ('', NULL, '', '', 'PART_M', '2013-04-12 10:49:14.295', NULL, 'R0', 'OtKqFUph', 'CHECK', 'ACTIVE', 125, NULL, NULL);
INSERT INTO ar_customer_account VALUES ('', NULL, '', '', 'PART_C', '2013-04-23 20:34:35.327', NULL, 'R0', 'nVyvfXlq', 'TIP', 'ACTIVE', 1045, 1044, 145);


--
-- Data for Name: billing_billing_account; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_billing_account VALUES (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, false, '', NULL, '2013-04-24 00:00:00', 'TIP', 'ACTIVE', '2013-04-23 20:36:04.817', '2013-04-23 20:36:04.818', NULL, 1046, 1042, NULL, 1045, 157, 141);


--
-- Data for Name: billing_wallet; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_wallet VALUES (1189, 0, false, '2013-04-23 20:36:26.12', NULL, 'PRINCIPAL', NULL, 1, 1, NULL, 1188, NULL);


--
-- Data for Name: billing_user_account; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_user_account VALUES ('ACTIVE', '2013-04-23 20:36:09.728', '2013-04-23 20:36:09.728', NULL, 1188, 1046, 1189);


--
-- Data for Name: billing_subscription; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_subscription VALUES (1049, 0, false, '2013-04-23 20:36:48.046', NULL, 'SUB_TEST', '', true, NULL, 'CREATED', '2013-04-23 20:36:31.937', '2013-04-23 00:00:00', NULL, 1, 1, NULL, 327, NULL, 1188);



--
-- Data for Name: billing_service_instance; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_service_instance VALUES (1051, 1, false, '2013-04-23 23:08:36.827', '2013-04-24 10:56:46.718', 'SOFT_EXCH_2010_2_BLACK', 'Exchange 2010 Premium - Blackberry option', NULL, 1, 'ACTIVE', '2013-04-24 10:56:46.718', '2013-04-23 00:00:36.76', NULL, 1, 1, 1, 354, 1049, NULL);
INSERT INTO billing_service_instance VALUES (1079, 1, false, '2013-04-24 15:53:21.568', '2013-04-24 15:55:48.519', 'MALIC', 'Microsoft Access License', NULL, 1, 'ACTIVE', '2013-04-24 15:55:48.519', '2013-04-24 00:00:21.536', NULL, 1, 1, 1, 359, 1049, NULL);


--
-- Data for Name: billing_one_shot_charge_inst; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_one_shot_charge_inst VALUES (1074, 1049, NULL, NULL);


--
-- Data for Name: billing_recurring_charge_inst; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_recurring_charge_inst VALUES ('2014-01-01 00:00:00', '2013-04-23 00:00:36.76', 1052, 330, 1051, 1049);
INSERT INTO billing_recurring_charge_inst VALUES ('2014-01-01 00:00:00', '2013-04-24 00:00:21.536', 1080, 376, 1079, 1049);






--
-- Data for Name: billing_wallet_operation; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_wallet_operation VALUES (1075, 0, false, '2013-04-24 10:51:45.634', NULL, 'MO_SUB_Microsoft', NULL, 0.00, 0.00, 0.00, NULL, '2013-04-24 00:00:45.543', '', '', '', 1.00, NULL, 'OPEN', '2013-04-23 00:00:00', 19.600000000000, NULL, NULL, 0.00, 0.00, 1, 1, NULL, 1074, NULL, 5, 1109, NULL);
INSERT INTO billing_wallet_operation VALUES (1076, 0, false, '2013-04-24 10:56:46.696', NULL, 'EXCH20102_SOFT_BLACK', NULL, 1.48, 7.57, 7.57, '2013-12-31 00:00:00', '2013-04-23 00:00:00', NULL, NULL, NULL, 0.76, '2013-04-23 00:00:00', 'OPEN', '2013-04-23 00:00:36.76', 19.600000000000, NULL, NULL, 12.00, 10.00, 1, 1, NULL, 1052, NULL, 5, 1109, NULL);
INSERT INTO billing_wallet_operation VALUES (1086, 0, false, '2013-04-24 15:55:48.509', NULL, 'MO20131_SOFT', NULL, 1.48, 7.54, 7.54, '2013-12-31 00:00:00', '2013-04-24 00:00:00', NULL, NULL, NULL, 0.75, '2013-04-24 00:00:00', 'OPEN', '2013-04-24 00:00:21.536', 19.600000000000, NULL, NULL, 12.00, 10.00, 1, 1, NULL, 1080, NULL, 5, 1109, NULL);

--
-- PostgreSQL database dump complete
--

