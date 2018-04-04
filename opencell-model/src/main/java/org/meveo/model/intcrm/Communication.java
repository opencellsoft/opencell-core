package org.meveo.model.intcrm;

import java.util.List;

import javax.persistence.OneToMany;

public class Communication {
	private String name;
	
	private String description;
	
	private String type;
	
	@OneToMany(mappedBy = "communication")
	private List<Template> templates;

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

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}
	
	
}
