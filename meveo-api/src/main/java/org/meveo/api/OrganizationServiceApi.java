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
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.exception.OrganizationAlreadyExistsException;
import org.meveo.api.exception.ParentSellerDoesNotExistsException;
import org.meveo.api.exception.TradingCountryDoesNotExistsException;
import org.meveo.api.exception.TradingCurrencyDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CreditCategoryEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OrganizationServiceApi extends BaseApi {

	@Inject
	private ParamBean paramBean;

	@Inject
	private SellerService sellerService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private CustomerBrandService customerBrandService;

	@Inject
	private CustomerCategoryService customerCategoryService;

	@Inject
	private CustomerService customerService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private UserAccountService userAccountService;

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
				throw new OrganizationAlreadyExistsException(
						orgDto.getOrganizationId());
			}

			TradingCountry tr = tradingCountryService.findByTradingCountryCode(
					orgDto.getCountryCode(), provider);
			if (tr == null) {
				throw new TradingCountryDoesNotExistsException(
						orgDto.getCountryCode());
			}

			TradingCurrency tc = tradingCurrencyService
					.findByTradingCurrencyCode(orgDto.getDefaultCurrencyCode(),
							provider);
			if (tc == null) {
				throw new TradingCurrencyDoesNotExistsException(
						orgDto.getDefaultCurrencyCode());
			}

			// with parent seller
			if (!StringUtils.isBlank(orgDto.getParentId())) {

				Seller parentSeller = sellerService.findByCode(em,
						orgDto.getParentId(), provider);

				if (parentSeller == null) {
					throw new ParentSellerDoesNotExistsException(
							orgDto.getParentId());
				} else {
					int caPaymentMethod = Integer
							.parseInt(paramBean
									.getProperty(
											"asp.api.default.customerAccount.paymentMethod",
											"1"));
					int creditCategory = Integer
							.parseInt(paramBean
									.getProperty(
											"asp.api.default.customerAccount.creditCategory",
											"5"));
					CustomerAccount customerAccount = new CustomerAccount();
					customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
					customerAccount.setPaymentMethod(PaymentMethodEnum
							.getValue(caPaymentMethod));
					customerAccount.setCreditCategory(CreditCategoryEnum
							.getValue(creditCategory));
					customerAccountService.create(em, customerAccount,
							currentUser, provider);

					int baPaymentMethod = Integer
							.parseInt(paramBean
									.getProperty(
											"asp.api.default.customerAccount.paymentMethod",
											"1"));
					BillingAccount billingAccount = new BillingAccount();
					billingAccount.setStatus(AccountStatusEnum.ACTIVE);
					billingAccount.setCustomerAccount(customerAccount);
					billingAccount.setPaymentMethod(PaymentMethodEnum
							.getValue(baPaymentMethod));
					billingAccount
							.setElectronicBilling(Boolean.valueOf(paramBean
									.getProperty(
											"asp.api.default.billingAccount.electronicBilling",
											"true")));
					billingAccountService.create(em, billingAccount,
							currentUser, provider);

					UserAccount userAccount = new UserAccount();
					userAccount.setStatus(AccountStatusEnum.ACTIVE);
					userAccount.setBillingAccount(billingAccount);
					userAccount.setCode(paramBean.getProperty(
							"asg.api.default", "_DEF_")
							+ orgDto.getOrganizationId());
					userAccountService.create(em, userAccount, currentUser,
							provider);

					CustomerBrand customerBrand = customerBrandService
							.findByCode(em, paramBean.getProperty(
									"asp.api.default.customer.brand", "DEMO"));
					CustomerCategory customerCategory = customerCategoryService
							.findByCode(paramBean.getProperty(
									"asp.api.default.customer.category",
									"Business"));
					Customer customer = new Customer();
					customer.setSeller(parentSeller);
					customer.setCustomerBrand(customerBrand);
					customer.setCustomerCategory(customerCategory);
					customer.getCustomerAccounts().add(customerAccount);
					customerService.create(em, customer, currentUser, provider);

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

			throw new MissingParameterException(sb.toString());
		}
	}

	public void update(OrganizationDto orgDto) throws MeveoApiException {
		if (!StringUtils.isBlank(orgDto.getOrganizationId())
				&& !StringUtils.isBlank(orgDto.getCountryCode())
				&& !StringUtils.isBlank(orgDto.getDefaultCurrencyCode())) {

			Provider provider = providerService
					.findById(orgDto.getProviderId());
			User currentUser = userService.findById(orgDto.getCurrentUserId());

			TradingCountry tr = tradingCountryService.findByTradingCountryCode(
					orgDto.getCountryCode(), provider);

			TradingCurrency tc = tradingCurrencyService
					.findByTradingCurrencyCode(orgDto.getDefaultCurrencyCode(),
							provider);
			if (tc == null) {
				throw new TradingCurrencyDoesNotExistsException(
						orgDto.getDefaultCurrencyCode());
			}

			Seller seller = sellerService.findByCode(em,
					orgDto.getOrganizationId(), provider);

			if (!sellerService.hasChild(em, seller, provider)) {
				if (tr == null) {
					seller.setTradingCountry(tr);
				}

				seller.setTradingCurrency(tc);
				sellerService.update(em, seller, currentUser);
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

			throw new MissingParameterException(sb.toString());
		}
	}

}
