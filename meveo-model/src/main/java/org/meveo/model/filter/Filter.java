package org.meveo.model.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ModuleItem
@ExportIdentifier({ "code"})
@CustomFieldEntity(cftCodePrefix = "FILTER", cftCodeFields = "code", isManuallyManaged = false)
@Table(name = "MEVEO_FILTER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_FILTER_SEQ")
public class Filter extends BusinessCFEntity {

	private static final long serialVersionUID = -6150352877726034654L;
	private static final String FILTER_CODE_PREFIX = "FILTER_";

	@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name = "FILTER_CONDITION_ID")
	private FilterCondition filterCondition;

	@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "ORDER_CONDITION_ID")
	private OrderCondition orderCondition;

	@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "PRIMARY_SELECTOR_ID")
	private FilterSelector primarySelector;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name = "FILTER_ID")
	private List<FilterSelector> secondarySelectors=new ArrayList<FilterSelector>();

	@Column(name = "INPUT_XML", columnDefinition = "TEXT")
	private String inputXml;

	@Type(type="numeric_boolean")
    @Column(name = "SHARED")
	private Boolean shared = false;

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

	public String getAppliesTo() {
		return FILTER_CODE_PREFIX + getCode();
	}
}
