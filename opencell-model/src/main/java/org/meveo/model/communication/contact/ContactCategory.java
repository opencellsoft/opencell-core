package org.meveo.model.communication.contact;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "ContactCategory")
@ExportIdentifier({ "code" })
@Table(name = "com_contact_category", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
public class ContactCategory extends BusinessCFEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 419874471959920750L;

	@Column(name = "code")
	private String code;

	@Column(name = "description")
	private String description;
	
	public ContactCategory() {
		super();
	}

	public ContactCategory(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
