package org.meveo.service.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.AuditableField;
import org.meveo.model.admin.Seller;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.AuditCrudActionEnum;
import org.meveo.model.audit.AuditDataLog;
import org.meveo.model.audit.AuditDataLogRecord;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.audit.logging.AuditDataLogService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class AuditDataLogServiceTest {

    @Spy
    @InjectMocks
    private AuditDataLogService auditLogService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private AuditDataConfigurationService auditDataConfigurationService;

    @Mock
    private AuditableFieldService auditableFieldService;

    @Mock
    private Event<AuditableField> auditFieldEventProducer;

    @Before
    public void setUp() {
        // Need to use doReturn as auditLogService is spied and if when() is used, a real method would be called here
        doReturn(entityManager).when(auditLogService).getEntityManager();

        List<AuditDataHierarchy> dataHierarchies = new ArrayList<AuditDataHierarchy>();
        dataHierarchies.add(AuditDataConfigurationService.getAuditDataHierarchy(OfferTemplate.class));
        dataHierarchies.add(AuditDataConfigurationService.getAuditDataHierarchy(Subscription.class));
        dataHierarchies.add(AuditDataConfigurationService.getAuditDataHierarchy(Seller.class));

        when(auditDataConfigurationService.getAuditDataHierarchies()).thenReturn(dataHierarchies);
    }

    @Test
    public void test_Aggregation_Simple_SeparateTX() {

        AuditDataHierarchy dataHierarchy = AuditDataConfigurationService.getAuditDataHierarchy(Seller.class);

        List<AuditDataLogRecord> auditDataLogRecords = new ArrayList<>();

        AuditDataLogRecord auditDataLogRecord = new AuditDataLogRecord(1L, new Date(), "opencell.admin", "crm_seller", 5L, 111L, "INSERT", "GUI", "page/seller", null,
            "{\"email\": \"test@opencellsoft.com\", \"phone\": \"03546536168\", \"updated\": \"2022-10-19T14:17:39.745\", \"updater\": \"opencell.superadmin\", \"version\": 1, \"address_1\": \"El Aguacero S/N\", \"description\": \"France Seller test\", \"address_city\": \"Los Reartes\", \"address_zipcode\": \"5194\", \"address_country_id\": 10}");
        auditDataLogRecords.add(auditDataLogRecord);

        auditDataLogRecord = new AuditDataLogRecord(2L, new Date(), "opencell.admin", "crm_seller", 6L, 112L, "UPDATE", "JOB", "page/seller",
            "{\"email\": null, \"phone\": null, \"trading_currency_id\": 4, \"trading_country_id\": 8}",
            "{\"email\": \"test@opencellsoft.com\", \"phone\": \"1243324\", \"updated\": \"2022-10-19T14:17:39.745\", \"updater\": \"opencell.superadmin\", \"trading_currency_id\": 5, \"trading_country_id\": 7}");
        auditDataLogRecords.add(auditDataLogRecord);

        List<AuditDataLog> auditDataLogs = auditLogService.aggregateAuditLogs(dataHierarchy, auditDataLogRecords);

        assertThat(auditDataLogs.size()).isEqualTo(2);
        assertThat(auditDataLogs.get(0).getAction()).isEqualTo(AuditCrudActionEnum.INSERT);
        assertThat(auditDataLogs.get(0).getEntityClass()).isEqualTo(Seller.class.getName());
        assertThat(auditDataLogs.get(0).getEntityId()).isEqualTo(5L);
        assertThat(auditDataLogs.get(0).getTxId()).isEqualTo(111L);
        assertThat(auditDataLogs.get(0).getOrigin()).isEqualTo(ChangeOriginEnum.GUI);
        assertThat(auditDataLogs.get(0).getOriginName()).isEqualTo("page/seller");
        assertThat(auditDataLogs.get(0).getUserName()).isEqualTo("opencell.admin");
        assertThat(auditDataLogs.get(0).getValuesOld()).isNull();
        assertThat(auditDataLogs.get(0).getValuesChanged().get("contactInformation.email")).isEqualTo("test@opencellsoft.com");
        assertThat(auditDataLogs.get(0).getValuesChanged().get("contactInformation.phone")).isEqualTo("03546536168");
        assertThat(auditDataLogs.get(0).getValuesChanged().get("address.city")).isEqualTo("Los Reartes");

        assertThat(auditDataLogs.get(1).getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(auditDataLogs.get(1).getEntityClass()).isEqualTo(Seller.class.getName());
        assertThat(auditDataLogs.get(1).getEntityId()).isEqualTo(6L);
        assertThat(auditDataLogs.get(1).getTxId()).isEqualTo(112L);
        assertThat(auditDataLogs.get(1).getOrigin()).isEqualTo(ChangeOriginEnum.JOB);
        assertThat(auditDataLogs.get(1).getOriginName()).isEqualTo("page/seller");
        assertThat(auditDataLogs.get(1).getUserName()).isEqualTo("opencell.admin");
        assertThat(auditDataLogs.get(1).getValuesChanged().get("contactInformation.email")).isEqualTo("test@opencellsoft.com");
        assertThat(auditDataLogs.get(1).getValuesChanged().get("contactInformation.phone")).isEqualTo("1243324");
        assertThat(auditDataLogs.get(1).getValuesChanged().get("tradingCurrency")).isEqualTo(5);

        assertThat(auditDataLogs.get(1).getValuesOld().get("tradingCurrency")).isEqualTo(4);
        assertThat(auditDataLogs.get(1).getValuesOld().get("tradingCountry")).isEqualTo(8);

        assertThat(auditDataLogRecords).isEmpty();

    }

    @Test
    public void test_Aggregation_Simple_SameTX() {

        AuditDataHierarchy dataHierarchy = AuditDataConfigurationService.getAuditDataHierarchy(Seller.class);

        List<AuditDataLogRecord> auditDataLogRecords = new ArrayList<>();

        AuditDataLogRecord auditDataLogRecord = new AuditDataLogRecord(1L, new Date(), "opencell.admin", "crm_seller", 5L, 111L, "INSERT", "GUI", "page/seller", null,
            "{\"email\": \"test@opencellsoft.com\", \"phone\": \"03546536168\", \"updated\": \"2022-10-19T14:17:39.745\", \"updater\": \"opencell.superadmin\", \"version\": 1, \"address_1\": \"El Aguacero S/N\", \"description\": \"France Seller test\", \"address_city\": \"Los Reartes\", \"address_zipcode\": \"5194\", \"address_country_id\": 10}");
        auditDataLogRecords.add(auditDataLogRecord);

        auditDataLogRecord = new AuditDataLogRecord(2L, new Date(), "opencell.admin", "crm_seller", 6L, 111L, "UPDATE", "JOB", "page/seller",
            "{\"email\": null, \"phone\": null, \"trading_currency_id\": 4, \"trading_country_id\": 8}",
            "{\"email\": \"test@opencellsoft.com\", \"phone\": \"1243324\", \"updated\": \"2022-10-19T14:17:39.745\", \"updater\": \"opencell.superadmin\", \"trading_currency_id\": 5, \"trading_country_id\": 7}");
        auditDataLogRecords.add(auditDataLogRecord);

        List<AuditDataLog> auditDataLogs = auditLogService.aggregateAuditLogs(dataHierarchy, auditDataLogRecords);

        assertThat(auditDataLogs.size()).isEqualTo(2);
        assertThat(auditDataLogs.get(0).getAction()).isEqualTo(AuditCrudActionEnum.INSERT);
        assertThat(auditDataLogs.get(0).getEntityClass()).isEqualTo(Seller.class.getName());
        assertThat(auditDataLogs.get(0).getEntityId()).isEqualTo(5L);
        assertThat(auditDataLogs.get(0).getTxId()).isEqualTo(111L);
        assertThat(auditDataLogs.get(0).getOrigin()).isEqualTo(ChangeOriginEnum.GUI);
        assertThat(auditDataLogs.get(0).getOriginName()).isEqualTo("page/seller");
        assertThat(auditDataLogs.get(0).getUserName()).isEqualTo("opencell.admin");
        assertThat(auditDataLogs.get(0).getValuesOld()).isNull();
        assertThat(auditDataLogs.get(0).getValuesChanged().get("contactInformation.email")).isEqualTo("test@opencellsoft.com");
        assertThat(auditDataLogs.get(0).getValuesChanged().get("contactInformation.phone")).isEqualTo("03546536168");
        assertThat(auditDataLogs.get(0).getValuesChanged().get("address.city")).isEqualTo("Los Reartes");

        assertThat(auditDataLogs.get(1).getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(auditDataLogs.get(1).getEntityClass()).isEqualTo(Seller.class.getName());
        assertThat(auditDataLogs.get(1).getEntityId()).isEqualTo(6L);
        assertThat(auditDataLogs.get(1).getTxId()).isEqualTo(111L);
        assertThat(auditDataLogs.get(1).getOrigin()).isEqualTo(ChangeOriginEnum.JOB);
        assertThat(auditDataLogs.get(1).getOriginName()).isEqualTo("page/seller");
        assertThat(auditDataLogs.get(1).getUserName()).isEqualTo("opencell.admin");
        assertThat(auditDataLogs.get(1).getValuesChanged().get("contactInformation.email")).isEqualTo("test@opencellsoft.com");
        assertThat(auditDataLogs.get(1).getValuesChanged().get("contactInformation.phone")).isEqualTo("1243324");
        assertThat(auditDataLogs.get(1).getValuesChanged().get("tradingCurrency")).isEqualTo(5);

        assertThat(auditDataLogs.get(1).getValuesOld().get("tradingCurrency")).isEqualTo(4);
        assertThat(auditDataLogs.get(1).getValuesOld().get("tradingCountry")).isEqualTo(8);

        assertThat(auditDataLogRecords).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_Aggregation_Complex() {

        when(entityManager.createQuery(any(), any())).thenAnswer(new Answer<QuerySimulation>() {
            public QuerySimulation answer(InvocationOnMock invocation) throws Throwable {
                QuerySimulation query = new QuerySimulation();
                return query;
            }
        });

        AuditDataHierarchy dataHierarchy = AuditDataConfigurationService.getAuditDataHierarchy(OfferTemplate.class);

        List<AuditDataLogRecord> auditDataLogRecords = new ArrayList<>();

        // Run this sql to get the java version of AuditDataLogRecords
        // select 'auditDataLogRecord = new AuditDataLogRecord('||id||'L, new Date(),"opencell.admin","'||ref_table||'", '||case when ref_id is null then 'null' else ref_id||'L' end ||', '||tx_id||'L, "'||action||'",
        // "'||origin||'", "'||origin_name||'", '||case when data_old is null then 'null' else '"'||replace(data_old::varchar,'"', '\"')||'"' end ||', '||case when data_new is null then 'null' else
        // '"'||replace(data_new::varchar,'"', '\"')||'"' end ||'); auditDataLogRecords.add(auditDataLogRecord);' from audit_data_log_rec order by id

        // Offer A creation
        AuditDataLogRecord auditDataLogRecord = new AuditDataLogRecord(623L, new Date(), "opencell.admin", "cat_offer_template", 22L, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 22, \"code\": \"OfferA\", \"name\": \"Offer A\", \"type\": \"OFFER\", \"uuid\": \"252d46f8-4030-4542-9b87-d6ea0eccb511\", \"created\": \"2022-12-19T10:25:16.6\", \"creator\": \"opencell.admin\", \"disabled\": 0, \"is_model\": 0, \"auto_renew\": 0, \"image_path\": \"252d46f8-4030-4542-9b87-d6ea0eccb511.png\", \"valid_from\": \"2022-02-02T00:00:00\", \"description\": \"Offer A description\", \"status_date\": \"2022-12-19T10:25:16.417\", \"description_i18n\": {\"ENG\": \"Offer A EN\", \"FRA\": \"Offer A FR\"}, \"long_description\": \"Long description\", \"initial_term_type\": \"RECURRING\", \"life_cycle_status\": \"ACTIVE\", \"renewal_term_type\": \"RECURRING\", \"match_end_aggr_date\": 0, \"long_description_i18n\": {\"ENG\": \"Offer A long EN\", \"FRA\": \"Offer A long FR\"}, \"auto_end_of_engagement\": 0, \"is_offer_change_restricted\": 0, \"generate_quote_edr_per_product\": 0}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(624L, new Date(), "opencell.admin", "cat_offer_serv_templates", 56L, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 56, \"mandatory\": 0, \"offer_template_id\": 22, \"service_template_id\": 1}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(625L, new Date(), "opencell.admin", "cat_offer_serv_templates", 57L, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 57, \"mandatory\": 1, \"offer_template_id\": 22, \"service_template_id\": 2}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(626L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(627L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 3, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(628L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 1, \"offer_service_template_id\": 57}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(629L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 57}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(630L, new Date(), "opencell.admin", "cat_product_offer_customer_category", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"indx\": 0, \"product_id\": 22, \"customer_category_id\": -1}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(631L, new Date(), "opencell.admin", "cat_product_offer_customer_category", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"indx\": 1, \"product_id\": 22, \"customer_category_id\": -2}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(632L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -1, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(633L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -2, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(634L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -3, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Update Offer A - basic offer data

        auditDataLogRecord = new AuditDataLogRecord(635L, new Date(), "opencell.admin", "cat_offer_template", 22L, 5909052L, "UPDATE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"name\": \"Offer A\", \"updated\": null, \"updater\": null, \"description\": \"Offer A description\", \"status_date\": \"2022-12-19T10:25:16.417\", \"description_i18n\": {\"ENG\": \"Offer A EN\", \"FRA\": \"Offer A FR\"}}",
            "{\"name\": \"Offer A changed\", \"updated\": \"2022-12-19T10:28:44.454\", \"updater\": \"opencell.admin\", \"description\": \"Offer A description changed\", \"status_date\": \"2022-12-19T10:28:44.295\", \"description_i18n\": {\"ENG\": \"Offer A EN changed\", \"FRA\": \"Offer A FR\"}}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(636L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909052L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(637L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909052L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 3, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(638L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909052L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 1, \"offer_service_template_id\": 57}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(639L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909052L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 4, \"offer_service_template_id\": 57}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(640L, new Date(), "opencell.admin", "cat_product_offer_customer_category", null, 5909052L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"indx\": 1, \"product_id\": 22, \"customer_category_id\": -2}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(641L, new Date(), "opencell.admin", "cat_product_offer_customer_category", null, 5909052L, "UPDATE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"product_id\": 22, \"customer_category_id\": -1}", "{\"product_id\": 22, \"customer_category_id\": -2}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(642L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909052L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -1, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(643L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909052L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -2, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(644L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909052L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -3, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(645L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909052L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -1, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(646L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909052L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -3, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(647L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909052L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(648L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909052L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 3, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(649L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909052L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 1, \"offer_service_template_id\": 57}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(650L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909052L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 57}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Update Offer A - change incompatible services in Service A

        auditDataLogRecord = new AuditDataLogRecord(651L, new Date(), "opencell.admin", "cat_offer_template", 22L, 5909056L, "UPDATE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"updated\": \"2022-12-19T10:28:44.454\", \"status_date\": \"2022-12-19T10:28:44.295\"}", "{\"updated\": \"2022-12-19T10:30:20.01\", \"status_date\": \"2022-12-19T10:30:19.818\"}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(652L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909056L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(653L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909056L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 3, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(654L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909056L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 1, \"offer_service_template_id\": 57}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(655L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909056L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 4, \"offer_service_template_id\": 57}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(656L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909056L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -1, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(657L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909056L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -3, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(658L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909056L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -1, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(659L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909056L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -3, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(660L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909056L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(661L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909056L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(662L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909056L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 1, \"offer_service_template_id\": 57}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(663L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909056L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 57}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Update Offer A - change Service B and remove incompatible service

        auditDataLogRecord = new AuditDataLogRecord(664L, new Date(), "opencell.admin", "cat_offer_serv_templates", 57L, 5909061L, "UPDATE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"mandatory\": 1, \"offer_template_id\": 22}", "{\"mandatory\": 0, \"offer_template_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(665L, new Date(), "opencell.admin", "cat_offer_template", 22L, 5909061L, "UPDATE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"updated\": \"2022-12-19T10:30:20.01\", \"status_date\": \"2022-12-19T10:30:19.818\"}", "{\"updated\": \"2022-12-19T10:31:46.518\", \"status_date\": \"2022-12-19T10:31:46.389\"}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(666L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909061L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(667L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909061L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 4, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(668L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909061L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 1, \"offer_service_template_id\": 57}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(669L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909061L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 4, \"offer_service_template_id\": 57}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(670L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909061L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -1, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(671L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909061L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -3, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(672L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909061L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -1, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(673L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909061L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -3, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(674L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909061L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(675L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909061L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(676L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909061L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 1, \"offer_service_template_id\": 57}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Update Offer A - delete Service B and add Service C

        auditDataLogRecord = new AuditDataLogRecord(677L, new Date(), "opencell.admin", "cat_offer_serv_templates", 58L, 5909066L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 58, \"mandatory\": 0, \"offer_template_id\": 22, \"service_template_id\": 3}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(678L, new Date(), "opencell.admin", "cat_offer_template", 22L, 5909066L, "UPDATE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"updated\": \"2022-12-19T10:31:46.518\", \"status_date\": \"2022-12-19T10:31:46.389\"}", "{\"updated\": \"2022-12-19T10:34:00.013\", \"status_date\": \"2022-12-19T10:33:59.758\"}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(679L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909066L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(680L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909066L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 4, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(681L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909066L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 1, \"offer_service_template_id\": 57}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(682L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909066L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -1, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(683L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909066L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -3, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(684L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909066L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -1, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(685L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909066L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -3, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(686L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909066L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(687L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909066L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(688L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909066L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 58}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(689L, new Date(), "opencell.admin", "cat_offer_serv_templates", 57L, 5909066L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"id\": 57, \"mandatory\": 0, \"offer_template_id\": 22, \"service_template_id\": 2}", null);
        auditDataLogRecords.add(auditDataLogRecord);

        // Delete Offer A

        auditDataLogRecord = new AuditDataLogRecord(690L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909071L, "DELETE", "API", "/catalog/offerTemplate/{offerTemplateCode}",
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(691L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909071L, "DELETE", "API", "/catalog/offerTemplate/{offerTemplateCode}",
            "{\"service_template_id\": 4, \"offer_service_template_id\": 56}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(692L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909071L, "DELETE", "API", "/catalog/offerTemplate/{offerTemplateCode}",
            "{\"service_template_id\": 4, \"offer_service_template_id\": 58}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(693L, new Date(), "opencell.admin", "cat_product_offer_customer_category", null, 5909071L, "DELETE", "API", "/catalog/offerTemplate/{offerTemplateCode}",
            "{\"indx\": 0, \"product_id\": 22, \"customer_category_id\": -2}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(694L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909071L, "DELETE", "API", "/catalog/offerTemplate/{offerTemplateCode}",
            "{\"seller_id\": -1, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(695L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909071L, "DELETE", "API", "/catalog/offerTemplate/{offerTemplateCode}",
            "{\"seller_id\": -3, \"product_id\": 22}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(696L, new Date(), "opencell.admin", "cat_offer_serv_templates", 56L, 5909071L, "DELETE", "API", "/catalog/offerTemplate/{offerTemplateCode}",
            "{\"id\": 56, \"mandatory\": 0, \"offer_template_id\": 22, \"service_template_id\": 1}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(697L, new Date(), "opencell.admin", "cat_offer_serv_templates", 58L, 5909071L, "DELETE", "API", "/catalog/offerTemplate/{offerTemplateCode}",
            "{\"id\": 58, \"mandatory\": 0, \"offer_template_id\": 22, \"service_template_id\": 3}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(698L, new Date(), "opencell.admin", "cat_offer_template", 22L, 5909071L, "DELETE", "API", "/catalog/offerTemplate/{offerTemplateCode}",
            "{\"id\": 22, \"code\": \"OfferA\", \"name\": \"Offer A changed\", \"type\": \"OFFER\", \"uuid\": \"252d46f8-4030-4542-9b87-d6ea0eccb511\", \"created\": \"2022-12-19T10:25:16.6\", \"creator\": \"opencell.admin\", \"updated\": \"2022-12-19T10:34:00.013\", \"updater\": \"opencell.admin\", \"disabled\": 0, \"is_model\": 0, \"auto_renew\": 0, \"image_path\": \"252d46f8-4030-4542-9b87-d6ea0eccb511.png\", \"valid_from\": \"2022-02-02T00:00:00\", \"description\": \"Offer A description changed\", \"status_date\": \"2022-12-19T10:33:59.758\", \"description_i18n\": {\"ENG\": \"Offer A EN changed\", \"FRA\": \"Offer A FR\"}, \"long_description\": \"Long description\", \"initial_term_type\": \"RECURRING\", \"life_cycle_status\": \"ACTIVE\", \"renewal_term_type\": \"RECURRING\", \"match_end_aggr_date\": 0, \"long_description_i18n\": {\"ENG\": \"Offer A long EN\", \"FRA\": \"Offer A long FR\"}, \"auto_end_of_engagement\": 0, \"is_offer_change_restricted\": 0, \"generate_quote_edr_per_product\": 0}",
            null);
        auditDataLogRecords.add(auditDataLogRecord);

        List<AuditDataLog> auditDataLogs = auditLogService.aggregateAuditLogs(dataHierarchy, auditDataLogRecords);

        assertThat(auditDataLogs.size()).isEqualTo(6);

        // ------
        // Offer A creation

        // Changed value:
        //
        // {isOfferChangeRestricted=0, subscriptionRenewal.initialTermType=RECURRING, auditable.created=2022-12-19T10:25:16.6, auditable.creator=opencell.admin, lifeCycleStatus=ACTIVE,
        // longDescriptionI18n={ENG=Offer A long EN, FRA=Offer A long FR}, disabled=0, subscriptionRenewal.extendAgreementPeriodToSubscribedTillDate=0, id=22, subscriptionRenewal.autoRenew=0,
        // generateQuoteEdrPerProduct=0, code=OfferA, isModel=0, imagePath=252d46f8-4030-4542-9b87-d6ea0eccb511.png, subscriptionRenewal.renewalTermType=RECURRING,
        // descriptionI18n={ENG=Offer A EN, FRA=Offer A FR}, uuid=252d46f8-4030-4542-9b87-d6ea0eccb511, statusDate=2022-12-19T10:25:16.417, description=Offer A description,
        // autoEndOfEngagement=0, name=Offer A, longDescription=Long description, offerServiceTemplates_56_INSERT={serviceTemplate=1, mandatory=0, incompatibleServices_INSERT=[2, 3],
        // offerTemplate=22, id=56}, offerServiceTemplates_57_INSERT={serviceTemplate=2, mandatory=1, incompatibleServices_INSERT=[1, 4], offerTemplate=22, id=57},
        // customerCategories_INSERT=[-1, -2], sellers_INSERT=[-1, -2, -3]}

        assertThat(auditDataLogs.get(0).getAction()).isEqualTo(AuditCrudActionEnum.INSERT);
        assertThat(auditDataLogs.get(0).getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(auditDataLogs.get(0).getEntityId()).isEqualTo(22L);
        assertThat(auditDataLogs.get(0).getTxId()).isEqualTo(5909046L);
        assertThat(auditDataLogs.get(0).getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(auditDataLogs.get(0).getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(auditDataLogs.get(0).getUserName()).isEqualTo("opencell.admin");

        assertThat(auditDataLogs.get(0).getValuesChanged().get("code")).isEqualTo("OfferA");
        assertThat(auditDataLogs.get(0).getValuesChanged().get("name")).isEqualTo("Offer A");
        assertThat(auditDataLogs.get(0).getValuesChanged().get("description")).isEqualTo("Offer A description");

        assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("offerTemplate")).isEqualTo(auditDataLogs.get(0).getEntityId().intValue());
        assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("serviceTemplate")).isEqualTo(1);
        assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("id")).isEqualTo(56);
        assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("mandatory")).isEqualTo(0);
//      assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("validity.from")).isEqualTo("2022-11-01T00:00:00");
//      assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("validity.to")).isEqualTo("2022-11-24T00:00:00"); 
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("incompatibleServices_INSERT")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("incompatibleServices_INSERT")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("incompatibleServices_INSERT")).get(1)).isEqualTo(3);

        assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("offerTemplate")).isEqualTo(auditDataLogs.get(0).getEntityId().intValue());
        assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("serviceTemplate")).isEqualTo(2);
        assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("id")).isEqualTo(57);
        assertThat(((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("mandatory")).isEqualTo(1);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("incompatibleServices_INSERT")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("incompatibleServices_INSERT")).get(0)).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("incompatibleServices_INSERT")).get(1)).isEqualTo(4);

        assertThat(((List<Object>) auditDataLogs.get(0).getValuesChanged().get("customerCategories_INSERT")).size()).isEqualTo(2);
        assertThat(((List<Object>) auditDataLogs.get(0).getValuesChanged().get("customerCategories_INSERT")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) auditDataLogs.get(0).getValuesChanged().get("customerCategories_INSERT")).get(1)).isEqualTo(-2);

        assertThat(((List<Object>) auditDataLogs.get(0).getValuesChanged().get("sellers_INSERT")).size()).isEqualTo(3);
        assertThat(((List<Object>) auditDataLogs.get(0).getValuesChanged().get("sellers_INSERT")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) auditDataLogs.get(0).getValuesChanged().get("sellers_INSERT")).get(1)).isEqualTo(-2);
        assertThat(((List<Object>) auditDataLogs.get(0).getValuesChanged().get("sellers_INSERT")).get(2)).isEqualTo(-3);

        assertThat((auditDataLogs.get(0).getValuesOld())).isNull();

        // ------
        // Update Offer A - basic data, customer category and seller removal

        // Changed value:
        //
        // {descriptionI18n={ENG=Offer A EN changed, FRA=Offer A FR}, statusDate=2022-12-19T10:28:44.295, description=Offer A description changed, auditable.updated=2022-12-19T10:28:44.454,
        // name=Offer A changed, auditable.updater=opencell.admin, customerCategories_DELETE=[-2], customerCategories_UPDATE=[-2], sellers_DELETE=[-2]}

        assertThat(auditDataLogs.get(1).getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(auditDataLogs.get(1).getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(auditDataLogs.get(1).getEntityId()).isEqualTo(22L);
        assertThat(auditDataLogs.get(1).getTxId()).isEqualTo(5909052L);
        assertThat(auditDataLogs.get(1).getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(auditDataLogs.get(1).getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(auditDataLogs.get(1).getUserName()).isEqualTo("opencell.admin");

        assertThat(auditDataLogs.get(1).getValuesChanged().get("code")).isNull();
        assertThat(auditDataLogs.get(1).getValuesChanged().get("name")).isEqualTo("Offer A changed");
        assertThat(auditDataLogs.get(1).getValuesChanged().get("description")).isEqualTo("Offer A description changed");
        assertThat(((Map<String, Object>) auditDataLogs.get(1).getValuesChanged().get("descriptionI18n")).get("ENG")).isEqualTo("Offer A EN changed");

        assertThat(((List<Object>) auditDataLogs.get(1).getValuesChanged().get("sellers_DELETE")).size()).isEqualTo(1);
        assertThat(((List<Object>) auditDataLogs.get(1).getValuesChanged().get("sellers_DELETE")).get(0)).isEqualTo(-2);

//        assertThat(((List<Object>) auditDataLogs.get(1).getValuesChanged().get("customerCategories_DELETE")).size()).isEqualTo(1);// AK for now fails
//        assertThat(((List<Object>) auditDataLogs.get(1).getValuesChanged().get("customerCategories_DELETE")).get(0)).isEqualTo(-1);// AK for now fails - treats that -2 was deleted, when in reality -1 was deleted

        assertThat(((List<Object>) auditDataLogs.get(1).getValuesChanged().get("customerCategories_UPDATE")).size()).isEqualTo(1);
        assertThat(((List<Object>) auditDataLogs.get(1).getValuesChanged().get("customerCategories_UPDATE")).get(0)).isEqualTo(-2);

        assertThat(auditDataLogs.get(1).getValuesChanged().get("offerServiceTemplates_56")).isNull();
        assertThat(auditDataLogs.get(1).getValuesChanged().get("offerServiceTemplates_57")).isNull();

        // Old value:
        //
        // {descriptionI18n={ENG=Offer A EN, FRA=Offer A FR}, statusDate=2022-12-19T10:25:16.417, description=Offer A description, auditable.updated=null, name=Offer A, auditable.updater=null,
        // customerCategories=[-2, -1], sellers=[-1, -2, -3], offerServiceTemplates_57={incompatibleServices=[1, 4]}, offerServiceTemplates_56={incompatibleServices=[2, 3]}}

        assertThat(auditDataLogs.get(1).getValuesOld().get("code")).isNull();
        assertThat(auditDataLogs.get(1).getValuesOld().get("name")).isEqualTo("Offer A");
        assertThat(auditDataLogs.get(1).getValuesOld().get("description")).isEqualTo("Offer A description");

        assertThat(((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("descriptionI18n")).size()).isEqualTo(2);
        assertThat(((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("descriptionI18n")).get("ENG")).isEqualTo("Offer A EN");

        assertThat(((List<Object>) auditDataLogs.get(1).getValuesOld().get("sellers")).size()).isEqualTo(3);
        assertThat(((List<Object>) auditDataLogs.get(1).getValuesOld().get("sellers")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) auditDataLogs.get(1).getValuesOld().get("sellers")).get(1)).isEqualTo(-2);
        assertThat(((List<Object>) auditDataLogs.get(1).getValuesOld().get("sellers")).get(2)).isEqualTo(-3);

        assertThat(((List<Object>) auditDataLogs.get(1).getValuesOld().get("customerCategories")).size()).isEqualTo(2);
        assertThat(((List<Object>) auditDataLogs.get(1).getValuesOld().get("customerCategories")).get(0)).isEqualTo(-2);
        assertThat(((List<Object>) auditDataLogs.get(1).getValuesOld().get("customerCategories")).get(1)).isEqualTo(-1);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(1)).isEqualTo(3);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).get(0)).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).get(1)).isEqualTo(4);

        // ------
        // Update Offer A - change incompatible services in Service A

        // Changed value:
        //
        // {statusDate=2022-12-19T10:30:19.818, auditable.updated=2022-12-19T10:30:20.01,
        // offerServiceTemplates_56={incompatibleServices_DELETE=[3], incompatibleServices_INSERT=[4]}}

        assertThat(auditDataLogs.get(2).getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(auditDataLogs.get(2).getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(auditDataLogs.get(2).getEntityId()).isEqualTo(22L);
        assertThat(auditDataLogs.get(2).getTxId()).isEqualTo(5909056L);
        assertThat(auditDataLogs.get(2).getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(auditDataLogs.get(2).getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(auditDataLogs.get(2).getUserName()).isEqualTo("opencell.admin");

        assertThat(auditDataLogs.get(2).getValuesChanged().get("code")).isNull();
        assertThat(auditDataLogs.get(2).getValuesChanged().get("statusDate")).isEqualTo("2022-12-19T10:30:19.818");
        assertThat(auditDataLogs.get(2).getValuesChanged().get("auditable.updated")).isEqualTo("2022-12-19T10:30:20.01");
// AK Why not 56_update?
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(2).getValuesChanged().get("offerServiceTemplates_56")).get("incompatibleServices_DELETE")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(2).getValuesChanged().get("offerServiceTemplates_56")).get("incompatibleServices_DELETE")).get(0)).isEqualTo(3);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(2).getValuesChanged().get("offerServiceTemplates_56")).get("incompatibleServices_INSERT")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(2).getValuesChanged().get("offerServiceTemplates_56")).get("incompatibleServices_INSERT")).get(0)).isEqualTo(4);

        assertThat(auditDataLogs.get(2).getValuesChanged().get("offerServiceTemplates_57")).isNull();

        // Old value:
        //
        // {statusDate=2022-12-19T10:28:44.295, auditable.updated=2022-12-19T10:28:44.454, sellers=[-1, -3], offerServiceTemplates_57={incompatibleServices=[1, 4]},
        // offerServiceTemplates_56={incompatibleServices=[2, 3]}}

        assertThat(auditDataLogs.get(2).getValuesOld().get("statusDate")).isEqualTo("2022-12-19T10:28:44.295");
        assertThat(auditDataLogs.get(2).getValuesOld().get("auditable.updated")).isEqualTo("2022-12-19T10:28:44.454");

        assertThat(((List<Object>) auditDataLogs.get(2).getValuesOld().get("sellers")).size()).isEqualTo(2);
        assertThat(((List<Object>) auditDataLogs.get(2).getValuesOld().get("sellers")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) auditDataLogs.get(2).getValuesOld().get("sellers")).get(1)).isEqualTo(-3);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(2).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(2).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(2).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(1)).isEqualTo(3);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(2).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(2).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).get(0)).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(2).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).get(1)).isEqualTo(4);

        // ------
        // Update Offer A - change Service B and remove incompatible service

        // Changed value:
        //
        // {statusDate=2022-12-19T10:31:46.389, auditable.updated=2022-12-19T10:31:46.518, offerServiceTemplates_57_UPDATE={mandatory=0, offerTemplate=22, incompatibleServices_DELETE=[4]}}

        assertThat(auditDataLogs.get(3).getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(auditDataLogs.get(3).getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(auditDataLogs.get(3).getEntityId()).isEqualTo(22L);
        assertThat(auditDataLogs.get(3).getTxId()).isEqualTo(5909061L);
        assertThat(auditDataLogs.get(3).getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(auditDataLogs.get(3).getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(auditDataLogs.get(3).getUserName()).isEqualTo("opencell.admin");

        assertThat(auditDataLogs.get(3).getValuesChanged().get("code")).isNull();
        assertThat(auditDataLogs.get(3).getValuesChanged().get("auditable.updated")).isNotNull();

        assertThat(((Map<String, Object>) auditDataLogs.get(3).getValuesChanged().get("offerServiceTemplates_57_UPDATE")).get("mandatory")).isEqualTo(0);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(3).getValuesChanged().get("offerServiceTemplates_57_UPDATE")).get("incompatibleServices_DELETE")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(3).getValuesChanged().get("offerServiceTemplates_57_UPDATE")).get("incompatibleServices_DELETE")).get(0)).isEqualTo(4);

        assertThat(auditDataLogs.get(3).getValuesChanged().get("offerServiceTemplates_56")).isNull();

        // Old value:
        //
        // {statusDate=2022-12-19T10:30:19.818, auditable.updated=2022-12-19T10:30:20.01, offerServiceTemplates_57={incompatibleServices=[1, 4], mandatory=1, offerTemplate=22},
        // sellers=[-1, -3], offerServiceTemplates_56={incompatibleServices=[2, 4]}}

        assertThat(auditDataLogs.get(3).getValuesOld().get("statusDate")).isEqualTo("2022-12-19T10:30:19.818");
        assertThat(auditDataLogs.get(3).getValuesOld().get("auditable.updated")).isEqualTo("2022-12-19T10:30:20.01");

        assertThat(((List<Object>) auditDataLogs.get(3).getValuesOld().get("sellers")).size()).isEqualTo(2);
        assertThat(((List<Object>) auditDataLogs.get(3).getValuesOld().get("sellers")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) auditDataLogs.get(3).getValuesOld().get("sellers")).get(1)).isEqualTo(-3);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(3).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(3).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(3).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(1)).isEqualTo(4);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(3).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(3).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).get(0)).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(3).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).get(1)).isEqualTo(4);

        assertThat(((Map<String, Object>) auditDataLogs.get(3).getValuesOld().get("offerServiceTemplates_57")).get("mandatory")).isEqualTo(1);

        // ------
        // Update Offer A - delete Service B and add Service C

        // Changed value:
        //
        // {statusDate=2022-12-19T10:33:59.758, auditable.updated=2022-12-19T10:34:00.013, offerServiceTemplates_58_INSERT={serviceTemplate=3, mandatory=0, incompatibleServices_INSERT=[4], offerTemplate=22, id=58},
        // offerServiceTemplates_57_DELETE={ serviceTemplate=2, mandatory=0, offerTemplate=22, id=57, incompatibleServices_DELETE=[1]}}

        assertThat(auditDataLogs.get(4).getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(auditDataLogs.get(4).getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(auditDataLogs.get(4).getEntityId()).isEqualTo(22L);
        assertThat(auditDataLogs.get(4).getTxId()).isEqualTo(5909066L);
        assertThat(auditDataLogs.get(4).getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(auditDataLogs.get(4).getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(auditDataLogs.get(4).getUserName()).isEqualTo("opencell.admin");

        assertThat(auditDataLogs.get(4).getValuesChanged().get("code")).isNull();
        assertThat(auditDataLogs.get(4).getValuesChanged().get("auditable.updated")).isNotNull();

        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_58_INSERT")).get("offerTemplate")).isEqualTo(auditDataLogs.get(0).getEntityId().intValue());
        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_58_INSERT")).get("serviceTemplate")).isEqualTo(3);
        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_58_INSERT")).get("id")).isEqualTo(58);
        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_58_INSERT")).get("mandatory")).isEqualTo(0);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_58_INSERT")).get("incompatibleServices_INSERT")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_58_INSERT")).get("incompatibleServices_INSERT")).get(0)).isEqualTo(4);

        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_57_DELETE")).get("offerTemplate")).isEqualTo(auditDataLogs.get(0).getEntityId().intValue());
        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_57_DELETE")).get("serviceTemplate")).isEqualTo(2);
        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_57_DELETE")).get("id")).isEqualTo(57);
        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_57_DELETE")).get("mandatory")).isEqualTo(0);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_57_DELETE")).get("incompatibleServices_DELETE")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_57_DELETE")).get("incompatibleServices_DELETE")).get(0)).isEqualTo(1);

        assertThat(auditDataLogs.get(4).getValuesChanged().get("offerServiceTemplates_56")).isNull();

        // Old value:
        //
        // {statusDate=2022-12-19T10:31:46.389, auditable.updated=2022-12-19T10:31:46.518, sellers=[-1, -3],
        // offerServiceTemplates_57={incompatibleServices=[1], serviceTemplate=2, mandatory=0, offerTemplate=22, id=57}, offerServiceTemplates_56={incompatibleServices=[2, 4]}}

        assertThat(auditDataLogs.get(4).getValuesOld().get("statusDate")).isEqualTo(auditDataLogs.get(3).getValuesChanged().get("statusDate"));
        assertThat(auditDataLogs.get(4).getValuesOld().get("auditable.updated")).isEqualTo("2022-12-19T10:31:46.518");

        assertThat(((List<Object>) auditDataLogs.get(4).getValuesOld().get("sellers")).size()).isEqualTo(2);
        assertThat(((List<Object>) auditDataLogs.get(4).getValuesOld().get("sellers")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) auditDataLogs.get(4).getValuesOld().get("sellers")).get(1)).isEqualTo(-3);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(4).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(4).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(4).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(1)).isEqualTo(4);

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(4).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(4).getValuesOld().get("offerServiceTemplates_57")).get("incompatibleServices")).get(0)).isEqualTo(1);

        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesOld().get("offerServiceTemplates_57")).get("mandatory")).isEqualTo(0);
        assertThat(((Map<String, Object>) auditDataLogs.get(4).getValuesOld().get("offerServiceTemplates_57")).get("serviceTemplate")).isEqualTo(2);

        // ------
        // Delete Offer A

        // Changed value:
        //
        // {id=22, subscriptionRenewal.autoRenew=0, code=OfferA, generateQuoteEdrPerProduct=0, descriptionI18n={ENG=Offer A EN changed, FRA=Offer A FR}, uuid=252d46f8-4030-4542-9b87-d6ea0eccb511,
        // autoEndOfEngagement=0, name=Offer A changed, subscriptionRenewal.initialTermType=RECURRING, isOfferChangeRestricted=0, auditable.creator=opencell.admin,
        // auditable.created=2022-12-19T10:25:16.6, lifeCycleStatus=ACTIVE, disabled=0, longDescriptionI18n={ENG=Offer A long EN, FRA=Offer A long FR},
        // subscriptionRenewal.extendAgreementPeriodToSubscribedTillDate=0, auditable.updater=opencell.admin, imagePath=252d46f8-4030-4542-9b87-d6ea0eccb511.png, isModel=0,
        // subscriptionRenewal.renewalTermType=RECURRING, statusDate=2022-12-19T10:33:59.758, description=Offer A description changed, auditable.updated=2022-12-19T10:34:00.013,
        // longDescription=Long description, customerCategories_DELETE=[-2], sellers_DELETE=[-1, -3], offerServiceTemplates_56_DELETE={serviceTemplate=1, mandatory=0, offerTemplate=22,
        // id=56, incompatibleServices_DELETE=[2, 4]}, offerServiceTemplates_58_DELETE={serviceTemplate=3, mandatory=0, offerTemplate=22, id=58, incompatibleServices_DELETE=[4]}}

        assertThat(auditDataLogs.get(5).getAction()).isEqualTo(AuditCrudActionEnum.DELETE);
        assertThat(auditDataLogs.get(5).getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(auditDataLogs.get(5).getEntityId()).isEqualTo(22L);
        assertThat(auditDataLogs.get(5).getTxId()).isEqualTo(5909071L);
        assertThat(auditDataLogs.get(5).getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(auditDataLogs.get(5).getOriginName()).isEqualTo("/catalog/offerTemplate/{offerTemplateCode}");
        assertThat(auditDataLogs.get(5).getUserName()).isEqualTo("opencell.admin");

        assertThat(auditDataLogs.get(5).getValuesChanged().get("code")).isEqualTo("OfferA");

        assertThat(((List<Object>) auditDataLogs.get(5).getValuesChanged().get("sellers_DELETE")).size()).isEqualTo(2);
        assertThat(((List<Object>) auditDataLogs.get(5).getValuesChanged().get("sellers_DELETE")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) auditDataLogs.get(5).getValuesChanged().get("sellers_DELETE")).get(1)).isEqualTo(-3);

        assertThat(((List<Object>) auditDataLogs.get(5).getValuesChanged().get("customerCategories_DELETE")).size()).isEqualTo(1);
        assertThat(((List<Object>) auditDataLogs.get(5).getValuesChanged().get("customerCategories_DELETE")).get(0)).isEqualTo(-2);

        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_56_DELETE")).get("offerTemplate")).isEqualTo(auditDataLogs.get(0).getEntityId().intValue());
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_56_DELETE")).get("serviceTemplate")).isEqualTo(1);
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_56_DELETE")).get("id")).isEqualTo(56);
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_56_DELETE")).get("mandatory")).isEqualTo(0);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_56_DELETE")).get("incompatibleServices_DELETE")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_56_DELETE")).get("incompatibleServices_DELETE")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_56_DELETE")).get("incompatibleServices_DELETE")).get(1)).isEqualTo(4);

        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_58_DELETE")).get("offerTemplate")).isEqualTo(auditDataLogs.get(0).getEntityId().intValue());
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_58_DELETE")).get("serviceTemplate")).isEqualTo(3);
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_58_DELETE")).get("id")).isEqualTo(58);
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_58_DELETE")).get("mandatory")).isEqualTo(0);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_58_DELETE")).get("incompatibleServices_DELETE")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(5).getValuesChanged().get("offerServiceTemplates_58_DELETE")).get("incompatibleServices_DELETE")).get(0)).isEqualTo(4);

        // Old value:
        //
        // {id=22, subscriptionRenewal.autoRenew=0, code=OfferA, generateQuoteEdrPerProduct=0, descriptionI18n={ENG=Offer A EN changed, FRA=Offer A FR},
        // uuid=252d46f8-4030-4542-9b87-d6ea0eccb511, autoEndOfEngagement=0, name=Offer A changed, subscriptionRenewal.initialTermType=RECURRING, isOfferChangeRestricted=0,
        // auditable.creator=opencell.admin, auditable.created=2022-12-19T10:25:16.6, lifeCycleStatus=ACTIVE, disabled=0, longDescriptionI18n={ENG=Offer A long EN, FRA=Offer A long FR},
        // subscriptionRenewal.extendAgreementPeriodToSubscribedTillDate=0, auditable.updater=opencell.admin, imagePath=252d46f8-4030-4542-9b87-d6ea0eccb511.png, isModel=0,
        // subscriptionRenewal.renewalTermType=RECURRING, statusDate=2022-12-19T10:33:59.758, description=Offer A description changed, auditable.updated=2022-12-19T10:34:00.013,
        // longDescription=Long description, customerCategories=[-2], sellers=[-1, -3], offerServiceTemplates_56={incompatibleServices=[2, 4], serviceTemplate=1, mandatory=0,
        // offerTemplate=22, id=56}, offerServiceTemplates_58={incompatibleServices=[4], serviceTemplate=3, mandatory=0, offerTemplate=22, id=58}}

        assertThat(auditDataLogs.get(5).getValuesOld().get("code")).isEqualTo("OfferA");

        assertThat(((List<Object>) auditDataLogs.get(5).getValuesOld().get("sellers")).size()).isEqualTo(2);
        assertThat(((List<Object>) auditDataLogs.get(5).getValuesOld().get("sellers")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) auditDataLogs.get(5).getValuesOld().get("sellers")).get(1)).isEqualTo(-3);

        assertThat(((List<Object>) auditDataLogs.get(5).getValuesOld().get("customerCategories")).size()).isEqualTo(1);
        assertThat(((List<Object>) auditDataLogs.get(5).getValuesOld().get("customerCategories")).get(0)).isEqualTo(-2);

        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_56")).get("offerTemplate")).isEqualTo(auditDataLogs.get(0).getEntityId().intValue());
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_56")).get("serviceTemplate")).isEqualTo(1);
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_56")).get("id")).isEqualTo(56);
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_56")).get("mandatory")).isEqualTo(0);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_56")).get("incompatibleServices")).get(1)).isEqualTo(4);

        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_58")).get("offerTemplate")).isEqualTo(auditDataLogs.get(0).getEntityId().intValue());
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_58")).get("serviceTemplate")).isEqualTo(3);
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_58")).get("id")).isEqualTo(58);
        assertThat(((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_58")).get("mandatory")).isEqualTo(0);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_58")).get("incompatibleServices")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(5).getValuesOld().get("offerServiceTemplates_58")).get("incompatibleServices")).get(0)).isEqualTo(4);

        assertThat(auditDataLogRecords).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_Aggregation_MultiEntities() {

//        when(entityManager.createQuery(any(), any())).thenAnswer(new Answer<QuerySimulation>() {
//            public QuerySimulation answer(InvocationOnMock invocation) throws Throwable {
//                QuerySimulation query = new QuerySimulation();
//                return query;
//            }
//        });

        QuerySimulation deleteQuerySimulated = new QuerySimulation();

        when(entityManager.createNamedQuery(eq("AuditDataLogRecord.deleteAuditDataLogRecords"))).thenAnswer(new Answer<QuerySimulation>() {
            public QuerySimulation answer(InvocationOnMock invocation) throws Throwable {

                return deleteQuerySimulated;
            }
        });

        List<AuditDataLogRecord> auditDataLogRecords = new ArrayList<>();

        // Run this sql to get the java version of AuditDataLogRecords
        // select 'auditDataLogRecord = new AuditDataLogRecord('||id||'L, new Date(),"opencell.admin","'||ref_table||'", '||case when ref_id is null then 'null' else ref_id||'L' end ||', '||tx_id||'L, "'||action||'",
        // "'||origin||'", "'||origin_name||'", '||case when data_old is null then 'null' else '"'||replace(data_old::varchar,'"', '\"')||'"' end ||', '||case when data_new is null then 'null' else
        // '"'||replace(data_new::varchar,'"', '\"')||'"' end ||'); auditDataLogRecords.add(auditDataLogRecord);' from audit_data_log_rec order by id

        // Offer A creation
        AuditDataLogRecord auditDataLogRecord = new AuditDataLogRecord(623L, new Date(), "opencell.admin", "cat_offer_template", 22L, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 22, \"code\": \"OfferA\", \"name\": \"Offer A\", \"type\": \"OFFER\", \"uuid\": \"252d46f8-4030-4542-9b87-d6ea0eccb511\", \"created\": \"2022-12-19T10:25:16.6\", \"creator\": \"opencell.admin\", \"disabled\": 0, \"is_model\": 0, \"auto_renew\": 0, \"image_path\": \"252d46f8-4030-4542-9b87-d6ea0eccb511.png\", \"valid_from\": \"2022-02-02T00:00:00\", \"description\": \"Offer A description\", \"status_date\": \"2022-12-19T10:25:16.417\", \"description_i18n\": {\"ENG\": \"Offer A EN\", \"FRA\": \"Offer A FR\"}, \"long_description\": \"Long description\", \"initial_term_type\": \"RECURRING\", \"life_cycle_status\": \"ACTIVE\", \"renewal_term_type\": \"RECURRING\", \"match_end_aggr_date\": 0, \"long_description_i18n\": {\"ENG\": \"Offer A long EN\", \"FRA\": \"Offer A long FR\"}, \"auto_end_of_engagement\": 0, \"is_offer_change_restricted\": 0, \"generate_quote_edr_per_product\": 0}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(624L, new Date(), "opencell.admin", "cat_offer_serv_templates", 56L, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 56, \"mandatory\": 0, \"offer_template_id\": 22, \"service_template_id\": 1}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(625L, new Date(), "opencell.admin", "cat_offer_serv_templates", 57L, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 57, \"mandatory\": 1, \"offer_template_id\": 22, \"service_template_id\": 2}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(626L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 2, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(627L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 3, \"offer_service_template_id\": 56}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(628L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 1, \"offer_service_template_id\": 57}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(629L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 57}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(630L, new Date(), "opencell.admin", "cat_product_offer_customer_category", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"indx\": 0, \"product_id\": 22, \"customer_category_id\": -1}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(631L, new Date(), "opencell.admin", "cat_product_offer_customer_category", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"indx\": 1, \"product_id\": 22, \"customer_category_id\": -2}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(632L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -1, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(633L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -2, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(634L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -3, \"product_id\": 22}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Offer B creation
        auditDataLogRecord = new AuditDataLogRecord(635L, new Date(), "opencell.admin", "cat_offer_template", 23L, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 23, \"code\": \"OfferB\", \"name\": \"Offer B\", \"type\": \"OFFER\", \"uuid\": \"252d46f8-4030-4542-9b87-d6ea0eccb511\", \"created\": \"2022-12-19T10:25:16.6\", \"creator\": \"opencell.admin\", \"disabled\": 0, \"is_model\": 0, \"auto_renew\": 0, \"image_path\": \"252d46f8-4030-4542-9b87-d6ea0eccb511.png\", \"valid_from\": \"2022-02-02T00:00:00\", \"description\": \"Offer B description\", \"status_date\": \"2022-12-19T10:25:16.417\", \"description_i18n\": {\"ENG\": \"Offer B EN\", \"FRA\": \"Offer B FR\"}, \"long_description\": \"Long description\", \"initial_term_type\": \"RECURRING\", \"life_cycle_status\": \"ACTIVE\", \"renewal_term_type\": \"RECURRING\", \"match_end_aggr_date\": 0, \"long_description_i18n\": {\"ENG\": \"Offer B long EN\", \"FRA\": \"Offer B long FR\"}, \"auto_end_of_engagement\": 0, \"is_offer_change_restricted\": 0, \"generate_quote_edr_per_product\": 0}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(636L, new Date(), "opencell.admin", "cat_offer_serv_templates", 66L, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 66, \"mandatory\": 0, \"offer_template_id\": 23, \"service_template_id\": 1}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(637L, new Date(), "opencell.admin", "cat_offer_serv_templates", 67L, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"id\": 67, \"mandatory\": 1, \"offer_template_id\": 23, \"service_template_id\": 2}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(638L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 2, \"offer_service_template_id\": 66}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(639L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 3, \"offer_service_template_id\": 66}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(640L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 1, \"offer_service_template_id\": 67}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(641L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 67}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(642L, new Date(), "opencell.admin", "cat_product_offer_customer_category", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"indx\": 0, \"product_id\": 23, \"customer_category_id\": -1}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(643L, new Date(), "opencell.admin", "cat_product_offer_customer_category", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"indx\": 1, \"product_id\": 23, \"customer_category_id\": -2}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(644L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -1, \"product_id\": 23}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(645L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -2, \"product_id\": 23}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(646L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 5909046L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -3, \"product_id\": 23}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Seller creation
        auditDataLogRecord = new AuditDataLogRecord(647L, new Date(), "opencell.admin", "crm_seller", 5L, 111L, "INSERT", "GUI", "page/seller", null,
            "{\"email\": \"test@opencellsoft.com\", \"phone\": \"03546536168\", \"updated\": \"2022-10-19T14:17:39.745\", \"updater\": \"opencell.superadmin\", \"version\": 1, \"address_1\": \"El Aguacero S/N\", \"description\": \"France Seller test\", \"address_city\": \"Los Reartes\", \"address_zipcode\": \"5194\", \"address_country_id\": 10}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Seller update
        auditDataLogRecord = new AuditDataLogRecord(648L, new Date(), "opencell.admin", "crm_seller", 6L, 111L, "UPDATE", "JOB", "page/seller",
            "{\"email\": null, \"phone\": null, \"trading_currency_id\": 4, \"trading_country_id\": 8}",
            "{\"email\": \"test@opencellsoft.com\", \"phone\": \"1243324\", \"updated\": \"2022-10-19T14:17:39.745\", \"updater\": \"opencell.superadmin\", \"trading_currency_id\": 5, \"trading_country_id\": 7}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Customer creation - will be ignored, as audit data log configuration is for Offer and Seller only
        auditDataLogRecord = new AuditDataLogRecord(649L, new Date(), "opencell.admin", "crm_customer", 5L, 111L, "INSERT", "GUI", "page/customer", null,
            "{\"email\": \"test@opencellsoft.com\", \"phone\": \"03546536168\", \"updated\": \"2022-10-19T14:17:39.745\", \"updater\": \"opencell.superadmin\", \"version\": 1, \"address_1\": \"El Aguacero S/N\", \"description\": \"France Seller test\", \"address_city\": \"Los Reartes\", \"address_zipcode\": \"5194\", \"address_country_id\": 10}");
        auditDataLogRecords.add(auditDataLogRecord);

        auditLogService.aggregateAuditLogs(auditDataLogRecords);

        ArgumentCaptor<AuditDataLog> argument = ArgumentCaptor.forClass(AuditDataLog.class);
        verify(auditLogService, times(4)).create(argument.capture());

        AuditDataLog offerACreateAL = argument.getAllValues().get(0);
        AuditDataLog offerBCreateAL = argument.getAllValues().get(1);
        AuditDataLog sellerCreateAL = argument.getAllValues().get(2);
        AuditDataLog sellerUpdateAL = argument.getAllValues().get(3);

        // ------
        // Offer A creation

        // {isOfferChangeRestricted=0, subscriptionRenewal.initialTermType=RECURRING, auditable.created=2022-12-19T10:25:16.6, auditable.creator=opencell.admin, lifeCycleStatus=ACTIVE,
        // longDescriptionI18n={ENG=Offer A long EN, FRA=Offer A long FR}, disabled=0, subscriptionRenewal.extendAgreementPeriodToSubscribedTillDate=0, id=22, subscriptionRenewal.autoRenew=0,
        // generateQuoteEdrPerProduct=0, code=OfferA, isModel=0, imagePath=252d46f8-4030-4542-9b87-d6ea0eccb511.png, subscriptionRenewal.renewalTermType=RECURRING,
        // descriptionI18n={ENG=Offer A EN, FRA=Offer A FR}, uuid=252d46f8-4030-4542-9b87-d6ea0eccb511, statusDate=2022-12-19T10:25:16.417, description=Offer A description,
        // autoEndOfEngagement=0, name=Offer A, longDescription=Long description, offerServiceTemplates_56_INSERT={serviceTemplate=1, mandatory=0, incompatibleServices_INSERT=[2, 3],
        // offerTemplate=22, id=56}, offerServiceTemplates_57_INSERT={serviceTemplate=2, mandatory=1, incompatibleServices_INSERT=[1, 4], offerTemplate=22, id=57},
        // customerCategories_INSERT=[-1, -2], sellers_INSERT=[-1, -2, -3]}

        assertThat(offerACreateAL.getAction()).isEqualTo(AuditCrudActionEnum.INSERT);
        assertThat(offerACreateAL.getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(offerACreateAL.getEntityId()).isEqualTo(22L);
        assertThat(offerACreateAL.getTxId()).isEqualTo(5909046L);
        assertThat(offerACreateAL.getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(offerACreateAL.getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(offerACreateAL.getUserName()).isEqualTo("opencell.admin");

        assertThat(offerACreateAL.getValuesChanged().get("code")).isEqualTo("OfferA");
        assertThat(offerACreateAL.getValuesChanged().get("name")).isEqualTo("Offer A");
        assertThat(offerACreateAL.getValuesChanged().get("description")).isEqualTo("Offer A description");

        assertThat(((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("offerTemplate")).isEqualTo(offerACreateAL.getEntityId().intValue());
        assertThat(((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("serviceTemplate")).isEqualTo(1);
        assertThat(((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("id")).isEqualTo(56);
        assertThat(((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("mandatory")).isEqualTo(0);
//      assertThat(((Map<String, Object>) auditDataLog1.getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("validity.from")).isEqualTo("2022-11-01T00:00:00");
//      assertThat(((Map<String, Object>) auditDataLog1.getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("validity.to")).isEqualTo("2022-11-24T00:00:00"); 
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("incompatibleServices_INSERT")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("incompatibleServices_INSERT")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_56_INSERT")).get("incompatibleServices_INSERT")).get(1)).isEqualTo(3);

        assertThat(((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("offerTemplate")).isEqualTo(offerACreateAL.getEntityId().intValue());
        assertThat(((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("serviceTemplate")).isEqualTo(2);
        assertThat(((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("id")).isEqualTo(57);
        assertThat(((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("mandatory")).isEqualTo(1);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("incompatibleServices_INSERT")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("incompatibleServices_INSERT")).get(0)).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) offerACreateAL.getValuesChanged().get("offerServiceTemplates_57_INSERT")).get("incompatibleServices_INSERT")).get(1)).isEqualTo(4);

        assertThat(((List<Object>) offerACreateAL.getValuesChanged().get("customerCategories_INSERT")).size()).isEqualTo(2);
        assertThat(((List<Object>) offerACreateAL.getValuesChanged().get("customerCategories_INSERT")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) offerACreateAL.getValuesChanged().get("customerCategories_INSERT")).get(1)).isEqualTo(-2);

        assertThat(((List<Object>) offerACreateAL.getValuesChanged().get("sellers_INSERT")).size()).isEqualTo(3);
        assertThat(((List<Object>) offerACreateAL.getValuesChanged().get("sellers_INSERT")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) offerACreateAL.getValuesChanged().get("sellers_INSERT")).get(1)).isEqualTo(-2);
        assertThat(((List<Object>) offerACreateAL.getValuesChanged().get("sellers_INSERT")).get(2)).isEqualTo(-3);

        // ------
        // Offer B creation

        // {isOfferChangeRestricted=0, subscriptionRenewal.initialTermType=RECURRING, auditable.created=2022-12-19T10:25:16.6, auditable.creator=opencell.admin, lifeCycleStatus=ACTIVE,
        // longDescriptionI18n={ENG=Offer B long EN, FRA=Offer B long FR}, disabled=0, subscriptionRenewal.extendAgreementPeriodToSubscribedTillDate=0, id=23, subscriptionRenewal.autoRenew=0,
        // generateQuoteEdrPerProduct=0, code=OfferB, isModel=0, imagePath=252d46f8-4030-4542-9b87-d6ea0eccb511.png, subscriptionRenewal.renewalTermType=RECURRING,
        // descriptionI18n={ENG=Offer B EN, FRA=Offer B FR}, uuid=252d46f8-4030-4542-9b87-d6ea0eccb511, statusDate=2022-12-19T10:25:16.417, description=Offer B description,
        // autoEndOfEngagement=0, name=Offer B, longDescription=Long description, offerServiceTemplates_66_INSERT={serviceTemplate=1, mandatory=0, incompatibleServices_INSERT=[2, 3],
        // offerTemplate=23, id=66}, offerServiceTemplates_67_INSERT={serviceTemplate=2, mandatory=1, incompatibleServices_INSERT=[1, 4], offerTemplate=23, id=67},
        // customerCategories_INSERT=[-1, -2], sellers_INSERT=[-1, -2, -3]}

        assertThat(offerBCreateAL.getAction()).isEqualTo(AuditCrudActionEnum.INSERT);
        assertThat(offerBCreateAL.getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(offerBCreateAL.getEntityId()).isEqualTo(23L);
        assertThat(offerBCreateAL.getTxId()).isEqualTo(5909046L);
        assertThat(offerBCreateAL.getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(offerBCreateAL.getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(offerBCreateAL.getUserName()).isEqualTo("opencell.admin");

        assertThat(offerBCreateAL.getValuesChanged().get("code")).isEqualTo("OfferB");
        assertThat(offerBCreateAL.getValuesChanged().get("name")).isEqualTo("Offer B");
        assertThat(offerBCreateAL.getValuesChanged().get("description")).isEqualTo("Offer B description");

        assertThat(((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_66_INSERT")).get("offerTemplate")).isEqualTo(offerBCreateAL.getEntityId().intValue());
        assertThat(((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_66_INSERT")).get("serviceTemplate")).isEqualTo(1);
        assertThat(((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_66_INSERT")).get("id")).isEqualTo(66);
        assertThat(((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_66_INSERT")).get("mandatory")).isEqualTo(0);
//      assertThat(((Map<String, Object>) auditDataLog2.getValuesChanged().get("offerServiceTemplates_66_INSERT")).get("validity.from")).isEqualTo("2022-11-01T00:00:00");
//      assertThat(((Map<String, Object>) auditDataLog2.getValuesChanged().get("offerServiceTemplates_66_INSERT")).get("validity.to")).isEqualTo("2022-11-24T00:00:00"); 
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_66_INSERT")).get("incompatibleServices_INSERT")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_66_INSERT")).get("incompatibleServices_INSERT")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_66_INSERT")).get("incompatibleServices_INSERT")).get(1)).isEqualTo(3);

        assertThat(((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_67_INSERT")).get("offerTemplate")).isEqualTo(offerBCreateAL.getEntityId().intValue());
        assertThat(((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_67_INSERT")).get("serviceTemplate")).isEqualTo(2);
        assertThat(((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_67_INSERT")).get("id")).isEqualTo(67);
        assertThat(((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_67_INSERT")).get("mandatory")).isEqualTo(1);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_67_INSERT")).get("incompatibleServices_INSERT")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_67_INSERT")).get("incompatibleServices_INSERT")).get(0)).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) offerBCreateAL.getValuesChanged().get("offerServiceTemplates_67_INSERT")).get("incompatibleServices_INSERT")).get(1)).isEqualTo(4);

        assertThat(((List<Object>) offerBCreateAL.getValuesChanged().get("customerCategories_INSERT")).size()).isEqualTo(2);
        assertThat(((List<Object>) offerBCreateAL.getValuesChanged().get("customerCategories_INSERT")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) offerBCreateAL.getValuesChanged().get("customerCategories_INSERT")).get(1)).isEqualTo(-2);

        assertThat(((List<Object>) offerBCreateAL.getValuesChanged().get("sellers_INSERT")).size()).isEqualTo(3);
        assertThat(((List<Object>) offerBCreateAL.getValuesChanged().get("sellers_INSERT")).get(0)).isEqualTo(-1);
        assertThat(((List<Object>) offerBCreateAL.getValuesChanged().get("sellers_INSERT")).get(1)).isEqualTo(-2);
        assertThat(((List<Object>) offerBCreateAL.getValuesChanged().get("sellers_INSERT")).get(2)).isEqualTo(-3);

        assertThat(sellerCreateAL.getAction()).isEqualTo(AuditCrudActionEnum.INSERT);
        assertThat(sellerCreateAL.getEntityClass()).isEqualTo(Seller.class.getName());
        assertThat(sellerCreateAL.getEntityId()).isEqualTo(5L);
        assertThat(sellerCreateAL.getTxId()).isEqualTo(111L);
        assertThat(sellerCreateAL.getOrigin()).isEqualTo(ChangeOriginEnum.GUI);
        assertThat(sellerCreateAL.getOriginName()).isEqualTo("page/seller");
        assertThat(sellerCreateAL.getUserName()).isEqualTo("opencell.admin");
        assertThat(sellerCreateAL.getValuesOld()).isNull();
        assertThat(sellerCreateAL.getValuesChanged().get("contactInformation.email")).isEqualTo("test@opencellsoft.com");
        assertThat(sellerCreateAL.getValuesChanged().get("contactInformation.phone")).isEqualTo("03546536168");
        assertThat(sellerCreateAL.getValuesChanged().get("address.city")).isEqualTo("Los Reartes");

        assertThat(sellerUpdateAL.getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(sellerUpdateAL.getEntityClass()).isEqualTo(Seller.class.getName());
        assertThat(sellerUpdateAL.getEntityId()).isEqualTo(6L);
        assertThat(sellerUpdateAL.getTxId()).isEqualTo(111L);
        assertThat(sellerUpdateAL.getOrigin()).isEqualTo(ChangeOriginEnum.JOB);
        assertThat(sellerUpdateAL.getOriginName()).isEqualTo("page/seller");
        assertThat(sellerUpdateAL.getUserName()).isEqualTo("opencell.admin");
        assertThat(sellerUpdateAL.getValuesChanged().get("contactInformation.email")).isEqualTo("test@opencellsoft.com");
        assertThat(sellerUpdateAL.getValuesChanged().get("contactInformation.phone")).isEqualTo("1243324");
        assertThat(sellerUpdateAL.getValuesChanged().get("tradingCurrency")).isEqualTo(5);

        assertThat(deleteQuerySimulated.getIdsParam().size()).isEqualTo(26);

        assertThat(auditDataLogRecords.size()).isEqualTo(1);
        assertThat(auditDataLogRecords.get(0).getId()).isEqualTo(649L);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_Aggregation_MissingParent() {

        when(entityManager.createQuery(any(), any())).thenAnswer(new Answer<QuerySimulation>() {
            public QuerySimulation answer(InvocationOnMock invocation) throws Throwable {
                QuerySimulation query = new QuerySimulation();
                return query;
            }
        });

        AuditDataHierarchy dataHierarchy = AuditDataConfigurationService.getAuditDataHierarchy(OfferTemplate.class);

        List<AuditDataLogRecord> auditDataLogRecords = new ArrayList<>();

        // Update Offer A - change incompatible services in Service A
        AuditDataLogRecord auditDataLogRecord = new AuditDataLogRecord(751L, new Date(), "opencell.admin", "cat_offer_template", 24L, 600L, "UPDATE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"updated\": \"2022-12-19T10:28:44.454\", \"status_date\": \"2022-12-19T10:28:44.295\"}", "{\"updated\": \"2022-12-19T10:30:20.01\", \"status_date\": \"2022-12-19T10:30:19.818\"}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(752L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 600L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 2, \"offer_service_template_id\": 76}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(753L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 600L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 3, \"offer_service_template_id\": 76}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(754L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 600L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 1, \"offer_service_template_id\": 77}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(755L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 600L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"service_template_id\": 4, \"offer_service_template_id\": 77}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(756L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 600L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -1, \"product_id\": 24}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(757L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 600L, "DELETE", "API", "/catalog/offerTemplate/createOrUpdate",
            "{\"seller_id\": -3, \"product_id\": 24}", null);
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(758L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 600L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -1, \"product_id\": 24}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(759L, new Date(), "opencell.admin", "cat_product_offer_seller", null, 600L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"seller_id\": -3, \"product_id\": 24}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(760L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 600L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 2, \"offer_service_template_id\": 76}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(761L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 600L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 76}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(762L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 600L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 1, \"offer_service_template_id\": 77}");
        auditDataLogRecords.add(auditDataLogRecord);
        auditDataLogRecord = new AuditDataLogRecord(763L, new Date(), "opencell.admin", "cat_offer_serv_incomp", null, 600L, "INSERT", "API", "/catalog/offerTemplate/createOrUpdate", null,
            "{\"service_template_id\": 4, \"offer_service_template_id\": 77}");
        auditDataLogRecords.add(auditDataLogRecord);

        List<AuditDataLog> auditDataLogs = auditLogService.aggregateAuditLogs(dataHierarchy, auditDataLogRecords);

        assertThat(auditDataLogs.size()).isEqualTo(3);

        // ------
        // Update Offer A - change incompatible services in Service A

        // Changed value:
        //
        // {statusDate=2022-12-19T10:30:19.818, auditable.updated=2022-12-19T10:30:20.01}

        assertThat(auditDataLogs.get(0).getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(auditDataLogs.get(0).getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(auditDataLogs.get(0).getEntityId()).isEqualTo(24L);
        assertThat(auditDataLogs.get(0).getTxId()).isEqualTo(600L);
        assertThat(auditDataLogs.get(0).getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(auditDataLogs.get(0).getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(auditDataLogs.get(0).getUserName()).isEqualTo("opencell.admin");

        assertThat(auditDataLogs.get(0).getValuesChanged().get("code")).isNull();
        assertThat(auditDataLogs.get(0).getValuesChanged().get("auditable.updated")).isNotNull();
        assertThat(auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_76")).isNull();
        assertThat(auditDataLogs.get(0).getValuesChanged().get("offerServiceTemplates_77")).isNull();
        assertThat(auditDataLogs.get(0).getValuesChanged().get("sellers")).isNull();

        // Old value:
        //
        // {statusDate=2022-12-19T10:28:44.295, auditable.updated=2022-12-19T10:28:44.454, sellers=[-1, -3]}

        assertThat(auditDataLogs.get(0).getValuesOld().get("statusDate")).isEqualTo("2022-12-19T10:28:44.295");
        assertThat((List<Integer>) auditDataLogs.get(0).getValuesOld().get("sellers")).containsAll(Arrays.asList(-1, -3));

        assertThat(auditDataLogs.get(0).getAuditDataLogRecords()).containsAll(Arrays.asList(751L, 756L, 757L, 758L, 759L));

        // ----
        // Changed value:
        //
        // {offerServiceTemplates_76={incompatibleServices_DELETE=[3], incompatibleServices_INSERT=[4]}}

        assertThat(auditDataLogs.get(1).getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(auditDataLogs.get(1).getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(auditDataLogs.get(1).getEntityId()).isEqualTo(0L);
        assertThat(auditDataLogs.get(1).getTxId()).isEqualTo(600L);
        assertThat(auditDataLogs.get(1).getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(auditDataLogs.get(1).getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(auditDataLogs.get(1).getUserName()).isEqualTo("opencell.admin");

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(1).getValuesChanged().get("offerServiceTemplates_76")).get("incompatibleServices_DELETE")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(1).getValuesChanged().get("offerServiceTemplates_76")).get("incompatibleServices_DELETE")).get(0)).isEqualTo(3);
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(1).getValuesChanged().get("offerServiceTemplates_76")).get("incompatibleServices_INSERT")).size()).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(1).getValuesChanged().get("offerServiceTemplates_76")).get("incompatibleServices_INSERT")).get(0)).isEqualTo(4);

        // Old value:
        //
        // {offerServiceTemplates_76={incompatibleServices=[2, 3]}}
        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("offerServiceTemplates_76")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("offerServiceTemplates_76")).get("incompatibleServices")).get(0)).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(1).getValuesOld().get("offerServiceTemplates_76")).get("incompatibleServices")).get(1)).isEqualTo(3);

        assertThat(auditDataLogs.get(1).getAuditDataLogRecords()).containsAll(Arrays.asList(752L, 753L, 760L, 761L));

        // ----
        // No change values for cat_offer_serv_incomp id 77

        assertThat(auditDataLogs.get(2).getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(auditDataLogs.get(2).getEntityClass()).isEqualTo(OfferTemplate.class.getName());
        assertThat(auditDataLogs.get(2).getEntityId()).isEqualTo(0L);
        assertThat(auditDataLogs.get(2).getTxId()).isEqualTo(600L);
        assertThat(auditDataLogs.get(2).getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(auditDataLogs.get(2).getOriginName()).isEqualTo("/catalog/offerTemplate/createOrUpdate");
        assertThat(auditDataLogs.get(2).getUserName()).isEqualTo("opencell.admin");

        assertThat(auditDataLogs.get(2).getValuesChanged()).isEmpty();

        // Old value:
        //
        // {offerServiceTemplates_77={incompatibleServices=[1, 4]}}

        assertThat(((List<Map<String, Object>>) ((Map<String, Object>) auditDataLogs.get(2).getValuesOld().get("offerServiceTemplates_77")).get("incompatibleServices")).size()).isEqualTo(2);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(2).getValuesOld().get("offerServiceTemplates_77")).get("incompatibleServices")).get(0)).isEqualTo(1);
        assertThat(((List<Object>) ((Map<String, Object>) auditDataLogs.get(2).getValuesOld().get("offerServiceTemplates_77")).get("incompatibleServices")).get(1)).isEqualTo(4);

        assertThat(auditDataLogs.get(2).getAuditDataLogRecords()).containsAll(Arrays.asList(754L, 755L, 762L, 763L));

        assertThat(auditDataLogRecords).isEmpty();

    }

    @Test
    public void test_Aggregation_AuditField() {

//        when(entityManager.createQuery(any(), any())).thenAnswer(new Answer<QuerySimulation>() {
//            public QuerySimulation answer(InvocationOnMock invocation) throws Throwable {
//                QuerySimulation query = new QuerySimulation();
//                return query;
//            }
//        });

        QuerySimulation deleteQuerySimulated = new QuerySimulation();

        when(entityManager.createNamedQuery(eq("AuditDataLogRecord.deleteAuditDataLogRecords"))).thenAnswer(new Answer<QuerySimulation>() {
            public QuerySimulation answer(InvocationOnMock invocation) throws Throwable {

                return deleteQuerySimulated;
            }
        });

        List<AuditDataLogRecord> auditDataLogRecords = new ArrayList<>();

        // Run this sql to get the java version of AuditDataLogRecords
        // select 'auditDataLogRecord = new AuditDataLogRecord('||id||'L, new Date(),"opencell.admin","'||ref_table||'", '||case when ref_id is null then 'null' else ref_id||'L' end ||', '||tx_id||'L, "'||action||'",
        // "'||origin||'", "'||origin_name||'", '||case when data_old is null then 'null' else '"'||replace(data_old::varchar,'"', '\"')||'"' end ||', '||case when data_new is null then 'null' else
        // '"'||replace(data_new::varchar,'"', '\"')||'"' end ||'); auditDataLogRecords.add(auditDataLogRecord);' from audit_data_log_rec order by id

        // Subscription A creation
        AuditDataLogRecord auditDataLogRecord = new AuditDataLogRecord(623L, new Date(), "opencell.admin", "billing_subscription", 22L, 5909046L, "INSERT", "API", "/billing/subscription/createOrUpdate", null,
            "{\"id\": 22, \"code\": \"SubA\", \"status\": \"CREATED\", \"user_account_id\":\"5\", \"uuid\": \"252d46f8-4030-4542-9b87-d6ea0eccb511\", \"created\": \"2022-12-19T10:25:16.6\", \"creator\": \"opencell.admin\", \"disabled\": 0, \"auto_renew\": 0, \"status_date\": \"2022-12-19T10:25:16.417\", \"initial_term_type\": \"RECURRING\", \"renewal_term_type\": \"RECURRING\", \"match_end_aggr_date\": 0, \"auto_end_of_engagement\": 0}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Update Subscription A - basic Subscription data

        auditDataLogRecord = new AuditDataLogRecord(624L, new Date(), "opencell.admin", "billing_subscription", 22L, 5909052L, "UPDATE", "API", "/billing/subscription/createOrUpdate",
            "{\"status\": \"CREATED\", \"auto_renew\": 0, \"renewal_term_type\": \"RECURRING\", \"user_account_id\":\"5\", \"updated\": null, \"updater\": null, \"status_date\": \"2022-12-19T10:25:16.417\"}",
            "{ \"status\": \"ACTIVE\", \"auto_renew\": 1, \"renewal_term_type\": \"CALENDAR\", \"user_account_id\":\"6\", \"updated\": \"2022-12-19T10:28:44.454\", \"updater\": \"opencell.admin\",  \"status_date\": \"2022-12-19T10:28:44.295\"}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Update Subscription A - renewal

        auditDataLogRecord = new AuditDataLogRecord(625L, new Date(), "opencell.admin", "billing_subscription", 22L, 5909053L, "UPDATE", "API", "/billing/subscription/createOrUpdate",
            "{\"updater\": null, \"renewed\": 0}", "{ \"updater\": \"admin\", \"renewed\": 1}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Subscription B creation
        auditDataLogRecord = new AuditDataLogRecord(635L, new Date(), "opencell.admin", "billing_subscription", 23L, 5909046L, "INSERT", "API", "/billing/subscription/createOrUpdate", null,
            "{\"id\": 23, \"code\": \"SubB\", \"status\": \"CREATED\", \"uuid\": \"252d46f8-4030-4542-9b87-d6ea0eccb511\", \"created\": \"2022-12-19T10:25:16.6\", \"creator\": \"opencell.admin\", \"disabled\": 0, \"auto_renew\": 0, \"status_date\": \"2022-12-19T10:25:16.417\", \"initial_term_type\": \"RECURRING\", \"renewal_term_type\": \"RECURRING\", \"match_end_aggr_date\": 0, \"auto_end_of_engagement\": 0}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Seller creation
        auditDataLogRecord = new AuditDataLogRecord(647L, new Date(), "opencell.admin", "crm_seller", 5L, 111L, "INSERT", "GUI", "page/seller", null,
            "{\"email\": \"test@opencellsoft.com\", \"phone\": \"03546536168\", \"updated\": \"2022-10-19T14:17:39.745\", \"updater\": \"opencell.superadmin\", \"version\": 1, \"address_1\": \"El Aguacero S/N\", \"description\": \"France Seller test\", \"address_city\": \"Los Reartes\", \"address_zipcode\": \"5194\", \"address_country_id\": 10}");
        auditDataLogRecords.add(auditDataLogRecord);

        // Delete Subscription A
        auditDataLogRecord = new AuditDataLogRecord(698L, new Date(), "opencell.admin", "billing_subscription", 22L, 5909071L, "DELETE", "API", "/billing/subscription/{subscriptionCode}",
            "{\"id\": 22, \"code\": \"SubA\", \"status\": \"ACTIVE\", \"user_account_id\":\"6\", \"uuid\": \"252d46f8-4030-4542-9b87-d6ea0eccb511\", \"created\": \"2022-12-19T10:25:16.6\", \"creator\": \"opencell.admin\", \"updated\": \"2022-12-19T10:34:00.013\", \"updater\": \"opencell.admin\", \"auto_renew\": 1, \"status_date\": \"2022-12-19T10:33:59.758\", \"initial_term_type\": \"RECURRING\", \"renewal_term_type\": \"RECURRING\", \"match_end_aggr_date\": 0, \"auto_end_of_engagement\": 0}",
            null);
        auditDataLogRecords.add(auditDataLogRecord);

        auditLogService.aggregateAuditLogs(auditDataLogRecords);

        ArgumentCaptor<AuditDataLog> auditDataArgument = ArgumentCaptor.forClass(AuditDataLog.class);
        verify(auditLogService, times(6)).create(auditDataArgument.capture());

        ArgumentCaptor<AuditableField> auditFieldArgument = ArgumentCaptor.forClass(AuditableField.class);
        verify(auditableFieldService, times(9)).create(auditFieldArgument.capture());

        ArgumentCaptor<AuditableField> notifyArgument = ArgumentCaptor.forClass(AuditableField.class);
        verify(auditFieldEventProducer, times(7)).fire(notifyArgument.capture());

        AuditDataLog subACreateAL = auditDataArgument.getAllValues().get(0);
        AuditDataLog subBCreateAL = auditDataArgument.getAllValues().get(1);
        AuditDataLog subAUpdate1AL = auditDataArgument.getAllValues().get(2);
        AuditDataLog subAUpdate2AL = auditDataArgument.getAllValues().get(3);
        AuditDataLog subADeleteAL = auditDataArgument.getAllValues().get(4);
        AuditDataLog sellerCreateAL = auditDataArgument.getAllValues().get(5);

        // [AuditableField [entityClass=org.meveo.model.billing.Subscription, entityId=22, name=status, previousState=null, currentState=CREATED],
        // AuditableField [entityClass=org.meveo.model.billing.Subscription, entityId=22, name=userAccount, previousState=null, currentState=5],
        // AuditableField [entityClass=org.meveo.model.billing.Subscription, entityId=22, name=subscriptionRenewal, previousState=null,
        // currentState={"subscriptionRenewal.initialTermType":"RECURRING","subscriptionRenewal.extendAgreementPeriodToSubscribedTillDate":"0","subscriptionRenewal.autoRenew":"0","subscriptionRenewal.renewalTermType":"RECURRING"}],
        // AuditableField [entityClass=org.meveo.model.billing.Subscription, entityId=23, name=status, previousState=null, currentState=CREATED],
        // AuditableField [entityClass=org.meveo.model.billing.Subscription, entityId=23, name=subscriptionRenewal, previousState=null,
        // currentState={"subscriptionRenewal.initialTermType":"RECURRING","subscriptionRenewal.extendAgreementPeriodToSubscribedTillDate":"0","subscriptionRenewal.autoRenew":"0","subscriptionRenewal.renewalTermType":"RECURRING"}],
        // AuditableField [entityClass=org.meveo.model.billing.Subscription, entityId=22, name=status, previousState=CREATED, currentState=ACTIVE],
        // AuditableField [entityClass=org.meveo.model.billing.Subscription, entityId=22, name=userAccount, previousState=5, currentState=6],
        // AuditableField [entityClass=org.meveo.model.billing.Subscription, entityId=22, name=subscriptionRenewal, previousState={"subscriptionRenewal.renewalTermType":"RECURRING","subscriptionRenewal.autoRenew":"0"},
        // currentState={"subscriptionRenewal.renewalTermType":"CALENDAR","subscriptionRenewal.autoRenew":"1"}],
        // AuditableField [entityClass=org.meveo.model.billing.Subscription, entityId=22, name=renewed, previousState=0, currentState=1]]

        AuditableField subACreateStatusAF = auditFieldArgument.getAllValues().get(0);
        AuditableField subACreateUAAF = auditFieldArgument.getAllValues().get(1);
        AuditableField subACreateRenewalAF = auditFieldArgument.getAllValues().get(2);
        AuditableField subBCreateStatusAF = auditFieldArgument.getAllValues().get(3);
        AuditableField subBCreateRenewalAF = auditFieldArgument.getAllValues().get(4);
        AuditableField subAUpdateStatusAF = auditFieldArgument.getAllValues().get(5);
        AuditableField subAUpdateUAAF = auditFieldArgument.getAllValues().get(6);
        AuditableField subAUpdateRenewalAF = auditFieldArgument.getAllValues().get(7);
        AuditableField subAUpdateRenewedAF = auditFieldArgument.getAllValues().get(8);

        subACreateStatusAF.setId(0L);
        subACreateUAAF.setId(1L);
        subACreateRenewalAF.setId(2L);
        subBCreateStatusAF.setId(3L);
        subBCreateRenewalAF.setId(4L);
        subAUpdateStatusAF.setId(5L);
        subAUpdateUAAF.setId(6L);
        subAUpdateRenewalAF.setId(7L);
        subAUpdateRenewedAF.setId(8L);

        AuditableField subACreateStatusAFN = notifyArgument.getAllValues().get(0);
        AuditableField subACreateRenewalAFN = notifyArgument.getAllValues().get(1);
        AuditableField subBCreateStatusAFN = notifyArgument.getAllValues().get(2);
        AuditableField subBCreateRenewalAFN = notifyArgument.getAllValues().get(3);
        AuditableField subAUpdateStatusAFN = notifyArgument.getAllValues().get(4);
        AuditableField subAUpdateRenewalAFN = notifyArgument.getAllValues().get(5);
        AuditableField subAUpdateRenewedAFN = notifyArgument.getAllValues().get(6);

        // ------
        // Offer A creation

        // Audit data log
        assertThat(subACreateAL.getAction()).isEqualTo(AuditCrudActionEnum.INSERT);
        assertThat(subACreateAL.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subACreateAL.getEntityId()).isEqualTo(22L);
        assertThat(subACreateAL.getTxId()).isEqualTo(5909046L);
        assertThat(subACreateAL.getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subACreateAL.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subACreateAL.getUserName()).isEqualTo("opencell.admin");

        assertThat(subACreateAL.getValuesChanged().get("code")).isEqualTo("SubA");
        assertThat(subACreateAL.getValuesChanged().get("status")).isEqualTo("CREATED");

        // Audit field log
        assertThat(subACreateStatusAF.getChangeType()).isEqualTo(AuditChangeTypeEnum.STATUS);
        assertThat(subACreateStatusAF.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subACreateStatusAF.getEntityId()).isEqualTo(22L);
        assertThat(subACreateStatusAF.getChangeOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subACreateStatusAF.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subACreateStatusAF.getActor()).isEqualTo("opencell.admin");
        assertThat(subACreateStatusAF.getName()).isEqualTo("status");
        assertThat(subACreateStatusAF.getPreviousState()).isNull();
        assertThat(subACreateStatusAF.getCurrentState()).isEqualTo("CREATED");

        assertThat(subACreateUAAF.getChangeType()).isEqualTo(AuditChangeTypeEnum.OTHER);
        assertThat(subACreateUAAF.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subACreateUAAF.getEntityId()).isEqualTo(22L);
        assertThat(subACreateUAAF.getChangeOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subACreateUAAF.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subACreateUAAF.getActor()).isEqualTo("opencell.admin");
        assertThat(subACreateUAAF.getName()).isEqualTo("userAccount");
        assertThat(subACreateUAAF.getPreviousState()).isNull();
        assertThat(subACreateUAAF.getCurrentState()).isEqualTo("5");

        assertThat(subACreateRenewalAF.getChangeType()).isEqualTo(AuditChangeTypeEnum.RENEWAL);
        assertThat(subACreateRenewalAF.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subACreateRenewalAF.getEntityId()).isEqualTo(22L);
        assertThat(subACreateRenewalAF.getChangeOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subACreateRenewalAF.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subACreateRenewalAF.getActor()).isEqualTo("opencell.admin");
        assertThat(subACreateRenewalAF.getName()).isEqualTo("subscriptionRenewal");
        assertThat(subACreateRenewalAF.getPreviousState()).isNull();
        assertThat(subACreateRenewalAF.getCurrentState()).isEqualTo(
            "{\"subscriptionRenewal.initialTermType\":\"RECURRING\",\"subscriptionRenewal.extendAgreementPeriodToSubscribedTillDate\":\"0\",\"subscriptionRenewal.autoRenew\":\"0\",\"subscriptionRenewal.renewalTermType\":\"RECURRING\"}");

        // Verify audit field notification
        assertThat(subACreateStatusAFN.getId()).isEqualTo(subACreateStatusAF.getId());
        assertThat(subACreateRenewalAFN.getId()).isEqualTo(subACreateRenewalAF.getId());

        // ------
        // Offer B creation

        // Audit data log
        assertThat(subBCreateAL.getAction()).isEqualTo(AuditCrudActionEnum.INSERT);
        assertThat(subBCreateAL.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subBCreateAL.getEntityId()).isEqualTo(23L);
        assertThat(subBCreateAL.getTxId()).isEqualTo(5909046L);
        assertThat(subBCreateAL.getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subBCreateAL.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subBCreateAL.getUserName()).isEqualTo("opencell.admin");

        assertThat(subBCreateAL.getValuesChanged().get("code")).isEqualTo("SubB");
        assertThat(subBCreateAL.getValuesChanged().get("status")).isEqualTo("CREATED");

        // Audit field log
        assertThat(subBCreateStatusAF.getChangeType()).isEqualTo(AuditChangeTypeEnum.STATUS);
        assertThat(subBCreateStatusAF.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subBCreateStatusAF.getEntityId()).isEqualTo(23L);
        assertThat(subBCreateStatusAF.getChangeOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subBCreateStatusAF.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subBCreateStatusAF.getActor()).isEqualTo("opencell.admin");
        assertThat(subBCreateStatusAF.getName()).isEqualTo("status");
        assertThat(subBCreateStatusAF.getPreviousState()).isNull();
        assertThat(subBCreateStatusAF.getCurrentState()).isEqualTo("CREATED");

        assertThat(subBCreateRenewalAF.getChangeType()).isEqualTo(AuditChangeTypeEnum.RENEWAL);
        assertThat(subBCreateRenewalAF.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subBCreateRenewalAF.getEntityId()).isEqualTo(23L);
        assertThat(subBCreateRenewalAF.getChangeOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subBCreateRenewalAF.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subBCreateRenewalAF.getActor()).isEqualTo("opencell.admin");
        assertThat(subBCreateRenewalAF.getName()).isEqualTo("subscriptionRenewal");
        assertThat(subBCreateRenewalAF.getPreviousState()).isNull();
        assertThat(subBCreateRenewalAF.getCurrentState()).isEqualTo(
            "{\"subscriptionRenewal.initialTermType\":\"RECURRING\",\"subscriptionRenewal.extendAgreementPeriodToSubscribedTillDate\":\"0\",\"subscriptionRenewal.autoRenew\":\"0\",\"subscriptionRenewal.renewalTermType\":\"RECURRING\"}");

        // Verify audit field notification
        assertThat(subBCreateStatusAFN.getId()).isEqualTo(subBCreateStatusAF.getId());
        assertThat(subBCreateRenewalAFN.getId()).isEqualTo(subBCreateRenewalAF.getId());

        // ------
        // Update Offer A - basic offer data

        // Audit data log
        assertThat(subAUpdate1AL.getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(subAUpdate1AL.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subAUpdate1AL.getEntityId()).isEqualTo(22L);
        assertThat(subAUpdate1AL.getTxId()).isEqualTo(5909052L);
        assertThat(subAUpdate1AL.getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subAUpdate1AL.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subAUpdate1AL.getUserName()).isEqualTo("opencell.admin");

        assertThat(subAUpdate1AL.getValuesChanged().get("status")).isEqualTo("ACTIVE");
        assertThat(subAUpdate1AL.getValuesChanged().get("userAccount")).isEqualTo("6");
        assertThat(subAUpdate1AL.getValuesChanged().get("subscriptionRenewal.autoRenew")).isEqualTo(1);
        assertThat(subAUpdate1AL.getValuesChanged().get("subscriptionRenewal.renewalTermType")).isEqualTo("CALENDAR");

        assertThat(subAUpdate1AL.getValuesOld().get("status")).isEqualTo("CREATED");
        assertThat(subAUpdate1AL.getValuesOld().get("userAccount")).isEqualTo("5");
        assertThat(subAUpdate1AL.getValuesOld().get("subscriptionRenewal.autoRenew")).isEqualTo(0);
        assertThat(subAUpdate1AL.getValuesOld().get("subscriptionRenewal.renewalTermType")).isEqualTo("RECURRING");

        // Audit field log
        assertThat(subAUpdateStatusAF.getChangeType()).isEqualTo(AuditChangeTypeEnum.STATUS);
        assertThat(subAUpdateStatusAF.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subAUpdateStatusAF.getEntityId()).isEqualTo(22L);
        assertThat(subAUpdateStatusAF.getChangeOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subAUpdateStatusAF.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subAUpdateStatusAF.getActor()).isEqualTo("opencell.admin");
        assertThat(subAUpdateStatusAF.getName()).isEqualTo("status");
        assertThat(subAUpdateStatusAF.getPreviousState()).isEqualTo("CREATED");
        assertThat(subAUpdateStatusAF.getCurrentState()).isEqualTo("ACTIVE");

        assertThat(subAUpdateUAAF.getChangeType()).isEqualTo(AuditChangeTypeEnum.OTHER);
        assertThat(subAUpdateUAAF.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subAUpdateUAAF.getEntityId()).isEqualTo(22L);
        assertThat(subAUpdateUAAF.getChangeOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subAUpdateUAAF.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subAUpdateUAAF.getActor()).isEqualTo("opencell.admin");
        assertThat(subAUpdateUAAF.getName()).isEqualTo("userAccount");
        assertThat(subAUpdateUAAF.getPreviousState()).isEqualTo("5");
        assertThat(subAUpdateUAAF.getCurrentState()).isEqualTo("6");

        assertThat(subAUpdateRenewalAF.getChangeType()).isEqualTo(AuditChangeTypeEnum.RENEWAL);
        assertThat(subAUpdateRenewalAF.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subAUpdateRenewalAF.getEntityId()).isEqualTo(22L);
        assertThat(subAUpdateRenewalAF.getChangeOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subAUpdateRenewalAF.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subAUpdateRenewalAF.getActor()).isEqualTo("opencell.admin");
        assertThat(subAUpdateRenewalAF.getName()).isEqualTo("subscriptionRenewal");
        assertThat(subAUpdateRenewalAF.getPreviousState()).isEqualTo("{\"subscriptionRenewal.renewalTermType\":\"RECURRING\",\"subscriptionRenewal.autoRenew\":\"0\"}");
        assertThat(subAUpdateRenewalAF.getCurrentState()).isEqualTo("{\"subscriptionRenewal.renewalTermType\":\"CALENDAR\",\"subscriptionRenewal.autoRenew\":\"1\"}");

        // Verify audit field notification
        assertThat(subAUpdateStatusAFN.getId()).isEqualTo(subAUpdateStatusAF.getId());
        assertThat(subAUpdateRenewalAFN.getId()).isEqualTo(subAUpdateRenewalAF.getId());

        // Update Offer A - renewal

        // Audit data log
        assertThat(subAUpdate2AL.getAction()).isEqualTo(AuditCrudActionEnum.UPDATE);
        assertThat(subAUpdate2AL.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subAUpdate2AL.getEntityId()).isEqualTo(22L);
        assertThat(subAUpdate2AL.getTxId()).isEqualTo(5909053L);
        assertThat(subAUpdate2AL.getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subAUpdate2AL.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subAUpdate2AL.getUserName()).isEqualTo("opencell.admin");

        assertThat(subAUpdate2AL.getValuesChanged().get("renewed")).isEqualTo(1);
        assertThat(subAUpdate2AL.getValuesChanged().get("auditable.updater")).isEqualTo("admin");

        assertThat(subAUpdate2AL.getValuesOld().get("renewed")).isEqualTo(0);
        assertThat(subAUpdate2AL.getValuesOld().get("auditable.updater")).isEqualTo("null");

        // Audit field log
        assertThat(subAUpdateRenewedAF.getChangeType()).isEqualTo(AuditChangeTypeEnum.RENEWAL);
        assertThat(subAUpdateRenewedAF.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subAUpdateRenewedAF.getEntityId()).isEqualTo(22L);
        assertThat(subAUpdateRenewedAF.getChangeOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subAUpdateRenewedAF.getOriginName()).isEqualTo("/billing/subscription/createOrUpdate");
        assertThat(subAUpdateRenewedAF.getActor()).isEqualTo("opencell.admin");
        assertThat(subAUpdateRenewedAF.getName()).isEqualTo("renewed");
        assertThat(subAUpdateRenewedAF.getPreviousState()).isEqualTo("0");
        assertThat(subAUpdateRenewedAF.getCurrentState()).isEqualTo("1");

        // Verify audit field notification
        assertThat(subAUpdateRenewedAFN.getId()).isEqualTo(subAUpdateRenewedAF.getId());

        // ------
        // Seller creation

        // Audit data log
        assertThat(sellerCreateAL.getAction()).isEqualTo(AuditCrudActionEnum.INSERT);
        assertThat(sellerCreateAL.getEntityClass()).isEqualTo(Seller.class.getName());
        assertThat(sellerCreateAL.getEntityId()).isEqualTo(5L);
        assertThat(sellerCreateAL.getTxId()).isEqualTo(111L);
        assertThat(sellerCreateAL.getOrigin()).isEqualTo(ChangeOriginEnum.GUI);
        assertThat(sellerCreateAL.getOriginName()).isEqualTo("page/seller");
        assertThat(sellerCreateAL.getUserName()).isEqualTo("opencell.admin");
        assertThat(sellerCreateAL.getValuesOld()).isNull();
        assertThat(sellerCreateAL.getValuesChanged().get("contactInformation.email")).isEqualTo("test@opencellsoft.com");
        assertThat(sellerCreateAL.getValuesChanged().get("contactInformation.phone")).isEqualTo("03546536168");
        assertThat(sellerCreateAL.getValuesChanged().get("address.city")).isEqualTo("Los Reartes");

        // ------
        // Offer A deletion

        // Audit data log
        assertThat(subADeleteAL.getAction()).isEqualTo(AuditCrudActionEnum.DELETE);
        assertThat(subADeleteAL.getEntityClass()).isEqualTo(Subscription.class.getName());
        assertThat(subADeleteAL.getEntityId()).isEqualTo(22L);
        assertThat(subADeleteAL.getTxId()).isEqualTo(5909071L);
        assertThat(subADeleteAL.getOrigin()).isEqualTo(ChangeOriginEnum.API);
        assertThat(subADeleteAL.getOriginName()).isEqualTo("/billing/subscription/{subscriptionCode}");
        assertThat(subADeleteAL.getUserName()).isEqualTo("opencell.admin");

        assertThat(subADeleteAL.getValuesChanged().get("code")).isEqualTo("SubA");
        assertThat(subADeleteAL.getValuesChanged().get("status")).isEqualTo("ACTIVE");

        assertThat(subADeleteAL.getValuesOld().get("code")).isEqualTo("SubA");
        assertThat(subADeleteAL.getValuesOld().get("status")).isEqualTo("ACTIVE");

        assertThat(deleteQuerySimulated.getIdsParam().size()).isEqualTo(6);

        assertThat(auditDataLogRecords.size()).isEqualTo(0);
    }

    private class QuerySimulation implements TypedQuery<Long> {

        Long idParam = null;
        List<Long> idsParam = null;

        @Override
        public int executeUpdate() {

            return 0;
        }

        @Override
        public int getMaxResults() {

            return 0;
        }

        @Override
        public int getFirstResult() {

            return 0;
        }

        @Override
        public Map<String, Object> getHints() {

            return null;
        }

        @Override
        public Set<Parameter<?>> getParameters() {

            return null;
        }

        @Override
        public Parameter<?> getParameter(String name) {

            return null;
        }

        @Override
        public <T> Parameter<T> getParameter(String name, Class<T> type) {

            return null;
        }

        @Override
        public Parameter<?> getParameter(int position) {

            return null;
        }

        @Override
        public <T> Parameter<T> getParameter(int position, Class<T> type) {

            return null;
        }

        @Override
        public boolean isBound(Parameter<?> param) {

            return false;
        }

        @Override
        public <T> T getParameterValue(Parameter<T> param) {

            return null;
        }

        @Override
        public Object getParameterValue(String name) {

            return null;
        }

        @Override
        public Object getParameterValue(int position) {

            return null;
        }

        @Override
        public FlushModeType getFlushMode() {

            return null;
        }

        @Override
        public LockModeType getLockMode() {

            return null;
        }

        @Override
        public <T> T unwrap(Class<T> cls) {

            return null;
        }

        @Override
        public List getResultList() {

            return null;
        }

        @Override
        public Long getSingleResult() {

            if (idParam == 56L || idParam == 57L || idParam == 58L) {
                return 22L;

            } else if (idParam == 66L || idParam == 67L || idParam == 68L) {
                return 23L;
            }
            return null;
        }

        @Override
        public TypedQuery<Long> setMaxResults(int maxResult) {

            return null;
        }

        @Override
        public TypedQuery setFirstResult(int startPosition) {

            return null;
        }

        @Override
        public TypedQuery setHint(String hintName, Object value) {

            return null;
        }

        @Override
        public TypedQuery setParameter(Parameter param, Object value) {

            return null;
        }

        @Override
        public TypedQuery setParameter(Parameter param, Calendar value, TemporalType temporalType) {

            return null;
        }

        @Override
        public TypedQuery setParameter(Parameter param, Date value, TemporalType temporalType) {

            return null;
        }

        @Override
        public TypedQuery setParameter(String name, Object value) {
            if (value instanceof Long) {
                idParam = ((Long) value).longValue();
            } else if (value instanceof List) {
                idsParam = (List<Long>) value;
            }
            return this;
        }

        @Override
        public TypedQuery setParameter(String name, Calendar value, TemporalType temporalType) {

            return null;
        }

        @Override
        public TypedQuery setParameter(String name, Date value, TemporalType temporalType) {

            return null;
        }

        @Override
        public TypedQuery setParameter(int position, Object value) {

            return null;
        }

        @Override
        public TypedQuery setParameter(int position, Calendar value, TemporalType temporalType) {

            return null;
        }

        @Override
        public TypedQuery setParameter(int position, Date value, TemporalType temporalType) {

            return null;
        }

        @Override
        public TypedQuery setFlushMode(FlushModeType flushMode) {

            return null;
        }

        @Override
        public TypedQuery setLockMode(LockModeType lockMode) {

            return null;
        }

        public List<Long> getIdsParam() {
            return idsParam;
        }
    }
}