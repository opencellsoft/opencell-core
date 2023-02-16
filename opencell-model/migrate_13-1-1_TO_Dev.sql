-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: src/main/db_resources/changelog/db.current.xml
-- Ran at: 10/26/22, 5:05 PM
-- Against: meveo@jdbc:postgresql://localhost/meveo
-- Liquibase version: 3.8.0
-- *********************************************************************

-- Lock Database
UPDATE databasechangeloglock SET LOCKED = TRUE, LOCKEDBY = '192.168.1.200 (192.168.1.200)', LOCKGRANTED = '2022-10-26 17:05:35.422' WHERE ID = 1 AND LOCKED = FALSE;

UPDATE databasechangelog SET MD5SUM = '8:52c43999a1430d5d2d426cf7f757617b' WHERE ID = '#4511_20190919-P' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:00b7c9aed9dd6c963129efbe9208a21b' WHERE ID = '#INTRD-4798_20220303' AND AUTHOR = 'MohamedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4f0b073009d6b079d5385626ca284926' WHERE ID = '#INTRD-7954_20220616' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8c6fba0e3620e20e9afc4f63de78b473' WHERE ID = '#INTRD-5777_2022-03-17_PG' AND AUTHOR = 'MohammedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:62a215a37bd5d1620b72ff5cb2fbd105' WHERE ID = '#INTRD-8753_20220727_001' AND AUTHOR = 'anas.rouaguebe' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b7b3d6a53dcb18dcbf553608704ffd67' WHERE ID = '#INTRD-9535-20220902' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:562c5012a994b56ad9b09aefc445cdab' WHERE ID = '#4303_300520191' AND AUTHOR = 'mohamed.el.youssoufi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7f4582279fadf9fbb314eacb089a5687' WHERE ID = '#4322_20190612 - Add a filter to GenericWorkflow' AND AUTHOR = 'MounirBahije' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c24794a1ac80ef14e2a8c1aab8f449fc' WHERE ID = '#4054_201917061 - Encryption' AND AUTHOR = 'MohamedElYoussoufi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:04cd2466f50a55d612076ae3a9ea68d1' WHERE ID = '#4371_20190620_2' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b2e8e867b8ac9172501a2d350d752551' WHERE ID = '#4371_20190620_4' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b7f27e39c960357768a5799e2ab85ff3' WHERE ID = '#4371_20190620_5' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8ba0eab50abe351358ba5aa1406b3ec3' WHERE ID = '#4160_20190105 - Accounting Writing' AND AUTHOR = 'MounirBOUKAYOUA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3f60f95b694bc30bfefe9b4309ad52fa' WHERE ID = '#4337_20190710' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:66eceb45eb9b3f3b047cc7af76d0ef5f' WHERE ID = '#4372_20190715' AND AUTHOR = 'RedaDebbache' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:38098975a7afad4f97637d8a4d6f111b' WHERE ID = '#4372_20190723' AND AUTHOR = 'RedaDebbache' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5b4d3fb3a36e686221ee3722fa80036f' WHERE ID = '#4458_20190626_1' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7b89a7541aa5ed7aae4d287664410a21' WHERE ID = '#4458_20190626_2' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:33f519c34db466f2141e47a17457c455' WHERE ID = '#4458_20190626_4' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f5656552eb69c0fe6582cfcc0b7d4fe5' WHERE ID = '#4468_20190631_1' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3c03e1044526697a4b13c276acb6615b' WHERE ID = '#4326_20190709' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5e928029c152bae055e691685f19debe' WHERE ID = '#4457_20190728' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3330d29522ca3cab20e9431a2c0f5349' WHERE ID = '#4472_20190807' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5a39e1c56bf442a5c8eaf6fb2a5417ba' WHERE ID = '#4326_20190811' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c541fa9ef450ef722895284883d66f17' WHERE ID = '#4512_20190819' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:50b308eb3d736d2a7a87443ca977160a' WHERE ID = '#4480_250919' AND AUTHOR = 'MohammedElAzzouzi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a955c38eda846c432eedf0607b66b7dd' WHERE ID = '#4480_210819' AND AUTHOR = 'SaidRamli' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bac29d1d1a875e9017f9081509f4d5c5' WHERE ID = '#4538_20190829' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8f9a2e2d0f4fa1ad8d9ac3c232006217' WHERE ID = '#4550_20190904' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:58104a1185a12ef5da7e6fe443a76b8e' WHERE ID = '#4313_17062019' AND AUTHOR = 'mohamed.el.youssoufi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f050b084f181a1d8ea4251c6b3b36cc1' WHERE ID = '#3774_07102019' AND AUTHOR = 'FranckValot' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:205d8cdc1ad4f4b20f490744463f49f2' WHERE ID = '#4064_11092019' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3344afc4133d631cce5bec1a5796c977' WHERE ID = '#4606_260919' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7e82d5f8133bea3c46551c3c7660e1f9' WHERE ID = '#4555_20190903' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:88b351474b1b4b81257105b73c7e9c7d' WHERE ID = '#4555_20190903-end' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:58444b434a29f40f0549c4a99d46777f' WHERE ID = '#4555_20190905' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:202023e98bf856a3d8c7fa32c6297fd1' WHERE ID = '#4555_20190905-end' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0711679bcd0bff00edb2ef96e83f6f9d' WHERE ID = '#4511_20190919-P-end' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:333fbdc1b9bfa46f352c1979cae4f03b' WHERE ID = '#4490_31102019' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5a97dd0b460fdd4fac59b90d0ce25a57' WHERE ID = '#4523-Changing-amount-for-a-service-instance-with-prorating' AND AUTHOR = 'abdelkader.bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1c3788bd4223fd676659a16216b3ad79' WHERE ID = '#3510_20190516' AND AUTHOR = 'MounirBahije' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:245f4410f371a42a83ae96218ba00c0c' WHERE ID = '#4766_20191111' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7267e8917f637912fc4e1f6f391dd99c' WHERE ID = '#4765_20191112' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:20ac1711a331fe90bbfe060695ab3abc' WHERE ID = '#4783_20191118' AND AUTHOR = 'MohamedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0cf38b5dd4e061de8d371642658e8374' WHERE ID = '#4412_20191125' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7a388e8682828582ba8a1b7d2a3928e9' WHERE ID = '#4795_221119' AND AUTHOR = 'Mohammed_ElAzzouzi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bd2ee20571e15715549a3b24d61cec52' WHERE ID = '#4494-Enable_a_subscription_to_have_the_same_access_point_in_two_or_more_different_periods_of_time' AND AUTHOR = 'abdelkader.bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ba7371a1d1d0636bb74ab2592bbac8b4' WHERE ID = '#4712_20191223' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:51714fb4f9c6bee9c7cdc08c91957b9d' WHERE ID = '#4791_20200113' AND AUTHOR = 'KhalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:746c2f4f115cd0783556e4b9a1ec5962' WHERE ID = '4745_20200108' AND AUTHOR = 'MohamedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:177c0dd91f57c18dd7893a67200ec813' WHERE ID = '#4906_Creation_CDR_Table_2' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:934174a40cab36dad502364fbf2dfef0' WHERE ID = '#4933_20200117' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6b2e38d42ec1dbc3fa0d14bda6c45e8b' WHERE ID = '#3743_20190414' AND AUTHOR = 'HatimOudad' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f8d0e50bfa663e430f4d700d3b6e86d1' WHERE ID = '#500X_20200218' AND AUTHOR = 'mohamed.el.youssoufi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e1ce0bd3503168ba35afcb239a36c49c' WHERE ID = '#4787_20200224' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4add5fb485578b5d4483c5ecedf7c13b' WHERE ID = '#4096_2020-02-07_2' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:50ef7537ddb18b5dd20c72edfaa4528b' WHERE ID = '#4096_2020-02-07_3' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:33e29cd98e362d3d88bd961182ca5422' WHERE ID = '#4201_20200225' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:690684b0189c1a5b00cc847cccaf6b80' WHERE ID = '#4071_20200227' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:37d93cfbc9a62f8b13243ddd945587a1' WHERE ID = '#4894-Additional_fields_in_RT' AND AUTHOR = 'abdelkader.bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ba2c0045f137341e564c516abe33e5e4' WHERE ID = '#4893-Add-custom-field-support-to-WalletOperation' AND AUTHOR = 'abdelkader.bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:104f2801d322e6efa07522f17ded87bb' WHERE ID = '#4892_2020-03-04' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6c6a4788e36057bd65c41db71604b497' WHERE ID = '#5038_2020-03-06' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:cb0c09ae510ed158ed4496d49787408f' WHERE ID = '#5064_2020-03-15' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8de76504159bac000e7f27c5ffdde0e1' WHERE ID = '#4891-Copy-recurrence-calendar-and-applyInAdvance-to-RecurringChargeInstance-from-a-RecurringChargeTemplate' AND AUTHOR = 'abdelkader.bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fa3701c21d01afc83e3325a3f2fb22c2' WHERE ID = '#4757_20191118' AND AUTHOR = 'khalid.horri' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ef691dc4f7e4913ac7f5294b455aac03' WHERE ID = '#4924_20200219' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6d6795646dadc8752899afebefeaa43e' WHERE ID = '#5085_20200330 - triggeredNextCharge has an incorrect value' AND AUTHOR = 'Mohammed_ELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:02ea57fdcad162995449a2e46cb25991' WHERE ID = '#4903_GDPR_Anonymize_CF' AND AUTHOR = 'Mounir_BOUKAYOUA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d387ae2def516f7184efd88295a505ba' WHERE ID = '#4879_20190121' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:501b5e221d3b913f4ed5d263395a0859' WHERE ID = '#5052_20200323' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:faa2bd9fcb376d36557e165ff8e93c6b' WHERE ID = '#5014_20200313' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ed88d488a4b459b13149abd9332506cf' WHERE ID = '#5014_20200402' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:439eca1ff14fd6e44464922c8de92315' WHERE ID = '#5125_20200407' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3a1f0121f8daf7e43ecd0a02fdcad850' WHERE ID = '#2447_20200421' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:48f2a68a15eee2f5c731056ef60c2075' WHERE ID = '#5130_20200414' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f0a9045afced0e88f875e1c4e01b5510' WHERE ID = '#4004_ Add_CustomField_to_OrderItem' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fbc27f59a477fd647dd83c1ee3147ca1' WHERE ID = '#40167_20200407' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:524e010cbffd08f4fce01080764c7c65' WHERE ID = '#5163_20200415' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8d911f29962a3058b356315d26e025a6' WHERE ID = '#5174_20200423' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0b3599ac7754f8b4776491bfe18af31d' WHERE ID = '#5193_20200428' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9ed6834951254d277acb8fc7bcf93b42' WHERE ID = '#5174_20200504' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d2493274ba1a0091728744426486566d' WHERE ID = '#4792_20200504' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1725b4ba8f44311d76ae7ff890fb7cae' WHERE ID = '#5172_20200426' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:852993534c6fb02fbb4e4f2b8be7224c' WHERE ID = '#5206_20200505' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9f1eabc43f8834958137b8620d44965e' WHERE ID = '#5257_20200527' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d3d1dfe8f7f87f2ccd9fa502a7c1a1b4' WHERE ID = '#4895_20200515' AND AUTHOR = 'MohammedAmineTazi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ab4f4ce7a39d909a210f265ace321412' WHERE ID = '#5279_20200608_Cancellation of a billing run is impossible' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:671b8ca2b6697c916557b3a0d2e47d80' WHERE ID = '#5266_20200622' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:754d0dfaa5403580af56dd4c610200c4' WHERE ID = '#5319_20200703' AND AUTHOR = 'Mohammed_EL-AZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4f433d528d587b85df7594966c06b814' WHERE ID = '#5331_20190630' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e5e0832acc43cc042ec072b3e25085af' WHERE ID = '#5295_20200626' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:656ea887b35b912336f7dfaaac48e60d' WHERE ID = '#5330_20190630' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:42051df357bc64ef982c20d8309de32d' WHERE ID = '#5372_20190630' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ea6240a0b05e57aeacf2329b5f183a74' WHERE ID = '#5372_20200711' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5360b68913225a40564246e91ac2002b' WHERE ID = '#5303_20200629' AND AUTHOR = 'hznibar' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:33c69fe6a915c04ed79f3bcd218bbed1' WHERE ID = '#5241_20200622' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4e023267578fdc9a1f5c5d330c2ed700' WHERE ID = '#5241_20200721' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f26ea072e688964f6029452094792ea9' WHERE ID = '#5388_20200715' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0ea86807584fdf5174fc91c930553425' WHERE ID = '#FEAT-777_20200728j' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:52c336ea4fe9e5ead234e71ef7711199' WHERE ID = '#4764_20200720' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9e78b1c5f62092568d11c22875ccbed7' WHERE ID = '#5408_20200724' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:753ab875f32fe3c56af1d0e16dd7516b' WHERE ID = '#5405_20190723' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7f4f2c383dc810044768634f56d3d364' WHERE ID = '#5404_20200728' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fb1663a7c1d537e49d43874ba9f682d6' WHERE ID = '#5400_23072020' AND AUTHOR = 'KhalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:80c3668cfb4580d436634c10bfce1ac5' WHERE ID = '#4156_20120719' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b321a60a81d51423de1ff1b6a65a7950' WHERE ID = '#4906_Creation_CDR_Table' AND AUTHOR = 'MohammedAmineTazi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:46f2e7fd207df91acb0f8277d086c751' WHERE ID = '#5372_20190813' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6de623ba0f2a39e0cd80f1c01cc7baca' WHERE ID = '#5442_CET_ES' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8c76379b0a6a171e34d895877e55acd2' WHERE ID = '#5454_20200824' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8473ca073b05f08a7614f82ab6954a2e' WHERE ID = '#5303_Update_CDR_Table_20200826' AND AUTHOR = 'MohammedAmineTazi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:85f739dc012c8bc9557a101957c866c3' WHERE ID = '#CORE-5215_11082020' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5f0ee512cbad419e42df7a4a50f0c5bc' WHERE ID = '#5303_20200831' AND AUTHOR = 'hznibar' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f163583420152301bc7dfed3089766cb' WHERE ID = '#5461_20200901' AND AUTHOR = 'Mohammed_EL-AZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:941f407f3777899ff99813278b811e6d' WHERE ID = '#5460_BA_fields' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:43a5296801a6a6bbac358aab2f59050a' WHERE ID = '#CORE-5457_20200825' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:14e8f7e45e358cc9951be42eed74cebf' WHERE ID = '#5303_20200902' AND AUTHOR = 'hznibar' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7e70b814e577b86cf7cb810ae3b0e5c5' WHERE ID = '#5471-2020-09-07' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2fb4a3689809399853d75b83d398b22c' WHERE ID = '#5489-2020-09-14' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ec9260d14689fa317c234c5062435329' WHERE ID = '#5510_20200923' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ac5e814862edfa09b523003681caa7d2' WHERE ID = '#5534_20200929' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fe335a632f2fc1bf8fe46dff05c8a595' WHERE ID = '#5471_20200930' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:70abe2b4d5da026048d8f5500cc88d19' WHERE ID = '#3402_20201007' AND AUTHOR = 'zbariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6968124273fe9f977a654eab956a8995' WHERE ID = '#5556_20201006' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2656b147a6f66fe6dfcdd4f08f2cf89d' WHERE ID = '#5501_20201007' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:79fe4973f8926357bd103df883e769ff' WHERE ID = '#5155_20200928' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:524f82c324ed076c5135d5436d6ddd7c' WHERE ID = '#5546_20201023' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b164cc9d979c416d2ef6a439b6b26e16' WHERE ID = '#5621_20201104_offer_template_fix' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:64106afa5add13b68c5b57e8f7a06c15' WHERE ID = '#5621_20201104_offer_template' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9fd3639bcec2a8ae1e74d162dce1f078' WHERE ID = '#5975_20210225_offer_template_allowed_offer_change_should_be_many_to_many' AND AUTHOR = 'MounirBOUKAYOUA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:387c855560c3ced1392fa2806cdcd673' WHERE ID = '#5599_20201029' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b64b7de665a44142b86db143df51df92' WHERE ID = '#5416_20120809' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8eab1f54bab34396b6bac66ebf6020c1' WHERE ID = '#5610-2020-11-05 - Dunning workflow' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:54d43f1f0fc0b999b318d752bc4ef30a' WHERE ID = '#5587_20201021' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d85ee44b2f9cca976f3477b0cbe966a9' WHERE ID = 'AbdelkaderBouazza' AND AUTHOR = '#5512_20201113' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ca968e758acf4ed4a6bcf575d3c004b1' WHERE ID = '#5587_20201125' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:baee890c58af9e10631ae38e18c3dc76' WHERE ID = '#5214_20201117' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:69b8c94b10562b3de3ddf3a1ad704c6d' WHERE ID = '#5680_20201126' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:88a4c98191b14a5316adfc514eb788c6' WHERE ID = '5687_20203011' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:31cf24fd4d86460ef122c2c3a0aeb234' WHERE ID = '5697_20201208' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f3105ee6a1160766f3bf0686e5d3fb09' WHERE ID = '#5632_20201207_RT_TYPE' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ef5801ac8dda50e767d5fd2f57eb3796' WHERE ID = '#5627_20201212' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f935c1b92c24c35d174f024052e796f0' WHERE ID = '#4427_20201202' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:157a549880f744a6e8ee2ed9e0fc9212' WHERE ID = '#4427_20201216' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:17f9d3208ba94c89ba17678d2bb97448' WHERE ID = '#5627_20210112' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9ed47580381be489a13b910096e9474e' WHERE ID = '#5884_20210119' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fe3eec653de1fa8c1fba697d70a3f9b2' WHERE ID = '#5946_20210110' AND AUTHOR = 'KhalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e8e418f3e348f92e6b91a1e12f35f5e9' WHERE ID = '#5951_20210209_structure' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1f3d2859cadf47225d8573f6c0352200' WHERE ID = '#5627_20210107' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:431bc562446cdc0912d5df1f22db5c36' WHERE ID = '#5872_20210104' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2b59c5583857ba5c1d5decb0e7226ac0' WHERE ID = '#5700_20210118' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f476469291b7daf73b0df2579ab0e252' WHERE ID = '#5909_20210118' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:90150f9fb83464dd7ee635e64550e81d' WHERE ID = '#5904_20210129' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e7262a927b1360b6d18870b57f1e32d1' WHERE ID = '#5960_20210212' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f4529feac5c485f2fd8472df80c9830c' WHERE ID = '#5993_20210304' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b0e1bc87bb4febcf8b82cba3724e40d7' WHERE ID = '#5863_22122020' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f30631c7193c3aac5d58543da52ffbff' WHERE ID = '#5999_20210305' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f7c60043ece9c597b67c747b4f8d9e63' WHERE ID = '#6040_20210323' AND AUTHOR = 'amineTazi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d32ac33814b35c40947023444dd54e95' WHERE ID = '#6012_20210323' AND AUTHOR = 'MohamedAliHammal' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:62ecb872a546d5bebd3dbbba66f71e6e' WHERE ID = '4756_20191104' AND AUTHOR = 'Andrius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fd2f9b3da729b6d4fc4b8bb3b95982d2' WHERE ID = '4756_20210311' AND AUTHOR = 'Andrius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fb60477d9dddf9c63dc362bfe5a0d35b' WHERE ID = '6055_20210330' AND AUTHOR = 'Andrius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a8a8958d59626c3966ebf1c99462ef11' WHERE ID = '#5622_20210406' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f442643bf029d81d51c1da53b429ddfc' WHERE ID = '#5214_20200415_migration' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b7df88aeb7e080368cce4b539a3d02e8' WHERE ID = '#4742_20191030' AND AUTHOR = 'MohamedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0c54c298454ad7098fdb956625e4cc74' WHERE ID = '#6076_20210419' AND AUTHOR = 'Zbariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c39620361002e4b6859cf11fa9e26ded' WHERE ID = '#5991_20210224' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f5946e957dda9ef79c85274e545ebe48' WHERE ID = '#5623_20210427' AND AUTHOR = 'Mbarek' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:87791b37c97b7a227708873f828650ff' WHERE ID = 'rebuild-mediapost-seq' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c8b8714af1b6f3c834b5f15793f84f35' WHERE ID = 'rebuild-mediapost-tbl-01' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bada7f5666baabb2e7c49c3d76937d3a' WHERE ID = 'rebuild-mediapost-tbl-04' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d24631de0545f7f53b2ea72b5cbfaea7' WHERE ID = 'rebuild-mediapost-tbl-05' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8170d84f3ec231f6691676a90a0d51ea' WHERE ID = 'rebuild-mediapost-tbl-06' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1e677e05e6742ad740dbca15dd71c575' WHERE ID = 'rebuild-mediapost-tbl-07' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:59154417f18eaa23eb1e3d1c0e9420fa' WHERE ID = 'rebuild-mediapost-tbl-08' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b7cba67ab5e429cb49504a4028d1d5ac' WHERE ID = 'rebuild-mediapost-tbl-10' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5d0b9bb240194cbecff8b113dde5d008' WHERE ID = 'rebuild-mediapost-tbl-13' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:611f3e5ce63867a4918897b0220426a8' WHERE ID = 'rebuild-mediapost-tbl-14' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:12ce29ec26b28bde03fa4a57928b6120' WHERE ID = 'rebuild-mediapost-tbl-15' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b922ad1a5d812bd952302627c3058acb' WHERE ID = 'rebuild-mediapost-tbl-16' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4dc49de9c47047f4f06df50c17d4d117' WHERE ID = 'rebuild-mediapost-tbl-19' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:87731d851e772e554e9c65e60e4dafbd' WHERE ID = 'rebuild-mediapost-tbl-20' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7a9e8162f1ae0f8102cf9576a163d982' WHERE ID = 'rebuild-mediapost-tbl-29' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fec0b2e8101b573e96d77c6b81990188' WHERE ID = 'rebuild-mediapost-tbl-30' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a90c125222058dbf63849fd2bff6c2fa' WHERE ID = 'rebuild-mediapost-fk-01' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3d20db7d72355d583b6f5cebb95d147c' WHERE ID = 'rebuild-mediapost-fk-02' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2d4728da448397f8b8a440bebde8084f' WHERE ID = 'rebuild-mediapost-fk-03' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a72e442bb9a81084a6e71b20d02ca95f' WHERE ID = 'rebuild-mediapost-fk-04' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:dbdbd912dc260fb0c35210ae2b458945' WHERE ID = 'rebuild-mediapost-fk-05' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3faab5e5295fd23334342a3ce5d259b2' WHERE ID = 'rebuild-mediapost-fk-06' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:51e4c48a4fe67e0c904c2d78301b4e7b' WHERE ID = 'rebuild-mediapost-fk-07' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2b3e41c693b2a4316c727392fa2cf4dd' WHERE ID = 'rebuild-mediapost-fk-08' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:cad03916bac3d50d42fe9d9c24a6018e' WHERE ID = 'rebuild-mediapost-fk-09' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:65142d9ae84dde1ff51213d9e8135b0a' WHERE ID = 'rebuild-mediapost-fk-10' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:06d41d829bc3d5df11680ae55ddb6194' WHERE ID = 'rebuild-mediapost-fk-11' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:04069bbc4c1dba931959a88a14aa3e3a' WHERE ID = 'rebuild-mediapost-fk-17' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a5589dd82f3cbc2305aa7191007c5d6e' WHERE ID = 'rebuild-mediapost-fk-19' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6e5aec4d664bddaaba14e8fc9545b8ac' WHERE ID = 'rebuild-mediapost-fk-20' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8362b603ba71f8d82a47cc49271e1156' WHERE ID = 'rebuild-mediapost-fk-21' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1d3cbcdae1618eb20bdae4c2a9cd1407' WHERE ID = 'rebuild-mediapost-fk-22' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3325a0234ca1e877b93a17bf19ac9a74' WHERE ID = 'rebuild-mediapost-fk-23' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:35c6cf5890557cf3cb23ef857221a230' WHERE ID = 'rebuild-mediapost-fk_commercial_rule_header_rule_item' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a43feccccee883d8f684f6fd668975e2' WHERE ID = 'rebuild-mediapost-fk-24' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e9a6aa76fea7e483df1cc40d0668532f' WHERE ID = 'rebuild-mediapost-fk-25' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c19caf1b41f2d4bdd80678ceb25c204e' WHERE ID = 'rebuild-mediapost-fk-26' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:21c1ca3619ea6479919a499354793c79' WHERE ID = '21112020-540-23-entity-quoteCustomerService' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2ed597dc840534b863b818b0e2742268' WHERE ID = '21112020-540-23-entity-quoteProduct' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:26753860b831f57c461ca14a5dd5fbfc' WHERE ID = '21112020-540-23-entity-quoteVersion' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:13523b49eb2bfc09ced721d4fe2cbccb' WHERE ID = '22112020-540-23-entity-contract' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d3498568ba40d166db8725575fc2f6e8' WHERE ID = '22112020-540-23-entity-contract-item' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fb3981f5001a7d016a10dd8f3461a862' WHERE ID = '540-30_20201023_PricePlanMatrixItem' AND AUTHOR = 'TarikF' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:13919e671333417a3a829a9c94687dd0' WHERE ID = '540-31_20201023_PricePlanVersion' AND AUTHOR = 'TarikF' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a393362fd732ce433ca2435889b39b35' WHERE ID = '540-15_20201019_servicetype' AND AUTHOR = 'TarikF' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:93759bef775fbdc044600b73157a726d' WHERE ID = 'current-str-fk-32' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:71d300c0f194dcbd32700939f5794784' WHERE ID = 'current-str-fk-33' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:60be035038773f099b841ff3f9f9ef51' WHERE ID = 'current-str-fk-34' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:72d0d1f6b3e0bdef6a774b1fb7059137' WHERE ID = 'current-str-fk-43' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:105a9bedbf0c63049645d04014985ce6' WHERE ID = 'current-str-fk-44' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3911e00d670f7974d91650c839e879a3' WHERE ID = 'current-str-fk-45' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:662d07beaed943ec1a5b0bf7a38814a0' WHERE ID = 'current-str-fk-46' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:dda3537d3c8b79ae87a0a623fedda43b' WHERE ID = 'current-str-fk-47' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:07d9ef7bf76f4d450f359dbbbf239c61' WHERE ID = 'current-str-fk-48' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:82b3f866c4d1eef1156b9b7a333afb46' WHERE ID = 'current-str-fk-49' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4ae9ae20c693ad6f1c7a97fa38a50988' WHERE ID = 'current-str-fk-50' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:01bb3ff7b94c71596df33e01edc8e2ea' WHERE ID = 'current-str-fk-51' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9400ad1124ac8ba67232ff272b2582e9' WHERE ID = 'current-str-fk-52' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2a8c3979f61075dc7f2eeff8ef8786cc' WHERE ID = 'rebuild-mediapost-fk-28' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:168517652fdc3ce86800d3117e407f80' WHERE ID = 'rebuild-mediapost-fk-29' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:527f01c1fdd643ab9379407f0a6a3df6' WHERE ID = 'rebuild-mediapost-fk-30' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e6217252921023e3ee691bab5cead630' WHERE ID = 'rebuild-mediapost-fk-31' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:749274972b471888c86812b17988783b' WHERE ID = 'rebuild-mediapost-fk-32' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9fcb03a80040f2793b8c47900dbdc091' WHERE ID = 'rebuild-mediapost-fk-33' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9df9f9257d7eec1abcf4cdc13a35e693' WHERE ID = 'rebuild-mediapost-fk-35' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:91a685d9ad3a5f39fe55ec6b4ba053bc' WHERE ID = 'rebuild-mediapost-fk-36' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b76ef546fdf4428285125e248ce5acce' WHERE ID = 'rebuild-mediapost-fk-40' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:67883e6ae3143275cad36c93de155218' WHERE ID = 'rebuild-mediapost-fk-41' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bcdbe59c10d4608a2c002443cdced491' WHERE ID = '#5595_20201023' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d7b4309ed3ea5ef85112804dde8bb58b' WHERE ID = '#5595_#32_20201023' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1f48a8ef795d77474bf5499ec9572d97' WHERE ID = '#5595_18_20201102' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:05f8a272f5f1e7293ab2e510d3988494' WHERE ID = '#5595-36-20201103' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4ec0f10ded1725a7d49dd266fe815085' WHERE ID = '#5595_49_20201118' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:292c7d066eb4edf90092f85439d17b5a' WHERE ID = '#43-20201113' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bb646238b02798a9112116422908e7d2' WHERE ID = '#5596-20201113' AND AUTHOR = 'Rachid.AIT' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:afe425a92a7789c19289533d2d28148d' WHERE ID = '#5569_54052_20201117' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:80705135ff526a8711e1363b5c8dcfdd' WHERE ID = '#51-20201123' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:96d58f1fc05e6967fc5e2531507b6300' WHERE ID = '#65-20201127' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9e8d3494c30900616d99f1b3f3b00afb' WHERE ID = '540-66-20201201' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ced8372ec7d93bf2c4488d6b2b1b3098' WHERE ID = '540-69-20201202' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9464c7b745176889f171be24103c37b1' WHERE ID = '540-75-20201203' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c74c88050d818ff5d43caa490260bfda' WHERE ID = '#5569_124_20200118' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:233391776987e38d5da5e6f6f90f54ee' WHERE ID = 'current-str-fk-35' AND AUTHOR = 'TarikFAKHOURI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1d3bba05f889e69749aa66b36a8268a8' WHERE ID = '540-84-20201209' AND AUTHOR = 'TarikFa' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b46ad4e498fabdd8ae96ce11d03ac7e3' WHERE ID = '540-83-20201211' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d9bff30f9832642ac4545ebb49e85b1d' WHERE ID = '#FEAT-761-2020-10-19' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7945740eb7676bcc640facb71382d645' WHERE ID = 'rebuild-mediapost-tbl-' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2c310adabbc4f47c00fb45dcda86cd8d' WHERE ID = 'current-mediapost-tbl-charge_attribute' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3d279b4ddfacc79ee955b2ff651edd48' WHERE ID = '#REF_INV_20201223' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:74c5325d58f24fb62bf2efa15c75f06c' WHERE ID = 'rebuild-mediapost-fk-27' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:efad974db8502851480af674fd0c49de' WHERE ID = 'rebuild-mediapost-fk-41' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:478bc8cfcd17321a767ca649145eca32' WHERE ID = 'rebuild-mediapost-fk-39' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ec6a79656947f9a0e173c91eee5ce30b' WHERE ID = 'rebuild-mediapost-fk_grouped_attributes_commercial_rule_line' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e2974aa34cf26640bc05172fdbc13d13' WHERE ID = '540-103-add-swagger-docs-to-All-ChargeTemplates' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9bfc11368a5cd5789f704726233eeee5' WHERE ID = '#540-100-20201225' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:13bcedd299fa6e5c7431b1dd0a0a79d3' WHERE ID = '20201230_pricematrix' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f4fe71a889f517525cb217593736302d' WHERE ID = '20201230-ticket5870' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:194b6862c08b26b699f642ccc35940d8' WHERE ID = '540-112-20210107' AND AUTHOR = 'TarikF' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:72c8d9742117e033955bc3217427124e' WHERE ID = '20210113' AND AUTHOR = 'TarikF' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:307538a183cc9ed8a9a0f63d98fd871f' WHERE ID = 'rebuild-mediapost-fk-attribute_tag' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:133a9c4118dff7bd4d4a964f5eaa3ee6' WHERE ID = '540-139-20012020' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:23525d2e3bdeb398a7d2569b9b13084f' WHERE ID = '#20210121-540-128' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:77ca23730a6535f89995a98ead31467a' WHERE ID = '#20210122-540-128' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1cb836ea01b70bc19e832d5d406fe315' WHERE ID = '#20210127-540-190' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:cc85ae288713069de151519bce801db7' WHERE ID = 'Invoice_lines_job_20200114' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2123fbcdac14f0aaab5873409b2ac340' WHERE ID = '#5916_20210128' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7cd0e2183bc035a2e76f519ff27e3c9c' WHERE ID = '#5937_20210204' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:08d251eef97f7188a20b672578740daa' WHERE ID = '#214_20210203' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:733db12fabc80e827f9e970377e81d68' WHERE ID = '20210204_540' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0dba53766181f94cbfad00ccba8d2a57' WHERE ID = 'Invoice_lines_job_202001145' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9bd25021f5935067610a32b783ef57ec' WHERE ID = 'Invoicing_job_20210205_45' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7e1c46c0348ccd1d3ea30ab090025351' WHERE ID = '#20210205_540_223' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a1aa0ee4c1327c2714df5b84939883fe' WHERE ID = '#5938_20210205' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:659a55aa0f73ddfb58126f65172300c2' WHERE ID = '#20210209_540177' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6bb01dd7790b4b9ead471ee5f2dbab46' WHERE ID = '#20210215-248' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3af9a88950644dc055eeaece9646319a' WHERE ID = '#20210212_540238' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0930894e83403f967170ca2921652020' WHERE ID = '#5950_20210210' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4d86ad04c5ca08b576ba972ab12f22b1' WHERE ID = '#246_20210212' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2d8dfaa49f9fbf3a25aa9cd500dd7133' WHERE ID = '#248_20210215' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:278b8a43ad1e82f96785370512b92f73' WHERE ID = '#20200215-247' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1c9f882bddee7bf08a5a198e73afc14f' WHERE ID = '#20210217_260' AND AUTHOR = 'TarikFA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:427a72718a0ae06d49a44a5b27e7c16f' WHERE ID = '#5952_20210210' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:37cf8a42db237522aca40335ca053398' WHERE ID = '#268_20210219_cpq_structure' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:17333905b895b6aad6e847bc4f63927e' WHERE ID = '#222_20210219' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1c9426c1b7f27af8e7f2aa11791c0f56' WHERE ID = '#270_20210222' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e294deb4f0b85add75978a1fa9f70cea' WHERE ID = '#20210222_540_269' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2c851f6754fad4107ab57079f3913ed0' WHERE ID = '#20210123_540_280' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:90008e6d678b0d7442a308d0e180bd9f' WHERE ID = '#20210123_540_281' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2e8494b37fe61725cfd0f57a37aa3404' WHERE ID = '#20210224_5868' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c2d75fee18b37426cb7a6ef93751ab42' WHERE ID = '#20210222_907' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:25a7bda5cecf5cdc0babc3d0d2826327' WHERE ID = '#20210302_540' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:604dc51179d820ab23c157385cb3f8a2' WHERE ID = '#20210303_540305' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f4e3720fe3586b256d116410b458aba1' WHERE ID = '#20210304_540320' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:73321ab1e782219870d356181b3e7f73' WHERE ID = '#20210304-312' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2a3b16a41325119b93e81894d2e0ebfc' WHERE ID = '#20210310-342' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:763c918f6539f9d6131d52c1e027de68' WHERE ID = '#20210315-329' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4ab01209e2347a272af4d491dbe032dd' WHERE ID = '#20210316-356' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b6147b763b76b4dacb2bcea36b7fbfd7' WHERE ID = '#20210316-356-2' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:29f93ab29235bdf6a4924867ea1a272f' WHERE ID = '#20210317_M540_16' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bda336010554b21ae201d2ca53013fd3' WHERE ID = '#2020317-342' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:852cde054e615391625736717427f315' WHERE ID = '#20210322_377' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4ee8d32000f9daa5b8e28333cb3aafdc' WHERE ID = '#20210323_380' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:09d4829f1e6261daa1ea8159cdb07ca4' WHERE ID = '#20210323-382' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d11bc884839aa2233e564541749cfe4c' WHERE ID = '#20220325_360' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9deadefecffebd112fe2eb1f15b3048d' WHERE ID = '#20210329_399' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:19fabd4be3ca936432906a6d5f063566' WHERE ID = '#20210401_402' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bec0b16843a9f6b5d86d8e8c2acd0ae0' WHERE ID = '#20210406_opencell-portal-593' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d32ac33814b35c40947023444dd54e95' WHERE ID = 'rebuild-str-fk_6012_20210323' AND AUTHOR = 'MohamedAliHammal' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:599090ece3a67fcba3db7240096240a2' WHERE ID = 'rebuild_5621_20201104_offer_template' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3b518c9ddffc7040c7b8494f6cfc8062' WHERE ID = '#20210415-435' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:dcbbe870d9bb16c7c304a12957493f33' WHERE ID = '#20210416_441' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2502839e88875b14ce98262fcc0db384' WHERE ID = '#470_20210422' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6a663fb38620e8cd3bfe93a5a4132437' WHERE ID = '#cpq_add_missing_migration' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:da47865a47bcad365a3f6a62547de6e6' WHERE ID = '#5937_20210428' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e0f73650092ca9b765b729fb079a3f00' WHERE ID = '#5595_540498_20210430' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8e61874ab99e223c10e0f96ca8b657a8' WHERE ID = '#5595_540499_20210430' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1d883948cd8ee8a9faca080df5dc137a' WHERE ID = '#5595_540504_20210503' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:199e6fba58115647ba890f1d9ad62a38' WHERE ID = '#6076_20210504' AND AUTHOR = 'Zbariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b03413f3da2569df06d9c67e133027a3' WHERE ID = '#5595_20210506' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b74a82c8f232e7e603cba8ee9deb1c88' WHERE ID = '#5595_20210512' AND AUTHOR = 'RachidAityaazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:370bd1db43d179a9aa1380bef48bca45' WHERE ID = '#5623-562-20210520' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a281f076a491e887d2960457dc458f31' WHERE ID = '#5257_20210519' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a60aa71adaa4dc4d6c44eb4d1d0a020f' WHERE ID = '#533_20210526' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c132d6c8b180bf0f46b0401ec4fc1bf1' WHERE ID = '#5869_20210521' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:32eb98f47a3c8122a9891b01c6fbe40e' WHERE ID = '#5595_540582_20210528' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:78cca25c19fdfdd50e21d4a15057b7c8' WHERE ID = '#6147_20210530' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:10a68226744c9a3fec72025a12dfbe3e' WHERE ID = '#5599_20210526' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2b89ef2ce50a4b7771071e8329d1bfea' WHERE ID = '#5416_20210526' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6d9056293d8a599ff22c119c0eb55055' WHERE ID = '#559_20210603' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a92e7c9a35e9040df3020ef3b3165700' WHERE ID = '#20210608-540-527' AND AUTHOR = 'Rachid.Aityaazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b1b27b9ba56b69723921f1b732520406' WHERE ID = '#20210611-540-637' AND AUTHOR = 'Rachid.Aityaazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:18d9470102d964bded5b66b906917a49' WHERE ID = '#20210614_540_update_script_quotation_and_order' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:033d87ee3cc3be0b132196c57c08d2a3' WHERE ID = '#20210622-INTRD-272' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0d9177dc9d4a5411184c1a0a6088a715' WHERE ID = '#INTRD-242_20210623' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c8a24558ff588d8687ed96aafe02cceb' WHERE ID = '#5595_540582_20210528_2' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4c7aaaefe06dd24964d637e8504eb5ba' WHERE ID = '#6168_20210624' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ec1614b7299ed41858e8536977cafae5' WHERE ID = '#INTRD-257_20210706' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e4327a289db097e5965aeb1e9b0f00e6' WHERE ID = '#INTRD-248_30062021' AND AUTHOR = 'Mounir_BOUKAYOUA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:96e8332291a7ce169f5be4db0cd3bca6' WHERE ID = '#INTRD-254_20210706' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d4cb886643903620041ad9b9996eb9cf' WHERE ID = '#INTRD-264_20210714' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5fc28956b84e823ecdfbe0bb8b1f304e' WHERE ID = '#INTRD-339_20210625' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:725e9435802234aa19615abff6f89601' WHERE ID = '#INTRD-257_20210625' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:cc78e36bd962ca34c084d7f07584cfd9' WHERE ID = '#INTRD-682_20210707' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e6d4bb3af785d659063cc537b9dbfc82' WHERE ID = '#INTRD-634_20210706' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8fef5ccc14e80b6dfa41c24fb1de2a47' WHERE ID = '#INTRD-1090_20210714' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:806462b8b1f11835a75c3f84aeb8e9be' WHERE ID = '#INTRD-754_20210727' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a1864e94f77567e8de613585306b3405' WHERE ID = '#INTRD-1174_20210802' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e0d676cf51bee74a3d9dbb56b9b92072' WHERE ID = '#INTRD-760_13072021' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a7dbaf241fe33aa8c9ee4c803fcfac8a' WHERE ID = '#INTRD-1291_20210804' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8a4ae9fd5a1413f121f6102ec65afd5d' WHERE ID = '#INTRD-1275_20210805' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:50554e80e61815a93b2c57f5ac51f108' WHERE ID = 'INTRD-1310_050821' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bd2dfb3a7a5151c4feff5e867fcca94a' WHERE ID = '#INTRD-1277_20210810' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ec472c0eb0067bf9b7f726b3a52303f1' WHERE ID = '#INTRD-1357_10082021' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:35a27ed691708149fa29059ab006c6b9' WHERE ID = '#INTRD-1313_20210809' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9ad29ca72d04bbdc0358f7d4450d5e48' WHERE ID = '#INTRD-1380_11082021' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6ba96350219b2f58838cd1b52dc3a04e' WHERE ID = '#INTRD-1365-12082021' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:eca83b431daa16d63a2d9c1ad1ef1d77' WHERE ID = 'INTRD-1221_20210803' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:55a42313ddeed08f45df9f50a3494c55' WHERE ID = '#INTRD-1396_20210812' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7c325268679af261027b53693b3f15fa' WHERE ID = '#INTRD-1384_17082021' AND AUTHOR = 'YoussefIZEM' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:183262af23a593f37ca0b9bcfcbf3fdc' WHERE ID = '#INTRD-689_20210818' AND AUTHOR = 'RAityaazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3385c35c3fc6f532f5ef17a60442f5be' WHERE ID = '#INTRD-1386_05092021' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7d23686b4139f522fba28533c2fc184c' WHERE ID = '#INTRD-1441_20210728' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:78724428f9ce20be5122eba7d9fe0e4f' WHERE ID = '#INTRD-1461_20210820' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:cadb246cb14820e6f4b9af0b431c7cd8' WHERE ID = '#INTRD-828_20210817' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:398b3cbd593002397dd2d18578734364' WHERE ID = '#INTRD-828_20210824' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:cba71151c19ff82997b7bbaae10e0c02' WHERE ID = '#INTRD-828_20210824-accum' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e679bb47b52f53827b8cd55039aadaa9' WHERE ID = '#INTRD_1609_20210902' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:958d313967369be13bac0b13ba8e7f01' WHERE ID = '#INTRD-1654_20210902' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:aa44d8008b71fc94acd862b0a92e1af1' WHERE ID = 'INTRD_1687_20210906' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6973234dba891d2b05651be9fcf65cfb' WHERE ID = '#6149_20210531' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:928d977c74b93f6a7ae1da37fec0b278' WHERE ID = '#6117_20210608' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3b6a7a852ff32b67418c16ff2749b494' WHERE ID = '#INTRD-1651_03092021' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:843478c4e096afa7a1e7794cfa2af97e' WHERE ID = 'INTRD_1629_20210914' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d68e06ec2db179e3e1eefca98cc2c6e0' WHERE ID = '#INTRD-1707_20210917' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2abeeca98175aa79c947b947489710ef' WHERE ID = '#INTRD-1581_20210902' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3c7ffcce6d6ff79cb288e55ae1b075c7' WHERE ID = 'INTRD-1901_20210920' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b51219309f78725cc9b59357e40efecf' WHERE ID = 'INTRD_1782_20210913' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b11ef1f5599d09d163010cca61f35cba' WHERE ID = '#INTRD-1692_20210906' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:74029ccc41c6be007323c7a9f42f9e78' WHERE ID = '#INTRD-1692_20210906-pg' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8a7c0c346e3512e09c93a00635b07b75' WHERE ID = '#INTRD-1692_20210907' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3e6f62a7a4abbbbc25b9b73146900d6e' WHERE ID = '#INTRD-1692_20210908' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:182d2a2e01f1e50342dad9cc9b365cf8' WHERE ID = '#INTRD-754_20210915' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a0e4320cbed213da7eb6a7dff33918e1' WHERE ID = '#INTRD-1994_20210923' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bc6e4f90152a40e0034b6fbc0a755e73' WHERE ID = 'INTRD-2046_20210927' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:13b1978b98c137d23a1e734937eb4d14' WHERE ID = 'INTRD-1924_20210922' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:15b709165ba36441095c6d3bfa761d7a' WHERE ID = 'INTRD_1985_20210923' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:872a04e5e7dd0c28bdcf15261ed34ea7' WHERE ID = 'INTRD-1925_20210922' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3e4371428bcf3af9e0e40f570ea544dc' WHERE ID = 'INTRD_1794_20210917' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4b1ce5296920939bd7410948488c2f05' WHERE ID = '#INTRD-2184_20211004' AND AUTHOR = 'MohamedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bdbe4d271ad19d4e3a4f54564451bad9' WHERE ID = '#6117_20210622' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7aded5c0371b3eef3f2534d5bd4c3ca7' WHERE ID = '#INTRD-1275_20211011' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e032b73afd6563b9af792b58639d9b59' WHERE ID = 'INTRD-2252_20211006' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b82b4bb444aabb2235c6c7a4f2f2650f' WHERE ID = '#2374_20211012' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:309bab0406a51a4f29c307eadd768387' WHERE ID = 'INTRD-2412_20211015' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4c54ebd8a53b1ce352f0e3d770772c98' WHERE ID = 'INTRD-2444_20211018' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:62471882a6c455ab43c6943f51ed9a2d' WHERE ID = 'INTRD-2535_20211021' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0f4834f5255246cc78bab33f208ab9a8' WHERE ID = '#INTRD-2421_20211012' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c8f45d2c77cb727733f004bf28b7b0bf' WHERE ID = '#INTRD-2421_20211012-pg' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6af128cd3d0818f0f11dd3ddb29e1ac7' WHERE ID = '#INTRD-2421_20211012-end' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f531613366229ebf48c7c0d74921e973' WHERE ID = '#INTRD-2421_2021012-reports' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5ce66af48cd7de91b8e27a8b961dac45' WHERE ID = '#INTRD-2421_20211018-reports' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:aa4384d064ee454c0e3a6827eb9dec72' WHERE ID = '#INTRD-2322-20211018' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f28164036d5c65d5219383ac34558402' WHERE ID = 'INTRD-2444_20211019' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3048b69ae01eace79eceb2bef47c1b2f' WHERE ID = '#INTRD-2364-create_data_model' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0df2a211eec4344c20e69324d4bddf17' WHERE ID = 'INTRD-2431_20211014' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fe8f9ad7d9e156cc39b825d346ca42a7' WHERE ID = 'INTRD-2431_20211021' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:be2d762c72c8dbad32b9eb34dd7f709c' WHERE ID = 'INTRD-2529_20211021' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5b8413c5c52e221332ecac624496d890' WHERE ID = 'INTRD-2366_20211021' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:485a75dd0686c396a9e1ca4ef5476214' WHERE ID = 'INTRD-2445_20211021' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:efa50481ee88eb25f94a5a8d91b4f1f5' WHERE ID = 'INTRD-2596_20211022' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e7dbd7256413670adfd9ca2d2079c6c8' WHERE ID = '#INTRD-2628-20211025' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:930e3378fc80f934b2c3f20b727be845' WHERE ID = 'INTRD-2679_20211027' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bd0c4d173268fb5c3180c2c4e10924ca' WHERE ID = 'INTRD-2498_20211019' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9d88ad558e1f28ff4671edc2ef0ec85e' WHERE ID = 'INTRD-2431_20211021_fix' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:544176db85e563e4360e88a2d4bb4b60' WHERE ID = 'INTRD-2548_20211027' AND AUTHOR = 'YoussefIZEM' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:92d1cb854e91cfad6945a48a829659ba' WHERE ID = 'INTRD-2601_20211102' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:305264eb8a027b07d3e5e50fe3bf64ce' WHERE ID = '#INTRD-1166_20211018_Business_attributes_default_value' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f194069f1be53189dec5a900bea806c4' WHERE ID = '#2333_20211021' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:14225e0596a36324b1cd658cc639ee0a' WHERE ID = 'INTRD-30_2021114' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6cb4fc858eaf7d2b3a7da548a45c096c' WHERE ID = 'INTRD-2449_2021114' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f70ed00a74969544200f46ee70d3e043' WHERE ID = '#INTRD-1254' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d41342e1a319682441ebcc164b6fdbfe' WHERE ID = 'INTRD-2868_20211105' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:02918a4caca908ea084a3952dcdd7b0e' WHERE ID = 'INTRD-2418_20211105' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bb303da36e3a00e572ed4f4f663b8f05' WHERE ID = '#INTRD-1889' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:768b016f916fb0c85b38b656c9a1b950' WHERE ID = '#INTRD-1419_20211108' AND AUTHOR = 'HHanine' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9b6cdc0a31aa4dc48b9187dead71374b' WHERE ID = '#INTRD-1418_20211202' AND AUTHOR = 'HHanine' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:81bdb86aa5b94c513904b10db01a8f95' WHERE ID = '#INTRD-2800_04112021' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:58f5d72560d21b828263f7c3605cd8da' WHERE ID = '#INTRD-2961_20211109' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:643ef4a44cd4b096f615598c3ca23619' WHERE ID = 'INTRD-2959_11082021' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a220704ef01e1b15e66874c33154f8c4' WHERE ID = '#INTRD-2963_20211109' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:690d951b595df665f46093921af1ad6c' WHERE ID = '#INTRD-2764-20211104' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:622623088afff8b0bfea90c445f4a76e' WHERE ID = '#INTRD-2973_20211110' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f697e28c3e043c7857083df00f8e5272' WHERE ID = '#INTRD-3250_20211123' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9716c574a9c058cd13d2372bd44975a0' WHERE ID = '#INTRD-3307_20211125' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:eb352ca4123f175a5e2bddceea8e1f3a' WHERE ID = 'INTRD-2425_20211014' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2be2ef1ed6dce4b14edd3d769043cd20' WHERE ID = '#INTRD-3426_202112012' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:dfb4f16ab914c0049b918262e587fc9a' WHERE ID = 'INTRD-1421-Payment_deferral' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7f741bdc858f974cf8fbbb4ba1ebd920' WHERE ID = 'INTRD-3610_20211210' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3d432369ae31937db370cadcaeac6f0a' WHERE ID = 'INTRD-3721_20211216' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7a582c818061f5de960d8b40080f7b1a' WHERE ID = 'INTRD-3707_20211216' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e845623a590f302cf15073da3fbbe373' WHERE ID = 'INTRD-3781_20211217' AND AUTHOR = 'hichamElHaloui' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bb46c455c55634f954f3512f0390a790' WHERE ID = '#INTRD-3461_20212128' AND AUTHOR = 'hichamElHaloui' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d0fc3cb519eedd8b620e13c058c4547c' WHERE ID = 'INTRD-3807_20211227' AND AUTHOR = 'HHanine' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:148cb8328cf681145f94e030d8ed2665' WHERE ID = '#INTRD-4072_20211229' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:49b25fe797aff140f0c0d75c030d355e' WHERE ID = '#INTRD-4178_20220106' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:70594119f4eafcb9e9e100b671a9bb9e' WHERE ID = '#INTRD-4457_20220117' AND AUTHOR = 'hichamElHaloui' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:228423e959a1bb6c6ed5c14e1920dc7a' WHERE ID = '#INTRD-4454_20220117' AND AUTHOR = 'hichamElHaloui' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e6cb76ed29613611914ce9b82b997fb4' WHERE ID = '#INTRD-4410_20220117' AND AUTHOR = 'IlyassTalal' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:338b32f48b0e31e54c09b929f7264ecb' WHERE ID = '#INTRD-4677_20220125' AND AUTHOR = 'hatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4ea5debd52c8eaacf522a2e3683fff85' WHERE ID = '#INTRD-4705_20220127' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1a1ac46ad4d2c121616e0ce6aebf9d7e' WHERE ID = '#INTRD-4705-cpq_20220127' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:00c90aa9f64533870aa2829b910d54e3' WHERE ID = '#INTRD-4654_20220203' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1d109937c2bc78a2954bb35bff8ee9e0' WHERE ID = '#INTRD-5636-20220318' AND AUTHOR = 'hhanine' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8d5846ed865d27adbaff88a058a2d050' WHERE ID = '#INTRD-4654_20220203_2' AND AUTHOR = 'HanineHicham' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fcafac758ee629af79a7640eb82305ae' WHERE ID = '#INTRD-4777_20220208' AND AUTHOR = 'hichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5236a6f438add3c2d01f1885f5ca80e4' WHERE ID = '#4065_2022' AND AUTHOR = 'Mounir_BOUKAYOUA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0a927687db968931bcb9ed51d5ab6653' WHERE ID = '#INTRD-4682_20220209' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c2833d6077b4d6be5e74129699643850' WHERE ID = '#INTRD-5200_2022-02-08' AND AUTHOR = 'MohammedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:53e68aa51cde9f32abb97750d4059e71' WHERE ID = '#INTRD-5274_2022-02-22' AND AUTHOR = 'HichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6f6af8051e16886ee58625b5c974ccea' WHERE ID = '#INTRD-5286_2022-02_23' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6916506ad872e3b5824419639d430542' WHERE ID = '#INTRD-5306_2022-02_24' AND AUTHOR = 'hichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:467556d98c982f6957cc5458d20e00ee' WHERE ID = '#INTRD-5343_2022-02_25' AND AUTHOR = 'hichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2b2755c782e67d2d46b1185b7b11c501' WHERE ID = '#INTRD-5385_20220228' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:682daeb5e6829b8f127a7ef524af9d19' WHERE ID = '#INTRD-5529_20220304' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:79a4849dafbe57b1097a9563bd2a6041' WHERE ID = '#INTRD-5458_20220304' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f42dbe73b5e0112b165a13531fa229be' WHERE ID = '#INTRD-5276_2022-02-24_PG' AND AUTHOR = 'MohammedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:93aa397e98bbba84349da33e79ff1507' WHERE ID = '#INTRD-828_20210902' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:dd7846f6b2b9b6501f5562eb177296b7' WHERE ID = '#INTRD-828-drop-view' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:67c81e37d4c9f6ad290add855be527a2' WHERE ID = '#INTRD-828-create-view-fix' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f8b6a06a44d8b62d869b7ff7153713df' WHERE ID = '#INTRD-4950-20220209' AND AUTHOR = 'MohammedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:28f026ce8c88bee06e707b93e8ac6fa3' WHERE ID = '#FIX-delete-tax' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e97ae1162a628e8f3a1f14d90d3927cd' WHERE ID = '#INTRD-5628_2022-03_11' AND AUTHOR = 'hichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9ba588d3e672247e6fb0884370eb5063' WHERE ID = '#INTRD-5419-20220308' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:39685603e521cfac176ac89c3179dbf6' WHERE ID = '#INTRD-5310-20220310' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ecc939dcac79751c8f39c315f5329faf' WHERE ID = '#INTRD-5341_20220307' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:70b0923f76a0a69adbfd5d1891d4e68c' WHERE ID = '#INTRD-5804-20220321' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1d49f4c688005e629475fcb2a30a2b1d' WHERE ID = '#INTRD-4852-18032022' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6a63f056a9636c8ef1371ded56e7ea5b' WHERE ID = '#INTRD-4702-20220321' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:17164c7a3000e0088416e433a74ac023' WHERE ID = '#INTRD-5841-21032022' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:19270ceac540484ff46622d8f6fa45e1' WHERE ID = '#INTRD-5881_20220322' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9ebe41f8343f5858b2e3ea0cc4b5f8fc' WHERE ID = '#INTRD-5921-20220324' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:50bf6b9da5e61e40743ebbc452d451d0' WHERE ID = '#INTRD-5846-24032022' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:385d94ec393ac45fdd84e99daf6e7193' WHERE ID = '#INTRD-5994-20220328' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e5fcd94e8c7b05ec748fa488a6086595' WHERE ID = '#INTRD-5908-20220330' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:92c722319528235719b4bae570b78424' WHERE ID = '#20220326_M540_178' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4f4d29c0ce7c7aee9288ba748527c9c1' WHERE ID = '#INTRD-6160-20220401' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:edee7e8795d019dd590ac7985067fce9' WHERE ID = '#INTRD-6136-20220406' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:dbb25acf71be9235397ff6c8a00e88cb' WHERE ID = '#INTRD-6298_20220405' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3ce058d5ee92892b7be324a73c135fe9' WHERE ID = '#INTRD-6263_20220406' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:cf9abd54439e1a3bf1aff0eb4fe16d40' WHERE ID = '#INTRD-6264_20220406' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b858bfbf52417e8c8668bac6629f3aa9' WHERE ID = 'INTRD-6012-07042022' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2098cea0125536d5debe5c586e1ec6da' WHERE ID = '#INTRD-6453_20220411' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d84dd90310b7a9a5e5df4372fb362117' WHERE ID = '#INTRD-6056_20220413' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a3019c24fcfeee5fd8fde770c2cc008a' WHERE ID = 'INTRD-6474-13042022' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ca28e6f3cb10064e1132d0f38bf5cdfe' WHERE ID = '#INTRD-6562_20220414' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e0b2662573f01da1e05b98c7dc2c2255' WHERE ID = '#INTRD-6562_20220414-new-field' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ae9962a5840f9752c00a10a04b1f843b' WHERE ID = '#INTRD-5859_20220321' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:91454b1ef090bd723a87f36352616de9' WHERE ID = '#INTRD-5872_20220322' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ea983b3af166daa673fd49ea3ce6b9ef' WHERE ID = '#INTRD-5872_20220326' AND AUTHOR = 'R.AITYAAZZA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:367890c0902695d7500daba4b5bd056b' WHERE ID = '#INTRD-6108_20220330' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:aae28ef6de4b75d2d3b356e507b060a2' WHERE ID = '#INTRD-6631_20220419' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0faead04f95665d7da1fc769f720f15b' WHERE ID = '#INTRD-6614_20220418' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b6fa13544975dee3dce775476e36b160' WHERE ID = '#INTRD-6664_20220420' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:11cfcbd745e790cc1d5081e8e6b3d88b' WHERE ID = '#INTRD-6633_20220420' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a8396a3e11b9fff1753b9ef342ad12c0' WHERE ID = '#INTRD-6536_20220419-change-on-field' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:300cfca34e6f45e99f68ffb41e32fe19' WHERE ID = '#INTRD-6536_20220419-new-fk' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:af2bb0cc3fdc021b11c57ae633db5cf4' WHERE ID = '#INTRD-6668_20220421' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:75979e48754586f25045f9b623f969b8' WHERE ID = 'INTRD-6608_20220420' AND AUTHOR = 'AkadidAbdelmounaim' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9952722f8b9375dc4b0a9445aee0014a' WHERE ID = '#INTRD-6779_2022_04_28' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:384f5d853f3c3b6e3d842af42bc6cdcf' WHERE ID = '#INTRD-6899_20220502' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0324fd75d72a065e5d9be98255302460' WHERE ID = '#INTRD-6895_20220504' AND AUTHOR = 'HichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1d49f4c688005e629475fcb2a30a2b1d' WHERE ID = '#INTRD-6923_20220505' AND AUTHOR = 'hatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1bcffffa5031ebe7fea3fcb4f65c77b8' WHERE ID = 'INTRD-7006_09052022' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:081c2b96e09ee7a2fd2444180d078c06' WHERE ID = 'INTRD-7038_11052022' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4613b1d4563c9241d4d32e62a5843dd7' WHERE ID = '#INTRD-7149_20220511' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7c93c0f84ef31eb805ae043196a259f6' WHERE ID = '#20220404_M540_6296' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e376410c6207047db689c04d9d755d55' WHERE ID = '#20220407_ADHOC' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a326f53226cabdaf9cabd6f0200e6d67' WHERE ID = 'INTRD-6024-13052022' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0d6a155888cc22f5d4ed7ec5fd9e0b44' WHERE ID = '#INTRD-7099_2022051Z' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ec2120df00a1515f40cbedbd4570abcf' WHERE ID = '#INTRD-6672_20220511' AND AUTHOR = 'HichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:51afbddadbae97ecdecac0c1a1a15c9b' WHERE ID = 'INTRD-5806_20220516' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ac02aae433fffb218da84e7a4396c882' WHERE ID = '#INTRD-7244_20220517' AND AUTHOR = 'HichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b89d53e0fd03b908c6bdb4a064ac11c6' WHERE ID = '#INTRD-7273_20220517' AND AUTHOR = 'HichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:aa0975fba5567b36542df935243b44c6' WHERE ID = '#INTRD-7192_20220518' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3108cf0dcf59fad12748d5eb6e6e5c51' WHERE ID = 'INTRD-5311_20220518' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:140100dd44139afc7829abe40ade87a0' WHERE ID = 'INTRD-5312_20220518' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3fe283e7167c9e276d203956766511b8' WHERE ID = 'INTRD-5311_20220518-fix' AND AUTHOR = 'AkadidAbdelmounaim' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:37ddd0fc549168aed26ebfd4bdc8fad8' WHERE ID = 'INTRD-7031-18052022' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f1699bd10493bd2751b05f4b6031219d' WHERE ID = 'INTRD-7031-18052022-split' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:65730fba46ff1d92455e865902f9ae58' WHERE ID = 'INTRD-7040_18052022' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a53b901d183a55c301fdaa5750d57fdb' WHERE ID = 'INTRD-7039_23052022' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:185e1199d094c713a7096a076db8eda1' WHERE ID = '#INTRD-7179_20220524' AND AUTHOR = 'HichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:139d1c046fc68665cc1b86e37533b3e0' WHERE ID = '#INTRD-7157_20220523' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:362ab8db67373fa28063b5916cd68c4b' WHERE ID = '#INTRD-5725_20220524' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1cc30fb84769ba1fbb98843396439cfe' WHERE ID = '#INTRD-7422_20220525' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e6b3c1afa03cb3caad253d95e0487834' WHERE ID = '#INTRD-7537_20220531' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a125879e2cca9df929926b98f025e410' WHERE ID = 'INTRD-5570_20220601' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c69e64f66d2c49c26e30d01bf2980e8d' WHERE ID = '#INTRD-7428_20220601' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7f963a0f16c140bf81b7d31025679a71' WHERE ID = '#INTRD-7629_20220603' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ec986113197b3a58613765b434859c81' WHERE ID = 'INTRD-7479_20220603' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3956ca1c8d8ef3c4ad5cb2547a5bdbc5' WHERE ID = '#INTRD-7642_20220603' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:aa809f83bb23d8d6e708779322b59509' WHERE ID = '#INTRD-7399_20220603' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:768ccf52cbfdfe9241081f32e3f26362' WHERE ID = '#INTRD-7644_20220606' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b6a8fb3597f80722ae7fe7ca17bf208c' WHERE ID = '#INTRD-7352_20220614' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b8f18e401eb69ed851292f212b4aa738' WHERE ID = '#INTRD-7124-130622' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:045aaeafc3e0546961b967dfee596a7d' WHERE ID = '#INTRD-7770_20220613' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f2bf7d26b6de9613e7c8728e5bde26bb' WHERE ID = '#INTRD-7872_20220615' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:27440e892491ce6e3c5ee4d68997cfbd' WHERE ID = '#INTRD-4424_20220221' AND AUTHOR = 'Mounir_BOUKAYOUA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0a732ccccac14b3cd7c5215075b38eae' WHERE ID = '#INTRD-7605_20220617' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:aebbe10a181c10f3cd53ebceb3bbc52a' WHERE ID = '#INTRD-7722_20220618' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:0dacb0a2e231591318a1fcab15ca2a03' WHERE ID = '#INTRD-8026_20220621' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3a2cf8889d083233bb21a555a268a592' WHERE ID = '#INTRD-8026_20220622' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ab20b32f4e1cac29b97551a9431497c4' WHERE ID = '#INTRD-8036-20220621' AND AUTHOR = 'hichamhanine' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4b639db5f62e5ed9f7c112f8b56f1529' WHERE ID = '#INTRD-8055_20220621' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:51e67180bcc86a190ffdef376c4aba93' WHERE ID = '#INTRD-7850-20220622' AND AUTHOR = 'hichamhanine' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ebec9b1e2fe4b76637067076d205b9e9' WHERE ID = '#INTRD-8109_20220622' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:fdc7b5277d19480dec41b479429c163e' WHERE ID = '#INTRD-8022_20220624' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bd71f43aff6142014da574b045faf278' WHERE ID = '#INTRD-8023_20220624' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ed83033debaeca9d603ff5594ca49596' WHERE ID = '#INTRD-8158_20220624' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f95e8b4789625439738ab958b900ea39' WHERE ID = '#INTRD-8147_20220624' AND AUTHOR = 'anas.rouaguebe' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1d5763f58ec72ad100b826a75e0e033a' WHERE ID = '#INTRD-8211_20220626' AND AUTHOR = 'anas.rouaguebe' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:721b84e5000c6982371cb6e7b3c83d79' WHERE ID = '#INTRD-8067_20220627' AND AUTHOR = 'anas.rouaguebe' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ebc773b37a31868c84d74c254433ed4b' WHERE ID = '#INTRD-8077_20220625' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:89eeeb8226582195a1c544773280bd25' WHERE ID = '#INTRD-8219_20220627' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d0b381d8c471b1ed69be2d405e62a6c0' WHERE ID = '#INTRD-8023_20220624-fk1' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:5777dc9611c959ae72f39f9b2ba4ce1e' WHERE ID = '#INTRD-8023_20220624-fk2' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c27857342c217ea18dcb48c98a6c3b0a' WHERE ID = '#INTRD-8132_20220628' AND AUTHOR = 'anas.rouaguebe' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e2f11307b4bf42646886158b0829477f' WHERE ID = '#INTRD-8117_20220629' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1459e9bc8d6c66f0fafb06fe2b49a3c7' WHERE ID = '#6122_20210517' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f58a9c827030797c7069ae6491bf7e81' WHERE ID = '07042021-540-423' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:82302b090cffc846ce8bb38e18213e2a' WHERE ID = '#6122_20210524' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b8b3661ffc6c71a01146905a360bdbcc' WHERE ID = '#20210123_540_281_1' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a64a5dded31ebfcf1ca5321588bff0f6' WHERE ID = '#20210123_540_281_2' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:db0b39a1e1056eea0b0d52e9c9dda9e9' WHERE ID = '#20210123_540_281_3' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1b743a0e405a79346f8902000ffc855b' WHERE ID = '#20210123_540_281_4' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d536d60ee1ea3f55f6d99a239ad2cac1' WHERE ID = '#20210123_540_281_5' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ddbe052fb3ce9acb69d755bfa60a1180' WHERE ID = '#INTRD-8106_20220630' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d05272e3d0a310d4336169750b2eded2' WHERE ID = '#INTRD-8027_20220629' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:8d176be9402611aeb09a60d257aeafed' WHERE ID = '#INTRD-8580_20220714' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:aa4c9dd13f61396fbf13a2d28c1bc3eb' WHERE ID = '#20220408_M540_6296' AND AUTHOR = 'Rachid.Aityaazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ef2b74355a6901c9d9d076b45281eed4' WHERE ID = '#20220412_M540_172' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c6d623976cefb729a661014ba687e86b' WHERE ID = '#20220412_M540_6533' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:95b7d4e86e9b6be580ba2fc23222dcff' WHERE ID = '#20220415_M540_6609' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:3dba0a777e010452fcb59e00dfb8d5df' WHERE ID = '#20220415_M540_6296' AND AUTHOR = 'Rachid.Aityaazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:23a947b1ce1ffd227b78d801cf77ffb0' WHERE ID = '#20220415_M540_6931' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a62aa56fb50156bedac6aa17015ee887' WHERE ID = '#20220519_M540_7341' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2b0a41bf2aa68aebed0c6b05a84a746f' WHERE ID = '#INTRD-4681_20220407' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:924af20e497bcc4e08097845e0676c39' WHERE ID = '#INTRD-8689_20220727' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a9dc1ab693d0faa513682cc0b0c212cc' WHERE ID = '#INTRD-7663_20220613' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c7b4935f536666d2d99f145ef5b0ef35' WHERE ID = '#INTRD-7872_20220615-data' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d9f66b41211da5c5298a01fba8af9337' WHERE ID = '#INTRD-8700_20220728' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2fa4e9dffbc08d22dcbc81e414977f46' WHERE ID = '#INTRD-8812_20220729' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c097d66b5b09cb16726be558290cb9b0' WHERE ID = '#INTRD-8719_20220727' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c99c6af84446ee5a4af5c60888f52a36' WHERE ID = '#INTRD-8721_20220728' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f68c53319620d5a71c531cd87a2b9759' WHERE ID = '#INTRD-8701_20220801' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2014e3605d3dd4b3e546f3149f250d72' WHERE ID = '#INTRD-8777_20220728' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9b20db4b88f8fc52ad9d5e788a7342be' WHERE ID = '#INTRD-8252_20220628' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b736bd1a0bdd635089984e2b04a52872' WHERE ID = '#INTRD-8743_20220802' AND AUTHOR = 'HichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:70ede32b164eaea4211eace10d3e47c4' WHERE ID = '#INTRD-9117_20220811' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:2aefaaeaf554d8149211c13ade4335c0' WHERE ID = '#INTRD-9117_20220814-remove-oo-ids' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a6954eeddebf2c566c75fa807487e7c4' WHERE ID = '#INTRD-8867_20220817-' AND AUTHOR = 'KHorri' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b139fa78e5acff72462100662b69a1f1' WHERE ID = '#INTRD-9232_20220819' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a2492fae790e5790b50e43c5b8af1724' WHERE ID = '#INTRD-7950_20220816' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:f13fc4566af42131f857d2cc255827b7' WHERE ID = '#INTRD-9451_20220902' AND AUTHOR = 'KhalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:bbfe2bb6e498c0ea6b336a86e6256448' WHERE ID = '#INTRD-86_20220815' AND AUTHOR = 'AbdellatifBARI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:d859568ba5f5d4de81306b20a3e0fea5' WHERE ID = '#INTRD-7301_20220820_order' AND AUTHOR = 'zelmeliani' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:42372b1aae25a736c5bd6210bdc58f67' WHERE ID = '#INTRD-7301_20220820_subscription' AND AUTHOR = 'zelmeliani' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:e9cae2f0f229ae141cc47d69ab74c721' WHERE ID = '#INTRD-9331_20220824_rt_migration' AND AUTHOR = 'anas.rouaguebe' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:65f2b5af06fddff64957c3578905869f' WHERE ID = '#INTRD-7303_20220824_sales_person_name_migration' AND AUTHOR = 'zelmeliani' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ee3b89c925c72b8b200af526a118c58a' WHERE ID = '#INTRD-9383-20220830' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1135ef2b5853e86603d2898c9dabfcb2' WHERE ID = '#INTRD-9442_20220831' AND AUTHOR = 'HichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:9e3c4fb138965b0a988d9bd43a89ab46' WHERE ID = '#INTRD-9383-20220905' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a890c17531c90e236d5ee32823ef3f3b' WHERE ID = '#INTRD-9583-20220906-br-generateao' AND AUTHOR = 'a.rouaguebe' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:a23884246c3d5e261b0d5ddc786d3f70' WHERE ID = '#INTRD-9581_20200907' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:b9ecb0c888bc87d73539dc251b423b4c' WHERE ID = 'INTRD-9535-20220908' AND AUTHOR = 'Abdelkader.bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:aa32b8eba21d932fc28b4e1d01ccbe65' WHERE ID = '#INTRD-10135_28092022' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:22383517d7889d0b5d0323dffbeb1165' WHERE ID = '#20210623-INTRD-277-quoteValidationScript' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:00b139a0c62fd9f78cd81cf7df59d6bf' WHERE ID = '#20210623-INTRD-277-orderAdvacementScript' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:1d5546953c94013dd58bafe14d0333dd' WHERE ID = '#FIX-INTRD-634_20210706' AND AUTHOR = 'AkadidAbdelmounaim' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4b210fa2c7bf5a872f47e60ce134a096' WHERE ID = '#FIX2-INTRD-634_20210706' AND AUTHOR = 'AkadidAbdelmounaim' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:6bb331781b1bbb4fec4b1ed2614a686a' WHERE ID = 'INTRD-2547-2480-2482-2478_20211021' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:da66b301e0d4b493e66446d404a4b213' WHERE ID = 'INTRD-2511-1_20211029' AND AUTHOR = 'Mohammed_ElAzzouzi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:4b156a40bf7cc1ad86ad02602a511ede' WHERE ID = 'INTRD-2511-2_20211029' AND AUTHOR = 'Mohammed_ElAzzouzi' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:c983b73d6610aeb3ea778b9dd23bdda8' WHERE ID = '#FIX-INTRD-2601_20211102-changeset' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:66f1038f6696d6d6cd2b4e3ab65323f1' WHERE ID = '#INTRD-3250part2_20211123' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:28f026ce8c88bee06e707b93e8ac6fa3' WHERE ID = '#INTRD-5380_2022_02_28' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ff4c7788b0d7b84f499c2d601622dcc7' WHERE ID = '#INTRD-5533_2022_03_07' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:662a2ef1d3efa2f3c0dc3c775d60e092' WHERE ID = '#INTRD-4590-20220308' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:7db8489c72cc7697a655421bf4c43885' WHERE ID = '#INTRD-8158_20220915' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:48548c574e51fab49407512e621346e5' WHERE ID = '#INTRD-10097_202000926' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/structure.xml';

