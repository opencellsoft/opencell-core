package org.meveo.model.dunning;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "collection_plan_status")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "collection_plan_status_seq")})
public class CollectionPlanStatus extends AuditableEntity {

	private static final long serialVersionUID = 1L;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id", referencedColumnName = "id")
	private DunningSettings dunningSettings;
}
