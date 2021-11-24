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
package org.meveo.admin.action.payments;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.RandomStringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.AccountBean;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CheckPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaypalPaymentMethod;
import org.meveo.model.payments.StripePaymentMethod;
import org.meveo.model.payments.WirePaymentMethod;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;

/**
 * Standard backing bean for {@link CustomerAccount} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Named
@ViewScoped
public class CustomerAccountBean extends AccountBean<CustomerAccount> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link CustomerAccount} service. Extends {@link PersistenceService}.
     */
    @Inject
    private CustomerAccountService customerAccountService;

    /**
     * Injected @{link Custome} service. Extends {@link PersistenceService}.
     */
    @Inject
    private CustomerService customerService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private PaymentMethodService paymentMethodService;

    @Inject
    private CounterInstanceService counterInstanceService;

    /**
     * Customer Id passed as a parameter. Used when creating new Customer Account from customer account window, so default customer account will be set on newly created customer
     * Account.
     */
    private Long customerId;

    private CustomerAccount customerAccountTransfer;

    private BigDecimal amountToTransfer;

    private PaymentMethodEnum newPaymentMethodType = PaymentMethodEnum.CARD;
    private PaymentMethod selectedPaymentMethod;

    private CounterInstance selectedCounterInstance;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CustomerAccountBean() {
        super(CustomerAccount.class);
    }

    @Override
    public CustomerAccount initEntity() {
        super.initEntity();
        if (entity.getId() == null) {
            entity.setDateDunningLevel(new Date());
            entity.setPassword(RandomStringUtils.randomAlphabetic(8));
        }
        if (entity.getId() == null && getCustomerId() != null) {
            Customer customer = customerService.findById(getCustomerId());
            populateAccounts(customer);
        }
        this.initNestedFields(entity);
        if (entity.getId() != null) {
            if (!entity.getCardPaymentMethods(false).isEmpty()) {
                if (entity.isNoMoreValidCard()) {
                    messages.warn(new BundleKey("messages", "customerAccount.noMoreValidCard"));
                }
            }
        }
        selectedCounterInstance = entity.getCounters() != null && entity.getCounters().size() > 0 ? entity.getCounters().values().iterator().next() : null;
        return entity;
    }

    @Override
    public CustomerAccount getEntity() {
        CustomerAccount ca = super.getEntity();
        this.initNestedFields(ca);
        return ca;
    }

    private void initNestedFields(CustomerAccount ca) {
        if (ca.getAddress() == null) {
            ca.setAddress(new Address());
        }
        if (ca.getName() == null) {
            ca.setName(new Name());
        }
        if (ca.getContactInformation() == null) {
            ca.setContactInformation(new ContactInformation());
        }
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        if (entity.getPreferredPaymentMethod() == null) {
            throw new ValidationException("CustomerAccount does not have a preferred payment method", "paymentMethod.noPreferredPaymentMethod");
        }

        if (!entity.isTransient()) {
            CustomerAccount customerAccountFromDB = customerAccountService.findByCode(entity.getCode());
            if (!entity.getCustomer().equals(customerAccountFromDB.getCustomer())) {
                // a safeguard to allow this only if all the WO/RT have been invoiced.
                Long countNonTreatedWO = walletOperationService.countNonTreatedWOByCA(entity);
                if (countNonTreatedWO > 0) {
                    messages.error(new BundleKey("messages", "customerAccount.nontreatedWO"));
                    return null;
                }
                Long countNonInvoicedRT = ratedTransactionService.countNotInvoicedRTByCA(entity);
                if (countNonInvoicedRT > 0) {
                    messages.error(new BundleKey("messages", "customerAccount.nonInvoicedRT"));
                    return null;
                }
                Long countUnmatchedAO = accountOperationService.countUnmatchedAOByCA(entity);
                if (countUnmatchedAO > 0) {
                    messages.error(new BundleKey("messages", "customerAccount.unmatchedAO"));
                    return null;
                }
            }
        }

        entity.setCustomer(customerService.findById(entity.getCustomer().getId()));

        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return getEditViewName();// "/pages/payments/customerAccounts/customerAccountDetail.xhtml?edit=true&customerAccountId=" + entity.getId() +
                                     // "&faces-redirect=true&includeViewParams=true";
        }

        return null;
    }

    /**
     * Move selected accountOperation from current CustomerAccount to customerAccountTo
     * 
     * @return output view
     */
    public String transferAccount() {
        try {
            entity = customerAccountService.refreshOrRetrieve(entity);
            customerAccountTransfer = customerAccountService.refreshOrRetrieve(customerAccountTransfer);

            customerAccountService.transferAccount(entity, customerAccountTransfer, getAmountToTransfer());
            messages.info(new BundleKey("messages", "customerAccount.transfertOK"));
            setCustomerAccountTransfer(null);
            setAmountToTransfer(null);

        } catch (Exception e) {
            log.error("failed to transfer account ", e);
            messages.error(new BundleKey("messages", "customerAccount.transfertKO"), e.getMessage());
            return null;
        }

        return "customerAccountDetailOperationsTab";
    }

    public String backCA() {

        return "customerAccountDetailOperationsTab";
    }

    @Override
    protected IPersistenceService<CustomerAccount> getPersistenceService() {
        return customerAccountService;
    }

    /**
     * Compute balance due
     * 
     * @return due balance
     */
    public BigDecimal getBalanceDue() {
        if (entity.getId() == null) {
            return new BigDecimal(0);
        } else {
            return customerAccountService.customerAccountBalanceDue(entity, new Date());
        }
    }

    /**
     * Compute balance exigible without litigation.
     * 
     * @return exigible balance without litigation
     */
    public BigDecimal getBalanceDueWithoutLitigation() {
        if (entity.getId() == null) {
            return new BigDecimal(0);
        } else {
            return customerAccountService.customerAccountBalanceDueWithoutLitigation(entity, new Date());
        }
    }

    /**
     * Compute a total balance
     * 
     * @return Total balance
     */
    public BigDecimal getBalanceTotal() {
        if (entity.getId() == null) {
            return new BigDecimal(0);
        } else {
            return customerAccountService.customerAccountBalanceDue(entity, null);
        }
    }

    /**
     * Compute a total balance without litigation
     * 
     * @return Total balance without litigation
     */
    public BigDecimal getBalanceTotalWithoutLitigation() {
        if (entity.getId() == null) {
            return new BigDecimal(0);
        } else {
            return customerAccountService.customerAccountBalanceDueWithoutLitigation(entity, null);
        }
    }

    /**
     * Is current customerAccount active.
     * 
     * @return State of customer account : active / not active
     */
    public boolean isActiveAccount() {
        if (entity != null && entity.getId() != null) {
            return entity.getStatus() == CustomerAccountStatusEnum.ACTIVE;
        }
        return false;
    }

    /**
     * Close customerAccount.
     * 
     * @return Edit view
     */
    public String closeCustomerAccount() {
        log.info("closeAccount customerAccountId:" + entity.getId());
        try {
            entity = customerAccountService.refreshOrRetrieve(entity);
            customerAccountService.closeCustomerAccount(entity);
            messages.info(new BundleKey("messages", "customerAccount.closeSuccessful"));

        } catch (Exception e) {
            log.error("Failed to close account ", e);
            messages.error(new BundleKey("messages", "close.closeUnsuccessful"), e.getMessage());
        }
        return getEditViewName();
    }

    /**
     * @param customerAccountTransfer the customerAccountTransfer to set
     */
    public void setCustomerAccountTransfer(CustomerAccount customerAccountTransfer) {
        this.customerAccountTransfer = customerAccountTransfer;
    }

    /**
     * @return the customerAccountTransfer
     */
    public CustomerAccount getCustomerAccountTransfer() {
        return customerAccountTransfer;
    }

    /**
     * @param amountToTransfer the amountToTransfer to set
     */
    public void setAmountToTransfer(BigDecimal amountToTransfer) {
        this.amountToTransfer = amountToTransfer;
    }

    /**
     * @return the amountToTransfer
     */
    public BigDecimal getAmountToTransfer() {
        return amountToTransfer;
    }

    public void populateAccounts(Customer customer) {
        entity.setCustomer(customer);

        if (customer != null && appProvider.isLevelDuplication()) {
            entity.setCode(customer.getCode());
            entity.setDescription(customer.getDescription());
            entity.setAddress(customer.getAddress());
            entity.setExternalRef1(customer.getExternalRef1());
            entity.setExternalRef2(customer.getExternalRef2());
            entity.setProviderContact(customer.getProviderContact());
            entity.setName(customer.getName());
            entity.setPrimaryContact(customer.getPrimaryContact());
            entity.setContactInformation(customer.getContactInformation());
        }
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("customer");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("customer");
    }

    public PaymentMethod getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public void setSelectedPaymentMethod(PaymentMethod selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }

    public void newPaymentMethod() {
        if (newPaymentMethodType == PaymentMethodEnum.CARD) {
            selectedPaymentMethod = new CardPaymentMethod();
        } else if (newPaymentMethodType == PaymentMethodEnum.CHECK) {
            selectedPaymentMethod = new CheckPaymentMethod();
        } else if (newPaymentMethodType == PaymentMethodEnum.WIRETRANSFER) {
            selectedPaymentMethod = new WirePaymentMethod();
        } else if (newPaymentMethodType == PaymentMethodEnum.DIRECTDEBIT) {
            selectedPaymentMethod = new DDPaymentMethod();
        } else if (newPaymentMethodType == PaymentMethodEnum.PAYPAL) {
            selectedPaymentMethod = new PaypalPaymentMethod();
        } else if (newPaymentMethodType == PaymentMethodEnum.STRIPE) {
            selectedPaymentMethod = new StripePaymentMethod();
        }

    }

    public void editPaymentMethod(PaymentMethod paymentMethod) throws BusinessException {
        selectedPaymentMethod = clonePaymentMethod(paymentMethod);
    }

    private PaymentMethod clonePaymentMethod(PaymentMethod itemToClone) throws BusinessException {

        try {
            return (PaymentMethod) BeanUtilsBean.getInstance().cloneBean(itemToClone);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to clone paymentMethod for edit", e);
            throw new BusinessException(e);
        }
    }

    /**
     * Cancel editing/creation of payment method
     */
    public void cancelPaymentMethodEdit() {
        selectedPaymentMethod = null;
    }

    /**
     * Save or update payment method
     */
    @ActionMethod
    public void savePaymentMethod() {
        try {
            checkIsBicRequired();

            selectedPaymentMethod.updateAudit(currentUser);
            if (selectedPaymentMethod instanceof CardPaymentMethod) {
                if (((CardPaymentMethod) selectedPaymentMethod).getTokenId() == null && ((CardPaymentMethod) selectedPaymentMethod).getCardNumber() != null) {
                    ((CardPaymentMethod) selectedPaymentMethod).setHiddenCardNumber(CardPaymentMethod.hideCardNumber(((CardPaymentMethod) selectedPaymentMethod).getCardNumber()));
                }
            }

            if (selectedPaymentMethod instanceof DDPaymentMethod) {
                DDPaymentMethod ddPaymentMethod = (DDPaymentMethod) selectedPaymentMethod;
                String error = paymentMethodService.validateBankCoordinates(ddPaymentMethod, entity.getCustomer(), false);
                if (!StringUtils.isBlank(error)) {
                    throw new BusinessException(error);
                }
            }

            if (entity.getPaymentMethods() == null) {
                entity.setPaymentMethods(new ArrayList<PaymentMethod>());
            }

            if (entity.getPreferredPaymentMethod() == null) {
                selectedPaymentMethod.setPreferred(true);
            }

            if (!entity.getPaymentMethods().stream().anyMatch(paymentMethod -> paymentMethod.getId()!= null && paymentMethod.getId().equals(selectedPaymentMethod.getId()))) {
                selectedPaymentMethod.setCustomerAccount(getEntity());
                entity.getPaymentMethods().add(selectedPaymentMethod);
            } else {
                entity.getPaymentMethods().set(entity.getPaymentMethods().indexOf(selectedPaymentMethod), selectedPaymentMethod);
            }
            entity.addPaymentMethodToAudit(new Object() {
            }.getClass().getEnclosingMethod().getName(), selectedPaymentMethod);
            selectedPaymentMethod = null;

            messages.info(new BundleKey("messages", "paymentMethod.saved.ok"));

        } catch (Exception e) {
            log.error("Failed to save payment method", e);
            messages.error(new BundleKey("messages", "paymentMethod.saved.ko"), e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            facesContext.validationFailed();
        }
    }

    public PaymentMethodEnum getNewPaymentMethodType() {
        return newPaymentMethodType;
    }

    public void setNewPaymentMethodType(PaymentMethodEnum newPaymentMethodType) {
        this.newPaymentMethodType = newPaymentMethodType;
    }

    /**
     * Mark payment method as preferred. Only one payment method can be preferred.
     * 
     * @param paymentMethodToPrefer Payment method to mark as preferred. If null, a first payment method will be checked as preferred.
     */
    public void setPreferredPaymentMethod(PaymentMethod paymentMethodToPrefer) {

        if (entity.getPaymentMethods() == null || entity.getPaymentMethods().isEmpty()) {
            return;
        }

        boolean showMessage = false;
        if (paymentMethodToPrefer == null) {
            paymentMethodToPrefer = entity.getPaymentMethods().get(0);
        } else {
            showMessage = true;
        }
        String error = null;
        if (paymentMethodToPrefer instanceof DDPaymentMethod) {
            DDPaymentMethod ddPaymentMethod = (DDPaymentMethod) paymentMethodToPrefer;
            error = paymentMethodService.validateBankCoordinates(ddPaymentMethod, entity.getCustomer(), true);
        }
        if (!StringUtils.isBlank(error)) {
            log.error("cannot define as preferred payment method : " + error);
            messages.error(new BundleKey("messages", "paymentMethod.setPreferred.ko"), error);
        } else {
            paymentMethodToPrefer.setPreferred(true);
            entity.addPaymentMethodToAudit(new Object() {
            }.getClass().getEnclosingMethod().getName(), paymentMethodToPrefer);
            for (PaymentMethod paymentMethod : entity.getPaymentMethods()) {
                if (!paymentMethod.equals(paymentMethodToPrefer)) {
                    paymentMethod.setPreferred(false);
                }
            }
            entity.addPaymentMethodToAudit(new Object() {
            }.getClass().getEnclosingMethod().getName(), paymentMethodToPrefer);
            if (showMessage) {
                messages.info(new BundleKey("messages", "paymentMethod.setPreferred.ok"));
            }
        }
    }

    public void removePaymentMethod(PaymentMethod paymentMethod) {
        if (entity.getPaymentMethods() == null || entity.getPaymentMethods().isEmpty()) {
            return;
        }
        entity.getPaymentMethods().remove(paymentMethod);
        entity.addPaymentMethodToAudit(new Object() {
        }.getClass().getEnclosingMethod().getName(), paymentMethod);
        messages.info(new BundleKey("messages", "paymentMethod.removed.ok"));

    }

    @ActionMethod
    public void disablePaymentMethod(PaymentMethod paymentMethod) {
        if (entity.getPaymentMethods() == null || entity.getPaymentMethods().isEmpty()) {
            return;
        }
        paymentMethod.setDisabled(true);
        paymentMethod.setPreferred(false);
        entity.getPaymentMethods().set(entity.getPaymentMethods().indexOf(paymentMethod), paymentMethod);
        entity.addPaymentMethodToAudit(new Object() {
        }.getClass().getEnclosingMethod().getName(), paymentMethod);
        messages.info(new BundleKey("messages", "disabled.successful"));

    }

    @ActionMethod
    public void enablePaymentMethod(PaymentMethod paymentMethod) {
        if (entity.getPaymentMethods() == null || entity.getPaymentMethods().isEmpty()) {
            return;
        }
        paymentMethod.setDisabled(false);
        entity.getPaymentMethods().set(entity.getPaymentMethods().indexOf(paymentMethod), paymentMethod);
        entity.ensureOnePreferredPaymentMethod();
        entity.addPaymentMethodToAudit(new Object() {
        }.getClass().getEnclosingMethod().getName(), paymentMethod);
        messages.info(new BundleKey("messages", "enabled.successful"));

    }

    /**
     * If Seller country is different from IBAN customer country (two first letter), then the BIC is mandatory. If no country on seller, check this on "Application configuration"
     * Bank information Iban two first letters. If no seller nor system country information, BIC stay mandatory.
     * 
     * @throws Exception exception
     */
    public void checkIsBicRequired() throws Exception {
        if (selectedPaymentMethod instanceof DDPaymentMethod && ((DDPaymentMethod) selectedPaymentMethod).getBankCoordinates() != null) {
            String iban = ((DDPaymentMethod) selectedPaymentMethod).getBankCoordinates().getIban();
            String bic = ((DDPaymentMethod) selectedPaymentMethod).getBankCoordinates().getBic();
            if (!StringUtils.isBlank(iban) && iban.length() > 1) {
                if (StringUtils.isBlank(bic) && customerService.isBicRequired(entity.getCustomer(), iban)) {
                    throw new Exception("Missing BIC.");
                }
            }
        }
    }

    public CounterInstance getSelectedCounterInstance() {
        if (entity == null) {
            initEntity();
        }
        return selectedCounterInstance;
    }

    public void setSelectedCounterInstance(CounterInstance selectedCounterInstance) {
        if (selectedCounterInstance != null) {
            this.selectedCounterInstance = counterInstanceService.refreshOrRetrieve(selectedCounterInstance);
        } else {
            this.selectedCounterInstance = null;
        }
    }
}