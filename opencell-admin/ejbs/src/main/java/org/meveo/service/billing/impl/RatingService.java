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
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.exception.UnrolledbackBusinessException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.admin.util.NumberUtil;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
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
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

@Stateless
public class RatingService extends BusinessService<WalletOperation> {

    @Inject
    private EdrService edrService;

    @EJB
    private SubscriptionService subscriptionService;

    @EJB
    private RatedTransactionService ratedTransactionService;

    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

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
     * @param chargeTemplate charge template
     * @param subscriptionDate subscription date
     * @param offerCode code of offer
     * @param chargeInstance charge instance
     * @param applicationType type of application
     * @param applicationDate date of application
     * @param amountWithoutTax amount without tax
     * @param amountWithTax amount with tax
     * @param inputQuantity input quantity
     * @param quantity quantity
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
    public WalletOperation prerateChargeApplication(ChargeTemplate chargeTemplate, Date subscriptionDate, String offerCode, ChargeInstance chargeInstance,
            ApplicationTypeEnum applicationType, Date applicationDate, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal inputQuantity, BigDecimal quantity,
            TradingCurrency tCurrency, Long countryId, String languageCode, BigDecimal taxPercent, BigDecimal discountPercent, Date nextApplicationDate,
            InvoiceSubCategory invoiceSubCategory, String criteria1, String criteria2, String criteria3, String orderNumber, Date startdate, Date endDate,
            ChargeApplicationModeEnum mode, UserAccount userAccount) throws BusinessException {
        long startDate = System.currentTimeMillis();
        WalletOperation walletOperation = new WalletOperation();
        Auditable auditable = new Auditable(currentUser);
        walletOperation.setAuditable(auditable);

        // TODO do this in the right place (one time by userAccount)
        BillingAccount billingAccount = userAccount.getBillingAccount();
        boolean isExonerated = billingAccountService.isExonerated(billingAccount);

        if (chargeTemplate.getChargeType().equals(RecurringChargeTemplate.CHARGE_TYPE)) {
            walletOperation.setSubscriptionDate(subscriptionDate);
        }

        walletOperation
            .setQuantity(NumberUtil.getInChargeUnit(quantity, chargeTemplate.getUnitMultiplicator(), chargeTemplate.getUnitNbDecimal(), chargeTemplate.getRoundingMode()));

        walletOperation.setInputQuantity(inputQuantity);
        walletOperation.setRatingUnitDescription(chargeTemplate.getRatingUnitDescription());
        walletOperation.setInputUnitDescription(chargeTemplate.getInputUnitDescription());
        walletOperation.setOperationDate(applicationDate);
        walletOperation.setOrderNumber(orderNumber);
        walletOperation.setParameter1(criteria1);
        walletOperation.setParameter2(criteria2);
        walletOperation.setParameter3(criteria3);
        if (chargeInstance != null) {
            walletOperation.setChargeInstance(chargeInstance);
            if (chargeInstance.getInvoicingCalendar() != null) {
                chargeInstance.getInvoicingCalendar().setInitDate(subscriptionDate);

                walletOperation.setInvoicingDate(chargeInstance.getInvoicingCalendar().nextCalendarDate(walletOperation.getOperationDate()));
            }
        }

        walletOperation.setCode(chargeTemplate.getCode());

        String descTranslated = (chargeInstance == null || chargeInstance.getDescription() == null) ? chargeTemplate.getDescriptionOrCode() : chargeInstance.getDescription();
        Map<String, String> descriptionI18n = chargeTemplate.getDescriptionI18n();
        if (descriptionI18n != null && descriptionI18n.containsKey(languageCode)) {
            descTranslated = descriptionI18n.get(languageCode);
        }

        walletOperation.setDescription(descTranslated);
        walletOperation.setTaxPercent(isExonerated ? BigDecimal.ZERO : taxPercent);
        walletOperation.setCurrency(tCurrency.getCurrency());
        walletOperation.setStartDate(startdate);
        walletOperation.setEndDate(endDate);
        walletOperation.setOfferCode(offerCode);
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
        if (chargeInstance.getSubscription() != null) {
            walletOperation.setBillingAccount(billingAccount);
        }

        BigDecimal unitPriceWithoutTax = amountWithoutTax;
        BigDecimal unitPriceWithTax = null;

        if (unitPriceWithoutTax != null) {
            unitPriceWithTax = amountWithTax;
        }
        log.debug("Before  rateBareWalletOperation:" + (System.currentTimeMillis() - startDate));
        rateBareWalletOperation(walletOperation, unitPriceWithoutTax, unitPriceWithTax, countryId, tCurrency);
        log.debug("After  rateBareWalletOperation:" + (System.currentTimeMillis() - startDate));
        log.debug(" wo amountWithoutTax={}", walletOperation.getAmountWithoutTax());
        return walletOperation;

    }

    
    /**
     * used to rate a oneshot, recurring or product charge and triggerEDR
     * @param chargeInstance charge instance
     * @param applicationType type of application
     * @param applicationDate application date
     * @param amountWithoutTax amoun without tax
     * @param amountWithTax amount with tax
     * @param inputQuantity input quantity
     * @param quantity quantity
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
            BigDecimal amountWithTax, BigDecimal inputQuantity, BigDecimal quantity, TradingCurrency tCurrency, Long countryId, BigDecimal taxPercent, BigDecimal discountPercent,
            Date nextApplicationDate, InvoiceSubCategory invoiceSubCategory, String criteria1, String criteria2, String criteria3, String orderNumber, Date startdate, Date endDate,
            ChargeApplicationModeEnum mode, boolean forSchedule, boolean isVirtual) throws BusinessException {
        Date subscriptionDate = null;

        if (chargeInstance instanceof RecurringChargeInstance) {
            subscriptionDate = ((RecurringChargeInstance) chargeInstance).getServiceInstance().getSubscriptionDate();
        }

        UserAccount ua = chargeInstance.getUserAccount();
        BillingAccount billingAccount = ua.getBillingAccount();
        String languageCode = billingAccount.getTradingLanguage().getLanguage().getLanguageCode();

        Subscription subscription = chargeInstance.getSubscription();
        WalletOperation walletOperation = prerateChargeApplication(chargeInstance.getChargeTemplate(), subscriptionDate,
            subscription == null ? null : subscription.getOffer().getCode(), chargeInstance, applicationType, applicationDate,
            amountWithoutTax, amountWithTax, inputQuantity, quantity, tCurrency, countryId, languageCode, taxPercent, discountPercent, nextApplicationDate, invoiceSubCategory,
            criteria1, criteria2, criteria3, orderNumber, startdate, endDate, mode, chargeInstance.getUserAccount());

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
                        if (chargeInstance.getAuditable() == null) {
                            log.info("trigger EDR from code " + triggeredEDRTemplate.getCode());
                        } else {
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
                    if (actionStatus == null || ActionStatusEnum.SUCCESS != actionStatus.getStatus()) {
                        throw new BusinessException("Error charging Edr on remote instance Code " + actionStatus.getErrorCode() + ", info " + actionStatus.getMessage());
                    }
                }
            }
        }

        return walletOperation;
    }

    
    /**
     * used to rate or rerate a bareWalletOperation.
     * @param bareWalletOperation operation
     * @param unitPriceWithoutTax unit price without tax
     * @param unitPriceWithTax unit price with tax
     * @param countryId country id
     * @param tcurrency trading currency
     * @throws BusinessException business exception
     */
    public void rateBareWalletOperation(WalletOperation bareWalletOperation, BigDecimal unitPriceWithoutTax, BigDecimal unitPriceWithTax, Long countryId, TradingCurrency tcurrency)
            throws BusinessException {
        long startDate = System.currentTimeMillis();
        PricePlanMatrix ratePrice = null;

        if (unitPriceWithoutTax == null) {
            List<PricePlanMatrix> chargePricePlans = ratingCacheContainerProvider.getPricePlansByChargeCode(bareWalletOperation.getCode());
            if (chargePricePlans == null || chargePricePlans.isEmpty()) {
                throw new BusinessException("No price plan for charge code " + bareWalletOperation.getCode());
            }
            ratePrice = ratePrice(chargePricePlans, bareWalletOperation, countryId, tcurrency,
                bareWalletOperation.getSeller() != null ? bareWalletOperation.getSeller().getId() : null);
            if (ratePrice == null || ratePrice.getAmountWithoutTax() == null) {
                throw new BusinessException("Invalid price plan for charge code " + bareWalletOperation.getCode());
            }
            log.debug("found ratePrice:" + ratePrice.getId());
            unitPriceWithoutTax = ratePrice.getAmountWithoutTax();
            unitPriceWithTax = ratePrice.getAmountWithTax();
            WalletInstance wallet = bareWalletOperation.getWallet();
            UserAccount userAccount = wallet.getUserAccount();
            if (ratePrice.getAmountWithoutTaxEL() != null) {
                unitPriceWithoutTax = getExpressionValue(ratePrice.getAmountWithoutTaxEL(), ratePrice, bareWalletOperation, userAccount,
                    unitPriceWithoutTax);
                if(unitPriceWithoutTax == null) {
                    throw new BusinessException("Cant get price from EL:"+ratePrice.getAmountWithoutTaxEL());
                }
            }
            if (ratePrice.getAmountWithTaxEL() != null) {
                unitPriceWithTax = getExpressionValue(ratePrice.getAmountWithTaxEL(), ratePrice, bareWalletOperation, userAccount,
                    unitPriceWithoutTax);
                if(unitPriceWithTax == null) {
                    throw new BusinessException("Cant get price from EL:"+ratePrice.getAmountWithTaxEL());
                }
            }
        }

        log.debug("After unitPriceWithoutTax:" + (System.currentTimeMillis() - startDate));

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
                    unitPriceWithoutTax = unitPriceWithoutTax.divide(new BigDecimal(sharedQuantity), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    if (unitPriceWithTax != null) {
                        unitPriceWithTax = unitPriceWithTax.divide(new BigDecimal(sharedQuantity), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    }
                    log.info("charge is shared " + sharedQuantity + " times, so unit price is " + unitPriceWithoutTax);
                }
            }
        }

