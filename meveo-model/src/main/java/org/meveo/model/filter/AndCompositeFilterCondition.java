package org.meveo.model.filter;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@DiscriminatorValue(value = "COMPOSITE_AND")
@Table(name = "MEVEO_AND_COMPOSITE_FILTER_CONDITION")
public class AndCompositeFilterCondition extends FilterCondition {

	private static final long serialVersionUID = 8683573995597386129L;

	@OneToMany(orphanRemoval = true)
	@JoinColumn(name = "COMPOSITE_AND_FILTER_CONDITION_ID")
	private List<FilterCondition> filterConditions;

	public List<FilterCondition> getFilterConditions() {
		return filterConditions;
	}

	public void setFilterConditions(List<FilterCondition> filterConditions) {
		this.filterConditions = filterConditions;
	}

}
