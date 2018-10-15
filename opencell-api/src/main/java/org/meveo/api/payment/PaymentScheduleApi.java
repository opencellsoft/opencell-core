package org.meveo.api.payment;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PaymentScheduleInstanceBalanceDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceDto;
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
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.payments.impl.PaymentScheduleInstanceItemService;
import org.meveo.service.payments.impl.PaymentScheduleInstanceService;
import org.meveo.service.payments.impl.PaymentScheduleTemplateService;
import org.primefaces.model.SortOrder;

/**
 * The Class PaymentScheduleApi.
 *
 * @author anasseh
 * @lastModifiedVersion 5.2
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class PaymentScheduleApi extends BaseApi {

    /** The calendar service. */
    @Inject
    private CalendarService calendarService;

    /** The invoice type service. */
    @Inject
    private InvoiceTypeService invoiceTypeService;

    /** The invoice sub category service. */
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    /** The service template service. */
    @Inject
    private ServiceTemplateService serviceTemplateService;

    /** The payment schedule template service. */
    @Inject
    private PaymentScheduleTemplateService paymentScheduleTemplateService;

    /** The payment schedule instance service. */
    @Inject
    private PaymentScheduleInstanceService paymentScheduleInstanceService;

    /** The payment schedule instance item service. */
    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    /** The entity to dto converter. */
    @Inject
    private EntityToDtoConverter entityToDtoConverter;

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

        if (StringUtils.isBlank(paymentScheduleTemplateDto.getDueDateDays())) {
            missingParameters.add("dueDateDays");
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

        if (StringUtils.isBlank(paymentScheduleTemplateDto.getGenerateAdvancePaymentInvoice())) {
            missingParameters.add("generateAdvancePaymentInvoice");
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

        InvoiceType invoiceType = invoiceTypeService.findByCode(paymentScheduleTemplateDto.getAdvancePaymentInvoiceTypeCode());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, paymentScheduleTemplateDto.getAdvancePaymentInvoiceTypeCode());
        }
        PaymentScheduleTemplate paymentScheduleTemplate = new PaymentScheduleTemplate();
        paymentScheduleTemplate.setCode(paymentScheduleTemplateDto.getCode());
        paymentScheduleTemplate.setDescription(paymentScheduleTemplateDto.getDescription());
        paymentScheduleTemplate.setCalendar(calendar);
        paymentScheduleTemplate.setServiceTemplate(serviceTemplate);
        paymentScheduleTemplate.setDueDateDays(paymentScheduleTemplateDto.getDueDateDays());
        paymentScheduleTemplate.setAmount(paymentScheduleTemplateDto.getAmount());
        paymentScheduleTemplate.setPaymentLabel(paymentScheduleTemplateDto.getPaymentLabel());
        paymentScheduleTemplate.setAdvancePaymentInvoiceType(invoiceType);
        paymentScheduleTemplate.setAdvancePaymentInvoiceSubCategory(invoiceSubCategory);
        paymentScheduleTemplate.setGenerateAdvancePaymentInvoice(paymentScheduleTemplateDto.getGenerateAdvancePaymentInvoice().booleanValue());
        paymentScheduleTemplate.setDoPayment(paymentScheduleTemplateDto.getDoPayment().booleanValue());
        paymentScheduleTemplate.setApplyAgreement(paymentScheduleTemplateDto.isApplyAgreement());
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
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getDueDateDays())) {
            paymentScheduleTemplate.setDueDateDays(paymentScheduleTemplateDto.getDueDateDays());
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
        if (paymentScheduleTemplateDto.getGenerateAdvancePaymentInvoice() != null) {
            paymentScheduleTemplate.setGenerateAdvancePaymentInvoice(paymentScheduleTemplateDto.getGenerateAdvancePaymentInvoice().booleanValue());
        }

        if (paymentScheduleTemplateDto.isApplyAgreement() != null) {
            paymentScheduleTemplate.setApplyAgreement(paymentScheduleTemplateDto.isApplyAgreement());
        }

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
                    PaymentScheduleInstanceBalanceDto paymentScheduleInstanceBalanceDto = new PaymentScheduleInstanceBalanceDto();
                    Long nbPaidItems = paymentScheduleInstanceItemService.countPaidItems(psInstance);
                    Long nbIncomingItems = paymentScheduleInstanceItemService.countIncomingItems(psInstance);
                    BigDecimal sumAmountPaid = paymentScheduleInstanceItemService.sumAmountPaid(psInstance);
                    BigDecimal sumAmountIncoming = paymentScheduleInstanceItemService.sumAmountIncoming(psInstance);
                    if (nbPaidItems != null) {
                        paymentScheduleInstanceBalanceDto.setNbSchedulePaid(nbPaidItems.intValue());
                    }
                    if (nbIncomingItems != null) {
                        paymentScheduleInstanceBalanceDto.setNbScheduleIncoming(nbIncomingItems.intValue());
                    }
                    paymentScheduleInstanceBalanceDto.setSumAmountPaid(sumAmountPaid);
                    paymentScheduleInstanceBalanceDto.setSumAmountIncoming(sumAmountIncoming);
                    instanceDto.setPaymentScheduleInstanceBalanceDto(paymentScheduleInstanceBalanceDto);
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
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws BusinessException the business exception
     */
    public void updatePaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto)
            throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(paymentScheduleInstanceDto.getStatus())) {
            missingParameters.add("status");
        }
        if (StringUtils.isBlank(paymentScheduleInstanceDto.getId())) {
            missingParameters.add("id");
        }
        handleMissingParameters();

        PaymentScheduleInstance paymentScheduleInstance = paymentScheduleInstanceService.findById(paymentScheduleInstanceDto.getId());
        if (paymentScheduleInstance == null) {
            throw new EntityDoesNotExistsException(Subscription.class, paymentScheduleInstanceDto.getId());
        }
        paymentScheduleInstanceService.detach(paymentScheduleInstance);
        paymentScheduleInstance.setStatus(paymentScheduleInstanceDto.getStatus());

        if (!StringUtils.isBlank(paymentScheduleInstanceDto.getAmount())) {
            paymentScheduleInstance.setAmount(paymentScheduleInstanceDto.getAmount());
        }
        if (!StringUtils.isBlank(paymentScheduleInstanceDto.getDueDateDays())) {
            paymentScheduleInstance.setDueDateDays(paymentScheduleInstanceDto.getDueDateDays());
        }
        if (!StringUtils.isBlank(paymentScheduleInstanceDto.getCalendarCode())) {
            Calendar calendar = calendarService.findByCode(paymentScheduleInstanceDto.getCalendarCode());
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, paymentScheduleInstanceDto.getCalendarCode());
            }
            paymentScheduleInstance.setCalendar(calendar);
        }

        paymentScheduleInstanceService.update(paymentScheduleInstance);
    }

    /**
     * Terminate payment schedule instance.
     *
     * @param paymentScheduleInstanceDto the payment schedule instance dto
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws BusinessException the business exception
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
            throw new EntityDoesNotExistsException(Subscription.class, paymentScheduleInstanceDto.getId());
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
            throw new EntityDoesNotExistsException(Subscription.class, paymentScheduleInstanceDto.getId());
        }
        paymentScheduleInstanceService.cancel(paymentScheduleInstance);
    }
}