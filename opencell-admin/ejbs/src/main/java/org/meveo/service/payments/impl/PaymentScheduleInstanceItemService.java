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
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.invoice.CreateInvoiceResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.OperationTypeEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
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
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
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

    /** The invoice sub category service. */
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

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
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     * @throws Exception the exception
     */
    public void processItem(PaymentScheduleInstanceItem paymentScheduleInstanceItem) throws BusinessException {
        paymentScheduleInstanceItem = retrieveIfNotManaged(paymentScheduleInstanceItem);
        UserAccount userAccount = paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getUserAccount();
        BillingAccount billingAccount = userAccount.getBillingAccount();
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();
        InvoiceSubCategory invoiceSubCat = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getAdvancePaymentInvoiceSubCategory();
        BigDecimal amount = paymentScheduleInstanceItem.getPaymentScheduleInstance().getAmount();
        BigDecimal amounts[] = getAmounts(invoiceSubCat, amount, billingAccount.getTradingCountry(), userAccount);
        List<Long> aoIdsToPay = new ArrayList<Long>();
        Invoice invoice = null;
        RecordedInvoice recordedInvoicePS = null;
        PaymentMethod preferredMethod = customerAccount.getPreferredPaymentMethod();
        InvoiceType invoiceType = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getAdvancePaymentInvoiceType();
        if (preferredMethod == null) {
            throw new BusinessException("preferredMethod is null");
        }
        if (paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().isGenerateAdvancePaymentInvoice()) {
            InvoiceDto invoiceDto = new InvoiceDto();
            invoiceDto.setInvoiceType(invoiceType.getCode());
            invoiceDto.setBillingAccountCode(
                paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getUserAccount().getBillingAccount().getCode());
            invoiceDto.setInvoiceDate(new Date());
            invoiceDto.setDueDate(paymentScheduleInstanceItem.getDueDate());
            invoiceDto.setInvoiceMode(InvoiceModeEnum.AGGREGATED);

            SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();
            subCategoryInvoiceAgregateDto.setInvoiceSubCategoryCode(invoiceSubCat.getCode());
            subCategoryInvoiceAgregateDto.setDescription(invoiceSubCat.getDescription());
            subCategoryInvoiceAgregateDto.setAmountWithoutTax(amounts[0]);

            CategoryInvoiceAgregateDto categoryInvoiceAgregateDto = new CategoryInvoiceAgregateDto();
            categoryInvoiceAgregateDto.setCategoryInvoiceCode(invoiceSubCat.getInvoiceCategory().getCode());
            categoryInvoiceAgregateDto.setDescription(invoiceSubCat.getInvoiceCategory().getDescription());
            categoryInvoiceAgregateDto.getListSubCategoryInvoiceAgregateDto().add(subCategoryInvoiceAgregateDto);

            invoiceDto.getCategoryInvoiceAgregates().add(categoryInvoiceAgregateDto);

            CreateInvoiceResponseDto createInvoiceResponseDto = null;
            try {
                createInvoiceResponseDto = invoiceService.create(invoiceDto);
            } catch (MeveoApiException e) {
                throw new BusinessException(e.getMessage());
            }

            invoice = invoiceService.findById(createInvoiceResponseDto.getInvoiceId());
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
                applyOneShotPS(invoiceSubCat, paymentScheduleInstanceItem, amounts[0]);
            } catch (Exception e) {
                throw new BusinessException(e.getMessage());
            }
        }

        if (paymentScheduleInstanceItem.isLast()) {
            paymentScheduleInstanceItem.getPaymentScheduleInstance().setStatus(PaymentScheduleStatusEnum.DONE);
            paymentScheduleInstanceItem.getPaymentScheduleInstance().setStatusDate(new Date());
        }
    }

    private void applyOneShotPS(InvoiceSubCategory invoiceSubCat, PaymentScheduleInstanceItem paymentScheduleInstanceItem, BigDecimal amount) throws BusinessException {
        OneShotChargeTemplate oneShot = createOneShotCharge(invoiceSubCat, paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getPaymentLabel());

        oneShotChargeInstanceService.oneShotChargeApplication(paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription(), oneShot, null,
            new Date(), new BigDecimal("-" + amount), null, new BigDecimal(1), null, null, null, null, true);
    }

    /**
     * Creates the one shot charge.
     *
     * @param invoiceSubCategory the invoice sub category
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
     * @param customerAccount the customer account
     * @param amount the amount
     * @param doPaymentResponseDto the do payment response dto
     * @param paymentMethodType the payment method type
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
     * @param invoiceSubCategory the invoice sub category
     * @param amountWithTax the amount with tax
     * @param tradingCountry the trading country
     * @param userAccount the user account
     * @return the amounts
     * @throws BusinessException the business exception
     */
    private BigDecimal[] getAmounts(InvoiceSubCategory invoiceSubCategory, BigDecimal amountWithTax, TradingCountry tradingCountry, UserAccount userAccount)
            throws BusinessException {
        BigDecimal[] amounts = new BigDecimal[3];
        BigDecimal amountTax, amountWithoutTax;
        Integer rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();
        Tax tax = null;
        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(invoiceSubCategory, tradingCountry, new Date());
        if (invoiceSubcategoryCountry == null) {
            throw new BusinessException(
                "No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country=" + tradingCountry.getCountryCode());
        }
        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), userAccount, userAccount.getBillingAccount(), null);
        }
        if (tax == null) {
            throw new BusinessException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }
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
     * @param recordedInvoice
     */
    public void checkPaymentRecordInvoice(RecordedInvoice recordedInvoice) {
        // TODO Auto-generated method stub

    }

    public Long countPaidItems(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (Long) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.countPaidItems")
                .setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId()).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Long countIncomingItems(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (Long) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.countIncomingItems")
                .setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId()).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public BigDecimal sumAmountPaid(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (BigDecimal) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.amountPaidItems")
                .setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId()).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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