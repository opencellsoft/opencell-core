/**
 * 
 */
package org.meveo.service.payments.impl;

import static org.meveo.commons.utils.NumberUtils.round;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.OperationTypeEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.PaymentScheduleStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.util.ApplicationProvider;

/**
 * The Class PaymentScheduleInstanceItemService.
 *
 * @author anasseh
 * @since 5.2
 */
@Stateless
public class PaymentScheduleInstanceItemService extends PersistenceService<PaymentScheduleInstanceItem> {

    /** The invoice service. */
    @Inject
    private InvoiceService invoiceService;

    /** The payment service. */
    @Inject
    private PaymentService paymentService;

    /** The o CC template service. */
    @Inject
    private OCCTemplateService oCCTemplateService;

    /** The one shot charge template service. */
    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    /** The one shot charge instance service. */
    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    /** The recorded invoice service. */
    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    /** The invoice sub category country service. */
    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    /** The Constant HUNDRED. */
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    /** The app provider. */
    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    /**
     * Gets the items to process.
     *
     * @param processingDate the processing date
     * @return the items to process
     */
    @SuppressWarnings("unchecked")
    public List<PaymentScheduleInstanceItem> getItemsToProcess(Date processingDate) {
        try {
            return (List<PaymentScheduleInstanceItem>) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.listItemsToProcess")
                .setParameter("requestPaymentDateIN", processingDate).getResultList();
        } catch (Exception e) {
            return new ArrayList<PaymentScheduleInstanceItem>();
        }
    }

    /**
     * Process item.
     *
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @throws BusinessException the business exception
     */
    public void processItem(PaymentScheduleInstanceItem paymentScheduleInstanceItem) throws BusinessException {
        paymentScheduleInstanceItem = retrieveIfNotManaged(paymentScheduleInstanceItem);
        UserAccount userAccount = paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getUserAccount();
        BillingAccount billingAccount = userAccount.getBillingAccount();
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();
        InvoiceSubCategory invoiceSubCat = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getAdvancePaymentInvoiceSubCategory();
        BigDecimal amount = paymentScheduleInstanceItem.getPaymentScheduleInstance().getAmount();
        Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCat,
            paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getSeller(), userAccount.getBillingAccount(), new Date(), false);
        if (tax == null) {
            throw new BusinessException("Cant found tax for invoiceSubCat:" + invoiceSubCat.getCode());
        }
        BigDecimal amounts[] = getAmounts(amount, tax);
        List<Long> aoIdsToPay = new ArrayList<Long>();
        Invoice invoice = null;
        RecordedInvoice recordedInvoicePS = null;
        PaymentMethod preferredMethod = customerAccount.getPreferredPaymentMethod();
        InvoiceType invoiceType = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getAdvancePaymentInvoiceType();
        if (preferredMethod == null) {
            throw new BusinessException("preferredMethod is null");
        }

        if (paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().isGenerateAdvancePaymentInvoice()) {
            invoice = new Invoice();
            invoice.setInvoiceType(invoiceType);
            invoice.setBillingAccount(paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getUserAccount().getBillingAccount());
            invoice.setInvoiceDate(new Date());
            invoice.setDueDate(paymentScheduleInstanceItem.getDueDate());
            invoice.setSeller(paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getSeller());

            SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
            subCategoryInvoiceAgregate.setAuditable(new Auditable(currentUser));
            subCategoryInvoiceAgregate.setInvoiceSubCategory(invoiceSubCat);
            subCategoryInvoiceAgregate.setDescription(invoiceSubCat.getDescription());
            subCategoryInvoiceAgregate.setAmountWithoutTax(amounts[0]);
            subCategoryInvoiceAgregate.setAmountWithTax(amounts[2]);
            subCategoryInvoiceAgregate.setAmountTax(amounts[1]);
            subCategoryInvoiceAgregate.setQuantity(BigDecimal.ONE);
            subCategoryInvoiceAgregate.setWallet(userAccount.getWallet());
            subCategoryInvoiceAgregate.setAccountingCode(invoiceSubCat.getAccountingCode());
            subCategoryInvoiceAgregate.setBillingAccount(userAccount.getBillingAccount());
            subCategoryInvoiceAgregate.setUserAccount(userAccount);
            subCategoryInvoiceAgregate.setItemNumber(1);
            subCategoryInvoiceAgregate.setInvoice(invoice);

            TaxInvoiceAgregate invoiceAgregateTax = new TaxInvoiceAgregate();
            invoiceAgregateTax.setAuditable(new Auditable(currentUser));
            invoiceAgregateTax.setInvoice(invoice);
            invoiceAgregateTax.setTax(tax);
            invoiceAgregateTax.setTaxPercent(tax.getPercent());
            invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());
            invoiceAgregateTax.setAmountWithoutTax(amounts[0]);
            invoiceAgregateTax.setAmountWithTax(amounts[2]);
            invoiceAgregateTax.setAmountTax(amounts[1]);

