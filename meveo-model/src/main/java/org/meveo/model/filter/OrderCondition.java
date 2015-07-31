package org.meveo.model.filter;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "MEVEO_FILTER_ORDER_CONDITION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_FILTER_ORDER_CONDITION_SEQ")
public class OrderCondition extends BaseEntity {

	private static final long serialVersionUID = 1523437333405252113L;
	
	@ElementCollection
	@CollectionTable(name = "MEVEO_FILTER_OC_FIELD_NAMES", joinColumns = @JoinColumn(name = "ORDER_CONDITION_ID"))
	@Column(name = "FIELD_NAME")
	private List<String> fieldNames;

	@Column(name = "ASCENDING")
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

}
