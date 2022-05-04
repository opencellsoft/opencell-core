package org.meveo.service.billing.impl;

import static java.util.Collections.emptyList;
import static org.meveo.commons.utils.StringUtils.isBlank;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.math.NumberUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ExtraMinAmount;
import org.meveo.model.billing.MinAmountData;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.IInvoicingMinimumApplicable;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;

@Stateless
public class MinAmountService extends PersistenceService<BusinessEntity> {


    /**
     * Gets the invoiceable amount for each level account.
     *
     * @param billableEntity The billable entity
     * @param billingAccount The billing account
     * @param lastTransactionDate last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @param extraMinAmounts The extra minimum amounts generated in children levels
     * @param accountClass The account level's class
     * @param invoicingProcessType invoicing process invoiceLine or ratedTransaction
     * @return return invoiceable amount grouped by entity
     */
    public Map<Long, MinAmountData> getInvoiceableAmountDataPerAccount(IBillableEntity billableEntity, BillingAccount billingAccount,
                                                                       Date lastTransactionDate, Date invoiceUpToDate, List<ExtraMinAmount> extraMinAmounts,
                                                                       Class accountClass, String invoicingProcessType) {
        Map<Long, MinAmountData> accountToMinAmount = new HashMap<>();
        List<Object[]> amountsList = computeInvoiceableAmountForAccount(billableEntity, new Date(0), lastTransactionDate, invoiceUpToDate, accountClass, invoicingProcessType);
        for (Object[] amounts : amountsList) {
            BigDecimal amountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal amountWithTax = (BigDecimal) amounts[1];
            IInvoicingMinimumApplicable entity = (IInvoicingMinimumApplicable) getEntityManager().find(accountClass, amounts[2]);
            Seller seller = getSeller(billingAccount, entity);
            MinAmountData minAmountDataInfo = accountToMinAmount.get(entity.getId());
            if (minAmountDataInfo == null) {
                String minAmountEL = getMinimumAmountElInfo(entity, "getMinimumAmountEl");
                String minAmountLabelEL = getMinimumAmountElInfo(entity, "getMinimumLabelEl");
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, entity);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, entity);
                if (minAmount == null) {
                    continue;
                }
                MinAmountData minAmountData = new MinAmountData(minAmount, minAmountLabel, new Amounts(), null, entity, seller);
                accountToMinAmount.put(entity.getId(), minAmountData);

                if (extraMinAmounts != null) {
                    accountToMinAmount = appendExtraAmount(extraMinAmounts, accountToMinAmount, entity);
                }
            }
            minAmountDataInfo = accountToMinAmount.get(entity.getId());
            minAmountDataInfo.getAmounts().addAmounts(amountWithoutTax, amountWithTax, null);

        }
        return accountToMinAmount;
    }

    /**
     * Gets the invoiceable amount for an entity in each hierarchy level.
     *
     * @param billableEntity the billable entity
     * @param firstTransactionDate first Transaction Date
     * @param lastTransactionDate last Transaction Date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @param accountClass account class
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, seller id
     */
    private List<Object[]> computeInvoiceableAmountForAccount(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate,
                                                              Class accountClass, String invoicingProcessType) {
        if (accountClass.equals(ServiceInstance.class)) {
            return computeInvoiceableAmountForServicesWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate, invoiceUpToDate, invoicingProcessType);
        }
        if (accountClass.equals(Subscription.class)) {
            return computeInvoiceableAmountForSubscriptionsWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate, invoiceUpToDate, invoicingProcessType);
        }
        if (accountClass.equals(UserAccount.class)) {
            return computeInvoiceableAmountForUserAccountsWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate, invoiceUpToDate, invoicingProcessType);
        }
        if (accountClass.equals(BillingAccount.class)) {
            return computeInvoiceableAmountForBillingAccountWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate, invoiceUpToDate, invoicingProcessType);
        }
        if (accountClass.equals(CustomerAccount.class)) {
            return computeInvoiceableAmountForCustomerAccountWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate, invoiceUpToDate, invoicingProcessType);
        }
        if (accountClass.equals(Customer.class)) {
            return computeInvoiceableAmountForCustomerWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate, invoiceUpToDate, invoicingProcessType);
        }
        return null;
    }
    
    /**
     * Summed rated transaction amounts applied on services, that have minimum invoiceable amount rule, grouped by invoice subCategory for a given billable entity.
     * 
     * @param billableEntity Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, serviceInstance
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForServicesWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate,
                                                                                Date lastTransactionDate, Date invoiceUpToDate, String invoicingProcessType) {
        if (billableEntity instanceof Subscription) {
        	Query query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableByServiceWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
        	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
                query.setParameter("invoiceUpToDate", invoiceUpToDate);
        	}
            return query.getResultList();
        } else if (billableEntity instanceof BillingAccount) {
            Query query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableByServiceWithMinAmountByBA")
                    .setParameter("billingAccount", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
        	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
                query.setParameter("invoiceUpToDate", invoiceUpToDate);
        	}
            return query.getResultList();
        }
        return null;
    }

    /**
     * Summed rated transaction amounts applied on subscriptions, that have minimum invoiceable amount rule for a given subscription.
     *
     * @param billableEntity Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, subscription
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForSubscriptionsWithMinAmountRule(IBillableEntity billableEntity,
                                                                                     Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate, String invoicingProcessType) {
        if (billableEntity instanceof Subscription) {
            Query query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableBySubscriptionWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
        	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
                query.setParameter("invoiceUpToDate", invoiceUpToDate);
        	}
            return query.getResultList();
        } else if (billableEntity instanceof BillingAccount) {
            Query query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableBySubscriptionWithMinAmountByBA")
                    .setParameter("billingAccount", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
        	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
                query.setParameter("invoiceUpToDate", invoiceUpToDate);
        	}
            return query.getResultList();
        }
        return null;
    }
    
    /**
     * Summed rated transaction amounts applied on UserAccounts, that have minimum invoiceable amount rule for a given user account.
     *
     * @param billableEntity Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, subscription
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForUserAccountsWithMinAmountRule(IBillableEntity billableEntity,
                                                                                    Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate, String invoicingProcessType) {
        if (billableEntity instanceof Subscription) {
            Query query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableForUAWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
        	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
                query.setParameter("invoiceUpToDate", invoiceUpToDate);
        	}
            return query.getResultList();
	    } else if (billableEntity instanceof BillingAccount) {
	        Query query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableWithMinAmountByUA")
	                .setParameter("billingAccount", billableEntity)
	                .setParameter("firstTransactionDate", firstTransactionDate)
	                .setParameter("lastTransactionDate", lastTransactionDate);
	    	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
	            query.setParameter("invoiceUpToDate", invoiceUpToDate);
	    	}
	        return query.getResultList();
	    }
        return null;
    }

    /**
     * Summed rated transaction amounts applied on BillingAccounts, that have minimum invoiceable amount rule for a given billing account
     *
     * @param billableEntity Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, seller id
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForBillingAccountWithMinAmountRule(IBillableEntity billableEntity,
                                                                                      Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate, String invoicingProcessType) {
        Query query;
    	if (billableEntity instanceof Subscription) {
            query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableForBAWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
	    	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
	            query.setParameter("invoiceUpToDate", invoiceUpToDate);
	    	}
	        return query.getResultList();
	    }
    	query =  getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableWithMinAmountByBA")
                .setParameter("billingAccount", billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);
    	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
            query.setParameter("invoiceUpToDate", invoiceUpToDate);
    	}
        return query.getResultList();
    }

    /**
     * Summed rated transaction amounts applied on CustomerAccounts, that have minimum invoiceable amount rule for a given customer account
     *
     * @param billableEntity Customer account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, seller id
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForCustomerAccountWithMinAmountRule(IBillableEntity billableEntity,
                                                                                       Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate, String invoicingProcessType) {
        Query query;
    	if (billableEntity instanceof Subscription) {
            query =  getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableForCAWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
	    	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
	            query.setParameter("invoiceUpToDate", invoiceUpToDate);
	    	}
	        return query.getResultList();
        }
        query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableWithMinAmountByCA")
                .setParameter("customerAccount", ((BillingAccount) billableEntity).getCustomerAccount())
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);
    	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
            query.setParameter("invoiceUpToDate", invoiceUpToDate);
    	}
        return query.getResultList();
    }
    
    /**
     * Summed rated transaction amounts applied on Customer, that have minimum invoiceable amount rule for a given customer
     *
     * @param billableEntity Customer account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, seller id
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForCustomerWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate,
                                                                                Date lastTransactionDate, Date invoiceUpToDate, String invoicingProcessType) {
        Query query;
    	if (billableEntity instanceof Subscription) {
            query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableForCustomerWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
	    	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
	            query.setParameter("invoiceUpToDate", invoiceUpToDate);
	    	}
	        return query.getResultList();
        }
        query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumInvoiceableWithMinAmountByCustomer")
                .setParameter("customer", ((BillingAccount) billableEntity).getCustomerAccount().getCustomer())
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);
    	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
            query.setParameter("invoiceUpToDate", invoiceUpToDate);
    	}
        return query.getResultList();
    }

    private Seller getSeller(BillingAccount billingAccount, IInvoicingMinimumApplicable entity) {
        if (entity instanceof ServiceInstance) {
            return ((ServiceInstance) entity).getSubscription().getSeller();
        }
        if (entity instanceof Subscription) {
            return ((Subscription) entity).getSeller();
        }
        return billingAccount.getCustomerAccount().getCustomer().getSeller();
    }

    private String getMinimumAmountElInfo(IInvoicingMinimumApplicable entity, String method) {
        try {
            Method getMinimumAmountElMethod = entity.getClass().getMethod(method);
            if (getMinimumAmountElMethod != null) {
                String value = (String) getMinimumAmountElMethod.invoke(entity);
                if (value == null && entity instanceof ServiceInstance) {
                    getMinimumAmountElMethod = ((ServiceInstance) entity).getServiceTemplate().getClass().getMethod(method);
                    value = (String) getMinimumAmountElMethod.invoke(((ServiceInstance) entity).getServiceTemplate());
                }
                if (value == null && entity instanceof Subscription) {
                    getMinimumAmountElMethod = ((Subscription) entity).getOffer().getClass().getMethod(method);
                    value = (String) getMinimumAmountElMethod.invoke(((Subscription) entity).getOffer());
                }
                return value;
            } else {
                throw new BusinessException("The method getMinimumAmountEl () is not defined for the entity " + entity.getClass().getSimpleName());
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BusinessException("The method getMinimumAmountEl () is not defined for the entity " + entity.getClass().getSimpleName());
        }
    }

    public BigDecimal evaluateMinAmountExpression(String expression, IInvoicingMinimumApplicable entity) throws BusinessException {
        if (isBlank(expression)) {
            return null;
        }
        if(NumberUtils.isCreatable(expression)) {
        	return new BigDecimal(expression);
        }
        Map<Object, Object> userMap = new HashMap<>();
        if (entity instanceof BillingAccount) {
            userMap = constructElContext(expression, (BillingAccount) entity, null, null, null);
        }
        if (entity instanceof UserAccount) {
            userMap = constructElContext(expression, null, null, null, (UserAccount) entity);
        }
        if (entity instanceof Subscription) {
            userMap = constructElContext(expression, null, (Subscription) entity, null, null);
        }
        if (entity instanceof ServiceInstance) {
            userMap = constructElContext(expression, null, null, (ServiceInstance) entity, null);
        }
        return evaluateExpression(expression, userMap, BigDecimal.class);
    }

    private Map<Object, Object> constructElContext(String expression, BillingAccount ba,
                                                   Subscription subscription, ServiceInstance serviceInstance, UserAccount ua) {
        Map<Object, Object> contextMap = new HashMap<>();
        if (expression.startsWith("#{")) {
            if (expression.indexOf(ValueExpressionWrapper.VAR_SERVICE_INSTANCE) >= 0) {
                contextMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, serviceInstance);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_SUBSCRIPTION) >= 0) {
                if (subscription == null) {
                    subscription = serviceInstance.getSubscription();
                }
                contextMap.put(ValueExpressionWrapper.VAR_SUBSCRIPTION, subscription);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_OFFER) >= 0) {
                if (subscription == null) {
                    subscription = serviceInstance.getSubscription();
                }
                contextMap.put(ValueExpressionWrapper.VAR_OFFER, subscription.getOffer());
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_USER_ACCOUNT) >= 0) {
                if (ua == null) {
                    ua = subscription != null ? subscription.getUserAccount() : serviceInstance.getSubscription().getUserAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_USER_ACCOUNT, ua);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, ba);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT) >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, ba.getCustomerAccount());
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_SHORT) >= 0 || expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER) >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_CUSTOMER_SHORT, ba.getCustomerAccount().getCustomer());
                contextMap.put(ValueExpressionWrapper.VAR_CUSTOMER, ba.getCustomerAccount().getCustomer());
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_PROVIDER) >= 0) {
                contextMap.put(ValueExpressionWrapper.VAR_PROVIDER, appProvider);
            }
        }
        return contextMap;
    }

    public String evaluateMinAmountLabelExpression(String expression, IInvoicingMinimumApplicable entity) throws BusinessException {
        if (isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<>();
        if (entity instanceof BillingAccount) {
            userMap = constructElContext(expression, (BillingAccount) entity, null, null, null);
        }
        if (entity instanceof UserAccount) {
            userMap = constructElContext(expression, null, null, null, (UserAccount) entity);
        }
        if (entity instanceof Subscription) {
            userMap = constructElContext(expression, null, (Subscription) entity, null, null);
        }
        if (entity instanceof ServiceInstance) {
            userMap = constructElContext(expression, null, null, (ServiceInstance) entity, null);
        }
        return evaluateExpression(expression, userMap, String.class);
    }

    private Map<Long, MinAmountData> appendExtraAmount(List<ExtraMinAmount> extraMinAmounts, Map<Long, MinAmountData> accountToMinAmount, IInvoicingMinimumApplicable entity) {
        MinAmountData minAmountDataInfo = accountToMinAmount.get(entity.getId());
        extraMinAmounts.forEach(extraMinAmount -> {
            IInvoicingMinimumApplicable extraMinAmountEntity = extraMinAmount.getEntity();
            if (isExtraMinAmountEntityChildOfEntity(extraMinAmountEntity, entity)) {
                Map<String, Amounts> extraAmounts = extraMinAmount.getCreatedAmount();
                for (Map.Entry<String, Amounts> amountInfo : extraAmounts.entrySet()) {
                    minAmountDataInfo.getAmounts().addAmounts(amountInfo.getValue());
                }
            }

        });
        return accountToMinAmount;
    }

    private boolean isExtraMinAmountEntityChildOfEntity(IInvoicingMinimumApplicable child, IInvoicingMinimumApplicable parent) {
        if (parent instanceof Subscription && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().equals(parent);
        }
        if (parent instanceof UserAccount && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().equals(parent);
        }
        if (parent instanceof UserAccount && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().equals(parent);
        }
        if (parent instanceof BillingAccount && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().getBillingAccount().equals(parent);
        }
        if (parent instanceof BillingAccount && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().getBillingAccount().equals(parent);
        }
        if (parent instanceof BillingAccount && child instanceof UserAccount) {
            return ((UserAccount) child).getBillingAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().getBillingAccount().getCustomerAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof UserAccount) {
            return ((UserAccount) child).getBillingAccount().getCustomerAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof BillingAccount) {
            return ((BillingAccount) child).getCustomerAccount().equals(parent);
        }
        if (parent instanceof Customer && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof UserAccount) {
            return ((UserAccount) child).getBillingAccount().getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof BillingAccount) {
            return ((BillingAccount) child).getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof CustomerAccount) {
            return ((CustomerAccount) child).getCustomer().equals(parent);
        }
        return false;
    }

    /**
     * Prepare each account level with minimum amount activated to generate the minimum object.
     *
     * @param billableEntity the billable entity can be a subscription or a billing account
     * @param billingAccount The billing account
     * @param extraMinAmounts The extra minimum amount generated in children levels
     * @param accountClass The account class
     * @param accountToMinAmount where to store the entity and new generated amounts to reach the minimum
     * @return A map where to store amounts for each entity
     */
    public Map<Long, MinAmountData> prepareAccountsWithMinAmount(IBillableEntity billableEntity, BillingAccount billingAccount,
                                                                 List<ExtraMinAmount> extraMinAmounts, Class accountClass,
                                                                 Map<Long, MinAmountData> accountToMinAmount) {
        List<IInvoicingMinimumApplicable> accountsWithMinAmount = getAccountsWithMinAmountElNotNull(billableEntity, accountClass);
        for (IInvoicingMinimumApplicable entity : accountsWithMinAmount) {
            MinAmountData minAmountInfo = accountToMinAmount.get(entity.getId());
            if (minAmountInfo == null) {
                String minAmountEL = getMinimumAmountElInfo(entity, "getMinimumAmountEl");
                String minAmountLabelEL = getMinimumAmountElInfo(entity, "getMinimumLabelEl");
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, entity);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, entity);
                Amounts accountAmounts = new Amounts(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                accountToMinAmount.put(entity.getId(), new MinAmountData(minAmount, minAmountLabel, accountAmounts.clone(),
                        null, entity, getSeller(billingAccount, entity)));
                if (extraMinAmounts != null) {
                    accountToMinAmount = appendExtraAmount(extraMinAmounts, accountToMinAmount, entity);
                }
            } else {
                if ((minAmountInfo.getMinAmount()).compareTo(appProvider.isEntreprise() ? minAmountInfo.getAmounts().getAmountWithoutTax()
                        : minAmountInfo.getAmounts().getAmountWithTax()) <= 0) {
                    accountToMinAmount.put(entity.getId(), null);
                }
            }
        }
        return accountToMinAmount;
    }

    private List<? extends IInvoicingMinimumApplicable> getAccountsWithMinAmountElNotNull(IBillableEntity billableEntity, Class<? extends IInvoicingMinimumApplicable> accountClass) {
        if (accountClass.equals(ServiceInstance.class)) {
            return getServicesWithMinAmount(billableEntity);
        }
        if (accountClass.equals(Subscription.class)) {
            return getSubscriptionsWithMinAmount(billableEntity);
        }
        if (accountClass.equals(UserAccount.class)) {
            return getUserAccountsWithMinAmountELNotNull(billableEntity);
        }
        if (accountClass.equals(BillingAccount.class)) {
            return getBillingAccountsWithMinAmountELNotNull(billableEntity);
        }
        if (accountClass.equals(CustomerAccount.class)) {
            return getCustomerAccountsWithMinAmountELNotNull(billableEntity);
        }
        if (accountClass.equals(Customer.class)) {
            return getCustomersWithMinAmountELNotNull(billableEntity);
        }
        return new ArrayList<>();
    }

    private List<IInvoicingMinimumApplicable> getServicesWithMinAmount(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("ServiceInstance.getServicesWithMinAmountBySubscription")
                    .setParameter("subscription", billableEntity)
                    .getResultList();
        } else if (billableEntity instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("ServiceInstance.getServicesWithMinAmountByBA")
                    .setParameter("billingAccount", billableEntity)
                    .getResultList();
        }
        return emptyList();
    }

    private List<? extends IInvoicingMinimumApplicable> getSubscriptionsWithMinAmount(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountBySubscription", Subscription.class)
                    .setParameter("subscription", billableEntity)
                    .getResultList();
        } else if (billableEntity instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountByBA", Subscription.class)
                    .setParameter("billingAccount", billableEntity)
                    .getResultList();
        }
        return emptyList();
    }

    private List<IInvoicingMinimumApplicable> getUserAccountsWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            return getEntityManager().createNamedQuery("UserAccount.getUserAccountsWithMinAmountELNotNullByUA")
                    .setParameter("userAccount", ((Subscription) billableEntity).getUserAccount())
                    .getResultList();
        }
        return getEntityManager().createNamedQuery("UserAccount.getUserAccountsWithMinAmountELNotNullByBA")
                .setParameter("billingAccount", billableEntity)
                .getResultList();
    }

    private List<IInvoicingMinimumApplicable> getBillingAccountsWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        return getEntityManager().createNamedQuery("BillingAccount.getBillingAccountsWithMinAmountELNotNullByBA")
                .setParameter("billingAccount", billableEntity)
                .getResultList();
    }

    private List<IInvoicingMinimumApplicable> getCustomerAccountsWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        return getEntityManager().createNamedQuery("CustomerAccount.getCustomerAccountsWithMinAmountELNotNullByBA")
                .setParameter("customerAccount", ((BillingAccount) billableEntity).getCustomerAccount())
                .getResultList();
    }

    private List<IInvoicingMinimumApplicable> getCustomersWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        return getEntityManager().createNamedQuery("Customer.getCustomersWithMinAmountELNotNullByBA")
                .setParameter("customer", ((BillingAccount) billableEntity).getCustomerAccount().getCustomer())
                .getResultList();
    }

    /**
     * Compute total invoiceable amount based on billableEntity
     * @param billableEntity Billable entity to compute total for
     * @param firstTransactionDate First transaction date
     * @param lastTransactionDate  Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @param invoicingProcessType
     * @return Amounts with and without tax, tax amount
     */
    public Amounts computeTotalInvoiceableAmount(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate, String invoicingProcessType) {
        if (billableEntity instanceof Subscription) {
            return computeTotalInvoiceableAmountForSubscription((Subscription) billableEntity, firstTransactionDate, lastTransactionDate, invoiceUpToDate, invoicingProcessType);
        }
        return computeTotalInvoiceableAmountForBillingAccount((BillingAccount) billableEntity, firstTransactionDate, lastTransactionDate, invoiceUpToDate, invoicingProcessType);
    }

    /**
     * Summed rated transaction amounts for a given subscription
     * 
     * @param subscription Subscription
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return Amounts with and without tax, tax amount
     */
    private Amounts computeTotalInvoiceableAmountForSubscription(Subscription subscription, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate, String invoicingProcessType) {
    	Query query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumTotalInvoiceableBySubscription")
                .setParameter("subscription", subscription)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);
    	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
            query.setParameter("invoiceUpToDate", invoiceUpToDate);
    	}
            	
    	return (Amounts) query.getSingleResult();
    }

    /**
     * Summed rated transaction amounts for a given billing account
     * 
     * @param billingAccount Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return Amounts with and without tax, tax amount
     */
    private Amounts computeTotalInvoiceableAmountForBillingAccount(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate, String invoicingProcessType) {
    	Query query = getEntityManager().createNamedQuery(invoicingProcessType + ".sumTotalInvoiceableByBA")
                .setParameter("billingAccount", billingAccount)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);
    	if(!"InvoiceLine".equalsIgnoreCase(invoicingProcessType)) {
            query.setParameter("invoiceUpToDate", invoiceUpToDate);
    	}
    	
        return (Amounts) query.getSingleResult();
    }

    /**
     * Determine if minimum functionality is used at all. A check is done on serviceInstance,
     * subscription or billing account entities for minimumAmountEl field value presence.
     *
     * @return An array of booleans indicating if minimum invoicing amount rule exists on service,
     * subscription and billingAccount levels, in that particular order.
     */
    public boolean[] isMinUsed() {
        boolean baMin = false;
        boolean subMin = false;
        boolean servMin = false;
        boolean uaMin = false;
        boolean caMin = false;
        boolean custMin = false;
        try {
            getEntityManager()
                    .createNamedQuery("BillingAccount.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            baMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("UserAccount.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            uaMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("Subscription.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            subMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("ServiceInstance.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            servMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("CustomerAccount.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            caMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager()
                    .createNamedQuery("Customer.getMinimumAmountUsed")
                    .setMaxResults(1)
                    .getSingleResult();
            custMin = true;
        } catch (NoResultException e) {
        }
        return new boolean[]{servMin, subMin, uaMin, baMin, caMin, custMin};
    }
    
}