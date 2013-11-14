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
import org.meveo.api.exception.SellerAlreadyExistsException;
import org.meveo.api.exception.SellerDoesNotExistsException;
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
				throw new SellerAlreadyExistsException(
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

			Seller parentSeller = null;
			// with parent seller
			if (!StringUtils.isBlank(orgDto.getParentId())) {
				parentSeller = sellerService.findByCode(em,
						orgDto.getParentId(), provider);
			}

			String customerPrefix = paramBean.getProperty(
					"asp.api.default.customer.prefix", "CUST_");
			String customerAccountPrefix = paramBean.getProperty(
					"asp.api.default.customerAccount.prefix", "CA_");
			String billingAccountPrefix = paramBean.getProperty(
					"asp.api.default.billingAccount.prefix", "BA_");
			String userAccountPrefix = paramBean.getProperty(
					"asp.api.default.userAccount.prefix", "UA_");

			int caPaymentMethod = Integer.parseInt(paramBean.getProperty(
					"asp.api.default.customerAccount.paymentMethod", "1"));
			int creditCategory = Integer.parseInt(paramBean.getProperty(
					"asp.api.default.customerAccount.creditCategory", "5"));

			int baPaymentMethod = Integer.parseInt(paramBean.getProperty(
					"asp.api.default.customerAccount.paymentMethod", "1"));

			CustomerBrand customerBrand = customerBrandService.findByCode(em,
					paramBean.getProperty("asp.api.default.customer.brand",
							"DEMO"));
			CustomerCategory customerCategory = customerCategoryService
					.findByCode(paramBean.getProperty(
							"asp.api.default.customer.category", "Business"));

			if (parentSeller != null) {
				CustomerAccount customerAccount = new CustomerAccount();
				customerAccount.setCode(customerAccountPrefix
						+ orgDto.getOrganizationId());
				customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
				customerAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(caPaymentMethod));
				customerAccount.setCreditCategory(CreditCategoryEnum
						.getValue(creditCategory));
				customerAccountService.create(em, customerAccount, currentUser,
						provider);

				BillingAccount billingAccount = new BillingAccount();
				billingAccount.setCode(billingAccountPrefix
						+ orgDto.getOrganizationId());
				billingAccount.setStatus(AccountStatusEnum.ACTIVE);
				billingAccount.setCustomerAccount(customerAccount);
				billingAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(baPaymentMethod));
				billingAccount
						.setElectronicBilling(Boolean.valueOf(paramBean
								.getProperty(
										"asp.api.default.billingAccount.electronicBilling",
										"true")));
				billingAccountService.create(em, billingAccount, currentUser,
						provider);

				UserAccount userAccount = new UserAccount();
				userAccount.setCode(userAccountPrefix
						+ orgDto.getOrganizationId());
				userAccount.setStatus(AccountStatusEnum.ACTIVE);
				userAccount.setBillingAccount(billingAccount);
				userAccount.setCode(paramBean.getProperty("asg.api.default",
						"_DEF_") + orgDto.getOrganizationId());
				userAccountService.create(em, userAccount, currentUser,
						provider);

				Customer customer = new Customer();
				customer.setCode(customerPrefix + orgDto.getOrganizationId());
				customer.setSeller(parentSeller);
				customer.setCustomerBrand(customerBrand);
				customer.setCustomerCategory(customerCategory);
				customer.getCustomerAccounts().add(customerAccount);
				customerService.create(em, customer, currentUser, provider);

				Auditable auditable = new Auditable();
				auditable.setCreated(new Date());
				auditable.setCreator(currentUser);

				Seller newSeller = new Seller();
				newSeller.setSeller(parentSeller);
				newSeller.setActive(true);
				newSeller.setCode(orgDto.getOrganizationId());
				newSeller.setAuditable(auditable);
				newSeller.setProvider(provider);
				newSeller.setTradingCountry(tr);
				newSeller.setTradingCurrency(tc);
				newSeller.setDescription(orgDto.getName());
				sellerService.create(em, newSeller, currentUser, provider);

				// add user account to parent's billing account
				String parentBillingAccountCode = billingAccountPrefix
						+ parentSeller.getCode();
				BillingAccount parentBillingAccount = billingAccountService
						.findByCode(em, parentBillingAccountCode, provider);
				if (parentBillingAccount != null) {
					UserAccount parentUserAccount = new UserAccount();
					parentUserAccount.setCode(userAccountPrefix
							+ orgDto.getOrganizationId());
					parentUserAccount.setStatus(AccountStatusEnum.ACTIVE);
					parentUserAccount.setBillingAccount(parentBillingAccount);
					parentUserAccount.setCode(paramBean
							.getProperty(
									"asg.api.default.organization.userAccount",
									"USER_")
							+ orgDto.getOrganizationId());
					userAccountService.create(em, parentUserAccount,
							currentUser, provider);
				}
			} else {
				CustomerAccount customerAccount = new CustomerAccount();
				customerAccount.setCode(customerAccountPrefix
						+ orgDto.getOrganizationId());
				customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
				customerAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(caPaymentMethod));
				customerAccount.setCreditCategory(CreditCategoryEnum
						.getValue(creditCategory));
				customerAccountService.create(em, customerAccount, currentUser,
						provider);

				BillingAccount billingAccount = new BillingAccount();
				billingAccount.setCode(billingAccountPrefix
						+ orgDto.getOrganizationId());
				billingAccount.setStatus(AccountStatusEnum.ACTIVE);
				billingAccount.setCustomerAccount(customerAccount);
				billingAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(baPaymentMethod));
				billingAccount
						.setElectronicBilling(Boolean.valueOf(paramBean
								.getProperty(
										"asp.api.default.billingAccount.electronicBilling",
										"true")));
				billingAccountService.create(em, billingAccount, currentUser,
						provider);

				Customer customer = new Customer();
				customer.setCode(customerPrefix + orgDto.getOrganizationId());
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
				sellerService.create(em, newSeller, currentUser, provider);
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(orgDto.getOrganizationId())) {
				missingFields.add("organizationId");
			}
			if (StringUtils.isBlank(orgDto.getCountryCode())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(orgDto.getDefaultCurrencyCode())) {
				missingFields.add("defaultCurrencyCode");
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

			if (seller == null) {
				throw new SellerDoesNotExistsException(
						orgDto.getOrganizationId());
			}

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
				missingFields.add("organizationId");
			}
			if (StringUtils.isBlank(orgDto.getCountryCode())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(orgDto.getDefaultCurrencyCode())) {
				missingFields.add("defaultCurrencyCode");
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

	public void remove(String organizationId, Long providerId)
			throws MeveoApiException {
		if (!StringUtils.isBlank(organizationId)) {
			Provider provider = providerService.findById(providerId);

			Customer customer = customerService.findByCode(em, organizationId,
					provider);
			if (customer != null) {
				customerService.remove(customer);
			}

			String userAccountPrefix = paramBean.getProperty(
					"asg.api.default.organization.userAccount", "USER_");
			UserAccount userAccount = userAccountService.findByCode(em,
					userAccountPrefix + organizationId, provider);
			if (userAccount != null) {
				userAccountService.remove(em, userAccount);
			}

			Seller seller = sellerService.findByCode(em, organizationId,
					provider);

			if (seller == null) {
				throw new SellerDoesNotExistsException(organizationId);
			} else {
				sellerService.remove(em, seller);
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(organizationId)) {
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
}
