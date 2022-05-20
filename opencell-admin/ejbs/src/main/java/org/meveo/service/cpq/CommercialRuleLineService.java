package org.meveo.service.cpq;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.trade.CommercialRuleLine;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;

/**
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay.
 * @version 10.0
 * 
 * Commercial Rule Line type service implementation.
 */

@Stateless
public class CommercialRuleLineService extends PersistenceService<CommercialRuleLine> {
	
	@Inject
	AttributeService attributeService;
	
	@Inject
	OfferTemplateService offerTemplateService;
	
	@Inject
	ProductService productService;
	
	@Inject
	ProductVersionService productVersionService;
	
	@Inject
	GroupedAttributeService groupedAttributeService;
	
	@SuppressWarnings("unchecked")
	public List<Long> getSourceProductAttributeRules(String attributeCode,String productCode) throws BusinessException{
		String queryName="CommercialRuleLine.getSourceAttributeRules";
		if(!StringUtils.isEmpty(productCode)) { 
			queryName="CommercialRuleLine.getSourceProductAttributeRules";
		}
		Query query = getEntityManager().createNamedQuery(queryName)
				.setParameter("attributeCode", attributeCode);
		if(!StringUtils.isEmpty(productCode)) { 
			query.setParameter("productCode", productCode);
		}
				
		List<Long> commercialRules=(List<Long>)query.getResultList();
		return commercialRules;
	} 
	
	@SuppressWarnings("unchecked")
	public List<Long> getSourceOfferAttributeRules(String attributeCode,String offerCode) throws BusinessException{
		String queryName="CommercialRuleLine.getSourceAttributeRules";
		
		if(!StringUtils.isEmpty(offerCode)) { 
			queryName="CommercialRuleLine.getSourceOfferAttributeRules";
		}
		Query query = getEntityManager().createNamedQuery(queryName)
				.setParameter("attributeCode", attributeCode);
		if(!StringUtils.isEmpty(offerCode)) { 
			query.setParameter("offerCode", offerCode);
		}
				
		List<Long> commercialRules=(List<Long>)query.getResultList();
		return commercialRules;
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getSourceProductRules(String offerCode,String productCode,Integer currentVersion) throws BusinessException{
		String queryName="CommercialRuleLine.getSourceProductRules";
		if(!StringUtils.isEmpty(offerCode)) {
			queryName="CommercialRuleLine.getSourceProductRulesWithOffer";
		}
		
		if(currentVersion!=null) {
			ProductVersion productVersion=productVersionService.findByProductAndVersion(productCode, currentVersion);
			if(productVersion==null) {
				throw new EntityDoesNotExistsException(ProductVersion.class, productCode+" and version "+currentVersion);
			}
		}
		
		Query query = getEntityManager().createNamedQuery(queryName)
				.setParameter("productCode", productCode);
		if(!StringUtils.isEmpty(offerCode)) {
		 query.setParameter("offerCode", offerCode);
		}
		List<Long> commercialRules=(List<Long>)query.getResultList();
		return commercialRules;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Long> getSourceGroupedAttributesRules(String groupedAttributeCode,String productCode) throws BusinessException{
		
		Query query = getEntityManager().createNamedQuery("CommercialRuleLine.getSourceGroupedAttributeRules")
				.setParameter("groupedAttributeCode", groupedAttributeCode).setParameter("productCode", productCode);
		List<Long> commercialRules=(List<Long>)query.getResultList();
		return commercialRules;
	}

}