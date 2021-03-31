package org.meveo.model.cpq.trade;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.RuleOperatorEnum;
import org.meveo.model.cpq.tags.Tag;

/**
 *  @author Tarik FAKHOURI.
 *  @author Mbarek-Ay
 *   @author Rachid.AIT
 *	@version 10.0
 */
@Entity
@Table(name = "cpq_commercial_rule_line", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_commercial_rule_line_seq")})
@NamedQueries({  
	@NamedQuery(name = "CommercialRuleLine.getSourceAttributeRules", query = "select c.id from CommercialRuleLine c where c.sourceAttribute.code=:attributeCode"),
	@NamedQuery(name = "CommercialRuleLine.getSourceProductAttributeRules", query = "select c.id from CommercialRuleLine c where c.sourceAttribute.code=:attributeCode and c.sourceProduct.code=:productCode"),
	@NamedQuery(name = "CommercialRuleLine.getSourceProductRules", query = "select c.id from CommercialRuleLine c where c.sourceProduct.code=:productCode"),
	@NamedQuery(name = "CommercialRuleLine.getSourceProductRulesWithOffer", query = "select c.id from CommercialRuleLine c where c.sourceOfferTemplate.code=:offerCode and c.sourceProduct.code=:productCode"),
	@NamedQuery(name = "CommercialRuleLine.getSourceGroupedAttributeRules", query = "select c.id from CommercialRuleLine c where c.sourceGroupedAttributes.code=:groupedAttributeCode and c.sourceProduct.code=:productCode")
})
public class CommercialRuleLine extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7531793312686419097L;
	
	
	/**
	 * Trade rule header
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "commercial_rule_item_id", referencedColumnName = "id")
	private CommercialRuleItem commercialRuleItem;
	
	/**
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_template_id", referencedColumnName = "id")
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
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "attribute_id", referencedColumnName = "id") 
	private Attribute sourceAttribute;
	
	 /** 
     * grouped service
     */
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grouped_attributes_id", referencedColumnName = "id")
	private GroupedAttributes sourceGroupedAttributes;
	
	/**
	 * tag source
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id", referencedColumnName = "id")
	private Tag sourceTag;
	
	/**
	 * attribute value
	 */
	@Column(name = "source_attribute_value", length = 255)
	@Size(max = 255)
	private String sourceAttributeValue;
	
	
	
	/**
	 * grouped attribute value
	 */
	@Column(name = "source_grouped_attribute_value", length = 255)
	@Size(max = 255)
	private String sourceGroupedAttributeValue;
	
 
	/**
	 * rule operator
	 */
	@Column(name = "operator", nullable = false)
	@Enumerated(EnumType.STRING) 
	private RuleOperatorEnum operator;
	 
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
	 * @return the sourceAttribute
	 */
	public Attribute getSourceAttribute() {
		return sourceAttribute;
	}

	/**
	 * @param sourceAttribute the sourceAttribute to set
	 */
	public void setSourceAttribute(Attribute sourceAttribute) {
		this.sourceAttribute = sourceAttribute;
	}

	/**
	 * @return the sourceGroupedAttributes
	 */
	public GroupedAttributes getSourceGroupedAttributes() {
		return sourceGroupedAttributes;
	}

	/**
	 * @param sourceGroupedAttributes the sourceGroupedAttributes to set
	 */
	public void setSourceGroupedAttributes(GroupedAttributes sourceGroupedAttributes) {
		this.sourceGroupedAttributes = sourceGroupedAttributes;
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
	 * @return the sourceAttributeValue
	 */
	public String getSourceAttributeValue() {
		return sourceAttributeValue;
	}

	/**
	 * @param sourceAttributeValue the sourceAttributeValue to set
	 */
	public void setSourceAttributeValue(String sourceAttributeValue) {
		this.sourceAttributeValue = sourceAttributeValue;
	}

 
	/**
	 * @return the operator
	 */
	public RuleOperatorEnum getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(RuleOperatorEnum operator) {
		this.operator = operator;
	}

	/**
	 * @return the commercialRuleItem
	 */
	public CommercialRuleItem getCommercialRuleItem() {
		return commercialRuleItem;
	}

	/**
	 * @param commercialRuleItem the commercialRuleItem to set
	 */
	public void setCommercialRuleItem(CommercialRuleItem commercialRuleItem) {
		this.commercialRuleItem = commercialRuleItem;
	}

	/**
	 * @return the sourceGroupedAttributeValue
	 */
	public String getSourceGroupedAttributeValue() {
		return sourceGroupedAttributeValue;
	}

	/**
	 * @param sourceGroupedAttributeValue the sourceGroupedAttributeValue to set
	 */
	public void setSourceGroupedAttributeValue(String sourceGroupedAttributeValue) {
		this.sourceGroupedAttributeValue = sourceGroupedAttributeValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(commercialRuleItem, operator, sourceAttribute, sourceAttributeValue,
				sourceGroupedAttributeValue, sourceGroupedAttributes, sourceOfferTemplate, sourceProduct,
				sourceProductVersion, sourceTag);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		CommercialRuleLine other = (CommercialRuleLine) obj;
		return Objects.equals(commercialRuleItem, other.commercialRuleItem) && operator == other.operator
				&& Objects.equals(sourceAttribute, other.sourceAttribute)
				&& Objects.equals(sourceAttributeValue, other.sourceAttributeValue)
				&& Objects.equals(sourceGroupedAttributeValue, other.sourceGroupedAttributeValue)
				&& Objects.equals(sourceGroupedAttributes, other.sourceGroupedAttributes)
				&& Objects.equals(sourceOfferTemplate, other.sourceOfferTemplate)
				&& Objects.equals(sourceProduct, other.sourceProduct)
				&& Objects.equals(sourceProductVersion, other.sourceProductVersion)
				&& Objects.equals(sourceTag, other.sourceTag);
	}
	
	

 

}

