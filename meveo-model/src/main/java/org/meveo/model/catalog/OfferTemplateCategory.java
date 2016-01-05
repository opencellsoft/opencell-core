package org.meveo.model.catalog;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "OFFER_CATEGORY")
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_OFFER_TEMPLATE_CATEGORY", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE",
		"PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_TEMPLATE_CATEGORY_SEQ")
public class OfferTemplateCategory extends BusinessCFEntity {

	private static final long serialVersionUID = -5088201294684394309L;

	@Override
	public ICustomFieldEntity getParentCFEntity() {
		return null;
	}

}
