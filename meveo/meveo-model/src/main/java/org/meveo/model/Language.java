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

package org.meveo.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.jboss.seam.annotations.AutoCreate;

/**
 * Language entity.
 * 
 * @author Marouane ALAMI
 * @created 2013.03.07
 */

@Entity
@Table(name = "LANGUAGE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "NEWER_LANGUAGE_SEQ")

public class Language {
	
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


	
}
