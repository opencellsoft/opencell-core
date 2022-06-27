package org.meveo.model.mediation;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
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
	private String criterialEl;

    @Column(name = "key_el", nullable = false)
	@NotNull
	private String keyEl;

    @Column(name = "is_new_version_el", nullable = false)
	@NotNull
	private String isNewVersionEl;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mediation_setting_id", nullable = false, referencedColumnName = "id")
	@NotNull
	private MediationSetting mediationSetting;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getCriterialEl() {
		return criterialEl;
	}

	public void setCriterialEl(String criterialEl) {
		this.criterialEl = criterialEl;
	}

	public String getKeyEl() {
		return keyEl;
	}

	public void setKeyEl(String keyEl) {
		this.keyEl = keyEl;
	}

	public String getIsNewVersionEl() {
		return isNewVersionEl;
	}

	public void setIsNewVersionEl(String isNewVersionEl) {
		this.isNewVersionEl = isNewVersionEl;
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
		result = prime * result + Objects.hash(criterialEl, isNewVersionEl, keyEl, mediationSetting, priority);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EdrVersioningRule other = (EdrVersioningRule) obj;
		return Objects.equals(criterialEl, other.criterialEl) && Objects.equals(isNewVersionEl, other.isNewVersionEl)
				&& Objects.equals(keyEl, other.keyEl) && Objects.equals(mediationSetting, other.mediationSetting)
				&& Objects.equals(priority, other.priority);
	}
    
    
}
