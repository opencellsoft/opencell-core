package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CountryDto;
import org.meveo.api.exception.EnvironmentException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Country;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@Stateless
public class CountryServiceApi {

	@Inject
	private CountryService countryService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	public void create(CountryDto countryDto) throws EnvironmentException {
		if (!StringUtils.isBlank(countryDto.getCountryCode())
				&& !StringUtils.isBlank(countryDto.getName())
				&& !StringUtils.isBlank(countryDto.getCurrencyCode())) {
			if (countryService.findByCode(countryDto.getCountryCode()) != null) {
				throw new EnvironmentException("Country code already exist.");
			} else {
				if (currencyService.findByCode(countryDto.getCurrencyCode()) != null) {
					countryService.create(countryDto.getUserId(),
							countryDto.getCountryCode(), countryDto.getName(),
							countryDto.getCurrencyCode());
				} else {
					throw new EnvironmentException(
							"Currency code does not exist.");
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryDto.getCountryCode())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(countryDto.getName())) {
				missingFields.add("name");
			}
			if (StringUtils.isBlank(countryDto.getCurrencyCode())) {
				missingFields.add("currencyCode");
			}
			sb.append(org.apache.commons.lang.StringUtils.join(
					missingFields.toArray(), ", "));
			sb.append(".");
		}
	}

	public CountryDto find(String countryCode) throws EnvironmentException {
		if (!StringUtils.isBlank(countryCode)) {
			Country country = countryService.findByCode(countryCode);
			if (country != null) {
				return new CountryDto(country);
			}
			throw new EnvironmentException("Country code " + countryCode
					+ " does not exists.");
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryCode)) {
				missingFields.add("countryCode");
			}
			sb.append(org.apache.commons.lang.StringUtils.join(
					missingFields.toArray(), ", "));
			sb.append(".");
			throw new EnvironmentException(sb.toString());
		}
	}

}
