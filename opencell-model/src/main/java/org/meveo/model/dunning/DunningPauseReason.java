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

import org.meveo.model.crm.Provider;


/**
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@Table(name = "dunning_pause_reasons")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "dunning_pause_reasons_seq") })
@NamedQueries({
		@NamedQuery(name = "DunningPauseReasons.findByCodeAndDunningSettingCode", query = "FROM DunningPauseReason d where d.pauseReason = :pauseReason and d.dunningSettings.code = :dunningSettingsCode") })

public class DunningPauseReason extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	public DunningPauseReason() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DunningPauseReason(@Size(max = 1000) String pauseReason, @Size(max = 255) String description) {
		super();
		this.pauseReason = pauseReason;
		this.description = description;
	}

	/**
	 * pause reason
	 */
	@Column(name = "pause_reason", nullable = false)
	@Size(max = 255, min = 1)
	@NotNull
	private String pauseReason;
	
	
	 
  	/**
	 *description 
	 */
	@Column(name = "description", length = 255)
	@Size(max = 255)
	private String description;

	/**
	 * PaymentPlanPolicy associated to the entity
	 */	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    protected Provider provider; 
    
	
	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	/**
	 * dunning settings associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id",nullable = false, referencedColumnName = "id")
	@NotNull
	private DunningSettings dunningSettings;

	public String getPauseReason() {
		return pauseReason;
	}



	public void setPauseReason(String pauseReason) {
		this.pauseReason = pauseReason;
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
