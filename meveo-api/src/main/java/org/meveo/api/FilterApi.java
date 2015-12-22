package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.FilterDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.filter.Filter;
import org.meveo.service.filter.FilterService;

/**
 * @author Tyshan Shi
 *
 **/
@Stateless
public class FilterApi extends BaseApi {

	@Inject
	private FilterService filterService;

	private void create(FilterDto dto, User currentUser,Provider provider) {
		Filter filter = new Filter();
		filter.setCode(dto.getCode());
		filter.setDescription(dto.getDescription());
		filter.setInputXml(dto.getInputXml());
		filter.setShared(dto.getShared());
		filterService.create(filter, currentUser,provider);
	}

	public void createOrUpdate(FilterDto dto, User currentUser) throws MeveoApiException {
		if (dto != null && !StringUtils.isBlank(dto.getCode())) {
			Provider provider=currentUser.getProvider();
			Filter existed = filterService.findByCode(dto.getCode(), provider);
			if (existed != null) {
				existed.setDescription(dto.getDescription());
				existed.setInputXml(dto.getInputXml());
				existed.setShared(dto.getShared());
				filterService.update(existed,currentUser);
			} else {
				create(dto, currentUser,provider);
			}
		} else {
			missingParameters.add("code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
