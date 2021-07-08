package org.meveo.model.dunning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

/**
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@Table(name = "dunning_collection_plan_statuses")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_collection_plan_statuses_seq")})
public class DunningCollectionPlanStatuses extends AuditableEntity  {
	
	private static final long serialVersionUID = 1L;
	
	
	
	
	
	
	
	public DunningCollectionPlanStatuses() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	


	public DunningCollectionPlanStatuses(@Size(max = 50) String language, @Size(max = 50) String status,
			@Size(max = 255) String context, DunningSettings dunningSettings) {
		super();
		this.language = language;
		this.status = status;
		this.context = context;
		this.dunningSettings = dunningSettings;
	}





	/**
	 *language code
	 */
	@Column(name = "language", length = 50)
	@Size(max = 50)
	private String language;
	
	
	/**
	 * status
	 */
	@Column(name = "status", length = 50)
	@Size(max = 50)
	private String status;
	
	
	 
  	/**
	 *context 
	 */
	@Column(name = "context", length = 255)
	@Size(max = 255)
	private String context;

	 
	/**
	 * dunning settings associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id",nullable = false, referencedColumnName = "id")
	@NotNull
	private DunningSettings dunningSettings;







	public String getLanguage() {
		return language;
	}





	public void setLanguage(String language) {
		this.language = language;
	}





	public String getStatus() {
		return status;
	}





	public void setStatus(String status) {
		this.status = status;
	}





	public String getContext() {
		return context;
	}





	public void setContext(String context) {
		this.context = context;
	}





	public DunningSettings getDunningSettings() {
		return dunningSettings;
	}





	public void setDunningSettings(DunningSettings dunningSettings) {
		this.dunningSettings = dunningSettings;
	}
 

     
	
	
	
	
}
