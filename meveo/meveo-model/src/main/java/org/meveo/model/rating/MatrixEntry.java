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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

/**
 * Matrix entry.
 * 
 * @author Ignas Lelys
 * @created 2009.08.11
 */
@Entity
@Table(name = "RATING_MATRIX_ENTRY")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "RATING_MATRIX_ENTRY_SEQ")
public class MatrixEntry extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "COORDINATES")
    private String coordinates;

    @Column(name = "VALUE")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATRIX_DEFINITION_ID")
    private MatrixDefinition matrixDefinition;

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MatrixDefinition getMatrixDefinition() {
        return matrixDefinition;
    }

    public void setMatrixDefinition(MatrixDefinition matrixDefinition) {
        this.matrixDefinition = matrixDefinition;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
        result = prime * result + ((matrixDefinition == null) ? 0 : matrixDefinition.hashCode());
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
        MatrixEntry other = (MatrixEntry) obj;
        if (coordinates == null) {
            if (other.coordinates != null)
                return false;
        } else if (!coordinates.equals(other.coordinates))
            return false;
        if (matrixDefinition == null) {
            if (other.matrixDefinition != null)
                return false;
        } else if (!matrixDefinition.equals(other.matrixDefinition))
            return false;
        return true;
    }

}
