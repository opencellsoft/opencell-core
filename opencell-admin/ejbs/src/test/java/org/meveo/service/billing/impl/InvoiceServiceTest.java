package org.meveo.service.billing.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionGroup;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.tax.TaxClass;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;
import org.meveo.util.ApplicationProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

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

    @Mock
    private BillingAccountService billingAccountService;

    @Mock
    private TaxMappingService taxMappingService;

    @Mock
    @ApplicationProvider
    private Provider appProvider;

    @Mock
    @CurrentUser
    private MeveoUser currentUser;

    @Mock
    private ParamBeanFactory paramBeanFactory;

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
        InvoiceService.RatedTransactionsToInvoice ratedTransactionsToInvoice = invoiceService.getRatedTransactionGroups(subscription, ba, null, bc, invoiceType, null, null, null, false, paymentMethod);
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
        InvoiceService.RatedTransactionsToInvoice ratedTransactionsToInvoice = invoiceService.getRatedTransactionGroups(ba, ba, null, bc, invoiceType, null, null, null, false, paymentMethod);
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

        InvoiceService.RatedTransactionsToInvoice ratedTransactionsToInvoice = invoiceService.getRatedTransactionGroups(order, ba, new BillingRun(), bc, invoiceType, mock(Filter.class), mock(Date.class),
            mock(Date.class), false, paymentMethod);

        assertThat(ratedTransactionsToInvoice).isNotNull();
        Assert.assertEquals(ratedTransactionsToInvoice.ratedTransactionGroups.size(), 3);
        RatedTransactionGroup ratedTransactionGroup = ratedTransactionsToInvoice.ratedTransactionGroups.get(0);
        Assert.assertEquals(ratedTransactionGroup.getInvoiceKey().split("_").length, 5);
    }

    @Test
    public void test_appendInvoiceAggregates_withUserAccount() {

        Seller seller = new Seller();

        CustomerAccount ca = new CustomerAccount();
        ca.setId(1L);
        BillingAccount ba = new BillingAccount();
        ba.setCustomerAccount(ca);
        ba.setId(2L);

        TradingLanguage tradingLanguage = new TradingLanguage();
        tradingLanguage.setLanguageCode("en");
        tradingLanguage.setId(3L);

        ba.setTradingLanguage(tradingLanguage);

        List<RatedTransaction> rts = new ArrayList<RatedTransaction>();

        UserAccount ua1 = new UserAccount();
        WalletInstance wallet1 = new WalletInstance();
        wallet1.setId(5L);
        wallet1.setCode("wallet1");
        ua1.setCode("ua1");
        ua1.setWallet(wallet1);
        ua1.setBillingAccount(ba);
        ua1.setId(6L);

        UserAccount ua2 = new UserAccount();
        WalletInstance wallet2 = new WalletInstance();
        wallet2.setId(7L);
        wallet2.setCode("wallet2");

        ua2.setCode("ua2");
        ua2.setWallet(wallet2);
        ua2.setBillingAccount(ba);
        ua2.setId(8L);

        Subscription subscription1 = new Subscription();
        subscription1.setCode("subsc1");
        subscription1.setUserAccount(ua1);
        subscription1.setId(9L);

        Subscription subscription2 = new Subscription();
        subscription2.setCode("subsc2");
        subscription2.setUserAccount(ua1);
        subscription2.setId(10L);

        InvoiceCategory cat1 = new InvoiceCategory();
        cat1.setCode("cat1");
        cat1.setId(11L);

        InvoiceCategory cat2 = new InvoiceCategory();
        cat2.setCode("cat2");
        cat2.setId(12L);

        InvoiceSubCategory subCat11 = new InvoiceSubCategory();
        subCat11.setInvoiceCategory(cat1);
        subCat11.setCode("subCat11");
        subCat11.setId(13L);

        InvoiceSubCategory subCat12 = new InvoiceSubCategory();
        subCat12.setInvoiceCategory(cat1);
        subCat12.setCode("subCat12");
        subCat12.setId(14L);

        InvoiceSubCategory subCat21 = new InvoiceSubCategory();
        subCat21.setInvoiceCategory(cat2);
        subCat21.setCode("subCat21");
        subCat21.setId(15L);

        InvoiceSubCategory subCat22 = new InvoiceSubCategory();
        subCat22.setInvoiceCategory(cat2);
        subCat22.setCode("subCat22");
        subCat22.setId(16L);

        Tax tax = new Tax();
        tax.setId(17L);
        tax.setCode("tax1");
        tax.setPercent(new BigDecimal(15));

        TaxClass taxClass = new TaxClass();
        taxClass.setId(18L);

        AccountingCode accountingCode = new AccountingCode();
        accountingCode.setId(19L);

        InvoiceType invoiceType = new InvoiceType();
        invoiceType.setId(4L);

        RatedTransaction rt111 = new RatedTransaction(new Date(), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1), new BigDecimal(2), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1),
            RatedTransactionStatusEnum.OPEN, ua1.getWallet(), ba, ua1, subCat11, null, null, null, null, null, subscription1, null, null, null, null, null, "rt111", "RT111", new Date(), new Date(), seller, tax,
            tax.getPercent(), null, taxClass, accountingCode);
        rt111.setId(20L);
        rts.add(rt111);

        RatedTransaction rt112 = new RatedTransaction(new Date(), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1), new BigDecimal(2), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1),
            RatedTransactionStatusEnum.OPEN, ua1.getWallet(), ba, ua1, subCat12, null, null, null, null, null, subscription1, null, null, null, null, null, "rt112", "RT112", new Date(), new Date(), seller, tax,
            tax.getPercent(), null, taxClass, accountingCode);
        rt112.setId(21L);
        rts.add(rt112);

        RatedTransaction rt121 = new RatedTransaction(new Date(), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1), new BigDecimal(2), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1),
            RatedTransactionStatusEnum.OPEN, ua1.getWallet(), ba, ua1, subCat21, null, null, null, null, null, subscription1, null, null, null, null, null, "rt121", "RT121", new Date(), new Date(), seller, tax,
            tax.getPercent(), null, taxClass, accountingCode);
        rt121.setId(22L);
        rts.add(rt121);

        RatedTransaction rt122 = new RatedTransaction(new Date(), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1), new BigDecimal(2), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1),
            RatedTransactionStatusEnum.OPEN, ua1.getWallet(), ba, ua1, subCat22, null, null, null, null, null, subscription1, null, null, null, null, null, "rt122", "RT122", new Date(), new Date(), seller, tax,
            tax.getPercent(), null, taxClass, accountingCode);
        rt122.setId(23L);
        rts.add(rt122);

        RatedTransaction rt211 = new RatedTransaction(new Date(), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1), new BigDecimal(2), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1),
            RatedTransactionStatusEnum.OPEN, ua2.getWallet(), ba, ua2, subCat11, null, null, null, null, null, subscription2, null, null, null, null, null, "rt211", "RT211", new Date(), new Date(), seller, tax,
            tax.getPercent(), null, taxClass, accountingCode);
        rt211.setId(24L);
        rts.add(rt211);

        RatedTransaction rt212 = new RatedTransaction(new Date(), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1), new BigDecimal(2), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1),
            RatedTransactionStatusEnum.OPEN, ua2.getWallet(), ba, ua2, subCat12, null, null, null, null, null, subscription2, null, null, null, null, null, "rt212", "RT212", new Date(), new Date(), seller, tax,
            tax.getPercent(), null, taxClass, accountingCode);
        rt212.setId(25L);
        rts.add(rt212);

        RatedTransaction rt221 = new RatedTransaction(new Date(), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1), new BigDecimal(2), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1),
            RatedTransactionStatusEnum.OPEN, ua2.getWallet(), ba, ua2, subCat21, null, null, null, null, null, subscription2, null, null, null, null, null, "rt221", "RT221", new Date(), new Date(), seller, tax,
            tax.getPercent(), null, taxClass, accountingCode);
        rt221.setId(26L);
        rts.add(rt221);

        RatedTransaction rt222 = new RatedTransaction(new Date(), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1), new BigDecimal(2), new BigDecimal(15), new BigDecimal(16), new BigDecimal(1),
            RatedTransactionStatusEnum.OPEN, ua2.getWallet(), ba, ua2, subCat22, null, null, null, null, null, subscription2, null, null, null, null, null, "rt222", "RT222", new Date(), new Date(), seller, tax,
            tax.getPercent(), null, taxClass, accountingCode);
        rt222.setId(27L);
        rts.add(rt222);

        when(billingAccountService.isExonerated(any())).thenReturn(false);
        TaxInfo taxInfo = taxMappingService.new TaxInfo();
        taxInfo.tax = tax;
        taxInfo.taxClass = taxClass;

        when(taxMappingService.determineTax(any(), any(), any(), any(), any(), anyBoolean(), anyBoolean())).thenReturn(taxInfo);

        when(appProvider.getRoundingMode()).thenReturn(RoundingModeEnum.NEAREST);
        when(appProvider.getRounding()).thenReturn(6);
        when(appProvider.getInvoiceRoundingMode()).thenReturn(RoundingModeEnum.NEAREST);
        when(appProvider.getInvoiceRounding()).thenReturn(2);
        when(appProvider.isEntreprise()).thenReturn(true);

        ParamBean paramBean = mock(ParamBean.class);
        when(paramBean.getPropertyAsBoolean(eq("invoice.agregateByUA"), anyBoolean())).thenReturn(true);

        when(paramBeanFactory.getInstance()).thenReturn(paramBean);

        Invoice invoice = new Invoice();
        invoice.setInvoiceType(invoiceType);
        invoice.setBillingAccount(ba);
        invoiceService.appendInvoiceAgregates(ba, ba, invoice, rts, false, null, false);

        assertThat(invoice.getInvoiceAgregates().size()).isEqualTo(13);
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(0)).getInvoiceSubCategory().getCode()).isEqualTo("subCat11");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(0)).getUserAccount().getCode()).isEqualTo("ua1");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(0)).getWallet().getCode()).isEqualTo("wallet1");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(0)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat1");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(1)).getInvoiceSubCategory().getCode()).isEqualTo("subCat12");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(1)).getUserAccount().getCode()).isEqualTo("ua1");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(1)).getWallet().getCode()).isEqualTo("wallet1");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(1)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat1");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(2)).getInvoiceSubCategory().getCode()).isEqualTo("subCat21");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(2)).getUserAccount().getCode()).isEqualTo("ua1");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(2)).getWallet().getCode()).isEqualTo("wallet1");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(2)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat2");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(3)).getInvoiceSubCategory().getCode()).isEqualTo("subCat22");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(3)).getUserAccount().getCode()).isEqualTo("ua1");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(3)).getWallet().getCode()).isEqualTo("wallet1");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(3)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat2");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(4)).getInvoiceSubCategory().getCode()).isEqualTo("subCat11");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(4)).getUserAccount().getCode()).isEqualTo("ua2");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(4)).getWallet().getCode()).isEqualTo("wallet2");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(4)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat1");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(5)).getInvoiceSubCategory().getCode()).isEqualTo("subCat12");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(5)).getUserAccount().getCode()).isEqualTo("ua2");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(5)).getWallet().getCode()).isEqualTo("wallet2");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(5)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat1");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(6)).getInvoiceSubCategory().getCode()).isEqualTo("subCat21");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(6)).getUserAccount().getCode()).isEqualTo("ua2");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(6)).getWallet().getCode()).isEqualTo("wallet2");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(6)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat2");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(7)).getInvoiceSubCategory().getCode()).isEqualTo("subCat22");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(7)).getUserAccount().getCode()).isEqualTo("ua2");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(7)).getWallet().getCode()).isEqualTo("wallet2");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(7)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat2");

        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(8)).getInvoiceCategory().getCode()).isEqualTo("cat1");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(8)).getUserAccount().getCode()).isEqualTo("ua1");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(8)).getSubCategoryInvoiceAgregates().size()).isEqualTo(2);

        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(9)).getInvoiceCategory().getCode()).isEqualTo("cat2");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(9)).getUserAccount().getCode()).isEqualTo("ua1");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(9)).getSubCategoryInvoiceAgregates().size()).isEqualTo(2);

        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(10)).getInvoiceCategory().getCode()).isEqualTo("cat1");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(10)).getUserAccount().getCode()).isEqualTo("ua2");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(10)).getSubCategoryInvoiceAgregates().size()).isEqualTo(2);

        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(11)).getInvoiceCategory().getCode()).isEqualTo("cat2");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(11)).getUserAccount().getCode()).isEqualTo("ua2");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(11)).getSubCategoryInvoiceAgregates().size()).isEqualTo(2);

        assertThat(((TaxInvoiceAgregate) invoice.getInvoiceAgregates().get(12)).getTax().getCode()).isEqualTo("tax1");

        when(paramBean.getPropertyAsBoolean(eq("invoice.agregateByUA"), anyBoolean())).thenReturn(false);

        invoice = new Invoice();
        invoice.setInvoiceType(invoiceType);
        invoice.setBillingAccount(ba);
        invoiceService.appendInvoiceAgregates(ba, ba, invoice, rts, false, null, false);

        assertThat(invoice.getInvoiceAgregates().size()).isEqualTo(7);
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(0)).getInvoiceSubCategory().getCode()).isEqualTo("subCat11");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(0)).getUserAccount()).isNull();
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(0)).getWallet()).isNull();
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(0)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat1");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(1)).getInvoiceSubCategory().getCode()).isEqualTo("subCat12");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(1)).getUserAccount()).isNull();
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(1)).getWallet()).isNull();
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(1)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat1");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(2)).getInvoiceSubCategory().getCode()).isEqualTo("subCat21");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(2)).getUserAccount()).isNull();
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(2)).getWallet()).isNull();
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(2)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat2");

        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(3)).getInvoiceSubCategory().getCode()).isEqualTo("subCat22");
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(3)).getUserAccount()).isNull();
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(3)).getWallet()).isNull();
        assertThat(((SubCategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(3)).getCategoryInvoiceAgregate().getInvoiceCategory().getCode()).isEqualTo("cat2");

        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(4)).getInvoiceCategory().getCode()).isEqualTo("cat1");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(4)).getUserAccount()).isNull();
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(4)).getSubCategoryInvoiceAgregates().size()).isEqualTo(2);

        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(5)).getInvoiceCategory().getCode()).isEqualTo("cat2");
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(5)).getUserAccount()).isNull();
        assertThat(((CategoryInvoiceAgregate) invoice.getInvoiceAgregates().get(5)).getSubCategoryInvoiceAgregates().size()).isEqualTo(2);

        assertThat(((TaxInvoiceAgregate) invoice.getInvoiceAgregates().get(6)).getTax().getCode()).isEqualTo("tax1");
    }

}
