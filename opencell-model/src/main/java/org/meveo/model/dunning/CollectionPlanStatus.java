package org.meveo.model.dunning;

import java.util.Hashtable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "collection_plan_status", uniqueConstraints = @UniqueConstraint(columnNames = {"status"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "collection_plan_status_seq")})
public class CollectionPlanStatus extends AuditableEntity {

	private static final long serialVersionUID = 1L;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id", referencedColumnName = "id")
	private DunningSettings dunningSettings;
	
	@Type(type = "json")
	@Column(name = "language", columnDefinition = "jsonb")
	private Map<String, String> language = new Hashtable<String, String>();
	
	@Column(name = "status", length = 255)
	private String status;
	
	@Column(name = "context", length = 255)
	private String context;

	/**
	 * @return the dunningSettings
	 */
	public DunningSettings getDunningSettings() {
		return dunningSettings;
	}

	/**
	 * @param dunningSettings the dunningSettings to set
	 */
	public void setDunningSettings(DunningSettings dunningSettings) {
		this.dunningSettings = dunningSettings;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the context
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * @return the language
	 */
	public Map<String, String> getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(Map<String, String> language) {
		this.language = language;
	}
	
	
}
