package org.meveo.model.dunning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

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
