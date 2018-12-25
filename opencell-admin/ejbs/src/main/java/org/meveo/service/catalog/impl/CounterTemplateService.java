/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
		Query query = getEntityManager()
				.createQuery("DELETE CounterTemplate t WHERE t.code LIKE '"
						+ prefix + "%'");
		
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