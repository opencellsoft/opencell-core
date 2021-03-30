package org.meveo.service.cpq;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.trade.CommercialRuleLine;
import org.meveo.service.base.PersistenceService;

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
	
	@SuppressWarnings("unchecked")
	public List<Long> getSourceProductAttributeRules(String attributeCode,String productCode) throws BusinessException{
		Attribute attribute=attributeService.findByCode(attributeCode);
		String queryName="CommercialRuleLine.getSourceAttributeRules";
		if(attribute == null) { 
			throw new EntityDoesNotExistsException(Attribute.class,attributeCode);
		}
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

}