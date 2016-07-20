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

	@Column(name = "CODE", nullable = false, length = 60)
	@Size(max = 60, min = 1)
	@NotNull
	private String code;

	@Column(name = "ENTITY_CLASS", length = 255)
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
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		boolean isSecuredEntity = that instanceof SecuredEntity;
		boolean isBusinessEntity = that instanceof BusinessEntity;
		if (!isSecuredEntity && !isBusinessEntity) {
			return false;
		}

		String thatCode = null;
		String thatClass = null;
		if (isSecuredEntity) {
			thatCode = ((SecuredEntity) that).getCode();
			thatClass = ((SecuredEntity) that).getEntityClass();
		}
		if (isBusinessEntity) {
			thatCode = ((BusinessEntity) that).getCode();
			thatClass = ReflectionUtils.getCleanClassName(((BusinessEntity) that).getClass().getName());
		}

		thatCode = thatClass + "-_-" + thatCode;
		String thisCode = entityClass + "-_-" + code;

		if (!thisCode.equals(thatCode)) {
			return false;
		}
		return true;
	}
}
