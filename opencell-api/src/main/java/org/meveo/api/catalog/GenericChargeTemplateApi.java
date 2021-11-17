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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetChargeTemplateResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ChargeTemplateStatusEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.cpq.AttributeService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class GenericChargeTemplateApi extends BaseApi {

    @Inject
    private ChargeTemplateServiceAll chargeTemplateService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;
    
    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private AttributeService attributeService;

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    public EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }

    public ChargeTemplateDto find(String chargeTemplateCode) throws MeveoApiException {
        if (StringUtils.isBlank(chargeTemplateCode)) {
            missingParameters.add("chargeTemplateCode");
        }
        handleMissingParameters();

        ChargeTemplate chargeTemplate = (ChargeTemplate) chargeTemplateService.findByCode(chargeTemplateCode);
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(ChargeTemplate.class, chargeTemplateCode);
        }

        return new ChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
    }

	/**
	 * @param chargeTemplateCode
	 * @param status
	 * @return
	 */
	public void updateStatus(String chargeTemplateCode, String status) {
		ChargeTemplate chargeTemplate = chargeTemplateService.findByCode(chargeTemplateCode);
		if(chargeTemplate ==null) {
    		throw new EntityDoesNotExistsException(ChargeTemplate.class, chargeTemplateCode);
    	}
		chargeTemplateService.updateStatus(chargeTemplate, status);
	}

	/**
	 * @param chargeTemplateCode
	 * @return
	 */
	public GetChargeTemplateResponseDto duplicateCharge(String chargeTemplateCode) {
		GetChargeTemplateResponseDto getChargeTemplateResponseDto = new GetChargeTemplateResponseDto();
		ChargeTemplate chargeTemplate = chargeTemplateService.findByCode(chargeTemplateCode);
		
		if(chargeTemplate ==null) {
    		throw new EntityDoesNotExistsException(ChargeTemplate.class, chargeTemplateCode);
    	}
		
		
		//charge Template to be duplicated
		ChargeTemplate duplicateChargeTemplate = null;
		
		try {
			duplicateChargeTemplate = (ChargeTemplate) BeanUtils.cloneBean(chargeTemplate);
			duplicateChargeTemplate.setId(null);
			duplicateChargeTemplate.setCode(chargeTemplateService.findDuplicateCode(chargeTemplate));
			duplicateChargeTemplate.setStatus(ChargeTemplateStatusEnum.DRAFT);

			if(chargeTemplate.getAttributes() != null) {
				Set<Attribute> attributes = new HashSet<Attribute>();
				for(Attribute attribute:chargeTemplate.getAttributes()) {
					Attribute attributeNew = attributeService.findByCode(attribute.getCode());
					attributes.add(attributeNew);
				}
				duplicateChargeTemplate.setAttributes(attributes);
			}
			
			if(chargeTemplate.getEdrTemplates() != null) {
				List<TriggeredEDRTemplate> edrTemplates = new ArrayList<TriggeredEDRTemplate>();
				for(TriggeredEDRTemplate triggeredEDRTemplate:chargeTemplate.getEdrTemplates()) {
					TriggeredEDRTemplate triggeredEDRTemplateNew = triggeredEDRTemplateService.findByCode(triggeredEDRTemplate.getCode());
					edrTemplates.add(triggeredEDRTemplateNew);
				}
				duplicateChargeTemplate.setEdrTemplates(edrTemplates);
			}
			

			chargeTemplateService.create(duplicateChargeTemplate);
			
	        List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByChargeCode(chargeTemplateCode);
	        
	        //Duplicate Price Plan Matrix
	        if(pricePlanMatrixes != null && !pricePlanMatrixes.isEmpty()) {
	        	for(PricePlanMatrix pricePlanMatrix:pricePlanMatrixes) {

	        		@SuppressWarnings("unchecked")
	            	List<PricePlanMatrixVersion> pricesVersions = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.lastVersion")
							.setParameter("pricePlanMatrixCode", pricePlanMatrix.getCode()).getResultList();
	            	
	        		PricePlanMatrix pricePlanMatrixNew = (PricePlanMatrix) BeanUtils.cloneBean(pricePlanMatrix);

	        		pricePlanMatrixNew.setId(null);
	        		pricePlanMatrixNew.setEventCode(duplicateChargeTemplate.getCode());
	        		pricePlanMatrixNew.setCode(pricePlanMatrixService.findDuplicateCode(pricePlanMatrix));

	        		List<PricePlanMatrixVersion> versionsNew = new ArrayList<PricePlanMatrixVersion>();
	        		pricePlanMatrixNew.setVersions(versionsNew);
	        		
	        		pricePlanMatrixService.create(pricePlanMatrixNew);


	        		if(pricesVersions != null && !pricesVersions.isEmpty()) {
		        		for(PricePlanMatrixVersion priceVersion: pricesVersions) {
		            		PricePlanMatrixVersion priceVersionNew = (PricePlanMatrixVersion) BeanUtils.cloneBean(priceVersion);
		            		
		            		priceVersionNew.setId(null);
		            		priceVersionNew.setStatus(VersionStatusEnum.DRAFT);
		            		priceVersionNew.setPricePlanMatrix(pricePlanMatrixNew);
		            		
		            		if(priceVersion.getColumns() != null) {
			            		Set<PricePlanMatrixColumn> pricePlanColumns = new HashSet<>();
			            		for(PricePlanMatrixColumn pricePlanColumn:priceVersion.getColumns()){
			            			PricePlanMatrixColumn pricePlanColumnNew = pricePlanMatrixColumnService.findByCode(pricePlanColumn.getCode());
			            			pricePlanColumns.add(pricePlanColumnNew);
			            		}
			            		priceVersionNew.setColumns(pricePlanColumns);
		            		}
		            		
		            		if(priceVersion.getLines() != null) {
		            			Set<PricePlanMatrixLine> lines = new HashSet<>();
			            		for(PricePlanMatrixLine pricePlanMatrixLine:priceVersion.getLines()){
			            			PricePlanMatrixLine pricePlanMatrixLineNew = pricePlanMatrixLineService.findById(pricePlanMatrixLine.getId());
			            			lines.add(pricePlanMatrixLineNew);
			            		}
			            		priceVersionNew.setLines(lines);
		            		}

		            		priceVersionNew.setPricePlanMatrix(pricePlanMatrixNew);
		            		pricePlanMatrixVersionService.create(priceVersionNew);
		            		//versionsNew.add(priceVersionNew);
		            	}
	        		}
	        		//pricePlanMatrixNew.setVersions(versionsNew);

//	        		pricePlanMatrixService.create(pricePlanMatrixNew);
	        	}
	        }
			
			getChargeTemplateResponseDto.setChargeTemplate(new ChargeTemplateDto(duplicateChargeTemplate, entityToDtoConverter.getCustomFieldsDTO(duplicateChargeTemplate)));
			
		} catch (Exception e) {
            log.error("Error when trying to cloneBean quoteOffer : ", e);
        }
		
		return getChargeTemplateResponseDto;
	}

}