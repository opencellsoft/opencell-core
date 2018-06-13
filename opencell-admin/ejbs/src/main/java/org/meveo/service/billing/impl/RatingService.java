package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.ws.rs.core.Response;

import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.exception.UnrolledbackBusinessException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.LevelEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.ProductOfferingService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

/**
 * Rate charges such as {@link org.meveo.model.catalog.OneShotChargeTemplate}, {@link org.meveo.model.catalog.RecurringChargeTemplate} and
 * {@link org.meveo.model.catalog.UsageChargeTemplate}. Generate the {@link org.meveo.model.billing.WalletOperation} with the appropriate values.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0.1
 */
@Stateless
public class RatingService extends BusinessService<WalletOperation> {

    @Inject
    private EdrService edrService;

    @EJB
    private SubscriptionService subscriptionService;

    @EJB
    private RatedTransactionService ratedTransactionService;

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    @Inject
    private AccessService accessService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private MeveoInstanceService meveoInstanceService;

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private ProductOfferingService productOfferingService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    /**
     * @param level level enum
     * @param chargeCode charge's code
     * @param chargeDate charge's date
     * @param recChargeInstance reccurring charge instance
     * @return shared quantity
     */
    public int getSharedQuantity(LevelEnum level, String chargeCode, Date chargeDate, RecurringChargeInstance recChargeInstance) {
        int result = 0;
        try {
            String strQuery = "select SUM(r.serviceInstance.quantity) from " + RecurringChargeInstance.class.getSimpleName() + " r " + "WHERE r.code=:chargeCode "
                    + "AND r.subscriptionDate<=:chargeDate " + "AND (r.serviceInstance.terminationDate is NULL OR r.serviceInstance.terminationDate>:chargeDate) ";
            switch (level) {
            case BILLING_ACCOUNT:
                strQuery += "AND r.subscription.userAccount.billingAccount=:billingAccount ";
                break;
            case CUSTOMER:
                strQuery += "AND r.subscription.userAccount.billingAccount.customerAccount.customer=:customer ";
                break;
            case CUSTOMER_ACCOUNT:
                strQuery += "AND r.subscription.userAccount.billingAccount.customerAccount=:customerAccount ";
                break;
            case PROVIDER:
                break;
            case SELLER:
                strQuery += "AND r.subscription.userAccount.billingAccount.customerAccount.customer.seller=:seller ";
                break;
            case USER_ACCOUNT:
                strQuery += "AND r.subscription.userAccount=:userAccount ";
                break;
            default:
                break;

            }
            Query query = getEntityManager().createQuery(strQuery);
            query.setParameter("chargeCode", chargeCode);
            query.setParameter("chargeDate", chargeDate);
            UserAccount userAccount = recChargeInstance.getUserAccount();
            BillingAccount billingAccount = userAccount.getBillingAccount();
            CustomerAccount customerAccount = billingAccount.getCustomerAccount();
            Customer customer = customerAccount.getCustomer();
            switch (level) {
            case BILLING_ACCOUNT:
                query.setParameter("billingAccount", billingAccount);
                break;
            case CUSTOMER:
                query.setParameter("customer", customer);
                break;
            case CUSTOMER_ACCOUNT:
                query.setParameter("customerAccount", customerAccount);
                break;
            case PROVIDER:
                break;
            case SELLER:
                query.setParameter("seller", customer.getSeller());
                break;
            case USER_ACCOUNT:
                query.setParameter("userAccount", userAccount);
                break;
            default:
                break;

            }
            Number sharedQuantity = (Number) query.getSingleResult();
            if (sharedQuantity != null) {
                result = sharedQuantity.intValue();
            }
        } catch (Exception e) {
            log.error("faile to get shared quantity", e);
        }
        return result;
    }

