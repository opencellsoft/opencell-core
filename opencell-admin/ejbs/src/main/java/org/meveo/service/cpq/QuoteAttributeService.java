package org.meveo.service.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.OfferTemplateAttribute;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.offer.QuoteOffer;
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
    	Attribute attribute=quoteAttribute.getAttribute();
    	
    	//check mandatory product attributes
    	if(quoteAttribute.getQuoteProduct()!=null) {
    	for(ProductVersionAttribute productVersionAttribute:quoteAttribute.getQuoteProduct().getProductVersion().getAttributes()) {
    	if(productVersionAttribute.isMandatory() && !checkAttributeValue(quoteAttribute) &&  productVersionAttribute.getDefaultValue()==null) {
			 throw new BusinessApiException("The attribute " + attribute.getCode() + " is mandatory");
		}}
    	}
    	
    	//check mandatory offer attributes
    	if(quoteAttribute.getQuoteOffer()!=null) {
    	QuoteOffer quoteOffer=quoteAttribute.getQuoteOffer();
    	for(OfferTemplateAttribute offerTemplateAttribute:quoteOffer.getOfferTemplate().getOfferAttributes()) {
        	if(offerTemplateAttribute.isMandatory() && !checkAttributeValue(quoteAttribute) &&  offerTemplateAttribute.getDefaultValue()==null) {
    			 throw new BusinessApiException("The attribute " + attribute.getCode() + " is mandatory");
    		}}
    	}
    	
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
	        	var productVersionAttribute = mandatoryEl.get();
	        	if(mandatoryEl.isPresent()) {
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
	    		var offerTempalteAttribute = offerTemplatMandatoryEl.get();
				if(offerTemplatMandatoryEl.isPresent()) {
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
	    
	    
	    public boolean checkAttributeValue(QuoteAttribute quoteAttribute) {
			Attribute attribute=quoteAttribute.getAttribute();
			switch (attribute.getAttributeType()) {
				case TOTAL :
				case COUNT :
				case NUMERIC :
				case INTEGER:
					if(quoteAttribute.getDoubleValue()==null && quoteAttribute.getStringValue()==null)
						return false;
					break;
				case LIST_MULTIPLE_TEXT:
				case LIST_TEXT:
				case EXPRESSION_LANGUAGE :
				case TEXT:
					if(quoteAttribute.getStringValue()==null)
						return false;
					break;

				case DATE:
					if(quoteAttribute.getDateValue()==null)
						return false;
					break;
				case BOOLEAN:
					if(quoteAttribute.getBooleanValue()==null)
						return false;
					break;
				default:
					break;
			}
			return true;
		}
}