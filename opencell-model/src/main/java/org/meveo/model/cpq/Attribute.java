/**
 * 
 */
package org.meveo.model.cpq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.trade.CommercialRuleHeader;

/**
 * @author Rachid.AIT-YAAZZA
 *
 */
@Entity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "Attribute")
@Table(name = "cpq_attribute", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_attribute_seq"), })
@NamedQueries({
	@NamedQuery(name = "Attribute.updateParentAttribute", query = "update Attribute set parentAttribute=null where parentAttribute.id=:id")})
public class Attribute extends EnableBusinessCFEntity{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5934892816847168643L;

	  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Attribute parentAttribute;
	 
    /**
     * expression language used in 'business attributes' to define attribute value.
     */
    @Column(name = "el_value", length = 255)
    private String elValue;
    
    /**
     * The lower number, the higher the priority is
     */
    @Column(name = "priority")
    private Integer priority = 0;
     
    
    /**
	 * allowed values
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@Column(name = "allowed_values")
	@CollectionTable(name = "cpq_attribute_allowed_values", joinColumns = @JoinColumn(name = "attribute_id", referencedColumnName = "id"))
	@OrderColumn(name = "order_idx")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<String> allowedValues=new ArrayList<>();
	
	
	   /**
     * list of tag attached
     */   
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_attribute_charge", joinColumns = @JoinColumn(name = "attribute_id"), inverseJoinColumns = @JoinColumn(name = "charge_id"))
    private Set<ChargeTemplate> chargeTemplates = new HashSet<>();
    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "attribute_type")
    protected AttributeTypeEnum attributeType;

    
    
    /**
     * list of Media
     */   
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_attribute_media", joinColumns = @JoinColumn(name = "attribute_id"), inverseJoinColumns = @JoinColumn(name = "media_id"))
    private List<Media> medias = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "cpq_attribute_tag", joinColumns = @JoinColumn(name = "attribute_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Tag> tags = new ArrayList<>();
    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "targetAttribute", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<CommercialRuleHeader> commercialRules = new ArrayList<>();
    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "cpq_assigned_attributes", joinColumns = @JoinColumn(name = "attribute_id"), inverseJoinColumns = @JoinColumn(name = "assigned_attribute_id"))
    private List<Attribute> assignedAttributes = new ArrayList<>();
    
    @Column(name = "unit_nb_decimal")
    protected Integer unitNbDecimal = BaseEntity.NB_DECIMALS;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToMany(mappedBy = "attributes", fetch = FetchType.LAZY)
	private List<GroupedAttributes> groupedAttributes;
	

    @OneToMany(mappedBy = "attribute", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("id")
    private List<ProductVersionAttribute> productVersionAttributes = new ArrayList<>();
    

    @OneToMany(mappedBy = "attribute", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("id")
    private List<OfferTemplateAttribute> offerTemplateAttribute = new ArrayList<>();
    
	@Enumerated(EnumType.STRING)
	@Column(name = "attribute_category")
    private AttributeCategoryEnum attributeCategory = AttributeCategoryEnum.REGULAR;

    public Attribute(){
	}

	public Attribute(Long id) {
		this.id = id;
	}

	/**
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	/**
	 * @return the allowedValues
	 */
	public List<String> getAllowedValues() {
		return allowedValues;
	}

	/**
	 * @param allowedValues the allowedValues to list
	 */
	public void setAllowedValues(List<String> allowedValues) {
		this.allowedValues = allowedValues;
	}

	/**
	 * @return the serviceType
	 */
	public AttributeTypeEnum getAttributeType() {
		return attributeType;
	}

	/**
	 * @param attributeType the serviceType to set
	 */
	public void setAttributeType(AttributeTypeEnum attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return the chargeTemplates
	 */
	public Set<ChargeTemplate> getChargeTemplates() {
		return chargeTemplates;
	}

	/**
	 * @param chargeTemplates the chargeTemplates to set
	 */
	public void setChargeTemplates(Set<ChargeTemplate> chargeTemplates) {
		this.chargeTemplates = chargeTemplates;
	}
 

	/**
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the commercialRules
	 */
	public List<CommercialRuleHeader> getCommercialRules() {
		return commercialRules;
	}

	/**
	 * @param commercialRules the commercialRules to set
	 */
	public void setCommercialRules(List<CommercialRuleHeader> commercialRules) {
		this.commercialRules = commercialRules;
	}

	

	public List<Attribute> getAssignedAttributes() {
		return assignedAttributes;
	}

	public void setAssignedAttributes(List<Attribute> assignedAttributes) {
		this.assignedAttributes = assignedAttributes;
	}

	/**
	 * @return the parentAttribute
	 */
	public Attribute getParentAttribute() {
		return parentAttribute;
	}

	/**
	 * @param parentAttribute the parentAttribute to set
	 */
	public void setParentAttribute(Attribute parentAttribute) {
		this.parentAttribute = parentAttribute;
	}

	/**
	 * @return the unitNbDecimal
	 */
	public Integer getUnitNbDecimal() {
		return unitNbDecimal;
	}

	/**
	 * @param unitNbDecimal the unitNbDecimal to set
	 */
	public void setUnitNbDecimal(Integer unitNbDecimal) {
		this.unitNbDecimal = unitNbDecimal;
	}

	/**
	 * @return the medias
	 */
	public List<Media> getMedias() {
		return medias;
	}

	/**
	 * @param medias the medias to set
	 */
	public void setMedias(List<Media> medias) {
		this.medias = medias;
	}


	public List<GroupedAttributes> getGroupedAttributes() {
		return groupedAttributes;
	}

	public void setGroupedAttributes(List<GroupedAttributes> groupedAttributes) {
		this.groupedAttributes = groupedAttributes;
	}

	/**
	 * @return the productVersionAttributes
	 */
	public List<ProductVersionAttribute> getProductVersionAttributes() {
		return productVersionAttributes;
	}

	/**
	 * @param productVersionAttributes the productVersionAttributes to set
	 */
	public void setProductVersionAttributes(List<ProductVersionAttribute> productVersionAttributes) {
		this.productVersionAttributes = productVersionAttributes;
	}

	/**
	 * @return the offerTemplateAttribute
	 */
	public List<OfferTemplateAttribute> getOfferTemplateAttribute() {
		return offerTemplateAttribute;
	}

	/**
	 * @param offerTemplateAttribute the offerTemplateAttribute to set
	 */
	public void setOfferTemplateAttribute(List<OfferTemplateAttribute> offerTemplateAttribute) {
		this.offerTemplateAttribute = offerTemplateAttribute;
	}

	/**
	 * @return the attributeCategory
	 */
	public AttributeCategoryEnum getAttributeCategory() {
		return attributeCategory;
	}

	/**
	 * @param attributeCategory the attributeCategory to set
	 */
	public void setAttributeCategory(AttributeCategoryEnum attributeCategory) {
		this.attributeCategory = attributeCategory;
	}

	/**
	 * @return the elValue
	 */
	public String getElValue() {
		return elValue;
	}

	/**
	 * @param elValue the elValue to set
	 */
	public void setElValue(String elValue) {
		this.elValue = elValue;
	}
	
	
}
