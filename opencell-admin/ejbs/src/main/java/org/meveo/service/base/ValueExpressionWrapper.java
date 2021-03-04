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

package org.meveo.service.base;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.tax.TaxCategory;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.crm.impl.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EL expression resolver
 * 
 * @author Andrius Karpavicius
 *
 */
@SuppressWarnings("rawtypes")
public class ValueExpressionWrapper {

    static ExpressionFactory expressionFactory = ExpressionFactory.newInstance();

    private SimpleELResolver simpleELResolver;

    private ELContext context;

    private ValueExpression ve;

    static protected Logger log = LoggerFactory.getLogger(ValueExpressionWrapper.class);

    static HashMap<String, ValueExpressionWrapper> valueExpressionWrapperMap = new HashMap<String, ValueExpressionWrapper>();

    /**
     * EL variable class (class name) to parameter name mapping.
     */
    private static final Map<String, String[]> elVariablesByClass = new HashMap<>();

    /**
     * EL expression variable - provider - 'prov'
     */
    public static final String VAR_PROVIDER = "prov";

    /**
     * EL expression variable - seller - 'seller'
     */
    public static final String VAR_SELLER = "seller";

    /**
     * EL expression variable - customer - 'c'
     */
    public static final String VAR_CUSTOMER_SHORT = "c";

    /**
     * EL expression variable - customer - 'cust'
     */
    public static final String VAR_CUSTOMER = "cust";

    /**
     * EL expression variable - customer account - 'ca'
     */
    public static final String VAR_CUSTOMER_ACCOUNT = "ca";

    /**
     * EL expression variable - billing account - 'ba'
     */
    public static final String VAR_BILLING_ACCOUNT = "ba";

    /**
     * EL expression variable - user account - 'ua'
     */
    public static final String VAR_USER_ACCOUNT = "ua";

    /**
     * EL expression variable - access - 'access'
     */
    public static final String VAR_ACCESS = "access";

    /**
     * EL expression variable - subscription - 'sub'
     */
    public static final String VAR_SUBSCRIPTION = "sub";

    /**
     * EL expression variable - tax category - 'taxCategory'
     */
    public static final String VAR_TAX_CATEGORY = "taxCategory";

    /**
     * EL expression variable - tax class - 'taxClass'
     */
    public static final String VAR_TAX_CLASS = "taxClass";

    /**
     * EL expression variable - invoice - 'iv'
     */
    public static final String VAR_INVOICE_SHORT = "iv";

    /**
     * EL expression variable - invoice - 'invoice'
     */
    public static final String VAR_INVOICE = "invoice";

    /**
     * EL expression variable - billing run - 'br'
     */
    public static final String VAR_BILLING_RUN = "br";

    /**
     * EL expression variable - service template - 'serviceTemplate'
     */
    public static final String VAR_SERVICE_TEMPLATE = "serviceTemplate";

    /**
     * EL expression variable - service instance - 'serviceInstance'
     */
    public static final String VAR_SERVICE_INSTANCE = "serviceInstance";

    /**
     * EL expression variable - product instance - 'productInstance'
     */
    public static final String VAR_PRODUCT_INSTANCE = "productInstance";

    /**
     * EL expression variable - offer - 'offer'
     */
    public static final String VAR_OFFER = "offer";

    /**
     * EL expression variable - price plan - 'pp'
     */
    public static final String VAR_PRICE_PLAN_SHORT = "pp";

    /**
     * EL expression variable - price plan - 'priceplan'
     */
    public static final String VAR_PRICE_PLAN = "priceplan";

    /**
     * EL expression variable - dpi - 'dpi'
     */
    public static final String VAR_DISCOUNT_PLAN_INSTANCE = "dpi";

    /**
     * EL expression variable - EDR - 'edr'
     */
    public static final String VAR_EDR = "edr";

    /**
     * EL expression variable - charge template - 'charge'
     */
    public static final String VAR_CHARGE_TEMPLATE_SHORT = "charge";

    /**
     * EL expression variable - charge template - 'chargeTemplate'
     */
    public static final String VAR_CHARGE_TEMPLATE = "chargeTemplate";

