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
package org.meveo.admin.action.billing;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.action.catalog.OfferTemplateBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.util.pagination.EntityListDataModelPF;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.ProductChargeInstanceService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.UsageChargeInstanceService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.medina.impl.AccessService;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link Subscription} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Wassim Drira
 * @author Said Ramli
 * @author Abdellatif BARI
 * @author Mounir BAHIJE
 * @lastModifiedVersion 7.0
 */
@Named
@ViewScoped
public class SubscriptionBean extends CustomFieldBean<Subscription> {

    private static final long serialVersionUID = 1L;

    @Inject
    private SubscriptionService subscriptionService;

    /**
     * UserAccount service. TODO (needed?)
     */
    @Inject
    private UserAccountService userAccountService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private UsageChargeInstanceService usageChargeInstanceService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private AccessService accessService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private ProductChargeInstanceService productChargeInstanceService;

    @Inject
    private ProductInstanceService productInstanceService;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private SellerService sellerService;

    @Inject
    private WalletTemplateService walletTemplateService;

    @Inject
    @ViewBean
    private OfferTemplateBean offerTemplateBean;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private FacesContext facesContext;

    @Inject
    private BillingAccountService billingAccountService;

    private ServiceInstance selectedServiceInstance;

    private ProductInstance productInstance;

    private BigDecimal quantity = BigDecimal.ONE;

    private OneShotChargeInstance oneShotChargeInstance = null;

    private RecurringChargeInstance recurringChargeInstance;

    private UsageChargeInstance usageChargeInstance;

    private WalletTemplate selectedWalletTemplate;

    private boolean showApplyOneShotForm = false;

    private String selectedWalletTemplateCode;

    private List<WalletTemplate> prepaidWalletTemplates;

    private Date terminationDate;

    private SubscriptionTerminationReason terminationReason;

    private ServiceInstance selectedTerminableService;

    private LazyDataModel<OfferTemplate> activeOfferTemplateDataModel;

    private CounterInstance selectedCounterInstance;

    /**
     * User Account Id passed as a parameter. Used when creating new subscription entry from user account definition window, so default uset Account will be set on newly created
     * subscription entry.
     */
    private Long userAccountId;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */

    private EntityListDataModelPF<ServiceTemplate> serviceTemplates = new EntityListDataModelPF<ServiceTemplate>(new ArrayList<ServiceTemplate>());

    private EntityListDataModelPF<ServiceInstance> serviceInstances = new EntityListDataModelPF<ServiceInstance>(new ArrayList<ServiceInstance>());

    private EntityListDataModelPF<ServiceInstance> terminableServices = new EntityListDataModelPF<ServiceInstance>(new ArrayList<ServiceInstance>());

    private EntityListDataModelPF<OneShotChargeInstance> oneShotChargeInstances = null;
    private EntityListDataModelPF<RecurringChargeInstance> recurringChargeInstances = null;
    private EntityListDataModelPF<UsageChargeInstance> usageChargeInstances = null;
    private EntityListDataModelPF<ProductChargeInstance> productChargeInstances = null;
    private EntityListDataModelPF<ProductInstance> productInstances = null;

