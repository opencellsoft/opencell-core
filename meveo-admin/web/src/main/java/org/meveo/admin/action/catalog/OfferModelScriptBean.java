package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.script.OfferModelScriptService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class OfferModelScriptBean extends BaseBean<OfferModelScript> {

	private static final long serialVersionUID = -4034706786961656074L;

	@Inject
	private OfferModelScriptService offerModelScriptService;

	public OfferModelScriptBean() {
		super(OfferModelScript.class);
	}

	@Override
	protected IPersistenceService<OfferModelScript> getPersistenceService() {
		return offerModelScriptService;
	}

	public void testCompilation() {
		offerModelScriptService.compileScript(entity, true);
		if (!entity.isError()) {
			messages.info(new BundleKey("messages", "scriptInstance.compilationSuccessfull"));
		}
	}

}
