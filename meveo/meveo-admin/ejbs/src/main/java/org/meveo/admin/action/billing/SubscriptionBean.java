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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeApplication;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.local.OneShotChargeInstanceServiceLocal;
import org.meveo.service.billing.local.RecurringChargeInstanceServiceLocal;
import org.meveo.service.billing.local.ServiceInstanceServiceLocal;
import org.meveo.service.billing.local.SubscriptionServiceLocal;
import org.meveo.service.billing.local.UserAccountServiceLocal;
import org.meveo.service.catalog.local.ServiceTemplateServiceLocal;

/**
 * Standard backing bean for {@link Subscription} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 */
@Name("subscriptionBean")
@Scope(ScopeType.CONVERSATION)
public class SubscriptionBean extends BaseBean<Subscription> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected
     * 
     * @{link Subscription} service. Extends {@link PersistenceService}
     */
    @In
    private SubscriptionServiceLocal subscriptionService;

    /**
     * UserAccount service. TODO (needed?)
     */
    @In
    private UserAccountServiceLocal userAccountService;

    @In(required = false)
    private User currentUser;

    /** set only in termination action* */
    @Out(required = false)
    private ServiceInstance selectedServiceInstance = new ServiceInstance();

    /** Entity to edit. */
    @Out(required = false)
    private Integer quantity = 1;

    /** Entity to edit. */
    @Out(required = false)
    private Long selectedServiceInstanceId;

    /** Entity to edit. */
    @Out(required = false)
    private OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance();

    @Out(required = false)
    private RecurringChargeInstance recurringChargeInstance = new RecurringChargeInstance();

    @In
    private ServiceInstanceServiceLocal serviceInstanceService;

    @In
    private OneShotChargeInstanceServiceLocal oneShotChargeInstanceService;

    @In
    private RecurringChargeInstanceServiceLocal recurringChargeInstanceService;

    @In
    private ServiceTemplateServiceLocal serviceTemplateService;

    private Integer oneShotChargeInstanceQuantity = 1;
    
    private Integer recurringChargeServiceInstanceQuantity = 1;
    

    
    /**
     * User Account Id passed as a parameter. Used when creating new
     * subscription entry from user account definition window, so default uset
     * Account will be set on newly created subscription entry.
     */
    @RequestParameter
    private Long userAccountId;

    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */

	@Out(required = false)
	private List<ServiceTemplate> servicetemplates = new ArrayList<ServiceTemplate>();
	
	@Out(required = false)
	private List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();


    public SubscriptionBean() {
        super(Subscription.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity
     * from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationExceptionC
     */
    @Begin(nested = true)
    @Factory("subscription")
    public Subscription init() {
         initEntity();
        if (userAccountId != null) {
        	UserAccount userAccount=userAccountService.findById(userAccountId);
            populateAccounts(userAccount);
        }
        if (entity.getId() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(entity.getSubscriptionDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            entity.setSubscriptionDate(calendar.getTime());
        }else{
			servicetemplates.clear();
			log.info("entity.getOffer()="+entity.getOffer().getCode());
			if (entity.getOffer() != null) {
				List<ServiceInstance> serviceInstances = entity.getServiceInstances();
				for (ServiceTemplate serviceTemplate : entity.getOffer()
						.getServiceTemplates()) {
					boolean alreadyInstanciated = false;
					for (ServiceInstance serviceInstance : serviceInstances) {
						if (serviceTemplate.getCode().equals(serviceInstance.getCode())) {
							alreadyInstanciated = true;
							break;
						}
					}
					if (!alreadyInstanciated) {
						servicetemplates.add(serviceTemplate);
					}

				}
			}
			serviceInstances.clear();
			serviceInstances.addAll(entity.getServiceInstances());
		
		}

		log.info("serviceInstances="+serviceInstances.size());
		log.info("servicetemplates="+servicetemplates.size());
        return entity;    
        
    }

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "subscriptions", required = false)
    protected PaginationDataModel<Subscription> getDataModel() {
        return entities;
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Begin(join = true)
    @Factory("subscriptions")
    public void list() {
        super.list();
    }

    /**
     * Conversation is ended and user is redirected from edit to his previous
     * window.
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
     */
    @End(beforeRedirect = true, root=false)
    public String saveOrUpdate() {
    	
    	if (entity.getDefaultLevel()!=null && entity.getDefaultLevel()) {
            UserAccount userAccount = entity.getUserAccount();
            if(subscriptionService.isDuplicationExist(entity)){
                entity.setDefaultLevel(false);
                    statusMessages.addFromResourceBundle(Severity.ERROR, "error.account.duplicateDefautlLevel");
                    return null;
             }
            
        }
    	
        saveOrUpdate(entity);
        Redirect.instance().setParameter("edit", "false");
        Redirect.instance().setParameter("objectId", entity.getId());
        Redirect.instance().setViewId("/pages/billing/subscriptions/subscriptionDetail");
        Redirect.instance().execute();
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public String saveOrUpdate(Subscription entity) {
        if (entity.isTransient()) {
        	serviceInstances.clear();
            subscriptionService.create(entity);
            servicetemplates.addAll(entity.getOffer().getServiceTemplates());
            statusMessages.addFromResourceBundle(Severity.INFO, "save.successful");
        } else {
        	subscriptionService.update(entity);
            statusMessages.addFromResourceBundle(Severity.INFO, "update.successful");
        }

        return back();
    }

    public void newOneShotChargeInstance() {
        this.oneShotChargeInstance = new OneShotChargeInstance();
    }

    public void editOneShotChargeIns(OneShotChargeInstance oneShotChargeIns) {
        this.oneShotChargeInstance = oneShotChargeIns;
    }

    public void saveOneShotChargeIns() {
        log.info("saveOneShotChargeIns getObjectId=#0", getObjectId());

        try {
            if (oneShotChargeInstance != null && oneShotChargeInstance.getId() != null) {
                oneShotChargeInstanceService.update(oneShotChargeInstance);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                oneShotChargeInstance.setChargeDate(calendar.getTime());
               
                oneShotChargeInstance.setSubscription(entity);
                Long id = oneShotChargeInstanceService.oneShotChargeApplication(entity,
                    (OneShotChargeTemplate) oneShotChargeInstance.getChargeTemplate(), oneShotChargeInstance
                            .getChargeDate() == null ? new Date() : oneShotChargeInstance.getChargeDate(),
                    oneShotChargeInstance.getAmountWithoutTax(), oneShotChargeInstance.getAmount2(),
                    oneShotChargeInstanceQuantity, oneShotChargeInstance.getCriteria1(), oneShotChargeInstance
                            .getCriteria2(), oneShotChargeInstance.getCriteria3(), currentUser);
                oneShotChargeInstance.setId(id);
                oneShotChargeInstance.setProvider(oneShotChargeInstance.getChargeTemplate().getProvider());
                entity.getOneShotChargeInstances().add(oneShotChargeInstance);
            }
            statusMessages.addFromResourceBundle("save.successful");
            oneShotChargeInstance = new OneShotChargeInstance();
            setObjectId(null);
        } catch (Exception e) {
            log.error("exception when applying one shot charge!", e);
            statusMessages.addFromResourceBundle(Severity.ERROR, e.getMessage());
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
                    log.info("update RecurringChargeIns #0, id:#1", recurringChargeInstance, recurringChargeInstance
                            .getId());
                    recurringChargeInstance.getServiceInstance().setQuantity(recurringChargeServiceInstanceQuantity);
                    recurringChargeInstanceService.update(recurringChargeInstance);
                } else {
                    log.info("save RecurringChargeIns #0", recurringChargeInstance);

                    recurringChargeInstance.setSubscription(entity);
                    Long id = recurringChargeInstanceService
                            .recurringChargeApplication(entity, (RecurringChargeTemplate) recurringChargeInstance
                                    .getChargeTemplate(), recurringChargeInstance.getChargeDate(),
                                    recurringChargeInstance.getAmountWithoutTax(),
                                    recurringChargeInstance.getAmount2(), 1, recurringChargeInstance.getCriteria1(),
                                    recurringChargeInstance.getCriteria2(), recurringChargeInstance.getCriteria3(),
                                    currentUser);
                    recurringChargeInstance.setId(id);
                    recurringChargeInstance.setProvider(recurringChargeInstance.getChargeTemplate().getProvider());
                    entity.getRecurringChargeInstances().add(recurringChargeInstance);
                }
                statusMessages.addFromResourceBundle("save.successful");
                recurringChargeInstance = new RecurringChargeInstance();
                setObjectId(null);
            }
        } catch (BusinessException e1) {
            statusMessages.addFromResourceBundle(Severity.ERROR, e1.getMessage());
        } catch (Exception e) {
            log.error("exception when applying recurring charge!", e);
            statusMessages.addFromResourceBundle(Severity.ERROR, e.getMessage());
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

		log.info("serviceInstances2="+serviceInstances.size());
		return serviceInstances;
	}
	
	public List<ServiceTemplate> getServiceTemplates() {

		log.info("servicetemplates2="+servicetemplates.size());
		return servicetemplates;
	}

    // @Factory("oneShotChargeInstances")
    public List<OneShotChargeInstance> getOneShotChargeInstances() {
        return (entity == null || entity.getId() == null) ? null : oneShotChargeInstanceService
                .findOneShotChargeInstancesBySubscriptionId(entity.getId());
    }

    public List<ChargeApplication> getOneShotChargeApplications() {
        log.info("run oneShotChargeApplications");
        if (this.oneShotChargeInstance == null || this.oneShotChargeInstance.getId() == null) {
            return null;
        }
        List<ChargeApplication> results = new ArrayList<ChargeApplication>(oneShotChargeInstance
                .getChargeApplications());

        Collections.sort(results, new Comparator<ChargeApplication>() {
            public int compare(ChargeApplication c0, ChargeApplication c1) {

                return c1.getApplicationDate().compareTo(c0.getApplicationDate());
            }
        });
        log.info("retrieve #0 chargeApplications", results != null ? results.size() : 0);
        return results;
    }

    public List<ChargeApplication> getRecurringChargeApplications() {
        log.info("run recurringChargeApplications");
        if (this.recurringChargeInstance == null || this.recurringChargeInstance.getId() == null) {
            return null;
        }
        List<ChargeApplication> results = new ArrayList<ChargeApplication>(recurringChargeInstance
                .getChargeApplications());
        Collections.sort(results, new Comparator<ChargeApplication>() {
            public int compare(ChargeApplication c0, ChargeApplication c1) {

                return c1.getApplicationDate().compareTo(c0.getApplicationDate());
            }
        });
        log.info("retrieve #0 chargeApplications", results != null ? results.size() : 0);
        return results;
    }

    // @Factory("recurringChargeInstances")
    public List<RecurringChargeInstance> getRecurringChargeInstances() {
        return (entity == null || entity.getId() == null) ? null : recurringChargeInstanceService
                .findRecurringChargeInstanceBySubscriptionId(entity.getId());
    }



    public void instanciateManyServices() {
        log.info("instanciateManyServices");
        try {
            if (quantity <= 0) {
                log.warn("instanciateManyServices quantity is negative! set it to 1");
                quantity = 1;
            }
            boolean isChecked = false;
            for (Long id : checked.keySet()) {
                log.debug("instanciateManyServices id=#0", id);
                if (checked.get(id)) {
                    isChecked = true;
                    log.debug("instanciateManyServices id=#0 checked, quantity=#1", id, quantity);
                    ServiceTemplate serviceTemplate = serviceTemplateService.findById(id);
                    ServiceInstance serviceInstance = new ServiceInstance();
                    serviceInstance.setProvider(serviceTemplate.getProvider());
                    serviceInstance.setCode(serviceTemplate.getCode());
                    serviceInstance.setDescription(serviceTemplate.getDescription());
                    serviceInstance.setServiceTemplate(serviceTemplate);
                    serviceInstance.setSubscription(entity);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    
                    serviceInstance.setSubscriptionDate(calendar.getTime());                    
                    serviceInstance.setQuantity(quantity);
                    serviceInstanceService.serviceInstanciation(serviceInstance, currentUser);
                    serviceInstances.add(serviceInstance);
					servicetemplates.remove(serviceTemplate);
                }
            }
            if (!isChecked) {
                statusMessages.addFromResourceBundle(Severity.WARN, "instanciation.selectService");
            } else
                statusMessages.addFromResourceBundle("instanciation.instanciateSuccessful");
        } catch (BusinessException e1) {
            statusMessages.addFromResourceBundle(Severity.ERROR, e1.getMessage());
        } catch (Exception e) {
            log.error("error in SubscriptionBean.instanciateManyServices", e);
            statusMessages.addFromResourceBundle(Severity.ERROR, e.getMessage());
        }
        checked.clear();
    }

    public void activateService() {
        log.info("activateService...");
        try {
            log.debug("activateService id=#0 checked", selectedServiceInstanceId);
            ServiceInstance serviceInstance = serviceInstanceService.findById(selectedServiceInstanceId);
            if (serviceInstance != null) {
                log.debug("activateService:serviceInstance.getRecurrringChargeInstances.size=#0", serviceInstance
                        .getRecurringChargeInstances().size());

                if (serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
                    statusMessages.addFromResourceBundle(Severity.ERROR, "error.activation.terminatedService");
                    return;
                }
                if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
                    statusMessages.addFromResourceBundle(Severity.ERROR, "error.activation.activeService");
                    return;
                }

                serviceInstanceService.serviceActivation(serviceInstance, null, null, currentUser);
            } else {
                log.error("activateService id=#0 is NOT a serviceInstance");
            }

            statusMessages.addFromResourceBundle("activation.activateSuccessful");
        } catch (BusinessException e1) {
            statusMessages.addFromResourceBundle(Severity.ERROR, e1.getMessage());
        } catch (Exception e) {
            log.error("unexpected exception when deleting!", e);
            statusMessages.addFromResourceBundle(Severity.ERROR, e.getMessage());
        }
    }

    public void terminateService() {
        try {
            Date terminationDate = selectedServiceInstance.getTerminationDate();

            SubscriptionTerminationReason newSubscriptionTerminationReason = selectedServiceInstance
                    .getSubscriptionTerminationReason();
            log
                    .info(
                            "selected subscriptionTerminationReason=#0,terminationDate=#1,selectedServiceInstanceId=#2,status=#3",
                            newSubscriptionTerminationReason != null ? newSubscriptionTerminationReason.getId() : null,
                            terminationDate, selectedServiceInstanceId, selectedServiceInstance.getStatus());

            if (selectedServiceInstance.getStatus() != InstanceStatusEnum.TERMINATED) {
                serviceInstanceService.terminateService(selectedServiceInstance, terminationDate,
                        newSubscriptionTerminationReason, currentUser);
            } else {
                serviceInstanceService.updateTerminationMode(selectedServiceInstance, terminationDate, currentUser);
            }

            statusMessages.addFromResourceBundle("resiliation.resiliateSuccessful");
        } catch (BusinessException e1) {
            statusMessages.addFromResourceBundle(Severity.ERROR, e1.getMessage());
        } catch (Exception e) {
            log.error("unexpected exception when deleting!", e);
            statusMessages.addFromResourceBundle(Severity.ERROR, e.getMessage());
        }
    }

    public void cancelService() {
        try {
            ServiceInstance serviceInstance = serviceInstanceService.findById(selectedServiceInstanceId);

            if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
                statusMessages.addFromResourceBundle(Severity.ERROR, "error.termination.inactiveService");
                return;
            }
            serviceInstanceService.cancelService(serviceInstance, currentUser);

            statusMessages.addFromResourceBundle("cancellation.cancelSuccessful");
        } catch (Exception e) {
            log.error("unexpected exception when deleting!", e);
            statusMessages.addFromResourceBundle(Severity.ERROR, e.getMessage());
        }
    }

    public void suspendService() {
        try {
            ServiceInstance serviceInstance = serviceInstanceService.findById(selectedServiceInstanceId);
            serviceInstanceService.serviceSusupension(serviceInstance, new Date(), currentUser);

            statusMessages.addFromResourceBundle("suspension.suspendSuccessful");
        } catch (BusinessException e1) {
            statusMessages.addFromResourceBundle(Severity.ERROR, e1.getMessage());
        } catch (Exception e) {
            log.error("unexpected exception when deleting!", e);
            statusMessages.addFromResourceBundle(Severity.ERROR, e.getMessage());
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

	public void populateAccounts(UserAccount userAccount){
	        entity.setUserAccount(userAccount);
			if(subscriptionService.isDuplicationExist(entity)){
			    entity.setDefaultLevel(false);
			}else{
			    entity.setDefaultLevel(true);
			}
		  if(userAccount!=null && userAccount.getProvider()!=null && userAccount.getProvider().isLevelDuplication()){
		      entity.setCode(userAccount.getCode());
		      entity.setDescription(userAccount.getDescription());
	     }
	}
	
    public Integer getRecurringChargeServiceInstanceQuantity() {
        return recurringChargeServiceInstanceQuantity;
    }

    public void setRecurringChargeServiceInstanceQuantity(
            Integer recurringChargeServiceInstanceQuantity) {
        this.recurringChargeServiceInstanceQuantity = recurringChargeServiceInstanceQuantity;
    }	
}