        log.debug("After getChargeInstance:" + (System.currentTimeMillis() - startDate));

        BigDecimal priceWithoutTax = bareWalletOperation.getQuantity().multiply(unitPriceWithoutTax);
        BigDecimal priceWithTax = null;
        BigDecimal unitPriceAmountTax = null;
        BigDecimal amountTax = BigDecimal.ZERO;

        if (bareWalletOperation.getTaxPercent() != null) {
            unitPriceAmountTax = unitPriceWithoutTax.multiply(bareWalletOperation.getTaxPercent().divide(HUNDRED));
            amountTax = priceWithoutTax.multiply(bareWalletOperation.getTaxPercent().divide(HUNDRED));
        }

        if (unitPriceWithTax == null || unitPriceWithTax.intValue() == 0) {
            if (unitPriceAmountTax != null) {
                unitPriceWithTax = unitPriceWithoutTax.add(unitPriceAmountTax);
                priceWithTax = priceWithoutTax.add(amountTax);
            }
        } else {
            unitPriceAmountTax = unitPriceWithTax.subtract(unitPriceWithoutTax);
            priceWithTax = bareWalletOperation.getQuantity().multiply(unitPriceWithTax);
            amountTax = priceWithTax.subtract(priceWithoutTax);
        }

        Integer rounding = appProvider.getRounding();
        if (rounding != null && rounding > 0) {
            priceWithoutTax = NumberUtils.round(priceWithoutTax, rounding);
            priceWithTax = NumberUtils.round(priceWithTax, rounding);
        }

