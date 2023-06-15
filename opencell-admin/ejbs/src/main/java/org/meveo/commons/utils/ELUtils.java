package org.meveo.commons.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixForRating;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.model.rating.EDR;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.util.ApplicationProvider;

public class ELUtils implements Serializable {

    private static final long serialVersionUID = -168471925044668712L;

    @Inject
    private AccessService accessService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    /**
     * Evaluate EL expression with BigDecimal as result
     *
     * @param expression EL exception to evaluate
     * @param walletOperation Wallet operation
     * @param ua User account
     * @param priceplan Price plan
     * @param amount Amount used in EL
     * @return Evaluated value from expression.
     * @throws InvalidELException Failed to evaluate EL expression
     */
    public BigDecimal evaluateAmountExpression(String expression, WalletOperation walletOperation, UserAccount ua, PricePlanMatrix priceplan, BigDecimal amount) throws InvalidELException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, null, walletOperation, ua, amount, walletOperation.getEdr());

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);

    }

    /**
     * Evaluate EL expression with boolean as result
     *
     * @param expression EL exception to evaluate
     * @param walletOperation Wallet operation
     * @param ua User account
     * @param priceplan Price plan
     * @param priceplanForRating A simplified Price plan entity as DTO
     * @param edr EDR
     * @return true/false True if expression is matched
     * @throws InvalidELException Failed to evaluate EL expression
     */
    public boolean evaluateBooleanExpression(String expression, WalletOperation walletOperation, UserAccount ua, PricePlanMatrix priceplan, PricePlanMatrixForRating priceplanForRating, EDR edr)
            throws InvalidELException {

        if (StringUtils.isBlank(expression)) {
            return true;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, priceplanForRating, walletOperation, ua, null, edr);

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
     * @throws InvalidELException Failed to evaluate EL expression
     */
    public String evaluateStringExpression(String expression, WalletOperation walletOperation, UserAccount ua, PricePlanMatrix priceplan, EDR edr) throws InvalidELException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, null, walletOperation, ua, null, edr);

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
     * @throws InvalidELException Failed to evaluate EL expression
     */
    public Double evaluateDoubleExpression(String expression, WalletOperation walletOperation, UserAccount ua, PricePlanMatrix priceplan, EDR edr) throws InvalidELException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, priceplan, null, walletOperation, ua, null, edr);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, Double.class);
    }

    /**
     * Construct variable context for EL expression evaluation
     *
     * @param expression EL expression
     * @param priceplan Price plan. Optional. If not provided, will be resolved from Wallet operation if referenced in EL expression
     * @param pricePlanForRating A simplified Price plan entity as DTO
     * @param walletOperation Wallet operation. Mandatory
     * @param ua User account. Optional. If not provided, will be resolved from Wallet operation if referenced in EL expression
     * @param amount Amount
     * @param edr EDR. Optional. If not provided, will be resolved from Wallet operation if referenced in EL expression
     * @return A map of variables
     */
    private Map<Object, Object> constructElContext(String expression, PricePlanMatrix priceplan, PricePlanMatrixForRating pricePlanForRating, WalletOperation walletOperation, UserAccount ua, BigDecimal amount, EDR edr) {

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

            } else if (pricePlanForRating != null) {
                userMap.put(ValueExpressionWrapper.VAR_PRICE_PLAN, pricePlanForRating);
                userMap.put(ValueExpressionWrapper.VAR_PRICE_PLAN_SHORT, pricePlanForRating);
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
                CpqQuote quote = service.getQuoteProduct() != null ? service.getQuoteProduct().getQuote() : null;
                if (quote != null) {
                    userMap.put(ValueExpressionWrapper.VAR_CPQ_QUOTE, quote);
                }

            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_QUOTE_VERSION) >= 0) {
            ServiceInstance service = chargeInstance.getServiceInstance();
            if (service != null) {
                QuoteVersion quoteVersion = service.getQuoteProduct() != null ? service.getQuoteProduct().getQuoteVersion() : null;
                if (quoteVersion != null) {
                    userMap.put(ValueExpressionWrapper.VAR_QUOTE_VERSION, quoteVersion);
                }

            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_PRODUCT_INSTANCE) >= 0) {
            ProductInstance productInstance = null;
            if (chargeInstance != null && chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.PRODUCT) {
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

}
