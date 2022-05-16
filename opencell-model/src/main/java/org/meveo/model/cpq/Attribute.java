/**
 * 
 */
package org.meveo.model.cpq;

import static javax.persistence.EnumType.STRING;

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
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
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
	@NamedQuery(name = "Attribute.updateParentAttribute", query = "update Attribute set parentAttribute=null where parentAttribute.id=:id"),
	@NamedQuery(name = "Attribute.findByCodes", query = "SELECT a FROM Attribute a WHERE LOWER(a.code) IN :codes", hints = {@QueryHint(name = "org.hibernate.cacheable", value = "TRUE")})
})
public class Attribute extends EnableBusinessCFEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5934892816847168643L;

	  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Attribute parentAttribute;
	 
	
	  /**
     * Mandatory
     */
    @Type(type = "numeric_boolean")
    @Column(name = "mandatory")
    @NotNull
    protected boolean mandatory=Boolean.FALSE;
    
    
    /**
     * The lower number, the higher the priority is
     */
    @Column(name = "priority", columnDefinition = "int DEFAULT 0")
    private Integer priority = 0;
     
    
    /**
	 * allowed values
	 */
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "allowed_values")
	@CollectionTable(name = "cpq_attribute_allowed_values", joinColumns = @JoinColumn(name = "attribute_id", referencedColumnName = "id"))
	private Set<String> allowedValues=new HashSet<>();
	
	
	   /**
     * list of tag attached
     */
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_attribute_charge", joinColumns = @JoinColumn(name = "attribute_id"), inverseJoinColumns = @JoinColumn(name = "charge_id"))
    private Set<ChargeTemplate> chargeTemplates = new HashSet<>();
    
    /**
     * attribute order in the GUI
     */
    @Column(name = "sequence")
    protected Integer sequence;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "attribute_type")
    protected AttributeTypeEnum attributeType;

	  /**
     * Display
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display")
    @NotNull
    protected boolean display;
    
    
    /**
     * list of Media
     */
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_attribute_media", joinColumns = @JoinColumn(name = "attribute_id"), inverseJoinColumns = @JoinColumn(name = "media_id"))
    private List<Media> medias = new ArrayList<>();


	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "cpq_attribute_tag", joinColumns = @JoinColumn(name = "attribute_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "targetAttribute", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<CommercialRuleHeader> commercialRules = new ArrayList<>();

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "parentAttribute", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("id")
    private List<Attribute> assignedAttributes = new ArrayList<>();
    
    @Column(name = "unit_nb_decimal")
    protected int unitNbDecimal = BaseEntity.NB_DECIMALS;


	@Type(type = "numeric_boolean")
    @Column(name = "read_only")
    protected Boolean readOnly = Boolean.FALSE;
	
	@Column(name = "default_value")
	private String defaultValue;

	@ManyToMany(mappedBy = "attributes")
	private List<GroupedAttributes> groupedAttributes;

	@Column(name = "validation_type", length = 10)
	@Enumerated(STRING)
	private AttributeValidationType validationType;

	@Column(name = "validation_pattern", length = 2000)
	private String validationPattern;

	@Column(name = "validation_label")
	private String validationLabel;

    public Attribute(){
	}

	public Attribute(Long id) {
		this.id = id;
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
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
	public Set<String> getAllowedValues() {
		return allowedValues;
	}

	/**
	 * @param allowedValues the allowedValues to set
	 */
	public void setAllowedValues(Set<String> allowedValues) {
		this.allowedValues = allowedValues;
	}

	/**
	 * @return the display
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay(boolean display) {
		this.display = display;
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

	/**
	 * @return the assignedAttributes
	 */
	public List<Attribute> getAssignedAttributes() {
		return assignedAttributes;
	}

	/**
	 * @param assignedAttributes the assignedAttributes to set
	 */
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
	public int getUnitNbDecimal() {
		return unitNbDecimal;
	}

	/**
	 * @param unitNbDecimal the unitNbDecimal to set
	 */
	public void setUnitNbDecimal(int unitNbDecimal) {
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

	/**
	 * @return the readOnly
	 */
	public Boolean getReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public List<GroupedAttributes> getGroupedAttributes() {
		return groupedAttributes;
	}

	public void setGroupedAttributes(List<GroupedAttributes> groupedAttributes) {
		this.groupedAttributes = groupedAttributes;
	}

	public AttributeValidationType getValidationType() {
		return validationType;
	}

	public void setValidationType(AttributeValidationType validationType) {
		this.validationType = validationType;
	}

	public String getValidationPattern() {
		return validationPattern;
	}

	public void setValidationPattern(String validationPattern) {
		this.validationPattern = validationPattern;
	}

	public String getValidationLabel() {
		return validationLabel;
	}

	public void setValidationLabel(String validationLabel) {
		this.validationLabel = validationLabel;
	}
}