    /**
     * EL expression variable - charge instance - 'ci'
     */
    public static final String VAR_CHARGE_INSTANCE = "ci";

    /**
     * EL expression variable - wallet operation - 'op'
     */
    public static final String VAR_WALLET_OPERATION = "op";

    /**
     * EL expression variable - date - 'date'
     */
    public static final String VAR_DATE = "date";

    /**
     * EL expression variable - amount - 'amount'
     */
    public static final String VAR_AMOUNT = "amount";

    /**
     * EL expression variable - wallet instance - 'wallet'
     */
    public static final String VAR_WALLET_INSTANCE = "wallet";

    /**
     * Variables in EL expression
     * 
     * @author Andrius Karpavicius
     *
     */

    /**
     * Construct a EL variable class (class name) to parameter name mapping
     */
    static {

        elVariablesByClass.put(Provider.class.getName(), new String[] { VAR_PROVIDER });
        elVariablesByClass.put(Seller.class.getName(), new String[] { VAR_SELLER });
        elVariablesByClass.put(Customer.class.getName(), new String[] { VAR_CUSTOMER_SHORT, VAR_CUSTOMER });
        elVariablesByClass.put(CustomerAccount.class.getName(), new String[] { VAR_CUSTOMER_ACCOUNT });
        elVariablesByClass.put(BillingAccount.class.getName(), new String[] { VAR_BILLING_ACCOUNT });
        elVariablesByClass.put(UserAccount.class.getName(), new String[] { VAR_USER_ACCOUNT });
        elVariablesByClass.put(Access.class.getName(), new String[] { VAR_ACCESS });
        elVariablesByClass.put(Subscription.class.getName(), new String[] { VAR_SUBSCRIPTION });
        elVariablesByClass.put(TaxCategory.class.getName(), new String[] { VAR_TAX_CATEGORY });
        elVariablesByClass.put(TaxClass.class.getName(), new String[] { VAR_TAX_CLASS });
        elVariablesByClass.put(Invoice.class.getName(), new String[] { VAR_INVOICE_SHORT, VAR_INVOICE });
        elVariablesByClass.put(BillingRun.class.getName(), new String[] { VAR_BILLING_RUN });
        elVariablesByClass.put(ServiceTemplate.class.getName(), new String[] { VAR_SERVICE_TEMPLATE });
        elVariablesByClass.put(ServiceInstance.class.getName(), new String[] { VAR_SERVICE_INSTANCE });
        elVariablesByClass.put(ProductInstance.class.getName(), new String[] { VAR_PRODUCT_INSTANCE });
        elVariablesByClass.put(OfferTemplate.class.getName(), new String[] { VAR_OFFER });
        elVariablesByClass.put(PricePlanMatrix.class.getName(), new String[] { VAR_PRICE_PLAN_SHORT, VAR_PRICE_PLAN });
        elVariablesByClass.put(DiscountPlanInstance.class.getName(), new String[] { VAR_DISCOUNT_PLAN_INSTANCE });
        elVariablesByClass.put(EDR.class.getName(), new String[] { VAR_EDR });
        elVariablesByClass.put(RecurringChargeTemplate.class.getName(), new String[] { VAR_CHARGE_TEMPLATE_SHORT, VAR_CHARGE_TEMPLATE });
        elVariablesByClass.put(OneShotChargeTemplate.class.getName(), new String[] { VAR_CHARGE_TEMPLATE_SHORT, VAR_CHARGE_TEMPLATE });
        elVariablesByClass.put(UsageChargeTemplate.class.getName(), new String[] { VAR_CHARGE_TEMPLATE_SHORT, VAR_CHARGE_TEMPLATE });
        elVariablesByClass.put(ChargeTemplate.class.getName(), new String[] { VAR_CHARGE_TEMPLATE_SHORT, VAR_CHARGE_TEMPLATE });
        elVariablesByClass.put(ChargeInstance.class.getName(), new String[] { VAR_CHARGE_INSTANCE });
        elVariablesByClass.put(RecurringChargeInstance.class.getName(), new String[] { VAR_CHARGE_INSTANCE });
        elVariablesByClass.put(OneShotChargeInstance.class.getName(), new String[] { VAR_CHARGE_INSTANCE });
        elVariablesByClass.put(UsageChargeInstance.class.getName(), new String[] { VAR_CHARGE_INSTANCE });
        elVariablesByClass.put(WalletOperation.class.getName(), new String[] { VAR_WALLET_OPERATION });
        elVariablesByClass.put(Date.class.getName(), new String[] { VAR_DATE });
        elVariablesByClass.put(BigDecimal.class.getName(), new String[] { VAR_AMOUNT });
    }

