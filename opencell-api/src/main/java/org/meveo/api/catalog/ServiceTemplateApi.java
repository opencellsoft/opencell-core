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

package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.catalog.*;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListServiceTemplateResponseDto;
import org.meveo.api.exception.*;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.*;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Abdellatif BARI
 * @author Youssef IZEM
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 10.0.0
 */
@Stateless
public class ServiceTemplateApi extends BaseCrudApi<ServiceTemplate, ServiceTemplateDto> {

    private static final String DEFAULT_SORT_ORDER_ID = "id";

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private WalletTemplateService walletTemplateService;

    @Inject
    private ServiceChargeTemplateRecurringService serviceChargeTemplateRecurringService;

    @Inject
    private ServiceChargeTemplateSubscriptionService serviceChargeTemplateSubscriptionService;

    @Inject
    private ServiceChargeTemplateTerminationService serviceChargeTemplateTerminationService;

    @Inject
    private ServiceChargeTemplateUsageService serviceUsageChargeTemplateService;

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private BusinessServiceModelService businessServiceModelService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private SubscriptionApi subscriptionApi;

    /**
     * Sets the service charge template.
     *
     * @param serviceTemplate the service template.
     * @param serviceChargeTemplate the service charge template.
     * @param serviceChargeTemplateDto the service charge template Dto.
     * @param chargeTemplate the charge template
     * @throws EntityDoesNotExistsException entity does not exists exception
     */

    private void setServiceChargeTemplate(ServiceTemplate serviceTemplate, ServiceChargeTemplate serviceChargeTemplate, BaseServiceChargeTemplateDto serviceChargeTemplateDto, ChargeTemplate chargeTemplate)
            throws MeveoApiException {

        List<WalletTemplate> wallets = new ArrayList<>();
        for (String walletCode : serviceChargeTemplateDto.getWallets().getWallet()) {
            if (!walletCode.equals(WalletTemplate.PRINCIPAL)) {
                WalletTemplate walletTemplate = walletTemplateService.findByCode(walletCode);
                if (walletTemplate == null) {
                    throw new EntityDoesNotExistsException(WalletTemplate.class, walletCode);
                }
                wallets.add(walletTemplate);
            }
        }
        serviceChargeTemplate.setChargeTemplate(chargeTemplate);
        serviceChargeTemplate.setWalletTemplates(wallets);
        serviceChargeTemplate.setServiceTemplate(serviceTemplate);
        CounterTemplate counterTemplate = getCounterTemplate(serviceChargeTemplate, serviceChargeTemplateDto.getCounterTemplate());
        if (counterTemplate == null || !counterTemplate.getAccumulator()) {
            serviceChargeTemplate.setCounterTemplate(counterTemplate);
        } else {
            throw new InvalidParameterException("The counterTemplate parameter: " + counterTemplate.getCode() + " should not be an accumulator counter");
        }
        List<CounterTemplate> counterTemplates = new ArrayList<>();
        if (serviceChargeTemplateDto.getAccumulatorCounterTemplates() != null) {
            for (String counterCode : serviceChargeTemplateDto.getAccumulatorCounterTemplates().getCounterTemplate()) {
                CounterTemplate accumulatorCounter = getCounterTemplate(serviceChargeTemplate, counterCode);
                if (accumulatorCounter.getAccumulator() != null && accumulatorCounter.getAccumulator()) {
                    counterTemplates.add(accumulatorCounter);
                }
            }
        }
        serviceChargeTemplate.setAccumulatorCounterTemplates(counterTemplates);
    }

    private CounterTemplate getCounterTemplate(ServiceChargeTemplate serviceChargeTemplate, String counterTemplateCode) {
        if (StringUtils.isBlank(counterTemplateCode)) {
            return null;
        }
        CounterTemplate counterTemplate = counterTemplateService.getCounterTemplate(counterTemplateCode);

        if (serviceChargeTemplate instanceof ServiceChargeTemplateTermination || serviceChargeTemplate instanceof ServiceChargeTemplateSubscription) {
            log.trace("Select only accumulator counter");
            if (counterTemplate != null && counterTemplate.getAccumulator() != null && counterTemplate.getAccumulator()) {
                return counterTemplate;
            } else {
                throw new InvalidParameterException("The counterTemplate parameter: " + counterTemplateCode + " must be an accumulator counter");
            }
        }
        return counterTemplate;
    }

