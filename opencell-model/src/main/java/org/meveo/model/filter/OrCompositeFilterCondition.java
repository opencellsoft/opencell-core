package org.meveo.model.filter;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@DiscriminatorValue(value = "COMPOSITE_OR")
@Table(name = "meveo_or_composite_filter_condition")
public class OrCompositeFilterCondition extends FilterCondition {

	private static final long serialVersionUID = 8683573995597386129L;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "composite_or_filter_condition_id")
	private Set<FilterCondition> filterConditions;

	public Set<FilterCondition> getFilterConditions() {
		return filterConditions;
	}

	public void setFilterConditions(Set<FilterCondition> filterConditions) {
		this.filterConditions = filterConditions;
	}

}
