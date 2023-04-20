package org.meveo.admin.job;

import static org.meveo.model.billing.InvoiceLineStatusEnum.OPEN;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderLotService;
import org.meveo.util.ApplicationProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceLinesFactoryTest {

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private BillingAccountService billingAccountService;

    @Mock
    private BillingRunService billingRunService;

    @Mock
    private AccountingArticleService accountingArticleService;

    @Mock
    private OfferTemplateService offerTemplateService;

    @Mock
    private ServiceInstanceService instanceService;

    @Mock
    private CommercialOrderService commercialOrderService;

    @Mock
    private ProductVersionService productVersionService;

    @Mock
    private OrderLotService orderLotService;

    @Mock
    @ApplicationProvider
    private Provider appProvider;
    
    @Mock
    EntityManager entityManager;

    @InjectMocks
    private InvoiceLinesFactory factory;

    private Subscription subscription;
    private BillingAccount billingAccount;
    private BillingRun billingRun;
    private CommercialOrder commercialOrder;
    private ProductVersion productVersion;
    private ServiceInstance serviceInstance;
    private OrderLot orderLot;
    private OfferTemplate offerTemplate;
    private AccountingArticle accountingArticle;

    @Before
    public void setUp() {
        subscription = new Subscription();
        subscription.setId(1l);
        subscription.setCode("SUB_01");
        subscription.setDescription("Subscription");

        billingAccount = new BillingAccount();
        billingAccount.setId(1L);
        billingAccount.setCode("BA_001");

        billingRun = new BillingRun();
        billingRun.setStatus(BillingRunStatusEnum.NEW);
        billingRun.setId(1L);
        billingRun.setDescriptionOrCode("Billing Run 001");

        commercialOrder = new CommercialOrder();
        commercialOrder.setId(1L);
        commercialOrder.setVersion(1);

        productVersion = new ProductVersion();
        productVersion.setId(1L);
        productVersion.setVersion(1);

        serviceInstance = new ServiceInstance();
        serviceInstance.setId(1L);
        serviceInstance.setCode("Service_01");
        serviceInstance.setDescription("Service Instance 01");

        orderLot = new OrderLot();
        orderLot.setId(1L);
        orderLot.setCode("OrderLot_001");

        offerTemplate = new OfferTemplate();
        offerTemplate.setId(1L);
        offerTemplate.setCode("OfferTemplate_001");

        accountingArticle = new AccountingArticle();
        accountingArticle.setId(1L);
        accountingArticle.setCode("ACCA_001");
        accountingArticle.setDescription("Accounting Article 001");
        appProvider.setRounding(3);
        appProvider.setRoundingMode(RoundingModeEnum.NEAREST);
        
        when(subscriptionService.getEntityManager()).thenReturn(entityManager);
        when(billingAccountService.getEntityManager()).thenReturn(entityManager);
        when(billingRunService.getEntityManager()).thenReturn(entityManager);
        when(offerTemplateService.getEntityManager()).thenReturn(entityManager);
        when(orderLotService.getEntityManager()).thenReturn(entityManager);
        when(instanceService.getEntityManager()).thenReturn(entityManager);
        when(commercialOrderService.getEntityManager()).thenReturn(entityManager);
        when(productVersionService.getEntityManager()).thenReturn(entityManager);
        

        when(entityManager.getReference(Subscription.class,1L)).thenReturn(subscription);
        when(entityManager.getReference(BillingAccount.class,1L)).thenReturn(billingAccount);
        when(entityManager.getReference(BillingRun.class,1L)).thenReturn(billingRun);
        when(entityManager.getReference(OfferTemplate.class,1L)).thenReturn(offerTemplate);
        when(entityManager.getReference(OrderLot.class,1L)).thenReturn(orderLot);
        when(entityManager.getReference(ServiceInstance.class,1L)).thenReturn(serviceInstance);
        when(entityManager.getReference(CommercialOrder.class,1L)).thenReturn(commercialOrder);
        when(entityManager.getReference(ProductVersion.class,1L)).thenReturn(productVersion);
        when(appProvider.getRoundingMode()).thenReturn(RoundingModeEnum.NEAREST);
        when(appProvider.getRounding()).thenReturn(3);
    }

    @Test
    public void test_create_invoiceLines_withoutAgg() throws ParseException {
        AggregationConfiguration configuration = new AggregationConfiguration(false, false,DateAggregationOption.NO_DATE_AGGREGATION);
        Map<String, Object> record = buildRecord();
        BillingRun billingRun = new BillingRun();
        billingRun.setId(1L);

        InvoiceLine invoiceLine = factory.create(record, Map.of(), configuration, null, appProvider, billingRun, null);

        Assert.assertEquals(invoiceLine.getStatus(), OPEN);
        Assert.assertEquals(invoiceLine.getOrderNumber(), "1123456");
        Assert.assertEquals(invoiceLine.getBillingRun().getId(), Long.valueOf(1));
        BigDecimal amountWithoutTax = new BigDecimal(100.124);
        BigDecimal amountTax = new BigDecimal(10.012);
        BigDecimal amountWithTax = new BigDecimal(110.136);
        Assert.assertEquals(invoiceLine.getAmountWithoutTax(), amountWithoutTax.setScale(3, RoundingMode.HALF_UP));
        Assert.assertEquals(invoiceLine.getAmountTax(), amountTax.setScale(3, RoundingMode.HALF_UP));
        Assert.assertEquals(invoiceLine.getAmountWithTax(), amountWithTax.setScale(3, RoundingMode.HALF_UP));
    }

    @Test
    public void test_create_invoiceLines_withAgg() throws ParseException {
    	AggregationConfiguration configuration = new AggregationConfiguration(false, false,DateAggregationOption.NO_DATE_AGGREGATION);
        Map<String, Object> record = buildRecord();
        BillingRun billingRun = new BillingRun();
        billingRun.setId(1L);

        InvoiceLine invoiceLine = factory.create(record, Map.of(), configuration, null, appProvider, billingRun, null);

        Assert.assertEquals(invoiceLine.getStatus(), OPEN);
        Assert.assertEquals(invoiceLine.getOrderNumber(), "1123456");
        Assert.assertEquals("labe", invoiceLine.getLabel());
        Assert.assertEquals(invoiceLine.getUnitPrice(), BigDecimal.valueOf(20));
    }

    @Test
    public void test_create_invoiceLines_enterprise() throws ParseException {
    	AggregationConfiguration configuration = new AggregationConfiguration(false, false,DateAggregationOption.NO_DATE_AGGREGATION);
        Map<String, Object> record = buildRecord();
        when(appProvider.isEntreprise()).thenReturn(Boolean.TRUE);

        InvoiceLine invoiceLine = factory.create(record, Map.of(), configuration, null, appProvider, billingRun, null);

        Assert.assertEquals(invoiceLine.getStatus(), OPEN);
        Assert.assertEquals(invoiceLine.getOrderNumber(), "1123456");
        Assert.assertEquals(invoiceLine.getRawAmount(), new BigDecimal(110.13574));
        Assert.assertEquals(invoiceLine.getUnitPrice(), BigDecimal.valueOf(20));
    }

    private Map<String, Object> buildRecord() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        Map<String, Object> record = new HashMap<>();
        record.put("billing_account__id", BigInteger.valueOf(1));
        record.put("billing_run_id", BigInteger.valueOf(1));
        record.put("article_id", BigInteger.valueOf(1));
        record.put("service_instance_id", BigInteger.valueOf(1));
        record.put("service_instance_id", BigInteger.valueOf(1));
        record.put("offer_id", BigInteger.valueOf(1));
        record.put("order_id", BigInteger.valueOf(1));
        record.put("product_version_id", BigInteger.valueOf(1));
        record.put("order_lot_id", BigInteger.valueOf(1));
        record.put("valueDate", dateFormat.parse("10/12/2020"));
        record.put("order_number", "1123456");
        record.put("quantity", new BigDecimal(10));
        record.put("tax_percent", new BigDecimal(10));
        record.put("sum_with_tax", new BigDecimal(110.13574));
        record.put("sum_without_tax", new BigDecimal(100.1234));
        record.put("label", "labe");
        record.put("unit_amount_without_tax", new BigDecimal(10));
        record.put("unit_amount_with_tax", new BigDecimal(20));
        record.put("start_date", dateFormat.parse("11/12/2020"));
        record.put("usage_date", dateFormat.parse("12/12/2020"));
        record.put("end_date", dateFormat.parse("13/12/2020"));
        record.put("unit_price", new BigDecimal(11));
        record.put("subscription_id", BigInteger.valueOf(1));
        return record;
    }
}