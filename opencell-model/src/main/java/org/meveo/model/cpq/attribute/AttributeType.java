package org.meveo.model.cpq.attribute;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * 
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay
 * @version 10.0
 *
 */
@Entity
@Table(name = "cpq_attribute_type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_attribute_type_seq"), })
public class AttributeType extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1596603828388934360L;
	
	@Column(name = "attribute_type", length = 20, nullable = false)
	@NotNull
	private String attributeType;
	
	@Column(name = "label", length = 255, nullable = false)
	@NotNull
	private String label;

	/**
	 * @return the attributeType
	 */
	public String getAttributeType() {
		return attributeType;
	}

	/**
	 * @param attributeType the attributeType to set
	 */
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(attributeType, label);
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
		AttributeType other = (AttributeType) obj;
		return Objects.equals(attributeType, other.attributeType) && Objects.equals(label, other.label);
	}
	
	
	
	

}
