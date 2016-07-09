package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.BusinessService;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class ChargeTemplateServiceAll extends BusinessService<ChargeTemplate> {

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
}