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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.ServiceChargeTemplate;
import org.meveo.service.base.BusinessService;

/**
 * Counter Template service implementation.
 * 
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */
@Stateless
public class CounterTemplateService extends BusinessService<CounterTemplate> {

	public void removeByPrefix(String prefix) {
	    StringBuilder deleteQuery = new StringBuilder("DELETE CounterTemplate t WHERE t.code LIKE :prefix");
		Query query = getEntityManager()
				.createQuery(deleteQuery.toString()).setParameter("prefix", prefix + "%'");
		
		query.executeUpdate();
	}
	

		
	public  int getNbrCounterWithNotService() { 
		return ((Long)getEntityManager().createNamedQuery("counterTemplate.getNbrCounterWithNotService",Long.class)
				.getSingleResult()).intValue();
	}

	public List<CounterTemplate> getCounterWithNotService() { 
		return (List<CounterTemplate>)getEntityManager().createNamedQuery("counterTemplate.getCounterWithNotService",CounterTemplate.class)
				.getResultList();
	}
	
	/**
     * Gets the counter template by code.
     * 
     * @param counterTemplateCode the counter template code to search
     * @return the counter template
     * @throws MeveoApiException Meveo api exception
     */
	public CounterTemplate getCounterTemplate(String counterTemplateCode) throws MeveoApiException {
        CounterTemplate counterTemplate = null;
        // search for counter
        if (!StringUtils.isBlank(counterTemplateCode)) {
            counterTemplate = (CounterTemplate) findByCode(counterTemplateCode);
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, counterTemplateCode);
            }
        }
        return counterTemplate;
    }
	
	/**
     * Gets the counter template.
     * 
     * @param serviceChargeTemplate the service charge template
     * @param prefix the counter template code prefix 
     * @return the counter template.
     * @throws BusinessException the business exception
     */
	public CounterTemplate getCounterTemplate(@SuppressWarnings("rawtypes") ServiceChargeTemplate serviceChargeTemplate, String prefix) 
            throws BusinessException
             {
        CounterTemplate counterTemplate = null;
        if (serviceChargeTemplate.getCounterTemplate() != null) {
            // check if counter code already exists
            String counterTemplateCode = prefix + serviceChargeTemplate.getCounterTemplate().getCode();
            counterTemplate = findByCode(counterTemplateCode);

            if (counterTemplate == null) {
                counterTemplate = new CounterTemplate();
                try {
                    BeanUtils.copyProperties(counterTemplate, serviceChargeTemplate.getCounterTemplate());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new BusinessException(e.getMessage());
                }
                counterTemplate.setCode(counterTemplateCode);
                counterTemplate.setAuditable(null);
                counterTemplate.setId(null);
                create(counterTemplate);
            }
        }
        return counterTemplate;
    }

}