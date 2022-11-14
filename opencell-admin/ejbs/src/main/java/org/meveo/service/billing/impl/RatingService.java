/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.core.Response;

import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ChargingEdrOnRemoteInstanceErrorException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.PriceELErrorException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.exception.RatingScriptExecutionErrorException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ChargeTemplate.ChargeMainTypeEnum;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.catalog.LevelEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.contract.ContractRateTypeEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.RatingResult;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.cpq.ContractItemService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.catalog.TriggeredEdrScriptService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;

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
    private TaxMappingService taxMappingService;

    @Inject
    private AccessService accessService;

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
    private WalletOperationService walletOperationService;

    @EJB
    private RatingService ratingServiceNewTX;

    @Inject
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;
    
    @Inject
    private ContractService contractService;
    
    @Inject
    private ContractItemService contractItemService;
    
    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;
    
    
    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    final private static BigDecimal HUNDRED = new BigDecimal("100");
    
    @Inject
    private DiscountPlanService discountPlanService;
    @Inject
    private DiscountPlanItemService discountPlanItemService;
    @Inject
    private AccountingArticleService accountingArticleService;

    /**
     * @param level level enum
     * @param chargeCode charge's code
     * @param chargeDate charge's date
     * @param recChargeInstance recurring charge instance
     * @return shared quantity
     */
    public int getSharedQuantity(LevelEnum level, String chargeCode, Date chargeDate, RecurringChargeInstance recChargeInstance) {
        int result = 0;
        try {
            String strQuery = "select SUM(r.serviceInstance.quantity) from " + RecurringChargeInstance.class.getSimpleName() + " r " + "WHERE r.code=:chargeCode " + "AND r.subscriptionDate<=:chargeDate "
                    + "AND (r.serviceInstance.terminationDate is NULL OR r.serviceInstance.terminationDate>:chargeDate) ";
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
     * Rate a charge. Note: DOES NOT persist walletOperation to DB.
     *
     * @param chargeInstance Charge instance to rate
     * @param applicationDate Date of application
     * @param inputQuantity Input quantity
     * @param quantityInChargeUnits Input quantity converted to charge units. If null, will be calculated automatically
     * @param orderNumberOverride Order number to overrite. If not provided, will default to an order number from a charge instance
     * @param startdate Charge period start date if applicable
     * @param endDate Charge period end date if applicable.
     * @param chargeMode Charge mode
     * @param fullRatingPeriod Full rating period dates when prorata is applied. In such case startDate-endDate will be shorted than fullRatingPeriod. Is NOT provided when prorata
     *        is not applied.
     * @param edr EDR being rated
     * @param isReservation - is this a reservation instead of a real wallet operation
     * @param isVirtual Is this a virtual charge - simulation of rating, charge instance will be matched by code to the charge instantiated in subscription
     * @return Rating result containing a rated wallet operation (NOT persisted)
     * @throws BusinessException General business exception
     * @throws RatingException Failure to rate charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult rateCharge(ChargeInstance chargeInstance, Date applicationDate, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, String orderNumberOverride, Date startdate, Date endDate,
                                   DatePeriod fullRatingPeriod, ChargeApplicationModeEnum chargeMode, EDR edr, boolean isReservation, boolean isVirtual) throws BusinessException, RatingException {

        // For virtual operation, lookup charge in the subscription
        if (isVirtual && chargeInstance.getSubscription() != null) {
            List<ServiceInstance> serviceInstances = chargeInstance.getSubscription().getServiceInstances();
            for (ServiceInstance serviceInstance : serviceInstances) {
                for (ChargeInstance chargeInstanceFromService : serviceInstance.getChargeInstances()) {
                    if (chargeInstanceFromService.getCode().equals(chargeInstance.getCode())) {
                        chargeInstance = chargeInstanceFromService;
                        break;
                    }
                }
            }
        }

        WalletOperation walletOperation = null;

        if (quantityInChargeUnits == null) {
            quantityInChargeUnits = chargeTemplateService.evaluateRatingQuantity(chargeInstance.getChargeTemplate(), inputQuantity);
        }

        Date invoicingDate = null;
        if (chargeInstance.getInvoicingCalendar() != null) {

            Date defaultInitDate = null;
            if (chargeInstance instanceof RecurringChargeInstance && ((RecurringChargeInstance) chargeInstance).getSubscriptionDate() != null) {
                defaultInitDate = ((RecurringChargeInstance) chargeInstance).getSubscriptionDate();
            } else if (chargeInstance.getServiceInstance() != null) {
                defaultInitDate = chargeInstance.getServiceInstance().getSubscriptionDate();
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                defaultInitDate = chargeInstance.getSubscription().getSubscriptionDate();
            }

            Calendar invoicingCalendar = CalendarService.initializeCalendar(chargeInstance.getInvoicingCalendar(), defaultInitDate, chargeInstance);
            invoicingDate = invoicingCalendar.nextCalendarDate(applicationDate);
        }

        if (isReservation) {
            if (orderNumberOverride != null)
                walletOperation = new WalletReservation(chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate,
                        orderNumberOverride.equals(ChargeInstance.NO_ORDER_NUMBER) ? null : orderNumberOverride,
                        edr != null ? edr.getParameter1() : chargeInstance.getCriteria1(), edr != null ? edr.getParameter2() : chargeInstance.getCriteria2(), edr != null ? edr.getParameter3() : chargeInstance.getCriteria3(),
                        edr != null ? edr.getParameter4() : null, null, startdate, endDate, null, invoicingDate);
            else
                walletOperation = new WalletReservation(chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate,
                        chargeInstance.getOrderNumber(),
                        edr != null ? edr.getParameter1() : chargeInstance.getCriteria1(), edr != null ? edr.getParameter2() : chargeInstance.getCriteria2(), edr != null ? edr.getParameter3() : chargeInstance.getCriteria3(),
                        edr != null ? edr.getParameter4() : null, null, startdate, endDate, null, invoicingDate);
        } else {
            if (orderNumberOverride != null)
                walletOperation = new WalletOperation(chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate,
                        orderNumberOverride.equals(ChargeInstance.NO_ORDER_NUMBER) ? null : orderNumberOverride,
                        edr != null ? edr.getParameter1() : chargeInstance.getCriteria1(), edr != null ? edr.getParameter2() : chargeInstance.getCriteria2(), edr != null ? edr.getParameter3() : chargeInstance.getCriteria3(),
                        edr != null ? edr.getParameter4() : null, null, startdate, endDate, null, invoicingDate);
            else
                walletOperation = new WalletOperation(chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate,
                        chargeInstance.getOrderNumber(),
                        edr != null ? edr.getParameter1() : chargeInstance.getCriteria1(), edr != null ? edr.getParameter2() : chargeInstance.getCriteria2(), edr != null ? edr.getParameter3() : chargeInstance.getCriteria3(),
                        edr != null ? edr.getParameter4() : null, null, startdate, endDate, null, invoicingDate);
        }
        walletOperation.setChargeMode(chargeMode);
        walletOperation.setFullRatingPeriod(fullRatingPeriod);

        Integer sortIndex = getSortIndex(walletOperation);
        walletOperation.setSortIndex(sortIndex);
        walletOperation.setEdr(edr);

        rateBareWalletOperation(walletOperation, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), chargeInstance.getCountry().getId(), chargeInstance.getCurrency());
        log.info("rateCharge walletoperation={}, unitamount={}",walletOperation.getCode(),walletOperation.getUnitAmountWithoutTax());
        RatingResult ratedEDRResult = new RatingResult();
        ratedEDRResult.setWalletOperation(walletOperation);
        
        if(!isVirtual) {
        	walletOperationService.create(walletOperation);
        }else {
        	walletOperation.setUuid(UUID.randomUUID().toString());
        }
    	applyDiscount(ratedEDRResult, walletOperation, isVirtual);
        
        return ratedEDRResult;

    }
    
    public static Integer getSortIndex(WalletOperation wo) {
        if (wo.getChargeInstance() == null) {
            return null;
        }
        ChargeTemplate chargeTemplate = wo.getChargeInstance().getChargeTemplate();
        String expression = chargeTemplate.getSortIndexEl();
        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = new HashMap<>();
        userMap.put("op", wo);

        Integer sortIndex = ValueExpressionWrapper.evaluateExpression(expression, userMap, Integer.class);
        return sortIndex;
    }

    /**
     * Rate a charge and triggerEDR. Same as rateCharge but in addition triggers EDRs, unless its a virtual operation. NOTE: Does not persist WO.
     *
     *
     *
     * @param chargeInstance Charge instance to rate
     * @param applicationDate Date of application
     * @param inputQuantity Input quantity
     * @param quantityInChargeUnits Input quantity converted to charge units. If null, will be calculated automatically
     * @param orderNumberOverride Order number to override. If not provided, will default to an order number from a charge instance
     * @param startDate Charge period start date if applicable
     * @param endDate Charge period end date if applicable.
     * @param fullRatingPeriod Full rating period dates when prorata is applied. In such case startDate-endDate will be shorted than fullRatingPeriod. Is NOT provided when prorata
     *        is not applied.
     * @param chargeMode Charge mode
     * @param edr EDR being rated
     * @param forSchedule - is it to be scheduled
     * @param isVirtual Is this a virtual charge - simulation of rating. Charge instance will be matched by code to the charge instantiated in subscription, EDRS will not be
     *        triggered.
     * @return Rating result containing a rated wallet operation (NOT persisted) and triggered EDRs
     * @throws BusinessException business exception
     * @throws RatingException Failure to rate charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult rateChargeAndTriggerEDRs(ChargeInstance chargeInstance, Date applicationDate, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, String orderNumberOverride, Date startDate, Date endDate,
                                                 DatePeriod fullRatingPeriod, ChargeApplicationModeEnum chargeMode, EDR edr, boolean forSchedule, boolean isVirtual) throws BusinessException, RatingException {

        RatingResult ratedEDRResult = rateCharge(chargeInstance, applicationDate, inputQuantity, quantityInChargeUnits, orderNumberOverride, startDate, endDate, fullRatingPeriod, chargeMode, edr, false, isVirtual);

        // Do not trigger EDRs for virtual or Scheduled operations
        if (forSchedule || isVirtual) {
            return ratedEDRResult;
        }

        WalletOperation walletOperation = ratedEDRResult.getWalletOperation();

        List<EDR> triggeredEdrs = triggerEDRs(walletOperation, edr, isVirtual);
        ratedEDRResult.setTriggeredEDRs(triggeredEdrs);
        return ratedEDRResult;
    }

    /**
     * Create new EDRs if charge has triggerEDRTemplate
     *
     * @param walletOperation Wallet operation
     * @param edr The original event record
     * @param isVirtual do not persist EDR if isVirtual = true
     * @return A list of triggered EDRs
     * @throws BusinessException business exception
     * @throws ChargingEdrOnRemoteInstanceErrorException Failure to communicate with a remote Opencell instance
     */
    private List<EDR> triggerEDRs(WalletOperation walletOperation, EDR edr, boolean isVirtual) throws BusinessException, ChargingEdrOnRemoteInstanceErrorException {

        List<EDR> triggredEDRs = new ArrayList<>();

        ChargeInstance chargeInstance = walletOperation.getChargeInstance();
        UserAccount ua = chargeInstance.getUserAccount();
        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

        EntityManager em = getEntityManager();

        List<TriggeredEDRTemplate> triggeredEDRTemplates = chargeTemplate.getEdrTemplates();
        for (TriggeredEDRTemplate triggeredEDRTemplate : triggeredEDRTemplates) {

            if (StringUtils.isBlank(triggeredEDRTemplate.getConditionEl()) || evaluateBooleanExpression(triggeredEDRTemplate.getConditionEl(), walletOperation, null, walletOperation.getPriceplan(), edr)) {

                MeveoInstance meveoInstance = null;

                if (triggeredEDRTemplate.getMeveoInstance() != null) {
                    meveoInstance = triggeredEDRTemplate.getMeveoInstance();
                }
                if (!StringUtils.isBlank(triggeredEDRTemplate.getOpencellInstanceEL())) {
                    String opencellInstanceCode = evaluateStringExpression(triggeredEDRTemplate.getOpencellInstanceEL(), walletOperation, ua, null, null);
                    meveoInstance = meveoInstanceService.findByCode(opencellInstanceCode);
                }

                if (meveoInstance == null) {
                    EDR newEdr = new EDR();
                    newEdr.setCreated(new Date());
                    newEdr.setEventDate(walletOperation.getOperationDate());
                    newEdr.setOriginBatch(EDR.EDR_TABLE_ORIGIN);
                    newEdr.setOriginRecord("CHRG_" + chargeInstance.getId() + "_" + walletOperation.getOperationDate().getTime());
                    newEdr.setParameter1(evaluateStringExpression(triggeredEDRTemplate.getParam1El(), walletOperation, ua, null, edr));
                    newEdr.setParameter2(evaluateStringExpression(triggeredEDRTemplate.getParam2El(), walletOperation, ua, null, edr));
                    newEdr.setParameter3(evaluateStringExpression(triggeredEDRTemplate.getParam3El(), walletOperation, ua, null, edr));
                    newEdr.setParameter4(evaluateStringExpression(triggeredEDRTemplate.getParam4El(), walletOperation, ua, null, edr));
                    newEdr.setQuantity(BigDecimal.valueOf(evaluateDoubleExpression(triggeredEDRTemplate.getQuantityEl(), walletOperation, ua, null, edr)));

                    Subscription sub = null;

                    if (!StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
                        String subCode = evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), walletOperation, ua, null, edr);
                        sub = subscriptionService.findByCode(subCode);
                        if (sub == null) {
                            log.info("Could not find subscription for code={} (EL={}) in triggered EDR with code {}", subCode, triggeredEDRTemplate.getSubscriptionEl(), triggeredEDRTemplate.getCode());
                        }
                    } else if (walletOperation.getSubscription() != null) {
                        sub = em.getReference(Subscription.class, walletOperation.getSubscription().getId());
                    }

                    if (sub != null) {
                        newEdr.setSubscription(sub);
                        log.info("trigger EDR from code {}", triggeredEDRTemplate.getCode());

                        if (triggeredEDRTemplate.getTriggeredEdrScript() != null) {
                            newEdr = triggeredEdrScriptService.updateEdr(triggeredEDRTemplate.getTriggeredEdrScript().getCode(), newEdr, walletOperation);
                        }

                        if (!isVirtual) {
                            edrService.create(newEdr);
                        }

                        triggredEDRs.add(newEdr);

                    } else {
                        // removed for the case of product instance on user account without subscription
                        // throw new BusinessException("cannot find subscription for the trigerred EDR with code " + triggeredEDRTemplate.getCode());
                    }

                } else {
                    if (StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
                        throw new BusinessException("TriggeredEDRTemplate.subscriptionEl must not be null and must point to an existing Access.");
                    }

                    CDR cdr = new CDR();
                    String subCode = evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), walletOperation, ua, null, edr);
                    cdr.setAccessCode(subCode);
                    cdr.setEventDate(walletOperation.getOperationDate());
                    cdr.setParameter1(evaluateStringExpression(triggeredEDRTemplate.getParam1El(), walletOperation, ua, null, edr));
                    cdr.setParameter2(evaluateStringExpression(triggeredEDRTemplate.getParam2El(), walletOperation, ua, null, edr));
                    cdr.setParameter3(evaluateStringExpression(triggeredEDRTemplate.getParam3El(), walletOperation, ua, null, edr));
                    cdr.setParameter4(evaluateStringExpression(triggeredEDRTemplate.getParam4El(), walletOperation, ua, null, edr));
                    cdr.setQuantity(BigDecimal.valueOf(evaluateDoubleExpression(triggeredEDRTemplate.getQuantityEl(), walletOperation, ua, null, edr)));

                    String url = "api/rest/billing/mediation/chargeCdr";
                    Response response = meveoInstanceService.callTextServiceMeveoInstance(url, meveoInstance, cdr.toCsv());
                    ActionStatus actionStatus = response.readEntity(ActionStatus.class);
                    log.trace("Triggered remote EDR response {}", actionStatus);

                    if (actionStatus != null && ActionStatusEnum.SUCCESS != actionStatus.getStatus()) {
                        throw new ChargingEdrOnRemoteInstanceErrorException("Error charging EDR. Error code " + actionStatus.getErrorCode() + ", info " + actionStatus.getMessage());

                    } else if (actionStatus == null) {
                        throw new ChargingEdrOnRemoteInstanceErrorException("Error charging EDR. No response code from API.");
                    }
                }
            }
        }
        return triggredEDRs;
    }

    /**
     * Rate or rerate a Wallet operation - determine a unit price, lookup tax and calculate total amounts. Unless price is overridden, consults price plan for a unit price to
     * charge.
     *
     * THIS IS A SINGLE PLACE WHERE RATING SHOULD OCCUR
     *
     * @param bareWalletOperation operation
     * @param unitPriceWithoutTaxOverridden Unit price without tax - An overridden price
     * @param unitPriceWithTaxOverriden unit price with tax - An overridden price
     * @param buyerCountryId Buyer's country id
     * @param buyerCurrency Buyer's trading currency
     * @throws BusinessException business exception
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    
    public void rateBareWalletOperation(WalletOperation bareWalletOperation, BigDecimal unitPriceWithoutTaxOverridden, BigDecimal unitPriceWithTaxOverriden, Long buyerCountryId, TradingCurrency buyerCurrency)
            throws BusinessException, RatingException {

        ChargeInstance chargeInstance = bareWalletOperation.getChargeInstance();
    	AccountingArticle accountingArticle=accountingArticleService.getAccountingArticleByChargeInstance(chargeInstance);
    	bareWalletOperation.setAccountingArticle(accountingArticle);
        // Let charge template's rating script handle all the rating
        if (chargeInstance != null && chargeInstance.getChargeTemplate().getRatingScript() != null) {

            if (unitPriceWithoutTaxOverridden != null) {
                bareWalletOperation.setUnitAmountWithoutTax(unitPriceWithoutTaxOverridden);
                bareWalletOperation.setOverrodePrice(true);
            }
            if (unitPriceWithTaxOverriden != null) {
                bareWalletOperation.setUnitAmountWithTax(unitPriceWithTaxOverriden);
                bareWalletOperation.setOverrodePrice(true);
            }

            executeRatingScript(bareWalletOperation, chargeInstance.getChargeTemplate().getRatingScript());

            // Use a standard price plan approach to rating
        } else {

            RecurringChargeTemplate recChargeTemplate = null;
            if (chargeInstance instanceof RecurringChargeInstance) {
                recChargeTemplate = ((RecurringChargeInstance) chargeInstance).getRecurringChargeTemplate();
            }

            // Determine and set tax if it was not set before.
            // An absence of tax class and presence of tax means that tax was set manually and should not be recalculated at invoicing time.
            if (bareWalletOperation.getTax() == null) {

                TaxInfo taxInfo = taxMappingService.determineTax(chargeInstance, bareWalletOperation.getOperationDate(), accountingArticle);
                if(taxInfo==null) {
                	throw new BusinessException("No tax found for the chargeInstance "+chargeInstance.getCode());
                }
                bareWalletOperation.setTaxClass(taxInfo.taxClass);
                bareWalletOperation.setTax(taxInfo.tax);
                bareWalletOperation.setTaxPercent(taxInfo.tax.getPercent());
            }

            PricePlanMatrix pricePlan = null;
            if ((unitPriceWithoutTaxOverridden == null && appProvider.isEntreprise()) || (unitPriceWithTaxOverriden == null && !appProvider.isEntreprise())) {

                List<PricePlanMatrix> chargePricePlans = getActivePricePlansByChargeCode(bareWalletOperation.getCode());
                if (chargePricePlans == null || chargePricePlans.isEmpty()) {
                    throw new NoPricePlanException("No price plan for charge code " + bareWalletOperation.getCode());
                }
                Subscription subscription=bareWalletOperation.getSubscription();
                BillingAccount billingAccount=subscription.getUserAccount().getBillingAccount();
                CustomerAccount customerAccount=billingAccount.getCustomerAccount();
                Customer customer=customerAccount.getCustomer();
                Contract contract=contractService.getContractByAccount(customer, billingAccount, customerAccount);
                ServiceInstance serviceInstance=chargeInstance.getServiceInstance();
                ChargeTemplate  chargeTemplate=chargeInstance.getChargeTemplate();
                OfferTemplate offerTemplate=subscription.getOffer();
                ContractItem contractItem = null;
                if(contract!=null && serviceInstance!=null) {
                    contractItem = contractItemService.getApplicableContractItem(contract, offerTemplate, serviceInstance.getCode(), chargeTemplate);
                }
                if (contractItem!=null && ContractRateTypeEnum.FIXED.equals(contractItem.getContractRateType())) {

                    if (contractItem.getPricePlan() != null) {
                        PricePlanMatrix pricePlanMatrix = contractItem.getPricePlan();
                        PricePlanMatrixVersion ppmVersion = pricePlanMatrixVersionService.getLastPublishedVersion(pricePlanMatrix.getCode());
                        if (ppmVersion != null) {
                            PricePlanMatrixLine pricePlanMatrixLine = pricePlanMatrixService.loadPrices(ppmVersion, bareWalletOperation);
                            unitPriceWithoutTaxOverridden = pricePlanMatrixLine.getPricetWithoutTax();
                        }

                    } else {
                        unitPriceWithoutTaxOverridden = contractItem.getAmountWithoutTax();
                    }
                }


                if(unitPriceWithoutTaxOverridden==null) {
                    pricePlan = ratePrice(chargePricePlans, bareWalletOperation, buyerCountryId, buyerCurrency);
                    if (pricePlan == null) {
                        throw new NoPricePlanException("No price plan matched for charge code " + bareWalletOperation.getCode());

                    }
                    //for V11 needs
//                else if ((pricePlan.getAmountWithoutTax() == null && appProvider.isEntreprise()) || (pricePlan.getAmountWithTax() == null && !appProvider.isEntreprise())) {
//                    throw new NoPricePlanException("Price plan " + pricePlan.getId() + " does not contain amounts for charge " + bareWalletOperation.getCode());
//                }
                    log.debug("Will apply priceplan {} for {}", pricePlan.getId(), bareWalletOperation.getCode());

                    PricePlanMatrixVersion ppmVersion = pricePlanMatrixVersionService.getLastPublishedVersion(pricePlan.getCode());
                    if (ppmVersion != null) {

                        if (!ppmVersion.isMatrix()) {
                            if (appProvider.isEntreprise()) {
                                unitPriceWithoutTaxOverridden = ppmVersion.getAmountWithoutTax();
                                if (ppmVersion.getAmountWithoutTaxEL() != null) {
                                    unitPriceWithoutTaxOverridden = evaluateAmountExpression(ppmVersion.getAmountWithoutTaxEL(),
                                            bareWalletOperation, bareWalletOperation.getChargeInstance().getUserAccount(),
                                            null, unitPriceWithoutTaxOverridden);
                                    if (unitPriceWithoutTaxOverridden == null) {
                                        throw new PriceELErrorException("Can't evaluate price for price plan " + ppmVersion.getId()
                                                + " EL:" + ppmVersion.getAmountWithoutTaxEL());
                                    }
                                }

                            } else {
                                unitPriceWithTaxOverriden = ppmVersion.getAmountWithTax();
                                if (ppmVersion.getAmountWithTaxEL() != null) {
                                    unitPriceWithTaxOverriden = evaluateAmountExpression(ppmVersion.getAmountWithTaxEL(),
                                            bareWalletOperation, bareWalletOperation.getWallet().getUserAccount(), null,
                                            unitPriceWithoutTaxOverridden);
                                    if (unitPriceWithTaxOverriden == null) {
                                        throw new PriceELErrorException("Can't evaluate price for price plan " + ppmVersion.getId()
                                                + " EL:" + ppmVersion.getAmountWithTaxEL());
                                    }
                                }
                            }
                        } else {
                            PricePlanMatrixLine pricePlanMatrixLine = pricePlanMatrixService.loadPrices(ppmVersion, bareWalletOperation);
                            if (pricePlanMatrixLine != null) {
                                unitPriceWithoutTaxOverridden = pricePlanMatrixLine.getPricetWithoutTax();
                            }
                            if (unitPriceWithoutTaxOverridden == null) {
                                throw new PriceELErrorException("no price for price plan version " + ppmVersion.getId()
                                        + "and charge instance : " + bareWalletOperation.getChargeInstance());
                            }
                        }

                    } else {
                        if (appProvider.isEntreprise()) {
                            unitPriceWithoutTaxOverridden = pricePlan.getAmountWithoutTax();
                            if (pricePlan.getAmountWithoutTaxEL() != null) {
                                unitPriceWithoutTaxOverridden = evaluateAmountExpression(pricePlan.getAmountWithoutTaxEL(), bareWalletOperation, bareWalletOperation.getChargeInstance().getUserAccount(), pricePlan,
                                        unitPriceWithoutTaxOverridden);
                                if (unitPriceWithoutTaxOverridden == null) {
                                    throw new PriceELErrorException("Can't evaluate price for price plan " + pricePlan.getId() + " EL:" + pricePlan.getAmountWithoutTaxEL());
                                }
                            }

                        } else {
                            unitPriceWithTaxOverriden = pricePlan.getAmountWithTax();
                            if (pricePlan.getAmountWithTaxEL() != null) {
                                unitPriceWithTaxOverriden = evaluateAmountExpression(pricePlan.getAmountWithTaxEL(), bareWalletOperation, bareWalletOperation.getWallet().getUserAccount(), pricePlan,
                                        unitPriceWithoutTaxOverridden);
                                if (unitPriceWithTaxOverriden == null) {
                                    throw new PriceELErrorException("Can't evaluate price for price plan " + pricePlan.getId() + " EL:" + pricePlan.getAmountWithTaxEL());
                                }
                            }
                        }
                    }
                    if (contractItem != null && ContractRateTypeEnum.PERCENTAGE.equals(contractItem.getContractRateType()) && contractItem.getRate() > 0) {
                        BigDecimal amount = unitPriceWithoutTaxOverridden.abs().multiply(BigDecimal.valueOf(contractItem.getRate()).divide(HUNDRED));
                        if (amount != null && unitPriceWithoutTaxOverridden.compareTo(amount) > 0)
                            unitPriceWithoutTaxOverridden = unitPriceWithoutTaxOverridden.subtract(amount);

                    }
                }

            } else {
                bareWalletOperation.setOverrodePrice(true);
            }

            // if the wallet operation correspond to a recurring charge that is
            // shared, we divide the price by the number of
            // shared charges
            if (recChargeTemplate != null && recChargeTemplate.getShareLevel() != null) {
                RecurringChargeInstance recChargeInstance = (RecurringChargeInstance) chargeInstance;
                int sharedQuantity = getSharedQuantity(recChargeTemplate.getShareLevel(), recChargeInstance.getCode(), bareWalletOperation.getOperationDate(), recChargeInstance);
                if (sharedQuantity > 0) {
                    if (appProvider.isEntreprise()) {
                        unitPriceWithoutTaxOverridden = unitPriceWithoutTaxOverridden.divide(new BigDecimal(sharedQuantity), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    } else {
                        unitPriceWithTaxOverriden = unitPriceWithTaxOverriden.divide(new BigDecimal(sharedQuantity), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    }
                    log.info("charge is shared {} times, so unit price is {}", sharedQuantity, unitPriceWithoutTaxOverridden);
                }
            }
            // Override wallet operation parameters using PP EL parameters
            bareWalletOperation = overrideWalletOperationParameters(bareWalletOperation, pricePlan);

            calculateAmounts(bareWalletOperation, unitPriceWithoutTaxOverridden, unitPriceWithTaxOverriden);

            // calculate WO description based on EL from Price plan
            if (pricePlan != null && pricePlan.getWoDescriptionEL() != null) {
                String woDescription = evaluateStringExpression(pricePlan.getWoDescriptionEL(), bareWalletOperation, null, null, null);
                if (woDescription != null) {
                    bareWalletOperation.setDescription(woDescription);
                }
            }

            // get invoiceSubCategory based on EL from Price plan
            if (pricePlan != null && pricePlan.getInvoiceSubCategoryEL() != null) {
                String invoiceSubCategoryCode = evaluateStringExpression(pricePlan.getInvoiceSubCategoryEL(), bareWalletOperation,
                        bareWalletOperation.getWallet() != null ? bareWalletOperation.getWallet().getUserAccount() : null, null, null);
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
        }

        // Execute a final rating script set on offer template
        if (bareWalletOperation.getOfferTemplate() != null && bareWalletOperation.getOfferTemplate().getGlobalRatingScriptInstance() != null) {
            log.trace("Will execute an offer level rating script for offer {}", bareWalletOperation.getOfferTemplate());
            executeRatingScript(bareWalletOperation, bareWalletOperation.getOfferTemplate().getGlobalRatingScriptInstance());
        }

    }

    /**
     * Override wallet operation parameters using EL paramaters in the price plan.
     *
     * @param bareWalletOperation the wallet operation
     * @param pricePlan the Price plan
     * @return a wallet operation
     */
    private WalletOperation overrideWalletOperationParameters(WalletOperation bareWalletOperation, PricePlanMatrix pricePlan) {
        if (pricePlan != null && StringUtils.isNotBlank(pricePlan.getParameter1El())) {
            String parameter1 = evaluateStringExpression(pricePlan.getParameter1El(), bareWalletOperation, null, pricePlan, null);
            if (parameter1 != null) {
                bareWalletOperation.setParameter1(parameter1);
            }
        }
        if (pricePlan != null && StringUtils.isNotBlank(pricePlan.getParameter2El())) {
            String parameter2 = evaluateStringExpression(pricePlan.getParameter2El(), bareWalletOperation, null, pricePlan, null);
            if (parameter2 != null) {
                bareWalletOperation.setParameter2(parameter2);
            }
        }
        if (pricePlan != null && StringUtils.isNotBlank(pricePlan.getParameter3El())) {
            String parameter3 = evaluateStringExpression(pricePlan.getParameter3El(), bareWalletOperation, null, pricePlan, null);
            if (parameter3 != null) {
                bareWalletOperation.setParameter3(parameter3);
            }
        }

        return bareWalletOperation;
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
        if(unitPrice == null)
        	throw new BusinessException("No unit price found");
        	
        // process ratingEL here
        if (walletOperation.getPriceplan() != null) {
            String ratingEl = walletOperation.getPriceplan().getTotalAmountEL();
            if (!StringUtils.isBlank(ratingEl)) {
                amount = BigDecimal.valueOf(evaluateDoubleExpression(ratingEl, walletOperation, walletOperation.getWallet().getUserAccount(), null, null));
            }
        }

        if (amount == null) {
            amount = walletOperation.getQuantity().multiply(unitPrice);
        }

        // Unit prices and unit taxes are with higher precision
        BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(unitPrice, unitPrice, walletOperation.getTaxPercent(), appProvider.isEntreprise(), rounding, roundingMode.getRoundingMode());
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(amount, amount, walletOperation.getTaxPercent(), appProvider.isEntreprise(), rounding, roundingMode.getRoundingMode());

        walletOperation.setUnitAmountWithoutTax(unitAmounts[0]);
        walletOperation.setUnitAmountWithTax(unitAmounts[1]);
        walletOperation.setUnitAmountTax(unitAmounts[2]);
        walletOperation.setAmountWithoutTax(amounts[0]);
        walletOperation.setAmountWithTax(amounts[1]);
        walletOperation.setAmountTax(amounts[2]);

        // we override the wo amount if minimum amount el is set on price plan
        if (walletOperation.getPriceplan() != null && !StringUtils.isBlank(walletOperation.getPriceplan().getMinimumAmountEL())) {
            BigDecimal minimumAmount = BigDecimal.valueOf(evaluateDoubleExpression(walletOperation.getPriceplan().getMinimumAmountEL(), walletOperation, walletOperation.getWallet().getUserAccount(), null, null));

            if ((appProvider.isEntreprise() && walletOperation.getAmountWithoutTax().compareTo(minimumAmount) < 0) || (!appProvider.isEntreprise() && walletOperation.getAmountWithTax().compareTo(minimumAmount) < 0)) {

                // Remember the raw calculated amount
                walletOperation.setRawAmountWithoutTax(walletOperation.getAmountWithoutTax());
                walletOperation.setRawAmountWithTax(walletOperation.getAmountWithTax());

                amounts = NumberUtils.computeDerivedAmounts(minimumAmount, minimumAmount, walletOperation.getTaxPercent(), appProvider.isEntreprise(), rounding, roundingMode.getRoundingMode());

                walletOperation.setAmountWithoutTax(amounts[0]);
                walletOperation.setAmountWithTax(amounts[1]);
                walletOperation.setAmountTax(amounts[2]);
            }
        }
    }

    /**
     * Find a matching price plan for a given wallet operation
     *
     * @param listPricePlan List of price plans to consider
     * @param bareOperation Wallet operation to lookup price plan for
     * @param buyerCountryId Buyer's county id
     * @param buyerCurrency Buyer's trading currency
     * @return Matched price plan
     * @throws BusinessException Business exception
     */
    private PricePlanMatrix ratePrice(List<PricePlanMatrix> listPricePlan, WalletOperation bareOperation, Long buyerCountryId, TradingCurrency buyerCurrency) throws BusinessException {
        // FIXME: the price plan properties could be null !
        Date startDate = bareOperation.getStartDate();
        Date endDate = bareOperation.getEndDate();

        RecurringChargeTemplate recChargeTemplate = null;
        ChargeInstance chargeInstance = bareOperation.getChargeInstance();
        if (chargeInstance instanceof RecurringChargeInstance) {
            recChargeTemplate = ((RecurringChargeInstance) chargeInstance).getRecurringChargeTemplate();
        }

        for (PricePlanMatrix pricePlan : listPricePlan) {

            log.trace("Try to verify price plan {} for WO {}", pricePlan.getId(), bareOperation.getCode());

            Seller seller = pricePlan.getSeller();
            boolean sellerAreEqual = seller == null || seller.getId().equals(bareOperation.getSeller().getId());
            if (!sellerAreEqual) {
                log.trace("The seller of the customer {} is not the same as pricePlan seller {}", bareOperation.getSeller().getId(), seller.getId());
                continue;
            }

            TradingCountry tradingCountry = pricePlan.getTradingCountry();
            boolean countryAreEqual = tradingCountry == null || tradingCountry.getId().equals(buyerCountryId);
            if (!countryAreEqual) {
                log.trace("The countryId={} of the billing account is not the same as pricePlan with countryId={}", buyerCountryId, tradingCountry.getId());
                continue;
            }

            TradingCurrency tradingCurrency = pricePlan.getTradingCurrency();
            boolean currencyAreEqual = tradingCurrency == null || (buyerCurrency != null && buyerCurrency.getId().equals(tradingCurrency.getId()));
            if (!currencyAreEqual) {
                log.trace("The currency of the customer account {} is not the same as pricePlan currency {}", (buyerCurrency != null ? buyerCurrency.getCurrencyCode() : "null"), tradingCurrency.getId());
                continue;
            }
            Date subscriptionDate = bareOperation.getSubscriptionDate();
            Date startSubscriptionDate = pricePlan.getStartSubscriptionDate();
            Date endSubscriptionDate = pricePlan.getEndSubscriptionDate();
            boolean subscriptionDateInPricePlanPeriod = subscriptionDate == null || ((startSubscriptionDate == null || subscriptionDate.after(startSubscriptionDate) || subscriptionDate.equals(startSubscriptionDate))
                    && (endSubscriptionDate == null || subscriptionDate.before(endSubscriptionDate)));
            if (!subscriptionDateInPricePlanPeriod) {
                log.trace("The subscription date {} is not in the priceplan subscription range {} - {}", subscriptionDate, startSubscriptionDate, endSubscriptionDate);
                continue;
            }

            int subscriptionAge = 0;
            Date operationDate = bareOperation.getOperationDate();
            if (subscriptionDate != null && operationDate != null) {
                subscriptionAge = DateUtils.monthsBetween(operationDate, DateUtils.addDaysToDate(subscriptionDate, -1));
            }

            boolean subscriptionMinAgeOK = pricePlan.getMinSubscriptionAgeInMonth() == null || subscriptionAge >= pricePlan.getMinSubscriptionAgeInMonth();
            if (!subscriptionMinAgeOK) {
                log.trace("The subscription age={} is less than the priceplan subscription age min={}", subscriptionAge, pricePlan.getMinSubscriptionAgeInMonth());
                continue;
            }
            Long maxSubscriptionAgeInMonth = pricePlan.getMaxSubscriptionAgeInMonth();
            boolean subscriptionMaxAgeOK = maxSubscriptionAgeInMonth == null || maxSubscriptionAgeInMonth == 0 || subscriptionAge < maxSubscriptionAgeInMonth;
            if (!subscriptionMaxAgeOK) {
                log.trace("The subscription age {} is greater than the priceplan subscription age max {}", subscriptionAge, maxSubscriptionAgeInMonth);
                continue;
            }

            Date startRatingDate = pricePlan.getStartRatingDate();
            Date endRatingDate = pricePlan.getEndRatingDate();
            boolean applicationDateInPricePlanPeriod = (startRatingDate == null || operationDate.after(startRatingDate) || operationDate.equals(startRatingDate))
                    && (endRatingDate == null || operationDate.before(endRatingDate));
            if (!applicationDateInPricePlanPeriod) {
                log.trace("The application date {} is not in the priceplan application range {} - {}", operationDate, startRatingDate, endRatingDate);
                continue;
            }

            String criteria1Value = pricePlan.getCriteria1Value();
            boolean criteria1SameInPricePlan = criteria1Value == null || criteria1Value.equals(bareOperation.getParameter1());
            if (!criteria1SameInPricePlan) {
                log.trace("The operation param1 {} is not compatible with price plan criteria 1: {}", bareOperation.getParameter1(), criteria1Value);
                continue;
            }
            String criteria2Value = pricePlan.getCriteria2Value();
            String parameter2 = bareOperation.getParameter2();
            boolean criteria2SameInPricePlan = criteria2Value == null || criteria2Value.equals(parameter2);
            if (!criteria2SameInPricePlan) {
                log.trace("The operation param2 {} is not compatible with price plan criteria 2: {}", parameter2, criteria2Value);
                continue;
            }
            String criteria3Value = pricePlan.getCriteria3Value();
            boolean criteria3SameInPricePlan = criteria3Value == null || criteria3Value.equals(bareOperation.getParameter3());
            if (!criteria3SameInPricePlan) {
                log.trace("The operation param3 {} is not compatible with price plan criteria 3: {}", bareOperation.getParameter3(), criteria3Value);
                continue;
            }
            if (!StringUtils.isBlank(pricePlan.getCriteriaEL())) {
                UserAccount ua = bareOperation.getWallet().getUserAccount();
                if (!evaluateBooleanExpression(pricePlan.getCriteriaEL(), bareOperation, ua, pricePlan, null)) {
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
                    log.trace("The operation offerCode {} is not compatible with price plan offerCode: {}", bareOperation.getOfferTemplate() != null ? bareOperation.getOfferTemplate() : bareOperation.getOfferCode(),
                            ppOfferTemplate);
                    continue;
                }
            }

            BigDecimal maxQuantity = pricePlan.getMaxQuantity();
            BigDecimal quantity = bareOperation.getQuantity();
            boolean quantityMaxOk = maxQuantity == null || maxQuantity.compareTo(quantity) > 0;
            if (!quantityMaxOk) {
                log.trace("The quantity " + quantity + " is strictly greater than " + maxQuantity);
                continue;
            }

            BigDecimal minQuantity = pricePlan.getMinQuantity();
            boolean quantityMinOk = minQuantity == null || minQuantity.compareTo(quantity) <= 0;
            if (!quantityMinOk) {
                log.trace("The quantity " + quantity + " is less than " + minQuantity);
                continue;
            }
            if ((recChargeTemplate != null && recChargeTemplate.isProrataOnPriceChange()) && (!isStartDateBetween(startDate, pricePlan.getValidityFrom(), pricePlan.getValidityDate()) || !isEndDateBetween(endDate, startDate, pricePlan.getValidityDate()))) {
                    continue;
            }
            Calendar validityCalendar = pricePlan.getValidityCalendar();
            boolean validityCalendarOK = validityCalendar == null || validityCalendar.previousCalendarDate(operationDate) != null;
            if (validityCalendarOK) {
                bareOperation.setPriceplan(pricePlan);
                return pricePlan;
            } else if (validityCalendar != null) {
                log.trace("The operation date " + operationDate + " does not match pricePlan validity calendar " + validityCalendar.getCode() + "period range ");
            }

        }
        return null;
    }

    private boolean isStartDateBetween(Date date, Date from, Date to) {
        return (from != null && (date.equals(from) || (date.after(from))));
    }

    private boolean isEndDateBetween(Date date, Date from, Date to) {
        return date.after(from) && (to == null || (date.before(to) || date.equals(to)));
    }

    /**
     * Re-rate wallet operations. Each wallet operation is rerated independently and marked as failed to rerate if error occurs.
     *
     * @param woIds Ids of wallet operations to be re-rated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     * @throws RatingException Operation re-rating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void reRateInNewTx(List<Long> woIds, boolean useSamePricePlan) throws BusinessException, RatingException {

        for (Long woId : woIds) {

            try {
                ratingServiceNewTX.reRateInNewTx(woId, useSamePricePlan);

            } catch (RatingException e) {
                log.trace("Failed to rerate Wallet operation {}: {}", woId, e.getRejectionReason());
                walletOperationService.markAsFailedToRerateInNewTx(woId, e);

            } catch (BusinessException e) {
                log.error("Failed to rerate Wallet operation {}: {}", woId, e.getMessage(), e);
                walletOperationService.markAsFailedToRerateInNewTx(woId, e);
            }
        }
    }

    /**
     * Re-rate wallet operations together. Each wallet operation is rerated and marked as failed to rerate if error occurs.
     *
     * @param woIds Ids of wallet operations to be re-rated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     * @throws RatingException Operation re-rating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public void reRate(List<Long> woIds, boolean useSamePricePlan) throws BusinessException, RatingException {

        for (Long woId : woIds) {

            try {
                reRate(woId, useSamePricePlan);

            } catch (RatingException e) {
                log.trace("Failed to rerate Wallet operation {}: {}", woId, e.getRejectionReason());
                throw e;

            } catch (BusinessException e) {
                log.error("Failed to rerate Wallet operation {}: {}", woId, e.getMessage(), e);
                throw e;
            }
        }

    }

    /**
     * Rerate wallet operation in new transaction
     * 
     * @param operationToRerateId wallet operation to be rerated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     * @throws RatingException Operation rerating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void reRateInNewTx(Long operationToRerateId, boolean useSamePricePlan) throws BusinessException, RatingException {
        reRate(operationToRerateId, useSamePricePlan);
    }

    /**
     * Rerate wallet operation
     *
     * @param operationToRerateId wallet operation to be rerated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     * @throws RatingException Operation rerating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
	public void reRate(Long operationToRerateId, boolean useSamePricePlan) throws BusinessException, RatingException {
		reRate(operationToRerateId, useSamePricePlan,false);
	}
    /**
     * Rerate wallet operation
     *
     * @param operationToRerateId wallet operation to be rerated
     * @param useSamePricePlan true if same price plan will be used
     * @param reRateRecChIns true if the recurringChangeInstance should be reRatin, for example the calendar changing
     * @throws BusinessException business exception
     * @throws RatingException Operation rerating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
	public void reRate(Long operationToRerateId, boolean useSamePricePlan,boolean reRateRecChIns) throws BusinessException, RatingException {

		WalletOperation operationToRerate = getEntityManager().find(WalletOperation.class, operationToRerateId);
		if (operationToRerate.getStatus() != WalletOperationStatusEnum.TO_RERATE) {
			return;
		}

		ChargeInstance chargeInstance = operationToRerate.getChargeInstance();
		ChargeMainTypeEnum chargeType = chargeInstance.getChargeMainType();
		WalletOperation operationReRated = operationToRerate.getUnratedClone();

		if (chargeType == ChargeMainTypeEnum.RECURRING) {
			RecurringChargeInstance recurringChargeInstance = recurringChargeInstanceService.findById(operationToRerate.getChargeInstance().getId());
			if (!reRateRecChIns) {

				Date partFrom = operationToRerate.getStartDate();
				Date partTo = operationToRerate.getEndDate();
				Date overallFrom = walletOperationService.getRecurringPeriodStartDate(recurringChargeInstance, operationToRerate.getOperationDate());
				Date overallTo = walletOperationService.getRecurringPeriodEndDate(recurringChargeInstance, operationToRerate.getOperationDate());
            	BigDecimal prorata = DateUtils.calculateProrataRatio(partFrom, partTo, overallFrom, overallTo, false);

				if(prorateSubscriptionCharges(operationToRerate, recurringChargeInstance) || prorateTerminationCharges(operationToRerate, recurringChargeInstance) ) {					
					operationReRated.setQuantity(recurringChargeInstance.getQuantity().multiply(prorata));
				} else {
					operationReRated.setQuantity(recurringChargeInstance.getQuantity());
				}
				

				log.debug("operationReRated.quantity:" + operationReRated.getQuantity());

			} else {

				recurringChargeInstanceService.rerateRecurringCharge(chargeInstance.getId(), recurringChargeInstance.getSubscriptionDate(),
						recurringChargeInstance.getNextChargeDate(), false);
				return;
			}
		}

		// Change related OPEN or REJECTED Rated transaction status to RERATED
		RatedTransaction ratedTransaction = operationToRerate.getRatedTransaction();
		if (ratedTransaction != null && (ratedTransaction.getStatus() == RatedTransactionStatusEnum.OPEN || ratedTransaction.getStatus() == RatedTransactionStatusEnum.REJECTED)) {
			ratedTransaction.changeStatus(RatedTransactionStatusEnum.RERATED);
		}

		PricePlanMatrix priceplan = operationReRated.getPriceplan();
		WalletInstance wallet = operationReRated.getWallet();
		UserAccount userAccount = wallet.getUserAccount();

		if (useSamePricePlan && priceplan != null) {
			BigDecimal unitAmountWithTax = operationReRated.getUnitAmountWithTax();
			BigDecimal unitAmountWithoutTax = operationReRated.getUnitAmountWithoutTax();

			if (appProvider.isEntreprise()) {
				unitAmountWithoutTax = priceplan.getAmountWithoutTax();
				if (priceplan.getAmountWithoutTaxEL() != null) {
					unitAmountWithoutTax = evaluateAmountExpression(priceplan.getAmountWithoutTaxEL(), operationReRated, userAccount, priceplan, unitAmountWithoutTax);
					if (unitAmountWithoutTax == null) {
						throw new BusinessException("Can't find unitPriceWithoutTax from PP :" + priceplan.getAmountWithoutTaxEL());
					}
				}

			} else {
				unitAmountWithTax = priceplan.getAmountWithTax();
				if (priceplan.getAmountWithTaxEL() != null) {
					unitAmountWithTax = evaluateAmountExpression(priceplan.getAmountWithTaxEL(), operationReRated, userAccount, priceplan, unitAmountWithoutTax);
					if (unitAmountWithTax == null) {
						throw new BusinessException("Can't find unitPriceWithoutTax from PP :" + priceplan.getAmountWithTaxEL());
					}
				}
			}

			calculateAmounts(operationReRated, unitAmountWithoutTax, unitAmountWithTax);

		} else {
			operationReRated.setUnitAmountWithoutTax(null);
			operationReRated.setUnitAmountWithTax(null);
			operationReRated.setUnitAmountTax(null);
			operationReRated.setChargeMode(ChargeApplicationModeEnum.RERATING);

			rateBareWalletOperation(operationReRated, null, null, priceplan == null || priceplan.getTradingCountry() == null ? null : priceplan.getTradingCountry().getId(),
					priceplan != null ? priceplan.getTradingCurrency() : null);
		}
		create(operationReRated);

		getEntityManager().createNamedQuery("WalletOperation.setStatusToReratedWithReratedWo").setParameter("now", new Date()).setParameter("newWo", operationReRated)
				.setParameter("id", operationToRerateId).executeUpdate();

	}

    private boolean prorateSubscriptionCharges(WalletOperation operationToRerate, RecurringChargeInstance recurringChargeInstance) {
    	boolean prorateSubscription = recurringChargeInstance.getRecurringChargeTemplate().getSubscriptionProrata() != null
				&& recurringChargeInstance.getRecurringChargeTemplate().getSubscriptionProrata();
    	    	
		if (!StringUtils.isBlank(recurringChargeInstance.getRecurringChargeTemplate().getSubscriptionProrataEl())) {
			prorateSubscription = recurringChargeTemplateService.matchExpression(recurringChargeInstance.getRecurringChargeTemplate().getSubscriptionProrataEl(),
					recurringChargeInstance.getServiceInstance(), null, recurringChargeInstance.getRecurringChargeTemplate(), recurringChargeInstance);
		}
				
		return prorateSubscription  && DateUtils.compare(recurringChargeInstance.getServiceInstance().getSubscriptionDate(), operationToRerate.getStartDate()) == 0;
	}
    
    private boolean prorateTerminationCharges(WalletOperation operationToRerate, RecurringChargeInstance recurringChargeInstance) {
    	    	
    	return walletOperationService.prorateTerminationCharges(recurringChargeInstance) &&  DateUtils.compare(recurringChargeInstance.getServiceInstance().getTerminationDate(), operationToRerate.getEndDate()) == 0;
	}

	/**
     * Evaluate EL expression with BigDecimal as result
     *
     * @param expression EL exception to evaluate
     * @param walletOperation Wallet operation
     * @param ua User account
     * @param priceplan Price plan
     * @param amount Amount used in EL
     * @return Evaluated value from expression.
     */
    private BigDecimal evaluateAmountExpression(String expression, WalletOperation walletOperation, UserAccount ua, PricePlanMatrix priceplan, BigDecimal amount) {

        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, walletOperation, ua, amount, null);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);

    }

    /**
     * Evaluate EL expression with boolean as result
     *
     * @param expression EL exception to evaluate
     * @param walletOperation Wallet operation
     * @param ua User account
     * @param priceplan Price plan
     * @param edr EDR
     * @return true/false True if expression is matched
     * @throws BusinessException Business exception
     */
    private boolean evaluateBooleanExpression(String expression, WalletOperation walletOperation, UserAccount ua, PricePlanMatrix priceplan, EDR edr) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return true;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, walletOperation, ua, null, edr);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
    }

    /**
     * Evaluate EL expression with String as result
     *
     * @param expression EL exception to evaluate
     * @param walletOperation Wallet operation
     * @param ua User account
     * @param priceplan Price plan
     * @param edr EDR
     * @return Evaluated value
     * @throws BusinessException business exception
     */
    private String evaluateStringExpression(String expression, WalletOperation walletOperation, UserAccount ua, PricePlanMatrix priceplan, EDR edr) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, walletOperation, ua, null, edr);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
    }

    /**
     * Evaluate EL expression with Double as result
     *
     * @param expression EL exception to evaluate
     * @param walletOperation Wallet operation
     * @param ua User account
     * @param priceplan Price plan
     * @param edr EDR
     * @return Evaluated value
     * @throws BusinessException business exception
     */
    private Double evaluateDoubleExpression(String expression, WalletOperation walletOperation, UserAccount ua, PricePlanMatrix priceplan, EDR edr) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, walletOperation, ua, null, edr);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, Double.class);
    }

    /**
     * Construct variable context for EL expression evaluation
     *
     * @param expression EL expression
     * @param priceplan Price plan
     * @param walletOperation Wallet operation
     * @param ua User account
     * @param amount Amount
     * @param edr EDR
     * @return A map of variables
     */
    private Map<Object, Object> constructElContext(String expression, PricePlanMatrix priceplan, WalletOperation walletOperation, UserAccount ua, BigDecimal amount, EDR edr) {

        Map<Object, Object> userMap = new HashMap<>();

        ChargeInstance chargeInstance = walletOperation.getChargeInstance();
        if ((walletOperation.getChargeInstance() instanceof HibernateProxy)) {
            chargeInstance = (ChargeInstance) ((HibernateProxy) walletOperation.getChargeInstance()).getHibernateLazyInitializer().getImplementation();
        }
        if (edr != null) {
            userMap.put(ValueExpressionWrapper.VAR_EDR, edr);
        }
        userMap.put(ValueExpressionWrapper.VAR_WALLET_OPERATION, walletOperation);
        if (amount != null) {
            userMap.put(ValueExpressionWrapper.VAR_AMOUNT, amount.doubleValue());
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_ACCESS) >= 0 && walletOperation.getEdr() != null && walletOperation.getEdr().getAccessCode() != null) {
            Access access = accessService.findByUserIdAndSubscription(walletOperation.getEdr().getAccessCode(), chargeInstance.getSubscription(), walletOperation.getEdr().getEventDate());
            userMap.put(ValueExpressionWrapper.VAR_ACCESS, access);
        }

        if (expression.indexOf(ValueExpressionWrapper.VAR_PRICE_PLAN) >= 0 || expression.indexOf(ValueExpressionWrapper.VAR_PRICE_PLAN_SHORT) >= 0) {
            if (priceplan == null && walletOperation.getPriceplan() != null) {
                priceplan = walletOperation.getPriceplan();
            }
            if (priceplan != null) {
                userMap.put(ValueExpressionWrapper.VAR_PRICE_PLAN, priceplan);
                userMap.put(ValueExpressionWrapper.VAR_PRICE_PLAN_SHORT, priceplan);
            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE_SHORT) >= 0 || expression.indexOf(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE) >= 0) {
            ChargeTemplate charge = chargeInstance.getChargeTemplate();
            userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE_SHORT, charge);
            userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE, charge);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_SERVICE_INSTANCE) >= 0) {
            ServiceInstance service = chargeInstance.getServiceInstance();
            if (service != null) {
                userMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, service);
            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CPQ_QUOTE) >= 0) {
            ServiceInstance service = chargeInstance.getServiceInstance();
            if (service != null) {
            	CpqQuote quote=service.getQuoteProduct()!=null?service.getQuoteProduct().getQuote():null;
            	if(quote!=null) {
            		userMap.put(ValueExpressionWrapper.VAR_CPQ_QUOTE, quote);
            	}
                
            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_QUOTE_VERSION) >= 0) {
            ServiceInstance service = chargeInstance.getServiceInstance();
            if (service != null) {
            	QuoteVersion quoteVersion=service.getQuoteProduct()!=null?service.getQuoteProduct().getQuoteVersion():null;
            	if(quoteVersion!=null) {
            		userMap.put(ValueExpressionWrapper.VAR_QUOTE_VERSION, quoteVersion);
            	}
                
            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_PRODUCT_INSTANCE) >= 0) {
            ProductInstance productInstance = null;
            if (chargeInstance instanceof ProductChargeInstance) {
                productInstance = ((ProductChargeInstance) chargeInstance).getProductInstance();

            }
            if (productInstance != null) {
                userMap.put(ValueExpressionWrapper.VAR_PRODUCT_INSTANCE, productInstance);
            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_OFFER) >= 0) {
            OfferTemplate offer = chargeInstance.getSubscription().getOffer();
            userMap.put(ValueExpressionWrapper.VAR_OFFER, offer);
        }
        if (expression.contains(ValueExpressionWrapper.VAR_USER_ACCOUNT) || expression.contains(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) || expression.contains(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT)
                || expression.contains(ValueExpressionWrapper.VAR_CUSTOMER_SHORT) || expression.contains(ValueExpressionWrapper.VAR_CUSTOMER)) {
            if (ua == null) {
                ua = chargeInstance.getUserAccount();
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_USER_ACCOUNT) >= 0) {
                userMap.put(ValueExpressionWrapper.VAR_USER_ACCOUNT, ua);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
                userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, ua.getBillingAccount());
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT) >= 0) {
                userMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, ua.getBillingAccount().getCustomerAccount());
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_SHORT) >= 0 || expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER) >= 0) {
                userMap.put(ValueExpressionWrapper.VAR_CUSTOMER_SHORT, ua.getBillingAccount().getCustomerAccount().getCustomer());
                userMap.put(ValueExpressionWrapper.VAR_CUSTOMER, ua.getBillingAccount().getCustomerAccount().getCustomer());
            }
        }

        if (expression.indexOf(ValueExpressionWrapper.VAR_PROVIDER) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_PROVIDER, appProvider);
        }

        return userMap;
    }

    /**
     * Execute a rating script
     *
     * @param bareWalletOperation Wallet operation to rate
     * @param scriptInstance Script to execute
     * @throws RatingException Rating exception
     */
    private void executeRatingScript(WalletOperation bareWalletOperation, ScriptInstance scriptInstance) throws RatingException {

        String scriptInstanceCode = scriptInstance.getCode();
        try {
            if (log.isTraceEnabled()) {
                log.trace("Will execute {} script {} for charge {}", bareWalletOperation.getPriceplan() != null ? "priceplan" : "rating", scriptInstanceCode, bareWalletOperation.getChargeInstance().getId());
            }

            scriptInstanceService.executeCached(bareWalletOperation, scriptInstanceCode, null);

        } catch (RatingException e) {
            throw e;

        } catch (BusinessException e) {
            throw new RatingScriptExecutionErrorException("Failed when run script " + scriptInstanceCode + ", info " + e.getMessage(), e);
        }
    }
    

    public void applyDiscount(RatingResult ratingResult, WalletOperation walletOperation, boolean isVirtual) {
    	ChargeInstance chargeInstance = walletOperation.getChargeInstance();
    	HashSet<DiscountPlanInstance> discountPlanInstances = new HashSet<DiscountPlanInstance>();
    	if(chargeInstance.getServiceInstance() != null) {
    		discountPlanInstances.addAll(chargeInstance.getServiceInstance().getAllDiscountPlanInstances());
    	}
    	if (walletOperation.getSubscription() != null) {
    		discountPlanInstances.addAll(walletOperation.getSubscription().getAllDiscountPlanInstances());
    	}
    	if (walletOperation.getSubscription() != null) {
    		discountPlanInstances.addAll(walletOperation.getSubscription().getUserAccount().getBillingAccount().getAllDiscountPlanInstances());
    	}
    	var accountingArticle = walletOperation.getAccountingArticle()!=null?walletOperation.getAccountingArticle():accountingArticleService.getAccountingArticleByChargeInstance(chargeInstance);
    	List<DiscountPlanItem>  applicableDiscountPlanItems = new ArrayList<DiscountPlanItem>();
    	List<DiscountPlanItem>  fixedDiscountPlanItems = new ArrayList<DiscountPlanItem>();
    	if(!discountPlanInstances.isEmpty()) {
    		DiscountPlan discountPlan =null;
    		for(DiscountPlanInstance discountPlanInstance: discountPlanInstances) {
    			discountPlan=discountPlanInstance.getDiscountPlan();

    			applicableDiscountPlanItems.addAll(discountPlanItemService.getApplicableDiscountPlanItems(walletOperation.getBillingAccount(), discountPlan, walletOperation.getSubscription(), walletOperation, accountingArticle,DiscountPlanItemTypeEnum.PERCENTAGE, walletOperation.getOperationDate()));
    			fixedDiscountPlanItems.addAll(
    					discountPlanItemService.getApplicableDiscountPlanItems(walletOperation.getBillingAccount(), discountPlan, 
    							walletOperation.getSubscription(), walletOperation, walletOperation.getAccountingArticle(), DiscountPlanItemTypeEnum.FIXED, walletOperation.getOperationDate()));
    		}
    		if(!applicableDiscountPlanItems.isEmpty()) {
    			Seller seller = walletOperation.getSeller() != null ? walletOperation.getSeller() : walletOperation.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
    			ratingResult.getWalletOperations().addAll(discountPlanService.calculateDiscountplanItems(applicableDiscountPlanItems, seller, walletOperation.getBillingAccount(), walletOperation.getOperationDate(), walletOperation.getQuantity(), 
    					walletOperation.getUnitAmountWithoutTax(), walletOperation.getCode(), walletOperation.getWallet(), walletOperation.getOfferTemplate(), 
    					walletOperation.getServiceInstance(), walletOperation.getSubscription(), walletOperation.getDescription(), isVirtual, chargeInstance, walletOperation, DiscountPlanTypeEnum.PRODUCT,DiscountPlanTypeEnum.OFFER,DiscountPlanTypeEnum.QUOTE));
    		}
    		if(!fixedDiscountPlanItems.isEmpty()) {
    			ratingResult.getEligibleFixedDiscountItems().addAll(fixedDiscountPlanItems);
    		}

    	}

    }

}