            CategoryInvoiceAgregate categoryInvoiceAgregate = new CategoryInvoiceAgregate();
            categoryInvoiceAgregate.setAuditable(new Auditable(currentUser));
            categoryInvoiceAgregate.setInvoiceCategory(invoiceSubCat.getInvoiceCategory());
            categoryInvoiceAgregate.setDescription(invoiceSubCat.getInvoiceCategory().getDescription());
            categoryInvoiceAgregate.addSubCategoryInvoiceAggregate(subCategoryInvoiceAgregate);
            categoryInvoiceAgregate.setInvoice(invoice);
            categoryInvoiceAgregate.setAmountWithoutTax(amounts[0]);
            categoryInvoiceAgregate.setAmountWithTax(amounts[2]);
            categoryInvoiceAgregate.setAmountTax(amounts[1]);

            subCategoryInvoiceAgregate.setCategoryInvoiceAgregate(categoryInvoiceAgregate);

            invoice.getInvoiceAgregates().add(categoryInvoiceAgregate);
            invoice.getInvoiceAgregates().add(subCategoryInvoiceAgregate);

            invoice.setAmountWithoutTax(amounts[0]);
            invoice.setAmountTax(amounts[1]);
            invoice.setAmountWithTax(amounts[2]);
            invoice.setNetToPay(amounts[2]);

            invoiceService.assignInvoiceNumber(invoice);
            invoiceService.create(invoice);
            invoiceService.postCreate(invoice);

