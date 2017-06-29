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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.MultilanguageEntityService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class ChargeTemplateService<P extends ChargeTemplate> extends MultilanguageEntityService<P> {
	
	@Inject
	private TriggeredEDRTemplateService edrTemplateService;
	
	@Inject
    private CustomFieldInstanceService customFieldInstanceService;
	
	public synchronized void duplicate(P entity) throws BusinessException{
		
		entity = refreshOrRetrieve(entity);
        // Lazy load related values first 
		entity.getEdrTemplates().size();
		String code=findDuplicateCode(entity);
		
        // Detach and clear ids of entity and related entities
		detach(entity);
		entity.setId(null);
        String sourceAppliesToEntity = entity.clearUuid();
        
		List<TriggeredEDRTemplate> edrTemplates=entity.getEdrTemplates();
		entity.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
		if(edrTemplates!=null&edrTemplates.size()!=0){
			for(TriggeredEDRTemplate edrTemplate:edrTemplates){
				edrTemplateService.detach(edrTemplate);
				entity.getEdrTemplates().add(edrTemplate);
			}
		}
		entity.setChargeInstances(null);
		entity.setCode(code);
		create(entity);
        customFieldInstanceService.duplicateCfValues(sourceAppliesToEntity, entity);
	}
	
	/**
	 * @param table name of table(cat_serv_usage_charge_template, cat_serv_rec_charge_template, cat_serv_sub_charge_template, cat_serv_trm_charge_template)
	 * @param chargeId id of the charge which is used to check 
	 * @return list of service's Id linked to charge
	 * @throws BusinessException
	 */
	private synchronized List<Long> getServiceIdsLinkedToCharge(String table, Long chargeId) throws BusinessException {
		List<Long> result = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT SERVICE_TEMPLATE_ID FROM " );
		builder.append(table);
		builder.append(" WHERE CHARGE_TEMPLATE_ID = ");
		builder.append(chargeId);
		Query query = this.getEntityManager().createNativeQuery(builder.toString());
		List<Object> resultList = query.getResultList();
		if (resultList != null) {
			for (Object charge : resultList) {
				result.add(Long.valueOf(((BigInteger)charge).longValue()));
			}
			
		}
		
		return result;
	}
	
	/**
	 * @param table name of table(cat_serv_usage_charge_template, cat_serv_rec_charge_template, cat_serv_sub_charge_template, cat_serv_trm_charge_template)
	 * @param chargeId id of the charge which is used to check 
	 * @return list of service's Id linked to charge
	 * @throws BusinessException
	 */
	private synchronized int remove(String table, Long chargeId) throws BusinessException {
		int result = 0;
		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM " );
		builder.append(table);
		builder.append(" WHERE CHARGE_TEMPLATE_ID = ");
		builder.append(chargeId);
		Query query = this.getEntityManager().createNativeQuery(builder.toString());
		result = query.executeUpdate();
		
		
		return result;
	}
	
	/**
	 * @param table name of table(cat_serv_usage_charge_template, cat_serv_rec_charge_template, cat_serv_sub_charge_template, cat_serv_trm_charge_template)
	 * @param chargeId id of the charge which is used to check 
	 * @return list of service's Id linked to charge
	 * @throws BusinessException
	 */
	public synchronized int deleteCharge(Long chargeId) throws BusinessException {
		int result = 0;
		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM CAT_CHARGE_TEMPLATE WHERE ID =  " );
		builder.append(chargeId);
		Query query = this.getEntityManager().createNativeQuery(builder.toString());
		result = query.executeUpdate();
			
		return result;
	}
	
	
	/**
	 * @param table name of table(cat_serv_usage_charge_template, cat_serv_rec_charge_template, cat_serv_sub_charge_template, cat_serv_trm_charge_template)
	 * @param chargeId id of the charge which is used to check 
	 * @return list of service's Id linked to charge
	 * @throws BusinessException
	 */
	private synchronized int removeRelatedCharge(String table, Long chargeId) throws BusinessException {
		int result = 0;
		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM " );
		builder.append(table);
		builder.append(" WHERE ID = ");
		builder.append(chargeId);
		Query query = this.getEntityManager().createNativeQuery(builder.toString());
		result = query.executeUpdate();
		
		
		return result;
	}
	
	/**
	 * @param chargeId id of the charge which is used to check 
	 * @return list of service's Id linked to charge
	 * @throws BusinessException
	 */
	public synchronized int removeRelatedChargeUsage(Long chargeId) throws BusinessException {
		return this.removeRelatedCharge("cat_usage_charge_template", chargeId);
	}
	
	/**
     * @param chargeId id of the charge which is used to check 
	 * @return list of service's Id linked to charge
	 * @throws BusinessException
	 */
	public synchronized int removeRelatedChargeRecurring(Long chargeId) throws BusinessException {
		return this.removeRelatedCharge("cat_recurring_charge_templ", chargeId);
	}
	
	/**
	 * @param chargeId id of the charge which is used to check 
	 * @return list of service's Id linked to charge
	 * @throws BusinessException
	 */
	public synchronized int removeRelatedChargeOneshot(Long chargeId) throws BusinessException {
		return this.removeRelatedCharge("cat_one_shot_charge_templ", chargeId);
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
}
