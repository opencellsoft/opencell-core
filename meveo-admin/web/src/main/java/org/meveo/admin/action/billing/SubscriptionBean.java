/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.StatefulBaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.EntityListDataModelPF;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UsageChargeInstanceService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.medina.impl.AccessService;

@Named
@ConversationScoped
public class SubscriptionBean extends StatefulBaseBean<Subscription> {

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

	@Inject
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private OneShotChargeInstanceService oneShotChargeInstanceService;

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	private UsageChargeInstanceService usageChargeInstanceService;

	@Inject
	private AccessService accessService;

	@Inject
	private WalletOperationService walletOperationService;

	private ServiceInstance selectedServiceInstance;

	private BigDecimal quantity = BigDecimal.ONE;

	private OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance();

	private RecurringChargeInstance recurringChargeInstance;

	private BigDecimal oneShotChargeInstanceQuantity = BigDecimal.ONE;

	private String oneShotChargeInstanceWalletCode = null;

	/**
	 * User Account Id passed as a parameter. Used when creating new
	 * subscription entry from user account definition window, so default uset
	 * Account will be set on newly created subscription entry.
	 */
	private Long userAccountId;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */

	private EntityListDataModelPF<ServiceTemplate> serviceTemplates = new EntityListDataModelPF<ServiceTemplate>(
			new ArrayList<ServiceTemplate>());

	private EntityListDataModelPF<ServiceInstance> serviceInstances = new EntityListDataModelPF<ServiceInstance>(
			new ArrayList<ServiceInstance>());

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
	@Override
	public Subscription initEntity() {
		super.initEntity();
		if (userAccountId != null) {
			UserAccount userAccount = userAccountService.findById(getUserAccountId());
			populateAccounts(userAccount);

			// check if has default
			if (!userAccount.getDefaultLevel()) {
				entity.setDefaultLevel(true);
			}
		}
		log.debug("SubscriptionBean initEntity id={}", entity.getId());
		if (entity.getId() == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(entity.getSubscriptionDate());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			entity.setSubscriptionDate(calendar.getTime());
		} else {
			log.debug("entity.getOffer()=" + entity.getOffer().getCode());
			initServiceTemplates();

			serviceInstances.addAll(entity.getServiceInstances());
		}

		initCustomFields(AccountLevelEnum.SUB);

		log.debug("serviceInstances=" + serviceInstances.getSize());
		log.debug("servicetemplates=" + serviceTemplates.getSize());

		return entity;
	}

