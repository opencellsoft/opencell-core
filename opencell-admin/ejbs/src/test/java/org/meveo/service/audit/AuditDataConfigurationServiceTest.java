package org.meveo.service.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TemporalType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.audit.AuditDataConfiguration;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.shared.Title;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.DeletionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class AuditDataConfigurationServiceTest {

    @Spy
    @InjectMocks
    private AuditDataConfigurationService auditDataConfigurationService;

    @Mock
    private MeveoUser currentUser;

    @Mock
    private EntityManager entityManager;

    @Mock
    private DeletionService deletionService;

    @Before
    public void setUp() {
        doReturn(entityManager).when(auditDataConfigurationService).getEntityManager();
    }

    @Test
    public void test_TriggerCreation_Simple() {

        List<ProcedureSimulation> queries = new ArrayList<AuditDataConfigurationServiceTest.ProcedureSimulation>();

        when(entityManager.createNamedStoredProcedureQuery(any())).thenAnswer(new Answer<ProcedureSimulation>() {
            public ProcedureSimulation answer(InvocationOnMock invocation) throws Throwable {
                ProcedureSimulation query = new ProcedureSimulation();
                query.setQueryName((String) invocation.getArguments()[0]);
                queries.add(query);
                return query;
            }
        });

        AuditDataConfiguration auditDataConfiguration = new AuditDataConfiguration(Title.class.getName(), null, null);

        auditDataConfigurationService.create(auditDataConfiguration);

        assertThat(queries.size()).isEqualTo(1);
        assertThat(queries.get(0).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(0).getTableName()).isEqualTo("adm_title");
        assertThat(queries.get(0).getFields()).isNull();
        assertThat(queries.get(0).getActions()).isNull();
        assertThat(queries.get(0).getPreserveField()).isNull();
        assertThat(queries.get(0).getSaveEvenDiffIsEmpty()).isEqualTo(false);
    }

    @Test
    public void test_TriggerCreation_Simple_WithFieldsAndActions() {

        List<ProcedureSimulation> queries = new ArrayList<AuditDataConfigurationServiceTest.ProcedureSimulation>();

        when(entityManager.createNamedStoredProcedureQuery(any())).thenAnswer(new Answer<ProcedureSimulation>() {
            public ProcedureSimulation answer(InvocationOnMock invocation) throws Throwable {
                ProcedureSimulation query = new ProcedureSimulation();
                query.setQueryName((String) invocation.getArguments()[0]);
                queries.add(query);
                return query;
            }
        });

        AuditDataConfiguration auditDataConfiguration = new AuditDataConfiguration(Title.class.getName(), "code, isCompany", "INSERT, UPDATE");

        auditDataConfigurationService.create(auditDataConfiguration);

        assertThat(queries.size()).isEqualTo(1);
        assertThat(queries.get(0).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(0).getTableName()).isEqualTo("adm_title");
        assertThat(queries.get(0).getFields()).isEqualTo("code,is_company");
        assertThat(queries.get(0).getActions()).isEqualTo("INSERT, UPDATE");
        assertThat(queries.get(0).getPreserveField()).isNull();
        assertThat(queries.get(0).getSaveEvenDiffIsEmpty()).isEqualTo(false);
    }

    @Test
    public void test_TriggerCreation_Complex() {

        List<ProcedureSimulation> queries = new ArrayList<AuditDataConfigurationServiceTest.ProcedureSimulation>();

        when(entityManager.createNamedStoredProcedureQuery(any())).thenAnswer(new Answer<ProcedureSimulation>() {
            public ProcedureSimulation answer(InvocationOnMock invocation) throws Throwable {
                ProcedureSimulation query = new ProcedureSimulation();
                query.setQueryName((String) invocation.getArguments()[0]);
                queries.add(query);
                return query;
            }
        });

        AuditDataConfiguration auditDataConfiguration = new AuditDataConfiguration(OfferTemplate.class.getName(), null, null);

        auditDataConfigurationService.create(auditDataConfiguration);

        assertThat(queries.size()).isEqualTo(16);
        assertThat(queries.get(0).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(0).getTableName()).isEqualTo("cat_offer_template");
        assertThat(queries.get(0).getFields()).isNull();
        assertThat(queries.get(0).getActions()).isNull();
        assertThat(queries.get(0).getPreserveField()).isNull();
        assertThat(queries.get(0).getSaveEvenDiffIsEmpty()).isEqualTo(true);

        assertThat(queries.get(1).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(1).getTableName()).isEqualTo("cat_offer_serv_templates");
        assertThat(queries.get(1).getFields()).isNull();
        assertThat(queries.get(1).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(1).getPreserveField()).isEqualTo("offer_template_id");
        assertThat(queries.get(1).getSaveEvenDiffIsEmpty()).isEqualTo(true);

        assertThat(queries.get(2).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(2).getTableName()).isEqualTo("cat_offer_serv_incomp");
        assertThat(queries.get(2).getFields()).isNull();
        assertThat(queries.get(2).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(2).getPreserveField()).isEqualTo("offer_service_template_id");
        assertThat(queries.get(2).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(3).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(3).getTableName()).isEqualTo("cat_offer_product_template");
        assertThat(queries.get(3).getFields()).isNull();
        assertThat(queries.get(3).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(3).getPreserveField()).isEqualTo("offer_template_id");
        assertThat(queries.get(3).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(4).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(4).getTableName()).isEqualTo("cpq_offer_component");
        assertThat(queries.get(4).getFields()).isNull();
        assertThat(queries.get(4).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(4).getPreserveField()).isEqualTo("offer_template_id");
        assertThat(queries.get(4).getSaveEvenDiffIsEmpty()).isEqualTo(true);

        assertThat(queries.get(5).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(5).getTableName()).isEqualTo("cpq_offer_component_tags");
        assertThat(queries.get(5).getFields()).isNull();
        assertThat(queries.get(5).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(5).getPreserveField()).isEqualTo("offer_component_id");
        assertThat(queries.get(5).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(6).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(6).getTableName()).isEqualTo("cat_offer_tmpl_discount_plan");
        assertThat(queries.get(6).getFields()).isNull();
        assertThat(queries.get(6).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(6).getPreserveField()).isEqualTo("offer_tmpl_id");
        assertThat(queries.get(6).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(7).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(7).getTableName()).isEqualTo("cpq_offer_template_tags");
        assertThat(queries.get(7).getFields()).isNull();
        assertThat(queries.get(7).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(7).getPreserveField()).isEqualTo("offer_template_id");
        assertThat(queries.get(7).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(8).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(8).getTableName()).isEqualTo("cpq_offer_template_media");
        assertThat(queries.get(8).getFields()).isNull();
        assertThat(queries.get(8).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(8).getPreserveField()).isEqualTo("offer_template_id");
        assertThat(queries.get(8).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(9).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(9).getTableName()).isEqualTo("offer_template_attribute");
        assertThat(queries.get(9).getFields()).isNull();
        assertThat(queries.get(9).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(9).getPreserveField()).isEqualTo("offer_template_id");
        assertThat(queries.get(9).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(10).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(10).getTableName()).isEqualTo("cat_product_offer_tmpl_cat");
        assertThat(queries.get(10).getFields()).isNull();
        assertThat(queries.get(10).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(10).getPreserveField()).isEqualTo("product_id");
        assertThat(queries.get(10).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(11).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(11).getTableName()).isEqualTo("cat_product_offer_digital_res");
        assertThat(queries.get(11).getFields()).isNull();
        assertThat(queries.get(11).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(11).getPreserveField()).isEqualTo("product_id");
        assertThat(queries.get(11).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(12).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(12).getTableName()).isEqualTo("cat_product_offer_bam");
        assertThat(queries.get(12).getFields()).isNull();
        assertThat(queries.get(12).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(12).getPreserveField()).isEqualTo("product_id");
        assertThat(queries.get(12).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(13).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(13).getTableName()).isEqualTo("cat_product_offer_channels");
        assertThat(queries.get(13).getFields()).isNull();
        assertThat(queries.get(13).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(13).getPreserveField()).isEqualTo("product_id");
        assertThat(queries.get(13).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(14).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(14).getTableName()).isEqualTo("cat_product_offer_seller");
        assertThat(queries.get(14).getFields()).isNull();
        assertThat(queries.get(14).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(14).getPreserveField()).isEqualTo("product_id");
        assertThat(queries.get(14).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(15).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(15).getTableName()).isEqualTo("cat_product_offer_customer_category");
        assertThat(queries.get(15).getFields()).isNull();
        assertThat(queries.get(15).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(15).getPreserveField()).isEqualTo("product_id");
        assertThat(queries.get(15).getSaveEvenDiffIsEmpty()).isEqualTo(false);
    }

    @Test
    public void test_TriggerCreation_Complex_WithFieldsAndActions() {

        List<ProcedureSimulation> queries = new ArrayList<AuditDataConfigurationServiceTest.ProcedureSimulation>();

        when(entityManager.createNamedStoredProcedureQuery(any())).thenAnswer(new Answer<ProcedureSimulation>() {
            public ProcedureSimulation answer(InvocationOnMock invocation) throws Throwable {
                ProcedureSimulation query = new ProcedureSimulation();
                query.setQueryName((String) invocation.getArguments()[0]);
                queries.add(query);
                return query;
            }
        });

        AuditDataConfiguration auditDataConfiguration = new AuditDataConfiguration(OfferTemplate.class.getName(), "code, offerServiceTemplates, allowedDiscountPlans", "UPDATE,DELETE");

        auditDataConfigurationService.create(auditDataConfiguration);

        assertThat(queries.size()).isEqualTo(4);
        assertThat(queries.get(0).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(0).getTableName()).isEqualTo("cat_offer_template");
        assertThat(queries.get(0).getFields()).isEqualTo("code");
        assertThat(queries.get(0).getActions()).isEqualTo("UPDATE,DELETE");
        assertThat(queries.get(0).getPreserveField()).isNull();
        assertThat(queries.get(0).getSaveEvenDiffIsEmpty()).isEqualTo(true);

        assertThat(queries.get(1).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(1).getTableName()).isEqualTo("cat_offer_serv_templates");
        assertThat(queries.get(1).getFields()).isNull();
        assertThat(queries.get(1).getActions()).isEqualTo("UPDATE,DELETE");
        assertThat(queries.get(1).getPreserveField()).isEqualTo("offer_template_id");
        assertThat(queries.get(1).getSaveEvenDiffIsEmpty()).isEqualTo(true);

        assertThat(queries.get(2).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(2).getTableName()).isEqualTo("cat_offer_serv_incomp");
        assertThat(queries.get(2).getFields()).isNull();
        assertThat(queries.get(2).getActions()).isEqualTo("UPDATE,DELETE");
        assertThat(queries.get(2).getPreserveField()).isEqualTo("offer_service_template_id");
        assertThat(queries.get(2).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(3).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(3).getTableName()).isEqualTo("cat_offer_tmpl_discount_plan");
        assertThat(queries.get(3).getFields()).isNull();
        assertThat(queries.get(3).getActions()).isEqualTo("UPDATE,DELETE");
        assertThat(queries.get(3).getPreserveField()).isEqualTo("offer_tmpl_id");
        assertThat(queries.get(3).getSaveEvenDiffIsEmpty()).isEqualTo(false);
    }

    @Test
    public void test_TriggerDeletion_Simple() {

        List<ProcedureSimulation> queries = new ArrayList<AuditDataConfigurationServiceTest.ProcedureSimulation>();

        when(entityManager.createNamedStoredProcedureQuery(any())).thenAnswer(new Answer<ProcedureSimulation>() {
            public ProcedureSimulation answer(InvocationOnMock invocation) throws Throwable {
                ProcedureSimulation query = new ProcedureSimulation();
                query.setQueryName((String) invocation.getArguments()[0]);
                queries.add(query);
                return query;
            }
        });

        AuditDataConfiguration auditDataConfiguration = new AuditDataConfiguration(Title.class.getName(), null, null);

        auditDataConfigurationService.remove(auditDataConfiguration);

        assertThat(queries.size()).isEqualTo(1);
        assertThat(queries.get(0).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(0).getTableName()).isEqualTo("adm_title");
        assertThat(queries.get(0).getFields()).isNull();
        assertThat(queries.get(0).getActions()).isNull();
        assertThat(queries.get(0).getPreserveField()).isNull();
        assertThat(queries.get(0).getSaveEvenDiffIsEmpty()).isEqualTo(false);
    }

    @Test
    public void test_TriggerDeletion_Complex() {

        List<ProcedureSimulation> queries = new ArrayList<AuditDataConfigurationServiceTest.ProcedureSimulation>();

        when(entityManager.createNamedStoredProcedureQuery(any())).thenAnswer(new Answer<ProcedureSimulation>() {
            public ProcedureSimulation answer(InvocationOnMock invocation) throws Throwable {
                ProcedureSimulation query = new ProcedureSimulation();
                query.setQueryName((String) invocation.getArguments()[0]);
                queries.add(query);
                return query;
            }
        });

        AuditDataConfiguration auditDataConfiguration = new AuditDataConfiguration(OfferTemplate.class.getName(), null, "INSERT");

        auditDataConfigurationService.remove(auditDataConfiguration);

        for (ProcedureSimulation procedureSimulation : queries) {
            System.out.println(procedureSimulation);
        }

        assertThat(queries.size()).isEqualTo(16);
        assertThat(queries.get(0).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(0).getTableName()).isEqualTo("cat_offer_template");

        assertThat(queries.get(1).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(1).getTableName()).isEqualTo("cat_offer_serv_templates");

        assertThat(queries.get(2).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(2).getTableName()).isEqualTo("cat_offer_serv_incomp");

        assertThat(queries.get(3).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(3).getTableName()).isEqualTo("cat_offer_product_template");

        assertThat(queries.get(4).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(4).getTableName()).isEqualTo("cpq_offer_component");

        assertThat(queries.get(5).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(5).getTableName()).isEqualTo("cpq_offer_component_tags");

        assertThat(queries.get(6).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(6).getTableName()).isEqualTo("cat_offer_tmpl_discount_plan");

        assertThat(queries.get(7).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(7).getTableName()).isEqualTo("cpq_offer_template_tags");

        assertThat(queries.get(8).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(8).getTableName()).isEqualTo("cpq_offer_template_media");

        assertThat(queries.get(9).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(9).getTableName()).isEqualTo("offer_template_attribute");

        assertThat(queries.get(10).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(10).getTableName()).isEqualTo("cat_product_offer_tmpl_cat");

        assertThat(queries.get(11).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(11).getTableName()).isEqualTo("cat_product_offer_digital_res");

        assertThat(queries.get(12).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(12).getTableName()).isEqualTo("cat_product_offer_bam");

        assertThat(queries.get(13).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(13).getTableName()).isEqualTo("cat_product_offer_channels");

        assertThat(queries.get(14).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(14).getTableName()).isEqualTo("cat_product_offer_seller");

        assertThat(queries.get(15).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(15).getTableName()).isEqualTo("cat_product_offer_customer_category");
    }

    @Test
    public void test_TriggerDeletion_Complex_WithFieldsAndActions() {

        List<ProcedureSimulation> queries = new ArrayList<AuditDataConfigurationServiceTest.ProcedureSimulation>();

        when(entityManager.createNamedStoredProcedureQuery(any())).thenAnswer(new Answer<ProcedureSimulation>() {
            public ProcedureSimulation answer(InvocationOnMock invocation) throws Throwable {
                ProcedureSimulation query = new ProcedureSimulation();
                query.setQueryName((String) invocation.getArguments()[0]);
                queries.add(query);
                return query;
            }
        });

        AuditDataConfiguration auditDataConfiguration = new AuditDataConfiguration(OfferTemplate.class.getName(), "code, offerServiceTemplates, allowedDiscountPlans", "UPDATE,DELETE");

        auditDataConfigurationService.remove(auditDataConfiguration);

        assertThat(queries.size()).isEqualTo(4);
        assertThat(queries.get(0).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(0).getTableName()).isEqualTo("cat_offer_template");

        assertThat(queries.get(1).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(1).getTableName()).isEqualTo("cat_offer_serv_templates");

        assertThat(queries.get(2).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(2).getTableName()).isEqualTo("cat_offer_serv_incomp");

        assertThat(queries.get(3).getQueryName()).isEqualTo("AuditDataConfiguration.deleteDataAuditTrigger");
        assertThat(queries.get(3).getTableName()).isEqualTo("cat_offer_tmpl_discount_plan");
    }

    @Test
    public void test_AuditDataHierarchy_Subscription() {

        AuditDataHierarchy dataHierarchy = AuditDataConfigurationService.getAuditDataHierarchy(Subscription.class);

        assertThat(dataHierarchy.entityClass).isEqualTo(Subscription.class);
        assertThat(dataHierarchy.getFieldName()).isNull();
        assertThat(dataHierarchy.getParentIdDbColumn()).isNull();
        assertThat(dataHierarchy.getParentIdField()).isNull();
        assertThat(dataHierarchy.getSaveEvenDiffIsEmpty()).isEqualTo(true);
        assertThat(dataHierarchy.getDbColumByFieldname("subscriptionRenewal.initialTermType")).isEqualTo("initial_term_type");
        assertThat(dataHierarchy.getDbColumByFieldname("statusDate")).isEqualTo("status_date");
        assertThat(dataHierarchy.getDbColumByFieldname("auditable.created")).isEqualTo("created");
        assertThat(dataHierarchy.getDbColumByFieldname("validity.from")).isEqualTo("start_date");

        assertThat(dataHierarchy.getRelatedEntities().size()).isEqualTo(2);

        AuditDataHierarchy discountPlanDH = dataHierarchy.getRelatedEntities().get(0);
        assertThat(discountPlanDH.getEntityClass()).isEqualTo(DiscountPlanInstance.class);
        assertThat(discountPlanDH.getFieldName()).isEqualTo("discountPlanInstances");
        assertThat(discountPlanDH.getParentIdDbColumn()).isEqualTo("subscription_id");
        assertThat(discountPlanDH.getParentIdField()).isEqualTo("subscription");
        assertThat(discountPlanDH.getSaveEvenDiffIsEmpty()).isEqualTo(false);
        assertThat(discountPlanDH.getDbColumByFieldname("statusDate")).isEqualTo("status_date");
        assertThat(discountPlanDH.getDbColumByFieldname("billingAccount")).isEqualTo("billing_account_id");

        AuditDataHierarchy attributeInstanceDH = dataHierarchy.getRelatedEntities().get(1);
        assertThat(attributeInstanceDH.getEntityClass()).isEqualTo(AttributeInstance.class);
        assertThat(attributeInstanceDH.getFieldName()).isEqualTo("attributeInstances");
        assertThat(attributeInstanceDH.getParentIdDbColumn()).isEqualTo("subscription_id");
        assertThat(attributeInstanceDH.getParentIdField()).isEqualTo("subscription");
        assertThat(attributeInstanceDH.getSaveEvenDiffIsEmpty()).isEqualTo(false);
        assertThat(attributeInstanceDH.getDbColumByFieldname("auditable.creator")).isEqualTo("creator");
        assertThat(attributeInstanceDH.getDbColumByFieldname("booleanValue")).isEqualTo("boolean_value");
        assertThat(attributeInstanceDH.getDbColumByFieldname("parentAttributeValue")).isEqualTo("parent_id");
    }

    @Test
    public void test_TriggerCreation_Subscription() {

        List<ProcedureSimulation> queries = new ArrayList<AuditDataConfigurationServiceTest.ProcedureSimulation>();

        when(entityManager.createNamedStoredProcedureQuery(any())).thenAnswer(new Answer<ProcedureSimulation>() {
            public ProcedureSimulation answer(InvocationOnMock invocation) throws Throwable {
                ProcedureSimulation query = new ProcedureSimulation();
                query.setQueryName((String) invocation.getArguments()[0]);
                queries.add(query);
                return query;
            }
        });

        AuditDataConfiguration auditDataConfiguration = new AuditDataConfiguration(Subscription.class.getName(), null, null);

        auditDataConfigurationService.create(auditDataConfiguration);

        assertThat(queries.size()).isEqualTo(3);
        assertThat(queries.get(0).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(0).getTableName()).isEqualTo("billing_subscription");
        assertThat(queries.get(0).getFields()).isNull();
        assertThat(queries.get(0).getActions()).isNull();
        assertThat(queries.get(0).getPreserveField()).isNull();
        assertThat(queries.get(0).getSaveEvenDiffIsEmpty()).isEqualTo(true);

        assertThat(queries.get(1).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(1).getTableName()).isEqualTo("billing_discount_plan_instance");
        assertThat(queries.get(1).getFields()).isNull();
        assertThat(queries.get(1).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(1).getPreserveField()).isEqualTo("subscription_id");
        assertThat(queries.get(1).getSaveEvenDiffIsEmpty()).isEqualTo(false);

        assertThat(queries.get(2).getQueryName()).isEqualTo("AuditDataConfiguration.recreateDataAuditTrigger");
        assertThat(queries.get(2).getTableName()).isEqualTo("cpq_attribute_instance");
        assertThat(queries.get(2).getFields()).isNull();
        assertThat(queries.get(2).getActions()).isEqualTo("INSERT,UPDATE,DELETE");
        assertThat(queries.get(2).getPreserveField()).isEqualTo("subscription_id");
        assertThat(queries.get(2).getSaveEvenDiffIsEmpty()).isEqualTo(false);
    }

    private class ProcedureSimulation implements StoredProcedureQuery {

        String queryName;
        String tableName;
        String fields;
        String actions;
        String preserveField;
        boolean saveEvenDiffIsEmpty;

        @Override
        public Query setMaxResults(int maxResult) {
            return this;
        }

        @Override
        public int getMaxResults() {
            return 0;
        }

        @Override
        public Query setFirstResult(int startPosition) {
            return this;
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
        public Query setLockMode(LockModeType lockMode) {
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
        public StoredProcedureQuery setHint(String hintName, Object value) {
            return null;
        }

        @Override
        public <T> StoredProcedureQuery setParameter(Parameter<T> param, T value) {
            return null;
        }

        @Override
        public StoredProcedureQuery setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
            return null;
        }

        @Override
        public StoredProcedureQuery setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
            return null;
        }

        @Override
        public StoredProcedureQuery setParameter(String name, Object value) {
            if (name.equals("tableName")) {
                tableName = (String) value;
            } else if (name.equals("fields")) {
                fields = (String) value;
            } else if (name.equals("actions")) {
                actions = (String) value;
            } else if (name.equals("preserveField")) {
                preserveField = (String) value;
            } else if (name.equals("saveEvenDiffIsEmpty")) {
                saveEvenDiffIsEmpty = (boolean) value;
            }
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(String name, Calendar value, TemporalType temporalType) {
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(String name, Date value, TemporalType temporalType) {
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(int position, Object value) {
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(int position, Calendar value, TemporalType temporalType) {
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(int position, Date value, TemporalType temporalType) {
            return this;
        }

        @Override
        public StoredProcedureQuery setFlushMode(FlushModeType flushMode) {
            return this;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public StoredProcedureQuery registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
            return this;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public StoredProcedureQuery registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
            return this;
        }

        @Override
        public Object getOutputParameterValue(int position) {
            return null;
        }

        @Override
        public Object getOutputParameterValue(String parameterName) {
            return null;
        }

        @Override
        public boolean execute() {
            return false;
        }

        @Override
        public int executeUpdate() {
            return 0;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public List getResultList() {
            return null;
        }

        @Override
        public Object getSingleResult() {
            return null;
        }

        @Override
        public boolean hasMoreResults() {
            return false;
        }

        @Override
        public int getUpdateCount() {
            return 0;
        }

        public String getTableName() {
            return tableName;
        }

        public String getFields() {
            return fields;
        }

        public String getActions() {
            return actions;
        }

        public String getPreserveField() {
            return preserveField;
        }

        public boolean getSaveEvenDiffIsEmpty() {
            return saveEvenDiffIsEmpty;
        }

        public void setQueryName(String queryName) {
            this.queryName = queryName;
        }

        public String getQueryName() {
            return queryName;
        }

        @Override
        public String toString() {
            return "ProcedureSimulation [tableName=" + tableName + ", fields=" + fields + ", actions=" + actions + ", queryName=" + queryName + "]";
        }
    }
}