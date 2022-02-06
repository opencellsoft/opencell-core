package org.meveo.service.script;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.crm.impl.AccountEntitySearchService;

import com.opencsv.CSVWriter;

public class ReportExtractCfvalue extends Script {

	private final String CLIENT = "CLIENT";
	private final String FILE_NAME="subsciption.csv";

	private final AccountEntitySearchService accountentityService = (AccountEntitySearchService) getServiceInterface(
			AccountEntitySearchService.class.getSimpleName());

	@Override
	public void init(Map<String, Object> methodContext) throws BusinessException {
	}

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		List<String> keycfValues=new ArrayList<>();
		List<Customer> customers = getTablesWithCfvalues();
		if (customers == null || customers.size() == 0) {
			return;
		}
		for (Customer customer : customers) {
			if (CLIENT.equals(customer.getCustomerCategory().getCode()))
				for (CustomerAccount account : customer.getCustomerAccounts()) {
					for (BillingAccount billingAccount : account.getBillingAccounts()) {
						for (UserAccount userAccount : billingAccount.getUsersAccounts()) {
							for (Subscription subscription : userAccount.getSubscriptions()) {
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_brandCode") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode().get("CF_SUB_brandCode")
											.toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_serviceEndDate") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_serviceEndDate").toString());
								}
								if (subscription.getCfValues().getValuesByCode()
										.get("CF_SUB_serviceStartDate") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_serviceStartDate").toString());
								}
								if (subscription.getCode() != null) {
									keycfValues.add(subscription.getCode());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_lcvdCode") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode().get("CF_SUB_lcvdCode")
											.toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_retailerId") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_retailerId").toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_networkId") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode().get("CF_SUB_networkId")
											.toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_contractType") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_contractType").toString());
								}
								if (subscription.getCfValues().getValuesByCode()
										.get("CF_SUB_originBackOffice") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_originBackOffice").toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_balloisId") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode().get("CF_SUB_balloisId")
											.toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_immat") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode().get("CF_SUB_immat")
											.toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_companyMgmCode") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_companyMgmCode").toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_companyMdpCode") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_companyMdpCode").toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_serviceCode") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_serviceCode").toString());
								}
								if (subscription.getCfValues().getValuesByCode()
										.get("CF_SUB_serviceDescription") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_serviceDescription").toString());
								}
								if (subscription.getCfValues().getValuesByCode()
										.get("CF_SUB_serviceDuration") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_serviceDuration").toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_serviceVatRate") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_serviceVatRate").toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_CODIFICATION") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_CODIFICATION").toString());
								}
								if (subscription.getCfValues().getValuesByCode()
										.get("CF_SUB_serviceFirstPaymentDate") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_serviceFirstPaymentDate").toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_balance") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode().get("CF_SUB_balance")
											.toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_balanceJV") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode().get("CF_SUB_balanceJV")
											.toString());
								}
								if (subscription.getCfValues().getValuesByCode()
										.get("CF_SUB_last_balanceClient") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_last_balanceClient").toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_last_balance") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_last_balance").toString());
								}
								if (subscription.getCfValues().getValuesByCode()
										.get("CF_SUB_last_balanceBrand") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_last_balanceBrand").toString());
								}
								if (subscription.getCfValues().getValuesByCode().get("CF_SUB_last_balanceJV") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_last_balanceJV").toString());
								}
								if (subscription.getCfValues().getValuesByCode()
										.get("CF_SUB_FL_ConversionRate") != null) {
									keycfValues.add(subscription.getCfValues().getValuesByCode()
											.get("CF_SUB_FL_ConversionRate").toString());
								}

							}
						}
					}
				}
		}
		try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_NAME))) {
			String[] stockArr = new String[keycfValues.size()];
			stockArr = keycfValues.toArray(stockArr);
			writer.writeNext(stockArr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<Customer> getTablesWithCfvalues() {
		List<Customer> list = accountentityService.getEntityManager()
				.createQuery("select c from  Customer c ", Customer.class).getResultList();
		return list;
	}
}
