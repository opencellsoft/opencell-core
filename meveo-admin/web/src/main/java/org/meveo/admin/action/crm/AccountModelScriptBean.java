package org.meveo.admin.action.crm;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.crm.AccountModelScript;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.AccountModelScriptService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class AccountModelScriptBean extends BaseBean<AccountModelScript> {

	private static final long serialVersionUID = -8844358920768852531L;

	@Inject
	private AccountModelScriptService accountModelScriptService;

	@Inject
	private BusinessAccountModelService businessAccountModelService;

	private Long bamId;

	public AccountModelScriptBean() {
		super(AccountModelScript.class);
	}

	@Override
	public AccountModelScript initEntity() {
		if (bamId != null) {
			return super.initEntity();
		} else {
			return null;
		}
	}

	@Override
	public AccountModelScript initEntity(Long id) {
		super.initEntity(id);

		if (entity.isError()) {
			accountModelScriptService.compileScript(entity, true);
		}

		return entity;
	}

	@Override
	protected IPersistenceService<AccountModelScript> getPersistenceService() {
		return accountModelScriptService;
	}
	
	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	public void testCompilation() {
		accountModelScriptService.compileScript(entity, true);
		if (!entity.isError()) {
			messages.info(new BundleKey("messages", "scriptInstance.compilationSuccessfull"));
		}
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {

		entity.setCode(accountModelScriptService.getFullClassname(entity.getScript()));

		AccountModelScript actionDuplicate = accountModelScriptService.findByCode(entity.getCode(), getCurrentProvider());
		if (actionDuplicate != null && !actionDuplicate.getId().equals(entity.getId())) {
			messages.error(new BundleKey("messages", "offerModelScript.actionAlreadyExists"));
			return null;
		}

		// check duplicate script
		if (entity.isTransient() && accountModelScriptService.isExistsCode(entity.getCode(), getCurrentProvider())) {
			messages.error(new BundleKey("messages", "javax.persistence.EntityExistsException"));
			return null;
		}

		try {
			String result = super.saveOrUpdate(killConversation);

			// find bom
			BusinessAccountModel businessAccountModel = businessAccountModelService.findById(bamId);
			businessAccountModel.setScript(entity);
			businessAccountModelService.update(businessAccountModel, getCurrentUser());

			if (entity.isError()) {
				return null;
			}

			return result;
		} catch (Exception e) {
			messages.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void deleteInlist() {
		// delete in bom
		List<BusinessAccountModel> businessAccountModels = businessAccountModelService.findByScriptId(entity.getId());
		for (BusinessAccountModel bam : businessAccountModels) {
			bam.setScript(null);
			try {
				businessAccountModelService.update(bam, getCurrentUser());
			} catch (BusinessException e) {
				messages.error(e.getMessage());
			}
		}

		super.deleteInlist();
	}

	public Long getBamId() {
		return bamId;
	}

	public void setBamId(Long bamId) {
		this.bamId = bamId;
	}

}
