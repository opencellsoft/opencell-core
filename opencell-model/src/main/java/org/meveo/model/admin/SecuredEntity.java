package org.meveo.model.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
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
        this.setEntityClass(ReflectionUtils.getCleanClassName(businessEntity.getClass().getName()));
    }

    public SecuredEntity(SecuredEntity securedEntity) {
        this.setCode(securedEntity.getCode());
        this.setEntityClass(securedEntity.getEntityClass());
    }

    @Column(name = "code", nullable = false, length = 255)
    @Size(max = 255, min = 1)
    @NotNull
    private String code;

    @Column(name = "entity_class", length = 255)
    @Size(max = 255)
    private String entityClass;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public String readableEntityClass() {
        if (entityClass != null) {
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

        String thatCode = null;
        String thatClass = null;
        if (isSecuredEntity) {
            thatCode = ((SecuredEntity) obj).getCode();
            thatClass = ((SecuredEntity) obj).getEntityClass();
        }
        if (isBusinessEntity) {
            thatCode = ((BusinessEntity) obj).getCode();
            thatClass = ReflectionUtils.getCleanClassName(obj.getClass().getName());
        }

        thatCode = thatClass + "-_-" + thatCode;
        String thisCode = entityClass + "-_-" + code;

        if (!thisCode.equals(thatCode)) {
            return false;
        }
        return true;
    }
}
