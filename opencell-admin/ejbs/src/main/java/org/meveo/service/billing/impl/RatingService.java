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
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ChargingEdrOnRemoteInstanceErrorException;
import org.meveo.admin.exception.CommunicateToRemoteInstanceException;
import org.meveo.admin.exception.CounterInstantiationException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.NoTaxException;
import org.meveo.admin.exception.PriceELErrorException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.exception.RatingScriptExecutionErrorException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.commons.utils.ELUtils;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.DatePeriod;
import org.meveo.model.RatingResult;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.DiscountPlanInstanceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
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
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.contract.ContractRateTypeEnum;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.EDR;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.cpq.ContractItemService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.mediation.MediationsettingService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.catalog.TriggeredEdrScript;
import org.meveo.service.script.catalog.TriggeredEdrScriptInterface;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;

/**
 * Rate charges such as {@link org.meveo.model.catalog.OneShotChargeTemplate}, {@link org.meveo.model.catalog.RecurringChargeTemplate} and {@link org.meveo.model.catalog.UsageChargeTemplate}. Generate the
 * {@link org.meveo.model.billing.WalletOperation} with the appropriate values.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public abstract class RatingService extends PersistenceService<WalletOperation> {

    @EJB
    private SubscriptionService subscriptionService;

    @Inject
    private TaxMappingService taxMappingService;

    @Inject
    private MeveoInstanceService meveoInstanceService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private PricePlanSelectionService pricePlanSelectionService;

    @Inject
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private ContractService contractService;

    @Inject
    private ContractItemService contractItemService;

    @Inject
    protected CounterInstanceService counterInstanceService;

    final private static BigDecimal HUNDRED = new BigDecimal("100");
    
    @Inject
    private DiscountPlanService discountPlanService;
    @Inject
    private DiscountPlanItemService discountPlanItemService;
    @Inject
    private AccountingArticleService accountingArticleService;

    @Inject
    private RecurringRatingService recurringRatingService;
    
    @Inject
    private ELUtils elUtils;

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
            log.error("failed to get shared quantity", e);
        }
        return result;
    }

    /**
     * Rate a charge - instantiate a wallet operation. Note: DOES NOT persist walletOperation to DB.
     *
     * @param chargeInstance Charge instance to rate
     * @param applicationDate Date of application
     * @param inputQuantity Input quantity
     * @param quantityInChargeUnits Input quantity converted to charge units. If null, will be calculated automatically
     * @param orderNumberOverride Order number to override. If not provided, will default to an order number from a charge instance
     * @param startdate Charge period start date if applicable
     * @param endDate Charge period end date if applicable.
     * @param chargeMode Charge mode
     * @param fullRatingPeriod Full rating period dates when prorata is applied. In such case startDate-endDate will be shorted than fullRatingPeriod. Is NOT provided when prorata is not applied.
     * @param edr EDR being rated
     * @param reservation - Reservation the rating is for
     * @param isVirtual Is this a virtual charge - simulation of rating, charge instance will be matched by code to the charge instantiated in subscription
     * @return Rating result containing a rated wallet operation (NOT persisted)
     * @throws InvalidELException Failure to evaluate EL expression
     * @throws RatingException Failure to rate charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    protected RatingResult rateCharge(ChargeInstance chargeInstance, Date applicationDate, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, String orderNumberOverride, Date startdate, Date endDate,
            DatePeriod fullRatingPeriod, ChargeApplicationModeEnum chargeMode, EDR edr, Reservation reservation, boolean isVirtual) throws InvalidELException, RatingException {

        WalletOperation walletOperation = null;

        if (quantityInChargeUnits == null) {
            quantityInChargeUnits = chargeTemplateService.evaluateRatingQuantity(chargeInstance.getChargeTemplate(), inputQuantity);
        }

        Date invoicingDate = null;
        if (chargeInstance.getInvoicingCalendar() != null) {

            Date defaultInitDate = null;
            RecurringChargeInstance charge = getEntityManager().getReference(RecurringChargeInstance.class, chargeInstance.getId());
            if (chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.RECURRING && charge.getSubscriptionDate() != null) {
                defaultInitDate = charge.getSubscriptionDate();
            } else if (chargeInstance.getServiceInstance() != null) {
                defaultInitDate = chargeInstance.getServiceInstance().getSubscriptionDate();
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                defaultInitDate = chargeInstance.getSubscription().getSubscriptionDate();
            }
            boolean isApplyInAdvance = recurringRatingService.isApplyInAdvance(charge);
        	if(isApplyInAdvance) {
        		invoicingDate=applicationDate;
        	}else {
        		Calendar invoicingCalendar = CalendarService.initializeCalendar(chargeInstance.getInvoicingCalendar(), defaultInitDate, chargeInstance);
        		invoicingDate = invoicingCalendar.nextCalendarDate(applicationDate);
        	}
        }
        String extraParam = edr != null ? paramBeanFactory.getInstance().getPropertyAsBoolean("edr.propagate.extraParameter", false) ? edr.getExtraParameter(): edr.getParameter4() : null;
        if (reservation != null) {
            if (orderNumberOverride != null) {
                walletOperation = new WalletReservation(chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate, orderNumberOverride.equals(ChargeInstance.NO_ORDER_NUMBER) ? null : orderNumberOverride,
                    edr != null ? edr.getParameter1() : chargeInstance.getCriteria1(), edr != null ? edr.getParameter2() : chargeInstance.getCriteria2(), edr != null ? edr.getParameter3() : chargeInstance.getCriteria3(),
                            extraParam, null, startdate, endDate, null, invoicingDate, reservation);
            } else {
                walletOperation = new WalletReservation(chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate, chargeInstance.getOrderNumber(),
                    edr != null ? edr.getParameter1() : chargeInstance.getCriteria1(), edr != null ? edr.getParameter2() : chargeInstance.getCriteria2(), edr != null ? edr.getParameter3() : chargeInstance.getCriteria3(),
                            extraParam, null, startdate, endDate, null, invoicingDate, reservation);
            }

        } else {
            if (orderNumberOverride != null) {
                walletOperation = new WalletOperation(chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate, orderNumberOverride.equals(ChargeInstance.NO_ORDER_NUMBER) ? null : orderNumberOverride,
                    edr != null ? edr.getParameter1() : chargeInstance.getCriteria1(), edr != null ? edr.getParameter2() : chargeInstance.getCriteria2(), edr != null ? edr.getParameter3() : chargeInstance.getCriteria3(),
                            extraParam , null, startdate, endDate, null, invoicingDate);
            } else {
                walletOperation = new WalletOperation(chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate, chargeInstance.getOrderNumber(),
                    edr != null ? edr.getParameter1() : chargeInstance.getCriteria1(), edr != null ? edr.getParameter2() : chargeInstance.getCriteria2(), edr != null ? edr.getParameter3() : chargeInstance.getCriteria3(),
                            extraParam , null, startdate, endDate, null, invoicingDate);
            }
        }
        walletOperation.setChargeMode(chargeMode);
        walletOperation.setFullRatingPeriod(fullRatingPeriod);

        Integer sortIndex = getSortIndex(walletOperation);
        walletOperation.setSortIndex(sortIndex);
        walletOperation.setEdr(edr);

        RatingResult ratedEDRResult = rateBareWalletOperation(walletOperation, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), chargeInstance.getCountry().getId(), chargeInstance.getCurrency(), isVirtual);
        
        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

        if (walletOperation.getInputUnitDescription() == null) {
            walletOperation.setInputUnitDescription(chargeTemplate.getInputUnitDescription());
        }
        if (walletOperation.getRatingUnitDescription() == null) {
            walletOperation.setRatingUnitDescription(chargeTemplate.getRatingUnitDescription());
        }
        if (walletOperation.getInvoiceSubCategory() == null) {
            walletOperation.setInvoiceSubCategory(chargeTemplate.getInvoiceSubCategory());
        }
 

    	applyDiscount(ratedEDRResult, walletOperation, isVirtual);
        
        return ratedEDRResult;

    }
    
    public static Integer getSortIndex(WalletOperation wo) throws InvalidELException {
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
     * Rate a charge and instantiate triggered EDRs. Same as rateCharge but in addition instantiates triggered EDRs, unless its a virtual operation. NOTE: Does not persist WO nor EDRs.
     *
     * @param chargeInstance Charge instance to rate
     * @param applicationDate Date of application
     * @param inputQuantity Input quantity
     * @param quantityInChargeUnits Input quantity converted to charge units. If null, will be calculated automatically
     * @param orderNumberOverride Order number to override. If not provided, will default to an order number from a charge instance
     * @param startDate Charge period start date if applicable
     * @param endDate Charge period end date if applicable.
     * @param fullRatingPeriod Full rating period dates when prorata is applied. In such case startDate-endDate will be shorted than fullRatingPeriod. Is NOT provided when prorata is not applied.
     * @param chargeMode Charge mode
     * @param edr EDR being rated
     * @param reservation - Reservation the rating is for
     * @param forSchedule - is it to be scheduled
     * @param isVirtual Is this a virtual charge - simulation of rating. Charge instance will be matched by code to the charge instantiated in subscription, EDRS will not be triggered.
     * @return Rating result containing a rated wallet operation (NOT persisted) and triggered EDRs (NOT persisted)
     * @throws ValidationException Failure to rate due to data or EL validation exceptions
     * @throws RatingException Failure to rate charge due to lack of funds, inconsistency or other rating related failure
     * @throws CommunicateToRemoteInstanceException Failed to communicate to a remote Opencell instance when sending Triggered EDRs
     */
    public RatingResult rateChargeAndInstantiateTriggeredEDRs(ChargeInstance chargeInstance, Date applicationDate, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, String orderNumberOverride, Date startDate,
            Date endDate, DatePeriod fullRatingPeriod, ChargeApplicationModeEnum chargeMode, EDR edr, Reservation reservation, boolean forSchedule, boolean isVirtual)
            throws ValidationException, RatingException, CommunicateToRemoteInstanceException {

        RatingResult ratedEDRResult = rateCharge(chargeInstance, applicationDate, inputQuantity, quantityInChargeUnits, orderNumberOverride, startDate, endDate, fullRatingPeriod, chargeMode, edr, reservation, isVirtual);

        // Do not trigger EDRs for virtual or Scheduled operations
        if (forSchedule || isVirtual) {
            return ratedEDRResult;
        }

        for (WalletOperation walletOperation : ratedEDRResult.getWalletOperations()) {
            List<EDR> triggeredEdrs = instantiateTriggeredEDRs(walletOperation, edr, isVirtual, true);
            ratedEDRResult.addTriggeredEDRs(triggeredEdrs);
        }
        return ratedEDRResult;
    }

    @Inject
    private MediationsettingService mediationsettingService;
    /**
     * Instantiate new EDRs if charge has triggerEDRTemplate. EDRs are NOT persisted.
     *
     * @param walletOperation Wallet operation
     * @param edr The original event record
     * @param isVirtual Do not send EDR to a remote server if isVirtual = true
     * @return A list of triggered EDRs
     * @throws RatingException Failed to run a script to update EDR
     * @throws CommunicateToRemoteInstanceException Failure to communicate with a remote Opencell instance
     * @throws ChargingEdrOnRemoteInstanceErrorException Failure to charge CDR on a remote Opencell instance
     * @throws InvalidELException Failed to evaluate EL expression
     * @throws ElementNotFoundException Subscription as resolved from EL expression was not found
     */
    public List<EDR> instantiateTriggeredEDRs(WalletOperation walletOperation, EDR edr, boolean isVirtual, boolean evaluatEdrVersioning)
            throws RatingException, InvalidELException, ElementNotFoundException, CommunicateToRemoteInstanceException, ChargingEdrOnRemoteInstanceErrorException {


        List<EDR> triggredEDRs = new ArrayList<>();

        ChargeInstance chargeInstance = walletOperation.getChargeInstance();
        UserAccount ua = chargeInstance.getUserAccount();
        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
	    
        List<TriggeredEDRTemplate> triggeredEDRTemplates = chargeTemplate.getEdrTemplates();

        for (TriggeredEDRTemplate triggeredEDRTemplate : triggeredEDRTemplates) {

            if (!StringUtils.isBlank(triggeredEDRTemplate.getConditionEl()) && !elUtils.evaluateBooleanExpression(triggeredEDRTemplate.getConditionEl(), walletOperation, null, walletOperation.getPriceplan(), null, edr)) {
                continue;
            }

            MeveoInstance meveoInstance = null;

            if (triggeredEDRTemplate.getMeveoInstance() != null) {
                meveoInstance = triggeredEDRTemplate.getMeveoInstance();
            }
            if (!StringUtils.isBlank(triggeredEDRTemplate.getOpencellInstanceEL())) {
                String opencellInstanceCode = elUtils.evaluateStringExpression(triggeredEDRTemplate.getOpencellInstanceEL(), walletOperation, ua, null, null);
                meveoInstance = meveoInstanceService.findByCode(opencellInstanceCode);
            }

            if (meveoInstance == null) {
                EDR newEdr = new EDR();
                newEdr.setCreated(new Date());
                newEdr.setEventDate(walletOperation.getOperationDate());
                newEdr.setOriginBatch(EDR.EDR_TABLE_ORIGIN);
                newEdr.setOriginRecord("CHRG_" + chargeInstance.getId() + "_" + walletOperation.getOperationDate().getTime());
                newEdr.setParameter1(elUtils.evaluateStringExpression(triggeredEDRTemplate.getParam1El(), walletOperation, ua, null, edr));
                newEdr.setParameter2(elUtils.evaluateStringExpression(triggeredEDRTemplate.getParam2El(), walletOperation, ua, null, edr));
                newEdr.setParameter3(elUtils.evaluateStringExpression(triggeredEDRTemplate.getParam3El(), walletOperation, ua, null, edr));
                newEdr.setParameter4(elUtils.evaluateStringExpression(triggeredEDRTemplate.getParam4El(), walletOperation, ua, null, edr));
                newEdr.setQuantity(BigDecimal.valueOf(elUtils.evaluateDoubleExpression(triggeredEDRTemplate.getQuantityEl(), walletOperation, ua, null, edr)));
                newEdr.setWalletOperation(walletOperation);
                newEdr.setBusinessKey(walletOperation.getBusinessKey());

                Subscription sub = null;

                if (!StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
                    String subCode = elUtils.evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), walletOperation, ua, null, edr);
                    if (subCode != null) {
                        sub = subscriptionService.findByCode(subCode);
                        if (sub == null) {
                            throw new ElementNotFoundException(subCode + " from EL " + triggeredEDRTemplate.getSubscriptionEl() + " in Triggered EDR " + triggeredEDRTemplate.getCode(), "Subscription");
                        }
                    }
                }
                if (sub == null) {
                    sub = walletOperation.getSubscription();
                }

                newEdr.setSubscription(sub);

                log.debug("trigger EDR from code {}", triggeredEDRTemplate.getCode());

                if (triggeredEDRTemplate.getTriggeredEdrScript() != null) {
                    newEdr = updateTriggeredEdrByScript(triggeredEDRTemplate.getTriggeredEdrScript().getCode(), newEdr, walletOperation);
                }

                triggredEDRs.add(newEdr);

            } else if (!isVirtual) {
                if (StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
                    throw new InvalidParameterException("TriggeredEDRTemplate.subscriptionEl must not be null and must point to an existing Access.");
                }

                CDR cdr = new CDR();
                String subCode = elUtils.evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), walletOperation, ua, null, edr);
                cdr.setAccessCode(subCode);
                cdr.setEventDate(walletOperation.getOperationDate());
                cdr.setParameter1(elUtils.evaluateStringExpression(triggeredEDRTemplate.getParam1El(), walletOperation, ua, null, edr));
                cdr.setParameter2(elUtils.evaluateStringExpression(triggeredEDRTemplate.getParam2El(), walletOperation, ua, null, edr));
                cdr.setParameter3(elUtils.evaluateStringExpression(triggeredEDRTemplate.getParam3El(), walletOperation, ua, null, edr));
                cdr.setParameter4(elUtils.evaluateStringExpression(triggeredEDRTemplate.getParam4El(), walletOperation, ua, null, edr));
                cdr.setQuantity(BigDecimal.valueOf(elUtils.evaluateDoubleExpression(triggeredEDRTemplate.getQuantityEl(), walletOperation, ua, null, edr)));

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
        if(evaluatEdrVersioning) {
            mediationsettingService.applyEdrVersioningRule(triggredEDRs, null, true);
        }
        return triggredEDRs;

    }

    private EDR updateTriggeredEdrByScript(String scriptCode, EDR newEdr, WalletOperation walletOperation) throws RatingException {

        try {
            TriggeredEdrScriptInterface scriptInterface = (TriggeredEdrScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);

            Map<String, Object> scriptContext = new HashMap<>();
            scriptContext.put(TriggeredEdrScript.CONTEXT_ENTITY, newEdr);
            scriptContext.put(TriggeredEdrScript.CONTEXT_WO, walletOperation);

            return scriptInterface.updateEdr(scriptContext);

        } catch (RatingException e) {
            throw e;

        } catch (Exception e) {
            throw new RatingScriptExecutionErrorException("Failed when run script " + scriptCode + ", info " + e.getMessage(), e);
        }
    }

    /**
     * Rate a Wallet operation - determine a unit price, lookup tax and calculate total amounts. Unless price is overridden, consults price plan for a unit price to charge.
     *
     * THIS IS A SINGLE PLACE WHERE RATING SHOULD OCCUR
     *
     * @param bareWalletOperation operation
     * @param unitPriceWithoutTaxOverridden Unit price without tax - An overridden price
     * @param unitPriceWithTaxOverridden unit price with tax - An overridden price
     * @param buyerCountryId Buyer's country id
     * @param buyerCurrency Buyer's trading currency
     * @param isVirtual Is this a virtual rating
     * @throws PriceELErrorException Priceplan amount EL expression resolves to NULL
     * @throws InvalidELException Failed to resolve EL expression
     * @throws NoTaxException Unable to determine a tax to apply
     * @throws NoPricePlanException No price plan matched for a charge
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */

    public RatingResult rateBareWalletOperation(WalletOperation bareWalletOperation, BigDecimal unitPriceWithoutTaxOverridden, BigDecimal unitPriceWithTaxOverridden, Long buyerCountryId, TradingCurrency buyerCurrency,
            boolean isVirtual) throws InvalidELException, PriceELErrorException, NoTaxException, NoPricePlanException, RatingException {

        RatingResult ratedEDRResult = new RatingResult();
        WalletOperation discountedWalletOperation=null;
        ChargeInstance chargeInstance = bareWalletOperation.getChargeInstance();
	    
		try {
			String businessKey = (String) ValueExpressionWrapper.evaluateExpression(
					bareWalletOperation.getChargeInstance().getChargeTemplate().getBusinessKeyEl(), Map.of("op", bareWalletOperation), String.class);
			bareWalletOperation.setBusinessKey(businessKey);
		} catch (Exception e) {
			throw new InvalidELException(String.format("Error during businessKeyEl evaluation: subscription=%s, product instance=%s,%s, charge=%s, EDR=%s",
					bareWalletOperation.getSubscription().getCode(), getProductId(bareWalletOperation), getProductCode(bareWalletOperation), 
					getChargeTemplateCode(bareWalletOperation), (bareWalletOperation.getEdr() == null)? null : bareWalletOperation.getEdr().getId()));
		}
	   
	    
		try {
			String businessKey = (String) ValueExpressionWrapper.evaluateExpression(
					bareWalletOperation.getChargeInstance().getChargeTemplate().getBusinessKeyEl(), Map.of("op", bareWalletOperation), String.class);
			bareWalletOperation.setBusinessKey(businessKey);
		} catch (Exception e) {
			throw new InvalidELException(String.format("Error during businessKeyEl evaluation: subscription=%s, product instance=%s,%s, charge=%s, EDR=%s",
					bareWalletOperation.getSubscription().getCode(), getProductId(bareWalletOperation), getProductCode(bareWalletOperation), 
					getChargeTemplateCode(bareWalletOperation), (bareWalletOperation.getEdr() == null)? null : bareWalletOperation.getEdr().getId()));
		}
	   
        // Let charge template's rating script handle all the rating
        if (chargeInstance != null && chargeInstance.getChargeTemplate().getRatingScript() != null) {

            if (unitPriceWithoutTaxOverridden != null) {
                bareWalletOperation.setUnitAmountWithoutTax(unitPriceWithoutTaxOverridden);
            }
            if (unitPriceWithTaxOverridden != null) {
                bareWalletOperation.setUnitAmountWithTax(unitPriceWithTaxOverridden);
            }

            executeRatingScript(bareWalletOperation, chargeInstance.getChargeTemplate().getRatingScript(), isVirtual);

            // Use a standard price plan approach to rating
        } else {
            BigDecimal unitPriceWithoutTax = unitPriceWithoutTaxOverridden;
            BigDecimal unitPriceWithTax = unitPriceWithTaxOverridden;
            
            RecurringChargeTemplate recurringChargeTemplate = getRecurringChargeTemplateFromChargeInstance(chargeInstance);

            PricePlanMatrix pricePlan = null;
            BillingAccount billingAccount = bareWalletOperation.getBillingAccount();
            CustomerAccount customerAccount = billingAccount.getCustomerAccount();
            Customer customer = customerAccount.getCustomer();

            //Get the list of customers (current and parents)
            List<Customer> customers = new ArrayList<>();
            getCustomer(customer, customers);
            List<Long> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
            //Get contract by list of customer ids, billing account and customer account
            List<Contract> contracts = contractService.getContractByAccount(ids, billingAccount, customerAccount, bareWalletOperation);
             
            // Unit price was not overridden
            if ((unitPriceWithoutTaxOverridden == null && appProvider.isEntreprise()) || (unitPriceWithTaxOverridden == null && !appProvider.isEntreprise())) {

                Contract contract = lookupSuitableContract(customers, contracts);
                
                // Check if unit price was not overridden by a contract
                // To save the first contract containing Rules by priority (BA->CA->Customer->seller) on WalletOperation.rulesContract
                Contract contractWithRules = lookupSuitableContract(customers, contracts, true);
                bareWalletOperation.setRulesContract(contractWithRules);

                ServiceInstance serviceInstance = chargeInstance.getServiceInstance();
                ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
                ContractItem contractItem = null;
                if (contract != null && serviceInstance != null) {
                    OfferTemplate offerTemplate = serviceInstance.getSubscription().getOffer();
                    if (contract != null && bareWalletOperation.getOperationDate().compareTo(contract.getBeginDate()) >= 0 && bareWalletOperation.getOperationDate().compareTo(contract.getEndDate()) < 0) {
                    Long productId = serviceInstance.getProductVersion() != null ? serviceInstance.getProductVersion().getProduct().getId() : null;
                        contractItem = contractItemService.getApplicableContractItem(contract, offerTemplate, productId, chargeTemplate);
                    }

                    // If contract rate type is Fixed - unit rate comes either from 1. hardcoded in contact item or 2. from a price plan specified in a contract
                    if (contractItem != null && ContractRateTypeEnum.FIXED.equals(contractItem.getContractRateType())) {

                        if (contractItem.getPricePlan() != null) {
							pricePlan = contractItem.getPricePlan();
							PricePlanMatrixLine pricePlanMatrixLine = pricePlanSelectionService.determinePricePlanLine(pricePlan, bareWalletOperation);
                            if (pricePlanMatrixLine != null) {
                                try {
                                    unitPriceWithoutTax = pricePlanMatrixLine.getValue();
                                    if (pricePlan.getScriptInstance() != null) {
                                    	log.debug("start to execute script instance for ratePrice {}", pricePlan);
                                    	executeRatingScript(bareWalletOperation, pricePlan.getScriptInstance(), false);
                                    	unitPriceWithoutTax=bareWalletOperation.getUnitAmountWithoutTax()!=null?bareWalletOperation.getUnitAmountWithoutTax():BigDecimal.ZERO;
                                    	unitPriceWithTax=bareWalletOperation.getUnitAmountWithTax()!=null?bareWalletOperation.getUnitAmountWithTax():BigDecimal.ZERO;
                                    }
                                    
                                    bareWalletOperation.setContract(contract);
                                    bareWalletOperation.setPriceplan(pricePlan);
                                    bareWalletOperation.setPricePlanMatrixVersion(pricePlanMatrixLine.getPricePlanMatrixVersion());
                                    bareWalletOperation.setPricePlanMatrixLine(pricePlanMatrixLine);
                                }catch(NoPricePlanException e) {
                                    log.warn("Price not found for contract : " + contract.getCode(), e);
                                } catch (Exception e) {
                                    log.warn("Error on contract code " + contract.getCode(), e);
                                }
                            }

                        } else {
                            unitPriceWithoutTax = contractItem.getAmountWithoutTax();
                            bareWalletOperation.setContract(contract);
                        }
                    }

                }

                // No associated contract found or contract rate is not fixed - a price discount is applied by contract to a default price 
                if (unitPriceWithoutTax == null) {
                    
                    // Find a default price plan
                    pricePlan = pricePlanSelectionService.determineDefaultPricePlan(bareWalletOperation, buyerCountryId, buyerCurrency);
                    bareWalletOperation.setPriceplan(pricePlan);
                    
                    log.debug("Will apply priceplan {} for {}", pricePlan.getId(), bareWalletOperation.getCode());

                    Amounts unitPrices = determineUnitPrice(pricePlan, bareWalletOperation);
                    unitPriceWithoutTax = unitPrices.getAmountWithoutTax();
                    unitPriceWithTax = unitPrices.getAmountWithTax();
                    bareWalletOperation.setUnitAmountWithoutTax(unitPriceWithoutTax);
                    bareWalletOperation.setUnitAmountWithTax(unitPriceWithTax);
                    if (pricePlan.getScriptInstance() != null) {
                    	log.debug("start to execute script instance for ratePrice {}", pricePlan);
                    	executeRatingScript(bareWalletOperation, pricePlan.getScriptInstance(), false);
                    	unitPriceWithoutTax=bareWalletOperation.getUnitAmountWithoutTax()!=null?bareWalletOperation.getUnitAmountWithoutTax():BigDecimal.ZERO;
                    	unitPriceWithTax=bareWalletOperation.getUnitAmountWithTax()!=null?bareWalletOperation.getUnitAmountWithTax():BigDecimal.ZERO;
                    }

                    BigDecimal amount= BigDecimal.ZERO;
                    BigDecimal discountRate=null;
                    PricePlanMatrixLine pricePlanMatrixLine =null;
                    
                    // A price discount is applied to a default price by a contract
                    if (contractItem != null && ContractRateTypeEnum.PERCENTAGE.equals(contractItem.getContractRateType()) ) {
                    	boolean seperateDiscount=contractItem.isSeparateDiscount();
                        bareWalletOperation.setContract(contract);
                        
                        // Discount rate is hardcoded in a contract
                        if (contractItem.getRate() != null && contractItem.getRate() > 0) {
                            discountRate = BigDecimal.valueOf(contractItem.getRate());
                            amount = unitPriceWithoutTax.abs().multiply(BigDecimal.valueOf(contractItem.getRate()).divide(HUNDRED));
                            if (amount != null && unitPriceWithoutTax.compareTo(amount) > 0 && !seperateDiscount) {
                                unitPriceWithoutTax = unitPriceWithoutTax.subtract(amount);
                            }
                            
                            // Discount rate is specified in a price plan associated to a contact
                        } else if (contractItem.getPricePlan() != null) {
                            pricePlanMatrixLine = pricePlanSelectionService.determinePricePlanLine(contractItem.getPricePlan(), bareWalletOperation);
                            if (pricePlanMatrixLine != null) {
                                discountRate = pricePlanMatrixLine.getValue();
                                if (discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) {
                                    amount = unitPriceWithoutTax.abs().multiply(discountRate.divide(HUNDRED));
                                    if (amount != null && unitPriceWithoutTax.compareTo(amount) > 0 && !seperateDiscount) {
                                        unitPriceWithoutTax = unitPriceWithoutTax.subtract(amount);
                                    }
                                }
                            }
                        }
                        if(seperateDiscount) {
	                        if (bareWalletOperation.getTax() == null) {
		                        TaxInfo taxInfo = taxMappingService.determineTax(bareWalletOperation);
		                        if(taxInfo==null) {
			                        throw new BusinessException("No tax found for the chargeInstance "+ bareWalletOperation.getChargeInstance().getCode());
		                        }
	                        }
                        	discountedWalletOperation=rateDiscountedWalletOperation(bareWalletOperation,unitPriceWithoutTax,amount,discountRate,bareWalletOperation.getBillingAccount(),pricePlanMatrixLine);
                        }
                    }
                }
            } else {
                if (contracts != null && !contracts.isEmpty()) {
                    bareWalletOperation.setRulesContract(addContractWithRules(contracts));
                }
                bareWalletOperation.setOverrodePrice(true);
            }

            // if the wallet operation correspond to a recurring charge that is shared, we divide the price by the number of shared charges
            if (recurringChargeTemplate != null && recurringChargeTemplate.getShareLevel() != null) {
                RecurringChargeInstance recChargeInstance = (RecurringChargeInstance) PersistenceUtils.initializeAndUnproxy(chargeInstance);
                int sharedQuantity = getSharedQuantity(recurringChargeTemplate.getShareLevel(), recChargeInstance.getCode(), bareWalletOperation.getOperationDate(), recChargeInstance);
                if (sharedQuantity > 0) {
                    if (appProvider.isEntreprise()) {
                        unitPriceWithoutTax = unitPriceWithoutTax.divide(new BigDecimal(sharedQuantity), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    } else {
                        unitPriceWithTax = unitPriceWithTax.divide(new BigDecimal(sharedQuantity), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    }
                    log.trace("charge is shared {} times, so unit price is {}", sharedQuantity, unitPriceWithoutTax);
                }
            }
            
            // Override wallet operation parameters using PP EL parameters
            setWalletOperationPropertiesFromAPriceplan(bareWalletOperation, pricePlan);
            
            calculateAmounts(bareWalletOperation, unitPriceWithoutTax, unitPriceWithTax);
        }

        // Execute a final rating script set on offer template
        if (bareWalletOperation.getOfferTemplate() != null && bareWalletOperation.getOfferTemplate().getGlobalRatingScriptInstance() != null) {
            log.trace("Will execute an offer level rating script for offer {}", bareWalletOperation.getOfferTemplate());
            executeRatingScript(bareWalletOperation, bareWalletOperation.getOfferTemplate().getGlobalRatingScriptInstance(), isVirtual);
        }
        
        ratedEDRResult.addWalletOperation(bareWalletOperation);
        if(discountedWalletOperation!=null) {
        	ratedEDRResult.addWalletOperation(discountedWalletOperation);
        }
		return ratedEDRResult;
    }

    private RecurringChargeTemplate getRecurringChargeTemplateFromChargeInstance(ChargeInstance chargeInstance) {
        RecurringChargeTemplate recurringChargeTemplate = null;
        if (chargeInstance != null && chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.RECURRING) {
        	recurringChargeTemplate = ((RecurringChargeInstance) PersistenceUtils.initializeAndUnproxy(chargeInstance)).getRecurringChargeTemplate();
        }
        return recurringChargeTemplate;
    }

    private static String getChargeTemplateCode(WalletOperation bareWalletOperation) {
        String chargeTemplateCode = null;

        if(bareWalletOperation.getChargeInstance() != null && bareWalletOperation.getChargeInstance().getChargeTemplate() != null) {
            chargeTemplateCode = bareWalletOperation.getChargeInstance().getChargeTemplate().getCode();
        }

        return chargeTemplateCode;
    }
    /**
     * Get the customer and all parent customers
     * @param pCustomer Customer
     * @param pCustomerList List of customers (current customer and all parents)
     */
    private void getCustomer(Customer pCustomer, List<Customer> pCustomerList) {
        if(pCustomer != null) {
            pCustomerList.add(pCustomer);
        }
    }

    /**
     * Get Product code
     * @param bareWalletOperation {@link WalletOperation}
     * @return Product code
     */
    private static String getProductCode(WalletOperation bareWalletOperation) {
        String productCode = null;

        if(bareWalletOperation.getServiceInstance() != null && bareWalletOperation.getServiceInstance().getProductVersion() != null
                && bareWalletOperation.getServiceInstance().getProductVersion().getProduct() != null ) {
            productCode = bareWalletOperation.getServiceInstance().getProductVersion().getProduct().getCode();
        }

        return productCode;
    }

    private Contract lookupSuitableContract(List<Customer> customers, List<Contract> contracts) {
        return this.lookupSuitableContract(customers, contracts, false);
    }

    private Contract lookupSuitableContract(List<Customer> customers, List<Contract> contracts, boolean withRules) {
        Contract contract = null;
        if(contracts != null && !contracts.isEmpty()) {
            // Prioritize BA Contract then CA Contract then Customer Hierarchy Contract then Seller Contract
            Optional<Contract> contractLookup = contracts.stream().filter(c -> c.getBillingAccount() != null && (!withRules || (c.getBillingRules()!=null && !c.getBillingRules().isEmpty()))).findFirst()
                    .or(() -> contracts.stream().filter(c -> c.getCustomerAccount() != null && (!withRules || (c.getBillingRules()!=null && !c.getBillingRules().isEmpty()))).findFirst());
            if(contractLookup.isEmpty()) {
                for (Customer iCustomer : customers) {
                    contractLookup = contracts.stream().filter(c -> c.getCustomer() != null && c.getCustomer().getId().equals(iCustomer.getId()) && (!withRules || (c.getBillingRules()!=null && !c.getBillingRules().isEmpty()))).findFirst();
                    if(contractLookup.isPresent()) {
                        break;
                    }
                }
            }
            contract = contractLookup.orElseGet(() -> contracts.get(0));
        }
        return contract;
    }


    private Contract addContractWithRules(List<Contract> contracts) {
        return contracts.stream()
                .filter(c -> c.getBillingAccount() != null && c.getBillingRules()!=null && !c.getBillingRules()
                        .isEmpty())
                .findFirst() // BA Contract
                .or(() -> contracts.stream()
                        .filter(c -> c.getCustomerAccount() != null && c.getBillingRules()!=null && !c.getBillingRules()
                                .isEmpty())
                        .findFirst()) // CA Contract
                .or(() -> contracts.stream()
                        .filter(c -> c.getCustomer() != null && c.getBillingRules()!=null && !c.getBillingRules()
                                .isEmpty())
                        .findFirst()) // Customer Contract
                .orElse(contracts.get(0));
    }

    /**
     * Determine unit price from a priceplan
     * 
     * @param pricePlan Price plan
     * @param wo Wallet operation
     * @return Amount without and with tax
     * @throws InvalidELException Failed to evaluate EL expression
     * @throws PriceELErrorException Amount EL expression evaluated to null
     */
    private Amounts determineUnitPrice(PricePlanMatrix pricePlan, WalletOperation wo) throws PriceELErrorException, InvalidELException {

        BigDecimal priceWithoutTax = null;
        BigDecimal priceWithTax = null;

        ServiceInstance serviceInstance = wo.getServiceInstance();

        PricePlanMatrixVersion ppmVersion = pricePlanSelectionService.getPublishedVersionValidForDate(pricePlan.getId(), serviceInstance, wo.getOperationDate());
        if (ppmVersion != null) {
            wo.setPricePlanMatrixVersion(ppmVersion);
            if (!ppmVersion.isMatrix()) {
                if (appProvider.isEntreprise()) {
                	priceWithoutTax = ppmVersion.getPrice();
                } else {
                	priceWithTax = ppmVersion.getPrice();
                }
                if(wo.getUnitAmountWithoutTax() == null) {
                    wo.setUnitAmountWithoutTax(priceWithoutTax);
                }
                if (ppmVersion.getPriceEL() != null) {
                    priceWithoutTax = elUtils.evaluateAmountExpression(ppmVersion.getPriceEL(), wo, wo.getChargeInstance().getUserAccount(), null, priceWithoutTax).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    if (priceWithoutTax == null) {
                        throw new PriceELErrorException("Can't evaluate price for price plan " + ppmVersion.getId() + " EL:" + ppmVersion.getPriceEL());
                    }
                }
            } else {
                PricePlanMatrixLine pricePlanMatrixLine = pricePlanSelectionService.determinePricePlanLine(ppmVersion, wo);
                if(pricePlanMatrixLine!=null) {
                    wo.setPricePlanMatrixLine(pricePlanMatrixLine);
                    if(appProvider.isEntreprise()) {
                        priceWithoutTax = pricePlanMatrixLine.getValue();
                    } else {
                        priceWithTax = pricePlanMatrixLine.getValue();
                    }
                    if(wo.getUnitAmountWithoutTax() == null) {
                        wo.setUnitAmountWithoutTax(priceWithoutTax);
                    }
                    String amountEL = ppmVersion.getPriceEL();
                    if (!StringUtils.isBlank(amountEL)) {
                        priceWithoutTax = elUtils.evaluateAmountExpression(amountEL, wo, wo.getChargeInstance().getUserAccount(), null, priceWithoutTax);
                    }
                    String amountELPricePlanMatrixLine = pricePlanMatrixLine.getValueEL();
                    if (!StringUtils.isBlank(amountELPricePlanMatrixLine)) {
                        BigDecimal amountElPPML = elUtils.evaluateAmountExpression(amountELPricePlanMatrixLine, wo, wo.getChargeInstance().getUserAccount(), null, priceWithoutTax);
                        if (amountElPPML != null) {
                            if (appProvider.isEntreprise()) {
                                priceWithoutTax = amountElPPML;
                            } else {
                                priceWithTax = amountElPPML;
                            }
                        }
                    }
                }
                if (priceWithoutTax == null && priceWithTax == null) {
                    throw new PriceELErrorException("no price for price plan version " + ppmVersion.getId() + "and charge instance : " + wo.getChargeInstance());
                }
            }
        } else {
            if (appProvider.isEntreprise()) {
                priceWithoutTax = pricePlan.getAmountWithoutTax();
                if (pricePlan.getAmountWithoutTaxEL() != null) {
                    priceWithoutTax = elUtils.evaluateAmountExpression(pricePlan.getAmountWithoutTaxEL(), wo, wo.getChargeInstance().getUserAccount(), pricePlan, priceWithoutTax);
                    if (priceWithoutTax == null) {
                        throw new PriceELErrorException("Can't evaluate price for price plan " + pricePlan.getId() + " EL:" + pricePlan.getAmountWithoutTaxEL());
                    }
                }
            } else {
                priceWithTax = pricePlan.getAmountWithTax();
                if (pricePlan.getAmountWithTaxEL() != null) {
                    priceWithTax = elUtils.evaluateAmountExpression(pricePlan.getAmountWithTaxEL(), wo, wo.getWallet().getUserAccount(), pricePlan, priceWithTax);
                    if (priceWithTax == null) {
                        throw new PriceELErrorException("Can't evaluate price for price plan " + pricePlan.getId() + " EL:" + pricePlan.getAmountWithTaxEL());
                    }
                }
            }
        }
        if(priceWithoutTax == null && priceWithTax == null) {
            throw new BusinessException("Couldn't find a price for charge " + wo.getChargeInstance().getCode() + " and price plan " + pricePlan.getCode() + ": no price version and price plan amount is null");
        }
        return new Amounts(priceWithoutTax, priceWithTax);
    }

    /**
     * Set wallet operation properties using EL parameters in the price plan.
     *
     * @param bareWalletOperation the wallet operation
     * @param pricePlan the Price plan
     * @return a wallet operation
     */
    private void setWalletOperationPropertiesFromAPriceplan(WalletOperation bareWalletOperation, PricePlanMatrix pricePlan) {
        if (pricePlan == null) {
            return;
        }
        if (StringUtils.isNotBlank(pricePlan.getParameter1El())) {
            String parameter1 = elUtils.evaluateStringExpression(pricePlan.getParameter1El(), bareWalletOperation, null, pricePlan, null);
            if (parameter1 != null) {
                bareWalletOperation.setParameter1(parameter1);
            }
        }
        if (StringUtils.isNotBlank(pricePlan.getParameter2El())) {
            String parameter2 = elUtils.evaluateStringExpression(pricePlan.getParameter2El(), bareWalletOperation, null, pricePlan, null);
            if (parameter2 != null) {
                bareWalletOperation.setParameter2(parameter2);
            }
        }
        if (StringUtils.isNotBlank(pricePlan.getParameter3El())) {
            String parameter3 = elUtils.evaluateStringExpression(pricePlan.getParameter3El(), bareWalletOperation, null, pricePlan, null);
            if (parameter3 != null) {
                bareWalletOperation.setParameter3(parameter3);
            }
        }

        // calculate WO description based on EL from Price plan
        if (pricePlan.getWoDescriptionEL() != null) {
            String woDescription = elUtils.evaluateStringExpression(pricePlan.getWoDescriptionEL(), bareWalletOperation, null, null, null);
            if (woDescription != null) {
                bareWalletOperation.setDescription(woDescription);
            }
        }

        // get invoiceSubCategory based on EL from Price plan
        if (pricePlan.getInvoiceSubCategoryEL() != null) {
            String invoiceSubCategoryCode = elUtils.evaluateStringExpression(pricePlan.getInvoiceSubCategoryEL(), bareWalletOperation,
                bareWalletOperation.getWallet() != null ? bareWalletOperation.getWallet().getUserAccount() : null, null, null);
            if (!StringUtils.isBlank(invoiceSubCategoryCode)) {
                InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(invoiceSubCategoryCode);
                if (invoiceSubCategory != null) {
                    bareWalletOperation.setInvoiceSubCategory(invoiceSubCategory);
                }
            }
        }
    }

    /**
     * Calculate, round (if needed) and set total amounts and taxes: [B2C] amountWithoutTax = round(amountWithTax) - round(amountTax) [B2B] amountWithTax = round(amountWithoutTax) + round(amountTax)
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
        if (unitPrice == null)
            throw new BusinessException("No unit price found");

        // process ratingEL here
        if (walletOperation.getPriceplan() != null) {
            String ratingEl = walletOperation.getPriceplan().getTotalAmountEL();
            if (!StringUtils.isBlank(ratingEl)) {
                amount = new BigDecimal(Double.toString(elUtils.evaluateDoubleExpression(ratingEl, walletOperation, walletOperation.getWallet().getUserAccount(), null, null)));
            }
        }

        if (amount == null) {
            amount = walletOperation.getQuantity().multiply(unitPrice);
        }

        walletOperation.setAmountWithoutTax(amount);
        walletOperation.setAmountWithTax(amount);
        AccountingArticle accountingArticle = accountingArticleService.getAccountingArticleByChargeInstance(walletOperation.getChargeInstance(), walletOperation);
        if (accountingArticle == null) {
            throw new RatingException("Unable to match an accounting article for a charge " + walletOperation.getChargeInstance().getId() + "/" + walletOperation.getChargeInstance().getCode());
        }
        walletOperation.setAccountingArticle(accountingArticle);

        TaxInfo taxInfo = taxMappingService.determineTax(walletOperation);
        if(taxInfo==null) {
            throw new BusinessException("No tax found for the chargeInstance "+ walletOperation.getChargeInstance().getCode());
        }
        walletOperation.setTaxClass(taxInfo.taxClass);
        walletOperation.setTax(taxInfo.tax);
        walletOperation.setTaxPercent(taxInfo.tax.getPercent());

        // Unit prices and unit taxes are with higher precision
        BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(unitPrice, unitPrice, walletOperation.getTaxPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(amount, amount, walletOperation.getTaxPercent(), appProvider.isEntreprise(), rounding, roundingMode.getRoundingMode());

        walletOperation.setUnitAmountWithoutTax(unitAmounts[0]);
        walletOperation.setUnitAmountWithTax(unitAmounts[1]);
        walletOperation.setUnitAmountTax(unitAmounts[2]);
        walletOperation.setAmountWithoutTax(amounts[0]);
        walletOperation.setAmountWithTax(amounts[1]);
        walletOperation.setAmountTax(amounts[2]);

        // we override the wo amount if minimum amount el is set on price plan
        if (walletOperation.getPriceplan() != null && !StringUtils.isBlank(walletOperation.getPriceplan().getMinimumAmountEL())) {
            BigDecimal minimumAmount = new BigDecimal(Double.toString(elUtils.evaluateDoubleExpression(walletOperation.getPriceplan().getMinimumAmountEL(), walletOperation, walletOperation.getWallet().getUserAccount(), null, null)));

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
     * Get a rerated copy of a wallet operation. New wallet operation is not persisted nor status of wallet operation to rerate is changed
     *
     * @param walletOperationToRerate wallet operation to be rerated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     * @throws RatingException Operation rerating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult rateRatedWalletOperation(WalletOperation walletOperationToRerate, boolean useSamePricePlan) throws BusinessException, RatingException {

        WalletOperation operation = walletOperationToRerate.getUnratedClone();
        operation.setOperationDate(operation.getEdr() != null ? operation.getEdr().getEventDate() : operation.getOperationDate());
        RatingResult ratingResult = new RatingResult();
        PricePlanMatrix priceplan = operation.getPriceplan();
        WalletInstance wallet = operation.getWallet();
        UserAccount userAccount = wallet.getUserAccount();

        if (useSamePricePlan && priceplan != null) {
            BigDecimal unitAmountWithTax = operation.getUnitAmountWithTax();
            BigDecimal unitAmountWithoutTax = operation.getUnitAmountWithoutTax();

            if (appProvider.isEntreprise()) {
                unitAmountWithoutTax = priceplan.getAmountWithoutTax();
                if (priceplan.getAmountWithoutTaxEL() != null) {
                    unitAmountWithoutTax = elUtils.evaluateAmountExpression(priceplan.getAmountWithoutTaxEL(), operation, userAccount, priceplan, unitAmountWithoutTax);
                    if (unitAmountWithoutTax == null) {
                        throw new BusinessException("Can't find unitPriceWithoutTax from PP :" + priceplan.getAmountWithoutTaxEL());
                    }
                }

            } else {
                unitAmountWithTax = priceplan.getAmountWithTax();
                if (priceplan.getAmountWithTaxEL() != null) {
                    unitAmountWithTax = elUtils.evaluateAmountExpression(priceplan.getAmountWithTaxEL(), operation, userAccount, priceplan, unitAmountWithoutTax);
                    if (unitAmountWithTax == null) {
                        throw new BusinessException("Can't find unitPriceWithoutTax from PP :" + priceplan.getAmountWithTaxEL());
                    }
                }
            }

            calculateAmounts(operation, unitAmountWithoutTax, unitAmountWithTax);

        } else {
            operation.setUnitAmountWithoutTax(null);
            operation.setUnitAmountWithTax(null);
            operation.setUnitAmountTax(null);
            operation.setChargeMode(ChargeApplicationModeEnum.RERATING);

            ratingResult=rateBareWalletOperation(operation, null, null, priceplan == null || priceplan.getTradingCountry() == null ? null : priceplan.getTradingCountry().getId(),
                priceplan != null ? priceplan.getTradingCurrency() : null, false);
        }

        return ratingResult;
    }


    /**
     * Execute a rating script
     *
     * @param bareWalletOperation Wallet operation to rate
     * @param scriptInstance Script to execute
     * @param isVirtual Is this a virtual rating
     * @throws RatingException Rating exception
     */
    private void executeRatingScript(WalletOperation bareWalletOperation, ScriptInstance scriptInstance, boolean isVirtual) throws RatingException {

        String scriptInstanceCode = scriptInstance.getCode();
        try {
            if (log.isTraceEnabled()) {
                log.trace("Will execute {} script {} for charge {}", bareWalletOperation.getPriceplan() != null ? "priceplan" : "rating", scriptInstanceCode, bareWalletOperation.getChargeInstance().getId());
            }
            Map<String, Object> context = new HashMap<>();
            context.put("isVirtual", isVirtual);
            scriptInstanceService.executeCached(bareWalletOperation, scriptInstanceCode, context);

        } catch (RatingException e) {
            throw e;

        } catch (Exception e) {
            throw new RatingScriptExecutionErrorException("Failed when run script " + scriptInstanceCode + ", info " + e.getMessage(), e);
        }
    }
    

    public void applyDiscount(RatingResult ratingResult, WalletOperation walletOperation, boolean isVirtual) {
    	ChargeInstance chargeInstance = walletOperation.getChargeInstance();
    	HashSet<DiscountPlanInstance> discountPlanInstances = new HashSet<>();
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
    	List<DiscountPlanItem>  applicableDiscountPlanItems = new ArrayList<>();
    	List<DiscountPlanItem>  fixedDiscountPlanItems = new ArrayList<>();
    	if(!discountPlanInstances.isEmpty()) {
    		DiscountPlan discountPlan =null;
    		Date operationDate=walletOperation.getOperationDate()!=null?walletOperation.getOperationDate():new Date();
    		for(DiscountPlanInstance discountPlanInstance: discountPlanInstances) {

    			if (!discountPlanInstance.isEffective(operationDate) || discountPlanInstance.getStatus().equals(DiscountPlanInstanceStatusEnum.EXPIRED)) {
                    continue;
                }
    			discountPlan=discountPlanInstance.getDiscountPlan();

    			applicableDiscountPlanItems.addAll(discountPlanItemService.getApplicableDiscountPlanItems(walletOperation.getBillingAccount(), discountPlan, walletOperation.getSubscription(), walletOperation, accountingArticle,DiscountPlanItemTypeEnum.PERCENTAGE, operationDate));
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

    public static boolean isORChargeMatch(ChargeInstance chargeInstance) throws InvalidELException {
        if (chargeInstance.getServiceInstance() != null) {
            boolean anyFalseAttribute = chargeInstance.getServiceInstance().getAttributeInstances().stream()
                    .filter(attributeInstance -> attributeInstance.getAttribute().getAttributeType() == AttributeTypeEnum.BOOLEAN)
                    .filter(attributeInstance -> attributeInstance.getBooleanValue() != null)
                    .filter(attributeInstance -> attributeInstance.getAttribute().getChargeTemplates().contains(chargeInstance.getChargeTemplate()))
                    .anyMatch(attributeInstance -> attributeInstance.getBooleanValue() == null || !attributeInstance.getBooleanValue());

            if (anyFalseAttribute)
                return false;
        }
        if (StringUtils.isBlank(chargeInstance.getChargeTemplate().getFilterExpression())) {
            return true;
        }

        return ValueExpressionWrapper.evaluateToBooleanOneVariable(chargeInstance.getChargeTemplate().getFilterExpression(), "ci", chargeInstance);
    }

    /**
     * Increment accumulator counter by a given value. Will instantiate a counter period if one was not created yet matching the given date. Counter changes are recorded in ratingResult.
     *
     * @param walletOperations Wallet operations
     * @param ratingResult Rating result to record counter changes done.
     * @param isVirtual Is this a virtual operation - no counter period entity exists nor should be persisted
     * @throws CounterInstantiationException Failure to create a new counter period
     */
    protected void incrementAccumulatorCounterValues(List<WalletOperation> walletOperations, RatingResult ratingResult, boolean isVirtual) throws CounterInstantiationException {

        if (walletOperations.isEmpty()) {
            return;
        }
        Map<ChargeInstance, List<WalletOperation>> groupedWOByChargeInstance = walletOperations.stream().collect(Collectors.groupingBy(wo -> wo.getChargeInstance()));

        for (Entry<ChargeInstance, List<WalletOperation>> groupedWos : groupedWOByChargeInstance.entrySet()) {
            ChargeInstance chargeInstance = groupedWos.getKey();
            if (chargeInstance.getAccumulatorCounterInstances() != null && !chargeInstance.getAccumulatorCounterInstances().isEmpty()) {
                List<CounterValueChangeInfo> counterChangeInfo = counterInstanceService.incrementAccumulatorCounterValue(chargeInstance, groupedWos.getValue(), isVirtual);
                ratingResult.addCounterChange(counterChangeInfo);
            }
        }
    }

    /**
     * Revert counter values
     * 
     * @param countersToRevert Counter values to revert. Key = Counter period id, value - value to return
     */
    protected void revertCounterChanges(Map<Long, BigDecimal> countersToRevert) {

        if (countersToRevert == null || countersToRevert.isEmpty()) {
            return;
        }
        log.error("Failed to rate charge will revert the applied counters {}", countersToRevert);

        for (Entry<Long, BigDecimal> counterInfo : countersToRevert.entrySet()) {
            counterInstanceService.incrementCounterValue(counterInfo.getKey(), counterInfo.getValue());
        }
    }
    
    private WalletOperation rateDiscountedWalletOperation(WalletOperation bareWalletOperation,BigDecimal unitPriceWithoutTax,BigDecimal amount,BigDecimal discountValue,BillingAccount billingAccount,
    		PricePlanMatrixLine pricePlanMatrixLine) {
    	
    	ParamBean paramBean = ParamBean.getInstance();
		String defaultArticle = paramBean.getProperty("default.article", "ART-STD");
    	AccountingArticle accountingArticle=null;
        BigDecimal walletOperationDiscountAmount=amount.negate();
    	WalletOperation discountWalletOperation = new WalletOperation();
    	BigDecimal discountedAmount=unitPriceWithoutTax.subtract(amount);
    	BigDecimal taxPercent=bareWalletOperation.getTaxPercent();

        // Unit prices and unit taxes are with higher precision
        BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(walletOperationDiscountAmount, walletOperationDiscountAmount, bareWalletOperation.getTaxPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(walletOperationDiscountAmount, walletOperationDiscountAmount, bareWalletOperation.getTaxPercent(), appProvider.isEntreprise(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        BigDecimal quantity=bareWalletOperation.getQuantity();
    	ChargeInstance chargeInstance=bareWalletOperation.getChargeInstance();
    
    	discountWalletOperation.setCode(bareWalletOperation.getCode());
    	discountWalletOperation.setDescription(bareWalletOperation.getDescription());  
    	discountWalletOperation.setAmountWithoutTax(quantity.compareTo(BigDecimal.ZERO)>0?quantity.multiply(amounts[0]):BigDecimal.ZERO);
    	discountWalletOperation.setAmountWithTax(quantity.compareTo(BigDecimal.ZERO)>0?quantity.multiply(amounts[1]):BigDecimal.ZERO);
    	discountWalletOperation.setAmountTax(quantity.compareTo(BigDecimal.ZERO)>0?quantity.multiply(amounts[2]):BigDecimal.ZERO);
    	discountWalletOperation.setTaxPercent(taxPercent);
    	discountWalletOperation.setUnitAmountWithoutTax(unitAmounts[0]);
    	discountWalletOperation.setUnitAmountWithTax(unitAmounts[1]);
    	discountWalletOperation.setUnitAmountTax(unitAmounts[2]);
    	discountWalletOperation.setQuantity(quantity);
    	discountWalletOperation.setTax(bareWalletOperation.getTax());//
    	discountWalletOperation.setCreated(new Date()); 
    	discountWalletOperation.setSeller(bareWalletOperation.getSeller());
	    discountWalletOperation.setBillingAccount(billingAccount);
    	discountWalletOperation.setWallet(bareWalletOperation.getWallet());
    	discountWalletOperation.setOfferTemplate(bareWalletOperation.getOfferTemplate());
    	discountWalletOperation.setServiceInstance(bareWalletOperation.getServiceInstance());
    	discountWalletOperation.setOperationDate(bareWalletOperation.getOperationDate()); 
    	discountWalletOperation.setChargeInstance(chargeInstance);
    	discountWalletOperation.setInputQuantity(quantity);
    	discountWalletOperation.setCurrency(bareWalletOperation.getCurrency()!=null?bareWalletOperation.getCurrency():billingAccount != null ? billingAccount.getCustomerAccount().getTradingCurrency().getCurrency(): bareWalletOperation.getCurrency());
    	discountWalletOperation.setDiscountedWO(bareWalletOperation); 
    	discountWalletOperation.setDiscountPlanType(DiscountPlanItemTypeEnum.PERCENTAGE);
    	discountWalletOperation.setDiscountValue(discountValue);
    	discountWalletOperation.setDiscountedAmount(discountedAmount);
    	discountWalletOperation.setOrderNumber(bareWalletOperation.getOrderNumber());
    	discountWalletOperation.setSubscription(bareWalletOperation.getSubscription());
    	discountWalletOperation.setUserAccount(bareWalletOperation.getUserAccount());
    	discountWalletOperation.setContract(bareWalletOperation.getContract());
	    discountWalletOperation.setRulesContract(bareWalletOperation.getRulesContract());
        if (pricePlanMatrixLine != null) {
            discountWalletOperation.setPricePlanMatrixVersion(pricePlanMatrixLine.getPricePlanMatrixVersion());
            discountWalletOperation.setPriceplan(pricePlanMatrixLine.getPricePlanMatrixVersion().getPricePlanMatrix());
            discountWalletOperation.setPricePlanMatrixLine(pricePlanMatrixLine);
        }
        discountWalletOperation.setBusinessKey(bareWalletOperation.getBusinessKey());

    	accountingArticle = accountingArticleService.getAccountingArticleByChargeInstance(chargeInstance, discountWalletOperation);
    	if(defaultArticle.equalsIgnoreCase(accountingArticle.getCode())) {
    		accountingArticle=bareWalletOperation.getAccountingArticle();
    	}
    	discountWalletOperation.setAccountingArticle(accountingArticle);
    	
    	log.info("rateDiscountWalletOperation walletOperation code={},discountValue={},UnitAmountWithoutTax={},UnitAmountWithTax={},UnitAmountTax={}",discountWalletOperation.getCode(),discountedAmount,amounts[0],amounts[1],amounts[2]);
    	return discountWalletOperation;
    }    
}