package org.meveo.model.communication.contact;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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

	public ContactCategory() {
		super();
	}

	public ContactCategory(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

}
