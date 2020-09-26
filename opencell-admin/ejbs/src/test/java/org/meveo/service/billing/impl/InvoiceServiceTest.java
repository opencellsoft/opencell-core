package org.meveo.service.billing.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.*;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.RejectedBillingAccountService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.billing.TaxScriptService;
import org.meveo.service.tax.TaxClassService;
import org.meveo.service.tax.TaxMappingService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private RatedTransactionService ratedTransactionService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityManagerWrapper emWrapper;

    @Before
    public void setUp() {
        when(ratedTransactionService.listRTsToInvoice(any(), any(), any(), any(), anyInt())).thenAnswer(new Answer<List<RatedTransaction>>() {
            public List<RatedTransaction> answer(InvocationOnMock invocation) throws Throwable {
                List<RatedTransaction> ratedTransactions = new ArrayList<>();
                IBillableEntity entity = (IBillableEntity) invocation.getArguments()[0];
                RatedTransaction rt1 = getRatedTransaction(entity, 1l);
                RatedTransaction rt2 = getRatedTransaction(entity, 2l);
                RatedTransaction rt3 = getRatedTransaction(entity, 3l);
                ratedTransactions.add(rt1);
                ratedTransactions.add(rt2);
                ratedTransactions.add(rt3);
                return ratedTransactions;
            }
        });

        when(emWrapper.getEntityManager()).thenReturn(entityManager);

    }

    private RatedTransaction getRatedTransaction(IBillableEntity entity, long sellerId) {
        RatedTransaction rt = new RatedTransaction();
        if (entity instanceof Subscription) {
            rt.setSubscription((Subscription) entity);
        } else if (entity instanceof BillingAccount) {
            rt.setBillingAccount((BillingAccount) entity);
        } else if (entity instanceof Order) {
            rt.setOrderNumber(((Order) entity).getOrderNumber());

        }
        rt.setBillingAccount(mock(BillingAccount.class));
        Seller seller = new Seller();
        seller.setId(sellerId);
        rt.setSeller(seller);
        return rt;
    }

    @Test
    public void test_getRatedTransactionGroups_EntityToInvoice_Subscription() {
        Subscription subscription = mock(Subscription.class);
        BillingAccount ba = mock(BillingAccount.class);
        BillingCycle bc = mock(BillingCycle.class);
        InvoiceType invoiceType = mock(InvoiceType.class);
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        InvoiceService.RatedTransactionsToInvoice ratedTransactionsToInvoice = invoiceService
                .getRatedTransactionGroups(subscription, ba, null, bc, invoiceType, null, null, null, false, paymentMethod);
        assertThat(ratedTransactionsToInvoice).isNotNull();
        Assert.assertEquals(ratedTransactionsToInvoice.ratedTransactionGroups.size(), 3);
        RatedTransactionGroup ratedTransactionGroup = ratedTransactionsToInvoice.ratedTransactionGroups.get(0);
        Assert.assertEquals(ratedTransactionGroup.getBillingAccount(), ba);
        Assert.assertEquals(ratedTransactionGroup.getInvoiceKey().split("_").length, 5);
    }

    @Test
    public void test_getRatedTransactionGroups_EntityToInvoice_BillingAccount() {
        BillingAccount ba = mock(BillingAccount.class);
        BillingCycle bc = mock(BillingCycle.class);
        InvoiceType invoiceType = mock(InvoiceType.class);
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        InvoiceService.RatedTransactionsToInvoice ratedTransactionsToInvoice = invoiceService
                .getRatedTransactionGroups(ba, ba, null, bc, invoiceType, null, null, null, false, paymentMethod);
        assertThat(ratedTransactionsToInvoice).isNotNull();
        Assert.assertEquals(ratedTransactionsToInvoice.ratedTransactionGroups.size(), 3);
        RatedTransactionGroup ratedTransactionGroup = ratedTransactionsToInvoice.ratedTransactionGroups.get(0);
        Assert.assertEquals(ratedTransactionGroup.getBillingAccount(), ba);
        Assert.assertEquals(ratedTransactionGroup.getInvoiceKey().split("_").length, 5);
    }

    @Test
    public void test_getRatedTransactionGroups_EntityToInvoice_Order() {
        Order order = new Order();
        BillingAccount ba = new BillingAccount();
        ba.setId(1L);
        BillingCycle bc = new BillingCycle();
        InvoiceType invoiceType = new InvoiceType();
        PaymentMethod paymentMethod = new CardPaymentMethod();

        InvoiceService.RatedTransactionsToInvoice ratedTransactionsToInvoice = invoiceService.getRatedTransactionGroups(order, ba, new BillingRun(), bc, invoiceType, mock(Filter.class), mock(Date.class), mock(Date.class), false, paymentMethod);

        assertThat(ratedTransactionsToInvoice).isNotNull();
        Assert.assertEquals(ratedTransactionsToInvoice.ratedTransactionGroups.size(), 3);
        RatedTransactionGroup ratedTransactionGroup = ratedTransactionsToInvoice.ratedTransactionGroups.get(0);
        Assert.assertEquals(ratedTransactionGroup.getInvoiceKey().split("_").length, 5);
    }
}
