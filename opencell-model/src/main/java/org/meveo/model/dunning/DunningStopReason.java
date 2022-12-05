package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author Mbarek-Ay
 * @version 11.0
 */
@Entity
@Table(name = "dunning_stop_reasons")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "dunning_stop_reasons_seq") })
@NamedQueries({
		@NamedQuery(name = "DunningStopReason.findByStopReason", query = "FROM DunningStopReason d where d.stopReason = :stopReason"),
		@NamedQuery(name = "DunningStopReason.findByCodeAndDunningSettingCode", query = "FROM DunningStopReason d where d.stopReason = :stopReason and d.dunningSettings.code = :dunningSettingsCode")
	})
public class DunningStopReason extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	public DunningStopReason() {
		super();
	}

	public DunningStopReason(String stopReason, String description, DunningSettings dunningSettings) {
		super();
		this.stopReason = stopReason;
		this.description = description;
		this.dunningSettings = dunningSettings;
	}

	/**
	 * stop reason
	 */
	@Column(name = "stop_reason", nullable = false)
	@Size(max = 255, min = 1)
	@NotNull
	private String stopReason;
	 
  	/**
	 *description 
	 */
	@Column(name = "description", length = 255)
	@Size(max = 255)
	private String description;
	 
	/**
	 * dunning settings associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id", nullable = false, referencedColumnName = "id")
	@NotNull
	private DunningSettings dunningSettings;

	public String getStopReason() {
		return stopReason;
	}

	public void setStopReason(String stopReason) {
		this.stopReason = stopReason;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DunningSettings getDunningSettings() {
		return dunningSettings;
	}

	public void setDunningSettings(DunningSettings dunningSettings) {
		this.dunningSettings = dunningSettings;
	}


 
	
	
	
	
}