    private void createServiceChargeTemplateRecurring(ServiceTemplate serviceTemplate, ServiceChargeTemplateRecurringDto serviceChargeTemplateDto) throws MeveoApiException, BusinessException {
        RecurringChargeTemplate chargeTemplate = ofNullable(recurringChargeTemplateService.findByCode(serviceChargeTemplateDto.getCode()))
            .orElseThrow(() -> new EntityDoesNotExistsException(RecurringChargeTemplate.class, serviceChargeTemplateDto.getCode()));
        ServiceChargeTemplateRecurring serviceChargeTemplate = new ServiceChargeTemplateRecurring();
        setServiceChargeTemplate(serviceTemplate, serviceChargeTemplate, serviceChargeTemplateDto, chargeTemplate);
        serviceChargeTemplateRecurringService.create(serviceChargeTemplate);
    }

    private void createServiceChargeTemplateSubscription(ServiceTemplate serviceTemplate, ServiceChargeTemplateSubscriptionDto serviceChargeTemplateDto) throws MeveoApiException, BusinessException {
        OneShotChargeTemplate chargeTemplate = ofNullable(oneShotChargeTemplateService.findByCode(serviceChargeTemplateDto.getCode()))
            .orElseThrow(() -> new EntityDoesNotExistsException(OneShotChargeTemplate.class, serviceChargeTemplateDto.getCode()));
        ServiceChargeTemplateSubscription serviceChargeTemplate = new ServiceChargeTemplateSubscription();
        setServiceChargeTemplate(serviceTemplate, serviceChargeTemplate, serviceChargeTemplateDto, chargeTemplate);
        serviceChargeTemplateSubscriptionService.create(serviceChargeTemplate);
    }

    private void createServiceChargeTemplateTermination(ServiceTemplate serviceTemplate, ServiceChargeTemplateTerminationDto serviceChargeTemplateDto) throws MeveoApiException, BusinessException {
        OneShotChargeTemplate chargeTemplate = ofNullable(oneShotChargeTemplateService.findByCode(serviceChargeTemplateDto.getCode()))
            .orElseThrow(() -> new EntityDoesNotExistsException(OneShotChargeTemplate.class, serviceChargeTemplateDto.getCode()));
        ServiceChargeTemplateTermination serviceChargeTemplate = new ServiceChargeTemplateTermination();
        setServiceChargeTemplate(serviceTemplate, serviceChargeTemplate, serviceChargeTemplateDto, chargeTemplate);
        serviceChargeTemplateTerminationService.create(serviceChargeTemplate);
    }

    private void createServiceChargeTemplateUsage(ServiceTemplate serviceTemplate, ServiceUsageChargeTemplateDto serviceChargeTemplateDto) throws MeveoApiException, BusinessException {
        UsageChargeTemplate chargeTemplate = ofNullable(usageChargeTemplateService.findByCode(serviceChargeTemplateDto.getCode()))
            .orElseThrow(() -> new EntityDoesNotExistsException(UsageChargeTemplate.class, serviceChargeTemplateDto.getCode()));
        ServiceChargeTemplateUsage serviceChargeTemplate = new ServiceChargeTemplateUsage();
        setServiceChargeTemplate(serviceTemplate, serviceChargeTemplate, serviceChargeTemplateDto, chargeTemplate);
        serviceUsageChargeTemplateService.create(serviceChargeTemplate);
    }

    private void createServiceChargeTemplateRecurring(ServiceTemplateDto postData, ServiceTemplate serviceTemplate) throws MeveoApiException, BusinessException {

        if (postData.getServiceChargeTemplateRecurrings() != null) {
            for (ServiceChargeTemplateRecurringDto serviceChargeTemplateDto : postData.getServiceChargeTemplateRecurrings().getServiceChargeTemplateRecurring()) {
                // Create service charge template.
                createServiceChargeTemplateRecurring(serviceTemplate, serviceChargeTemplateDto);
            }
        }
    }

