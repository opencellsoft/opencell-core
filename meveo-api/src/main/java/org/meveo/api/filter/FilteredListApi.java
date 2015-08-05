package org.meveo.api.filter;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.crm.Provider;
import org.meveo.model.filter.Filter;
import org.meveo.service.filter.FilterService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class FilteredListApi extends BaseApi {

	@Inject
	private FilterService filterService;

	public String list(String filterCode, Provider provider) throws MeveoApiException {
		String result = "";

		Filter filter = filterService.findByCode(filterCode, provider);
		if (filter == null) {
			throw new EntityDoesNotExistsException(Filter.class, filterCode);
		}

		return result;
	}

}
