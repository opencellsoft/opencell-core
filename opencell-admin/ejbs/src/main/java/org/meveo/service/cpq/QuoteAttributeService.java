package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.quote.QuoteVersion;
/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteAttributeService extends AttributeValueService<QuoteAttribute> {

	
    public AttributeValue findByAttributeAndQuoteProduct(Long attributeId, Long quoteProductId) {
        try {
            return (AttributeValue) this.getEntityManager()
					.createNamedQuery("QuoteAttribute.findByAttributeAndQuoteProduct")
                    .setParameter("attributeId", attributeId)
                    .setParameter("quoteProductId", quoteProductId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void create(QuoteAttribute quoteAttribute) throws BusinessException {
    	QuoteVersion quoteVersion=quoteAttribute.getQuoteProduct()!=null?
    			quoteAttribute.getQuoteProduct().getQuoteVersion():quoteAttribute.getQuoteOffer()!=null?quoteAttribute.getQuoteOffer().getQuoteVersion():null;
        checkMandatoryEl(quoteAttribute, quoteVersion);
        super.create(quoteAttribute);
    }

    @Override
    public QuoteAttribute update(QuoteAttribute quoteAttribute) throws BusinessException {
    	QuoteVersion quoteVersion=quoteAttribute.getQuoteProduct()!=null?
    			quoteAttribute.getQuoteProduct().getQuoteVersion():quoteAttribute.getQuoteOffer()!=null?quoteAttribute.getQuoteOffer().getQuoteVersion():null;
		checkMandatoryEl(quoteAttribute, quoteVersion);
        return super.update(quoteAttribute);
    }
    

	private void checkMandatoryEl(QuoteAttribute quoteAttribute, QuoteVersion quoteVersion) {
    	if(!quoteAttribute.getAttribute().getProductVersionAttributes().isEmpty()) {
    		if(quoteAttribute.getQuoteProduct() != null
    				&& quoteAttribute.getQuoteProduct().getProductVersion() != null) {
	        	var mandatoryEl = findMandatoryByProductVersion(quoteAttribute, quoteAttribute.getQuoteProduct().getProductVersion());
	        	var productVersionAttribute = mandatoryEl.get();
	        	if(mandatoryEl.isPresent()) {
	        		super.evaluateMandatoryEl(productVersionAttribute.getValidationType(), 
							productVersionAttribute.getValidationPattern(), 
							quoteAttribute, 
							productVersionAttribute.getMandatoryWithEl(), 
							quoteVersion.getQuote(), quoteVersion, null, null);
	        	}	
    		}
    		
    		if(quoteAttribute.getQuoteOffer() != null 
    				&& quoteAttribute.getQuoteOffer().getOfferTemplate() != null) {
	    		var offerTemplatMandatoryEl = findMandatoryByOfferTemplate(quoteAttribute, quoteAttribute.getQuoteOffer().getOfferTemplate());
	    		var offerTempalteAttribute = offerTemplatMandatoryEl.get();
				if(offerTemplatMandatoryEl.isPresent()) {
	        		super.evaluateMandatoryEl(offerTempalteAttribute.getValidationType(), 
	        									offerTempalteAttribute.getValidationPattern(), 
	        									quoteAttribute, 
	        									offerTempalteAttribute.getMandatoryWithEl(), 
	        									quoteVersion.getQuote(), 
	        									quoteVersion, null, null);
	        	}
    		}
        }
    }
}