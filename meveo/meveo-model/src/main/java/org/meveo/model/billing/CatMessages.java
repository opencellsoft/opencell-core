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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;
import org.meveo.model.BusinessEntity;

/**
 * CAT_MESSAGES entity
 * 
 * @author Mbarek
 * @created 2013.03.11
 */

@Entity
@Table(name = "CAT_MESSAGES")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_MESSAGES_SEQ")

public class CatMessages  extends AuditableEntity{
	private static final long serialVersionUID = 1L;
	
	
	@Column(name = "ENTITY_ID")
	private BusinessEntity entityId;
	
	@Column(name = "LANGUAGE_CODE", length = 3)
	private String languageCode;
	
	@Column(name = "DESCRIPTION", length = 100)
	private String description;
	

	public BusinessEntity getEntityId() {
		return entityId;
	}

	public void setEntityId(BusinessEntity entityId) {
		this.entityId = entityId;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	 

	 
}
