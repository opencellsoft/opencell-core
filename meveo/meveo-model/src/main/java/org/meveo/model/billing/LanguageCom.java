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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;

/**
 * LanguageCom entity.
 * 
 * @author Marouane ALAMI
 * @created 2013.03.07
 */

@Entity
@Table(name = "BILLING_LANGUAGE_COM")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_LANGUAGE_COM_SEQ")

public class LanguageCom  extends AuditableEntity{
	private static final long serialVersionUID = 1L;
	

	
	
	
	@Column(name = "LANGUAGE_CODE", length = 3)
	private String languageCode;
	
	
	@Column(name = "PR_DESCRIPTION", length = 100)
	private String prDescription;
	
	
	


	public String getLanguageCode() {
		return languageCode;
	}


	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getPrDescription() {
		return prDescription;
	}


	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}
	
}
