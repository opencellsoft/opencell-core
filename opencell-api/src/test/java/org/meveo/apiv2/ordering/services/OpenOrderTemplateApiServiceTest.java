package org.meveo.apiv2.ordering.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.ordering.resource.order.ImmutableOpenOrderTemplateInput;
import org.meveo.apiv2.ordering.resource.order.ImmutableThresholdInput;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.ordering.OpenOrderArticle;
import org.meveo.model.ordering.OpenOrderProduct;
import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.model.ordering.OpenOrderTypeEnum;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.order.OpenOrderTemplateService;
import org.meveo.service.order.ThresholdService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OpenOrderTemplateApiServiceTest {
    @InjectMocks
    private OpenOrderTemplateApiService openOrderTemplateApiService;

    @Mock
    private ProductService productService;
    
    @Mock
    private ThresholdService thresholdService;

    @Mock
    private OpenOrderTemplateService openOrderTemplateService;
    @Mock
    private AccountingArticleService accountingArticleService;
    @Mock
    private MeveoUser currentUser;
    
    @Before
    public void setUp() {
    	
    }

    @Test
    public void createOpenOrderTemplate_typeArticle_productExist()
    {
        OpenOrderTemplateInput input =
                ImmutableOpenOrderTemplateInput.builder()
                        .code("OOT1")
                        .templateName("some name")
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.ARTICLES)
                        .products(Collections.singleton("product 1"))

                        .build();
        doReturn(new Product()).when(productService).findByCode(any());
        doReturn(null).when(openOrderTemplateService).findByCode(any());
        doReturn("TU-OOT").when(currentUser).getUserName();
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
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.PRODUCTS)
                        .articles(Collections.singleton("article 1"))

                        .build();
        doReturn(new AccountingArticle()).when(accountingArticleService).findByCode(any());
        doReturn(null).when(openOrderTemplateService).findByCode(any());
        doReturn("TU-OOT").when(currentUser).getUserName();
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
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.ARTICLES)
                        .articles(Collections.singleton("article 1"))

                        .thresholds(Collections.singleton(
                                ImmutableThresholdInput.builder().sequence(1).percentage(-1).externalRecipient("example").build()))

                        .build();
        doReturn(new AccountingArticle()).when(accountingArticleService).findByCode(any());
        doReturn(null).when(openOrderTemplateService).findByCode(any());
        doReturn("TU-OOT").when(currentUser).getUserName();

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
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.ARTICLES)
                        .articles(Collections.singleton("article 1"))
                        .thresholds(Arrays.asList(
                                ImmutableThresholdInput.builder().sequence(1).percentage(99).externalRecipient("example").build(),
                                ImmutableThresholdInput.builder().sequence(2).percentage(50).externalRecipient("example").build()
                                ))

                        .build();
        doReturn(new AccountingArticle()).when(accountingArticleService).findByCode(any());
        doReturn(null).when(openOrderTemplateService).findByCode(any());
        doReturn("TU-OOT").when(currentUser).getUserName();

        try {
            openOrderTemplateApiService.create(input);
        } catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof BusinessApiException);
            Assert.assertEquals("Threshold sequence and percentage dosn’t match, threshold with high sequence number should contain the highest percentage", ex.getMessage());
        }

    }
    
    @Test
    public void updateOpenOrderTemplate_typeArticle_productExist()
    {
        OpenOrderTemplateInput input =
                ImmutableOpenOrderTemplateInput.builder()
                        .code("OOT1")
                        .templateName("some name")
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.ARTICLES)
                        .products(Collections.singleton("product 1"))

                        .build();

        
        doNothing().when(thresholdService).deleteThresholdsByOpenOrderTemplateId(any());
        doReturn(new Product()).when(productService).findByCode(any());
        OpenOrderTemplate openOrderTemplate = new OpenOrderTemplate();
    	openOrderTemplate.setProducts(new ArrayList<OpenOrderProduct>());
    	openOrderTemplate.setArticles(new ArrayList<OpenOrderArticle>());
        doReturn(openOrderTemplate).when(openOrderTemplateService).findByCode(any());
        doReturn("TU-OOT").when(currentUser).getUserName();
        try {
            openOrderTemplateApiService.update("some name", input);
        } catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof BusinessApiException);
            Assert.assertEquals("Open order template of type ARTICLE can not be applied on products", ex.getMessage());
        }

    }
    
    @Test
    public void updateOpenOrderTemplate_typeProduct_articleExist()
    {
        OpenOrderTemplateInput input =
                ImmutableOpenOrderTemplateInput.builder()
                        .code("OOT1")
                        .templateName("some name")
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.PRODUCTS)
                        .articles(Collections.singleton("article 1"))

                        .build();
        doReturn(new AccountingArticle()).when(accountingArticleService).findByCode(any());
        doReturn("TU-OOT").when(currentUser).getUserName();
        OpenOrderTemplate openOrderTemplate = new OpenOrderTemplate();
    	openOrderTemplate.setProducts(new ArrayList<OpenOrderProduct>());
    	openOrderTemplate.setArticles(new ArrayList<OpenOrderArticle>());
        doReturn(openOrderTemplate).when(openOrderTemplateService).findByCode(any());
        doNothing().when(thresholdService).deleteThresholdsByOpenOrderTemplateId(any());
        try {
            openOrderTemplateApiService.update("some name",input);
        } catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof BusinessApiException);
            Assert.assertEquals("Open order template of type PRODUCT can not be applied on articles", ex.getMessage());
        }
    }
    
    @Test
    public void updateOpenOrderTemplate_threshold_notPercentage()
    {
        OpenOrderTemplateInput input =
                ImmutableOpenOrderTemplateInput.builder()
                        .code("OOT1")
                        .templateName("some name")
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.ARTICLES)
                        .articles(Collections.singleton("article 1"))

                        .thresholds(Collections.singleton(
                                ImmutableThresholdInput.builder().sequence(1).percentage(-1).externalRecipient("example").build()))

                        .build();
        doReturn(new AccountingArticle()).when(accountingArticleService).findByCode(any());
        doReturn("TU-OOT").when(currentUser).getUserName();
        OpenOrderTemplate openOrderTemplate = new OpenOrderTemplate();
    	openOrderTemplate.setProducts(new ArrayList<OpenOrderProduct>());
    	openOrderTemplate.setArticles(new ArrayList<OpenOrderArticle>());
        doReturn(openOrderTemplate).when(openOrderTemplateService).findByCode(any());
        doNothing().when(thresholdService).deleteThresholdsByOpenOrderTemplateId(any());

        try {
            openOrderTemplateApiService.update("some name",input);
        } catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof BusinessApiException);
            Assert.assertEquals("Threshold should be between 1 and 100", ex.getMessage());
        }

    }

    @Test
    public void updateOpenOrderTemplate_threshold_unordered()
    {
        OpenOrderTemplateInput input =
                ImmutableOpenOrderTemplateInput.builder()
                        .code("OOT1")
                        .templateName("some name")
                        .description("desc")
                        .openOrderType(OpenOrderTypeEnum.ARTICLES)
                        .articles(Collections.singleton("article 1"))
                        .thresholds(Arrays.asList(
                                ImmutableThresholdInput.builder().sequence(1).percentage(99).externalRecipient("example").build(),
                                ImmutableThresholdInput.builder().sequence(2).percentage(50).externalRecipient("example").build()
                                ))

                        .build();
        doReturn(new AccountingArticle()).when(accountingArticleService).findByCode(any());
        doReturn("TU-OOT").when(currentUser).getUserName();
        OpenOrderTemplate openOrderTemplate = new OpenOrderTemplate();
    	openOrderTemplate.setProducts(new ArrayList<OpenOrderProduct>());
    	openOrderTemplate.setArticles(new ArrayList<OpenOrderArticle>());
        doReturn(openOrderTemplate).when(openOrderTemplateService).findByCode(any());
        doNothing().when(thresholdService).deleteThresholdsByOpenOrderTemplateId(any());

        try {
            openOrderTemplateApiService.update("some name",input);
        } catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof BusinessApiException);
            Assert.assertEquals("Threshold sequence and percentage dosn’t match, threshold with high sequence number should contain the highest percentage", ex.getMessage());
        }

    }

}