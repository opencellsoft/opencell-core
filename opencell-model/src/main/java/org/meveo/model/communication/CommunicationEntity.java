package org.meveo.model.communication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@CustomFieldEntity(cftCodePrefix = "COMENT")
@ExportIdentifier({ "code" })
//@DiscriminatorValue(value = "")
@Table(name = "com_communication_entity")
public class CommunicationEntity {
	@Column(name = "name", length = 50)
	@Size(max = 50)
	private String name;

	@Column(name = "name", length = 50)
	@Size(max = 50)
	private String description;

	@Column(name = "name", length = 50)
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
