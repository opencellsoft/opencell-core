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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseProviderlessEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.MultilanguageEntity;

@Entity
@Cacheable
@Table(name = "ADM_MESSAGES", uniqueConstraints = @UniqueConstraint(columnNames = { "ENTITY_CODE", "ENTITY_CLASS","LANGUAGE_CODE" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_MESSAGES_SEQ")
public class CatMessages extends BaseProviderlessEntity {

	private static final long serialVersionUID = -2933410380534805846L;

	@Column(name="ENTITY_CODE",length=60)
	private String entityCode;

	@Column(name = "LANGUAGE_CODE", length = 3)
	@Size(max = 3)
	private String languageCode;

	@Column(name = "DESCRIPTION", length = 100)
	@Size(max = 100)
	private String description;
	
	@Transient
	private String entityDescription;
	
	@Column(name="ENTITY_CLASS",length=100)
	private String entityClass;
	
	@Transient
	private String group;
	
	@Transient
	private String key;

	public CatMessages() {
		super();
	}
	public CatMessages(String entityClass,String entityCode,String languageCode,String description){
		this.entityClass=entityClass;
		this.entityCode=entityCode;
		this.languageCode=languageCode;
		this.description=description;
	}

    public CatMessages(BusinessEntity businessEntity, String languageCode, String description) {
        super();

        this.entityClass =ReflectionUtils.getCleanClassName(businessEntity.getClass().getSimpleName());
        this.entityCode = businessEntity.getCode();
        this.languageCode = languageCode;
        this.description = description;
    }
	   
	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String messageCode) {
		this.entityCode = messageCode;
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

    public String getEntityClass() {
		return entityClass;
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

	public String getEntityDescription() {
		return entityDescription;
	}

	public void setEntityDescription(String entityDescription) {
		this.entityDescription = entityDescription;
	}
	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}
    
}
