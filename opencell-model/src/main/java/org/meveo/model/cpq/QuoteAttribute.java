package org.meveo.model.cpq;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.quote.QuoteProduct;

@Entity
@Table(name = "cpq_quote_attribute", uniqueConstraints = @UniqueConstraint(columnNames = { "cpq_attribute_id", "cpq_quote_product_id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_attribute_seq")})
public class QuoteAttribute extends AuditableEntity{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 582541599112934770L;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_attribute_id", nullable = false)
	@NotNull
	private Attribute attribute;
	
	
	@Column(name = "value")
	private String value;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_quote_product_id", nullable = false)
	@NotNull
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

}