    private void createServiceChargeTemplateSubscription(ServiceTemplateDto postData, ServiceTemplate serviceTemplate) throws MeveoApiException, BusinessException {

        if (postData.getServiceChargeTemplateSubscriptions() != null) {
            for (ServiceChargeTemplateSubscriptionDto serviceChargeTemplateDto : postData.getServiceChargeTemplateSubscriptions().getServiceChargeTemplateSubscription()) {
                // Create service charge template.
                createServiceChargeTemplateSubscription(serviceTemplate, serviceChargeTemplateDto);
            }
        }
    }

    private void createServiceChargeTemplateTermination(ServiceTemplateDto postData, ServiceTemplate serviceTemplate) throws MeveoApiException, BusinessException {

        if (postData.getServiceChargeTemplateTerminations() != null) {
            for (ServiceChargeTemplateTerminationDto serviceChargeTemplateDto : postData.getServiceChargeTemplateTerminations().getServiceChargeTemplateTermination()) {
                // Create service charge template.
                createServiceChargeTemplateTermination(serviceTemplate, serviceChargeTemplateDto);
            }
        }
    }

    private void createServiceChargeTemplateUsage(ServiceTemplateDto postData, ServiceTemplate serviceTemplate) throws MeveoApiException, BusinessException {

        if (postData.getServiceChargeTemplateUsages() != null) {
            for (ServiceUsageChargeTemplateDto serviceChargeTemplateDto : postData.getServiceChargeTemplateUsages().getServiceChargeTemplateUsage()) {
                // Create service charge template.
                createServiceChargeTemplateUsage(serviceTemplate, serviceChargeTemplateDto);
            }
        }
    }

    @Override
    public ServiceTemplate create(ServiceTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        // check if code already exists
        if (serviceTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(ServiceTemplateService.class, postData.getCode());
        }

        Calendar invoicingCalendar = null;
        if (postData.getInvoicingCalendar() != null) {
            invoicingCalendar = calendarService.findByCode(postData.getInvoicingCalendar());
            if (invoicingCalendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getInvoicingCalendar());
            }
        }

        BusinessServiceModel businessService = null;
        if (!StringUtils.isBlank(postData.getSomCode())) {
            businessService = businessServiceModelService.findByCode(postData.getSomCode());
            if (businessService == null) {
                throw new EntityDoesNotExistsException(BusinessServiceModel.class, postData.getSomCode());
            }
        }

        ServiceTemplate serviceTemplate = new ServiceTemplate();

        Boolean autoEndOfEngagement = postData.getAutoEndOfEngagement();
        if (autoEndOfEngagement != null) {
            serviceTemplate.setAutoEndOfEngagement(autoEndOfEngagement);
        }

        serviceTemplate.setBusinessServiceModel(businessService);
        serviceTemplate.setCode(postData.getCode());
        serviceTemplate.setDescription(postData.getDescription());
        serviceTemplate.setLongDescription(postData.getLongDescription());
        serviceTemplate.setInvoicingCalendar(invoicingCalendar);
        serviceTemplate.setMinimumAmountEl(postData.getMinimumAmountEl());
        serviceTemplate.setMinimumAmountElSpark(postData.getMinimumAmountElSpark());
        serviceTemplate.setMinimumLabelEl(postData.getMinimumLabelEl());
        serviceTemplate.setMinimumLabelElSpark(postData.getMinimumLabelElSpark());
        serviceTemplate.setServiceRenewal(subscriptionApi.subscriptionRenewalFromDto(serviceTemplate.getServiceRenewal(), postData.getRenewalRule(), false));

