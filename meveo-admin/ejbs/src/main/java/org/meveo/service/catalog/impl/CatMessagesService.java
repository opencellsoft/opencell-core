/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.QueryBuilder.QueryLikeStyleEnum;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * CatMessagesService service implementation.
 */
@Stateless
public class CatMessagesService extends PersistenceService<CatMessages> {
	
	
	@Inject
	private TitleService titleService;
	@Inject
	private TaxService taxService;
	@Inject
	private InvoiceCategoryService invoiceCategoryService;
	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;
	@Inject 
	private UsageChargeTemplateService usageChargeTemplateService;
	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;
	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;
	@Inject
	private PricePlanMatrixService pricePlanMatrixService;

	@PostConstruct
	private void init() {

	}

	public String getMessageDescription(BusinessEntity businessEntity,
			String languageCode) {
		String result = getMessageDescription(getMessageCode(businessEntity), languageCode,businessEntity.getDescription());
		if(StringUtils.isBlank(result)){
			result=businessEntity.getCode();
		}
        return result;
	}

	@SuppressWarnings("unchecked")
	public String getMessageDescription(String messageCode,
			String languageCode, String defaultDescription) {
		long startDate = System.currentTimeMillis();
		if (messageCode == null || languageCode == null) {
			return null;
		}
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterionWildcard("c.messageCode", messageCode, true);
		qb.addCriterionWildcard("c.languageCode", languageCode, true);
		List<CatMessages> catMessages = qb.getQuery(getEntityManager())
				.getResultList();

		String description = (catMessages.size() > 0 && !StringUtils
				.isBlank(catMessages.get(0).getDescription())) ? catMessages
				.get(0).getDescription() : defaultDescription;

		log.debug("get message " + messageCode + " description =" + description
				+ ", time=" + (System.currentTimeMillis() - startDate));
		return description;
	}

    public CatMessages getCatMessages(IEntity businessEntity, String languageCode) {

        return getCatMessages(getMessageCode(businessEntity), languageCode);
    }
    
	public CatMessages getCatMessages(String messageCode, String languageCode) {
		return getCatMessages(getEntityManager(), messageCode, languageCode);
	}

	@SuppressWarnings("unchecked")
	private CatMessages getCatMessages(EntityManager em, String messageCode,
			String languageCode) {
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterionWildcard("c.messageCode", messageCode, true);
		qb.addCriterionWildcard("c.languageCode", languageCode, true);
		List<CatMessages> cats = (List<CatMessages>) qb.getQuery(em)
				.getResultList();
		return cats != null && cats.size() > 0 ? cats.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public List<CatMessages> getCatMessagesList(String messageCode) {
		log.info("getCatMessagesList messageCode={} ", messageCode);
		if (StringUtils.isBlank(messageCode)) {
			return new ArrayList<CatMessages>();
		}
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterion("c.messageCode", "=", messageCode, true);
		List<CatMessages> cats = (List<CatMessages>) qb.getQuery(
				getEntityManager()).getResultList();
		return cats;
	}

    /**
     * Get all messages of a given class in a given language
     * 
     * @param clazz Class to get messages for
     * @param languageCode Language to get messages in
     * @return A list of messages
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<CatMessages> getCatMessagesList(Class clazz, String languageCode) {
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.like("c.messageCode", getMessageCodePrefix(clazz), QueryLikeStyleEnum.MATCH_BEGINNING, false);
        qb.addCriterionWildcard("c.languageCode", languageCode, true);
        List<CatMessages> cats = (List<CatMessages>) qb.getQuery(getEntityManager()).getResultList();
        return cats;
    }

	public void batchRemove(String entityName, Long id, Provider provider) {
		String strQuery = "DELETE FROM "
				+ CatMessages.class.getSimpleName()
				+ " c WHERE c.messageCode=:messageCode and c.provider=:provider";

		try {
			getEntityManager().createQuery(strQuery)
					.setParameter("messageCode", entityName + "_" + id)
					.setParameter("provider", provider).executeUpdate();
		} catch (Exception e) {
			log.error("failed to batch remove",e);
		}
	}

    /**
     * Get a message code prefix for a given entity
     * 
     * @param entity Entity
     * @return A message code in a format "className_id"
     */
    public String getMessageCode(IEntity entity) {
        String className = entity.getClass().getSimpleName();
        // Suppress javassist proxy suffix
        if (className.indexOf("_") >= 0) {
            className = className.substring(0, className.indexOf("_"));
        }
        return className + "_" + entity.getId();
    }
    
    /**
     * Get a message code prefix for a given class
     * 
     * @param clazz Class
     * @return A message code in a format "className_"
     */
    @SuppressWarnings("rawtypes")
    public String getMessageCodePrefix(Class clazz) {
        String className = clazz.getSimpleName();
        // Suppress javassist proxy suffix
        if (className.indexOf("_") >= 0) {
            className = className.substring(0, className.indexOf("_"));
        }
        return className + "_";
    }
    
   
	 public CatMessages findByCodeAndLanguage(String messageCode,String languageCode, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterionWildcard("c.messageCode", messageCode, true);
		qb.addCriterionWildcard("c.languageCode", languageCode, true);
		qb.addCriterionEntity("provider", provider);
		try {
            return (CatMessages) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
	}
	 
	 
	    @SuppressWarnings({ "unchecked" })
	    @Override
	    public List<CatMessages> list(PaginationConfiguration config) {
	        List<CatMessages> catMessages=super.list(config);
	        for(CatMessages catMsg:catMessages){
	        	BusinessEntity obj=getObject(catMsg);
	        	catMsg.setEntityCode(obj.getCode());
	        	catMsg.setEntityDescription(obj.getDescription());
	        } 
	        return catMessages;
	    }
	 
	 private BusinessEntity getObject(CatMessages catMessages){
			if(catMessages==null){
				return null;
			}
			String messagesCode=catMessages.getMessageCode();
			String[] codes=messagesCode.split("_");
			
			if(codes!=null&&codes.length==2){
				Long id=null;
				try{
					id=Long.valueOf(codes[1]);
				}catch(Exception e){
					return null;
				}
				if("Title".equals(codes[0])){
					return titleService.findById(id);
				}else if("Tax".equals(codes[0])){
					return taxService.findById(id);
				}else if("InvoiceCategory".equals(codes[0])){
					return invoiceCategoryService.findById(id);
				}else if("InvoiceSubCategory".equals(codes[0])){
					return invoiceSubCategoryService.findById(id);
				}else if("UsageChargeTemplate".equals(codes[0])){
					return usageChargeTemplateService.findById(id);
				}else if("OneShotChargeTemplate".equals(codes[0])){
					return oneShotChargeTemplateService.findById(id);
				}else if("RecurringChargeTemplate".equals(codes[0])){
					return recurringChargeTemplateService.findById(id);
				}else if("PricePlanMatrix".equals(codes[0])){
					return pricePlanMatrixService.findById(id);
				}
			}
			
			return null;
		}
}
