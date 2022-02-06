package org.meveo.service.script;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.crm.impl.AccountEntitySearchService;

import com.opencsv.CSVWriter;

public class ReportExtractCustomerAccount extends Script  {
	


	private final String CLIENT = "CLIENT";
	private final String FILE_NAME="customerAccount.csv";

	private final AccountEntitySearchService accountentityService = (AccountEntitySearchService) getServiceInterface(
			AccountEntitySearchService.class.getSimpleName());

	@Override
	public void init(Map<String, Object> methodContext) throws BusinessException {
	}

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		List<String> customAccountValues=new ArrayList<>();
		List<Customer> customers = getTablesCustomerAccounts();
		if (customers == null || customers.size() == 0) {
			return;
		}
		for (Customer customer : customers) {
			if (CLIENT.equals(customer.getCustomerCategory().getCode()))
				for (CustomerAccount customerAccount : customer.getCustomerAccounts()) {
				if(customer.getCode()!=null) {
					customAccountValues.add(customer.getCode());
				}
				if(customerAccount.getCode()!=null){
					customAccountValues.add(customerAccount.getCode());
				}
				if(customerAccount.getDescription()!=null) {
					customAccountValues.add(customerAccount.getDescription());
				}
				if(customerAccount.getPrimaryContact()!=null) {
					customAccountValues.add(customerAccount.getPrimaryContact().toString());
				}
				if (customerAccount.getExternalRef1() != null) {
					customAccountValues.add(customerAccount.getExternalRef1());
				}
				if(customerAccount.getTradingCurrency()!=null) {
					customAccountValues.add(customerAccount.getTradingCurrency().toString());
				}
				if(customerAccount.getTradingLanguage()!=null) {
					customAccountValues.add(customerAccount.getTradingLanguage().toString());
				}
				if(customerAccount.getJobTitle()!=null) {
					customAccountValues.add(customerAccount.getJobTitle());
				}
				if(customerAccount.getName()!=null) {
					customAccountValues.add(customerAccount.getName().toString());
				}
				if(customerAccount.getDefaultLevel()!=null) {
					customAccountValues.add(customerAccount.getDefaultLevel().toString());
				}
				if(customerAccount.getCreditCategory()!=null) {
					customAccountValues.add(customerAccount.getCreditCategory().toString());
				}
				if(customerAccount.getVatNo()!=null) {
					customAccountValues.add(customerAccount.getVatNo());
				}
				if(customerAccount.getRegistrationNo()!=null) {
					customAccountValues.add(customerAccount.getRegistrationNo());
				}
				if(customerAccount.getDunningLevel()!=null) {
					customAccountValues.add(customerAccount.getDunningLevel().toString());
				}
				if(customerAccount.getDateDunningLevel()!=null) {
					customAccountValues.add(customerAccount.getDateDunningLevel().toString());
				}

				if(customerAccount.getContactInformation().getEmail()!=null) {
					customAccountValues.add(customerAccount.getContactInformation().getEmail());
				}
				if(customerAccount.getContactInformation().getPhone()!=null) {
					customAccountValues.add(customerAccount.getContactInformation().getPhone());
				}
				if(customerAccount.getContactInformation().getMobile()!=null) {
					customAccountValues.add(customerAccount.getContactInformation().getMobile());
				}
				if(customerAccount.getAddress().getAddress1()!=null) {
					customAccountValues.add(customerAccount.getAddress().getAddress1());
				}
				if(customerAccount.getAddress().getAddress2()!=null) {
					customAccountValues.add(customerAccount.getAddress().getAddress2());
				}
				if(customerAccount.getAddress().getAddress3()!=null) {
					customAccountValues.add(customerAccount.getAddress().getAddress3());
				}
				if(customerAccount.getAddress().getCountry()!=null) {
					customAccountValues.add(customerAccount.getAddress().getCountry().toString());
				}
				if(customerAccount.getPaymentMethods()!=null) {
					customAccountValues.add(customerAccount.getPaymentMethods().toString());
				}
				}
			
		
		
		}
		
		try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_NAME))) {
			String[] stockArr = new String[customAccountValues.size()];
			stockArr = customAccountValues.toArray(stockArr);
			writer.writeNext(stockArr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<Customer> getTablesCustomerAccounts() {
		List<Customer> list = accountentityService.getEntityManager()
				.createQuery("select c from  Customer c  ", Customer.class).getResultList();
		return list;
	}


}
