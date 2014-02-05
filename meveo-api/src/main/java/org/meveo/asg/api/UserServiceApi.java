package org.meveo.asg.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.UserDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.asg.api.model.EntityCodeEnum;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.UserAccountService;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class UserServiceApi extends BaseAsgApi {

	ParamBean paramBean = ParamBean.getInstance();

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private UserAccountService userAccountService;

	public void create(UserDto userDto) throws MeveoApiException {
		if (!StringUtils.isBlank(userDto.getUserId())
				&& !StringUtils.isBlank(userDto.getName())
				&& !StringUtils.isBlank(userDto.getOrganizationId())) {

			Provider provider = providerService.findById(userDto
					.getProviderId());
			User currentUser = userService.findById(userDto.getCurrentUserId());

			try {
				userDto.setUserId(asgIdMappingService.getNewCode(em,
						userDto.getUserId(), EntityCodeEnum.U));

				userDto.setOrganizationId(asgIdMappingService.getMeveoCode(em,
						userDto.getOrganizationId(), EntityCodeEnum.ORG));
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

			String billingAccountPrefix = paramBean.getProperty(
					"asp.api.default.billingAccount.prefix", "BA_");

			BillingAccount billingAccount = billingAccountService.findByCode(
					billingAccountPrefix + userDto.getOrganizationId(),
					provider);

			if (billingAccount == null) {
				throw new MeveoApiException("Billing account with code="
						+ userDto.getOrganizationId() + " does not exists.");
			} else {
				Auditable auditable = new Auditable();
				auditable.setCreator(currentUser);
				auditable.setCreated(new Date());

				UserAccount userAccount = new UserAccount();
				userAccount.setActive(true);
				userAccount.setAuditable(auditable);
				userAccount.setBillingAccount(billingAccount);
				userAccount.setCode(userDto.getUserId());
				userAccount.setDescription(userDto.getName());
				userAccount.setProvider(provider);
				userAccountService.create(em, userAccount, currentUser,
						provider);
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(userDto.getUserId())) {
				missingFields.add("userId");
			}
			if (StringUtils.isBlank(userDto.getName())) {
				missingFields.add("name");
			}
			if (StringUtils.isBlank(userDto.getOrganizationId())) {
				missingFields.add("organizationId");
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

	public void update(UserDto userDto) throws MeveoApiException {
		if (!StringUtils.isBlank(userDto.getUserId())
				&& !StringUtils.isBlank(userDto.getName())) {

			Provider provider = providerService.findById(userDto
					.getProviderId());
			User currentUser = userService.findById(userDto.getCurrentUserId());

			try {
				userDto.setUserId(asgIdMappingService.getMeveoCode(em,
						userDto.getUserId(), EntityCodeEnum.U));
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

			UserAccount userAccount = userAccountService.findByCode(em,
					userDto.getUserId(), provider);
			if (userAccount == null) {
				throw new MeveoApiException("User account with code="
						+ userDto.getUserId() + " does not exists.");
			} else {
				userAccount.setDescription(userDto.getName());
				userAccountService.update(em, userAccount, currentUser);
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(userDto.getUserId())) {
				missingFields.add("User Id");
			}
			if (StringUtils.isBlank(userDto.getName())) {
				missingFields.add("Name");
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

	public void remove(Long providerId, String userId) throws MeveoApiException {

		if (!StringUtils.isBlank(userId)) {
			Provider provider = providerService.findById(providerId);

			try {
				userId = asgIdMappingService.getMeveoCode(em, userId,
						EntityCodeEnum.U);
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

			UserAccount userAccount = userAccountService.findByCode(em, userId,
					provider);

			if (userAccount == null) {
				throw new MeveoApiException("User account with code=" + userId
						+ " does not exists.");
			} else {
				userAccountService.remove(em, userAccount);
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			missingFields.add("User Id");

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
