package org.meveo.model.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "MEVEO_FILTER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_FILTER_SEQ")
public class Filter extends BusinessEntity {

	private static final long serialVersionUID = -6150352877726034654L;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FILTER_CONDITION_ID")
	private FilterCondition filterCondition;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ORDER_CONDITION_ID")
	private OrderCondition orderCondition;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "PRIMARY_SELECTOR_ID")
	private FilterSelector primarySelector;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "FILTER_ID")
	private List<FilterSelector> secondarySelectors = new ArrayList<FilterSelector>();

	@Column(name = "INPUT_XML", columnDefinition = "TEXT")
	private String inputXml;

	@Column(name = "SHARED")
	private Boolean shared;

	public FilterCondition getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(FilterCondition filterCondition) {
		this.filterCondition = filterCondition;
	}

	public OrderCondition getOrderCondition() {
		return orderCondition;
	}

	public void setOrderCondition(OrderCondition orderCondition) {
		this.orderCondition = orderCondition;
	}

	public FilterSelector getPrimarySelector() {
		return primarySelector;
	}

	public void setPrimarySelector(FilterSelector primarySelector) {
		this.primarySelector = primarySelector;
	}

	public List<FilterSelector> getSecondarySelectors() {
		return secondarySelectors;
	}

	public void setSecondarySelectors(List<FilterSelector> secondarySelectors) {
		this.secondarySelectors = secondarySelectors;
	}

	public String getInputXml() {
		return inputXml;
	}

	public void setInputXml(String inputXml) {
		this.inputXml = inputXml;
	}

	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

}