    /**
     * Evaluate expression.
     * 
     * @param expression Expression to evaluate
     * @param contextMap Context of values (optional)
     * @return A value that expression evaluated to
     * @throws BusinessException business exception.
     */
    public static boolean evaluateToBoolean(String expression, Map<Object, Object> contextMap) throws BusinessException {

        Object value = evaluateExpression(expression, contextMap, Boolean.class);
        if (value instanceof Boolean) {
            return (boolean) value;
        } else {
            return false;
        }
    }

    /**
     * Evaluate expression to a boolean value ignoring exceptions
     * 
     * @param expression Expression to evaluate
     * @param contextMap Context of values (optional)
     * @return A boolean value expression evaluates to. An empty expression evaluates to true. Failure to evaluate, return false;
     */
    public static boolean evaluateToBooleanIgnoreErrors(String expression, Map<Object, Object> contextMap) {
        try {
            return evaluateToBoolean(expression, contextMap);
        } catch (BusinessException e) {
            log.error("Failed to evaluate expression {} on variable {}", expression, contextMap, e);
            return false;
        }
    }

    /**
     * Evaluate expression to a boolean value. Note: method needs to have a unique name as is evaluated from JSF pages.
     * 
     * @param expression Expression to evaluate
     * @param variableName Variable name to give to a variable in context
     * @param variable Variable to make available in context
     * @return A boolean value expression evaluates to. An empty expression evaluates to true;
     * @throws BusinessException business exception.
     */
    public static boolean evaluateToBooleanOneVariable(String expression, String variableName, Object variable) throws BusinessException {

        boolean result = evaluateToBooleanMultiVariable(expression, variableName, variable);
        return result;
    }

    /**
     * Evaluate expression to a boolean value ignoring exceptions
     * 
     * @param expression Expression to evaluate
     * @param variableName Variable name to give to a variable in context
     * @param variable Variable to make available in context
     * @return A boolean value expression evaluates to. An empty expression evaluates to true. Failure to evaluate, return false;
     */
    public static boolean evaluateToBooleanIgnoreErrors(String expression, String variableName, Object variable) {
        try {
            return evaluateToBooleanMultiVariable(expression, variableName, variable);
        } catch (BusinessException e) {
            log.error("Failed to evaluate expression {} on variable {}/{}", expression, variableName, variable, e);
            return false;
        }
    }

