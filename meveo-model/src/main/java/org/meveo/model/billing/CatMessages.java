/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.Auditable;
import org.meveo.model.AuditableEntity;
import org.meveo.model.IEntity;

@Entity
@Cacheable
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

    public CatMessages(IEntity businessEntity, String languageCode, String description) {
        super();

        String className = businessEntity.getClass().getSimpleName();
        // supress javassist proxy suffix
        if (className.indexOf("_") >= 0) {
            className = className.substring(0, className.indexOf("_"));
        }
        this.messageCode = className + "_" + businessEntity.getId();
        this.languageCode = languageCode;
        this.description = description;
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

    /**
     * Parse entity ID from a message code that is in a format "classname_id"
     * 
     * @return Entity identifier
     */
    public long getEntityId() {
        
        //Logger log = LoggerFactory.getLogger(this.getClass());
        try {
            return Long.parseLong(messageCode.substring(messageCode.lastIndexOf('_') + 1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
