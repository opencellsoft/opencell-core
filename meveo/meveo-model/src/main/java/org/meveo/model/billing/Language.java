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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ProviderlessEntity;

/**
 * Language entity.
 * 
 * @author Marouane ALAMI
 * @created 2013.03.07
 */

@Entity
@Table(name = "ADM_LANGUAGE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_LANGUAGE_SEQ")

public class Language  extends ProviderlessEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "LANGUAGE_CODE", length = 3)
	private String languageCode;
	
	
	@Column(name = "DESCRIPTION_EN", length = 100)
	private String descriptionEn;



	public String getLanguageCode() {
		return languageCode;
	}


	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}


	public String getDescriptionEn() {
		return descriptionEn;
	}


	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Language other = (Language) obj;
        if (languageCode == null) {
            if (other.languageCode != null)
                return false;
        } else if (!languageCode.equals(other.languageCode))
            return false;
        return true;
    }
	
}
