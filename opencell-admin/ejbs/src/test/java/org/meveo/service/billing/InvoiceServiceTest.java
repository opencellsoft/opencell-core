package org.meveo.service.billing;

import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.mockito.junit.MockitoJUnitRunner;

import javax.ejb.EJB;
import javax.inject.Inject;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceTest {

    @Mock
    private CustomerAccountService customerAccountService;

    /**
     * The billing account service.
     */
    @Mock
    private BillingAccountService billingAccountService;

    /**
     * The rated transaction service.
     */
    @Mock
    private RatedTransactionService ratedTransactionService;

    /**
     * The rejected billing account service.
     */
    @Mock
    private RejectedBillingAccountService rejectedBillingAccountService;

    /**
     * The invoice type service.
     */
    @Mock
    private InvoiceTypeService invoiceTypeService;

    /**
     * The order service.
     */
    @Mock
    private OrderService orderService;

    /**
     * The recorded invoice service.
     */
    @Mock
    private RecordedInvoiceService recordedInvoiceService;

    /**
     * The service singleton.
     */
    @Mock
    private ServiceSingleton serviceSingleton;

    @Mock
    private ScriptInstanceService scriptInstanceService;

    @Mock
    protected CustomFieldInstanceService customFieldInstanceService;

    @Mock
    private EmailSender emailSender;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private BillingRunService billingRunService;

    @Mock
    private InvoiceCategoryService invoiceCategoryService;

    @Mock
    private InvoiceSubCategoryService invoiceSubcategoryService;

    @Mock
    private TaxMappingService taxMappingService;

    @Mock
    private TaxScriptService taxScriptService;

    @Mock
    private TaxService taxService;

    @Mock
    private TaxClassService taxClassService;

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    @Mock
    private InvoiceService invoiceService;

    @Test
    public void createAgregatesAndInvoiceInNewTransactionTest() {

    }
}
