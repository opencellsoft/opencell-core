package org.meveo.api.billing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.account.AccessApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;
import org.meveo.api.dto.billing.ChargeInstanceOverrideDto;
import org.meveo.api.dto.billing.DueDateDelayDto;
import org.meveo.api.dto.billing.InstantiateServicesRequestDto;
import org.meveo.api.dto.billing.OperationServicesRequestDto;
import org.meveo.api.dto.billing.ProductDto;
import org.meveo.api.dto.billing.ProductInstanceDto;
import org.meveo.api.dto.billing.RateSubscriptionRequestDto;
import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.billing.ServiceToActivateDto;
import org.meveo.api.dto.billing.ServiceToInstantiateDto;
import org.meveo.api.dto.billing.ServiceToUpdateDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.SubscriptionRenewalDto;
import org.meveo.api.dto.billing.SubscriptionsDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.billing.UpdateServicesRequestDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.RateSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.DueDateDelayEnum;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.order.OrderService;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Wassim Drira
 * @author Said Ramli
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class SubscriptionApi extends BaseApi {

    /**
     * Default sort for list call.
     */
    private static final String DEFAULT_SORT_ORDER_ID = "id";

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @Inject
    private TerminationReasonService terminationReasonService;

    @Inject
    private WalletTemplateService walletTemplateService;

    @Inject
    private AccessApi accessApi;

    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private ProductInstanceService productInstanceService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private OrderService orderService;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private BillingCycleService billingCycleService;

    private ParamBean paramBean = ParamBean.getInstance();

    /**
     * v5.0 admin parameter to authorize/bare the multiactivation of an instantiated service
     * 
     * @param postData The subscription dto
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public void create(SubscriptionDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getUserAccount())) {
            missingParameters.add("userAccount");
        }
        if (StringUtils.isBlank(postData.getOfferTemplate())) {
            missingParameters.add("offerTemplate");
        }
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getSubscriptionDate())) {
            missingParameters.add("subscriptionDate");
        }

        handleMissingParametersAndValidate(postData);

        if (subscriptionService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Subscription.class, postData.getCode());
        }

        UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount());
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), postData.getSubscriptionDate());
        if (offerTemplate == null) {
            throw new EntityDoesNotExistsException(OfferTemplate.class,
                postData.getOfferTemplate() + " / " + DateUtils.formatDateWithPattern(postData.getSubscriptionDate(), paramBean.getDateTimeFormat()));
        }

        if (offerTemplate.isDisabled()) {
            throw new MeveoApiException("Cannot subscribe to disabled offer");
        }

        Subscription subscription = new Subscription();
        
        subscription.setCode(postData.getCode());
        subscription.setDescription(postData.getDescription());
        subscription.setUserAccount(userAccount);
        subscription.setOffer(offerTemplate);
        if (!StringUtils.isBlank(postData.getBillingCycle())) {
            BillingCycle billingCycle = billingCycleService.findByCode(postData.getBillingCycle());
            if (billingCycle == null) {
                throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
            }
            subscription.setBillingCycle(billingCycle);
        }
        subscription.setSubscriptionDate(postData.getSubscriptionDate());
        subscription.setTerminationDate(postData.getTerminationDate());
        if (postData.getRenewalRule() == null) {
            subscription.setSubscriptionRenewal(subscriptionRenewalFromDto(offerTemplate.getSubscriptionRenewal(), null, false));
        } else {
            subscription.setSubscriptionRenewal(subscriptionRenewalFromDto(null, postData.getRenewalRule(), false));
        }
        
        Boolean subscriptionAutoEndOfEngagement = postData.getAutoEndOfEngagement();
        if (subscriptionAutoEndOfEngagement == null) {
            subscription.setAutoEndOfEngagement(offerTemplate.getAutoEndOfEngagement());
        } else {
            subscription.setAutoEndOfEngagement(postData.getAutoEndOfEngagement());
        }
        
        subscription.updateSubscribedTillAndRenewalNotifyDates();
        // ignoring postData.getEndAgreementDate() if subscription.getAutoEndOfEngagement is true
        if (subscription.getAutoEndOfEngagement() == null || !subscription.getAutoEndOfEngagement()) {
            subscription.setEndAgreementDate(postData.getEndAgreementDate());
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), subscription, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        subscriptionService.create(subscription);
        

        if (postData.getProducts() != null) {
            for (ProductDto productDto : postData.getProducts().getProducts()) {
                if (StringUtils.isBlank(productDto.getCode())) {
                    log.warn("code is null={}", productDto);
                    continue;
                }
                ApplyProductRequestDto dto = new ApplyProductRequestDto(productDto);
                applyProduct(dto);
            }
        }
    }

    /**
     * v5.0 admin parameter to authorize/bare the multiactivation of an instantiated service
     * 
     * @param postData subscription Dto
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public void update(SubscriptionDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getSubscriptionDate())) {
            missingParameters.add("subscriptionDate");
        }

        handleMissingParametersAndValidate(postData);

        Subscription subscription = subscriptionService.findByCode(postData.getCode());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getCode());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
            throw new MeveoApiException("Subscription is already RESILIATED.");
        }

        if (!StringUtils.isBlank(postData.getUserAccount())) {
            UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount());
            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
            } else if (!subscription.getUserAccount().equals(userAccount)) {
                throw new InvalidParameterException(
                    "Can not change the parent account. Subscription's current parent account (user account) is " + subscription.getUserAccount().getCode());
            }
            subscription.setUserAccount(userAccount);
        }

        if (postData.getOfferTemplate() != null) {
            OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), postData.getSubscriptionDate());
            if (offerTemplate == null) {
                throw new EntityDoesNotExistsException(OfferTemplate.class,
                    postData.getOfferTemplate() + " / " + DateUtils.formatDateWithPattern(postData.getSubscriptionDate(), paramBean.getDateTimeFormat()));
            } else if (subscription.getServiceInstances() != null && !subscription.getServiceInstances().isEmpty() && !subscription.getOffer().equals(offerTemplate)) {
                throw new InvalidParameterException("Cannot change the offer of subscription once the services are instantiated");
            } else if (offerTemplate.isDisabled()) {
                throw new InvalidParameterException("Cannot subscribe to disabled offer");
            }
            subscription.setOffer(offerTemplate);
        }

        if (!StringUtils.isBlank(postData.getBillingCycle())) {
            BillingCycle billingCycle = billingCycleService.findByCode(postData.getBillingCycle());
            if (billingCycle == null) {
                throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
            }
            subscription.setBillingCycle(billingCycle);
        }

        subscription.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        subscription.setDescription(postData.getDescription());
        subscription.setSubscriptionDate(postData.getSubscriptionDate());
        subscription.setTerminationDate(postData.getTerminationDate());
        
        subscription.setSubscriptionRenewal(subscriptionRenewalFromDto(subscription.getSubscriptionRenewal(), postData.getRenewalRule(), subscription.isRenewed()));
        subscription.setMinimumAmountEl(postData.getMinimumAmountEl());
        subscription.setMinimumLabelEl(postData.getMinimumLabelEl());
        
        if (postData.getAutoEndOfEngagement() != null) {
            subscription.setAutoEndOfEngagement(postData.getAutoEndOfEngagement());
            subscription.updateSubscribedTillAndRenewalNotifyDates();
        } 
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), subscription, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        subscription = subscriptionService.update(subscription);
        // ignoring postData.getEndAgreementDate() if subscription.getAutoEndOfEngagement is true
        if (subscription.getAutoEndOfEngagement() == null || !subscription.getAutoEndOfEngagement()) {
            subscription.setEndAgreementDate(postData.getEndAgreementDate());
        }
        if (postData.getProducts() != null) {
            for (ProductDto productDto : postData.getProducts().getProducts()) {
                if (StringUtils.isBlank(productDto.getCode())) {
                    log.warn("code is null={}", productDto);
                    continue;
                }
                ApplyProductRequestDto dto = new ApplyProductRequestDto(productDto);
                applyProduct(dto);
            }
        }

    }

    /**
     * v5.0 admin parameter to authorize/bare the multiactivation of an instantiated service
     * 
     * @param activateServicesDto activateServicesDto
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException Business exception
     */
    public void activateServices(ActivateServicesRequestDto activateServicesDto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(activateServicesDto.getSubscription())) {
            missingParameters.add("subscription");
        }
        if (activateServicesDto.getServicesToActivateDto().getService() == null || activateServicesDto.getServicesToActivateDto().getService().size() == 0) {
            missingParameters.add("services");
        }

        handleMissingParametersAndValidate(activateServicesDto);

        Subscription subscription = subscriptionService.findByCode(activateServicesDto.getSubscription());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, activateServicesDto.getSubscription());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
        }

        // check if exists
        List<ServiceToActivateDto> serviceToActivateDtos = new ArrayList<>();
        for (ServiceToActivateDto serviceToActivateDto : activateServicesDto.getServicesToActivateDto().getService()) {

            if (StringUtils.isBlank(serviceToActivateDto.getSubscriptionDate())) {
                missingParameters.add("SubscriptionDate");
                handleMissingParameters();
            }
            ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceToActivateDto.getCode());
            if (serviceTemplate == null) {
                throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceToActivateDto.getCode());
            }
            serviceToActivateDto.setServiceTemplate(serviceTemplate);
            serviceToActivateDtos.add(serviceToActivateDto);
        }

        // Find instantiated or instantiate if not instantiated yet
        List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
        for (ServiceToActivateDto serviceToActivateDto : serviceToActivateDtos) {

            ServiceTemplate serviceTemplate = serviceToActivateDto.getServiceTemplate();

            ServiceInstance serviceInstance = null;

            if (paramBean.isServiceMultiInstantiation()) {
                List<ServiceInstance> alreadyInstantiatedServices = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription,
                    InstanceStatusEnum.INACTIVE);
                if (alreadyInstantiatedServices != null && !alreadyInstantiatedServices.isEmpty()) {
                    serviceInstance = alreadyInstantiatedServices.get(0);
                }

            } else {
                List<ServiceInstance> alreadyInstantiatedServices = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription,
                    InstanceStatusEnum.INACTIVE, InstanceStatusEnum.ACTIVE);

                if (alreadyInstantiatedServices != null && !alreadyInstantiatedServices.isEmpty()) {
                    for (ServiceInstance alreadyInstantiatedService : alreadyInstantiatedServices) {
                        if (alreadyInstantiatedService.getStatus() == InstanceStatusEnum.ACTIVE) {
                            throw new MeveoApiException("ServiceInstance with code=" + alreadyInstantiatedService.getCode() + " is already activated.");
                        } else if (alreadyInstantiatedService.getStatus() == InstanceStatusEnum.INACTIVE) {
                            serviceInstance = alreadyInstantiatedService;
                            break;
                        }
                    }
                }
            }

            // Require a quantity if service was not instantiated before
            if (serviceInstance == null && serviceToActivateDto.getQuantity() == null) {
                throw new MissingParameterException("quantity for service " + serviceToActivateDto.getCode());
            }

            // Update instantiated service with info
            if (serviceInstance != null) {
                log.debug("Found already instantiated service {} of {} for subscription {} quantity {}", serviceInstance.getId(), serviceTemplate.getCode(), subscription.getCode(),
                    serviceInstance.getQuantity());

                if (serviceToActivateDto.getSubscriptionDate() != null) {
                    serviceInstance.setSubscriptionDate(serviceToActivateDto.getSubscriptionDate());
                }
                if (serviceToActivateDto.getQuantity() != null) {
                    serviceInstance.setQuantity(serviceToActivateDto.getQuantity());
                }
                // Do not update existing value
                if (activateServicesDto.getOrderNumber() != null) {
                    serviceInstance.setOrderNumber(activateServicesDto.getOrderNumber());
                }
                if (!StringUtils.isBlank(serviceToActivateDto.getDescription())) {
                    serviceInstance.setDescription(serviceToActivateDto.getDescription());
                }
                serviceInstances.add(serviceInstance);

                // Instantiate if it was not instantiated earlier
            } else if (serviceInstance == null) {

                log.debug("Will instantiate as part of activation service {} for subscription {} quantity {}", serviceTemplate.getCode(), subscription.getCode(),
                    serviceToActivateDto.getQuantity());

                serviceInstance = new ServiceInstance();
                serviceInstance.setCode(serviceTemplate.getCode());
                if (!StringUtils.isBlank(serviceToActivateDto.getDescription())) {
                    serviceInstance.setDescription(serviceToActivateDto.getDescription());
                } else {
                    serviceInstance.setDescription(serviceTemplate.getDescription());
                }
                serviceInstance.setServiceTemplate(serviceTemplate);
                serviceInstance.setSubscription(subscription);
                serviceInstance.setRateUntilDate(serviceToActivateDto.getRateUntilDate());
                if (serviceToActivateDto.getSubscriptionDate() == null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    serviceInstance.setSubscriptionDate(calendar.getTime());
                } else {
                    serviceInstance.setSubscriptionDate(serviceToActivateDto.getSubscriptionDate());
                }
                serviceInstance.setQuantity(serviceToActivateDto.getQuantity());
                serviceInstance.setOrderNumber(activateServicesDto.getOrderNumber());
                serviceInstance.setOrderItemId(activateServicesDto.getOrderItemId());
                serviceInstance.setOrderItemAction(activateServicesDto.getOrderItemAction());

                // populate customFields
                try {
                    populateCustomFields(serviceToActivateDto.getCustomFields(), serviceInstance, true);
                } catch (MissingParameterException | InvalidParameterException e) {
                    log.error("Failed to associate custom field instance to an entity: {} {}", serviceToActivateDto.getCode(), e.getMessage());
                    throw e;
                } catch (Exception e) {
                    log.error("Failed to associate custom field instance to an entity {}", serviceToActivateDto.getCode(), e);
                    throw new MeveoApiException("Failed to associate custom field instance to an entity " + serviceToActivateDto.getCode());
                }

                try {
                    String descriptionOverride = !StringUtils.isBlank(serviceToActivateDto.getDescription()) ? serviceToActivateDto.getDescription() : null;
                    serviceInstanceService.serviceInstanciation(serviceInstance, descriptionOverride);
                    serviceInstances.add(serviceInstance);

                } catch (BusinessException e) {
                    log.error("Failed to instantiate a service {} on subscription {}", serviceToActivateDto.getCode(), subscription.getCode(), e);
                    throw e;
                }
            }

            // override price and description
            if (serviceToActivateDto.getChargeInstanceOverrides() != null && serviceToActivateDto.getChargeInstanceOverrides().getChargeInstanceOverride() != null) {
                for (ChargeInstanceOverrideDto chargeInstanceOverrideDto : serviceToActivateDto.getChargeInstanceOverrides().getChargeInstanceOverride()) {
                    if (!StringUtils.isBlank(chargeInstanceOverrideDto.getChargeInstanceCode())) {
                        ChargeInstance chargeInstance = chargeInstanceService.findByCodeAndService(chargeInstanceOverrideDto.getChargeInstanceCode(), subscription.getId(),
                            InstanceStatusEnum.INACTIVE);
                        if (chargeInstance == null) {
                            throw new EntityDoesNotExistsException(ChargeInstance.class, chargeInstanceOverrideDto.getChargeInstanceCode());
                        }
                        if (chargeInstance.getChargeTemplate().getAmountEditable() != null && chargeInstance.getChargeTemplate().getAmountEditable()) {
                            if (chargeInstanceOverrideDto.getAmountWithoutTax() != null) {
                                log.debug("override AmountWithoutTax:{}", chargeInstanceOverrideDto.getAmountWithoutTax());
                                chargeInstance.setAmountWithoutTax(chargeInstanceOverrideDto.getAmountWithoutTax());
                            }
                            if (!appProvider.isEntreprise() && chargeInstanceOverrideDto.getAmountWithTax() != null) {
                                log.debug("override AmountWithTax:{}", chargeInstanceOverrideDto.getAmountWithTax());
                                chargeInstance.setAmountWithTax(chargeInstanceOverrideDto.getAmountWithTax());
                            }
                            if (!StringUtils.isBlank(chargeInstanceOverrideDto.getDescription())) {
                                log.debug("override description:{}", chargeInstanceOverrideDto.getDescription());
                                chargeInstance.setDescription(chargeInstanceOverrideDto.getDescription());
                            }
                        } else {
                            log.warn("Charge with code {} is not overrideable", chargeInstanceOverrideDto.getChargeInstanceCode());
                        }
                    } else {
                        log.warn("chargeInstance.code and amountWithoutTax must not be null.");
                    }
                }
            }
        }

        // activate services
        for (ServiceInstance serviceInstance : serviceInstances) {

            try {
                serviceInstanceService.serviceActivation(serviceInstance, null, null);
            } catch (BusinessException e) {
                log.error("Failed to activate a service {}/{} on subscription {}", serviceInstance.getId(), serviceInstance.getCode(), subscription.getCode(), e);
                throw e;
            }
        }
    }

    /**
     * v5.0 admin parameter to authorize/bare the multiactivation of an instantiated service
     * 
     * @param instantiateServicesDto instantiateServices Dto
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException Business exception
     */
    public void instantiateServices(InstantiateServicesRequestDto instantiateServicesDto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(instantiateServicesDto.getSubscription())) {
            missingParameters.add("subscription");
        }
        if (instantiateServicesDto.getServicesToInstantiate().getService() == null || instantiateServicesDto.getServicesToInstantiate().getService().size() == 0) {
            missingParameters.add("services");
        }

        handleMissingParametersAndValidate(instantiateServicesDto);

        Subscription subscription = subscriptionService.findByCode(instantiateServicesDto.getSubscription());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, instantiateServicesDto.getSubscription());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
        }
        // check if exists
        List<ServiceToInstantiateDto> serviceToInstantiateDtos = new ArrayList<>();
        for (ServiceToInstantiateDto serviceToInstantiateDto : instantiateServicesDto.getServicesToInstantiate().getService()) {
            if (serviceToInstantiateDto.getQuantity() == null) {
                throw new MissingParameterException("quantity for service " + serviceToInstantiateDto.getCode());
            }
            ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceToInstantiateDto.getCode());
            if (serviceTemplate == null) {
                throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceToInstantiateDto.getCode());
            }

            serviceToInstantiateDto.setServiceTemplate(serviceTemplate);
            serviceToInstantiateDtos.add(serviceToInstantiateDto);
        }

        // instantiate
        for (ServiceToInstantiateDto serviceToInstantiateDto : serviceToInstantiateDtos) {
            ServiceTemplate serviceTemplate = serviceToInstantiateDto.getServiceTemplate();

            ServiceInstance serviceInstance = null;

            if (paramBean.isServiceMultiInstantiation()) {
                List<ServiceInstance> subscriptionServiceInstances = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription,
                    InstanceStatusEnum.INACTIVE);
                if (!subscriptionServiceInstances.isEmpty()) {
                    throw new MeveoApiException("ServiceInstance with code=" + serviceToInstantiateDto.getCode() + " is already instanciated.");
                }

            } else {
                List<ServiceInstance> subscriptionServiceInstances = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription,
                    InstanceStatusEnum.INACTIVE, InstanceStatusEnum.ACTIVE);
                if (!subscriptionServiceInstances.isEmpty()) {
                    throw new MeveoApiException("ServiceInstance with code=" + serviceToInstantiateDto.getCode() + " is already instanciated or activated.");
                }
            }
            log.debug("Will instantiate service {} for subscription {} quantity {}", serviceTemplate.getCode(), subscription.getCode(), serviceToInstantiateDto.getQuantity());

            serviceInstance = new ServiceInstance();
            serviceInstance.setCode(serviceTemplate.getCode());
            serviceInstance.setDescription(serviceTemplate.getDescription());
            serviceInstance.setServiceTemplate(serviceTemplate);
            serviceInstance.setSubscription(subscription);
            serviceInstance.setRateUntilDate(serviceToInstantiateDto.getRateUntilDate());
            serviceInstance.setQuantity(serviceToInstantiateDto.getQuantity());
            serviceInstance.setOrderNumber(instantiateServicesDto.getOrderNumber());
            serviceInstance.setOrderItemId(instantiateServicesDto.getOrderItemId());
            serviceInstance.setOrderItemAction(instantiateServicesDto.getOrderItemAction());

            if (serviceToInstantiateDto.getSubscriptionDate() == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                serviceInstance.setSubscriptionDate(calendar.getTime());
            } else {
                serviceInstance.setSubscriptionDate(serviceToInstantiateDto.getSubscriptionDate());
            }
            // populate customFields
            try {
                populateCustomFields(serviceToInstantiateDto.getCustomFields(), serviceInstance, true);
            } catch (MissingParameterException | InvalidParameterException e) {
                log.error("Failed to associate custom field instance to an entity: {} {}", serviceToInstantiateDto.getCode(), e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity {}", serviceToInstantiateDto.getCode(), e);
                throw new MeveoApiException("Failed to associate custom field instance to an entity " + serviceToInstantiateDto.getCode());
            }
            serviceInstance.setTerminationDate(subscription.getTerminationDate());
            try {
                String descriptionOverride = !StringUtils.isBlank(serviceToInstantiateDto.getDescription()) ? serviceToInstantiateDto.getDescription() : null;
                serviceInstanceService.serviceInstanciation(serviceInstance, descriptionOverride);

            } catch (BusinessException e) {
                log.error("Failed to instantiate a service {} on subscription {}", serviceToInstantiateDto.getCode(), subscription.getCode(), e);
                throw e;
            }
        }
    }

    /**
     * Apply an one shot charge on a subscription
     * 
     * @param postData The apply one shot charge instance request dto
     * @throws MeveoApiException Meveo api exception
     */
    public void applyOneShotChargeInstance(ApplyOneShotChargeInstanceRequestDto postData) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getOneShotCharge())) {
            missingParameters.add("oneShotCharge");
        }
        if (StringUtils.isBlank(postData.getSubscription())) {
            missingParameters.add("subscription");
        }
        if (postData.getOperationDate() == null) {
            missingParameters.add("operationDate");
        }

        handleMissingParametersAndValidate(postData);

        OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getOneShotCharge());
        if (oneShotChargeTemplate == null) {
            throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getOneShotCharge());
        }

        Subscription subscription = subscriptionService.findByCode(postData.getSubscription());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
        }
        if (postData.getWallet() != null) {
            WalletTemplate walletTemplate = walletTemplateService.findByCode(postData.getWallet());
            if (walletTemplate == null) {
                throw new EntityDoesNotExistsException(WalletTemplate.class, postData.getWallet());
            }

            if ((!postData.getWallet().equals("PRINCIPAL")) && !subscription.getUserAccount().getPrepaidWallets().containsKey(postData.getWallet())) {
                if (postData.getCreateWallet() != null && postData.getCreateWallet()) {
                    subscription.getUserAccount().getWalletInstance(postData.getWallet());
                } else {
                    throw new MeveoApiException("Wallet " + postData.getWallet() + " is not attached to the user account, but were instructed not to create it");
                }
            }
        }

        try {
            oneShotChargeInstanceService.oneShotChargeApplication(subscription, (OneShotChargeTemplate) oneShotChargeTemplate, postData.getWallet(), postData.getOperationDate(),
                postData.getAmountWithoutTax(), postData.getAmountWithTax(), postData.getQuantity(), postData.getCriteria1(), postData.getCriteria2(), postData.getCriteria3(),
                postData.getDescription(), subscription.getOrderNumber(), true);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
    }

    /**
     * Apply a product charge on a subscription
     * @param postData Apply product request dto
     * @return List wallet operation generated
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public List<WalletOperationDto> applyProduct(ApplyProductRequestDto postData) throws MeveoApiException, BusinessException {
        List<WalletOperationDto> result = new ArrayList<>();
        if (StringUtils.isBlank(postData.getProduct())) {
            missingParameters.add("product");
        }
        if (StringUtils.isBlank(postData.getSubscription())) {
            missingParameters.add("subscription");
        }
        if (postData.getOperationDate() == null) {
            missingParameters.add("operationDate");
        }

        handleMissingParametersAndValidate(postData);

        ProductTemplate productTemplate = productTemplateService.findByCode(postData.getProduct(), postData.getOperationDate());
        if (productTemplate == null) {
            throw new EntityDoesNotExistsException(ProductTemplate.class,
                postData.getProduct() + "/" + DateUtils.formatDateWithPattern(postData.getOperationDate(), paramBeanFactory.getInstance().getDateTimeFormat()));
        }

        Subscription subscription = subscriptionService.findByCode(postData.getSubscription());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
        }

        if ((subscription.getStatus() != SubscriptionStatusEnum.ACTIVE) && (subscription.getStatus() != SubscriptionStatusEnum.CREATED)) {
            throw new MeveoApiException("subscription is not ACTIVE or CREATED: [" + subscription.getStatus() + "]");
        }

        List<WalletOperation> walletOperations = null;

        try {
            ProductInstance productInstance = new ProductInstance(null, subscription, productTemplate, postData.getQuantity(), postData.getOperationDate(), postData.getProduct(),
                StringUtils.isBlank(postData.getDescription()) ? productTemplate.getDescriptionOrCode() : postData.getDescription(), null);

            // populate customFields
            try {
                populateCustomFields(postData.getCustomFields(), productInstance, true);
            } catch (MissingParameterException | InvalidParameterException e) {
                log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                throw e;
            }

            productInstanceService.instantiateProductInstance(productInstance, postData.getCriteria1(), postData.getCriteria2(), postData.getCriteria3(), false);

            walletOperations = productInstanceService.applyProductInstance(productInstance, postData.getCriteria1(), postData.getCriteria2(), postData.getCriteria3(), true, false);
            for (WalletOperation walletOperation : walletOperations) {
                result.add(new WalletOperationDto(walletOperation));
            }
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }

        return result;
    }

    /**
     * Terminate subscription
     * @param postData Terminate subscription request dto
     * @param orderNumber order number
     * @throws MeveoApiException Meveo api exception
     */
    public void terminateSubscription(TerminateSubscriptionRequestDto postData, String orderNumber) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getSubscriptionCode())) {
            missingParameters.add("subscriptionCode");
        }
        if (StringUtils.isBlank(postData.getTerminationReason())) {
            missingParameters.add("terminationReason");
        }
        if (postData.getTerminationDate() == null) {
            missingParameters.add("terminationDate");
        }

        handleMissingParametersAndValidate(postData);

        Subscription subscription = subscriptionService.findByCode(postData.getSubscriptionCode());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscriptionCode());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
            throw new MeveoApiException("Subscription is already RESILIATED.");
        }

        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(postData.getTerminationReason());
        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getTerminationReason());
        }

        try {
            subscriptionService.terminateSubscription(subscription, postData.getTerminationDate(), subscriptionTerminationReason,
                ChargeInstance.NO_ORDER_NUMBER.equals(orderNumber) ? subscription.getOrderNumber() : orderNumber, postData.getOrderItemId(), postData.getOrderItemAction());
        } catch (BusinessException e) {
            log.error("error while setting subscription termination", e);
            throw new MeveoApiException(e.getMessage());
        }
    }

    /**
     * Terminate services
     * @param terminateSubscriptionDto Terminate subscription services request dto
     * @throws MeveoApiException Meveo api exception
     */
    public void terminateServices(TerminateSubscriptionServicesRequestDto terminateSubscriptionDto) throws MeveoApiException {

        if (StringUtils.isBlank(terminateSubscriptionDto.getSubscriptionCode())) {
            missingParameters.add("subscriptionCode");
        }
        if ((terminateSubscriptionDto.getServices() == null || terminateSubscriptionDto.getServices().isEmpty())
                && (terminateSubscriptionDto.getServiceIds() == null || terminateSubscriptionDto.getServiceIds().isEmpty())) {
            missingParameters.add("services or serviceIds");
        }
        if (StringUtils.isBlank(terminateSubscriptionDto.getTerminationReason())) {
            missingParameters.add("terminationReason");
        }
        if (terminateSubscriptionDto.getTerminationDate() == null) {
            missingParameters.add("terminationDate");
        }

        handleMissingParametersAndValidate(terminateSubscriptionDto);

        Subscription subscription = subscriptionService.findByCode(terminateSubscriptionDto.getSubscriptionCode());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, terminateSubscriptionDto.getSubscriptionCode());
        }

        SubscriptionTerminationReason serviceTerminationReason = terminationReasonService.findByCode(terminateSubscriptionDto.getTerminationReason());
        if (serviceTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, terminateSubscriptionDto.getTerminationReason());
        }

        if (terminateSubscriptionDto.getServices() != null) {
            for (String serviceInstanceCode : terminateSubscriptionDto.getServices()) {
                ServiceInstance serviceInstance = getSingleServiceInstance(null, serviceInstanceCode, subscription, InstanceStatusEnum.ACTIVE);
                serviceInstance.setOrderItemId(terminateSubscriptionDto.getOrderItemId());
                serviceInstance.setOrderItemAction(terminateSubscriptionDto.getOrderItemAction());
                try {
                    serviceInstanceService.terminateService(serviceInstance, terminateSubscriptionDto.getTerminationDate(), serviceTerminationReason,
                        ChargeInstance.NO_ORDER_NUMBER.equals(terminateSubscriptionDto.getOrderNumber()) ? serviceInstance.getOrderNumber()
                                : terminateSubscriptionDto.getOrderNumber());
                } catch (BusinessException e) {
                    log.error("service termination={}", e.getMessage());
                    throw new MeveoApiException(e.getMessage());
                }
            }
        }

        if (terminateSubscriptionDto.getServiceIds() != null) {
            for (Long serviceInstanceId : terminateSubscriptionDto.getServiceIds()) {
                ServiceInstance serviceInstance = getSingleServiceInstance(serviceInstanceId, null, subscription, InstanceStatusEnum.ACTIVE);
                serviceInstance.setOrderItemId(terminateSubscriptionDto.getOrderItemId());
                serviceInstance.setOrderItemAction(terminateSubscriptionDto.getOrderItemAction());
                try {
                    serviceInstanceService.terminateService(serviceInstance, terminateSubscriptionDto.getTerminationDate(), serviceTerminationReason,
                        ChargeInstance.NO_ORDER_NUMBER.equals(terminateSubscriptionDto.getOrderNumber()) ? serviceInstance.getOrderNumber()
                                : terminateSubscriptionDto.getOrderNumber());
                } catch (BusinessException e) {
                    log.error("service termination={}", e.getMessage());
                    throw new MeveoApiException(e.getMessage());
                }
            }
        }
    }

    /**
     * List subscription by user account
     * @param userAccountCode user account code
     * @param mergedCF true/false (true if we want the merged CF in return)
     * @param sortBy name of column to be sorted
     * @param sortOrder ASC/DESC
     * @return instance of SubscriptionsListDto which contains list of Subscription DTO
     * @throws MeveoApiException meveo api exception
     */
    public SubscriptionsDto listByUserAccount(String userAccountCode, boolean mergedCF, String sortBy, SortOrder sortOrder) throws MeveoApiException {

        if (StringUtils.isBlank(userAccountCode)) {
            missingParameters.add("userAccountCode");
            handleMissingParameters();
        }

        UserAccount userAccount = userAccountService.findByCode(userAccountCode);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
        }

        SubscriptionsDto result = new SubscriptionsDto();
        List<Subscription> subscriptions = subscriptionService.listByUserAccount(userAccount, sortBy,
            sortOrder != null ? org.primefaces.model.SortOrder.valueOf(sortOrder.name()) : org.primefaces.model.SortOrder.ASCENDING);
        if (subscriptions != null) {
            for (Subscription s : subscriptions) {
                result.getSubscription().add(subscriptionToDto(s, CustomFieldInheritanceEnum.getInheritCF(true, mergedCF)));
            }
        }

        return result;

    }

    /**
     * List subbscriptions
     * @param mergedCF truf if merging inherited CF
     * @param pagingAndFiltering paging and filtering.
     * @return instance of SubscriptionsListDto which contains list of Subscription DTO
     * @throws MeveoApiException meveo api exception
     */
    public SubscriptionsListResponseDto list(Boolean mergedCF, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        boolean merge = mergedCF != null && mergedCF;
        return list(pagingAndFiltering, CustomFieldInheritanceEnum.getInheritCF(true, merge));
    }

    public SubscriptionsListResponseDto list(PagingAndFiltering pagingAndFiltering, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {
        
        String sortBy = DEFAULT_SORT_ORDER_ID;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }

        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, org.primefaces.model.SortOrder.ASCENDING, null, pagingAndFiltering, Subscription.class);

        Long totalCount = subscriptionService.count(paginationConfiguration);

        SubscriptionsListResponseDto result = new SubscriptionsListResponseDto();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<Subscription> subscriptions = subscriptionService.list(paginationConfiguration);
            if (subscriptions != null) {
                for (Subscription subscription : subscriptions) {
                    result.getSubscriptions().getSubscription().add(subscriptionToDto(subscription, inheritCF));
                }
            }
        }

        return result;

    }

    /**
     * Find subscription by code
     * @param subscriptionCode code of subscription to find
     * @return instance of SubscriptionsListDto which contains list of Subscription DTO
     * @throws MeveoApiException meveo api exception
     */
    public SubscriptionDto findSubscription(String subscriptionCode) throws MeveoApiException {
        return this.findSubscription(subscriptionCode, false, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    /**
     * Find subscription
     * @param subscriptionCode code of subscription to find
     * @param mergedCF true/false
     * @param inheritCF Custom field inheritance type
     * @return instance of SubscriptionsListDto which contains list of Subscription DTO
     * @throws MeveoApiException meveo api exception
     */
    public SubscriptionDto findSubscription(String subscriptionCode, boolean mergedCF, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {
        SubscriptionDto result = new SubscriptionDto();

        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
            handleMissingParameters();
        }
        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        CustomFieldInheritanceEnum inherit = (inheritCF != null && !mergedCF) ? inheritCF : CustomFieldInheritanceEnum.getInheritCF(true, mergedCF);

        result = subscriptionToDto(subscription, inherit);

        return result;
    }

    /**
     * Create or update Subscription based on subscription code.
     *
     * @param postData posted data to API
     *
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(SubscriptionDto postData) throws MeveoApiException, BusinessException {
        if (subscriptionService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * Convert subscription entity to dto
     * @param subscription instance of Subscription to be mapped
     * @return instance of SubscriptionDto.
     */
    public SubscriptionDto subscriptionToDto(Subscription subscription) {
        return this.subscriptionToDto(subscription, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    /**
     * Convert subscription dto to entity
     * @param subscription instance of Subscription to be mapped
     * @param inheritCF choose whether CF values are inherited and/or merged
     * @return instance of SubscriptionDto
     */
    public SubscriptionDto subscriptionToDto(Subscription subscription, CustomFieldInheritanceEnum inheritCF) {
        SubscriptionDto dto = new SubscriptionDto(subscription);
        if (subscription.getAccessPoints() != null) {
            for (Access ac : subscription.getAccessPoints()) {
                CustomFieldsDto customFieldsDTO = null;
                customFieldsDTO = entityToDtoConverter.getCustomFieldsDTO(ac, inheritCF);

                AccessDto accessDto = new AccessDto(ac, customFieldsDTO);
                dto.getAccesses().getAccess().add(accessDto);
            }
        }

        dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(subscription, inheritCF));
        if (subscription.getServiceInstances() != null) {
            for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {
                ServiceInstanceDto serviceInstanceDto = null;
                CustomFieldsDto customFieldsDTO = null;
                customFieldsDTO = entityToDtoConverter.getCustomFieldsDTO(serviceInstance, inheritCF);

                serviceInstanceDto = new ServiceInstanceDto(serviceInstance, customFieldsDTO);
                dto.getServices().addServiceInstance(serviceInstanceDto);
            }
        }

        if (subscription.getProductInstances() != null) {
            for (ProductInstance productInstance : subscription.getProductInstances()) {
                CustomFieldsDto customFieldsDTO = null;
                customFieldsDTO = entityToDtoConverter.getCustomFieldsDTO(productInstance, inheritCF);

                dto.getProductInstances().add(new ProductInstanceDto(productInstance, customFieldsDTO));
            }
        }

        return dto;
    }

    public void createOrUpdatePartialWithAccessAndServices(SubscriptionDto subscriptionDto, String orderNumber, Long orderItemId, OrderItemActionEnum orderItemAction)
            throws MeveoApiException, BusinessException {

        SubscriptionDto existedSubscriptionDto = null;
        try {
            existedSubscriptionDto = findSubscription(subscriptionDto.getCode());
        } catch (Exception e) {
            existedSubscriptionDto = null;
        }

        log.debug("createOrUpdatePartial subscription {}", subscriptionDto);
        if (existedSubscriptionDto == null) {
            create(subscriptionDto);

        } else if (!StringUtils.isBlank(subscriptionDto.getTerminationDate())) {
            TerminateSubscriptionRequestDto terminateSubscriptionDto = new TerminateSubscriptionRequestDto();
            terminateSubscriptionDto.setSubscriptionCode(subscriptionDto.getCode());
            terminateSubscriptionDto.setTerminationDate(subscriptionDto.getTerminationDate());
            terminateSubscriptionDto.setTerminationReason(subscriptionDto.getTerminationReason());
            terminateSubscriptionDto.setOrderItemId(orderItemId);
            terminateSubscriptionDto.setOrderItemAction(orderItemAction);
            terminateSubscription(terminateSubscriptionDto, orderNumber != null ? orderNumber : existedSubscriptionDto.getOrderNumber());
            return;

        } else {

            if (!StringUtils.isBlank(subscriptionDto.getUserAccount())) {
                existedSubscriptionDto.setUserAccount(subscriptionDto.getUserAccount());
            }

            if (!StringUtils.isBlank(subscriptionDto.getOfferTemplate())) {
                existedSubscriptionDto.setOfferTemplate(subscriptionDto.getOfferTemplate());
            }

            if (!StringUtils.isBlank(subscriptionDto.getDescription())) {
                existedSubscriptionDto.setDescription(subscriptionDto.getDescription());
            }
            if (!StringUtils.isBlank(subscriptionDto.getSubscriptionDate())) {
                existedSubscriptionDto.setSubscriptionDate(subscriptionDto.getSubscriptionDate());
            }

            if (!StringUtils.isBlank(subscriptionDto.getEndAgreementDate())) {
                existedSubscriptionDto.setEndAgreementDate(subscriptionDto.getEndAgreementDate());
            }

            if (subscriptionDto.getCustomFields() != null && !subscriptionDto.getCustomFields().isEmpty()) {
                existedSubscriptionDto.setCustomFields(subscriptionDto.getCustomFields());
            }

            if (subscriptionDto.getRenewalRule() != null) {
                existedSubscriptionDto.setRenewalRule(subscriptionDto.getRenewalRule());
            }
            
            if (subscriptionDto.getAutoEndOfEngagement() != null) {
                existedSubscriptionDto.setAutoEndOfEngagement(subscriptionDto.getAutoEndOfEngagement());
            }

            update(existedSubscriptionDto);
        }

        // accesses
        if (subscriptionDto.getAccesses() != null) {
            for (AccessDto accessDto : subscriptionDto.getAccesses().getAccess()) {
                if (StringUtils.isBlank(accessDto.getCode())) {
                    log.warn("code is null={}", accessDto);
                    continue;
                }
                if (!StringUtils.isBlank(accessDto.getSubscription()) && !accessDto.getSubscription().equalsIgnoreCase(subscriptionDto.getCode())) {
                    throw new MeveoApiException("Access's subscription " + accessDto.getSubscription() + " doesn't match with parent subscription " + subscriptionDto.getCode());
                } else {
                    accessDto.setSubscription(subscriptionDto.getCode());
                }
                accessApi.createOrUpdatePartial(accessDto);
            }
        }

        // Update, instantiate, activate or terminate services
        if (subscriptionDto.getServices() != null && subscriptionDto.getServices().getServiceInstance() != null) {

            InstantiateServicesRequestDto instantiateServicesDto = new InstantiateServicesRequestDto();
            instantiateServicesDto.setSubscription(subscriptionDto.getCode());
            instantiateServicesDto.setOrderNumber(orderNumber);
            instantiateServicesDto.setOrderItemId(orderItemId);
            instantiateServicesDto.setOrderItemAction(orderItemAction);
            List<ServiceToInstantiateDto> serviceToInstantiates = instantiateServicesDto.getServicesToInstantiate().getService();

            ActivateServicesRequestDto activateServicesDto = new ActivateServicesRequestDto();
            activateServicesDto.setSubscription(subscriptionDto.getCode());
            activateServicesDto.setOrderNumber(orderNumber);
            activateServicesDto.setOrderItemId(orderItemId);
            activateServicesDto.setOrderItemAction(orderItemAction);

            UpdateServicesRequestDto updateServicesRequestDto = new UpdateServicesRequestDto();
            updateServicesRequestDto.setSubscriptionCode(subscriptionDto.getCode());
            updateServicesRequestDto.setOrderNumber(orderNumber);
            updateServicesRequestDto.setOrderItemId(orderItemId);
            updateServicesRequestDto.setOrderItemAction(orderItemAction);

            List<TerminateSubscriptionServicesRequestDto> servicesToTerminate = new ArrayList<>();

            for (ServiceInstanceDto serviceInstanceDto : subscriptionDto.getServices().getServiceInstance()) {

                // Service will be terminated
                if (serviceInstanceDto.getTerminationDate() != null) {

                    if (StringUtils.isBlank(serviceInstanceDto.getCode()) && serviceInstanceDto.getId() == null) {
                        log.warn("code or ID is null={}", serviceInstanceDto);
                        continue;
                    }

                    TerminateSubscriptionServicesRequestDto terminateServiceDto = new TerminateSubscriptionServicesRequestDto();
                    if (!StringUtils.isBlank(serviceInstanceDto.getCode())) {
                        terminateServiceDto.addServiceCode(serviceInstanceDto.getCode());
                    }
                    if (serviceInstanceDto.getId() != null) {
                        terminateServiceDto.addServiceId(serviceInstanceDto.getId());
                    }
                    terminateServiceDto.setSubscriptionCode(subscriptionDto.getCode());
                    terminateServiceDto.setTerminationDate(serviceInstanceDto.getTerminationDate());
                    terminateServiceDto.setTerminationReason(serviceInstanceDto.getTerminationReason());
                    terminateServiceDto.setOrderNumber(orderNumber != null ? orderNumber : serviceInstanceDto.getOrderNumber());
                    terminateServiceDto.setOrderItemId(orderItemId);
                    terminateServiceDto.setAction(orderItemAction);
                    servicesToTerminate.add(terminateServiceDto);

                    // Service will be updated
                } else if (serviceInstanceDto.getId() != null) {

                    ServiceToUpdateDto serviceToUpdate = new ServiceToUpdateDto();
                    serviceToUpdate.setId(serviceInstanceDto.getId());
                    serviceToUpdate.setCustomFields(serviceInstanceDto.getCustomFields());
                    if (serviceInstanceDto.getEndAgreementDate() != null) {
                        serviceToUpdate.setEndAgreementDate(serviceInstanceDto.getEndAgreementDate());
                    }

                    updateServicesRequestDto.addService(serviceToUpdate);

                    // Service will be instantiated or activated
                } else {

                    if (StringUtils.isBlank(serviceInstanceDto.getCode())) {
                        log.warn("code is null={}", serviceInstanceDto);
                        continue;
                    }

                    // Service will be instantiated
                    if (StringUtils.isBlank(serviceInstanceDto.getSubscriptionDate())) {// instance service in sub's

                        ServiceToInstantiateDto serviceToInstantiate = new ServiceToInstantiateDto();
                        serviceToInstantiate.setCode(serviceInstanceDto.getCode());
                        serviceToInstantiate.setQuantity(serviceInstanceDto.getQuantity());
                        serviceToInstantiate.setCustomFields(serviceInstanceDto.getCustomFields());
                        serviceToInstantiate.setRateUntilDate(serviceInstanceDto.getRateUntilDate());
                        serviceToInstantiates.add(serviceToInstantiate);

                        // Service will be activated
                    } else {

                        ServiceToActivateDto serviceToActivateDto = new ServiceToActivateDto();
                        serviceToActivateDto.setCode(serviceInstanceDto.getCode());
                        serviceToActivateDto.setSubscriptionDate(serviceInstanceDto.getSubscriptionDate());
                        serviceToActivateDto.setQuantity(serviceInstanceDto.getQuantity());
                        serviceToActivateDto.setCustomFields(serviceInstanceDto.getCustomFields());
                        serviceToActivateDto.setRateUntilDate(serviceInstanceDto.getRateUntilDate());
                        activateServicesDto.getServicesToActivateDto().addService(serviceToActivateDto);
                    }
                }
            }

            if (!serviceToInstantiates.isEmpty()) {
                instantiateServices(instantiateServicesDto);
            }

            if (activateServicesDto.getServicesToActivateDto().getService() != null && !activateServicesDto.getServicesToActivateDto().getService().isEmpty()) {
                activateServices(activateServicesDto);
            }

            // Update services
            if (updateServicesRequestDto.getServicesToUpdate() != null && !updateServicesRequestDto.getServicesToUpdate().isEmpty()) {
                updateServiceInstance(updateServicesRequestDto);
            }

            // Terminate services
            if (!servicesToTerminate.isEmpty()) {
                for (TerminateSubscriptionServicesRequestDto terminationServiceDto : servicesToTerminate) {
                    terminateServices(terminationServiceDto);
                }
            }
        }

        // Instantiate products
        if (subscriptionDto.getProducts() != null) {
            for (ProductDto productDto : subscriptionDto.getProducts().getProducts()) {
                if (StringUtils.isBlank(productDto.getCode())) {
                    log.warn("code is null={}", productDto);
                    continue;
                }
                ApplyProductRequestDto dto = new ApplyProductRequestDto(productDto);
                applyProduct(dto);
            }
        }
    }

    /**
     * Suspend subscription
     * @param subscriptionCode subscription code
     * @param suspensionDate suspension date
     * @throws MissingParameterException Missing parameter exception
     * @throws EntityDoesNotExistsException Entity does not exists exception
     * @throws IncorrectSusbcriptionException Incorrect susbcription exception
     * @throws IncorrectServiceInstanceException Incorrect service instance exception
     * @throws BusinessException Business exception
     */
    public void suspendSubscription(String subscriptionCode, Date suspensionDate)
            throws MissingParameterException, EntityDoesNotExistsException, IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
            handleMissingParameters();
        }
        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }
        subscriptionService.subscriptionSuspension(subscription, suspensionDate);
    }

    /**
     * Resume subscription
     * @param subscriptionCode subscription code
     * @param suspensionDate suspension data
     * @throws MissingParameterException missiong parameter exeption
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrection service isntance exception
     * @throws BusinessException business exception.
     */
    public void resumeSubscription(String subscriptionCode, Date suspensionDate)
            throws MissingParameterException, EntityDoesNotExistsException, IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
            handleMissingParameters();
        }
        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }
        subscriptionService.subscriptionReactivation(subscription, suspensionDate);
    }

    /**
     * Suspend services
     * @param provisionningServicesRequestDto provisioning service request.
     *
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     */
    public void suspendServices(OperationServicesRequestDto provisionningServicesRequestDto)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException, MeveoApiException {
        suspendOrResumeServices(provisionningServicesRequestDto, true);
    }

    /**
     * Resume services
     * @param provisionningServicesRequestDto provisioning service request.
     *
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     */
    public void resumeServices(OperationServicesRequestDto provisionningServicesRequestDto)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException, MeveoApiException {
        suspendOrResumeServices(provisionningServicesRequestDto, false);
    }

    /**
     * Suspend or resume services
     * 
     * @param postData operation serivices request
     * @param isToSuspend true if it is to be suspended.
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     */
    private void suspendOrResumeServices(OperationServicesRequestDto postData, boolean isToSuspend)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException, MeveoApiException {
        String subscriptionCode = null;
        if (postData == null) {
            missingParameters.add("provisionningServicesRequestDto");
        } else {
            subscriptionCode = postData.getSubscriptionCode();
        }

        if (postData != null && StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        handleMissingParametersAndValidate(postData);

        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        if (postData != null) {
            for (ServiceToUpdateDto serviceToSuspendDto : postData.getServicesToUpdate()) {
                ServiceInstance serviceInstanceToSuspend = getSingleServiceInstance(serviceToSuspendDto.getId(), serviceToSuspendDto.getCode(), subscription,
                    isToSuspend ? InstanceStatusEnum.ACTIVE : InstanceStatusEnum.SUSPENDED);

                if (isToSuspend) {
                    serviceInstanceService.serviceSuspension(serviceInstanceToSuspend, serviceToSuspendDto.getActionDate());
                } else {
                    serviceInstanceService.serviceReactivation(serviceInstanceToSuspend, serviceToSuspendDto.getActionDate());
                }
            }
        }
    }

    public void updateServiceInstance(UpdateServicesRequestDto postData) throws MeveoApiException, BusinessException {
        if (postData.getServicesToUpdate() == null) {
            missingParameters.add("servicesToUpdate");
        }
        if (StringUtils.isBlank(postData.getSubscriptionCode())) {
            missingParameters.add("subscriptionCode");
        }

        handleMissingParametersAndValidate(postData);

        Subscription subscription = subscriptionService.findByCode(postData.getSubscriptionCode());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscriptionCode());
        }

        for (ServiceToUpdateDto serviceToUpdateDto : postData.getServicesToUpdate()) {

            ServiceInstance serviceToUpdate = getSingleServiceInstance(serviceToUpdateDto.getId(), serviceToUpdateDto.getCode(), subscription, InstanceStatusEnum.ACTIVE,
                InstanceStatusEnum.INACTIVE);

            if (serviceToUpdateDto.getEndAgreementDate() != null) {
                serviceToUpdate.setEndAgreementDate(serviceToUpdateDto.getEndAgreementDate());
            }

            if (!StringUtils.isBlank(serviceToUpdateDto.getDescription())) {
                serviceToUpdate.setDescription(serviceToUpdateDto.getDescription());
            }

            if (serviceToUpdateDto.getQuantity() != null) {
                serviceToUpdate.setQuantity(serviceToUpdateDto.getQuantity());
            }

            // populate customFields
            try {
                populateCustomFields(serviceToUpdateDto.getCustomFields(), serviceToUpdate, false);
            } catch (MissingParameterException | InvalidParameterException e) {
                log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw e;
            }

            serviceInstanceService.update(serviceToUpdate);
        }
    }

    public ServiceInstanceDto findServiceInstance(String subscriptionCode, Long serviceInstanceId, String serviceInstanceCode) throws MeveoApiException {
        ServiceInstanceDto result = new ServiceInstanceDto();

        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        ServiceInstance serviceInstance = getSingleServiceInstance(serviceInstanceId, serviceInstanceCode, subscription);
        if (serviceInstance != null) {
            result = new ServiceInstanceDto(serviceInstance, entityToDtoConverter.getCustomFieldsDTO(serviceInstance, true));
        }

        return result;
    }

    public DueDateDelayDto getDueDateDelay(String subscriptionCode, String invoiceNumber, String invoiceTypeCode, String orderCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        handleMissingParameters();

        DueDateDelayDto result = new DueDateDelayDto();

        Invoice invoice = null;
        if (!StringUtils.isBlank(invoiceNumber) && !StringUtils.isBlank(invoiceTypeCode)) {
            InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
            if (invoiceType != null) {
                invoice = invoiceService.findByInvoiceNumberAndType(invoiceNumber, invoiceType);
            }
        }

        Order order = null;
        if (!StringUtils.isBlank(orderCode)) {
            order = orderService.findByCode(orderCode);
        }

        BillingAccount billingAccount = subscription.getUserAccount().getBillingAccount();
        BillingCycle billingCycle = billingAccount.getBillingCycle();

        Integer delay = billingCycle.getDueDateDelay();
        String delayEL = billingCycle.getDueDateDelayEL();
        DueDateDelayEnum delayOrigin = DueDateDelayEnum.BC;
        if (order != null && !StringUtils.isBlank(order.getDueDateDelayEL())) {
            delay = invoiceService.evaluateIntegerExpression(order.getDueDateDelayEL(), billingAccount, invoice, order);
            delayEL = order.getDueDateDelayEL();
            delayOrigin = DueDateDelayEnum.ORDER;
        } else {
            if (!StringUtils.isBlank(billingAccount.getCustomerAccount().getDueDateDelayEL())) {
                delay = invoiceService.evaluateIntegerExpression(billingAccount.getCustomerAccount().getDueDateDelayEL(), billingAccount, invoice, null);
                delayEL = billingAccount.getCustomerAccount().getDueDateDelayEL();
                delayOrigin = DueDateDelayEnum.CA;
            } else if (!StringUtils.isBlank(billingCycle.getDueDateDelayEL())) {
                delay = invoiceService.evaluateIntegerExpression(billingCycle.getDueDateDelayEL(), billingAccount, invoice, null);
                delayEL = billingCycle.getDueDateDelayEL();
                delayOrigin = DueDateDelayEnum.ORDER;
            }
        }

        result.setComputedDelay(delay);
        result.setDelayEL(delayEL);
        result.setDelayOrigin(delayOrigin);

        return result;
    }

    /**
     * @param type of one shot charge (SUBSCRIPTION, TERMINATION, OTHER)
     * @return list of one shot charge template type other.s
     *
     */
    private List<OneShotChargeTemplateDto> getOneShotCharges(OneShotChargeTemplateTypeEnum type) {
        List<OneShotChargeTemplateDto> results = new ArrayList<OneShotChargeTemplateDto>();
        if (oneShotChargeTemplateService == null) {
            return results;
        }

        List<OneShotChargeTemplate> list = oneShotChargeTemplateService.list();
        for (OneShotChargeTemplate chargeTemplate : list) {
            if (chargeTemplate.getOneShotChargeTemplateType() == type) {
                OneShotChargeTemplateDto oneshotChartTemplateDto = new OneShotChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate, true));
                results.add(oneshotChartTemplateDto);
            }
        }

        return results;
    }

    /**
     * @return list of one shot charge others.
     */
    public List<OneShotChargeTemplateDto> getOneShotChargeOthers() {
        return this.getOneShotCharges(OneShotChargeTemplateTypeEnum.OTHER);
    }

    /**
     * Convert SubscriptionRenewalDto to an entity and validate the data converted.
     *
     * @param renewalInfo Entity object to populate. Will instantiate a new object if null.
     * @param renewalInfoDto SubscriptionRenewalDto
     * @param subscriptionRenewed is subscription renewed already. If true, allows to update only daysNotifyRenewal, endOfTermAction, extendAgreementPeriod fields.
     * @return Entity object populated or instantiated
     * @throws InvalidParameterException invalid parameter exception
     * @throws MissingParameterException missing parameter exception
     */
    public SubscriptionRenewal subscriptionRenewalFromDto(SubscriptionRenewal renewalInfo, SubscriptionRenewalDto renewalInfoDto, boolean subscriptionRenewed)
            throws InvalidParameterException, MissingParameterException {

        if (renewalInfo == null) {
            renewalInfo = new SubscriptionRenewal();
        }

        if (renewalInfoDto == null) {
            return renewalInfo;
        }

        if (!subscriptionRenewed) {
            renewalInfo.setInitialyActiveFor(renewalInfoDto.getInitialyActiveFor());
            renewalInfo.setInitialyActiveForUnit(renewalInfoDto.getInitialyActiveForUnit());
            renewalInfo.setRenewFor(renewalInfoDto.getRenewFor());
            renewalInfo.setRenewForUnit(renewalInfoDto.getRenewForUnit());
        }

        renewalInfo.setAutoRenew(renewalInfoDto.isAutoRenew());
        renewalInfo.setDaysNotifyRenewal(renewalInfoDto.getDaysNotifyRenewal());
        renewalInfo.setEndOfTermAction(renewalInfoDto.getEndOfTermAction());
        renewalInfo.setExtendAgreementPeriodToSubscribedTillDate(renewalInfoDto.isExtendAgreementPeriodToSubscribedTillDate());
        if (renewalInfoDto.getTerminationReasonCode() != null) {
            SubscriptionTerminationReason terminationReason = terminationReasonService.findByCode(renewalInfoDto.getTerminationReasonCode());
            if (terminationReason == null) {
                throw new InvalidParameterException("renewalInfo/terminationReason");
            }
            renewalInfo.setTerminationReason(terminationReason);
        }

        if (renewalInfo.getInitialyActiveFor() != null) {

            List<String> missingFields = new ArrayList<>();

            if (renewalInfo.getInitialyActiveForUnit() == null) {
                missingFields.add("renewalInfo/initillyActiveForUnit");
            }

            if (renewalInfo.getEndOfTermAction() == null) {
                missingFields.add("renewalInfo/endOfTermAction");
            }

            if (renewalInfo.isAutoRenew()) {
                if (renewalInfo.getRenewFor() == null) {
                    missingFields.add("renewalInfo/renewFor");
                }
                if (renewalInfo.getRenewForUnit() == null) {
                    missingFields.add("renewalInfo/renewForUnit");
                }
            }
            if (renewalInfo.getEndOfTermAction() == EndOfTermActionEnum.TERMINATE && renewalInfo.getTerminationReason() == null) {
                missingFields.add("renewalInfo/terminationReason");
            }

            if (!missingFields.isEmpty()) {
                throw new MissingParameterException(missingFields);
            }
        }

        return renewalInfo;
    }

    /**
     * Find a service instance matching id or code for a given subscription and optional statuses. I
     *
     * @param serviceId Service instance id
     * @param serviceCode Service instance code
     * @param subscription Subscription containing service instance
     * @param statuses Statuses to match (optional)
     * @return Service instance matched
     * @throws MissingParameterException Either serviceId or serviceCode value must be provided
     * @throws EntityDoesNotExistsException Service instance was not matched
     * @throws InvalidParameterException More than one matching service instance found or does not correspond to given subscription and/or statuses
     */
    private ServiceInstance getSingleServiceInstance(Long serviceId, String serviceCode, Subscription subscription, InstanceStatusEnum... statuses)
            throws MissingParameterException, EntityDoesNotExistsException, InvalidParameterException {

        ServiceInstance serviceInstance = null;
        if (serviceId != null) {
            serviceInstance = serviceInstanceService.findById(serviceId);

            if (serviceInstance == null) {
                throw new EntityDoesNotExistsException(ServiceInstance.class, serviceId);

            } else if (!serviceInstance.getSubscription().equals(subscription)
                    || (statuses != null && statuses.length > 0 && !ArrayUtils.contains(statuses, serviceInstance.getStatus()))) {
                throw new InvalidParameterException("Service instance id " + serviceId + " does not correspond to subscription " + subscription.getCode() + " or is not of status ["
                        + (statuses != null ? StringUtils.concatenate((Object[]) statuses) : "") + "]");
            }

        } else if (!StringUtils.isBlank(serviceCode)) {
            List<ServiceInstance> services = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceCode, subscription, statuses);
            if (services.size() == 1) {
                serviceInstance = services.get(0);
            } else if (services.size() > 1) {
                throw new InvalidParameterException("More than one service instance with status [" + (statuses != null ? StringUtils.concatenate((Object[]) statuses) : "")
                        + "] was found. Please use ID to refer to service instance.");
            } else {
                throw new EntityDoesNotExistsException("Service instance with code " + serviceCode + " was not found or is not of status ["
                        + (statuses != null ? StringUtils.concatenate((Object[]) statuses) : "") + "]");
            }

        } else {
            throw new MissingParameterException("service id or code");
        }
        return serviceInstance;
    }

    public List<ServiceInstanceDto> listServiceInstance(String subscriptionCode, String serviceInstanceCode) throws MissingParameterException {
        List<ServiceInstanceDto> result = new ArrayList<>();

        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        if (StringUtils.isBlank(serviceInstanceCode)) {
            missingParameters.add("serviceInstanceCode");
        }

        handleMissingParameters();

        List<ServiceInstance> serviceInstances = serviceInstanceService.listServiceInstance(subscriptionCode, serviceInstanceCode);
        if (serviceInstances != null && !serviceInstances.isEmpty()) {
            result = serviceInstances.stream().map(p -> new ServiceInstanceDto(p, entityToDtoConverter.getCustomFieldsDTO(p, true))).collect(Collectors.toList());
        }

        return result;
    }

    
    /**
     * Rate subscription.
     *
     * @param postData the post data
     * @return instance of RateSubscriptionResponseDto.
     * @throws BusinessException the business exception
     * @throws MissingParameterException the missing parameter exception
     * @throws InvalidParameterException invalid parameter exception
     */
    public RateSubscriptionResponseDto rateSubscription(RateSubscriptionRequestDto postData) throws BusinessException, MissingParameterException, InvalidParameterException {

        String subscriptionCode = postData.getSubscriptionCode();
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }
        Date today = new Date();
        if (postData.getRateUntilDate() == null) {
            postData.setRateUntilDate(today);
        }
        Date rateUntillDate = postData.getRateUntilDate();
        if (today.after(rateUntillDate)) {
            throw new InvalidParameterException("rateUntilDate", String.valueOf(rateUntillDate));
        }

        handleMissingParameters();

        RateSubscriptionResponseDto result = new RateSubscriptionResponseDto();

        // Recurring charges :
        List<Long> activeRecurringChargeIds = recurringChargeInstanceService.findIdsByStatusAndSubscriptionCode(InstanceStatusEnum.ACTIVE, rateUntillDate, subscriptionCode, false);
        for (Long chargeId : activeRecurringChargeIds) {
            int nbRating = recurringChargeInstanceService.applyRecurringCharge(chargeId, rateUntillDate).getNbRating();
            result.addResult(chargeId, nbRating);
        }
        return result;
    }
}