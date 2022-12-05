package org.meveo.model.cpq.tags;

import java.util.Objects;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.enums.OperatorEnum;
import org.meveo.model.cpq.enums.OperatorLogicEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * 
 * @author Tarik F.
 * @author Mbarek-Ay
 * @version 10.0
 *
 */

@Entity
@Table(name = "cpq_tag_filter")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_tag_filter_seq"), })
public class TagFilter extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;
	
	@Column(name = "operator", nullable = false)
	@Valid
	@NotNull
	@Enumerated(EnumType.STRING)
	private OperatorEnum operator;
	
	@Column(name = "entity", length = 50, nullable = false)
	@Size(max = 50)
	@NotNull
	private String entity;
	
	@Column(name = "field", nullable = false, length = 50)
	@NotNull
	@Valid
	@Size(max = 50)
	private String field;

	@Column(name = "comparaison", nullable = false)
	@Valid
	@NotNull
	@Enumerated(EnumType.STRING)
	private OperatorLogicEnum comparaison;
	
	@Column(name = "value", length = 50)
	@Size(max = 50)
	private String value;
	
	

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	/**
	 * @return the operator
	 */
	public OperatorEnum getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(OperatorEnum operator) {
		this.operator = operator;
	}

	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * @return the comparison
	 */
	public OperatorLogicEnum getComparaison() {
		return comparaison;
	}

	/**
	 * @param comparaison the comparison to set
	 */
	public void setComparaison(OperatorLogicEnum comparaison) {
		this.comparaison = comparaison;
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
		result = prime * result + Objects.hash(comparaison, entity, field, operator, value);
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
		TagFilter other = (TagFilter) obj;
		return comparaison == other.comparaison && Objects.equals(entity, other.entity)
				&& Objects.equals(field, other.field) && operator == other.operator
				&& Objects.equals(value, other.value);
	}




	
}
