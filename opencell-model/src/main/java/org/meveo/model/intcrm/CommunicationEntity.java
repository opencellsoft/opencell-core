package org.meveo.model.intcrm;

import java.util.List;

import javax.persistence.OneToMany;

public class CommunicationEntity {
	private String name;
	
	private String description;
	
	private String type;
	
	@OneToMany(mappedBy = "campaign")
	private List<Template> templates;
    
}
