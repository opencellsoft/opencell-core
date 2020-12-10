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

package org.meveo.api.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PaymentScheduleInstanceBalanceDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceItemDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceResponseDto;
import org.meveo.api.dto.payment.PaymentScheduleInstancesDto;
import org.meveo.api.dto.payment.PaymentScheduleTemplateDto;
import org.meveo.api.dto.payment.PaymentScheduleTemplatesDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.payments.impl.PaymentScheduleInstanceItemService;
import org.meveo.service.payments.impl.PaymentScheduleInstanceService;
import org.meveo.service.payments.impl.PaymentScheduleTemplateService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.tax.TaxClassService;
import org.primefaces.model.SortOrder;

/**
 * The Class PaymentScheduleApi.
 *
 * @author anasseh
 * @lastModifiedVersion 5.3
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class PaymentScheduleApi extends BaseApi {

    /**
     * The calendar service.
     */
    @Inject
    private CalendarService calendarService;

    /**
     * The invoice type service.
     */
    @Inject
    private InvoiceTypeService invoiceTypeService;

    /**
     * The invoice sub category service.
     */
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    /**
     * The service template service.
     */
    @Inject
    private ServiceTemplateService serviceTemplateService;

    /**
     * The payment schedule template service.
     */
    @Inject
    private PaymentScheduleTemplateService paymentScheduleTemplateService;

    /**
     * The payment schedule instance service.
     */
    @Inject
    private PaymentScheduleInstanceService paymentScheduleInstanceService;

    /**
     * The payment schedule instance item service.
     */
    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    /**
     * The entity to dto converter.
     */
    @Inject
    private EntityToDtoConverter entityToDtoConverter;

    @Inject
    private TaxClassService taxClassService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    /**
     * Creates the payment schedule template.
     *
     * @param paymentScheduleTemplateDto paymentScheduleTemplateDto
     * @return the id of paymentScheduleTemplate if created successful otherwise null
     * @throws BusinessException business exception
     * @throws MeveoApiException opencell api exception
     */
    public Long createPaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto) throws BusinessException, MeveoApiException {

        if (StringUtils.isBlank(paymentScheduleTemplateDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getCalendarCode())) {
            missingParameters.add("calendarCode");
        }
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getServiceTemplateCode())) {
            missingParameters.add("serviceTemplateCode");
        }

        if (StringUtils.isBlank(paymentScheduleTemplateDto.getPaymentDayInMonth())) {
            missingParameters.add("paymentDayInMonth");
        }
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getAmount())) {
            missingParameters.add("amount");
        }

        if (StringUtils.isBlank(paymentScheduleTemplateDto.getPaymentLabel())) {
            missingParameters.add("paymentLabel");
        }
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getAdvancePaymentInvoiceTypeCode())) {
            missingParameters.add("advancePaymentInvoiceTypeCode");
        }
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getAdvancePaymentInvoiceSubCategoryCode())) {
            missingParameters.add("advancePaymentInvoiceSubCategoryCode");
        }

        if (StringUtils.isBlank(paymentScheduleTemplateDto.getDoPayment())) {
            missingParameters.add("doPayment");
        }
        if (StringUtils.isBlank(paymentScheduleTemplateDto.isApplyAgreement())) {
            missingParameters.add("applyAgreement");
        }

        handleMissingParameters();

        if (paymentScheduleTemplateService.findByCode(paymentScheduleTemplateDto.getCode()) != null) {
            throw new EntityAlreadyExistsException(PaymentScheduleTemplate.class, paymentScheduleTemplateDto.getCode());
        }

        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(paymentScheduleTemplateDto.getServiceTemplateCode());
        if (serviceTemplate == null) {
            throw new EntityDoesNotExistsException(ServiceTemplate.class, paymentScheduleTemplateDto.getServiceTemplateCode());
        }

        Calendar calendar = calendarService.findByCode(paymentScheduleTemplateDto.getCalendarCode());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, paymentScheduleTemplateDto.getCalendarCode());
        }

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(paymentScheduleTemplateDto.getAdvancePaymentInvoiceSubCategoryCode());
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, paymentScheduleTemplateDto.getAdvancePaymentInvoiceSubCategoryCode());
        }

        TaxClass taxClass = null;
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getTaxClassCode())) {
            taxClassService.findByCode(paymentScheduleTemplateDto.getTaxClassCode());
            if (taxClass == null) {
                throw new EntityDoesNotExistsException(TaxClass.class, paymentScheduleTemplateDto.getTaxClassCode());
            }
        }
        InvoiceType invoiceType = invoiceTypeService.findByCode(paymentScheduleTemplateDto.getAdvancePaymentInvoiceTypeCode());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, paymentScheduleTemplateDto.getAdvancePaymentInvoiceTypeCode());
        }
        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(paymentScheduleTemplateDto.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, paymentScheduleTemplateDto.getScriptInstanceCode());
            }
        }
        PaymentScheduleTemplate paymentScheduleTemplate = new PaymentScheduleTemplate();
        paymentScheduleTemplate.setCode(paymentScheduleTemplateDto.getCode());
        paymentScheduleTemplate.setDescription(paymentScheduleTemplateDto.getDescription());
        paymentScheduleTemplate.setCalendar(calendar);
        paymentScheduleTemplate.setServiceTemplate(serviceTemplate);
        paymentScheduleTemplate.setPaymentDayInMonth(paymentScheduleTemplateDto.getPaymentDayInMonth());
        paymentScheduleTemplate.setAmount(paymentScheduleTemplateDto.getAmount());
        paymentScheduleTemplate.setPaymentLabel(paymentScheduleTemplateDto.getPaymentLabel());
        paymentScheduleTemplate.setAdvancePaymentInvoiceType(invoiceType);
        paymentScheduleTemplate.setAdvancePaymentInvoiceSubCategory(invoiceSubCategory);
        paymentScheduleTemplate.setDoPayment(paymentScheduleTemplateDto.getDoPayment().booleanValue());
        paymentScheduleTemplate.setApplyAgreement(paymentScheduleTemplateDto.isApplyAgreement());
        paymentScheduleTemplate.setAmountEl(paymentScheduleTemplateDto.getAmountEl());
        paymentScheduleTemplate.setFilterEl(paymentScheduleTemplateDto.getFilterEl());
        paymentScheduleTemplate.setTaxClass(taxClass);
        paymentScheduleTemplate.setPaymentDayInMonthEl(paymentScheduleTemplateDto.getPaymentDayInMonthEl());
        paymentScheduleTemplate.setScriptInstance(scriptInstance);
        paymentScheduleTemplate.setUseBankingCalendar(paymentScheduleTemplateDto.getUseBankingCalendar());
        // populate customFields
        try {
            populateCustomFields(paymentScheduleTemplateDto.getCustomFields(), paymentScheduleTemplate, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        paymentScheduleTemplateService.create(paymentScheduleTemplate);

        return paymentScheduleTemplate.getId();

    }

    /**
     * Update payment schedule template.
     *
     * @param paymentScheduleTemplateDto the payment schedule template dto
     * @return the long
     * @throws BusinessException the business exception
     * @throws MeveoApiException the meveo api exception
     */
    public Long updatePaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto) throws BusinessException, MeveoApiException {
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        PaymentScheduleTemplate paymentScheduleTemplate = paymentScheduleTemplateService.findByCode(paymentScheduleTemplateDto.getCode());
        if (paymentScheduleTemplate == null) {
            throw new EntityDoesNotExistsException(PaymentScheduleTemplate.class, paymentScheduleTemplateDto.getCode());
        }

        ServiceTemplate serviceTemplate = null;
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getServiceTemplateCode())) {
            serviceTemplate = serviceTemplateService.findByCode(paymentScheduleTemplateDto.getServiceTemplateCode());
            if (serviceTemplate == null) {
                throw new EntityDoesNotExistsException(ServiceTemplate.class, paymentScheduleTemplateDto.getServiceTemplateCode());
            }
        }

        Calendar calendar = null;
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getCalendarCode())) {
            calendar = calendarService.findByCode(paymentScheduleTemplateDto.getCalendarCode());
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, paymentScheduleTemplateDto.getCalendarCode());
            }
        }

        InvoiceSubCategory invoiceSubCategory = null;
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getAdvancePaymentInvoiceSubCategoryCode())) {
            invoiceSubCategory = invoiceSubCategoryService.findByCode(paymentScheduleTemplateDto.getAdvancePaymentInvoiceSubCategoryCode());
            if (invoiceSubCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, paymentScheduleTemplateDto.getAdvancePaymentInvoiceSubCategoryCode());
            }
        }

        InvoiceType invoiceType = null;
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getAdvancePaymentInvoiceTypeCode())) {
            invoiceType = invoiceTypeService.findByCode(paymentScheduleTemplateDto.getAdvancePaymentInvoiceTypeCode());
            if (invoiceType == null) {
                throw new EntityDoesNotExistsException(InvoiceType.class, paymentScheduleTemplateDto.getAdvancePaymentInvoiceTypeCode());
            }
        }

        TaxClass taxClass = null;
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getTaxClassCode())) {
            taxClass = taxClassService.findByCode(paymentScheduleTemplateDto.getTaxClassCode());
            if (taxClass == null) {
                throw new EntityDoesNotExistsException(TaxClass.class, paymentScheduleTemplateDto.getTaxClassCode());
            }
        }

        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(paymentScheduleTemplateDto.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, paymentScheduleTemplateDto.getScriptInstanceCode());
            }
        }

        paymentScheduleTemplate
                .setCode(StringUtils.isBlank(paymentScheduleTemplateDto.getUpdatedCode()) ? paymentScheduleTemplateDto.getCode() : paymentScheduleTemplateDto.getUpdatedCode());
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getDescription())) {
            paymentScheduleTemplate.setDescription(paymentScheduleTemplateDto.getDescription());
        }
        if (calendar != null) {
            paymentScheduleTemplate.setCalendar(calendar);
        }
        if (serviceTemplate != null) {
            paymentScheduleTemplate.setServiceTemplate(serviceTemplate);
        }
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getPaymentDayInMonth())) {
            paymentScheduleTemplate.setPaymentDayInMonth(paymentScheduleTemplateDto.getPaymentDayInMonth());
        }
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getAmount())) {
            paymentScheduleTemplate.setAmount(paymentScheduleTemplateDto.getAmount());
        }
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getPaymentLabel())) {
            paymentScheduleTemplate.setPaymentLabel(paymentScheduleTemplateDto.getPaymentLabel());
        }

        if (invoiceType != null) {
            paymentScheduleTemplate.setAdvancePaymentInvoiceType(invoiceType);
        }
        if (invoiceSubCategory != null) {
            paymentScheduleTemplate.setAdvancePaymentInvoiceSubCategory(invoiceSubCategory);
        }        

        if (paymentScheduleTemplateDto.isApplyAgreement() != null) {
            paymentScheduleTemplate.setApplyAgreement(paymentScheduleTemplateDto.isApplyAgreement());
        }
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getAmountEl())) {
            paymentScheduleTemplate.setAmountEl(paymentScheduleTemplateDto.getAmountEl());
        }
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getFilterEl())) {
            paymentScheduleTemplate.setFilterEl(paymentScheduleTemplateDto.getFilterEl());
        }
        if (taxClass != null) {
            paymentScheduleTemplate.setTaxClass(taxClass);
        }
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getPaymentDayInMonthEl())) {
            paymentScheduleTemplate.setPaymentDayInMonthEl(paymentScheduleTemplateDto.getPaymentDayInMonthEl());
        }
        if (scriptInstance != null) {
            paymentScheduleTemplate.setScriptInstance(scriptInstance);
        }
        if (paymentScheduleTemplateDto.getUseBankingCalendar() != null) {
            paymentScheduleTemplate.setUseBankingCalendar(paymentScheduleTemplateDto.getUseBankingCalendar());
        }
        // populate customFields
        try {
            populateCustomFields(paymentScheduleTemplateDto.getCustomFields(), paymentScheduleTemplate, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        paymentScheduleTemplateService.update(paymentScheduleTemplate);

        return paymentScheduleTemplate.getId();

    }

    /**
     * Creates the or update payment schedule template.
     *
     * @param paymentScheduleTemplateDto the payment schedule template dto
     * @return the long
     * @throws BusinessException the business exception
     * @throws MeveoApiException the meveo api exception
     */
    public Long createOrUpdatePaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto) throws BusinessException, MeveoApiException {
        if (paymentScheduleTemplateService.findByCode(paymentScheduleTemplateDto.getCode()) == null) {
            return createPaymentScheduleTemplate(paymentScheduleTemplateDto);
        }
        return updatePaymentScheduleTemplate(paymentScheduleTemplateDto);

    }

    /**
     * Removes the payment schedule template.
     *
     * @param paymentScheduleTemplateCode the payment schedule template code
     * @throws BusinessException the business exception
     * @throws MeveoApiException the meveo api exception
     */
    public void removePaymentScheduleTemplate(String paymentScheduleTemplateCode) throws BusinessException, MeveoApiException {
        if (StringUtils.isBlank(paymentScheduleTemplateCode)) {
            missingParameters.add("paymentScheduleTemplateCode");
        }
        handleMissingParameters();

        PaymentScheduleTemplate paymentScheduleTemplate = paymentScheduleTemplateService.findByCode(paymentScheduleTemplateCode);
        if (paymentScheduleTemplate == null) {
            throw new EntityDoesNotExistsException(PaymentScheduleTemplate.class, paymentScheduleTemplateCode);
        }

        paymentScheduleTemplateService.remove(paymentScheduleTemplate);
    }

    /**
     * Find payment schedule template.
     *
     * @param paymentScheduleTemplateCode the payment schedule template code
     * @return the payment schedule template dto
     * @throws BusinessException the business exception
     * @throws MeveoApiException the meveo api exception
     */
    public PaymentScheduleTemplateDto findPaymentScheduleTemplate(String paymentScheduleTemplateCode) throws BusinessException, MeveoApiException {
        if (StringUtils.isBlank(paymentScheduleTemplateCode)) {
            missingParameters.add("paymentScheduleTemplateCode");
        }
        handleMissingParameters();

        PaymentScheduleTemplate paymentScheduleTemplate = paymentScheduleTemplateService.findByCode(paymentScheduleTemplateCode);
        if (paymentScheduleTemplate == null) {
            throw new EntityDoesNotExistsException(PaymentScheduleTemplate.class, paymentScheduleTemplateCode);
        }

        return new PaymentScheduleTemplateDto(paymentScheduleTemplate, entityToDtoConverter.getCustomFieldsDTO(paymentScheduleTemplate));
    }

    /**
     * List payment schedule template.
     *
     * @param pagingAndFiltering the paging and filtering
     * @return the payment schedule templates dto
     * @throws InvalidParameterException the invalid parameter exception
     */
    public PaymentScheduleTemplatesDto listPaymentScheduleTemplate(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, null, pagingAndFiltering, PaymentScheduleTemplate.class);
        Long totalCount = paymentScheduleTemplateService.count(paginationConfig);
        PaymentScheduleTemplatesDto result = new PaymentScheduleTemplatesDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<PaymentScheduleTemplate> templates = paymentScheduleTemplateService.list(paginationConfig);
            for (PaymentScheduleTemplate psTemplate : templates) {
                result.getTemplates().add(new PaymentScheduleTemplateDto(psTemplate, entityToDtoConverter.getCustomFieldsDTO(psTemplate)));
            }
        }

        return result;
    }

    /**
     * List payment schedule instance.
     *
     * @param pagingAndFiltering the paging and filtering
     * @return the payment schedule instances dto
     * @throws InvalidParameterException the invalid parameter exception
     */
    public PaymentScheduleInstancesDto listPaymentScheduleInstance(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
        PaginationConfiguration paginationConfig = toPaginationConfiguration("status", SortOrder.ASCENDING, null, pagingAndFiltering, PaymentScheduleInstance.class);
        Long totalCount = paymentScheduleInstanceService.count(paginationConfig);
        PaymentScheduleInstancesDto result = new PaymentScheduleInstancesDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<PaymentScheduleInstance> instances = paymentScheduleInstanceService.list(paginationConfig);
            for (PaymentScheduleInstance psInstance : instances) {
                PaymentScheduleInstanceDto instanceDto = new PaymentScheduleInstanceDto(psInstance);
                if (pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("balance")) {                   
                    instanceDto = addPaymentScheduleInstanceBalance(psInstance, instanceDto);
                }
                result.getInstances().add(instanceDto);
            }
        }

        return result;
    }

    /**
     * Update payment schedule instance.
     *
     * @param paymentScheduleInstanceDto the payment schedule instance dto
     * @throws BusinessException the business exception
     * @throws MeveoApiException 
     */
    public void updatePaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto)
            throws BusinessException, MeveoApiException {
        if (StringUtils.isBlank(paymentScheduleInstanceDto.getStatus())) {
            missingParameters.add("status");
        }
        if (StringUtils.isBlank(paymentScheduleInstanceDto.getId())) {
            missingParameters.add("id");
        }
        handleMissingParameters();

        PaymentScheduleInstance paymentScheduleInstance = paymentScheduleInstanceService.findById(paymentScheduleInstanceDto.getId());
        if (paymentScheduleInstance == null) {
            throw new EntityDoesNotExistsException(PaymentScheduleInstance.class, paymentScheduleInstanceDto.getId());
        }
        paymentScheduleInstanceService.detach(paymentScheduleInstance);
        paymentScheduleInstance.setStatus(paymentScheduleInstanceDto.getStatus());

        if (!StringUtils.isBlank(paymentScheduleInstanceDto.getPaymentDayInMonth())) {
            paymentScheduleInstance.setPaymentDayInMonth(paymentScheduleInstanceDto.getPaymentDayInMonth());
        }
        if (!StringUtils.isBlank(paymentScheduleInstanceDto.getAmount())) {
            paymentScheduleInstance.setAmount(paymentScheduleInstanceDto.getAmount());
            updatePaymentScheduleInstanceItem(paymentScheduleInstance);
        }
        if (!StringUtils.isBlank(paymentScheduleInstanceDto.getCalendarCode())) {
            Calendar calendar = calendarService.findByCode(paymentScheduleInstanceDto.getCalendarCode());
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, paymentScheduleInstanceDto.getCalendarCode());
            }
            paymentScheduleInstance.setCalendar(calendar);
        }
        // populate customFields
        try {
            populateCustomFields(paymentScheduleInstanceDto.getCustomFields(), paymentScheduleInstance, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        paymentScheduleInstanceService.update(paymentScheduleInstance);
    }

    private void updatePaymentScheduleInstanceItem(PaymentScheduleInstance paymentScheduleInstance) {
        if (paymentScheduleInstance.getPaymentScheduleInstanceItems() == null) {
            return;
        }
        List<PaymentScheduleInstanceItem> openPaymentScheduleInstanceItem = paymentScheduleInstance.getPaymentScheduleInstanceItems().stream().filter(item -> !item.isPaid())
                .collect(Collectors.toList());
        if (openPaymentScheduleInstanceItem == null || openPaymentScheduleInstanceItem.isEmpty()) {
            log.debug("No items to process");
            return;
        }

        paymentScheduleInstanceService.updatePaymentScheduleIntanceItems(paymentScheduleInstance, openPaymentScheduleInstanceItem);
    }

    /**
     * Terminate payment schedule instance.
     *
     * @param paymentScheduleInstanceDto the payment schedule instance dto
     * @throws MissingParameterException    the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws BusinessException            the business exception
     */
    public void terminatePaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto)
            throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(paymentScheduleInstanceDto.getId())) {
            missingParameters.add("id");
        }
        if (StringUtils.isBlank(paymentScheduleInstanceDto.getEndDate())) {
            missingParameters.add("endDate");
        }
        handleMissingParameters();
        PaymentScheduleInstance paymentScheduleInstance = paymentScheduleInstanceService.findById(paymentScheduleInstanceDto.getId());
        if (paymentScheduleInstance == null) {
            throw new EntityDoesNotExistsException(PaymentScheduleInstance.class, paymentScheduleInstanceDto.getId());
        }
        paymentScheduleInstanceService.terminate(paymentScheduleInstance, paymentScheduleInstanceDto.getEndDate());
    }
    
    /**
     * Cancel payment schedule instance.
     *
     * @param paymentScheduleInstanceDto the payment schedule instance dto
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws BusinessException the business exception
     */
    public void cancelPaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto)
            throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(paymentScheduleInstanceDto.getId())) {
            missingParameters.add("id");
        }       
        handleMissingParameters();
        PaymentScheduleInstance paymentScheduleInstance = paymentScheduleInstanceService.findById(paymentScheduleInstanceDto.getId());
        if (paymentScheduleInstance == null) {
            throw new EntityDoesNotExistsException(PaymentScheduleInstance.class, paymentScheduleInstanceDto.getId());
        }
        paymentScheduleInstanceService.cancel(paymentScheduleInstance);
    }

    /**
     * Find  PaymentScheduleInstance by ID.
     * @param id PaymentScheduleInstance ID
     * @return A paymentScheduleInstance dto
     * @throws MissingParameterException
     * @throws EntityDoesNotExistsException
     */
	public PaymentScheduleInstanceResponseDto findPaymentScheduleInstance(Long id) throws MissingParameterException, EntityDoesNotExistsException {
        if (StringUtils.isBlank(id)) {
            missingParameters.add("id");
        }
        handleMissingParameters();
        PaymentScheduleInstance paymentScheduleInstance = paymentScheduleInstanceService.findById(id);
        if (paymentScheduleInstance == null) {
            throw new EntityDoesNotExistsException(PaymentScheduleInstance.class, id);
        }
        PaymentScheduleInstanceDto instanceDto = new PaymentScheduleInstanceDto(paymentScheduleInstance);
        instanceDto = addPaymentScheduleInstanceBalance(paymentScheduleInstance, instanceDto);
        PaymentScheduleInstanceResponseDto paymentScheduleInstanceResponseDto = new PaymentScheduleInstanceResponseDto();
        paymentScheduleInstanceResponseDto.setPaymentScheduleInstanceDto(instanceDto);
        return paymentScheduleInstanceResponseDto;
    }

    /**
     * Add balance to PaymentScheduleInstanceDto
     *
     * @param paymentScheduleInstance
     * @param PaymentScheduleInstanceDto
     * @return PaymentScheduleInstanceDto with balance
     */
    private PaymentScheduleInstanceDto addPaymentScheduleInstanceBalance(PaymentScheduleInstance paymentScheduleInstance, PaymentScheduleInstanceDto paymentScheduleInstanceDto) {
        PaymentScheduleInstanceBalanceDto paymentScheduleInstanceBalanceDto = new PaymentScheduleInstanceBalanceDto();
        Long nbPaidItems = paymentScheduleInstanceItemService.countPaidItems(paymentScheduleInstance);
        Long nbIncomingItems = paymentScheduleInstanceItemService.countIncomingItems(paymentScheduleInstance);
        BigDecimal sumAmountPaid = paymentScheduleInstanceItemService.sumAmountPaid(paymentScheduleInstance);
        if (sumAmountPaid == null) {
            sumAmountPaid = BigDecimal.ZERO;
        }
        BigDecimal sumAmountIncoming = paymentScheduleInstanceItemService.sumAmountIncoming(paymentScheduleInstance);
        if (nbPaidItems != null) {
            paymentScheduleInstanceBalanceDto.setNbSchedulePaid(nbPaidItems.intValue());
        }
        if (nbIncomingItems != null) {
            paymentScheduleInstanceBalanceDto.setNbScheduleIncoming(nbIncomingItems.intValue());
        }
        paymentScheduleInstanceBalanceDto.setSumAmountPaid(sumAmountPaid);
        paymentScheduleInstanceBalanceDto.setSumAmountIncoming(sumAmountIncoming);
        paymentScheduleInstanceDto.setPaymentScheduleInstanceBalanceDto(paymentScheduleInstanceBalanceDto);
        return paymentScheduleInstanceDto;
    }

    /**
     * Replace payment Schedule instance items.
     *
     * @param paymentScheduleInstanceId
     * @param paymentScheduleInstanceItemDtos
     */
    public void replacePaymentScheduleInstanceItems(Long paymentScheduleInstanceId, List<PaymentScheduleInstanceItemDto> paymentScheduleInstanceItemDtos) {
        if (StringUtils.isBlank(paymentScheduleInstanceId)) {
            missingParameters.add("paymentScheduleInstanceId");
        }
        handleMissingParameters();
        PaymentScheduleInstance paymentScheduleInstance = paymentScheduleInstanceService.findById(paymentScheduleInstanceId);
        if (paymentScheduleInstance == null) {
            throw new EntityDoesNotExistsException(PaymentScheduleInstance.class, paymentScheduleInstanceId);
        }

        List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems = fromDto(paymentScheduleInstanceItemDtos);
        paymentScheduleInstanceService.replacePaymentScheduleInstanceItems(paymentScheduleInstance, paymentScheduleInstanceItems);
    }

    private List<PaymentScheduleInstanceItem> fromDto(List<PaymentScheduleInstanceItemDto> paymentScheduleInstanceItemDtos) {
        List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems = new ArrayList<>();
        for (PaymentScheduleInstanceItemDto dto : paymentScheduleInstanceItemDtos) {
            PaymentScheduleInstanceItem paymentScheduleInstanceItem = new PaymentScheduleInstanceItem();
            paymentScheduleInstanceItem.setAmount(dto.getAmount());
            paymentScheduleInstanceItem.setRequestPaymentDate(dto.getRequestPaymentDate());
            paymentScheduleInstanceItems.add(paymentScheduleInstanceItem);
        }
        return paymentScheduleInstanceItems;
    }
}