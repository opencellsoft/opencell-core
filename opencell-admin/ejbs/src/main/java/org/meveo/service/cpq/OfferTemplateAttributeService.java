/**
 * 
 */
package org.meveo.service.cpq;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.OfferTemplateAttribute;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.util.regex.Pattern;

import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

/**
 * @author Rachid.AITYAAZZA
 *
 */

@Stateless
public class OfferTemplateAttributeService extends PersistenceService<OfferTemplateAttribute>{
	

	
	public OfferTemplateAttribute findByOfferTemplateAndAttribute(Long offerTemplateId, Long attributeVersion) {
		try{
			return  this.getEntityManager().createNamedQuery("OfferTemplateAttribute.findByAttributeAndOfferTemplate", OfferTemplateAttribute.class)
																	.setParameter("attributeId", attributeVersion)
																	.setParameter("offerTemplateId", offerTemplateId)
																	.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
}