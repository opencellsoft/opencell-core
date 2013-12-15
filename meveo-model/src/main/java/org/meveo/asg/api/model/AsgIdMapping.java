package org.meveo.asg.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.IEntity;

//TODO: move this to ASG project
@Entity
@Table(name = "ASG_ID_MAPPING")
@SequenceGenerator(name = "ASG_ID_GEN", sequenceName = "CAT_CALENDAR_SEQ")
public class AsgIdMapping implements IEntity {

	@Id
	@Column(name = "MEVEO_CODE")
	@GeneratedValue(generator = "ASG_ID_GEN")
	Long meveoCode;

	@Column(name = "ASG_ID", length = 200, unique = true)
	String asgId;

	@Enumerated(EnumType.STRING)
	@Column(name = "ENTITY_TYPE")
	EntityCodeEnum entityType;

	public String getAsgId() {
		return asgId;
	}

	public void setAsgId(String asgId) {
		this.asgId = asgId;
	}

	public EntityCodeEnum getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityCodeEnum entityType) {
		this.entityType = entityType;
	}

	public Long getMeveoCode() {
		return meveoCode;
	}

	public void setMeveoCode(Long meveoCode) {
		this.meveoCode = meveoCode;
	}

	@Override
	public Serializable getId() {
		return asgId;
	}

	@Override
	public boolean isTransient() {
		return false;
	}

}
