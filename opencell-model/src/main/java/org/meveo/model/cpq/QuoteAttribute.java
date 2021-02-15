package org.meveo.model.cpq;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.quote.QuoteProduct;

@Entity
@Table(name = "cpq_quote_attribute")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_attribute_seq")})
@NamedQuery(name = "QuoteAttribute.findByAttributeAndQuoteProduct", query = "select q from QuoteAttribute q left join q.attribute qa left join q.quoteProduct qq where qq.id=:quoteProductId and qa.id=:attributeId")
public class QuoteAttribute extends AttributeValue<QuoteAttribute> {

	
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
	@JoinColumn(name = "cpq_quote_product_id")
	private QuoteProduct quoteProduct;


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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		QuoteAttribute that = (QuoteAttribute) o;
		return Objects.equals(quoteProduct, that.quoteProduct);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), quoteProduct);
	}
}
