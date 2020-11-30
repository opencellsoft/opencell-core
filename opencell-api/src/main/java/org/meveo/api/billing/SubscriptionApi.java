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

package org.meveo.api.billing;

import org.apache.commons.lang3.ArrayUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.account.AccessApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.billing.*;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.RateSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.EntityNotAllowedException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.filter.ObjectFilter;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.VersionCreated;
import org.meveo.event.qualifier.VersionRemoved;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.DueDateDelayEnum;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.DiscountPlanInstanceService;
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
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.PaymentMethodService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static org.meveo.commons.utils.StringUtils.isNotBlank;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Wassim Drira
 * @author Said Ramli
 * @author Mohamed El Youssoufi
 * @author Youssef IZEM
 * @author Mounir BAHIJE
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.2.2
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
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

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private SellerService sellerService;

    @Inject
    private CustomerService customerService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private DiscountPlanInstanceService discountPlanInstanceService;

    @Inject
    private DiscountPlanService discountPlanService;

    @Inject
    private PaymentMethodService paymentMethodService;

    private ParamBean paramBean = ParamBean.getInstance();

    @Inject
    private EmailTemplateService emailTemplateService;

    @Inject
    @VersionCreated
    private Event<Subscription> versionCreatedEvent;

    @Inject
    @VersionRemoved
    private Event<Subscription> versionRemovedEvent;

    private void setRenewalTermination(SubscriptionRenewal renewal, String terminationReason) throws EntityDoesNotExistsException {
        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(terminationReason);
        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, terminationReason);
        }
        renewal.setTerminationReason(subscriptionTerminationReason);
        renewal.setInitialTermType(SubscriptionRenewal.InitialTermTypeEnum.FIXED);
        renewal.setAutoRenew(false);
        renewal.setEndOfTermAction(EndOfTermActionEnum.TERMINATE);
    }

    private void setSubscriptionFutureTermination(SubscriptionDto postData, Subscription subscription) throws EntityDoesNotExistsException {
        if (postData.getTerminationDate() != null && postData.getTerminationDate().compareTo(new Date()) > 0 && !StringUtils.isBlank(postData.getTerminationReason())) {

            subscription.setTerminationDate(postData.getTerminationDate());
            subscription.setSubscribedTillDate(postData.getTerminationDate());
            setRenewalTermination(subscription.getSubscriptionRenewal(), postData.getTerminationReason());
        }
    }

    private void setServiceFutureTermination(ServiceToUpdateDto serviceToUpdateDto, ServiceInstance serviceInstance) throws EntityDoesNotExistsException {
        if (serviceToUpdateDto.getTerminationDate() != null && serviceToUpdateDto.getTerminationDate().compareTo(new Date()) > 0 && !StringUtils.isBlank(serviceToUpdateDto.getTerminationReason())) {

            serviceInstance.setTerminationDate(serviceToUpdateDto.getTerminationDate());
            serviceInstance.setSubscribedTillDate(serviceToUpdateDto.getTerminationDate());
            setRenewalTermination(serviceInstance.getServiceRenewal(), serviceToUpdateDto.getTerminationReason());
        }
    }

    /**
     * v5.0 admin parameter to authorize/bare the multiactivation of an instantiated service
     *
     * @param postData The subscription dto
     * @return the subscription
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public Subscription create(SubscriptionDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getUserAccount())) {
            missingParameters.add("userAccount");
        }
        if (StringUtils.isBlank(postData.getOfferTemplate())) {
            missingParameters.add("offerTemplate");
        }
        handleMissingParameters(postData);
        return createSubscription(postData);
    }

    /**
     *
     * @param postData
     * @param subscription
     * @throws EntityDoesNotExistsException
     */
    private void populateElectronicBillingFields(SubscriptionDto postData, Subscription subscription) throws EntityDoesNotExistsException {
        MailingTypeEnum mailingType = null;
        if (postData.getMailingType() != null) {
            mailingType = MailingTypeEnum.getByLabel(postData.getMailingType());
        }

        EmailTemplate emailTemplate = null;
        if (postData.getEmailTemplate() != null) {
            emailTemplate = emailTemplateService.findByCode(postData.getEmailTemplate());
            if (emailTemplate == null) {
                throw new EntityDoesNotExistsException(EmailTemplate.class, postData.getEmailTemplate());
            }
        }
        if (postData.getElectronicBilling() == null) {
            subscription.setElectronicBilling(false);
        } else {
            subscription.setElectronicBilling(postData.getElectronicBilling());
        }
        subscription.setEmail(postData.getEmail());
        subscription.setMailingType(mailingType);
        subscription.setEmailTemplate(emailTemplate);
        subscription.setCcedEmails(postData.getCcedEmails());
    }

    /**
     * v5.0 admin parameter to authorize/bare the multiactivation of an instantiated service
     *
     * @param postData subscription Dto
     * @return the subscription
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public Subscription update(SubscriptionDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getSubscriptionDate())) {
            missingParameters.add("subscriptionDate");
        }
        if (postData.getElectronicBilling() != null && postData.getElectronicBilling()) {
            if (StringUtils.isBlank(postData.getEmail())) {
                missingParameters.add("email");
            }
            if (postData.getMailingType() != null && StringUtils.isBlank(postData.getEmailTemplate())) {
                missingParameters.add("emailTemplate");
            }
        }

        handleMissingParametersAndValidate(postData);

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(postData.getCode(), postData.getValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getCode(), postData.getValidityDate());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
            throw new MeveoApiException("Subscription is already RESILIATED.");
        }

        if (!StringUtils.isBlank(postData.getUserAccount())) {
            UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount());
            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
            } else if (!subscription.getUserAccount().equals(userAccount)) {
                throw new InvalidParameterException("Can not change the parent account. Subscription's current parent account (user account) is " + subscription.getUserAccount().getCode());
            }
            subscription.setUserAccount(userAccount);
        }

        if (postData.getOfferTemplate() != null) {
            OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), postData.getSubscriptionDate());
            if (offerTemplate == null) {
                throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate() + " / " + DateUtils.formatDateWithPattern(postData.getSubscriptionDate(), paramBean.getDateTimeFormat()));
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

        if (isNotBlank(postData.getSeller())) {
            Seller seller = sellerService.findByCode(postData.getSeller());
            if (seller == null)
                throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
            subscription.setSeller(seller);
        }

        MailingTypeEnum mailingType = null;
        if (postData.getMailingType() != null) {
            mailingType = MailingTypeEnum.valueOf(postData.getMailingType());
        }

        EmailTemplate emailTemplate = null;
        if (postData.getEmailTemplate() != null) {
            emailTemplate = emailTemplateService.findByCode(postData.getEmailTemplate());
            if (emailTemplate == null) {
                throw new EntityDoesNotExistsException(EmailTemplate.class, postData.getEmailTemplate());
            }
        }

        subscription.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        subscription.setDescription(postData.getDescription());
        subscription.setSubscriptionDate(postData.getSubscriptionDate());
        // subscription.setTerminationDate(postData.getTerminationDate());

        SubscriptionRenewal subscriptionRenewal = subscriptionRenewalFromDto(subscription.getSubscriptionRenewal(), postData.getRenewalRule(), subscription.isRenewed());
        subscription.setSubscriptionRenewal(subscriptionRenewal);

        setSubscriptionFutureTermination(postData, subscription);

        if (!StringUtils.isBlank(postData.getMinimumChargeTemplate())) {
            OneShotChargeTemplate minimumChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getMinimumChargeTemplate());
            if (minimumChargeTemplate == null) {
                throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getMinimumChargeTemplate());
            } else {
                subscription.setMinimumChargeTemplate(minimumChargeTemplate);
            }
        }

        if (Objects.nonNull(postData.getPaymentMethod())) {
            PaymentMethod paymentMethod = paymentMethodService.findById(postData.getPaymentMethod().getId());
            if (paymentMethod == null) {
                throw new EntityNotFoundException("payment method not found!");
            }
            subscription.setPaymentMethod(paymentMethod);
        }

        if (postData.getMinimumAmountEl() != null) {
            subscription.setMinimumAmountEl(postData.getMinimumAmountEl());
        }
        if (postData.getMinimumAmountElSpark() != null) {
            subscription.setMinimumAmountElSpark(postData.getMinimumAmountElSpark());
        }
        if (postData.getMinimumLabelEl() != null) {
            subscription.setMinimumLabelEl(postData.getMinimumLabelEl());
        }
        if (postData.getMinimumLabelElSpark() != null) {
            subscription.setMinimumLabelElSpark(postData.getMinimumLabelElSpark());
        }
        if (postData.getAutoEndOfEngagement() != null) {
            subscription.setAutoEndOfEngagement(postData.getAutoEndOfEngagement());
            subscriptionService.updateSubscribedTillAndRenewalNotifyDates(subscription);
        }
        if (postData.getRatingGroup() != null) {
            subscription.setRatingGroup(postData.getRatingGroup());
        }

        if (postData.getElectronicBilling() != null) {
            subscription.setElectronicBilling(postData.getElectronicBilling());
        }
        if (postData.getEmail() != null) {
            subscription.setEmail(postData.getEmail());
        }
        if (mailingType != null) {
            subscription.setMailingType(mailingType);
        }
        if (emailTemplate != null) {
            subscription.setEmailTemplate(emailTemplate);
        }
        if (postData.getCcedEmails() != null) {
            subscription.setCcedEmails(postData.getCcedEmails());
        }
        if (subscription.getElectronicBilling() && subscription.getEmail() == null) {
            missingParameters.add("email");
            if (subscription.getMailingType() != null && subscription.getEmailTemplate() == null) {
                missingParameters.add("emailTemplate");
            }
            handleMissingParameters();
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
        // terminate discounts
        if (postData.getDiscountPlansForTermination() != null) {
            for (String dpiCode : postData.getDiscountPlansForTermination()) {
                DiscountPlanInstance dpi = discountPlanInstanceService.findBySubscriptionAndCode(subscription, dpiCode);
                if (dpi == null) {
                    throw new EntityDoesNotExistsException(DiscountPlanInstance.class, dpiCode);
                }
                subscriptionService.terminateDiscountPlan(subscription, dpi);
            }
        }

        // instantiate the discounts
        if (postData.getDiscountPlansForInstantiation() != null) {
            for (DiscountPlanDto discountPlanDto : postData.getDiscountPlansForInstantiation()) {
                DiscountPlan dp = discountPlanService.findByCode(discountPlanDto.getCode());
                if (dp == null) {
                    throw new EntityDoesNotExistsException(DiscountPlan.class, discountPlanDto.getCode());
                }

                discountPlanService.detach(dp);
                dp = DiscountPlanDto.copyFromDto(discountPlanDto, dp);

                // populate customFields
                try {
                    populateCustomFields(discountPlanDto.getCustomFields(), dp, false);
                } catch (MissingParameterException | InvalidParameterException e) {
                    log.error("Failed to associate custom field instance to an entity: {} {}", discountPlanDto.getCode(), e.getMessage());
                    throw e;
                } catch (Exception e) {
                    log.error("Failed to associate custom field instance to an entity {}", discountPlanDto.getCode(), e);
                    throw new MeveoApiException("Failed to associate custom field instance to an entity " + discountPlanDto.getCode());
                }

                subscriptionService.instantiateDiscountPlan(subscription, dp);
            }

        }

        return subscription;
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
        if (activateServicesDto.getServicesToActivateDto() == null || activateServicesDto.getServicesToActivateDto().getService() == null || activateServicesDto.getServicesToActivateDto().getService().size() == 0) {
            missingParameters.add("services");
        }

        handleMissingParametersAndValidate(activateServicesDto);

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(activateServicesDto.getSubscription(), activateServicesDto.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, activateServicesDto.getSubscription(), activateServicesDto.getSubscriptionValidityDate());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
        }

        activateServices(activateServicesDto.getServicesToActivateDto(), subscription, activateServicesDto.getOrderNumber(), activateServicesDto.getOrderItemId(), activateServicesDto.getOrderItemAction());
    }

    private void activateServices(ServicesToActivateDto servicesToActivate, Subscription subscription, String orderNumber, Long orderItemId, OrderItemActionEnum orderItemAction) {
        List<ServiceTemplate> serviceToActivate = new ArrayList<>();
        List<ServiceToActivateDto> servicesToActivateDto = new ArrayList<>();
        getServiceToActivate(servicesToActivate.getService(), serviceToActivate, servicesToActivateDto);
        subscriptionService.checkCompatibilityOfferServices(subscription, serviceToActivate);

        // Find instantiated or instantiate if not instantiated yet
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        for (ServiceToActivateDto serviceToActivateDto : servicesToActivate.getService()) {
            if (StringUtils.isBlank(serviceToActivateDto.getSubscriptionDate())) {
                missingParameters.add("SubscriptionDate");
                handleMissingParameters();
            }

            // ServiceTemplate serviceTemplate = serviceToActivateDto.getServiceTemplate();

            ServiceInstance serviceInstance = null;

            if (paramBean.isServiceMultiInstantiation()) {
                List<ServiceInstance> alreadyInstantiatedServices = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceToActivateDto.getCode(), subscription, InstanceStatusEnum.INACTIVE);
                if (alreadyInstantiatedServices != null && !alreadyInstantiatedServices.isEmpty()) {
                    serviceInstance = alreadyInstantiatedServices.get(0);
                }

            } else {
                List<ServiceInstance> alreadyInstantiatedServices = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceToActivateDto.getCode(), subscription, InstanceStatusEnum.INACTIVE,
                        InstanceStatusEnum.ACTIVE);

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
                log.debug("Found already instantiated service {} of {} for subscription {} quantity {}", serviceInstance.getId(), serviceInstance.getServiceTemplate().getCode(), subscription.getCode(),
                        serviceInstance.getQuantity());
                if (serviceToActivateDto.getOverrideCode() != null) {
                    serviceInstance.setCode(serviceToActivateDto.getOverrideCode());
                }
                if (serviceToActivateDto.getSubscriptionDate() != null) {
                    serviceInstance.setSubscriptionDate(serviceToActivateDto.getSubscriptionDate());
                }
                if (serviceToActivateDto.getQuantity() != null) {
                    serviceInstance.setQuantity(serviceToActivateDto.getQuantity());
                }
                // Do not update existing value
                if (orderNumber != null) {
                    serviceInstance.setOrderNumber(orderNumber);
                }
                if (!StringUtils.isBlank(serviceToActivateDto.getDescription())) {
                    serviceInstance.setDescription(serviceToActivateDto.getDescription());
                }

                if (!StringUtils.isBlank(serviceToActivateDto.getMinimumAmountEl())) {
                    serviceInstance.setMinimumAmountEl(serviceToActivateDto.getMinimumAmountEl());
                }
                if (!StringUtils.isBlank(serviceToActivateDto.getMinimumLabelEl())) {
                    serviceInstance.setMinimumLabelEl(serviceToActivateDto.getMinimumLabelEl());
                }
                if (!StringUtils.isBlank(serviceToActivateDto.getMinimumInvoiceSubCategory())) {
                    InvoiceSubCategory minimumInvoiceSubCategory = invoiceSubCategoryService.findByCode(serviceToActivateDto.getMinimumInvoiceSubCategory());
                    if (minimumInvoiceSubCategory == null) {
                        throw new EntityDoesNotExistsException(InvoiceSubCategory.class, serviceToActivateDto.getMinimumInvoiceSubCategory());
                    } else {
                        serviceInstance.setMinimumInvoiceSubCategory(minimumInvoiceSubCategory);
                    }
                }

                serviceInstances.add(serviceInstance);

                // Instantiate if it was not instantiated earlier
            } else if (serviceInstance == null) {
                ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceToActivateDto.getCode());
                if (serviceTemplate == null) {
                    throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceToActivateDto.getCode());
                }

                log.debug("Will instantiate as part of activation service {} for subscription {} quantity {}", serviceTemplate.getCode(), subscription.getCode(), serviceToActivateDto.getQuantity());

                serviceInstance = new ServiceInstance();
                serviceInstance.setCode(serviceTemplate.getCode());
                if (serviceToActivateDto.getOverrideCode() != null) {
                    serviceInstance.setCode(serviceToActivateDto.getOverrideCode());
                }
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
                serviceInstance.setOrderNumber(orderNumber);
                serviceInstance.setOrderItemId(orderItemId);
                serviceInstance.setOrderItemAction(orderItemAction);
                org.meveo.model.catalog.Calendar calendarPS = null;
                if (!StringUtils.isBlank(serviceToActivateDto.getCalendarPSCode())) {
                    calendarPS = calendarService.findByCode(serviceToActivateDto.getCalendarPSCode());
                    if (calendarPS == null) {
                        throw new EntityDoesNotExistsException(org.meveo.model.catalog.Calendar.class, serviceToActivateDto.getCalendarPSCode());
                    }
                }
                serviceInstance.setPaymentDayInMonthPS(serviceToActivateDto.getPaymentDayInMonthPS());
                serviceInstance.setAmountPS(serviceToActivateDto.getAmountPS());
                serviceInstance.setCalendarPS(calendarPS);

                setMinimumAmountElServiceInstance(serviceToActivateDto, serviceInstance, serviceTemplate);

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
                        ChargeInstance chargeInstance = chargeInstanceService.findByCodeAndSubscription(chargeInstanceOverrideDto.getChargeInstanceCode(), subscription, InstanceStatusEnum.INACTIVE);
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
                serviceInstanceService.serviceActivation(serviceInstance);
            } catch (BusinessException e) {
                log.error("Failed to activate a service {}/{} on subscription {}", serviceInstance.getId(), serviceInstance.getCode(), subscription.getCode(), e);
                throw e;
            }
        }
    }

    private void getServiceToActivate(List<ServiceToActivateDto> services, List<ServiceTemplate> serviceToActivate,
                                      List<ServiceToActivateDto> servicesToActivateDto) {
        for (ServiceToActivateDto serviceToActivateDto : services) {
            if (serviceToActivateDto.getQuantity() == null) {
                throw new MissingParameterException("quantity for service " + serviceToActivateDto.getCode());
            }
            ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceToActivateDto.getCode());
            if (serviceTemplate == null) {
                throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceToActivateDto.getCode());
            }

            serviceToActivateDto.setServiceTemplate(serviceTemplate);
            servicesToActivateDto.add(serviceToActivateDto);
            serviceToActivate.add(serviceTemplate);
        }
    }

    private void setMinimumAmountElServiceInstance(ServiceToActivateDto serviceToActivateDto, ServiceInstance serviceInstance, ServiceTemplate serviceTemplate) {
        serviceInstance.setMinimumAmountEl(serviceTemplate.getMinimumAmountEl());
        serviceInstance.setMinimumLabelEl(serviceTemplate.getMinimumLabelEl());
        serviceInstance.setMinimumInvoiceSubCategory(serviceTemplate.getMinimumInvoiceSubCategory());

        if (!StringUtils.isBlank(serviceToActivateDto.getMinimumAmountEl())) {
            serviceInstance.setMinimumAmountEl(serviceToActivateDto.getMinimumAmountEl());
        }
        if (!StringUtils.isBlank(serviceToActivateDto.getMinimumLabelEl())) {
            serviceInstance.setMinimumLabelEl(serviceToActivateDto.getMinimumLabelEl());
        }
        if (!StringUtils.isBlank(serviceToActivateDto.getMinimumChargeTemplate())) {
            OneShotChargeTemplate minimumChargeTemplate = oneShotChargeTemplateService.findByCode(serviceToActivateDto.getMinimumChargeTemplate());
            if (minimumChargeTemplate == null) {
                throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, serviceToActivateDto.getMinimumChargeTemplate());
            } else {
                serviceInstance.setMinimumChargeTemplate(minimumChargeTemplate);
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

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(instantiateServicesDto.getSubscription(), instantiateServicesDto.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, instantiateServicesDto.getSubscription());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
        }
        List<ServiceToInstantiateDto> serviceToInstantiateDtos = checkCompatibilityAndGetServiceToInstantiate(subscription, instantiateServicesDto.getServicesToInstantiate());

        // instantiate
        for (ServiceToInstantiateDto serviceToInstantiateDto : serviceToInstantiateDtos) {
            instantiateServiceForSubscription(serviceToInstantiateDto, subscription, instantiateServicesDto.getOrderNumber(), instantiateServicesDto.getOrderItemId(), instantiateServicesDto.getOrderItemAction());
        }
    }

    private void instantiateServiceForSubscription(ServiceToInstantiateDto serviceToInstantiateDto, Subscription subscription, String orderNumber, Long orderItemId, OrderItemActionEnum orderItemAction) {
        ServiceTemplate serviceTemplate = serviceToInstantiateDto.getServiceTemplate();

        ServiceInstance serviceInstance = null;

        if (paramBean.isServiceMultiInstantiation()) {
            List<ServiceInstance> subscriptionServiceInstances = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription, InstanceStatusEnum.INACTIVE);
            if (!subscriptionServiceInstances.isEmpty()) {
                throw new MeveoApiException("ServiceInstance with code=" + serviceToInstantiateDto.getCode() + " is already instanciated.");
            }

        } else {
            List<ServiceInstance> subscriptionServiceInstances = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription, InstanceStatusEnum.INACTIVE,
                    InstanceStatusEnum.ACTIVE);
            if (!subscriptionServiceInstances.isEmpty()) {
                throw new MeveoApiException("ServiceInstance with code=" + serviceToInstantiateDto.getCode() + " is already instanciated or activated.");
            }
        }
        log.debug("Will instantiate service {} for subscription {} quantity {}", serviceTemplate.getCode(), subscription.getCode(), serviceToInstantiateDto.getQuantity());

        org.meveo.model.catalog.Calendar calendarPS = null;
        if (!StringUtils.isBlank(serviceToInstantiateDto.getCalendarPSCode())) {
            calendarPS = calendarService.findByCode(serviceToInstantiateDto.getCalendarPSCode());
            if (calendarPS == null) {
                throw new EntityDoesNotExistsException(org.meveo.model.catalog.Calendar.class, serviceToInstantiateDto.getCalendarPSCode());
            }
        }
        serviceInstance = new ServiceInstance();
        serviceInstance.setCode(serviceToInstantiateDto.getOverrideCode());
        if (StringUtils.isBlank(serviceInstance.getCode())) {
            serviceInstance.setCode(serviceTemplate.getCode());
        }
        serviceInstance.setDescription(serviceTemplate.getDescription());
        serviceInstance.setServiceTemplate(serviceTemplate);
        serviceInstance.setSubscription(subscription);
        serviceInstance.setRateUntilDate(serviceToInstantiateDto.getRateUntilDate());
        serviceInstance.setQuantity(serviceToInstantiateDto.getQuantity());
        serviceInstance.setOrderNumber(orderNumber);
        serviceInstance.setOrderItemId(orderItemId);
        serviceInstance.setOrderItemAction(orderItemAction);
        serviceInstance.setAmountPS(serviceToInstantiateDto.getAmountPS());
        serviceInstance.setCalendarPS(calendarPS);

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

    private List<ServiceToInstantiateDto> checkCompatibilityAndGetServiceToInstantiate(Subscription subscription, ServicesToInstantiateDto servicesToInstantiateDto) {
        List<ServiceTemplate> serviceToInstantiates = new ArrayList<>();

        // check if exists
        List<ServiceToInstantiateDto> serviceToInstantiateDtos = new ArrayList<>();
        for (ServiceToInstantiateDto serviceToInstantiateDto : servicesToInstantiateDto.getService()) {
            if (serviceToInstantiateDto.getQuantity() == null) {
                throw new MissingParameterException("quantity for service " + serviceToInstantiateDto.getCode());
            }
            ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceToInstantiateDto.getCode());
            if (serviceTemplate == null) {
                throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceToInstantiateDto.getCode());
            }

            serviceToInstantiateDto.setServiceTemplate(serviceTemplate);
            serviceToInstantiateDtos.add(serviceToInstantiateDto);
            serviceToInstantiates.add(serviceTemplate);
        }

        subscriptionService.checkCompatibilityOfferServices(subscription, serviceToInstantiates);
        return serviceToInstantiateDtos;
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

        handleMissingParametersAndValidate(postData);

        if (postData.getOperationDate() == null) {
            postData.setOperationDate(new Date());
        }

        OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getOneShotCharge());
        if (oneShotChargeTemplate == null) {
            throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getOneShotCharge());
        }

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(postData.getSubscription(), postData.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription(), postData.getSubscriptionValidityDate());
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

        OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance();

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), oneShotChargeInstance, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        try {

            oneShotChargeInstanceService
                    .oneShotChargeApplication(subscription, null, (OneShotChargeTemplate) oneShotChargeTemplate, postData.getWallet(), postData.getOperationDate(),
                            postData.getAmountWithoutTax(), postData.getAmountWithTax(), postData.getQuantity(), postData.getCriteria1(), postData.getCriteria2(),
                            postData.getCriteria3(), postData.getDescription(), subscription.getOrderNumber(), oneShotChargeInstance.getCfValues(), true);

        } catch (RatingException e) {
            log.trace("Failed to apply one shot charge {}: {}", oneShotChargeTemplate.getCode(), e.getRejectionReason());
            throw new MeveoApiException(e.getMessage());

        } catch (BusinessException e) {
            log.error("Failed to apply one shot charge {}: {}", oneShotChargeTemplate.getCode(), e.getMessage(), e);
            throw e;
        }

    }

    /**
     * Apply a product charge on a subscription
     *
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
            throw new EntityDoesNotExistsException(ProductTemplate.class, postData.getProduct() + "/" + DateUtils.formatDateWithPattern(postData.getOperationDate(), paramBeanFactory.getInstance().getDateTimeFormat()));
        }

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(postData.getSubscription(), postData.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription(), postData.getSubscriptionValidityDate());
        }

        if ((subscription.getStatus() != SubscriptionStatusEnum.ACTIVE) && (subscription.getStatus() != SubscriptionStatusEnum.CREATED)) {
            throw new MeveoApiException("subscription is not ACTIVE or CREATED: [" + subscription.getStatus() + "]");
        }

        List<WalletOperation> walletOperations = null;

        try {
            ProductInstance productInstance = new ProductInstance(null, subscription, productTemplate, postData.getQuantity(), postData.getOperationDate(), postData.getProduct(),
                    StringUtils.isBlank(postData.getDescription()) ? productTemplate.getDescriptionOrCode() : postData.getDescription(), null, subscription.getSeller());

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
     *
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

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(postData.getSubscriptionCode(), postData.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscriptionCode(), postData.getSubscriptionValidityDate());
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
     *
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

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(terminateSubscriptionDto.getSubscriptionCode(), terminateSubscriptionDto.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, terminateSubscriptionDto.getSubscriptionCode(), terminateSubscriptionDto.getSubscriptionValidityDate());
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
                            ChargeInstance.NO_ORDER_NUMBER.equals(terminateSubscriptionDto.getOrderNumber()) ? serviceInstance.getOrderNumber() : terminateSubscriptionDto.getOrderNumber());
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
                            ChargeInstance.NO_ORDER_NUMBER.equals(terminateSubscriptionDto.getOrderNumber()) ? serviceInstance.getOrderNumber() : terminateSubscriptionDto.getOrderNumber());
                } catch (BusinessException e) {
                    log.error("service termination={}", e.getMessage());
                    throw new MeveoApiException(e.getMessage());
                }
            }
        }
    }

    /**
     * List subscription by user account
     *
     * @param userAccountCode user account code
     * @param mergedCF true/false (true if we want the merged CF in return)
     * @param sortBy name of column to be sorted
     * @param sortOrder ASC/DESC
     * @return instance of SubscriptionsListDto which contains list of Subscription DTO
     * @throws MeveoApiException meveo api exception
     */
    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "subscription", itemPropertiesToFilter = {@FilterProperty(property = "seller", entityClass = Seller.class),
            @FilterProperty(property = "userAccount", entityClass = UserAccount.class)}, totalRecords = "listSize")
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

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "subscription", itemPropertiesToFilter = {@FilterProperty(property = "seller", entityClass = Seller.class),
            @FilterProperty(property = "userAccount", entityClass = UserAccount.class)}, totalRecords = "listSize")
    public SubscriptionsDto listByCustomer(String customerCode, boolean mergedCF) throws MeveoApiException {

        if (StringUtils.isBlank(customerCode)) {
            missingParameters.add("customerCode");
            handleMissingParameters();
        }

        Customer customer = customerService.findByCode(customerCode);
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }

        SubscriptionsDto result = new SubscriptionsDto();
        List<Subscription> subscriptions = subscriptionService.listByCustomer(customer);
        if (subscriptions != null) {
            for (Subscription s : subscriptions) {
                result.getSubscription().add(subscriptionToDto(s, CustomFieldInheritanceEnum.getInheritCF(true, mergedCF)));
            }
        }

        return result;
    }

    /**
     * List subbscriptions
     *
     * @param mergedCF truf if merging inherited CF
     * @param pagingAndFiltering paging and filtering.
     * @return instance of SubscriptionsListDto which contains list of Subscription DTO
     * @throws MeveoApiException meveo api exception
     */
    public SubscriptionsListResponseDto list(Boolean mergedCF, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        boolean merge = mergedCF != null && mergedCF;
        return list(pagingAndFiltering, CustomFieldInheritanceEnum.getInheritCF(true, merge));
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "subscriptions.subscription", itemPropertiesToFilter = {@FilterProperty(property = "seller", entityClass = Seller.class),
            @FilterProperty(property = "userAccount", entityClass = UserAccount.class)}, totalRecords = "subscriptions.listSize")
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
     *
     * @param subscriptionCode code of subscription to find
     * @return instance of SubscriptionsListDto which contains list of Subscription DTO
     * @throws MeveoApiException meveo api exception
     */
    public SubscriptionDto findSubscription(String subscriptionCode, Date validityDate) throws MeveoApiException {
        return this.findSubscription(subscriptionCode, false, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, validityDate);
    }

    public SubscriptionDto findSubscription(String subscriptionCode, CustomFieldInheritanceEnum inheritCF, Date validityDate) throws MeveoApiException {
        return this.findSubscription(subscriptionCode, false, inheritCF, validityDate);
    }

    /**
     * Find subscription
     *
     * @param subscriptionCode code of subscription to find
     * @param mergedCF true/false
     * @param inheritCF Custom field inheritance type
     * @return instance of SubscriptionsListDto which contains list of Subscription DTO
     * @throws MeveoApiException meveo api exception
     */
    @SecuredBusinessEntityMethod(resultFilter = ObjectFilter.class)
    @FilterResults(itemPropertiesToFilter = {@FilterProperty(property = "seller", entityClass = Seller.class), @FilterProperty(property = "userAccount", entityClass = UserAccount.class)})
    public SubscriptionDto findSubscription(String subscriptionCode, boolean mergedCF, CustomFieldInheritanceEnum inheritCF, Date validityDate) throws MeveoApiException {
        SubscriptionDto result = new SubscriptionDto();

        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
            handleMissingParameters();
        }
        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, validityDate);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, validityDate);
        }

        CustomFieldInheritanceEnum inherit = (inheritCF != null && !mergedCF) ? inheritCF : CustomFieldInheritanceEnum.getInheritCF(true, mergedCF);

        result = subscriptionToDto(subscription, inherit);

        return result;
    }

    /**
     * Create or update Subscription based on subscription code.
     *
     * @param postData posted data to API
     * @return the subscription
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public Subscription createOrUpdate(SubscriptionDto postData) throws MeveoApiException, BusinessException {
        Subscription subscription = subscriptionService.findByCodeAndValidityDate(postData.getCode(), postData.getValidityDate());
        if (subscription == null) {
            subscription = create(postData);
        } else {
            subscription = update(postData);
        }
        return subscription;
    }

    /**
     * Convert subscription dto to entity
     *
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

                serviceInstanceDto = serviceInstanceToDto(serviceInstance, customFieldsDTO, inheritCF);
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
        if (subscription.getDiscountPlanInstances() != null && !subscription.getDiscountPlanInstances().isEmpty()) {
            dto.setDiscountPlanInstances(subscription.getDiscountPlanInstances().stream()
                    .map(discountPlanInstance -> new DiscountPlanInstanceDto(discountPlanInstance, entityToDtoConverter.getCustomFieldsDTO(discountPlanInstance, CustomFieldInheritanceEnum.INHERIT_NONE)))
                    .collect(Collectors.toList()));
        }

        dto.setAutoEndOfEngagement(subscription.getAutoEndOfEngagement());
        setAuditableFieldsDto(subscription, dto);
        return dto;
    }

    /**
     * Get the ServiceInstanceDto dto
     *
     * @param serviceInstance instance of ServiceInstance entity
     * @param customFieldsDTO the custom field DTO
     * @param inheritCF the inherit CF
     * @return the Service instance dto
     */
    private ServiceInstanceDto serviceInstanceToDto(ServiceInstance serviceInstance, CustomFieldsDto customFieldsDTO, CustomFieldInheritanceEnum inheritCF) {

        CustomFieldsDto cFsDTO = null;

        List<ChargeInstanceDto> recurringChargeInstances = null;
        if (serviceInstance.getRecurringChargeInstances() != null) {
            recurringChargeInstances = new ArrayList<>();
            for (RecurringChargeInstance ci : serviceInstance.getRecurringChargeInstances()) {
                cFsDTO = entityToDtoConverter.getCustomFieldsDTO(ci, inheritCF);
                recurringChargeInstances.add(new ChargeInstanceDto(ci, cFsDTO));
            }
        }

        List<ChargeInstanceDto> subscriptionChargeInstances = null;
        if (serviceInstance.getSubscriptionChargeInstances() != null) {
            subscriptionChargeInstances = new ArrayList<>();
            for (OneShotChargeInstance ci : serviceInstance.getSubscriptionChargeInstances()) {
                cFsDTO = entityToDtoConverter.getCustomFieldsDTO(ci, inheritCF);
                subscriptionChargeInstances.add(new ChargeInstanceDto(ci, cFsDTO));
            }
        }

        List<ChargeInstanceDto> terminationChargeInstances = null;
        if (serviceInstance.getTerminationChargeInstances() != null) {
            terminationChargeInstances = new ArrayList<>();
            for (OneShotChargeInstance ci : serviceInstance.getTerminationChargeInstances()) {
                cFsDTO = entityToDtoConverter.getCustomFieldsDTO(ci, inheritCF);
                terminationChargeInstances.add(new ChargeInstanceDto(ci, cFsDTO));
            }
        }

        List<ChargeInstanceDto> usageChargeInstances = null;
        if (serviceInstance.getUsageChargeInstances() != null) {
            usageChargeInstances = new ArrayList<>();
            for (UsageChargeInstance ci : serviceInstance.getUsageChargeInstances()) {
                cFsDTO = entityToDtoConverter.getCustomFieldsDTO(ci, inheritCF);
                usageChargeInstances.add(new ChargeInstanceDto(ci, cFsDTO));
            }
        }

        ServiceInstanceDto serviceInstanceDto = new ServiceInstanceDto(serviceInstance, recurringChargeInstances, subscriptionChargeInstances, terminationChargeInstances, usageChargeInstances, customFieldsDTO);

        setAuditableFieldsDto(serviceInstance, serviceInstanceDto);
        return serviceInstanceDto;
    }

    public void createOrUpdatePartialWithAccessAndServices(SubscriptionDto subscriptionDto, String orderNumber, Long orderItemId, OrderItemActionEnum orderItemAction) throws MeveoApiException, BusinessException {

        SubscriptionDto existedSubscriptionDto = null;
        try {
            existedSubscriptionDto = findSubscription(subscriptionDto.getCode(), subscriptionDto.getValidityDate());
        } catch (Exception e) {
            existedSubscriptionDto = null;
        }

        log.debug("createOrUpdatePartial subscription {}", subscriptionDto);
        if (existedSubscriptionDto == null) {
            createSubscription(subscriptionDto, true);

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

        createAccess(subscriptionDto);
        createService(subscriptionDto, orderNumber, orderItemId, orderItemAction);

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

    private void createService(SubscriptionDto subscriptionDto, String orderNumber, Long orderItemId, OrderItemActionEnum orderItemAction) {
        // Update, instantiate, activate or terminate services
        if (subscriptionDto.getServices() != null && subscriptionDto.getServices().getServiceInstance() != null) {

            InstantiateServicesRequestDto instantiateServicesDto = new InstantiateServicesRequestDto();
            instantiateServicesDto.setSubscription(subscriptionDto.getCode());
            instantiateServicesDto.setSubscriptionValidityDate(subscriptionDto.getValidityDate());
            instantiateServicesDto.setOrderNumber(orderNumber);
            instantiateServicesDto.setOrderItemId(orderItemId);
            instantiateServicesDto.setOrderItemAction(orderItemAction);
            List<ServiceToInstantiateDto> serviceToInstantiates = instantiateServicesDto.getServicesToInstantiate().getService();

            ActivateServicesRequestDto activateServicesDto = new ActivateServicesRequestDto();
            activateServicesDto.setSubscription(subscriptionDto.getCode());
            instantiateServicesDto.setSubscriptionValidityDate(subscriptionDto.getValidityDate());
            activateServicesDto.setOrderNumber(orderNumber);
            activateServicesDto.setOrderItemId(orderItemId);
            activateServicesDto.setOrderItemAction(orderItemAction);

            UpdateServicesRequestDto updateServicesRequestDto = new UpdateServicesRequestDto();
            updateServicesRequestDto.setSubscriptionCode(subscriptionDto.getCode());
            instantiateServicesDto.setSubscriptionValidityDate(subscriptionDto.getValidityDate());
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
                    terminateServiceDto.setSubscriptionValidityDate(subscriptionDto.getValidityDate());
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
                        serviceToInstantiate.setAmountPS(serviceInstanceDto.getAmountPS());
                        serviceToInstantiate.setCalendarPSCode(serviceInstanceDto.getCalendarPSCode());
                        serviceToInstantiates.add(serviceToInstantiate);

                        // Service will be activated
                    } else {

                        ServiceToActivateDto serviceToActivateDto = new ServiceToActivateDto();
                        serviceToActivateDto.setCode(serviceInstanceDto.getCode());
                        serviceToActivateDto.setSubscriptionDate(serviceInstanceDto.getSubscriptionDate());
                        serviceToActivateDto.setQuantity(serviceInstanceDto.getQuantity());
                        serviceToActivateDto.setCustomFields(serviceInstanceDto.getCustomFields());
                        serviceToActivateDto.setRateUntilDate(serviceInstanceDto.getRateUntilDate());
                        serviceToActivateDto.setAmountPS(serviceInstanceDto.getAmountPS());
                        serviceToActivateDto.setPaymentDayInMonthPS(serviceInstanceDto.getPaymentDayInMonthPS());
                        serviceToActivateDto.setCalendarPSCode(serviceInstanceDto.getCalendarPSCode());
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
    }

    private void createAccess(SubscriptionDto subscriptionDto) {
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
                    accessDto.setSubscriptionValidity(subscriptionDto.getValidityDate());
                }
                accessApi.createOrUpdatePartial(accessDto);
            }
        }
    }

    /**
     * Suspend subscription
     *
     * @param subscriptionCode subscription code
     * @param suspensionDate suspension date
     * @param subscriptionValidityDate
     * @throws MissingParameterException Missing parameter exception
     * @throws EntityDoesNotExistsException Entity does not exists exception
     * @throws IncorrectSusbcriptionException Incorrect susbcription exception
     * @throws IncorrectServiceInstanceException Incorrect service instance exception
     * @throws BusinessException Business exception
     */
    public void suspendSubscription(String subscriptionCode, Date suspensionDate, Date subscriptionValidityDate)
            throws MissingParameterException, EntityDoesNotExistsException, IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
            handleMissingParameters();
        }
        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, subscriptionValidityDate);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, subscriptionValidityDate);
        }
        subscriptionService.subscriptionSuspension(subscription, suspensionDate);
    }

    /**
     * Resume subscription
     *
     * @param subscriptionCode subscription code
     * @param suspensionDate suspension data
     * @param subscriptionValidityDate
     * @throws MissingParameterException missiong parameter exeption
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrection service isntance exception
     * @throws BusinessException business exception.
     */
    public void resumeSubscription(String subscriptionCode, Date suspensionDate, Date subscriptionValidityDate)
            throws MissingParameterException, EntityDoesNotExistsException, IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
            handleMissingParameters();
        }
        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, subscriptionValidityDate);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, subscriptionValidityDate);
        }
        subscriptionService.subscriptionReactivation(subscription, suspensionDate);
    }

    /**
     * Suspend services
     *
     * @param provisionningServicesRequestDto provisioning service request.
     *
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     */
    public void suspendServices(OperationServicesRequestDto provisionningServicesRequestDto) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException, MeveoApiException {
        suspendOrResumeServices(provisionningServicesRequestDto, true);
    }

    /**
     * Resume services
     *
     * @param provisionningServicesRequestDto provisioning service request.
     *
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     */
    public void resumeServices(OperationServicesRequestDto provisionningServicesRequestDto) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException, MeveoApiException {
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
    private void suspendOrResumeServices(OperationServicesRequestDto postData, boolean isToSuspend) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException, MeveoApiException {
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

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, postData.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, postData.getSubscriptionValidityDate());
        }

        if (postData != null) {
            for (ServiceToUpdateDto serviceToSuspendDto : postData.getServicesToUpdate()) {
                ServiceInstance serviceInstanceToSuspend = getSingleServiceInstance(serviceToSuspendDto.getId(), serviceToSuspendDto.getCode(), subscription,
                        isToSuspend ? InstanceStatusEnum.ACTIVE : InstanceStatusEnum.SUSPENDED);

                if (isToSuspend) {
                    serviceInstanceService.serviceSuspension(serviceInstanceToSuspend, serviceToSuspendDto.getActionDate());
                } else {
                    serviceInstanceService.serviceReactivation(serviceInstanceToSuspend, serviceToSuspendDto.getActionDate(), true, false);
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

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(postData.getSubscriptionCode(), postData.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscriptionCode(), postData.getSubscriptionValidityDate());
        }

        for (ServiceToUpdateDto serviceToUpdateDto : postData.getServicesToUpdate()) {

            ServiceInstance serviceToUpdate = getSingleServiceInstance(serviceToUpdateDto.getId(), serviceToUpdateDto.getCode(), subscription, InstanceStatusEnum.ACTIVE, InstanceStatusEnum.INACTIVE);

            if (serviceToUpdateDto.getEndAgreementDate() != null) {
                serviceToUpdate.setEndAgreementDate(serviceToUpdateDto.getEndAgreementDate());
            }

            if (!StringUtils.isBlank(serviceToUpdateDto.getDescription())) {
                serviceToUpdate.setDescription(serviceToUpdateDto.getDescription());
            }

            if (serviceToUpdateDto.getQuantity() != null) {
                serviceToUpdate.setQuantity(serviceToUpdateDto.getQuantity());
            }

            if (serviceToUpdateDto.getOverrideCode() != null) {
                serviceToUpdate.setCode(serviceToUpdateDto.getOverrideCode());
            }

            SubscriptionRenewal serviceRenewal = subscriptionRenewalFromDto(serviceToUpdate.getServiceRenewal(), serviceToUpdateDto.getServiceRenewal(), serviceToUpdate.isRenewed());
            serviceToUpdate.setServiceRenewal(serviceRenewal);

            setServiceFutureTermination(serviceToUpdateDto, serviceToUpdate);

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

    public ServiceInstanceDto findServiceInstance(String subscriptionCode, Long serviceInstanceId, String serviceInstanceCode, Date subscriptionValidityDate) throws MeveoApiException {
        ServiceInstanceDto result = new ServiceInstanceDto();

        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, subscriptionValidityDate);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, subscriptionValidityDate);
        }

        ServiceInstance serviceInstance = getSingleServiceInstance(serviceInstanceId, serviceInstanceCode, subscription);
        if (serviceInstance != null) {
            result = serviceInstanceToDto(serviceInstance, entityToDtoConverter.getCustomFieldsDTO(serviceInstance, CustomFieldInheritanceEnum.INHERIT_NO_MERGE), CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
        }

        return result;
    }

    public DueDateDelayDto getDueDateDelay(String subscriptionCode, Date subscriptionValidityDate, String invoiceNumber, String invoiceTypeCode, String orderCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, subscriptionValidityDate);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, subscriptionValidityDate);
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

        Integer delay = null;
        String delayEL = billingCycle.getDueDateDelayEL();
        DueDateDelayEnum delayOrigin = DueDateDelayEnum.BC;
        if (order != null && !StringUtils.isBlank(order.getDueDateDelayEL())) {
            delay = InvoiceService.evaluateDueDelayExpression(order.getDueDateDelayEL(), billingAccount, invoice, order);
            delayEL = order.getDueDateDelayEL();
            delayOrigin = DueDateDelayEnum.ORDER;
        } else {
            if (!StringUtils.isBlank(billingAccount.getCustomerAccount().getDueDateDelayEL())) {
                delay = InvoiceService.evaluateDueDelayExpression(billingAccount.getCustomerAccount().getDueDateDelayEL(), billingAccount, invoice, null);
                delayEL = billingAccount.getCustomerAccount().getDueDateDelayEL();
                delayOrigin = DueDateDelayEnum.CA;
            } else if (!StringUtils.isBlank(billingCycle.getDueDateDelayEL())) {
                delay = InvoiceService.evaluateDueDelayExpression(billingCycle.getDueDateDelayEL(), billingAccount, invoice, null);
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
                OneShotChargeTemplateDto oneshotChartTemplateDto = new OneShotChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate));
                results.add(oneshotChartTemplateDto);
            }
        }

        return results;
    }

    private List<OneShotChargeInstanceDto> getOneShotCharges(String subscriptionCode, Date validityDate) throws EntityDoesNotExistsException, InvalidParameterException {
        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, validityDate);

        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, validityDate);
        }

        List<OneShotChargeInstance> oneShotChargeInstances = oneShotChargeInstanceService.findOneShotChargeInstancesBySubscriptionId(subscription.getId());
        List<OneShotChargeInstanceDto> oneShotChargeInstanceDtos = new ArrayList<>();

        for (OneShotChargeInstance oneShotChargeInstance : oneShotChargeInstances) {
            OneShotChargeInstanceDto oneShotChargeInstanceDto = null;
            List<WalletOperation> sortedWalletOperations = oneShotChargeInstance.getWalletOperationsSorted();
            if (oneShotChargeInstance.getAmountWithTax() == null && sortedWalletOperations != null && !sortedWalletOperations.isEmpty()) {
                oneShotChargeInstanceDto = new OneShotChargeInstanceDto(oneShotChargeInstance, sortedWalletOperations.get(0).getAmountWithoutTax(), sortedWalletOperations.get(0).getAmountWithTax());
            } else {
                oneShotChargeInstanceDto = new OneShotChargeInstanceDto(oneShotChargeInstance);
            }

            oneShotChargeInstanceDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(oneShotChargeInstance, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
            oneShotChargeInstanceDtos.add(oneShotChargeInstanceDto);
        }

        return oneShotChargeInstanceDtos;
    }

    /**
     * @return list of one shot charge others.
     */
    public List<OneShotChargeTemplateDto> getOneShotChargeOthers() throws EntityDoesNotExistsException, InvalidParameterException {
        return this.getOneShotCharges(OneShotChargeTemplateTypeEnum.OTHER);
    }

    public List<OneShotChargeInstanceDto> getOneShotChargeOthers(String subscriptionCode, Date validityDate) throws EntityDoesNotExistsException, InvalidParameterException {

        return this.getOneShotCharges(subscriptionCode, validityDate);
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
    public SubscriptionRenewal subscriptionRenewalFromDto(SubscriptionRenewal renewalInfo, SubscriptionRenewalDto renewalInfoDto, boolean subscriptionRenewed) throws InvalidParameterException, MissingParameterException {

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

            } else if (!serviceInstance.getSubscription().equals(subscription) || (statuses != null && statuses.length > 0 && !ArrayUtils.contains(statuses, serviceInstance.getStatus()))) {
                throw new InvalidParameterException("Service instance id " + serviceId + " does not correspond to subscription " + subscription.getCode() + " or is not of status ["
                        + (statuses != null ? StringUtils.concatenate((Object[]) statuses) : "") + "]");
            }

        } else if (!StringUtils.isBlank(serviceCode)) {
            List<ServiceInstance> services = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceCode, subscription, statuses);
            if (services.size() == 1) {
                serviceInstance = services.get(0);
            } else if (services.size() > 1) {
                throw new InvalidParameterException(
                        "More than one service instance with status [" + (statuses != null ? StringUtils.concatenate((Object[]) statuses) : "") + "] was found. Please use ID to refer to service instance.");
            } else {
                throw new EntityDoesNotExistsException("Service instance with code " + serviceCode + " was not found or is not of status [" + (statuses != null ? StringUtils.concatenate((Object[]) statuses) : "") + "]");
            }

        } else {
            throw new MissingParameterException("service id or code");
        }
        return serviceInstance;
    }

    public List<ServiceInstanceDto> listServiceInstance(String subscriptionCode, Date subscriptionValidityDate, String serviceInstanceCode) throws MissingParameterException {
        List<ServiceInstanceDto> result = new ArrayList<>();

        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        if (StringUtils.isBlank(serviceInstanceCode)) {
            missingParameters.add("serviceInstanceCode");
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, subscriptionValidityDate);

        if(subscription == null)
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, subscriptionValidityDate);

        List<ServiceInstance> serviceInstances = serviceInstanceService.listServiceInstance(subscription.getId() ,serviceInstanceCode);
        if (serviceInstances != null && !serviceInstances.isEmpty()) {
            result = serviceInstances.stream().map(p -> serviceInstanceToDto(p, entityToDtoConverter.getCustomFieldsDTO(p, CustomFieldInheritanceEnum.INHERIT_NO_MERGE), CustomFieldInheritanceEnum.INHERIT_NO_MERGE))
                    .collect(Collectors.toList());
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
        Subscription subscription = subscriptionService.findByCodeAndValidityDate(postData.getSubscriptionCode(), postData.getSubscriptionValidityDate());
        if(subscription == null)
            return result;

        // Recurring charges :
        List<Long> activeRecurringChargeIds = recurringChargeInstanceService.findIdsByStatusAndSubscriptionId(InstanceStatusEnum.ACTIVE, rateUntillDate, subscription.getId());
        for (Long chargeId : activeRecurringChargeIds) {
            int nbRating = recurringChargeInstanceService.applyRecurringCharge(chargeId, rateUntillDate, false).getNbRating();
            result.addResult(chargeId, nbRating);
        }
        return result;
    }

    /**
     * Activates all instantiated services of a given subscription.
     *
     * @param subscriptionCode The subscription code
     * @throws BusinessException
     * @throws MissingParameterException
     */
    public void activateSubscription(String subscriptionCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        subscriptionService.activateInstantiatedService(subscription);
    }

    public void cancelSubscriptionRenewal(String subscriptionCode, Date subscriptionValidityDate) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, subscriptionValidityDate);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, subscriptionValidityDate);
        }

        subscriptionService.cancelSubscriptionRenewal(subscription);
    }

    public SubscriptionForCustomerResponseDto activateForCustomer(SubscriptionForCustomerRequestDto postData) throws MeveoApiException, BusinessException {

        SubscriptionForCustomerResponseDto result = new SubscriptionForCustomerResponseDto();

        String subscriptionCode = postData.getSubscriptionCode();
        if (StringUtils.isBlank(subscriptionCode)) {
            this.missingParameters.add("subscriptionCode");
        }
        String customerCode = postData.getSubscriptionClientId();
        if (StringUtils.isBlank(customerCode)) {
            this.missingParameters.add("subscriptionClientId");
        }
        this.handleMissingParameters();

        // Checking if Subscription exist :
        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, postData.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, postData.getSubscriptionValidityDate());
        }
        // Checking if Customer exist :
        Customer customer = this.customerService.findByCode(customerCode);
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }
        // cheking if the Subscription belongs to the given customer :
        if (!this.subscriptionBelondsToCustomer(customerCode, subscription)) {
            throw new InvalidParameterException(String.format("Subscription [%s] doesn't belongs to Customer [%s] ", subscriptionCode, customerCode));
        }

        this.subscriptionService.activateInstantiatedService(subscription);
        result.setSubscriptionEndDate(DateUtils.formatDateWithPattern(subscription.getEndAgreementDate(), DateUtils.DATE_PATTERN));

        return result;
    }

    public void terminateOneShotCharge(String oneShotChargeCode, String subscriptionCode, Date validityDate) {
        try {
            Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, validityDate);
            if (subscription == null) {
                throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, validityDate);
            }
            OneShotChargeInstance oneShotChargeInstance = oneShotChargeInstanceService.findByCodeAndSubsription(oneShotChargeCode, subscription.getId());
            oneShotChargeInstanceService.terminateOneShotChargeInstance(oneShotChargeInstance);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param customerCode the customer code
     * @param subscription the subscription
     * @return true, if the subscription belongs to the given customer, false otherwise
     */
    private boolean subscriptionBelondsToCustomer(String customerCode, Subscription subscription) {
        try {
            // we stipulate that this chain of getters is NPE free. otherwise false is returned
            return customerCode.equals(subscription.getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().getCode());
        } catch (Exception e) {
            log.error("Error on subscriptionBelondsToCustomer [{}] ", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Create a subscription and activate services in a single transaction
     *
     * @param postData
     * @throws MeveoApiException
     * @throws BusinessException
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void subscribeAndActivateServices(SubscriptionAndServicesToActivateRequestDto postData) throws MeveoApiException, BusinessException {
        ActivateServicesRequestDto activateServicesRequestDto = new ActivateServicesRequestDto();
        activateServicesRequestDto.setServicesToActivateDto(postData.getServicesToActivateDto());
        activateServicesRequestDto.setSubscription(postData.getCode());
        activateServicesRequestDto.setSubscriptionValidityDate(postData.getValidityDate());

        this.create(postData);
        this.activateServices(activateServicesRequestDto);
    }

    private Subscription createSubscription(SubscriptionDto postData, boolean extraValidtion) throws MeveoApiException, BusinessException {
        if (extraValidtion) {
            if (StringUtils.isBlank(postData.getCode())) {
                missingParameters.add("code");
            }
            if (StringUtils.isBlank(postData.getUserAccount())) {
                missingParameters.add("userAccount");
            }
            if (StringUtils.isBlank(postData.getOfferTemplate())) {
                missingParameters.add("offerTemplate");
            }
            handleMissingParametersAndValidate(postData);
        }
        return createSubscription(postData);
    }

    private Subscription createSubscription(SubscriptionDto postData) throws MeveoApiException, BusinessException {
        if (subscriptionService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Subscription.class, postData.getCode());
        }

        return createSubscriptionWithoutCheckOnCodeExistence(postData);
    }

    private Subscription createSubscriptionWithoutCheckOnCodeExistence(SubscriptionDto postData) {
        if (StringUtils.isBlank(postData.getSubscriptionDate())) {
            postData.setSubscriptionDate(new Date());
        }

        UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount());
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), postData.getSubscriptionDate());
        if (offerTemplate == null) {
            throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate() + " / " + DateUtils.formatDateWithPattern(postData.getSubscriptionDate(), paramBean.getDateTimeFormat()));
        }

        if (offerTemplate.isDisabled()) {
            throw new MeveoApiException("Cannot subscribe to disabled offer");
        }

        Seller seller = null;
        if (StringUtils.isBlank(postData.getSeller())) {
            // v5.2 : code for API backward compatibility call, seller code must be mandatory in future versions
            seller = userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        } else {
            seller = sellerService.findByCode(postData.getSeller());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
            }

            if (offerTemplate.getSellers().size() > 0) {
                if (!offerTemplate.getSellers().contains(seller)) {
                    throw new EntityNotAllowedException(Seller.class, Subscription.class, postData.getSeller());
                }
            }
        }

        Subscription subscription = new Subscription();

        subscription.setCode(postData.getCode());
        subscription.setDescription(postData.getDescription());
        subscription.setUserAccount(userAccount);
        subscription.setSeller(seller);
        subscription.setOffer(offerTemplate);
        subscription.setFromValidity(postData.getValidityDate());
        if (!StringUtils.isBlank(postData.getBillingCycle())) {
            BillingCycle billingCycle = billingCycleService.findByCode(postData.getBillingCycle());
            if (billingCycle == null) {
                throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
            }
            subscription.setBillingCycle(billingCycle);
        }

        if (Objects.nonNull(postData.getPaymentMethod())) {
            PaymentMethod paymentMethod = paymentMethodService.findById(postData.getPaymentMethod().getId());
            if (paymentMethod == null) {
                throw new EntityNotFoundException("payment method not found!");
            }
            subscription.setPaymentMethod(paymentMethod);
        }

        subscription.setSubscriptionDate(postData.getSubscriptionDate());

        // subscription.setTerminationDate(postData.getTerminationDate());

        SubscriptionRenewal subscriptionRenewal = null;
        if (postData.getRenewalRule() == null) {
            subscriptionRenewal = subscriptionRenewalFromDto(offerTemplate.getSubscriptionRenewal().copy(), null, false);
        } else {
            subscriptionRenewal = subscriptionRenewalFromDto(null, postData.getRenewalRule(), false);
        }
        subscription.setSubscriptionRenewal(subscriptionRenewal);

        setSubscriptionFutureTermination(postData, subscription);

        Boolean subscriptionAutoEndOfEngagement = postData.getAutoEndOfEngagement();
        if (subscriptionAutoEndOfEngagement == null) {
            subscription.setAutoEndOfEngagement(offerTemplate.getAutoEndOfEngagement());
        } else {
            subscription.setAutoEndOfEngagement(postData.getAutoEndOfEngagement());
        }

        subscriptionService.updateSubscribedTillAndRenewalNotifyDates(subscription);
        // ignoring postData.getEndAgreementDate() if subscription.getAutoEndOfEngagement is true
        if (subscription.getAutoEndOfEngagement() == null || !subscription.getAutoEndOfEngagement()) {
            subscription.setEndAgreementDate(postData.getEndAgreementDate());
        }

        setMinimumAmountElSubscription(postData, subscription, offerTemplate);
        subscription.setRatingGroup(postData.getRatingGroup());

        // populate Electronic Billing Fields
        populateElectronicBillingFields(postData, subscription);

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
        userAccount.getSubscriptions().add(subscription);

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

        // instantiate the discounts
        if (postData.getDiscountPlansForInstantiation() != null) {
            for (DiscountPlanDto discountPlanDto : postData.getDiscountPlansForInstantiation()) {
                DiscountPlan dp = discountPlanService.findByCode(discountPlanDto.getCode());
                if (dp == null) {
                    throw new EntityDoesNotExistsException(DiscountPlan.class, discountPlanDto.getCode());
                }

                discountPlanService.detach(dp);
                dp = DiscountPlanDto.copyFromDto(discountPlanDto, dp);

                // populate customFields
                try {
                    populateCustomFields(discountPlanDto.getCustomFields(), dp, true);
                } catch (MissingParameterException | InvalidParameterException e) {
                    log.error("Failed to associate custom field instance to an entity: {} {}", discountPlanDto.getCode(), e.getMessage());
                    throw e;
                } catch (Exception e) {
                    log.error("Failed to associate custom field instance to an entity {}", discountPlanDto.getCode(), e);
                    throw new MeveoApiException("Failed to associate custom field instance to an entity " + discountPlanDto.getCode());
                }
                subscriptionService.instantiateDiscountPlan(subscription, dp);
            }
        }
        return subscription;
    }

    private void setMinimumAmountElSubscription(SubscriptionDto postData, Subscription subscription, OfferTemplate offerTemplate) {
        subscription.setMinimumAmountEl(offerTemplate.getMinimumAmountEl());
        subscription.setMinimumLabelEl(offerTemplate.getMinimumLabelEl());
        subscription.setMinimumAmountElSpark(offerTemplate.getMinimumAmountElSpark());
        subscription.setMinimumLabelElSpark(offerTemplate.getMinimumLabelElSpark());
        subscription.setMinimumChargeTemplate(offerTemplate.getMinimumChargeTemplate());

        if (!StringUtils.isBlank(postData.getMinimumAmountEl())) {
            subscription.setMinimumAmountEl(postData.getMinimumAmountEl());
        }
        if (!StringUtils.isBlank(postData.getMinimumLabelEl())) {
            subscription.setMinimumLabelEl(postData.getMinimumLabelEl());
        }
        if (!StringUtils.isBlank(postData.getMinimumAmountElSpark())) {
            subscription.setMinimumAmountElSpark(postData.getMinimumAmountElSpark());
        }
        if (!StringUtils.isBlank(postData.getMinimumLabelElSpark())) {
            subscription.setMinimumLabelElSpark(postData.getMinimumLabelElSpark());
        }
        if (!StringUtils.isBlank(postData.getMinimumChargeTemplate())) {
            OneShotChargeTemplate minimumChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getMinimumChargeTemplate());
            if (minimumChargeTemplate == null) {
                throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getMinimumChargeTemplate());
            } else {
                subscription.setMinimumChargeTemplate(minimumChargeTemplate);
            }
        }
    }

    public void patchSubscription(String code, SubscriptionPatchDto subscriptionPatchDto) throws Exception {

        if (StringUtils.isBlank(subscriptionPatchDto.getTerminationReason())) {
            missingParameters.add("terminationReason");
        }
        handleMissingParameters();

        SubscriptionDto existingSubscriptionDto = findSubscription(code, null);
        Subscription existingSubscription = subscriptionService.findByCode(code);

        if (existingSubscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, code);
        }

        Date effectiveDate = subscriptionPatchDto.getEffectiveDate() == null ? new Date(): subscriptionPatchDto.getEffectiveDate();

        if (existingSubscription.getValidity() != null &&
                (isEffectiveDateBeforeValidTo(subscriptionPatchDto, existingSubscription) || isValidToNullAndEffectiveDateBeforeOrEqualValidFrom(subscriptionPatchDto, existingSubscription))
        ) {
            String from = existingSubscription.getValidity().getFrom() == null ? "-" : existingSubscription.getValidity().getFrom().toString();
            String to = existingSubscription.getValidity().getTo() == null ? "-" : existingSubscription.getValidity().getTo().toString();
            throw new InvalidParameterException("A version already exists for effectiveDate=" + effectiveDate + " (Subscription[code=" + code + ", validFrom=" + from + " validTo=" + to + "])). Only last version can be updated.");
        }

        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(subscriptionPatchDto.getTerminationReason());
        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, subscriptionPatchDto.getTerminationReason());
        }
        subscriptionService.terminateSubscription(existingSubscription, effectiveDate, subscriptionTerminationReason, existingSubscription.getOrderNumber());


        existingSubscriptionDto.setValidityDate(effectiveDate);
        if (subscriptionPatchDto.getUpdateSubscriptionDate()) {
            existingSubscriptionDto.setSubscriptionDate(effectiveDate);
        }
        if (subscriptionPatchDto.getResetRenewalTerms()) {
            existingSubscriptionDto.setRenewalRule(null);
        }
        if (isNotBlank(subscriptionPatchDto.getOfferTemplate())) {
            existingSubscriptionDto.setOfferTemplate(subscriptionPatchDto.getOfferTemplate());
        }
        if (isNotBlank(subscriptionPatchDto.getNewSubscriptionCode())) {
            existingSubscriptionDto.setCode(subscriptionPatchDto.getNewSubscriptionCode());
        }

        Subscription newSubscription = createSubscriptionWithoutCheckOnCodeExistence(existingSubscriptionDto);

        for (AccessDto access : existingSubscriptionDto.getAccesses().getAccess()) {
            access.setSubscription(existingSubscriptionDto.getCode());
        }
        createAccess(existingSubscriptionDto);

        if (subscriptionPatchDto.getServicesToInstantiate() != null) {
            List<ServiceToInstantiateDto> serviceToInstantiateDtos = checkCompatibilityAndGetServiceToInstantiate(newSubscription, subscriptionPatchDto.getServicesToInstantiate());
            serviceToInstantiateDtos.stream()
                    .forEach(s -> s.setSubscriptionDate(effectiveDate));
            for (ServiceToInstantiateDto serviceToInstantiateDto : serviceToInstantiateDtos) {
                instantiateServiceForSubscription(serviceToInstantiateDto, newSubscription, null, null, null);
            }
        }

        if (subscriptionPatchDto.getServicesToActivate() != null) {
            subscriptionPatchDto.getServicesToActivate()
                    .getService()
                    .stream()
                    .forEach(s -> s.setSubscriptionDate(effectiveDate));
            activateServices(subscriptionPatchDto.getServicesToActivate(), newSubscription, null, null, null);
        }

        versionCreatedEvent.fire(newSubscription);

    }

    private boolean isValidToNullAndEffectiveDateBeforeOrEqualValidFrom(SubscriptionPatchDto subscriptionPatchDto, Subscription existingSubscription) {
        Date effectiveDate = subscriptionPatchDto.getEffectiveDate() == null ? new Date() : subscriptionPatchDto.getEffectiveDate();
        return existingSubscription.getValidity().getTo() == null && (effectiveDate.before(existingSubscription.getValidity().getFrom()) || effectiveDate.equals(existingSubscription.getValidity().getFrom()));
    }

    private boolean isEffectiveDateBeforeValidTo(SubscriptionPatchDto subscriptionPatchDto, Subscription existingSubscription) {
        Date effectiveDate = subscriptionPatchDto.getEffectiveDate() == null ? new Date() : subscriptionPatchDto.getEffectiveDate();
        return existingSubscription.getValidity().getTo() != null && effectiveDate.before(existingSubscription.getValidity().getTo());
    }

    public void rollbackOffer(String code, OfferRollbackDto offerRollbackDto) {
        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(offerRollbackDto.getTerminationReason());
        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, offerRollbackDto.getTerminationReason());
        }

        List<Subscription> subscriptions = subscriptionService.findListByCode(code);

        if (subscriptions.isEmpty())
            throw new EntityDoesNotExistsException(Subscription.class, code);
        if (subscriptions.size() == 1)
            throw new InvalidParameterException("Subscription with code: " + code + " had one version, could not rollback it");


        Subscription actualSubscription = subscriptions.stream()
                .filter(s -> s.getValidity().getTo() == null)
                .findFirst()
                .get();

        Subscription lastSubscription = subscriptions.stream()
                .filter(s -> s.getValidity().getTo() != null)
                .sorted((a, b) -> b.getValidity().getTo().compareTo(a.getValidity().getTo()))
                .findFirst()
                .get();

        subscriptionService.terminateSubscription(actualSubscription, actualSubscription.getValidity().getFrom(), subscriptionTerminationReason, actualSubscription.getOrderNumber());

        lastSubscription.setToValidity(null);
        subscriptionService.subscriptionReactivation(lastSubscription, lastSubscription.getSubscriptionDate());
        reactivateServices(lastSubscription);
        if(lastSubscription.getInitialSubscriptionRenewal() != null)
            subscriptionService.cancelSubscriptionTermination(lastSubscription);
        versionRemovedEvent.fire(lastSubscription);
    }

    private void reactivateServices(Subscription lastSubscription) {
        for(ServiceInstance serviceInstance : lastSubscription.getServiceInstances()){
            serviceInstanceService.serviceReactivation(serviceInstance, serviceInstance.getSubscriptionDate(), true, true);
        }
    }
}