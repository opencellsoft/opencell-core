package org.meveo.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.OrganizationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OrganizationServiceApi extends BaseApi {

	@Inject
	private SellerService sellerService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	public void create(OrganizationDto orgDto) throws MeveoApiException {
		if (!StringUtils.isBlank(orgDto.getOrganizationId())
				&& !StringUtils.isBlank(orgDto.getCountryCode())
				&& !StringUtils.isBlank(orgDto.getDefaultCurrencyCode())) {

			Provider provider = providerService
					.findById(orgDto.getProviderId());
			User currentUser = userService.findById(orgDto.getCurrentUserId());

			Seller org = sellerService.findByCode(orgDto.getOrganizationId(),
					provider);
			if (org != null) {
				throw new MeveoApiException("Organization with id="
						+ orgDto.getOrganizationId() + " already exists.");
			}

			TradingCountry tr = tradingCountryService.findByTradingCountryCode(
					orgDto.getCountryCode(), provider);
			if (tr == null) {
				throw new MeveoApiException("Trading country with code="
						+ orgDto.getCountryCode() + " does not exists.");
			}

			TradingCurrency tc = tradingCurrencyService
					.findByTradingCurrencyCode(orgDto.getDefaultCurrencyCode(),
							provider);
			if (tc == null) {
				throw new MeveoApiException("Trading currency with code="
						+ orgDto.getDefaultCurrencyCode() + " does not exists.");
			}

			if (!StringUtils.isBlank(orgDto.getParentId())) { // with parent
																// seller
				Seller parentSeller = sellerService.findByCode(em,
						orgDto.getParentId(), provider);
				if (parentSeller == null) {
					throw new MeveoApiException("Parent seller with code="
							+ orgDto.getParentId() + " does not exists.");
				} else {
					Auditable auditable = new Auditable();
					auditable.setCreated(new Date());
					auditable.setCreator(currentUser);

					Seller newSeller = new Seller();
					newSeller.setActive(true);
					newSeller.setCode(orgDto.getOrganizationId());
					newSeller.setAuditable(auditable);
					newSeller.setProvider(provider);
					newSeller.setTradingCountry(tr);
					newSeller.setTradingCurrency(tc);
					newSeller.setDescription(orgDto.getName());
					newSeller.setSeller(parentSeller);
					sellerService.create(em, newSeller, currentUser, provider);
				}
			} else { // no parent seller

			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(orgDto.getOrganizationId())) {
				missingFields.add("Organization Id");
			}
			if (StringUtils.isBlank(orgDto.getCountryCode())) {
				missingFields.add("Country code");
			}
			if (StringUtils.isBlank(orgDto.getDefaultCurrencyCode())) {
				missingFields.add("Default currency code");
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
