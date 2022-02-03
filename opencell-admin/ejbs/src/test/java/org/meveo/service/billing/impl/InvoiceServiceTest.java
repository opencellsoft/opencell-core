package org.meveo.service.billing.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionGroup;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.Provider;
import org.meveo.model.order.Order;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceTest {

    @Spy
    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private RatedTransactionService ratedTransactionService;

    @Mock
    private EntityManager entityManager;

    @Mock
    @ApplicationProvider
    protected Provider appProvider;

    @Mock
    private BillingAccountService billingAccountService;

    @Mock
    @CurrentUser
    protected MeveoUser currentUser;

    @Mock
    private CustomFieldInstanceService customFieldInstanceService;
    @Before
    public void setUp() {
        when(ratedTransactionService.listRTsToInvoice(any(), any(), any(), any(), anyInt())).thenAnswer(new Answer<List<RatedTransaction>>() {
            public List<RatedTransaction> answer(InvocationOnMock invocation) throws Throwable {
                List<RatedTransaction> ratedTransactions = new ArrayList<>();
                IBillableEntity entity = (IBillableEntity) invocation.getArguments()[1];
                RatedTransaction rt1 = getRatedTransaction(entity, 1l);
                RatedTransaction rt2 = getRatedTransaction(entity, 2l);
                RatedTransaction rt3 = getRatedTransaction(entity, 3l);
                ratedTransactions.add(rt1);
                ratedTransactions.add(rt2);
                ratedTransactions.add(rt3);
                return ratedTransactions;
            }
        });
        doReturn(entityManager).when(invoiceService).getEntityManager();

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
        rt.setAmountWithoutTax(BigDecimal.ZERO);
        rt.setAmountWithTax(BigDecimal.ZERO);
        rt.setAmountTax(BigDecimal.ZERO);
        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        invoiceSubCategory.setInvoiceCategory(mock(InvoiceCategory.class));
        rt.setInvoiceSubCategory(invoiceSubCategory);
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
        Order order = mock(Order.class);
        BillingAccount ba = mock(BillingAccount.class);
        BillingCycle bc = mock(BillingCycle.class);
        InvoiceType invoiceType = mock(InvoiceType.class);
        PaymentMethod paymentMethod = mock(PaymentMethod.class);

        InvoiceService.RatedTransactionsToInvoice ratedTransactionsToInvoice = invoiceService
                .getRatedTransactionGroups(order, ba, null, bc, invoiceType, null, null, null, false, paymentMethod);
        assertThat(ratedTransactionsToInvoice).isNotNull();
        Assert.assertEquals(ratedTransactionsToInvoice.ratedTransactionGroups.size(), 3);
        RatedTransactionGroup ratedTransactionGroup = ratedTransactionsToInvoice.ratedTransactionGroups.get(0);
        Assert.assertEquals(ratedTransactionGroup.getBillingAccount(), ba);
        Assert.assertEquals(ratedTransactionGroup.getInvoiceKey().split("_").length, 5);
    }

}
