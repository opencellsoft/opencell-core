package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.SequenceDto;
import org.meveo.api.dto.billing.InvoiceSequenceDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.InvoiceSequenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;

/**
 * The CRUD Api for InvoiceSequence Entity.
 *
 * @author akadid abdelmounaim
 */
@Stateless
public class InvoiceSequenceApi extends BaseApi {

    /** The invoice type service. */
    @Inject
    private InvoiceTypeService invoiceTypeService;
    
    /** The invoice sequence service. */
    @Inject
    private InvoiceSequenceService invoiceSequenceService;

    /** The seller service. */
    @Inject
    private SellerService sellerService;

    /**
     * Handle parameters.
     *
     * @param postData the post data
     * @throws MeveoApiException the meveo api exception
     */
    private void handleParameters(InvoiceSequenceDto postData) throws MeveoApiException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(postData);
    }
    
    /**
     * Creates the InvoiceSequence.
     *
     * @param postData the post data
     * @return the action status
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public ActionStatus create(InvoiceSequenceDto postData) throws MeveoApiException, BusinessException {
        handleParameters(postData);
        ActionStatus result = new ActionStatus();

        if (invoiceSequenceService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(InvoiceSequence.class, postData.getCode());
        }
        
        InvoiceSequence invoiceSequence = new InvoiceSequence();
        invoiceSequence.setCode(postData.getCode());
        invoiceSequence.setDescription(postData.getDescription());
        invoiceSequence.setCurrentInvoiceNb(postData.getCurrentInvoiceNb());
        invoiceSequence.setSequenceSize(postData.getSequenceSize());
        invoiceSequenceService.create(invoiceSequence);
        return result;
    }
    
    /**
     * Update the InvoiceSequence.
     *
     * @param invoiceTypeDto the invoice type dto
     * @return the action status
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public ActionStatus update(InvoiceSequenceDto invoiceSequenceDto) throws MeveoApiException, BusinessException {

        handleParameters(invoiceSequenceDto);
        ActionStatus result = new ActionStatus();

        // check if invoiceSequence exists
        InvoiceSequence invoiceSequence = invoiceSequenceService.findByCode(invoiceSequenceDto.getCode());
        if (invoiceSequence == null) {
            throw new EntityDoesNotExistsException(InvoiceSequence.class, invoiceSequenceDto.getCode());
        }

        invoiceSequence.setSequenceSize(invoiceSequenceDto.getSequenceSize());
        invoiceSequence.setDescription(invoiceSequenceDto.getDescription());
        invoiceSequence.setCurrentInvoiceNb(invoiceSequenceDto.getCurrentInvoiceNb());
        
        invoiceSequenceService.update(invoiceSequence);
        return result;
    }


    /**
     * Find the InvoiceSequence.
     *
     * @param invoiceSequenceCode the invoice sequence code
     * @return the sequence Dto
     * @throws MeveoApiException the meveo api exception
     */
    public SequenceDto find(String invoiceSequenceCode) throws MeveoApiException {

        if (StringUtils.isBlank(invoiceSequenceCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        SequenceDto result = new SequenceDto();

        InvoiceSequence invoiceSequence = invoiceSequenceService.findByCode(invoiceSequenceCode);
        if (invoiceSequence == null) {
            throw new EntityDoesNotExistsException(InvoiceSequence.class, invoiceSequenceCode);
        }
        result = new SequenceDto(invoiceSequence);
        return result;
    }


    /**
     * List InvoiceSequences.
     *
     * @return the invoice types dto
     * @throws MeveoApiException the meveo api exception
     */
    public List<SequenceDto> list() throws MeveoApiException {
    	List<SequenceDto> invoiceTypeesDto = new ArrayList<SequenceDto>();

        List<InvoiceSequence> invoiceSeuqences = invoiceSequenceService.list();
        if (invoiceSeuqences != null && !invoiceSeuqences.isEmpty()) {
            for (InvoiceSequence invSeq : invoiceSeuqences) {
            	SequenceDto invoiceTypeDto = new SequenceDto(invSeq);
                invoiceTypeesDto.add(invoiceTypeDto);
            }
        }

        return invoiceTypeesDto;
    }
    
}