	private void initServiceTemplates() {
		if (entity.getOffer() != null) {
			List<ServiceInstance> serviceInstances = entity.getServiceInstances();
			for (ServiceTemplate serviceTemplate : entity.getOffer().getServiceTemplates()) {
				boolean alreadyInstanciated = false;

				for (ServiceInstance serviceInstance : serviceInstances) {
					if (serviceInstance.getStatus() != InstanceStatusEnum.CANCELED
							&& serviceInstance.getStatus() != InstanceStatusEnum.TERMINATED
							&& serviceInstance.getStatus() != InstanceStatusEnum.CLOSED)
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		if (entity.getDefaultLevel() != null && entity.getDefaultLevel()) {
			if (subscriptionService.isDuplicationExist(entity)) {
				entity.setDefaultLevel(false);
				messages.error(new BundleKey("messages", "error.account.duplicateDefautlLevel"));

				return null;
			}

		}

		super.saveOrUpdate(killConversation);

		saveCustomFields();

		return "/pages/billing/subscriptions/subscriptionDetail?edit=false&subscriptionId=" + entity.getId()
				+ "&faces-redirect=true&includeViewParams=true";
	}

	@Override
	protected String saveOrUpdate(Subscription entity) throws BusinessException {
		if (entity.isTransient()) {
			log.debug("SubscriptionBean save, # of service templates:{}", entity.getOffer().getServiceTemplates()
					.size());
			subscriptionService.create(entity);
			serviceTemplates.addAll(entity.getOffer().getServiceTemplates());
			messages.info(new BundleKey("messages", "save.successful"));
		} else {
			log.debug("SubscriptionBean update");
			subscriptionService.update(entity);
			messages.info(new BundleKey("messages", "update.successful"));
		}

		saveCustomFields();

		return back();
	}

	public void newOneShotChargeInstance() {
		log.debug("newOneShotChargeInstance ");
		this.oneShotChargeInstance = new OneShotChargeInstance();
	}

	public void editOneShotChargeIns(OneShotChargeInstance oneShotChargeIns) {
		this.oneShotChargeInstance = oneShotChargeIns;
	}

	public void saveOneShotChargeIns() {
		log.debug("saveOneShotChargeIns getObjectId=" + getObjectId());

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
				oneShotChargeInstance.setSeller(entity.getUserAccount().getBillingAccount().getCustomerAccount()
						.getCustomer().getSeller());
				oneShotChargeInstance.setCurrency(entity.getUserAccount().getBillingAccount().getCustomerAccount()
						.getTradingCurrency());
				oneShotChargeInstance.setCountry(entity.getUserAccount().getBillingAccount().getTradingCountry());
				// Long id =
				oneShotChargeInstance = oneShotChargeInstanceService.oneShotChargeApplication(entity,
						(OneShotChargeTemplate) oneShotChargeInstance.getChargeTemplate(),
						oneShotChargeInstanceWalletCode, oneShotChargeInstance.getChargeDate(),
						oneShotChargeInstance.getAmountWithoutTax(), oneShotChargeInstance.getAmountWithTax(),
						oneShotChargeInstanceQuantity, oneShotChargeInstance.getCriteria1(),
						oneShotChargeInstance.getCriteria2(), oneShotChargeInstance.getCriteria3(), getCurrentUser());
				// oneShotChargeInstance.setId(id);
				// oneShotChargeInstance.setProvider(oneShotChargeInstance.getChargeTemplate().getProvider());
			}
			messages.info(new BundleKey("messages", "save.successful"));

			clearObjectId();
		} catch (Exception e) {
			log.error("exception when applying one shot charge!", e.getMessage());
			messages.error(e.getMessage());
		}
	}

	public void newRecurringChargeInstance() {
		this.recurringChargeInstance = new RecurringChargeInstance();
	}

	public void editRecurringChargeIns(RecurringChargeInstance recurringChargeIns) {
		this.recurringChargeInstance = recurringChargeIns;
		log.debug("setting recurringChargeIns " + recurringChargeIns);
	}

	public void saveRecurringChargeIns() {
		log.debug("saveRecurringChargeIns getObjectId=#0", getObjectId());
		try {
			if (recurringChargeInstance != null) {
				if (recurringChargeInstance.getId() != null) {
					log.debug("update RecurringChargeIns #0, id:#1", recurringChargeInstance,
							recurringChargeInstance.getId());
					recurringChargeInstanceService.update(recurringChargeInstance);
				} else {
					log.debug("save RecurringChargeIns #0", recurringChargeInstance);

					recurringChargeInstance.setSubscription(entity);
					Long id = recurringChargeInstanceService.recurringChargeApplication(entity,
							(RecurringChargeTemplate) recurringChargeInstance.getChargeTemplate(),
							recurringChargeInstance.getChargeDate(), recurringChargeInstance.getAmountWithoutTax(),
							recurringChargeInstance.getAmountWithTax(), 1, recurringChargeInstance.getCriteria1(),
							recurringChargeInstance.getCriteria2(), recurringChargeInstance.getCriteria3(),
							getCurrentUser());
					recurringChargeInstance.setId(id);
					recurringChargeInstance.setProvider(recurringChargeInstance.getChargeTemplate().getProvider());
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

	public EntityListDataModelPF<ServiceInstance> getServiceInstances() {
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
		return (entity == null || entity.getId() == null) ? null : oneShotChargeInstanceService
				.findOneShotChargeInstancesBySubscriptionId(entity.getId());
	}

	public List<WalletOperation> getOneShotWalletOperations() {
		log.debug("getOneShotWalletOperations");
		if (this.oneShotChargeInstance == null || this.oneShotChargeInstance.getId() == null) {
			return null;
		}
		List<WalletOperation> results = new ArrayList<WalletOperation>(oneShotChargeInstance.getWalletOperations());

		Collections.sort(results, new Comparator<WalletOperation>() {
			public int compare(WalletOperation c0, WalletOperation c1) {

				return c1.getOperationDate().compareTo(c0.getOperationDate());
			}
		});
		log.debug("retrieved " + (results != null ? results.size() : 0) + " WalletOperations");
		return results;
	}

	public List<WalletOperation> getRecurringWalletOperations() {
		log.debug("getRecurringWalletOperations");
		if (this.recurringChargeInstance == null || this.recurringChargeInstance.getId() == null) {
			log.debug("recurringChargeInstance is null");
			return null;
		}

		log.debug("recurringChargeInstance is " + recurringChargeInstance.getId());

		List<WalletOperation> results = walletOperationService.listByChargeInstance(recurringChargeInstance);
		Collections.sort(results, new Comparator<WalletOperation>() {
			public int compare(WalletOperation c0, WalletOperation c1) {
				return c1.getOperationDate().compareTo(c0.getOperationDate());
			}
		});

		log.debug("retrieve " + (results != null ? results.size() : 0) + " WalletOperations");

		return results;
	}

	// @Factory("recurringChargeInstances")
	public List<RecurringChargeInstance> getRecurringChargeInstances() {
		return (entity == null || entity.getId() == null) ? null : recurringChargeInstanceService
				.findRecurringChargeInstanceBySubscriptionId(entity.getId());
	}

	public List<UsageChargeInstance> getUsageChargeInstances() {
		return (entity == null || entity.getId() == null) ? null : usageChargeInstanceService
				.findUsageChargeInstanceBySubscriptionId(entity.getId());
	}

	public void instanciateManyServices() {
		log.debug("instanciateManyServices");
		try {
			if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
				log.warn("instanciateManyServices quantity is negative! set it to 1");
				quantity = BigDecimal.ONE;
			}
			boolean isChecked = false;
			log.debug("serviceTemplates is " + serviceTemplates.getSize());

			log.debug("serviceTemplates is " + serviceTemplates.getSelectedItemsAsList());

			for (ServiceTemplate serviceTemplate : serviceTemplates.getSelectedItemsAsList()) {
				isChecked = true;
				log.debug("instanciateManyServices id={} checked, quantity={}", serviceTemplate.getId(), quantity);

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
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);

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
		log.debug("activateService...");
		try {
			log.debug("activateService id={} checked", selectedServiceInstance.getId());
			if (selectedServiceInstance != null) {
				log.debug("activateService:serviceInstance.getRecurrringChargeInstances.size={}",
						selectedServiceInstance.getRecurringChargeInstances().size());

				if (selectedServiceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
					messages.info(new BundleKey("messages", "error.activation.terminatedService"));
					return;
				}
				if (selectedServiceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
					messages.info(new BundleKey("messages", "error.activation.activeService"));
					return;
				}

				serviceInstanceService.serviceActivation(selectedServiceInstance, null, null, getCurrentUser());
			} else {
				log.error("activateService id=#0 is NOT a serviceInstance");
			}
			selectedServiceInstance = null;
			messages.info(new BundleKey("messages", "activation.activateSuccessful"));

		} catch (BusinessException e1) {
			messages.error(e1.getMessage());
		} catch (Exception e) {
			log.error("unexpected exception when activating service!", e);
			messages.error(e.getMessage());
		}
	}

	public void terminateService() {
		try {
			Date terminationDate = selectedServiceInstance.getTerminationDate();

			SubscriptionTerminationReason newSubscriptionTerminationReason = selectedServiceInstance
					.getSubscriptionTerminationReason();
			log.debug(
					"selected subscriptionTerminationReason={},terminationDate={},selectedServiceInstanceId={},status={}",
					new Object[] {
							newSubscriptionTerminationReason != null ? newSubscriptionTerminationReason.getId() : null,
							terminationDate, selectedServiceInstance.getId(), selectedServiceInstance.getStatus() });

			if (selectedServiceInstance.getStatus() != InstanceStatusEnum.TERMINATED) {
				serviceInstanceService.terminateService(selectedServiceInstance, terminationDate,
						newSubscriptionTerminationReason, getCurrentUser());
			} else {
				serviceInstanceService
						.updateTerminationMode(selectedServiceInstance, terminationDate, getCurrentUser());
			}

			selectedServiceInstance = null;

			initServiceTemplates();
			serviceTemplates.setSelectedItems(null);

			messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));

		} catch (BusinessException e1) {
			messages.error(e1.getMessage());
		} catch (Exception e) {
			log.error("unexpected exception when terminating service!", e);
			messages.error(e.getMessage());
		}
	}

	public void cancelService() {
		try {

			if (selectedServiceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
				messages.error(new BundleKey("messages", "error.termination.inactiveService"));
				return;
			}
			// serviceInstanceService.cancelService(selectedServiceInstance,
			// getCurrentUser());

			selectedServiceInstance = null;
			messages.info(new BundleKey("messages", "cancellation.cancelSuccessful"));

		} catch (Exception e) {
			log.error("unexpected exception when canceling service!", e);
			messages.error(e.getMessage());
		}
	}

	public void suspendService() {
		try {
			serviceInstanceService.serviceSuspension(selectedServiceInstance, new Date(), getCurrentUser());

			selectedServiceInstance = null;
			messages.info(new BundleKey("messages", "suspension.suspendSuccessful"));

		} catch (BusinessException e1) {
			messages.error(e1.getMessage());
		} catch (Exception e) {
			log.error("unexpected exception when suspending service!", e);
			messages.error(e.getMessage());
		}
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getOneShotChargeInstanceQuantity() {
		return oneShotChargeInstanceQuantity;
	}

	public void setOneShotChargeInstanceQuantity(BigDecimal oneShotChargeInstanceQuantity) {
		this.oneShotChargeInstanceQuantity = oneShotChargeInstanceQuantity;
	}

	public String getOneShotChargeInstanceWalletCode() {
		return oneShotChargeInstanceWalletCode;
	}

	public void setOneShotChargeInstanceWalletCode(String oneShotChargeInstanceWalletCode) {
		this.oneShotChargeInstanceWalletCode = oneShotChargeInstanceWalletCode;
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

	private Long getUserAccountId() {
		return userAccountId;
	}

	public void setUserAccountId(Long userAccountId) {
		this.userAccountId = userAccountId;
	}

	public String getDate() {
		return (new Date()).toString();
	}

	public List<Access> getAccess() {
		return accessService.listBySubscription(entity);
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

}