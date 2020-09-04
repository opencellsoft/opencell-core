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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;


/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class ChargeTemplateService<P extends ChargeTemplate> extends BusinessService<P> {

    @Inject
    private TriggeredEDRTemplateService edrTemplateService;
    
    @Inject
    private UnitOfMeasureService unitOfMeasureService;

    public synchronized void duplicate(P chargeTemplate) throws BusinessException {

        chargeTemplate = refreshOrRetrieve(chargeTemplate);
        // Lazy load related values first
        chargeTemplate.getEdrTemplates().size();
        String code = findDuplicateCode(chargeTemplate);

        // Detach and clear ids of entity and related entities
        detach(chargeTemplate);
        chargeTemplate.setId(null);
        chargeTemplate.clearUuid();

        List<TriggeredEDRTemplate> edrTemplates = chargeTemplate.getEdrTemplates();
        chargeTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
        if (edrTemplates != null && edrTemplates.size() != 0) {
            for (TriggeredEDRTemplate edrTemplate : edrTemplates) {
                edrTemplateService.detach(edrTemplate);
                chargeTemplate.getEdrTemplates().add(edrTemplate);
            }
        }
        chargeTemplate.setCode(code);
        create(chargeTemplate);
    }

    /**
     * @param table name of table(cat_serv_usage_charge_template, cat_serv_rec_charge_template, cat_serv_sub_charge_template, cat_serv_trm_charge_template)
     * @param chargeId id of the charge which is used to check
     * @return list of service's Id linked to charge
     * @throws BusinessException Business exception
     */
    @SuppressWarnings("unchecked")
    private synchronized List<Long> getServiceIdsLinkedToCharge(String table, Long chargeId) throws BusinessException {
        List<Long> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT SERVICE_TEMPLATE_ID FROM ");
        builder.append(table);
        builder.append(" WHERE CHARGE_TEMPLATE_ID = ");
        builder.append(chargeId);
        Query query = this.getEntityManager().createNativeQuery(builder.toString());
        List<Object> resultList = query.getResultList();
        if (resultList != null) {
            for (Object charge : resultList) {
                result.add(Long.valueOf(((BigInteger) charge).longValue()));
            }

        }

        return result;
    }

    /**
     * @param table name of table(cat_serv_usage_charge_template, cat_serv_rec_charge_template, cat_serv_sub_charge_template, cat_serv_trm_charge_template)
     * @param chargeId id of the charge which is used to check
     * @return list of service's Id linked to charge
     * @throws BusinessException Business exception
     */
    private synchronized int remove(String table, Long chargeId) throws BusinessException {
        int result = 0;
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ");
        builder.append(table);
        builder.append(" WHERE CHARGE_TEMPLATE_ID = ");
        builder.append(chargeId);
        Query query = this.getEntityManager().createNativeQuery(builder.toString());
        result = query.executeUpdate();

        return result;
    }

    /**
     * @param chargeId id of the charge which is used to check
     * @return deletion's result
     * @throws BusinessException business exception.
     */
    public synchronized int deleteCharge(Long chargeId) throws BusinessException {
        int result = 0;
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM CAT_CHARGE_TEMPLATE WHERE ID =  ");
        builder.append(chargeId);
        Query query = this.getEntityManager().createNativeQuery(builder.toString());
        result = query.executeUpdate();

        return result;
    }

    /**
     * @param table name of table(cat_serv_usage_charge_template, cat_serv_rec_charge_template, cat_serv_sub_charge_template, cat_serv_trm_charge_template)
     * @param chargeId id of the charge which is used to check
     * @return deletion's result.
     * @throws BusinessException business exception.
     */
    private synchronized int removeRelatedCharge(String chargeType, Long chargeId) throws BusinessException {
        int result = 0;
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM CAT_CHARGE_TEMPLATE WHERE ID = ");
        builder.append(chargeId);
        builder.append("AND CHARGE_TYPE ='");
        builder.append(chargeType);
        builder.append("'");
        Query query = this.getEntityManager().createNativeQuery(builder.toString());
        result = query.executeUpdate();

        return result;
    }

    /**
     * @param chargeId id of the charge which is used to check
     * @return deletion's result
     * @throws BusinessException business exception.
     */
    public synchronized int removeRelatedChargeUsage(Long chargeId) throws BusinessException {
        return this.removeRelatedCharge("U", chargeId);
    }

    /**
     * @param chargeId id of the charge which is used to check
     * @return deletion's result.
     * @throws BusinessException business exception
     */
    public synchronized int removeRelatedChargeRecurring(Long chargeId) throws BusinessException {
        return this.removeRelatedCharge("R", chargeId);
    }

    /**
     * @param chargeId id of the charge which is used to check
     * @return deletion's result.
     * @throws BusinessException business exception.
     */
    public synchronized int removeRelatedChargeOneshot(Long chargeId) throws BusinessException {
        return this.removeRelatedCharge("O", chargeId);
    }

    /**
     * @param chargeId charge's ID
     * @return list of service's ID
     * @throws BusinessException exception when error occurs
     */
    public synchronized List<Long> getServiceIdsLinkedToChargeUsage(Long chargeId) throws BusinessException {
        return this.getServiceIdsLinkedToCharge("cat_serv_usage_charge_template", chargeId);

    }

    /**
     * @param chargeId charge's ID
     * @return list of service's ID
     * @throws BusinessException exception when error occurs
     */
    public synchronized List<Long> getServiceIdsLinkedToChargeRecurring(Long chargeId) throws BusinessException {
        return this.getServiceIdsLinkedToCharge("cat_serv_rec_charge_template", chargeId);

    }

    /**
     * @param chargeId charge's ID
     * @return list of service's ID
     * @throws BusinessException exception when error occurs
     */
    public synchronized List<Long> getServiceIdsLinkedToChargeSubscription(Long chargeId) throws BusinessException {
        return this.getServiceIdsLinkedToCharge("cat_serv_sub_charge_template", chargeId);

    }

    /**
     * @param chargeId charge's ID
     * @return list of service's ID
     * @throws BusinessException exception when error occurs
     */
    public synchronized List<Long> getServiceIdsLinkedToChargeTermination(Long chargeId) throws BusinessException {
        return this.getServiceIdsLinkedToCharge("cat_serv_trm_charge_template", chargeId);
    }

    /**
     * @param chargeId charge's ID
     * @return list of service's ID
     * @throws BusinessException exception when error occurs
     */
    public synchronized int removeServiceLinkChargeRecurring(Long chargeId) throws BusinessException {
        return this.remove("cat_serv_rec_charge_template", chargeId);
    }

    /**
     * @param chargeId charge's ID
     * @return list of service's ID
     * @throws BusinessException exception when error occurs
     */
    public synchronized int removeServiceLinkChargeSubscription(Long chargeId) throws BusinessException {
        return this.remove("cat_serv_sub_charge_template", chargeId);
    }

    /**
     * @param chargeId charge's ID
     * @return list of service's ID
     * @throws BusinessException exception when error occurs
     */
    public synchronized int removeServiceLinkChargeTermination(Long chargeId) throws BusinessException {
        return this.remove("cat_serv_trm_charge_template", chargeId);
    }

    /**
     * @param chargeId charge's ID
     * @return list of service's ID
     * @throws BusinessException exception when error occurs
     */
    public synchronized int removeServiceLinkChargeUsage(Long chargeId) throws BusinessException {
        return this.remove("cat_serv_usage_charge_template", chargeId);
    }
    
    
    @Override
    public void create(P entity) throws BusinessException {
    	validateEntity(entity);
    	super.create(entity);
    }
    
    @Override
    public P updateNoCheck(P entity) throws BusinessException {
    	validateEntity(entity);
    	return super.updateNoCheck(entity);
    }
    
    @Override
    public P update(P entity) throws BusinessException {
    	validateEntity(entity);
    	return super.update(entity);
    }
    
    
	/**
	 * calculate rating quantity to be used based on chargeTemplate:
	 * 
	 * First evaluate inputUnitEl and ratingUnitEl When an EL is not defined, then
	 * use the corresponding unit field (resp input and rating) if we have both
	 * units (from ELs or reference fields), then ignore unitMultiplicator and
	 * compute multiplicator from units. if we don't have both units, then use
	 * unitMultiplicator from ChargeTemplate ultimately, if unitMultiplicator is not
	 * set, then use default value of 1
	 * 
	 * 
	 * @param chargeTemplate
	 * @param quantity
	 * @return
	 * @throws BusinessException
	 */
	public BigDecimal evaluateRatingQuantity(ChargeTemplate chargeTemplate, BigDecimal quantity) throws BusinessException {
		UnitOfMeasure inputUnitFromEL = getUOMfromEL(chargeTemplate.getInputUnitEL());
		UnitOfMeasure outputUnitFromEL = getUOMfromEL(chargeTemplate.getOutputUnitEL());
		BigDecimal multiplicator = chargeTemplate.getUnitMultiplicator();
		RoundingModeEnum roundingMode = chargeTemplate.getRoundingMode();
		int unitNbDecimal = chargeTemplate.getUnitNbDecimal();
        if (unitNbDecimal == 0) {
            unitNbDecimal = 2;
        }

		inputUnitFromEL = inputUnitFromEL != null ? inputUnitFromEL : chargeTemplate.getInputUnitOfMeasure();
		outputUnitFromEL = outputUnitFromEL != null ? outputUnitFromEL : chargeTemplate.getRatingUnitOfMeasure();

		if (inputUnitFromEL != null || outputUnitFromEL != null) {
			if (inputUnitFromEL != null && outputUnitFromEL != null) {
				if (inputUnitFromEL.isCompatibleWith(outputUnitFromEL)) {
					BigDecimal outputMultiplicator = BigDecimal.valueOf(outputUnitFromEL.getMultiplicator());
					BigDecimal inputMultiplicator = BigDecimal.valueOf(inputUnitFromEL.getMultiplicator());
					Integer scale = calculateNeededScale(inputMultiplicator, outputMultiplicator);
					return quantity.multiply(inputMultiplicator).divide(outputMultiplicator, scale, RoundingMode.HALF_UP);
				} else {
					throw new BusinessException("incompatible input/rating UnitOfMeasures: " + inputUnitFromEL 
							+ "/" + outputUnitFromEL +" for chargeTemplate "+chargeTemplate.getCode());
				}
			} else {
				throw new BusinessException("null value not accepted on input/rating UnitOfMeasures: "
							+ inputUnitFromEL + "/" + outputUnitFromEL);
			}
		} else if (multiplicator != null) {
			return quantity.multiply(multiplicator).setScale(unitNbDecimal, roundingMode.getRoundingMode());
		}
		return quantity.setScale(unitNbDecimal, roundingMode.getRoundingMode());
	}

	/**
	 * This method calculate the needed scale based on default scale + the difference of precision from the two numbers
	 * 
	 * @param inputMultiplicator
	 * @param outputMultiplicator
	 * 
	 * @return scale
	 */
	private Integer calculateNeededScale(BigDecimal inputMultiplicator, BigDecimal outputMultiplicator) {
		return BaseEntity.NB_DECIMALS + Math.abs(inputMultiplicator.toBigInteger().toString().length() - outputMultiplicator.toBigInteger().toString().length()) ;
		
	}
	
	private UnitOfMeasure getUOMfromEL(String expression) throws BusinessException {
		UnitOfMeasure unitFromEL = null;
		if (!StringUtils.isBlank(expression)) {
			String code = "";
			code = ValueExpressionWrapper.evaluateToStringMultiVariable(expression);
			unitFromEL = unitOfMeasureService.findByCode(code);
			if (unitFromEL == null) {
				throw new BusinessException("Cannot find unitOfMeasure by code '" + code + "', el was : " + expression);
			}
		}
		return unitFromEL;
	}
	
	private void validateEntity(P entity) throws BusinessException {
		UnitOfMeasure ratingUnitOfMeasure = entity.getRatingUnitOfMeasure();
		UnitOfMeasure inputUnitOfMeasure = entity.getInputUnitOfMeasure();
		if (inputUnitOfMeasure != null && ratingUnitOfMeasure != null) {
			if (!inputUnitOfMeasure.isCompatibleWith(ratingUnitOfMeasure)) {
				throw new BusinessException( "incompatible input/rating UnitOfMeasures: " + inputUnitOfMeasure + "/" + ratingUnitOfMeasure);
			}
		} else if (inputUnitOfMeasure != null || ratingUnitOfMeasure != null) {
			throw new BusinessException("input/rating UnitOfMeasures must both be specified or both null: " + inputUnitOfMeasure + "/" + ratingUnitOfMeasure);
		}
	}
}
