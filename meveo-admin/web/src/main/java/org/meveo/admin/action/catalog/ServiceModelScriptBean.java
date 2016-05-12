package org.meveo.admin.action.catalog;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.meveo.service.script.ServiceModelScriptService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class ServiceModelScriptBean extends BaseBean<ServiceModelScript> {

	private static final long serialVersionUID = -4034706786961656074L;

	@Inject
	private ServiceModelScriptService serviceModelScriptService;

	@Inject
	private BusinessServiceModelService businessServiceModelService;

	private Long somId;

	public ServiceModelScriptBean() {
		super(ServiceModelScript.class);
	}

	@Override
	public ServiceModelScript initEntity() {
		if (somId != null) {
			return super.initEntity();
		} else {
			return null;
		}
	}

	@Override
	public ServiceModelScript initEntity(Long id) {
		super.initEntity(id);

		if (entity.isError()) {
			serviceModelScriptService.compileScript(entity, true);
		}

		return entity;
	}

	@Override
	protected IPersistenceService<ServiceModelScript> getPersistenceService() {
		return serviceModelScriptService;
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	public void testCompilation() {
		serviceModelScriptService.compileScript(entity, true);
		if (!entity.isError()) {
			messages.info(new BundleKey("messages", "scriptInstance.compilationSuccessfull"));
		}
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {

		entity.setCode(serviceModelScriptService.getFullClassname(entity.getScript()));

		ServiceModelScript actionDuplicate = serviceModelScriptService.findByCode(entity.getCode(), getCurrentProvider());
		if (actionDuplicate != null && !actionDuplicate.getId().equals(entity.getId())) {
			messages.error(new BundleKey("messages", "serviceModelScript.actionAlreadyExists"));
			return null;
		}

		// check duplicate script
		if (entity.isTransient() && serviceModelScriptService.isExistsCode(entity.getCode(), getCurrentProvider())) {
			messages.error(new BundleKey("messages", "javax.persistence.ScriptExistsException"));
			return null;
		}

		try {
			String result = super.saveOrUpdate(killConversation);

			// find bom
			BusinessServiceModel businessServiceModel = businessServiceModelService.findById(somId);
			businessServiceModel.setScript(entity);
			businessServiceModelService.update(businessServiceModel, getCurrentUser());

			if (entity.isError()) {
				return null;
			}

			return result;
		} catch (Exception e) {
			messages.error(e.getMessage());
			return null;
		}
	}

	public Long getSomId() {
		return somId;
	}

	public void setSomId(Long somId) {
		this.somId = somId;
	}

}