    /**
     * This method is used to prerate a oneshot or recurring charge.
     * 
     * @param chargeTemplate charge template
     * @param subscriptionDate subscription date
     * @param offerTemplate offer Template
     * @param chargeInstance charge instance
     * @param applicationType type of application
     * @param applicationDate date of application
     * @param amountWithoutTax amount without tax
     * @param amountWithTax amount with tax
     * @param inputQuantity input quantity
     * @param quantityInChargeUnits Input quantity converted to charge units. If null, will be calculated automatically
     * @param tCurrency trading currency
     * @param countryId id of country
     * @param languageCode code of language
     * @param taxPercent tax percent
     * @param discountPercent discount percent
     * @param nextApplicationDate next date of application
     * @param invoiceSubCategory subcategory of invoice
     * @param criteria1 criteria 1
     * @param criteria2 criteria 2
     * @param criteria3 criteria 3
     * @param orderNumber order number
     * @param startdate start date
     * @param endDate end date
     * @param mode mode
     * @param userAccount user account
     * @return wallet operation
     * @throws BusinessException business exception
     */
    public WalletOperation prerateChargeApplication(ChargeTemplate chargeTemplate, Date subscriptionDate, OfferTemplate offerTemplate, ChargeInstance chargeInstance,
            ApplicationTypeEnum applicationType, Date applicationDate, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal inputQuantity,
            BigDecimal quantityInChargeUnits, TradingCurrency tCurrency, Long countryId, String languageCode, BigDecimal taxPercent, BigDecimal discountPercent,
            Date nextApplicationDate, InvoiceSubCategory invoiceSubCategory, String criteria1, String criteria2, String criteria3, String orderNumber, Date startdate, Date endDate,
            ChargeApplicationModeEnum mode, UserAccount userAccount) throws BusinessException {

        WalletOperation walletOperation = new WalletOperation();
        Auditable auditable = new Auditable(currentUser);
        walletOperation.setAuditable(auditable);

        // TODO do this in the right place (one time by userAccount)
        BillingAccount billingAccount = userAccount.getBillingAccount();
        boolean isExonerated = billingAccountService.isExonerated(billingAccount);

        if (chargeTemplate.getChargeType().equals(RecurringChargeTemplate.CHARGE_TYPE)) {
            walletOperation.setSubscriptionDate(subscriptionDate);
        }

        walletOperation.setInputQuantity(inputQuantity);
        if (quantityInChargeUnits != null) {
            walletOperation.setQuantity(quantityInChargeUnits);

        } else if (inputQuantity != null) {
            walletOperation.setQuantity(
                NumberUtils.getInChargeUnit(inputQuantity, chargeTemplate.getUnitMultiplicator(), chargeTemplate.getUnitNbDecimal(), chargeTemplate.getRoundingMode()));
        }

        walletOperation.setRatingUnitDescription(chargeTemplate.getRatingUnitDescription());
        walletOperation.setInputUnitDescription(chargeTemplate.getInputUnitDescription());
        walletOperation.setOperationDate(applicationDate);
        walletOperation.setOrderNumber(orderNumber);
        walletOperation.setParameter1(criteria1);
        walletOperation.setParameter2(criteria2);
        walletOperation.setParameter3(criteria3);
        if (chargeInstance != null) {
            walletOperation.setDescription(chargeInstance.getDescription());
            walletOperation.setChargeInstance(chargeInstance);
            if (chargeInstance.getInvoicingCalendar() != null) {
                chargeInstance.getInvoicingCalendar().setInitDate(subscriptionDate);

                walletOperation.setInvoicingDate(chargeInstance.getInvoicingCalendar().nextCalendarDate(walletOperation.getOperationDate()));
            }
        }

        walletOperation.setCode(chargeTemplate.getCode());
        walletOperation.setTaxPercent(isExonerated ? BigDecimal.ZERO : taxPercent);
        walletOperation.setCurrency(tCurrency.getCurrency());
        walletOperation.setStartDate(startdate);
        walletOperation.setEndDate(endDate);
        // walletOperation.setOfferCode(offerTemplate.getCode()); Offer code is set in walletOperation.setOfferTemplate
        walletOperation.setOfferTemplate(offerTemplate);
        walletOperation.setInvoiceSubCategory(invoiceSubCategory);
        walletOperation.setStatus(WalletOperationStatusEnum.OPEN);
        if (chargeInstance != null) {
            walletOperation.setSeller(chargeInstance.getSeller());
        } else {
            CustomerAccount customerAccount = billingAccount.getCustomerAccount();
            Customer customer = customerAccount.getCustomer();
            Seller seller = customer.getSeller();
            walletOperation.setSeller(seller);
        }

        // TODO:check that setting the principal wallet at this stage is correct
        walletOperation.setWallet(userAccount.getWallet());
        if (chargeInstance != null && chargeInstance.getSubscription() != null) {
            walletOperation.setBillingAccount(billingAccount);
        }

        BigDecimal unitPriceWithoutTax = amountWithoutTax;
        BigDecimal unitPriceWithTax = amountWithTax;

        rateBareWalletOperation(walletOperation, unitPriceWithoutTax, unitPriceWithTax, countryId, tCurrency);
        log.debug(" wo amountWithoutTax={}", walletOperation.getAmountWithoutTax());
        return walletOperation;

    }

