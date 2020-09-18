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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "meveo_filter_condition")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "meveo_filter_condition_seq"), })
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "filter_condition_type")
public class FilterCondition extends BaseEntity {

    private static final long serialVersionUID = -4620739918936998431L;

    @OneToOne(mappedBy = "filterCondition")
    private Filter filter;

    @Column(name = "filter_condition_type", length = 50, nullable = false)
    @Size(max = 50)
    @NotNull
    private String filterConditionType;

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