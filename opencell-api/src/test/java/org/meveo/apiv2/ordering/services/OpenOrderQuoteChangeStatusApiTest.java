package org.meveo.apiv2.ordering.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.ordering.services.ooq.OpenOrderQuoteApi;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.ordering.OpenOrderArticle;
import org.meveo.model.ordering.OpenOrderProduct;
import org.meveo.model.ordering.OpenOrderQuote;
import org.meveo.model.ordering.OpenOrderQuoteStatusEnum;
import org.meveo.model.ordering.OpenOrderTypeEnum;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.service.order.OpenOrderQuoteService;
import org.meveo.service.order.OpenOrderService;
import org.meveo.service.settings.impl.OpenOrderSettingService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OpenOrderQuoteChangeStatusApiTest {

    @InjectMocks
    private OpenOrderQuoteApi openOrderQuoteApi;
    @Mock
    private OpenOrderQuoteService openOrderQuoteService;
    @Mock
    private OpenOrderSettingService openOrderSettingService;

    @Mock
    private OpenOrderService openOrderService;

    @Test(expected = EntityDoesNotExistsException.class)
    public void ooqFoundErr() {
        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.DRAFT);

    }

    @Test
    public void notSetting() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.ACCEPTED, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(null);

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.DRAFT);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "No Open Order setting found");
        }

    }

    @Test
    public void updateStatusToDRAFT() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.DRAFT, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.DRAFT);

    }

    @Test
    public void updateStatusToDRAFTErr() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.ACCEPTED, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.DRAFT);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Cannot change status 'ACCEPTED' to DRAFT");
        }

    }

    @Test
    public void updateStatusToWAITING_VALIDATION() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.DRAFT, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.WAITING_VALIDATION);

    }

    @Test
    public void updateStatusToWAITING_VALIDATIONErrStatus() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.ACCEPTED, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.WAITING_VALIDATION);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Open Order Quote status must be DRAFT or REJECTED");
        }

    }

    @Test
    public void updateStatusToWAITING_VALIDATIONErrSettings() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.ACCEPTED, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.WAITING_VALIDATION);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "ASK VALIDATION feature is not activated");
        }

    }

    @Test
    public void updateStatusToWAITING_VALIDATIONErrMissingArticles() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.DRAFT, null, buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.WAITING_VALIDATION);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Cannot ask validation for Open Order Quote without Articles");
        }

    }

    @Test
    public void updateStatusToWAITING_VALIDATIONErrMissingArticles2() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.DRAFT, new ArrayList<>(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.WAITING_VALIDATION);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Cannot ask validation for Open Order Quote without Articles");
        }

    }

    @Test
    public void updateStatusToWAITING_VALIDATIONErrMissingProducts() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.PRODUCTS, OpenOrderQuoteStatusEnum.DRAFT, buildArticles(), null);

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.WAITING_VALIDATION);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Cannot ask validation for Open Order Quote without Products");
        }

    }

    @Test
    public void updateStatusToWAITING_VALIDATIONErrMissingProducts2() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.PRODUCTS, OpenOrderQuoteStatusEnum.DRAFT, buildArticles(), new ArrayList<>());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.WAITING_VALIDATION);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Cannot ask validation for Open Order Quote without Products");
        }

    }

    @Test
    public void updateStatusToACCEPTED() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.SENT, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.ACCEPTED);

    }

    @Test
    public void updateStatusToACCEPTEDErrStatus() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.DRAFT, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.ACCEPTED);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Open Order Quote status must be SENT");
        }

    }

    @Test
    public void updateStatusToSENT() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.VALIDATED, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.SENT);

    }

    @Test
    public void updateStatusToSENTErrStatus() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.DRAFT, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.SENT);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Open Order Quote status must be VALIDATED");
        }

    }

    @Test
    public void updateStatusToSENTErrSettings() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.VALIDATED, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.SENT);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "ASK VALIDATION feature shall not be activated");
        }

    }

    @Test
    public void updateStatusToVALIDATED() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.WAITING_VALIDATION, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));
        Mockito.when(openOrderService.create(ooq)).thenReturn(new OpenOrder());

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.VALIDATED);

    }

    @Test
    public void updateStatusToVALIDATEDErrStatus() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.DRAFT, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));
        Mockito.when(openOrderService.create(ooq)).thenReturn(new OpenOrder());

       openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.VALIDATED);
    }

    @Test
    public void updateStatusToVALIDATEDErrSettings() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.WAITING_VALIDATION, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));
        Mockito.when(openOrderService.create(ooq)).thenReturn(new OpenOrder());

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.VALIDATED);
    }

    @Test
    public void updateStatusToREJECTED() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.WAITING_VALIDATION, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.REJECTED);

    }

    @Test
    public void updateStatusToREJECTEDErrStatus() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.DRAFT, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(true);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.REJECTED);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Open Order Quote status must be WAITING_VALIDATION");
        }

    }

    @Test
    public void updateStatusToREJECTEDErrSettings() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.WAITING_VALIDATION, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        try {
            openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.REJECTED);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "ASK VALIDATION feature is not activated");
        }

    }

    @Test
    public void updateStatusToCANCELED_State1() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.DRAFT, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.CANCELED);

    }

    @Test
    public void updateStatusToCANCELED_State2() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.SENT, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.CANCELED);

    }

    @Test
    public void updateStatusToCANCELED_State3() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.ACCEPTED, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.CANCELED);

    }

    @Test
    public void updateStatusToCANCELED_State4() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.CANCELED, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.CANCELED);

    }

    @Test
    public void updateStatusToCANCELED_State5() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.WAITING_VALIDATION, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.CANCELED);

    }

    @Test
    public void updateStatusToCANCELED_State6() {
        OpenOrderQuote ooq = buildOOQ(OpenOrderTypeEnum.ARTICLES, OpenOrderQuoteStatusEnum.REJECTED, buildArticles(), buildProducts());

        OpenOrderSetting setting = new OpenOrderSetting();
        setting.setUseManagmentValidationForOOQuotation(false);

        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(ooq);
        Mockito.when(openOrderSettingService.list()).thenReturn(List.of(setting));

        openOrderQuoteApi.changeStatus("OOQ", OpenOrderQuoteStatusEnum.CANCELED);

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
}