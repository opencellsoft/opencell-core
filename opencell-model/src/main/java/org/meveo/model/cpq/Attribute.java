/**
 * 
 */
package org.meveo.model.cpq;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.cpq.enums.AttributeTypeEnum;

/**
 * @author Rachid.AIT-YAAZZA
 *
 */
@Entity
@Table(name = "cpq_attribute", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_attribute_seq"), })
public class Attribute extends EnableBusinessCFEntity{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5934892816847168643L;




	/**
	 * the grouped service
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "grouped_attributes_id", referencedColumnName = "id")
	private GroupedAttributes groupedAttributes;
	
	 
	
	
	
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
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "allowed_values")
	@CollectionTable(name = "cpq_attribute_allowed_values", joinColumns = @JoinColumn(name = "attribute_id", referencedColumnName = "id"))
	private Set<String> allowedValues=new HashSet<String>();
    
    

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

    public Attribute(){

	}

	public Attribute(Long id) {
		this.id = id;
	}

	/**
	 * @return the groupedAttributes
	 */
	public GroupedAttributes getGroupedAttributes() {
		return groupedAttributes;
	}

	/**
	 * @param groupedAttributes the groupedAttributes to set
	 */
	public void setGroupedAttributes(GroupedAttributes groupedAttributes) {
		this.groupedAttributes = groupedAttributes;
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
    
    

}
