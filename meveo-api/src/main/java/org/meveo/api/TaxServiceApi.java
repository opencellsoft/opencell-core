package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.CountryTaxDto;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Tax;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TaxServiceApi extends BaseApi {

	@Inject
	private TaxService taxService;

	@Inject
	private ProviderService providerService;

	@Inject
	private UserService userService;

	public void create(TaxDto taxDto) throws MeveoApiException {
		if (!StringUtils.isBlank(taxDto.getTaxId())
				&& !StringUtils.isBlank(taxDto.getName())
				&& taxDto.getCountryTaxes() != null
				&& taxDto.getCountryTaxes().size() > 0) {

			Provider provider = providerService
					.findById(taxDto.getProviderId());
			User currentUser = userService.findById(taxDto.getCurrentUserId());

			for (CountryTaxDto ct : taxDto.getCountryTaxes()) {
				Tax tax = new Tax();
				tax.setCode(taxDto.getTaxId() + "_" + ct.getCountryCode());
				tax.setDescription(taxDto.getName());
				tax.setPercent(ct.getTaxValue());
				taxService.create(em, tax, currentUser, provider);
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(taxDto.getTaxId())) {
				missingFields.add("Tax Id");
			}
			if (StringUtils.isBlank(taxDto.getName())) {
				missingFields.add("Tax Name");
			}
			if (taxDto.getCountryTaxes() == null) {
				missingFields.add("Country Tax");
			} else {
				if (taxDto.getCountryTaxes().size() == 0) {
					missingFields.add("Country Tax");
				}
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public void remove(String taxId) throws MeveoApiException {
		List<Tax> taxes = taxService.findStartsWithCode(em, taxId + "\\_");

		for (Tax tax : taxes) {
			taxService.remove(em, tax);
		}
	}

	public void update(TaxDto taxDto) throws MeveoApiException {
		if (!StringUtils.isBlank(taxDto.getTaxId())
				&& !StringUtils.isBlank(taxDto.getName())
				&& taxDto.getCountryTaxes() != null
				&& taxDto.getCountryTaxes().size() > 0) {

			Provider provider = providerService
					.findById(taxDto.getProviderId());
			User currentUser = userService.findById(taxDto.getCurrentUserId());

			for (CountryTaxDto ct : taxDto.getCountryTaxes()) {
				String code = taxDto.getTaxId() + "_" + ct.getCountryCode();
				Tax tax = taxService.findByCode(em, code);

				if (tax != null) { // update
					tax.setDescription(taxDto.getName());
					tax.setPercent(ct.getTaxValue());
					taxService.update(em, tax, currentUser);
				} else { // create
					tax = new Tax();
					tax.setCode(code);
					tax.setDescription(taxDto.getName());
					tax.setPercent(ct.getTaxValue());
					taxService.create(em, tax, currentUser, provider);
				}

			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(taxDto.getTaxId())) {
				missingFields.add("Tax Id");
			}
			if (StringUtils.isBlank(taxDto.getName())) {
				missingFields.add("Tax Name");
			}
			if (taxDto.getCountryTaxes() == null) {
				missingFields.add("Country Tax");
			} else {
				if (taxDto.getCountryTaxes().size() == 0) {
					missingFields.add("Country Tax");
				}
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

}