    /**
     * Evaluate expression to a boolean value.
     * 
     * @param expression Expression to evaluate
     * @param contextVarNameAndValue An array of context variables and their names in the following order: variable 1 name, variable 1, variable 2 name, variable2, etc..
     * @return A boolean value expression evaluates to. An empty expression evaluates to true;
     * @throws BusinessException business exception.
     */
    public static boolean evaluateToBooleanMultiVariable(String expression, Object... contextVarNameAndValue) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return true;
        }

        Map<Object, Object> contextMap = new HashMap<Object, Object>();
        if (contextVarNameAndValue != null) {
            for (int i = 0; i < contextVarNameAndValue.length; i = i + 2) {
                contextMap.put(contextVarNameAndValue[i], contextVarNameAndValue[i + 1]);
            }
        }
        Object value = evaluateExpression(expression, contextMap, Boolean.class);
        if (value instanceof Boolean) {
            return (boolean) value;
        } else {
            return false;
        }
    }

    /**
     * Evaluate expression to a String value ignoring exceptions. Converting to string if necessary.
     * 
     * @param expression Expression to evaluate
     * @param variableName Variable name to give to a variable in context
     * @param variable Variable to make available in context
     * @return A boolean value expression evaluates to. An empty expression evaluates to true. Failure to evaluate, return false;
     */
    public static String evaluateToStringIgnoreErrors(String expression, String variableName, Object variable) {
        try {
            return evaluateToStringMultiVariable(expression, variableName, variable);
        } catch (BusinessException e) {
            log.error("Failed to evaluate expression {} on variable {}/{}", expression, variableName, variable, e);
            return null;
        }
    }

    /**
     * Evaluate expression to a string value, converting to string if necessary.
     * 
     * @param expression Expression to evaluate
     * @param contextVarNameAndValue An array of context variables and their names in the following order: variable 1 name, variable 1, variable 2 name, variable2, etc..
     * @return A boolean value expression evaluates to. An empty expression evaluates to true;
     * @throws BusinessException business exception
     */
    public static String evaluateToStringMultiVariable(String expression, Object... contextVarNameAndValue) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> contextMap = new HashMap<Object, Object>();
        if (contextVarNameAndValue != null) {
            for (int i = 0; i < contextVarNameAndValue.length; i = i + 2) {
                contextMap.put(contextVarNameAndValue[i], contextVarNameAndValue[i + 1]);
            }
        }
        Object value = evaluateExpression(expression, contextMap, String.class);
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    /**
     * Evaluate expression.
     * 
     * @param <T> Expected result value class
     * 
     * @param expression Expression to evaluate
     * @param resultClass An expected result class
     * @param parameters Parameters to expose for EL expression evaluation
     * @return A value that expression evaluated to
     * @throws BusinessException business exception.
     */
    public static <T> T evaluateExpression(String expression, Class<T> resultClass, Object... parameters) throws BusinessException {
        Map<Object, Object> contextMap = populateContext(expression, parameters);

        return evaluateExpression(expression, contextMap, resultClass);
    }

    /**
     * Evaluate expression.
     * 
     * @param <T>
     * 
     * @param expression Expression to evaluate
     * @param contextMap Context of values
     * @param resultClass An expected result class
     * @return A value that expression evaluated to
     * @throws BusinessException business exception.
     */
    @SuppressWarnings("unchecked")
    public static <T> T evaluateExpression(String expression, Map<Object, Object> contextMap, Class<T> resultClass) throws BusinessException {

        Object result = null;
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        expression = StringUtils.trim(expression);

        if (expression.indexOf("#{") < 0) {
            // log.debug("the expression '{}' doesn't contain any EL", expression);
            if (resultClass.equals(String.class)) {
                return (T) expression;
            } else if (resultClass.equals(Double.class)) {
                return (T) Double.valueOf(expression);
            } else if (resultClass.equals(BigDecimal.class)) {
                return (T) new BigDecimal(expression);
            } else if (resultClass.equals(Boolean.class)) {
                if ("true".equalsIgnoreCase(expression)) {
                    return (T) Boolean.TRUE;
                } else {
                    return (T) Boolean.FALSE;
                }
            } else if (resultClass.equals(Integer.class)) {
                return (T) Integer.valueOf(expression);
            } else if (resultClass.equals(Date.class)) {
                return (T) DateUtils.parseDate(expression);
            }
        }

        try {
            result = ValueExpressionWrapper.getValue(expression, contextMap, resultClass);
            log.trace("EL {} => {}", expression, result);

            return (T) result;

        } catch (Exception e) {
            log.warn("EL {} throw error with variables {}", expression, contextMap, e);
            throw new BusinessException("Error while evaluating expression " + expression + " : " + e.getMessage());
        }
    }

    private static Object getValue(String expression, Map<Object, Object> userMap, Class resultClass) {
        ValueExpressionWrapper result = null;
        if (valueExpressionWrapperMap.containsKey(expression)) {
            result = valueExpressionWrapperMap.get(expression);
        }
        if (result == null) {
            result = new ValueExpressionWrapper(expression, userMap, resultClass);
        }
        return result.getValue(userMap);
    }

    private ValueExpressionWrapper(String expression, Map<Object, Object> userMap, Class resultClass) {
        if (userMap != null && expression.contains("appProvider")) {
            Provider appProvider = ((ProviderService) EjbUtils.getServiceInterface("ProviderService")).getProvider();
            userMap.put("appProvider", appProvider);
        }
        simpleELResolver = new SimpleELResolver(userMap);
        final VariableMapper variableMapper = new SimpleVariableMapper();
        final MeveoFunctionMapper functionMapper = new MeveoFunctionMapper();
        final CompositeELResolver compositeELResolver = new CompositeELResolver();
        compositeELResolver.add(simpleELResolver);
        compositeELResolver.add(new ArrayELResolver());
        compositeELResolver.add(new ListELResolver());
        compositeELResolver.add(new BeanELResolver());
        compositeELResolver.add(new MapELResolver());
        context = new ELContext() {
            @Override
            public ELResolver getELResolver() {
                return compositeELResolver;
            }

            @Override
            public FunctionMapper getFunctionMapper() {
                return functionMapper;
            }

            @Override
            public VariableMapper getVariableMapper() {
                return variableMapper;
            }
        };
        ve = expressionFactory.createValueExpression(context, expression, resultClass);
    }

    private Object getValue(Map<Object, Object> userMap) {
        simpleELResolver.setUserMap(userMap);
        return ve.getValue(context);
    }

    public static boolean collectionContains(String[] collection, String key) {
        return Arrays.asList(collection).contains(key);
    }

    /**
     * Populate EL context with variables by examining parameter classes
     * 
     * @param el EL to construct the context for
     * @param parameters Available parameters
     * @return A variable map for use as EL context
     */
    public static Map<Object, Object> populateContext(String el, Object... parameters) {
        return completeContext(el, new HashMap<>(), parameters);
    }

    /**
     * complete context with variables by examining parameter classes, with an initial contextMap
     * 
     * @param el EL to construct the context for
     * @param contextMap initial context map that will be completed by needed variables
     * @param parameters Available parameters
     * @return A variable map for use as EL context
     */
    public static Map<Object, Object> completeContext(String el, Map<Object, Object> contextMap, Object... parameters) {

        ChargeInstance chargeInstance = null;
        ServiceInstance serviceInstance = null;
        Subscription subscription = null;
        UserAccount userAccount = null;
        BillingAccount billingAccount = null;
        CustomerAccount customerAccount = null;
        Customer customer = null;
        Invoice invoice = null;
        List<Access> accessPoints = null;

        // Recognize passed parameters
        for (Object parameter : parameters) {
            if (parameter == null) {
                continue;
            }

            String className = ReflectionUtils.getCleanClassName(parameter.getClass().getName());
            String[] elVariableNames = elVariablesByClass.get(className);
            if (elVariableNames == null) {
                continue;
            }

            for (String elVariableName : elVariableNames) {
                contextMap.put(elVariableName, parameter);
            }
            if (parameter instanceof ChargeInstance) {
                chargeInstance = (ChargeInstance) parameter;
            }
            if (parameter instanceof ServiceInstance) {
                serviceInstance = (ServiceInstance) parameter;
            }
            if (parameter instanceof Subscription) {
                subscription = (Subscription) parameter;
            }
            if (parameter instanceof UserAccount) {
                userAccount = (UserAccount) parameter;
            }
            if (parameter instanceof BillingAccount) {
                billingAccount = (BillingAccount) parameter;
            }
            if (parameter instanceof CustomerAccount) {
                customerAccount = (CustomerAccount) parameter;
            }
            if (parameter instanceof Customer) {
                customer = (Customer) parameter;
            }
            if (parameter instanceof Invoice) {
                invoice = (Invoice) parameter;
            }
            if (parameter instanceof List) {
                List list = (List) parameter;
                if (list != null && !list.isEmpty()) {
                    if (list.get(0) instanceof Access) {
                        accessPoints = list;
                    }
                }
            }
        }

        // Append any derived parameters
        if (el.contains(VAR_SERVICE_INSTANCE) && !contextMap.containsKey(VAR_SERVICE_INSTANCE) && chargeInstance != null) {
            serviceInstance = chargeInstance.getServiceInstance();
            contextMap.put(VAR_SERVICE_INSTANCE, serviceInstance);
        }
        if (el.contains(VAR_SERVICE_TEMPLATE) && !contextMap.containsKey(VAR_SERVICE_TEMPLATE)) {
            if (serviceInstance != null) {
                contextMap.put(VAR_SERVICE_TEMPLATE, serviceInstance.getServiceTemplate());
            } else if (chargeInstance != null && chargeInstance.getServiceInstance() != null) {
                contextMap.put(VAR_SERVICE_TEMPLATE, chargeInstance.getServiceInstance().getServiceTemplate());
            }
        }
        if (el.contains(VAR_SUBSCRIPTION) && !contextMap.containsKey(VAR_SUBSCRIPTION)) {
            if (serviceInstance != null) {
                subscription = serviceInstance.getSubscription();
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                subscription = chargeInstance.getSubscription();
            } else if (chargeInstance != null && chargeInstance.getServiceInstance() != null) {
                subscription = chargeInstance.getServiceInstance().getSubscription();
            } else if (invoice != null && invoice.getSubscription() != null) {
                subscription = invoice.getSubscription();
            }
            contextMap.put(VAR_SUBSCRIPTION, subscription);
        }

        if (el.contains(VAR_ACCESS) && !contextMap.containsKey(VAR_ACCESS)) {
            if (subscription != null) {
                accessPoints = subscription.getAccessPoints();
            } else if (serviceInstance != null && serviceInstance.getSubscription() != null && serviceInstance.getSubscription().getAccessPoints() != null) {
                accessPoints = serviceInstance.getSubscription().getAccessPoints();
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null && serviceInstance.getSubscription().getAccessPoints() != null) {
                accessPoints = chargeInstance.getSubscription().getAccessPoints();
            } else if (chargeInstance != null && chargeInstance.getServiceInstance() != null && chargeInstance.getServiceInstance().getSubscription().getAccessPoints() != null) {
                accessPoints = chargeInstance.getServiceInstance().getSubscription().getAccessPoints();
            } else if (invoice != null && invoice.getSubscription() != null) {
                accessPoints = invoice.getSubscription().getAccessPoints();
            }
            contextMap.put(VAR_ACCESS, accessPoints);
        }
        if (el.contains(VAR_USER_ACCOUNT) && !contextMap.containsKey(VAR_USER_ACCOUNT)) {
            if (subscription != null) {
                userAccount = subscription.getUserAccount();
            } else if (serviceInstance != null) {
                userAccount = serviceInstance.getSubscription().getUserAccount();
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                userAccount = chargeInstance.getSubscription().getUserAccount();
            } else if (chargeInstance != null && chargeInstance.getServiceInstance() != null) {
                userAccount = chargeInstance.getServiceInstance().getSubscription().getUserAccount();
            }
            contextMap.put(VAR_USER_ACCOUNT, userAccount);
        }

        if (el.contains(VAR_BILLING_ACCOUNT) && !contextMap.containsKey(VAR_BILLING_ACCOUNT)) {
            if (userAccount != null) {
                billingAccount = userAccount.getBillingAccount();
            } else if (subscription != null) {
                billingAccount = subscription.getUserAccount().getBillingAccount();
            } else if (serviceInstance != null) {
                billingAccount = serviceInstance.getSubscription().getUserAccount().getBillingAccount();
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                billingAccount = chargeInstance.getSubscription().getUserAccount().getBillingAccount();
            } else if (chargeInstance != null && chargeInstance.getServiceInstance() != null) {
                billingAccount = chargeInstance.getServiceInstance().getSubscription().getUserAccount().getBillingAccount();
            } else if (invoice != null) {
                billingAccount = invoice.getBillingAccount();
            }
            contextMap.put(VAR_BILLING_ACCOUNT, billingAccount);
        }

        if (el.contains(VAR_CUSTOMER_ACCOUNT) && !contextMap.containsKey(VAR_CUSTOMER_ACCOUNT)) {
            if (billingAccount != null) {
                customerAccount = billingAccount.getCustomerAccount();
            } else if (userAccount != null) {
                customerAccount = userAccount.getBillingAccount().getCustomerAccount();
            } else if (subscription != null) {
                customerAccount = subscription.getUserAccount().getBillingAccount().getCustomerAccount();
            } else if (serviceInstance != null) {
                customerAccount = serviceInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount();
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                customerAccount = chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount();
            } else if (chargeInstance != null && chargeInstance.getServiceInstance() != null) {
                customerAccount = chargeInstance.getServiceInstance().getSubscription().getUserAccount().getBillingAccount().getCustomerAccount();
            } else if (invoice != null) {
                customerAccount = invoice.getBillingAccount().getCustomerAccount();
            }
            contextMap.put(VAR_CUSTOMER_ACCOUNT, customerAccount);
        }

        if (el.contains(VAR_CUSTOMER) && !contextMap.containsKey(VAR_CUSTOMER)) {
            if (customerAccount != null) {
                customer = customerAccount.getCustomer();
            } else if (billingAccount != null) {
                customer = billingAccount.getCustomerAccount().getCustomer();
            } else if (userAccount != null) {
                customer = userAccount.getBillingAccount().getCustomerAccount().getCustomer();
            } else if (subscription != null) {
                customer = subscription.getUserAccount().getBillingAccount().getCustomerAccount().getCustomer();
            } else if (serviceInstance != null) {
                customer = serviceInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getCustomer();
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                customer = chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getCustomer();
            } else if (chargeInstance != null && chargeInstance.getServiceInstance() != null) {
                customer = chargeInstance.getServiceInstance().getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getCustomer();
            } else if (invoice != null) {
                customer = invoice.getBillingAccount().getCustomerAccount().getCustomer();
            }
            contextMap.put(VAR_CUSTOMER, customer);
        }

        if (el.contains(VAR_SELLER) && !contextMap.containsKey(VAR_SELLER)) {
            if (subscription != null) {
                contextMap.put(VAR_SELLER, subscription.getSeller());
            } else if (serviceInstance != null) {
                contextMap.put(VAR_SELLER, serviceInstance.getSubscription().getSeller());
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                contextMap.put(VAR_SELLER, chargeInstance.getSubscription().getSeller());
            } else if (chargeInstance != null && chargeInstance.getServiceInstance() != null) {
                contextMap.put(VAR_SELLER, chargeInstance.getServiceInstance().getSubscription().getSeller());
            } else if (customer != null) {
                contextMap.put(VAR_SELLER, customer.getSeller());
            } else if (customerAccount != null) {
                contextMap.put(VAR_SELLER, customerAccount.getCustomer().getSeller());
            } else if (billingAccount != null) {
                contextMap.put(VAR_SELLER, billingAccount.getCustomerAccount().getCustomer().getSeller());
            } else if (userAccount != null) {
                contextMap.put(VAR_SELLER, userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller());
            }
        }

        if (el.contains(VAR_OFFER) && !contextMap.containsKey(VAR_OFFER)) {
            if (subscription != null) {
                contextMap.put(VAR_OFFER, subscription.getOffer());
            } else if (serviceInstance != null) {
                contextMap.put(VAR_OFFER, serviceInstance.getSubscription().getOffer());
            } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                contextMap.put(VAR_OFFER, chargeInstance.getSubscription().getOffer());
            } else if (chargeInstance != null && chargeInstance.getServiceInstance() != null) {
                contextMap.put(VAR_OFFER, chargeInstance.getServiceInstance().getSubscription().getOffer());
            }
        }

        if (el.contains(VAR_BILLING_RUN) && !contextMap.containsKey(VAR_BILLING_RUN)) {
            if (invoice != null && invoice.getBillingRun() != null) {
                contextMap.put(VAR_BILLING_RUN, invoice.getBillingRun());
            }
        }

        return contextMap;
    }
}