        bareWalletOperation.setUnitAmountWithoutTax(unitPriceWithoutTax);
        bareWalletOperation.setUnitAmountWithTax(unitPriceWithTax);
        bareWalletOperation.setUnitAmountTax(unitPriceAmountTax);
        bareWalletOperation.setTaxPercent(bareWalletOperation.getTaxPercent());
        bareWalletOperation.setAmountWithoutTax(priceWithoutTax);
        bareWalletOperation.setAmountWithTax(priceWithTax);
        bareWalletOperation.setAmountTax(amountTax);

        if (ratePrice != null && ratePrice.getScriptInstance() != null) {
            log.debug("start to execute script instance for ratePrice {}", ratePrice);
            try {
                log.debug("execute priceplan script " + ratePrice.getScriptInstance().getCode());
                ScriptInterface script = scriptInstanceService.getCachedScriptInstance(ratePrice.getScriptInstance().getCode());
                HashMap<String, Object> context = new HashMap<String, Object>();
                context.put(Script.CONTEXT_ENTITY, bareWalletOperation);
                script.execute(context);
            } catch (Exception e) {
                log.error("Error when run script {}", ratePrice.getScriptInstance().getCode(), e);
                throw new BusinessException("failed when run script " + ratePrice.getScriptInstance().getCode() + ", info " + e.getMessage());
            }
        }

