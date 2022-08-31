package org.meveo.apiv2.ordering.ooq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.ordering.services.ooq.OpenOrderQuoteApi;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.ordering.OpenOrderArticle;
import org.meveo.model.ordering.OpenOrderProduct;
import org.meveo.model.ordering.OpenOrderQuote;
import org.meveo.model.ordering.OpenOrderQuoteStatusEnum;
import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.model.ordering.Threshold;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.order.OpenOrderQuoteService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class OpenOrderQuoteDuplicateApiTest {

    @InjectMocks
    private OpenOrderQuoteApi openOrderQuoteApi;
    @Mock
    private OpenOrderQuoteService openOrderQuoteService;
    @Mock
    private ServiceSingleton serviceSingleton;
    @Mock
    private MeveoUser currentUser;

    @Test
    public void duplicateNominal() {
        OpenOrderQuote quote = buildOOQ();

        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(quote);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(null);
        Mockito.when(serviceSingleton.getNextOpenOrderQuoteSequence()).thenReturn("OOT-NUMBER");
        doReturn("TU-OOQ").when(currentUser).getUserName();

        openOrderQuoteApi.duplicate(1L);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void duplicateOOQNotFound() {
        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(null);
        openOrderQuoteApi.duplicate(1L);

    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void duplicateOOQAlreadyExists() {
        OpenOrderQuote quote = buildOOQ();
        OpenOrderQuote existingOOQ = buildOOQ();
        existingOOQ.setId(-1L);

        Mockito.when(openOrderQuoteService.findById(any())).thenReturn(quote);
        Mockito.when(openOrderQuoteService.findByCode(any())).thenReturn(existingOOQ);

        openOrderQuoteApi.duplicate(1L);

    }

    // ************************************************************
    // ************************ TOOLS *****************************
    // ************************************************************

    private OpenOrderQuote buildOOQ() {
        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setCode("ABC");
        ooq.setStatus(OpenOrderQuoteStatusEnum.ACCEPTED);
        ooq.setDescription("Test OOQ");
        ooq.setActivationDate(Date.from(Instant.now().minus(Duration.ofDays(1))));
        ooq.setQuoteNumber("OOQ-22-000001");
        ooq.setId(1L);
        ooq.setEndOfValidityDate(Date.from(Instant.now().plus(Duration.ofDays(30))));
        ooq.setExternalReference("EXT-REF");
        ooq.setMaxAmount(BigDecimal.valueOf(1000000));


        OpenOrderTemplate tmpl = new OpenOrderTemplate();
        tmpl.setId(1L);
        ooq.setOpenOrderTemplate(tmpl);

        TradingCurrency cur = new TradingCurrency();
        cur.setId(1L);
        ooq.setCurrency(cur);

        BillingAccount billAcc = new BillingAccount();
        billAcc.setId(1L);
        ooq.setBillingAccount(billAcc);

        //List<Tag> tags = new
        Tag t = new Tag();
        t.setId(1L);
        ooq.setTags(List.of(t));

        OpenOrderProduct op = new OpenOrderProduct();
        op.setId(1L);
        op.setProduct(new Product());
        ooq.setProducts(List.of(op));

        OpenOrderArticle oa = new OpenOrderArticle();
        oa.setId(1L);
        oa.setAccountingArticle(new AccountingArticle());
        ooq.setArticles(List.of(oa));

        Threshold th = new Threshold();
        th.setId(1L);
        ooq.setThresholds(List.of(th));

        return ooq;
    }


}