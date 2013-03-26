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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_CURRENCY_SEQ")
public class Currency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    @Column(name = "ID")
    private Long id;
    
    
    /** Currency code e.g. EUR for euros. */
    @Column(name = "CURRENCY_CODE", length = 3, unique = true)
    private String currencyCode;

    /** Currency ISO code e.g. 987 for euros. */
    @Column(name = "ISO_CODE")
    private String isoCode;

    /** Currency name. */
    @Column(name = "DESCRIPTION_EN")
    private String DescriotionEn;

    /** Flag field that indicates if it is system currency. */
    @Column(name = "SYSTEM_CURRENCY")
    private Boolean systemCurrency;

    
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

   
   

    public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getDescriotionEn() {
		return DescriotionEn;
	}

	public void setDescriotionEn(String descriotionEn) {
		DescriotionEn = descriotionEn;
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
        result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
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
        if (currencyCode == null) {
            if (other.currencyCode != null)
                return false;
        } else if (!currencyCode.equals(other.currencyCode))
            return false;
        return true;
    }

    public String toString() {
        return DescriotionEn;
    }
}
