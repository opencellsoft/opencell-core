package org.meveo.model.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;

/**
 * Entity that accessible entities for a user.
 */
@Entity
@Table(name = "ADM_SECURED_ENTITY")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_SECURED_ENTITY_SEQ")
public class SecuredEntity extends BusinessEntity{
	private static final long serialVersionUID = 84222776645282176L;
	
	public SecuredEntity() {
	}
	
	public SecuredEntity(BusinessEntity businessEntity) {
		this.setProvider(businessEntity.getProvider());
		this.setCode(businessEntity.getCode());
		this.setDescription(businessEntity.getDescription());
		this.setVersion(businessEntity.getVersion());
		this.setActive(businessEntity.isActive());
		this.setDisabled(businessEntity.isDisabled());
		this.setAuditable(businessEntity.getAuditable());
		this.setAppendGeneratedCode(businessEntity.isAppendGeneratedCode());
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	@NotNull
	private User user;
	
	@Column(name = "ENTITY_CLASS", length = 255)
    @Size(max = 255)
    private String entityClass;
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getEntityClass() {
		return entityClass;
	}
	
	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}
	
	public String getReadableEntityClass(){
		return ReflectionUtils.getHumanClassName(this.getEntityClass());
	}
}
