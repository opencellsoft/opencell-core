package org.meveo.model.filter;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "MEVEO_FILTER_CONDITION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_FILTER_CONDITION_SEQ")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE")
public class FilterCondition extends BaseEntity {

	private static final long serialVersionUID = -4620739918936998431L;

	@OneToOne(mappedBy = "filterCondition")
	public Filter filter;

	@Column(name = "FILTER_CONDITION_TYPE", length = 50, nullable = false)
	@Size(max = 50)
	@NotNull
	public String filterConditionType;

	public boolean match(BaseEntity e) {
		return false;
	}

	public List<BaseEntity> filter(List<BaseEntity> e) {
		return null;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public String getFilterConditionType() {
		return filterConditionType;
	}

	public void setFilterConditionType(String filterConditionType) {
		this.filterConditionType = filterConditionType;
	}

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof FilterCondition)) {
            return false;
        }

        FilterCondition other = (FilterCondition) obj;
        return (other.getId() != null) && other.getId().equals(this.getId());
    }
}