package org.meveo.service.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

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
    	if(quoteVersion!= null)
    		checkMandatoryEl(quoteAttribute, quoteVersion);
        super.create(quoteAttribute);
    }

    @Override
    public QuoteAttribute update(QuoteAttribute quoteAttribute) throws BusinessException {
    	QuoteVersion quoteVersion=quoteAttribute.getQuoteProduct()!=null?
    			quoteAttribute.getQuoteProduct().getQuoteVersion():quoteAttribute.getQuoteOffer()!=null?quoteAttribute.getQuoteOffer().getQuoteVersion():null;
    	if(quoteVersion!= null)
    		checkMandatoryEl(quoteAttribute, quoteVersion);
        return super.update(quoteAttribute);
    }
    

	private void checkMandatoryEl(QuoteAttribute quoteAttribute, QuoteVersion quoteVersion) {
    	if(!quoteAttribute.getAttribute().getProductVersionAttributes().isEmpty()) {
    		if(quoteAttribute.getQuoteProduct() != null
    				&& quoteAttribute.getQuoteProduct().getProductVersion() != null) {
	        	var mandatoryEl = findMandatoryByProductVersion(quoteAttribute, quoteAttribute.getQuoteProduct().getProductVersion());
	        	if(mandatoryEl.isPresent()) {
	        		var productVersionAttribute = mandatoryEl.get();
	        		super.evaluateMandatoryEl(productVersionAttribute.getValidationType(), 
							productVersionAttribute.getValidationPattern(),
							productVersionAttribute.getValidationLabel(),
							quoteAttribute, 
							productVersionAttribute.getMandatoryWithEl(), 
							quoteVersion.getQuote(), quoteVersion, null, null);
	        	}	
    		}
    		
    		if(quoteAttribute.getQuoteOffer() != null 
    				&& quoteAttribute.getQuoteOffer().getOfferTemplate() != null) {
	    		var offerTemplatMandatoryEl = findMandatoryByOfferTemplate(quoteAttribute, quoteAttribute.getQuoteOffer().getOfferTemplate());
				if(offerTemplatMandatoryEl.isPresent()) {
					var offerTempalteAttribute = offerTemplatMandatoryEl.get();
	        		super.evaluateMandatoryEl(offerTempalteAttribute.getValidationType(), 
	        									offerTempalteAttribute.getValidationPattern(),
	        									offerTempalteAttribute.getValidationLabel(),
	        									quoteAttribute, 
	        									offerTempalteAttribute.getMandatoryWithEl(), 
	        									quoteVersion.getQuote(), 
	        									quoteVersion, null, null);
	        	}
    		}
        }
    }
	
	  @SuppressWarnings("unchecked")
	    public List<QuoteAttribute> findByQuoteVersionAndTotaltype(Long quoteVersionId) { 
		 List<QuoteAttribute> quoteAttributs=new ArrayList<QuoteAttribute>();
	    	try {
	    		quoteAttributs = (List<QuoteAttribute>)getEntityManager().createNamedQuery("QuoteAttribute.findByQuoteVersionAndTotalType").setParameter("quoteVersionId", quoteVersionId).getResultList();
	    	} catch (Exception e) {
	    		log.error("findByQuoteVersionAndTotaltype error ", e.getMessage());
	    	}
	    	return quoteAttributs;
	    }
	     
	    public Double getSumDoubleByVersionAndAttribute(Long quoteVersionId,Long attributeId) {  
	    	Double totalValue=0.0;
	    	try {
	    		totalValue = (Double)getEntityManager().createNamedQuery("QuoteAttribute.getSumDoubleByVersionAndAttribute").setParameter("quoteVersionId", quoteVersionId).setParameter("attributeId", attributeId).getSingleResult();
	    	} catch (Exception e) {
	    		log.error("getSumDoubleByVersionAndAttribute error ", e.getMessage());
	    	}
	    	return totalValue;
	    }
}