    public SubscriptionBean() {
        super(Subscription.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return subscription.
     */
    @Override
    public Subscription initEntity() {
        super.initEntity();
        if (entity.getId() == null && userAccountId != null) {
            UserAccount userAccount = userAccountService.findById(getUserAccountId());
            populateAccounts(userAccount);

        }
        log.debug("SubscriptionBean initEntity id={}", entity.getId());
        if (entity.getId() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(entity.getSubscriptionDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            entity.setSubscriptionDate(calendar.getTime());

        } else {
            initServiceTemplates();
            initServiceInstances(entity.getServiceInstances());
            initTerminableServices(entity.getServiceInstances());
            selectedCounterInstance = entity.getCounters() != null && entity.getCounters().size() > 0 ? entity.getCounters().values().iterator().next() : null;

        }

        return entity;
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("productInstances");
    }

    private void initServiceTemplates() {

        // Clear existing list value
        serviceTemplates = new EntityListDataModelPF<ServiceTemplate>(new ArrayList<ServiceTemplate>());
        boolean allowServiceMultiInstantiation = ParamBeanFactory.getAppScopeInstance().isServiceMultiInstantiation();

        if (entity.getOffer() == null) {
            return;
        }

        List<ServiceInstance> serviceInstances = entity.getServiceInstances();
        for (OfferServiceTemplate offerServiceTemplate : entity.getOffer().getOfferServiceTemplates()) {
            ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
            if (serviceTemplate.isDisabled()) {
                continue;
            }

            boolean alreadyInstanciated = false;

            if (!allowServiceMultiInstantiation) {
                for (ServiceInstance serviceInstance : serviceInstances) {
                    if (serviceTemplate.getCode().equals(serviceInstance.getCode()) && !hasTerminatedStatus(serviceInstance.getStatus())) {
                        alreadyInstanciated = true;
                        break;
                    }
                }
            }

            if (!alreadyInstanciated) {
                serviceTemplate.setDescriptionOverride(serviceTemplate.getDescription());
                serviceTemplates.add(serviceTemplate);
            }
        }
        log.debug("servicetemplates initialized with {} templates ", serviceTemplates.getSize());
    }

    /**
     * Check either the service's status is TERMINATED, CLOSED, CANCELED
     *
     * @param status service's status
     * @return true if service's status is TERMINATED, CLOSED, CANCELED
     */
    private boolean hasTerminatedStatus(InstanceStatusEnum status) {
        return InstanceStatusEnum.TERMINATED.equals(status) || InstanceStatusEnum.CLOSED.equals(status) || InstanceStatusEnum.CANCELED.equals(status);
    }

    public BillingCycle getBillingCycle() {
        return entity.getBillingCycle();
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        if (billingCycle != null) {
            entity.setBillingCycle(billingCycle);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        entity.setOffer(offerTemplateService.refreshOrRetrieve(entity.getOffer()));
        entity.setUserAccount(userAccountService.refreshOrRetrieve(entity.getUserAccount()));
        SubscriptionRenewal subscriptionRenewal = entity.getSubscriptionRenewal();
        org.meveo.model.catalog.Calendar calendarRenewFor = calendarService.refreshOrRetrieve(subscriptionRenewal.getCalendarRenewFor());
        subscriptionRenewal.setCalendarRenewFor(calendarRenewFor);
        org.meveo.model.catalog.Calendar calendarInitialyActiveFor = calendarService.refreshOrRetrieve(subscriptionRenewal.getCalendarInitialyActiveFor());
        subscriptionRenewal.setCalendarInitialyActiveFor(calendarInitialyActiveFor);
        entity.setSubscriptionRenewal(subscriptionRenewal);

        if (entity.getOffer().getValidity() != null && !entity.getOffer().getValidity().isCorrespondsToPeriod(entity.getSubscriptionDate())) {

            String datePattern = paramBeanFactory.getInstance().getDateFormat();
            messages.error(new BundleKey("messages", "subscription.error.offerTemplateInvalidVersion"), entity.getOffer().getValidity().toString(datePattern),
                DateUtils.formatDateWithPattern(entity.getSubscriptionDate(), datePattern));
            facesContext.validationFailed();
            return null;
        }
        setObjectId(entity.getId());
        if (entity.getOffer().isDisabled()) {
            messages.error(new BundleKey("messages", "message.subscription.offerIsDisabled"));
            return null;
        }

        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return "subscriptionDetailSubscriptionTab"; // getEditViewName(); //
                                                        // "/pages/billing/subscriptions/subscriptionDetail?edit=false&subscriptionId="
                                                        // + entity.getId() +
            // "&faces-redirect=true&includeViewParams=true";
        }
        return null;
    }

    public void newOneShotChargeInstance() {
        this.oneShotChargeInstance = new OneShotChargeInstance();
        selectedWalletTemplate = new WalletTemplate();
        selectedWalletTemplateCode = null;
    }

    public void editOneShotChargeIns(OneShotChargeInstance oneShotChargeIns) {
        this.oneShotChargeInstance = oneShotChargeInstanceService.refreshOrRetrieve(oneShotChargeIns);
        selectedWalletTemplate = new WalletTemplate();
        selectedWalletTemplateCode = null;
    }

    public void saveOneShotChargeIns() {
        log.debug("saveOneShotChargeIns getObjectId={}, wallet {}", getObjectId(), selectedWalletTemplate);

        if (oneShotChargeInstance.getChargeTemplate() == null) {
            messages.error(new BundleKey("messages", "error.codeRequired"));
            return;
        }

        try {
            if (selectedWalletTemplate.getCode() == null) {
                selectedWalletTemplate.setCode(WalletTemplate.PRINCIPAL);
            }

            entity = subscriptionService.refreshOrRetrieve(entity);
            String description = oneShotChargeInstance.getDescription();
            OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findById(oneShotChargeInstance.getChargeTemplate().getId());
            oneShotChargeInstance.setChargeTemplate(oneShotChargeTemplate);
            oneShotChargeInstance.setDescription(description);

            if (oneShotChargeInstance.getChargeDate() == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                oneShotChargeInstance.setChargeDate(calendar.getTime());
            }

            oneShotChargeInstance.setSubscription(entity);
            oneShotChargeInstance.setSeller(entity.getSeller());
            oneShotChargeInstance.setCurrency(entity.getUserAccount().getBillingAccount().getCustomerAccount().getTradingCurrency());
            oneShotChargeInstance.setCountry(entity.getUserAccount().getBillingAccount().getTradingCountry());

            oneShotChargeInstanceService.oneShotChargeApplication(entity, null, (OneShotChargeTemplate) oneShotChargeInstance.getChargeTemplate(), selectedWalletTemplate.getCode(), oneShotChargeInstance.getChargeDate(),
                oneShotChargeInstance.getAmountWithoutTax(), oneShotChargeInstance.getAmountWithTax(), oneShotChargeInstance.getQuantity(), oneShotChargeInstance.getCriteria1(), oneShotChargeInstance.getCriteria2(),
                oneShotChargeInstance.getCriteria3(), description, null, null, true, ChargeApplicationModeEnum.SUBSCRIPTION);

            oneShotChargeInstance = null;
            oneShotChargeInstances = null;
            clearObjectId();

            showApplyOneShotForm = false;

            messages.info(new BundleKey("messages", "save.successful"));

        } catch (BusinessException e1) {
            log.error("exception when applying one shot charge! {}", e1.getMessage());
            messages.error(e1.getMessage());
        } catch (Exception e) {
            log.error("exception when applying one shot charge! {}", e.getMessage());
            messages.error(e.getMessage());
        }
    }

    public void editRecurringChargeIns(RecurringChargeInstance recurringChargeIns) {
        this.recurringChargeInstance = recurringChargeInstanceService.refreshOrRetrieve(recurringChargeIns);
    }

    public void saveRecurringChargeIns() {
        log.debug("saveRecurringChargeIns getObjectId={}", getObjectId());
        try {
            if ((recurringChargeInstance != null) && (recurringChargeInstance.getId() != null)) {
                log.debug("update RecurringChargeIns {}, id={}", recurringChargeInstance, recurringChargeInstance.getId());
                recurringChargeInstanceService.update(recurringChargeInstance);

                recurringChargeInstance = null;
                recurringChargeInstances = null;
                clearObjectId();

                messages.info(new BundleKey("messages", "save.successful"));
            }
        } catch (Exception e) {
            log.error("exception when applying recurring charge!", e);
            messages.error(e.getMessage());
        }
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Subscription> getPersistenceService() {
        return subscriptionService;
    }

    // /**
    // * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
    // */
    // protected List<String> getFormFieldsToFetch() {
    // return Arrays.asList("serviceInstances");
    // }
    //
    // /**
    // * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
    // */
    // protected List<String> getListFieldsToFetch() {
    // return Arrays.asList("serviceInstances");
    // }

    public EntityListDataModelPF<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public EntityListDataModelPF<ServiceInstance> getTerminableServices() {
        return terminableServices;
    }

    public EntityListDataModelPF<ServiceTemplate> getServiceTemplates() {
        return serviceTemplates;
    }

    public OneShotChargeInstance getOneShotChargeInstance() {
        if (oneShotChargeInstance != null && oneShotChargeInstance.getChargeTemplate() != null) {
            if (oneShotChargeInstance.getDescription() != null && oneShotChargeInstance.getDescription().equals(oneShotChargeInstance.getChargeTemplate().getDescription())) {
                if (oneShotChargeInstance.getChargeTemplate().getDescriptionI18n() != null) {
                    String languageCode = tradingLanguageService.retrieveIfNotManaged(entity.getUserAccount().getBillingAccount().getTradingLanguage()).getLanguage().getLanguageCode();
                    if (!StringUtils.isBlank(oneShotChargeInstance.getChargeTemplate().getDescriptionI18n().get(languageCode))) {
                        oneShotChargeInstance.setDescription(oneShotChargeInstance.getChargeTemplate().getDescriptionI18n().get(languageCode));
                    }
                }
                if (StringUtils.isBlank(oneShotChargeInstance.getDescription())) {
                    oneShotChargeInstance.setDescription(oneShotChargeInstance.getChargeTemplate().getDescription());
                }
            }
        }
        return oneShotChargeInstance;
    }

    public RecurringChargeInstance getRecurringChargeInstance() {
        return recurringChargeInstance;
    }

    public EntityListDataModelPF<OneShotChargeInstance> getOneShotChargeInstances() {

        if (oneShotChargeInstances != null || (entity == null || entity.getId() == null)) {
            return oneShotChargeInstances;
        }

        oneShotChargeInstances = new EntityListDataModelPF<OneShotChargeInstance>(new ArrayList<OneShotChargeInstance>());
        oneShotChargeInstances.addAll(oneShotChargeInstanceService.findOneShotChargeInstancesBySubscriptionId(entity.getId()));
        return oneShotChargeInstances;
    }

    public List<WalletOperation> getOneShotWalletOperations() {
        if (this.oneShotChargeInstance == null || this.oneShotChargeInstance.getId() == null) {
            return null;
        }
        return oneShotChargeInstance.getWalletOperationsSorted();
    }

    public List<WalletOperation> getRecurringWalletOperations() {
        if (this.recurringChargeInstance == null || this.recurringChargeInstance.getId() == null) {
            return null;
        }

        return recurringChargeInstance.getWalletOperationsSorted();
    }

    public EntityListDataModelPF<RecurringChargeInstance> getRecurringChargeInstances() {

        if (recurringChargeInstances != null || (entity == null || entity.getId() == null)) {
            return recurringChargeInstances;
        }

        recurringChargeInstances = new EntityListDataModelPF<RecurringChargeInstance>(new ArrayList<RecurringChargeInstance>());
        recurringChargeInstances.addAll(recurringChargeInstanceService.findRecurringChargeInstanceBySubscriptionId(entity.getId()));
        return recurringChargeInstances;
    }

    public EntityListDataModelPF<UsageChargeInstance> getUsageChargeInstances() {

        if (usageChargeInstances != null || (entity == null || entity.getId() == null)) {
            return usageChargeInstances;
        }

        usageChargeInstances = new EntityListDataModelPF<UsageChargeInstance>(new ArrayList<UsageChargeInstance>());
        usageChargeInstances.addAll(usageChargeInstanceService.findUsageChargeInstanceBySubscriptionId(entity.getId()));
        return usageChargeInstances;
    }

    @ActionMethod
    public void instanciateManyServices() throws BusinessException {

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("instanciateManyServices quantity is negative! set it to 1");
            quantity = BigDecimal.ONE;
        }
        boolean isChecked = false;

        entity = subscriptionService.refreshOrRetrieve(entity);

        log.debug("Instantiating serviceTemplates {}", serviceTemplates.getSelectedItemsAsList());

        OfferTemplate offerTemplate = ((Subscription) entity).getOffer();

        subscriptionService.checkCompatibilityOfferServices(((Subscription) entity), serviceTemplates.getSelectedItemsAsList());

        for (ServiceTemplate serviceTemplate : serviceTemplates.getSelectedItemsAsList()) {

            String descriptionOverride = serviceTemplate.getDescriptionOverride();
            serviceTemplate = serviceTemplateService.findById(serviceTemplate.getId());

            isChecked = true;
            log.debug("instanciateManyServices id={} checked, quantity={}", serviceTemplate.getId(), quantity);

            ServiceInstance serviceInstance = new ServiceInstance();
            serviceInstance.setCode(serviceTemplate.getCode());
            serviceInstance.setDescription(descriptionOverride);
            serviceInstance.setServiceTemplate(serviceTemplate);
            serviceInstance.setSubscription((Subscription) entity);
            if (entity.getSubscriptionDate() != null) {
                serviceInstance.setSubscriptionDate(entity.getSubscriptionDate());
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                serviceInstance.setSubscriptionDate(calendar.getTime());
            }
            serviceInstance.setQuantity(quantity);
            if (BooleanUtils.isTrue(serviceInstance.getAutoEndOfEngagement())) {
                serviceInstance.setEndAgreementDate(serviceInstance.getSubscribedTillDate());
            }
            serviceInstanceService.serviceInstanciation(serviceInstance, descriptionOverride);
            serviceInstances.add(serviceInstance);
            ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();
            if (!paramBean.isServiceMultiInstantiation()) {
                serviceTemplates.remove(serviceTemplate);
            }
        }

        if (!isChecked) {
            messages.warn(new BundleKey("messages", "instanciation.selectService"));
        } else {
            subscriptionService.refresh(entity);
            resetChargesDataModels();
            messages.info(new BundleKey("messages", "instanciation.instanciateSuccessful"));
        }

        keepCurrentTab();

    }

    /**
     * actives services.
     */
    @ActionMethod
    public void activateService() throws BusinessException {
        log.debug("activateService...");
        if (selectedServiceInstance != null) {
            log.debug("activateService id={} checked", selectedServiceInstance.getId());
            if (selectedServiceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
                messages.info(new BundleKey("messages", "error.activation.terminatedService"));
                return;
            } else if (selectedServiceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
                messages.info(new BundleKey("messages", "error.activation.activeService"));
                return;
            }

            // Obtain EM attached service instance entity
            entity = subscriptionService.refreshOrRetrieve(entity);
            selectedServiceInstance = entity.getServiceInstances().get(entity.getServiceInstances().indexOf(selectedServiceInstance));

            log.debug("activateService:serviceInstance.getRecurrringChargeInstances.size={}", selectedServiceInstance.getRecurringChargeInstances().size());

            try {
                serviceInstanceService.serviceActivation(selectedServiceInstance);

            } catch (Exception e) {
                messages.error(new BundleKey("messages", "activation.activateUnsuccessful"), e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
                return;

            } finally {

                entity = subscriptionService.refreshOrRetrieve(entity);

                initServiceInstances(entity.getServiceInstances());
                initServiceTemplates();
                resetChargesDataModels();
                keepCurrentTab();
            }
        } else {
            log.error("activateService id=#0 is NOT a serviceInstance");
        }
        selectedServiceInstance = null;
        messages.info(new BundleKey("messages", "activation.activateSuccessful"));

    }

    /**
     * Keeps current active Tab selected.
     */
    private void keepCurrentTab() {
        super.setActiveMainTab(super.getActiveTab());
    }

    @ActionMethod
    public void saveProductInstance() throws BusinessException {

        if (productInstance.isTransient()) {

            if (productInstance.getProductTemplate().getValidity() != null && !productInstance.getProductTemplate().getValidity().isCorrespondsToPeriod(productInstance.getApplicationDate())) {

                String datePattern = paramBeanFactory.getInstance().getDateFormat();
                messages.error(new BundleKey("messages", "productInstance.error.productTemplateInvalidVersion"), productInstance.getProductTemplate().getValidity().toString(datePattern),
                    DateUtils.formatDateWithPattern(productInstance.getApplicationDate(), datePattern));
                facesContext.validationFailed();
                return;
            }

            productInstance.setCode(productInstance.getProductTemplate().getCode());
            productInstance.setDescription(productInstance.getProductTemplate().getDescription());
            if (productInstance.getApplicationDate() == null) {
                productInstance.setApplicationDate(new Date());
            }

            entity = getPersistenceService().retrieveIfNotManaged(entity);
            productInstance.setSubscription(entity);
            productInstance.setUserAccount(entity.getUserAccount());
            productInstance.setProductTemplate(productTemplateService.retrieveIfNotManaged(productInstance.getProductTemplate()));

            try {
                // productInstanceService.create(productInstance);
                // save custom field before product application so we can use in el
                customFieldDataEntryBean.saveCustomFieldsToEntity(productInstance, true);
                productInstanceService.saveAndApplyProductInstance(productInstance, null, null, null, true);
                productChargeInstances = null;
                productInstances = null;
                productInstance = null;

                messages.info(new BundleKey("messages", "productInstance.saved.ok"));
            } catch (BusinessException e) {
                messages.error(new BundleKey("messages", "message.product.application.fail"), e.getMessage());

            } catch (Exception e) {
                log.error("unexpected exception when applying a product! {}", e.getMessage());
                messages.error(new BundleKey("messages", "message.product.application.fail"), e.getMessage());
            }

            // For update operation only custom field values can be changed
        } else {
            // save custom field before product application so we can use in el
            customFieldDataEntryBean.saveCustomFieldsToEntity(productInstance, false);

            productChargeInstances = null;
            productInstances = null;
            productInstance = null;

            messages.info(new BundleKey("messages", "productInstance.saved.ok"));
        }
    }

    @ActionMethod
    public void terminateService() throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        log.debug("selected subscriptionTerminationReason={}, terminationDate={}, selectedServiceInstanceId={}, status={}", terminationReason, terminationDate, selectedServiceInstance.getId(),
            selectedServiceInstance.getStatus());

        // Obtain EM attached service instance entity
        entity = subscriptionService.refreshOrRetrieve(entity);
        selectedServiceInstance = entity.getServiceInstances().get(entity.getServiceInstances().indexOf(selectedServiceInstance));

        serviceInstanceService.terminateService(selectedServiceInstance, terminationDate, terminationReason, entity.getOrderNumber());

        subscriptionService.refresh(entity);

        initServiceInstances(entity.getServiceInstances());
        initTerminableServices(entity.getServiceInstances());
        initServiceTemplates();
        resetChargesDataModels();

        selectedServiceInstance = null;
        terminationReason = null;
        terminationDate = null;

        messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));
    }

    @ActionMethod
    public String terminateSubscription() throws BusinessException {

        entity = subscriptionService.refreshOrRetrieve(entity);

        log.debug("selected subscriptionTerminationReason={}, terminationDate={}, subscriptionId={}, status={}", terminationReason, terminationDate, entity.getCode(), entity.getStatus());

        subscriptionService.terminateSubscription(entity, terminationDate, terminationReason, entity.getOrderNumber());

        terminationReason = null;
        terminationDate = null;
        setObjectId(entity.getId());
        messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));
        return "subscriptionDetail";
    }

    public void cancelService() {
        try {

            if (selectedServiceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
                messages.error(new BundleKey("messages", "error.termination.inactiveService"));
                return;
            }
            // serviceInstanceService.cancelService(selectedServiceInstance);

            selectedServiceInstance = null;
            messages.info(new BundleKey("messages", "cancellation.cancelSuccessful"));

        } catch (Exception e) {
            log.error("unexpected exception when canceling service!", e);
            messages.error(e.getMessage());
        }
    }

    @ActionMethod
    public void suspendService() throws BusinessException {
        // Obtain EM attached service instance entity
        entity = subscriptionService.refreshOrRetrieve(entity);
        selectedServiceInstance = entity.getServiceInstances().get(entity.getServiceInstances().indexOf(selectedServiceInstance));

        serviceInstanceService.serviceSuspension(selectedServiceInstance, new Date());

        subscriptionService.refresh(entity);

        initServiceInstances(entity.getServiceInstances());
        initServiceTemplates();
        resetChargesDataModels();

        selectedServiceInstance = null;
        messages.info(new BundleKey("messages", "suspension.suspendSuccessful"));
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public ServiceInstance getSelectedServiceInstance() {
        return selectedServiceInstance;
    }

    public void setSelectedTerminableService(ServiceInstance selectedTerminableService) {
        this.selectedTerminableService = selectedTerminableService;
    }

    public ServiceInstance getSelectedTerminableService() {
        return selectedTerminableService;
    }

    public void setSelectedServiceInstance(ServiceInstance selectedServiceInstance) {
        this.selectedServiceInstance = selectedServiceInstance;
    }

    public void populateAccounts(UserAccount userAccount) {
        userAccount.getBillingAccount().getDiscountPlanInstances().size();
        entity.setUserAccount(userAccount);
        if (userAccount != null && appProvider.isLevelDuplication()) {
            entity.setCode(userAccount.getCode());
            entity.setDescription(userAccount.getDescription());
        }
    }

    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getDate() {
        return (new Date()).toString();
    }

    public List<Access> getAccess() {
        if (entity.getId() == null) {
            return null;
        }
        return accessService.listBySubscription(entity);
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public UsageChargeInstance getUsageChargeInstance() {
        return usageChargeInstance;
    }

    public void setUsageChargeInstance(UsageChargeInstance usageChargeInstance) {
        this.usageChargeInstance = usageChargeInstance;
    }

    public void editUsageChargeIns(UsageChargeInstance chargeInstance) {
        this.usageChargeInstance = usageChargeInstanceService.refreshOrRetrieve(chargeInstance);
        log.debug("setting usageChargeIns " + chargeInstance);
    }

    @ActionMethod
    public void saveUsageChargeIns() throws BusinessException {
        log.debug("saveUsageChargeIns getObjectId={}", getObjectId());
        if (usageChargeInstance != null && usageChargeInstance.getId() != null) {
            log.debug("update usageChargeIns {}, id={}", usageChargeInstance, usageChargeInstance.getId());
            usageChargeInstanceService.update(usageChargeInstance);

            usageChargeInstance = null;
            usageChargeInstances = null;
            messages.info(new BundleKey("messages", "save.successful"));
        }
    }

    public List<WalletTemplate> findWalletTemplatesForOneShot() {

        if (prepaidWalletTemplates == null && !entity.isTransient()) {
            prepaidWalletTemplates = walletTemplateService.findBySubscription(entity);
        }

        return prepaidWalletTemplates;
    }

    public void deleteServiceInstance(ServiceInstance serviceInstance) {
        try {
            entity = subscriptionService.refreshOrRetrieve(entity);

            serviceTemplates.add(serviceInstance.getServiceTemplate());
            serviceInstanceService.remove(serviceInstance.getId());
            serviceInstances.remove(serviceInstance);
            selectedServiceInstance = null;
            subscriptionService.refresh(entity);
            resetChargesDataModels();

            messages.info(new BundleKey("messages", "delete.successful"));
        } catch (Exception e) {
            log.error("exception when delete service instance!", e);
            messages.error(e.getMessage());
        }
    }

    public WalletTemplate getSelectedWalletTemplate() {
        return selectedWalletTemplate;
    }

    public void setSelectedWalletTemplate(WalletTemplate selectedWalletTemplate) {
        this.selectedWalletTemplate = selectedWalletTemplate;
    }

    public boolean isShowApplyOneShotForm() {
        return showApplyOneShotForm;
    }

    public void setShowApplyOneShotForm(boolean showApplyOneShotForm) {
        this.showApplyOneShotForm = showApplyOneShotForm;
    }

    public String getSelectedWalletTemplateCode() {
        if (selectedWalletTemplate != null && selectedWalletTemplate.getCode() != null) {
            selectedWalletTemplateCode = selectedWalletTemplate.getCode();
        }
        return selectedWalletTemplateCode;
    }

    public void setSelectedWalletTemplateCode(String selectedWalletTemplateCode) {
        this.selectedWalletTemplateCode = selectedWalletTemplateCode;
    }

    private void initServiceInstances(List<ServiceInstance> instantiatedServices) {
        serviceInstances = new EntityListDataModelPF<ServiceInstance>(new ArrayList<ServiceInstance>());
        serviceInstances.addAll(instantiatedServices);

        log.debug("serviceInstances initialized with {} items", serviceInstances.getSize());
    }

    private void initTerminableServices(List<ServiceInstance> serviceInstances) {

        terminableServices = new EntityListDataModelPF<ServiceInstance>(new ArrayList<ServiceInstance>());
        if (serviceInstances != null && !serviceInstances.isEmpty()) {
            for (ServiceInstance serviceInstance : serviceInstances) {
                if (serviceInstanceService.willBeTerminatedInFuture(serviceInstance)) {
                    terminableServices.add(serviceInstance);
                }
            }
        }
        log.debug("terminableServices initialized with {} items", terminableServices.getSize());
    }

    private void resetChargesDataModels() {
        oneShotChargeInstances = null;
        recurringChargeInstances = null;
        usageChargeInstances = null;
        productChargeInstances = null;
        prepaidWalletTemplates = null;
    }

    public boolean filterByDate(Object value, Object filter, Locale locale) throws ParseException {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if (filterText == null || filterText.isEmpty()) {
            return true;
        }
        if (value == null) {
            return false;
        }
        Date filterDate;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String filterFormat = formatter.format((Date) value);
        Date dateFrom;
        Date dateTo;
        String fromPart = filterText.substring(0, filterText.indexOf("-"));
        String toPart = filterText.substring(filterText.indexOf("-") + 1);
        filterDate = DateUtils.parseDateWithPattern(filterFormat, "dd/MM/yyyy");
        dateFrom = fromPart.isEmpty() ? null : DateUtils.parseDateWithPattern(fromPart, "dd/MM/yyyy");
        dateTo = toPart.isEmpty() ? null : DateUtils.parseDateWithPattern(toPart, "dd/MM/yyyy");
        return (dateFrom == null || filterDate.after(dateFrom) || filterDate.equals(dateFrom)) && (dateTo == null || filterDate.before(dateTo) || filterDate.equals(dateTo));

    }

    public void resetFilters() {
        DataTable dataTable = (DataTable) facesContext.getViewRoot().findComponent("recurringWalletForm:recurringWalletOperationTable");
        if (dataTable != null) {
            dataTable.reset();
        }
    }

    public EntityListDataModelPF<ProductChargeInstance> getProductChargeInstances() {

        if (productChargeInstances != null || (entity == null || entity.getId() == null)) {
            return productChargeInstances;
        }

        productChargeInstances = new EntityListDataModelPF<ProductChargeInstance>(new ArrayList<ProductChargeInstance>());
        productChargeInstances.addAll(productChargeInstanceService.findBySubscriptionId(entity.getId()));
        return productChargeInstances;
    }

    public EntityListDataModelPF<ProductInstance> getProductInstances() {

        if (productInstances != null || (entity == null || entity.getId() == null)) {
            return productInstances;
        }

        productInstances = new EntityListDataModelPF<ProductInstance>(new ArrayList<ProductInstance>());
        productInstances.addAll(productInstanceService.findBySubscription(entity));
        return productInstances;
    }

    public void initProductInstance() {
        productInstance = new ProductInstance();
    }

    public ProductInstance getProductInstance() {
        return productInstance;
    }

    public void setProductInstance(ProductInstance productInstance) {
        this.productInstance = productInstance;
    }

    public List<ProductTemplate> getOfferProductTemplatesByDate(Date date) {
        List<ProductTemplate> result = new ArrayList<>();

        if (entity != null && entity.getOffer() != null) {
            for (OfferProductTemplate offerProductTemplate : offerTemplateService.retrieveIfNotManaged(entity.getOffer()).getOfferProductTemplates()) {
                if (offerProductTemplate.getProductTemplate().getValidity() == null || offerProductTemplate.getProductTemplate().getValidity().isCorrespondsToPeriod(date)) {
                    result.add(offerProductTemplate.getProductTemplate());
                }
            }
        }

        return result;
    }

    public BigDecimal getServiceAmountWithoutTax() {
        BigDecimal quantity = this.oneShotChargeInstance.getQuantity();

        return quantity.multiply(this.getOneShotWalletOperations().get(0).getAmountWithoutTax());
    }

    public void updateProductInstanceCode() {
        productInstance.setCode(productInstance.getProductTemplate().getCode());
        customFieldDataEntryBean.refreshFieldsAndActions(productInstance);
    }

    public void editProductInstance(ProductInstance prodInstance) {
        this.productInstance = productInstanceService.refreshOrRetrieve(prodInstance);
        customFieldDataEntryBean.refreshFieldsAndActions(this.productInstance);
    }

    public void cancelProductInstanceEdit() {
        this.productInstance = null;
    }

    /**
     * Subscription date change listener - clear offer template picklist and update subscrivedTillDate
     */
    public void onSubscriptionDateChange() {
        activeOfferTemplateDataModel = null;
        updateSubscribedTillDate();
    }

    /**
     * Update subscribedTillDate field in subscription
     */
    public void updateSubscribedTillDate() {
        SubscriptionRenewal subscriptionRenewal = entity.getSubscriptionRenewal();
        org.meveo.model.catalog.Calendar calendarInitialyActiveFor = calendarService.refreshOrRetrieve(subscriptionRenewal.getCalendarInitialyActiveFor());
        subscriptionRenewal.setCalendarInitialyActiveFor(calendarInitialyActiveFor);
        entity.setSubscriptionRenewal(subscriptionRenewal);
        subscriptionService.updateSubscribedTillAndRenewalNotifyDates(entity);
    }

    /**
     * Auto update end of engagement date.
     */
    public void autoUpdateEndOfEngagementDate() {
        entity.autoUpdateEndOfEngagementDate();
    }

    /**
     * Copy subscription renewal and other information from offer
     */
    public void copyInfoFromOffer() {
        SubscriptionRenewal subscriptionRenewal = entity.getOffer().getSubscriptionRenewal();
        entity.setSubscriptionRenewal(subscriptionRenewal);
        updateSubscribedTillDate();
        /* Subscription should not inherit min Amount from OfferTemplate #4757 */
        // entity.setMinimumAmountEl(entity.getOffer().getMinimumAmountEl());
        // entity.setMinimumLabelEl(entity.getOffer().getMinimumLabelEl());
        entity.setMinimumAmountElSpark(entity.getOffer().getMinimumAmountElSpark());
        entity.setMinimumLabelElSpark(entity.getOffer().getMinimumLabelElSpark());
    }

    public boolean isServiceInstancesEmpty() {
        if (entity.isTransient()) {
            return true;
        }
        return serviceInstances.getRowCount() == 0;
    }

    public List<Seller> listProductSellers() {
        if (productInstance != null && productInstance.getProductTemplate() != null) {
            if (productInstance.getProductTemplate().getSellers().size() > 0) {
                return productInstance.getProductTemplate().getSellers();
            } else {
                return sellerService.list();
            }
        } else {
            return new ArrayList<Seller>();
        }
    }

    public List<Seller> listSellers() {
        if (entity != null && entity.getOffer() != null) {
            OfferTemplate offer = entity.getOffer();
            offer = offerTemplateService.retrieveIfNotManaged(entity.getOffer());
            if (offer.getSellers().size() > 0) {
                return offer.getSellers();
            } else {
                return sellerService.list();
            }
        } else {
            return new ArrayList<Seller>();
        }
    }

    @ActionMethod
    public String cancelSubscriptionRenewal() throws BusinessException {
        subscriptionService.cancelSubscriptionRenewal(entity);
        PrimeFaces.current().resetInputs("subscriptionTab");
        return null;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public SubscriptionTerminationReason getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(SubscriptionTerminationReason terminationReason) {
        this.terminationReason = terminationReason;
    }

    /**
     * Compute balance due
     *
     * @return due balance
     * @throws BusinessException General business exception
     */
    public BigDecimal getBalanceDue() throws BusinessException {
        if (entity.getId() == null) {
            return new BigDecimal(0);
        } else {
            return subscriptionService.subscriptionBalanceDue(entity, new Date());
        }
    }

    /**
     * Compute balance exigible without litigation.
     *
     * @return exigible balance without litigation
     * @throws BusinessException General business exception
     */
    public BigDecimal getBalanceExigibleWithoutLitigation() throws BusinessException {
        if (entity.getId() == null) {
            return new BigDecimal(0);
        } else {
            return subscriptionService.subscriptionBalanceExigibleWithoutLitigation(entity, new Date());
        }
    }

    @ActionMethod
    public String instantiateDiscountPlan() throws BusinessException {
        if (entity.getDiscountPlan() != null) {
            DiscountPlan dp = entity.getDiscountPlan();
            entity = subscriptionService.instantiateDiscountPlan(entity, dp);
            entity.setDiscountPlan(null);
        }
        return getEditViewName();
    }

    @ActionMethod
    public String deleteDiscountPlanInstance(DiscountPlanInstance dpi) throws BusinessException {
        subscriptionService.terminateDiscountPlan(entity, dpi);
        return getEditViewName();
    }

    public List<DiscountPlan> getAllowedDiscountPlans() {
        if (entity.getOffer() != null) {
            List<DiscountPlan> allowedDiscountPlans = entity.getOffer().getAllowedDiscountPlans();
            if (entity.getUserAccount() != null) {
                BillingAccount billingAccount = billingAccountService.retrieveIfNotManaged(entity.getUserAccount().getBillingAccount());
                billingAccount.getDiscountPlanInstances().forEach(dpi -> allowedDiscountPlans.remove(dpi.getDiscountPlan()));
            }
            return allowedDiscountPlans;
        }
        return Collections.emptyList();
    }

    /**
     * Check is terminated subscription
     *
     * @return true is the subscription is terminated
     */
    public boolean isTerminatedSubscription() {
        return subscriptionService.willBeTerminatedInFuture(entity);
    }

    /**
     * cancel subscription termination.
     */
    @ActionMethod
    public void cancelSubscriptionTermination() throws BusinessException {
        log.debug("cancelTermination...");
        entity = subscriptionService.refreshOrRetrieve(entity);
        subscriptionService.cancelSubscriptionTermination(entity);
        subscriptionService.refresh(entity);
        messages.info(new BundleKey("messages", "termination.cancelTerminationSuccessful"));
    }

    /**
     * cancel termination.
     */
    @ActionMethod
    public void cancelServiceTermination() throws BusinessException {
        log.debug("cancelTermination...");
        if (selectedTerminableService != null) {
            log.debug("service id={} checked", selectedTerminableService.getId());

            if (!serviceInstanceService.willBeTerminatedInFuture(selectedTerminableService)) {
                messages.info(new BundleKey("messages", "error.cancelTerminationService"));
                return;
            }

            // Obtain EM attached service instance entity
            entity = subscriptionService.refreshOrRetrieve(entity);
            selectedTerminableService = entity.getServiceInstances().get(entity.getServiceInstances().indexOf(selectedTerminableService));

            serviceInstanceService.cancelServiceTermination(selectedTerminableService);
            subscriptionService.refresh(entity);

            initTerminableServices(entity.getServiceInstances());
            keepCurrentTab();

        } else {
            log.error("cancelTermination id=#0 is NOT a serviceInstance");
        }
        selectedTerminableService = null;
        messages.info(new BundleKey("messages", "termination.cancelTerminationSuccessful"));
    }

    public void refreshUAInformation() {

        // Overcome lazy loading issue when later instantiating discount plans
        UserAccount ua = userAccountService.retrieveIfNotManaged(entity.getUserAccount());
        ua.getBillingAccount().getDiscountPlanInstances().size();
    }

    public LazyDataModel<OfferTemplate> getActiveOfferTemplateDataModel() {

        if (activeOfferTemplateDataModel == null) {
            HashMap<String, Object> filters = new HashMap<String, Object>();
            filters.put("lifeCycleStatus", LifeCycleStatusEnum.ACTIVE);
            if (entity != null && entity.getSubscriptionDate() != null) {
                filters.put("minmaxOptionalRange validity.from validity.to", entity.getSubscriptionDate());
            }
            activeOfferTemplateDataModel = offerTemplateBean.getLazyDataModel(filters, true);
        }
        return activeOfferTemplateDataModel;
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
    /**
     * Get configured payment method's list
     * @return list of payment methods
     */
    public List<PaymentMethod> listPaymentMethod() {
        if (Objects.nonNull(entity) && Objects.nonNull(entity.getUserAccount()) ) {
            UserAccount userAccount = userAccountService.findById(entity.getUserAccount().getId());
            List<PaymentMethod> paymentMethods = userAccount.getBillingAccount().getCustomerAccount().getPaymentMethods();
            return paymentMethods.stream()
                    .map(paymentMethod -> paymentMethod instanceof HibernateProxy ? (PaymentMethod)((HibernateProxy) paymentMethod).getHibernateLazyInitializer().getImplementation() : paymentMethod)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}