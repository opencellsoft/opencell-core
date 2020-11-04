package org.meveo.model.cpq.trade;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.GroupedService;
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
@Table(name = "cpq_trade_rule_header", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_trade_rule_header_seq"), })
public class TradeRuleHeader extends BusinessEntity {

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
	@JoinColumn(name = "cpq_product_id", referencedColumnName = "id")
	private Product targetProduct;

	/**
	 * version of the product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_product_version_id", referencedColumnName = "id")
	private  ProductVersion targetProductVersion;

	
	 /** 
     * grouped service
     */


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grouped_service_id", referencedColumnName = "id")
	private GroupedService groupedService;
	
	
	/**
     * service template
     */ 
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "service_template_id", referencedColumnName = "id") 
	private ServiceTemplate serviceTemplate;
	
	/**
	 * attribute name
	 */
	@Column(name = "target_attribute_name", length = 20)
	@Size(max = 20)
	private String targetAttributeName;
	
	
	/**
	 * attribute value
	 */
	@Column(name = "target_service_value", length = 255)
	@Size(max = 255)
	private String targetServiceValue;

	/**
	 * tag target
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_tag_id", referencedColumnName = "id")
	private Tag tagTarget;

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
	 * @return the targetServiceValue
	 */
	public String getTargetServiceValue() {
		return targetServiceValue;
	}

	/**
	 * @param targetServiceValue the targetServiceValue to set
	 */
	public void setTargetServiceValue(String targetServiceValue) {
		this.targetServiceValue = targetServiceValue;
	}

	/**
	 * @return the tagTarget
	 */
	public Tag getTagTarget() {
		return tagTarget;
	}

	/**
	 * @param tagTarget the tagTarget to set
	 */
	public void setTagTarget(Tag tagTarget) {
		this.tagTarget = tagTarget;
	}

	/**
	 * @return the ruleEl
	 */
	public String getRuleEl() {
		return ruleEl;
	}

	
	/**
	 * @return the groupedService
	 */
	public GroupedService getGroupedService() {
		return groupedService;
	}

	/**
	 * @param groupedService the groupedService to set
	 */
	public void setGroupedService(GroupedService groupedService) {
		this.groupedService = groupedService;
	}

	/**
	 * @return the serviceTemplate
	 */
	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	/**
	 * @param serviceTemplate the serviceTemplate to set
	 */
	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	/**
	 * @param ruleEl the ruleEl to set
	 */
	public void setRuleEl(String ruleEl) {
		this.ruleEl = ruleEl;
	}
	
	

	/**
	 * @return the targetAttributeName
	 */
	public String getTargetAttributeName() {
		return targetAttributeName;
	}

	/**
	 * @param targetAttributeName the targetAttributeName to set
	 */
	public void setTargetAttributeName(String targetAttributeName) {
		this.targetAttributeName = targetAttributeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(ruleEl, ruleType, tagTarget, targetServiceValue,
				targetProduct, targetProductVersion);
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
		TradeRuleHeader other = (TradeRuleHeader) obj;
		return Objects.equals(ruleEl, other.ruleEl) && ruleType == other.ruleType
				&& Objects.equals(tagTarget, other.tagTarget)
				&& Objects.equals(targetServiceValue, other.targetServiceValue)
				&& Objects.equals(targetProduct, other.targetProduct)
				&& Objects.equals(targetProductVersion, other.targetProductVersion);
	}
	
	
	
	
	
	

}
