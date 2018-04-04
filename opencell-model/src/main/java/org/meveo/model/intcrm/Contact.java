package org.meveo.model.intcrm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.security.Permission;

@Entity
@CustomFieldEntity(cftCodePrefix = "CONTACT")
@ExportIdentifier({ "code" })
//@DiscriminatorValue(value = "")
@Table(name = "crm_contact")
public class Contact {
	@Column(name = "description", length = 512)
	@Size(max = 512)
	private String description;

	@Column(name = "email", length = 255)
	@Size(max = 255)
	private String email;

	@Column(name = "assistantname", length = 50)
	@Size(max = 50)	
	private String assistantName;

	@Column(name = "assistantphone", length = 255)
	@Size(max = 255)
	private String assistantPhone;
	
	
	@Column(name = "isvip",  columnDefinition = "tinyint default false")
	private boolean isVip;
	

	@Column(name = "issuspect",  columnDefinition = "tinyint default false")
	private boolean isSuspect;
	
	@OneToMany(mappedBy = "crm_contact", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<ContactGroup> contactGroups = new ArrayList<>();

}
