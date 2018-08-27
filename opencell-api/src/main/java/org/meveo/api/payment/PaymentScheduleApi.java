package org.meveo.api.payment;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PaymentScheduleTemplateDto;
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
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.payments.impl.PaymentScheduleTemplateService;

/**
 * @author anasseh
 * @lastModifiedVersion 5.2
 **/
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class PaymentScheduleApi extends BaseApi {

    @Inject
    private CalendarService calendarService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private PaymentScheduleTemplateService paymentScheduleTemplateService;

    /**
     * @param paymentScheduleTemplateDto paymentScheduleTemplateDto
     * @return the id of paymentScheduleTemplate if created successful otherwise null
     * @throws NoAllOperationUnmatchedException no all operation un matched exception
     * @throws UnbalanceAmountException balance amount exception
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
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getStartDate())) {
            missingParameters.add("startDate");
        }
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getDueDateDays())) {
            missingParameters.add("dueDateDays");
        }
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getAmount())) {
            missingParameters.add("amount");
        }
        if (StringUtils.isBlank(paymentScheduleTemplateDto.getNumberPayments())) {
            missingParameters.add("numberPayments");
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
        paymentScheduleTemplate.setStartDate(paymentScheduleTemplateDto.getStartDate());
        paymentScheduleTemplate.setDueDateDays(paymentScheduleTemplateDto.getDueDateDays());
        paymentScheduleTemplate.setAmount(paymentScheduleTemplateDto.getAmount());
        paymentScheduleTemplate.setNumberPayments(paymentScheduleTemplateDto.getNumberPayments());
        paymentScheduleTemplate.setPaymentLabel(paymentScheduleTemplateDto.getPaymentLabel());
        paymentScheduleTemplate.setAdvancePaymentInvoiceType(invoiceType);
        paymentScheduleTemplate.setAdvancePaymentInvoiceSubCategory(invoiceSubCategory);
        paymentScheduleTemplate.setGenerateAdvancePaymentInvoice(paymentScheduleTemplateDto.getGenerateAdvancePaymentInvoice().booleanValue());

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
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getStartDate())) {
            paymentScheduleTemplate.setStartDate(paymentScheduleTemplateDto.getStartDate());
        }
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getDueDateDays())) {
            paymentScheduleTemplate.setDueDateDays(paymentScheduleTemplateDto.getDueDateDays());
        }
        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getAmount())) {
            paymentScheduleTemplate.setAmount(paymentScheduleTemplateDto.getAmount());
        }

        if (!StringUtils.isBlank(paymentScheduleTemplateDto.getNumberPayments())) {
            paymentScheduleTemplate.setNumberPayments(paymentScheduleTemplateDto.getNumberPayments());
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

    public Long createOrUpdatePaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto) throws BusinessException, MeveoApiException {
        if (paymentScheduleTemplateService.findByCode(paymentScheduleTemplateDto.getCode()) == null) {
            return createPaymentScheduleTemplate(paymentScheduleTemplateDto);
        }
        return updatePaymentScheduleTemplate(paymentScheduleTemplateDto);

    }
}