package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.catalog.Calendar;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.catalog.impl.CalendarService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BillingCycleApi extends BaseApi {

    @Inject
    private BillingCycleService billingCycleService;

    @Inject
    private CalendarService calendarService;
    
    @Inject
    private InvoiceTypeService invoiceTypeService;

    public void create(BillingCycleDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }
        if (postData.getInvoiceDateDelay() == null) {
            missingParameters.add("invoiceDateDelay");
        }
        if (postData.getDueDateDelay() == null) {
            missingParameters.add("dueDateDelay");
        }
        if (postData.getInvoiceDateDelay() == null) {
            missingParameters.add("invoiceDateDelay");
        }

        handleMissingParametersAndValidate(postData);
     
        

        if (billingCycleService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(BillingCycle.class, postData.getCode());
        }

        Calendar calendar = calendarService.findByCode(postData.getCalendar());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }
        
        InvoiceType invoiceType = null;
        if (!StringUtils.isBlank(postData.getInvoiceTypeCode())) {
			invoiceType = invoiceTypeService.findByCode(postData.getInvoiceTypeCode());
			if(invoiceType == null){
				 throw new EntityDoesNotExistsException(InvoiceType.class, postData.getInvoiceTypeCode());
			}
        }

        BillingCycle billingCycle = new BillingCycle();
        billingCycle.setCode(postData.getCode());
        billingCycle.setDescription(postData.getDescription());
        billingCycle.setBillingTemplateName(postData.getBillingTemplateName());
        billingCycle.setBillingTemplateNameEL(postData.getBillingTemplateNameEL());
        billingCycle.setInvoiceDateDelay(postData.getInvoiceDateDelay());
        billingCycle.setDueDateDelay(postData.getDueDateDelay());
        billingCycle.setDueDateDelayEL(postData.getDueDateDelayEL());
        billingCycle.setCalendar(calendar);
        billingCycle.setTransactionDateDelay(postData.getTransactionDateDelay());
        billingCycle.setInvoiceDateProductionDelay(postData.getInvoiceDateProductionDelay());
        billingCycle.setInvoicingThreshold(postData.getInvoicingThreshold());
        billingCycle.setInvoiceType(invoiceType);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), billingCycle, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        billingCycleService.create(billingCycle);
    }

    public void update(BillingCycleDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }
        if (postData.getInvoiceDateDelay() == null) {
            missingParameters.add("invoiceDateDelay");
        }
        if (postData.getDueDateDelay() == null) {
            missingParameters.add("dueDateDelay");
        }
        if (postData.getInvoiceDateDelay() == null) {
            missingParameters.add("invoiceDateDelay");
        }

        handleMissingParametersAndValidate(postData);
        

        

        BillingCycle billingCycle = billingCycleService.findByCode(postData.getCode());

        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, postData.getCode());
        }

        Calendar calendar = calendarService.findByCode(postData.getCalendar());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }

        InvoiceType invoiceType = null;
        if (!StringUtils.isBlank(postData.getInvoiceTypeCode())) {
			invoiceType = invoiceTypeService.findByCode(postData.getInvoiceTypeCode());
			if(invoiceType == null){
				 throw new EntityDoesNotExistsException(InvoiceType.class, postData.getInvoiceTypeCode());
			}
        }
        billingCycle.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        billingCycle.setDescription(postData.getDescription());
        billingCycle.setBillingTemplateName(postData.getBillingTemplateName());
        billingCycle.setBillingTemplateNameEL(postData.getBillingTemplateNameEL());
        billingCycle.setInvoiceDateDelay(postData.getInvoiceDateDelay());
        billingCycle.setDueDateDelay(postData.getDueDateDelay());
        billingCycle.setDueDateDelayEL(postData.getDueDateDelayEL());
        billingCycle.setCalendar(calendar);
        billingCycle.setTransactionDateDelay(postData.getTransactionDateDelay());
        billingCycle.setInvoiceDateProductionDelay(postData.getInvoiceDateProductionDelay());
        billingCycle.setInvoicingThreshold(postData.getInvoicingThreshold());
        billingCycle.setInvoiceType(invoiceType);
	   // populate customFields
	    try {
	        populateCustomFields(postData.getCustomFields(), billingCycle, true, true);
	
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }  
        
        billingCycle = billingCycleService.update(billingCycle);
    }
    
    public BillingCycleDto find(String billingCycleCode) throws MeveoApiException {

        if (StringUtils.isBlank(billingCycleCode)) {
            missingParameters.add("billingCycleCode");
            handleMissingParameters();
        }

        BillingCycleDto result = new BillingCycleDto();

        BillingCycle billingCycle = billingCycleService.findByCode(billingCycleCode);
        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
        }

        result = new BillingCycleDto(billingCycle,entityToDtoConverter.getCustomFieldsDTO(billingCycle, true));

        return result;
    }

    public void remove(String billingCycleCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(billingCycleCode)) {
            missingParameters.add("billingCycleCode");
            handleMissingParameters();
        }

        BillingCycle billingCycle = billingCycleService.findByCode(billingCycleCode);
        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
        }

        billingCycleService.remove(billingCycle);
    }

    public void createOrUpdate(BillingCycleDto postData) throws MeveoApiException, BusinessException {
       
        BillingCycle billingCycle = billingCycleService.findByCode(postData.getCode());
        if (billingCycle == null) {
            create(postData);
        } else {
            update(postData);
        }
    }
    
    
}
