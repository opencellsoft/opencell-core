package org.meveo.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.AccountOperationDto;
import org.meveo.api.dto.CustomerAccountDto;
import org.meveo.api.dto.MatchingAmountDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CustomerAccountApi extends BaseApi {

	@Inject
	private CustomerAccountService customerAccountService;

	public CustomerAccountDto getCustomerAccount(String customerAccountCode,
			User currentUser) throws Exception {

		CustomerAccountDto customerAccountDto = new CustomerAccountDto();
		if (!StringUtils.isBlank(customerAccountCode)
			) {
			Provider provider = currentUser.getProvider();
			CustomerAccount customerAccount = customerAccountService
					.findByCode(em, customerAccountCode, provider);
			if (customerAccount == null) {
				throw new BusinessException(
						"Cannot find customer account with code="
								+ customerAccountCode);
			}

			customerAccountDto
					.setStatus(customerAccount.getStatus().toString() != null ? customerAccount
							.getStatus().toString() : null);
			customerAccountDto.setPaymentMethod(customerAccount
					.getPaymentMethod().toString() != null ? customerAccount
					.getPaymentMethod().toString() : null);
			customerAccountDto.setCreditCategory(customerAccount
					.getCreditCategory().toString() != null ? customerAccount
					.getCreditCategory().toString() : null);

			customerAccountDto.setDateStatus(customerAccount.getDateStatus());
			customerAccountDto.setDateDunningLevel(customerAccount
					.getDateDunningLevel());
			customerAccountDto.setEmail(customerAccount.getContactInformation()
					.getEmail() != null ? customerAccount
					.getContactInformation().getEmail() : null);
			customerAccountDto.setPhone(customerAccount.getContactInformation()
					.getPhone() != null ? customerAccount
					.getContactInformation().getPhone() : null);
			customerAccountDto
					.setMobile(customerAccount.getContactInformation()
							.getMobile() != null ? customerAccount
							.getContactInformation().getMobile() : null);
			customerAccountDto.setFax(customerAccount.getContactInformation()
					.getFax() != null ? customerAccount.getContactInformation()
					.getFax() : null);

			customerAccountDto.setCustomerCode(customerAccount.getCustomer()
					.getCode());
			customerAccountDto.setDunningLevel(customerAccount
					.getDunningLevel().toString() != null ? customerAccount
					.getDunningLevel().toString() : null);
			customerAccountDto.setMandateIdentification(customerAccount
					.getMandateIdentification());
			customerAccountDto.setMandateDate(customerAccount.getMandateDate());

			List<AccountOperation> accountOperations = customerAccount
					.getAccountOperations();
			AccountOperationDto accountOperationDto = new AccountOperationDto();

			for (AccountOperation accountOp : accountOperations) {
				accountOperationDto.setDueDate(accountOp.getDueDate());
				accountOperationDto.setType(accountOp.getType());
				accountOperationDto.setTransactionDate(accountOp
						.getTransactionDate());
				accountOperationDto
						.setTransactionCategory(accountOp
								.getTransactionCategory().toString() != null ? accountOp
								.getTransactionCategory().toString() : null);
				accountOperationDto.setReference(accountOp.getReference());
				accountOperationDto.setAccountCode(accountOp.getAccountCode());
				accountOperationDto.setAccountCodeClientSide(accountOp
						.getAccountCodeClientSide());
				accountOperationDto.setAmount(accountOp.getAmount());
				accountOperationDto.setMatchingAmount(accountOp
						.getMatchingAmount());
				accountOperationDto.setUnMatchingAmount(accountOp
						.getUnMatchingAmount());
				accountOperationDto.setMatchingStatus(accountOp
						.getMatchingStatus().toString() != null ? accountOp
						.getMatchingStatus().toString() : null);
				accountOperationDto.setOccCode(accountOp.getOccCode());
				accountOperationDto.setOccDescription(accountOp
						.getOccDescription());

				List<MatchingAmount> matchingAmounts = accountOp
						.getMatchingAmounts();
				MatchingAmountDto matchingAmountDto = new MatchingAmountDto();
				for (MatchingAmount matchingAmount : matchingAmounts) {
					matchingAmountDto.setMatchingCode(matchingAmount
							.getMatchingCode().getCode());
					matchingAmountDto.setMatchingAmount(matchingAmount
							.getMatchingAmount());
					accountOperationDto.addMatchingAmounts(matchingAmountDto);
				}
				customerAccountDto.addAccountOperations(accountOperationDto);
			}
			BigDecimal balance = customerAccountService
					.customerAccountBalanceDue(null, customerAccount.getCode(),
							new Date());
			if (balance == null) {
				throw new BusinessException(
						"account balance calculation failed");
			}
			customerAccountDto.setBalance(balance);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(customerAccountCode)) {
				missingFields.add("CustomerAccountCode");
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

		return customerAccountDto;
	}

}
