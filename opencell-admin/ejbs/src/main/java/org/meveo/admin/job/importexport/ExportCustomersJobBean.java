package org.meveo.admin.job.importexport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.account.Address;
import org.meveo.model.jaxb.account.Name;
import org.meveo.model.jaxb.customer.CustomFields;
import org.meveo.model.jaxb.customer.CustomerAccount;
import org.meveo.model.jaxb.customer.CustomerAccounts;
import org.meveo.model.jaxb.customer.Customers;
import org.meveo.model.jaxb.customer.Sellers;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

@Stateless
public class ExportCustomersJobBean {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

    @Inject
    private Logger log;

    @Inject
    private CustomerService customerService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private JobExecutionService jobExecutionService;

    /** paramBeanFactory */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    private Sellers sellers;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, String parameter) {
        String exportDir = paramBeanFactory.getChrootDir() + File.separator + "exports" + File.separator + "customers" + File.separator;
        log.info("exportDir=" + exportDir);
        File dir = new File(exportDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp = sdf.format(new Date());
        List<Seller> sellersInDB = customerService.listSellersWithCustomers();
        sellers = new Sellers(sellersInDB);// ,param.getProperty("connectorCRM.dateFormat",
                                           // "yyyy-MM-dd"));
        for (org.meveo.model.jaxb.customer.Seller seller : sellers.getSeller()) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            List<Customer> customers = customerService.listBySellerCode(seller.getCode());
            seller.setCustomers(customersToDto(customers));
        }
        try {
            JAXBUtils.marshaller(sellers, new File(dir + File.separator + "CUSTOMER_" + timestamp + ".xml"));
        } catch (JAXBException e) {
            log.error("Failed to export customers job", e);
        }

    }

    private Customers customersToDto(List<org.meveo.model.crm.Customer> customerList) {
        Customers dto = new Customers();
        if (customerList != null) {
            for (org.meveo.model.crm.Customer cust : customerList) {
                dto.getCustomer().add(customerToDto(cust));
            }
        }

        return dto;
    }

    private org.meveo.model.jaxb.customer.Customer customerToDto(org.meveo.model.crm.Customer cust) {
        org.meveo.model.jaxb.customer.Customer dto = new org.meveo.model.jaxb.customer.Customer();
        if (cust != null) {
            dto.setDesCustomer(cust.getDescription());
            dto.setCode(cust.getCode());
            dto.setCustomerCategory(cust.getCustomerCategory() == null ? "" : cust.getCustomerCategory().getCode());
            dto.setCustomerBrand(cust.getCustomerBrand() == null ? "" : cust.getCustomerBrand().getCode());
            if (cust.getCfValues() != null) {
                dto.setCustomFields(CustomFields.toDTO(cust.getCfValues().getValuesByCode()));
            }
            if (cust.getAddress() != null) {
                dto.setAddress(new Address(cust.getAddress()));
            }
            if (cust.getName() != null) {
                dto.setName(new Name(cust.getName()));
            }
            dto.setCustomerAccounts(customerAccountsToDto(cust.getCustomerAccounts()));
        }
        return dto;
    }

    private CustomerAccounts customerAccountsToDto(List<org.meveo.model.payments.CustomerAccount> customerAccounts) {
        CustomerAccounts dto = new CustomerAccounts();
        if (customerAccounts != null) {
            for (org.meveo.model.payments.CustomerAccount ca : customerAccounts) {
                dto.getCustomerAccount().add(customerAccountToDto(ca));
            }
        }
        return dto;
    }

    private CustomerAccount customerAccountToDto(org.meveo.model.payments.CustomerAccount ca) {
        CustomerAccount dto = new CustomerAccount();
        if (ca != null) {
            dto.setCode(ca.getCode());
            dto.setDescription(ca.getDescription());
            dto.setExternalRef1(ca.getExternalRef1());
            dto.setExternalRef2(ca.getExternalRef2());
            if (ca.getName() != null) {
                dto.setName(new Name(ca.getName()));
            }
            if (ca.getAddress() != null) {
                dto.setAddress(new Address(ca.getAddress()));
            }
            dto.setTradingCurrencyCode(ca.getTradingCurrency() == null ? null : ca.getTradingCurrency().getCurrencyCode());
            dto.setTradingLanguageCode(ca.getTradingLanguage() == null ? null : ca.getTradingLanguage().getLanguageCode());
            if (ca.getCfValues() != null) {
                dto.setCustomFields(CustomFields.toDTO(ca.getCfValues().getValuesByCode()));
            }
            dto.setCreditCategory(ca.getCreditCategory() == null ? null : ca.getCreditCategory().getCode());
            if (ca.getContactInformation() != null) {
                dto.setEmail(ca.getContactInformation().getEmail());
                dto.setTel1(ca.getContactInformation().getPhone());
                dto.setTel2(ca.getContactInformation().getMobile());
            }
        }
        return dto;
    }
}