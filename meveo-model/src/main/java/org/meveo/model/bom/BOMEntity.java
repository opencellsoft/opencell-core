package org.meveo.model.bom;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.scripts.ScriptInstance;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ObservableEntity
@Table(name = "CAT_BOM_ENTITY", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_BOM_ENTITY_SEQ")
public class BOMEntity extends BusinessEntity {

	private static final long serialVersionUID = 683873220792653929L;

	@ManyToOne
	@JoinColumn(name = "OFFER_TEMPLATE_ID")
	private OfferTemplate offerTemplate;

	@ManyToOne
	@JoinColumn(name = "SCRIPT_INSTANCE_ID")
	private ScriptInstance scriptInstance;

	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	public ScriptInstance getScriptInstance() {
		return scriptInstance;
	}

	public void setScriptInstance(ScriptInstance scriptInstance) {
		this.scriptInstance = scriptInstance;
	}

}
