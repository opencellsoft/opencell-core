package org.meveo.model.dunning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "invoice_dunning_statuses")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "invoice_dunning_statuses_seq")})
@NamedQueries({
		@NamedQuery(name = "DunningInvoiceStatus.findByCodeAndDunningSettingCode", query = "FROM DunningInvoiceStatus d where d.status = :status and d.dunningSettings.code = :dunningSettingsCode"),
		@NamedQuery(name = "DunningInvoiceStatus.findByStatusAndLanguage", query = "FROM DunningInvoiceStatus d where d.status = :status and d.language.id = :languageId and d.context IN ('PAUSED_DUNNING','STOPPED_DUNNING','EXCLUDED_FROM_DUNNING')") })

public class DunningInvoiceStatus extends AuditableEntity  {
	
	private static final long serialVersionUID = 1L;
	
	
	
	
	public DunningInvoiceStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public DunningInvoiceStatus(@Size(max = 50) TradingLanguage language, @Size(max = 50) String status,
			@Size(max = 255) DunningInvoiceStatusContextEnum context, DunningSettings dunningSettings) {
		super();
		this.language = language;
		this.status = status;
		this.context = context;
		this.dunningSettings = dunningSettings;
	}



	/**
	 *language
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id",nullable = false, referencedColumnName = "id")
	@NotNull
	private TradingLanguage language;
	
	
	/**
	 * status
	 */
	@Column(name = "status", nullable = false)
	@Size(max = 255, min = 1)
	@NotNull
	@JsonProperty
	private String status;
	
	
	 
  	/**
	 *context 
	 */
	@Column(name = "context", length = 255)
	@Enumerated(EnumType.STRING)
	private DunningInvoiceStatusContextEnum context;

	 
	/**
	 * dunning settings associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id")
	private DunningSettings dunningSettings;




	public TradingLanguage getLanguage() {
		return language;
	}



	public void setLanguage(TradingLanguage language) {
		this.language = language;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public DunningInvoiceStatusContextEnum getContext() {
		return context;
	}



	public void setContext(DunningInvoiceStatusContextEnum context) {
		this.context = context;
	}



	public DunningSettings getDunningSettings() {
		return dunningSettings;
	}



	public void setDunningSettings(DunningSettings dunningSettings) {
		this.dunningSettings = dunningSettings;
	}

	
	
	
}