    /**
     * used to rate a oneshot, recurring or product charge and triggerEDR
     * 
     * @param chargeInstance charge instance
     * @param applicationType type of application
     * @param applicationDate application date
     * @param amountWithoutTax amoun without tax
     * @param amountWithTax amount with tax
     * @param inputQuantity input quantity
     * @param quantityInChargeUnits Input quantity converted to charge units. If null, will be calculated later automatically
     * @param tCurrency trading currency
     * @param countryId country id
     * @param taxPercent tax percent
     * @param discountPercent discount percent
     * @param nextApplicationDate next application date
     * @param invoiceSubCategory sub category date
     * @param criteria1 criteria 1
     * @param criteria2 criteria 2
     * @param criteria3 criteria 3
     * @param orderNumber order number
     * @param startdate start date
     * @param endDate end date
     * @param mode mode
     * @param forSchedule true/false
     * @param isVirtual true/false
     * @return wallet operation
     * @throws BusinessException business exception
     */
    public WalletOperation rateChargeApplication(ChargeInstance chargeInstance, ApplicationTypeEnum applicationType, Date applicationDate, BigDecimal amountWithoutTax,
            BigDecimal amountWithTax, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, TradingCurrency tCurrency, Long countryId, BigDecimal taxPercent,
            BigDecimal discountPercent, Date nextApplicationDate, InvoiceSubCategory invoiceSubCategory, String criteria1, String criteria2, String criteria3, String orderNumber,
            Date startdate, Date endDate, ChargeApplicationModeEnum mode, boolean forSchedule, boolean isVirtual) throws BusinessException {
        Date subscriptionDate = null;

        if (chargeInstance instanceof RecurringChargeInstance) {
            subscriptionDate = ((RecurringChargeInstance) chargeInstance).getSubscriptionDate();
        }

        UserAccount ua = chargeInstance.getUserAccount();
        BillingAccount billingAccount = ua.getBillingAccount();
        String languageCode = billingAccount.getTradingLanguage().getLanguage().getLanguageCode();

        Subscription subscription = chargeInstance.getSubscription();
        WalletOperation walletOperation = prerateChargeApplication(chargeInstance.getChargeTemplate(), subscriptionDate, subscription == null ? null : subscription.getOffer(),
            chargeInstance, applicationType, applicationDate, amountWithoutTax, amountWithTax, inputQuantity, quantityInChargeUnits, tCurrency, countryId, languageCode, taxPercent,
            discountPercent, nextApplicationDate, invoiceSubCategory, criteria1, criteria2, criteria3, orderNumber, startdate, endDate, mode, chargeInstance.getUserAccount());

        chargeInstance.getWalletOperations().add(walletOperation);

        // handle associated edr creation unless it is a Scheduled or virtual operation
        if (forSchedule || isVirtual) {
            return walletOperation;
        }

        List<TriggeredEDRTemplate> triggeredEDRTemplates = chargeInstance.getChargeTemplate().getEdrTemplates();
        for (TriggeredEDRTemplate triggeredEDRTemplate : triggeredEDRTemplates) {

            boolean conditionCheck = triggeredEDRTemplate.getConditionEl() == null || "".equals(triggeredEDRTemplate.getConditionEl())
                    || matchExpression(triggeredEDRTemplate.getConditionEl(), walletOperation, ua, walletOperation.getPriceplan());
            log.debug("checking condition for {} : {} -> {}", triggeredEDRTemplate.getCode(), triggeredEDRTemplate.getConditionEl(), conditionCheck);
            if (conditionCheck) {
                if (triggeredEDRTemplate.getMeveoInstance() == null) {
                    EDR newEdr = new EDR();
                    newEdr.setCreated(new Date());
                    newEdr.setEventDate(applicationDate);
                    newEdr.setOriginBatch(EDR.EDR_TABLE_ORIGIN);
                    newEdr.setOriginRecord("CHRG_" + chargeInstance.getId() + "_" + applicationDate.getTime());
                    newEdr.setParameter1(evaluateStringExpression(triggeredEDRTemplate.getParam1El(), walletOperation, ua));
                    newEdr.setParameter2(evaluateStringExpression(triggeredEDRTemplate.getParam2El(), walletOperation, ua));
                    newEdr.setParameter3(evaluateStringExpression(triggeredEDRTemplate.getParam3El(), walletOperation, ua));
                    newEdr.setParameter4(evaluateStringExpression(triggeredEDRTemplate.getParam4El(), walletOperation, ua));
                    newEdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDRTemplate.getQuantityEl(), walletOperation, ua)));
                    newEdr.setStatus(EDRStatusEnum.OPEN);
                    Subscription sub = null;
                    if (StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
                        sub = subscription;
                    } else {
                        String subCode = evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), walletOperation, ua);
                        sub = subscriptionService.findByCode(subCode);
                        if (sub == null) {
                            log.info("could not find subscription for code =" + subCode + " (EL=" + triggeredEDRTemplate.getSubscriptionEl() + ") in triggered EDR with code "
                                    + triggeredEDRTemplate.getCode());
                        }
                    }
                    if (sub != null) {
                        newEdr.setSubscription(sub);
                        log.info("trigger EDR from code " + triggeredEDRTemplate.getCode());
                        if (chargeInstance.getAuditable() != null) {
                            edrService.create(newEdr);
                        }
                    } else {
                        throw new BusinessException("cannot find subscription for the trigerred EDR with code " + triggeredEDRTemplate.getCode());
                    }
                } else {
                    CDR cdr = new CDR();
                    String subCode = evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), walletOperation, ua);
                    cdr.setAccess_id(subCode);
                    cdr.setTimestamp(applicationDate);
                    cdr.setParam1(evaluateStringExpression(triggeredEDRTemplate.getParam1El(), walletOperation, ua));
                    cdr.setParam2(evaluateStringExpression(triggeredEDRTemplate.getParam2El(), walletOperation, ua));
                    cdr.setParam3(evaluateStringExpression(triggeredEDRTemplate.getParam3El(), walletOperation, ua));
                    cdr.setParam4(evaluateStringExpression(triggeredEDRTemplate.getParam4El(), walletOperation, ua));
                    cdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDRTemplate.getQuantityEl(), walletOperation, ua)));
                    String url = "api/rest/billing/mediation/chargeCdr";
                    Response response = meveoInstanceService.callTextServiceMeveoInstance(url, triggeredEDRTemplate.getMeveoInstance(), cdr.toCsv());
                    ActionStatus actionStatus = response.readEntity(ActionStatus.class);
                    log.debug("response {}", actionStatus);
                    if (actionStatus != null && ActionStatusEnum.SUCCESS != actionStatus.getStatus()) {
                        throw new BusinessException("Error charging Edr on remote instance Code " + actionStatus.getErrorCode() + ", info " + actionStatus.getMessage());
                    } else if (actionStatus == null) {
                        throw new BusinessException("Error charging Edr on remote instance");
                    }
                }
            }
        }

        return walletOperation;
    }

    /**
     * used to rate or rerate a bareWalletOperation.
     * 
     * @param bareWalletOperation operation
     * @param unitPriceWithoutTax unit price without tax
     * @param unitPriceWithTax unit price with tax
     * @param countryId country id
     * @param tcurrency trading currency
     * @throws BusinessException business exception
     */
    public void rateBareWalletOperation(WalletOperation bareWalletOperation, BigDecimal unitPriceWithoutTax, BigDecimal unitPriceWithTax, Long countryId, TradingCurrency tcurrency)
            throws BusinessException {

        PricePlanMatrix pricePlan = null;

        if ((unitPriceWithoutTax == null && appProvider.isEntreprise()) || (unitPriceWithTax == null && !appProvider.isEntreprise())) {

            List<PricePlanMatrix> chargePricePlans = pricePlanMatrixService.getActivePricePlansByChargeCode(bareWalletOperation.getCode());
            if (chargePricePlans == null || chargePricePlans.isEmpty()) {
                throw new BusinessException("No price plan for charge code " + bareWalletOperation.getCode());
            }

            pricePlan = ratePrice(chargePricePlans, bareWalletOperation, countryId, tcurrency,
                bareWalletOperation.getSeller() != null ? bareWalletOperation.getSeller().getId() : null);
            if (pricePlan == null || (pricePlan.getAmountWithoutTax() == null && appProvider.isEntreprise())
                    || (pricePlan.getAmountWithTax() == null && !appProvider.isEntreprise())) {
                throw new BusinessException("No price plan matched (" + (pricePlan == null) + ") or does not contain amounts for charge code " + bareWalletOperation.getCode());
            }
            log.debug("Found ratePrice {} for {}", pricePlan.getId(), bareWalletOperation.getCode());
            if (appProvider.isEntreprise()) {
                unitPriceWithoutTax = pricePlan.getAmountWithoutTax();
                if (pricePlan.getAmountWithoutTaxEL() != null) {
                    unitPriceWithoutTax = getExpressionValue(pricePlan.getAmountWithoutTaxEL(), pricePlan, bareWalletOperation,
                        bareWalletOperation.getChargeInstance().getUserAccount(), unitPriceWithoutTax);
                    if (unitPriceWithoutTax == null) {
                        throw new BusinessException("Cant get price from EL:" + pricePlan.getAmountWithoutTaxEL());
                    }
                }

            } else {
                unitPriceWithTax = pricePlan.getAmountWithTax();
                if (pricePlan.getAmountWithTaxEL() != null) {
                    unitPriceWithTax = getExpressionValue(pricePlan.getAmountWithTaxEL(), pricePlan, bareWalletOperation, bareWalletOperation.getWallet().getUserAccount(),
                        unitPriceWithoutTax);
                    if (unitPriceWithTax == null) {
                        throw new BusinessException("Cant get price from EL:" + pricePlan.getAmountWithTaxEL());
                    }
                }
            }
        }


        // if the wallet operation correspond to a recurring charge that is
        // shared, we divide the price by the number of
        // shared charges
        ChargeInstance chargeInstance = bareWalletOperation.getChargeInstance();
        if (chargeInstance != null && chargeInstance instanceof RecurringChargeInstance) {
            RecurringChargeTemplate recChargeTemplate = ((RecurringChargeInstance) chargeInstance).getRecurringChargeTemplate();
            if (recChargeTemplate.getShareLevel() != null) {
                RecurringChargeInstance recChargeInstance = (RecurringChargeInstance) chargeInstance;
                int sharedQuantity = getSharedQuantity(recChargeTemplate.getShareLevel(), recChargeInstance.getCode(), bareWalletOperation.getOperationDate(), recChargeInstance);
                if (sharedQuantity > 0) {
                    if (appProvider.isEntreprise()) {
                        unitPriceWithoutTax = unitPriceWithoutTax.divide(new BigDecimal(sharedQuantity), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    } else {
                        unitPriceWithTax = unitPriceWithTax.divide(new BigDecimal(sharedQuantity), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    }
                    log.info("charge is shared " + sharedQuantity + " times, so unit price is " + unitPriceWithoutTax);
                }
            }
        }


        calculateAmounts(bareWalletOperation, unitPriceWithoutTax, unitPriceWithTax);

        // we override the wo if minimum amount el is set
        if (pricePlan != null) {
            if (appProvider.isEntreprise() && !StringUtils.isBlank(pricePlan.getMinimumAmountWithoutTaxEl())) {
                BigDecimal minimumAmount = new BigDecimal(
                    evaluateDoubleExpression(pricePlan.getMinimumAmountWithoutTaxEl(), bareWalletOperation, bareWalletOperation.getWallet().getUserAccount()));
                // if minAmount > amountWithoutTax override its value
                if (bareWalletOperation.getAmountWithoutTax().compareTo(minimumAmount) < 0) {
                    BigDecimal oldAmountWithoutTax = bareWalletOperation.getAmountWithoutTax();

                    Integer rounding = appProvider.getRounding();
                    bareWalletOperation.setAmountWithoutTax(minimumAmount);
                    BigDecimal amountTax = minimumAmount.multiply(bareWalletOperation.getTaxPercent().divide(HUNDRED));
                    if (rounding != null && rounding > 0) {
                        amountTax = NumberUtils.round(amountTax, rounding);
                    }
                    bareWalletOperation.setAmountTax(amountTax);
                    bareWalletOperation.setAmountWithTax(minimumAmount.add(amountTax));

                    // sets the raw amount
                    if (StringUtils.isBlank(pricePlan.getAmountWithoutTaxEL())) {
                        bareWalletOperation.setRawAmountWithoutTax(pricePlan.getAmountWithoutTax());
                    } else {
                        BigDecimal oldPriceWithoutTax = getExpressionValue(pricePlan.getAmountWithoutTaxEL(), pricePlan, bareWalletOperation,
                            bareWalletOperation.getChargeInstance().getUserAccount(), oldAmountWithoutTax);
                        if (oldPriceWithoutTax != null) {
                            oldPriceWithoutTax = oldPriceWithoutTax.multiply(bareWalletOperation.getQuantity());
                            bareWalletOperation.setRawAmountWithoutTax(oldPriceWithoutTax);
                        }
                    }
                }
            } else if (!StringUtils.isBlank(pricePlan.getMinimumAmountWithTaxEl())) {
                BigDecimal minimumAmount = new BigDecimal(
                    evaluateDoubleExpression(pricePlan.getMinimumAmountWithTaxEl(), bareWalletOperation, bareWalletOperation.getWallet().getUserAccount()));
                if (bareWalletOperation.getAmountWithTax().compareTo(minimumAmount) < 0) {
                    BigDecimal oldAmountWithTax = pricePlan.getAmountWithTax();
                    bareWalletOperation.setAmountWithTax(minimumAmount);

                    if (StringUtils.isBlank(pricePlan.getAmountWithTaxEL())) {
                        bareWalletOperation.setRawAmountWithTax(oldAmountWithTax);
                    } else {
                        BigDecimal oldPriceWithTax = getExpressionValue(pricePlan.getAmountWithTaxEL(), pricePlan, bareWalletOperation,
                            bareWalletOperation.getChargeInstance().getUserAccount(), oldAmountWithTax);
                        if (oldPriceWithTax != null) {
                            oldPriceWithTax = oldPriceWithTax.multiply(bareWalletOperation.getQuantity());
                            bareWalletOperation.setRawAmountWithTax(oldPriceWithTax);
                        }
                    }
                }
            }
        }

        // calculate WO description based on EL from Price plan
        if (pricePlan != null && pricePlan.getWoDescriptionEL() != null) {
            String woDescription = evaluateStringExpression(pricePlan.getWoDescriptionEL(), bareWalletOperation, null);
            if (woDescription != null) {
                bareWalletOperation.setDescription(woDescription);
            }
        }
        
        // get invoiceSubCategory based on EL from Price plan
        if (pricePlan != null && pricePlan.getInvoiceSubCategoryEL() != null) {
            String invoiceSubCategoryCode = evaluateStringExpression(pricePlan.getInvoiceSubCategoryEL(), bareWalletOperation, bareWalletOperation.getWallet()!=null?bareWalletOperation.getWallet().getUserAccount():null);
            if (!StringUtils.isBlank(invoiceSubCategoryCode)) {
                InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(invoiceSubCategoryCode);
                if(invoiceSubCategory != null) {
                    bareWalletOperation.setInvoiceSubCategory(invoiceSubCategory);
                }
            }
        }

        if (pricePlan != null && pricePlan.getScriptInstance() != null) {
            log.debug("start to execute script instance for ratePrice {}", pricePlan);
            executeRatingScript(bareWalletOperation, pricePlan.getScriptInstance().getCode());
        }
        ProductOffering productOffering = null;
        if (pricePlan != null && pricePlan.getOfferTemplate() != null) {
            productOffering = pricePlan.getOfferTemplate();

        } else if (bareWalletOperation.getOfferTemplate() != null) {
            productOffering = bareWalletOperation.getOfferTemplate();

        } else if (bareWalletOperation.getOfferCode() != null) {
            productOffering = productOfferingService.findByCode(bareWalletOperation.getOfferCode(), Arrays.asList("globalRatingScriptInstance"));
        }

        if (productOffering != null && productOffering.getGlobalRatingScriptInstance() != null) {
            log.debug("start to execute script instance for productOffering {}", productOffering);
            executeRatingScript(bareWalletOperation, productOffering.getGlobalRatingScriptInstance().getCode());
        }

    }

    /**
     * Calculate, round (if needed) and set total amounts and taxes: [B2C] amountWithoutTax = round(amountWithTax) - round(amountTax) [B2B] amountWithTax = round(amountWithoutTax)
     * + round(amountTax)
     * 
     * Unit prices and taxes are not rounded
     * 
     * @param walletOperation Wallet operation
     * @param unitPriceWithoutTax Unit price without tax. Used in B2B (provider.isEnterise=true) as base to calculate taxes and price/amount with tax.
     * @param unitPriceWithTax Unit price with tax. Used in B2C (provider.isEnterise=false) as base to calculate taxes and price/amount without tax.
     * @throws BusinessException Business exception
     */
    public void calculateAmounts(WalletOperation walletOperation, BigDecimal unitPriceWithoutTax, BigDecimal unitPriceWithTax) throws BusinessException {

        BigDecimal unitAmountTax = BigDecimal.ZERO;
        BigDecimal amountTax = BigDecimal.ZERO;
        BigDecimal priceWithoutTax = null;
        BigDecimal priceWithTax = null;

        Integer rounding = appProvider.getRounding();

        // Calculate and round total prices and taxes:
        // [B2C] amountWithoutTax = round(amountWithTax) - round(amountTax)
        // [B2B] amountWithTax = round(amountWithoutTax) + round(amountTax)
        // Unit prices and taxes are not rounded
        if (appProvider.isEntreprise()) {

            priceWithoutTax = walletOperation.getQuantity().multiply(unitPriceWithoutTax);

            // process ratingEL here
            if (walletOperation.getPriceplan() != null && !StringUtils.isBlank(walletOperation.getPriceplan().getRatingEL())) {
                priceWithoutTax = new BigDecimal(
                    evaluateDoubleExpression(walletOperation.getPriceplan().getRatingEL(), walletOperation, walletOperation.getWallet().getUserAccount()));
            }

            if (rounding != null && rounding > 0) {
                priceWithoutTax = NumberUtils.round(priceWithoutTax, rounding);
            }

            if (walletOperation.getTaxPercent() != null) {
                unitAmountTax = unitPriceWithoutTax.multiply(walletOperation.getTaxPercent().divide(HUNDRED));

                amountTax = priceWithoutTax.multiply(walletOperation.getTaxPercent().divide(HUNDRED));
                if (rounding != null && rounding > 0) {
                    amountTax = NumberUtils.round(amountTax, rounding);
                }
            }

            unitPriceWithTax = unitPriceWithoutTax.add(unitAmountTax);
            priceWithTax = priceWithoutTax.add(amountTax);

        } else {

            priceWithTax = walletOperation.getQuantity().multiply(unitPriceWithTax);
            if (rounding != null && rounding > 0) {
                priceWithTax = NumberUtils.round(priceWithTax, rounding);
            }

            if (walletOperation.getTaxPercent() != null) {
                BigDecimal percentPlusOne = BigDecimal.ONE.add(walletOperation.getTaxPercent().divide(HUNDRED, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));

                unitAmountTax = unitPriceWithTax.subtract(unitPriceWithTax.divide(percentPlusOne, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
                amountTax = priceWithTax.subtract(priceWithTax.divide(percentPlusOne, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
                if (rounding != null && rounding > 0) {
                    amountTax = NumberUtils.round(amountTax, rounding);
                }
            }

            unitPriceWithoutTax = unitPriceWithTax.subtract(unitAmountTax);
            priceWithoutTax = priceWithTax.subtract(amountTax);

        }

        walletOperation.setUnitAmountWithoutTax(unitPriceWithoutTax);
        walletOperation.setUnitAmountWithTax(unitPriceWithTax);
        walletOperation.setUnitAmountTax(unitAmountTax);
        walletOperation.setTaxPercent(walletOperation.getTaxPercent());
        walletOperation.setAmountWithoutTax(priceWithoutTax);
        walletOperation.setAmountWithTax(priceWithTax);
        walletOperation.setAmountTax(amountTax);
    }

    /**
     * @param listPricePlan list of price plan
     * @param bareOperation operation
     * @param countryId county id
     * @param tcurrency trading currency
     * @param sellerId seller's id
     * @return matrix of price plan
     * @throws BusinessException business exception
     */
    private PricePlanMatrix ratePrice(List<PricePlanMatrix> listPricePlan, WalletOperation bareOperation, Long countryId, TradingCurrency tcurrency, Long sellerId)
            throws BusinessException {
        // FIXME: the price plan properties could be null !
        // log.debug("AKK RS ratePrice line 613");
        // log.info("ratePrice rate " + bareOperation);
        for (PricePlanMatrix pricePlan : listPricePlan) {

            Seller seller = pricePlan.getSeller();
            boolean sellerAreEqual = seller == null || seller.getId().equals(sellerId);
            if (!sellerAreEqual) {
                log.debug("The seller of the customer {} is not the same as pricePlan seller {}", sellerId, seller.getId());
                continue;
            }

            TradingCountry tradingCountry = pricePlan.getTradingCountry();
            boolean countryAreEqual = tradingCountry == null || tradingCountry.getId().equals(countryId);
            if (!countryAreEqual) {
                log.debug("The countryId={} of the billing account is not the same as pricePlan with countryId={}", countryId, tradingCountry.getId());
                continue;
            }

            TradingCurrency tradingCurrency = pricePlan.getTradingCurrency();
            boolean currencyAreEqual = tradingCurrency == null || (tcurrency != null && tcurrency.getId().equals(tradingCurrency.getId()));
            if (!currencyAreEqual) {
                log.debug("The currency of the customer account {} is not the same as pricePlan currency {}", (tcurrency != null ? tcurrency.getCurrencyCode() : "null"),
                    tradingCurrency.getId());
                continue;
            }
            Date subscriptionDate = bareOperation.getSubscriptionDate();
            Date startSubscriptionDate = pricePlan.getStartSubscriptionDate();
            Date endSubscriptionDate = pricePlan.getEndSubscriptionDate();
            boolean subscriptionDateInPricePlanPeriod = subscriptionDate == null
                    || ((startSubscriptionDate == null || subscriptionDate.after(startSubscriptionDate) || subscriptionDate.equals(startSubscriptionDate))
                            && (endSubscriptionDate == null || subscriptionDate.before(endSubscriptionDate)));
            if (!subscriptionDateInPricePlanPeriod) {
                log.debug("The subscription date {} is not in the priceplan subscription range {} - {}", subscriptionDate, startSubscriptionDate, endSubscriptionDate);
                continue;
            }

            int subscriptionAge = 0;
            Date operationDate = bareOperation.getOperationDate();
            if (subscriptionDate != null && operationDate != null) {
                // logger.info("subscriptionDate=" +bareOperation.getSubscriptionDate() + "->" +DateUtils.addDaysToDate(bareOperation.getSubscriptionDate(),-1));
                subscriptionAge = DateUtils.monthsBetween(operationDate, DateUtils.addDaysToDate(subscriptionDate, -1));
            }
            // log.info("subscriptionAge=" + subscriptionAge);

            boolean subscriptionMinAgeOK = pricePlan.getMinSubscriptionAgeInMonth() == null || subscriptionAge >= pricePlan.getMinSubscriptionAgeInMonth();
            // log.info("subscriptionMinAgeOK(" + pricePlan.getMinSubscriptionAgeInMonth() + ")=" +subscriptionMinAgeOK);
            if (!subscriptionMinAgeOK) {
                log.debug("The subscription age={} is less than the priceplan subscription age min={}", subscriptionAge, pricePlan.getMinSubscriptionAgeInMonth());
                continue;
            }
            Long maxSubscriptionAgeInMonth = pricePlan.getMaxSubscriptionAgeInMonth();
            boolean subscriptionMaxAgeOK = maxSubscriptionAgeInMonth == null || maxSubscriptionAgeInMonth == 0 || subscriptionAge < maxSubscriptionAgeInMonth;
            // log.debug("subscriptionMaxAgeOK(" + maxSubscriptionAgeInMonth + ")=" + subscriptionMaxAgeOK);
            if (!subscriptionMaxAgeOK) {
                log.debug("The subscription age {} is greater than the priceplan subscription age max {}", subscriptionAge, maxSubscriptionAgeInMonth);
                continue;
            }

            Date startRatingDate = pricePlan.getStartRatingDate();
            Date endRatingDate = pricePlan.getEndRatingDate();
            boolean applicationDateInPricePlanPeriod = (startRatingDate == null || operationDate.after(startRatingDate) || operationDate.equals(startRatingDate))
                    && (endRatingDate == null || operationDate.before(endRatingDate));
            // log.debug("applicationDateInPricePlanPeriod(" + startRatingDate + " - " + endRatingDate + ")=" + applicationDateInPricePlanPeriod);
            if (!applicationDateInPricePlanPeriod) {
                log.debug("The application date {} is not in the priceplan application range {} - {}", operationDate, startRatingDate, endRatingDate);
                continue;
            }

            String criteria1Value = pricePlan.getCriteria1Value();
            boolean criteria1SameInPricePlan = criteria1Value == null || criteria1Value.equals(bareOperation.getParameter1());
            // log.info("criteria1SameInPricePlan(" + pricePlan.getCriteria1Value() + ")=" + criteria1SameInPricePlan);
            if (!criteria1SameInPricePlan) {
                log.debug("The operation param1 {} is not compatible with price plan criteria 1: {}", bareOperation.getParameter1(), criteria1Value);
                continue;
            }
            String criteria2Value = pricePlan.getCriteria2Value();
            String parameter2 = bareOperation.getParameter2();
            boolean criteria2SameInPricePlan = criteria2Value == null || criteria2Value.equals(parameter2);
            // log.info("criteria2SameInPricePlan(" + pricePlan.getCriteria2Value() + ")=" + criteria2SameInPricePlan);
            if (!criteria2SameInPricePlan) {
                log.debug("The operation param2 {} is not compatible with price plan criteria 2: {}", parameter2, criteria2Value);
                continue;
            }
            String criteria3Value = pricePlan.getCriteria3Value();
            boolean criteria3SameInPricePlan = criteria3Value == null || criteria3Value.equals(bareOperation.getParameter3());
            // log.info("criteria3SameInPricePlan(" + pricePlan.getCriteria3Value() + ")=" + criteria3SameInPricePlan);
            if (!criteria3SameInPricePlan) {
                log.debug("The operation param3 {} is not compatible with price plan criteria 3: {}", bareOperation.getParameter3(), criteria3Value);
                continue;
            }
            if (!StringUtils.isBlank(pricePlan.getCriteriaEL())) {
                UserAccount ua = bareOperation.getWallet().getUserAccount();
                if (!matchExpression(pricePlan.getCriteriaEL(), bareOperation, ua, pricePlan)) {
                    log.debug("The operation is not compatible with price plan criteria EL: {}", pricePlan.getCriteriaEL());
                    continue;
                }
            }

            OfferTemplate ppOfferTemplate = pricePlan.getOfferTemplate();
            if (ppOfferTemplate != null) {
                boolean offerCodeSameInPricePlan = true;

                if (bareOperation.getOfferTemplate() != null) {
                    offerCodeSameInPricePlan = bareOperation.getOfferTemplate().getId().equals(ppOfferTemplate.getId());
                } else {
                    offerCodeSameInPricePlan = ppOfferTemplate.getCode().equals(bareOperation.getOfferCode());
                }

                if (!offerCodeSameInPricePlan) {
                    log.debug("The operation offerCode {} is not compatible with price plan offerCode: {}",
                        bareOperation.getOfferTemplate() != null ? bareOperation.getOfferTemplate() : bareOperation.getOfferCode(), ppOfferTemplate);
                    continue;
                }
            }

            // log.debug("offerCodeSameInPricePlan");
            BigDecimal maxQuantity = pricePlan.getMaxQuantity();
            BigDecimal quantity = bareOperation.getQuantity();
            boolean quantityMaxOk = maxQuantity == null || maxQuantity.compareTo(quantity) > 0;
            if (!quantityMaxOk) {
                log.debug("The quantity " + quantity + " is strictly greater than " + maxQuantity);
                continue;
            }
            // log.debug("quantityMaxOkInPricePlan");

            BigDecimal minQuantity = pricePlan.getMinQuantity();
            boolean quantityMinOk = minQuantity == null || minQuantity.compareTo(quantity) <= 0;
            if (!quantityMinOk) {
                log.debug("The quantity " + quantity + " is less than " + minQuantity);
                continue;
            }
            // log.debug("quantityMinOkInPricePlan");

            Calendar validityCalendar = pricePlan.getValidityCalendar();
            boolean validityCalendarOK = validityCalendar == null || validityCalendar.previousCalendarDate(operationDate) != null;
            if (validityCalendarOK) {
                // log.debug("validityCalendarOkInPricePlan calendar " + validityCalendar + " operation date " + operationDate);
                bareOperation.setPriceplan(pricePlan);
                return pricePlan;
            } else if (validityCalendar != null) {
                log.debug("The operation date " + operationDate + " does not match pricePlan validity calendar " + validityCalendar.getCode() + "period range ");
            }

        }
        return null;
    }

    /**
     * Rerate wallet operation
     * 
     * @param operationToRerateId wallet operation to be rerated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void reRate(Long operationToRerateId, boolean useSamePricePlan) throws BusinessException {

        WalletOperation operationToRerate = getEntityManager().find(WalletOperation.class, operationToRerateId);
        try {
            ratedTransactionService.reratedByWalletOperationId(operationToRerate.getId());
            WalletOperation operation = operationToRerate.getUnratedClone();
            operationToRerate.setReratedWalletOperation(operation);
            operationToRerate.setStatus(WalletOperationStatusEnum.RERATED);
            PricePlanMatrix priceplan = operation.getPriceplan();
            WalletInstance wallet = operation.getWallet();
            UserAccount userAccount = wallet.getUserAccount();

            if (useSamePricePlan) {
                BigDecimal unitAmountWithTax = operation.getUnitAmountWithTax();
                BigDecimal unitAmountWithoutTax = operation.getUnitAmountWithoutTax();

                if (priceplan != null) {

                    if (appProvider.isEntreprise()) {
                        unitAmountWithoutTax = priceplan.getAmountWithoutTax();
                        if (priceplan.getAmountWithoutTaxEL() != null) {
                            unitAmountWithoutTax = getExpressionValue(priceplan.getAmountWithoutTaxEL(), priceplan, operation, userAccount, unitAmountWithoutTax);
                            if (unitAmountWithoutTax == null) {
                                throw new BusinessException("Cant get price from EL:" + priceplan.getAmountWithoutTaxEL());
                            }
                        }

                    } else {
                        unitAmountWithTax = priceplan.getAmountWithTax();
                        if (priceplan.getAmountWithTaxEL() != null) {
                            unitAmountWithTax = getExpressionValue(priceplan.getAmountWithTaxEL(), priceplan, operation, userAccount, unitAmountWithoutTax);
                            if (unitAmountWithTax == null) {
                                throw new BusinessException("Cant get price from EL:" + priceplan.getAmountWithTaxEL());
                            }
                        }
                    }
                }

                calculateAmounts(operation, unitAmountWithoutTax, unitAmountWithTax);

            } else {
                operation.setUnitAmountWithoutTax(null);
                operation.setUnitAmountWithTax(null);
                operation.setUnitAmountTax(null);

                ChargeInstance chargeInstance = operationToRerate.getChargeInstance();
                TradingCountry tradingCountry = chargeInstance.getUserAccount().getBillingAccount().getTradingCountry();
                ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
                InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(chargeTemplate.getInvoiceSubCategory(),
                    tradingCountry, operation.getOperationDate());
                if (invoiceSubcategoryCountry == null) {
                    throw new IncorrectChargeTemplateException("reRate: No invoiceSubcategoryCountry exists for invoiceSubCategory code="
                            + chargeTemplate.getInvoiceSubCategory().getCode() + " and trading country=" + tradingCountry.getCountryCode());
                }

                Tax tax = null;               
                if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
                    tax = invoiceSubcategoryCountry.getTax();
                } else {
                    tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), wallet == null ? null : userAccount, operation.getBillingAccount(),
                        null);
                }
                if (tax == null) {
                    throw new IncorrectChargeTemplateException("reRate: no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
                }
                operation.setTaxPercent(tax.getPercent());

                rateBareWalletOperation(operation, null, null, priceplan.getTradingCountry() == null ? null : priceplan.getTradingCountry().getId(),
                    priceplan.getTradingCurrency());
            }
            create(operation);
            updateNoCheck(operationToRerate);
            log.debug("updated wallet operation");
        } catch (UnrolledbackBusinessException e) {
            log.error("Failed to reRate", e.getMessage());
            operationToRerate.setStatus(WalletOperationStatusEnum.TREATED);
            operationToRerate.setReratedWalletOperation(null);
        }

        log.debug("end rerate wallet operation");
    }

    /**
     * @param expression EL expression
     * @param priceplan price plan
     * @param walletOperation operation
     * @param amount amount used in EL
     * @return evaluated value from expression.
     */
    private BigDecimal getExpressionValue(String expression, PricePlanMatrix priceplan, WalletOperation walletOperation, UserAccount ua, BigDecimal amount) {
        BigDecimal result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, walletOperation, ua, amount);

        Object res = null;
        try {
            res = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);

            if (res != null) {
                if (res instanceof BigDecimal) {
                    result = (BigDecimal) res;
                } else if (res instanceof Number) {
                    result = new BigDecimal(((Number) res).doubleValue());
                } else if (res instanceof String) {
                    result = new BigDecimal(((String) res));
                } else {
                    log.error("Amount Expression " + expression + " do not evaluate to number but " + res);
                }
            }
        } catch (BusinessException e1) {
            log.error("Amount Expression {} error", expression, e1);

        } catch (Exception e) {
            log.error("Error Amount Expression " + expression, e);
        }
        return result;
    }

    /**
     * @param expression EL exception
     * @param walletOperation operation
     * @param ua user account
     * @param priceplan price plan
     * @return true/false true if expression is matched
     * @throws BusinessException business exception
     */
    private boolean matchExpression(String expression, WalletOperation walletOperation, UserAccount ua, PricePlanMatrix priceplan) throws BusinessException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, walletOperation, ua, null);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param walletOperation wallet operation
     * @param ua user account
     * @return evaluated value
     * @throws BusinessException business exception
     */
    private String evaluateStringExpression(String expression, WalletOperation walletOperation, UserAccount ua) throws BusinessException {
        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, null, walletOperation, ua, null);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param walletOperation wallet operation
     * @param ua user account
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private Double evaluateDoubleExpression(String expression, WalletOperation walletOperation, UserAccount ua) throws BusinessException {
        Double result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, null, walletOperation, ua, null);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Double.class);
        try {
            result = (Double) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to double but " + res);
        }
        return result;
    }

    private Map<Object, Object> constructElContext(String expression, PricePlanMatrix priceplan, WalletOperation walletOperation, UserAccount ua, BigDecimal amount) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();

        ChargeInstance chargeInstance = walletOperation.getChargeInstance();
        if ((walletOperation.getChargeInstance() instanceof HibernateProxy)) {
            chargeInstance = (ChargeInstance) ((HibernateProxy) walletOperation.getChargeInstance()).getHibernateLazyInitializer().getImplementation();
        }
        userMap.put("op", walletOperation);
        if (amount != null) {
            userMap.put("amount", amount.doubleValue());
        }
        if (expression.indexOf("access") >= 0 && walletOperation.getEdr() != null && walletOperation.getEdr().getAccessCode() != null) {
            Access access = accessService.findByUserIdAndSubscription(walletOperation.getEdr().getAccessCode(), chargeInstance.getSubscription());
            userMap.put("access", access);
        }

        if (expression.indexOf("priceplan") >= 0 || expression.indexOf("pp") >= 0) {
            if (priceplan == null && walletOperation.getPriceplan() != null) {
                priceplan = walletOperation.getPriceplan();
            }
            if (priceplan != null) {
                userMap.put("priceplan", priceplan);
                userMap.put("pp", priceplan);
            }
        }
        if (expression.indexOf("charge") >= 0) {
            ChargeTemplate charge = chargeInstance.getChargeTemplate();
            userMap.put("charge", charge);
        }
        if (expression.indexOf("serviceInstance") >= 0) {
            ServiceInstance service = null;
            if (chargeInstance instanceof RecurringChargeInstance) {
                service = ((RecurringChargeInstance) chargeInstance).getServiceInstance();
            } else if (chargeInstance instanceof UsageChargeInstance) {
                service = ((UsageChargeInstance) chargeInstance).getServiceInstance();
            } else if (chargeInstance instanceof OneShotChargeInstance) {
                service = ((OneShotChargeInstance) chargeInstance).getSubscriptionServiceInstance();
                if (service == null) {
                    service = ((OneShotChargeInstance) chargeInstance).getTerminationServiceInstance();
                }
            }
            if (service != null) {
                userMap.put("serviceInstance", service);
            }
        }
        if (expression.indexOf("productInstance") >= 0) {
            ProductInstance productInstance = null;
            if (chargeInstance instanceof ProductChargeInstance) {
                productInstance = ((ProductChargeInstance) chargeInstance).getProductInstance();

            }
            if (productInstance != null) {
                userMap.put("productInstance", productInstance);
            }
        }
        if (expression.indexOf("offer") >= 0) {
            OfferTemplate offer = chargeInstance.getSubscription().getOffer();
            userMap.put("offer", offer);
        }
        if (expression.contains("ua") || expression.contains("ba") || expression.contains("ca") || expression.contains("c")) {
            if (ua == null) {
                ua = chargeInstance.getUserAccount();
            }
            if (expression.indexOf("ua") >= 0) {
                userMap.put("ua", ua);
            }
            if (expression.indexOf("ba") >= 0) {
                userMap.put("ba", ua.getBillingAccount());
            }
            if (expression.indexOf("ca") >= 0) {
                userMap.put("ca", ua.getBillingAccount().getCustomerAccount());
            }
            if (expression.indexOf("c") >= 0) {
                userMap.put("c", ua.getBillingAccount().getCustomerAccount().getCustomer());
            }
        }

        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }

        return userMap;
    }

    private void executeRatingScript(WalletOperation bareWalletOperation, String scriptInstanceCode) throws BusinessException {
        try {
            log.debug("execute priceplan script " + scriptInstanceCode);
            ScriptInterface script = scriptInstanceService.getCachedScriptInstance(scriptInstanceCode);
            HashMap<String, Object> context = new HashMap<String, Object>();
            context.put(Script.CONTEXT_ENTITY, bareWalletOperation);
            script.execute(context);
        } catch (Exception e) {
            log.error("Error when run script {}", scriptInstanceCode, e);
            throw new BusinessException("failed when run script " + scriptInstanceCode + ", info " + e.getMessage());
        }
    }
}