package org.meveo.model.cpq.trade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.tags.Tag;

/**
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay
 * @version 10.0
 */
@Entity
@Table(name = "cpq_commercial_rule_header", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_commercial_rule_header_seq"), })
public class CommercialRuleHeader extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1589097219846982133L;
	
	/**
	 * rule type : can be 0 => Prérequis / 1 => incompatible 
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "rule_type", nullable = false)
	@NotNull
	private RuleTypeEnum ruleType = RuleTypeEnum.PRE_REQUISITE;

	/**
	 * offer code
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_template_id", referencedColumnName = "id")
	private OfferTemplate targetOfferTemplate;

	/**
	 * product code
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Product targetProduct;

	/**
	 * version of the product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_version_id", referencedColumnName = "id")
	private  ProductVersion targetProductVersion;

	
	 /** 
     * grouped service
     */
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grouped_attributes_id", referencedColumnName = "id")
	private GroupedAttributes targetGroupedAttributes;
	

	/**
	 * attribute id
	 */
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "attribute_id", referencedColumnName = "id") 
	private Attribute targetAttribute;
	
	
	/**
	 * attribute value
	 */
	@Column(name = "target_attribute_value", length = 255)
	@Size(max = 255)
	private String targetAttributeValue;

	/**
	 * tag target
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id", referencedColumnName = "id")
	private Tag targetTag;

	/**
	 * rule El
	 */
	@Size(max = 2000)
    @Column(name = "rule_el", columnDefinition = "TEXT")
	private String ruleEl;

	/**
	 * @return the ruleType
	 */
	public RuleTypeEnum getRuleType() {
		return ruleType;
	}

	/**
	 * @param ruleType the ruleType to set
	 */
	public void setRuleType(RuleTypeEnum ruleType) {
		this.ruleType = ruleType;
	}

	/**
	 * @return the targetCommercialOffer
	 */
	public OfferTemplate getTargetOfferTemplate() {
		return targetOfferTemplate;
	}

	/**
	 * @param targetCommercialOffer the targetCommercialOffer to set
	 */
	public void setTargetOfferTemplate(OfferTemplate offerTemplate) {
		this.targetOfferTemplate = offerTemplate;
	}

	/**
	 * @return the targetProduct
	 */
	public Product getTargetProduct() {
		return targetProduct;
	}

	/**
	 * @param targetProduct the targetProduct to set
	 */
	public void setTargetProduct(Product targetProduct) {
		this.targetProduct = targetProduct;
	}

	/**
	 * @return the targetProductVersion
	 */
	public ProductVersion getTargetProductVersion() {
		return targetProductVersion;
	}

	/**
	 * @param targetProductVersion the targetProductVersion to set
	 */
	public void setTargetProductVersion(ProductVersion targetProductVersion) {
		this.targetProductVersion = targetProductVersion;
	}

	 
	/**
	 * @return the targetGroupedAttributes
	 */
	public GroupedAttributes getTargetGroupedAttributes() {
		return targetGroupedAttributes;
	}

	/**
	 * @param targetGroupedAttributes the targetGroupedAttributes to set
	 */
	public void setTargetGroupedAttributes(GroupedAttributes targetGroupedAttributes) {
		this.targetGroupedAttributes = targetGroupedAttributes;
	}

	/**
	 * @return the targetAttribute
	 */
	public Attribute getTargetAttribute() {
		return targetAttribute;
	}

	/**
	 * @param targetAttribute the targetAttribute to set
	 */
	public void setTargetAttribute(Attribute targetAttribute) {
		this.targetAttribute = targetAttribute;
	}

	/**
	 * @return the targetAttributeValue
	 */
	public String getTargetAttributeValue() {
		return targetAttributeValue;
	}

	/**
	 * @param targetAttributeValue the targetAttributeValue to set
	 */
	public void setTargetAttributeValue(String targetAttributeValue) {
		this.targetAttributeValue = targetAttributeValue;
	}

	/**
	 * @return the targetTag
	 */
	public Tag getTargetTag() {
		return targetTag;
	}

	/**
	 * @param targetTag the targetTag to set
	 */
	public void setTargetTag(Tag targetTag) {
		this.targetTag = targetTag;
	}

	/**
	 * @return the ruleEl
	 */
	public String getRuleEl() {
		return ruleEl;
	}

	/**
	 * @param ruleEl the ruleEl to set
	 */
	public void setRuleEl(String ruleEl) {
		this.ruleEl = ruleEl;
	}

 
	
	
	
	

}
