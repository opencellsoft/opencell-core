package org.meveo.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.SequenceDto;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.dto.billing.InvoiceTypesDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.payments.impl.OCCTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class InvoiceTypeApi extends BaseApi {

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private OCCTemplateService occTemplateService;
    
    @Inject
    private SellerService sellerService;

    
    private void handleParameters(InvoiceTypeDto invoiceTypeDto) throws MissingParameterException{
        if (StringUtils.isBlank(invoiceTypeDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(invoiceTypeDto.getOccTemplateCode())) {
            missingParameters.add("occTemplateCode");
        }     
        handleMissingParameters();	
    }
    
    
    public ActionStatus create(InvoiceTypeDto invoiceTypeDto, User currentUser) throws MeveoApiException, BusinessException {
        handleParameters(invoiceTypeDto);
        ActionStatus result = new ActionStatus();
        Provider provider = currentUser.getProvider();
        if (invoiceTypeService.findByCode(invoiceTypeDto.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(InvoiceType.class, invoiceTypeDto.getCode());
        }        
        OCCTemplate occTemplate = occTemplateService.findByCode(invoiceTypeDto.getOccTemplateCode(), provider);        
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, invoiceTypeDto.getOccTemplateCode());
        }        
        List<InvoiceType> invoiceTypesToApplies = new ArrayList<InvoiceType>();       
        if(invoiceTypeDto.getAppliesTo() != null){
        	for(String invoiceTypeCode : invoiceTypeDto.getAppliesTo()){
        		 InvoiceType tmpInvoiceType = null;
        		 tmpInvoiceType = invoiceTypeService.findByCode(invoiceTypeCode, provider);
        		 if(tmpInvoiceType == null){
        			 throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode); 
        		 }
        		 invoiceTypesToApplies.add(tmpInvoiceType);
        	}
        }        
        InvoiceType invoiceType = new InvoiceType();
        invoiceType.setCode(invoiceTypeDto.getCode());
        invoiceType.setDescription(invoiceTypeDto.getDescription());
        invoiceType.setOccTemplate(occTemplate);
        invoiceType.setAppliesTo(invoiceTypesToApplies);
        invoiceType.setSequence(invoiceTypeDto.getSequenceDto() == null ? null : invoiceTypeDto.getSequenceDto().fromDto());
        if(invoiceTypeDto.getSellerSequences() != null){
	        for(Entry<String,SequenceDto> entry : invoiceTypeDto.getSellerSequences().entrySet()){
	        	Seller seller = sellerService.findByCode(entry.getKey(), provider);
	        	if(seller == null){
	        		 throw new EntityDoesNotExistsException(Seller.class, entry.getKey()); 
	        	}
	        	invoiceType.getSellerSequence().put(seller, entry.getValue() == null ? null : entry.getValue().fromDto());
	        }
        }
        if(invoiceTypeDto.getProviderSequences() != null){
	        for(Entry<String,SequenceDto> entry : invoiceTypeDto.getProviderSequences().entrySet()){
	        	if(!entry.getKey().equals(provider.getCode())){
	        		throw new BusinessApiException("Other provider not allowed");
	        	}

	        	invoiceType.getProviderSequence().put(provider, entry.getValue() == null ? null : entry.getValue().fromDto());
	        }
        }
        invoiceType.setMatchingAuto(invoiceTypeDto.isMatchingAuto());
        invoiceTypeService.create(invoiceType, currentUser);
        return result;
    }

    public ActionStatus update(InvoiceTypeDto invoiceTypeDto, User currentUser) throws MeveoApiException, BusinessException {

    	handleParameters(invoiceTypeDto);        
        ActionStatus result = new ActionStatus();
        Provider provider = currentUser.getProvider();

        // check if invoiceType exists
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeDto.getCode(), provider);
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeDto.getCode());
        } 
        
        if(invoiceTypeDto.getSequenceDto().getCurrentInvoiceNb() < invoiceTypeService.getMaxCurrentInvoiceNumber()) {
        	throw new MeveoApiException("Not able to update, check the current number");
        }
        
        invoiceType.setSequence(invoiceTypeDto.getSequenceDto().fromDto());
        
        OCCTemplate occTemplate = occTemplateService.findByCode(invoiceTypeDto.getOccTemplateCode(), provider);        
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, invoiceTypeDto.getOccTemplateCode());
        }    
        invoiceType.setOccTemplate(occTemplate);   
		if(!StringUtils.isBlank(invoiceTypeDto.getDescription())){
	        invoiceType.setDescription(invoiceTypeDto.getDescription());
		}

		if(!StringUtils.isBlank(invoiceTypeDto.isMatchingAuto())){
			invoiceType.setMatchingAuto(invoiceTypeDto.isMatchingAuto());
		}			
        List<InvoiceType> invoiceTypesToApplies = new ArrayList<InvoiceType>();       
        if(invoiceTypeDto.getAppliesTo() != null){
        	for(String invoiceTypeCode : invoiceTypeDto.getAppliesTo()){
        		 InvoiceType tmpInvoiceType = null;
        		 tmpInvoiceType = invoiceTypeService.findByCode(invoiceTypeCode, provider);
        		 if(tmpInvoiceType == null){
        			 throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode); 
        		 }
        		 invoiceTypesToApplies.add(tmpInvoiceType);
        	}
        }
        invoiceType.setAppliesTo(invoiceTypesToApplies);
        
        if(invoiceTypeDto.getSellerSequences() != null){
	        for(Entry<String,SequenceDto> entry : invoiceTypeDto.getSellerSequences().entrySet()){
	        	Seller seller = sellerService.findByCode(entry.getKey(), provider);
	        	if(seller == null){
	        		 throw new EntityDoesNotExistsException(Seller.class, entry.getKey()); 
	        	}
	        	invoiceType.getSellerSequence().put(seller, entry.getValue() == null ? null : entry.getValue().fromDto());
	        }
        }
        if(invoiceTypeDto.getProviderSequences() != null){
	        for(Entry<String,SequenceDto> entry : invoiceTypeDto.getProviderSequences().entrySet()){
	        	if(!entry.getKey().equals(provider.getCode())){
	        		throw new BusinessApiException("Other provider not allowed");
	        	}

	        	invoiceType.getProviderSequence().put(provider, entry.getValue() == null ? null : entry.getValue().fromDto());
	        }
        }
        
        invoiceTypeService.update(invoiceType, currentUser);
        return result;
    }

    public InvoiceTypeDto find(String invoiceTypeCode, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(invoiceTypeCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        InvoiceTypeDto result = new InvoiceTypeDto();

        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode, provider);
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
        }
        result = new InvoiceTypeDto(invoiceType);
        return result;
    }

    public ActionStatus remove(String invoiceTypeCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(invoiceTypeCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        ActionStatus result = new ActionStatus();
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode, provider);
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
        }
        invoiceTypeService.remove(invoiceType);
        return result;
    }

    public void createOrUpdate(InvoiceTypeDto invoiceTypeDto, User currentUser) throws MeveoApiException, BusinessException {
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeDto.getCode(), currentUser.getProvider());

        if (invoiceType == null) {
            create(invoiceTypeDto, currentUser);
        } else {
            update(invoiceTypeDto, currentUser);
        }
    }

    public InvoiceTypesDto list(Provider provider) throws MeveoApiException {
    	InvoiceTypesDto invoiceTypeesDto = new InvoiceTypesDto();

        if (provider != null) {
            List<InvoiceType> invoiceTypees = invoiceTypeService.list(provider);
            if (invoiceTypees != null && !invoiceTypees.isEmpty()) {
                for (InvoiceType t : invoiceTypees) {
                    InvoiceTypeDto invoiceTypeDto = new InvoiceTypeDto(t);
                    invoiceTypeesDto.getInvoiceTypes().add(invoiceTypeDto);
                }
            }
        }

        return invoiceTypeesDto;
    }
}