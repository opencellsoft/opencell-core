/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.hierarchy;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@ExportIdentifier({ "code", "hierarchyType" })
@Table(name = "hierarchy_entity", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "hierarchy_type" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "hierarchy_entity_seq"), })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "hierarchy_type")
public abstract class HierarchyLevel<T> extends BusinessEntity implements Comparable<HierarchyLevel<T>> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("rawtypes")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private HierarchyLevel parentLevel;

    @SuppressWarnings("rawtypes")
    @OneToMany(mappedBy = "parentLevel", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("orderLevel")
    private Set<HierarchyLevel> childLevels;

    @Column(name = "hierarchy_type", insertable = false, updatable = false, length = 10)
    @Size(max = 10)
    private String hierarchyType;

    @Column(name = "order_level")
    protected Long orderLevel = 0L;

    @SuppressWarnings("rawtypes")
    public HierarchyLevel getParentLevel() {
        return parentLevel;
    }

    @SuppressWarnings("rawtypes")
    public void setParentLevel(HierarchyLevel parentLevel) {
        this.parentLevel = parentLevel;
    }

    @SuppressWarnings("rawtypes")
    public Set<HierarchyLevel> getChildLevels() {
        return childLevels;
    }

    @SuppressWarnings("rawtypes")
    public void setChildLevels(Set<HierarchyLevel> childLevels) {
        this.childLevels = childLevels;
    }

    public String getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(String hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public Long getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(Long orderLevel) {
        this.orderLevel = orderLevel;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int compareTo(HierarchyLevel hierarchyLevel) {
        return Long.compare(this.orderLevel, hierarchyLevel.orderLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        HierarchyLevel<?> that = (HierarchyLevel<?>) o;
        boolean equalCode;
        if (code == null) {
            equalCode = that.getCode() == null;
        } else {
            equalCode = code.equals(that.getCode());
        }
        boolean equalHierarchyType;
        if (hierarchyType == null) {
            equalHierarchyType = that.getHierarchyType() == null;
        } else {
            equalHierarchyType = hierarchyType.equals(that.hierarchyType);
        }
        return equalCode && equalHierarchyType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        if (hierarchyType != null) {
            result = 31 * result + hierarchyType.hashCode();
        }
        result = 31 * result + code.hashCode();
        return result;
    }

    @Override
    public BusinessEntity getParentEntity() {
        return parentLevel;
    }
}