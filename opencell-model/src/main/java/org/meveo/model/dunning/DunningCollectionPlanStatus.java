package org.meveo.model.dunning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "dunning_collection_plan_statuses")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "dunning_collection_plan_statuses_seq") })
public class DunningCollectionPlanStatus extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	public DunningCollectionPlanStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DunningCollectionPlanStatus(@Size(max = 50) String status, DunningCollectionPlanStatusContextEnum context, DunningSettings dunningSettings) {
		super();
		this.status = status;
		this.context = context;
		this.dunningSettings = dunningSettings;
	}

	/**
	 * status
	 */
	@Column(name = "status", length = 50)
	@Size(max = 50)
	@NotNull
	private String status;

  	/**
	 *context 
	 */
	@Column(name = "context", length = 255)
	@Enumerated(EnumType.STRING)
	@NotNull
	private DunningCollectionPlanStatusContextEnum context;

	/**
	 * dunning settings associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id",nullable = false, referencedColumnName = "id")
	@NotNull
	private DunningSettings dunningSettings;


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DunningCollectionPlanStatusContextEnum getContext() {
		return context;
	}

	public void setContext(DunningCollectionPlanStatusContextEnum context) {
		this.context = context;
	}

	public DunningSettings getDunningSettings() {
		return dunningSettings;
	}

	public void setDunningSettings(DunningSettings dunningSettings) {
		this.dunningSettings = dunningSettings;
	}
 

     
	
	
	
	
}
