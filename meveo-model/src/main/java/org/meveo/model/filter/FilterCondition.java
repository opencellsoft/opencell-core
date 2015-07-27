package org.meveo.model.filter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "MEVEO_FILTER_CONDITION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_FILTER_CONDITION_SEQ")
public class FilterCondition extends BaseEntity {

	private static final long serialVersionUID = -4620739918936998431L;

	@Column(name = "FIELD_NAME", length = 60, nullable = false)
	private String fieldName;

	@Column(name = "OPERAND", length = 60, nullable = false)
	private String operand;

	@Column(name = "EL", length = 2000)
	private String el;

	@Column(name = "SQL", length = 2000)
	private String sql;

	@ManyToOne
	@JoinColumn(name = "FILTER_ID")
	private Filter filter;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		this.operand = operand;
	}

	public String getEl() {
		return el;
	}

	public void setEl(String el) {
		this.el = el;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

}
