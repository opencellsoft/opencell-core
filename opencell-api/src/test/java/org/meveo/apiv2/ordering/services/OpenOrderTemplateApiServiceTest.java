package org.meveo.apiv2.ordering.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.ordering.resource.order.ImmutableOpenOrderTemplateInput;
import org.meveo.apiv2.ordering.resource.order.ImmutableThresholdInput;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.ordering.OpenOrderTypeEnum;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.order.OpenOrderTemplateService;
import org.meveo.service.order.ThresholdService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class OpenOrderTemplateApiServiceTest {
    @InjectMocks
    private OpenOrderTemplateApiService openOrderTemplateApiService;

    @Mock
    private ProductService productService;

    @Mock
    private OpenOrderTemplateService openOrderTemplateService;
    @Mock
    private AccountingArticleService accountingArticleService;

    @Test
    public void createOpenOrderTemplate_typeArticle_productExist()
    {
        OpenOrderTemplateInput input =
                ImmutableOpenOrderTemplateInput.builder()
                        .code("OOT1")
                        .templateName("some name")
                        .numberOfInstantiation(10)
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.ARTICLES)
                        .products(Collections.singleton("product 1"))

                        .build();
        doReturn(new Product()).when(productService).findByCode(any());
        doReturn(null).when(openOrderTemplateService).findByCode(any());
        try {
            openOrderTemplateApiService.create(input);
        } catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof BusinessApiException);
            Assert.assertEquals("Open order template of type ARTICLE can not be applied on products", ex.getMessage());
        }

    }

    @Test
    public void createOpenOrderTemplate_typeProduct_articleExist()
    {
        OpenOrderTemplateInput input =
                ImmutableOpenOrderTemplateInput.builder()
                        .code("OOT1")
                        .templateName("some name")
                        .numberOfInstantiation(10)
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.PRODUCTS)
                        .articles(Collections.singleton("article 1"))

                        .build();
        doReturn(new AccountingArticle()).when(accountingArticleService).findByCode(any());
        doReturn(null).when(openOrderTemplateService).findByCode(any());
        try {
            openOrderTemplateApiService.create(input);
        } catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof BusinessApiException);
            Assert.assertEquals("Open order template of type PRODUCT can not be applied on articles", ex.getMessage());
        }

    }

    @Test
    public void createOpenOrderTemplate_threshold_notPercentage()
    {
        OpenOrderTemplateInput input =
                ImmutableOpenOrderTemplateInput.builder()
                        .code("OOT1")
                        .templateName("some name")
                        .numberOfInstantiation(10)
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.ARTICLES)
                        .articles(Collections.singleton("article 1"))

                        .thresholds(Collections.singleton(
                                ImmutableThresholdInput.builder().sequence(1).percentage(-1).build()))

                        .build();
        doReturn(new AccountingArticle()).when(accountingArticleService).findByCode(any());
        doReturn(null).when(openOrderTemplateService).findByCode(any());

        try {
            openOrderTemplateApiService.create(input);
        } catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof BusinessApiException);
            Assert.assertEquals("Threshold should be between 1 and 100", ex.getMessage());
        }

    }

    @Test
    public void createOpenOrderTemplate_threshold_unordered()
    {
        OpenOrderTemplateInput input =
                ImmutableOpenOrderTemplateInput.builder()
                        .code("OOT1")
                        .templateName("some name")
                        .numberOfInstantiation(10)
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.ARTICLES)
                        .articles(Collections.singleton("article 1"))
                        .thresholds(Arrays.asList(
                                ImmutableThresholdInput.builder().sequence(1).percentage(99).build(),
                                ImmutableThresholdInput.builder().sequence(2).percentage(50).build()
                                ))

                        .build();
        doReturn(new AccountingArticle()).when(accountingArticleService).findByCode(any());
        doReturn(null).when(openOrderTemplateService).findByCode(any());

        try {
            openOrderTemplateApiService.create(input);
        } catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof BusinessApiException);
            Assert.assertEquals("Threshold sequence and percentage dosn’t match, threshold with high sequence number should contain the highest percentage", ex.getMessage());
        }

    }


}
