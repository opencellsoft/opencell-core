package org.meveo.model.mediation;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.Auditable;
import org.meveo.model.AuditableEntity;

/**
 * 
 * @author Tarik FAKHOURI
 * @category Mediation
 * @version 13.0
 */
@Entity
@Table(name = "edr_versioning_rule")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "edr_versioning_rule_seq")})
@SuppressWarnings("serial")
public class EdrVersioningRule extends AuditableEntity {

    @Column(name = "priority", nullable = false)
	@NotNull
	private Integer priority;

    @Column(name = "criterial_el", nullable = false)
	@NotNull
	private String criteriaEL;

    @Column(name = "key_el", nullable = false)
	@NotNull
	private String keyEL;

    @Column(name = "is_new_version_el", nullable = false)
	@NotNull
	private String isNewVersionEL;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mediation_setting_id", nullable = false, referencedColumnName = "id")
//	@NotNull
	private MediationSetting mediationSetting;
    
    @PrePersist
    private void prePersist() {
    	if(auditable == null)
    		auditable = new Auditable();
    	auditable.setCreated(new Date());
    }

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	public MediationSetting getMediationSetting() {
		return mediationSetting;
	}

	public void setMediationSetting(MediationSetting mediationSetting) {
		this.mediationSetting = mediationSetting;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(criteriaEL, isNewVersionEL, keyEL, mediationSetting, priority);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		EdrVersioningRule other = (EdrVersioningRule) obj;
		return Objects.equals(criteriaEL, other.criteriaEL) && Objects.equals(isNewVersionEL, other.isNewVersionEL)
				&& Objects.equals(keyEL, other.keyEL) && Objects.equals(mediationSetting, other.mediationSetting)
				&& Objects.equals(priority, other.priority);
	}

	public String getCriteriaEL() {
		return criteriaEL;
	}

	public void setCriteriaEL(String criterialEL) {
		this.criteriaEL = criterialEL;
	}

	public String getKeyEL() {
		return keyEL;
	}

	public void setKeyEL(String keyEL) {
		this.keyEL = keyEL;
	}

	public String getIsNewVersionEL() {
		return isNewVersionEL;
	}

	public void setIsNewVersionEL(String isNewVersionEL) {
		this.isNewVersionEL = isNewVersionEL;
	}
    
    
}
