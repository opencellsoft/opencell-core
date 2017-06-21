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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.MultilanguageEntity;

@Entity
@Cacheable
@Table(name = "adm_messages", uniqueConstraints = @UniqueConstraint(columnNames = { "entity_code", "entity_class","language_code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_messages_seq"), })
public class CatMessages extends BaseEntity {

	private static final long serialVersionUID = -2933410380534805846L;

	@Column(name="entity_code",length=255,nullable=false)
    @Size(max = 255, min = 1)
    @NotNull
	private String entityCode;

	@Column(name = "language_code", length = 3,nullable=false)
	@Size(max = 3)
    @NotNull
	private String languageCode;

	@Column(name = "description", length = 255)
	@Size(max = 255)
	private String description;
	
	@Transient
	private String entityDescription;
	
	@Column(name="entity_class",length=60,nullable=false)
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

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CatMessages)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        CatMessages other = (CatMessages) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        return (StringUtils.compare(entityClass, other.getEntityClass()) == 0 && StringUtils.compare(entityCode, other.getEntityCode()) == 0
                && StringUtils.compare(languageCode, other.getLanguageCode()) == 0);
    }
}
