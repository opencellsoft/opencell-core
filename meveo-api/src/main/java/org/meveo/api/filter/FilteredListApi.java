package org.meveo.api.filter;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.filter.FilteredListDto;
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

	public String list(String filterCode, Integer firstRow, Integer numberOfRows, Provider provider)
			throws MeveoApiException {
		String result = "";

		Filter filter = filterService.findByCode(filterCode, provider);
		if (filter == null) {
			throw new EntityDoesNotExistsException(Filter.class, filterCode);
		}

		try {
			result = filterService.filteredList(filter, firstRow, numberOfRows);
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}

		return result;
	}

	public String listByXmlInput(FilteredListDto postData, Provider provider) throws MeveoApiException {
		String result = "";

		try {
			Filter filter = filterService.parse(postData.getXmlInput());
			result = filterService.filteredList(filter, postData.getFirstRow(), postData.getNumberOfRows());
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}

		return result;
	}

}
