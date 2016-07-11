package org.meveo.model.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	}

	@Column(name = "CODE", nullable = false, length = 60)
	@Size(max = 60, min = 1)
	@NotNull
	protected String code;

	@Column(name = "DESCRIPTION", nullable = true, length = 100)
	@Size(max = 100)
	protected String description;

	@Column(name = "ENTITY_CLASS", length = 255)
	@Size(max = 255)
	private String entityClass;
	
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
        if (!isSecuredEntity || !isBusinessEntity) {
            return false;
        }

        String otherCode = null;
        if(isSecuredEntity){
        	otherCode = ((SecuredEntity) obj).getCode();
        }
        if(isBusinessEntity){
        	otherCode = ((BusinessEntity) obj).getCode();
        }

        if (code == null) {
            if (otherCode != null) {
                return false;
            }
        } else if (!code.equals(otherCode)) {
            return false;
        }
        return true;
    }
}
