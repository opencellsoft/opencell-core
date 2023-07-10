package org.meveo.api.billing;

import static java.util.Optional.ofNullable;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.cpq.ContractService;

public class ContractHierarchyHelper {
	
    @Inject
    private ContractService contractService;
    
    @Inject
    protected ParamBeanFactory paramBeanFactory;

	public Contract checkContractHierarchy(BillingAccount billingAccount, String contractCode) {
		boolean allowAnyContract = paramBeanFactory.getInstance().getPropertyAsBoolean("contract.allow_any_contract", false);
		if (StringUtils.isNotBlank(contractCode)) {
			Contract contract = ofNullable(contractService.findByCode(contractCode)).orElseThrow(() -> new BusinessException("No contract found with the given code : " + contractCode));
			if (allowAnyContract) {
    			return contract;
    		}else {
			CustomerAccount customerAccount = billingAccount.getCustomerAccount();
			Customer customer = customerAccount.getCustomer();
			Customer customerParent = customer.getParentCustomer();
			Seller seller = customer.getSeller();
			
			boolean isAttachedToBA = contract.getBillingAccount() != null && contract.getBillingAccount().getCode().equals(billingAccount.getCode());
			boolean isAttachedToCA = contract.getCustomerAccount() != null && contract.getCustomerAccount().getCode().equals(customerAccount.getCode());
			boolean isAttachedToCustomer = contract.getCustomer() != null && contract.getCustomer().getCode().equals(customer.getCode());
			boolean isAttacheToParentCustomer = contract.getCustomer() != null && contract.getCustomer().getCode().equals(customerParent.getCode());
			boolean isAttachedToSeller = contract.getSeller() != null && contract.getSeller().getCode().equals(seller.getCode());
			
			if (isAttachedToBA || isAttachedToCA || isAttachedToCustomer || isAttacheToParentCustomer || isAttachedToSeller) {
				return contract;
			} else {
				throw new BusinessApiException("Current contract code : " + contract.getCode() + " is not applicable for any customer hierarchy.");
			}
		}
		}
		return null;
	}

}
