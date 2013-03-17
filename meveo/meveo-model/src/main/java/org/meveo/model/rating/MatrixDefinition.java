/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.rating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;

/**
 * Matrix definition entity.
 * 
 * @author Ignas Lelys
 * @created 2009.07.13
 */
@Entity
@Table(name = "RATING_MATRIX_DEFINITION")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "RATING_MATRIX_DEFINITION_SEQ")
public class MatrixDefinition extends AuditableEntity {

    private static final long serialVersionUID = 1L;
	
    @Column(name = "NAME")
    private String name;

    @Column(name = "DIMENSION")
    private Long dimension;


    @Column(name = "ENTRY_TYPE")
    @Enumerated(EnumType.STRING)
    private MatrixEntryType entryType;

    @Column(name = "START_DATE")
    private Date startDate;

    @Column(name = "END_DATE")
    private Date endDate;

    @OneToMany(mappedBy = "matrixDefinition", cascade=CascadeType.ALL)
    @MapKey(name = "coordinates")
    private Map<String, MatrixEntry> entries;

    public List<MatrixEntry> getMatrixEntries() {
        Collection<MatrixEntry> values = entries.values();
        return entries != null ? new ArrayList<MatrixEntry>(values) : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDimension() {
        return dimension;
    }

    public void setDimension(Long dimension) {
        this.dimension = dimension;
    }

    public MatrixEntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(MatrixEntryType entryType) {
        this.entryType = entryType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Map<String, MatrixEntry> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, MatrixEntry> entries) {
        this.entries = entries;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MatrixDefinition other = (MatrixDefinition) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public String toString() {
        return name;
    }

}
