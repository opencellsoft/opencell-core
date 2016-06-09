package org.meveo.model.filter;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "MEVEO_PRIMITIVE_FILTER_CONDITION")
@DiscriminatorValue(value = "PRIMITIVE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_PRIMITIVE_FILTER_CONDITION_SEQ")
public class PrimitiveFilterCondition extends FilterCondition {

	private static final long serialVersionUID = 5812098177203454113L;

	@Column(name = "FIELD_NAME", length = 60)
    @Size(max = 60)
	private String fieldName;

	@Column(name = "OPERATOR", length = 60)
    @Size(max = 60)
	private String operator;

	@Column(name = "OPERAND", length = 255)
    @Size(max = 255)
	private String operand;

	@Transient
	private String className;

	@Transient
	private String label;

	@Transient
	private String defaultValue;

	@Override
	public boolean match(BaseEntity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<BaseEntity> filter(List<BaseEntity> e) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		this.operand = operand;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
