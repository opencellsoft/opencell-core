package org.meveo.model.mediation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.AuditableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 
 * @author Tarik FAKHOURI
 * @category Mediation
 * @version 13.0
 */
@Entity
@Table(name = "mediation_setting")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "mediation_setting_seq")})
@SuppressWarnings("serial")
public class MediationSetting extends AuditableEntity {

    @Column(name = "enable_edr_versioning")
    @Convert(converter = NumericBooleanConverter.class)
	private boolean enableEdrVersioning = Boolean.FALSE;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "mediationSetting", orphanRemoval = true)
	private Set<EdrVersioningRule> rules = new HashSet<>();

	public boolean isEnableEdrVersioning() {
		return enableEdrVersioning;
	}

	public void setEnableEdrVersioning(boolean enableEdrVersioning) {
		this.enableEdrVersioning = enableEdrVersioning;
	}

	public Set<EdrVersioningRule> getRules() {
		return rules;
	}

	public void setRules(Set<EdrVersioningRule> rules) {
		this.rules = rules;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(enableEdrVersioning);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		MediationSetting other = (MediationSetting) obj;
		return enableEdrVersioning == other.enableEdrVersioning;
	}


    
    
}