        if (!StringUtils.isBlank(postData.getMinimumChargeTemplate())) {
            OneShotChargeTemplate minimumChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getMinimumChargeTemplate());
            if (minimumChargeTemplate == null) {
                throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getMinimumChargeTemplate());
            } else {
                serviceTemplate.setMinimumChargeTemplate(minimumChargeTemplate);
            }
        }

        if (postData.isDisabled() != null) {
            serviceTemplate.setDisabled(postData.isDisabled());
        }

        try {
            saveImage(serviceTemplate, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), serviceTemplate, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        serviceTemplateService.create(serviceTemplate);

        // check for recurring charges
        createServiceChargeTemplateRecurring(postData, serviceTemplate);

        // check for subscription charges
        createServiceChargeTemplateSubscription(postData, serviceTemplate);

        // check for termination charges
        createServiceChargeTemplateTermination(postData, serviceTemplate);

        // check for usage charges
        createServiceChargeTemplateUsage(postData, serviceTemplate);

        return serviceTemplate;
    }

    @Override
    public ServiceTemplate update(ServiceTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        // check if code already exists
        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(postData.getCode());
        if (serviceTemplate == null) {
            throw new EntityDoesNotExistsException(ServiceTemplateService.class, postData.getCode());
        }

        Boolean autoEndOfEngagement = postData.getAutoEndOfEngagement();
        if (autoEndOfEngagement != null) {
            serviceTemplate.setAutoEndOfEngagement(autoEndOfEngagement);
        }

        serviceTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        if (postData.getDescription() != null) {
            serviceTemplate.setDescription(StringUtils.isBlank(postData.getDescription()) ? null : postData.getDescription());
        }
        if (postData.getLongDescription() != null) {
            serviceTemplate.setLongDescription(StringUtils.isBlank(postData.getLongDescription()) ? null : postData.getLongDescription());
        }
        if (postData.getMinimumAmountEl() != null) {
            serviceTemplate.setMinimumAmountEl(postData.getMinimumAmountEl());
        }
        if (postData.getMinimumAmountElSpark() != null) {
            serviceTemplate.setMinimumAmountElSpark(postData.getMinimumAmountElSpark());
        }
        if (postData.getMinimumLabelEl() != null) {
            serviceTemplate.setMinimumLabelEl(postData.getMinimumLabelEl());
        }
        if (postData.getMinimumLabelElSpark() != null) {
            serviceTemplate.setMinimumLabelElSpark(postData.getMinimumLabelElSpark());
        }

        if (postData.getMinimumChargeTemplate() != null) {
            if (StringUtils.isBlank(postData.getMinimumChargeTemplate())) {
                serviceTemplate.setMinimumChargeTemplate(null);

            } else {
                OneShotChargeTemplate minimumChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getMinimumChargeTemplate());
                if (minimumChargeTemplate == null) {
                    throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getMinimumChargeTemplate());
                }
                serviceTemplate.setMinimumChargeTemplate(minimumChargeTemplate);
            }
        }
        if (postData.getRenewalRule() != null) {
            serviceTemplate.setServiceRenewal(subscriptionApi.subscriptionRenewalFromDto(serviceTemplate.getServiceRenewal(), postData.getRenewalRule(), false));
        }

        if (postData.getInvoicingCalendar() != null) {
            if (StringUtils.isBlank(postData.getInvoicingCalendar())) {
                serviceTemplate.setInvoicingCalendar(null);

            } else {
                Calendar invoicingCalendar = calendarService.findByCode(postData.getInvoicingCalendar());
                if (invoicingCalendar == null) {
                    throw new EntityDoesNotExistsException(Calendar.class, postData.getInvoicingCalendar());
                }

                serviceTemplate.setInvoicingCalendar(invoicingCalendar);
            }
        }

        if (postData.getSomCode() != null) {
            if (StringUtils.isBlank(postData.getSomCode())) {
                serviceTemplate.setBusinessServiceModel(null);
            } else {
                BusinessServiceModel businessService = null;
                if (!StringUtils.isBlank(postData.getSomCode())) {
                    businessService = businessServiceModelService.findByCode(postData.getSomCode());
                    if (businessService == null) {
                        throw new EntityDoesNotExistsException(BusinessServiceModel.class, postData.getSomCode());
                    }
                }
                serviceTemplate.setBusinessServiceModel(businessService);
            }
        }

        setAllWalletTemplatesToNull(serviceTemplate);

        try {
            saveImage(serviceTemplate, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), serviceTemplate, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        serviceTemplate = serviceTemplateService.update(serviceTemplate);

        // check for recurring charges
        if (postData.getServiceChargeTemplateRecurrings() != null) {
            serviceChargeTemplateRecurringService.removeByServiceTemplate(serviceTemplate);
            createServiceChargeTemplateRecurring(postData, serviceTemplate);
        }

        // check for subscription charges
        if (postData.getServiceChargeTemplateSubscriptions() != null) {
            serviceChargeTemplateSubscriptionService.removeByServiceTemplate(serviceTemplate);
            createServiceChargeTemplateSubscription(postData, serviceTemplate);
        }

        // check for termination charges
        if (postData.getServiceChargeTemplateTerminations() != null) {
        	serviceChargeTemplateTerminationService.removeByServiceTemplate(serviceTemplate);
            createServiceChargeTemplateTermination(postData, serviceTemplate);
            
        }

        // check for usage charges
        if (postData.getServiceChargeTemplateUsages() != null) {
        	serviceUsageChargeTemplateService.removeByServiceTemplate(serviceTemplate);
            createServiceChargeTemplateUsage(postData, serviceTemplate);
        }
        return serviceTemplate;
    }

    @Override
    public ServiceTemplateDto find(String serviceTemplateCode) throws MeveoApiException {
        return find(serviceTemplateCode, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    public ServiceTemplateDto find(String serviceTemplateCode, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

        if (StringUtils.isBlank(serviceTemplateCode)) {
            missingParameters.add("serviceTemplateCode");
            handleMissingParameters();
        }

        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateCode);
        if (serviceTemplate == null) {
            throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateCode);
        }
        ServiceTemplateDto result = tranform(serviceTemplate, inheritCF);
        return result;
    }

    private ServiceTemplateDto tranform(ServiceTemplate serviceTemplate, CustomFieldInheritanceEnum inheritCF) {
        return new ServiceTemplateDto(serviceTemplate, entityToDtoConverter.getCustomFieldsDTO(serviceTemplate, inheritCF), true);
    }

    private void setAllWalletTemplatesToNull(ServiceTemplate serviceTemplate) {
        List<ServiceChargeTemplateRecurring> listRec = new ArrayList<>();
        for (ServiceChargeTemplateRecurring recurring : serviceTemplate.getServiceRecurringCharges()) {
            recurring.setWalletTemplates(null);
            listRec.add(recurring);
        }
        serviceTemplate.setServiceRecurringCharges(listRec);

        List<ServiceChargeTemplateSubscription> listSubs = new ArrayList<>();
        for (ServiceChargeTemplateSubscription subscription : serviceTemplate.getServiceSubscriptionCharges()) {
            subscription.setWalletTemplates(null);
            listSubs.add(subscription);
        }
        serviceTemplate.setServiceSubscriptionCharges(listSubs);

        List<ServiceChargeTemplateTermination> listTerms = new ArrayList<>();
        for (ServiceChargeTemplateTermination termination : serviceTemplate.getServiceTerminationCharges()) {
            termination.setWalletTemplates(null);
            listTerms.add(termination);
        }
        serviceTemplate.setServiceTerminationCharges(listTerms);

        List<ServiceChargeTemplateUsage> listUsages = new ArrayList<>();
        for (ServiceChargeTemplateUsage usage : serviceTemplate.getServiceUsageCharges()) {
            usage.setWalletTemplates(null);
            listUsages.add(usage);
        }
        serviceTemplate.setServiceUsageCharges(listUsages);
    }

    @Override
    public void remove(String serviceTemplateCode) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(serviceTemplateCode)) {
            missingParameters.add("serviceTemplateCode");
            handleMissingParameters();
        }

        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateCode);
        if (serviceTemplate == null) {
            throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateCode);
        }

        setAllWalletTemplatesToNull(serviceTemplate);

        serviceTemplateService.remove(serviceTemplate);
    }

    public GetListServiceTemplateResponseDto list(PagingAndFiltering pagingAndFiltering) throws MeveoApiException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        String sortBy = DEFAULT_SORT_ORDER_ID;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }

        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, pagingAndFiltering.getMultiSortOrder(), null, pagingAndFiltering, ServiceTemplate.class);

        Long totalCount = serviceTemplateService.count(paginationConfiguration);

        GetListServiceTemplateResponseDto result = new GetListServiceTemplateResponseDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            serviceTemplateService.list(paginationConfiguration).forEach(service -> result.addServiceTemplate(tranform(service, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
        }

        return result;

    }

    public GetListServiceTemplateResponseDto listGetAll(PagingAndFiltering pagingAndFiltering) {
        GetListServiceTemplateResponseDto result = new GetListServiceTemplateResponseDto();
        result.setPaging( pagingAndFiltering );

        List<ServiceTemplate> serviceTemplates = serviceTemplateService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (serviceTemplates != null) {
            for (ServiceTemplate serviceTemplate : serviceTemplates) {
                result.getListServiceTemplate().add(new ServiceTemplateDto(serviceTemplate));
            }
        }

        return result;
    }
}