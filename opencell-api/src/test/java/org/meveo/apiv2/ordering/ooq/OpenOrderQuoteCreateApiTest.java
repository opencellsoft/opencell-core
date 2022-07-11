package org.meveo.apiv2.ordering.ooq;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.ordering.resource.ooq.OpenOrderQuoteDto;
import org.meveo.apiv2.ordering.resource.order.ThresholdInput;
import org.meveo.apiv2.ordering.services.ooq.OpenOrderQuoteApi;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.ordering.OpenOrderArticle;
import org.meveo.model.ordering.OpenOrderProduct;
import org.meveo.model.ordering.OpenOrderQuote;
import org.meveo.model.ordering.OpenOrderQuoteStatusEnum;
import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.model.ordering.OpenOrderTypeEnum;
import org.meveo.model.ordering.Threshold;
import org.meveo.model.ordering.ThresholdRecipientsEnum;
import org.meveo.model.settings.MaximumValidityUnitEnum;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.cpq.TagService;
import org.meveo.service.order.OpenOrderArticleService;
import org.meveo.service.order.OpenOrderProductService;
import org.meveo.service.order.OpenOrderQuoteService;
import org.meveo.service.order.OpenOrderTemplateService;
import org.meveo.service.settings.impl.OpenOrderSettingService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.annotation.Nullable;
import javax.ws.rs.core.Link;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class OpenOrderQuoteCreateApiTest {

    @InjectMocks
    private OpenOrderQuoteApi openOrderQuoteApi;
    @Mock
    private OpenOrderQuoteService openOrderQuoteService;
    @Mock
    private OpenOrderTemplateService openOrderTemplateService;
    @Mock
    private BillingAccountService billingAccountService;
    @Mock
    private TagService tagService;
    @Mock
    private OpenOrderProductService openOrderProductService;
    @Mock
    private OpenOrderArticleService openOrderArticleService;
    @Mock
    private MeveoUser currentUser;
    @Mock
    private ServiceSingleton serviceSingleton;
    @Mock
    private OpenOrderSettingService openOrderSettingService;

    @Test
    public void createWithArticleNominal() {
        // String code, String billingAccountCode, String description, String externalReference,
        // OpenOrderTypeEnum openOrderType, String template, BigDecimal maxAmount,
        // Date endOfValidityDate, Date activationDate,
        // Set<ThresholdInput> thresholds, Set<String> tags, Set<String> articles, Set<String> products
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        // ************************************
        // DEFINE => AMOUNT / APPLY => DATE !!
        // ************************************
        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(false);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(1000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderArticleService.findByArticleCodeAndTemplate(any(), any())).thenReturn(ooa);
        Mockito.when(serviceSingleton.getNextOpenOrderSequence()).thenReturn("OOT-NUMBER");
        doReturn("TU-OOQ").when(currentUser).getUserName();

        openOrderQuoteApi.create(dto);

    }

    @Test
    public void createWithProductNominal() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.PRODUCTS, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), null, Set.of("P"));

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderProduct oop = new OpenOrderProduct();
        Product p = new Product();
        p.setCode("P");
        oop.setProduct(p);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(false);
        orderSetting.setApplyMaximumValidityUnit(null);
        orderSetting.setApplyMaximumValidityValue(null);
        orderSetting.setDefineMaximumValidity(false);
        orderSetting.setDefineMaximumValidityValue(null);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderProductService.findByProductCodeAndTemplate(any(), any())).thenReturn(oop);
        Mockito.when(serviceSingleton.getNextOpenOrderSequence()).thenReturn("OOT-NUMBER");
        doReturn("TU-OOQ").when(currentUser).getUserName();

        openOrderQuoteApi.create(dto);

    }

    @Test
    public void templateThresholdRecepientNominal() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com")));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderArticleService.findByArticleCodeAndTemplate(any(), any())).thenReturn(ooa);
        Mockito.when(serviceSingleton.getNextOpenOrderSequence()).thenReturn("OOT-NUMBER");
        doReturn("TU-OOQ").when(currentUser).getUserName();

        openOrderQuoteApi.create(dto);

    }

    @Test
    public void templateThresholdInvalidRecipientErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com")));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "All existing recipients in a template line must be present in the OpenOrder existing line. Missing recipients : [CUSTOMER]");
        }

    }

    @Test
    public void templateThresholdInvalidRecipientCONSOMERErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER, ThresholdRecipientsEnum.SALES_AGENT), "test@oc.com")));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "All existing recipients in a template line must be present in the OpenOrder existing line. Missing recipients : [SALES_AGENT, USER]");
        }

    }

    @Test
    public void thresholdSequenceInvalidErr() {
        ThresholdInput thresholdInput1 = buildThresholdInput(1, 50, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com");
        ThresholdInput thresholdInput2 = buildThresholdInput(3, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput1, thresholdInput2), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(buildThreshold(1, 50, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com")));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Threshold sequence are not consecutive [1, 3]");
        }

    }

    @Test
    public void thresholdPercentageSequenceInvalidErr() {
        ThresholdInput thresholdInput1 = buildThresholdInput(1, 50, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com");
        ThresholdInput thresholdInput2 = buildThresholdInput(2, 20, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput1, thresholdInput2), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(buildThreshold(1, 50, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com")));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Thresholds percent are not in correct sequence : current percente='20', previous one='50'");
        }

    }

    @Test
    public void thresholdSequenceInvalidStartErr() {
        ThresholdInput thresholdInput1 = buildThresholdInput(2, 50, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com");
        ThresholdInput thresholdInput2 = buildThresholdInput(3, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput1, thresholdInput2), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(buildThreshold(1, 50, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com")));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Threshold sequence shall be start by '1'");
        }

    }

    @Test
    public void thresholdSequenceInvalidSizeErr() {
        ThresholdInput thresholdInput1 = buildThresholdInput(1, 50, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com");
        ThresholdInput thresholdInput2 = buildThresholdInput(2, 60, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput3 = buildThresholdInput(2, 60, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput4 = buildThresholdInput(4, 60, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput1, thresholdInput2, thresholdInput3, thresholdInput4), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(buildThreshold(1, 50, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com")));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Threshold sequence are not consecutive [1, 2, 2, 4]");
        }

    }

    @Test
    public void templateAndThresholdPercentageInvalidErr() {
        ThresholdInput thresholdInput1 = buildThresholdInput(1, 50, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com");
        ThresholdInput thresholdInput2 = buildThresholdInput(2, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput1, thresholdInput2), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(buildThreshold(1, 20, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com")));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "All existing thresholds on template must be present in the OpenOrder. Missing percentages : [20]");
        }

    }

    @Test
    public void templateAndThresholdPercentageInvalid2Err() {
        ThresholdInput thresholdInput1 = buildThresholdInput(1, 10, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com");
        ThresholdInput thresholdInput2 = buildThresholdInput(2, 20, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput3 = buildThresholdInput(3, 33, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput4 = buildThresholdInput(4, 41, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput5 = buildThresholdInput(5, 50, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput6 = buildThresholdInput(6, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput1, thresholdInput2, thresholdInput3, thresholdInput4, thresholdInput5, thresholdInput6), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(
                buildThreshold(1, 20, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com"),
                buildThreshold(2, 20, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com"),
                buildThreshold(3, 30, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com"),
                buildThreshold(4, 60, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com")
        ));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "All existing thresholds on template must be present in the OpenOrder. Missing percentages : [30, 60]");
        }

    }

    @Test
    public void templateAndThresholdValidPercentageNominal() {
        ThresholdInput thresholdInput1 = buildThresholdInput(1, 10, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com");
        ThresholdInput thresholdInput2 = buildThresholdInput(2, 20, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput3 = buildThresholdInput(3, 30, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput4 = buildThresholdInput(4, 33, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput5 = buildThresholdInput(5, 50, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput6 = buildThresholdInput(6, 60, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput7 = buildThresholdInput(7, 70, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput8 = buildThresholdInput(8, 90, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        ThresholdInput thresholdInput9 = buildThresholdInput(9, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput1, thresholdInput2, thresholdInput3, thresholdInput4, thresholdInput5, thresholdInput6, thresholdInput7, thresholdInput8, thresholdInput9),
                Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setThresholds(List.of(
                buildThreshold(1, 20, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com"),
                buildThreshold(2, 20, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com"),
                buildThreshold(3, 30, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com"),
                buildThreshold(4, 60, List.of(ThresholdRecipientsEnum.CUSTOMER), "test@oc.com")
        ));

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderArticleService.findByArticleCodeAndTemplate(any(), any())).thenReturn(ooa);
        Mockito.when(serviceSingleton.getNextOpenOrderSequence()).thenReturn("OOT-NUMBER");
        doReturn("TU-OOQ").when(currentUser).getUserName();

        openOrderQuoteApi.create(dto);

    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void duplicatedCodeErr() {
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(new OpenOrderQuote());

        openOrderQuoteApi.create(dto);

    }


    @Test(expected = EntityDoesNotExistsException.class)
    public void billingAccountNotFoundErr() {
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(null);

        openOrderQuoteApi.create(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void templateNotFoundErr() {
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, Set.of("TAG_A"), Set.of("A"), null);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(null);

        openOrderQuoteApi.create(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void tagNotFoundErr() {
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(null);

        openOrderQuoteApi.create(dto);

    }

    @Test
    public void thresholdPercentageInvalidErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 0, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Invalid Threshold percentage '0'. Value must be between 1 and 100");
        }

    }

    @Test
    public void thresholdPercentageInvalidErr2() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 101, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Invalid Threshold percentage '101'. Value must be between 1 and 100");
        }

    }

    @Test
    public void thresholdEmptyReceipientErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 85, null, "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Threshold Recipients must not be empty");
        }

    }

    @Test
    public void invalidProductTypeErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), Set.of("INAVLID"));

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrderQuote with type ARTICLES shall not have PRODUCTS");
        }

    }

    @Test
    public void invalidArticleContentErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), null, null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrderQuote with type ARTICLES must have at least one ARTICLES");
        }

    }

    @Test
    public void invalidProductContentErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.PRODUCTS, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), null, null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrderQuote with type PRODUCTS must have at least one PRODUCTS");
        }

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void invalidProductNotExitErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.PRODUCTS, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), null, Set.of("A"));

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderProductService.findByProductCodeAndTemplate(any(), any())).thenReturn(null);

        openOrderQuoteApi.create(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void invalidArticleNotExitErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderArticleService.findByArticleCodeAndTemplate(any(), any())).thenReturn(null);

        openOrderQuoteApi.create(dto);

    }

    @Test
    public void invalidArticleTypeErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 1, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.PRODUCTS, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("INAVLID"), Set.of("A"));

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderProduct oop = new OpenOrderProduct();
        Product p = new Product();
        p.setCode("A");
        oop.setProduct(p);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrderQuote with type PRODUCTS shall not have ARTICLES");
        }

    }

    @Test
    public void invalidOpenOrderTypeErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderArticleService.findByArticleCodeAndTemplate(any(), any())).thenReturn(ooa);
        doReturn("TU-OOQ").when(currentUser).getUserName();

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrder type shall be the same as Template : given='ARTICLES' | template='PRODUCTS'");
        }

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void noOORSettingErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(null);

        openOrderQuoteApi.create(dto);

    }

    @Test
    public void oorNotEnabledErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(false);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrder not enable in settings");
        }

    }

    @Test
    public void oorSettingAmountInvalidErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount is greater than or equal OpenOrder settings maximum amount");
        }

    }

    @Test
    public void oorSettingAmountValidityDateDaysErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(1);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Given end validity date '" + LocalDate.now().plusDays(5) + "' exceed maximum OpenOrder settings '" + LocalDate.now().plusDays(1) + "'");
        }

    }

    @Test
    public void oorSettingAmountValidityDateMounthErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().plusMonths(5).atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Months);
        orderSetting.setApplyMaximumValidityValue(1);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Given end validity date '" + LocalDate.now().plusMonths(5) + "' exceed maximum OpenOrder settings '" + LocalDate.now().plusMonths(1) + "'");
        }

    }

    @Test
    public void oorSettingAmountValidityDateWeeksErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().plusWeeks(5).atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Weeks);
        orderSetting.setApplyMaximumValidityValue(1);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Given end validity date '" + LocalDate.now().plusWeeks(5) + "' exceed maximum OpenOrder settings '" + LocalDate.now().plusWeeks(1) + "'");
        }

    }

    @Test
    public void oorSettingAmountValidityDateYearsErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().plusYears(5).atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Years);
        orderSetting.setApplyMaximumValidityValue(1);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Given end validity date '" + LocalDate.now().plusYears(5) + "' exceed maximum OpenOrder settings '" + LocalDate.now().plusYears(1) + "'");
        }

    }

    @Test
    public void oorSettingAmountValidityInvalidErr() {
        ThresholdInput thresholdInput = buildThresholdInput(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().plusYears(5).atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("BIL-ACC-1");

        OpenOrderArticle ooa = new OpenOrderArticle();
        AccountingArticle aa = new AccountingArticle();
        aa.setCode("A");
        ooa.setAccountingArticle(aa);

        Tag tag = new Tag();
        tag.setCode("TAG_A");

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(null);
        orderSetting.setApplyMaximumValidityValue(null);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);

        try {
            openOrderQuoteApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Invalid OpenOrder settings : Maximum Validity Value and Maximum Validity Unit must not be null");
        }

    }

    // ************************************************************
    // ************************ TOOLS *****************************
    // ************************************************************

    private OpenOrderQuoteDto buildDto(String code, String billingAccountCode, String description, String externalReference,
                                       OpenOrderTypeEnum openOrderType, String template, BigDecimal maxAmount,
                                       Date endOfValidityDate, Date activationDate,
                                       Set<ThresholdInput> thresholds, Set<String> tags, Set<String> articles, Set<String> products) {
        return new OpenOrderQuoteDto() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getBillingAccountCode() {
                return billingAccountCode;
            }

            @Nullable
            @Override
            public String getDescription() {
                return description;
            }

            @Nullable
            @Override
            public String getExternalReference() {
                return externalReference;
            }

            @Override
            public OpenOrderTypeEnum getOpenOrderType() {
                return openOrderType;
            }

            @Override
            public String getOpenOrderTemplate() {
                return template;
            }

            @Override
            public BigDecimal getMaxAmount() {
                return maxAmount;
            }

            @Nullable
            @Override
            public Date getEndOfValidityDate() {
                return endOfValidityDate;
            }

            @Override
            public Date getActivationDate() {
                return activationDate;
            }

            @Nullable
            @Override
            public Set<ThresholdInput> getThresholds() {
                return thresholds;
            }

            @Nullable
            @Override
            public Set<String> getTags() {
                return tags;
            }

            @Nullable
            @Override
            public Set<String> getArticles() {
                return articles;
            }

            @Nullable
            @Override
            public Set<String> getProducts() {
                return products;
            }
        };

    }

    private OpenOrderQuote buildOOQ(OpenOrderTypeEnum type,
                                    OpenOrderQuoteStatusEnum status,
                                    List<OpenOrderArticle> articles,
                                    List<OpenOrderProduct> products) {
        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(-1L);
        ooq.setCode("OOQ-CODE-TU");
        ooq.setStatus(status);
        ooq.setActivationDate(null);
        ooq.setArticles(articles);
        ooq.setCurrency(null);
        ooq.setOpenOrderNumber(UUID.randomUUID().toString());
        ooq.setOpenOrderTemplate(null);
        ooq.setBillingAccount(null);
        ooq.setEndOfValidityDate(null);
        ooq.setExternalReference(null);
        ooq.setMaxAmount(null);
        ooq.setOpenOrderType(type);
        ooq.setProducts(products);
        ooq.setTags(null);
        ooq.setThresholds(null);

        return ooq;
    }

    private List<OpenOrderArticle> buildArticles() {
        OpenOrderArticle article = new OpenOrderArticle();
        article.setId(-1L);

        return List.of(article);
    }

    private List<OpenOrderProduct> buildProducts() {
        OpenOrderProduct products = new OpenOrderProduct();
        products.setId(-1L);

        return List.of(products);
    }

    private ThresholdInput buildThresholdInput(Integer sequence, Integer percentage,
                                               List<ThresholdRecipientsEnum> recipients,
                                               String getExternalRecipient) {

        return new ThresholdInput() {

            @Override
            public Integer getSequence() {
                return sequence;
            }

            @Override
            public Integer getPercentage() {
                return percentage;
            }

            @Override
            public List<ThresholdRecipientsEnum> getRecipients() {
                return recipients;
            }

            @Nullable
            @Override
            public String getExternalRecipient() {
                return getExternalRecipient;
            }

            @Nullable
            @Override
            public Long getId() {
                return null;
            }

            @Nullable
            @Override
            public String getCode() {
                return null;
            }

            @Nullable
            @Override
            public List<Link> getLinks() {
                return null;
            }
        };
    }

    private Threshold buildThreshold(Integer sequence, Integer percentage,
                                     List<ThresholdRecipientsEnum> recipients,
                                     String getExternalRecipient) {
        Threshold threshold = new Threshold();
        threshold.setSequence(sequence);
        threshold.setPercentage(percentage);
        threshold.setExternalRecipient(getExternalRecipient);
        threshold.setRecipients(recipients);

        return threshold;
    }


}