UPDATE databasechangelog SET MD5SUM = '8:ff54f80479f81aa69941ae242487e33d' WHERE ID = '#INTRD-1166_20211018_Business_attributes' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:b678ec888d7b0edb37cd1e0c4cf6535e' WHERE ID = '#5116_10122020' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:15a01e2009881fac43a39a8d5990f756' WHERE ID = '#INTRD-292_20210816' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:53211e47f6faef50c3a8fa8c8412052c' WHERE ID = '#5890-2021-02-02_42' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:fa1b48c1c74c68dfe1453fce0fb0ad26' WHERE ID = 'rebuild-data-cat_unit_of_measure' AND AUTHOR = 'Mohammed_ELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:d448da0622c1571189fb3aec6fa22846' WHERE ID = '500X_20200218_purge_job' AND AUTHOR = 'mohamed.el.youssoufi' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:38f5b75c98622dbea65dc341036d825b' WHERE ID = '5657_20201201' AND AUTHOR = 'NabilOuachi' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:7e93f55865496350115584441d625d0a' WHERE ID = '#5592-2021-02-02_41' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:5c4d0d059a147a8b0034ba6b2a3f6b8d' WHERE ID = '#5916_20210128' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:cf3e34d2a75b7efc13d634a4a4a96fbb' WHERE ID = '#5951_20210209_data' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:58f4abbfc4251164eefc2836d0c61f46' WHERE ID = '#5946_08032021' AND AUTHOR = 'KhalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:f6e1ac45bb9dcad58264b345eca396c3' WHERE ID = '#4924_20200219' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:b8f36e9fbd8c35586ab8c633e7ef3d83' WHERE ID = '#4924_20200219_2' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:762d850c3ad2deb9fdc9bfcebac5d3f3' WHERE ID = '#5303_20200707 - CDR and Mediation' AND AUTHOR = 'AmineTazi' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:5a85d3ccc61a34bb5a61e05aef203883' WHERE ID = '#5418_20201030' AND AUTHOR = 'Mohammed_ELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:a6dc7f1a13674dc4c3e9ed96e2cbb068' WHERE ID = '#5303_20200814 - CDR and Mediation' AND AUTHOR = 'AmineTazi' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:ad656aa87fcb27bdc83261264e61ee90' WHERE ID = '#INTRD-1090_20210729' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:7f8ce79cf2f217d3d42c737d052cb121' WHERE ID = '#INTRD-1589_2021_14_09' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:2309004107ca50c1d791f798edb56285' WHERE ID = '1589_20210930' AND AUTHOR = 'YoussefIZEM' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:79302620a88c8c0df13b17ca505a9ee8' WHERE ID = 'INTRD-2143_20211006' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:449affb097534c141dba2e4a3a073da7' WHERE ID = '#4044_30122021_counter_period_job' AND AUTHOR = 'Mbarek-Ay' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:e44a9350c178d30e99cbd2e0b651db20' WHERE ID = 'INTRD_1985_20210923_data' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:ec01215ac1045a02489fa730f6f036ee' WHERE ID = 'INTRD-2547-2480-2482-2478_20211021' AND AUTHOR = 'khalidHORRI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:f735ac5a28980f3a54932067be95e816' WHERE ID = 'INTRD_2290_20211015_build' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:7922983061fd5e25f7af6e82ca594a8c' WHERE ID = 'INTRD-2698-20211028' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:03912bb0fd8724db5d306804c7a9e8c1' WHERE ID = 'INTRD-2634_20211027' AND AUTHOR = 'YoussefIZEM' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:247f4c8f4ef92a877f115135eae9897b' WHERE ID = 'INTRD-2731_20211103' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:aa8d523feaf2f407b4999ef48812333e' WHERE ID = 'INTRD-2732_20211104' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:7ec7e258f99cba4a494003b302c12e04' WHERE ID = '#INTRD-1166_20211110_Business_attributes' AND AUTHOR = 'MohammedELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:0db2b41fbab97484ade63322c7f60393' WHERE ID = 'INTRD-2849_20211110' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:45640ec9ae95d949d37bf4ab2e464956' WHERE ID = '#INTRD-2961_20211109-add-status-color_code' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:f9d5b9baa385c0bf4c138ff054127b73' WHERE ID = '#INTRD-2962_20211110' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:760f648c9b6a2a256718f76640c9ff2f' WHERE ID = 'INTRD-3334_2021_11_26' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:ca6719be507d0076ad1d26ce99c56dc4' WHERE ID = 'INTRD-2756_20211110' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:8f5b2829a198749fe48adf8a10962208' WHERE ID = 'INTRD-3040_20211110' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:91273c8579a3e709b4c6d612d951b111' WHERE ID = 'INTRD-2910_20211124' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:8545cbb012484a458045366af61e8e77' WHERE ID = 'INTRD-2756_20211110' AND AUTHOR = 'Mohammed_ELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:3f3683efd19638ddda6b7719fdde0abe' WHERE ID = 'INTRD-2909_20211201' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:75eadf18fce1cb19ce087197e8c1bb55' WHERE ID = 'INTRD-3316_20211129_query_notification_template' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:32bd6e3fc1e802ea94ecd461a0693bfa' WHERE ID = 'INTRD-3316_20211215_query_notification_template' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:59a0961ba9169d94a2a6d86ce62a293d' WHERE ID = 'INTRD-3678_20211214' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:666efe596d3c3a5c1915a14bd7469244' WHERE ID = 'INTRD-3751_20211216' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:63535a32a600753c07e4bf24ff3b3aac' WHERE ID = 'INTRD-4227_20220110' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:07fdc5ab9cf3b87964df94ef5439303a' WHERE ID = 'INTRD-3432_21012022_Manual_refund_by_card_KO' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:89df59884f6f6e00a469c8ca2df6dc56' WHERE ID = '#INTRD-4950-20220208' AND AUTHOR = 'MohammedSTITANE' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:fa023302642ca7992a6a1b2d7005d213' WHERE ID = 'INTRD-3507_20220218' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:79698e181e3f763956008752fbbfde8f' WHERE ID = 'INTRD-5669_20220324' AND AUTHOR = 'HichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:d001441c2d0731e8e917723240b1cb4a' WHERE ID = '#INTRD-5242__22-02-2022' AND AUTHOR = 'Mohammed_ELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:aaaca1f20c1c2f0abcb285b87be484cd' WHERE ID = 'INTRD-4891-25022022' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:f6fa02d8d993f76aefe6d53a8edd5dd4' WHERE ID = '#INTRD-5831_20220325' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:3c2b7f184c37341da021070269cd9f25' WHERE ID = '#INTRD-5830_20220324' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:931386c5bbb0ec832c69c335c6bdc60d' WHERE ID = '#INTRD-5955_20220324' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:a5109809f965e18ca3dba657b15df311' WHERE ID = '#INTRD-5958_20220328' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:2ff8297641f4c6141d2e4fa6fffaaf6a' WHERE ID = '#INTRD-5992_20220329' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:3493267261e3c2b065be9544ddcef596' WHERE ID = '#INTRD-6075_20220330' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:562e75cc98eacfbffb988bf2c81ea48b' WHERE ID = '#INTRD-5924-20220401' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:29be6d41194973b11925bc9bccee023f' WHERE ID = '#INTRD-6638_20220422' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:08a7b5ef004a93cd1d9a4a9bb7b1ce5c' WHERE ID = '#INTRD-7144_05162022' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:0e13b7dc3e41c7dd09d850af3dc4bac5' WHERE ID = '#INTRD-7339_20220607' AND AUTHOR = 'hichamhanine' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:6a9affd0a879e90950858503d8a6a616' WHERE ID = '#INTRD-1304_21062022' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:07fe5b9f2f147c1a586bc1764cdaa4d2' WHERE ID = '#INTRD-8420_20220708' AND AUTHOR = 'hichamHANINE' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:00b51597c8db2f0d2307f8ae70f8ce28' WHERE ID = '#INTRD-8903_08112022' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:b601d01565e2882b90df5a5fa5d0fa6f' WHERE ID = '#INTRD-6668_20220421' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:5e81c829109e6ae2f1494753348a774c' WHERE ID = '#INTRD-6638_20220422-update-occT' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:971425e2911324291e379696d4a2f1e9' WHERE ID = 'INTRD-6305-07042022' AND AUTHOR = 'Abdelkader.Bouazza' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:b0adbc0700f61f4b659e5e6f35b98764' WHERE ID = 'INTRD-5691-20220315' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:a77da3f485dca542799c2e4958ca5eea' WHERE ID = '#INTRD-6961_20220511' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:dccb517763338da7ab824340bc902d75' WHERE ID = '#6023_130522' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:fa1f21dcdb2db396efc880714cbf3459' WHERE ID = '#INTRD-7375_20220523' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:8b4e322ad12357d3546de9ec1e7fd297' WHERE ID = '#7477-260522' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:c7b254435aca62f7ebdd30307fa04fcc' WHERE ID = '#INTRD-7750_20220608' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:b84b436a066a5447e434f6dd1a540632' WHERE ID = '#INTRD-7722_20220618' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:57c53cf73308a9ceac43c92eb5864c73' WHERE ID = '#INTRD-3262_20211123' AND AUTHOR = 'YoussefIZEM' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:906ea862dd380afd110ea28e98933f7c' WHERE ID = 'INTRD-2701_20211124' AND AUTHOR = 'hichamElHaloui' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:be36bf127f0d5c5b169fca573731a8bd' WHERE ID = '#5486_remove_CF_for_old_RT_Aggregation' AND AUTHOR = 'Mohammed_ELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:86590693ff9efb221027d3cd616651fb' WHERE ID = '5407_20201030' AND AUTHOR = 'Mohammed_ELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:3c5b9961f799b77dd2aad893083c30fb' WHERE ID = '#5610_20201102 - bad dept operation type' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:c4084429325cc7efe4623a36a8445d0b' WHERE ID = '#5632_20201207_RT_TYPE_DATA' AND AUTHOR = 'NabilOUACHI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:ca6ddbf45f5d61cb44bda1a768956518' WHERE ID = '5743_20201215' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:cd0b7cbc0756d97dfa97181f56cd6c86' WHERE ID = '5678_20210214' AND AUTHOR = 'anasseh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:d742055e1e915caf45c2c9cc4f4e6043' WHERE ID = '#20210607_540_563' AND AUTHOR = 'TarikFA.' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:db88660c1cf654f92f18005d25d178fe' WHERE ID = '#INTRD-1406_20210826' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:55fb71685df6ba70b5a1ff403b7fd080' WHERE ID = '#2122_20211001' AND AUTHOR = 'YoussefIZEM' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:be4aef66585cea5fe6bc6542306d084a' WHERE ID = '#INTRD-8507_20220715' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:3346716548450b00d794bafadb35f92c' WHERE ID = '#INTRD-8700_20220728' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:75e552dab42b94e1f5f5e0fe44c77691' WHERE ID = '#INTRD-8946_20220804' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:15a01e2009881fac43a39a8d5990f756' WHERE ID = '#INTRD-292_20210816' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:fa1b48c1c74c68dfe1453fce0fb0ad26' WHERE ID = 'rebuild-data-cat_unit_of_measure' AND AUTHOR = 'Mohammed_ELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:62580bb9df9f8fe8b5816e9347b5fa23' WHERE ID = 'INTRD-4724_20220201' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:2e8c228dacc7254f18dddd28c5f2b653' WHERE ID = 'INTRD-5089_20220215' AND AUTHOR = 'TarikRabeh' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:77a6616de6df116c930b70d2defcc9d9' WHERE ID = '#INTRD-5888_2022-03_23' AND AUTHOR = 'hichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:e77c713a9e89de3d8de4431fbe7a0720' WHERE ID = 'INTRD-5701_20220215' AND AUTHOR = 'HatimOUDAD' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:e4aaa6dd88eb7e7dc1e618496decf9fc' WHERE ID = 'INTRD-5997_20220330' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:e650e6cd026a0a88abfe3787f5c0bb3a' WHERE ID = '#INTRD-6341_20220406' AND AUTHOR = 'aelmalki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:a6f64ee8e80d57314b436d83f9d40350' WHERE ID = '#INTRD-6283_20220404' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:60810efeea58b9e036ebdbad246df57e' WHERE ID = '#INTRD-6291_20220406' AND AUTHOR = 'AbdelmounaimAkadid' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:ab9d26346d13a039ecf8aa2de152602d' WHERE ID = '#INTRD-6283_20220422' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:35a6ba43a0462c29fd2d48ea9371438d' WHERE ID = 'INTRD-6908_20220503' AND AUTHOR = 'HichamELHALOUI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:4b6580820b6de1a9960568ab1b8b0f12' WHERE ID = '#INTRD-8004_210622' AND AUTHOR = 'AbdelkaderBouazza' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:04b238ac1c3508430de86be0d5e9bac5' WHERE ID = '#6048_20210326' AND AUTHOR = 'ZBARIKI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:bffdfff4015f3a598bb68cc5674a6607' WHERE ID = '#6048_20210611' AND AUTHOR = 'AmineBENAICHA' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:567a7c0b340661f338bbd3c69ee7ea2c' WHERE ID = 'INTRD-263_20210716' AND AUTHOR = 'ZBariki' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:61bbdfdeb3f3f99a3b53116da7a170d2' WHERE ID = 'J215_20210708' AND AUTHOR = 'Mohammed_ELAZZOUZI' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:2309004107ca50c1d791f798edb56285' WHERE ID = '1589_20210930' AND AUTHOR = 'YoussefIZEM' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:e8e5c68ed79a120d6c4afe3bc36aa1b9' WHERE ID = 'INTRD-3765_07012022' AND AUTHOR = 'hichamElHaloui' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:a60532dcaeafe3ed03cb0613e520c800' WHERE ID = '#5308_20200618 - Add script' AND AUTHOR = 'AndriusKarpavicius' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:e7ab86c60fa49ca3548e1a606981404b' WHERE ID = '#5417_20200929 - Error while running CUSTOMERS_PER_SELLER report extracts' AND AUTHOR = 'Mohamed-Ali-HAMMAL' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

