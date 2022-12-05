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
package org.meveo.model.hierarchy;

import java.util.Set;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

/**
 * Entity hierarchy
 * 
 * @author Andrius Karpavicius
 * @param <T> Entity class
 */
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

    /**
     * Parent entity
     */
    @SuppressWarnings("rawtypes")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private HierarchyLevel parentLevel;

    /**
     * Child entities
     */
    @SuppressWarnings("rawtypes")
    @OneToMany(mappedBy = "parentLevel", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("orderLevel")
    private Set<HierarchyLevel> childLevels;

    /**
     * Hierarcy type
     */
    @Column(name = "hierarchy_type", insertable = false, updatable = false, length = 10)
    @Size(max = 10)
    private String hierarchyType;

    /**
     * Order index
     */
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

        HierarchyLevel<?> other = (HierarchyLevel<?>) o;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        boolean equalCode;
        if (code == null) {
            equalCode = other.getCode() == null;
        } else {
            equalCode = code.equals(other.getCode());
        }
        boolean equalHierarchyType;
        if (hierarchyType == null) {
            equalHierarchyType = other.getHierarchyType() == null;
        } else {
            equalHierarchyType = hierarchyType.equals(other.hierarchyType);
        }
        return equalCode && equalHierarchyType;
    }

    @Override
    public int hashCode() {
        int result = 31;
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