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
import org.meveo.model.ordering.OpenOrderStatusEnum;
import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.model.ordering.OpenOrderTemplateStatusEnum;
import org.meveo.model.ordering.OpenOrderTypeEnum;
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

import jakarta.annotation.Nullable;
import jakarta.ws.rs.core.Link;
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
public class OpenOrderQuoteUpdateApiTest {

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
    public void updateNominal() {
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderArticleService.findByArticleCodeAndTemplate(any(), any())).thenReturn(ooa);
        Mockito.when(serviceSingleton.getNextOpenOrderQuoteSequence()).thenReturn("OOT-NUMBER");
        doReturn("TU-OOQ").when(currentUser).getUserName();

        openOrderQuoteApi.update(1L, dto);

    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void duplicatedCodeErr() {
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(2L);

        OpenOrderSetting orderSetting = new OpenOrderSetting();
        orderSetting.setApplyMaximumValidity(true);
        orderSetting.setApplyMaximumValidityUnit(MaximumValidityUnitEnum.Days);
        orderSetting.setApplyMaximumValidityValue(5);
        orderSetting.setDefineMaximumValidity(true);
        orderSetting.setDefineMaximumValidityValue(10000);
        orderSetting.setUseOpenOrders(true);

        Mockito.when(openOrderSettingService.findLastOne()).thenReturn(orderSetting);
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);

        openOrderQuoteApi.update(1L, dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void ooqNotFoundErr() {
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(null);

        openOrderQuoteApi.update(1L, dto);

    }


    @Test(expected = EntityDoesNotExistsException.class)
    public void billingAccountNotFoundErr() {
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(null);

        openOrderQuoteApi.update(1L, dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void templateNotFoundErr() {
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(null);

        openOrderQuoteApi.update(1L, dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void tagNotFoundErr() {
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(null);

        openOrderQuoteApi.update(1L, dto);

    }

    @Test
    public void templateInvalidErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 0, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderTemplate otherTemplate = new OpenOrderTemplate();
        otherTemplate.setId(2L);
        otherTemplate.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(otherTemplate);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Template cannot be updated");
        }

    }

    @Test
    public void updateOOQWithInvalidACCEPTEDStatus() {
        ThresholdInput thresholdInput = buildThreshold(1, 0, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderTemplate otherTemplate = new OpenOrderTemplate();
        otherTemplate.setId(2L);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);
        ooq.setStatus(OpenOrderQuoteStatusEnum.ACCEPTED);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Cannot update OpenOrderQuote with status : ACCEPTED");
        }

    }

    @Test
    public void updateOOQWithInvalidCANCELEDStatus() {
        ThresholdInput thresholdInput = buildThreshold(1, 0, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderTemplate otherTemplate = new OpenOrderTemplate();
        otherTemplate.setId(2L);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);
        ooq.setStatus(OpenOrderQuoteStatusEnum.CANCELED);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Cannot update OpenOrderQuote with status : CANCELED");
        }

    }

    @Test
    public void thresholdPercentageInvalidErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 0, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Invalid Threshold percentage '0'. Value must be between 1 and 100");
        }

    }

    @Test
    public void thresholdPercentageInvalidErr2() {
        ThresholdInput thresholdInput = buildThreshold(1, 101, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Invalid Threshold percentage '101'. Value must be between 1 and 100");
        }

    }

    @Test
    public void thresholdEmptyReceipientErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 85, null, "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Threshold Recipients must not be empty");
        }

    }

    @Test
    public void invalidProductTypeErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), Set.of("INAVLID"));

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrderQuote with type ARTICLES shall not have PRODUCTS");
        }

    }

    @Test
    public void invalidArticleContentErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), null, null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrderQuote with type ARTICLES must have at least one ARTICLES");
        }

    }

    @Test
    public void invalidProductContentErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.PRODUCTS, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), null, null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrderQuote with type PRODUCTS must have at least one PRODUCTS");
        }

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void invalidProductNotExitErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.PRODUCTS, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), null, Set.of("A"));

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderProductService.findByProductCodeAndTemplate(any(), any())).thenReturn(null);

        openOrderQuoteApi.update(1L, dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void invalidArticleNotExitErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderArticleService.findByArticleCodeAndTemplate(any(), any())).thenReturn(null);

        openOrderQuoteApi.update(1L, dto);

    }

    @Test
    public void invalidArticleTypeErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 1, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.PRODUCTS, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("INAVLID"), Set.of("A"));

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.ARTICLES);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrderQuote with type PRODUCTS shall not have ARTICLES");
        }

    }

    @Test
    public void invalidOpenOrderTypeErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
        OpenOrderQuoteDto dto = buildDto("OOQ-1", "BIL-ACC-1", "Description de OOQ test", "EXT-REF",
                OpenOrderTypeEnum.ARTICLES, "TMP-CODE-1", BigDecimal.valueOf(1000),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Set.of(thresholdInput), Set.of("TAG_A"), Set.of("A"), null);

        OpenOrderTemplate template = new OpenOrderTemplate();
        template.setId(1L);
        template.setCode("TMP-CODE-1");
        template.setStatus(OpenOrderTemplateStatusEnum.ACTIVE);
        template.setOpenOrderType(OpenOrderTypeEnum.PRODUCTS);

        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setId(1L);
        ooq.setOpenOrderTemplate(template);

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
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(ooq);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(openOrderTemplateService.findByCode(any())).thenReturn(template);
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(billingAccount);
        Mockito.when(tagService.findByCode(any())).thenReturn(tag);
        Mockito.when(openOrderArticleService.findByArticleCodeAndTemplate(any(), any())).thenReturn(ooa);
        doReturn("TU-OOQ").when(currentUser).getUserName();

        try {
            openOrderQuoteApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "OpenOrder type shall be the same as Template : given='ARTICLES' | template='PRODUCTS'");
        }

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void noOORSettingErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
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
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
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
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
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
    public void oorSettingAmountValidityDateErr() {
        ThresholdInput thresholdInput = buildThreshold(1, 100, List.of(ThresholdRecipientsEnum.CONSUMER), "test@oc.com");
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
        ooq.setQuoteNumber(UUID.randomUUID().toString());
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

    private ThresholdInput buildThreshold(Integer sequence, Integer percentage,
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


}