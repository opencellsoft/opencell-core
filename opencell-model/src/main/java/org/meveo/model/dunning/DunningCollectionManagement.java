package org.meveo.model.dunning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;

/**
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@Table(name = "dunning_collection_management")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_collection_management_seq")})
public class DunningCollectionManagement extends AuditableEntity  {
	
	private static final long serialVersionUID = 1L;
	
	  
	
	
	
	
 	public DunningCollectionManagement() {
		super();
		// TODO Auto-generated constructor stub
	}


	public DunningCollectionManagement(boolean includeCollectionAgency, @Size(max = 100) String emailCollectionAgency,
			@Size(max = 100) String agentFirstNameItem, @Size(max = 100) String agentLastNameItem,
			@Size(max = 100) String agentEmailItem, DunningSettings dunningSettings) {
		super();
		this.includeCollectionAgency = includeCollectionAgency;
		EmailCollectionAgency = emailCollectionAgency;
		this.agentFirstNameItem = agentFirstNameItem;
		AgentLastNameItem = agentLastNameItem;
		AgentEmailItem = agentEmailItem;
		this.dunningSettings = dunningSettings;
	}


	/**
	 * include collection agency
	 */
	@Type(type = "numeric_boolean")
	@Column(name = "include_collection_agency") 
	private boolean includeCollectionAgency=false;
	
	
 	/**
	 * email collection agency
	 */
	@Column(name = "email_collection_agency", length = 100)
	@Size(max = 100)
	private String EmailCollectionAgency;
	
	
	/**
	 * agent First Name Item
	 */
	@Column(name = "agent_first_name_item", length = 100)
	@Size(max = 100)
	private String agentFirstNameItem;
	
	
	/**
	 * agent Last Name Item
	 */
	@Column(name = "agent_last_name_item", length = 100)
	@Size(max = 100)
	private String AgentLastNameItem;
	
	
	/**
	 * agent email Item
	 */
	@Column(name = "agent_email_item", length = 100)
	@Size(max = 100)
	private String AgentEmailItem;
	
	/**
	 * dunning settings associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id")
	private DunningSettings dunningSettings;
	
	
	


	public DunningSettings getDunningSettings() {
		return dunningSettings;
	}


	public void setDunningSettings(DunningSettings dunningSettings) {
		this.dunningSettings = dunningSettings;
	}


	public boolean isIncludeCollectionAgency() {
		return includeCollectionAgency;
	}


	public void setIncludeCollectionAgency(boolean includeCollectionAgency) {
		this.includeCollectionAgency = includeCollectionAgency;
	}


	public String getEmailCollectionAgency() {
		return EmailCollectionAgency;
	}


	public void setEmailCollectionAgency(String emailCollectionAgency) {
		EmailCollectionAgency = emailCollectionAgency;
	}


	public String getAgentFirstNameItem() {
		return agentFirstNameItem;
	}


	public void setAgentFirstNameItem(String agentFirstNameItem) {
		this.agentFirstNameItem = agentFirstNameItem;
	}


	public String getAgentLastNameItem() {
		return AgentLastNameItem;
	}


	public void setAgentLastNameItem(String agentLastNameItem) {
		AgentLastNameItem = agentLastNameItem;
	}


	public String getAgentEmailItem() {
		return AgentEmailItem;
	}


	public void setAgentEmailItem(String agentEmailItem) {
		AgentEmailItem = agentEmailItem;
	}
	
	
	
	
	
	 
	 
	
	
	
	
}
