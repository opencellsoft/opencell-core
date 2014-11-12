package org.meveo.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.WalletBalanceDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.WalletReservationService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class WalletBalanceApi extends BaseApi {

	@Inject
	private WalletReservationService walletReservationService;

	public BigDecimal getCurrentAmount(WalletBalanceDto walletBalance,
			Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(walletBalance.getSellerCode())
				&& !StringUtils.isBlank(walletBalance.getUserAccountCode())) {
			try {
				if (walletBalance.isAmountWithTax()) {
					return walletReservationService.getCurrentBalanceWithTax(
							em, provider, walletBalance.getSellerCode(),
							walletBalance.getUserAccountCode(),
							walletBalance.getStartDate(),
							walletBalance.getEndDate());
				} else {
					return walletReservationService
							.getCurrentBalanceWithoutTax(em, provider,
									walletBalance.getSellerCode(),
									walletBalance.getUserAccountCode(),
									walletBalance.getStartDate(),
									walletBalance.getEndDate());
				}
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();
			if (StringUtils.isBlank(walletBalance.getSellerCode())) {
				missingFields.add("sellerCode");
			}
			if (StringUtils.isBlank(walletBalance.getUserAccountCode())) {
				missingFields.add("userAccountCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public BigDecimal getReservedAmount(WalletBalanceDto walletBalance,
			Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(walletBalance.getSellerCode())
				&& !StringUtils.isBlank(walletBalance.getUserAccountCode())) {
			try {
				if (walletBalance.isAmountWithTax()) {
					return walletReservationService.getReservedBalanceWithTax(
							em, provider, walletBalance.getSellerCode(),
							walletBalance.getUserAccountCode(),
							walletBalance.getStartDate(),
							walletBalance.getEndDate());
				} else {
					return walletReservationService
							.getReservedBalanceWithoutTax(em, provider,
									walletBalance.getSellerCode(),
									walletBalance.getUserAccountCode(),
									walletBalance.getStartDate(),
									walletBalance.getEndDate());
				}
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();
			if (StringUtils.isBlank(walletBalance.getSellerCode())) {
				missingFields.add("sellerCode");
			}
			if (StringUtils.isBlank(walletBalance.getUserAccountCode())) {
				missingFields.add("userAccountCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public BigDecimal getOpenAmount(WalletBalanceDto walletBalance,
			Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(walletBalance.getSellerCode())
				&& !StringUtils.isBlank(walletBalance.getUserAccountCode())) {

			try {
				if (walletBalance.isAmountWithTax()) {
					return walletReservationService.getOpenBalanceWithoutTax(
							em, provider, walletBalance.getSellerCode(),
							walletBalance.getUserAccountCode(),
							walletBalance.getStartDate(),
							walletBalance.getEndDate());
				} else {
					return walletReservationService.getOpenBalanceWithTax(em,
							provider, walletBalance.getSellerCode(),
							walletBalance.getUserAccountCode(),
							walletBalance.getStartDate(),
							walletBalance.getEndDate());
				}
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(walletBalance.getSellerCode())) {
				missingFields.add("sellerCode");
			}
			if (StringUtils.isBlank(walletBalance.getUserAccountCode())) {
				missingFields.add("userAccountCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

}
