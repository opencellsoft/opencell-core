package org.meveo.model.cpq;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.cpq.trade.CommercialRuleHeader;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

/**
 * 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "GroupedAttributes")
@Table(name = "cpq_grouped_attributes", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_grouped_attributes_seq"), }) 
public class GroupedAttributes extends EnableBusinessCFEntity {

	public GroupedAttributes() {
	}

	

	public GroupedAttributes(GroupedAttributes copy) {
		this.mandatory = copy.mandatory;
		this.display = copy.display;
		this.sequence = copy.sequence;
		this.code = copy.code;
		this.description = copy.description;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 

	  /**
     * Mandatory
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "mandatory")
    @NotNull
    protected Boolean mandatory;

	  /**
     * Display
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "display")
    @NotNull
    protected Boolean display;

    @Column(name = "sequence")
    private Integer sequence;

    @OneToMany(mappedBy = "targetGroupedAttributes", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<CommercialRuleHeader> commercialRules = new ArrayList<>();
    

	/**
	 * list of attributes attached to this product version
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
				name = "cpq_grouped_attributes_attribute",
				joinColumns = @JoinColumn(name = "grouped_attributes", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "attribute_id", referencedColumnName = "id")				
			)
    private List<Attribute> attributes = new ArrayList<Attribute>();

	/**
	 * @return the mandatory
	 */
	public Boolean getMandatory() {
		return mandatory;
	}


	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}


	/**
	 * @return the display
	 */
	public Boolean getDisplay() {
		return display;
	}


	/**
	 * @param display the display to set
	 */
	public void setDisplay(Boolean display) {
		this.display = display;
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
	 * @return the attributes
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}


	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	

	
	 
	




}
