package org.meveo.model.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;

/**
 * Entity that accessible entities for a user.
 */
@Embeddable
public class SecuredEntity implements Serializable {

	private static final long serialVersionUID = 84222776645282176L;

	public SecuredEntity() {
	}

	public SecuredEntity(BusinessEntity businessEntity) {
		this.setCode(businessEntity.getCode());
		this.setDescription(businessEntity.getDescription());
		this.setEntityClass(ReflectionUtils.getCleanClassName(businessEntity.getClass().getName()));
	}

	@Column(name = "CODE", nullable = false, length = 60)
	@Size(max = 60, min = 1)
	@NotNull
	protected String code;

	@Column(name = "ENTITY_CLASS", length = 255)
	@Size(max = 255)
	private String entityClass;

	@Transient
	protected String description;

	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}
	
	public String readableEntityClass() {
		if(entityClass != null){
			return ReflectionUtils.getHumanClassName(entityClass);
		}
		return "";
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } 
        boolean isSecuredEntity = obj instanceof SecuredEntity;
        boolean isBusinessEntity = obj instanceof BusinessEntity;
        if (!isSecuredEntity && !isBusinessEntity) {
            return false;
        }

        String otherCode = null;
        String otherClass = null;
        if(isSecuredEntity){
        	otherCode = ((SecuredEntity) obj).getCode();
        	otherClass = ((SecuredEntity) obj).getEntityClass();
        }
        if(isBusinessEntity){
        	otherCode = ((BusinessEntity) obj).getCode();
        	otherClass = ReflectionUtils.getCleanClassName(((BusinessEntity) obj).getClass().getName());
        }
                
        otherCode = otherClass + "-_-" + otherCode;
        String thisCode = entityClass + "-_-" + code;

        if(!thisCode.equals(otherCode)){
        	return false;
        }
        return true;
    }
}
