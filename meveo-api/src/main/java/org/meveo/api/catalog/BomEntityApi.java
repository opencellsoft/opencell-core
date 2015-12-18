package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BomEntityDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.bom.BOMEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.catalog.impl.BusinessOfferService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BomEntityApi extends BaseApi {

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private BusinessOfferService bomEntityService;

	@Inject
	private ScriptInstanceService scriptInstanceService;

	public void create(BomEntityDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getBomCode()) && !StringUtils.isBlank(postData.getOfferTemplateCode())) {
			if (bomEntityService.findByCode(postData.getBomCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(BOMEntity.class, postData.getBomCode());
			}
			
			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplateCode(),
					currentUser.getProvider());
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplateCode());
			}

			ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getCreationScriptCode(),
					currentUser.getProvider());

			BOMEntity bomEntity = new BOMEntity();
			bomEntity.setCode(postData.getBomCode());
			bomEntity.setOfferTemplate(offerTemplate);
			bomEntity.setCreationScript(scriptInstance);
			bomEntity.setDescription(postData.getDescription());

			bomEntityService.create(bomEntity, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getBomCode())) {
				missingParameters.add("bomCode");
			}
			if (StringUtils.isBlank(postData.getOfferTemplateCode())) {
				missingParameters.add("offerTemplateCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(BomEntityDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getBomCode()) && !StringUtils.isBlank(postData.getOfferTemplateCode())) {
			BOMEntity bomEntity = bomEntityService.findByCode(postData.getBomCode(), currentUser.getProvider());
			if (bomEntity == null) {
				throw new EntityDoesNotExistsException(BOMEntity.class, postData.getBomCode());
			}

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplateCode(),
					currentUser.getProvider());
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplateCode());
			}

			ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getCreationScriptCode(),
					currentUser.getProvider());

			bomEntity.setDescription(postData.getDescription());
			bomEntity.setOfferTemplate(offerTemplate);
			bomEntity.setCreationScript(scriptInstance);

			bomEntityService.update(bomEntity, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getBomCode())) {
				missingParameters.add("bomCode");
			}
			if (StringUtils.isBlank(postData.getOfferTemplateCode())) {
				missingParameters.add("offerTemplateCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public BomEntityDto find(String bomEntityCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(bomEntityCode)) {
			BOMEntity bomEntity = bomEntityService.findByCode(bomEntityCode, provider);
			if (bomEntity != null) {
				BomEntityDto bomEntityDto = new BomEntityDto();
				bomEntityDto.setBomCode(bomEntity.getCode());
				bomEntityDto.setDescription(bomEntity.getDescription());
				if (bomEntity.getOfferTemplate() != null) {
					bomEntityDto.setOfferTemplateCode(bomEntity.getOfferTemplate().getCode());
				}
				if (bomEntity.getCreationScript() != null) {
					bomEntityDto.setCreationScriptCode(bomEntity.getCreationScript().getCode());
				}

				return bomEntityDto;
			}

			throw new EntityDoesNotExistsException(BOMEntity.class, bomEntityCode);
		} else {
			if (StringUtils.isBlank(bomEntityCode)) {
				missingParameters.add("bomEntityCode");
			}

			throw new MeveoApiException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String bomEntityCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(bomEntityCode)) {
			BOMEntity bomEntity = bomEntityService.findByCode(bomEntityCode, provider);
			if (bomEntity == null) {
				throw new EntityDoesNotExistsException(BOMEntity.class, bomEntityCode);
			}

			bomEntityService.remove(bomEntity);
		} else {
			if (StringUtils.isBlank(bomEntityCode)) {
				missingParameters.add("bomEntityCode");
			}

			throw new MeveoApiException(getMissingParametersExceptionMessage());
		}
	}

	public void createOrUpdate(BomEntityDto postData, User currentUser) throws MeveoApiException {
		BOMEntity bomEntity = bomEntityService.findByCode(postData.getBomCode(), currentUser.getProvider());
		if (bomEntity == null) {
			// create
			create(postData, currentUser);
		} else {
			// update
			update(postData, currentUser);
		}
	}
}
