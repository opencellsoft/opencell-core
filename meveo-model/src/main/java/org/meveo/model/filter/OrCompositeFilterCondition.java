package org.meveo.model.filter;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@DiscriminatorValue(value = "COMPOSITE_OR")
@Table(name = "MEVEO_OR_COMPOSITE_FILTER_CONDITION")
public class OrCompositeFilterCondition extends FilterCondition {

	private static final long serialVersionUID = 8683573995597386129L;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "COMPOSITE_OR_FILTER_CONDITION_ID")
	private Set<FilterCondition> filterConditions;

	public Set<FilterCondition> getFilterConditions() {
		return filterConditions;
	}

	public void setFilterConditions(Set<FilterCondition> filterConditions) {
		this.filterConditions = filterConditions;
	}

}
