package org.meveo.model.filter;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.validation.constraint.ClassName;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "MEVEO_FILTER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_FILTER_SEQ")
public class Filter extends BusinessEntity {

	private static final long serialVersionUID = -6150352877726034654L;

	@ClassName
	@Size(max = 255)
	@NotNull
	@Column(name = "TARGET_ENTITY", length = 255, nullable = false)
	private String targetEntity;

	@OneToMany(mappedBy = "filter", fetch = FetchType.LAZY)
	@MapKey(name = "operand")
	private Map<String, FilterCondition> filterConditions = new HashMap<String, FilterCondition>();

	public String getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(String targetEntity) {
		this.targetEntity = targetEntity;
	}

	public Map<String, FilterCondition> getFilterConditions() {
		return filterConditions;
	}

	public void setFilterConditions(Map<String, FilterCondition> filterConditions) {
		this.filterConditions = filterConditions;
	}

}
