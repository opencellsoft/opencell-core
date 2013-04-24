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

SELECT pg_catalog.setval('access_point_seq', 1, false);


--
-- Name: access_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('access_seq', 1, false);


--
-- Data for Name: account_entity; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO account_entity VALUES (125, 2, false, '2013-04-12 10:51:37.077', '2013-04-17 12:15:47.741', '55', 'hfbf', '', '', '', '', 'Australie', NULL, '', true, '52', '', 'ben', 'gbvdv', NULL, 1, 1, 1, 1, NULL);


--
-- Name: account_entity_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('account_entity_seq', 1, false);


--
-- Data for Name: adm_country; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_country VALUES (1, '2013-04-19 18:45:43.824949', NULL, 1, 'AD', 'Andorra', NULL, NULL, 5, 8);
INSERT INTO adm_country VALUES (2, '2013-04-19 18:45:43.824949', NULL, 1, 'AE', 'United Arab Emirates', NULL, NULL, 54, 3);
INSERT INTO adm_country VALUES (3, '2013-04-19 18:45:43.824949', NULL, 1, 'AF', 'Afghanistan', NULL, NULL, 1, 2);
INSERT INTO adm_country VALUES (4, '2013-04-19 18:45:43.824949', NULL, 1, 'AG', 'Antigua and Barbuda', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (5, '2013-04-19 18:45:43.824949', NULL, 1, 'AI', 'Anguilla', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (6, '2013-04-19 18:45:43.824949', NULL, 1, 'AL', 'Albania', NULL, NULL, 3, 2);
INSERT INTO adm_country VALUES (7, '2013-04-19 18:45:43.824949', NULL, 1, 'AM', 'Armenia', NULL, NULL, 11, 2);
INSERT INTO adm_country VALUES (8, '2013-04-19 18:45:43.824949', NULL, 1, 'AN', 'Netherlands Antilles', NULL, NULL, 8, 21);
INSERT INTO adm_country VALUES (9, '2013-04-19 18:45:43.824949', NULL, 1, 'AO', 'Angola', NULL, NULL, 6, 2);
INSERT INTO adm_country VALUES (10, '2013-04-19 18:45:43.824949', NULL, 1, 'AR', 'Argentina', NULL, NULL, 10, 8);
INSERT INTO adm_country VALUES (11, '2013-04-19 18:45:43.824949', NULL, 1, 'AS', 'American Samoa', NULL, NULL, 49, 1);
INSERT INTO adm_country VALUES (12, '2013-04-19 18:45:43.824949', NULL, 1, 'AT', 'Austria', NULL, NULL, 5, 1);
INSERT INTO adm_country VALUES (13, '2013-04-19 18:45:43.824949', NULL, 1, 'AU', 'Australia', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (14, '2013-04-19 18:45:43.824949', NULL, 1, 'AW', 'Aruba', NULL, NULL, 12, 2);
INSERT INTO adm_country VALUES (15, '2013-04-19 18:45:43.824949', NULL, 1, 'AZ', 'Azerbaijan', NULL, NULL, 6, 2);
INSERT INTO adm_country VALUES (16, '2013-04-19 18:45:43.824949', NULL, 1, 'BA', 'Bosnia and Herzegovina', NULL, NULL, 25, 2);
INSERT INTO adm_country VALUES (17, '2013-04-19 18:45:43.824949', NULL, 1, 'BB', 'Barbados', NULL, NULL, 18, 2);
INSERT INTO adm_country VALUES (18, '2013-04-19 18:45:43.824949', NULL, 1, 'BD', 'Bangladesh', NULL, NULL, 17, 2);
INSERT INTO adm_country VALUES (19, '2013-04-19 18:45:43.824949', NULL, 1, 'BE', 'Belgium', NULL, NULL, 5, 12);
INSERT INTO adm_country VALUES (20, '2013-04-19 18:45:43.824949', NULL, 1, 'BF', 'Burkina Faso', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (21, '2013-04-19 18:45:43.824949', NULL, 1, 'BG', 'Bulgaria', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (22, '2013-04-19 18:45:43.824949', NULL, 1, 'BH', 'Bahrain', NULL, NULL, 16, 2);
INSERT INTO adm_country VALUES (23, '2013-04-19 18:45:43.824949', NULL, 1, 'BI', 'Burundi', NULL, NULL, 30, 2);
INSERT INTO adm_country VALUES (24, '2013-04-19 18:45:43.824949', NULL, 1, 'BJ', 'Benin', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (25, '2013-04-19 18:45:43.824949', NULL, 1, 'BM', 'Bermuda', NULL, NULL, 21, 2);
INSERT INTO adm_country VALUES (26, '2013-04-19 18:45:43.824949', NULL, 1, 'BN', 'Brunei Darussalam', NULL, NULL, 28, 2);
INSERT INTO adm_country VALUES (27, '2013-04-19 18:45:43.824949', NULL, 1, 'BO', 'Bolivia', NULL, NULL, 24, 2);
INSERT INTO adm_country VALUES (28, '2013-04-19 18:45:43.824949', NULL, 1, 'BR', 'Brazil', NULL, NULL, 27, 2);
INSERT INTO adm_country VALUES (29, '2013-04-19 18:45:43.824949', NULL, 1, 'BS', 'The Bahamas', NULL, NULL, 15, 2);
INSERT INTO adm_country VALUES (30, '2013-04-19 18:45:43.824949', NULL, 1, 'BT', 'Bhutan', NULL, NULL, 16, 2);
INSERT INTO adm_country VALUES (31, '2013-04-19 18:45:43.824949', NULL, 1, 'BV', 'Bouvet Island', NULL, NULL, 31, 2);
INSERT INTO adm_country VALUES (32, '2013-04-19 18:45:43.824949', NULL, 1, 'BW', 'Botswana', NULL, NULL, 26, 2);
INSERT INTO adm_country VALUES (33, '2013-04-19 18:45:43.824949', NULL, 1, 'BY', 'Belarus', NULL, NULL, 23, 2);
INSERT INTO adm_country VALUES (34, '2013-04-19 18:45:43.824949', NULL, 1, 'BZ', 'Belize', NULL, NULL, 19, 2);
INSERT INTO adm_country VALUES (35, '2013-04-19 18:45:43.824949', NULL, 1, 'CA', 'Canada', NULL, NULL, 35, 2);
INSERT INTO adm_country VALUES (36, '2013-04-19 18:45:43.824949', NULL, 1, 'CC', 'Cocos (Keeling) Islands', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (37, '2013-04-19 18:45:43.824949', NULL, 1, 'CD', 'Congo, Democratic Republic of th', NULL, NULL, 43, 12);
INSERT INTO adm_country VALUES (38, '2013-04-19 18:45:43.824949', NULL, 1, 'CF', 'Central African Republic', NULL, NULL, 34, 12);
INSERT INTO adm_country VALUES (39, '2013-04-19 18:45:43.824949', NULL, 1, 'CG', 'Congo, Republic of the', NULL, NULL, 34, 12);
INSERT INTO adm_country VALUES (40, '2013-04-19 18:45:43.824949', NULL, 1, 'CH', 'Switzerland', NULL, NULL, 92, 2);
INSERT INTO adm_country VALUES (41, '2013-04-19 18:45:43.824949', NULL, 1, 'CI', 'Cote Ivoire', NULL, NULL, 20, 12);
INSERT INTO adm_country VALUES (42, '2013-04-19 18:45:43.824949', NULL, 1, 'CK', 'Cook Islands', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (43, '2013-04-19 18:45:43.824949', NULL, 1, 'CL', 'Chile', NULL, NULL, 38, 2);
INSERT INTO adm_country VALUES (44, '2013-04-19 18:45:43.824949', NULL, 1, 'CM', 'Cameroon', NULL, NULL, 34, 12);
INSERT INTO adm_country VALUES (45, '2013-04-19 18:45:43.824949', NULL, 1, 'CN', 'China', NULL, NULL, 39, 6);
INSERT INTO adm_country VALUES (46, '2013-04-19 18:45:43.824949', NULL, 1, 'CO', 'Colombia', NULL, NULL, 41, 8);
INSERT INTO adm_country VALUES (47, '2013-04-19 18:45:43.824949', NULL, 1, 'CR', 'Costa Rica', NULL, NULL, 46, 8);
INSERT INTO adm_country VALUES (48, '2013-04-19 18:45:43.824949', NULL, 1, 'CU', 'Cuba', NULL, NULL, 48, 8);
INSERT INTO adm_country VALUES (49, '2013-04-19 18:45:43.824949', NULL, 1, 'CV', 'Cape Verde', NULL, NULL, 36, 8);
INSERT INTO adm_country VALUES (50, '2013-04-19 18:45:43.824949', NULL, 1, 'CX', 'Christmas Island', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (51, '2013-04-19 18:45:43.824949', NULL, 1, 'CY', 'Cyprus', NULL, NULL, 40, 2);
INSERT INTO adm_country VALUES (52, '2013-04-19 18:45:43.824949', NULL, 1, 'CZ', 'Czech Republic', NULL, NULL, 152, 2);
INSERT INTO adm_country VALUES (53, '2013-04-19 18:45:43.824949', NULL, 1, 'DE', 'Germany', NULL, NULL, 5, 1);
INSERT INTO adm_country VALUES (54, '2013-04-19 18:45:43.824949', NULL, 1, 'DJ', 'Djibouti', NULL, NULL, 51, 12);
INSERT INTO adm_country VALUES (55, '2013-04-19 18:45:43.824949', NULL, 1, 'DK', 'Denmark', NULL, NULL, 50, 2);
INSERT INTO adm_country VALUES (56, '2013-04-19 18:45:43.824949', NULL, 1, 'DM', 'Dominica', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (57, '2013-04-19 18:45:43.824949', NULL, 1, 'DO', 'Dominican Republic', NULL, NULL, 52, 2);
INSERT INTO adm_country VALUES (58, '2013-04-19 18:45:43.824949', NULL, 1, 'DZ', 'Algeria', NULL, NULL, 4, 8);
INSERT INTO adm_country VALUES (59, '2013-04-19 18:45:43.824949', NULL, 1, 'EC', 'Ecuador', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (60, '2013-04-19 18:45:43.824949', NULL, 1, 'EE', 'Estonia', NULL, NULL, 56, 2);
INSERT INTO adm_country VALUES (61, '2013-04-19 18:45:43.824949', NULL, 1, 'EG', 'Egypt', NULL, NULL, 53, 8);
INSERT INTO adm_country VALUES (62, '2013-04-19 18:45:43.824949', NULL, 1, 'ER', 'Eritrea', NULL, NULL, 55, 2);
INSERT INTO adm_country VALUES (63, '2013-04-19 18:45:43.824949', NULL, 1, 'ES', 'Spain', NULL, NULL, 5, 8);
INSERT INTO adm_country VALUES (64, '2013-04-19 18:45:43.824949', NULL, 1, 'ET', 'Ethiopia', NULL, NULL, 57, 2);
INSERT INTO adm_country VALUES (65, '2013-04-19 18:45:43.824949', NULL, 1, 'FI', 'Finland', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (66, '2013-04-19 18:45:43.824949', NULL, 1, 'FJ', 'Fiji', NULL, NULL, 59, 2);
INSERT INTO adm_country VALUES (67, '2013-04-19 18:45:43.824949', NULL, 1, 'FK', 'Falkland Islands (Islas Malvinas', NULL, NULL, 58, 2);
INSERT INTO adm_country VALUES (68, '2013-04-19 18:45:43.824949', NULL, 1, 'FM', 'Micronesia, Federated States of', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (69, '2013-04-19 18:45:43.824949', NULL, 1, 'FO', 'Faroe Islands', NULL, NULL, 50, 2);
INSERT INTO adm_country VALUES (70, '2013-04-19 18:45:43.824949', NULL, 1, 'FR', 'France', NULL, NULL, 5, 12);
INSERT INTO adm_country VALUES (71, '2013-04-19 18:45:43.824949', NULL, 1, 'GA', 'Gabon', NULL, NULL, 34, 12);
INSERT INTO adm_country VALUES (72, '2013-04-19 18:45:43.824949', NULL, 1, 'GD', 'Grenada', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (73, '2013-04-19 18:45:43.824949', NULL, 1, 'GE', 'Georgia', NULL, NULL, 61, 2);
INSERT INTO adm_country VALUES (74, '2013-04-19 18:45:43.824949', NULL, 1, 'GF', 'French Guiana', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (75, '2013-04-19 18:45:43.824949', NULL, 1, 'GH', 'Ghana', NULL, NULL, 62, 2);
INSERT INTO adm_country VALUES (76, '2013-04-19 18:45:43.824949', NULL, 1, 'GI', 'Gibraltar', NULL, NULL, 63, 2);
INSERT INTO adm_country VALUES (77, '2013-04-19 18:45:43.824949', NULL, 1, 'GL', 'Greenland', NULL, NULL, 50, 2);
INSERT INTO adm_country VALUES (78, '2013-04-19 18:45:43.824949', NULL, 1, 'GM', 'The Gambia', NULL, NULL, 60, 2);
INSERT INTO adm_country VALUES (79, '2013-04-19 18:45:43.824949', NULL, 1, 'GN', 'Guinea', NULL, NULL, 66, 2);
INSERT INTO adm_country VALUES (80, '2013-04-19 18:45:43.824949', NULL, 1, 'GP', 'Guadeloupe', NULL, NULL, 5, 12);
INSERT INTO adm_country VALUES (81, '2013-04-19 18:45:43.824949', NULL, 1, 'GQ', 'Equatorial Guinea', NULL, NULL, 34, 2);
INSERT INTO adm_country VALUES (82, '2013-04-19 18:45:43.824949', NULL, 1, 'GR', 'Greece', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (83, '2013-04-19 18:45:43.824949', NULL, 1, 'GS', 'South Georgia and the South Sand', NULL, NULL, 65, 2);
INSERT INTO adm_country VALUES (84, '2013-04-19 18:45:43.824949', NULL, 1, 'GT', 'Guatemala', NULL, NULL, 64, 2);
INSERT INTO adm_country VALUES (85, '2013-04-19 18:45:43.824949', NULL, 1, 'GU', 'Guam', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (86, '2013-04-19 18:45:43.824949', NULL, 1, 'GW', 'Guinea-Bissau', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (87, '2013-04-19 18:45:43.824949', NULL, 1, 'GY', 'Guyana', NULL, NULL, 67, 2);
INSERT INTO adm_country VALUES (88, '2013-04-19 18:45:43.824949', NULL, 1, 'HK', 'Hong Kong (SAR)', NULL, NULL, 70, 2);
INSERT INTO adm_country VALUES (89, '2013-04-19 18:45:43.824949', NULL, 1, 'HM', 'Heard Island and McDonald Island', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (90, '2013-04-19 18:45:43.824949', NULL, 1, 'HN', 'Honduras', NULL, NULL, 69, 2);
INSERT INTO adm_country VALUES (91, '2013-04-19 18:45:43.824949', NULL, 1, 'HR', 'Croatia', NULL, NULL, 47, 2);
INSERT INTO adm_country VALUES (92, '2013-04-19 18:45:43.824949', NULL, 1, 'HT', 'Haiti', NULL, NULL, 68, 2);
INSERT INTO adm_country VALUES (93, '2013-04-19 18:45:43.824949', NULL, 1, 'HU', 'Hungary', NULL, NULL, 71, 2);
INSERT INTO adm_country VALUES (94, '2013-04-19 18:45:43.824949', NULL, 1, 'ID', 'Indonesia', NULL, NULL, 74, 2);
INSERT INTO adm_country VALUES (95, '2013-04-19 18:45:43.824949', NULL, 1, 'IE', 'Ireland', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (96, '2013-04-19 18:45:43.824949', NULL, 1, 'IL', 'Israel', NULL, NULL, 78, 2);
INSERT INTO adm_country VALUES (97, '2013-04-19 18:45:43.824949', NULL, 1, 'IN', 'India', NULL, NULL, 73, 2);
INSERT INTO adm_country VALUES (98, '2013-04-19 18:45:43.824949', NULL, 1, 'IO', 'British Indian Ocean Territory', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (99, '2013-04-19 18:45:43.824949', NULL, 1, 'IQ', 'Iraq', NULL, NULL, 76, 8);
INSERT INTO adm_country VALUES (100, '2013-04-19 18:45:43.824949', NULL, 1, 'IR', 'Iran', NULL, NULL, 75, 2);
INSERT INTO adm_country VALUES (101, '2013-04-19 18:45:43.824949', NULL, 1, 'IS', 'Iceland', NULL, NULL, 77, 2);
INSERT INTO adm_country VALUES (102, '2013-04-19 18:45:43.824949', NULL, 1, 'IT', 'Italy', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (103, '2013-04-19 18:45:43.824949', NULL, 1, 'JM', 'Jamaica', NULL, NULL, 79, 2);
INSERT INTO adm_country VALUES (104, '2013-04-19 18:45:43.824949', NULL, 1, 'JO', 'Jordan', NULL, NULL, 81, 2);
INSERT INTO adm_country VALUES (105, '2013-04-19 18:45:43.824949', NULL, 1, 'JP', 'Japan', NULL, NULL, 80, 2);
INSERT INTO adm_country VALUES (106, '2013-04-19 18:45:43.824949', NULL, 1, 'KE', 'Kenya', NULL, NULL, 83, 2);
INSERT INTO adm_country VALUES (107, '2013-04-19 18:45:43.824949', NULL, 1, 'KG', 'Kyrgyzstan', NULL, NULL, 84, 2);
INSERT INTO adm_country VALUES (108, '2013-04-19 18:45:43.824949', NULL, 1, 'KH', 'Cambodia', NULL, NULL, 33, 2);
INSERT INTO adm_country VALUES (109, '2013-04-19 18:45:43.824949', NULL, 1, 'KI', 'Kiribati', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (110, '2013-04-19 18:45:43.824949', NULL, 1, 'KM', 'Comoros', NULL, NULL, 42, 2);
INSERT INTO adm_country VALUES (111, '2013-04-19 18:45:43.824949', NULL, 1, 'KN', 'Saint Kitts and Nevis', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (112, '2013-04-19 18:45:43.824949', NULL, 1, 'KP', 'Korea, North', NULL, NULL, 45, 2);
INSERT INTO adm_country VALUES (113, '2013-04-19 18:45:43.824949', NULL, 1, 'KR', 'Korea, South', NULL, NULL, 44, 2);
INSERT INTO adm_country VALUES (114, '2013-04-19 18:45:43.824949', NULL, 1, 'KW', 'Kuwait', NULL, NULL, 85, 2);
INSERT INTO adm_country VALUES (115, '2013-04-19 18:45:43.824949', NULL, 1, 'KY', 'Cayman Islands', NULL, NULL, 32, 2);
INSERT INTO adm_country VALUES (116, '2013-04-19 18:45:43.824949', NULL, 1, 'KZ', 'Kazakhstan', NULL, NULL, 82, 2);
INSERT INTO adm_country VALUES (117, '2013-04-19 18:45:43.824949', NULL, 1, 'LA', 'Laos', NULL, NULL, 86, 2);
INSERT INTO adm_country VALUES (118, '2013-04-19 18:45:43.824949', NULL, 1, 'LB', 'Lebanon', NULL, NULL, 89, 2);
INSERT INTO adm_country VALUES (119, '2013-04-19 18:45:43.824949', NULL, 1, 'LC', 'Saint Lucia', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (120, '2013-04-19 18:45:43.824949', NULL, 1, 'LI', 'Liechtenstein', NULL, NULL, 92, 2);
INSERT INTO adm_country VALUES (121, '2013-04-19 18:45:43.824949', NULL, 1, 'LK', 'Sri Lanka', NULL, NULL, 143, 2);
INSERT INTO adm_country VALUES (122, '2013-04-19 18:45:43.824949', NULL, 1, 'LR', 'Liberia', NULL, NULL, 90, 2);
INSERT INTO adm_country VALUES (123, '2013-04-19 18:45:43.824949', NULL, 1, 'LS', 'Lesotho', NULL, NULL, 87, 2);
INSERT INTO adm_country VALUES (124, '2013-04-19 18:45:43.824949', NULL, 1, 'LT', 'Lithuania', NULL, NULL, 93, 2);
INSERT INTO adm_country VALUES (125, '2013-04-19 18:45:43.824949', NULL, 1, 'LU', 'Luxembourg', NULL, NULL, 5, 12);
INSERT INTO adm_country VALUES (126, '2013-04-19 18:45:43.824949', NULL, 1, 'LV', 'Latvia', NULL, NULL, 88, 2);
INSERT INTO adm_country VALUES (127, '2013-04-19 18:45:43.824949', NULL, 1, 'LY', 'Libya', NULL, NULL, 91, 2);
INSERT INTO adm_country VALUES (128, '2013-04-19 18:45:43.824949', NULL, 1, 'MA', 'Morocco', NULL, NULL, 102, 3);
INSERT INTO adm_country VALUES (129, '2013-04-19 18:45:43.824949', NULL, 1, 'MC', 'Monaco', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (130, '2013-04-19 18:45:43.824949', NULL, 1, 'MD', 'Moldova', NULL, NULL, 106, 2);
INSERT INTO adm_country VALUES (131, '2013-04-19 18:45:43.824949', NULL, 1, 'MG', 'Madagascar', NULL, NULL, 97, 2);
INSERT INTO adm_country VALUES (132, '2013-04-19 18:45:43.824949', NULL, 1, 'MH', 'Marshall Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (133, '2013-04-19 18:45:43.824949', NULL, 1, 'MK', 'Macedonia, The Former Yugoslav R', NULL, NULL, 95, 2);
INSERT INTO adm_country VALUES (134, '2013-04-19 18:45:43.824949', NULL, 1, 'ML', 'Mali', NULL, NULL, 20, 12);
INSERT INTO adm_country VALUES (135, '2013-04-19 18:45:43.824949', NULL, 1, 'MM', 'Burma', NULL, NULL, 109, 2);
INSERT INTO adm_country VALUES (136, '2013-04-19 18:45:43.824949', NULL, 1, 'MN', 'Mongolia', NULL, NULL, 107, 2);
INSERT INTO adm_country VALUES (137, '2013-04-19 18:45:43.824949', NULL, 1, 'MO', 'Macao', NULL, NULL, 94, 2);
INSERT INTO adm_country VALUES (138, '2013-04-19 18:45:43.824949', NULL, 1, 'MP', 'Northern Mariana Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (139, '2013-04-19 18:45:43.824949', NULL, 1, 'MQ', 'Martinique', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (140, '2013-04-19 18:45:43.824949', NULL, 1, 'MR', 'Mauritania', NULL, NULL, 104, 2);
INSERT INTO adm_country VALUES (141, '2013-04-19 18:45:43.824949', NULL, 1, 'MS', 'Montserrat', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (142, '2013-04-19 18:45:43.824949', NULL, 1, 'MT', 'Malta', NULL, NULL, 101, 2);
INSERT INTO adm_country VALUES (143, '2013-04-19 18:45:43.824949', NULL, 1, 'MU', 'Mauritius', NULL, NULL, 103, 2);
INSERT INTO adm_country VALUES (144, '2013-04-19 18:45:43.824949', NULL, 1, 'MV', 'Maldives', NULL, NULL, 100, 2);
INSERT INTO adm_country VALUES (145, '2013-04-19 18:45:43.824949', NULL, 1, 'MW', 'Malawi', NULL, NULL, 99, 2);
INSERT INTO adm_country VALUES (146, '2013-04-19 18:45:43.824949', NULL, 1, 'MX', 'Mexico', NULL, NULL, 105, 2);
INSERT INTO adm_country VALUES (147, '2013-04-19 18:45:43.824949', NULL, 1, 'MY', 'Malaysia', NULL, NULL, 98, 2);
INSERT INTO adm_country VALUES (148, '2013-04-19 18:45:43.824949', NULL, 1, 'MZ', 'Mozambique', NULL, NULL, 108, 2);
INSERT INTO adm_country VALUES (149, '2013-04-19 18:45:43.824949', NULL, 1, 'NA', 'Namibia', NULL, NULL, 110, 2);
INSERT INTO adm_country VALUES (150, '2013-04-19 18:45:43.824949', NULL, 1, 'NC', 'New Caledonia', NULL, NULL, 114, 2);
INSERT INTO adm_country VALUES (151, '2013-04-19 18:45:43.824949', NULL, 1, 'NE', 'Niger', NULL, NULL, 20, 12);
INSERT INTO adm_country VALUES (152, '2013-04-19 18:45:43.824949', NULL, 1, 'NF', 'Norfolk Island', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (153, '2013-04-19 18:45:43.824949', NULL, 1, 'NG', 'Nigeria', NULL, NULL, 113, 2);
INSERT INTO adm_country VALUES (154, '2013-04-19 18:45:43.824949', NULL, 1, 'NI', 'Nicaragua', NULL, NULL, 112, 8);
INSERT INTO adm_country VALUES (155, '2013-04-19 18:45:43.824949', NULL, 1, 'NL', 'Netherlands', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (156, '2013-04-19 18:45:43.824949', NULL, 1, 'NO', 'Norway', NULL, NULL, 31, 2);
INSERT INTO adm_country VALUES (157, '2013-04-19 18:45:43.824949', NULL, 1, 'NP', 'Nepal', NULL, NULL, 111, 2);
INSERT INTO adm_country VALUES (158, '2013-04-19 18:45:43.824949', NULL, 1, 'NR', 'Nauru', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (159, '2013-04-19 18:45:43.824949', NULL, 1, 'NU', 'Niue', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (160, '2013-04-19 18:45:43.824949', NULL, 1, 'NZ', 'New Zealand', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (161, '2013-04-19 18:45:43.824949', NULL, 1, 'OM', 'Oman', NULL, NULL, 115, 2);
INSERT INTO adm_country VALUES (162, '2013-04-19 18:45:43.824949', NULL, 1, 'PA', 'Panama', NULL, NULL, 120, 8);
INSERT INTO adm_country VALUES (163, '2013-04-19 18:45:43.824949', NULL, 1, 'PE', 'Peru', NULL, NULL, 123, 8);
INSERT INTO adm_country VALUES (164, '2013-04-19 18:45:43.824949', NULL, 1, 'PF', 'French Polynesia', NULL, NULL, 114, 12);
INSERT INTO adm_country VALUES (165, '2013-04-19 18:45:43.824949', NULL, 1, 'PG', 'Papua New Guinea', NULL, NULL, 121, 12);
INSERT INTO adm_country VALUES (166, '2013-04-19 18:45:43.824949', NULL, 1, 'PH', 'Philippines', NULL, NULL, 124, 2);
INSERT INTO adm_country VALUES (167, '2013-04-19 18:45:43.824949', NULL, 1, 'PK', 'Pakistan', NULL, NULL, 119, 2);
INSERT INTO adm_country VALUES (168, '2013-04-19 18:45:43.824949', NULL, 1, 'PL', 'Poland', NULL, NULL, 125, 2);
INSERT INTO adm_country VALUES (169, '2013-04-19 18:45:43.824949', NULL, 1, 'PM', 'Saint Pierre and Miquelon', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (170, '2013-04-19 18:45:43.824949', NULL, 1, 'PN', 'Pitcairn Islands', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (171, '2013-04-19 18:45:43.824949', NULL, 1, 'PR', 'Puerto Rico', NULL, NULL, 49, 8);
INSERT INTO adm_country VALUES (172, '2013-04-19 18:45:43.824949', NULL, 1, 'PS', 'Palestinian Territory, Occupied', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (173, '2013-04-19 18:45:43.824949', NULL, 1, 'PT', 'Portugal', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (174, '2013-04-19 18:45:43.824949', NULL, 1, 'PW', 'Palau', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (175, '2013-04-19 18:45:43.824949', NULL, 1, 'PY', 'Paraguay', NULL, NULL, 122, 2);
INSERT INTO adm_country VALUES (176, '2013-04-19 18:45:43.824949', NULL, 1, 'QA', 'Qatar', NULL, NULL, 126, 2);
INSERT INTO adm_country VALUES (177, '2013-04-19 18:45:43.824949', NULL, 1, 'RE', 'RÃ©union', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (178, '2013-04-19 18:45:43.824949', NULL, 1, 'RO', 'Romania', NULL, NULL, 128, 2);
INSERT INTO adm_country VALUES (179, '2013-04-19 18:45:43.824949', NULL, 1, 'RU', 'Russia', NULL, NULL, 129, 2);
INSERT INTO adm_country VALUES (180, '2013-04-19 18:45:43.824949', NULL, 1, 'RW', 'Rwanda', NULL, NULL, 130, 2);
INSERT INTO adm_country VALUES (181, '2013-04-19 18:45:43.824949', NULL, 1, 'SA', 'Saudi Arabia', NULL, NULL, 9, 2);
INSERT INTO adm_country VALUES (182, '2013-04-19 18:45:43.824949', NULL, 1, 'SB', 'Solomon Islands', NULL, NULL, 131, 2);
INSERT INTO adm_country VALUES (183, '2013-04-19 18:45:43.824949', NULL, 1, 'SC', 'Seychelles', NULL, NULL, 136, 2);
INSERT INTO adm_country VALUES (184, '2013-04-19 18:45:43.824949', NULL, 1, 'SD', 'Sudan', NULL, NULL, 21, 2);
INSERT INTO adm_country VALUES (185, '2013-04-19 18:45:43.824949', NULL, 1, 'SE', 'Sweden', NULL, NULL, 145, 2);
INSERT INTO adm_country VALUES (186, '2013-04-19 18:45:43.824949', NULL, 1, 'SG', 'Singapore', NULL, NULL, 138, 2);
INSERT INTO adm_country VALUES (187, '2013-04-19 18:45:43.824949', NULL, 1, 'SH', 'Saint Helena', NULL, NULL, 144, 2);
INSERT INTO adm_country VALUES (188, '2013-04-19 18:45:43.824949', NULL, 1, 'SI', 'Slovenia', NULL, NULL, 140, 2);
INSERT INTO adm_country VALUES (189, '2013-04-19 18:45:43.824949', NULL, 1, 'SJ', 'Svalbard', NULL, NULL, 31, 2);
INSERT INTO adm_country VALUES (190, '2013-04-19 18:45:43.824949', NULL, 1, 'SK', 'Slovakia', NULL, NULL, 139, 2);
INSERT INTO adm_country VALUES (191, '2013-04-19 18:45:43.824949', NULL, 1, 'SL', 'Sierra Leone', NULL, NULL, 137, 2);
INSERT INTO adm_country VALUES (192, '2013-04-19 18:45:43.824949', NULL, 1, 'SM', 'San Marino', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (193, '2013-04-19 18:45:43.824949', NULL, 1, 'SN', 'Senegal', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (194, '2013-04-19 18:45:43.824949', NULL, 1, 'SO', 'Somalia', NULL, NULL, 141, 2);
INSERT INTO adm_country VALUES (195, '2013-04-19 18:45:43.824949', NULL, 1, 'SR', 'Suriname', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (196, '2013-04-19 18:45:43.824949', NULL, 1, 'ST', 'SÃ£o TomÃ© and PrÃ_ncipe', NULL, NULL, 134, 2);
INSERT INTO adm_country VALUES (197, '2013-04-19 18:45:43.824949', NULL, 1, 'SV', 'El Salvador', NULL, NULL, 132, 2);
INSERT INTO adm_country VALUES (198, '2013-04-19 18:45:43.824949', NULL, 1, 'SY', 'Syria', NULL, NULL, 148, 2);
INSERT INTO adm_country VALUES (199, '2013-04-19 18:45:43.824949', NULL, 1, 'SZ', 'Swaziland', NULL, NULL, 147, 2);
INSERT INTO adm_country VALUES (200, '2013-04-19 18:45:43.824949', NULL, 1, 'TC', 'Turks and Caicos Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (201, '2013-04-19 18:45:43.824949', NULL, 1, 'TD', 'Chad', NULL, NULL, 34, 2);
INSERT INTO adm_country VALUES (202, '2013-04-19 18:45:43.824949', NULL, 1, 'TF', 'French Southern and Antarctic La', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (203, '2013-04-19 18:45:43.824949', NULL, 1, 'TG', 'Togo', NULL, NULL, 20, 2);
INSERT INTO adm_country VALUES (204, '2013-04-19 18:45:43.824949', NULL, 1, 'TH', 'Thailand', NULL, NULL, 153, 2);
INSERT INTO adm_country VALUES (205, '2013-04-19 18:45:43.824949', NULL, 1, 'TJ', 'Tajikistan', NULL, NULL, 149, 2);
INSERT INTO adm_country VALUES (206, '2013-04-19 18:45:43.824949', NULL, 1, 'TK', 'Tokelau', NULL, NULL, 72, 2);
INSERT INTO adm_country VALUES (207, '2013-04-19 18:45:43.824949', NULL, 1, 'TM', 'Turkmenistan', NULL, NULL, 157, 2);
INSERT INTO adm_country VALUES (208, '2013-04-19 18:45:43.824949', NULL, 1, 'TN', 'Tunisia', NULL, NULL, 156, 2);
INSERT INTO adm_country VALUES (209, '2013-04-19 18:45:43.824949', NULL, 1, 'TO', 'Tonga', NULL, NULL, 154, 2);
INSERT INTO adm_country VALUES (210, '2013-04-19 18:45:43.824949', NULL, 1, 'TL', 'East timor', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (211, '2013-04-19 18:45:43.824949', NULL, 1, 'TR', 'Turkey', NULL, NULL, 159, 2);
INSERT INTO adm_country VALUES (212, '2013-04-19 18:45:43.824949', NULL, 1, 'TT', 'Trinidad and Tobago', NULL, NULL, 155, 2);
INSERT INTO adm_country VALUES (213, '2013-04-19 18:45:43.824949', NULL, 1, 'TV', 'Tuvalu', NULL, NULL, 13, 2);
INSERT INTO adm_country VALUES (214, '2013-04-19 18:45:43.824949', NULL, 1, 'TW', 'Taiwan', NULL, NULL, 150, 2);
INSERT INTO adm_country VALUES (215, '2013-04-19 18:45:43.824949', NULL, 1, 'TZ', 'Tanzania', NULL, NULL, 151, 2);
INSERT INTO adm_country VALUES (216, '2013-04-19 18:45:43.824949', NULL, 1, 'UA', 'Ukraine', NULL, NULL, 160, 2);
INSERT INTO adm_country VALUES (217, '2013-04-19 18:45:43.824949', NULL, 1, 'UG', 'Uganda', NULL, NULL, 117, 2);
INSERT INTO adm_country VALUES (218, '2013-04-19 18:45:43.824949', NULL, 1, 'GB', 'United Kingdom', NULL, NULL, 65, 2);
INSERT INTO adm_country VALUES (219, '2013-04-19 18:45:43.824949', NULL, 1, 'UM', 'United States Minor Outlying Isl', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (220, '2013-04-19 18:45:43.824949', NULL, 1, 'US', 'United States', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (221, '2013-04-19 18:45:43.824949', NULL, 1, 'UY', 'Uruguay', NULL, NULL, 161, 2);
INSERT INTO adm_country VALUES (222, '2013-04-19 18:45:43.824949', NULL, 1, 'UZ', 'Uzbekistan', NULL, NULL, 118, 2);
INSERT INTO adm_country VALUES (223, '2013-04-19 18:45:43.824949', NULL, 1, 'VA', 'Holy See Vatican City', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (224, '2013-04-19 18:45:43.824949', NULL, 1, 'VC', 'Saint Vincent and the Grenadines', NULL, NULL, 7, 2);
INSERT INTO adm_country VALUES (225, '2013-04-19 18:45:43.824949', NULL, 1, 'VE', 'Venezuela', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (226, '2013-04-19 18:45:43.824949', NULL, 1, 'VG', 'British Virgin Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (227, '2013-04-19 18:45:43.824949', NULL, 1, 'VI', 'Virgin Islands', NULL, NULL, 49, 2);
INSERT INTO adm_country VALUES (228, '2013-04-19 18:45:43.824949', NULL, 1, 'VN', 'Vietnam', NULL, NULL, 164, 2);
INSERT INTO adm_country VALUES (229, '2013-04-19 18:45:43.824949', NULL, 1, 'VU', 'Vanuatu', NULL, NULL, 162, 2);
INSERT INTO adm_country VALUES (230, '2013-04-19 18:45:43.824949', NULL, 1, 'WF', 'Wallis and Futuna', NULL, NULL, 114, 2);
INSERT INTO adm_country VALUES (231, '2013-04-19 18:45:43.824949', NULL, 1, 'WS', 'Samoa', NULL, NULL, 133, 2);
INSERT INTO adm_country VALUES (232, '2013-04-19 18:45:43.824949', NULL, 1, 'YE', 'Yemen', NULL, NULL, 165, 2);
INSERT INTO adm_country VALUES (233, '2013-04-19 18:45:43.824949', NULL, 1, 'YT', 'Mayotte', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (234, '2013-04-19 18:45:43.824949', NULL, 1, 'YU', 'Yugoslavia', NULL, NULL, 5, 2);
INSERT INTO adm_country VALUES (235, '2013-04-19 18:45:43.824949', NULL, 1, 'ZA', 'South Africa', NULL, NULL, 2, 2);
INSERT INTO adm_country VALUES (236, '2013-04-19 18:45:43.824949', NULL, 1, 'ZM', 'Zambia', NULL, NULL, 166, 2);
INSERT INTO adm_country VALUES (237, '2013-04-19 18:45:43.824949', NULL, 1, 'ZW', 'Zimbabwe', NULL, NULL, 167, 2);


--
-- Data for Name: adm_currency; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_currency VALUES (1, '2013-04-19 18:45:43.824949', NULL, 1, 'AFA', 'Afghani', true, NULL, NULL);
INSERT INTO adm_currency VALUES (2, '2013-04-19 18:45:43.824949', NULL, 1, 'ZAR', 'Rand', true, NULL, NULL);
INSERT INTO adm_currency VALUES (3, '2013-04-19 18:45:43.824949', NULL, 1, 'ALL', 'Lek', true, NULL, NULL);
INSERT INTO adm_currency VALUES (4, '2013-04-19 18:45:43.824949', NULL, 1, 'DZD', 'Dinar algérien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (5, '2013-04-19 18:45:43.824949', NULL, 1, 'EUR', 'Euro', true, NULL, NULL);
INSERT INTO adm_currency VALUES (6, '2013-04-19 18:45:43.824949', NULL, 1, 'AOA', 'Kwanza', true, NULL, NULL);
INSERT INTO adm_currency VALUES (7, '2013-04-19 18:45:43.824949', NULL, 1, 'XCD', 'Dollar des Cara bes de lEst', true, NULL, NULL);
INSERT INTO adm_currency VALUES (8, '2013-04-19 18:45:43.824949', NULL, 1, 'ANG', 'Florin des Antilles', true, NULL, NULL);
INSERT INTO adm_currency VALUES (9, '2013-04-19 18:45:43.824949', NULL, 1, 'SAR', 'Riyal saoudien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (10, '2013-04-19 18:45:43.824949', NULL, 1, 'ARS', 'Peso', true, NULL, NULL);
INSERT INTO adm_currency VALUES (11, '2013-04-19 18:45:43.824949', NULL, 1, 'AMD', 'Dram', true, NULL, NULL);
INSERT INTO adm_currency VALUES (12, '2013-04-19 18:45:43.824949', NULL, 1, 'AWG', 'Florin d Aruba', true, NULL, NULL);
INSERT INTO adm_currency VALUES (13, '2013-04-19 18:45:43.824949', NULL, 1, 'AUD', 'Dollar australien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (14, '2013-04-19 18:45:43.824949', NULL, 1, 'AZM', 'Manat azerbaïdjanais', true, NULL, NULL);
INSERT INTO adm_currency VALUES (15, '2013-04-19 18:45:43.824949', NULL, 1, 'BSD', 'Dollar des Bahamas', true, NULL, NULL);
INSERT INTO adm_currency VALUES (16, '2013-04-19 18:45:43.824949', NULL, 1, 'BHD', 'Dinar de Bahreïn', true, NULL, NULL);
INSERT INTO adm_currency VALUES (17, '2013-04-19 18:45:43.824949', NULL, 1, 'BDT', 'Taka', true, NULL, NULL);
INSERT INTO adm_currency VALUES (18, '2013-04-19 18:45:43.824949', NULL, 1, 'BBD', 'Dollar de Barbade', true, NULL, NULL);
INSERT INTO adm_currency VALUES (19, '2013-04-19 18:45:43.824949', NULL, 1, 'BZD', 'Dollar de Belize', true, NULL, NULL);
INSERT INTO adm_currency VALUES (20, '2013-04-19 18:45:43.824949', NULL, 1, 'XOF', 'Franc CFA - BCEAO', true, NULL, NULL);
INSERT INTO adm_currency VALUES (21, '2013-04-19 18:45:43.824949', NULL, 1, 'BMD', 'Dollar des Bermudes', true, NULL, NULL);
INSERT INTO adm_currency VALUES (22, '2013-04-19 18:45:43.824949', NULL, 1, 'BTN', 'Ngultrum', true, NULL, NULL);
INSERT INTO adm_currency VALUES (23, '2013-04-19 18:45:43.824949', NULL, 1, 'BYR', 'Rouble biãlorussie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (24, '2013-04-19 18:45:43.824949', NULL, 1, 'BOB', 'Boliviano', true, NULL, NULL);
INSERT INTO adm_currency VALUES (25, '2013-04-19 18:45:43.824949', NULL, 1, 'BAM', 'Mark bosniaque convertible', true, NULL, NULL);
INSERT INTO adm_currency VALUES (26, '2013-04-19 18:45:43.824949', NULL, 1, 'BWP', 'Pula', true, NULL, NULL);
INSERT INTO adm_currency VALUES (27, '2013-04-19 18:45:43.824949', NULL, 1, 'BRL', 'Real', true, NULL, NULL);
INSERT INTO adm_currency VALUES (28, '2013-04-19 18:45:43.824949', NULL, 1, 'BND', 'Dollar de Brunéi', true, NULL, NULL);
INSERT INTO adm_currency VALUES (29, '2013-04-19 18:45:43.824949', NULL, 1, 'BGN', 'Lev', true, NULL, NULL);
INSERT INTO adm_currency VALUES (30, '2013-04-19 18:45:43.824949', NULL, 1, 'BIF', 'Franc du Burundi', true, NULL, NULL);
INSERT INTO adm_currency VALUES (31, '2013-04-19 18:45:43.824949', NULL, 1, 'NOK', 'Couronne norvégienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (32, '2013-04-19 18:45:43.824949', NULL, 1, 'KYD', 'Dollar des îles Caïmanes', true, NULL, NULL);
INSERT INTO adm_currency VALUES (33, '2013-04-19 18:45:43.824949', NULL, 1, 'KHR', 'Riel', true, NULL, NULL);
INSERT INTO adm_currency VALUES (34, '2013-04-19 18:45:43.824949', NULL, 1, 'XAF', 'Franc CFA - BEAC', true, NULL, NULL);
INSERT INTO adm_currency VALUES (35, '2013-04-19 18:45:43.824949', NULL, 1, 'CAD', 'Dollar canadien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (36, '2013-04-19 18:45:43.824949', NULL, 1, 'CVE', 'Escudo du Cap-Vert', true, NULL, NULL);
INSERT INTO adm_currency VALUES (37, '2013-04-19 18:45:43.824949', NULL, 1, 'CFA', 'FRANC CFA-BEAC', true, NULL, NULL);
INSERT INTO adm_currency VALUES (38, '2013-04-19 18:45:43.824949', NULL, 1, 'CLP', 'Peso chilien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (39, '2013-04-19 18:45:43.824949', NULL, 1, 'CNY', 'Yuan Ren-Min-Bi', true, NULL, NULL);
INSERT INTO adm_currency VALUES (40, '2013-04-19 18:45:43.824949', NULL, 1, 'CYP', 'Livre chypriote', true, NULL, NULL);
INSERT INTO adm_currency VALUES (41, '2013-04-19 18:45:43.824949', NULL, 1, 'COP', 'Peso colombien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (42, '2013-04-19 18:45:43.824949', NULL, 1, 'KMF', 'Franc des Comores', true, NULL, NULL);
INSERT INTO adm_currency VALUES (43, '2013-04-19 18:45:43.824949', NULL, 1, 'CDF', 'FRANC DU CONGO DEMOCRATIQUE', true, NULL, NULL);
INSERT INTO adm_currency VALUES (44, '2013-04-19 18:45:43.824949', NULL, 1, 'KRW', 'Won', true, NULL, NULL);
INSERT INTO adm_currency VALUES (45, '2013-04-19 18:45:43.824949', NULL, 1, 'KPW', 'Won de la Corée du Nord', true, NULL, NULL);
INSERT INTO adm_currency VALUES (46, '2013-04-19 18:45:43.824949', NULL, 1, 'CRC', 'Colon de Costa Rica', true, NULL, NULL);
INSERT INTO adm_currency VALUES (47, '2013-04-19 18:45:43.824949', NULL, 1, 'HRK', 'Kuna', true, NULL, NULL);
INSERT INTO adm_currency VALUES (48, '2013-04-19 18:45:43.824949', NULL, 1, 'CUP', 'Peso cubain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (49, '2013-04-19 18:45:43.824949', NULL, 1, 'USD', 'Dollar des Etats-unis', true, NULL, NULL);
INSERT INTO adm_currency VALUES (50, '2013-04-19 18:45:43.824949', NULL, 1, 'DKK', 'Couronne danoise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (51, '2013-04-19 18:45:43.824949', NULL, 1, 'DJF', 'Franc de Djibouti', true, NULL, NULL);
INSERT INTO adm_currency VALUES (52, '2013-04-19 18:45:43.824949', NULL, 1, 'DOP', 'Peso dominicain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (53, '2013-04-19 18:45:43.824949', NULL, 1, 'EGP', 'Livre égyptienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (54, '2013-04-19 18:45:43.824949', NULL, 1, 'AED', 'Dirham des émirats arabes unis', true, NULL, NULL);
INSERT INTO adm_currency VALUES (55, '2013-04-19 18:45:43.824949', NULL, 1, 'ERN', 'Nafka', true, NULL, NULL);
INSERT INTO adm_currency VALUES (56, '2013-04-19 18:45:43.824949', NULL, 1, 'EEK', 'Couronne d Estonie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (57, '2013-04-19 18:45:43.824949', NULL, 1, 'ETB', 'Birr éthiopien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (58, '2013-04-19 18:45:43.824949', NULL, 1, 'FKP', 'Livre de Falkland', true, NULL, NULL);
INSERT INTO adm_currency VALUES (59, '2013-04-19 18:45:43.824949', NULL, 1, 'FJD', 'Dollar des Fidji', true, NULL, NULL);
INSERT INTO adm_currency VALUES (60, '2013-04-19 18:45:43.824949', NULL, 1, 'GMD', 'Dalasie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (61, '2013-04-19 18:45:43.824949', NULL, 1, 'GEL', 'Lari', true, NULL, NULL);
INSERT INTO adm_currency VALUES (62, '2013-04-19 18:45:43.824949', NULL, 1, 'GHC', 'Cedi ghanéen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (63, '2013-04-19 18:45:43.824949', NULL, 1, 'GIP', 'Livre de Gibraltar', true, NULL, NULL);
INSERT INTO adm_currency VALUES (64, '2013-04-19 18:45:43.824949', NULL, 1, 'GTQ', 'Quetzal', true, NULL, NULL);
INSERT INTO adm_currency VALUES (65, '2013-04-19 18:45:43.824949', NULL, 1, 'GBP', 'Livre sterling', true, NULL, NULL);
INSERT INTO adm_currency VALUES (66, '2013-04-19 18:45:43.824949', NULL, 1, 'GNF', 'Franc guinéen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (67, '2013-04-19 18:45:43.824949', NULL, 1, 'GYD', 'Dollar de Guyane', true, NULL, NULL);
INSERT INTO adm_currency VALUES (68, '2013-04-19 18:45:43.824949', NULL, 1, 'HTG', 'Gourde', true, NULL, NULL);
INSERT INTO adm_currency VALUES (69, '2013-04-19 18:45:43.824949', NULL, 1, 'HNL', 'Lempira', true, NULL, NULL);
INSERT INTO adm_currency VALUES (70, '2013-04-19 18:45:43.824949', NULL, 1, 'HKD', 'Dollar de Hong-Kong', true, NULL, NULL);
INSERT INTO adm_currency VALUES (71, '2013-04-19 18:45:43.824949', NULL, 1, 'HUF', 'Forint', true, NULL, NULL);
INSERT INTO adm_currency VALUES (72, '2013-04-19 18:45:43.824949', NULL, 1, 'NZD', 'Dollar néo-zélandais', true, NULL, NULL);
INSERT INTO adm_currency VALUES (73, '2013-04-19 18:45:43.824949', NULL, 1, 'INR', 'Roupie indienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (74, '2013-04-19 18:45:43.824949', NULL, 1, 'IDR', 'Rupiah', true, NULL, NULL);
INSERT INTO adm_currency VALUES (75, '2013-04-19 18:45:43.824949', NULL, 1, 'IRR', 'Rial iranien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (76, '2013-04-19 18:45:43.824949', NULL, 1, 'IQD', 'Dinar iraquien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (77, '2013-04-19 18:45:43.824949', NULL, 1, 'ISK', 'Couronne islandaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (78, '2013-04-19 18:45:43.824949', NULL, 1, 'ILS', 'Sheqel', true, NULL, NULL);
INSERT INTO adm_currency VALUES (79, '2013-04-19 18:45:43.824949', NULL, 1, 'JMD', 'Dollar jamaïcain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (80, '2013-04-19 18:45:43.824949', NULL, 1, 'JPY', 'Yen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (81, '2013-04-19 18:45:43.824949', NULL, 1, 'JOD', 'Dinar jordanien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (82, '2013-04-19 18:45:43.824949', NULL, 1, 'KZT', 'Tenge', true, NULL, NULL);
INSERT INTO adm_currency VALUES (83, '2013-04-19 18:45:43.824949', NULL, 1, 'KES', 'Shilling du Kenya', true, NULL, NULL);
INSERT INTO adm_currency VALUES (84, '2013-04-19 18:45:43.824949', NULL, 1, 'KGS', 'Som', true, NULL, NULL);
INSERT INTO adm_currency VALUES (85, '2013-04-19 18:45:43.824949', NULL, 1, 'KWD', 'Dinar koweïtien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (86, '2013-04-19 18:45:43.824949', NULL, 1, 'LAK', 'Kip', true, NULL, NULL);
INSERT INTO adm_currency VALUES (87, '2013-04-19 18:45:43.824949', NULL, 1, 'LSL', 'Loti', true, NULL, NULL);
INSERT INTO adm_currency VALUES (88, '2013-04-19 18:45:43.824949', NULL, 1, 'LVL', 'Lats letton', true, NULL, NULL);
INSERT INTO adm_currency VALUES (89, '2013-04-19 18:45:43.824949', NULL, 1, 'LBP', 'Livre libanaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (90, '2013-04-19 18:45:43.824949', NULL, 1, 'LRD', 'Dollar libérien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (91, '2013-04-19 18:45:43.824949', NULL, 1, 'LYD', 'Dinar libyen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (92, '2013-04-19 18:45:43.824949', NULL, 1, 'CHF', 'Franc suisse', true, NULL, NULL);
INSERT INTO adm_currency VALUES (93, '2013-04-19 18:45:43.824949', NULL, 1, 'LTL', 'Litas lituanien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (94, '2013-04-19 18:45:43.824949', NULL, 1, 'MOP', 'Pataca', true, NULL, NULL);
INSERT INTO adm_currency VALUES (95, '2013-04-19 18:45:43.824949', NULL, 1, 'MKD', 'Denar', true, NULL, NULL);
INSERT INTO adm_currency VALUES (96, '2013-04-19 18:45:43.824949', NULL, 1, 'MGA', 'Ariary malgache', true, NULL, NULL);
INSERT INTO adm_currency VALUES (97, '2013-04-19 18:45:43.824949', NULL, 1, 'MGF', 'Franc malgache', true, NULL, NULL);
INSERT INTO adm_currency VALUES (98, '2013-04-19 18:45:43.824949', NULL, 1, 'MYR', 'Ringgit de Malaisie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (99, '2013-04-19 18:45:43.824949', NULL, 1, 'MWK', 'Kwacha', true, NULL, NULL);
INSERT INTO adm_currency VALUES (100, '2013-04-19 18:45:43.824949', NULL, 1, 'MVR', 'Rufiyaa', true, NULL, NULL);
INSERT INTO adm_currency VALUES (101, '2013-04-19 18:45:43.824949', NULL, 1, 'MTL', 'Livre maltaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (102, '2013-04-19 18:45:43.824949', NULL, 1, 'MAD', 'Dirham marocain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (103, '2013-04-19 18:45:43.824949', NULL, 1, 'MUR', 'Roupie mauricienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (104, '2013-04-19 18:45:43.824949', NULL, 1, 'MRO', 'Ouguija', true, NULL, NULL);
INSERT INTO adm_currency VALUES (105, '2013-04-19 18:45:43.824949', NULL, 1, 'MXN', 'Peso mexicain', true, NULL, NULL);
INSERT INTO adm_currency VALUES (106, '2013-04-19 18:45:43.824949', NULL, 1, 'MDL', 'Leu de Moldave', true, NULL, NULL);
INSERT INTO adm_currency VALUES (107, '2013-04-19 18:45:43.824949', NULL, 1, 'MNT', 'Tugrik', true, NULL, NULL);
INSERT INTO adm_currency VALUES (108, '2013-04-19 18:45:43.824949', NULL, 1, 'MZM', 'Metical', true, NULL, NULL);
INSERT INTO adm_currency VALUES (109, '2013-04-19 18:45:43.824949', NULL, 1, 'MMK', 'Kyat', true, NULL, NULL);
INSERT INTO adm_currency VALUES (110, '2013-04-19 18:45:43.824949', NULL, 1, 'NAD', 'Dollar namibien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (111, '2013-04-19 18:45:43.824949', NULL, 1, 'NPR', 'Roupie Népalaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (112, '2013-04-19 18:45:43.824949', NULL, 1, 'NIO', 'Cordoba oro', true, NULL, NULL);
INSERT INTO adm_currency VALUES (113, '2013-04-19 18:45:43.824949', NULL, 1, 'NGN', 'Naira', true, NULL, NULL);
INSERT INTO adm_currency VALUES (114, '2013-04-19 18:45:43.824949', NULL, 1, 'XPF', 'Franc CFP', true, NULL, NULL);
INSERT INTO adm_currency VALUES (115, '2013-04-19 18:45:43.824949', NULL, 1, 'OMR', 'Rial Omani', true, NULL, NULL);
INSERT INTO adm_currency VALUES (116, '2013-04-19 18:45:43.824949', NULL, 1, 'XAU', 'Opérations sur or', true, NULL, NULL);
INSERT INTO adm_currency VALUES (117, '2013-04-19 18:45:43.824949', NULL, 1, 'UGX', 'Shilling ougandais', true, NULL, NULL);
INSERT INTO adm_currency VALUES (118, '2013-04-19 18:45:43.824949', NULL, 1, 'UZS', 'Soum ouzbek', true, NULL, NULL);
INSERT INTO adm_currency VALUES (119, '2013-04-19 18:45:43.824949', NULL, 1, 'PKR', 'Roupie pakistanaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (120, '2013-04-19 18:45:43.824949', NULL, 1, 'PAB', 'Balboa', true, NULL, NULL);
INSERT INTO adm_currency VALUES (121, '2013-04-19 18:45:43.824949', NULL, 1, 'PGK', 'Kina', true, NULL, NULL);
INSERT INTO adm_currency VALUES (122, '2013-04-19 18:45:43.824949', NULL, 1, 'PYG', 'Guarani', true, NULL, NULL);
INSERT INTO adm_currency VALUES (123, '2013-04-19 18:45:43.824949', NULL, 1, 'PEN', 'Nouveau sol', true, NULL, NULL);
INSERT INTO adm_currency VALUES (124, '2013-04-19 18:45:43.824949', NULL, 1, 'PHP', 'Peso philippin', true, NULL, NULL);
INSERT INTO adm_currency VALUES (125, '2013-04-19 18:45:43.824949', NULL, 1, 'PLN', 'Zloty', true, NULL, NULL);
INSERT INTO adm_currency VALUES (126, '2013-04-19 18:45:43.824949', NULL, 1, 'QAR', 'Riyal du Qatar', true, NULL, NULL);
INSERT INTO adm_currency VALUES (127, '2013-04-19 18:45:43.824949', NULL, 1, 'RON', 'LEI (Nouveau Leu)', true, NULL, NULL);
INSERT INTO adm_currency VALUES (128, '2013-04-19 18:45:43.824949', NULL, 1, 'ROL', 'Leu', true, NULL, NULL);
INSERT INTO adm_currency VALUES (129, '2013-04-19 18:45:43.824949', NULL, 1, 'RUB', 'Rouble russe (nouveau)', true, NULL, NULL);
INSERT INTO adm_currency VALUES (130, '2013-04-19 18:45:43.824949', NULL, 1, 'RWF', 'Franc du Rwanda', true, NULL, NULL);
INSERT INTO adm_currency VALUES (131, '2013-04-19 18:45:43.824949', NULL, 1, 'SBD', 'Dollar des îles Salomon', true, NULL, NULL);
INSERT INTO adm_currency VALUES (132, '2013-04-19 18:45:43.824949', NULL, 1, 'SVC', 'Colon salvadorien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (133, '2013-04-19 18:45:43.824949', NULL, 1, 'WST', 'Tala', true, NULL, NULL);
INSERT INTO adm_currency VALUES (134, '2013-04-19 18:45:43.824949', NULL, 1, 'STD', 'Dobra', true, NULL, NULL);
INSERT INTO adm_currency VALUES (135, '2013-04-19 18:45:43.824949', NULL, 1, 'CSD', 'Dinar Serbe', true, NULL, NULL);
INSERT INTO adm_currency VALUES (136, '2013-04-19 18:45:43.824949', NULL, 1, 'SCR', 'Roupie des Seychelles', true, NULL, NULL);
INSERT INTO adm_currency VALUES (137, '2013-04-19 18:45:43.824949', NULL, 1, 'SLL', 'Leone', true, NULL, NULL);
INSERT INTO adm_currency VALUES (138, '2013-04-19 18:45:43.824949', NULL, 1, 'SGD', 'Dollar de Singapour', true, NULL, NULL);
INSERT INTO adm_currency VALUES (139, '2013-04-19 18:45:43.824949', NULL, 1, 'SKK', 'Couronne slovaque', true, NULL, NULL);
INSERT INTO adm_currency VALUES (140, '2013-04-19 18:45:43.824949', NULL, 1, 'SIT', 'Tolar', true, NULL, NULL);
INSERT INTO adm_currency VALUES (141, '2013-04-19 18:45:43.824949', NULL, 1, 'SOS', 'Shilling Somalien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (142, '2013-04-19 18:45:43.824949', NULL, 1, 'SDG', 'Livre soudanaise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (143, '2013-04-19 18:45:43.824949', NULL, 1, 'LKR', 'Roupie de Sri Lanka', true, NULL, NULL);
INSERT INTO adm_currency VALUES (144, '2013-04-19 18:45:43.824949', NULL, 1, 'SHP', 'Livre de Sainte-Hélène', true, NULL, NULL);
INSERT INTO adm_currency VALUES (145, '2013-04-19 18:45:43.824949', NULL, 1, 'SEK', 'Couronne suédoise', true, NULL, NULL);
INSERT INTO adm_currency VALUES (146, '2013-04-19 18:45:43.824949', NULL, 1, 'SRD', 'Florin du suriname', true, NULL, NULL);
INSERT INTO adm_currency VALUES (147, '2013-04-19 18:45:43.824949', NULL, 1, 'SZL', 'Lilangeni', true, NULL, NULL);
INSERT INTO adm_currency VALUES (148, '2013-04-19 18:45:43.824949', NULL, 1, 'SYP', 'Livre syrienne', true, NULL, NULL);
INSERT INTO adm_currency VALUES (149, '2013-04-19 18:45:43.824949', NULL, 1, 'TJS', 'Somoni', true, NULL, NULL);
INSERT INTO adm_currency VALUES (150, '2013-04-19 18:45:43.824949', NULL, 1, 'TWD', 'Nouveau dollar de Taïwan', true, NULL, NULL);
INSERT INTO adm_currency VALUES (151, '2013-04-19 18:45:43.824949', NULL, 1, 'TZS', 'Shilling tanzanien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (152, '2013-04-19 18:45:43.824949', NULL, 1, 'CZK', 'Couronne tchèque', true, NULL, NULL);
INSERT INTO adm_currency VALUES (153, '2013-04-19 18:45:43.824949', NULL, 1, 'THB', 'Baht', true, NULL, NULL);
INSERT INTO adm_currency VALUES (154, '2013-04-19 18:45:43.824949', NULL, 1, 'TOP', 'Pa anga', true, NULL, NULL);
INSERT INTO adm_currency VALUES (155, '2013-04-19 18:45:43.824949', NULL, 1, 'TTD', 'Dollar de Trinité et de Tobago', true, NULL, NULL);
INSERT INTO adm_currency VALUES (156, '2013-04-19 18:45:43.824949', NULL, 1, 'TND', 'Dinar tunisien', true, NULL, NULL);
INSERT INTO adm_currency VALUES (157, '2013-04-19 18:45:43.824949', NULL, 1, 'TMM', 'Manat turkmène', true, NULL, NULL);
INSERT INTO adm_currency VALUES (158, '2013-04-19 18:45:43.824949', NULL, 1, 'TRY', 'Nouvelle Livre turque', true, NULL, NULL);
INSERT INTO adm_currency VALUES (159, '2013-04-19 18:45:43.824949', NULL, 1, 'TRL', 'Livre turque', true, NULL, NULL);
INSERT INTO adm_currency VALUES (160, '2013-04-19 18:45:43.824949', NULL, 1, 'UAH', 'HRYVNIA', true, NULL, NULL);
INSERT INTO adm_currency VALUES (161, '2013-04-19 18:45:43.824949', NULL, 1, 'UYU', 'Nouveau Peso uruguayen', true, NULL, NULL);
INSERT INTO adm_currency VALUES (162, '2013-04-19 18:45:43.824949', NULL, 1, 'VUV', 'Vatu', true, NULL, NULL);
INSERT INTO adm_currency VALUES (163, '2013-04-19 18:45:43.824949', NULL, 1, 'VEF', 'Bolivar Fuerte', true, NULL, NULL);
INSERT INTO adm_currency VALUES (164, '2013-04-19 18:45:43.824949', NULL, 1, 'VND', 'Dong', true, NULL, NULL);
INSERT INTO adm_currency VALUES (165, '2013-04-19 18:45:43.824949', NULL, 1, 'YER', 'Riyal yéménite', true, NULL, NULL);
INSERT INTO adm_currency VALUES (166, '2013-04-19 18:45:43.824949', NULL, 1, 'ZMK', 'Kwacha de Zambie', true, NULL, NULL);
INSERT INTO adm_currency VALUES (167, '2013-04-19 18:45:43.824949', NULL, 1, 'ZWD', 'Dollar du Zimbabwe', true, NULL, NULL);
INSERT INTO adm_currency VALUES (168, '2013-04-19 18:45:43.824949', NULL, 1, 'GHS', 'Cedi ghanéen', true, NULL, NULL);


--
-- Name: adm_currency_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_currency_seq', 1, false);


--
-- Data for Name: adm_language; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_language VALUES (1, '2013-04-19 18:45:43.824949', NULL, 1, 'allemand', 'DEU', NULL, NULL);
INSERT INTO adm_language VALUES (2, '2013-04-19 18:45:43.824949', NULL, 1, 'anglais', 'ENG', NULL, NULL);
INSERT INTO adm_language VALUES (3, '2013-04-19 18:45:43.824949', NULL, 1, 'arabe', 'ARA', NULL, NULL);
INSERT INTO adm_language VALUES (4, '2013-04-19 18:45:43.824949', NULL, 1, 'bulgare', 'BUL', NULL, NULL);
INSERT INTO adm_language VALUES (5, '2013-04-19 18:45:43.824949', NULL, 1, 'catalan', 'CAT', NULL, NULL);
INSERT INTO adm_language VALUES (6, '2013-04-19 18:45:43.824949', NULL, 1, 'chinois', 'ZHO', NULL, NULL);
INSERT INTO adm_language VALUES (7, '2013-04-19 18:45:43.824949', NULL, 1, 'danois', 'DAN', NULL, NULL);
INSERT INTO adm_language VALUES (8, '2013-04-19 18:45:43.824949', NULL, 1, 'espagnol', 'ESL', NULL, NULL);
INSERT INTO adm_language VALUES (9, '2013-04-19 18:45:43.824949', NULL, 1, 'estonien', 'EST', NULL, NULL);
INSERT INTO adm_language VALUES (10, '2013-04-19 18:45:43.824949', NULL, 1, 'féroïen', 'FAO', NULL, NULL);
INSERT INTO adm_language VALUES (11, '2013-04-19 18:45:43.824949', NULL, 1, 'finlandais', 'FIN', NULL, NULL);
INSERT INTO adm_language VALUES (12, '2013-04-19 18:45:43.824949', NULL, 1, 'français', 'FRA', NULL, NULL);
INSERT INTO adm_language VALUES (13, '2013-04-19 18:45:43.824949', NULL, 1, 'grec', 'ELL', NULL, NULL);
INSERT INTO adm_language VALUES (14, '2013-04-19 18:45:43.824949', NULL, 1, 'hindi', 'HIN', NULL, NULL);
INSERT INTO adm_language VALUES (15, '2013-04-19 18:45:43.824949', NULL, 1, 'hongrois', 'HUN', NULL, NULL);
INSERT INTO adm_language VALUES (16, '2013-04-19 18:45:43.824949', NULL, 1, 'islandais', 'ISL', NULL, NULL);
INSERT INTO adm_language VALUES (17, '2013-04-19 18:45:43.824949', NULL, 1, 'italien', 'ITA', NULL, NULL);
INSERT INTO adm_language VALUES (18, '2013-04-19 18:45:43.824949', NULL, 1, 'japonais', 'JPN', NULL, NULL);
INSERT INTO adm_language VALUES (19, '2013-04-19 18:45:43.824949', NULL, 1, 'letton', 'LAV', NULL, NULL);
INSERT INTO adm_language VALUES (20, '2013-04-19 18:45:43.824949', NULL, 1, 'lituanien', 'LIT', NULL, NULL);
INSERT INTO adm_language VALUES (21, '2013-04-19 18:45:43.824949', NULL, 1, 'néerlandais', 'NLD', NULL, NULL);
INSERT INTO adm_language VALUES (22, '2013-04-19 18:45:43.824949', NULL, 1, 'norvégien', 'NOR', NULL, NULL);
INSERT INTO adm_language VALUES (23, '2013-04-19 18:45:43.824949', NULL, 1, 'polonais', 'POL', NULL, NULL);
INSERT INTO adm_language VALUES (24, '2013-04-19 18:45:43.824949', NULL, 1, 'portugais', 'POR', NULL, NULL);
INSERT INTO adm_language VALUES (25, '2013-04-19 18:45:43.824949', NULL, 1, 'roumain', 'RON', NULL, NULL);
INSERT INTO adm_language VALUES (26, '2013-04-19 18:45:43.824949', NULL, 1, 'russe', 'RUS', NULL, NULL);
INSERT INTO adm_language VALUES (27, '2013-04-19 18:45:43.824949', NULL, 1, 'serbe', 'SRP', NULL, NULL);
INSERT INTO adm_language VALUES (28, '2013-04-19 18:45:43.824949', NULL, 1, 'slovaque', 'SLK', NULL, NULL);
INSERT INTO adm_language VALUES (29, '2013-04-19 18:45:43.824949', NULL, 1, 'slovène', 'SLV', NULL, NULL);
INSERT INTO adm_language VALUES (30, '2013-04-19 18:45:43.824949', NULL, 1, 'suédois', 'SVE', NULL, NULL);
INSERT INTO adm_language VALUES (31, '2013-04-19 18:45:43.824949', NULL, 1, 'tchèque', 'CES', NULL, NULL);
INSERT INTO adm_language VALUES (32, '2013-04-19 18:45:43.824949', NULL, 1, 'turc', 'TUR', NULL, NULL);


--
-- Data for Name: adm_title; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_title VALUES (1, 0, false, '2013-04-19 18:45:43.824949', NULL, 'AGCE', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (2, 0, false, '2013-04-19 18:45:43.824949', NULL, 'ASSO', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (3, 0, false, '2013-04-19 18:45:43.824949', NULL, 'CAB', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (4, 0, false, '2013-04-19 18:45:43.824949', NULL, 'COLL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (5, 0, false, '2013-04-19 18:45:43.824949', NULL, 'COM', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (6, 0, false, '2013-04-19 18:45:43.824949', NULL, 'COPR', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (7, 0, false, '2013-04-19 18:45:43.824949', NULL, 'CSSE', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (8, 0, false, '2013-04-19 18:45:43.824949', NULL, 'EARL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (9, 0, false, '2013-04-19 18:45:43.824949', NULL, 'ETS', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (10, 0, false, '2013-04-19 18:45:43.824949', NULL, 'EURL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (11, 0, false, '2013-04-19 18:45:43.824949', NULL, 'GAEC', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (12, 0, false, '2013-04-19 18:45:43.824949', NULL, 'HLM', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (13, 0, false, '2013-04-19 18:45:43.824949', NULL, 'HOPI', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (14, 0, false, '2013-04-19 18:45:43.824949', NULL, 'INST', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (15, 0, false, '2013-04-19 18:45:43.824949', NULL, 'LABO', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (16, 0, false, '2013-04-19 18:45:43.824949', NULL, 'M', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (17, 0, false, '2013-04-19 18:45:43.824949', NULL, 'MLLES', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (18, 0, false, '2013-04-19 18:45:43.824949', NULL, 'MLLE_M', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (19, 0, false, '2013-04-19 18:45:43.824949', NULL, 'MLLE', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (20, 0, false, '2013-04-19 18:45:43.824949', NULL, 'MM', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (21, 0, false, '2013-04-19 18:45:43.824949', NULL, 'MME', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (22, 0, false, '2013-04-19 18:45:43.824949', NULL, 'MME_M', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (23, 0, false, '2013-04-19 18:45:43.824949', NULL, 'MMES', false, 1, NULL, NULL);
INSERT INTO adm_title VALUES (24, 0, false, '2013-04-19 18:45:43.824949', NULL, 'MTRE', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (25, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SA', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (26, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SARL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (27, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SCEA', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (28, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SCI', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (29, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SCM', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (30, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SCP', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (31, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SELARL', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (32, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SNC', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (33, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SNI', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (34, 0, false, '2013-04-19 18:45:43.824949', NULL, 'STE', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (35, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SUCC', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (36, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SYNDIC', true, 1, NULL, NULL);
INSERT INTO adm_title VALUES (37, 0, false, '2013-04-19 18:45:43.824949', NULL, 'SYNDCOP', true, 1, NULL, NULL);


--
-- Data for Name: adm_user; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_user VALUES (1, 0, false, '2013-04-19 18:45:43.824949', NULL, '2014-04-19', NULL, NULL, 'fb93a3221422999ba49eb103977a6d736376505b', 'MEVEO.ADMIN', 1, 1, NULL, NULL);
INSERT INTO adm_user VALUES (6, 0, false, '2013-04-19 18:45:43.824949', NULL, '2014-04-19', NULL, NULL, 'fb93a3221422999ba49eb103977a6d736376505b', 'MEVEO.SUPERADMIN', 1, 1, NULL, NULL);


--
-- Data for Name: ar_customer_account; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO ar_customer_account VALUES ('', NULL, '', '', 'PART_M', '2013-04-12 10:49:14.295', NULL, 'R0', 'OtKqFUph', 'CHECK', 'ACTIVE', 125, NULL, NULL);


--
-- Data for Name: billing_billing_account; Type: TABLE DATA; Schema: public; Owner: meveo
--



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


--
-- Data for Name: billing_trading_currency; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_trading_currency VALUES (146, 2, false, '2013-04-12 12:02:59.77', '2013-04-17 14:49:18.052', 0.266600000000, 'Riyal Saoudien', 1, 1, 1, 9);
INSERT INTO billing_trading_currency VALUES (145, 1, false, '2013-04-12 12:01:16.438', '2013-04-17 14:49:33.718', 1.313600000000, 'Euro', 1, 1, 1, 5);
INSERT INTO billing_trading_currency VALUES (232, 2, false, '2013-04-16 16:12:50.03', '2013-04-17 14:51:25.339', 0.272300000000, 'Dirham des Ã©mirats unis', 1, 1, 1, 54);
INSERT INTO billing_trading_currency VALUES (149, 1, false, '2013-04-12 12:09:54.989', '2013-04-17 14:52:29.742', 2.652100000000, 'Bahrein Dinar', 1, 1, 1, 16);
INSERT INTO billing_trading_currency VALUES (218, 5, false, '2013-04-16 11:39:48.855', '2013-04-17 14:53:50.639', 3.514200000000, 'Dinar Koweitien', 1, 1, 1, 85);
INSERT INTO billing_trading_currency VALUES (237, 0, false, '2013-04-17 14:54:42.397', NULL, 0.558200000000, 'Livre turque', 1, 1, NULL, 158);
INSERT INTO billing_trading_currency VALUES (238, 0, false, '2013-04-17 14:56:22.139', NULL, 0.274600000000, 'Riyal du Qatar', 1, 1, NULL, 126);


--
-- Data for Name: billing_trading_language; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_trading_language VALUES (139, 1, false, '2013-04-12 11:19:50.57', '2013-04-12 11:22:07.895', 'Arabic ', 1, 1, 1, 3);
INSERT INTO billing_trading_language VALUES (141, 0, false, '2013-04-12 11:25:44.671', NULL, 'French', 1, 1, NULL, 12);
INSERT INTO billing_trading_language VALUES (138, 1, false, '2013-04-12 11:10:03.388', '2013-04-12 11:29:02.661', 'Turkish ', 1, 1, 1, 32);
INSERT INTO billing_trading_language VALUES (244, 0, false, '2013-04-17 15:27:34.349', NULL, 'German', 1, 1, NULL, 1);


--
-- Data for Name: billing_user_account; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: crm_customer; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: crm_provider; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO crm_provider VALUES (1, 1, false, '2013-04-19 18:45:43.824949', NULL, 'ASG', NULL, false, false, '33333333333', 'owner', 'SGMB', '11', 'SGMB', '11', '12345', '11', 'PROV1', '1111', '11', NULL, NULL, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, false, NULL, true, true, true, NULL, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL, 1, NULL);


--
-- Data for Name: adm_input_history; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: adm_input_history_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_input_history_seq', 1, false);


--
-- Data for Name: adm_medina_configuration; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: adm_medina_configuration_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_medina_configuration_seq', 1, false);


--
-- Data for Name: adm_messages; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_messages VALUES (246, 0, false, '2013-04-17 15:31:26.647', NULL, 'Abonnement logiciel (Turque)', 'TUR', 'InvoiceCategory_245', 1, 1, NULL);
INSERT INTO adm_messages VALUES (247, 0, false, '2013-04-17 15:31:26.649', NULL, 'Abonnement logiciel (Arabe)', 'ARA', 'InvoiceCategory_245', 1, 1, NULL);
INSERT INTO adm_messages VALUES (248, 0, false, '2013-04-17 15:31:26.652', NULL, 'Abonnement logiciel (FranÃ§ais)', 'FRA', 'InvoiceCategory_245', 1, 1, NULL);
INSERT INTO adm_messages VALUES (251, 1, false, '2013-04-17 15:32:43.513', '2013-04-17 15:38:21.558', 'Consommation (Arabe)', 'ARA', 'InvoiceCategory_249', 1, 1, 1);
INSERT INTO adm_messages VALUES (250, 1, false, '2013-04-17 15:32:43.51', '2013-04-17 15:38:21.566', 'Consommation (Turque)', 'TUR', 'InvoiceCategory_249', 1, 1, 1);
INSERT INTO adm_messages VALUES (252, 1, false, '2013-04-17 15:32:43.515', '2013-04-17 15:38:21.572', 'Consommation (FranÃÂ§ais))', 'FRA', 'InvoiceCategory_249', 1, 1, 1);
INSERT INTO adm_messages VALUES (242, 3, false, '2013-04-17 15:26:59.833', '2013-04-17 15:39:11.169', 'Abonnement (Arabe)', 'ARA', 'InvoiceCategory_240', 1, 1, 1);
INSERT INTO adm_messages VALUES (241, 3, false, '2013-04-17 15:26:59.83', '2013-04-17 15:39:11.181', 'Abonnement (Turque)', 'TUR', 'InvoiceCategory_240', 1, 1, 1);
INSERT INTO adm_messages VALUES (243, 3, false, '2013-04-17 15:26:59.834', '2013-04-17 15:39:11.188', 'Abonnement (FranÃÂ§ais)', 'FRA', 'InvoiceCategory_240', 1, 1, 1);
INSERT INTO adm_messages VALUES (268, 1, false, '2013-04-17 15:49:42.341', '2013-04-17 15:53:07.553', 'Taxe de 10 pourcent (arabe)', 'ARA', 'Tax_266', 1, 1, 1);
INSERT INTO adm_messages VALUES (267, 1, false, '2013-04-17 15:49:42.339', '2013-04-17 15:53:07.564', 'Taxe de 10 pourcent (turque)', 'TUR', 'Tax_266', 1, 1, 1);
INSERT INTO adm_messages VALUES (269, 1, false, '2013-04-17 15:49:42.343', '2013-04-17 15:53:07.568', 'Taxe de 10 pourcent (franÃ§ais)', 'FRA', 'Tax_266', 1, 1, 1);
INSERT INTO adm_messages VALUES (283, 0, false, '2013-04-17 15:53:55.445', NULL, 'Taxe de 150 pourcent (turque)', 'TUR', 'Tax_282', 1, 1, NULL);
INSERT INTO adm_messages VALUES (284, 0, false, '2013-04-17 15:53:55.447', NULL, 'Taxe de 15 pourcent (arabe)', 'ARA', 'Tax_282', 1, 1, NULL);
INSERT INTO adm_messages VALUES (285, 0, false, '2013-04-17 15:53:55.449', NULL, 'Taxe de 15 pourcent (franÃ§ais)', 'FRA', 'Tax_282', 1, 1, NULL);
INSERT INTO adm_messages VALUES (287, 0, false, '2013-04-17 15:54:52.007', NULL, 'Taxe de 19,6 pourcent (turque)', 'TUR', 'Tax_286', 1, 1, NULL);
INSERT INTO adm_messages VALUES (288, 0, false, '2013-04-17 15:54:52.009', NULL, 'Taxe de 19,6 pourcent (arabe)', 'ARA', 'Tax_286', 1, 1, NULL);
INSERT INTO adm_messages VALUES (289, 0, false, '2013-04-17 15:54:52.01', NULL, 'Taxe de 19,6 pourcent (franÃ§ais)', 'FRA', 'Tax_286', 1, 1, NULL);
INSERT INTO adm_messages VALUES (291, 0, false, '2013-04-17 15:56:07.803', NULL, 'Taxe de 7 pourcent (turque)', 'TUR', 'Tax_290', 1, 1, NULL);
INSERT INTO adm_messages VALUES (292, 0, false, '2013-04-17 15:56:07.805', NULL, 'Taxe de 7 pourcent (arabe)', 'ARA', 'Tax_290', 1, 1, NULL);
INSERT INTO adm_messages VALUES (293, 0, false, '2013-04-17 15:56:07.808', NULL, 'Taxe de 7 pourcent (franÃ§ais)', 'FRA', 'Tax_290', 1, 1, NULL);
INSERT INTO adm_messages VALUES (295, 0, false, '2013-04-17 15:58:22.261', NULL, 'SUBSCRIPTION', 'TUR', 'InvoiceSubCategory_294', 1, 1, NULL);
INSERT INTO adm_messages VALUES (296, 0, false, '2013-04-17 15:58:22.262', NULL, 'SUBSCRIPTION', 'ARA', 'InvoiceSubCategory_294', 1, 1, NULL);
INSERT INTO adm_messages VALUES (297, 0, false, '2013-04-17 15:58:22.263', NULL, 'SUBSCRIPTION', 'FRA', 'InvoiceSubCategory_294', 1, 1, NULL);
INSERT INTO adm_messages VALUES (299, 0, false, '2013-04-17 15:59:10', NULL, 'Souscription de  serveur (Turque)', 'TUR', 'InvoiceSubCategory_298', 1, 1, NULL);
INSERT INTO adm_messages VALUES (300, 0, false, '2013-04-17 15:59:10.002', NULL, 'Souscription de serveur (Arabe)', 'ARA', 'InvoiceSubCategory_298', 1, 1, NULL);
INSERT INTO adm_messages VALUES (301, 0, false, '2013-04-17 15:59:10.004', NULL, 'Souscription de serveur (FranÃ§ais)', 'FRA', 'InvoiceSubCategory_298', 1, 1, NULL);
INSERT INTO adm_messages VALUES (303, 0, false, '2013-04-17 16:00:47.072', NULL, 'Souscription de progiciel (Turque)', 'TUR', 'InvoiceSubCategory_302', 1, 1, NULL);
INSERT INTO adm_messages VALUES (304, 0, false, '2013-04-17 16:00:47.074', NULL, 'Souscription de progiciel (Arabe)', 'ARA', 'InvoiceSubCategory_302', 1, 1, NULL);
INSERT INTO adm_messages VALUES (305, 0, false, '2013-04-17 16:00:47.076', NULL, 'Souscription de progiciel (FranÃ§ais)', 'FRA', 'InvoiceSubCategory_302', 1, 1, NULL);
INSERT INTO adm_messages VALUES (307, 0, false, '2013-04-17 16:02:54.629', NULL, 'Souscription de domaine (turque)', 'TUR', 'InvoiceSubCategory_306', 1, 1, NULL);
INSERT INTO adm_messages VALUES (308, 0, false, '2013-04-17 16:02:54.631', NULL, 'Souscription de domaine (arabe)', 'ARA', 'InvoiceSubCategory_306', 1, 1, NULL);
INSERT INTO adm_messages VALUES (309, 0, false, '2013-04-17 16:02:54.632', NULL, 'Souscription de domaine (franÃ§ais)', 'FRA', 'InvoiceSubCategory_306', 1, 1, NULL);
INSERT INTO adm_messages VALUES (311, 0, false, '2013-04-17 16:04:43.499', NULL, 'Consommation Data (Turque)', 'TUR', 'InvoiceSubCategory_310', 1, 1, NULL);
INSERT INTO adm_messages VALUES (312, 0, false, '2013-04-17 16:04:43.501', NULL, 'Consommation Data (Arabe)', 'ARA', 'InvoiceSubCategory_310', 1, 1, NULL);
INSERT INTO adm_messages VALUES (313, 0, false, '2013-04-17 16:04:43.503', NULL, 'Consommation Data (FranÃ§ais)', 'FRA', 'InvoiceSubCategory_310', 1, 1, NULL);
INSERT INTO adm_messages VALUES (332, 3, false, '2013-04-17 16:55:29.218', '2013-04-17 17:49:26.249', 'Exchange 2010 - souscription blackberry (Arabe)', 'ARA', 'ChargeTemplate_330', 1, 1, 1);
INSERT INTO adm_messages VALUES (331, 3, false, '2013-04-17 16:55:29.217', '2013-04-17 17:49:26.26', 'Exchange 2010 - souscription blackberry (Turque)', 'TUR', 'ChargeTemplate_330', 1, 1, 1);
INSERT INTO adm_messages VALUES (333, 3, false, '2013-04-17 16:55:29.22', '2013-04-17 17:49:26.266', 'Exchange 2010 - souscription blackberry (FranÃÂÃÂÃÂÃÂ§ais)', 'FRA', 'ChargeTemplate_330', 1, 1, 1);
INSERT INTO adm_messages VALUES (357, 1, false, '2013-04-17 17:30:03.303', '2013-04-17 17:51:22.858', 'License d''accÃÂ©s Microsoft (Arabe)', 'ARA', 'ChargeTemplate_355', 1, 1, 1);
INSERT INTO adm_messages VALUES (356, 1, false, '2013-04-17 17:30:03.301', '2013-04-17 17:51:22.869', 'License d''accÃÂ©s Microsoft (Turque)', 'TUR', 'ChargeTemplate_355', 1, 1, 1);
INSERT INTO adm_messages VALUES (358, 1, false, '2013-04-17 17:30:03.304', '2013-04-17 17:51:22.875', 'License d''accÃÂ©s Microsoft (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_355', 1, 1, 1);
INSERT INTO adm_messages VALUES (361, 0, false, '2013-04-17 17:41:03.565', NULL, 'Souscription Data (Turque)', 'TUR', 'InvoiceSubCategory_360', 1, 1, NULL);
INSERT INTO adm_messages VALUES (362, 0, false, '2013-04-17 17:41:03.566', NULL, 'Souscription Data (Arabe)', 'ARA', 'InvoiceSubCategory_360', 1, 1, NULL);
INSERT INTO adm_messages VALUES (340, 5, false, '2013-04-17 16:59:37.534', '2013-04-17 17:52:27.739', 'Option 1Go incluse (Arabe)', 'ARA', 'ChargeTemplate_338', 1, 1, 1);
INSERT INTO adm_messages VALUES (335, 2, false, '2013-04-17 16:57:37.13', '2013-04-17 17:50:27.068', 'Exchange 2010 Business', 'TUR', 'ChargeTemplate_334', 1, 1, 1);
INSERT INTO adm_messages VALUES (336, 2, false, '2013-04-17 16:57:37.131', '2013-04-17 17:50:27.079', 'Exchange 2010 Business', 'ARA', 'ChargeTemplate_334', 1, 1, 1);
INSERT INTO adm_messages VALUES (337, 2, false, '2013-04-17 16:57:37.133', '2013-04-17 17:50:27.085', 'Exchange 2010 Business', 'FRA', 'ChargeTemplate_334', 1, 1, 1);
INSERT INTO adm_messages VALUES (343, 2, false, '2013-04-17 17:00:17.826', '2013-04-17 17:50:00.953', 'Exchange 2010 Premium', 'TUR', 'ChargeTemplate_342', 1, 1, 1);
INSERT INTO adm_messages VALUES (344, 2, false, '2013-04-17 17:00:17.827', '2013-04-17 17:50:00.963', 'Exchange 2010 Premium', 'ARA', 'ChargeTemplate_342', 1, 1, 1);
INSERT INTO adm_messages VALUES (347, 4, false, '2013-04-17 17:02:59.86', '2013-04-17 17:56:29.507', ' Option 3Go supplÃÂ©mentaire (Turque)', 'TUR', 'ChargeTemplate_346', 1, 1, 1);
INSERT INTO adm_messages VALUES (348, 4, false, '2013-04-17 17:02:59.862', '2013-04-17 17:56:29.517', 'Option 3Go supplÃÂ©mentaire (Arabe)', 'ARA', 'ChargeTemplate_346', 1, 1, 1);
INSERT INTO adm_messages VALUES (345, 2, false, '2013-04-17 17:00:17.829', '2013-04-17 17:50:00.969', 'Exchange 2010 Premium', 'FRA', 'ChargeTemplate_342', 1, 1, 1);
INSERT INTO adm_messages VALUES (339, 5, false, '2013-04-17 16:59:37.532', '2013-04-17 17:52:27.75', 'Option 1Go incluse (Turque)', 'TUR', 'ChargeTemplate_338', 1, 1, 1);
INSERT INTO adm_messages VALUES (363, 0, false, '2013-04-17 17:41:03.568', NULL, 'Souscription Data (FranÃ§ais)', 'FRA', 'InvoiceSubCategory_360', 1, 1, NULL);
INSERT INTO adm_messages VALUES (381, 1, false, '2013-04-17 18:23:32.616', '2013-04-17 18:31:24.266', 'Microsoft Project 2013 (Turque)', 'TUR', 'ChargeTemplate_380', 1, 1, 1);
INSERT INTO adm_messages VALUES (383, 1, false, '2013-04-17 18:23:32.62', '2013-04-17 18:31:24.272', 'Microsoft Project 2013 (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_380', 1, 1, 1);
INSERT INTO adm_messages VALUES (386, 1, false, '2013-04-17 18:25:07.046', '2013-04-17 18:31:45.272', 'Microsoft Visio 2013', 'ARA', 'ChargeTemplate_384', 1, 1, 1);
INSERT INTO adm_messages VALUES (385, 1, false, '2013-04-17 18:25:07.044', '2013-04-17 18:31:45.282', 'Microsoft Visio 2013', 'TUR', 'ChargeTemplate_384', 1, 1, 1);
INSERT INTO adm_messages VALUES (341, 5, false, '2013-04-17 16:59:37.536', '2013-04-17 17:52:27.756', 'Option 1Go incluse (FranÃ§ais)', 'FRA', 'ChargeTemplate_338', 1, 1, 1);
INSERT INTO adm_messages VALUES (365, 2, false, '2013-04-17 17:46:34.024', '2013-04-17 17:53:21.456', 'Option 1Go supplÃ©mentaire (turque)', 'TUR', 'ChargeTemplate_364', 1, 1, 1);
INSERT INTO adm_messages VALUES (366, 2, false, '2013-04-17 17:46:34.026', '2013-04-17 17:53:21.467', 'Option 1Go supplÃ©mentaire (Arabe)', 'ARA', 'ChargeTemplate_364', 1, 1, 1);
INSERT INTO adm_messages VALUES (367, 2, false, '2013-04-17 17:46:34.028', '2013-04-17 17:53:21.472', 'Option 1Go supplÃ©mentaire (FranÃ§ais)', 'FRA', 'ChargeTemplate_364', 1, 1, 1);
INSERT INTO adm_messages VALUES (387, 1, false, '2013-04-17 18:25:07.047', '2013-04-17 18:31:45.289', 'Microsoft Visio 2013', 'FRA', 'ChargeTemplate_384', 1, 1, 1);
INSERT INTO adm_messages VALUES (349, 4, false, '2013-04-17 17:02:59.864', '2013-04-17 17:56:29.522', 'Option 3Go supplÃÂ©mentaire (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_346', 1, 1, 1);
INSERT INTO adm_messages VALUES (390, 1, false, '2013-04-17 18:26:14.525', '2013-04-17 18:32:09.236', 'Microsoft Sharepoint 2010 (Arabe)', 'ARA', 'ChargeTemplate_388', 1, 1, 1);
INSERT INTO adm_messages VALUES (389, 1, false, '2013-04-17 18:26:14.524', '2013-04-17 18:32:09.249', 'Microsoft Sharepoint 2010 (Turque)', 'TUR', 'ChargeTemplate_388', 1, 1, 1);
INSERT INTO adm_messages VALUES (391, 1, false, '2013-04-17 18:26:14.527', '2013-04-17 18:32:09.255', 'Microsoft Sharepoint 2010 (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_388', 1, 1, 1);
INSERT INTO adm_messages VALUES (394, 1, false, '2013-04-17 18:27:28.168', '2013-04-17 18:32:25.152', 'Microsoft Lync 2010 (Arabe)', 'ARA', 'ChargeTemplate_392', 1, 1, 1);
INSERT INTO adm_messages VALUES (393, 1, false, '2013-04-17 18:27:28.167', '2013-04-17 18:32:25.166', 'Microsoft Lync 2010 (Turque)', 'TUR', 'ChargeTemplate_392', 1, 1, 1);
INSERT INTO adm_messages VALUES (395, 1, false, '2013-04-17 18:27:28.17', '2013-04-17 18:32:25.172', 'Microsoft Lync 2010 (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_392', 1, 1, 1);
INSERT INTO adm_messages VALUES (370, 3, false, '2013-04-17 17:57:34.063', '2013-04-17 17:58:59.199', 'Option 2Go incluse (Arabe)', 'ARA', 'ChargeTemplate_368', 1, 1, 1);
INSERT INTO adm_messages VALUES (369, 3, false, '2013-04-17 17:57:34.061', '2013-04-17 17:58:59.211', 'Option 2Go incluse (Turque)', 'TUR', 'ChargeTemplate_368', 1, 1, 1);
INSERT INTO adm_messages VALUES (371, 3, false, '2013-04-17 17:57:34.065', '2013-04-17 17:58:59.218', 'Option 2Go incluse (franÃ§ais)', 'FRA', 'ChargeTemplate_368', 1, 1, 1);
INSERT INTO adm_messages VALUES (377, 0, false, '2013-04-17 18:09:41.377', NULL, 'Microsoft Office 2013 Standard (Turque)', 'TUR', 'ChargeTemplate_376', 1, 1, NULL);
INSERT INTO adm_messages VALUES (378, 0, false, '2013-04-17 18:09:41.379', NULL, 'Microsoft Office 2013 Standard (Arabe)', 'ARA', 'ChargeTemplate_376', 1, 1, NULL);
INSERT INTO adm_messages VALUES (379, 0, false, '2013-04-17 18:09:41.38', NULL, 'Microsoft Office 2013 Standard (FranÃ§ais)', 'FRA', 'ChargeTemplate_376', 1, 1, NULL);
INSERT INTO adm_messages VALUES (374, 1, false, '2013-04-17 18:08:38.845', '2013-04-17 18:21:39.416', 'Microsoft Office 2013 Professionel (Arabe)', 'ARA', 'ChargeTemplate_372', 1, 1, 1);
INSERT INTO adm_messages VALUES (373, 1, false, '2013-04-17 18:08:38.844', '2013-04-17 18:21:39.429', 'Microsoft Office 2013 Professionel (turque)', 'TUR', 'ChargeTemplate_372', 1, 1, 1);
INSERT INTO adm_messages VALUES (375, 1, false, '2013-04-17 18:08:38.847', '2013-04-17 18:21:39.435', 'Microsoft Office 2013 Professionel (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_372', 1, 1, 1);
INSERT INTO adm_messages VALUES (382, 1, false, '2013-04-17 18:23:32.618', '2013-04-17 18:31:24.254', 'Microsoft Project 2013 (Arabe)', 'ARA', 'ChargeTemplate_380', 1, 1, 1);
INSERT INTO adm_messages VALUES (398, 1, false, '2013-04-17 18:28:56.872', '2013-04-17 18:32:40.732', 'Microsoft Dynamics CRM 2011 (Arabe)', 'ARA', 'ChargeTemplate_396', 1, 1, 1);
INSERT INTO adm_messages VALUES (397, 1, false, '2013-04-17 18:28:56.871', '2013-04-17 18:32:40.743', 'Microsoft Dynamics CRM 2011 (Turque)', 'TUR', 'ChargeTemplate_396', 1, 1, 1);
INSERT INTO adm_messages VALUES (399, 1, false, '2013-04-17 18:28:56.874', '2013-04-17 18:32:40.749', 'Microsoft Dynamics CRM 2011 (FranÃÂ§ais)', 'FRA', 'ChargeTemplate_396', 1, 1, 1);
INSERT INTO adm_messages VALUES (401, 0, false, '2013-04-17 18:34:33.249', NULL, 'Option 1GB supplÃ©mentaire (Turque)', 'TUR', 'ChargeTemplate_400', 1, 1, NULL);
INSERT INTO adm_messages VALUES (402, 0, false, '2013-04-17 18:34:33.252', NULL, 'Option 1GB supplÃ©mentaire (Arabe)', 'ARA', 'ChargeTemplate_400', 1, 1, NULL);
INSERT INTO adm_messages VALUES (403, 0, false, '2013-04-17 18:34:33.254', NULL, 'Option 1GB supplÃ©mentaire (FranÃ§ais)', 'FRA', 'ChargeTemplate_400', 1, 1, NULL);


--
-- Name: adm_messages_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_messages_seq', 38, true);


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

SELECT pg_catalog.setval('adm_role_permission_seq', 6, true);


--
-- Name: adm_role_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_role_seq', 1, false);


--
-- Name: adm_title_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_title_seq', 1, false);


--
-- Data for Name: adm_user_log; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: adm_user_log_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_user_log_seq', 1, false);


--
-- Data for Name: adm_user_provider; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_user_provider VALUES (1, 1);
INSERT INTO adm_user_provider VALUES (1, 6);


--
-- Data for Name: adm_user_role; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO adm_user_role VALUES (1, 1);
INSERT INTO adm_user_role VALUES (1, 6);
INSERT INTO adm_user_role VALUES (6, 1);
INSERT INTO adm_user_role VALUES (6, 6);


--
-- Name: adm_user_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_user_seq', 1, false);


--
-- Data for Name: adm_vertina_configuration; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: adm_vertina_configuration_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('adm_vertina_configuration_seq', 1, false);


--
-- Data for Name: ar_ddrequest_lot; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: ar_ddrequest_item; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: ar_account_operation; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: ar_account_operation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_account_operation_seq', 1, false);


--
-- Data for Name: ar_dunning_plan; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: ar_action_plan_item; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: bi_job; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: bi_job_history; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: ar_dunning_lot; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: ar_action_dunning; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: ar_action_dunning_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_action_dunning_seq', 1, false);


--
-- Name: ar_action_plan_item_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_action_plan_item_seq', 1, false);


--
-- Data for Name: ar_bank_operation; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: ar_bank_operation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_bank_operation_seq', 1, false);


--
-- Name: ar_ddrequest_item_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_ddrequest_item_seq', 1, false);


--
-- Data for Name: ar_ddrequest_lot_op; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: ar_ddrequest_lot_op_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_ddrequest_lot_op_seq', 1, false);


--
-- Name: ar_ddrequest_lot_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_ddrequest_lot_seq', 1, false);


--
-- Name: ar_dunning_lot_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_dunning_lot_seq', 1, false);


--
-- Name: ar_dunning_plan_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_dunning_plan_seq', 1, false);


--
-- Data for Name: ar_dunning_plan_transition; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: ar_dunning_plan_transition_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_dunning_plan_transition_seq', 1, false);


--
-- Data for Name: ar_matching_code; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: ar_matching_amount; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: ar_matching_amount_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_matching_amount_seq', 1, false);


--
-- Name: ar_matching_code_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_matching_code_seq', 1, false);


--
-- Data for Name: ar_occ_template; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: ar_occ_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('ar_occ_template_seq', 1, false);


--
-- Name: bi_job_history_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('bi_job_history_seq', 1, false);


--
-- Name: bi_job_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('bi_job_seq', 1, false);


--
-- Data for Name: bi_report; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: crm_email; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: bi_report_emails; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: bi_report_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('bi_report_seq', 1, false);


--
-- Data for Name: billing_access_point; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_calendar; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_calendar VALUES (318, 1, false, '2013-04-17 16:14:22.094', '2013-04-17 17:09:51.689', 'Monthly subscription calendar', 'CAL_SUB_MT', 'DURATION_TERM', 1, 1, 1);


--
-- Data for Name: billing_cycle; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_billing_run; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_billing_run_list; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_billing_run_list_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_billing_run_list_seq', 1, false);


--
-- Name: billing_billing_run_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_billing_run_seq', 1, false);


--
-- Name: billing_charge_applic_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_charge_applic_seq', 1, false);


--
-- Data for Name: billing_invoice_cat; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_invoice_cat VALUES (249, 2, false, '2013-04-17 15:32:43.503', '2013-04-17 15:38:21.573', 'CONSUMPTION', 'Consumption', NULL, 1, 1, 1);
INSERT INTO billing_invoice_cat VALUES (240, 5, false, '2013-04-17 15:26:59.825', '2013-04-17 15:39:11.188', 'SUBSCRIPTION', 'Subscription', NULL, 1, 1, 1);


--
-- Data for Name: billing_invoice_sub_cat; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_invoice_sub_cat VALUES (298, 0, false, '2013-04-17 15:59:09.996', NULL, 'SUB_SRV', 'Server subscription', 'PB,CG,CA,DA,ZONE,IC,GP', NULL, 1, 1, NULL, 240);
INSERT INTO billing_invoice_sub_cat VALUES (302, 0, false, '2013-04-17 16:00:47.068', NULL, 'SUB_SOFT', 'Software subscription', 'PB,CG,CA,DA,ZONE,IC,GP', NULL, 1, 1, NULL, 240);
INSERT INTO billing_invoice_sub_cat VALUES (306, 0, false, '2013-04-17 16:02:54.625', NULL, 'SUB_DOM', 'Domain subscription', 'PB,CG,CA,DA,ZONE,IC,GP', NULL, 1, 1, NULL, 240);
INSERT INTO billing_invoice_sub_cat VALUES (310, 0, false, '2013-04-17 16:04:43.495', NULL, 'CMP_DATA', 'Data Consumption', 'PB,CG,CA,DA,ZONE,IC,GP', NULL, 1, 1, NULL, 249);
INSERT INTO billing_invoice_sub_cat VALUES (360, 0, false, '2013-04-17 17:41:03.561', NULL, 'SUB_DATA', 'Data Subscription', 'PB,CG,CA,DA,ZONE,IC,GP', NULL, 1, 1, NULL, 240);


--
-- Data for Name: cat_charge_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_charge_template VALUES (376, 0, false, '2013-04-17 18:09:41.374', NULL, 'MO20131_SOFT', 'Microsoft Office 2013 Standard', false, 0, 1, 1, NULL, 302);
INSERT INTO cat_charge_template VALUES (372, 2, false, '2013-04-17 18:08:38.84', '2013-04-17 18:21:39.436', 'MO20132_SOFT', 'Microsoft Office 2013 Professional', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (380, 2, false, '2013-04-17 18:23:32.612', '2013-04-17 18:31:24.272', 'MPR2013_SOFT', 'Microsoft Project 2013', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (384, 2, false, '2013-04-17 18:25:07.04', '2013-04-17 18:31:45.289', 'MVI2013_SOFT', 'Microsoft Visio 2013', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (388, 2, false, '2013-04-17 18:26:14.521', '2013-04-17 18:32:09.255', 'MSH2010_SOFT', 'Microsoft Sharepoint 2010', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (392, 2, false, '2013-04-17 18:27:28.164', '2013-04-17 18:32:25.173', 'MLYNC2010_SOFT', 'Microsoft Lync 2010', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (396, 2, false, '2013-04-17 18:28:56.867', '2013-04-17 18:32:40.75', 'MDYCRM2011_SOFT', 'Microsoft Dynamics CRM 2011', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (330, 6, false, '2013-04-17 16:55:29.213', '2013-04-17 17:49:26.267', 'EXCH20102_SOFT_BLACK', 'Exchange 2010 - Blackberry subscription', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (400, 0, false, '2013-04-17 18:34:33.246', NULL, 'MSOFT_DATA', '1GB Additional Storage Option', false, 0, 1, 1, NULL, 360);
INSERT INTO cat_charge_template VALUES (342, 4, false, '2013-04-17 17:00:17.822', '2013-04-17 17:50:00.97', 'EXCH20102_SOFT', 'Exchange 2010 Premium', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (334, 4, false, '2013-04-17 16:57:37.126', '2013-04-17 17:50:27.085', 'EXCH20101_SOFT', 'Exchange 2010 Business', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (355, 2, false, '2013-04-17 17:30:03.298', '2013-04-17 17:51:22.876', 'MALIC_SOFT', 'Microsoft Access License', false, 0, 1, 1, 1, 302);
INSERT INTO cat_charge_template VALUES (338, 10, false, '2013-04-17 16:59:37.528', '2013-04-17 17:52:27.756', 'EXCH20101_DATA_1G', '1Go  comp. Option', false, 0, 1, 1, 1, 360);
INSERT INTO cat_charge_template VALUES (364, 4, false, '2013-04-17 17:46:34.02', '2013-04-17 17:53:21.473', 'EXCH20101_DATA_1G+', '1Go Additional Storage Option', false, 0, 1, 1, 1, 360);
INSERT INTO cat_charge_template VALUES (346, 10, false, '2013-04-17 17:02:59.858', '2013-04-17 17:56:29.523', 'EXCH20102_DATA_3G+', '3Go Additional Storage Option', false, 0, 1, 1, 1, 360);
INSERT INTO cat_charge_template VALUES (368, 5, false, '2013-04-17 17:57:34.058', '2013-04-17 17:58:59.219', 'EXCH20102_DATA_2G', '2Go comp. Option', false, 0, 1, 1, 1, 360);


--
-- Data for Name: billing_charge_instance; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_charge_instance_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_charge_instance_seq', 1, false);


--
-- Data for Name: cat_counter_template; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_counter; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_counter_period; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_country_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_country_seq', 1, false);


--
-- Name: billing_cycle_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_cycle_seq', 1, true);


--
-- Data for Name: billing_discountplan_instanciation; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_discountplan_instanciation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_discountplan_instanciation_seq', 1, false);


--
-- Name: billing_invoic_sub_country_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoic_sub_country_seq', 1, false);


--
-- Data for Name: billing_invoice; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_tax; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_tax VALUES (266, 2, false, '2013-04-17 15:49:42.335', '2013-04-17 15:53:07.569', 'TAX_10', '10 Percent Tax', '', 10.0000, 1, 1, 1);
INSERT INTO billing_tax VALUES (282, 0, false, '2013-04-17 15:53:55.442', NULL, 'TAX_15', '15 Percent Tax', '', 15.0000, 1, 1, NULL);
INSERT INTO billing_tax VALUES (286, 0, false, '2013-04-17 15:54:52.002', NULL, 'TAX_19.6', '19.6 Percent Tax', '', 19.6000, 1, 1, NULL);
INSERT INTO billing_tax VALUES (290, 0, false, '2013-04-17 15:56:07.799', NULL, 'TAX_07', '7 Percent Tax', '', 7.0000, 1, 1, NULL);


--
-- Data for Name: billing_invoice_agregate; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_invoice_agregate_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_agregate_seq', 1, false);


--
-- Data for Name: billing_invoice_cat_country; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_invoice_cat_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_cat_seq', 3, true);


--
-- Name: billing_invoice_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_seq', 1, false);


--
-- Data for Name: billing_invoice_sub_cat_country; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO billing_invoice_sub_cat_country VALUES (314, 0, false, '2013-04-17 16:05:39.423', NULL, NULL, 1, 1, NULL, 298, 282, 156);


--
-- Name: billing_invoice_sub_cat_country_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_sub_cat_country_seq', 49, true);


--
-- Name: billing_invoice_sub_cat_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_sub_cat_seq', 12, true);


--
-- Data for Name: billing_invoice_template; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_invoice_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_invoice_template_seq', 1, false);


--
-- Name: billing_language_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_language_seq', 1, false);


--
-- Data for Name: billing_subscrip_termin_reason; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_offer_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_offer_template VALUES (329, 0, false, '2013-04-17 16:44:03.371', NULL, 'DOM_MAIL', 'Mail Domain', 1, 1, NULL);
INSERT INTO cat_offer_template VALUES (325, 2, false, '2013-04-17 16:34:53.862', '2013-04-17 18:03:43.692', 'SH2010', 'Microsoft Sharepoint 2010', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (321, 2, false, '2013-04-17 16:22:36.363', '2013-04-17 18:04:00.844', 'MO20131', 'Microsoft Office 2013 Standard', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (322, 2, false, '2013-04-17 16:23:15.217', '2013-04-17 18:04:30.066', 'MO20132', 'Microsoft Office 2013 Professional', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (327, 1, false, '2013-04-17 16:41:35.892', '2013-04-17 18:05:01.011', 'DYCRM2011', 'Microsoft Dynamics CRM 2011', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (324, 2, false, '2013-04-17 16:33:09.26', '2013-04-17 18:05:13.584', 'PR20131', 'Microsoft Project 2013', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (320, 2, false, '2013-04-17 16:21:34.911', '2013-04-17 18:05:30.146', 'EXCH20102', 'Exchange 2010 Premium', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (319, 2, false, '2013-04-17 16:19:19.305', '2013-04-17 18:05:52.963', 'EXCH20101', 'Exchange 2010 Business', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (326, 1, false, '2013-04-17 16:40:44.701', '2013-04-17 18:06:05.506', 'LYNC2010', 'Microsoft Lync 2010', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (323, 2, false, '2013-04-17 16:32:17.864', '2013-04-17 18:06:17.937', 'VI20131', 'Microsoft Visio 2013', 1, 1, 1);
INSERT INTO cat_offer_template VALUES (328, 1, false, '2013-04-17 16:43:03.925', '2013-04-17 18:06:36.368', 'FASTVIEWER', 'Fastviewer', 1, 1, 1);


--
-- Data for Name: billing_subscription; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_service_template; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_service_template VALUES (354, 0, false, '2013-04-17 17:13:33.324', NULL, 'SOFT_EXCH_2010_2_BLACK', 'Exchange 2010 Premium - Blackberry option', 1, 1, NULL, 318);
INSERT INTO cat_service_template VALUES (350, 1, false, '2013-04-17 17:11:14.498', '2013-04-17 18:36:21.313', 'EXCH20101', 'Exchange 2010 Business', 1, 1, 1, 318);
INSERT INTO cat_service_template VALUES (352, 1, false, '2013-04-17 17:12:16.943', '2013-04-17 18:36:44.572', 'EXCH20102', 'Exchange 2010 Premium', 1, 1, 1, 318);
INSERT INTO cat_service_template VALUES (359, 1, false, '2013-04-17 17:32:56.416', '2013-04-17 18:37:27.47', 'MALIC', 'Microsoft Access License', 1, 1, 1, 318);


--
-- Data for Name: billing_service_instance; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_one_shot_charge_inst; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_priceplan_instanciation; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_priceplan_instanciation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_priceplan_instanciation_seq', 1, false);


--
-- Data for Name: billing_wallet_template; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_wallet; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_wallet_operation; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_rated_transaction; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_rated_transaction_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_rated_transaction_seq', 1, false);


--
-- Data for Name: cat_recurring_charge_templ; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 330, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 334, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 338, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 342, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 346, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 355, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 364, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 368, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 376, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 372, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 380, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 384, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 388, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 392, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 396, NULL);
INSERT INTO cat_recurring_charge_templ VALUES (true, 0, 'CALENDAR', true, true, 400, NULL);


--
-- Data for Name: billing_recurring_charge_inst; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: billing_serv_param_inst; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_serv_param_inst_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_serv_param_inst_seq', 1, false);


--
-- Name: billing_service_instance_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_service_instance_seq', 1, false);


--
-- Name: billing_subscription_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_subscription_seq', 1, false);


--
-- Name: billing_tax_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_tax_seq', 4, true);


--
-- Name: billing_term_reason_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_term_reason_seq', 1, false);


--
-- Name: billing_trading_country_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_trading_country_seq', 7, true);


--
-- Name: billing_trading_currency_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_trading_currency_seq', 7, true);


--
-- Name: billing_trading_language_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_trading_language_seq', 2, true);


--
-- Data for Name: billing_usage_charge_inst; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: billing_wallet_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('billing_wallet_seq', 1, false);


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


--
-- Name: cat_calendar_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_calendar_seq', 3, true);


--
-- Name: cat_charge_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_charge_template_seq', 1, false);


--
-- Name: cat_day_in_year_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_day_in_year_seq', 1, false);


--
-- Data for Name: crm_seller; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_discount_plan_matrix; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: cat_discount_plan_matrix_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_discount_plan_matrix_seq', 1, false);


--
-- Data for Name: cat_offer_serv_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: cat_offer_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_offer_template_seq', 6, true);


--
-- Data for Name: cat_one_shot_charge_templ; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_price_code; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_price_plan_matrix; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: cat_price_plan_matrix_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_price_plan_matrix_seq', 1, false);


--
-- Data for Name: cat_serv_onecharge_s_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_serv_onecharge_t_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_serv_reccharge_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO cat_serv_reccharge_templates VALUES (354, 330);
INSERT INTO cat_serv_reccharge_templates VALUES (350, 334);
INSERT INTO cat_serv_reccharge_templates VALUES (350, 338);
INSERT INTO cat_serv_reccharge_templates VALUES (352, 342);
INSERT INTO cat_serv_reccharge_templates VALUES (352, 368);
INSERT INTO cat_serv_reccharge_templates VALUES (359, 355);


--
-- Data for Name: cat_usage_charge_template; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_serv_usage_charge_template; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: cat_serv_usage_templates; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: cat_service_template_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('cat_service_template_seq', 1, false);


--
-- Data for Name: com_campaign; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_campaign_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_campaign_seq', 1, false);


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

SELECT pg_catalog.setval('com_contact_coords_seq', 1, false);


--
-- Name: com_contact_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_contact_seq', 1, false);


--
-- Name: com_message_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_message_seq', 1, false);


--
-- Data for Name: com_message_template; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_msg_tmpl_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_msg_tmpl_seq', 1, false);


--
-- Name: com_msg_tmpl_var_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_msg_tmpl_var_seq', 1, false);


--
-- Data for Name: com_msg_tmpl_variable; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_msg_var_val_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_msg_var_val_seq', 1, false);


--
-- Data for Name: com_msg_var_value; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_prov_pol_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_prov_pol_seq', 1, false);


--
-- Data for Name: com_provider_policy; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: com_sender_config; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: com_sndr_conf_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('com_sndr_conf_seq', 1, false);


--
-- Data for Name: crm_customer_brand; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO crm_customer_brand VALUES (317, 0, false, '2013-04-17 16:11:16.021', NULL, 'ASG', 'ASG Products and services', 1, 1, NULL);


--
-- Name: crm_customer_brand_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_customer_brand_seq', 1, true);


--
-- Data for Name: crm_customer_category; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO crm_customer_category VALUES (315, 0, false, '2013-04-17 16:09:51.62', NULL, 'Business', 'Company customer type', 1, 1, NULL);
INSERT INTO crm_customer_category VALUES (316, 0, false, '2013-04-17 16:10:45.292', NULL, 'Residential', 'Individual customer type', 1, 1, NULL);


--
-- Name: crm_customer_category_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_customer_category_seq', 2, true);


--
-- Name: crm_email_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_email_seq', 1, false);


--
-- Data for Name: crm_provider_config; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: crm_provider_config_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_provider_config_seq', 1, false);


--
-- Data for Name: crm_provider_contact; Type: TABLE DATA; Schema: public; Owner: meveo
--

INSERT INTO crm_provider_contact VALUES (235, 1, false, '2013-04-17 10:46:05.064', '2013-04-17 16:12:17.991', 'ASG ADMIN', 'ASG Administrator', '', '', '', '', 'Australie', NULL, '', 'qdqsdq@gmail.com', NULL, '', 'sdqsdq@gmail.com', '', '', '', 1, 1, 1);


--
-- Name: crm_provider_contact_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_provider_contact_seq', 1, false);


--
-- Name: crm_provider_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('crm_provider_seq', 1, false);


--
-- Data for Name: dwh_account_operation; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: dwh_account_operation_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('dwh_account_operation_seq', 1, false);


--
-- Data for Name: dwh_journal_entries; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: dwh_sales_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('dwh_sales_seq', 1, false);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('hibernate_sequence', 756, true);


--
-- Data for Name: job_execution; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: mediation_magic_numbers; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: mediation_magic_numbers_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('mediation_magic_numbers_seq', 1, false);


--
-- Data for Name: medina_access; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: medina_access_billing_subscription; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: medina_number_plan; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: medina_number_plan_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('medina_number_plan_seq', 1, false);


--
-- Data for Name: medina_time_plan; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: medina_time_plan_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('medina_time_plan_seq', 1, false);


--
-- Data for Name: medina_zonning_plan; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: medina_zonning_plan_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('medina_zonning_plan_seq', 1, false);


--
-- Data for Name: meveo_timer; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: offer_instance_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('offer_instance_seq', 1, false);


--
-- Data for Name: provider_titles; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Data for Name: rating_edr; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: rating_matrix_definition_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rating_matrix_definition_seq', 1, false);


--
-- Name: rating_matrix_entry_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rating_matrix_entry_seq', 1, false);


--
-- Name: rating_usage_type_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rating_usage_type_seq', 1, false);


--
-- Data for Name: report_emails; Type: TABLE DATA; Schema: public; Owner: meveo
--



--
-- Name: rm_line_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rm_line_seq', 1, false);


--
-- Name: rm_usage_counter_seq; Type: SEQUENCE SET; Schema: public; Owner: meveo
--

SELECT pg_catalog.setval('rm_usage_counter_seq', 1, false);


--
-- PostgreSQL database dump complete
--

