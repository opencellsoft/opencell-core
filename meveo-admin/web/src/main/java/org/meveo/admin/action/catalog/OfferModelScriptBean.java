package org.meveo.admin.action.catalog;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
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

	@Inject
	private BusinessOfferModelService businessOfferModelService;

	private Long bomId;

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

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		if (entity.isTransient()) {
			String derivedCode = offerModelScriptService.getDerivedCode(entity.getScript());
			entity.setCode(derivedCode);
		}

		OfferModelScript actionDuplicate = offerModelScriptService.findByCode(entity.getCode(), getCurrentProvider());
		if (actionDuplicate != null && !actionDuplicate.getId().equals(entity.getId())) {
			messages.error(new BundleKey("messages", "offerModelScript.actionAlreadyExists"));
			return null;
		}

		try {
			String result = super.saveOrUpdate(killConversation);

			if (entity.isError()) {
				return null;
			}

			// find bom
			BusinessOfferModel businessOfferModel = businessOfferModelService.findById(bomId);
			businessOfferModel.setScript(entity);
			businessOfferModelService.update(businessOfferModel, getCurrentUser());

			return result;
		} catch (Exception e) {
			messages.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void deleteInlist() {
		// delete in bom
		List<BusinessOfferModel> businessOfferModels = businessOfferModelService.findByScriptId(entity.getId());
		for (BusinessOfferModel bom : businessOfferModels) {
			bom.setScript(null);
			try {
				businessOfferModelService.update(bom, getCurrentUser());
			} catch (BusinessException e) {
				messages.error(e.getMessage());
			}
		}

		super.deleteInlist();
	}

	public Long getBomId() {
		return bomId;
	}

	public void setBomId(Long bomId) {
		this.bomId = bomId;
	}

}
