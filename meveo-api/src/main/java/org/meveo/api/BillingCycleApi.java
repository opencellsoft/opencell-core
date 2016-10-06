package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.Provider;
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

    public void create(BillingCycleDto postData, User currentUser) throws MeveoApiException, BusinessException {

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

        handleMissingParameters();
     
        Provider provider = currentUser.getProvider();

        if (billingCycleService.findByBillingCycleCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(BillingCycle.class, postData.getCode());
        }

        Calendar calendar = calendarService.findByCode(postData.getCalendar(), provider);
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }
        
        InvoiceType invoiceType = null;
        if (!StringUtils.isBlank(postData.getInvoiceTypeCode())) {
			invoiceType = invoiceTypeService.findByCode(postData.getInvoiceTypeCode(), currentUser.getProvider());
			if(invoiceType == null){
				 throw new EntityDoesNotExistsException(InvoiceType.class, postData.getInvoiceTypeCode());
			}
        }

        BillingCycle billingCycle = new BillingCycle();
        billingCycle.setCode(postData.getCode());
        billingCycle.setDescription(postData.getDescription());
        billingCycle.setBillingTemplateName(postData.getBillingTemplateName());
        billingCycle.setInvoiceDateDelay(postData.getInvoiceDateDelay());
        billingCycle.setDueDateDelay(postData.getDueDateDelay());
        billingCycle.setCalendar(calendar);
        billingCycle.setTransactionDateDelay(postData.getTransactionDateDelay());
        billingCycle.setInvoiceDateProductionDelay(postData.getInvoiceDateProductionDelay());
        billingCycle.setInvoicingThreshold(postData.getInvoicingThreshold());
        billingCycle.setInvoiceType(invoiceType);

        billingCycleService.create(billingCycle, currentUser);
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), billingCycle, true, currentUser, true);

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    public void update(BillingCycleDto postData, User currentUser) throws MeveoApiException, BusinessException {

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

        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();

        BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(postData.getCode(), provider);

        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, postData.getCode());
        }

        Calendar calendar = calendarService.findByCode(postData.getCalendar(), provider);
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }

        InvoiceType invoiceType = null;
        if (!StringUtils.isBlank(postData.getInvoiceTypeCode())) {
			invoiceType = invoiceTypeService.findByCode(postData.getInvoiceTypeCode(), currentUser.getProvider());
			if(invoiceType == null){
				 throw new EntityDoesNotExistsException(InvoiceType.class, postData.getInvoiceTypeCode());
			}
        }
        
        billingCycle.setDescription(postData.getDescription());
        billingCycle.setBillingTemplateName(postData.getBillingTemplateName());
        billingCycle.setInvoiceDateDelay(postData.getInvoiceDateDelay());
        billingCycle.setDueDateDelay(postData.getDueDateDelay());
        billingCycle.setCalendar(calendar);
        billingCycle.setTransactionDateDelay(postData.getTransactionDateDelay());
        billingCycle.setInvoiceDateProductionDelay(postData.getInvoiceDateProductionDelay());
        billingCycle.setInvoicingThreshold(postData.getInvoicingThreshold());
        billingCycle.setInvoiceType(invoiceType);
        
        billingCycleService.update(billingCycle, currentUser);
	   // populate customFields
	    try {
	        populateCustomFields(postData.getCustomFields(), billingCycle, true, currentUser, true);
	
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }  
    }
    
    public BillingCycleDto find(String billingCycleCode, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(billingCycleCode)) {
            missingParameters.add("billingCycleCode");
            handleMissingParameters();
        }

        BillingCycleDto result = new BillingCycleDto();

        BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingCycleCode, provider);
        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
        }

        result = new BillingCycleDto(billingCycle,entityToDtoConverter.getCustomFieldsDTO(billingCycle));

        return result;
    }

    public void remove(String billingCycleCode, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(billingCycleCode)) {
            missingParameters.add("billingCycleCode");
            handleMissingParameters();
        }

        BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingCycleCode, currentUser.getProvider());
        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
        }

        billingCycleService.remove(billingCycle, currentUser);
    }

    public void createOrUpdate(BillingCycleDto postData, User currentUser) throws MeveoApiException, BusinessException {
       
        BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(postData.getCode(), currentUser.getProvider());
        if (billingCycle == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
    
    
}