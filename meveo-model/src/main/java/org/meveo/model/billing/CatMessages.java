/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.AuditableEntity;
import org.meveo.model.IEntity;
import org.meveo.model.MultilanguageEntity;

@Entity
@Cacheable
@Table(name = "ADM_MESSAGES")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_MESSAGES_SEQ")
public class CatMessages extends AuditableEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "CODE", length = 50)
	@Size(max = 50)
	private String messageCode;

	@Column(name = "LANGUAGE_CODE", length = 3)
	@Size(max = 3)
	private String languageCode;

	@Column(name = "DESCRIPTION", length = 100)
	@Size(max = 100)
	private String description;
	
	@Transient
	private String entityCode;
	
	@Transient
	private String entityDescription;
	
	@Transient
	private String className;
	
	@Transient
	private String group;
	
	@Transient
	private String key;

	public CatMessages() {
		super();
	}

	public CatMessages(Auditable auditable) {
		super(auditable);
	}

    public CatMessages(IEntity businessEntity, String languageCode, String description) {
        super();

        String className =ReflectionUtils.getCleanClassName(businessEntity.getClass().getSimpleName());
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
    
    public String getEntityClass() {
    	if(StringUtils.isBlank(className)){
    		if (messageCode == null){
    			return "";
    		}	
    		if (messageCode.indexOf("_") >= 0) {
    			className = messageCode.substring(0, messageCode.indexOf("_"));
    		}
    	}
		return className;
    }
    
	public String getObjectType() {
		if(StringUtils.isBlank(key)){
			Class<?> entityClass = ReflectionUtils.getClassBySimpleNameAndAnnotation(getEntityClass(), MultilanguageEntity.class);
			if(entityClass != null){
				MultilanguageEntity annotation = entityClass.getAnnotation(MultilanguageEntity.class);
				key = annotation.key();
			}
		}
		return key;
	}
	
	public String getGroup() {
		if(StringUtils.isBlank(group)){
			Class<?> entityClass = ReflectionUtils.getClassBySimpleNameAndAnnotation(getEntityClass(), MultilanguageEntity.class);
			if(entityClass != null){
				MultilanguageEntity annotation = entityClass.getAnnotation(MultilanguageEntity.class);
				group = annotation.group();
			}
		}
		return group;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityDescription() {
		return entityDescription;
	}

	public void setEntityDescription(String entityDescription) {
		this.entityDescription = entityDescription;
	}
    
    
}
