package org.meveo.model.intcrm;

import javax.persistence.Column;
import javax.validation.constraints.Size;

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