UPDATE databasechangelog SET MD5SUM = '8:47a4aa5b86ad18e8acb269471882c902' WHERE ID = '#5425_20200930 - Failed to execute some report extract' AND AUTHOR = 'Mohamed-Ali-HAMMAL' AND FILENAME = 'src/main/db_resources/changelog/current/data.xml';

-- Changeset src/main/db_resources/changelog/current/structure.xml::#5882_20220325::MohammedELAZZOUZI
ALTER TABLE billing_invoice_line ADD user_account_id BIGINT;

ALTER TABLE billing_invoice_line ADD CONSTRAINT "fk_InvoiceLine_userAccount" FOREIGN KEY (user_account_id) REFERENCES billing_user_account (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#5882_20220325', 'MohammedELAZZOUZI', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2153, '8:aff01355f452ef771ba6a26d50436707', 'addColumn tableName=billing_invoice_line; addForeignKeyConstraint baseTableName=billing_invoice_line, constraintName=fk_InvoiceLine_userAccount, referencedTableName=billing_user_account', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-10188_20220928::MehdiBOURRAS
ALTER TABLE cpq_price_plan_matrix_line RENAME COLUMN price_el TO value_el;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10188_20220928', 'MehdiBOURRAS', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2154, '8:c74b97ab5c695cb31295e5d50614d057', 'renameColumn newColumnName=value_el, oldColumnName=price_el, tableName=cpq_price_plan_matrix_line', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-9598-20220909-contact-category::a.rouaguebe
CREATE TABLE com_contact_category (id BIGINT NOT NULL, version INTEGER, created TIMESTAMP WITHOUT TIME ZONE NOT NULL, updated TIMESTAMP WITHOUT TIME ZONE, code VARCHAR(255) NOT NULL, description VARCHAR(255), creator VARCHAR(100), updater VARCHAR(100), uuid VARCHAR(60) DEFAULT uuid_generate_v4() NOT NULL, cf_values JSONB, CONSTRAINT com_contact_category_pkey PRIMARY KEY (id));

ALTER TABLE com_contact_category ADD CONSTRAINT uk_com_contact_category_code UNIQUE (code);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9598-20220909-contact-category', 'a.rouaguebe', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2155, '8:e7af0ae08d53f6602aa3599662fbd604', 'createTable tableName=com_contact_category; addUniqueConstraint constraintName=uk_com_contact_category_code, tableName=com_contact_category', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-9684-20220909::khalidHORRI
ALTER TABLE cpq_price_plan_version ADD price_version_type VARCHAR(255) DEFAULT 'FIXED' NOT NULL;

ALTER TABLE cpq_price_plan_matrix_line RENAME COLUMN price_without_tax TO value;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9684-20220909', 'khalidHORRI', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2156, '8:7708b68eeab2d17e93a57290bcc37b24', 'addColumn tableName=cpq_price_plan_version; renameColumn newColumnName=value, oldColumnName=price_without_tax, tableName=cpq_price_plan_matrix_line', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-9854-20220915-contact-category-many-to-many::a.rouaguebe
CREATE TABLE com_contact_category_contact (contact_id BIGINT NOT NULL, contact_category_id BIGINT NOT NULL);

ALTER TABLE com_contact_category_contact ADD CONSTRAINT com_contact_category_contact_pk UNIQUE (contact_id, contact_category_id);

ALTER TABLE com_contact_category_contact ADD CONSTRAINT contact_category_id_fk FOREIGN KEY (contact_category_id) REFERENCES com_contact_category (id);

ALTER TABLE com_contact_category_contact ADD CONSTRAINT contact_id_fk FOREIGN KEY (contact_id) REFERENCES com_contact (id);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9854-20220915-contact-category-many-to-many', 'a.rouaguebe', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2157, '8:25ac252f083ff3345d0a2c85fc55dbe4', 'createTable tableName=com_contact_category_contact; addUniqueConstraint constraintName=com_contact_category_contact_pk, tableName=com_contact_category_contact; addForeignKeyConstraint baseTableName=com_contact_category_contact, constraintName=cont...', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-9786-20220915::HichamHANINE
ALTER TABLE crm_provider ADD rgaa_message VARCHAR(500);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9786-20220915', 'HichamHANINE', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2158, '8:b1b3749906a9bd4a1282dfbb73429b43', 'addColumn tableName=crm_provider', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-10283_20220930::HichamHANINE
ALTER TABLE security_deposit ADD billing_account_id BIGINT;

ALTER TABLE security_deposit ADD CONSTRAINT fk_billing_account_security_deposit FOREIGN KEY (billing_account_id) REFERENCES billing_billing_account (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10283_20220930', 'HichamHANINE', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2159, '8:8d2075d7aa476a47bae4c647d993ed0c', 'addColumn tableName=security_deposit; addForeignKeyConstraint baseTableName=security_deposit, constraintName=fk_billing_account_security_deposit, referencedTableName=billing_billing_account', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-9946_20220920::HichamHANINE
ALTER TABLE security_deposit ADD sd_invoice_id BIGINT;

ALTER TABLE security_deposit ADD CONSTRAINT fk_security_deposit_invoice FOREIGN KEY (sd_invoice_id) REFERENCES billing_invoice (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9946_20220920', 'HichamHANINE', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2160, '8:fec7b0aea804d46a64366ff90a40ae70', 'addColumn tableName=security_deposit; addForeignKeyConstraint baseTableName=security_deposit, constraintName=fk_security_deposit_invoice, referencedTableName=billing_invoice', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-10581_20221018::HichamHANINE
ALTER TABLE meveo_script_instance ADD description_i18n JSONB;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10581_20221018', 'HichamHANINE', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2161, '8:56e77784074e630e8fc3bf3550a17f9f', 'addColumn tableName=meveo_script_instance', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-9871_20220919::TarikFA.
ALTER TABLE billing_wallet_operation DROP CONSTRAINT price_plan_matrix_line_fk;

ALTER TABLE billing_wallet_operation ADD CONSTRAINT price_plan_matrix_line_fk FOREIGN KEY (price_plan_matrix_line_id) REFERENCES cpq_price_plan_matrix_line (id);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9871_20220919', 'TarikFA.', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2162, '8:997b5e01b52a6f8bb14c642ad6e36add', 'dropForeignKeyConstraint baseTableName=billing_wallet_operation, constraintName=price_plan_matrix_line_fk; addForeignKeyConstraint baseTableName=billing_wallet_operation, constraintName=price_plan_matrix_line_fk, referencedTableName=cpq_price_plan...', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-9804-20220915::khalidHORRI
ALTER TABLE cpq_contract_item ALTER COLUMN rate TYPE FLOAT8 USING (rate::FLOAT8);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9804-20220915', 'khalidHORRI', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2163, '8:4e1a6ae8c8c852dba0782af4f2266310', 'modifyDataType columnName=rate, tableName=cpq_contract_item', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-10163_20220930::TarikFA.
CREATE SEQUENCE billing_invoice_advance_mapping_seq START WITH 1;

CREATE TABLE billing_invoice_advance_mapping (id BIGINT NOT NULL, version INTEGER, advance_invoice_id BIGINT, invoice_id BIGINT, amount numeric(23, 12), CONSTRAINT advance_mapping_pkey PRIMARY KEY (id));

ALTER TABLE billing_invoice_advance_mapping ADD CONSTRAINT advance_invoice_id_fk FOREIGN KEY (advance_invoice_id) REFERENCES billing_invoice (id);

ALTER TABLE billing_invoice_advance_mapping ADD CONSTRAINT invoice_id_fk FOREIGN KEY (invoice_id) REFERENCES billing_invoice (id);

ALTER TABLE billing_invoice ADD invoice_balance numeric(23, 12);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10163_20220930', 'TarikFA.', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2164, '8:7967a450c92f5eaf3595a2f368e311b7', 'createSequence sequenceName=billing_invoice_advance_mapping_seq; createTable tableName=billing_invoice_advance_mapping; addForeignKeyConstraint baseTableName=billing_invoice_advance_mapping, constraintName=advance_invoice_id_fk, referencedTableNam...', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-9796_30092022_exclude_aged_balance::a.rouaguebe
ALTER TABLE billing_invoice_type ADD exclude_from_aged_trial_balance INTEGER DEFAULT 0 NOT NULL;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9796_30092022_exclude_aged_balance', 'a.rouaguebe', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2165, '8:f19e0fbb4398a8572523da64a343b15a', 'addColumn tableName=billing_invoice_type', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-4681_20220929::AndriusKarpavicius
update public.adm_secured_entity set entity_class = reverse(split_part(reverse(entity_class),'.',1));

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-4681_20220929', 'AndriusKarpavicius', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2166, '8:eab69bb373bd422228577ad386ca33c9', 'sql', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-10274_20221003::AndriusKarpavicius
ALTER TABLE adm_secured_entity DROP CONSTRAINT adm_secured_entity_pkey;

ALTER TABLE adm_secured_entity ALTER COLUMN  user_name DROP NOT NULL;

ALTER TABLE adm_secured_entity ADD PRIMARY KEY (id);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10274_20221003', 'AndriusKarpavicius', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2167, '8:d226e41f4bb9ec2ab817c7092ef0f09a', 'dropPrimaryKey constraintName=adm_secured_entity_pkey, tableName=adm_secured_entity; dropNotNullConstraint columnName=user_name, tableName=adm_secured_entity; addPrimaryKey tableName=adm_secured_entity', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-9766_20221011_EDR_new_fields::aelmalki
ALTER TABLE rating_edr ADD wallet_operation_id BIGINT;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9766_20221011_EDR_new_fields', 'aelmalki', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2168, '8:c64110fcf794fd05352979e1718216df', 'addColumn tableName=rating_edr', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-10540_20221013::TarikFA.
DROP TABLE billing_invoice_advance_mapping;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10540_20221013', 'TarikFA.', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2169, '8:af8ab4cf390d69b843d4cdcd95670aaf', 'dropTable tableName=billing_invoice_advance_mapping', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::INTRD-10603_20221113::AbdelmounaimAkadid
ALTER TABLE adm_inbound_req_cookies ALTER COLUMN coockies TYPE VARCHAR(500) USING (coockies::VARCHAR(500));

ALTER TABLE adm_inbound_req_cookies ALTER COLUMN coockies_key TYPE VARCHAR(500) USING (coockies_key::VARCHAR(500));

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('INTRD-10603_20221113', 'AbdelmounaimAkadid', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2170, '8:477695f39af43b84b4df3b6e32b52e00', 'modifyDataType columnName=coockies, tableName=adm_inbound_req_cookies; modifyDataType columnName=coockies_key, tableName=adm_inbound_req_cookies', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-10607_20221014::AdilElJaouhari
ALTER TABLE billing_invoice ADD use_current_rate INTEGER DEFAULT 0 NOT NULL;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10607_20221014', 'AdilElJaouhari', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2171, '8:51c816789d378f87e191855e1eb819ba', 'addColumn tableName=billing_invoice', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/structure.xml::#INTRD-10522_20221017_matchingCode-journal-entry::aelmalki
ALTER TABLE accounting_journal_entry ADD matching_code VARCHAR(255);

ALTER TABLE crm_provider ADD current_matching_code VARCHAR(255);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10522_20221017_matchingCode-journal-entry', 'aelmalki', 'src/main/db_resources/changelog/current/structure.xml', NOW(), 2172, '8:17538e8158fa6ce864e8be33dd29b7f6', 'addColumn tableName=accounting_journal_entry; addColumn tableName=crm_provider', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#NTRD-2413_20211028_Business_attributes_add_el::MohammedELAZZOUZI
UPDATE cpq_attribute SET el_value = '${serviceInstance.getActivationDate()}' WHERE id=-1;

UPDATE cpq_attribute SET el_value = '${serviceInstance.getQuantity()}' WHERE id=-2;

UPDATE cpq_attribute SET el_value = '${edr.getEventDate()}' WHERE id=-3;

UPDATE cpq_attribute SET el_value = '${edr.getQuantity()}' WHERE id=-4;

UPDATE cpq_attribute SET el_value = '${sub.getRenewalDate()}' WHERE id=-5;

UPDATE cpq_attribute SET el_value = '${serviceInstance.getDeliveryDate()}' WHERE id=-6;

UPDATE cpq_attribute SET el_value = '${quote.getValidationDate()}' WHERE id=-7;

UPDATE cpq_attribute SET el_value = '${sub.getSubscriptionDate()}' WHERE id=-8;

UPDATE cpq_attribute SET el_value = '${sub.getSubscriptionMonthsAge()}' WHERE id=-9;

UPDATE cpq_attribute SET el_value = '${sub.getSubscriptionDaysAge()}' WHERE id=-10;

UPDATE cpq_attribute SET el_value = '${edr.getParameter1()}' WHERE id=-11;

UPDATE cpq_attribute SET el_value = '${edr.getParameter2()}' WHERE id=-12;

UPDATE cpq_attribute SET el_value = '${edr.getParameter3()}' WHERE id=-13;

UPDATE cpq_attribute SET el_value = '${edr.getParameter4()}' WHERE id=-14;

UPDATE cpq_attribute SET el_value = '${edr.getParameter5()}' WHERE id=-15;

UPDATE cpq_attribute SET el_value = '${edr.getParameter6()}' WHERE id=-16;

UPDATE cpq_attribute SET el_value = '${edr.getParameter7()}' WHERE id=-17;

UPDATE cpq_attribute SET el_value = '${edr.getParameter8()}' WHERE id=-18;

UPDATE cpq_attribute SET el_value = '${edr.getParameter9()}' WHERE id=-19;

UPDATE cpq_attribute SET el_value = '${edr.getDateParam1()}' WHERE id=-21;

UPDATE cpq_attribute SET el_value = '${edr.getDateParam2()}' WHERE id=-22;

UPDATE cpq_attribute SET el_value = '${edr.getDateParam3()}' WHERE id=-23;

UPDATE cpq_attribute SET el_value = '${edr.getDateParam4()}' WHERE id=-24;

UPDATE cpq_attribute SET el_value = '${edr.getDateParam5()}' WHERE id=-25;

UPDATE cpq_attribute SET el_value = '${edr.getDecimalParam1()}' WHERE id=-31;

UPDATE cpq_attribute SET el_value = '${edr.getDecimalParam2()}' WHERE id=-32;

UPDATE cpq_attribute SET el_value = '${edr.getDecimalParam3()}' WHERE id=-33;

UPDATE cpq_attribute SET el_value = '${edr.getDecimalParam4()}' WHERE id=-34;

UPDATE cpq_attribute SET el_value = '${edr.getDecimalParam5()}' WHERE id=-35;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#NTRD-2413_20211028_Business_attributes_add_el', 'MohammedELAZZOUZI', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2173, '8:07beffb1e9738fad431e8e0265547d1c', 'update tableName=cpq_attribute; update tableName=cpq_attribute; update tableName=cpq_attribute; update tableName=cpq_attribute; update tableName=cpq_attribute; update tableName=cpq_attribute; update tableName=cpq_attribute; update tableName=cpq_at...', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-4267_20220108::AmineBENAICHA
INSERT INTO billing_tax_mapping (id, tax_category_id, tax_class_id, tax_id, priority, created, creator) VALUES ('-20', '-1', NULL, '-2', '-1', NOW(), 'applicationInitializer');

INSERT INTO billing_tax_mapping (id, tax_category_id, tax_class_id, tax_id, priority, created, creator) VALUES ('-21', '-3', NULL, '-5', '-1', NOW(), 'applicationInitializer');

INSERT INTO billing_tax_mapping (id, tax_category_id, tax_class_id, tax_id, priority, created, creator) VALUES ('-22', '-2', NULL, '-1', '-1', NOW(), 'applicationInitializer');

INSERT INTO billing_tax_mapping (id, tax_category_id, tax_class_id, tax_id, priority, created, creator) VALUES ('-23', NULL, NULL, '-1', '-1', NOW(), 'applicationInitializer');

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-4267_20220108', 'AmineBENAICHA', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2174, '8:75143ea823a9b4fd18b642ae2bd2cf34', 'insert tableName=billing_tax_mapping; insert tableName=billing_tax_mapping; insert tableName=billing_tax_mapping; insert tableName=billing_tax_mapping', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::INTRD-4680-01032022::TarikRabeh
UPDATE billing_user_account SET is_consumer = '1';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('INTRD-4680-01032022', 'TarikRabeh', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2175, '8:08cce383cb6bc267c77abad0c7494efa', 'update tableName=billing_user_account', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::INTRD-4285-20220317::AmineBENAICHA
INSERT INTO adm_role_role (role_id, child_role_id) VALUES (-1, -121);

INSERT INTO adm_role_role (role_id, child_role_id) VALUES (-2, -121);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('INTRD-4285-20220317', 'AmineBENAICHA', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2176, '8:fc2d415d1442f5283824203605bb6109', 'insert tableName=adm_role_role; insert tableName=adm_role_role', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-5882__23-03-2022::Mohammed_ELAZZOUZI
INSERT INTO crm_custom_field_tmpl (id, version, disabled, created, code, applies_to, description, field_type, storage_type, default_value, value_required, creator, gui_position) VALUES (nextval('crm_custom_fld_tmp_seq'), 0, 0, NOW(), 'waitingMillis', 'JobInstance_InvoiceLinesJob', 'Waiting before next launch (ms)', 'LONG', 'SINGLE', 0, '0', 'applicationInitializer', 'tab:Configuration:0;fieldGroup:Execution configuration:0;field:1');

INSERT INTO crm_custom_field_tmpl (id, version, disabled, created, code, applies_to, description, field_type, storage_type, default_value, value_required, creator, gui_position) VALUES (nextval('crm_custom_fld_tmp_seq'), 0, 0, NOW(), 'nbRuns', 'JobInstance_InvoiceLinesJob', 'Number of parallel execution', 'LONG', 'SINGLE', 1, '0', 'applicationInitializer', 'tab:Configuration:0;fieldGroup:Execution configuration:0;field:0');

INSERT INTO crm_custom_field_tmpl (id, version, disabled, created, code, applies_to, description, field_type, storage_type, default_value, value_required, creator) VALUES (nextval('crm_custom_fld_tmp_seq'), 0, 0, NOW(), 'maxInvoiceLinesPerTransaction', 'JobInstance_InvoiceLinesJob', 'Maximum of maxInvoiceLines generated per transaction', 'LONG', 'SINGLE', 0, '0', 'applicationInitializer');

INSERT INTO crm_custom_field_tmpl (id, version, disabled, created, code, applies_to, description, field_type, storage_type, default_value, value_required, creator) VALUES (nextval('crm_custom_fld_tmp_seq'), 0, 0, NOW(), 'maxBAsPerTransaction', 'JobInstance_InvoicingJobV2', 'commit interval', 'LONG', 'SINGLE', 1000, '0', 'applicationInitializer');

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-5882__23-03-2022', 'Mohammed_ELAZZOUZI', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2177, '8:869566a4acc25d35a3488d97f9f1e0e5', 'insert tableName=crm_custom_field_tmpl; insert tableName=crm_custom_field_tmpl; insert tableName=crm_custom_field_tmpl; insert tableName=crm_custom_field_tmpl', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-4702-20220321::ZBariki
UPDATE meveo_script_instance_cat SET code = 'ACCOUNTING_SCHEMES', description = 'Accounting schemes' WHERE code='FILE_ACCOUNTING_SCHEMES';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-4702-20220321', 'ZBariki', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2178, '8:e54f15317765cc4da369b2831419d02a', 'update tableName=meveo_script_instance_cat', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-5960_20220328::HichamHANINE
Update  cpq_order_offer o set subscription_id = (select id from billing_subscription s where s.order_offer_id = o.id) where o.subscription_id is null;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-5960_20220328', 'HichamHANINE', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2179, '8:2022467d6554f8a8dcf6c0e0ed2559a8', 'sql', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-4666_20220331::HatimOUDAD
UPDATE cpq_order_product SET production_action_type = 'CREATE' WHERE production_action_type is null;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-4666_20220331', 'HatimOUDAD', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2180, '8:10a7fcb70191726a8387f54d79a1fe65', 'update tableName=cpq_order_product', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-6298_20220405::aelmalki
UPDATE ar_accounting_scheme SET code = 'Dual entry scheme', long_description_i18n = '{"FRA":"<p>Ce schéma produit 2 lignes d''écriture:</p><ul><li>Une ligne pour le compte et le sens définis sur le type d''opération de de compte</li><li>Une ligne pour le compte de contrepartie défini sur le type d''opération de compte, mais avec un sens opposé</li></ul>","ENG":"<p>This scheme produces 2 accounting entries:</p><ul><li>An entry for the accounting code and the direction defined on the account operation type</li><li>An entry for the contra account defined on the account operation, type but with the opposite direction</li></ul>"}' WHERE code='DefaultAccountingScheme';

UPDATE ar_accounting_scheme SET code = 'Invoice and Credit Note', long_description_i18n = '{"FRA":"<p>Ce schéma produit des lignes d''écritures pour une facture ou un avoir (ci-dessous \"document\") :</p><ul><li>Une ligne unique pour le total TTC du document, sur le compte client (de la catégorie de client ou à défaut du type d''opération de compte).</li></ul><p class=\"ql-indent-1\">Le sens est celui de l''opération de compte (pour une facture : DEBIT, pour un avoir : CREDIT).</p><ul><li>Des lignes de contrepartie (au moins une) pour les montants HT pour les comptes de revenus, en utilisant les codes comptables des articles liés aux lignes du document.</li></ul><p class=\"ql-indent-1\">Le sens est l''opposé de celui du type d''opération (pour une facture : CREDIT, pour un avoir : DEBIT).</p><ul><li>S''il y a lieu, des lignes pour les montants des différentes taxes appliquées au document, en utilisant les codes comptables de ces taxes.</li></ul><p class=\"ql-indent-1\">Le sens est l''opposé de celui du type d''opération (pour une facture : CREDIT, pour un avoir : DEBIT).</p>","ENG":"<p>This scheme produces entries for an invoice or credit note (here after \"document\"):</p><ul><li>A single entry with the document total amount with tax using the customer accounting code (from the customer category, or if not set from the account operation type). </li></ul><p class=\"ql-indent-1\">Line direction is the one from the account operation type (for invoice: DEBIT, for credit note: CREDIT).</p><ul><li>\"Contra\" entries (at least one) with the amounts without tax for revenue accounts, using the accounting codes from the article linked to the document lines. </li></ul><p class=\"ql-indent-1\">Line direction is the opposite of direction set on the account operation type (for invoices: CREDIT, for credit note: DEBIT).</p><ul><li>\"Contra\" entries (if applies) with the tax amounts for the different taxes applied on the document, using the accounting codes from these taxes.</li></ul><p class=\"ql-indent-1\">Line direction is the opposite of direction set on the account operation type (for invoices: CREDIT, for credit note: DEBIT).</p>"}' WHERE code='org.meveo.service.script.accountingscheme.InvoiceAccountingSchemeScript';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-6298_20220405', 'aelmalki', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2181, '8:ec00c97e8dbabed3735ebaecf47ff6b5', 'update tableName=ar_accounting_scheme; update tableName=ar_accounting_scheme', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-6056_20220415::HatimOUDAD
UPDATE cpq_quote_product SET product_action_type = 'CREATE' WHERE product_action_type is null;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-6056_20220415', 'HatimOUDAD', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2182, '8:db4b7f9f152287e6ed8a6d5d689be2b0', 'update tableName=cpq_quote_product', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-7049_20220509::TarikRabeh
update public.accounting_journal_entry set 
		        trading_currency = (select ac.currency_code from adm_currency ac, billing_trading_currency btc, billing_invoice inv, ar_account_operation aco 
		        	where ac.id = btc.currency_id and inv.trading_currency_id = btc.id and inv.id = aco.invoice_id and aco.id =  accounting_operation_id),
		        trading_amount = (select inv.amount_without_tax from billing_invoice inv, ar_account_operation aco 
		        	where inv.id = aco.invoice_id and aco.id =  accounting_operation_id);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-7049_20220509', 'TarikRabeh', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2183, '8:8ee4fcb775f85f158c42cc8708159180', 'sql', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::FIX validateBR script compile::AbdelmounaimAkadid
UPDATE meveo_script_instance SET script = 'package org.meveo.service.script;

			import java.util.List;
			import java.util.Map;

			import org.meveo.admin.exception.BusinessException;
			import org.meveo.model.billing.BillingRun;
			import org.meveo.model.billing.BillingRunStatusEnum;
			import org.meveo.service.billing.impl.BillingRunExtensionService;
			import org.meveo.service.billing.impl.BillingRunService;

			public class ValidateBRScript extends Script {

				@Override
				public void execute(Map<String, Object> methodContext) throws BusinessException {

					BillingRunService billingRunService = (BillingRunService) getServiceInterface("BillingRunService");

					BillingRunExtensionService billingRunExtensionService = (BillingRunExtensionService) getServiceInterface(
							"BillingRunExtensionService");

					if (billingRunService != null) {
						List<BillingRun> billingRuns = billingRunService.getBillingRuns(BillingRunStatusEnum.PREINVOICED,  BillingRunStatusEnum.POSTINVOICED);

						for (BillingRun billingRun : billingRuns) {

							try {
								billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null,
										BillingRunStatusEnum.POSTVALIDATED, null);
							} catch (Exception e) {
								System.err.println("Error " + e.getMessage());
							}
						}

					}

				}
			}' WHERE id=-24;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('FIX validateBR script compile', 'AbdelmounaimAkadid', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2184, '8:3067883ba6a4a43355e3826038e4c516', 'update tableName=meveo_script_instance', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-7428_20220524::ZBariki
UPDATE finance_settings SET auxiliary_account_code_el = '#{substring(gcl.code, 0, 3)ca.description};' WHERE auxiliary_account_code_el = '#{substring(gcl.code, 0, 3), String.class)ca.description};';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-7428_20220524', 'ZBariki', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2185, '8:4e932de7fa745f06f3ef259d39e92529', 'update tableName=finance_settings', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-7360_20220525::hichamhanine
UPDATE ar_accounting_scheme SET long_description_i18n = '{"FRA":"<p>Ce schéma produit 2 lignes d''écriture:</p><ul><li>Une ligne pour le compte et le sens définis sur le type d''opération de de compte</li><li>Une ligne pour le compte de contrepartie défini sur le type d''opération de compte, mais avec un sens opposé</li></ul>","ENG":"<p>This scheme produces 2 accounting entries:</p><ul><li>An entry for the accounting code and the direction defined on the account operation type</li><li>An entry for the contra account defined on the account operation, type but with the opposite direction</li></ul>"}' WHERE code='Dual entry scheme';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-7360_20220525', 'hichamhanine', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2186, '8:d2df56805703c65ffe48bc89546cce6b', 'update tableName=ar_accounting_scheme', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-7573_20220602::TarikFA.
UPDATE billing_invoice_cat SET version = '0' WHERE code='ICAT_SECURITY_DEPOSIT';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-7573_20220602', 'TarikFA.', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2187, '8:68421b0688258e9223a16b1309704d6c', 'update tableName=billing_invoice_cat', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-7402_20220608::aelmalki
INSERT INTO ar_occ_template (id, version, created, creator, accounting_code_id, code, description, occ_category, journal_id, manual_creation_enabled) VALUES (-62, 0, NOW(), 'opencell.admin', -6, 'PPL_CREATION', 'Payment plan Creation', 'CREDIT', '-3', '0');

INSERT INTO ar_occ_template (id, version, created, creator, accounting_code_id, code, description, occ_category, journal_id, manual_creation_enabled) VALUES (-63, 0, NOW(), 'opencell.admin', -6, 'PPL_INSTALLMENT', 'Payment plan Installment', 'DEBIT', '-3', '0');

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-7402_20220608', 'aelmalki', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2188, '8:15265e8eb6182d14ce62a565525b629b', 'insert tableName=ar_occ_template; insert tableName=ar_occ_template', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-7632_20220611::HatimOUDAD
UPDATE cpq_order_product SET instance_status = 'ACTIVE' WHERE instance_status is null and production_action_type = 'ACTIVATE';

UPDATE cpq_order_product SET instance_status = 'INACTIVE' WHERE instance_status is null and production_action_type = 'CREATE';

UPDATE cpq_order_product SET instance_status = 'SUSPENDED' WHERE instance_status is null and production_action_type = 'SUSPEND';

UPDATE cpq_order_product SET instance_status = 'TERMINATED' WHERE instance_status is null and production_action_type = 'TERMINATE';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-7632_20220611', 'HatimOUDAD', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2189, '8:edf5c7e10d13828c276173627e36943c', 'update tableName=cpq_order_product; update tableName=cpq_order_product; update tableName=cpq_order_product; update tableName=cpq_order_product', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-8618-20220719::aelmalki
UPDATE cpq_attribute SET el_value = '${sub.getSubscriptionDate()}' WHERE id=-8;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-8618-20220719', 'aelmalki', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2190, '8:aaaca1f20c1c2f0abcb285b87be484cd', 'update tableName=cpq_attribute', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-9839_19012022::TarikRabeh
UPDATE dwh_report_extract SET sql_query = 'SELECT
				    TO_CHAR(ao.invoice_date,''MM'') AS "Mois",
				    TO_CHAR(ao.invoice_date,''DD/MM/YYYY'') AS "Date facture",
				    text(''HG'') AS "Code comptable",
				    ae.code AS "Client facturé",
				    text(''MAI'') AS "Type article",
				    split_part(bac.code, '','', 6) AS "Famille statistique",
				    split_part(bac.code, '','', 2) AS "Article",
				    trim(TO_CHAR(ia.amount_without_tax,''9999990D00'')) AS "Montant HT",
				    trim(TO_CHAR(ia.quantity,''9999990'')) AS "Qté facturée",
				    ao.reference AS "No facture",
				    ao.description AS "Catégorie facture",
				    ao.code AS "Type de pièce",
				    text(''VEN'') AS "Journal"
				FROM
				    ar_customer_account ae
				INNER JOIN ar_account_operation ao ON ao.customer_account_id = ae.id
				INNER JOIN billing_invoice i ON i.invoice_number = ao.reference
				INNER JOIN billing_invoice_agregate ia ON (ia.invoice_id = i.id AND type = ''F'')
				LEFT JOIN billing_accounting_code bac ON bac.id = ia.accounting_code_id
				WHERE :START_DATE<>:END_DATE
				AND ao.invoice_date >= to_date(''01/'' || TO_CHAR(CURRENT_DATE,''MM/YYYY''),''DD/MM/YYYY'') + interval ''-12 month''
				AND ao.invoice_date <= to_date(''01/'' || TO_CHAR(CURRENT_DATE,''MM/YYYY''),''DD/MM/YYYY'') + interval ''0 month''
				ORDER BY
				    ae.code,
				    ao.code,
				    split_part(bac.code, '','', 6),
				    split_part(bac.code, '','', 2)' WHERE code='SALES_JOURNAL';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-9839_19012022', 'TarikRabeh', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2191, '8:8840b432339c9fb01d2c2a697b35641e', 'update tableName=dwh_report_extract', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-10047_20220926_default_sd_template::a.rouaguebe
INSERT INTO security_deposit_templat (id, template_name, code, status, created, version, creator) VALUES (nextval('security_deposit_templat_seq'), 'DEFAULT_SD_TEMPLATE', uuid_generate_v4(), 'ACTIVE', NOW(), 0, 'applicationInitializer') on conflict do nothing;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10047_20220926_default_sd_template', 'a.rouaguebe', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2192, '8:1678808daaf98e8f4a0cbefefc0864b9', 'insert tableName=security_deposit_templat', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-10247_20220928_default_currency_sd_template::a.rouaguebe
UPDATE security_deposit_templat SET currency_id = (SELECT currency_id from crm_provider where id = 1) WHERE template_name = 'DEFAULT_SD_TEMPLATE';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10247_20220928_default_currency_sd_template', 'a.rouaguebe', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2193, '8:2f566b27d371c59d247667e855d9a4d1', 'update tableName=security_deposit_templat', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-10199_20221010::TarikFA.
UPDATE ar_occ_template SET accounting_code_id = NULL WHERE id = -50;

DELETE FROM billing_accounting_code WHERE code = 'T3P9476';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10199_20221010', 'TarikFA.', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2194, '8:81e04d3ae50a7fe5c0ec3112b11dee95', 'update tableName=ar_occ_template; delete tableName=billing_accounting_code', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-10400_20221007::HichamHANINE
UPDATE security_deposit SET status = 'VALIDATED' WHERE status='NEW';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10400_20221007', 'HichamHANINE', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2195, '8:60faa1624686cd36d73bbea56bbf4780', 'update tableName=security_deposit', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-10592_20221014::TarikFA.
UPDATE billing_accounting_code SET description = 'General accounts payable' WHERE code = '401000000';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10592_20221014', 'TarikFA.', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2196, '8:be3103ed12cf52704063dc22d19084ef', 'update tableName=billing_accounting_code', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-10587_20221014::TarikFA.
UPDATE ar_occ_template SET accounting_code_id = NULL WHERE id in (SELECT id from billing_accounting_code where code in ('120.121.21.4', '120.121.21.2', 'T3P9476'));

UPDATE billing_invoice_sub_cat SET accounting_code_id = NULL WHERE id in (SELECT id from billing_accounting_code where code in ('120.121.21.4', '120.121.21.2', 'T3P9476'));

DELETE FROM billing_accounting_code WHERE code in ('120.121.21.4', '120.121.21.2');

UPDATE ar_occ_template SET accounting_code_id = (SELECT id FROM billing_accounting_code WHERE code='411000000') WHERE code = 'PAY_BATCH';

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10587_20221014', 'TarikFA.', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2197, '8:3dad3d376a5ed5769484fd6e308774e9', 'update tableName=ar_occ_template; update tableName=billing_invoice_sub_cat; delete tableName=billing_accounting_code; update tableName=ar_occ_template', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-10572_20221014::HichamHANINE
UPDATE crm_customer_category SET tax_category_id = '-3' WHERE id in (-1,-2);

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10572_20221014', 'HichamHANINE', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2198, '8:36eb72b8ec2441bc4a83fb1feb78e44b', 'update tableName=crm_customer_category', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Changeset src/main/db_resources/changelog/current/data.xml::#INTRD-10572_20221018::HichamHANINE
UPDATE crm_customer_category SET tax_category_id = '-1' WHERE id in (-1,-2);

UPDATE billing_tax_mapping SET tax_category_id = '-1' WHERE tax_category_id = -3;

UPDATE billing_billing_account SET tax_category_id = '-1' WHERE tax_category_id = -3;

UPDATE billing_tax_category SET code = 'REGULAR1' WHERE id = -3;

UPDATE billing_tax_category SET code = 'REGULAR', created = NOW(), creator = 'applicationInitializer', description_i18n = '{"FRA":"Standard","ENG":"Regular"}', uuid = 'billing_tax_category_regular' WHERE id = -1;

DELETE FROM billing_tax_category WHERE id = -3;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('#INTRD-10572_20221018', 'HichamHANINE', 'src/main/db_resources/changelog/current/data.xml', NOW(), 2199, '8:503eba4d321a37c562674d55805796ac', 'update tableName=crm_customer_category; update tableName=billing_tax_mapping; update tableName=billing_billing_account; update tableName=billing_tax_category; update tableName=billing_tax_category; delete tableName=billing_tax_category', '', 'EXECUTED', NULL, NULL, '3.8.0', '6800343140');

-- Release Database Lock
UPDATE databasechangeloglock SET LOCKED = FALSE, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;

