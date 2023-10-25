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

package org.meveo.service.catalog.impl;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.bind.ValidationException;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ChargeTemplateStatusEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.cpq.AttributeService;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class ChargeTemplateServiceAll extends BusinessService<ChargeTemplate> {

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

	@SuppressWarnings("unchecked")
	public List<ChargeTemplate> findByEDRTemplate(TriggeredEDRTemplate edrTemplate){
		QueryBuilder qb=new QueryBuilder(this.getEntityClass(),"c");
		qb.addCriterionEntityInList("edrTemplates", edrTemplate);
		return qb.find(getEntityManager());
	}
	
	@SuppressWarnings("unchecked")
	public List<ChargeTemplate> findByInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory){
		QueryBuilder qb=new QueryBuilder(this.getEntityClass(),"c");
		qb.addCriterionEntity("invoiceSubCategory", invoiceSubCategory);
		return qb.find(getEntityManager());
	}

	/**
	 * @param chargeTemplate
	 * @param stringStatus
	 * @return
	 */
	public void updateStatus(ChargeTemplate chargeTemplate, String stringStatus) {
		ChargeTemplateStatusEnum status = ChargeTemplateStatusEnum.valueOf(stringStatus);
		if(ChargeTemplateStatusEnum.ACTIVE.equals(status)){
			List<PricePlanMatrix> activePricePlansByChargeCode = pricePlanMatrixService.getActivePricePlansByChargeCode(chargeTemplate.getCode());
			Optional<PricePlanMatrix> publishedPricePlanMatrix = activePricePlansByChargeCode.stream()
					.filter(pricePlanMatrix -> pricePlanMatrix.getVersions().stream()
							.filter(pricePlanMatrixVersion -> VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus()))
							.findFirst()
							.isPresent())
					.findFirst();
			if(publishedPricePlanMatrix.isEmpty() && chargeTemplate.getRatingScript()==null ){
				throw new BusinessException("to activate a charge, it should at least have ONE PUBLISHED Price plan Version");
			}
		}
		try {
			chargeTemplate.setStatus(status);
			update(chargeTemplate);
		} catch (ValidationException e) {
			throw new BusinessApiException(e.getMessage());
		}
	}

	public ChargeTemplate duplicateCharge(ChargeTemplate chargeTemplate) {
		//charge Template to be duplicated
		ChargeTemplate duplicateChargeTemplate = null;
		ChargeTemplateStatusEnum statusChargeTemplate = chargeTemplate.getStatus();
		
		try {
			//set status to null to bypass the validation used in the setStatus method
			chargeTemplate.setStatus(null);
			duplicateChargeTemplate = (ChargeTemplate) BeanUtils.cloneBean(chargeTemplate);
			chargeTemplate.setStatus(statusChargeTemplate);
			
			if(chargeTemplate.getProductCharges() != null) {
			    List<ProductChargeTemplateMapping> listProductChargeTemplateMapping = new ArrayList<ProductChargeTemplateMapping>();
                for(ProductChargeTemplateMapping pCTMapping : chargeTemplate.getProductCharges()) {
                    listProductChargeTemplateMapping.add(pCTMapping);
                }
                duplicateChargeTemplate.setProductCharges(listProductChargeTemplateMapping);
            }
			
			duplicateChargeTemplate.setId(null);
			duplicateChargeTemplate.setCode(findDuplicateCode(chargeTemplate));
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
			
			create(duplicateChargeTemplate);
			
	        List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByChargeCode(chargeTemplate.getCode());
	        
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
	        		pricePlanMatrixNew.setContractItems(null);
	        		pricePlanMatrixNew.setDiscountPlanItems(null);
	        		
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
		            	}
	        		}
	        	}
	        }
		} catch (Exception e) {
            log.error("Error when trying to cloneBean chargeTemplate : ", e);
			throw new BusinessApiException(e.getMessage());
        }
		return duplicateChargeTemplate;
	}

}