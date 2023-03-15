package org.meveo.model.cpq.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.enums.ScopeTypeEnum;
import org.meveo.model.cpq.tags.Tag;

/**
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay
 * @version 10.0
 */
@Entity
@Cacheable
@Table(name = "cpq_commercial_rule_header", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
 @Parameter(name = "sequence_name", value = "cpq_commercial_rule_header_seq")})
@NamedQueries({ 
	@NamedQuery(name = "CommercialRuleHeader.getTagRules", query = "select c from CommercialRuleHeader c where c.targetTag.code=:tagCode"),
	@NamedQuery(name = "CommercialRuleHeader.getOfferAttributeRules", query = "select c from CommercialRuleHeader c where c.targetAttribute.code=:attributeCode and c.targetOfferTemplate.code=:offerTemplateCode", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
    @NamedQuery(name = "CommercialRuleHeader.getProductAttributeRules", query = "select c from CommercialRuleHeader c where c.targetAttribute.code=:attributeCode and c.targetProduct.code=:productCode", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
	@NamedQuery(name = "CommercialRuleHeader.getAttributeRules", query = "select c from CommercialRuleHeader c where c.targetAttribute.code=:attributeCode", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
	@NamedQuery(name = "CommercialRuleHeader.getOfferRules", query = "select c from CommercialRuleHeader c where c.targetOfferTemplate.code=:offerCode", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
	@NamedQuery(name = "CommercialRuleHeader.getGroupedAttributeRules", query = "select c from CommercialRuleHeader c where c.targetGroupedAttributes.code=:groupedAttributeCode and c.targetProduct.code=:productCode"),
	@NamedQuery(name = "CommercialRuleHeader.getProductRules", query = "select c from CommercialRuleHeader c where c.targetProduct.code=:productCode and c.targetAttribute is null and c.targetGroupedAttributes is null", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
	@NamedQuery(name = "CommercialRuleHeader.getProductRulesWithOffer", query = "select c from CommercialRuleHeader c where c.targetOfferTemplate.code=:offerCode and c.targetProduct.code=:productCode and c.targetAttribute is null and c.targetGroupedAttributes is null", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") })
})

public class CommercialRuleHeader extends BusinessEntity {

	public CommercialRuleHeader(CommercialRuleHeader copy) {
		this.ruleType = copy.ruleType;
		this.targetOfferTemplate = copy.targetOfferTemplate;
		this.targetProduct = copy.targetProduct;
		this.targetProductVersion = copy.targetProductVersion;
		this.targetGroupedAttributes = copy.targetGroupedAttributes;
		this.targetAttribute = copy.targetAttribute;
		this.targetAttributeValue = copy.targetAttributeValue;
		this.targetTag = copy.targetTag;
		this.ruleEl = copy.ruleEl;
		this.commercialRuleItems = new ArrayList<>();
		this.disabled = copy.disabled;
		this.code = copy.code;
		this.description = copy.description;
		this.scopeType = copy.scopeType;
	}
	
	public CommercialRuleHeader() {
		
	}

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
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
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
    @Column(name = "rule_el")
	private String ruleEl;
	
	
	@OneToMany(mappedBy = "commercialRuleHeader", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<CommercialRuleItem> commercialRuleItems = new ArrayList<>();
	
    /**
     * Is entity disabled
     */
    @Type(type = "numeric_boolean")
    @Column(name = "disabled", nullable = false)
    @NotNull
    protected boolean disabled;

	@Enumerated(EnumType.STRING)
	@Column(name = "scope_type")
	private ScopeTypeEnum scopeType;


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

	/**
	 * @return the commercialRuleItems
	 */
	public List<CommercialRuleItem> getCommercialRuleItems() {
		return commercialRuleItems;
	}

	/**
	 * @param commercialRuleItems the commercialRuleItems to set
	 */
	public void setCommercialRuleItems(List<CommercialRuleItem> commercialRuleItems) {
		this.commercialRuleItems = commercialRuleItems;
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}


	public ScopeTypeEnum getScopeType() {
		return scopeType;
	}

	public void setScopeType(ScopeTypeEnum scopeType) {
		this.scopeType = scopeType;
	}
	
	public boolean isTargetOfferAttribute() {
		return targetProduct == null;
	}

	public String getTargetOfferTemplateCode() {
		return targetOfferTemplate != null ? targetOfferTemplate.getCode() : null;
	}

	public String getTargetProductCode() {
		return targetProduct != null ? targetProduct.getCode() : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		CommercialRuleHeader that = (CommercialRuleHeader) o;
		return id.equals(that.id) && code.equals(that.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, code);
	}
}

