/*----------IMPORT FOR POSTGRES-----------*/

--
-- Data for Name: adm_user_log; Type: TABLE DATA; Schema: public; Owner: meveo
--

CREATE SEQUENCE ADM_USER_LOG_SEQ start with 1 increment by 1;
CREATE TABLE ADM_USER_LOG(ID NUMERIC(19,0) NOT NULL DEFAULT nextval('ADM_USER_LOG_SEQ'), USER_NAME VARCHAR(255) NOT NULL,USER_ID NUMERIC(19,0) NOT NULL,DATE_EXECUTED DATE,ACTION VARCHAR(255),URL VARCHAR(255) NOT NULL, OBJECT_ID VARCHAR(50),PRIMARY KEY (ID));
ALTER SEQUENCE ADM_USER_LOG_SEQ OWNED BY ADM_USER_LOG.ID;

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- Name: access_point_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('access_point_seq', 10000, false);


--
-- Name: access_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('access_seq', 10000, false);


--
-- Data for Name: adm_currency; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_currency VALUES (1, '2013-04-23 20:25:20.006133', NULL, 1, 'AFA', 'Afghani', true, NULL, NULL);
INSERT INTO adm_currency VALUES (2, '2013-04-23 20:25:20.006133', NULL, 1, 'ZAR', 'Rand', true, NULL, NULL);
INSERT INTO adm_currency VALUES (3, '2013-04-23 20:25:20.006133', NULL, 1, 'ALL', 'Lek', true, NULL, NULL);
INSERT INTO adm_currency VALUES (4, '2013-04-23 20:25:20.006133', NULL, 1, 'DZD', 'Dinar algérien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (5, '2013-04-23 20:25:20.006133', NULL, 1, 'EUR', 'Euro', true, NULL, NULL);
INSERT INTO adm_currency VALUES (6, '2013-04-23 20:25:20.006133', NULL, 1, 'AOA', 'Kwanza', true, NULL, NULL);
INSERT INTO adm_currency VALUES (7, '2013-04-23 20:25:20.006133', NULL, 1, 'XCD', 'Dollar des Cara bes de lEst', true, NULL, NULL);
INSERT INTO adm_currency VALUES (8, '2013-04-23 20:25:20.006133', NULL, 1, 'ANG', 'Florin des Antilles', true, NULL, NULL);
INSERT INTO adm_currency VALUES (9, '2013-04-23 20:25:20.006133', NULL, 1, 'SAR', 'Riyal saoudien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (10, '2013-04-23 20:25:20.006133', NULL, 1, 'ARS', 'Peso', true, NULL, NULL);
INSERT INTO adm_currency VALUES (11, '2013-04-23 20:25:20.006133', NULL, 1, 'AMD', 'Dram', true, NULL, NULL);
INSERT INTO adm_currency VALUES (12, '2013-04-23 20:25:20.006133', NULL, 1, 'AWG', 'Florin d Aruba', true, NULL, NULL);
INSERT INTO adm_currency VALUES (13, '2013-04-23 20:25:20.006133', NULL, 1, 'AUD', 'Dollar australien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (14, '2013-04-23 20:25:20.006133', NULL, 1, 'AZM', 'Manat azerbaïdjanais', true, NULL, NULL);
INSERT INTO adm_currency VALUES (15, '2013-04-23 20:25:20.006133', NULL, 1, 'BSD', 'Dollar des Bahamas', true, NULL, NULL);
INSERT INTO adm_currency VALUES (16, '2013-04-23 20:25:20.006133', NULL, 1, 'BHD', 'Dinar de Bahreïn', true, NULL, NULL);
INSERT INTO adm_currency VALUES (17, '2013-04-23 20:25:20.006133', NULL, 1, 'BDT', 'Taka', true, NULL, NULL);
INSERT INTO adm_currency VALUES (18, '2013-04-23 20:25:20.006133', NULL, 1, 'BBD', 'Dollar de Barbade', true, NULL, NULL);
INSERT INTO adm_currency VALUES (19, '2013-04-23 20:25:20.006133', NULL, 1, 'BZD', 'Dollar de Belize', true, NULL, NULL);
INSERT INTO adm_currency VALUES (20, '2013-04-23 20:25:20.006133', NULL, 1, 'XOF', 'Franc CFA - BCEAO', true, NULL, NULL);
INSERT INTO adm_currency VALUES (21, '2013-04-23 20:25:20.006133', NULL, 1, 'BMD', 'Dollar des Bermudes', true, NULL, NULL);
INSERT INTO adm_currency VALUES (22, '2013-04-23 20:25:20.006133', NULL, 1, 'BTN', 'Ngultrum', true, NULL, NULL);
INSERT INTO adm_currency VALUES (23, '2013-04-23 20:25:20.006133', NULL, 1, 'BYR', 'Rouble biãlorussie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (24, '2013-04-23 20:25:20.006133', NULL, 1, 'BOB', 'Boliviano', true, NULL, NULL);
INSERT INTO adm_currency VALUES (25, '2013-04-23 20:25:20.006133', NULL, 1, 'BAM', 'Mark bosniaque convertible', true, NULL, NULL);
INSERT INTO adm_currency VALUES (26, '2013-04-23 20:25:20.006133', NULL, 1, 'BWP', 'Pula', true, NULL, NULL);
INSERT INTO adm_currency VALUES (27, '2013-04-23 20:25:20.006133', NULL, 1, 'BRL', 'Real', true, NULL, NULL);
INSERT INTO adm_currency VALUES (28, '2013-04-23 20:25:20.006133', NULL, 1, 'BND', 'Dollar de Brunéi', true, NULL, NULL);
INSERT INTO adm_currency VALUES (29, '2013-04-23 20:25:20.006133', NULL, 1, 'BGN', 'Lev', true, NULL, NULL);
INSERT INTO adm_currency VALUES (30, '2013-04-23 20:25:20.006133', NULL, 1, 'BIF', 'Franc du Burundi', true, NULL, NULL);
INSERT INTO adm_currency VALUES (31, '2013-04-23 20:25:20.006133', NULL, 1, 'NOK', 'Couronne norvégienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (32, '2013-04-23 20:25:20.006133', NULL, 1, 'KYD', 'Dollar des îles Caïmanes', true, NULL, NULL);
INSERT INTO adm_currency VALUES (33, '2013-04-23 20:25:20.006133', NULL, 1, 'KHR', 'Riel', true, NULL, NULL);
INSERT INTO adm_currency VALUES (34, '2013-04-23 20:25:20.006133', NULL, 1, 'XAF', 'Franc CFA - BEAC', true, NULL, NULL);
INSERT INTO adm_currency VALUES (35, '2013-04-23 20:25:20.006133', NULL, 1, 'CAD', 'Dollar canadien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (36, '2013-04-23 20:25:20.006133', NULL, 1, 'CVE', 'Escudo du Cap-Vert', true, NULL, NULL);
INSERT INTO adm_currency VALUES (37, '2013-04-23 20:25:20.006133', NULL, 1, 'CFA', 'FRANC CFA-BEAC', true, NULL, NULL);
INSERT INTO adm_currency VALUES (38, '2013-04-23 20:25:20.006133', NULL, 1, 'CLP', 'Peso chilien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (39, '2013-04-23 20:25:20.006133', NULL, 1, 'CNY', 'Yuan Ren-Min-Bi', true, NULL, NULL);
INSERT INTO adm_currency VALUES (40, '2013-04-23 20:25:20.006133', NULL, 1, 'CYP', 'Livre chypriote', true, NULL, NULL);
INSERT INTO adm_currency VALUES (41, '2013-04-23 20:25:20.006133', NULL, 1, 'COP', 'Peso colombien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (42, '2013-04-23 20:25:20.006133', NULL, 1, 'KMF', 'Franc des Comores', true, NULL, NULL);
INSERT INTO adm_currency VALUES (43, '2013-04-23 20:25:20.006133', NULL, 1, 'CDF', 'FRANC DU CONGO DEMOCRATIQUE', true, NULL, NULL);
INSERT INTO adm_currency VALUES (44, '2013-04-23 20:25:20.006133', NULL, 1, 'KRW', 'Won', true, NULL, NULL);
INSERT INTO adm_currency VALUES (45, '2013-04-23 20:25:20.006133', NULL, 1, 'KPW', 'Won de la Corée du Nord', true, NULL, NULL);
INSERT INTO adm_currency VALUES (46, '2013-04-23 20:25:20.006133', NULL, 1, 'CRC', 'Colon de Costa Rica', true, NULL, NULL);
INSERT INTO adm_currency VALUES (47, '2013-04-23 20:25:20.006133', NULL, 1, 'HRK', 'Kuna', true, NULL, NULL);
INSERT INTO adm_currency VALUES (48, '2013-04-23 20:25:20.006133', NULL, 1, 'CUP', 'Peso cubain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (49, '2013-04-23 20:25:20.006133', NULL, 1, 'USD', 'Dollar des Etats-unis', true, NULL, NULL);
INSERT INTO adm_currency VALUES (50, '2013-04-23 20:25:20.006133', NULL, 1, 'DKK', 'Couronne danoise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (51, '2013-04-23 20:25:20.006133', NULL, 1, 'DJF', 'Franc de Djibouti', true, NULL, NULL);
INSERT INTO adm_currency VALUES (52, '2013-04-23 20:25:20.006133', NULL, 1, 'DOP', 'Peso dominicain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (53, '2013-04-23 20:25:20.006133', NULL, 1, 'EGP', 'Livre égyptienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (54, '2013-04-23 20:25:20.006133', NULL, 1, 'AED', 'Dirham des émirats arabes unis', true, NULL, NULL);
INSERT INTO adm_currency VALUES (55, '2013-04-23 20:25:20.006133', NULL, 1, 'ERN', 'Nafka', true, NULL, NULL);
INSERT INTO adm_currency VALUES (56, '2013-04-23 20:25:20.006133', NULL, 1, 'EEK', 'Couronne d Estonie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (57, '2013-04-23 20:25:20.006133', NULL, 1, 'ETB', 'Birr éthiopien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (58, '2013-04-23 20:25:20.006133', NULL, 1, 'FKP', 'Livre de Falkland', true, NULL, NULL);
INSERT INTO adm_currency VALUES (59, '2013-04-23 20:25:20.006133', NULL, 1, 'FJD', 'Dollar des Fidji', true, NULL, NULL);
INSERT INTO adm_currency VALUES (60, '2013-04-23 20:25:20.006133', NULL, 1, 'GMD', 'Dalasie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (61, '2013-04-23 20:25:20.006133', NULL, 1, 'GEL', 'Lari', true, NULL, NULL);
INSERT INTO adm_currency VALUES (62, '2013-04-23 20:25:20.006133', NULL, 1, 'GHC', 'Cedi ghanéen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (63, '2013-04-23 20:25:20.006133', NULL, 1, 'GIP', 'Livre de Gibraltar', true, NULL, NULL);
INSERT INTO adm_currency VALUES (64, '2013-04-23 20:25:20.006133', NULL, 1, 'GTQ', 'Quetzal', true, NULL, NULL);
INSERT INTO adm_currency VALUES (65, '2013-04-23 20:25:20.006133', NULL, 1, 'GBP', 'Livre sterling', true, NULL, NULL);
INSERT INTO adm_currency VALUES (66, '2013-04-23 20:25:20.006133', NULL, 1, 'GNF', 'Franc guinéen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (67, '2013-04-23 20:25:20.006133', NULL, 1, 'GYD', 'Dollar de Guyane', true, NULL, NULL);
INSERT INTO adm_currency VALUES (68, '2013-04-23 20:25:20.006133', NULL, 1, 'HTG', 'Gourde', true, NULL, NULL);
INSERT INTO adm_currency VALUES (69, '2013-04-23 20:25:20.006133', NULL, 1, 'HNL', 'Lempira', true, NULL, NULL);
INSERT INTO adm_currency VALUES (70, '2013-04-23 20:25:20.006133', NULL, 1, 'HKD', 'Dollar de Hong-Kong', true, NULL, NULL);
INSERT INTO adm_currency VALUES (71, '2013-04-23 20:25:20.006133', NULL, 1, 'HUF', 'Forint', true, NULL, NULL);
INSERT INTO adm_currency VALUES (72, '2013-04-23 20:25:20.006133', NULL, 1, 'NZD', 'Dollar néo-zélandais', true, NULL, NULL);
INSERT INTO adm_currency VALUES (73, '2013-04-23 20:25:20.006133', NULL, 1, 'INR', 'Roupie indienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (74, '2013-04-23 20:25:20.006133', NULL, 1, 'IDR', 'Rupiah', true, NULL, NULL);
INSERT INTO adm_currency VALUES (75, '2013-04-23 20:25:20.006133', NULL, 1, 'IRR', 'Rial iranien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (76, '2013-04-23 20:25:20.006133', NULL, 1, 'IQD', 'Dinar iraquien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (77, '2013-04-23 20:25:20.006133', NULL, 1, 'ISK', 'Couronne islandaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (78, '2013-04-23 20:25:20.006133', NULL, 1, 'ILS', 'Sheqel', true, NULL, NULL);
INSERT INTO adm_currency VALUES (79, '2013-04-23 20:25:20.006133', NULL, 1, 'JMD', 'Dollar jamaïcain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (80, '2013-04-23 20:25:20.006133', NULL, 1, 'JPY', 'Yen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (81, '2013-04-23 20:25:20.006133', NULL, 1, 'JOD', 'Dinar jordanien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (82, '2013-04-23 20:25:20.006133', NULL, 1, 'KZT', 'Tenge', true, NULL, NULL);
INSERT INTO adm_currency VALUES (83, '2013-04-23 20:25:20.006133', NULL, 1, 'KES', 'Shilling du Kenya', true, NULL, NULL);
INSERT INTO adm_currency VALUES (84, '2013-04-23 20:25:20.006133', NULL, 1, 'KGS', 'Som', true, NULL, NULL);
INSERT INTO adm_currency VALUES (85, '2013-04-23 20:25:20.006133', NULL, 1, 'KWD', 'Dinar koweïtien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (86, '2013-04-23 20:25:20.006133', NULL, 1, 'LAK', 'Kip', true, NULL, NULL);
INSERT INTO adm_currency VALUES (87, '2013-04-23 20:25:20.006133', NULL, 1, 'LSL', 'Loti', true, NULL, NULL);
INSERT INTO adm_currency VALUES (88, '2013-04-23 20:25:20.006133', NULL, 1, 'LVL', 'Lats letton', true, NULL, NULL);
INSERT INTO adm_currency VALUES (89, '2013-04-23 20:25:20.006133', NULL, 1, 'LBP', 'Livre libanaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (90, '2013-04-23 20:25:20.006133', NULL, 1, 'LRD', 'Dollar libérien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (91, '2013-04-23 20:25:20.006133', NULL, 1, 'LYD', 'Dinar libyen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (92, '2013-04-23 20:25:20.006133', NULL, 1, 'CHF', 'Franc suisse', true, NULL, NULL);
INSERT INTO adm_currency VALUES (93, '2013-04-23 20:25:20.006133', NULL, 1, 'LTL', 'Litas lituanien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (94, '2013-04-23 20:25:20.006133', NULL, 1, 'MOP', 'Pataca', true, NULL, NULL);
INSERT INTO adm_currency VALUES (95, '2013-04-23 20:25:20.006133', NULL, 1, 'MKD', 'Denar', true, NULL, NULL);
INSERT INTO adm_currency VALUES (96, '2013-04-23 20:25:20.006133', NULL, 1, 'MGA', 'Ariary malgache', true, NULL, NULL);
INSERT INTO adm_currency VALUES (97, '2013-04-23 20:25:20.006133', NULL, 1, 'MGF', 'Franc malgache', true, NULL, NULL);
INSERT INTO adm_currency VALUES (98, '2013-04-23 20:25:20.006133', NULL, 1, 'MYR', 'Ringgit de Malaisie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (99, '2013-04-23 20:25:20.006133', NULL, 1, 'MWK', 'Kwacha', true, NULL, NULL);
INSERT INTO adm_currency VALUES (100, '2013-04-23 20:25:20.006133', NULL, 1, 'MVR', 'Rufiyaa', true, NULL, NULL);
INSERT INTO adm_currency VALUES (101, '2013-04-23 20:25:20.006133', NULL, 1, 'MTL', 'Livre maltaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (102, '2013-04-23 20:25:20.006133', NULL, 1, 'MAD', 'Dirham marocain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (103, '2013-04-23 20:25:20.006133', NULL, 1, 'MUR', 'Roupie mauricienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (104, '2013-04-23 20:25:20.006133', NULL, 1, 'MRO', 'Ouguija', true, NULL, NULL);
INSERT INTO adm_currency VALUES (105, '2013-04-23 20:25:20.006133', NULL, 1, 'MXN', 'Peso mexicain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (106, '2013-04-23 20:25:20.006133', NULL, 1, 'MDL', 'Leu de Moldave', true, NULL, NULL);
INSERT INTO adm_currency VALUES (107, '2013-04-23 20:25:20.006133', NULL, 1, 'MNT', 'Tugrik', true, NULL, NULL);
INSERT INTO adm_currency VALUES (108, '2013-04-23 20:25:20.006133', NULL, 1, 'MZM', 'Metical', true, NULL, NULL);
INSERT INTO adm_currency VALUES (109, '2013-04-23 20:25:20.006133', NULL, 1, 'MMK', 'Kyat', true, NULL, NULL);
INSERT INTO adm_currency VALUES (110, '2013-04-23 20:25:20.006133', NULL, 1, 'NAD', 'Dollar namibien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (111, '2013-04-23 20:25:20.006133', NULL, 1, 'NPR', 'Roupie Népalaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (112, '2013-04-23 20:25:20.006133', NULL, 1, 'NIO', 'Cordoba oro', true, NULL, NULL);
INSERT INTO adm_currency VALUES (113, '2013-04-23 20:25:20.006133', NULL, 1, 'NGN', 'Naira', true, NULL, NULL);
INSERT INTO adm_currency VALUES (114, '2013-04-23 20:25:20.006133', NULL, 1, 'XPF', 'Franc CFP', true, NULL, NULL);
INSERT INTO adm_currency VALUES (115, '2013-04-23 20:25:20.006133', NULL, 1, 'OMR', 'Rial Omani', true, NULL, NULL);
INSERT INTO adm_currency VALUES (116, '2013-04-23 20:25:20.006133', NULL, 1, 'XAU', 'Opérations sur or', true, NULL, NULL);
INSERT INTO adm_currency VALUES (117, '2013-04-23 20:25:20.006133', NULL, 1, 'UGX', 'Shilling ougandais', true, NULL, NULL);
INSERT INTO adm_currency VALUES (118, '2013-04-23 20:25:20.006133', NULL, 1, 'UZS', 'Soum ouzbek', true, NULL, NULL);
INSERT INTO adm_currency VALUES (119, '2013-04-23 20:25:20.006133', NULL, 1, 'PKR', 'Roupie pakistanaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (120, '2013-04-23 20:25:20.006133', NULL, 1, 'PAB', 'Balboa', true, NULL, NULL);
INSERT INTO adm_currency VALUES (121, '2013-04-23 20:25:20.006133', NULL, 1, 'PGK', 'Kina', true, NULL, NULL);
INSERT INTO adm_currency VALUES (122, '2013-04-23 20:25:20.006133', NULL, 1, 'PYG', 'Guarani', true, NULL, NULL);
INSERT INTO adm_currency VALUES (123, '2013-04-23 20:25:20.006133', NULL, 1, 'PEN', 'Nouveau sol', true, NULL, NULL);
INSERT INTO adm_currency VALUES (124, '2013-04-23 20:25:20.006133', NULL, 1, 'PHP', 'Peso philippin', true, NULL, NULL);
INSERT INTO adm_currency VALUES (125, '2013-04-23 20:25:20.006133', NULL, 1, 'PLN', 'Zloty', true, NULL, NULL);
INSERT INTO adm_currency VALUES (126, '2013-04-23 20:25:20.006133', NULL, 1, 'QAR', 'Riyal du Qatar', true, NULL, NULL);
INSERT INTO adm_currency VALUES (127, '2013-04-23 20:25:20.006133', NULL, 1, 'RON', 'LEI (Nouveau Leu)', true, NULL, NULL);
INSERT INTO adm_currency VALUES (128, '2013-04-23 20:25:20.006133', NULL, 1, 'ROL', 'Leu', true, NULL, NULL);
INSERT INTO adm_currency VALUES (129, '2013-04-23 20:25:20.006133', NULL, 1, 'RUB', 'Rouble russe (nouveau)', true, NULL, NULL);
INSERT INTO adm_currency VALUES (130, '2013-04-23 20:25:20.006133', NULL, 1, 'RWF', 'Franc du Rwanda', true, NULL, NULL);
INSERT INTO adm_currency VALUES (131, '2013-04-23 20:25:20.006133', NULL, 1, 'SBD', 'Dollar des îles Salomon', true, NULL, NULL);
INSERT INTO adm_currency VALUES (132, '2013-04-23 20:25:20.006133', NULL, 1, 'SVC', 'Colon salvadorien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (133, '2013-04-23 20:25:20.006133', NULL, 1, 'WST', 'Tala', true, NULL, NULL);
INSERT INTO adm_currency VALUES (134, '2013-04-23 20:25:20.006133', NULL, 1, 'STD', 'Dobra', true, NULL, NULL);
INSERT INTO adm_currency VALUES (135, '2013-04-23 20:25:20.006133', NULL, 1, 'CSD', 'Dinar Serbe', true, NULL, NULL);
INSERT INTO adm_currency VALUES (136, '2013-04-23 20:25:20.006133', NULL, 1, 'SCR', 'Roupie des Seychelles', true, NULL, NULL);
INSERT INTO adm_currency VALUES (137, '2013-04-23 20:25:20.006133', NULL, 1, 'SLL', 'Leone', true, NULL, NULL);
INSERT INTO adm_currency VALUES (138, '2013-04-23 20:25:20.006133', NULL, 1, 'SGD', 'Dollar de Singapour', true, NULL, NULL);
INSERT INTO adm_currency VALUES (139, '2013-04-23 20:25:20.006133', NULL, 1, 'SKK', 'Couronne slovaque', true, NULL, NULL);
INSERT INTO adm_currency VALUES (140, '2013-04-23 20:25:20.006133', NULL, 1, 'SIT', 'Tolar', true, NULL, NULL);
INSERT INTO adm_currency VALUES (141, '2013-04-23 20:25:20.006133', NULL, 1, 'SOS', 'Shilling Somalien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (142, '2013-04-23 20:25:20.006133', NULL, 1, 'SDG', 'Livre soudanaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (143, '2013-04-23 20:25:20.006133', NULL, 1, 'LKR', 'Roupie de Sri Lanka', true, NULL, NULL);
INSERT INTO adm_currency VALUES (144, '2013-04-23 20:25:20.006133', NULL, 1, 'SHP', 'Livre de Sainte-Hélène', true, NULL, NULL);
INSERT INTO adm_currency VALUES (145, '2013-04-23 20:25:20.006133', NULL, 1, 'SEK', 'Couronne suédoise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (146, '2013-04-23 20:25:20.006133', NULL, 1, 'SRD', 'Florin du suriname', true, NULL, NULL);
INSERT INTO adm_currency VALUES (147, '2013-04-23 20:25:20.006133', NULL, 1, 'SZL', 'Lilangeni', true, NULL, NULL);
INSERT INTO adm_currency VALUES (148, '2013-04-23 20:25:20.006133', NULL, 1, 'SYP', 'Livre syrienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (149, '2013-04-23 20:25:20.006133', NULL, 1, 'TJS', 'Somoni', true, NULL, NULL);
INSERT INTO adm_currency VALUES (150, '2013-04-23 20:25:20.006133', NULL, 1, 'TWD', 'Nouveau dollar de Taïwan', true, NULL, NULL);
INSERT INTO adm_currency VALUES (151, '2013-04-23 20:25:20.006133', NULL, 1, 'TZS', 'Shilling tanzanien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (152, '2013-04-23 20:25:20.006133', NULL, 1, 'CZK', 'Couronne tchèque', true, NULL, NULL);
INSERT INTO adm_currency VALUES (153, '2013-04-23 20:25:20.006133', NULL, 1, 'THB', 'Baht', true, NULL, NULL);
INSERT INTO adm_currency VALUES (154, '2013-04-23 20:25:20.006133', NULL, 1, 'TOP', 'Pa anga', true, NULL, NULL);
INSERT INTO adm_currency VALUES (155, '2013-04-23 20:25:20.006133', NULL, 1, 'TTD', 'Dollar de Trinité et de Tobago', true, NULL, NULL);
INSERT INTO adm_currency VALUES (156, '2013-04-23 20:25:20.006133', NULL, 1, 'TND', 'Dinar tunisien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (157, '2013-04-23 20:25:20.006133', NULL, 1, 'TMM', 'Manat turkmène', true, NULL, NULL);
INSERT INTO adm_currency VALUES (158, '2013-04-23 20:25:20.006133', NULL, 1, 'TRY', 'Nouvelle Livre turque', true, NULL, NULL);
INSERT INTO adm_currency VALUES (159, '2013-04-23 20:25:20.006133', NULL, 1, 'TRL', 'Livre turque', true, NULL, NULL);
INSERT INTO adm_currency VALUES (160, '2013-04-23 20:25:20.006133', NULL, 1, 'UAH', 'HRYVNIA', true, NULL, NULL);
INSERT INTO adm_currency VALUES (161, '2013-04-23 20:25:20.006133', NULL, 1, 'UYU', 'Nouveau Peso uruguayen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (162, '2013-04-23 20:25:20.006133', NULL, 1, 'VUV', 'Vatu', true, NULL, NULL);
INSERT INTO adm_currency VALUES (163, '2013-04-23 20:25:20.006133', NULL, 1, 'VEF', 'Bolivar Fuerte', true, NULL, NULL);
INSERT INTO adm_currency VALUES (164, '2013-04-23 20:25:20.006133', NULL, 1, 'VND', 'Dong', true, NULL, NULL);
INSERT INTO adm_currency VALUES (165, '2013-04-23 20:25:20.006133', NULL, 1, 'YER', 'Riyal yéménite', true, NULL, NULL);
INSERT INTO adm_currency VALUES (166, '2013-04-23 20:25:20.006133', NULL, 1, 'ZMK', 'Kwacha de Zambie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (167, '2013-04-23 20:25:20.006133', NULL, 1, 'ZWD', 'Dollar du Zimbabwe', true, NULL, NULL);
INSERT INTO adm_currency VALUES (168, '2013-04-23 20:25:20.006133', NULL, 1, 'GHS', 'Cedi ghanéen', true, NULL, NULL);

--
-- Data for Name: adm_language; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_language VALUES (1, '2013-04-23 20:25:20.006133', NULL, 1, 'allemand', 'DEU', NULL, NULL);
INSERT INTO adm_language VALUES (2, '2013-04-23 20:25:20.006133', NULL, 1, 'anglais', 'ENG', NULL, NULL);
INSERT INTO adm_language VALUES (3, '2013-04-23 20:25:20.006133', NULL, 1, 'arabe', 'ARA', NULL, NULL);
INSERT INTO adm_language VALUES (4, '2013-04-23 20:25:20.006133', NULL, 1, 'bulgare', 'BUL', NULL, NULL);
INSERT INTO adm_language VALUES (5, '2013-04-23 20:25:20.006133', NULL, 1, 'catalan', 'CAT', NULL, NULL);
INSERT INTO adm_language VALUES (6, '2013-04-23 20:25:20.006133', NULL, 1, 'chinois', 'ZHO', NULL, NULL);
INSERT INTO adm_language VALUES (7, '2013-04-23 20:25:20.006133', NULL, 1, 'danois', 'DAN', NULL, NULL);
INSERT INTO adm_language VALUES (8, '2013-04-23 20:25:20.006133', NULL, 1, 'espagnol', 'ESL', NULL, NULL);
INSERT INTO adm_language VALUES (9, '2013-04-23 20:25:20.006133', NULL, 1, 'estonien', 'EST', NULL, NULL);
INSERT INTO adm_language VALUES (10, '2013-04-23 20:25:20.006133', NULL, 1, 'féroïen', 'FAO', NULL, NULL);
INSERT INTO adm_language VALUES (11, '2013-04-23 20:25:20.006133', NULL, 1, 'finlandais', 'FIN', NULL, NULL);
INSERT INTO adm_language VALUES (12, '2013-04-23 20:25:20.006133', NULL, 1, 'français', 'FRA', NULL, NULL);
INSERT INTO adm_language VALUES (13, '2013-04-23 20:25:20.006133', NULL, 1, 'grec', 'ELL', NULL, NULL);
INSERT INTO adm_language VALUES (14, '2013-04-23 20:25:20.006133', NULL, 1, 'hindi', 'HIN', NULL, NULL);
INSERT INTO adm_language VALUES (15, '2013-04-23 20:25:20.006133', NULL, 1, 'hongrois', 'HUN', NULL, NULL);
INSERT INTO adm_language VALUES (16, '2013-04-23 20:25:20.006133', NULL, 1, 'islandais', 'ISL', NULL, NULL);
INSERT INTO adm_language VALUES (17, '2013-04-23 20:25:20.006133', NULL, 1, 'italien', 'ITA', NULL, NULL);
INSERT INTO adm_language VALUES (18, '2013-04-23 20:25:20.006133', NULL, 1, 'japonais', 'JPN', NULL, NULL);
INSERT INTO adm_language VALUES (19, '2013-04-23 20:25:20.006133', NULL, 1, 'letton', 'LAV', NULL, NULL);
INSERT INTO adm_language VALUES (20, '2013-04-23 20:25:20.006133', NULL, 1, 'lituanien', 'LIT', NULL, NULL);
INSERT INTO adm_language VALUES (21, '2013-04-23 20:25:20.006133', NULL, 1, 'néerlandais', 'NLD', NULL, NULL);
INSERT INTO adm_language VALUES (22, '2013-04-23 20:25:20.006133', NULL, 1, 'norvégien', 'NOR', NULL, NULL);
INSERT INTO adm_language VALUES (23, '2013-04-23 20:25:20.006133', NULL, 1, 'polonais', 'POL', NULL, NULL);
INSERT INTO adm_language VALUES (24, '2013-04-23 20:25:20.006133', NULL, 1, 'portugais', 'POR', NULL, NULL);
INSERT INTO adm_language VALUES (25, '2013-04-23 20:25:20.006133', NULL, 1, 'roumain', 'RON', NULL, NULL);
INSERT INTO adm_language VALUES (26, '2013-04-23 20:25:20.006133', NULL, 1, 'russe', 'RUS', NULL, NULL);
INSERT INTO adm_language VALUES (27, '2013-04-23 20:25:20.006133', NULL, 1, 'serbe', 'SRP', NULL, NULL);
INSERT INTO adm_language VALUES (28, '2013-04-23 20:25:20.006133', NULL, 1, 'slovaque', 'SLK', NULL, NULL);
INSERT INTO adm_language VALUES (29, '2013-04-23 20:25:20.006133', NULL, 1, 'slovène', 'SLV', NULL, NULL);
INSERT INTO adm_language VALUES (30, '2013-04-23 20:25:20.006133', NULL, 1, 'suédois', 'SVE', NULL, NULL);
INSERT INTO adm_language VALUES (31, '2013-04-23 20:25:20.006133', NULL, 1, 'tchèque', 'CES', NULL, NULL);
INSERT INTO adm_language VALUES (32, '2013-04-23 20:25:20.006133', NULL, 1, 'turc', 'TUR', NULL, NULL);


--
-- Data for Name: adm_country; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_country VALUES (1, '2013-04-23 20:25:20.006133', NULL, 1, 'AD', 'Andorra', NULL, NULL, 5, 8);
INSERT INTO adm_country VALUES (2, '2013-04-23 20:25:20.006133', NULL, 1, 'AE', 'United Arab Emirates', NULL, NULL, 54, 3);
INSERT INTO adm_country VALUES (3, '2013-04-23 20:25:20.006133', NULL, 1, 'AF', 'Afghanistan', NULL, NULL, 1, 2);
INSERT INTO adm_country VALUES (4, '2013-04-23 20:25:20.006133', NULL, 1, 'AG', 'Antigua and Barbuda', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (5, '2013-04-23 20:25:20.006133', NULL, 1, 'AI', 'Anguilla', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (6, '2013-04-23 20:25:20.006133', NULL, 1, 'AL', 'Albania', NULL, NULL, 3, 2);
INSERT INTO adm_country VALUES (7, '2013-04-23 20:25:20.006133', NULL, 1, 'AM', 'Armenia', NULL, NULL, 11, 2);
INSERT INTO adm_country VALUES (8, '2013-04-23 20:25:20.006133', NULL, 1, 'AN', 'Netherlands Antilles', NULL, NULL, 8, 21);
INSERT INTO adm_country VALUES (9, '2013-04-23 20:25:20.006133', NULL, 1, 'AO', 'Angola', NULL, NULL, 6, 2);
INSERT INTO adm_country VALUES (10, '2013-04-23 20:25:20.006133', NULL, 1, 'AR', 'Argentina', NULL, NULL, 10, 8);
INSERT INTO adm_country VALUES (11, '2013-04-23 20:25:20.006133', NULL, 1, 'AS', 'American Samoa', NULL, NULL, 49, 1);
INSERT INTO adm_country VALUES (12, '2013-04-23 20:25:20.006133', NULL, 1, 'AT', 'Austria', NULL, NULL, 5, 1);
INSERT INTO adm_country VALUES (13, '2013-04-23 20:25:20.006133', NULL, 1, 'AU', 'Australia', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (14, '2013-04-23 20:25:20.006133', NULL, 1, 'AW', 'Aruba', NULL, NULL, 12, 2);
INSERT INTO adm_country VALUES (15, '2013-04-23 20:25:20.006133', NULL, 1, 'AZ', 'Azerbaijan', NULL, NULL, 6, 2);
INSERT INTO adm_country VALUES (16, '2013-04-23 20:25:20.006133', NULL, 1, 'BA', 'Bosnia and Herzegovina', NULL, NULL, 25, 2);
INSERT INTO adm_country VALUES (17, '2013-04-23 20:25:20.006133', NULL, 1, 'BB', 'Barbados', NULL, NULL, 18, 2);
INSERT INTO adm_country VALUES (18, '2013-04-23 20:25:20.006133', NULL, 1, 'BD', 'Bangladesh', NULL, NULL, 17, 2);
INSERT INTO adm_country VALUES (19, '2013-04-23 20:25:20.006133', NULL, 1, 'BE', 'Belgium', NULL, NULL, 5, 12);
INSERT INTO adm_country VALUES (20, '2013-04-23 20:25:20.006133', NULL, 1, 'BF', 'Burkina Faso', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (21, '2013-04-23 20:25:20.006133', NULL, 1, 'BG', 'Bulgaria', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (22, '2013-04-23 20:25:20.006133', NULL, 1, 'BH', 'Bahrain', NULL, NULL, 16, 2);
INSERT INTO adm_country VALUES (23, '2013-04-23 20:25:20.006133', NULL, 1, 'BI', 'Burundi', NULL, NULL, 30, 2);
INSERT INTO adm_country VALUES (24, '2013-04-23 20:25:20.006133', NULL, 1, 'BJ', 'Benin', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (25, '2013-04-23 20:25:20.006133', NULL, 1, 'BM', 'Bermuda', NULL, NULL, 21, 2);
INSERT INTO adm_country VALUES (26, '2013-04-23 20:25:20.006133', NULL, 1, 'BN', 'Brunei Darussalam', NULL, NULL, 28, 2);
INSERT INTO adm_country VALUES (27, '2013-04-23 20:25:20.006133', NULL, 1, 'BO', 'Bolivia', NULL, NULL, 24, 2);
INSERT INTO adm_country VALUES (28, '2013-04-23 20:25:20.006133', NULL, 1, 'BR', 'Brazil', NULL, NULL, 27, 2);
INSERT INTO adm_country VALUES (29, '2013-04-23 20:25:20.006133', NULL, 1, 'BS', 'The Bahamas', NULL, NULL, 15, 2);
INSERT INTO adm_country VALUES (30, '2013-04-23 20:25:20.006133', NULL, 1, 'BT', 'Bhutan', NULL, NULL, 16, 2);
INSERT INTO adm_country VALUES (31, '2013-04-23 20:25:20.006133', NULL, 1, 'BV', 'Bouvet Island', NULL, NULL, 31, 2);
INSERT INTO adm_country VALUES (32, '2013-04-23 20:25:20.006133', NULL, 1, 'BW', 'Botswana', NULL, NULL, 26, 2);
INSERT INTO adm_country VALUES (33, '2013-04-23 20:25:20.006133', NULL, 1, 'BY', 'Belarus', NULL, NULL, 23, 2);
INSERT INTO adm_country VALUES (34, '2013-04-23 20:25:20.006133', NULL, 1, 'BZ', 'Belize', NULL, NULL, 19, 2);
INSERT INTO adm_country VALUES (35, '2013-04-23 20:25:20.006133', NULL, 1, 'CA', 'Canada', NULL, NULL, 35, 2);
INSERT INTO adm_country VALUES (36, '2013-04-23 20:25:20.006133', NULL, 1, 'CC', 'Cocos (Keeling) Islands', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (37, '2013-04-23 20:25:20.006133', NULL, 1, 'CD', 'Congo, Democratic Republic of th', NULL, NULL, 43, 12);
INSERT INTO adm_country VALUES (38, '2013-04-23 20:25:20.006133', NULL, 1, 'CF', 'Central African Republic', NULL, NULL, 34, 12);
INSERT INTO adm_country VALUES (39, '2013-04-23 20:25:20.006133', NULL, 1, 'CG', 'Congo, Republic of the', NULL, NULL, 34, 12);
INSERT INTO adm_country VALUES (40, '2013-04-23 20:25:20.006133', NULL, 1, 'CH', 'Switzerland', NULL, NULL, 92, 2);
INSERT INTO adm_country VALUES (41, '2013-04-23 20:25:20.006133', NULL, 1, 'CI', 'Cote Ivoire', NULL, NULL, 20, 12);
INSERT INTO adm_country VALUES (42, '2013-04-23 20:25:20.006133', NULL, 1, 'CK', 'Cook Islands', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (43, '2013-04-23 20:25:20.006133', NULL, 1, 'CL', 'Chile', NULL, NULL, 38, 2);
INSERT INTO adm_country VALUES (44, '2013-04-23 20:25:20.006133', NULL, 1, 'CM', 'Cameroon', NULL, NULL, 34, 12);
INSERT INTO adm_country VALUES (45, '2013-04-23 20:25:20.006133', NULL, 1, 'CN', 'China', NULL, NULL, 39, 6);
INSERT INTO adm_country VALUES (46, '2013-04-23 20:25:20.006133', NULL, 1, 'CO', 'Colombia', NULL, NULL, 41, 8);
INSERT INTO adm_country VALUES (47, '2013-04-23 20:25:20.006133', NULL, 1, 'CR', 'Costa Rica', NULL, NULL, 46, 8);
INSERT INTO adm_country VALUES (48, '2013-04-23 20:25:20.006133', NULL, 1, 'CU', 'Cuba', NULL, NULL, 48, 8);
INSERT INTO adm_country VALUES (49, '2013-04-23 20:25:20.006133', NULL, 1, 'CV', 'Cape Verde', NULL, NULL, 36, 8);
INSERT INTO adm_country VALUES (50, '2013-04-23 20:25:20.006133', NULL, 1, 'CX', 'Christmas Island', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (51, '2013-04-23 20:25:20.006133', NULL, 1, 'CY', 'Cyprus', NULL, NULL, 40, 2);
INSERT INTO adm_country VALUES (52, '2013-04-23 20:25:20.006133', NULL, 1, 'CZ', 'Czech Republic', NULL, NULL, 152, 2);
INSERT INTO adm_country VALUES (53, '2013-04-23 20:25:20.006133', NULL, 1, 'DE', 'Germany', NULL, NULL, 5, 1);
INSERT INTO adm_country VALUES (54, '2013-04-23 20:25:20.006133', NULL, 1, 'DJ', 'Djibouti', NULL, NULL, 51, 12);
INSERT INTO adm_country VALUES (55, '2013-04-23 20:25:20.006133', NULL, 1, 'DK', 'Denmark', NULL, NULL, 50, 2);
INSERT INTO adm_country VALUES (56, '2013-04-23 20:25:20.006133', NULL, 1, 'DM', 'Dominica', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (57, '2013-04-23 20:25:20.006133', NULL, 1, 'DO', 'Dominican Republic', NULL, NULL, 52, 2);
INSERT INTO adm_country VALUES (58, '2013-04-23 20:25:20.006133', NULL, 1, 'DZ', 'Algeria', NULL, NULL, 4, 8);
INSERT INTO adm_country VALUES (59, '2013-04-23 20:25:20.006133', NULL, 1, 'EC', 'Ecuador', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (60, '2013-04-23 20:25:20.006133', NULL, 1, 'EE', 'Estonia', NULL, NULL, 56, 2);
INSERT INTO adm_country VALUES (61, '2013-04-23 20:25:20.006133', NULL, 1, 'EG', 'Egypt', NULL, NULL, 53, 8);
INSERT INTO adm_country VALUES (62, '2013-04-23 20:25:20.006133', NULL, 1, 'ER', 'Eritrea', NULL, NULL, 55, 2);
INSERT INTO adm_country VALUES (63, '2013-04-23 20:25:20.006133', NULL, 1, 'ES', 'Spain', NULL, NULL, 5, 8);
INSERT INTO adm_country VALUES (64, '2013-04-23 20:25:20.006133', NULL, 1, 'ET', 'Ethiopia', NULL, NULL, 57, 2);
INSERT INTO adm_country VALUES (65, '2013-04-23 20:25:20.006133', NULL, 1, 'FI', 'Finland', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (66, '2013-04-23 20:25:20.006133', NULL, 1, 'FJ', 'Fiji', NULL, NULL, 59, 2);
INSERT INTO adm_country VALUES (67, '2013-04-23 20:25:20.006133', NULL, 1, 'FK', 'Falkland Islands (Islas Malvinas', NULL, NULL, 58, 2);
INSERT INTO adm_country VALUES (68, '2013-04-23 20:25:20.006133', NULL, 1, 'FM', 'Micronesia, Federated States of', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (69, '2013-04-23 20:25:20.006133', NULL, 1, 'FO', 'Faroe Islands', NULL, NULL, 50, 2);
INSERT INTO adm_country VALUES (70, '2013-04-23 20:25:20.006133', NULL, 1, 'FR', 'France', NULL, NULL, 5, 12);
INSERT INTO adm_country VALUES (71, '2013-04-23 20:25:20.006133', NULL, 1, 'GA', 'Gabon', NULL, NULL, 34, 12);
INSERT INTO adm_country VALUES (72, '2013-04-23 20:25:20.006133', NULL, 1, 'GD', 'Grenada', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (73, '2013-04-23 20:25:20.006133', NULL, 1, 'GE', 'Georgia', NULL, NULL, 61, 2);
INSERT INTO adm_country VALUES (74, '2013-04-23 20:25:20.006133', NULL, 1, 'GF', 'French Guiana', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (75, '2013-04-23 20:25:20.006133', NULL, 1, 'GH', 'Ghana', NULL, NULL, 62, 2);
INSERT INTO adm_country VALUES (76, '2013-04-23 20:25:20.006133', NULL, 1, 'GI', 'Gibraltar', NULL, NULL, 63, 2);
INSERT INTO adm_country VALUES (77, '2013-04-23 20:25:20.006133', NULL, 1, 'GL', 'Greenland', NULL, NULL, 50, 2);
INSERT INTO adm_country VALUES (78, '2013-04-23 20:25:20.006133', NULL, 1, 'GM', 'The Gambia', NULL, NULL, 60, 2);
INSERT INTO adm_country VALUES (79, '2013-04-23 20:25:20.006133', NULL, 1, 'GN', 'Guinea', NULL, NULL, 66, 2);
INSERT INTO adm_country VALUES (80, '2013-04-23 20:25:20.006133', NULL, 1, 'GP', 'Guadeloupe', NULL, NULL, 5, 12);
INSERT INTO adm_country VALUES (81, '2013-04-23 20:25:20.006133', NULL, 1, 'GQ', 'Equatorial Guinea', NULL, NULL, 34, 2);
INSERT INTO adm_country VALUES (82, '2013-04-23 20:25:20.006133', NULL, 1, 'GR', 'Greece', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (83, '2013-04-23 20:25:20.006133', NULL, 1, 'GS', 'South Georgia and the South Sand', NULL, NULL, 65, 2);
INSERT INTO adm_country VALUES (84, '2013-04-23 20:25:20.006133', NULL, 1, 'GT', 'Guatemala', NULL, NULL, 64, 2);
INSERT INTO adm_country VALUES (85, '2013-04-23 20:25:20.006133', NULL, 1, 'GU', 'Guam', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (86, '2013-04-23 20:25:20.006133', NULL, 1, 'GW', 'Guinea-Bissau', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (87, '2013-04-23 20:25:20.006133', NULL, 1, 'GY', 'Guyana', NULL, NULL, 67, 2);
INSERT INTO adm_country VALUES (88, '2013-04-23 20:25:20.006133', NULL, 1, 'HK', 'Hong Kong (SAR)', NULL, NULL, 70, 2);
INSERT INTO adm_country VALUES (89, '2013-04-23 20:25:20.006133', NULL, 1, 'HM', 'Heard Island and McDonald Island', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (90, '2013-04-23 20:25:20.006133', NULL, 1, 'HN', 'Honduras', NULL, NULL, 69, 2);
INSERT INTO adm_country VALUES (91, '2013-04-23 20:25:20.006133', NULL, 1, 'HR', 'Croatia', NULL, NULL, 47, 2);
INSERT INTO adm_country VALUES (92, '2013-04-23 20:25:20.006133', NULL, 1, 'HT', 'Haiti', NULL, NULL, 68, 2);
INSERT INTO adm_country VALUES (93, '2013-04-23 20:25:20.006133', NULL, 1, 'HU', 'Hungary', NULL, NULL, 71, 2);
INSERT INTO adm_country VALUES (94, '2013-04-23 20:25:20.006133', NULL, 1, 'ID', 'Indonesia', NULL, NULL, 74, 2);
INSERT INTO adm_country VALUES (95, '2013-04-23 20:25:20.006133', NULL, 1, 'IE', 'Ireland', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (96, '2013-04-23 20:25:20.006133', NULL, 1, 'IL', 'Israel', NULL, NULL, 78, 2);
INSERT INTO adm_country VALUES (97, '2013-04-23 20:25:20.006133', NULL, 1, 'IN', 'India', NULL, NULL, 73, 2);
INSERT INTO adm_country VALUES (98, '2013-04-23 20:25:20.006133', NULL, 1, 'IO', 'British Indian Ocean Territory', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (99, '2013-04-23 20:25:20.006133', NULL, 1, 'IQ', 'Iraq', NULL, NULL, 76, 8);
INSERT INTO adm_country VALUES (100, '2013-04-23 20:25:20.006133', NULL, 1, 'IR', 'Iran', NULL, NULL, 75, 2);
INSERT INTO adm_country VALUES (101, '2013-04-23 20:25:20.006133', NULL, 1, 'IS', 'Iceland', NULL, NULL, 77, 2);
INSERT INTO adm_country VALUES (102, '2013-04-23 20:25:20.006133', NULL, 1, 'IT', 'Italy', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (103, '2013-04-23 20:25:20.006133', NULL, 1, 'JM', 'Jamaica', NULL, NULL, 79, 2);
INSERT INTO adm_country VALUES (104, '2013-04-23 20:25:20.006133', NULL, 1, 'JO', 'Jordan', NULL, NULL, 81, 2);
INSERT INTO adm_country VALUES (105, '2013-04-23 20:25:20.006133', NULL, 1, 'JP', 'Japan', NULL, NULL, 80, 2);
INSERT INTO adm_country VALUES (106, '2013-04-23 20:25:20.006133', NULL, 1, 'KE', 'Kenya', NULL, NULL, 83, 2);
INSERT INTO adm_country VALUES (107, '2013-04-23 20:25:20.006133', NULL, 1, 'KG', 'Kyrgyzstan', NULL, NULL, 84, 2);
INSERT INTO adm_country VALUES (108, '2013-04-23 20:25:20.006133', NULL, 1, 'KH', 'Cambodia', NULL, NULL, 33, 2);
INSERT INTO adm_country VALUES (109, '2013-04-23 20:25:20.006133', NULL, 1, 'KI', 'Kiribati', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (110, '2013-04-23 20:25:20.006133', NULL, 1, 'KM', 'Comoros', NULL, NULL, 42, 2);
INSERT INTO adm_country VALUES (111, '2013-04-23 20:25:20.006133', NULL, 1, 'KN', 'Saint Kitts and Nevis', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (112, '2013-04-23 20:25:20.006133', NULL, 1, 'KP', 'Korea, North', NULL, NULL, 45, 2);
INSERT INTO adm_country VALUES (113, '2013-04-23 20:25:20.006133', NULL, 1, 'KR', 'Korea, South', NULL, NULL, 44, 2);
INSERT INTO adm_country VALUES (114, '2013-04-23 20:25:20.006133', NULL, 1, 'KW', 'Kuwait', NULL, NULL, 85, 2);
INSERT INTO adm_country VALUES (115, '2013-04-23 20:25:20.006133', NULL, 1, 'KY', 'Cayman Islands', NULL, NULL, 32, 2);
INSERT INTO adm_country VALUES (116, '2013-04-23 20:25:20.006133', NULL, 1, 'KZ', 'Kazakhstan', NULL, NULL, 82, 2);
INSERT INTO adm_country VALUES (117, '2013-04-23 20:25:20.006133', NULL, 1, 'LA', 'Laos', NULL, NULL, 86, 2);
INSERT INTO adm_country VALUES (118, '2013-04-23 20:25:20.006133', NULL, 1, 'LB', 'Lebanon', NULL, NULL, 89, 2);
INSERT INTO adm_country VALUES (119, '2013-04-23 20:25:20.006133', NULL, 1, 'LC', 'Saint Lucia', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (120, '2013-04-23 20:25:20.006133', NULL, 1, 'LI', 'Liechtenstein', NULL, NULL, 92, 2);
INSERT INTO adm_country VALUES (121, '2013-04-23 20:25:20.006133', NULL, 1, 'LK', 'Sri Lanka', NULL, NULL, 143, 2);
INSERT INTO adm_country VALUES (122, '2013-04-23 20:25:20.006133', NULL, 1, 'LR', 'Liberia', NULL, NULL, 90, 2);
INSERT INTO adm_country VALUES (123, '2013-04-23 20:25:20.006133', NULL, 1, 'LS', 'Lesotho', NULL, NULL, 87, 2);
INSERT INTO adm_country VALUES (124, '2013-04-23 20:25:20.006133', NULL, 1, 'LT', 'Lithuania', NULL, NULL, 93, 2);
INSERT INTO adm_country VALUES (125, '2013-04-23 20:25:20.006133', NULL, 1, 'LU', 'Luxembourg', NULL, NULL, 5, 12);
INSERT INTO adm_country VALUES (126, '2013-04-23 20:25:20.006133', NULL, 1, 'LV', 'Latvia', NULL, NULL, 88, 2);
INSERT INTO adm_country VALUES (127, '2013-04-23 20:25:20.006133', NULL, 1, 'LY', 'Libya', NULL, NULL, 91, 2);
INSERT INTO adm_country VALUES (128, '2013-04-23 20:25:20.006133', NULL, 1, 'MA', 'Morocco', NULL, NULL, 102, 3);
INSERT INTO adm_country VALUES (129, '2013-04-23 20:25:20.006133', NULL, 1, 'MC', 'Monaco', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (130, '2013-04-23 20:25:20.006133', NULL, 1, 'MD', 'Moldova', NULL, NULL, 106, 2);
INSERT INTO adm_country VALUES (131, '2013-04-23 20:25:20.006133', NULL, 1, 'MG', 'Madagascar', NULL, NULL, 97, 2);
INSERT INTO adm_country VALUES (132, '2013-04-23 20:25:20.006133', NULL, 1, 'MH', 'Marshall Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (133, '2013-04-23 20:25:20.006133', NULL, 1, 'MK', 'Macedonia, The Former Yugoslav R', NULL, NULL, 95, 2);
INSERT INTO adm_country VALUES (134, '2013-04-23 20:25:20.006133', NULL, 1, 'ML', 'Mali', NULL, NULL, 20, 12);
INSERT INTO adm_country VALUES (135, '2013-04-23 20:25:20.006133', NULL, 1, 'MM', 'Burma', NULL, NULL, 109, 2);
INSERT INTO adm_country VALUES (136, '2013-04-23 20:25:20.006133', NULL, 1, 'MN', 'Mongolia', NULL, NULL, 107, 2);
INSERT INTO adm_country VALUES (137, '2013-04-23 20:25:20.006133', NULL, 1, 'MO', 'Macao', NULL, NULL, 94, 2);
INSERT INTO adm_country VALUES (138, '2013-04-23 20:25:20.006133', NULL, 1, 'MP', 'Northern Mariana Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (139, '2013-04-23 20:25:20.006133', NULL, 1, 'MQ', 'Martinique', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (140, '2013-04-23 20:25:20.006133', NULL, 1, 'MR', 'Mauritania', NULL, NULL, 104, 2);
INSERT INTO adm_country VALUES (141, '2013-04-23 20:25:20.006133', NULL, 1, 'MS', 'Montserrat', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (142, '2013-04-23 20:25:20.006133', NULL, 1, 'MT', 'Malta', NULL, NULL, 101, 2);
INSERT INTO adm_country VALUES (143, '2013-04-23 20:25:20.006133', NULL, 1, 'MU', 'Mauritius', NULL, NULL, 103, 2);
INSERT INTO adm_country VALUES (144, '2013-04-23 20:25:20.006133', NULL, 1, 'MV', 'Maldives', NULL, NULL, 100, 2);
INSERT INTO adm_country VALUES (145, '2013-04-23 20:25:20.006133', NULL, 1, 'MW', 'Malawi', NULL, NULL, 99, 2);
INSERT INTO adm_country VALUES (146, '2013-04-23 20:25:20.006133', NULL, 1, 'MX', 'Mexico', NULL, NULL, 105, 2);
INSERT INTO adm_country VALUES (147, '2013-04-23 20:25:20.006133', NULL, 1, 'MY', 'Malaysia', NULL, NULL, 98, 2);
INSERT INTO adm_country VALUES (148, '2013-04-23 20:25:20.006133', NULL, 1, 'MZ', 'Mozambique', NULL, NULL, 108, 2);
INSERT INTO adm_country VALUES (149, '2013-04-23 20:25:20.006133', NULL, 1, 'NA', 'Namibia', NULL, NULL, 110, 2);
INSERT INTO adm_country VALUES (150, '2013-04-23 20:25:20.006133', NULL, 1, 'NC', 'New Caledonia', NULL, NULL, 114, 2);
INSERT INTO adm_country VALUES (151, '2013-04-23 20:25:20.006133', NULL, 1, 'NE', 'Niger', NULL, NULL, 20, 12);
INSERT INTO adm_country VALUES (152, '2013-04-23 20:25:20.006133', NULL, 1, 'NF', 'Norfolk Island', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (153, '2013-04-23 20:25:20.006133', NULL, 1, 'NG', 'Nigeria', NULL, NULL, 113, 2);
INSERT INTO adm_country VALUES (154, '2013-04-23 20:25:20.006133', NULL, 1, 'NI', 'Nicaragua', NULL, NULL, 112, 8);
INSERT INTO adm_country VALUES (155, '2013-04-23 20:25:20.006133', NULL, 1, 'NL', 'Netherlands', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (156, '2013-04-23 20:25:20.006133', NULL, 1, 'NO', 'Norway', NULL, NULL, 31, 2);
INSERT INTO adm_country VALUES (157, '2013-04-23 20:25:20.006133', NULL, 1, 'NP', 'Nepal', NULL, NULL, 111, 2);
INSERT INTO adm_country VALUES (158, '2013-04-23 20:25:20.006133', NULL, 1, 'NR', 'Nauru', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (159, '2013-04-23 20:25:20.006133', NULL, 1, 'NU', 'Niue', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (160, '2013-04-23 20:25:20.006133', NULL, 1, 'NZ', 'New Zealand', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (161, '2013-04-23 20:25:20.006133', NULL, 1, 'OM', 'Oman', NULL, NULL, 115, 2);
INSERT INTO adm_country VALUES (162, '2013-04-23 20:25:20.006133', NULL, 1, 'PA', 'Panama', NULL, NULL, 120, 8);
INSERT INTO adm_country VALUES (163, '2013-04-23 20:25:20.006133', NULL, 1, 'PE', 'Peru', NULL, NULL, 123, 8);
INSERT INTO adm_country VALUES (164, '2013-04-23 20:25:20.006133', NULL, 1, 'PF', 'French Polynesia', NULL, NULL, 114, 12);
INSERT INTO adm_country VALUES (165, '2013-04-23 20:25:20.006133', NULL, 1, 'PG', 'Papua New Guinea', NULL, NULL, 121, 12);
INSERT INTO adm_country VALUES (166, '2013-04-23 20:25:20.006133', NULL, 1, 'PH', 'Philippines', NULL, NULL, 124, 2);
INSERT INTO adm_country VALUES (167, '2013-04-23 20:25:20.006133', NULL, 1, 'PK', 'Pakistan', NULL, NULL, 119, 2);
INSERT INTO adm_country VALUES (168, '2013-04-23 20:25:20.006133', NULL, 1, 'PL', 'Poland', NULL, NULL, 125, 2);
INSERT INTO adm_country VALUES (169, '2013-04-23 20:25:20.006133', NULL, 1, 'PM', 'Saint Pierre and Miquelon', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (170, '2013-04-23 20:25:20.006133', NULL, 1, 'PN', 'Pitcairn Islands', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (171, '2013-04-23 20:25:20.006133', NULL, 1, 'PR', 'Puerto Rico', NULL, NULL, 49, 8);
INSERT INTO adm_country VALUES (172, '2013-04-23 20:25:20.006133', NULL, 1, 'PS', 'Palestinian Territory, Occupied', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (173, '2013-04-23 20:25:20.006133', NULL, 1, 'PT', 'Portugal', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (174, '2013-04-23 20:25:20.006133', NULL, 1, 'PW', 'Palau', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (175, '2013-04-23 20:25:20.006133', NULL, 1, 'PY', 'Paraguay', NULL, NULL, 122, 2);
INSERT INTO adm_country VALUES (176, '2013-04-23 20:25:20.006133', NULL, 1, 'QA', 'Qatar', NULL, NULL, 126, 2);
INSERT INTO adm_country VALUES (177, '2013-04-23 20:25:20.006133', NULL, 1, 'RE', 'RÃ©union', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (178, '2013-04-23 20:25:20.006133', NULL, 1, 'RO', 'Romania', NULL, NULL, 128, 2);
INSERT INTO adm_country VALUES (179, '2013-04-23 20:25:20.006133', NULL, 1, 'RU', 'Russia', NULL, NULL, 129, 2);
INSERT INTO adm_country VALUES (180, '2013-04-23 20:25:20.006133', NULL, 1, 'RW', 'Rwanda', NULL, NULL, 130, 2);
INSERT INTO adm_country VALUES (181, '2013-04-23 20:25:20.006133', NULL, 1, 'SA', 'Saudi Arabia', NULL, NULL, 9, 2);
INSERT INTO adm_country VALUES (182, '2013-04-23 20:25:20.006133', NULL, 1, 'SB', 'Solomon Islands', NULL, NULL, 131, 2);
INSERT INTO adm_country VALUES (183, '2013-04-23 20:25:20.006133', NULL, 1, 'SC', 'Seychelles', NULL, NULL, 136, 2);
INSERT INTO adm_country VALUES (184, '2013-04-23 20:25:20.006133', NULL, 1, 'SD', 'Sudan', NULL, NULL, 21, 2);
INSERT INTO adm_country VALUES (185, '2013-04-23 20:25:20.006133', NULL, 1, 'SE', 'Sweden', NULL, NULL, 145, 2);
INSERT INTO adm_country VALUES (186, '2013-04-23 20:25:20.006133', NULL, 1, 'SG', 'Singapore', NULL, NULL, 138, 2);
INSERT INTO adm_country VALUES (187, '2013-04-23 20:25:20.006133', NULL, 1, 'SH', 'Saint Helena', NULL, NULL, 144, 2);
INSERT INTO adm_country VALUES (188, '2013-04-23 20:25:20.006133', NULL, 1, 'SI', 'Slovenia', NULL, NULL, 140, 2);
INSERT INTO adm_country VALUES (189, '2013-04-23 20:25:20.006133', NULL, 1, 'SJ', 'Svalbard', NULL, NULL, 31, 2);
INSERT INTO adm_country VALUES (190, '2013-04-23 20:25:20.006133', NULL, 1, 'SK', 'Slovakia', NULL, NULL, 139, 2);
INSERT INTO adm_country VALUES (191, '2013-04-23 20:25:20.006133', NULL, 1, 'SL', 'Sierra Leone', NULL, NULL, 137, 2);
INSERT INTO adm_country VALUES (192, '2013-04-23 20:25:20.006133', NULL, 1, 'SM', 'San Marino', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (193, '2013-04-23 20:25:20.006133', NULL, 1, 'SN', 'Senegal', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (194, '2013-04-23 20:25:20.006133', NULL, 1, 'SO', 'Somalia', NULL, NULL, 141, 2);
INSERT INTO adm_country VALUES (195, '2013-04-23 20:25:20.006133', NULL, 1, 'SR', 'Suriname', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (196, '2013-04-23 20:25:20.006133', NULL, 1, 'ST', 'SÃ£o TomÃ© and PrÃ_ncipe', NULL, NULL, 134, 2);
INSERT INTO adm_country VALUES (197, '2013-04-23 20:25:20.006133', NULL, 1, 'SV', 'El Salvador', NULL, NULL, 132, 2);
INSERT INTO adm_country VALUES (198, '2013-04-23 20:25:20.006133', NULL, 1, 'SY', 'Syria', NULL, NULL, 148, 2);
INSERT INTO adm_country VALUES (199, '2013-04-23 20:25:20.006133', NULL, 1, 'SZ', 'Swaziland', NULL, NULL, 147, 2);
INSERT INTO adm_country VALUES (200, '2013-04-23 20:25:20.006133', NULL, 1, 'TC', 'Turks and Caicos Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (201, '2013-04-23 20:25:20.006133', NULL, 1, 'TD', 'Chad', NULL, NULL, 34, 2);
INSERT INTO adm_country VALUES (202, '2013-04-23 20:25:20.006133', NULL, 1, 'TF', 'French Southern and Antarctic La', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (203, '2013-04-23 20:25:20.006133', NULL, 1, 'TG', 'Togo', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (204, '2013-04-23 20:25:20.006133', NULL, 1, 'TH', 'Thailand', NULL, NULL, 153, 2);
INSERT INTO adm_country VALUES (205, '2013-04-23 20:25:20.006133', NULL, 1, 'TJ', 'Tajikistan', NULL, NULL, 149, 2);
INSERT INTO adm_country VALUES (206, '2013-04-23 20:25:20.006133', NULL, 1, 'TK', 'Tokelau', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (207, '2013-04-23 20:25:20.006133', NULL, 1, 'TM', 'Turkmenistan', NULL, NULL, 157, 2);
INSERT INTO adm_country VALUES (208, '2013-04-23 20:25:20.006133', NULL, 1, 'TN', 'Tunisia', NULL, NULL, 156, 2);
INSERT INTO adm_country VALUES (209, '2013-04-23 20:25:20.006133', NULL, 1, 'TO', 'Tonga', NULL, NULL, 154, 2);
INSERT INTO adm_country VALUES (210, '2013-04-23 20:25:20.006133', NULL, 1, 'TL', 'East timor', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (211, '2013-04-23 20:25:20.006133', NULL, 1, 'TR', 'Turkey', NULL, NULL, 159, 2);
INSERT INTO adm_country VALUES (212, '2013-04-23 20:25:20.006133', NULL, 1, 'TT', 'Trinidad and Tobago', NULL, NULL, 155, 2);
INSERT INTO adm_country VALUES (213, '2013-04-23 20:25:20.006133', NULL, 1, 'TV', 'Tuvalu', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (214, '2013-04-23 20:25:20.006133', NULL, 1, 'TW', 'Taiwan', NULL, NULL, 150, 2);
INSERT INTO adm_country VALUES (215, '2013-04-23 20:25:20.006133', NULL, 1, 'TZ', 'Tanzania', NULL, NULL, 151, 2);
INSERT INTO adm_country VALUES (216, '2013-04-23 20:25:20.006133', NULL, 1, 'UA', 'Ukraine', NULL, NULL, 160, 2);
INSERT INTO adm_country VALUES (217, '2013-04-23 20:25:20.006133', NULL, 1, 'UG', 'Uganda', NULL, NULL, 117, 2);
INSERT INTO adm_country VALUES (218, '2013-04-23 20:25:20.006133', NULL, 1, 'GB', 'United Kingdom', NULL, NULL, 65, 2);
INSERT INTO adm_country VALUES (219, '2013-04-23 20:25:20.006133', NULL, 1, 'UM', 'United States Minor Outlying Isl', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (220, '2013-04-23 20:25:20.006133', NULL, 1, 'US', 'United States', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (221, '2013-04-23 20:25:20.006133', NULL, 1, 'UY', 'Uruguay', NULL, NULL, 161, 2);
INSERT INTO adm_country VALUES (222, '2013-04-23 20:25:20.006133', NULL, 1, 'UZ', 'Uzbekistan', NULL, NULL, 118, 2);
INSERT INTO adm_country VALUES (223, '2013-04-23 20:25:20.006133', NULL, 1, 'VA', 'Holy See Vatican City', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (224, '2013-04-23 20:25:20.006133', NULL, 1, 'VC', 'Saint Vincent and the Grenadines', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (225, '2013-04-23 20:25:20.006133', NULL, 1, 'VE', 'Venezuela', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (226, '2013-04-23 20:25:20.006133', NULL, 1, 'VG', 'British Virgin Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (227, '2013-04-23 20:25:20.006133', NULL, 1, 'VI', 'Virgin Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (228, '2013-04-23 20:25:20.006133', NULL, 1, 'VN', 'Vietnam', NULL, NULL, 164, 2);
INSERT INTO adm_country VALUES (229, '2013-04-23 20:25:20.006133', NULL, 1, 'VU', 'Vanuatu', NULL, NULL, 162, 2);
INSERT INTO adm_country VALUES (230, '2013-04-23 20:25:20.006133', NULL, 1, 'WF', 'Wallis and Futuna', NULL, NULL, 114, 2);
INSERT INTO adm_country VALUES (231, '2013-04-23 20:25:20.006133', NULL, 1, 'WS', 'Samoa', NULL, NULL, 133, 2);
INSERT INTO adm_country VALUES (232, '2013-04-23 20:25:20.006133', NULL, 1, 'YE', 'Yemen', NULL, NULL, 165, 2);
INSERT INTO adm_country VALUES (233, '2013-04-23 20:25:20.006133', NULL, 1, 'YT', 'Mayotte', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (234, '2013-04-23 20:25:20.006133', NULL, 1, 'YU', 'Yugoslavia', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (235, '2013-04-23 20:25:20.006133', NULL, 1, 'ZA', 'South Africa', NULL, NULL, 2, 2);
INSERT INTO adm_country VALUES (236, '2013-04-23 20:25:20.006133', NULL, 1, 'ZM', 'Zambia', NULL, NULL, 166, 2);
INSERT INTO adm_country VALUES (237, '2013-04-23 20:25:20.006133', NULL, 1, 'ZW', 'Zimbabwe', NULL, NULL, 167, 2);




--
-- Data for Name: crm_provider; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO crm_provider VALUES (1, 1, false, '2013-04-23 20:25:20.006133', NULL, 'DEMO', NULL, false, false, '33333333333', 'owner', 'SGMB', '11', 'SGMB', '11', '12345', '11', 'PROV1', '1111', '11', NULL, NULL, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, true, NULL, true, true, true, 2, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL, 1, NULL);

--
-- Data for Name: adm_user; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_user VALUES (1, 17, false, '2013-04-23 20:25:20.006133', '2013-05-16 16:14:07.466', '2013-05-16', NULL, NULL, 'fb93a3221422999ba49eb103977a6d736376505b', 'MEVEO.ADMIN', 1, 1, 1, NULL);
INSERT INTO adm_user VALUES (6, 4, false, '2013-04-23 20:25:20.006133', NULL, '2014-04-23', NULL, NULL, 'fb93a3221422999ba49eb103977a6d736376505b', 'MEVEO.SUPERADMIN', 1, 1, NULL, NULL);
INSERT INTO adm_user VALUES (2380, 4, false, '2013-05-09 08:25:23.124', '2013-06-06 16:58:33.574', '2013-06-06', NULL, NULL, '3b23d1d4fccf6ccc84e5ac8253e86573059af093', 'MEVEO.TEST', 1, 1, 1, NULL);



--
-- Data for Name: crm_provider_contact; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO crm_provider_contact VALUES (235, 1, false, '2013-04-17 10:46:05.064', '2013-04-17 16:12:17.991', 'DEMO ADMIN', 'DEMO Administrator', '', '', '', '', 'Australie', NULL, '', 'qdqsdq@gmail.com', NULL, '', 'sdqsdq@gmail.com', '', '', '', 1, 1, 1);
INSERT INTO crm_provider_contact VALUES (10183, 0, false, '2013-05-27 11:21:28.153', NULL, '1234', '1234', '', '', '', 'DIJON', 'MX', NULL, '21000', 'toto@voila.fr', NULL, '', 'toto@voila.fr', '', '', '', 1, 1, NULL);



--
-- Data for Name: adm_title; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_title VALUES (1, 0, false, '2013-04-23 20:25:20.006133', NULL, 'AGCE', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (2, 0, false, '2013-04-23 20:25:20.006133', NULL, 'ASSO', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (3, 0, false, '2013-04-23 20:25:20.006133', NULL, 'CAB', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (4, 0, false, '2013-04-23 20:25:20.006133', NULL, 'COLL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (5, 0, false, '2013-04-23 20:25:20.006133', NULL, 'COM', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (6, 0, false, '2013-04-23 20:25:20.006133', NULL, 'COPR', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (7, 0, false, '2013-04-23 20:25:20.006133', NULL, 'CSSE', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (8, 0, false, '2013-04-23 20:25:20.006133', NULL, 'EARL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (9, 0, false, '2013-04-23 20:25:20.006133', NULL, 'ETS', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (10, 0, false, '2013-04-23 20:25:20.006133', NULL, 'EURL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (11, 0, false, '2013-04-23 20:25:20.006133', NULL, 'GAEC', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (12, 0, false, '2013-04-23 20:25:20.006133', NULL, 'HLM', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (13, 0, false, '2013-04-23 20:25:20.006133', NULL, 'HOPI', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (14, 0, false, '2013-04-23 20:25:20.006133', NULL, 'INST', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (15, 0, false, '2013-04-23 20:25:20.006133', NULL, 'LABO', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (16, 0, false, '2013-04-23 20:25:20.006133', NULL, 'M', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (17, 0, false, '2013-04-23 20:25:20.006133', NULL, 'MLLES', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (18, 0, false, '2013-04-23 20:25:20.006133', NULL, 'MLLE_M', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (19, 0, false, '2013-04-23 20:25:20.006133', NULL, 'MLLE', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (20, 0, false, '2013-04-23 20:25:20.006133', NULL, 'MM', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (21, 0, false, '2013-04-23 20:25:20.006133', NULL, 'MME', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (22, 0, false, '2013-04-23 20:25:20.006133', NULL, 'MME_M', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (23, 0, false, '2013-04-23 20:25:20.006133', NULL, 'MMES', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (24, 0, false, '2013-04-23 20:25:20.006133', NULL, 'MTRE', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (25, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SA', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (26, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SARL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (27, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SCEA', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (28, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SCI', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (29, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SCM', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (30, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SCP', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (31, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SELARL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (32, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SNC', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (33, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SNI', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (34, 0, false, '2013-04-23 20:25:20.006133', NULL, 'STE', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (35, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SUCC', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (36, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SYNDIC', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (37, 0, false, '2013-04-23 20:25:20.006133', NULL, 'SYNDCOP', true, 1, NULL, NULL);




--
-- Name: adm_country_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_country_seq', 20000, false);


--
-- Name: adm_currency_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_currency_seq', 20000, false);


--
-- Data for Name: adm_input_history; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: adm_input_history_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_input_history_seq', 20000, false);


--
-- Name: adm_language_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_language_seq', 20000, false);


--
-- Data for Name: adm_medina_configuration; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: adm_medina_configuration_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_medina_configuration_seq', 20000, false);


--
-- Data for Name: adm_messages; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_messages VALUES (246, 0, false, '2013-04-17 15:31:26.647', NULL, 'Abonnement logiciel (Turque)', 'TUR', 'InvoiceCategory_245', 1, 1, NULL);
INSERT INTO adm_messages VALUES (247, 0, false, '2013-04-17 15:31:26.649', NULL, 'Abonnement logiciel (Arabe)', 'ARA', 'InvoiceCategory_245', 1, 1, NULL);
INSERT INTO adm_messages VALUES (248, 0, false, '2013-04-17 15:31:26.652', NULL, 'Abonnement logiciel (FranÃ§ais)', 'FRA', 'InvoiceCategory_245', 1, 1, NULL);
INSERT INTO adm_messages VALUES (295, 0, false, '2013-04-17 15:58:22.261', NULL, 'SUBSCRIPTION', 'TUR', 'InvoiceSubCategory_294', 1, 1, NULL);
INSERT INTO adm_messages VALUES (296, 0, false, '2013-04-17 15:58:22.262', NULL, 'SUBSCRIPTION', 'ARA', 'InvoiceSubCategory_294', 1, 1, NULL);
INSERT INTO adm_messages VALUES (297, 0, false, '2013-04-17 15:58:22.263', NULL, 'SUBSCRIPTION', 'FRA', 'InvoiceSubCategory_294', 1, 1, NULL);
INSERT INTO adm_messages VALUES (357, 3, false, '2013-04-17 17:30:03.303', '2013-04-25 01:25:57.803', 'License d''accÃÂ©s Microsoft (Arabe)', 'ARA', 'ChargeTemplate_355', 1, 1, 1);
INSERT INTO adm_messages VALUES (356, 3, false, '2013-04-17 17:30:03.301', '2013-04-25 01:25:57.812', 'License d''accÃÂ©s Microsoft (Turque)', 'TUR', 'ChargeTemplate_355', 1, 1, 1);
INSERT INTO adm_messages VALUES (381, 3, false, '2013-04-17 18:23:32.616', '2013-04-25 01:27:40.013', 'Microsoft Project 2013 (Turque)', 'TUR', 'ChargeTemplate_380', 1, 1, 1);
INSERT INTO adm_messages VALUES (383, 3, false, '2013-04-17 18:23:32.62', '2013-04-25 01:27:40.027', 'Microsoft Project 2013 (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_380', 1, 1, 1);
INSERT INTO adm_messages VALUES (1121, 3, false, '2013-04-25 01:20:50.411', '2013-04-25 02:00:18.882', NULL, 'ENG', 'ChargeTemplate_400', 1, 1, 1);
INSERT INTO adm_messages VALUES (1122, 1, false, '2013-04-25 01:21:05.377', '2013-04-25 01:29:08.23', 'Option 1Go supplémentaire (Allemand)', 'DEU', 'ChargeTemplate_364', 1, 1, 1);
INSERT INTO adm_messages VALUES (339, 6, false, '2013-04-17 16:59:37.532', '2013-04-25 01:11:54.553', 'Option 1Go incluse (Turque)', 'TUR', 'ChargeTemplate_338', 1, 1, 1);
INSERT INTO adm_messages VALUES (340, 6, false, '2013-04-17 16:59:37.534', '2013-04-25 01:11:54.568', 'Option 1Go incluse (Arabe)', 'ARA', 'ChargeTemplate_338', 1, 1, 1);
INSERT INTO adm_messages VALUES (1123, 1, false, '2013-04-25 01:21:05.395', '2013-04-25 01:29:08.254', NULL, 'ENG', 'ChargeTemplate_364', 1, 1, 1);
INSERT INTO adm_messages VALUES (1124, 1, false, '2013-04-25 01:21:19.261', '2013-04-25 01:29:32.973', 'Microsoft Office 2013 Professionel (Allemand)', 'DEU', 'ChargeTemplate_372', 1, 1, 1);
INSERT INTO adm_messages VALUES (347, 6, false, '2013-04-17 17:02:59.86', '2013-04-25 01:31:58.926', ' Option 3Go supplÃÂ©mentaire (Turque)', 'TUR', 'ChargeTemplate_346', 1, 1, 1);
INSERT INTO adm_messages VALUES (348, 6, false, '2013-04-17 17:02:59.862', '2013-04-25 01:31:58.933', 'Option 3Go supplÃÂ©mentaire (Arabe)', 'ARA', 'ChargeTemplate_346', 1, 1, 1);
INSERT INTO adm_messages VALUES (343, 4, false, '2013-04-17 17:00:17.826', '2013-04-25 01:24:34.72', 'Exchange 2010 Premium (Turque)', 'TUR', 'ChargeTemplate_342', 1, 1, 1);
INSERT INTO adm_messages VALUES (344, 4, false, '2013-04-17 17:00:17.827', '2013-04-25 01:24:34.73', 'Exchange 2010 Premium (Arabe)', 'ARA', 'ChargeTemplate_342', 1, 1, 1);
INSERT INTO adm_messages VALUES (345, 4, false, '2013-04-17 17:00:17.829', '2013-04-25 01:24:34.748', 'Exchange 2010 Premium (Français)', 'FRA', 'ChargeTemplate_342', 1, 1, 1);
INSERT INTO adm_messages VALUES (1125, 1, false, '2013-04-25 01:21:19.278', '2013-04-25 01:29:32.984', NULL, 'ENG', 'ChargeTemplate_372', 1, 1, 1);
INSERT INTO adm_messages VALUES (335, 4, false, '2013-04-17 16:57:37.13', '2013-04-25 01:31:17.024', 'Exchange 2010 Business (Turque)', 'TUR', 'ChargeTemplate_334', 1, 1, 1);
INSERT INTO adm_messages VALUES (336, 4, false, '2013-04-17 16:57:37.131', '2013-04-25 01:31:17.031', 'Exchange 2010 Business (Arabe)', 'ARA', 'ChargeTemplate_334', 1, 1, 1);
INSERT INTO adm_messages VALUES (292, 3, false, '2013-04-17 15:56:07.805', '2013-05-03 10:27:04.534', 'Taxe de 5 pourcent (arabe)', 'ARA', 'Tax_290', 1, 1, 1);
INSERT INTO adm_messages VALUES (291, 3, false, '2013-04-17 15:56:07.803', '2013-05-03 10:27:04.549', 'Taxe de 5 pourcent (turque)', 'TUR', 'Tax_290', 1, 1, 1);
INSERT INTO adm_messages VALUES (293, 3, false, '2013-04-17 15:56:07.808', '2013-05-03 10:27:04.583', 'Taxe de 5 pourcent (franÃ§ais)', 'FRA', 'Tax_290', 1, 1, 1);
INSERT INTO adm_messages VALUES (284, 1, false, '2013-04-17 15:53:55.447', '2013-04-25 19:59:09.619', 'Taxe de 18 pourcent (arabe)', 'ARA', 'Tax_282', 1, 1, 1);
INSERT INTO adm_messages VALUES (283, 1, false, '2013-04-17 15:53:55.445', '2013-04-25 19:59:09.631', 'Taxe de 18 pourcent (turque)', 'TUR', 'Tax_282', 1, 1, 1);
INSERT INTO adm_messages VALUES (1263, 0, false, '2013-04-25 19:59:09.647', NULL, NULL, 'CES', 'Tax_282', 1, 1, NULL);
INSERT INTO adm_messages VALUES (267, 2, false, '2013-04-17 15:49:42.339', '2013-04-25 19:59:54.052', 'Taxe de 0 pourcent (turque)', 'TUR', 'Tax_266', 1, 1, 1);
INSERT INTO adm_messages VALUES (268, 2, false, '2013-04-17 15:49:42.341', '2013-04-25 19:59:54.063', 'Taxe de 0 pourcent (arabe)', 'ARA', 'Tax_266', 1, 1, 1);
INSERT INTO adm_messages VALUES (269, 2, false, '2013-04-17 15:49:42.343', '2013-04-25 19:59:54.092', 'Taxe de 0 pourcent (franÃ§ais)', 'FRA', 'Tax_266', 1, 1, 1);
INSERT INTO adm_messages VALUES (241, 8, false, '2013-04-17 15:26:59.83', '2013-04-30 18:31:52.076', 'Abonnement (Turque)', 'TUR', 'InvoiceCategory_240', 1, 1, 1);
INSERT INTO adm_messages VALUES (242, 8, false, '2013-04-17 15:26:59.833', '2013-04-30 18:31:52.083', 'Abonnement (Arabe)', 'ARA', 'InvoiceCategory_240', 1, 1, 1);
INSERT INTO adm_messages VALUES (243, 8, false, '2013-04-17 15:26:59.834', '2013-04-30 18:31:52.117', 'Abonnement (Français)', 'FRA', 'InvoiceCategory_240', 1, 1, 1);
INSERT INTO adm_messages VALUES (1060, 1, false, '2013-04-24 07:27:29.716', '2013-05-02 08:53:09.394', NULL, 'ARA', 'ChargeTemplate_1058', 1, 1, 1);
INSERT INTO adm_messages VALUES (1059, 1, false, '2013-04-24 07:27:29.713', '2013-05-02 08:53:09.409', NULL, 'TUR', 'ChargeTemplate_1058', 1, 1, 1);
INSERT INTO adm_messages VALUES (1061, 1, false, '2013-04-24 07:27:29.718', '2013-05-02 08:53:09.421', NULL, 'DEU', 'ChargeTemplate_1058', 1, 1, 1);
INSERT INTO adm_messages VALUES (288, 1, false, '2013-04-17 15:54:52.009', '2013-05-02 10:51:47.146', 'Taxe de 19,6 pourcent (arabe)', 'ARA', 'Tax_286', 1, 1, 1);
INSERT INTO adm_messages VALUES (287, 1, false, '2013-04-17 15:54:52.007', '2013-05-02 10:51:47.182', 'Taxe de 19,6 pourcent (turque)', 'TUR', 'Tax_286', 1, 1, 1);
INSERT INTO adm_messages VALUES (289, 1, false, '2013-04-17 15:54:52.01', '2013-05-02 10:51:47.25', 'Taxe de 19,6 pourcent (franÃ§ais)', 'FRA', 'Tax_286', 1, 1, 1);
INSERT INTO adm_messages VALUES (1980, 0, false, '2013-05-03 17:22:35.158', NULL, NULL, 'TUR', 'ChargeTemplate_1979', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1981, 0, false, '2013-05-03 17:22:35.16', NULL, '2', 'ARA', 'ChargeTemplate_1979', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1982, 0, false, '2013-05-03 17:22:35.161', NULL, NULL, 'DEU', 'ChargeTemplate_1979', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1983, 0, false, '2013-05-03 17:22:35.163', NULL, NULL, 'FRA', 'ChargeTemplate_1979', 1, 1, NULL);
INSERT INTO adm_messages VALUES (382, 3, false, '2013-04-17 18:23:32.618', '2013-04-25 01:27:40.005', 'Microsoft Project 2013 (Arabe)', 'ARA', 'ChargeTemplate_380', 1, 1, 1);
INSERT INTO adm_messages VALUES (1116, 1, false, '2013-04-25 01:20:26.289', '2013-04-25 01:27:40.02', 'Microsoft Project 2013 (Allemand)', 'DEU', 'ChargeTemplate_380', 1, 1, 1);
INSERT INTO adm_messages VALUES (1117, 1, false, '2013-04-25 01:20:26.308', '2013-04-25 01:27:40.033', NULL, 'ENG', 'ChargeTemplate_380', 1, 1, 1);
INSERT INTO adm_messages VALUES (1092, 0, false, '2013-04-24 16:39:02.569', NULL, NULL, 'TUR', 'InvoiceCategory_1091', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1093, 0, false, '2013-04-24 16:39:02.572', NULL, '0', 'ARA', 'InvoiceCategory_1091', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1094, 0, false, '2013-04-24 16:39:02.575', NULL, NULL, 'DEU', 'InvoiceCategory_1091', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1095, 0, false, '2013-04-24 16:39:02.578', NULL, NULL, 'FRA', 'InvoiceCategory_1091', 1, 1, NULL);
INSERT INTO adm_messages VALUES (307, 3, false, '2013-04-17 16:02:54.629', '2013-05-16 19:41:51.785', 'Souscription de domaine (turque)', 'TUR', 'InvoiceSubCategory_306', 1, 1, 1);
INSERT INTO adm_messages VALUES (308, 3, false, '2013-04-17 16:02:54.631', '2013-05-16 19:41:51.799', 'Souscription de domaine (arabe)', 'ARA', 'InvoiceSubCategory_306', 1, 1, 1);
INSERT INTO adm_messages VALUES (309, 3, false, '2013-04-17 16:02:54.632', '2013-05-16 19:41:51.846', 'Souscription de domaine (franÃ§ais)', 'FRA', 'InvoiceSubCategory_306', 1, 1, 1);
INSERT INTO adm_messages VALUES (303, 3, false, '2013-04-17 16:00:47.072', '2013-05-16 19:42:08.894', 'Souscription de progiciel (Turque)', 'TUR', 'InvoiceSubCategory_302', 1, 1, 1);
INSERT INTO adm_messages VALUES (304, 3, false, '2013-04-17 16:00:47.074', '2013-05-16 19:42:08.902', 'Souscription de progiciel (Arabe)', 'ARA', 'InvoiceSubCategory_302', 1, 1, 1);
INSERT INTO adm_messages VALUES (305, 3, false, '2013-04-17 16:00:47.076', '2013-05-16 19:42:08.931', 'Souscription de progiciel (FranÃ§ais)', 'FRA', 'InvoiceSubCategory_302', 1, 1, 1);
INSERT INTO adm_messages VALUES (1096, 0, false, '2013-04-24 16:39:02.58', NULL, NULL, 'ENG', 'InvoiceCategory_1091', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1114, 0, false, '2013-04-25 01:11:54.58', NULL, 'Option 1Go incluse (Allemand)', 'DEU', 'ChargeTemplate_338', 1, 1, NULL);
INSERT INTO adm_messages VALUES (341, 6, false, '2013-04-17 16:59:37.536', '2013-04-25 01:11:54.593', 'Option 1Go incluse (FranÃ§ais)', 'FRA', 'ChargeTemplate_338', 1, 1, 1);
INSERT INTO adm_messages VALUES (1115, 0, false, '2013-04-25 01:11:54.604', NULL, NULL, 'ENG', 'ChargeTemplate_338', 1, 1, NULL);
INSERT INTO adm_messages VALUES (394, 3, false, '2013-04-17 18:27:28.168', '2013-04-25 01:28:10.361', 'Microsoft Lync 2010 (Arabe)', 'ARA', 'ChargeTemplate_392', 1, 1, 1);
INSERT INTO adm_messages VALUES (401, 4, false, '2013-04-17 18:34:33.249', '2013-04-25 02:00:18.853', 'Option Microsoft 1,5 GB (Turque)', 'TUR', 'ChargeTemplate_400', 1, 1, 1);
INSERT INTO adm_messages VALUES (402, 4, false, '2013-04-17 18:34:33.252', '2013-04-25 02:00:18.861', 'Option Microsoft 1,5 GB (Arabe)', 'ARA', 'ChargeTemplate_400', 1, 1, 1);
INSERT INTO adm_messages VALUES (1120, 3, false, '2013-04-25 01:20:50.395', '2013-04-25 02:00:18.868', 'Option Microsoft 1,5 GB (Allemand)', 'DEU', 'ChargeTemplate_400', 1, 1, 1);
INSERT INTO adm_messages VALUES (365, 4, false, '2013-04-17 17:46:34.024', '2013-04-25 01:29:08.212', 'Option 1Go supplÃ©mentaire (turque)', 'TUR', 'ChargeTemplate_364', 1, 1, 1);
INSERT INTO adm_messages VALUES (366, 4, false, '2013-04-17 17:46:34.026', '2013-04-25 01:29:08.218', 'Option 1Go supplÃ©mentaire (Arabe)', 'ARA', 'ChargeTemplate_364', 1, 1, 1);
INSERT INTO adm_messages VALUES (367, 4, false, '2013-04-17 17:46:34.028', '2013-04-25 01:29:08.239', 'Option 1Go supplÃ©mentaire (FranÃ§ais)', 'FRA', 'ChargeTemplate_364', 1, 1, 1);
INSERT INTO adm_messages VALUES (374, 3, false, '2013-04-17 18:08:38.845', '2013-04-25 01:29:32.96', 'Microsoft Office 2013 Professionel (Arabe)', 'ARA', 'ChargeTemplate_372', 1, 1, 1);
INSERT INTO adm_messages VALUES (373, 3, false, '2013-04-17 18:08:38.844', '2013-04-25 01:29:32.966', 'Microsoft Office 2013 Professionel (turque)', 'TUR', 'ChargeTemplate_372', 1, 1, 1);
INSERT INTO adm_messages VALUES (375, 3, false, '2013-04-17 18:08:38.847', '2013-04-25 01:29:32.979', 'Microsoft Office 2013 Professionel (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_372', 1, 1, 1);
INSERT INTO adm_messages VALUES (387, 3, false, '2013-04-17 18:25:07.047', '2013-04-25 01:30:14.138', 'Microsoft Visio 2013 (Français)', 'FRA', 'ChargeTemplate_384', 1, 1, 1);
INSERT INTO adm_messages VALUES (398, 3, false, '2013-04-17 18:28:56.872', '2013-04-25 01:30:38.576', 'Microsoft Dynamics CRM 2011 (Arabe)', 'ARA', 'ChargeTemplate_396', 1, 1, 1);
INSERT INTO adm_messages VALUES (397, 3, false, '2013-04-17 18:28:56.871', '2013-04-25 01:30:38.584', 'Microsoft Dynamics CRM 2011 (Turque)', 'TUR', 'ChargeTemplate_396', 1, 1, 1);
INSERT INTO adm_messages VALUES (399, 3, false, '2013-04-17 18:28:56.874', '2013-04-25 01:30:38.601', 'Microsoft Dynamics CRM 2011 (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_396', 1, 1, 1);
INSERT INTO adm_messages VALUES (349, 6, false, '2013-04-17 17:02:59.864', '2013-04-25 01:31:58.947', 'Option 3Go supplÃÂ©mentaire (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_346', 1, 1, 1);
INSERT INTO adm_messages VALUES (370, 5, false, '2013-04-17 17:57:34.063', '2013-04-25 01:32:21.55', 'Option 2Go incluse (Arabe)', 'ARA', 'ChargeTemplate_368', 1, 1, 1);
INSERT INTO adm_messages VALUES (369, 5, false, '2013-04-17 17:57:34.061', '2013-04-25 01:32:21.555', 'Option 2Go incluse (Turque)', 'TUR', 'ChargeTemplate_368', 1, 1, 1);
INSERT INTO adm_messages VALUES (371, 5, false, '2013-04-17 17:57:34.065', '2013-04-25 01:32:21.565', 'Option 2Go incluse (franÃ§ais)', 'FRA', 'ChargeTemplate_368', 1, 1, 1);
INSERT INTO adm_messages VALUES (390, 3, false, '2013-04-17 18:26:14.525', '2013-04-25 02:11:11.604', 'Microsoft Sharepoint 2010 (Arabe)', 'ARA', 'ChargeTemplate_388', 1, 1, 1);
INSERT INTO adm_messages VALUES (389, 3, false, '2013-04-17 18:26:14.524', '2013-04-25 02:11:11.614', 'Microsoft Sharepoint 2010 (Turque)', 'TUR', 'ChargeTemplate_388', 1, 1, 1);
INSERT INTO adm_messages VALUES (1112, 1, false, '2013-04-25 01:10:00.704', '2013-04-25 02:11:11.619', 'Microsoft Sharepoint 2010 (Allemand)', 'DEU', 'ChargeTemplate_388', 1, 1, 1);
INSERT INTO adm_messages VALUES (377, 2, false, '2013-04-17 18:09:41.377', '2013-04-25 01:26:49.934', 'Microsoft Office 2013 Standard (Turque)', 'TUR', 'ChargeTemplate_376', 1, 1, 1);
INSERT INTO adm_messages VALUES (378, 2, false, '2013-04-17 18:09:41.379', '2013-04-25 01:26:49.941', 'Microsoft Office 2013 Standard (Arabe)', 'ARA', 'ChargeTemplate_376', 1, 1, 1);
INSERT INTO adm_messages VALUES (1055, 1, false, '2013-04-23 23:12:41.151', '2013-04-25 01:24:34.739', 'Exchange 2010 Premium (Allemand)', 'DEU', 'ChargeTemplate_342', 1, 1, 1);
INSERT INTO adm_messages VALUES (1082, 1, false, '2013-04-24 15:54:31.494', '2013-04-25 01:25:57.819', 'License d''accés Microsoft (Allemand)', 'DEU', 'ChargeTemplate_355', 1, 1, 1);
INSERT INTO adm_messages VALUES (1083, 1, false, '2013-04-24 15:54:31.516', '2013-04-25 01:25:57.834', NULL, 'ENG', 'ChargeTemplate_355', 1, 1, 1);
INSERT INTO adm_messages VALUES (331, 6, false, '2013-04-17 16:55:29.217', '2013-04-25 01:26:09.401', 'Exchange 2010 - souscription blackberry (Turque)', 'TUR', 'ChargeTemplate_330', 1, 1, 1);
INSERT INTO adm_messages VALUES (332, 6, false, '2013-04-17 16:55:29.218', '2013-04-25 01:26:09.41', 'Exchange 2010 - souscription blackberry (Arabe)', 'ARA', 'ChargeTemplate_330', 1, 1, 1);
INSERT INTO adm_messages VALUES (1084, 1, false, '2013-04-24 15:55:07.865', '2013-04-25 01:26:49.947', 'Microsoft Office 2013 Standard (Allemand)', 'DEU', 'ChargeTemplate_376', 1, 1, 1);
INSERT INTO adm_messages VALUES (1085, 1, false, '2013-04-24 15:55:07.888', '2013-04-25 01:26:49.958', NULL, 'ENG', 'ChargeTemplate_376', 1, 1, 1);
INSERT INTO adm_messages VALUES (393, 3, false, '2013-04-17 18:27:28.167', '2013-04-25 01:28:10.367', 'Microsoft Lync 2010 (Turque)', 'TUR', 'ChargeTemplate_392', 1, 1, 1);
INSERT INTO adm_messages VALUES (1118, 1, false, '2013-04-25 01:20:38.656', '2013-04-25 01:28:10.373', 'Microsoft Lync 2010 (Allemand)', 'DEU', 'ChargeTemplate_392', 1, 1, 1);
INSERT INTO adm_messages VALUES (395, 3, false, '2013-04-17 18:27:28.17', '2013-04-25 01:28:10.378', 'Microsoft Lync 2010 (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_392', 1, 1, 1);
INSERT INTO adm_messages VALUES (403, 4, false, '2013-04-17 18:34:33.254', '2013-04-25 02:00:18.876', 'Option Microsoft 1,5 GB (Français)', 'FRA', 'ChargeTemplate_400', 1, 1, 1);
INSERT INTO adm_messages VALUES (391, 3, false, '2013-04-17 18:26:14.527', '2013-04-25 02:11:11.624', 'Microsoft Sharepoint 2010 (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_388', 1, 1, 1);
INSERT INTO adm_messages VALUES (1137, 0, false, '2013-04-25 01:24:34.757', NULL, NULL, 'ENG', 'ChargeTemplate_342', 1, 1, NULL);
INSERT INTO adm_messages VALUES (358, 3, false, '2013-04-17 17:30:03.304', '2013-04-25 01:25:57.827', 'License d''accÃÂ©s Microsoft (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_355', 1, 1, 1);
INSERT INTO adm_messages VALUES (1054, 2, false, '2013-04-23 23:11:57.887', '2013-04-25 01:26:09.418', 'Exchange 2010 - souscription blackberry (Allemand)', 'DEU', 'ChargeTemplate_330', 1, 1, 1);
INSERT INTO adm_messages VALUES (333, 6, false, '2013-04-17 16:55:29.22', '2013-04-25 01:26:09.426', 'Exchange 2010 - souscription blackberry (Français)', 'FRA', 'ChargeTemplate_330', 1, 1, 1);
INSERT INTO adm_messages VALUES (1136, 1, false, '2013-04-25 01:23:52.515', '2013-04-25 01:26:09.434', NULL, 'ENG', 'ChargeTemplate_330', 1, 1, 1);
INSERT INTO adm_messages VALUES (379, 2, false, '2013-04-17 18:09:41.38', '2013-04-25 01:26:49.953', 'Microsoft Office 2013 Standard (FranÃ§ais)', 'FRA', 'ChargeTemplate_376', 1, 1, 1);
INSERT INTO adm_messages VALUES (1119, 1, false, '2013-04-25 01:20:38.681', '2013-04-25 01:28:10.383', NULL, 'ENG', 'ChargeTemplate_392', 1, 1, 1);
INSERT INTO adm_messages VALUES (386, 3, false, '2013-04-17 18:25:07.046', '2013-04-25 01:30:14.116', 'Microsoft Visio 2013 (Arabe)', 'ARA', 'ChargeTemplate_384', 1, 1, 1);
INSERT INTO adm_messages VALUES (385, 3, false, '2013-04-17 18:25:07.044', '2013-04-25 01:30:14.124', 'Microsoft Visio 2013 (Turque)', 'TUR', 'ChargeTemplate_384', 1, 1, 1);
INSERT INTO adm_messages VALUES (1087, 1, false, '2013-04-24 16:22:38.737', '2013-05-09 22:48:38.99', NULL, 'DEU', 'InvoiceCategory_249', 1, 1, 1);
INSERT INTO adm_messages VALUES (1088, 1, false, '2013-04-24 16:22:38.786', '2013-05-09 22:48:39.007', NULL, 'ENG', 'InvoiceCategory_249', 1, 1, 1);
INSERT INTO adm_messages VALUES (1126, 1, false, '2013-04-25 01:21:28.713', '2013-04-25 01:30:14.131', 'Microsoft Visio 2013 (Allemand)', 'DEU', 'ChargeTemplate_384', 1, 1, 1);
INSERT INTO adm_messages VALUES (1127, 1, false, '2013-04-25 01:21:28.731', '2013-04-25 01:30:14.145', NULL, 'ENG', 'ChargeTemplate_384', 1, 1, 1);
INSERT INTO adm_messages VALUES (1128, 1, false, '2013-04-25 01:21:38.789', '2013-04-25 01:30:38.593', 'Microsoft Dynamics CRM 2011 (Allemand)', 'DEU', 'ChargeTemplate_396', 1, 1, 1);
INSERT INTO adm_messages VALUES (1129, 1, false, '2013-04-25 01:21:38.805', '2013-04-25 01:30:38.61', NULL, 'ENG', 'ChargeTemplate_396', 1, 1, 1);
INSERT INTO adm_messages VALUES (1130, 1, false, '2013-04-25 01:21:47.111', '2013-04-25 01:31:17.037', 'Exchange 2010 Business (Allemand)', 'DEU', 'ChargeTemplate_334', 1, 1, 1);
INSERT INTO adm_messages VALUES (337, 4, false, '2013-04-17 16:57:37.133', '2013-04-25 01:31:17.044', 'Exchange 2010 Business (Français)', 'FRA', 'ChargeTemplate_334', 1, 1, 1);
INSERT INTO adm_messages VALUES (1131, 1, false, '2013-04-25 01:21:47.127', '2013-04-25 01:31:17.051', NULL, 'ENG', 'ChargeTemplate_334', 1, 1, 1);
INSERT INTO adm_messages VALUES (1532, 0, false, '2013-05-01 20:40:31.792', NULL, NULL, 'TUR', 'InvoiceCategory_1531', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1132, 1, false, '2013-04-25 01:21:56.045', '2013-04-25 01:31:58.94', ' Option 3Go supplémentaire (Allemand)', 'DEU', 'ChargeTemplate_346', 1, 1, 1);
INSERT INTO adm_messages VALUES (1133, 1, false, '2013-04-25 01:21:56.061', '2013-04-25 01:31:58.953', NULL, 'ENG', 'ChargeTemplate_346', 1, 1, 1);
INSERT INTO adm_messages VALUES (1134, 1, false, '2013-04-25 01:22:06.119', '2013-04-25 01:32:21.56', 'Option 2Go incluse (Allemand)', 'DEU', 'ChargeTemplate_368', 1, 1, 1);
INSERT INTO adm_messages VALUES (1135, 1, false, '2013-04-25 01:22:06.134', '2013-04-25 01:32:21.57', NULL, 'ENG', 'ChargeTemplate_368', 1, 1, 1);
INSERT INTO adm_messages VALUES (1145, 0, false, '2013-04-25 01:43:14.833', NULL, 'Microsoft Dynamics CRM 2011 customer (Turque)', 'TUR', 'ChargeTemplate_1144', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1146, 0, false, '2013-04-25 01:43:14.835', NULL, 'Microsoft Dynamics CRM 2011 customer (Arabe)', 'ARA', 'ChargeTemplate_1144', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1147, 0, false, '2013-04-25 01:43:14.837', NULL, 'Microsoft Dynamics CRM 2011 customer (Allemand)', 'DEU', 'ChargeTemplate_1144', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1148, 0, false, '2013-04-25 01:43:14.84', NULL, 'Microsoft Dynamics CRM 2011 customer (Français)', 'FRA', 'ChargeTemplate_1144', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1149, 0, false, '2013-04-25 01:43:14.842', NULL, NULL, 'ENG', 'ChargeTemplate_1144', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1151, 0, false, '2013-04-25 01:47:20.535', NULL, 'Fastviewer 1 session (Turque)', 'TUR', 'ChargeTemplate_1150', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1152, 0, false, '2013-04-25 01:47:20.537', NULL, 'Fastviewer 1 session (Arabe)', 'ARA', 'ChargeTemplate_1150', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1153, 0, false, '2013-04-25 01:47:20.539', NULL, 'Fastviewer 1 session (Allemand)', 'DEU', 'ChargeTemplate_1150', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1154, 0, false, '2013-04-25 01:47:20.54', NULL, 'Fastviewer 1 session (Français)', 'FRA', 'ChargeTemplate_1150', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1155, 0, false, '2013-04-25 01:47:20.542', NULL, NULL, 'ENG', 'ChargeTemplate_1150', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1157, 0, false, '2013-04-25 01:50:12.216', NULL, 'Fastviewer 5 sessions (Turque)', 'TUR', 'ChargeTemplate_1156', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1158, 0, false, '2013-04-25 01:50:12.218', NULL, 'Fastviewer 5 sessions (Arabe)', 'ARA', 'ChargeTemplate_1156', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1159, 0, false, '2013-04-25 01:50:12.22', NULL, 'Fastviewer 5 sessions (Allemand)', 'DEU', 'ChargeTemplate_1156', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1160, 0, false, '2013-04-25 01:50:12.222', NULL, 'Fastviewer 5 sessions (Français)', 'FRA', 'ChargeTemplate_1156', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1161, 0, false, '2013-04-25 01:50:12.224', NULL, NULL, 'ENG', 'ChargeTemplate_1156', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1163, 0, false, '2013-04-25 01:51:12.288', NULL, 'Fastviewer 10 sessions (Turque)', 'TUR', 'ChargeTemplate_1162', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1164, 0, false, '2013-04-25 01:51:12.291', NULL, 'Fastviewer 10 sessions (Arabe)', 'ARA', 'ChargeTemplate_1162', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1165, 0, false, '2013-04-25 01:51:12.293', NULL, 'Fastviewer 10 sessions (Allemand)', 'DEU', 'ChargeTemplate_1162', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1166, 0, false, '2013-04-25 01:51:12.296', NULL, 'Fastviewer 10 sessions (Français)', 'FRA', 'ChargeTemplate_1162', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1167, 0, false, '2013-04-25 01:51:12.298', NULL, NULL, 'ENG', 'ChargeTemplate_1162', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1169, 0, false, '2013-04-25 02:08:07.851', NULL, 'Microsoft Dynamics CRM 2011 - 1 GB include (Arabe)', 'TUR', 'ChargeTemplate_1168', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1170, 0, false, '2013-04-25 02:08:07.854', NULL, 'Microsoft Dynamics CRM 2011 - 1 GB include (Arabe)', 'ARA', 'ChargeTemplate_1168', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1171, 0, false, '2013-04-25 02:08:07.856', NULL, 'Microsoft Dynamics CRM 2011 - 1 GB include (Arabe)', 'DEU', 'ChargeTemplate_1168', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1172, 0, false, '2013-04-25 02:08:07.858', NULL, 'Microsoft Dynamics CRM 2011 - 1 GB include (Arabe)', 'FRA', 'ChargeTemplate_1168', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1173, 0, false, '2013-04-25 02:08:07.86', NULL, NULL, 'ENG', 'ChargeTemplate_1168', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1140, 1, false, '2013-04-25 01:35:29.505', '2013-04-25 02:09:56.159', 'Microsoft Lync 2010 customer (Arabe)', 'ARA', 'ChargeTemplate_1138', 1, 1, 1);
INSERT INTO adm_messages VALUES (1139, 1, false, '2013-04-25 01:35:29.503', '2013-04-25 02:09:56.165', 'Microsoft Lync 2010 customer (Turque)', 'TUR', 'ChargeTemplate_1138', 1, 1, 1);
INSERT INTO adm_messages VALUES (1141, 1, false, '2013-04-25 01:35:29.507', '2013-04-25 02:09:56.169', 'Microsoft Lync 2010 customer (Allemand)', 'DEU', 'ChargeTemplate_1138', 1, 1, 1);
INSERT INTO adm_messages VALUES (1142, 1, false, '2013-04-25 01:35:29.509', '2013-04-25 02:09:56.174', 'Microsoft Lync 2010 customer (Français)', 'FRA', 'ChargeTemplate_1138', 1, 1, 1);
INSERT INTO adm_messages VALUES (1143, 1, false, '2013-04-25 01:35:29.511', '2013-04-25 02:09:56.178', NULL, 'ENG', 'ChargeTemplate_1138', 1, 1, 1);
INSERT INTO adm_messages VALUES (1113, 1, false, '2013-04-25 01:10:00.752', '2013-04-25 02:11:11.629', NULL, 'ENG', 'ChargeTemplate_388', 1, 1, 1);
INSERT INTO adm_messages VALUES (1175, 0, false, '2013-04-25 02:14:03.06', NULL, 'Microsoft Sharepoint 2010 - 1 GB include (Turque)', 'TUR', 'ChargeTemplate_1174', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1176, 0, false, '2013-04-25 02:14:03.062', NULL, 'Microsoft Sharepoint 2010 - 1 GB include (Arabe)', 'ARA', 'ChargeTemplate_1174', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1177, 0, false, '2013-04-25 02:14:03.064', NULL, 'Microsoft Sharepoint 2010 - 1 GB include (Allemand)', 'DEU', 'ChargeTemplate_1174', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1178, 0, false, '2013-04-25 02:14:03.066', NULL, 'Microsoft Sharepoint 2010 - 1 GB include (Français)', 'FRA', 'ChargeTemplate_1174', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1179, 0, false, '2013-04-25 02:14:03.067', NULL, NULL, 'ENG', 'ChargeTemplate_1174', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1259, 2, false, '2013-04-25 19:58:13.791', '2013-05-03 10:27:04.561', 'Taxe de 5 pourcent (Allemand)', 'DEU', 'Tax_290', 1, 1, 1);
INSERT INTO adm_messages VALUES (1260, 2, false, '2013-04-25 19:58:13.799', '2013-05-03 10:27:04.572', NULL, 'CES', 'Tax_290', 1, 1, 1);
INSERT INTO adm_messages VALUES (1262, 0, false, '2013-04-25 19:59:09.638', NULL, 'Taxe de 18 pourcent (Allemand)', 'DEU', 'Tax_282', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1193, 2, false, '2013-04-25 14:20:47.449', '2013-05-02 10:51:59.076', '19 Percent Tax (Turque)', 'TUR', 'Tax_1192', 1, 1, 1);
INSERT INTO adm_messages VALUES (1194, 2, false, '2013-04-25 14:20:47.45', '2013-05-02 10:51:59.092', '19 Percent Tax (Arabe)', 'ARA', 'Tax_1192', 1, 1, 1);
INSERT INTO adm_messages VALUES (1195, 2, false, '2013-04-25 14:20:47.452', '2013-05-02 10:51:59.102', '19 Percent Tax (Allemand)', 'DEU', 'Tax_1192', 1, 1, 1);
INSERT INTO adm_messages VALUES (1196, 2, false, '2013-04-25 14:20:47.453', '2013-05-02 10:51:59.111', '19 Percent Tax (Français)', 'FRA', 'Tax_1192', 1, 1, 1);
INSERT INTO adm_messages VALUES (1197, 2, false, '2013-04-25 14:20:47.454', '2013-05-02 10:51:59.12', NULL, 'ENG', 'Tax_1192', 1, 1, 1);
INSERT INTO adm_messages VALUES (1261, 2, false, '2013-04-25 19:58:13.809', '2013-05-03 10:27:04.593', NULL, 'ENG', 'Tax_290', 1, 1, 1);
INSERT INTO adm_messages VALUES (285, 1, false, '2013-04-17 15:53:55.449', '2013-04-25 19:59:09.656', 'Taxe de 18 pourcent (franÃ§ais)', 'FRA', 'Tax_282', 1, 1, 1);
INSERT INTO adm_messages VALUES (1264, 0, false, '2013-04-25 19:59:09.665', NULL, NULL, 'ENG', 'Tax_282', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1265, 0, false, '2013-04-25 19:59:54.071', NULL, 'Taxe de 0 pourcent (Allemand)', 'DEU', 'Tax_266', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1266, 0, false, '2013-04-25 19:59:54.082', NULL, NULL, 'CES', 'Tax_266', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1267, 0, false, '2013-04-25 19:59:54.099', NULL, NULL, 'ENG', 'Tax_266', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1274, 2, false, '2013-04-25 20:02:16.464', '2013-05-16 19:41:34.5', 'Souscription Data (Allemand)', 'DEU', 'InvoiceSubCategory_360', 1, 1, 1);
INSERT INTO adm_messages VALUES (363, 3, false, '2013-04-17 17:41:03.568', '2013-05-16 19:41:34.537', 'Souscription Data (FranÃ§ais)', 'FRA', 'InvoiceSubCategory_360', 1, 1, 1);
INSERT INTO adm_messages VALUES (1275, 2, false, '2013-04-25 20:02:16.473', '2013-05-16 19:41:34.549', NULL, 'CES', 'InvoiceSubCategory_360', 1, 1, 1);
INSERT INTO adm_messages VALUES (1276, 2, false, '2013-04-25 20:02:16.489', '2013-05-16 19:41:34.562', NULL, 'ENG', 'InvoiceSubCategory_360', 1, 1, 1);
INSERT INTO adm_messages VALUES (1089, 4, false, '2013-04-24 16:22:52.635', '2013-04-30 18:31:52.089', NULL, 'DEU', 'InvoiceCategory_240', 1, 1, 1);
INSERT INTO adm_messages VALUES (1090, 4, false, '2013-04-24 16:22:52.661', '2013-04-30 18:31:52.122', NULL, 'ENG', 'InvoiceCategory_240', 1, 1, 1);
INSERT INTO adm_messages VALUES (1404, 0, false, '2013-04-30 18:48:51.265', NULL, 'test', 'TUR', 'ChargeTemplate_1403', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1405, 0, false, '2013-04-30 18:48:51.267', NULL, 'test', 'ARA', 'ChargeTemplate_1403', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1406, 0, false, '2013-04-30 18:48:51.269', NULL, NULL, 'DEU', 'ChargeTemplate_1403', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1407, 0, false, '2013-04-30 18:48:51.27', NULL, NULL, 'FRA', 'ChargeTemplate_1403', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1408, 0, false, '2013-04-30 18:48:51.273', NULL, NULL, 'ENG', 'ChargeTemplate_1403', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1414, 0, false, '2013-04-30 18:52:42.199', NULL, NULL, 'TUR', 'ChargeTemplate_1184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1415, 0, false, '2013-04-30 18:52:42.205', NULL, 'ssfdsfdsf', 'ARA', 'ChargeTemplate_1184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1416, 0, false, '2013-04-30 18:52:42.21', NULL, NULL, 'DEU', 'ChargeTemplate_1184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1417, 0, false, '2013-04-30 18:52:42.216', NULL, NULL, 'FRA', 'ChargeTemplate_1184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1418, 0, false, '2013-04-30 18:52:42.221', NULL, NULL, 'ENG', 'ChargeTemplate_1184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1477, 0, false, '2013-05-01 19:27:35.257', NULL, NULL, 'TUR', 'Tax_1476', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1478, 0, false, '2013-05-01 19:27:35.259', NULL, '2', 'ARA', 'Tax_1476', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1479, 0, false, '2013-05-01 19:27:35.26', NULL, NULL, 'DEU', 'Tax_1476', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1480, 0, false, '2013-05-01 19:27:35.262', NULL, NULL, 'FRA', 'Tax_1476', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1481, 0, false, '2013-05-01 19:27:35.264', NULL, NULL, 'ENG', 'Tax_1476', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1483, 0, false, '2013-05-01 19:34:56.194', NULL, NULL, 'TUR', 'Tax_1482', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1484, 0, false, '2013-05-01 19:34:56.196', NULL, '2', 'ARA', 'Tax_1482', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1485, 0, false, '2013-05-01 19:34:56.198', NULL, NULL, 'DEU', 'Tax_1482', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1486, 0, false, '2013-05-01 19:34:56.2', NULL, NULL, 'FRA', 'Tax_1482', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1487, 0, false, '2013-05-01 19:34:56.202', NULL, NULL, 'ENG', 'Tax_1482', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1489, 0, false, '2013-05-01 19:40:21.11', NULL, NULL, 'TUR', 'Tax_1488', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1490, 0, false, '2013-05-01 19:40:21.112', NULL, '2', 'ARA', 'Tax_1488', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1491, 0, false, '2013-05-01 19:40:21.114', NULL, NULL, 'DEU', 'Tax_1488', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1492, 0, false, '2013-05-01 19:40:21.116', NULL, NULL, 'FRA', 'Tax_1488', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1493, 0, false, '2013-05-01 19:40:21.118', NULL, NULL, 'ENG', 'Tax_1488', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1495, 0, false, '2013-05-01 19:40:53.14', NULL, NULL, 'TUR', 'Tax_1494', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1496, 0, false, '2013-05-01 19:40:53.142', NULL, '2', 'ARA', 'Tax_1494', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1497, 0, false, '2013-05-01 19:40:53.145', NULL, NULL, 'DEU', 'Tax_1494', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1498, 0, false, '2013-05-01 19:40:53.147', NULL, NULL, 'FRA', 'Tax_1494', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1499, 0, false, '2013-05-01 19:40:53.15', NULL, NULL, 'ENG', 'Tax_1494', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1508, 0, false, '2013-05-01 20:30:31.547', NULL, NULL, 'TUR', 'InvoiceCategory_1507', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1977, 0, false, '2013-05-03 17:19:58.162', NULL, NULL, 'FRA', 'ChargeTemplate_1973', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1509, 0, false, '2013-05-01 20:30:31.549', NULL, '2', 'ARA', 'InvoiceCategory_1507', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1510, 0, false, '2013-05-01 20:30:31.55', NULL, NULL, 'DEU', 'InvoiceCategory_1507', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1511, 0, false, '2013-05-01 20:30:31.552', NULL, NULL, 'FRA', 'InvoiceCategory_1507', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1512, 0, false, '2013-05-01 20:30:31.554', NULL, NULL, 'ENG', 'InvoiceCategory_1507', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1514, 0, false, '2013-05-01 20:33:47.552', NULL, NULL, 'TUR', 'InvoiceCategory_1513', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1515, 0, false, '2013-05-01 20:33:47.555', NULL, '2', 'ARA', 'InvoiceCategory_1513', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1516, 0, false, '2013-05-01 20:33:47.557', NULL, NULL, 'DEU', 'InvoiceCategory_1513', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1517, 0, false, '2013-05-01 20:33:47.56', NULL, NULL, 'FRA', 'InvoiceCategory_1513', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1518, 0, false, '2013-05-01 20:33:47.562', NULL, NULL, 'ENG', 'InvoiceCategory_1513', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1520, 0, false, '2013-05-01 20:34:58.575', NULL, NULL, 'TUR', 'InvoiceCategory_1519', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1521, 0, false, '2013-05-01 20:34:58.577', NULL, '2', 'ARA', 'InvoiceCategory_1519', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1522, 0, false, '2013-05-01 20:34:58.579', NULL, NULL, 'DEU', 'InvoiceCategory_1519', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1523, 0, false, '2013-05-01 20:34:58.581', NULL, NULL, 'FRA', 'InvoiceCategory_1519', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1524, 0, false, '2013-05-01 20:34:58.582', NULL, NULL, 'ENG', 'InvoiceCategory_1519', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1526, 0, false, '2013-05-01 20:35:36.88', NULL, NULL, 'TUR', 'InvoiceCategory_1525', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1527, 0, false, '2013-05-01 20:35:36.882', NULL, '2', 'ARA', 'InvoiceCategory_1525', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1528, 0, false, '2013-05-01 20:35:36.884', NULL, NULL, 'DEU', 'InvoiceCategory_1525', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1529, 0, false, '2013-05-01 20:35:36.886', NULL, NULL, 'FRA', 'InvoiceCategory_1525', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1530, 0, false, '2013-05-01 20:35:36.889', NULL, NULL, 'ENG', 'InvoiceCategory_1525', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1294, 2, false, '2013-04-25 20:08:05.781', '2013-05-16 19:41:51.811', 'Souscription de domaine (allemand)', 'DEU', 'InvoiceSubCategory_306', 1, 1, 1);
INSERT INTO adm_messages VALUES (1295, 2, false, '2013-04-25 20:08:05.788', '2013-05-16 19:41:51.834', NULL, 'CES', 'InvoiceSubCategory_306', 1, 1, 1);
INSERT INTO adm_messages VALUES (1284, 2, false, '2013-04-25 20:06:09.35', '2013-05-16 19:42:08.908', 'Souscription de progiciel (Allemand)', 'DEU', 'InvoiceSubCategory_302', 1, 1, 1);
INSERT INTO adm_messages VALUES (1285, 2, false, '2013-04-25 20:06:09.357', '2013-05-16 19:42:08.924', NULL, 'CES', 'InvoiceSubCategory_302', 1, 1, 1);
INSERT INTO adm_messages VALUES (1410, 7, false, '2013-04-30 18:52:31.528', '2013-05-21 16:13:58.18', 'tes', 'ARA', 'ChargeTemplate_1182', 1, 1, 1);
INSERT INTO adm_messages VALUES (1409, 7, false, '2013-04-30 18:52:31.521', '2013-05-21 16:13:58.184', 'test2', 'TUR', 'ChargeTemplate_1182', 1, 1, 1);
INSERT INTO adm_messages VALUES (1411, 7, false, '2013-04-30 18:52:31.537', '2013-05-21 16:13:58.187', NULL, 'DEU', 'ChargeTemplate_1182', 1, 1, 1);
INSERT INTO adm_messages VALUES (1412, 7, false, '2013-04-30 18:52:31.543', '2013-05-21 16:13:58.191', NULL, 'FRA', 'ChargeTemplate_1182', 1, 1, 1);
INSERT INTO adm_messages VALUES (1420, 3, false, '2013-04-30 18:52:53.348', '2013-05-23 11:52:32.082', 'sefdsfdsf', 'ARA', 'ChargeTemplate_1185', 1, 1, 1);
INSERT INTO adm_messages VALUES (1419, 3, false, '2013-04-30 18:52:53.341', '2013-05-23 11:52:32.123', NULL, 'TUR', 'ChargeTemplate_1185', 1, 1, 1);
INSERT INTO adm_messages VALUES (1421, 3, false, '2013-04-30 18:52:53.354', '2013-05-23 11:52:32.133', NULL, 'DEU', 'ChargeTemplate_1185', 1, 1, 1);
INSERT INTO adm_messages VALUES (1422, 3, false, '2013-04-30 18:52:53.361', '2013-05-23 11:52:32.142', NULL, 'FRA', 'ChargeTemplate_1185', 1, 1, 1);
INSERT INTO adm_messages VALUES (1423, 3, false, '2013-04-30 18:52:53.367', '2013-05-23 11:52:32.151', NULL, 'ENG', 'ChargeTemplate_1185', 1, 1, 1);
INSERT INTO adm_messages VALUES (1318, 7, false, '2013-04-26 11:39:02.31', '2013-05-23 11:53:00.305', NULL, 'ARA', 'ChargeTemplate_1183', 1, 1, 1);
INSERT INTO adm_messages VALUES (1317, 7, false, '2013-04-26 11:39:02.249', '2013-05-23 11:53:00.318', NULL, 'TUR', 'ChargeTemplate_1183', 1, 1, 1);
INSERT INTO adm_messages VALUES (1319, 7, false, '2013-04-26 11:39:02.319', '2013-05-23 11:53:00.33', NULL, 'DEU', 'ChargeTemplate_1183', 1, 1, 1);
INSERT INTO adm_messages VALUES (1320, 7, false, '2013-04-26 11:39:02.329', '2013-05-23 11:53:00.351', NULL, 'CES', 'ChargeTemplate_1183', 1, 1, 1);
INSERT INTO adm_messages VALUES (1533, 0, false, '2013-05-01 20:40:31.794', NULL, '2', 'ARA', 'InvoiceCategory_1531', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1534, 0, false, '2013-05-01 20:40:31.797', NULL, NULL, 'DEU', 'InvoiceCategory_1531', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1535, 0, false, '2013-05-01 20:40:31.798', NULL, NULL, 'FRA', 'InvoiceCategory_1531', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1536, 0, false, '2013-05-01 20:40:31.8', NULL, NULL, 'ENG', 'InvoiceCategory_1531', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1062, 1, false, '2013-04-24 07:27:29.72', '2013-05-02 08:53:09.439', NULL, 'FRA', 'ChargeTemplate_1058', 1, 1, 1);
INSERT INTO adm_messages VALUES (1551, 0, false, '2013-05-02 08:53:09.457', NULL, NULL, 'ENG', 'ChargeTemplate_1058', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1555, 0, false, '2013-05-02 10:10:42.439', NULL, NULL, 'TUR', 'InvoiceSubCategory_1554', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1556, 0, false, '2013-05-02 10:10:42.442', NULL, '2', 'ARA', 'InvoiceSubCategory_1554', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1557, 0, false, '2013-05-02 10:10:42.444', NULL, NULL, 'DEU', 'InvoiceSubCategory_1554', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1558, 0, false, '2013-05-02 10:10:42.446', NULL, NULL, 'FRA', 'InvoiceSubCategory_1554', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1559, 0, false, '2013-05-02 10:10:42.449', NULL, NULL, 'ENG', 'InvoiceSubCategory_1554', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1561, 0, false, '2013-05-02 10:12:54.29', NULL, NULL, 'TUR', 'Tax_1560', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1562, 0, false, '2013-05-02 10:12:54.291', NULL, '2', 'ARA', 'Tax_1560', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1563, 0, false, '2013-05-02 10:12:54.292', NULL, NULL, 'DEU', 'Tax_1560', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1564, 0, false, '2013-05-02 10:12:54.294', NULL, NULL, 'FRA', 'Tax_1560', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1565, 0, false, '2013-05-02 10:12:54.295', NULL, NULL, 'ENG', 'Tax_1560', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1567, 0, false, '2013-05-02 10:16:24.522', NULL, NULL, 'TUR', 'InvoiceSubCategory_1566', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1568, 0, false, '2013-05-02 10:16:24.525', NULL, '2', 'ARA', 'InvoiceSubCategory_1566', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1569, 0, false, '2013-05-02 10:16:24.53', NULL, NULL, 'DEU', 'InvoiceSubCategory_1566', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1570, 0, false, '2013-05-02 10:16:24.532', NULL, NULL, 'FRA', 'InvoiceSubCategory_1566', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1571, 0, false, '2013-05-02 10:16:24.534', NULL, NULL, 'ENG', 'InvoiceSubCategory_1566', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1573, 1, false, '2013-05-02 10:35:10.056', '2013-05-02 10:40:35.306', NULL, 'TUR', 'InvoiceSubCategory_1572', 1, 1, 1);
INSERT INTO adm_messages VALUES (1574, 1, false, '2013-05-02 10:35:10.058', '2013-05-02 10:40:35.32', '2', 'ARA', 'InvoiceSubCategory_1572', 1, 1, 1);
INSERT INTO adm_messages VALUES (1575, 1, false, '2013-05-02 10:35:10.06', '2013-05-02 10:40:35.326', NULL, 'DEU', 'InvoiceSubCategory_1572', 1, 1, 1);
INSERT INTO adm_messages VALUES (1576, 1, false, '2013-05-02 10:35:10.063', '2013-05-02 10:40:35.332', NULL, 'FRA', 'InvoiceSubCategory_1572', 1, 1, 1);
INSERT INTO adm_messages VALUES (1577, 1, false, '2013-05-02 10:35:10.066', '2013-05-02 10:40:35.338', NULL, 'ENG', 'InvoiceSubCategory_1572', 1, 1, 1);
INSERT INTO adm_messages VALUES (1579, 1, false, '2013-05-02 10:43:11.854', '2013-05-02 10:45:47.762', NULL, 'TUR', 'InvoiceSubCategory_1578', 1, 1, 1);
INSERT INTO adm_messages VALUES (1580, 1, false, '2013-05-02 10:43:11.857', '2013-05-02 10:45:47.767', '2', 'ARA', 'InvoiceSubCategory_1578', 1, 1, 1);
INSERT INTO adm_messages VALUES (1581, 1, false, '2013-05-02 10:43:11.859', '2013-05-02 10:45:47.772', NULL, 'DEU', 'InvoiceSubCategory_1578', 1, 1, 1);
INSERT INTO adm_messages VALUES (1582, 1, false, '2013-05-02 10:43:11.861', '2013-05-02 10:45:47.776', NULL, 'FRA', 'InvoiceSubCategory_1578', 1, 1, 1);
INSERT INTO adm_messages VALUES (1583, 1, false, '2013-05-02 10:43:11.863', '2013-05-02 10:45:47.782', NULL, 'ENG', 'InvoiceSubCategory_1578', 1, 1, 1);
INSERT INTO adm_messages VALUES (1586, 0, false, '2013-05-02 10:47:05.449', NULL, NULL, 'TUR', 'InvoiceSubCategory_1585', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1587, 0, false, '2013-05-02 10:47:05.451', NULL, '2', 'ARA', 'InvoiceSubCategory_1585', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1588, 0, false, '2013-05-02 10:47:05.452', NULL, NULL, 'DEU', 'InvoiceSubCategory_1585', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1589, 0, false, '2013-05-02 10:47:05.453', NULL, NULL, 'FRA', 'InvoiceSubCategory_1585', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1590, 0, false, '2013-05-02 10:47:05.454', NULL, NULL, 'ENG', 'InvoiceSubCategory_1585', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1592, 0, false, '2013-05-02 10:51:47.194', NULL, NULL, 'DEU', 'Tax_286', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1593, 0, false, '2013-05-02 10:51:47.263', NULL, NULL, 'ENG', 'Tax_286', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1610, 0, false, '2013-05-02 11:10:16.511', NULL, NULL, 'TUR', 'InvoiceSubCategory_1609', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1611, 0, false, '2013-05-02 11:10:16.513', NULL, '2', 'ARA', 'InvoiceSubCategory_1609', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1612, 0, false, '2013-05-02 11:10:16.514', NULL, NULL, 'DEU', 'InvoiceSubCategory_1609', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1613, 0, false, '2013-05-02 11:10:16.516', NULL, NULL, 'FRA', 'InvoiceSubCategory_1609', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1614, 0, false, '2013-05-02 11:10:16.517', NULL, NULL, 'ENG', 'InvoiceSubCategory_1609', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1616, 0, false, '2013-05-02 11:14:29.148', NULL, NULL, 'TUR', 'InvoiceSubCategory_1615', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1617, 0, false, '2013-05-02 11:14:29.15', NULL, '2', 'ARA', 'InvoiceSubCategory_1615', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1618, 0, false, '2013-05-02 11:14:29.151', NULL, NULL, 'DEU', 'InvoiceSubCategory_1615', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1619, 0, false, '2013-05-02 11:14:29.152', NULL, NULL, 'FRA', 'InvoiceSubCategory_1615', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1620, 0, false, '2013-05-02 11:14:29.154', NULL, NULL, 'ENG', 'InvoiceSubCategory_1615', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1622, 0, false, '2013-05-02 11:21:29.252', NULL, NULL, 'TUR', 'InvoiceSubCategory_1621', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1623, 0, false, '2013-05-02 11:21:29.254', NULL, '2', 'ARA', 'InvoiceSubCategory_1621', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1624, 0, false, '2013-05-02 11:21:29.256', NULL, NULL, 'DEU', 'InvoiceSubCategory_1621', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1625, 0, false, '2013-05-02 11:21:29.258', NULL, NULL, 'FRA', 'InvoiceSubCategory_1621', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1626, 0, false, '2013-05-02 11:21:29.26', NULL, NULL, 'ENG', 'InvoiceSubCategory_1621', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1628, 0, false, '2013-05-02 11:23:17.114', NULL, NULL, 'TUR', 'InvoiceSubCategory_1627', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1978, 0, false, '2013-05-03 17:19:58.164', NULL, NULL, 'ENG', 'ChargeTemplate_1973', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1629, 0, false, '2013-05-02 11:23:17.117', NULL, '2', 'ARA', 'InvoiceSubCategory_1627', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1630, 0, false, '2013-05-02 11:23:17.119', NULL, NULL, 'DEU', 'InvoiceSubCategory_1627', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1631, 0, false, '2013-05-02 11:23:17.122', NULL, NULL, 'FRA', 'InvoiceSubCategory_1627', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1632, 0, false, '2013-05-02 11:23:17.124', NULL, NULL, 'ENG', 'InvoiceSubCategory_1627', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1634, 0, false, '2013-05-02 11:25:33.394', NULL, NULL, 'TUR', 'InvoiceSubCategory_1633', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1635, 0, false, '2013-05-02 11:25:33.396', NULL, '2', 'ARA', 'InvoiceSubCategory_1633', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1636, 0, false, '2013-05-02 11:25:33.398', NULL, NULL, 'DEU', 'InvoiceSubCategory_1633', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1637, 0, false, '2013-05-02 11:25:33.4', NULL, NULL, 'FRA', 'InvoiceSubCategory_1633', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1638, 0, false, '2013-05-02 11:25:33.403', NULL, NULL, 'ENG', 'InvoiceSubCategory_1633', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1640, 1, false, '2013-05-02 11:32:24.36', '2013-05-02 11:33:54.77', NULL, 'TUR', 'InvoiceSubCategory_1639', 1, 1, 1);
INSERT INTO adm_messages VALUES (1641, 1, false, '2013-05-02 11:32:24.362', '2013-05-02 11:33:54.783', '2', 'ARA', 'InvoiceSubCategory_1639', 1, 1, 1);
INSERT INTO adm_messages VALUES (1642, 1, false, '2013-05-02 11:32:24.364', '2013-05-02 11:33:54.797', NULL, 'DEU', 'InvoiceSubCategory_1639', 1, 1, 1);
INSERT INTO adm_messages VALUES (1643, 1, false, '2013-05-02 11:32:24.367', '2013-05-02 11:33:54.808', NULL, 'FRA', 'InvoiceSubCategory_1639', 1, 1, 1);
INSERT INTO adm_messages VALUES (1644, 1, false, '2013-05-02 11:32:24.369', '2013-05-02 11:33:54.819', NULL, 'ENG', 'InvoiceSubCategory_1639', 1, 1, 1);
INSERT INTO adm_messages VALUES (1650, 0, false, '2013-05-02 11:45:14.609', NULL, NULL, 'TUR', 'InvoiceSubCategory_1649', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1651, 0, false, '2013-05-02 11:45:14.61', NULL, NULL, 'ARA', 'InvoiceSubCategory_1649', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1652, 0, false, '2013-05-02 11:45:14.612', NULL, NULL, 'DEU', 'InvoiceSubCategory_1649', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1653, 0, false, '2013-05-02 11:45:14.613', NULL, NULL, 'FAO', 'InvoiceSubCategory_1649', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1654, 0, false, '2013-05-02 11:45:14.615', NULL, '3', 'FRA', 'InvoiceSubCategory_1649', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1655, 0, false, '2013-05-02 11:45:14.617', NULL, NULL, 'ENG', 'InvoiceSubCategory_1649', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1657, 0, false, '2013-05-02 11:46:47.409', NULL, NULL, 'TUR', 'InvoiceSubCategory_1656', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1658, 0, false, '2013-05-02 11:46:47.411', NULL, NULL, 'ARA', 'InvoiceSubCategory_1656', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1659, 0, false, '2013-05-02 11:46:47.413', NULL, NULL, 'DEU', 'InvoiceSubCategory_1656', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1660, 0, false, '2013-05-02 11:46:47.414', NULL, NULL, 'FAO', 'InvoiceSubCategory_1656', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1661, 0, false, '2013-05-02 11:46:47.416', NULL, '3', 'FRA', 'InvoiceSubCategory_1656', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1662, 0, false, '2013-05-02 11:46:47.417', NULL, NULL, 'ENG', 'InvoiceSubCategory_1656', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1665, 0, false, '2013-05-02 11:48:04.646', NULL, NULL, 'TUR', 'InvoiceSubCategory_1664', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1666, 0, false, '2013-05-02 11:48:04.647', NULL, NULL, 'ARA', 'InvoiceSubCategory_1664', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1667, 0, false, '2013-05-02 11:48:04.648', NULL, NULL, 'DEU', 'InvoiceSubCategory_1664', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1668, 0, false, '2013-05-02 11:48:04.65', NULL, NULL, 'FAO', 'InvoiceSubCategory_1664', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1669, 0, false, '2013-05-02 11:48:04.651', NULL, '3', 'FRA', 'InvoiceSubCategory_1664', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1670, 0, false, '2013-05-02 11:48:04.653', NULL, NULL, 'ENG', 'InvoiceSubCategory_1664', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1672, 0, false, '2013-05-02 11:48:45.285', NULL, NULL, 'TUR', 'InvoiceSubCategory_1671', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1673, 0, false, '2013-05-02 11:48:45.287', NULL, NULL, 'ARA', 'InvoiceSubCategory_1671', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1674, 0, false, '2013-05-02 11:48:45.289', NULL, NULL, 'DEU', 'InvoiceSubCategory_1671', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1675, 0, false, '2013-05-02 11:48:45.291', NULL, NULL, 'FAO', 'InvoiceSubCategory_1671', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1676, 0, false, '2013-05-02 11:48:45.292', NULL, '3', 'FRA', 'InvoiceSubCategory_1671', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1677, 0, false, '2013-05-02 11:48:45.294', NULL, NULL, 'ENG', 'InvoiceSubCategory_1671', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1681, 1, false, '2013-05-02 11:50:29.304', '2013-05-02 11:50:32.303', NULL, 'TUR', 'InvoiceSubCategory_1680', 1, 1, 1);
INSERT INTO adm_messages VALUES (1682, 1, false, '2013-05-02 11:50:29.307', '2013-05-02 11:50:32.311', NULL, 'ARA', 'InvoiceSubCategory_1680', 1, 1, 1);
INSERT INTO adm_messages VALUES (1683, 1, false, '2013-05-02 11:50:29.309', '2013-05-02 11:50:32.317', NULL, 'DEU', 'InvoiceSubCategory_1680', 1, 1, 1);
INSERT INTO adm_messages VALUES (1686, 0, false, '2013-05-02 11:50:32.325', NULL, NULL, 'FAO', 'InvoiceSubCategory_1680', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1684, 1, false, '2013-05-02 11:50:29.311', '2013-05-02 11:50:32.336', '3', 'FRA', 'InvoiceSubCategory_1680', 1, 1, 1);
INSERT INTO adm_messages VALUES (1685, 1, false, '2013-05-02 11:50:29.314', '2013-05-02 11:50:32.344', NULL, 'ENG', 'InvoiceSubCategory_1680', 1, 1, 1);
INSERT INTO adm_messages VALUES (1688, 0, false, '2013-05-02 11:53:24.184', NULL, NULL, 'TUR', 'InvoiceSubCategory_1687', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1689, 0, false, '2013-05-02 11:53:24.186', NULL, NULL, 'ARA', 'InvoiceSubCategory_1687', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1690, 0, false, '2013-05-02 11:53:24.189', NULL, NULL, 'DEU', 'InvoiceSubCategory_1687', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1691, 0, false, '2013-05-02 11:53:24.19', NULL, NULL, 'FAO', 'InvoiceSubCategory_1687', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1692, 0, false, '2013-05-02 11:53:24.192', NULL, '3', 'FRA', 'InvoiceSubCategory_1687', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1693, 0, false, '2013-05-02 11:53:24.194', NULL, NULL, 'ENG', 'InvoiceSubCategory_1687', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1700, 0, false, '2013-05-02 12:02:04.134', NULL, NULL, 'TUR', 'InvoiceSubCategory_1699', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1701, 0, false, '2013-05-02 12:02:04.137', NULL, '2', 'ARA', 'InvoiceSubCategory_1699', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1702, 0, false, '2013-05-02 12:02:04.139', NULL, NULL, 'DEU', 'InvoiceSubCategory_1699', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1703, 0, false, '2013-05-02 12:02:04.141', NULL, NULL, 'FRA', 'InvoiceSubCategory_1699', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1704, 0, false, '2013-05-02 12:02:04.143', NULL, NULL, 'ENG', 'InvoiceSubCategory_1699', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1808, 0, false, '2013-05-02 21:57:05.05', NULL, NULL, 'TUR', 'Tax_1807', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1809, 0, false, '2013-05-02 21:57:05.052', NULL, '2', 'ARA', 'Tax_1807', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1810, 0, false, '2013-05-02 21:57:05.053', NULL, NULL, 'DEU', 'Tax_1807', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1811, 0, false, '2013-05-02 21:57:05.054', NULL, NULL, 'FRA', 'Tax_1807', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1812, 0, false, '2013-05-02 21:57:05.056', NULL, NULL, 'ENG', 'Tax_1807', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1816, 0, false, '2013-05-02 23:41:04.079', NULL, NULL, 'TUR', 'Tax_1815', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1817, 0, false, '2013-05-02 23:41:04.081', NULL, '2', 'ARA', 'Tax_1815', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1818, 0, false, '2013-05-02 23:41:04.082', NULL, NULL, 'DEU', 'Tax_1815', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1819, 0, false, '2013-05-02 23:41:04.083', NULL, NULL, 'FRA', 'Tax_1815', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1820, 0, false, '2013-05-02 23:41:04.085', NULL, NULL, 'ENG', 'Tax_1815', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1823, 0, false, '2013-05-03 00:10:04.359', NULL, NULL, 'TUR', 'InvoiceCategory_1822', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1824, 0, false, '2013-05-03 00:10:04.361', NULL, '2', 'ARA', 'InvoiceCategory_1822', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1825, 0, false, '2013-05-03 00:10:04.362', NULL, NULL, 'DEU', 'InvoiceCategory_1822', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1826, 0, false, '2013-05-03 00:10:04.364', NULL, NULL, 'FRA', 'InvoiceCategory_1822', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1827, 0, false, '2013-05-03 00:10:04.366', NULL, NULL, 'ENG', 'InvoiceCategory_1822', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1829, 0, false, '2013-05-03 00:12:52.346', NULL, NULL, 'TUR', 'InvoiceCategory_1828', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1830, 0, false, '2013-05-03 00:12:52.347', NULL, '2', 'ARA', 'InvoiceCategory_1828', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1831, 0, false, '2013-05-03 00:12:52.349', NULL, NULL, 'DEU', 'InvoiceCategory_1828', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1832, 0, false, '2013-05-03 00:12:52.351', NULL, NULL, 'FRA', 'InvoiceCategory_1828', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1833, 0, false, '2013-05-03 00:12:52.353', NULL, NULL, 'ENG', 'InvoiceCategory_1828', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1893, 0, false, '2013-05-03 14:52:56.55', NULL, NULL, 'TUR', 'InvoiceCategory_1892', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1894, 0, false, '2013-05-03 14:52:56.552', NULL, '2', 'ARA', 'InvoiceCategory_1892', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1895, 0, false, '2013-05-03 14:52:56.554', NULL, NULL, 'DEU', 'InvoiceCategory_1892', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1896, 0, false, '2013-05-03 14:52:56.555', NULL, NULL, 'FRA', 'InvoiceCategory_1892', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1897, 0, false, '2013-05-03 14:52:56.557', NULL, NULL, 'ENG', 'InvoiceCategory_1892', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1899, 0, false, '2013-05-03 14:59:42.744', NULL, NULL, 'TUR', 'InvoiceCategory_1898', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1900, 0, false, '2013-05-03 14:59:42.744', NULL, '2', 'ARA', 'InvoiceCategory_1898', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1901, 0, false, '2013-05-03 14:59:42.745', NULL, NULL, 'DEU', 'InvoiceCategory_1898', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1902, 0, false, '2013-05-03 14:59:42.746', NULL, NULL, 'FRA', 'InvoiceCategory_1898', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1903, 0, false, '2013-05-03 14:59:42.747', NULL, NULL, 'ENG', 'InvoiceCategory_1898', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1906, 0, false, '2013-05-03 15:23:50.205', NULL, NULL, 'TUR', 'InvoiceCategory_1905', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1907, 0, false, '2013-05-03 15:23:50.206', NULL, '2', 'ARA', 'InvoiceCategory_1905', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1908, 0, false, '2013-05-03 15:23:50.208', NULL, NULL, 'DEU', 'InvoiceCategory_1905', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1909, 0, false, '2013-05-03 15:23:50.21', NULL, NULL, 'FRA', 'InvoiceCategory_1905', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1910, 0, false, '2013-05-03 15:23:50.212', NULL, NULL, 'ENG', 'InvoiceCategory_1905', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1912, 0, false, '2013-05-03 15:29:50.693', NULL, NULL, 'TUR', 'InvoiceCategory_1911', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1913, 0, false, '2013-05-03 15:29:50.695', NULL, '2', 'ARA', 'InvoiceCategory_1911', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1914, 0, false, '2013-05-03 15:29:50.696', NULL, NULL, 'DEU', 'InvoiceCategory_1911', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1915, 0, false, '2013-05-03 15:29:50.698', NULL, NULL, 'FRA', 'InvoiceCategory_1911', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1916, 0, false, '2013-05-03 15:29:50.699', NULL, NULL, 'ENG', 'InvoiceCategory_1911', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1918, 0, false, '2013-05-03 15:39:54.549', NULL, NULL, 'TUR', 'InvoiceCategory_1917', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1919, 0, false, '2013-05-03 15:39:54.551', NULL, '2', 'ARA', 'InvoiceCategory_1917', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1920, 0, false, '2013-05-03 15:39:54.552', NULL, NULL, 'DEU', 'InvoiceCategory_1917', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1921, 0, false, '2013-05-03 15:39:54.553', NULL, NULL, 'FRA', 'InvoiceCategory_1917', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1922, 0, false, '2013-05-03 15:39:54.555', NULL, NULL, 'ENG', 'InvoiceCategory_1917', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1925, 0, false, '2013-05-03 16:15:34.45', NULL, NULL, 'TUR', 'ChargeTemplate_1924', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1926, 0, false, '2013-05-03 16:15:34.452', NULL, '2', 'ARA', 'ChargeTemplate_1924', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1927, 0, false, '2013-05-03 16:15:34.454', NULL, NULL, 'DEU', 'ChargeTemplate_1924', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1928, 0, false, '2013-05-03 16:15:34.455', NULL, NULL, 'FRA', 'ChargeTemplate_1924', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1929, 0, false, '2013-05-03 16:15:34.457', NULL, NULL, 'ENG', 'ChargeTemplate_1924', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1931, 0, false, '2013-05-03 16:28:42.811', NULL, NULL, 'TUR', 'ChargeTemplate_1930', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1932, 0, false, '2013-05-03 16:28:42.813', NULL, '2', 'ARA', 'ChargeTemplate_1930', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1933, 0, false, '2013-05-03 16:28:42.814', NULL, NULL, 'DEU', 'ChargeTemplate_1930', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1934, 0, false, '2013-05-03 16:28:42.815', NULL, NULL, 'FRA', 'ChargeTemplate_1930', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1935, 0, false, '2013-05-03 16:28:42.816', NULL, NULL, 'ENG', 'ChargeTemplate_1930', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1937, 0, false, '2013-05-03 16:32:02.82', NULL, NULL, 'TUR', 'ChargeTemplate_1936', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1938, 0, false, '2013-05-03 16:32:02.821', NULL, '2', 'ARA', 'ChargeTemplate_1936', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1939, 0, false, '2013-05-03 16:32:02.822', NULL, NULL, 'DEU', 'ChargeTemplate_1936', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1940, 0, false, '2013-05-03 16:32:02.823', NULL, NULL, 'FRA', 'ChargeTemplate_1936', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1941, 0, false, '2013-05-03 16:32:02.824', NULL, NULL, 'ENG', 'ChargeTemplate_1936', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1943, 0, false, '2013-05-03 16:32:24.716', NULL, NULL, 'TUR', 'ChargeTemplate_1942', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1944, 0, false, '2013-05-03 16:32:24.717', NULL, '2', 'ARA', 'ChargeTemplate_1942', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1945, 0, false, '2013-05-03 16:32:24.719', NULL, NULL, 'DEU', 'ChargeTemplate_1942', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1946, 0, false, '2013-05-03 16:32:24.72', NULL, NULL, 'FRA', 'ChargeTemplate_1942', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1947, 0, false, '2013-05-03 16:32:24.722', NULL, NULL, 'ENG', 'ChargeTemplate_1942', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1949, 0, false, '2013-05-03 16:57:57.538', NULL, NULL, 'TUR', 'ChargeTemplate_1948', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1950, 0, false, '2013-05-03 16:57:57.539', NULL, '2', 'ARA', 'ChargeTemplate_1948', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1951, 0, false, '2013-05-03 16:57:57.54', NULL, NULL, 'DEU', 'ChargeTemplate_1948', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1952, 0, false, '2013-05-03 16:57:57.541', NULL, NULL, 'FRA', 'ChargeTemplate_1948', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1953, 0, false, '2013-05-03 16:57:57.542', NULL, NULL, 'ENG', 'ChargeTemplate_1948', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1956, 0, false, '2013-05-03 17:01:26.979', NULL, NULL, 'TUR', 'ChargeTemplate_1955', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1957, 0, false, '2013-05-03 17:01:26.98', NULL, '2', 'ARA', 'ChargeTemplate_1955', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1958, 0, false, '2013-05-03 17:01:26.982', NULL, NULL, 'DEU', 'ChargeTemplate_1955', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1959, 0, false, '2013-05-03 17:01:26.983', NULL, NULL, 'FRA', 'ChargeTemplate_1955', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1960, 0, false, '2013-05-03 17:01:26.984', NULL, NULL, 'ENG', 'ChargeTemplate_1955', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1962, 0, false, '2013-05-03 17:11:48.215', NULL, NULL, 'TUR', 'ChargeTemplate_1961', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1963, 0, false, '2013-05-03 17:11:48.216', NULL, '2', 'ARA', 'ChargeTemplate_1961', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1964, 0, false, '2013-05-03 17:11:48.218', NULL, NULL, 'DEU', 'ChargeTemplate_1961', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1965, 0, false, '2013-05-03 17:11:48.219', NULL, NULL, 'FRA', 'ChargeTemplate_1961', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1966, 0, false, '2013-05-03 17:11:48.22', NULL, NULL, 'ENG', 'ChargeTemplate_1961', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1968, 0, false, '2013-05-03 17:12:17.766', NULL, NULL, 'TUR', 'ChargeTemplate_1967', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1969, 0, false, '2013-05-03 17:12:17.768', NULL, '2', 'ARA', 'ChargeTemplate_1967', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1970, 0, false, '2013-05-03 17:12:17.769', NULL, NULL, 'DEU', 'ChargeTemplate_1967', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1971, 0, false, '2013-05-03 17:12:17.77', NULL, NULL, 'FRA', 'ChargeTemplate_1967', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1972, 0, false, '2013-05-03 17:12:17.771', NULL, NULL, 'ENG', 'ChargeTemplate_1967', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1974, 0, false, '2013-05-03 17:19:58.158', NULL, NULL, 'TUR', 'ChargeTemplate_1973', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1975, 0, false, '2013-05-03 17:19:58.159', NULL, '2', 'ARA', 'ChargeTemplate_1973', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1976, 0, false, '2013-05-03 17:19:58.161', NULL, NULL, 'DEU', 'ChargeTemplate_1973', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1984, 0, false, '2013-05-03 17:22:35.164', NULL, NULL, 'ENG', 'ChargeTemplate_1979', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1986, 0, false, '2013-05-03 17:24:56.938', NULL, NULL, 'TUR', 'ChargeTemplate_1985', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1987, 0, false, '2013-05-03 17:24:56.94', NULL, '2', 'ARA', 'ChargeTemplate_1985', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1988, 0, false, '2013-05-03 17:24:56.941', NULL, NULL, 'DEU', 'ChargeTemplate_1985', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1989, 0, false, '2013-05-03 17:24:56.943', NULL, NULL, 'FRA', 'ChargeTemplate_1985', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1990, 0, false, '2013-05-03 17:24:56.944', NULL, NULL, 'ENG', 'ChargeTemplate_1985', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1992, 0, false, '2013-05-03 17:25:56.086', NULL, NULL, 'TUR', 'ChargeTemplate_1991', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1993, 0, false, '2013-05-03 17:25:56.087', NULL, '2', 'ARA', 'ChargeTemplate_1991', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1994, 0, false, '2013-05-03 17:25:56.088', NULL, NULL, 'DEU', 'ChargeTemplate_1991', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1995, 0, false, '2013-05-03 17:25:56.089', NULL, NULL, 'FRA', 'ChargeTemplate_1991', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1996, 0, false, '2013-05-03 17:25:56.09', NULL, NULL, 'ENG', 'ChargeTemplate_1991', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2000, 0, false, '2013-05-03 17:33:26.244', NULL, NULL, 'TUR', 'ChargeTemplate_1999', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2001, 0, false, '2013-05-03 17:33:26.246', NULL, '2', 'ARA', 'ChargeTemplate_1999', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2002, 0, false, '2013-05-03 17:33:26.247', NULL, NULL, 'DEU', 'ChargeTemplate_1999', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2003, 0, false, '2013-05-03 17:33:26.248', NULL, NULL, 'FRA', 'ChargeTemplate_1999', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2004, 0, false, '2013-05-03 17:33:26.25', NULL, NULL, 'ENG', 'ChargeTemplate_1999', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2006, 0, false, '2013-05-03 17:37:44.218', NULL, NULL, 'TUR', 'ChargeTemplate_2005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2007, 0, false, '2013-05-03 17:37:44.219', NULL, '2', 'ARA', 'ChargeTemplate_2005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2008, 0, false, '2013-05-03 17:37:44.221', NULL, NULL, 'DEU', 'ChargeTemplate_2005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2009, 0, false, '2013-05-03 17:37:44.222', NULL, NULL, 'FRA', 'ChargeTemplate_2005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2010, 0, false, '2013-05-03 17:37:44.224', NULL, NULL, 'ENG', 'ChargeTemplate_2005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2014, 0, false, '2013-05-03 17:39:25.527', NULL, NULL, 'TUR', 'ChargeTemplate_2013', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2015, 0, false, '2013-05-03 17:39:25.529', NULL, '2', 'ARA', 'ChargeTemplate_2013', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2016, 0, false, '2013-05-03 17:39:25.53', NULL, NULL, 'DEU', 'ChargeTemplate_2013', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2017, 0, false, '2013-05-03 17:39:25.532', NULL, NULL, 'FRA', 'ChargeTemplate_2013', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2018, 0, false, '2013-05-03 17:39:25.533', NULL, NULL, 'ENG', 'ChargeTemplate_2013', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2024, 0, false, '2013-05-03 18:00:14.572', NULL, NULL, 'TUR', 'ChargeTemplate_2023', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2025, 0, false, '2013-05-03 18:00:14.573', NULL, '2', 'ARA', 'ChargeTemplate_2023', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2026, 0, false, '2013-05-03 18:00:14.574', NULL, NULL, 'DEU', 'ChargeTemplate_2023', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2027, 0, false, '2013-05-03 18:00:14.576', NULL, NULL, 'FRA', 'ChargeTemplate_2023', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2028, 0, false, '2013-05-03 18:00:14.577', NULL, NULL, 'ENG', 'ChargeTemplate_2023', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2030, 0, false, '2013-05-03 18:05:54.757', NULL, NULL, 'TUR', 'ChargeTemplate_2029', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2031, 0, false, '2013-05-03 18:05:54.758', NULL, '2', 'ARA', 'ChargeTemplate_2029', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2032, 0, false, '2013-05-03 18:05:54.759', NULL, NULL, 'DEU', 'ChargeTemplate_2029', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2033, 0, false, '2013-05-03 18:05:54.761', NULL, NULL, 'FRA', 'ChargeTemplate_2029', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2034, 0, false, '2013-05-03 18:05:54.762', NULL, NULL, 'ENG', 'ChargeTemplate_2029', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2036, 0, false, '2013-05-03 18:07:34.634', NULL, NULL, 'TUR', 'ChargeTemplate_2035', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2037, 0, false, '2013-05-03 18:07:34.635', NULL, '2', 'ARA', 'ChargeTemplate_2035', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2038, 0, false, '2013-05-03 18:07:34.637', NULL, NULL, 'DEU', 'ChargeTemplate_2035', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2039, 0, false, '2013-05-03 18:07:34.638', NULL, NULL, 'FRA', 'ChargeTemplate_2035', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2040, 0, false, '2013-05-03 18:07:34.64', NULL, NULL, 'ENG', 'ChargeTemplate_2035', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2042, 0, false, '2013-05-03 18:07:55.337', NULL, NULL, 'TUR', 'ChargeTemplate_2041', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2043, 0, false, '2013-05-03 18:07:55.339', NULL, '2', 'ARA', 'ChargeTemplate_2041', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2044, 0, false, '2013-05-03 18:07:55.341', NULL, NULL, 'DEU', 'ChargeTemplate_2041', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2045, 0, false, '2013-05-03 18:07:55.342', NULL, NULL, 'FRA', 'ChargeTemplate_2041', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2046, 0, false, '2013-05-03 18:07:55.344', NULL, NULL, 'ENG', 'ChargeTemplate_2041', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2049, 0, false, '2013-05-03 18:08:45.908', NULL, NULL, 'TUR', 'ChargeTemplate_2048', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2050, 0, false, '2013-05-03 18:08:45.91', NULL, '2', 'ARA', 'ChargeTemplate_2048', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2051, 0, false, '2013-05-03 18:08:45.911', NULL, NULL, 'DEU', 'ChargeTemplate_2048', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2052, 0, false, '2013-05-03 18:08:45.912', NULL, NULL, 'FRA', 'ChargeTemplate_2048', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2053, 0, false, '2013-05-03 18:08:45.914', NULL, NULL, 'ENG', 'ChargeTemplate_2048', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2055, 0, false, '2013-05-03 18:09:03.328', NULL, NULL, 'TUR', 'ChargeTemplate_2054', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2056, 0, false, '2013-05-03 18:09:03.329', NULL, '2', 'ARA', 'ChargeTemplate_2054', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2057, 0, false, '2013-05-03 18:09:03.33', NULL, NULL, 'DEU', 'ChargeTemplate_2054', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2058, 0, false, '2013-05-03 18:09:03.333', NULL, NULL, 'FRA', 'ChargeTemplate_2054', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2059, 0, false, '2013-05-03 18:09:03.334', NULL, NULL, 'ENG', 'ChargeTemplate_2054', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2149, 0, false, '2013-05-06 16:19:37.677', NULL, NULL, 'TUR', 'Tax_2148', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2150, 0, false, '2013-05-06 16:19:37.679', NULL, '2', 'ARA', 'Tax_2148', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2151, 0, false, '2013-05-06 16:19:37.681', NULL, NULL, 'DEU', 'Tax_2148', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2152, 0, false, '2013-05-06 16:19:37.683', NULL, NULL, 'FRA', 'Tax_2148', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2153, 0, false, '2013-05-06 16:19:37.685', NULL, NULL, 'ENG', 'Tax_2148', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2155, 0, false, '2013-05-06 16:19:40.957', NULL, NULL, 'TUR', 'Tax_2154', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2156, 0, false, '2013-05-06 16:19:40.958', NULL, '2', 'ARA', 'Tax_2154', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2157, 0, false, '2013-05-06 16:19:40.96', NULL, NULL, 'DEU', 'Tax_2154', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2158, 0, false, '2013-05-06 16:19:40.961', NULL, NULL, 'FRA', 'Tax_2154', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2159, 0, false, '2013-05-06 16:19:40.962', NULL, NULL, 'ENG', 'Tax_2154', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2161, 0, false, '2013-05-06 16:20:07.079', NULL, NULL, 'TUR', 'Tax_2160', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2162, 0, false, '2013-05-06 16:20:07.081', NULL, '2', 'ARA', 'Tax_2160', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2163, 0, false, '2013-05-06 16:20:07.082', NULL, NULL, 'DEU', 'Tax_2160', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2164, 0, false, '2013-05-06 16:20:07.083', NULL, NULL, 'FRA', 'Tax_2160', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2165, 0, false, '2013-05-06 16:20:07.085', NULL, NULL, 'ENG', 'Tax_2160', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2167, 0, false, '2013-05-06 16:20:58.721', NULL, NULL, 'TUR', 'Tax_2166', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2168, 0, false, '2013-05-06 16:20:58.723', NULL, '2', 'ARA', 'Tax_2166', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2169, 0, false, '2013-05-06 16:20:58.725', NULL, NULL, 'DEU', 'Tax_2166', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2170, 0, false, '2013-05-06 16:20:58.726', NULL, NULL, 'FRA', 'Tax_2166', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2171, 0, false, '2013-05-06 16:20:58.728', NULL, NULL, 'ENG', 'Tax_2166', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2173, 0, false, '2013-05-06 16:22:39.359', NULL, NULL, 'TUR', 'InvoiceCategory_2172', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2174, 0, false, '2013-05-06 16:22:39.361', NULL, '2', 'ARA', 'InvoiceCategory_2172', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2175, 0, false, '2013-05-06 16:22:39.363', NULL, NULL, 'DEU', 'InvoiceCategory_2172', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2176, 0, false, '2013-05-06 16:22:39.365', NULL, NULL, 'FRA', 'InvoiceCategory_2172', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2177, 0, false, '2013-05-06 16:22:39.367', NULL, NULL, 'ENG', 'InvoiceCategory_2172', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2179, 0, false, '2013-05-06 16:26:57.077', NULL, NULL, 'TUR', 'Tax_2178', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2180, 0, false, '2013-05-06 16:26:57.078', NULL, '2', 'ARA', 'Tax_2178', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2181, 0, false, '2013-05-06 16:26:57.079', NULL, NULL, 'DEU', 'Tax_2178', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2182, 0, false, '2013-05-06 16:26:57.08', NULL, NULL, 'FRA', 'Tax_2178', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2183, 0, false, '2013-05-06 16:26:57.081', NULL, NULL, 'ENG', 'Tax_2178', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2185, 0, false, '2013-05-06 16:28:57.94', NULL, NULL, 'TUR', 'InvoiceCategory_2184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2186, 0, false, '2013-05-06 16:28:57.941', NULL, '2', 'ARA', 'InvoiceCategory_2184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2187, 0, false, '2013-05-06 16:28:57.942', NULL, NULL, 'DEU', 'InvoiceCategory_2184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2188, 0, false, '2013-05-06 16:28:57.944', NULL, NULL, 'FRA', 'InvoiceCategory_2184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2189, 0, false, '2013-05-06 16:28:57.945', NULL, NULL, 'ENG', 'InvoiceCategory_2184', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2191, 0, false, '2013-05-06 16:34:56.316', NULL, NULL, 'TUR', 'InvoiceCategory_2190', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2192, 0, false, '2013-05-06 16:34:56.318', NULL, '2', 'ARA', 'InvoiceCategory_2190', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2193, 0, false, '2013-05-06 16:34:56.32', NULL, NULL, 'DEU', 'InvoiceCategory_2190', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2194, 0, false, '2013-05-06 16:34:56.321', NULL, NULL, 'FRA', 'InvoiceCategory_2190', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2195, 0, false, '2013-05-06 16:34:56.323', NULL, NULL, 'ENG', 'InvoiceCategory_2190', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2216, 0, false, '2013-05-06 17:08:19.465', NULL, NULL, 'TUR', 'ChargeTemplate_2215', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2217, 0, false, '2013-05-06 17:08:19.466', NULL, '2', 'ARA', 'ChargeTemplate_2215', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2218, 0, false, '2013-05-06 17:08:19.468', NULL, NULL, 'DEU', 'ChargeTemplate_2215', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2219, 0, false, '2013-05-06 17:08:19.469', NULL, NULL, 'FRA', 'ChargeTemplate_2215', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2220, 0, false, '2013-05-06 17:08:19.47', NULL, NULL, 'ENG', 'ChargeTemplate_2215', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2222, 0, false, '2013-05-06 17:10:25.619', NULL, NULL, 'TUR', 'ChargeTemplate_2221', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2223, 0, false, '2013-05-06 17:10:25.62', NULL, '2', 'ARA', 'ChargeTemplate_2221', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2224, 0, false, '2013-05-06 17:10:25.621', NULL, NULL, 'DEU', 'ChargeTemplate_2221', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2225, 0, false, '2013-05-06 17:10:25.622', NULL, NULL, 'FRA', 'ChargeTemplate_2221', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2226, 0, false, '2013-05-06 17:10:25.623', NULL, NULL, 'ENG', 'ChargeTemplate_2221', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2229, 0, false, '2013-05-06 17:12:33.594', NULL, NULL, 'TUR', 'ChargeTemplate_2228', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2230, 0, false, '2013-05-06 17:12:33.597', NULL, '2', 'ARA', 'ChargeTemplate_2228', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2231, 0, false, '2013-05-06 17:12:33.598', NULL, NULL, 'DEU', 'ChargeTemplate_2228', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2232, 0, false, '2013-05-06 17:12:33.6', NULL, NULL, 'FRA', 'ChargeTemplate_2228', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2233, 0, false, '2013-05-06 17:12:33.602', NULL, NULL, 'ENG', 'ChargeTemplate_2228', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2236, 0, false, '2013-05-06 17:14:15.676', NULL, NULL, 'TUR', 'ChargeTemplate_2235', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2237, 0, false, '2013-05-06 17:14:15.677', NULL, '2', 'ARA', 'ChargeTemplate_2235', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2238, 0, false, '2013-05-06 17:14:15.679', NULL, NULL, 'DEU', 'ChargeTemplate_2235', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2239, 0, false, '2013-05-06 17:14:15.68', NULL, NULL, 'FRA', 'ChargeTemplate_2235', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2240, 0, false, '2013-05-06 17:14:15.681', NULL, NULL, 'ENG', 'ChargeTemplate_2235', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2244, 0, false, '2013-05-06 17:15:20.973', NULL, NULL, 'TUR', 'ChargeTemplate_2243', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2245, 0, false, '2013-05-06 17:15:20.974', NULL, '2', 'ARA', 'ChargeTemplate_2243', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2246, 0, false, '2013-05-06 17:15:20.975', NULL, NULL, 'DEU', 'ChargeTemplate_2243', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2247, 0, false, '2013-05-06 17:15:20.977', NULL, NULL, 'FRA', 'ChargeTemplate_2243', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2248, 0, false, '2013-05-06 17:15:20.978', NULL, NULL, 'ENG', 'ChargeTemplate_2243', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2251, 0, false, '2013-05-06 17:17:44.588', NULL, NULL, 'TUR', 'ChargeTemplate_2250', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2252, 0, false, '2013-05-06 17:17:44.589', NULL, '2', 'ARA', 'ChargeTemplate_2250', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2253, 0, false, '2013-05-06 17:17:44.59', NULL, NULL, 'DEU', 'ChargeTemplate_2250', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2254, 0, false, '2013-05-06 17:17:44.591', NULL, NULL, 'FRA', 'ChargeTemplate_2250', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2255, 0, false, '2013-05-06 17:17:44.592', NULL, NULL, 'ENG', 'ChargeTemplate_2250', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2280, 0, false, '2013-05-06 18:22:18.332', NULL, NULL, 'TUR', 'ChargeTemplate_2279', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2281, 0, false, '2013-05-06 18:22:18.334', NULL, '2', 'ARA', 'ChargeTemplate_2279', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2282, 0, false, '2013-05-06 18:22:18.335', NULL, NULL, 'DEU', 'ChargeTemplate_2279', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2283, 0, false, '2013-05-06 18:22:18.337', NULL, NULL, 'FRA', 'ChargeTemplate_2279', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2284, 0, false, '2013-05-06 18:22:18.338', NULL, NULL, 'ENG', 'ChargeTemplate_2279', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2286, 0, false, '2013-05-06 18:28:16.561', NULL, NULL, 'TUR', 'ChargeTemplate_2285', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2287, 0, false, '2013-05-06 18:28:16.563', NULL, '2', 'ARA', 'ChargeTemplate_2285', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2288, 0, false, '2013-05-06 18:28:16.565', NULL, NULL, 'DEU', 'ChargeTemplate_2285', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2289, 0, false, '2013-05-06 18:28:16.567', NULL, NULL, 'FRA', 'ChargeTemplate_2285', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2290, 0, false, '2013-05-06 18:28:16.569', NULL, NULL, 'ENG', 'ChargeTemplate_2285', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2292, 0, false, '2013-05-06 18:30:51.161', NULL, NULL, 'TUR', 'ChargeTemplate_2291', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2293, 0, false, '2013-05-06 18:30:51.162', NULL, '2', 'ARA', 'ChargeTemplate_2291', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2294, 0, false, '2013-05-06 18:30:51.164', NULL, NULL, 'DEU', 'ChargeTemplate_2291', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2295, 0, false, '2013-05-06 18:30:51.165', NULL, NULL, 'FRA', 'ChargeTemplate_2291', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2296, 0, false, '2013-05-06 18:30:51.166', NULL, NULL, 'ENG', 'ChargeTemplate_2291', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2298, 0, false, '2013-05-06 18:32:31.76', NULL, NULL, 'TUR', 'ChargeTemplate_2297', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2299, 0, false, '2013-05-06 18:32:31.761', NULL, '2', 'ARA', 'ChargeTemplate_2297', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2300, 0, false, '2013-05-06 18:32:31.763', NULL, NULL, 'DEU', 'ChargeTemplate_2297', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2301, 0, false, '2013-05-06 18:32:31.764', NULL, NULL, 'FRA', 'ChargeTemplate_2297', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2302, 0, false, '2013-05-06 18:32:31.766', NULL, NULL, 'ENG', 'ChargeTemplate_2297', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2304, 0, false, '2013-05-06 18:35:15.251', NULL, NULL, 'TUR', 'ChargeTemplate_2303', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2305, 0, false, '2013-05-06 18:35:15.253', NULL, '2', 'ARA', 'ChargeTemplate_2303', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2306, 0, false, '2013-05-06 18:35:15.254', NULL, NULL, 'DEU', 'ChargeTemplate_2303', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2307, 0, false, '2013-05-06 18:35:15.256', NULL, NULL, 'FRA', 'ChargeTemplate_2303', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2308, 0, false, '2013-05-06 18:35:15.258', NULL, NULL, 'ENG', 'ChargeTemplate_2303', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2321, 0, false, '2013-05-07 20:34:19.461', NULL, NULL, 'TUR', 'ChargeTemplate_2320', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2322, 0, false, '2013-05-07 20:34:19.462', NULL, '2', 'ARA', 'ChargeTemplate_2320', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2323, 0, false, '2013-05-07 20:34:19.464', NULL, NULL, 'DEU', 'ChargeTemplate_2320', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2324, 0, false, '2013-05-07 20:34:19.466', NULL, NULL, 'FRA', 'ChargeTemplate_2320', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2325, 0, false, '2013-05-07 20:34:19.467', NULL, NULL, 'ENG', 'ChargeTemplate_2320', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2327, 0, false, '2013-05-07 20:42:45.761', NULL, NULL, 'TUR', 'ChargeTemplate_2326', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2328, 0, false, '2013-05-07 20:42:45.762', NULL, '2', 'ARA', 'ChargeTemplate_2326', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2329, 0, false, '2013-05-07 20:42:45.763', NULL, NULL, 'DEU', 'ChargeTemplate_2326', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2330, 0, false, '2013-05-07 20:42:45.764', NULL, NULL, 'FRA', 'ChargeTemplate_2326', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2331, 0, false, '2013-05-07 20:42:45.766', NULL, NULL, 'ENG', 'ChargeTemplate_2326', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2333, 0, false, '2013-05-07 20:42:57.023', NULL, NULL, 'TUR', 'ChargeTemplate_2332', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2334, 0, false, '2013-05-07 20:42:57.024', NULL, '2', 'ARA', 'ChargeTemplate_2332', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2335, 0, false, '2013-05-07 20:42:57.025', NULL, NULL, 'DEU', 'ChargeTemplate_2332', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2336, 0, false, '2013-05-07 20:42:57.026', NULL, NULL, 'FRA', 'ChargeTemplate_2332', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2337, 0, false, '2013-05-07 20:42:57.027', NULL, NULL, 'ENG', 'ChargeTemplate_2332', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2339, 0, false, '2013-05-07 20:44:30.213', NULL, NULL, 'TUR', 'ChargeTemplate_2338', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2340, 0, false, '2013-05-07 20:44:30.214', NULL, '2', 'ARA', 'ChargeTemplate_2338', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2341, 0, false, '2013-05-07 20:44:30.214', NULL, NULL, 'DEU', 'ChargeTemplate_2338', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2342, 0, false, '2013-05-07 20:44:30.215', NULL, NULL, 'FRA', 'ChargeTemplate_2338', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2343, 0, false, '2013-05-07 20:44:30.217', NULL, NULL, 'ENG', 'ChargeTemplate_2338', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2345, 0, false, '2013-05-07 20:45:29.107', NULL, NULL, 'TUR', 'ChargeTemplate_2344', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2346, 0, false, '2013-05-07 20:45:29.108', NULL, '2', 'ARA', 'ChargeTemplate_2344', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2347, 0, false, '2013-05-07 20:45:29.109', NULL, NULL, 'DEU', 'ChargeTemplate_2344', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2348, 0, false, '2013-05-07 20:45:29.109', NULL, NULL, 'FRA', 'ChargeTemplate_2344', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2349, 0, false, '2013-05-07 20:45:29.11', NULL, NULL, 'ENG', 'ChargeTemplate_2344', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2351, 0, false, '2013-05-07 20:45:50.691', NULL, NULL, 'TUR', 'ChargeTemplate_2350', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2352, 0, false, '2013-05-07 20:45:50.693', NULL, '2', 'ARA', 'ChargeTemplate_2350', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2353, 0, false, '2013-05-07 20:45:50.694', NULL, NULL, 'DEU', 'ChargeTemplate_2350', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2354, 0, false, '2013-05-07 20:45:50.695', NULL, NULL, 'FRA', 'ChargeTemplate_2350', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2355, 0, false, '2013-05-07 20:45:50.696', NULL, NULL, 'ENG', 'ChargeTemplate_2350', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2357, 0, false, '2013-05-07 20:49:09.943', NULL, NULL, 'TUR', 'ChargeTemplate_2356', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2358, 0, false, '2013-05-07 20:49:09.945', NULL, '2', 'ARA', 'ChargeTemplate_2356', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2359, 0, false, '2013-05-07 20:49:09.947', NULL, NULL, 'DEU', 'ChargeTemplate_2356', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2360, 0, false, '2013-05-07 20:49:09.948', NULL, NULL, 'FRA', 'ChargeTemplate_2356', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2361, 0, false, '2013-05-07 20:49:09.95', NULL, NULL, 'ENG', 'ChargeTemplate_2356', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2363, 0, false, '2013-05-07 20:52:06.182', NULL, NULL, 'TUR', 'ChargeTemplate_2362', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2364, 0, false, '2013-05-07 20:52:06.184', NULL, '2', 'ARA', 'ChargeTemplate_2362', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2365, 0, false, '2013-05-07 20:52:06.185', NULL, NULL, 'DEU', 'ChargeTemplate_2362', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2366, 0, false, '2013-05-07 20:52:06.186', NULL, NULL, 'FRA', 'ChargeTemplate_2362', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2367, 0, false, '2013-05-07 20:52:06.188', NULL, NULL, 'ENG', 'ChargeTemplate_2362', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2369, 0, false, '2013-05-07 20:53:20.849', NULL, NULL, 'TUR', 'ChargeTemplate_2368', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2370, 0, false, '2013-05-07 20:53:20.85', NULL, '2', 'ARA', 'ChargeTemplate_2368', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2371, 0, false, '2013-05-07 20:53:20.852', NULL, NULL, 'DEU', 'ChargeTemplate_2368', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2372, 0, false, '2013-05-07 20:53:20.853', NULL, NULL, 'FRA', 'ChargeTemplate_2368', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2373, 0, false, '2013-05-07 20:53:20.854', NULL, NULL, 'ENG', 'ChargeTemplate_2368', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2375, 0, false, '2013-05-07 20:54:09.059', NULL, NULL, 'TUR', 'ChargeTemplate_2374', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2376, 0, false, '2013-05-07 20:54:09.061', NULL, '2', 'ARA', 'ChargeTemplate_2374', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2377, 0, false, '2013-05-07 20:54:09.062', NULL, NULL, 'DEU', 'ChargeTemplate_2374', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2378, 0, false, '2013-05-07 20:54:09.063', NULL, NULL, 'FRA', 'ChargeTemplate_2374', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2379, 0, false, '2013-05-07 20:54:09.065', NULL, NULL, 'ENG', 'ChargeTemplate_2374', 1, 1, NULL);
INSERT INTO adm_messages VALUES (251, 3, false, '2013-04-17 15:32:43.513', '2013-05-09 22:48:38.942', 'Consommation (Arabe)', 'ARA', 'InvoiceCategory_249', 1, 1, 1);
INSERT INTO adm_messages VALUES (250, 3, false, '2013-04-17 15:32:43.51', '2013-05-09 22:48:38.982', 'Consommation (Turque)', 'TUR', 'InvoiceCategory_249', 1, 1, 1);
INSERT INTO adm_messages VALUES (252, 3, false, '2013-04-17 15:32:43.515', '2013-05-09 22:48:38.999', 'Consommation (Français)', 'FRA', 'InvoiceCategory_249', 1, 1, 1);
INSERT INTO adm_messages VALUES (2420, 0, false, '2013-05-13 13:36:08.717', NULL, NULL, 'TUR', 'ChargeTemplate_2419', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2421, 0, false, '2013-05-13 13:36:08.72', NULL, NULL, 'ARA', 'ChargeTemplate_2419', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2422, 0, false, '2013-05-13 13:36:08.728', NULL, NULL, 'DEU', 'ChargeTemplate_2419', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2423, 0, false, '2013-05-13 13:36:08.73', NULL, 'test', 'FRA', 'ChargeTemplate_2419', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2424, 0, false, '2013-05-13 13:36:08.736', NULL, NULL, 'ENG', 'ChargeTemplate_2419', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2426, 0, false, '2013-05-13 13:37:21.651', NULL, NULL, 'TUR', 'ChargeTemplate_2425', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2427, 0, false, '2013-05-13 13:37:21.653', NULL, 'test', 'ARA', 'ChargeTemplate_2425', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2428, 0, false, '2013-05-13 13:37:21.655', NULL, NULL, 'DEU', 'ChargeTemplate_2425', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2429, 0, false, '2013-05-13 13:37:21.657', NULL, NULL, 'FRA', 'ChargeTemplate_2425', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2430, 0, false, '2013-05-13 13:37:21.658', NULL, NULL, 'ENG', 'ChargeTemplate_2425', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2432, 0, false, '2013-05-13 13:37:49.896', NULL, NULL, 'TUR', 'ChargeTemplate_2431', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2433, 0, false, '2013-05-13 13:37:49.898', NULL, 'test', 'ARA', 'ChargeTemplate_2431', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2434, 0, false, '2013-05-13 13:37:49.9', NULL, NULL, 'DEU', 'ChargeTemplate_2431', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2435, 0, false, '2013-05-13 13:37:49.905', NULL, NULL, 'FRA', 'ChargeTemplate_2431', 1, 1, NULL);
INSERT INTO adm_messages VALUES (2436, 0, false, '2013-05-13 13:37:49.907', NULL, NULL, 'ENG', 'ChargeTemplate_2431', 1, 1, NULL);
INSERT INTO adm_messages VALUES (361, 3, false, '2013-04-17 17:41:03.565', '2013-05-16 19:41:34.447', 'Souscription Data (Turque)', 'TUR', 'InvoiceSubCategory_360', 1, 1, 1);
INSERT INTO adm_messages VALUES (362, 3, false, '2013-04-17 17:41:03.566', '2013-05-16 19:41:34.48', 'Souscription Data (Arabe)', 'ARA', 'InvoiceSubCategory_360', 1, 1, 1);
INSERT INTO adm_messages VALUES (10001, 0, false, '2013-05-16 19:41:34.524', NULL, NULL, 'EST', 'InvoiceSubCategory_360', 1, 1, NULL);
INSERT INTO adm_messages VALUES (10002, 0, false, '2013-05-16 19:41:51.822', NULL, NULL, 'EST', 'InvoiceSubCategory_306', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1296, 2, false, '2013-04-25 20:08:05.801', '2013-05-16 19:41:51.857', NULL, 'ENG', 'InvoiceSubCategory_306', 1, 1, 1);
INSERT INTO adm_messages VALUES (10003, 0, false, '2013-05-16 19:42:08.915', NULL, NULL, 'EST', 'InvoiceSubCategory_302', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1286, 2, false, '2013-04-25 20:06:09.373', '2013-05-16 19:42:08.938', NULL, 'ENG', 'InvoiceSubCategory_302', 1, 1, 1);
INSERT INTO adm_messages VALUES (300, 4, false, '2013-04-17 15:59:10.002', '2013-05-16 19:42:22.839', 'Souscription de serveur (Arabe)', 'ARA', 'InvoiceSubCategory_298', 1, 1, 1);
INSERT INTO adm_messages VALUES (299, 4, false, '2013-04-17 15:59:10', '2013-05-16 19:42:22.851', 'Souscription de  serveur (Turque)', 'TUR', 'InvoiceSubCategory_298', 1, 1, 1);
INSERT INTO adm_messages VALUES (1305, 3, false, '2013-04-25 20:09:59.88', '2013-05-16 19:42:22.86', 'Souscription de  serveur (Allemand)', 'DEU', 'InvoiceSubCategory_298', 1, 1, 1);
INSERT INTO adm_messages VALUES (10004, 0, false, '2013-05-16 19:42:22.87', NULL, NULL, 'EST', 'InvoiceSubCategory_298', 1, 1, NULL);
INSERT INTO adm_messages VALUES (301, 4, false, '2013-04-17 15:59:10.004', '2013-05-16 19:42:22.88', 'Souscription de serveur (Français)', 'FRA', 'InvoiceSubCategory_298', 1, 1, 1);
INSERT INTO adm_messages VALUES (1306, 3, false, '2013-04-25 20:09:59.89', '2013-05-16 19:42:22.889', NULL, 'CES', 'InvoiceSubCategory_298', 1, 1, 1);
INSERT INTO adm_messages VALUES (1307, 3, false, '2013-04-25 20:09:59.908', '2013-05-16 19:42:22.899', NULL, 'ENG', 'InvoiceSubCategory_298', 1, 1, 1);
INSERT INTO adm_messages VALUES (311, 4, false, '2013-04-17 16:04:43.499', '2013-05-16 19:54:07.812', 'veri tüketimi', 'TUR', 'InvoiceSubCategory_310', 1, 1, 1);
INSERT INTO adm_messages VALUES (312, 4, false, '2013-04-17 16:04:43.501', '2013-05-16 19:54:07.822', 'استهلاك البيانات', 'ARA', 'InvoiceSubCategory_310', 1, 1, 1);
INSERT INTO adm_messages VALUES (1198, 3, false, '2013-04-25 14:22:25.871', '2013-05-16 19:54:07.834', 'Daten Verbrauch', 'DEU', 'InvoiceSubCategory_310', 1, 1, 1);
INSERT INTO adm_messages VALUES (1283, 2, false, '2013-04-25 20:04:17.098', '2013-05-16 19:54:07.844', NULL, 'CES', 'InvoiceSubCategory_310', 1, 1, 1);
INSERT INTO adm_messages VALUES (313, 4, false, '2013-04-17 16:04:43.503', '2013-05-16 19:54:07.855', 'Consommation Data', 'FRA', 'InvoiceSubCategory_310', 1, 1, 1);
INSERT INTO adm_messages VALUES (10000, 1, false, '2013-05-16 19:40:50.713', '2013-05-16 19:54:07.866', NULL, 'EST', 'InvoiceSubCategory_310', 1, 1, 1);
INSERT INTO adm_messages VALUES (1199, 3, false, '2013-04-25 14:22:25.886', '2013-05-16 19:54:07.876', 'Data Consumption', 'ENG', 'InvoiceSubCategory_310', 1, 1, 1);
INSERT INTO adm_messages VALUES (10006, 0, false, '2013-05-16 19:55:57.275', NULL, 'rrr', 'TUR', 'InvoiceSubCategory_10005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (10007, 0, false, '2013-05-16 19:55:57.277', NULL, 'rrr', 'ARA', 'InvoiceSubCategory_10005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (10008, 0, false, '2013-05-16 19:55:57.278', NULL, 'rr', 'DEU', 'InvoiceSubCategory_10005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (10009, 0, false, '2013-05-16 19:55:57.28', NULL, 'rr', 'FRA', 'InvoiceSubCategory_10005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (10010, 0, false, '2013-05-16 19:55:57.282', NULL, 'r', 'ENG', 'InvoiceSubCategory_10005', 1, 1, NULL);
INSERT INTO adm_messages VALUES (1413, 7, false, '2013-04-30 18:52:31.551', '2013-05-21 16:13:58.194', NULL, 'ENG', 'ChargeTemplate_1182', 1, 1, 1);
INSERT INTO adm_messages VALUES (1321, 7, false, '2013-04-26 11:39:02.341', '2013-05-23 11:53:00.342', NULL, 'FRA', 'ChargeTemplate_1183', 1, 1, 1);
INSERT INTO adm_messages VALUES (1322, 7, false, '2013-04-26 11:39:02.351', '2013-05-23 11:53:00.361', NULL, 'ENG', 'ChargeTemplate_1183', 1, 1, 1);


--
-- Name: adm_messages_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_messages_seq', 20000, false);


--
-- Data for Name: adm_permission; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_permission VALUES (1, 'administrationVisualization', 'administrationVisualization', 'administration');
INSERT INTO adm_permission VALUES (2, 'administrationManagement', 'administrationManagement', 'administration');
INSERT INTO adm_permission VALUES (3, 'catalogVisualization', 'catalogVisualization', 'catalog');
INSERT INTO adm_permission VALUES (4, 'catalogManagement', 'catalogManagement', 'catalog');
INSERT INTO adm_permission VALUES (5, 'accountVisualization', 'accountVisualization', 'account');
INSERT INTO adm_permission VALUES (6, 'accountManagement', 'accountManagement', 'account');
INSERT INTO adm_permission VALUES (7, 'reportingVisualization', 'reportingVisualization', 'reporting');
INSERT INTO adm_permission VALUES (8, 'reportingManagement', 'reportingManagement', 'reporting');
INSERT INTO adm_permission VALUES (9, 'customerSummaryVisualization', 'customerSummaryVisualization', 'customerSummary');
INSERT INTO adm_permission VALUES (10, 'adv', 'billingVisualization', 'billing');
INSERT INTO adm_permission VALUES (11, 'adv', 'billingManagement', 'billing');


--
-- Name: adm_permission_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_permission_seq', 20000, false);


--
-- Data for Name: adm_role; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_role VALUES (1, NULL, 'Administrateur', 'administrateur', NULL);
INSERT INTO adm_role VALUES (6, NULL, 'Super Administrateur', 'superAdministrateur', NULL);


--
-- Data for Name: adm_role_permission; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_role_permission VALUES (1, 1);
INSERT INTO adm_role_permission VALUES (1, 2);
INSERT INTO adm_role_permission VALUES (1, 3);
INSERT INTO adm_role_permission VALUES (1, 4);
INSERT INTO adm_role_permission VALUES (1, 5);
INSERT INTO adm_role_permission VALUES (1, 6);
INSERT INTO adm_role_permission VALUES (1, 7);
INSERT INTO adm_role_permission VALUES (1, 8);
INSERT INTO adm_role_permission VALUES (1, 9);
INSERT INTO adm_role_permission VALUES (1, 10);
INSERT INTO adm_role_permission VALUES (1, 11);


--
-- Name: adm_role_permission_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_role_permission_seq', 6, false);


--
-- Name: adm_role_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_role_seq', 20000, false);


--
-- Name: adm_title_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_title_seq', 20000, false);


--
-- Data for Name: adm_user_provider; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_user_provider VALUES (1, 6);
INSERT INTO adm_user_provider VALUES (1, 1);
INSERT INTO adm_user_provider VALUES (1, 2380);


--
-- Data for Name: adm_user_role; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_user_role VALUES (6, 1);
INSERT INTO adm_user_role VALUES (6, 6);
INSERT INTO adm_user_role VALUES (1, 1);
INSERT INTO adm_user_role VALUES (1, 6);
INSERT INTO adm_user_role VALUES (2380, 1);


--
-- Name: adm_user_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_user_seq', 20000, false);


--
-- Data for Name: adm_vertina_configuration; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: adm_vertina_configuration_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_vertina_configuration_seq', 20000, false);


--
-- Data for Name: billing_trading_country; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_trading_country VALUES (154, 0, false, '2013-04-12 12:20:09.074', NULL, 'United Arab emirats', 1, 1, NULL, 2);
INSERT INTO billing_trading_country VALUES (155, 0, false, '2013-04-12 12:27:34.972', NULL, 'Turkey', 1, 1, NULL, 211);
INSERT INTO billing_trading_country VALUES (156, 0, false, '2013-04-12 12:28:10.266', NULL, 'Germany', 1, 1, NULL, 53);
INSERT INTO billing_trading_country VALUES (157, 0, false, '2013-04-12 12:28:45.833', NULL, 'France', 1, 1, NULL, 70);
INSERT INTO billing_trading_country VALUES (158, 0, false, '2013-04-12 12:29:51.276', NULL, 'Saoudi Arabia', 1, 1, NULL, 181);
INSERT INTO billing_trading_country VALUES (159, 0, false, '2013-04-12 12:30:22.05', NULL, 'Qatar', 1, 1, NULL, 176);
INSERT INTO billing_trading_country VALUES (160, 0, false, '2013-04-12 12:30:48.308', NULL, 'Bahrain', 1, 1, NULL, 22);
INSERT INTO billing_trading_country VALUES (239, 0, false, '2013-04-17 15:01:46.92', NULL, 'Kuwait', 1, 1, NULL, 114);
INSERT INTO billing_trading_country VALUES (1105, 0, false, '2013-04-24 19:25:29.026', NULL, 'United States', 1, 1, NULL, 220);
INSERT INTO billing_trading_country VALUES (1505, 0, false, '2013-05-01 19:54:37.31', NULL, 'Andorra', 1, 1, NULL, 1);
INSERT INTO billing_trading_country VALUES (10179, 0, false, '2013-05-27 11:10:01.105', NULL, 'Netherlands Antilles', 1, 1, NULL, 8);


--
-- Data for Name: billing_trading_currency; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_trading_currency VALUES (146, 2, false, '2013-04-12 12:02:59.77', '2013-04-17 14:49:18.052', 0.266600000000, 'Riyal Saoudien', 1, 1, 1, 9);
INSERT INTO billing_trading_currency VALUES (149, 1, false, '2013-04-12 12:09:54.989', '2013-04-17 14:52:29.742', 2.652100000000, 'Bahrein Dinar', 1, 1, 1, 16);
INSERT INTO billing_trading_currency VALUES (237, 0, false, '2013-04-17 14:54:42.397', NULL, 0.558200000000, 'Livre turque', 1, 1, NULL, 158);
INSERT INTO billing_trading_currency VALUES (238, 0, false, '2013-04-17 14:56:22.139', NULL, 0.274600000000, 'Riyal du Qatar', 1, 1, NULL, 126);
INSERT INTO billing_trading_currency VALUES (1106, 1, false, '2013-04-24 19:28:05.741', '2013-04-24 19:28:52.388', 1.000000000000, 'Dollar des Etats-unis', 1, 1, 1, 49);
INSERT INTO billing_trading_currency VALUES (1110, 0, false, '2013-04-24 19:49:09.327', NULL, 0.285800000000, 'Dinar koweïtien', 1, 1, NULL, 85);
INSERT INTO billing_trading_currency VALUES (145, 6, false, '2013-04-12 12:01:16.438', '2013-05-03 10:26:16.325', 1.313600000000, 'Euro', 1, 1, 1, 5);
INSERT INTO billing_trading_currency VALUES (2138, 0, false, '2013-05-06 15:54:45.878', NULL, 0.000000000000, 'Peso', 1, 1, NULL, 10);
INSERT INTO billing_trading_currency VALUES (232, 4, false, '2013-04-16 16:12:50.03', '2013-05-15 09:38:39.891', 0.272305000000, 'Dirham des émirats unis', 1, 1, 1, 54);


--
-- Data for Name: billing_trading_language; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_trading_language VALUES (139, 1, false, '2013-04-12 11:19:50.57', '2013-04-12 11:22:07.895', 'Arabic ', 1, 1, 1, 3);
INSERT INTO billing_trading_language VALUES (138, 1, false, '2013-04-12 11:10:03.388', '2013-04-12 11:29:02.661', 'Turkish ', 1, 1, 1, 32);
INSERT INTO billing_trading_language VALUES (244, 0, false, '2013-04-17 15:27:34.349', NULL, 'German', 1, 1, NULL, 1);
INSERT INTO billing_trading_language VALUES (1078, 0, false, '2013-04-24 15:51:30.59', NULL, 'English', 1, 1, NULL, 2);
INSERT INTO billing_trading_language VALUES (141, 2, false, '2013-04-12 11:25:44.671', '2013-04-24 15:55:20.482', 'French', 1, 1, 1, 12);
INSERT INTO billing_trading_language VALUES (10173, 0, false, '2013-05-23 15:59:16.329', NULL, 'danois', 1, 1, NULL, 7);
INSERT INTO billing_trading_language VALUES (10177, 0, false, '2013-05-23 16:09:30.782', NULL, 'serbe', 1, 1, NULL, 27);
INSERT INTO billing_trading_language VALUES (10178, 0, false, '2013-05-27 11:08:48.622', NULL, 'catalan', 1, 1, NULL, 5);
INSERT INTO billing_trading_language VALUES (19950, 0, false, '2013-06-25 14:44:35.471', NULL, 'bulgare', 1, 1, NULL, 4);


--
-- Data for Name: crm_customer_brand; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO crm_customer_brand VALUES (317, 0, false, '2013-04-17 16:11:16.021', NULL, 'DEMO', 'DEMO Products and services', 1, 1, NULL);


--
-- Data for Name: crm_customer_category; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO crm_customer_category VALUES (315, 0, false, '2013-04-17 16:09:51.62', NULL, 'Business', 'Company customer type', 1, 1, NULL);
INSERT INTO crm_customer_category VALUES (316, 0, false, '2013-04-17 16:10:45.292', NULL, 'Residential', 'Individual customer type', 1, 1, NULL);


--
-- Data for Name: crm_seller; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO crm_seller VALUES (1109, 3, false, '2013-04-24 19:43:28.814', '2013-04-25 15:21:32.728', 'MAIN_SELLER', 'DEMO Distributor', '', 'Street', '', 'City', 'Allemagne', NULL, 'zipco', 1, 1, 1, NULL, 1105, 1106, 1078);
INSERT INTO crm_seller VALUES (1043, 11, false, '2013-04-23 20:34:10.437', '2013-04-25 17:45:18.811', 'DISTRIBUTOR_QATAR', 'Middle East distributor', '', 'Street', '', 'PARIS', 'France', NULL, '00000', 1, 1, 1, 1109, 159, 238, 139);
INSERT INTO crm_seller VALUES (1108, 3, false, '2013-04-24 19:32:19.341', '2013-04-24 19:44:16.572', 'DISTRIBUTOR_EU', 'European distribuor', '', 'Street', '', 'City', 'Allemagne', NULL, 'zipco', 1, 1, 1, 1109, 156, 145, 1078);
INSERT INTO crm_seller VALUES (1099, 0, false, '2013-04-24 19:08:20.627', NULL, 'SELLER_SA', 'Saoudi arabia seller', '', 'Street', '', 'CITY', 'Arabie Saoudite', NULL, 'Zip c', 1, 1, NULL, 1043, 158, 146, 139);
INSERT INTO crm_seller VALUES (1100, 0, false, '2013-04-24 19:10:05.316', NULL, 'SELLER_TR', 'Turkey Seller', '', 'Street', '', 'City', 'Australie', NULL, 'ZIPco', 1, 1, NULL, 1043, 155, 237, 138);
INSERT INTO crm_seller VALUES (1103, 3, false, '2013-04-24 19:21:25.725', '2013-04-24 19:22:43.908', 'SELLER_BH', 'Bahrein sellor', '', 'Street', '', 'City', 'Brunei', NULL, 'Zipco', 1, 1, 1, 1043, 160, 149, 139);
INSERT INTO crm_seller VALUES (1104, 0, false, '2013-04-24 19:24:27.126', NULL, 'SELLER_AE', 'United arab emirats seller', '', 'Street', '', 'City', 'Emirats Arabes Unis', NULL, 'zipco', 1, 1, NULL, 1043, 154, 232, 139);
INSERT INTO crm_seller VALUES (1101, 2, false, '2013-04-24 19:14:12.024', '2013-04-24 19:32:54.134', 'SELLER_DE', 'Germany seller', '', 'Street', '', 'City', 'Allemagne', NULL, 'zipco', 1, 1, 1, 1108, 156, 145, 244);
INSERT INTO crm_seller VALUES (1102, 2, false, '2013-04-24 19:15:23.48', '2013-04-24 19:33:09.343', 'SELLER_FR', 'french seller', '', 'Street', '', 'city', 'France', NULL, 'Zipco', 1, 1, 1, 1108, 157, 145, 141);


--
-- Name: ar_account_operation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_account_operation_seq', 20000, false);



--
-- Name: ar_action_dunning_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_action_dunning_seq', 20000, false);


--
-- Name: ar_action_plan_item_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_action_plan_item_seq', 20000, false);


--
-- Name: ar_bank_operation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_bank_operation_seq', 20000, false);


--
-- Name: ar_ddrequest_item_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_ddrequest_item_seq', 20000, false);


--
-- Name: ar_ddrequest_lot_op_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_ddrequest_lot_op_seq', 20000, false);


--
-- Name: ar_ddrequest_lot_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_ddrequest_lot_seq', 20000, false);


--
-- Name: ar_dunning_lot_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_dunning_lot_seq', 20000, false);


--
-- Name: ar_dunning_plan_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_dunning_plan_seq', 20000, false);


--
-- Name: ar_dunning_plan_transition_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_dunning_plan_transition_seq', 20000, false);




--
-- Name: ar_matching_amount_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_matching_amount_seq', 20000, false);


--
-- Name: ar_matching_code_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_matching_code_seq', 20000, false);


--
-- Data for Name: ar_occ_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO ar_occ_template VALUES (2, 1, false, '2013-05-22 16:52:39', NULL, 'REMB', '2000', '2000', 'Monay back', 'DEBIT', 1, 1, NULL);
INSERT INTO ar_occ_template VALUES (1, 1, false, '2013-05-22 16:48:27', NULL, 'PCHQ', '1001', '1001', 'Check payment', 'CREDIT', 1, 1, NULL);


--
-- Name: ar_occ_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_occ_template_seq', 20000, false);


--
-- Name: bi_job_history_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('bi_job_history_seq', 20003, true);


--
-- Name: bi_job_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('bi_job_seq', 20000, false);


--
-- Name: bi_report_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('bi_report_seq', 20000, false);





--
-- Data for Name: cat_calendar; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_calendar VALUES (318, 1, false, '2013-04-17 16:14:22.094', '2013-04-17 17:09:51.689', 'Monthly subscription calendar', 'CAL_SUB_MT', 'DURATION_TERM', 1, 1, 1);
INSERT INTO cat_calendar VALUES (1041, 0, false, '2013-04-23 20:31:30.257', NULL, 'Monthly invoicing calendar', 'CAL_INV_MT', 'BILLING', 1, 1, NULL);
INSERT INTO cat_calendar VALUES (1097, 0, false, '2013-04-24 17:23:50.248', NULL, 'Périodicité des compteurs', 'CAL_COUNTER', 'COUNTER', 1, 1, NULL);
INSERT INTO cat_calendar VALUES (1053, 1, false, '2013-04-23 23:11:26.363', '2013-04-30 19:59:11.478', 'Monthly valorisation calendar', 'CAL_VAL_MT', 'CHARGE_IMPUTATION', 1, 1, 1);



--
-- Data for Name: billing_cycle; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_cycle VALUES (1042, 0, false, '2013-04-23 20:32:13.081', NULL, 'CYC_INV_MT', 'Monthly invoice cycle', '1', 4, 2, 1, 1, NULL, 1041);


--
-- Data for Name: billing_subscrip_termin_reason; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_subscrip_termin_reason VALUES (1, 1, false, true, true, 'TERM_REASON_1', 'Résiliation de souscription', 1);
INSERT INTO billing_subscrip_termin_reason VALUES (19950, 0, true, false, false, 'TEST', 'TEST', 1);


--
-- Name: billing_billing_run_list_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_billing_run_list_seq', 20000, false);


--
-- Name: billing_billing_run_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_billing_run_seq', 20000, false);


--
-- Name: billing_charge_applic_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_charge_applic_seq', 10000, false);


--
-- Data for Name: billing_invoice_cat; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_invoice_cat VALUES (240, 14, false, '2013-04-17 15:26:59.825', '2013-04-30 18:31:52.124', 'SUBSCRIPTION', 'Subscription', NULL, 1, 1, 1);
INSERT INTO billing_invoice_cat VALUES (249, 4, false, '2013-04-17 15:32:43.503', '2013-05-09 22:48:39.007', 'CONSUMPTION', 'Consumption', NULL, 1, 1, 1);


--
-- Data for Name: billing_invoice_sub_cat; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_invoice_sub_cat VALUES (360, 4, false, '2013-04-17 17:41:03.561', '2013-05-16 19:41:34.564', 'SUB_DATA', 'Data Subscription', '1202.121.23.1', NULL, 1, 1, 1, 240);
INSERT INTO billing_invoice_sub_cat VALUES (306, 4, false, '2013-04-17 16:02:54.625', '2013-05-16 19:41:51.859', 'SUB_DOM', 'Domain subscription', '1202.121.23.2', NULL, 1, 1, 1, 240);
INSERT INTO billing_invoice_sub_cat VALUES (302, 4, false, '2013-04-17 16:00:47.068', '2013-05-16 19:42:08.939', 'SUB_SOFT', 'Software subscription', '1202.121.23.3', NULL, 1, 1, 1, 240);
INSERT INTO billing_invoice_sub_cat VALUES (298, 5, false, '2013-04-17 15:59:09.996', '2013-05-16 19:42:22.9', 'SUB_SRV', 'Server subscription', '1202.121.23.4', NULL, 1, 1, 1, 240);
INSERT INTO billing_invoice_sub_cat VALUES (310, 5, false, '2013-04-17 16:04:43.495', '2013-05-16 19:54:07.876', 'CMP_DATA', 'Data Consumption', '1202.121.21.2', NULL, 1, 1, 1, 249);


--
-- Data for Name: cat_charge_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_charge_template VALUES (330, 10, false, '2013-04-17 16:55:29.213', '2013-04-25 01:26:09.435', 'EXCH20102_SOFT_BLACK', 'Exchange 2010 - Blackberry subscription', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (376, 3, false, '2013-04-17 18:09:41.374', '2013-04-25 01:26:49.96', 'MO20131_SOFT', 'Microsoft Office 2013 Standard', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (380, 5, false, '2013-04-17 18:23:32.612', '2013-04-25 01:27:40.034', 'MPR2013_SOFT', 'Microsoft Project 2013', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (392, 5, false, '2013-04-17 18:27:28.164', '2013-04-25 01:28:10.384', 'MLYNC2010_SOFT', 'Microsoft Lync 2010', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (364, 7, false, '2013-04-17 17:46:34.02', '2013-04-25 01:29:08.255', 'EXCH20101_DATA_1G+', '1Go Additional Storage Option', false, 0, 1, 1, 1, 360);
INSERT INTO cat_charge_template VALUES (372, 5, false, '2013-04-17 18:08:38.84', '2013-04-25 01:29:32.986', 'MO20132_SOFT', 'Microsoft Office 2013 Professional', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (384, 5, false, '2013-04-17 18:25:07.04', '2013-04-25 01:30:14.146', 'MVI2013_SOFT', 'Microsoft Visio 2013', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (396, 5, false, '2013-04-17 18:28:56.867', '2013-04-25 01:30:38.611', 'MDYCRM2011_SOFT', 'Microsoft Dynamics CRM 2011', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (338, 12, false, '2013-04-17 16:59:37.528', '2013-04-25 01:11:54.609', 'EXCH20101_DATA_1G', '1Go  comp. Option', false, 0, 1, 1, 1, 360);
INSERT INTO cat_charge_template VALUES (334, 7, false, '2013-04-17 16:57:37.126', '2013-04-25 01:31:17.053', 'EXCH20101_SOFT', 'Exchange 2010 Business', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (346, 13, false, '2013-04-17 17:02:59.858', '2013-04-25 01:31:58.956', 'EXCH20102_DATA_3G+', '3Go Additional Storage Option', false, 0, 1, 1, 1, 360);
INSERT INTO cat_charge_template VALUES (368, 8, false, '2013-04-17 17:57:34.058', '2013-04-25 01:32:21.571', 'EXCH20102_DATA_2G', '2Go comp. Option', false, 0, 1, 1, 1, 360);
INSERT INTO cat_charge_template VALUES (1144, 0, false, '2013-04-25 01:43:14.828', NULL, 'CUST_MDYCRM2011_SOFT', 'Microsoft Dynamics CRM 2011 customer', false, NULL, 1, 1, NULL, 302);
INSERT INTO cat_charge_template VALUES (1150, 0, false, '2013-04-25 01:47:20.532', NULL, 'CUST_FASTVIEWER_1', 'Fastviewer 1 session', false, NULL, 1, 1, NULL, 302);
INSERT INTO cat_charge_template VALUES (1156, 0, false, '2013-04-25 01:50:12.213', NULL, 'CUST_FASTVIEWER_5', 'Fastviewer 5 sessions', false, NULL, 1, 1, NULL, 302);
INSERT INTO cat_charge_template VALUES (1162, 0, false, '2013-04-25 01:51:12.284', NULL, 'CUST_FASTVIEWER_10', 'Fastviewer 10 sessions', false, NULL, 1, 1, NULL, 302);
INSERT INTO cat_charge_template VALUES (342, 7, false, '2013-04-17 17:00:17.822', '2013-04-25 01:24:34.76', 'EXCH20102_SOFT', 'Exchange 2010 Premium', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (355, 5, false, '2013-04-17 17:30:03.298', '2013-04-25 01:25:57.835', 'MALIC_SOFT', 'Microsoft Access License', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (400, 7, false, '2013-04-17 18:34:33.246', '2013-04-25 02:00:18.883', 'MSOFT_DATA', 'Microsoft 1,5 GB Storage option', false, 0, 1, 1, 1, 360);
INSERT INTO cat_charge_template VALUES (1168, 0, false, '2013-04-25 02:08:07.848', NULL, 'CUST_MDYCRM2011_DATA', 'Microsoft Dynamics CRM 2011 - 1 GB include', false, NULL, 1, 1, NULL, 360);
INSERT INTO cat_charge_template VALUES (1138, 2, false, '2013-04-25 01:35:29.5', '2013-04-25 02:09:56.179', 'CUST_MLYNC2010_SOFT', 'Microsoft Lync 2010 customer', false, NULL, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (388, 6, false, '2013-04-17 18:26:14.521', '2013-04-25 02:11:11.629', 'CUST_MSH2010_SOFT', 'Microsoft Sharepoint 2010', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (1174, 0, false, '2013-04-25 02:14:03.057', NULL, 'CUST_MSH2010_DATA', 'Microsoft Sharepoint 2010 - 1 GB include', false, NULL, 1, 1, NULL, 360);
INSERT INTO cat_charge_template VALUES (1186, 0, false, '2013-04-25 04:21:48.83', NULL, 'CM_MSH2010_INC', 'Microsoft Sharepoint 2010 include file space', false, NULL, 1, 1, NULL, 310);
INSERT INTO cat_charge_template VALUES (1187, 0, false, '2013-04-25 04:23:08.948', NULL, 'CM_MSH2010_ADD', 'Microsoft Sharepoint 2010 additional file space', false, NULL, 1, 1, NULL, 310);
INSERT INTO cat_charge_template VALUES (1403, 0, false, '2013-04-30 18:48:51.26', NULL, 'TEST', 'TEST', false, NULL, 1, 1, NULL, 302);
INSERT INTO cat_charge_template VALUES (1184, 1, false, '2013-04-25 04:17:05.078', '2013-04-30 18:52:42.223', 'CM_MDYCRM2011_INC', 'Microsoft Dynamics CRM 2011 include file space', false, NULL, 1, 1, 1, 310);
INSERT INTO cat_charge_template VALUES (1058, 3, false, '2013-04-24 07:27:29.69', '2013-05-02 08:53:09.461', 'MO_SUB_Microsoft', 'One shot Subscription charge for Microsoft products', false, NULL, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (1182, 16, false, '2013-04-25 02:38:07.158', '2013-05-21 16:13:58.195', 'CM_MSOFT_INC', 'Microsoft file space include', false, NULL, 1, 1, 1, 310);
INSERT INTO cat_charge_template VALUES (1185, 5, false, '2013-04-25 04:18:48.241', '2013-05-23 11:52:32.152', 'CM_MDYCRM2011_ADD', 'Microsoft Dynamics CRM 2011 additional file space', false, NULL, 1, 1, 1, 310);
INSERT INTO cat_charge_template VALUES (1183, 13, false, '2013-04-25 04:10:52.822', '2013-05-23 11:53:00.362', 'CM_MSOFT_ADD', 'Microsoft additional file space', false, NULL, 1, 1, 1, 310);




--
-- Data for Name: cat_counter_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_counter_template VALUES (1180, 2, false, '2013-04-25 02:25:57.337', '2013-05-03 10:26:40.999', 'CT_MDYCRM2011', 'Microsoft Dynamics CRM 2011 counter', 'QUANTITY', 1024.000000000000, 'MByte', 1, 1, 1, 1097);
INSERT INTO cat_counter_template VALUES (10033, 1, false, '2013-05-17 09:18:36.546', '2013-05-17 09:23:12.15', 'CT_MSOFT', 'Microsoft Office FileSpace Storage', 'QUANTITY', 1000.000000000000, 'KBYTE', 1, 1, 1, 1097);

--
-- Name: billing_counter_instance_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_counter_instance_seq', 20050, true);



--
-- Name: billing_counter_period_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_counter_period_seq', 20000, false);


--
-- Name: billing_country_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_country_seq', 10000, false);


--
-- Name: billing_cycle_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_cycle_seq', 20000, false);


--
-- Name: billing_disc_inst_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_disc_inst_seq', 1, false);


--
-- Name: billing_discountplan_instanciation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_discountplan_instanciation_seq', 20000, false);



--
-- Data for Name: billing_tax; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_tax VALUES (282, 2, false, '2013-04-17 15:53:55.442', '2013-04-25 19:59:09.668', 'TAX_18', '18 Percent Tax', '', 18.000000000000, 1, 1, 1);
INSERT INTO billing_tax VALUES (266, 4, false, '2013-04-17 15:49:42.335', '2013-04-25 19:59:54.101', 'TAX_00', '0 Percent Tax', '', 0.000000000000, 1, 1, 1);
INSERT INTO billing_tax VALUES (286, 2, false, '2013-04-17 15:54:52.002', '2013-05-02 10:51:47.266', 'TAX_19.6', '19.6 Percent Tax', '', 19.600000000000, 1, 1, 1);
INSERT INTO billing_tax VALUES (1192, 4, false, '2013-04-25 14:20:47.443', '2013-05-02 10:51:59.122', 'TAX_19', '19 Percent Tax', '', 19.000000000000, 1, 1, 1);
INSERT INTO billing_tax VALUES (290, 6, false, '2013-04-17 15:56:07.799', '2013-05-03 10:27:04.594', 'TAX_05', '5 Percent Tax', '', 5.000000000000, 1, 1, 1);



--
-- Name: billing_invoic_sub_country_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoic_sub_country_seq', 20000, false);

--
-- Name: billing_invoice_agregate_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_agregate_seq', 20000, false);


--
-- Name: billing_invoice_cat_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_cat_seq', 20000, false);


--
-- Name: billing_invoice_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_seq', 20000, false);


--
-- Data for Name: billing_inv_sub_cat_country; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_inv_sub_cat_country VALUES (1200, 0, false, '2013-04-25 14:24:32.335', NULL, NULL, 1, 1, NULL, 310, 1192, 156);
INSERT INTO billing_inv_sub_cat_country VALUES (1201, 0, false, '2013-04-25 14:24:43.882', NULL, NULL, 1, 1, NULL, 310, 286, 157);
INSERT INTO billing_inv_sub_cat_country VALUES (1202, 0, false, '2013-04-25 14:25:50.28', NULL, NULL, 1, 1, NULL, 360, 1192, 156);
INSERT INTO billing_inv_sub_cat_country VALUES (1203, 0, false, '2013-04-25 14:25:59.622', NULL, NULL, 1, 1, NULL, 360, 286, 157);
INSERT INTO billing_inv_sub_cat_country VALUES (1268, 0, false, '2013-04-25 20:00:37.82', NULL, NULL, 1, 1, NULL, 360, 282, 155);
INSERT INTO billing_inv_sub_cat_country VALUES (1269, 0, false, '2013-04-25 20:01:11.196', NULL, NULL, 1, 1, NULL, 360, 290, 154);
INSERT INTO billing_inv_sub_cat_country VALUES (1270, 0, false, '2013-04-25 20:01:23.927', NULL, NULL, 1, 1, NULL, 360, 266, 160);
INSERT INTO billing_inv_sub_cat_country VALUES (1271, 0, false, '2013-04-25 20:01:40.973', NULL, NULL, 1, 1, NULL, 360, 266, 239);
INSERT INTO billing_inv_sub_cat_country VALUES (1272, 0, false, '2013-04-25 20:01:51.217', NULL, NULL, 1, 1, NULL, 360, 266, 159);
INSERT INTO billing_inv_sub_cat_country VALUES (1273, 0, false, '2013-04-25 20:02:04.25', NULL, NULL, 1, 1, NULL, 360, 266, 158);
INSERT INTO billing_inv_sub_cat_country VALUES (1277, 0, false, '2013-04-25 20:03:07.313', NULL, NULL, 1, 1, NULL, 310, 290, 154);
INSERT INTO billing_inv_sub_cat_country VALUES (1278, 0, false, '2013-04-25 20:03:24.485', NULL, NULL, 1, 1, NULL, 310, 266, 160);
INSERT INTO billing_inv_sub_cat_country VALUES (1279, 0, false, '2013-04-25 20:03:37.755', NULL, NULL, 1, 1, NULL, 310, 266, 239);
INSERT INTO billing_inv_sub_cat_country VALUES (1280, 0, false, '2013-04-25 20:03:48.94', NULL, NULL, 1, 1, NULL, 310, 266, 159);
INSERT INTO billing_inv_sub_cat_country VALUES (1281, 0, false, '2013-04-25 20:03:59.679', NULL, NULL, 1, 1, NULL, 310, 266, 158);
INSERT INTO billing_inv_sub_cat_country VALUES (1282, 0, false, '2013-04-25 20:04:09.877', NULL, NULL, 1, 1, NULL, 310, 282, 155);
INSERT INTO billing_inv_sub_cat_country VALUES (1287, 0, false, '2013-04-25 20:06:26.593', NULL, NULL, 1, 1, NULL, 302, 1192, 156);
INSERT INTO billing_inv_sub_cat_country VALUES (1288, 0, false, '2013-04-25 20:06:41.058', NULL, NULL, 1, 1, NULL, 302, 290, 154);
INSERT INTO billing_inv_sub_cat_country VALUES (1289, 0, false, '2013-04-25 20:06:49.754', NULL, NULL, 1, 1, NULL, 302, 266, 160);
INSERT INTO billing_inv_sub_cat_country VALUES (1290, 0, false, '2013-04-25 20:06:59.771', NULL, NULL, 1, 1, NULL, 302, 266, 239);
INSERT INTO billing_inv_sub_cat_country VALUES (1291, 0, false, '2013-04-25 20:07:08.887', NULL, NULL, 1, 1, NULL, 302, 266, 159);
INSERT INTO billing_inv_sub_cat_country VALUES (1292, 0, false, '2013-04-25 20:07:18.793', NULL, NULL, 1, 1, NULL, 302, 266, 158);
INSERT INTO billing_inv_sub_cat_country VALUES (1293, 0, false, '2013-04-25 20:07:29.955', NULL, NULL, 1, 1, NULL, 302, 282, 155);
INSERT INTO billing_inv_sub_cat_country VALUES (1297, 0, false, '2013-04-25 20:08:20.689', NULL, NULL, 1, 1, NULL, 306, 290, 154);
INSERT INTO billing_inv_sub_cat_country VALUES (1298, 0, false, '2013-04-25 20:08:29.535', NULL, NULL, 1, 1, NULL, 306, 266, 160);
INSERT INTO billing_inv_sub_cat_country VALUES (1299, 0, false, '2013-04-25 20:08:40.127', NULL, NULL, 1, 1, NULL, 306, 1192, 156);
INSERT INTO billing_inv_sub_cat_country VALUES (1300, 0, false, '2013-04-25 20:08:50.558', NULL, NULL, 1, 1, NULL, 306, 286, 157);
INSERT INTO billing_inv_sub_cat_country VALUES (1301, 0, false, '2013-04-25 20:09:03.35', NULL, NULL, 1, 1, NULL, 306, 266, 239);
INSERT INTO billing_inv_sub_cat_country VALUES (1302, 0, false, '2013-04-25 20:09:12.268', NULL, NULL, 1, 1, NULL, 306, 266, 159);
INSERT INTO billing_inv_sub_cat_country VALUES (1303, 0, false, '2013-04-25 20:09:20.902', NULL, NULL, 1, 1, NULL, 306, 266, 158);
INSERT INTO billing_inv_sub_cat_country VALUES (1304, 0, false, '2013-04-25 20:09:30.221', NULL, NULL, 1, 1, NULL, 306, 282, 155);
INSERT INTO billing_inv_sub_cat_country VALUES (314, 2, false, '2013-04-17 16:05:39.423', '2013-04-25 20:10:13.86', NULL, 1, 1, 1, 298, 1192, 156);
INSERT INTO billing_inv_sub_cat_country VALUES (1308, 0, false, '2013-04-25 20:10:23.958', NULL, NULL, 1, 1, NULL, 298, 290, 154);
INSERT INTO billing_inv_sub_cat_country VALUES (1309, 0, false, '2013-04-25 20:10:33.777', NULL, NULL, 1, 1, NULL, 298, 266, 160);
INSERT INTO billing_inv_sub_cat_country VALUES (1310, 0, false, '2013-04-25 20:10:43.946', NULL, NULL, 1, 1, NULL, 298, 286, 157);
INSERT INTO billing_inv_sub_cat_country VALUES (1311, 0, false, '2013-04-25 20:10:52.971', NULL, NULL, 1, 1, NULL, 298, 266, 239);
INSERT INTO billing_inv_sub_cat_country VALUES (1312, 0, false, '2013-04-25 20:11:01.366', NULL, NULL, 1, 1, NULL, 298, 266, 159);
INSERT INTO billing_inv_sub_cat_country VALUES (1313, 0, false, '2013-04-25 20:11:10.572', NULL, NULL, 1, 1, NULL, 298, 266, 158);
INSERT INTO billing_inv_sub_cat_country VALUES (1314, 0, false, '2013-04-25 20:11:20.421', NULL, NULL, 1, 1, NULL, 298, 282, 155);



--
-- Name: billing_invoice_sub_cat_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_sub_cat_seq', 20000, false);



--
-- Name: billing_invoice_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_template_seq', 20000, false);


--
-- Name: billing_invsubcat_country_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invsubcat_country_seq', 1, false);


--
-- Name: billing_language_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_language_seq', 10000, false);


--
-- Data for Name: cat_offer_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_offer_template VALUES (328, 1, false, '2013-04-17 16:43:03.925', '2013-04-17 18:06:36.368', 'FASTVIEWER', 'Fastviewer', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (320, 4, false, '2013-04-17 16:21:34.911', '2013-04-29 14:14:06.817', 'EXCH20102', 'Exchange 2010 Premium', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (327, 4, true, '2013-04-17 16:41:35.892', '2013-04-29 14:14:21.738', 'DYCRM2011', 'Microsoft Dynamics CRM 2011', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (325, 3, true, '2013-04-17 16:34:53.862', '2013-04-29 14:14:31.782', 'SH2010', 'Microsoft Sharepoint 2010', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (324, 3, true, '2013-04-17 16:33:09.26', '2013-04-29 14:14:52.154', 'PR20131', 'Microsoft Project 2013', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (326, 2, true, '2013-04-17 16:40:44.701', '2013-04-29 14:14:57.708', 'LYNC2010', 'Microsoft Lync 2010', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (329, 1, true, '2013-04-17 16:44:03.371', '2013-04-29 14:15:03.858', 'DOM_MAIL', 'Mail Domain', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (323, 3, true, '2013-04-17 16:32:17.864', '2013-04-29 14:15:10.196', 'VI20131', 'Microsoft Visio 2013', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (322, 3, true, '2013-04-17 16:23:15.217', '2013-04-29 14:15:16.43', 'MO20132', 'Microsoft Office 2013 Professional', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (319, 5, false, '2013-04-17 16:19:19.305', '2013-05-03 10:50:33.673', 'EXCH20101', 'Exchange 2010 Business', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (321, 4, false, '2013-04-17 16:22:36.363', '2013-05-17 09:29:28.901', 'MO20131', 'Microsoft Office 2013 Standard', 1, 1, 1);


--
-- Data for Name: cat_service_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_service_template VALUES (350, 1, false, '2013-04-17 17:11:14.498', '2013-04-17 18:36:21.313', 'EXCH20101', 'Exchange 2010 Business', 1, 1, 1, 318);
INSERT INTO cat_service_template VALUES (352, 1, false, '2013-04-17 17:12:16.943', '2013-04-17 18:36:44.572', 'EXCH20102', 'Exchange 2010 Premium', 1, 1, 1, 318);
INSERT INTO cat_service_template VALUES (359, 2, false, '2013-04-17 17:32:56.416', '2013-04-24 15:52:51.709', 'MALIC', 'Microsoft Access License', 1, 1, 1, 318);
INSERT INTO cat_service_template VALUES (1355, 0, false, '2013-04-29 14:10:58.81', NULL, 'EXCH20101_DATA_1G+', '1GB File storage Add', 1, 1, NULL, 318);
INSERT INTO cat_service_template VALUES (354, 1, false, '2013-04-17 17:13:33.324', '2013-04-29 14:11:36.114', 'EXCH_20102_BLACK', 'Exchange 2010 Premium - Blackberry option', 1, 1, 1, 318);
INSERT INTO cat_service_template VALUES (1356, 0, false, '2013-04-29 14:12:51.035', NULL, 'EXCH_20102_3G+', '3GB File storage Add', 1, 1, NULL, 318);
INSERT INTO cat_service_template VALUES (10034, 1, false, '2013-05-17 09:27:00.108', '2013-05-17 09:28:11.635', 'M020131', 'Microsoft Office 2013 standard', 1, 1, 1, 318);

--
-- Name: billing_operation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_operation_seq', 20150, true);


--
-- Name: billing_priceplan_inst_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_priceplan_inst_seq', 1, false);


--
-- Name: billing_priceplan_instanciation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_priceplan_instanciation_seq', 20000, false);

--
-- Name: billing_rated_transaction_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_rated_transaction_seq', 20000, false);


--
-- Data for Name: cat_recurring_charge_templ; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 330, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 342, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 355, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 376, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 388, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 338, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 380, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 392, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 400, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 364, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 372, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 384, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 396, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 334, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 346, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 368, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 1138, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 1144, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 1150, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 1156, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 1162, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 1168, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 1174, 1053);
INSERT INTO cat_recurring_charge_templ VALUES (false, 0, 'CALENDAR', false, false, 1403, 1053);


--
-- Name: billing_serv_param_inst_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_serv_param_inst_seq', 20000, false);


--
-- Name: billing_service_instance_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_service_instance_seq', 20002, true);


--
-- Name: billing_sub_term_reason_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_sub_term_reason_seq', 1, false);


--
-- Name: billing_subscrip_termin_reason_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_subscrip_termin_reason_seq', 20000, true);


--
-- Name: billing_subscription_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_subscription_seq', 20003, true);


--
-- Name: billing_tax_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_tax_seq', 20000, false);


--
-- Name: billing_term_reason_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_term_reason_seq', 10000, false);


--
-- Name: billing_trading_country_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_trading_country_seq', 20000, false);


--
-- Name: billing_trading_currency_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_trading_currency_seq', 20000, false);


--
-- Name: billing_trading_language_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_trading_language_seq', 20000, true);


--
-- Name: billing_wallet_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_wallet_seq', 20001, true);


--
-- Name: billing_wallet_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_wallet_template_seq', 20000, false);


--
-- Data for Name: cat_day_in_year; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_day_in_year VALUES (1, 1, 1, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (2, 1, 2, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (3, 1, 3, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (4, 1, 4, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (5, 1, 5, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (6, 1, 6, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (7, 1, 7, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (8, 1, 8, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (9, 1, 9, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (10, 1, 10, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (11, 1, 11, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (12, 1, 12, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (13, 1, 13, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (14, 1, 14, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (15, 1, 15, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (16, 1, 16, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (17, 1, 17, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (18, 1, 18, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (19, 1, 19, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (20, 1, 20, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (21, 1, 21, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (22, 1, 22, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (23, 1, 23, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (24, 1, 24, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (25, 1, 25, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (26, 1, 26, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (27, 1, 27, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (28, 1, 28, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (29, 1, 29, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (30, 1, 30, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (31, 1, 31, 'JANUARY', 1);
INSERT INTO cat_day_in_year VALUES (101, 1, 1, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (102, 1, 2, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (103, 1, 3, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (104, 1, 4, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (105, 1, 5, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (106, 1, 6, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (107, 1, 7, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (108, 1, 8, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (109, 1, 9, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (110, 1, 10, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (111, 1, 11, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (112, 1, 12, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (113, 1, 13, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (114, 1, 14, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (115, 1, 15, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (116, 1, 16, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (117, 1, 17, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (118, 1, 18, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (119, 1, 19, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (120, 1, 20, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (121, 1, 21, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (122, 1, 22, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (123, 1, 23, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (124, 1, 24, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (125, 1, 25, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (126, 1, 26, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (127, 1, 27, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (128, 1, 28, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (129, 1, 29, 'FEBRUARY', 1);
INSERT INTO cat_day_in_year VALUES (201, 1, 1, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (202, 1, 2, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (203, 1, 3, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (204, 1, 4, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (205, 1, 5, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (206, 1, 6, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (207, 1, 7, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (208, 1, 8, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (209, 1, 9, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (210, 1, 10, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (211, 1, 11, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (212, 1, 12, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (213, 1, 13, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (214, 1, 14, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (215, 1, 15, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (216, 1, 16, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (217, 1, 17, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (218, 1, 18, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (219, 1, 19, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (220, 1, 20, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (221, 1, 21, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (222, 1, 22, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (223, 1, 23, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (224, 1, 24, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (225, 1, 25, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (226, 1, 26, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (227, 1, 27, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (228, 1, 28, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (229, 1, 29, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (230, 1, 30, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (231, 1, 31, 'MARCH', 1);
INSERT INTO cat_day_in_year VALUES (301, 1, 1, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (302, 1, 2, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (303, 1, 3, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (304, 1, 4, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (305, 1, 5, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (306, 1, 6, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (307, 1, 7, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (308, 1, 8, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (309, 1, 9, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (310, 1, 10, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (311, 1, 11, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (312, 1, 12, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (313, 1, 13, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (314, 1, 14, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (315, 1, 15, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (316, 1, 16, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (317, 1, 17, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (318, 1, 18, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (319, 1, 19, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (320, 1, 20, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (321, 1, 21, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (322, 1, 22, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (323, 1, 23, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (324, 1, 24, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (325, 1, 25, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (326, 1, 26, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (327, 1, 27, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (328, 1, 28, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (329, 1, 29, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (330, 1, 30, 'APRIL', 1);
INSERT INTO cat_day_in_year VALUES (401, 1, 1, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (402, 1, 2, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (403, 1, 3, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (404, 1, 4, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (405, 1, 5, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (406, 1, 6, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (407, 1, 7, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (408, 1, 8, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (409, 1, 9, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (410, 1, 10, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (411, 1, 11, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (412, 1, 12, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (413, 1, 13, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (414, 1, 14, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (415, 1, 15, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (416, 1, 16, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (417, 1, 17, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (418, 1, 18, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (419, 1, 19, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (420, 1, 20, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (421, 1, 21, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (422, 1, 22, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (423, 1, 23, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (424, 1, 24, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (425, 1, 25, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (426, 1, 26, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (427, 1, 27, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (428, 1, 28, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (429, 1, 29, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (430, 1, 30, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (431, 1, 31, 'MAY', 1);
INSERT INTO cat_day_in_year VALUES (501, 1, 1, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (502, 1, 2, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (503, 1, 3, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (504, 1, 4, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (505, 1, 5, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (506, 1, 6, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (507, 1, 7, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (508, 1, 8, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (509, 1, 9, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (510, 1, 10, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (511, 1, 11, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (512, 1, 12, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (513, 1, 13, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (514, 1, 14, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (515, 1, 15, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (516, 1, 16, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (517, 1, 17, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (518, 1, 18, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (519, 1, 19, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (520, 1, 20, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (521, 1, 21, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (522, 1, 22, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (523, 1, 23, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (524, 1, 24, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (525, 1, 25, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (526, 1, 26, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (527, 1, 27, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (528, 1, 28, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (529, 1, 29, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (530, 1, 30, 'JUNE', 1);
INSERT INTO cat_day_in_year VALUES (601, 1, 1, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (602, 1, 2, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (603, 1, 3, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (604, 1, 4, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (605, 1, 5, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (606, 1, 6, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (607, 1, 7, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (608, 1, 8, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (609, 1, 9, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (610, 1, 10, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (611, 1, 11, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (612, 1, 12, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (613, 1, 13, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (614, 1, 14, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (615, 1, 15, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (616, 1, 16, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (617, 1, 17, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (618, 1, 18, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (619, 1, 19, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (620, 1, 20, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (621, 1, 21, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (622, 1, 22, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (623, 1, 23, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (624, 1, 24, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (625, 1, 25, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (626, 1, 26, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (627, 1, 27, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (628, 1, 28, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (629, 1, 29, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (630, 1, 30, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (631, 1, 31, 'JULY', 1);
INSERT INTO cat_day_in_year VALUES (701, 1, 1, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (702, 1, 2, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (703, 1, 3, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (704, 1, 4, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (705, 1, 5, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (706, 1, 6, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (707, 1, 7, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (708, 1, 8, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (709, 1, 9, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (710, 1, 10, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (711, 1, 11, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (712, 1, 12, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (713, 1, 13, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (714, 1, 14, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (715, 1, 15, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (716, 1, 16, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (717, 1, 17, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (718, 1, 18, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (719, 1, 19, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (720, 1, 20, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (721, 1, 21, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (722, 1, 22, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (723, 1, 23, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (724, 1, 24, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (725, 1, 25, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (726, 1, 26, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (727, 1, 27, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (728, 1, 28, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (729, 1, 29, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (730, 1, 30, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (731, 1, 31, 'AUGUST', 1);
INSERT INTO cat_day_in_year VALUES (801, 1, 1, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (802, 1, 2, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (803, 1, 3, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (804, 1, 4, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (805, 1, 5, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (806, 1, 6, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (807, 1, 7, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (808, 1, 8, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (809, 1, 9, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (810, 1, 10, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (811, 1, 11, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (812, 1, 12, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (813, 1, 13, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (814, 1, 14, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (815, 1, 15, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (816, 1, 16, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (817, 1, 17, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (818, 1, 18, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (819, 1, 19, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (820, 1, 20, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (821, 1, 21, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (822, 1, 22, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (823, 1, 23, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (824, 1, 24, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (825, 1, 25, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (826, 1, 26, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (827, 1, 27, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (828, 1, 28, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (829, 1, 29, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (830, 1, 30, 'SEPTEMBER', 1);
INSERT INTO cat_day_in_year VALUES (901, 1, 1, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (902, 1, 2, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (903, 1, 3, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (904, 1, 4, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (905, 1, 5, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (906, 1, 6, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (907, 1, 7, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (908, 1, 8, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (909, 1, 9, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (910, 1, 10, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (911, 1, 11, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (912, 1, 12, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (913, 1, 13, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (914, 1, 14, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (915, 1, 15, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (916, 1, 16, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (917, 1, 17, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (918, 1, 18, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (919, 1, 19, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (920, 1, 20, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (921, 1, 21, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (922, 1, 22, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (923, 1, 23, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (924, 1, 24, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (925, 1, 25, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (926, 1, 26, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (927, 1, 27, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (928, 1, 28, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (929, 1, 29, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (930, 1, 30, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (931, 1, 31, 'OCTOBER', 1);
INSERT INTO cat_day_in_year VALUES (1001, 1, 1, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1002, 1, 2, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1003, 1, 3, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1004, 1, 4, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1005, 1, 5, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1006, 1, 6, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1007, 1, 7, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1008, 1, 8, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1009, 1, 9, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1010, 1, 10, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1011, 1, 11, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1012, 1, 12, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1013, 1, 13, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1014, 1, 14, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1015, 1, 15, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1016, 1, 16, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1017, 1, 17, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1018, 1, 18, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1019, 1, 19, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1020, 1, 20, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1021, 1, 21, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1022, 1, 22, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1023, 1, 23, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1024, 1, 24, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1025, 1, 25, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1026, 1, 26, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1027, 1, 27, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1028, 1, 28, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1029, 1, 29, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1030, 1, 30, 'NOVEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1101, 1, 1, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1102, 1, 2, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1103, 1, 3, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1104, 1, 4, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1105, 1, 5, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1106, 1, 6, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1107, 1, 7, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1108, 1, 8, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1109, 1, 9, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1110, 1, 10, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1111, 1, 11, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1112, 1, 12, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1113, 1, 13, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1114, 1, 14, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1115, 1, 15, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1116, 1, 16, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1117, 1, 17, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1118, 1, 18, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1119, 1, 19, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1120, 1, 20, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1121, 1, 21, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1122, 1, 22, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1123, 1, 23, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1124, 1, 24, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1125, 1, 25, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1126, 1, 26, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1127, 1, 27, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1128, 1, 28, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1129, 1, 29, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1130, 1, 30, 'DECEMBER', 1);
INSERT INTO cat_day_in_year VALUES (1131, 1, 31, 'DECEMBER', 1);


--
-- Data for Name: cat_calendar_days; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_calendar_days VALUES (318, 1);
INSERT INTO cat_calendar_days VALUES (318, 101);
INSERT INTO cat_calendar_days VALUES (318, 201);
INSERT INTO cat_calendar_days VALUES (318, 301);
INSERT INTO cat_calendar_days VALUES (318, 401);
INSERT INTO cat_calendar_days VALUES (318, 501);
INSERT INTO cat_calendar_days VALUES (318, 601);
INSERT INTO cat_calendar_days VALUES (318, 701);
INSERT INTO cat_calendar_days VALUES (318, 801);
INSERT INTO cat_calendar_days VALUES (318, 901);
INSERT INTO cat_calendar_days VALUES (318, 1001);
INSERT INTO cat_calendar_days VALUES (318, 1101);
INSERT INTO cat_calendar_days VALUES (1041, 1);
INSERT INTO cat_calendar_days VALUES (1041, 101);
INSERT INTO cat_calendar_days VALUES (1041, 201);
INSERT INTO cat_calendar_days VALUES (1041, 301);
INSERT INTO cat_calendar_days VALUES (1041, 401);
INSERT INTO cat_calendar_days VALUES (1041, 501);
INSERT INTO cat_calendar_days VALUES (1041, 601);
INSERT INTO cat_calendar_days VALUES (1041, 701);
INSERT INTO cat_calendar_days VALUES (1041, 801);
INSERT INTO cat_calendar_days VALUES (1041, 901);
INSERT INTO cat_calendar_days VALUES (1041, 1001);
INSERT INTO cat_calendar_days VALUES (1041, 1101);
INSERT INTO cat_calendar_days VALUES (1097, 1);
INSERT INTO cat_calendar_days VALUES (1097, 101);
INSERT INTO cat_calendar_days VALUES (1097, 201);
INSERT INTO cat_calendar_days VALUES (1097, 301);
INSERT INTO cat_calendar_days VALUES (1097, 401);
INSERT INTO cat_calendar_days VALUES (1097, 501);
INSERT INTO cat_calendar_days VALUES (1097, 601);
INSERT INTO cat_calendar_days VALUES (1097, 701);
INSERT INTO cat_calendar_days VALUES (1097, 801);
INSERT INTO cat_calendar_days VALUES (1097, 901);
INSERT INTO cat_calendar_days VALUES (1097, 1001);
INSERT INTO cat_calendar_days VALUES (1097, 1101);
INSERT INTO cat_calendar_days VALUES (1053, 1);
INSERT INTO cat_calendar_days VALUES (1053, 101);
INSERT INTO cat_calendar_days VALUES (1053, 201);
INSERT INTO cat_calendar_days VALUES (1053, 301);
INSERT INTO cat_calendar_days VALUES (1053, 401);
INSERT INTO cat_calendar_days VALUES (1053, 501);
INSERT INTO cat_calendar_days VALUES (1053, 601);
INSERT INTO cat_calendar_days VALUES (1053, 701);
INSERT INTO cat_calendar_days VALUES (1053, 801);
INSERT INTO cat_calendar_days VALUES (1053, 901);
INSERT INTO cat_calendar_days VALUES (1053, 1001);
INSERT INTO cat_calendar_days VALUES (1053, 1101);


--
-- Name: cat_calendar_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_calendar_seq', 20000, false);


--
-- Name: cat_charge_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_charge_template_seq', 20000, false);


--
-- Name: cat_counter_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_counter_template_seq', 20000, false);


--
-- Name: cat_day_in_year_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_day_in_year_seq', 20000, false);

--
-- Name: cat_discount_plan_matrix_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_discount_plan_matrix_seq', 20000, false);


--
-- Data for Name: cat_offer_serv_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_offer_serv_templates VALUES (320, 352);
INSERT INTO cat_offer_serv_templates VALUES (320, 1356);
INSERT INTO cat_offer_serv_templates VALUES (320, 354);
INSERT INTO cat_offer_serv_templates VALUES (327, 354);
INSERT INTO cat_offer_serv_templates VALUES (327, 359);
INSERT INTO cat_offer_serv_templates VALUES (319, 350);
INSERT INTO cat_offer_serv_templates VALUES (319, 1355);
INSERT INTO cat_offer_serv_templates VALUES (321, 10034);


--
-- Name: cat_offer_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_offer_template_seq', 20000, false);


--
-- Data for Name: cat_one_shot_charge_templ; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_one_shot_charge_templ VALUES (true, 'OTHER', 1058);


SELECT pg_catalog.setval('account_entity_seq', 20003, true);

--
-- Name: cat_price_code_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_price_code_seq', 20000, false);


--
-- Data for Name: cat_price_plan_matrix; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_price_plan_matrix VALUES (1204, 0, false, '2013-04-25 15:29:00.747', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL, 'CM_MSOFT_INC', 99, 0,1,'2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1210, 4, false, '2013-04-25 15:35:05.384', '2013-05-03 10:27:34.479', 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL, 'CM_MSH2010_INC', 99, 0,1, '2013-04-01 00:00:00', '2013-04-22 00:00:00', 1, 1, 1, 1101, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1242, 1, false, '2013-04-25 17:17:09.801', '2013-05-01 22:16:30.829', 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL, 'EXCH20101_DATA_1G', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, 1, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1205, 0, false, '2013-04-25 15:30:08.966', NULL, 0.002500000000, 0.002100000000, NULL, NULL, NULL, NULL, NULL, 'CM_MSOFT_ADD', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1206, 0, false, '2013-04-25 15:31:38.942', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL, 'CM_MDYCRM2011_INC', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1208, 0, false, '2013-04-25 15:33:23.857', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL,  'CM_MDYCRM2011_INC', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1211, 0, false, '2013-04-25 15:35:52.182', NULL, 0.002500000000, 0.002100000000, NULL, NULL, NULL, NULL, NULL, 'CM_MSH2010_ADD', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1212, 0, false, '2013-04-25 15:37:01.209', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL,  'CM_MSH2010_INC', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1213, 0, false, '2013-04-25 15:37:59.089', NULL, 0.002500000000, 0.002100000000, NULL, NULL, NULL, NULL, NULL, 'CM_MSH2010_ADD', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1214, 0, false, '2013-04-25 15:44:16.092', NULL, 25.000000000000, 21.008400000000, NULL, NULL, NULL, NULL, NULL,  'MALIC_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1215, 0, false, '2013-04-25 15:45:13.073', NULL, 25.000000000000, 20.903000000000, NULL, NULL, NULL, NULL, NULL,'MALIC_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1216, 0, false, '2013-04-25 15:47:43.826', NULL, 15.000000000000, 12.605000000000, NULL, NULL, NULL, NULL, NULL,  'CUST_MLYNC2010_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1217, 0, false, '2013-04-25 15:49:31.214', NULL, 15.000000000000, 12.541800000000, NULL, NULL, NULL, NULL, NULL, 'CUST_MLYNC2010_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1218, 0, false, '2013-04-25 15:51:00.49', NULL, 2.000000000000, 1.680700000000, NULL, NULL, NULL, NULL, NULL,  'MLYNC2010_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1219, 0, false, '2013-04-25 15:52:03.09', NULL, 2.000000000000, 1.672200000000, NULL, NULL, NULL, NULL, NULL,  'MLYNC2010_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1220, 0, false, '2013-04-25 15:55:04.629', NULL, 35.000000000000, 29.411800000000, NULL, NULL, NULL, NULL, NULL,  'CUST_MDYCRM2011_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1221, 0, false, '2013-04-25 15:56:42.852', NULL, 35.000000000000, 29.264200000000, NULL, NULL, NULL, NULL, NULL,  'CUST_MDYCRM2011_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1222, 0, false, '2013-04-25 15:58:20.077', NULL, 2.000000000000, 1.680800000000, NULL, NULL, NULL, NULL, NULL,  'MDYCRM2011_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1223, 0, false, '2013-04-25 15:59:24.753', NULL, 2.000000000000, 1.672200000000, NULL, NULL, NULL, NULL, NULL,  'MDYCRM2011_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1224, 0, false, '2013-04-25 16:02:05.657', NULL, 20.000000000000, 16.806700000000, NULL, NULL, NULL, NULL, NULL, 'CUST_MSH2010_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1225, 1, false, '2013-04-25 16:03:30.683', '2013-04-25 16:04:10.388', 20.000000000000, 16.722400000000, NULL, NULL, NULL, NULL, NULL,  'CUST_MSH2010_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, 1, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1226, 0, false, '2013-04-25 16:08:12.103', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL,  'CUST_MSH2010_DATA', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1227, 0, false, '2013-04-25 16:11:18.412', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL,  'CUST_MSH2010_DATA', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1228, 0, false, '2013-04-25 16:13:29.707', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL,  'CUST_MDYCRM2011_DATA', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1229, 0, false, '2013-04-25 16:14:17.479', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL, 'CUST_MDYCRM2011_DATA', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1231, 0, false, '2013-04-25 16:20:58.405', NULL, 30.000000000000, 25.210800000000, NULL, NULL, NULL, NULL, NULL,  'MPR2013_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1232, 0, false, '2013-04-25 16:22:02.555', NULL, 30.000000000000, 25.083600000000, NULL, NULL, NULL, NULL, NULL,  'MPR2013_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1233, 0, false, '2013-04-25 16:29:32.452', NULL, 15.000000000000, 12.605000000000, NULL, NULL, NULL, NULL, NULL,  'MVI2013_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1234, 0, false, '2013-04-25 16:30:41.617', NULL, 15.000000000000, 12.541800000000, NULL, NULL, NULL, NULL, NULL, 'MVI2013_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1235, 0, false, '2013-04-25 16:36:06.277', NULL, 25.000000000000, 21.008400000000, NULL, NULL, NULL, NULL, NULL,  'MO20132_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1236, 0, false, '2013-04-25 16:37:09.817', NULL, 25.000000000000, 20.903000000000, NULL, NULL, NULL, NULL, NULL,  'MO20132_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1239, 0, false, '2013-04-25 17:14:34.072', NULL, 10.000000000000, 8.403400000000, NULL, NULL, NULL, NULL, NULL, 'EXCH20101_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1240, 0, false, '2013-04-25 17:15:30.068', NULL, 10.000000000000, 8.361200000000, NULL, NULL, NULL, NULL, NULL,  'EXCH20101_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1241, 0, false, '2013-04-25 17:16:32.882', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL,  'EXCH20101_DATA_1G', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1243, 0, false, '2013-04-25 17:18:07.931', NULL, 2.000000000000, 1.680700000000, NULL, NULL, NULL, NULL, NULL,  'EXCH20101_DATA_1G+', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1209, 2, false, '2013-04-25 15:34:13.854', NULL, 0.002500000000, 0.002100000000, NULL, NULL, NULL, NULL, NULL,  'CM_MDYCRM2011_ADD', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1244, 0, false, '2013-04-25 17:18:59.727', NULL, 2.000000000000, 1.672200000000, NULL, NULL, NULL, NULL, NULL,  'EXCH20101_DATA_1G+', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1245, 0, false, '2013-04-25 17:21:08.891', NULL, 14.000000000000, 11.764700000000, NULL, NULL, NULL, NULL, NULL, 'EXCH20102_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1246, 0, false, '2013-04-25 17:21:58.769', NULL, 14.000000000000, 11.705700000000, NULL, NULL, NULL, NULL, NULL,  'EXCH20102_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1247, 0, false, '2013-04-25 17:23:00.208', NULL, 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL, 'EXCH20102_DATA_2G', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1191, 3, false, '2013-04-25 14:19:16.144', '2013-04-29 07:52:31.267', 0.002500000000, 0.002100000000, NULL, NULL, NULL, NULL, NULL,  'CM_MSOFT_ADD', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, 1, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1249, 0, false, '2013-04-25 17:24:41.619', NULL, 2.000000000000, 1.680700000000, NULL, NULL, NULL, NULL, NULL, 'EXCH20102_DATA_3G+', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1250, 0, false, '2013-04-25 17:25:28.142', NULL, 2.000000000000, 1.672200000000, NULL, NULL, NULL, NULL, NULL,  'EXCH20102_DATA_3G+', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1248, 1, false, '2013-04-25 17:23:27.781', '2013-04-25 17:25:51.526', 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL,  'EXCH20102_DATA_2G', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, 1, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1238, 1, false, '2013-04-25 16:40:21.649', '2013-05-17 11:04:40.915', 20.000000000000, 16.722400000000, NULL, NULL, NULL, NULL, NULL, 'MO20131_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, 1, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1251, 0, false, '2013-04-25 17:27:10.434', NULL, 6.000000000000, 5.042000000000, NULL, NULL, NULL, NULL, NULL,  'EXCH20102_SOFT_BLACK', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1252, 0, false, '2013-04-25 17:27:58.844', NULL, 6.000000000000, 5.016700000000, NULL, NULL, NULL, NULL, NULL,  'EXCH20102_SOFT_BLACK', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1253, 0, false, '2013-04-25 17:31:47.08', NULL, 30.000000000000, 25.210100000000, NULL, NULL, NULL, NULL, NULL,  'CUST_FASTVIEWER_1', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1254, 0, false, '2013-04-25 17:32:37.206', NULL, 30.000000000000, 25.083600000000, NULL, NULL, NULL, NULL, NULL,  'CUST_FASTVIEWER_1', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1255, 0, false, '2013-04-25 17:34:04.02', NULL, 140.000000000000, 117.647100000000, NULL, NULL, NULL, NULL, NULL,  'CUST_FASTVIEWER_5', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1256, 0, false, '2013-04-25 17:34:54.118', NULL, 140.000000000000, 117.056900000000, NULL, NULL, NULL, NULL, NULL,  'CUST_FASTVIEWER_5', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1257, 0, false, '2013-04-25 17:35:50.363', NULL, 250.000000000000, 210.084000000000, NULL, NULL, NULL, NULL, NULL,  'CUST_FASTVIEWER_10', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, NULL, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1258, 2, false, '2013-04-25 17:36:43.646', '2013-04-28 21:35:01.152', 250.000000000000, 209.030100000000, NULL, NULL, NULL, NULL, NULL,  'CUST_FASTVIEWER_10', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, 1, 1102, 157, 145);
INSERT INTO cat_price_plan_matrix VALUES (1237, 1, false, '2013-04-25 16:39:21.466', '2013-05-17 11:04:03.156', 20.000000000000, 16.806800000000, NULL, NULL, NULL, NULL, NULL,  'MO20131_SOFT', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, 1, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (10044, 1, false, '2013-05-17 10:39:41.212', '2013-05-17 11:04:25.195', 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL,  'MO20131_SOFT', 99, 0,1, NULL, NULL, 1, 1, 1, NULL, 157, NULL);
INSERT INTO cat_price_plan_matrix VALUES (1207, 1, false, '2013-04-25 15:32:44.569', '2013-05-17 11:06:42.335', 0.002500000000, 0.002100000000, NULL, NULL, NULL, NULL, NULL, 'CM_MDYCRM2011_ADD', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, 1, 1101, 156, 145);
INSERT INTO cat_price_plan_matrix VALUES (1190, 4, false, '2013-04-25 14:15:55.623', '2013-05-21 15:47:32.599', 0.000000000000, 0.000000000000, NULL, NULL, NULL, NULL, NULL,  'CM_MSOFT_INC', 99, 0,1, '2013-04-01 00:00:00', '2013-04-01 00:00:00', 1, 1, 1, 1101, 156, 145);


--
-- Name: cat_price_plan_matrix_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_price_plan_matrix_seq', 20000, false);


--
-- Data for Name: cat_serv_onecharge_s_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_serv_onecharge_t_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_serv_reccharge_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_serv_reccharge_templates VALUES (350, 334);
INSERT INTO cat_serv_reccharge_templates VALUES (350, 338);
INSERT INTO cat_serv_reccharge_templates VALUES (352, 342);
INSERT INTO cat_serv_reccharge_templates VALUES (352, 368);
INSERT INTO cat_serv_reccharge_templates VALUES (359, 376);
INSERT INTO cat_serv_reccharge_templates VALUES (1355, 338);
INSERT INTO cat_serv_reccharge_templates VALUES (354, 330);
INSERT INTO cat_serv_reccharge_templates VALUES (1356, 346);
INSERT INTO cat_serv_reccharge_templates VALUES (10034, 376);


--
-- Data for Name: cat_usage_charge_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_usage_charge_template VALUES ('', NULL, NULL, NULL, NULL, 1, 'MByte', 'DECIMAL', 1.00, 2, 1186);
INSERT INTO cat_usage_charge_template VALUES ('', NULL, NULL, NULL, NULL, 1, 'MByte', 'DECIMAL', 1.00, 2, 1187);
INSERT INTO cat_usage_charge_template VALUES ('', NULL, NULL, NULL, NULL, 1, 'MByte', 'DECIMAL', 1.00, 2, 1184);
INSERT INTO cat_usage_charge_template VALUES ('', 'EXCH', '', '', '', 2, 'MByte', 'DECIMAL', 1000.00, 2, 1183);
INSERT INTO cat_usage_charge_template VALUES ('', 'EXCH', '', '', '', 1, 'MByte', 'DECIMAL', 0.01, 2, 1182);
INSERT INTO cat_usage_charge_template VALUES ('', '', '', '', '', 1, 'MByte', 'DECIMAL', 1.00, 2, 1185);


--
-- Data for Name: cat_serv_usage_charge_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_serv_usage_charge_template VALUES (10035, 0, 1, 1182, 10033, 10034);
INSERT INTO cat_serv_usage_charge_template VALUES (10036, 0, 1, 1183, NULL, 10034);


--
-- Name: cat_serv_usage_charge_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_serv_usage_charge_template_seq', 20000, false);


--
-- Name: cat_serv_usagechrg_templt_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_serv_usagechrg_templt_seq', 1, false);


--
-- Name: cat_service_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_service_template_seq', 20000, false);


--
-- Data for Name: com_campaign; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_campaign_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_campaign_seq', 20000, false);


--
-- Data for Name: com_contact; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: com_message; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: com_contact_com_message; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: com_contact_coords; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_contact_coords_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_contact_coords_seq', 20000, false);


--
-- Name: com_contact_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_contact_seq', 20000, false);


--
-- Name: com_message_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_message_seq', 20000, false);


--
-- Data for Name: com_message_template; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_msg_tmpl_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_msg_tmpl_seq', 20000, false);


--
-- Name: com_msg_tmpl_var_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_msg_tmpl_var_seq', 20000, false);


--
-- Data for Name: com_msg_tmpl_variable; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_msg_var_val_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_msg_var_val_seq', 20000, false);


--
-- Data for Name: com_msg_var_value; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_prov_pol_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_prov_pol_seq', 20000, false);


--
-- Data for Name: com_provider_policy; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: com_sender_config; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_sndr_conf_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_sndr_conf_seq', 20000, false);


--
-- Name: crm_customer_brand_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_customer_brand_seq', 20000, false);


--
-- Name: crm_customer_category_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_customer_category_seq', 20000, false);


--
-- Name: crm_email_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_email_seq', 20000, false);


--
-- Data for Name: crm_provider_config; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: crm_provider_config_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_provider_config_seq', 20000, false);


--
-- Name: crm_provider_contact_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_provider_contact_seq', 20000, false);


--
-- Name: crm_provider_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_provider_seq', 20000, false);


--
-- Name: crm_seller_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_seller_seq', 20000, true);


--
-- Data for Name: dwh_account_operation; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: dwh_account_operation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('dwh_account_operation_seq', 20000, false);


--
-- Data for Name: dwh_journal_entries; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: dwh_journal_entries_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('dwh_journal_entries_seq', 20000, false);


--
-- Name: dwh_sales_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('dwh_sales_seq', 10000, false);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('hibernate_sequence', 10225, true);


--
-- Data for Name: job_execution; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: job_execution_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('job_execution_seq', 20050, true);


--
-- Data for Name: mediation_magic_numbers; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: mediation_magic_numbers_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('mediation_magic_numbers_seq', 20000, false);

--
-- Name: medina_access_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('medina_access_seq', 20000, true);


--
-- Data for Name: medina_number_plan; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: medina_number_plan_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('medina_number_plan_seq', 20000, false);


--
-- Data for Name: medina_time_plan; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: medina_time_plan_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('medina_time_plan_seq', 20000, false);


--
-- Data for Name: medina_zonning_plan; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: medina_zonning_plan_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('medina_zonning_plan_seq', 20000, false);


--
-- Data for Name: meveo_timer; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: meveo_timer_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('meveo_timer_seq', 20250, true);


--
-- Name: offer_instance_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('offer_instance_seq', 10000, false);


--
-- Data for Name: provider_titles; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: rating_edr; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: rating_edr_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rating_edr_seq', 20000, false);


--
-- Name: rating_matrix_definition_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rating_matrix_definition_seq', 10000, false);


--
-- Name: rating_matrix_entry_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rating_matrix_entry_seq', 10000, false);


--
-- Name: rating_usage_type_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rating_usage_type_seq', 10000, false);


--
-- Data for Name: report_emails; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: rm_line_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rm_line_seq', 10000, false);


--
-- Name: rm_usage_counter_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rm_usage_counter_seq', 10000, false);




--
-- PostgreSQL database dump complete
--