        log.debug("After bareWalletOperation:" + (System.currentTimeMillis() - startDate));
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
    private PricePlanMatrix ratePrice(List<PricePlanMatrix> listPricePlan, WalletOperation bareOperation,
            Long countryId, TradingCurrency tcurrency, Long sellerId)
            throws BusinessException {
        // FIXME: the price plan properties could be null !

        // log.info("ratePrice rate " + bareOperation);
        for (PricePlanMatrix pricePlan : listPricePlan) {
            Seller seller = pricePlan.getSeller();
            boolean sellerAreEqual = seller == null || seller.getId().equals(sellerId);
            if (!sellerAreEqual) {
                log.debug("The seller of the customer " + sellerId + " is not the same as pricePlan seller " + seller.getId());
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
                log.debug("The currency of the customer account " + (tcurrency != null ? tcurrency.getCurrencyCode() : "null") + " is not the same as pricePlan currency"
                        + tradingCurrency.getId());
                continue;
            }
            Date subscriptionDate = bareOperation.getSubscriptionDate();
            Date startSubscriptionDate = pricePlan.getStartSubscriptionDate();
            Date endSubscriptionDate = pricePlan.getEndSubscriptionDate();
            boolean subscriptionDateInPricePlanPeriod = subscriptionDate == null
                    || ((startSubscriptionDate == null || subscriptionDate.after(startSubscriptionDate)
                            || subscriptionDate.equals(startSubscriptionDate))
                            && (endSubscriptionDate == null || subscriptionDate.before(endSubscriptionDate)));
            if (!subscriptionDateInPricePlanPeriod) {
                log.debug("The subscription date " + subscriptionDate + "is not in the priceplan subscription range");
                continue;
            }

            int subscriptionAge = 0;
            Date operationDate = bareOperation.getOperationDate();
            if (subscriptionDate != null && operationDate != null) {
                // logger.info("subscriptionDate=" +
                // bareOperation.getSubscriptionDate() + "->" +
                // DateUtils.addDaysToDate(bareOperation.getSubscriptionDate(),
                // -1));
                subscriptionAge = DateUtils.monthsBetween(operationDate, DateUtils.addDaysToDate(subscriptionDate, -1));
            }
            // log.info("subscriptionAge=" + subscriptionAge);
            boolean subscriptionMinAgeOK = pricePlan.getMinSubscriptionAgeInMonth() == null || subscriptionAge >= pricePlan.getMinSubscriptionAgeInMonth();
            // log.info("subscriptionMinAgeOK(" +
            // pricePlan.getMinSubscriptionAgeInMonth() + ")=" +
            // subscriptionMinAgeOK);
            if (!subscriptionMinAgeOK) {
                log.debug("The subscription age={} is less than the priceplan subscription age min={}", subscriptionAge, pricePlan.getMinSubscriptionAgeInMonth());
                continue;
            }
            Long maxSubscriptionAgeInMonth = pricePlan.getMaxSubscriptionAgeInMonth();
            boolean subscriptionMaxAgeOK = maxSubscriptionAgeInMonth == null || maxSubscriptionAgeInMonth == 0
                    || subscriptionAge < maxSubscriptionAgeInMonth;
            log.debug("subscriptionMaxAgeOK(" + maxSubscriptionAgeInMonth + ")=" + subscriptionMaxAgeOK);
            if (!subscriptionMaxAgeOK) {
                log.debug("The subscription age " + subscriptionAge + " is greater than the priceplan subscription age max :" + maxSubscriptionAgeInMonth);
                continue;
            }

            Date startRatingDate = pricePlan.getStartRatingDate();
            Date endRatingDate = pricePlan.getEndRatingDate();
            boolean applicationDateInPricePlanPeriod = (startRatingDate == null || operationDate.after(startRatingDate)
                    || operationDate.equals(startRatingDate))
                    && (endRatingDate == null || operationDate.before(endRatingDate));
            log.debug("applicationDateInPricePlanPeriod(" + startRatingDate + " - " + endRatingDate + ")=" + applicationDateInPricePlanPeriod);
            if (!applicationDateInPricePlanPeriod) {
                log.debug("The application date " + operationDate + " is not in the priceplan application range");
                continue;
            }
            String criteria1Value = pricePlan.getCriteria1Value();
            boolean criteria1SameInPricePlan = criteria1Value == null || criteria1Value.equals(bareOperation.getParameter1());
            // log.info("criteria1SameInPricePlan(" +
            // pricePlan.getCriteria1Value() + ")=" + criteria1SameInPricePlan);
            if (!criteria1SameInPricePlan) {
                log.debug("The operation param1 " + bareOperation.getParameter1() + " is not compatible with price plan criteria 1: " + criteria1Value);
                continue;
            }
            String criteria2Value = pricePlan.getCriteria2Value();
            String parameter2 = bareOperation.getParameter2();
            boolean criteria2SameInPricePlan = criteria2Value == null || criteria2Value.equals(parameter2);
            // log.info("criteria2SameInPricePlan(" +
            // pricePlan.getCriteria2Value() + ")=" + criteria2SameInPricePlan);
            if (!criteria2SameInPricePlan) {
                log.debug("The operation param2 " + parameter2 + " is not compatible with price plan criteria 2: " + criteria2Value);
                continue;
            }
            String criteria3Value = pricePlan.getCriteria3Value();
            boolean criteria3SameInPricePlan = criteria3Value == null || criteria3Value.equals(bareOperation.getParameter3());
            // log.info("criteria3SameInPricePlan(" +
            // pricePlan.getCriteria3Value() + ")=" + criteria3SameInPricePlan);
            if (!criteria3SameInPricePlan) {
                log.debug("The operation param3 " + bareOperation.getParameter3() + " is not compatible with price plan criteria 3: " + criteria3Value);
                continue;
            }
            if (!StringUtils.isBlank(pricePlan.getCriteriaEL())) {
                UserAccount ua = bareOperation.getWallet().getUserAccount();
                if (!matchExpression(pricePlan.getCriteriaEL(), bareOperation, ua, pricePlan)) {
                    log.debug("The operation is not compatible with price plan criteria EL: " + pricePlan.getCriteriaEL());
                    continue;
                }
            }

            OfferTemplate offerTemplate = pricePlan.getOfferTemplate();
            String offerCode = bareOperation.getOfferCode();
            boolean offerCodeSameInPricePlan = offerTemplate == null || offerTemplate.getCode().equals(offerCode);
            if (!offerCodeSameInPricePlan) {
                log.debug("The operation offerCode " + offerCode + " is not compatible with price plan offerCode: "
                        + ((offerTemplate == null) ? "null" : offerTemplate.getCode()));
                continue;
            }
            log.debug("offerCodeSameInPricePlan");
            BigDecimal maxQuantity = pricePlan.getMaxQuantity();
            BigDecimal quantity = bareOperation.getQuantity();
            boolean quantityMaxOk = maxQuantity == null || maxQuantity.compareTo(quantity) > 0;
            if (!quantityMaxOk) {
                log.debug("the quantity " + quantity + " is strictly greater than " + maxQuantity);
                continue;
            } else {
                log.debug("quantityMaxOkInPricePlan");
            }
            BigDecimal minQuantity = pricePlan.getMinQuantity();
            boolean quantityMinOk = minQuantity == null || minQuantity.compareTo(quantity) <= 0;
            if (!quantityMinOk) {
                log.debug("the quantity " + quantity + " is less than " + minQuantity);
                continue;
            } else {
                log.debug("quantityMinOkInPricePlan");
            }

            Calendar validityCalendar = pricePlan.getValidityCalendar();
            boolean validityCalendarOK = validityCalendar == null || validityCalendar.previousCalendarDate(operationDate) != null;
            if (validityCalendarOK) {
                log.debug("validityCalendarOkInPricePlan calendar " + validityCalendar + " operation date " + operationDate);
                bareOperation.setPriceplan(pricePlan);
                return pricePlan;
            } else if (validityCalendar != null) {
                log.debug("the operation date " + operationDate + " does not match pricePlan validity calendar " + validityCalendar.getCode()
                        + "period range ");
            }

        }
        return null;
    }

