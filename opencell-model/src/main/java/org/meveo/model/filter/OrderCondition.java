package org.meveo.model.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "meveo_filter_order_condition")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "meveo_filter_order_condition_seq"), })
public class OrderCondition extends BaseEntity {

	private static final long serialVersionUID = 1523437333405252113L;

	@ElementCollection
	@CollectionTable(name = "meveo_filter_oc_field_names", joinColumns = @JoinColumn(name = "order_condition_id"))
	@Column(name = "field_name")
	private List<String> fieldNames = new ArrayList<String>();

	@Type(type="numeric_boolean")
    @Column(name = "ascending")
	private boolean ascending;

	@OneToOne(mappedBy = "orderCondition")
	public Filter filter;

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
	@Override
	public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof OrderCondition)) {
            return false;
        }
        
		OrderCondition other = (OrderCondition) obj;
		return (other.getId() != null) && other.getId().equals(this.getId());
	}
}