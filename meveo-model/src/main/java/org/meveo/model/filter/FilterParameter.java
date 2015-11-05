package org.meveo.model.filter;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "MEVEO_FILTER_PARAMETER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_FILTER_PARAMETER_SEQ")
public class FilterParameter extends BusinessEntity {

	private static final long serialVersionUID = 1437367789928542468L;

	@ManyToOne
	@JoinColumn(name = "FILTER_ID", nullable = false)
	public Filter filter;

	@Column(name = "DEFAULT_VALUE", length = 50)
	private String defaultValue;

	@Column(name = "VALUE_REQUIRED")
	private boolean valueRequired;

	@Column(name = "FIELD_TYPE")
	@Enumerated(EnumType.STRING)
	private FilterParameterTypeEnum fieldType;

	@Column(name = "ENTITY_CLAZZ", length = 255)
	private String entityClazz;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "MEVEO_FILTER_PARAMETER_LIST_VAL")
	private Map<String, String> listValues = new HashMap<String, String>();

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public FilterParameterTypeEnum getFieldType() {
		return fieldType;
	}

	public void setFieldType(FilterParameterTypeEnum fieldType) {
		this.fieldType = fieldType;
	}

	public boolean isValueRequired() {
		return valueRequired;
	}

	public void setValueRequired(boolean valueRequired) {
		this.valueRequired = valueRequired;
	}

}
