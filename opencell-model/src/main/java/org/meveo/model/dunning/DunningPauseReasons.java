package org.meveo.model.dunning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;

/**
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@Table(name = "dunning_pause_reasons")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_pause_reasons_seq")})
@NamedQueries({
		@NamedQuery(name = "DunningPauseReasons.findByCodeAndDunningSettingCode", query = "FROM DunningPauseReasons d where d.pauseReason = :pauseReason and d.dunningSettings.code = :dunningSettingsCode") })

public class DunningPauseReasons extends AuditableEntity  {
	
	private static final long serialVersionUID = 1L;
	
	
	
	
	
	public DunningPauseReasons() {
		super();
		// TODO Auto-generated constructor stub
	}
 


	public DunningPauseReasons(@Size(max = 50) TradingLanguage language, @Size(max = 1000) String pauseReason,
			@Size(max = 255) String description) {
		super();
		this.language = language;
		this.pauseReason = pauseReason;
		this.description = description;
	}



	/**
	 *language
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id",nullable = false, referencedColumnName = "id")
	@NotNull
	private TradingLanguage language;
	
	
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
	 * dunning settings associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id",nullable = false, referencedColumnName = "id")
	@NotNull
	private DunningSettings dunningSettings;




	public TradingLanguage getLanguage() {
		return language;
	}



	public void setLanguage(TradingLanguage language) {
		this.language = language;
	}



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
