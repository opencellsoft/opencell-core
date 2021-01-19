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
public class QuoteAttribute extends AuditableEntity{

	
	public QuoteAttribute() {
	}


	public QuoteAttribute(QuoteAttribute copy) {
		this.attribute = copy.attribute;
		this.stringValue = copy.stringValue;
		this.quoteProduct = copy.quoteProduct;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 582541599112934770L;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_attribute_id", nullable = false)
	private Attribute attribute;
	
	
	@Column(name = "string_value")
	private String stringValue;

	@Column(name = "date_value")
	private Date dateValue;

	@Column(name = "double_value")
	private Double doubleValue;

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
	 * @return the stringValue
	 */
	// Double, String, Date
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @param stringValue the stringValue to set
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
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
		result = prime * result + Objects.hash(attribute, quoteProduct, stringValue);
		return result;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
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
}
