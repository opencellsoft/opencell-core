package org.meveo.model.cpq.trade;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.tags.Tag;

/**
 *  @author Tarik FAKHOURI.
 *  @author Mbarek-Ay
 *   @author Rachid.AIT
 *	@version 10.0
 */
@Entity
@Table(name = "cpq_trade_rule_line", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_trade_rule_line_seq"), })
public class TradeRuleLine extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7531793312686419097L;
	
	/**
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "commercial_offer_id", referencedColumnName = "id")
	private OfferTemplate sourceOfferTemplate;
	

	/**
	 * product code
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Product sourceProduct;

	/**
	 * version of the product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_version_id", referencedColumnName = "id")
	private  ProductVersion sourceProductVersion;

	/**
	 * attribute name
	 */
	@Column(name = "source_attribute_name", length = 20)
	@Size(max = 20)
	private String sourceAttributeName;
	
	/**
	 * tag source
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id", referencedColumnName = "id")
	private Tag sourceTag;
	
	/**
	 * operator : 0 => product or attribute is selected
	 */
	@Column(name = "operator")
	private int operator;
	
	/**
	 * attribute value 
	 */
	@Column(name = "value", length = 255)
	@Size(max = 255)
	private String value;

	/**
	 * @return the sourceOfferTemplate
	 */
	public OfferTemplate getSourceOfferTemplate() {
		return sourceOfferTemplate;
	}

	/**
	 * @param sourceOfferTemplate the sourceOfferTemplate to set
	 */
	public void setSourceOfferTemplate(OfferTemplate sourceOfferTemplate) {
		this.sourceOfferTemplate = sourceOfferTemplate;
	}

	/**
	 * @return the sourceProduct
	 */
	public Product getSourceProduct() {
		return sourceProduct;
	}

	/**
	 * @param sourceProduct the sourceProduct to set
	 */
	public void setSourceProduct(Product sourceProduct) {
		this.sourceProduct = sourceProduct;
	}

	/**
	 * @return the sourceProductVersion
	 */
	public ProductVersion getSourceProductVersion() {
		return sourceProductVersion;
	}

	/**
	 * @param sourceProductVersion the sourceProductVersion to set
	 */
	public void setSourceProductVersion(ProductVersion sourceProductVersion) {
		this.sourceProductVersion = sourceProductVersion;
	}

	/**
	 * @return the sourceAttributeName
	 */
	public String getSourceAttributeName() {
		return sourceAttributeName;
	}

	/**
	 * @param sourceAttributeName the sourceAttributeName to set
	 */
	public void setSourceAttributeName(String sourceAttributeName) {
		this.sourceAttributeName = sourceAttributeName;
	}

	/**
	 * @return the sourceTag
	 */
	public Tag getSourceTag() {
		return sourceTag;
	}

	/**
	 * @param sourceTag the sourceTag to set
	 */
	public void setSourceTag(Tag sourceTag) {
		this.sourceTag = sourceTag;
	}

	/**
	 * @return the operator
	 */
	public int getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(int operator) {
		this.operator = operator;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(operator, sourceAttributeName, sourceOfferTemplate, sourceProduct,
				sourceProductVersion, sourceTag, value);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradeRuleLine other = (TradeRuleLine) obj;
		return operator == other.operator && Objects.equals(sourceAttributeName, other.sourceAttributeName)
				&& Objects.equals(sourceOfferTemplate, other.sourceOfferTemplate)
				&& Objects.equals(sourceProduct, other.sourceProduct)
				&& Objects.equals(sourceProductVersion, other.sourceProductVersion)
				&& Objects.equals(sourceTag, other.sourceTag) && Objects.equals(value, other.value);
	}
}

