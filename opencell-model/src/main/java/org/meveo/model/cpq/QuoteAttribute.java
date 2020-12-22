package org.meveo.model.cpq;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.quote.QuoteProduct;

@Entity
@Table(name = "cpq_quote_attribute")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_attribute_seq")})
@NamedQuery(name = "QuoteAttribute.findByAttributeAndQuoteProduct", query = "select q from QuoteAttribute q left join q.attribute qa left join q.quoteProduct qq where qq.id=:quoteProductId and qa.id=:attributeId")
public class QuoteAttribute extends AuditableEntity{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 582541599112934770L;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_attribute_id", nullable = false)
	private Attribute attribute;
	
	
	@Column(name = "value")
	private String value;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "cpq_quote_product_id", nullable = false)
	private QuoteProduct quoteProduct;

	/**
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

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
		result = prime * result + Objects.hash(attribute, quoteProduct, value);
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
				&& Objects.equals(value, other.value);
	}

	public void update(QuoteAttribute other) {
		this.value = other.value;
		this.attribute = other.attribute;
		this.auditable = other.auditable;
		this.quoteProduct = other.quoteProduct;
		this.id = other.id;
		this.version = other.version;
		
	}

}
