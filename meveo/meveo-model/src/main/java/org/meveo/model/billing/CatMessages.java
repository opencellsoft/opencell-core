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

import org.meveo.model.Auditable;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "ADM_MESSAGES")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_MESSAGES_SEQ")
public class CatMessages extends AuditableEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "CODE", length = 50)
	private String messageCode;

	@Column(name = "LANGUAGE_CODE", length = 3)
	private String languageCode;

	@Column(name = "DESCRIPTION", length = 100)
	private String description;

	public CatMessages() {
		super();
	}

	public CatMessages(Auditable auditable) {
		super(auditable);
	}

	public CatMessages(String messageCode, String languageCode, String description) {
		super();
		this.messageCode = messageCode;
		this.languageCode = languageCode;
		this.description = description;
	}

	public CatMessages(String messageCode, String languageCode) {
		super();
		this.messageCode = messageCode;
		this.languageCode = languageCode;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
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