            paymentScheduleInstanceItem.setInvoice(invoice);
        }
        recordedInvoicePS = createRecordedInvoicePS(amounts, customerAccount, invoiceType, preferredMethod.getPaymentType(), invoice, aoIdsToPay, paymentScheduleInstanceItem);
        aoIdsToPay.add(recordedInvoicePS.getId());

        paymentScheduleInstanceItem.setRecordedInvoice(recordedInvoicePS);
        if (paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().isDoPayment()) {
            try {
                if (preferredMethod.getPaymentType() == PaymentMethodEnum.CARD) {
                    paymentService.payByCardToken(customerAccount, (amount.multiply(new BigDecimal(100))).longValue(), aoIdsToPay, true, true, null);
                } else if (preferredMethod.getPaymentType() == PaymentMethodEnum.DIRECTDEBIT) {
                    paymentService.payByMandat(customerAccount, (amount.multiply(new BigDecimal(100))).longValue(), aoIdsToPay, true, true, null);
                } else {
                    throw new BusinessException("Payment method " + preferredMethod.getPaymentType() + " not allowed");
                }
            } catch (Exception e) {
                throw new BusinessException(e.getMessage());
            }
        }

        if (paymentScheduleInstanceItem.isLast()) {
            paymentScheduleInstanceItem.getPaymentScheduleInstance().setStatus(PaymentScheduleStatusEnum.DONE);
            paymentScheduleInstanceItem.getPaymentScheduleInstance().setStatusDate(new Date());
        }
    }

    /**
     * Apply one shot reject PS.
     *
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @throws BusinessException the business exception
     */
    public void applyOneShotRejectPS(PaymentScheduleInstanceItem paymentScheduleInstanceItem) throws BusinessException {
        applyOneShotPS(paymentScheduleInstanceItem, true);
    }

    /**
     * Apply one shot PS.
     *
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @param isPaymentRejected the is payment rejected
     * @throws BusinessException the business exception
     */
    private void applyOneShotPS(PaymentScheduleInstanceItem paymentScheduleInstanceItem, boolean isPaymentRejected) throws BusinessException {
        UserAccount userAccount = paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getUserAccount();
        InvoiceSubCategory invoiceSubCat = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getAdvancePaymentInvoiceSubCategory();
        Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCat,
            paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getSeller(), userAccount.getBillingAccount(), new Date(), false);
        if (tax == null) {
            throw new BusinessException("applyOneShotPS: cant found tax for invoiceSubCat:" + invoiceSubCat.getCode());
        }
        BigDecimal amounts[] = getAmounts(paymentScheduleInstanceItem.getPaymentScheduleInstance().getAmount(), tax);
        String paymentlabel = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getPaymentLabel();
        OneShotChargeTemplate oneShot = createOneShotCharge(invoiceSubCat, paymentlabel);

        oneShotChargeInstanceService.oneShotChargeApplication(paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription(), oneShot, null,
            new Date(), new BigDecimal((isPaymentRejected ? "" : "-") + amounts[0]), null, new BigDecimal(1), null, null, null,
            paymentlabel + (isPaymentRejected ? " (Rejected)" : ""), null, true);
    }

    /**
     * Apply one shot PS.
     *
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @throws BusinessException the business exception
     */
    public void applyOneShotPS(PaymentScheduleInstanceItem paymentScheduleInstanceItem) throws BusinessException {
        applyOneShotPS(paymentScheduleInstanceItem, false);
    }

    /**
     * Creates the one shot charge.
     *
     * @param invoiceSubCategory the invoice sub category
     * @param paymentLabel the payment label
     * @return the one shot charge template
     * @throws BusinessException the business exception
     */
    private OneShotChargeTemplate createOneShotCharge(InvoiceSubCategory invoiceSubCategory, String paymentLabel) throws BusinessException {
        OneShotChargeTemplate oneShot = oneShotChargeTemplateService.findByCode("ADV_PAYMENT");
        if (oneShot == null) {
            oneShot = new OneShotChargeTemplate();
            oneShot.setCode("ADV_PAYMENT");
            oneShot.setDescription(paymentLabel);
            oneShot.setInvoiceSubCategory(invoiceSubCategory);
            oneShot.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.OTHER);
            oneShot.setType(OperationTypeEnum.CREDIT);
            oneShot.setAmountEditable(Boolean.TRUE);
            oneShotChargeTemplateService.create(oneShot);
        }
        return oneShot;
    }

    /**
     * Creates the PS AO.
     *
     * @param amounts the amounts
     * @param customerAccount the customer account
     * @param invoiceType the invoice type
     * @param paymentMethodType the payment method type
     * @param invoice the invoice
     * @param aoIdsToPay the ao ids to pay
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @return the account operation PS
     * @throws BusinessException the business exception
     */
    public RecordedInvoice createRecordedInvoicePS(BigDecimal amounts[], CustomerAccount customerAccount, InvoiceType invoiceType, PaymentMethodEnum paymentMethodType,
            Invoice invoice, List<Long> aoIdsToPay, PaymentScheduleInstanceItem paymentScheduleInstanceItem) throws BusinessException {
        OCCTemplate occTemplate = oCCTemplateService.getOccTemplateFromInvoiceType(amounts[2], invoiceType, null, null);
        RecordedInvoice recordedInvoicePS = new RecordedInvoice();
        recordedInvoicePS.setDueDate(paymentScheduleInstanceItem.getDueDate());
        recordedInvoicePS.setPaymentMethod(paymentMethodType);
        recordedInvoicePS.setAmount(amounts[2]);
        recordedInvoicePS.setUnMatchingAmount(recordedInvoicePS.getAmount());
        recordedInvoicePS.setMatchingAmount(BigDecimal.ZERO);
        recordedInvoicePS.setAccountingCode(occTemplate.getAccountingCode());
        recordedInvoicePS.setOccCode(occTemplate.getCode());
        recordedInvoicePS.setOccDescription(occTemplate.getDescription());
        recordedInvoicePS.setTransactionCategory(occTemplate.getOccCategory());
        recordedInvoicePS.setCustomerAccount(customerAccount);
        recordedInvoicePS.setReference(invoice == null ? "psItemID:" + paymentScheduleInstanceItem.getId() : invoice.getInvoiceNumber());
        recordedInvoicePS.setTransactionDate(new Date());
        recordedInvoicePS.setMatchingStatus(MatchingStatusEnum.O);
        recordedInvoicePS.setTaxAmount(amounts[1]);
        recordedInvoicePS.setAmountWithoutTax(amounts[0]);
        recordedInvoicePS.setPaymentScheduleInstanceItem(paymentScheduleInstanceItem);
        recordedInvoiceService.create(recordedInvoicePS);
        return recordedInvoicePS;

    }

    /**
     * Gets the amount tax and amount without tax from amoutWithTax for the right tax application.
     *
     * @param amountWithTax the amount with tax
     * @param tax the tax
     * @return the amounts
     * @throws BusinessException the business exception
     */
    private BigDecimal[] getAmounts(BigDecimal amountWithTax, Tax tax) throws BusinessException {
        BigDecimal[] amounts = new BigDecimal[3];
        BigDecimal amountTax, amountWithoutTax;
        Integer rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();

        BigDecimal percentPlusOne = BigDecimal.ONE.add(tax.getPercent().divide(HUNDRED, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
        amountTax = amountWithTax.subtract(amountWithTax.divide(percentPlusOne, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
        if (rounding != null && rounding > 0) {
            amountTax = round(amountTax, rounding, roundingMode);
        }
        amountWithoutTax = amountWithTax.subtract(amountTax);
        amounts[0] = amountWithoutTax;
        amounts[1] = amountTax;
        amounts[2] = amountWithTax;
        return amounts;
    }

    /**
     * Check payment record invoice.
     *
     * @param recordedInvoice the recorded invoice
     */
    public void checkPaymentRecordInvoice(RecordedInvoice recordedInvoice) {
        // TODO Auto-generated method stub

    }

    /**
     * Count paid items.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @return the long
     */
    public Long countPaidItems(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (Long) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.countPaidItems")
                .setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId()).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Count incoming items.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @return the long
     */
    public Long countIncomingItems(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (Long) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.countIncomingItems")
                .setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId()).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sum amount paid.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @return the big decimal
     */
    public BigDecimal sumAmountPaid(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (BigDecimal) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.amountPaidItems")
                .setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId()).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sum amount incoming.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @return the big decimal
     */
    public BigDecimal sumAmountIncoming(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (BigDecimal) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.amountIncomingItems")
                .setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId()).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}