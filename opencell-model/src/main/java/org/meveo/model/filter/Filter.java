/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

/**
 * @author anasseh
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 10.0
 */
@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code" })
@CustomFieldEntity(cftCodePrefix = "Filter", cftCodeFields = "code", isManuallyManaged = false)
@Table(name = "meveo_filter")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "meveo_filter_seq"), })
public class Filter extends EnableBusinessCFEntity {

    private static final long serialVersionUID = -6150352877726034654L;
    private static final String FILTER_CODE_PREFIX = "FILTER_";
   
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "filter_condition_id")
    private FilterCondition filterCondition;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_condition_id")
    private OrderCondition orderCondition;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "primary_selector_id")
    private FilterSelector primarySelector;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "filter_id")
    private List<FilterSelector> secondarySelectors = new ArrayList<FilterSelector>();

    @Column(name = "input_xml", columnDefinition = "TEXT")
    private String inputXml;

    @Type(type = "numeric_boolean")
    @Column(name = "shared")
    private Boolean shared = true;
    
    @Column(name = "polling_query", columnDefinition = "TEXT")
    private String pollingQuery;

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

	public String getPollingQuery() {
		return pollingQuery;
	}

	public void setPollingQuery(String pollingQuery) {
		this.pollingQuery = pollingQuery;
	}
    
}
