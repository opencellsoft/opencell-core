/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.billing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.EntityListDataModelPF;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.ServiceTemplateService;

/**
 * Standard backing bean for {@link Subscription} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 */
@Named
@ViewScoped
public class SubscriptionBean extends BaseBean<Subscription> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected
     * 
     * @{link Subscription} service. Extends {@link PersistenceService}
     */
    @Inject
    private SubscriptionService subscriptionService;

    /**
     * UserAccount service. TODO (needed?)
     */
    @Inject
    private UserAccountService userAccountService;

    /** set only in termination action* */
    // TODO: JavaEE6 migration. @Out(required = false)
    private ServiceInstance selectedServiceInstance = new ServiceInstance();

    /** Entity to edit. */
    private Integer quantity = 1;

    /** Entity to edit. */
    private Long selectedServiceInstanceId;

    /** Entity to edit. */
    private OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance();

    private RecurringChargeInstance recurringChargeInstance = new RecurringChargeInstance();

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    private Integer oneShotChargeInstanceQuantity = 1;

    private Integer recurringChargeServiceInstanceQuantity = 1;

    /**
     * User Account Id passed as a parameter. Used when creating new subscription entry from user account definition window, so default uset Account will be set on newly created
     * subscription entry.
     */
    private Long userAccountId;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */

    private EntityListDataModelPF<ServiceTemplate> serviceTemplates = new EntityListDataModelPF<ServiceTemplate>(new ArrayList<ServiceTemplate>());

    // TODO: JavaEE6 migration. @Out(required = false)
    private List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();

    public SubscriptionBean() {
        super(Subscription.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationExceptionC
     */
    public Subscription initEntity() {
        super.initEntity();
        if (userAccountId != null) {
            UserAccount userAccount = userAccountService.findById(getUserAccountId());
            populateAccounts(userAccount);
        }
        if (entity.getId() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(entity.getSubscriptionDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            entity.setSubscriptionDate(calendar.getTime());
        } else {
            log.info("entity.getOffer()=" + entity.getOffer().getCode());
            if (entity.getOffer() != null) {
                List<ServiceInstance> serviceInstances = entity.getServiceInstances();
                for (ServiceTemplate serviceTemplate : entity.getOffer().getServiceTemplates()) {
                    boolean alreadyInstanciated = false;
                    for (ServiceInstance serviceInstance : serviceInstances) {
                        if (serviceTemplate.getCode().equals(serviceInstance.getCode())) {
                            alreadyInstanciated = true;
                            break;
                        }
                    }
                    if (!alreadyInstanciated) {
                        serviceTemplates.add(serviceTemplate);
                    }

                }
            }
            serviceInstances.clear();
            serviceInstances.addAll(entity.getServiceInstances());

        }

        log.info("serviceInstances=" + serviceInstances.size());
        log.info("servicetemplates=" + serviceTemplates.getSize());
        return entity;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    public String saveOrUpdate(boolean killConversation) {
        if (entity.getDefaultLevel() != null && entity.getDefaultLevel()) {
            UserAccount userAccount = entity.getUserAccount();
            if (subscriptionService.isDuplicationExist(entity)) {
                entity.setDefaultLevel(false);
                messages.error(new BundleKey("messages", "error.account.duplicateDefautlLevel"));
                return null;
            }

        }

        super.saveOrUpdate(killConversation);
        return "/pages/billing/subscriptions/subscriptionDetail?edit=false&subscriptionId=" + entity.getId() + "&faces-redirect=true&includeViewParams=true";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String saveOrUpdate(Subscription entity) {
        if (entity.isTransient()) {
            serviceInstances.clear();
            subscriptionService.create(entity);
            serviceTemplates.addAll(entity.getOffer().getServiceTemplates());
            messages.info(new BundleKey("messages", "save.successful"));
        } else {
            subscriptionService.update(entity);
            messages.info(new BundleKey("messages", "update.successful"));
        }

        return back();
    }

    public void newOneShotChargeInstance() {
        log.info("newOneShotChargeInstance ");
        this.oneShotChargeInstance = new OneShotChargeInstance();
    }

    public void editOneShotChargeIns(OneShotChargeInstance oneShotChargeIns) {
        this.oneShotChargeInstance = oneShotChargeIns;
    }

    public void saveOneShotChargeIns() {
        log.info("saveOneShotChargeIns getObjectId=" + getObjectId());

        try {
            if (oneShotChargeInstance != null && oneShotChargeInstance.getId() != null) {
                oneShotChargeInstanceService.update(oneShotChargeInstance);
            } else {
                if (oneShotChargeInstance.getChargeDate() == null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    oneShotChargeInstance.setChargeDate(calendar.getTime());
                }

                oneShotChargeInstance.setSubscription(entity);
                Long id = oneShotChargeInstanceService.oneShotChargeApplication(entity, (OneShotChargeTemplate) oneShotChargeInstance.getChargeTemplate(),
                    oneShotChargeInstance.getChargeDate(), oneShotChargeInstance.getAmountWithoutTax(), oneShotChargeInstance.getAmountWithTax(), oneShotChargeInstanceQuantity,
                    oneShotChargeInstance.getCriteria1(), oneShotChargeInstance.getCriteria2(), oneShotChargeInstance.getCriteria3(), getCurrentUser());
                oneShotChargeInstance.setId(id);
                oneShotChargeInstance.setProvider(oneShotChargeInstance.getChargeTemplate().getProvider());
                entity.getOneShotChargeInstances().add(oneShotChargeInstance);
            }
            messages.info(new BundleKey("messages", "save.successful"));
            oneShotChargeInstance = new OneShotChargeInstance();
            clearObjectId();
        } catch (Exception e) {
            log.error("exception when applying one shot charge!", e);
            messages.error(e.getMessage());
        }
    }

    public void newRecurringChargeInstance() {
        this.recurringChargeInstance = new RecurringChargeInstance();
    }

    public void editRecurringChargeIns(RecurringChargeInstance recurringChargeIns) {
        this.recurringChargeInstance = recurringChargeIns;
        recurringChargeServiceInstanceQuantity = recurringChargeIns.getServiceInstance().getQuantity();
    }

    public void saveRecurringChargeIns() {
        log.info("saveRecurringChargeIns getObjectId=#0", getObjectId());
        try {
            if (recurringChargeInstance != null) {
                if (recurringChargeInstance.getId() != null) {
                    log.info("update RecurringChargeIns #0, id:#1", recurringChargeInstance, recurringChargeInstance.getId());
                    recurringChargeInstance.getServiceInstance().setQuantity(recurringChargeServiceInstanceQuantity);
                    recurringChargeInstanceService.update(recurringChargeInstance);
                } else {
                    log.info("save RecurringChargeIns #0", recurringChargeInstance);

                    recurringChargeInstance.setSubscription(entity);
                    Long id = recurringChargeInstanceService.recurringChargeApplication(entity, (RecurringChargeTemplate) recurringChargeInstance.getChargeTemplate(),
                        recurringChargeInstance.getChargeDate(), recurringChargeInstance.getAmountWithoutTax(), recurringChargeInstance.getAmountWithTax(), 1,
                        recurringChargeInstance.getCriteria1(), recurringChargeInstance.getCriteria2(), recurringChargeInstance.getCriteria3(), getCurrentUser());
                    recurringChargeInstance.setId(id);
                    recurringChargeInstance.setProvider(recurringChargeInstance.getChargeTemplate().getProvider());
                    entity.getRecurringChargeInstances().add(recurringChargeInstance);
                }
                messages.info(new BundleKey("messages", "save.successful"));
                recurringChargeInstance = new RecurringChargeInstance();
                clearObjectId();
            }
        } catch (BusinessException e1) {
            messages.error(e1.getMessage());
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

    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public EntityListDataModelPF<ServiceTemplate> getServiceTemplates() {
        return serviceTemplates;
    }

    public OneShotChargeInstance getOneShotChargeInstance() {
        return oneShotChargeInstance;
    }

    public RecurringChargeInstance getRecurringChargeInstance() {
        return recurringChargeInstance;
    }

    public List<OneShotChargeInstance> getOneShotChargeInstances() {
        return (entity == null || entity.getId() == null) ? null : oneShotChargeInstanceService.findOneShotChargeInstancesBySubscriptionId(entity.getId());
    }

    public List<WalletOperation> getOneShotWalletOperations() {
        log.info("getOneShotWalletOperations");
        if (this.oneShotChargeInstance == null || this.oneShotChargeInstance.getId() == null) {
            return null;
        }
        List<WalletOperation> results = new ArrayList<WalletOperation>(oneShotChargeInstance.getWalletOperations());

        Collections.sort(results, new Comparator<WalletOperation>() {
            public int compare(WalletOperation c0, WalletOperation c1) {

                return c1.getOperationDate().compareTo(c0.getOperationDate());
            }
        });
        log.info("retrieve #0 WalletOperations", results != null ? results.size() : 0);
        return results;
    }

    public List<WalletOperation> getRecurringWalletOperations() {
        log.info("getRecurringWalletOperations");
        if (this.recurringChargeInstance == null || this.recurringChargeInstance.getId() == null) {
            return null;
        }
        List<WalletOperation> results = new ArrayList<WalletOperation>(recurringChargeInstance.getWalletOperations());
        Collections.sort(results, new Comparator<WalletOperation>() {
            public int compare(WalletOperation c0, WalletOperation c1) {

                return c1.getOperationDate().compareTo(c0.getOperationDate());
            }
        });
        log.info("retrieve #0 WalletOperations", results != null ? results.size() : 0);
        return results;
    }

    // @Factory("recurringChargeInstances")
    public List<RecurringChargeInstance> getRecurringChargeInstances() {
        return (entity == null || entity.getId() == null) ? null : recurringChargeInstanceService.findRecurringChargeInstanceBySubscriptionId(entity.getId());
    }

    public void instanciateManyServices() {
        log.info("instanciateManyServices");
        try {
            if (quantity <= 0) {
                log.warn("instanciateManyServices quantity is negative! set it to 1");
                quantity = 1;
            }
            boolean isChecked = false;
            System.out.println("AKK serviceTemplates is " + serviceTemplates.getSize());

            System.out.println("AKK serviceTemplates is " + serviceTemplates.getSelectedItemsAsList());

            for (ServiceTemplate serviceTemplate : serviceTemplates.getSelectedItemsAsList()) {
                isChecked = true;
                log.debug("instanciateManyServices id=#0 checked, quantity=#1", serviceTemplate.getId(), quantity);
                ServiceInstance serviceInstance = new ServiceInstance();
                serviceInstance.setProvider(serviceTemplate.getProvider());
                serviceInstance.setCode(serviceTemplate.getCode());
                serviceInstance.setDescription(serviceTemplate.getDescription());
                serviceInstance.setServiceTemplate(serviceTemplate);
                serviceInstance.setSubscription((Subscription) entity);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);

                serviceInstance.setSubscriptionDate(calendar.getTime());
                serviceInstance.setQuantity(quantity);
                serviceInstanceService.serviceInstanciation(serviceInstance, getCurrentUser());
                serviceInstances.add(serviceInstance);
                serviceTemplates.remove(serviceTemplate);
            }
            if (!isChecked) {
                messages.warn(new BundleKey("messages", "instanciation.selectService"));
            } else {
                messages.info(new BundleKey("messages", "instanciation.instanciateSuccessful"));
            }
        } catch (BusinessException e1) {
            messages.error(e1.getMessage());
        } catch (Exception e) {
            log.error("error in SubscriptionBean.instanciateManyServices", e);
            messages.error(e.getMessage());
        }
    }

    public void activateService() {
        log.info("activateService...");
        try {
            log.debug("activateService id=#0 checked", selectedServiceInstanceId);
            ServiceInstance serviceInstance = serviceInstanceService.findById(selectedServiceInstanceId);
            if (serviceInstance != null) {
                log.debug("activateService:serviceInstance.getRecurrringChargeInstances.size=#0", serviceInstance.getRecurringChargeInstances().size());

                if (serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
                    messages.info(new BundleKey("messages", "error.activation.terminatedService"));
                    return;
                }
                if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
                    messages.info(new BundleKey("messages", "error.activation.activeService"));
                    return;
                }

                serviceInstanceService.serviceActivation(serviceInstance, null, null, getCurrentUser());
            } else {
                log.error("activateService id=#0 is NOT a serviceInstance");
            }

            messages.info(new BundleKey("messages", "activation.activateSuccessful"));
        } catch (BusinessException e1) {
            messages.error(e1.getMessage());
        } catch (Exception e) {
            log.error("unexpected exception when deleting!", e);
            messages.error(e.getMessage());
        }
    }

    public void terminateService() {
        try {
            Date terminationDate = selectedServiceInstance.getTerminationDate();

            SubscriptionTerminationReason newSubscriptionTerminationReason = selectedServiceInstance.getSubscriptionTerminationReason();
            log.info("selected subscriptionTerminationReason=#0,terminationDate=#1,selectedServiceInstanceId=#2,status=#3",
                newSubscriptionTerminationReason != null ? newSubscriptionTerminationReason.getId() : null, terminationDate, selectedServiceInstanceId,
                selectedServiceInstance.getStatus());

            if (selectedServiceInstance.getStatus() != InstanceStatusEnum.TERMINATED) {
                serviceInstanceService.terminateService(selectedServiceInstance, terminationDate, newSubscriptionTerminationReason, getCurrentUser());
            } else {
                serviceInstanceService.updateTerminationMode(selectedServiceInstance, terminationDate, getCurrentUser());
            }

            messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));
        } catch (BusinessException e1) {
            messages.error(e1.getMessage());
        } catch (Exception e) {
            log.error("unexpected exception when deleting!", e);
            messages.error(e.getMessage());
        }
    }

    public void cancelService() {
        try {
            ServiceInstance serviceInstance = serviceInstanceService.findById(selectedServiceInstanceId);

            if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
                messages.error(new BundleKey("messages", "error.termination.inactiveService"));
                return;
            }
            // serviceInstanceService.cancelService(serviceInstance, getCurrentUser());

            messages.info(new BundleKey("messages", "cancellation.cancelSuccessful"));
        } catch (Exception e) {
            log.error("unexpected exception when deleting!", e);
            messages.error(e.getMessage());
        }
    }

    public void suspendService() {
        try {
            ServiceInstance serviceInstance = serviceInstanceService.findById(selectedServiceInstanceId);
            serviceInstanceService.serviceSuspension(serviceInstance, new Date(), getCurrentUser());

            messages.info(new BundleKey("messages", "suspension.suspendSuccessful"));
        } catch (BusinessException e1) {
            messages.error(e1.getMessage());
        } catch (Exception e) {
            log.error("unexpected exception when deleting!", e);
            messages.error(e.getMessage());
        }
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getOneShotChargeInstanceQuantity() {
        return oneShotChargeInstanceQuantity;
    }

    public void setOneShotChargeInstanceQuantity(Integer oneShotChargeInstanceQuantity) {
        this.oneShotChargeInstanceQuantity = oneShotChargeInstanceQuantity;
    }

    public Long getSelectedServiceInstanceId() {
        return selectedServiceInstanceId;
    }

    public void setSelectedServiceInstanceId(Long selectedServiceInstanceId) {
        this.selectedServiceInstanceId = selectedServiceInstanceId;
        if (selectedServiceInstanceId != null) {
            selectedServiceInstance = serviceInstanceService.findById(selectedServiceInstanceId);
        }

    }

    public ServiceInstance getSelectedServiceInstance() {
        return selectedServiceInstance;
    }

    public void setSelectedServiceInstance(ServiceInstance selectedServiceInstance) {
        this.selectedServiceInstance = selectedServiceInstance;
    }

    public void populateAccounts(UserAccount userAccount) {
        entity.setUserAccount(userAccount);
        if (subscriptionService.isDuplicationExist(entity)) {
            entity.setDefaultLevel(false);
        } else {
            entity.setDefaultLevel(true);
        }
        if (userAccount != null && userAccount.getProvider() != null && userAccount.getProvider().isLevelDuplication()) {
            entity.setCode(userAccount.getCode());
            entity.setDescription(userAccount.getDescription());
        }
    }

    public Integer getRecurringChargeServiceInstanceQuantity() {
        return recurringChargeServiceInstanceQuantity;
    }

    public void setRecurringChargeServiceInstanceQuantity(Integer recurringChargeServiceInstanceQuantity) {
        this.recurringChargeServiceInstanceQuantity = recurringChargeServiceInstanceQuantity;
    }

    private Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }
}