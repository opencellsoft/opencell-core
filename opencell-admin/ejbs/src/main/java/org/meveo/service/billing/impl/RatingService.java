package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.meveo.admin.exception.ChargingEdrOnRemoteInstanceErrorException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.PriceELErrorException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.exception.RatingScriptExecutionErrorException;
import org.meveo.admin.exception.UnrolledbackBusinessException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
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
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.EDR;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.catalog.TriggeredEdrScriptService;

/**
 * Rate charges such as {@link org.meveo.model.catalog.OneShotChargeTemplate}, {@link org.meveo.model.catalog.RecurringChargeTemplate} and
 * {@link org.meveo.model.catalog.UsageChargeTemplate}. Generate the {@link org.meveo.model.billing.WalletOperation} with the appropriate values.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RatingService extends PersistenceService<WalletOperation> {

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

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private TriggeredEdrScriptService triggeredEdrScriptService;

    @Inject
    private TaxService taxService;

    /**
     * @param level level enum
     * @param chargeCode charge's code
     * @param chargeDate charge's date
     * @param recChargeInstance reccurring charge instance
     * @return shared quantity
     */
    @SuppressWarnings("deprecation")
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
     * Rate a oneshot or recurring charge. DOES NOT persist walletOperation to DB.
     * 
     * @param subscriptionDate Subscription date
     * @param chargeInstance Charge instance to apply
     * @param applicationType Application type
     * @param applicationDate Date of application
     * @param amountWithoutTax amount without tax
     * @param amountWithTax amount with tax
     * @param inputQuantity input quantity
     * @param quantityInChargeUnits Input quantity converted to charge units. If null, will be calculated automatically
     * @param tax Tax to apply
     * @param orderNumberOverride order number
     * @param startdate start date
     * @param endDate end date
     * @param chargeMode Charge mode
     * @return wallet operation
     * @throws BusinessException business exception
     * @throws RatingException Failure to rate charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public WalletOperation rateCharge(ChargeInstance chargeInstance, ApplicationTypeEnum applicationType, Date applicationDate, BigDecimal amountWithoutTax,
            BigDecimal amountWithTax, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, Tax tax, String orderNumberOverride, Date startdate, Date endDate,
            ChargeApplicationModeEnum chargeMode) throws BusinessException, RatingException {

        BillingAccount billingAccount = chargeInstance.getUserAccount().getBillingAccount();
        if (billingAccountService.isExonerated(billingAccount)) {
            tax = taxService.getZeroTax();
        }

        WalletOperation walletOperation = new WalletOperation(chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate,
            orderNumberOverride != null ? (orderNumberOverride.equals(ChargeInstance.NO_ORDER_NUMBER) ? null : orderNumberOverride) : chargeInstance.getOrderNumber(),
            chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), null, tax, startdate, endDate);

        BigDecimal unitPriceWithoutTax = amountWithoutTax;
        BigDecimal unitPriceWithTax = amountWithTax;

        rateBareWalletOperation(walletOperation, unitPriceWithoutTax, unitPriceWithTax, chargeInstance.getCountry().getId(), chargeInstance.getCurrency());
        log.debug(" wo amountWithoutTax={}", walletOperation.getAmountWithoutTax());
        return walletOperation;

    }

    /**
     * Rate a oneshot, recurring or product charge and triggerEDR. Same as rateCharge but in addition triggers EDRs. NOTE: Does not persist WO
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
     * @param tax Tax to charge
     * @param orderNumberOverride order number
     * @param startdate start date
     * @param endDate end date
     * @param chargeMode mode
     * @param forSchedule true/false
     * @param isVirtual true/false
     * @return wallet operation
     * @throws BusinessException business exception
     * @throws RatingException Failure to rate charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public WalletOperation rateChargeAndTriggerEDRs(ChargeInstance chargeInstance, ApplicationTypeEnum applicationType, Date applicationDate, BigDecimal amountWithoutTax,
            BigDecimal amountWithTax, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, Tax tax, String orderNumberOverride, Date startdate, Date endDate,
            ChargeApplicationModeEnum chargeMode, boolean forSchedule, boolean isVirtual) throws BusinessException, RatingException {

        UserAccount ua = chargeInstance.getUserAccount();

        Subscription subscription = chargeInstance.getSubscription();
        WalletOperation walletOperation = rateCharge(chargeInstance, applicationType, applicationDate, amountWithoutTax, amountWithTax, inputQuantity, quantityInChargeUnits, tax,
            orderNumberOverride, startdate, endDate, chargeMode);

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
                MeveoInstance meveoInstance = null;

                if (triggeredEDRTemplate.getMeveoInstance() != null) {
                    meveoInstance = triggeredEDRTemplate.getMeveoInstance();
                }
                if (!StringUtils.isBlank(triggeredEDRTemplate.getOpencellInstanceEL())) {
                    String opencellInstanceCode = evaluateStringExpression(triggeredEDRTemplate.getOpencellInstanceEL(), walletOperation, ua);
                    meveoInstance = meveoInstanceService.findByCode(opencellInstanceCode);
                }

                if (meveoInstance == null) {
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
                    Subscription sub = null;

                    if (StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
                        sub = subscription;

                    } else {
                        String subCode = evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), walletOperation, ua);
                        sub = subscriptionService.findByCode(subCode);
                        if (sub == null) {
                            log.info("Could not find subscription for code={} (EL={}) in triggered EDR with code {}", subCode, triggeredEDRTemplate.getSubscriptionEl(),
                                triggeredEDRTemplate.getCode());
                        }
                    }

                    if (sub != null) {
                        newEdr.setSubscription(sub);
                        log.info("trigger EDR from code {}", triggeredEDRTemplate.getCode());
                        if (chargeInstance.getAuditable() != null) {
                            log.info("trigger EDR from code {}", triggeredEDRTemplate.getCode());

                            if (triggeredEDRTemplate.getTriggeredEdrScript() != null) {
                                newEdr = triggeredEdrScriptService.updateEdr(triggeredEDRTemplate.getTriggeredEdrScript().getCode(), newEdr, walletOperation);
                            }

                            edrService.create(newEdr);
                        }

                    } else {
                        // removed for the case of product instance on user account without subscription
                        // throw new BusinessException("cannot find subscription for the trigerred EDR with code " + triggeredEDRTemplate.getCode());
                    }

                } else {
                    if (StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
                        throw new BusinessException("TriggeredEDRTemplate.subscriptionEl must not be null and must point to an existing Access.");
                    }

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
                    Response response = meveoInstanceService.callTextServiceMeveoInstance(url, meveoInstance, cdr.toCsv());
                    ActionStatus actionStatus = response.readEntity(ActionStatus.class);
                    log.trace("Triggered remote EDR response {}", actionStatus);

                    if (actionStatus != null && ActionStatusEnum.SUCCESS != actionStatus.getStatus()) {
                        throw new ChargingEdrOnRemoteInstanceErrorException(
                            "Error charging EDR. Error code " + actionStatus.getErrorCode() + ", info " + actionStatus.getMessage());

                    } else if (actionStatus == null) {
                        throw new ChargingEdrOnRemoteInstanceErrorException("Error charging EDR. No response code from API.");
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
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public void rateBareWalletOperation(WalletOperation bareWalletOperation, BigDecimal unitPriceWithoutTax, BigDecimal unitPriceWithTax, Long countryId, TradingCurrency tcurrency)
            throws BusinessException, RatingException {
        RecurringChargeTemplate recChargeTemplate = null;
        ChargeInstance chargeInstance = bareWalletOperation.getChargeInstance();
        if (chargeInstance != null && chargeInstance instanceof RecurringChargeInstance) {
            recChargeTemplate = ((RecurringChargeInstance) chargeInstance).getRecurringChargeTemplate();
        }
        PricePlanMatrix pricePlan = null;

        if ((unitPriceWithoutTax == null && appProvider.isEntreprise()) || (unitPriceWithTax == null && !appProvider.isEntreprise())) {

            List<PricePlanMatrix> chargePricePlans = getActivePricePlansByChargeCode(bareWalletOperation.getCode());
            if (chargePricePlans == null || chargePricePlans.isEmpty()) {
                throw new NoPricePlanException("No price plan for charge code " + bareWalletOperation.getCode());
            }

            pricePlan = ratePrice(chargePricePlans, bareWalletOperation, countryId, tcurrency, recChargeTemplate);
            if (pricePlan == null) {
                throw new NoPricePlanException("No price plan matched for charge code " + bareWalletOperation.getCode());

            } else if ((pricePlan.getAmountWithoutTax() == null && appProvider.isEntreprise()) || (pricePlan.getAmountWithTax() == null && !appProvider.isEntreprise())) {
                throw new NoPricePlanException("Price plan " + pricePlan.getId() + " does not contain amounts for charge " + bareWalletOperation.getCode());
            }
            log.debug("Will apply priceplan {} for {}", pricePlan.getId(), bareWalletOperation.getCode());
            if (appProvider.isEntreprise()) {
                unitPriceWithoutTax = pricePlan.getAmountWithoutTax();
                if (pricePlan.getAmountWithoutTaxEL() != null) {
                    unitPriceWithoutTax = evaluateAmountExpression(pricePlan.getAmountWithoutTaxEL(), pricePlan, bareWalletOperation,
                        bareWalletOperation.getChargeInstance().getUserAccount(), unitPriceWithoutTax);
                    if (unitPriceWithoutTax == null) {
                        throw new PriceELErrorException("Can't evaluate price for price plan " + pricePlan.getId() + " EL:" + pricePlan.getAmountWithoutTaxEL());
                    }
                }

            } else {
                unitPriceWithTax = pricePlan.getAmountWithTax();
                if (pricePlan.getAmountWithTaxEL() != null) {
                    unitPriceWithTax = evaluateAmountExpression(pricePlan.getAmountWithTaxEL(), pricePlan, bareWalletOperation, bareWalletOperation.getWallet().getUserAccount(),
                        unitPriceWithoutTax);
                    if (unitPriceWithTax == null) {
                        throw new PriceELErrorException("Can't evaluate price for price plan " + pricePlan.getId() + " EL:" + pricePlan.getAmountWithTaxEL());
                    }
                }
            }
        }

        // if the wallet operation correspond to a recurring charge that is
        // shared, we divide the price by the number of
        // shared charges

        if (recChargeTemplate  != null && recChargeTemplate.getShareLevel() != null) {
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


        calculateAmounts(bareWalletOperation, unitPriceWithoutTax, unitPriceWithTax);

        // calculate WO description based on EL from Price plan
        if (pricePlan != null && pricePlan.getWoDescriptionEL() != null) {
            String woDescription = evaluateStringExpression(pricePlan.getWoDescriptionEL(), bareWalletOperation, null);
            if (woDescription != null) {
                bareWalletOperation.setDescription(woDescription);
            }
        }

        // get invoiceSubCategory based on EL from Price plan
        if (pricePlan != null && pricePlan.getInvoiceSubCategoryEL() != null) {
            String invoiceSubCategoryCode = evaluateStringExpression(pricePlan.getInvoiceSubCategoryEL(), bareWalletOperation,
                bareWalletOperation.getWallet() != null ? bareWalletOperation.getWallet().getUserAccount() : null);
            if (!StringUtils.isBlank(invoiceSubCategoryCode)) {
                InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(invoiceSubCategoryCode);
                if (invoiceSubCategory != null) {
                    bareWalletOperation.setInvoiceSubCategory(invoiceSubCategory);
                }
            }
        }

        if (pricePlan != null && pricePlan.getScriptInstance() != null) {
            log.debug("start to execute script instance for ratePrice {}", pricePlan);
            executeRatingScript(bareWalletOperation, pricePlan.getScriptInstance());
        }
        ProductOffering productOffering = null;
        if (pricePlan != null && pricePlan.getOfferTemplate() != null) {
            productOffering = pricePlan.getOfferTemplate();

        } else if (bareWalletOperation.getOfferTemplate() != null) {
            productOffering = bareWalletOperation.getOfferTemplate();
        }

        if (productOffering != null && productOffering.getGlobalRatingScriptInstance() != null) {
            log.debug("start to execute script instance for productOffering {}", productOffering);
            executeRatingScript(bareWalletOperation, productOffering.getGlobalRatingScriptInstance());
        }

    }

    public List<PricePlanMatrix> getActivePricePlansByChargeCode(String code) {
        return pricePlanMatrixService.getActivePricePlansByChargeCode(code);
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

        Integer rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();

        // Calculate and round total prices and taxes:
        // [B2C] amountWithoutTax = round(amountWithTax) - round(amountTax)
        // [B2B] amountWithTax = round(amountWithoutTax) + round(amountTax)
        // Unit prices and unit taxes are not rounded

        BigDecimal amount = null;
        BigDecimal unitPrice = appProvider.isEntreprise() ? unitPriceWithoutTax : unitPriceWithTax;

        // process ratingEL here
        if (walletOperation.getPriceplan() != null) {
            String ratingEl = walletOperation.getPriceplan().getTotalAmountEL();
            if (!StringUtils.isBlank(ratingEl)) {
                amount = BigDecimal.valueOf(evaluateDoubleExpression(ratingEl, walletOperation, walletOperation.getWallet().getUserAccount()));
            }
        }

        if (amount == null) {
            amount = walletOperation.getQuantity().multiply(unitPrice);
        }

        // Unit prices and unit taxes are with higher precision
        BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(unitPrice, unitPrice, walletOperation.getTaxPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS,
            RoundingMode.HALF_UP);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(amount, amount, walletOperation.getTaxPercent(), appProvider.isEntreprise(), rounding,
            roundingMode.getRoundingMode());

        walletOperation.setUnitAmountWithoutTax(unitAmounts[0]);
        walletOperation.setUnitAmountWithTax(unitAmounts[1]);
        walletOperation.setUnitAmountTax(unitAmounts[2]);
        walletOperation.setAmountWithoutTax(amounts[0]);
        walletOperation.setAmountWithTax(amounts[1]);
        walletOperation.setAmountTax(amounts[2]);

        // we override the wo amount if minimum amount el is set on price plan
        if (walletOperation.getPriceplan() != null && !StringUtils.isBlank(walletOperation.getPriceplan().getMinimumAmountEL())) {
            BigDecimal minimumAmount = BigDecimal
                .valueOf(evaluateDoubleExpression(walletOperation.getPriceplan().getMinimumAmountEL(), walletOperation, walletOperation.getWallet().getUserAccount()));

            if ((appProvider.isEntreprise() && walletOperation.getAmountWithoutTax().compareTo(minimumAmount) < 0)
                    || (!appProvider.isEntreprise() && walletOperation.getAmountWithTax().compareTo(minimumAmount) < 0)) {

                // Remember the raw calculated amount
                walletOperation.setRawAmountWithoutTax(walletOperation.getAmountWithoutTax());
                walletOperation.setRawAmountWithTax(walletOperation.getAmountWithTax());

                amounts = NumberUtils.computeDerivedAmounts(minimumAmount, minimumAmount, walletOperation.getTaxPercent(), appProvider.isEntreprise(), rounding,
                    roundingMode.getRoundingMode());

                walletOperation.setAmountWithoutTax(amounts[0]);
                walletOperation.setAmountWithTax(amounts[1]);
                walletOperation.setAmountTax(amounts[2]);
            }
        }
    }

    /**
     * @param listPricePlan list of price plan
     * @param bareOperation operation
     * @param countryId county id
     * @param tcurrency trading currency
     * @param chargeTemplate chargeTemplate
     * @return matrix of price plan
     * @throws BusinessException business exception
     */
    private PricePlanMatrix ratePrice(List<PricePlanMatrix> listPricePlan, WalletOperation bareOperation, Long countryId, TradingCurrency tcurrency,
            ChargeTemplate chargeTemplate) throws BusinessException {
        // FIXME: the price plan properties could be null !
        // log.info("ratePrice rate " + bareOperation);
        Date startDate = bareOperation.getStartDate();
        Date endDate = bareOperation.getEndDate();
        for (PricePlanMatrix pricePlan : listPricePlan) {

            log.trace("Try to verify price plan {} for WO {}", pricePlan.getId(), bareOperation.getCode());

            Seller seller = pricePlan.getSeller();
            boolean sellerAreEqual = seller == null || seller.getId().equals(bareOperation.getSeller().getId());
            if (!sellerAreEqual) {
                log.trace("The seller of the customer {} is not the same as pricePlan seller {}", bareOperation.getSeller().getId(), seller.getId());
                continue;
            }

            TradingCountry tradingCountry = pricePlan.getTradingCountry();
            boolean countryAreEqual = tradingCountry == null || tradingCountry.getId().equals(countryId);
            if (!countryAreEqual) {
                log.trace("The countryId={} of the billing account is not the same as pricePlan with countryId={}", countryId, tradingCountry.getId());
                continue;
            }

            TradingCurrency tradingCurrency = pricePlan.getTradingCurrency();
            boolean currencyAreEqual = tradingCurrency == null || (tcurrency != null && tcurrency.getId().equals(tradingCurrency.getId()));
            if (!currencyAreEqual) {
                log.trace("The currency of the customer account {} is not the same as pricePlan currency {}", (tcurrency != null ? tcurrency.getCurrencyCode() : "null"),
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
                log.trace("The subscription date {} is not in the priceplan subscription range {} - {}", subscriptionDate, startSubscriptionDate, endSubscriptionDate);
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
                log.trace("The subscription age={} is less than the priceplan subscription age min={}", subscriptionAge, pricePlan.getMinSubscriptionAgeInMonth());
                continue;
            }
            Long maxSubscriptionAgeInMonth = pricePlan.getMaxSubscriptionAgeInMonth();
            boolean subscriptionMaxAgeOK = maxSubscriptionAgeInMonth == null || maxSubscriptionAgeInMonth == 0 || subscriptionAge < maxSubscriptionAgeInMonth;
            // log.debug("subscriptionMaxAgeOK(" + maxSubscriptionAgeInMonth + ")=" + subscriptionMaxAgeOK);
            if (!subscriptionMaxAgeOK) {
                log.trace("The subscription age {} is greater than the priceplan subscription age max {}", subscriptionAge, maxSubscriptionAgeInMonth);
                continue;
            }

            Date startRatingDate = pricePlan.getStartRatingDate();
            Date endRatingDate = pricePlan.getEndRatingDate();
            boolean applicationDateInPricePlanPeriod = (startRatingDate == null || operationDate.after(startRatingDate) || operationDate.equals(startRatingDate))
                    && (endRatingDate == null || operationDate.before(endRatingDate));
            // log.debug("applicationDateInPricePlanPeriod(" + startRatingDate + " - " + endRatingDate + ")=" + applicationDateInPricePlanPeriod);
            if (!applicationDateInPricePlanPeriod) {
                log.trace("The application date {} is not in the priceplan application range {} - {}", operationDate, startRatingDate, endRatingDate);
                continue;
            }

            String criteria1Value = pricePlan.getCriteria1Value();
            boolean criteria1SameInPricePlan = criteria1Value == null || criteria1Value.equals(bareOperation.getParameter1());
            // log.info("criteria1SameInPricePlan(" + pricePlan.getCriteria1Value() + ")=" + criteria1SameInPricePlan);
            if (!criteria1SameInPricePlan) {
                log.trace("The operation param1 {} is not compatible with price plan criteria 1: {}", bareOperation.getParameter1(), criteria1Value);
                continue;
            }
            String criteria2Value = pricePlan.getCriteria2Value();
            String parameter2 = bareOperation.getParameter2();
            boolean criteria2SameInPricePlan = criteria2Value == null || criteria2Value.equals(parameter2);
            // log.info("criteria2SameInPricePlan(" + pricePlan.getCriteria2Value() + ")=" + criteria2SameInPricePlan);
            if (!criteria2SameInPricePlan) {
                log.trace("The operation param2 {} is not compatible with price plan criteria 2: {}", parameter2, criteria2Value);
                continue;
            }
            String criteria3Value = pricePlan.getCriteria3Value();
            boolean criteria3SameInPricePlan = criteria3Value == null || criteria3Value.equals(bareOperation.getParameter3());
            // log.info("criteria3SameInPricePlan(" + pricePlan.getCriteria3Value() + ")=" + criteria3SameInPricePlan);
            if (!criteria3SameInPricePlan) {
                log.trace("The operation param3 {} is not compatible with price plan criteria 3: {}", bareOperation.getParameter3(), criteria3Value);
                continue;
            }
            if (!StringUtils.isBlank(pricePlan.getCriteriaEL())) {
                UserAccount ua = bareOperation.getWallet().getUserAccount();
                if (!matchExpression(pricePlan.getCriteriaEL(), bareOperation, ua, pricePlan)) {
                    log.trace("The operation is not compatible with price plan criteria EL: {}", pricePlan.getCriteriaEL());
                    continue;
                }
            }

            OfferTemplate ppOfferTemplate = pricePlan.getOfferTemplate();
            if (ppOfferTemplate != null) {
                boolean offerCodeSameInPricePlan = true;

                if (bareOperation.getOfferTemplate() != null) {
                    offerCodeSameInPricePlan = bareOperation.getOfferTemplate().getId().equals(ppOfferTemplate.getId());
                } else if (bareOperation.getOfferCode() != null) {
                    offerCodeSameInPricePlan = ppOfferTemplate.getCode().equals(bareOperation.getOfferCode());
                }

                if (!offerCodeSameInPricePlan) {
                    log.trace("The operation offerCode {} is not compatible with price plan offerCode: {}",
                        bareOperation.getOfferTemplate() != null ? bareOperation.getOfferTemplate() : bareOperation.getOfferCode(), ppOfferTemplate);
                    continue;
                }
            }

            // log.debug("offerCodeSameInPricePlan");
            BigDecimal maxQuantity = pricePlan.getMaxQuantity();
            BigDecimal quantity = bareOperation.getQuantity();
            boolean quantityMaxOk = maxQuantity == null || maxQuantity.compareTo(quantity) > 0;
            if (!quantityMaxOk) {
                log.trace("The quantity " + quantity + " is strictly greater than " + maxQuantity);
                continue;
            }
            // log.debug("quantityMaxOkInPricePlan");

            BigDecimal minQuantity = pricePlan.getMinQuantity();
            boolean quantityMinOk = minQuantity == null || minQuantity.compareTo(quantity) <= 0;
            if (!quantityMinOk) {
                log.trace("The quantity " + quantity + " is less than " + minQuantity);
                continue;
            }
            if (chargeTemplate != null && chargeTemplate instanceof RecurringChargeTemplate && ((RecurringChargeTemplate)chargeTemplate).isProrataOnPriceChange()) {
                if (!isStartDateBetween(startDate, pricePlan.getValidityFrom(), pricePlan.getValidityDate())
                        || !isEndDateBetween(endDate, startDate, pricePlan.getValidityDate())){
                    continue;
                }
            }
            Calendar validityCalendar = pricePlan.getValidityCalendar();
            boolean validityCalendarOK = validityCalendar == null || validityCalendar.previousCalendarDate(operationDate) != null;
            if (validityCalendarOK) {
                // log.debug("validityCalendarOkInPricePlan calendar " + validityCalendar + " operation date " + operationDate);
                bareOperation.setPriceplan(pricePlan);
                return pricePlan;
            } else if (validityCalendar != null) {
                log.trace("The operation date " + operationDate + " does not match pricePlan validity calendar " + validityCalendar.getCode() + "period range ");
            }

        }
        return null;
    }

    private boolean isStartDateBetween(Date date, Date from, Date to) {
        return (from != null && (date.equals(from) || (date.after(from))) && (to == null || (to != null || date.before(to))));
    }
    private boolean isEndDateBetween(Date date, Date from, Date to) {
        return date.after(from) && (to == null || (date.before(to) || date.equals(to)));
    }
    /**
     * Rerate wallet operation
     * 
     * @param operationToRerateId wallet operation to be rerated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     * @throws RatingException Operation rerating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void reRate(Long operationToRerateId, boolean useSamePricePlan) throws BusinessException, RatingException {

        WalletOperation operationToRerate = getEntityManager().find(WalletOperation.class, operationToRerateId);
        try {

            // Change related Rated transaction status to Rerated
            RatedTransaction ratedTransaction = operationToRerate.getRatedTransaction();
            if (ratedTransaction.getStatus() == RatedTransactionStatusEnum.BILLED) {
                throw new UnrolledbackBusinessException(
                    "Can not rerate an already billed Wallet Operation. Wallet Operation " + operationToRerateId + " corresponds to rated transaction " + ratedTransaction.getId());
            } else if (ratedTransaction.getStatus() != RatedTransactionStatusEnum.CANCELED && ratedTransaction.getStatus() != RatedTransactionStatusEnum.RERATED) {
                ratedTransaction.changeStatus(RatedTransactionStatusEnum.RERATED);
            }

            WalletOperation operation = operationToRerate.getUnratedClone();
            operationToRerate.setReratedWalletOperation(operation);
            operationToRerate.changeStatus(WalletOperationStatusEnum.RERATED);
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
                            unitAmountWithoutTax = evaluateAmountExpression(priceplan.getAmountWithoutTaxEL(), priceplan, operation, userAccount, unitAmountWithoutTax);
                            if (unitAmountWithoutTax == null) {
                                throw new BusinessException("Can't find unitPriceWithoutTax from PP :" + priceplan.getAmountWithoutTaxEL());
                            }
                        }

                    } else {
                        unitAmountWithTax = priceplan.getAmountWithTax();
                        if (priceplan.getAmountWithTaxEL() != null) {
                            unitAmountWithTax = evaluateAmountExpression(priceplan.getAmountWithTaxEL(), priceplan, operation, userAccount, unitAmountWithoutTax);
                            if (unitAmountWithTax == null) {
                                throw new BusinessException("Can't find unitPriceWithoutTax from PP :" + priceplan.getAmountWithTaxEL());
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

                Tax tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, operation.getOperationDate());

                operation.setTax(tax);
                operation.setTaxPercent(tax.getPercent());

                rateBareWalletOperation(operation, null, null, priceplan.getTradingCountry() == null ? null : priceplan.getTradingCountry().getId(),
                    priceplan.getTradingCurrency());
            }
            create(operation);
            updateNoCheck(operationToRerate);
            log.debug("updated wallet operation");

        } catch (UnrolledbackBusinessException e) {
            log.error("Failed to reRate", e.getMessage());
            operationToRerate.changeStatus(WalletOperationStatusEnum.TREATED);
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
    private BigDecimal evaluateAmountExpression(String expression, PricePlanMatrix priceplan, WalletOperation walletOperation, UserAccount ua, BigDecimal amount) {
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
        if (expression.indexOf("charge") >= 0 || expression.indexOf("chargeTemplate") >= 0) {
            ChargeTemplate charge = chargeInstance.getChargeTemplate();
            userMap.put("charge", charge);
            userMap.put("chargeTemplate", charge);
        }
        if (expression.indexOf("serviceInstance") >= 0) {
            ServiceInstance service = chargeInstance.getServiceInstance();
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

    private void executeRatingScript(WalletOperation bareWalletOperation, ScriptInstance scriptInstance) throws RatingScriptExecutionErrorException {

        String scriptInstanceCode = scriptInstance.getCode();
        try {
            log.debug("Will execute priceplan script " + scriptInstanceCode);
            scriptInstanceService.executeCached(bareWalletOperation, scriptInstanceCode, null);

        } catch (BusinessException e) {
            throw new RatingScriptExecutionErrorException("Failed when run script " + scriptInstanceCode + ", info " + e.getMessage(), e);
        }
    }
}