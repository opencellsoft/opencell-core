package org.meveo.model.cpq;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteProduct;

@Entity
@Table(name = "cpq_quote_attribute")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_attribute_seq")})
@NamedQuery(name = "QuoteAttribute.findByAttributeAndQuoteProduct", query = "select q from QuoteAttribute q left join q.attribute qa left join q.quoteProduct qq where qq.id=:quoteProductId and qa.id=:attributeId")
public class QuoteAttribute extends AttributeValue {

	
	public QuoteAttribute() {
	}


	public QuoteAttribute(QuoteAttribute copy) {
		this.attribute = copy.attribute;
		this.stringValue = copy.stringValue;
		this.quoteProduct = copy.quoteProduct;
		this.dateValue = copy.dateValue;
		this.doubleValue = copy.doubleValue;
		this.quoteProduct = copy.quoteProduct;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 582541599112934770L;


	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "cpq_quote_product_id", nullable = false)
	private QuoteProduct  quoteProduct ;
	

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "cpq_quote_offer_id")
	private QuoteOffer  quoteOffer;


	/**
	 * @return the quoteProduct
	 */
	public QuoteProduct getQuoteProduct() {
		return quoteProduct;
	}

	/**
	 * @param quoteProduct the quoteProduct to set
	 */
	public void setQuoteProduct(QuoteProduct quoteProduct) {
		this.quoteProduct = quoteProduct;
	}
	
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(attribute, quoteProduct, stringValue);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		QuoteAttribute other = (QuoteAttribute) obj;
		return Objects.equals(attribute, other.attribute) && Objects.equals(quoteProduct, other.quoteProduct)
				&& Objects.equals(stringValue, other.stringValue) && Objects.equals(dateValue, other.dateValue) && Objects.equals(doubleValue, other.doubleValue);
	}

	public void update(QuoteAttribute other) {
		this.stringValue = other.stringValue;
		this.doubleValue = other.doubleValue;
		this.dateValue = other.dateValue;
		this.attribute = other.attribute;
		this.auditable = other.auditable;
		this.quoteProduct = other.quoteProduct;
		this.id = other.id;
		this.version = other.version;
		
	}

	/**
	 * @return the quoteOffer
	 */
	public QuoteOffer getQuoteOffer() {
		return quoteOffer;
	}

	
	

	/**
	 * @param quoteOffer the quoteOffer to set
	 */
	public void setQuoteOffer(QuoteOffer quoteOffer) {
		this.quoteOffer = quoteOffer;
	}
	
	
}
