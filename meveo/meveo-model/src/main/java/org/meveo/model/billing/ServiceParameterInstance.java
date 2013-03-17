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
package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;

/**
 * @author Ignas Lelys
 * @created Oct 31, 2010
 * 
 */
@Entity
@Table(name = "BILLING_SERV_PARAM_INST")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_SERV_PARAM_INST_SEQ")
public class ServiceParameterInstance extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CUMULATIVE_PERIODS")
    private Integer cumulativePeriods;

    public Integer getCumulativePeriods() {
        return cumulativePeriods;
    }

    public void setCumulativePeriods(Integer cumulativePeriods) {
        this.cumulativePeriods = cumulativePeriods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        ServiceParameterInstance other = (ServiceParameterInstance) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

}
