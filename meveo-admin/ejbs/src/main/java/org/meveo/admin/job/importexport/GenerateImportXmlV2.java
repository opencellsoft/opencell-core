package org.meveo.admin.job.importexport;

import java.io.File;
import java.util.Date;

import org.meveo.commons.utils.JAXBUtils;
import org.meveo.model.jaxb.account.BillingAccount;
import org.meveo.model.jaxb.account.BillingAccounts;
import org.meveo.model.jaxb.account.Name;
import org.meveo.model.jaxb.account.UserAccount;
import org.meveo.model.jaxb.account.UserAccounts;
import org.meveo.model.jaxb.customer.Customer;
import org.meveo.model.jaxb.customer.CustomerAccount;
import org.meveo.model.jaxb.customer.CustomerAccounts;
import org.meveo.model.jaxb.customer.Customers;
import org.meveo.model.jaxb.customer.Seller;
import org.meveo.model.jaxb.customer.Sellers;
import org.meveo.model.jaxb.subscription.ServiceInstance;
import org.meveo.model.jaxb.subscription.Services;
import org.meveo.model.jaxb.subscription.Status;
import org.meveo.model.jaxb.subscription.Subscription;
import org.meveo.model.jaxb.subscription.Subscriptions;
import org.meveo.model.shared.DateUtils;

public class GenerateImportXmlV2 {

	/**
	 * @param args
	 */
	/************************** configuration properties ***********************************/
	private final int MAX_SELLERS = 1;
	private final int MAX_CUSTOMERS = 1;
	private final int MAX_CUSTOMER_ACCOUNTS = 1;

	private final int MAX_BILLING_ACCOUNTS = 1;
	private final int MAX_USER_ACCOUNTS = 1;

	private final int MAX_SUBSCRIPTIONS = 10;

	private final boolean IGNORE_ACCOUNTS_IMPORT = false;
	private final boolean IGNORE_SUBSCRIPTION_IMPORT = false;

	private static String customersFile = "c:\\temp\\CUSTOMER_JOB.xml";
	private static String accountsFile = "c:\\temp\\ACCOUNT_JOB.xml";
	private static String subscriptionsFile = "c:\\temp\\SUB_JOB.xml";
	private static String customerBrand = "DEFAULT";
	private static String customerCategory = "CLIENT";
	private static String serviceCode = "SERV1";
	private static String creditCategory = "VIP";
	private static String offerCode = "OFF1";
	private static String billingCycle = "CYC_INV_MT_1";
	private static String paymentMethod = "CHECK";

	/***********************************************************************/

	public static void main(String[] args) {
		new GenerateImportXmlV2();
	}

	public GenerateImportXmlV2() {
		System.out.println("import START");

		generateCustomers();

		System.out.println("Import DONE.");
	}

