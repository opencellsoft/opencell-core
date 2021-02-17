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

package org.meveo.api;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.InvoiceSequenceDto;
import org.meveo.api.dto.billing.InvoiceSequencesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.service.billing.impl.InvoiceSequenceService;

/**
 * The CRUD Api for InvoiceSequence Entity.
 *
 * @author akadid abdelmounaim
 */
@Stateless
public class InvoiceSequenceApi extends BaseApi {
    
    /** The invoice sequence service. */
    @Inject
    private InvoiceSequenceService invoiceSequenceService;

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
        invoiceSequence.setCurrentNumber(postData.getCurrentInvoiceNb());
        invoiceSequence.setSequenceSize(postData.getSequenceSize());
        invoiceSequenceService.create(invoiceSequence);
        return result;
    }
    
    /**
     * Update the InvoiceSequence.
     *
     * @param invoiceSequenceDto the invoice type dto
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
        invoiceSequence.setCurrentNumber(invoiceSequenceDto.getCurrentInvoiceNb());
        
        invoiceSequenceService.update(invoiceSequence);
        return result;
    }
    
    /**
     * Creates the or update the InvoiceType.
     *
     * @param invoiceSequenceDto the invoice sequence dto
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public void createOrUpdate(InvoiceSequenceDto invoiceSequenceDto) throws MeveoApiException, BusinessException {
        InvoiceSequence invoiceSequence = invoiceSequenceService.findByCode(invoiceSequenceDto.getCode());

        if (invoiceSequence == null) {
            create(invoiceSequenceDto);
        } else {
            update(invoiceSequenceDto);
        }
    }


    /**
     * Find the InvoiceSequence.
     *
     * @param invoiceSequenceCode the invoice sequence code
     * @return the invoice sequence Dto
     * @throws MeveoApiException the meveo api exception
     */
    public InvoiceSequenceDto find(String invoiceSequenceCode) throws MeveoApiException {

        if (StringUtils.isBlank(invoiceSequenceCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        InvoiceSequenceDto result = new InvoiceSequenceDto();

        InvoiceSequence invoiceSequence = invoiceSequenceService.findByCode(invoiceSequenceCode);
        if (invoiceSequence == null) {
            throw new EntityDoesNotExistsException(InvoiceSequence.class, invoiceSequenceCode);
        }
        result = new InvoiceSequenceDto(invoiceSequence);
        return result;
    }


    /**
     * List InvoiceSequences.
     *
     * @return the invoice sequences dto
     * @throws MeveoApiException the meveo api exception
     */
    public InvoiceSequencesDto list() throws MeveoApiException {
        InvoiceSequencesDto invoiceSequencesDto = new InvoiceSequencesDto();
        
        List<InvoiceSequence> invoiceSeuqences = invoiceSequenceService.list();
        if (invoiceSeuqences != null && !invoiceSeuqences.isEmpty()) {
            for (InvoiceSequence invSeq : invoiceSeuqences) {
                InvoiceSequenceDto invoiceSequenceDto = new InvoiceSequenceDto(invSeq);
            	invoiceSequencesDto.getInvoiceSequences().add(invoiceSequenceDto);
            }
        }

        return invoiceSequencesDto;
    }
    
}