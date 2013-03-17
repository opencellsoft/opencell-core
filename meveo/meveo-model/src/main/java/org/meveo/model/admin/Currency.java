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
package org.meveo.model.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.meveo.model.ProviderlessEntity;

/**
 * Currency entity.
 * 
 * @author Ignas Lelys
 * @created 2009.09.03
 */
@Entity
@Table(name = "ADM_CURRENCY")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_CURRENCY_SEQ")
public class Currency extends ProviderlessEntity {

    private static final long serialVersionUID = 1L;

    /** Currency code e.g. EUR for euros. */
    @Column(name = "CODE", unique = true)
    private String code;

    /** Currency ISO code e.g. 987 for euros. */
    @Column(name = "ISO_CODE")
    private String isoCode;

    /** Currency name. */
    @Column(name = "NAME")
    private String name;

    /** Flag field that indicates if it is system currency. */
    @Column(name = "SYSTEM_CURRENCY")
    private Boolean systemCurrency;

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSystemCurrency() {
        return systemCurrency;
    }

    public void setSystemCurrency(Boolean systemCurrency) {
        this.systemCurrency = systemCurrency;
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
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        Currency other = (Currency) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

    public String toString() {
        return name;
    }
}