	private void generateCustomers() {
		System.out.println("Preparing to import " + MAX_SELLERS + " sellers...");

		try {

			ServiceInstance serviceInstance = new ServiceInstance();
			serviceInstance.setCode(serviceCode);
			serviceInstance.setSubscriptionDate(DateUtils.formatDateWithPattern(new Date(), "Y-MM-d"));
			Status status = new Status();
			status.setDate(DateUtils.formatDateWithPattern(new Date(), "Y-MM-d"));
			status.setValue("ACTIVE");
			serviceInstance.setStatus(status);
			serviceInstance.setQuantity("1");

			Sellers sellers = new Sellers();
			BillingAccounts billingAccounts = new BillingAccounts();
			Subscriptions subscriptions = new Subscriptions();

			for (int i = 0; i < MAX_SELLERS; i++) {
				System.out.println("processing seller # " + i);

				Seller seller = new Seller();
				seller.setCode("JOB_SELLER" + i);
				seller.setDescription("JOB_SELLER" + i);
				seller.setTradingCountryCode("FR");
				seller.setTradingCurrencyCode("EUR");
				seller.setTradingLanguageCode("FRA");

				Customers customers = new Customers();
				for (int j = 0; j < MAX_CUSTOMERS; j++) {
					Customer customer = new Customer();
					customer.setCode("JOB_CUST" + i + "_" + j);
					customer.setDesCustomer("JOB_CUST" + i + "_" + j);
					customer.setCustomerCategory(customerCategory);
					customer.setCustomerBrand(customerBrand);

					CustomerAccounts customerAccounts = new CustomerAccounts();
					for (int k = 0; k < MAX_CUSTOMER_ACCOUNTS; k++) {
						CustomerAccount customerAccount = new CustomerAccount();
						customerAccount.setCode("JOB_CA" + i + "_" + j + "_" + k);
						customerAccount.setDescription("JOB_CA" + i + "_" + j + "_" + k);
						customerAccount.setTradingCurrencyCode("EUR");
						customerAccount.setCreditCategory(creditCategory);
						Name name = new Name();
						name.setName("JOB_NAME" + i + "_" + j + "_" + k);
						customerAccount.setName(name);

						if (IGNORE_ACCOUNTS_IMPORT) {
							customerAccounts.getCustomerAccount().add(customerAccount);
							continue;
						}

						for (int l = 0; l < MAX_BILLING_ACCOUNTS; l++) {
							BillingAccount billingAccount = new BillingAccount();
							billingAccount.setCode("JOB_BA" + i + "_" + j + "_" + k + "_" + l);
							billingAccount.setDescription("JOB_BA" + i + "_" + j + "_" + k + "_" + l);
							billingAccount.setCustomerAccountId(customerAccount.getCode());
							billingAccount.setBillingCycle(billingCycle);
							billingAccount.setTradingCountryCode("FR");
							billingAccount.setTradingLanguageCode("FRA");
							billingAccount.setPaymentMethod(paymentMethod);

							UserAccounts userAccounts = new UserAccounts();
							for (int m = 0; m < MAX_USER_ACCOUNTS; m++) {
								UserAccount userAccount = new UserAccount();
								userAccount.setCode("JOB_UA" + i + "_" + j + "_" + k + "_" + l + "_" + m);
								userAccount.setDescription("JOB_UA" + i + "_" + j + "_" + k + "_" + l + "_" + m);

								if (IGNORE_SUBSCRIPTION_IMPORT) {
									userAccounts.getUserAccount().add(userAccount);
									continue;
								}

								for (int n = 0; n < MAX_SUBSCRIPTIONS; n++) {
									Subscription subscription = new Subscription();
									subscription.setCode("JOB_SUB" + i + "_" + j + "_" + k + "_" + l + "_" + m + "_"
											+ n);
									subscription.setDescription("JOB_SUB" + i + "_" + j + "_" + k + "_" + l + "_" + m
											+ "_" + n);
									subscription.setUserAccountId(userAccount.getCode());
									subscription.setOfferCode(offerCode);
									subscription.setSubscriptionDate(DateUtils.formatDateWithPattern(new Date(),
											"Y-MM-d"));
									subscription.setUserAccountId(userAccount.getCode());
									Status statuSub = new Status();
									statuSub.setDate(new Date().toString());
									statuSub.setValue("ACTIVE");
									subscription.setStatus(statuSub);
									Services services = new Services();
									services.getServiceInstance().add(serviceInstance);
									subscription.setServices(services);

									subscriptions.getSubscription().add(subscription);
								}

								userAccounts.getUserAccount().add(userAccount);
							}

							billingAccount.setUserAccounts(userAccounts);
							billingAccounts.getBillingAccount().add(billingAccount);
						}

						customerAccounts.getCustomerAccount().add(customerAccount);
					}

					customer.setCustomerAccounts(customerAccounts);
					customers.getCustomer().add(customer);
				}

				seller.setCustomers(customers);
				sellers.getSeller().add(seller);
			}

			JAXBUtils.marshaller(sellers, new File(customersFile));
			JAXBUtils.marshaller(billingAccounts, new File(accountsFile));
			JAXBUtils.marshaller(subscriptions, new File(subscriptionsFile));

		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}
}