    // rerate
    /**
     * @param operationToRerateId wallet operation to be rerated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     */
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
                    operation.setUnitAmountWithoutTax(priceplan.getAmountWithoutTax());
                    operation.setUnitAmountWithTax(priceplan.getAmountWithTax());
                    if (priceplan.getAmountWithoutTaxEL() != null) {
                	       BigDecimal priceFromEL = getExpressionValue(priceplan.getAmountWithoutTaxEL(), priceplan, operation,
                                       userAccount, unitAmountWithoutTax);
                	        if(priceFromEL == null) {
                	            throw new BusinessException("Cant get price from EL:"+priceplan.getAmountWithoutTaxEL());
                	        }
                        operation.setUnitAmountWithoutTax(priceFromEL);

                    }
                    if (priceplan.getAmountWithTaxEL() != null) {
                  	BigDecimal priceFromEL = getExpressionValue(priceplan.getAmountWithTaxEL(), priceplan, operation,
                                userAccount, unitAmountWithoutTax);
            	        if(priceFromEL == null) {
            	            throw new BusinessException("Cant get price from EL:"+priceplan.getAmountWithTaxEL());
            	        }
                        operation.setUnitAmountWithTax(priceFromEL);

                    }
                    if (operation.getUnitAmountTax() != null && unitAmountWithTax != null) {
                        operation.setUnitAmountTax(unitAmountWithTax.subtract(unitAmountWithoutTax));
                    }
                }
                BigDecimal quantity = operation.getQuantity();
                operation.setAmountWithoutTax(unitAmountWithoutTax.multiply(quantity));
                if (unitAmountWithTax != null) {
                    operation.setAmountWithTax(unitAmountWithTax.multiply(quantity));
                }
                Integer rounding = appProvider.getRounding();
                if (rounding != null && rounding > 0) {
                    operation.setAmountWithoutTax(NumberUtils.round(operation.getAmountWithoutTax(), rounding));
                    operation.setAmountWithTax(NumberUtils.round(operation.getAmountWithTax(), rounding));
                }
                operation.setAmountTax(operation.getAmountWithTax().subtract(operation.getAmountWithoutTax()));
            } else {
                operation.setUnitAmountWithoutTax(null);
                operation.setUnitAmountWithTax(null);
                operation.setUnitAmountTax(null);

                ChargeInstance chargeInstance = operationToRerate.getChargeInstance();
                TradingCountry tradingCountry = chargeInstance.getUserAccount().getBillingAccount().getTradingCountry();
                ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
                InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(
                    chargeTemplate.getInvoiceSubCategory().getId(), tradingCountry.getId(), operation.getOperationDate());
                if (invoiceSubcategoryCountry == null) {
                    throw new IncorrectChargeTemplateException("reRate: No invoiceSubcategoryCountry exists for invoiceSubCategory code="
                            + chargeTemplate.getInvoiceSubCategory().getCode() + " and trading country="
                            + tradingCountry.getCountryCode());
                }

                Tax tax = invoiceSubcategoryCountry.getTax();
                if (tax == null) {
                    tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(),
                        wallet == null ? null : userAccount, operation.getBillingAccount(), null);
                    if (tax == null) {
                        throw new IncorrectChargeTemplateException("reRate: no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
                    }
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
     * @param bareOperation operation
     * @param ua user account
     * @param amount amount used in EL
     * @return evaluated value from expression.
     */
    private BigDecimal getExpressionValue(String expression, PricePlanMatrix priceplan, WalletOperation bareOperation, UserAccount ua, BigDecimal amount) {
        BigDecimal result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        ChargeInstance chargeInstance = bareOperation.getChargeInstance();
		if((bareOperation.getChargeInstance() instanceof HibernateProxy)){
			chargeInstance = (ChargeInstance)((HibernateProxy) bareOperation.getChargeInstance()).getHibernateLazyInitializer().getImplementation();	
		}
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("op", bareOperation);
        userMap.put("pp", priceplan);
        if (amount != null) {
            userMap.put("amount", amount.doubleValue());
        }
        if (expression.indexOf("access") >= 0 && bareOperation.getEdr() != null && bareOperation.getEdr().getAccessCode() != null) {
            Access access = accessService.findByUserIdAndSubscription(bareOperation.getEdr().getAccessCode(), chargeInstance.getSubscription());
            userMap.put("access", access);
        }
        if (expression.indexOf("priceplan") >= 0) {
            userMap.put("priceplan", priceplan);
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
        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }
        Object res = null;
        try {
            res = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
        } catch (BusinessException e1) {
            log.error("Amount Expression {} error in price plan {}", expression, priceplan, e1);
        }
        try {
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
        } catch (Exception e) {
            log.error("Error Amount Expression " + expression, e);
        }
        return result;
    }

    /**
     * @param expression EL exception
     * @param bareOperation operation
     * @param ua user account
     * @param priceplan price plan
     * @return true/false true if expression is matched
     * @throws BusinessException business exception
     */
    private boolean matchExpression(String expression, WalletOperation bareOperation, UserAccount ua, PricePlanMatrix priceplan) throws BusinessException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        ChargeInstance chargeInstance = bareOperation.getChargeInstance();
		if((bareOperation.getChargeInstance() instanceof HibernateProxy)){
			chargeInstance = (ChargeInstance)((HibernateProxy) bareOperation.getChargeInstance()).getHibernateLazyInitializer().getImplementation();	
		}
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("op", bareOperation);
        if (expression.indexOf("access") >= 0 && bareOperation.getEdr() != null && bareOperation.getEdr().getAccessCode() != null) {
            Access access = accessService.findByUserIdAndSubscription(bareOperation.getEdr().getAccessCode(), chargeInstance.getSubscription());
            userMap.put("access", access);
        }
        if (expression.indexOf("priceplan") >= 0) {
            userMap.put("priceplan", priceplan);
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
                    ((OneShotChargeInstance) chargeInstance).getTerminationServiceInstance();
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
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", ua);
        }
        BillingAccount billingAccount = ua.getBillingAccount();
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", customerAccount);
        }
        if (expression.indexOf("c") >= 0) {
            userMap.put("c", customerAccount.getCustomer());
        }
        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }
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
        ChargeInstance chargeInstance = walletOperation.getChargeInstance();
		if((walletOperation.getChargeInstance() instanceof HibernateProxy)){
			chargeInstance = (ChargeInstance)((HibernateProxy) walletOperation.getChargeInstance()).getHibernateLazyInitializer().getImplementation();	
		}
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("op", walletOperation);
        if (expression.indexOf("access") >= 0 && walletOperation.getEdr() != null && walletOperation.getEdr().getAccessCode() != null) {
            Access access = accessService.findByUserIdAndSubscription(walletOperation.getEdr().getAccessCode(), chargeInstance.getSubscription());
            userMap.put("access", access);
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
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", ua);
        }
        BillingAccount billingAccount = ua.getBillingAccount();
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", customerAccount);
        }
        if (expression.indexOf("c") >= 0) {
            userMap.put("c", customerAccount.getCustomer());
        }
        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }

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
        ChargeInstance chargeInstance = walletOperation.getChargeInstance();
		if((walletOperation.getChargeInstance() instanceof HibernateProxy)){
			chargeInstance = (ChargeInstance)((HibernateProxy) walletOperation.getChargeInstance()).getHibernateLazyInitializer().getImplementation();	
		}
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("op", walletOperation);
        if (expression.indexOf("access") >= 0 && walletOperation.getEdr() != null && walletOperation.getEdr().getAccessCode() != null) {
            Access access = accessService.findByUserIdAndSubscription(walletOperation.getEdr().getAccessCode(), chargeInstance.getSubscription());
            userMap.put("access", access);
        }
        if (expression.indexOf("charge") >= 0) {
            ChargeTemplate charge = chargeInstance.getChargeTemplate();
            userMap.put("charge", charge);
        }
        if (expression.indexOf("offer") >= 0) {
            OfferTemplate offer = chargeInstance.getSubscription().getOffer();
            userMap.put("offer", offer);
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
                    ((OneShotChargeInstance) chargeInstance).getTerminationServiceInstance();
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
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", ua);
        }
        BillingAccount billingAccount = ua.getBillingAccount();
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", customerAccount);
        }
        if (expression.indexOf("c") >= 0) {
            userMap.put("c", customerAccount.getCustomer());
        }
        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Double.class);
        try {
            result = (Double) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to double but " + res);
        }
        return result;
    }

}