package org.meveo.model.communication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "com_communication_entity" })
@Table(name = "com_communication_entity")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "com_communication_entity_seq") })
public class CommunicationEntity extends BaseEntity {
	@Column(name = "name", length = 50)
	@Size(max = 50)
	private String name;

	@Column(name = "description", length = 255)
	@Size(max = 50)
	private String description;

	@Column(name = "type", length = 50)
	@Size(max = 50)
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
    
}
