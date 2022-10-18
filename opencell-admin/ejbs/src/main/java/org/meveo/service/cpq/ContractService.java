package org.meveo.service.cpq;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.enums.ContractAccountLevel;
import org.meveo.model.cpq.enums.ContractStatusEnum;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tarik FAKHOURI.
 * @version 10.0
 */
@Stateless
public class ContractService extends BusinessService<Contract>  {

	private final static Logger LOGGER = LoggerFactory.getLogger(ContractService.class);

	@Inject
	private PricePlanMatrixVersionService pricePlanMatrixVersionService;

	private final static String CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE = "status of the contract (%s) is %s, it can not be updated nor removed";
	private final static String CONTRACT_CAN_NOT_CHANGE_THE_STATUS_ACTIVE_TO = "Status transition from ACTIVE to %s is not allowed";
	
	
	public ContractService() {
		
	}
	

	/**
	 * update contract
	 * @param contract
	 * @return update DRAFT contract
	 * @throws ContractException throw this exception if the contract status is ACTIVE or CLOSED
	 */
	public Contract updateContract(Contract contract) {
		LOGGER.info("updating contract {}", contract.getCode());
		
		if(contract.getStatus().equals(ProductStatusEnum.ACTIVE)) {
			LOGGER.warn("the contract {} can not be updated, because of its status => {}", contract.getCode(), contract.getStatus().toString());
			throw new BusinessException(String.format(CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, contract.getCode(), contract.getStatus().toString()));
		}
		if(!contract.getStatus().equals(ProductStatusEnum.DRAFT)) {
			LOGGER.warn("the contract {} can not be updated, because of its status => {}", contract.getCode(), contract.getStatus().toString());
			throw new BusinessException(String.format(CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, contract.getCode(), contract.getStatus().toString()));
		}
		
		update(contract);
		
		LOGGER.info("the contract ({}) updated successfully", contract.getCode());
		return contract;
	}
	
	/**
	 * delete contract by its contractCode
	 * @param contractCode
	 * @throws ContractException when : 
	 * 	<ul>
	 * 		<li>can't find any contract by id</li>
	 * 		<li>the status of contract is active</li>
	 * 	</ul>
	 */
	public void deleteContractByCode(String contractCode) {
		LOGGER.info("contract({}) to be deleted", contractCode);
		
		final Contract contratToDelete = findByCode(contractCode);
		if(contratToDelete == null) {
			throw new EntityDoesNotExistsException(Contract.class, contractCode);
		}
		if(contratToDelete.getStatus().equals(ProductStatusEnum.ACTIVE)) {
			LOGGER.warn("contract({}) can not be removed, because its status is active", contratToDelete.getCode());
			throw new BusinessException(String.format(CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, contratToDelete.getCode(), contratToDelete.getStatus().toString()));
		}
		getEntityManager().remove(contratToDelete);
		LOGGER.info("contract({}) is deleted successfully", contratToDelete.getCode());
	}
	
	/**
	 * @param contract
	 * @param status
	 * @return
	 * @throws ContractException
	 */
	public Contract updateStatus(Contract contract, ContractStatusEnum status){
		if(ContractStatusEnum.DRAFT.equals(contract.getStatus())
				&& ContractStatusEnum.ACTIVE.equals(status) && !contract.getContractItems().isEmpty()) {
			List<PricePlanMatrix> pricePlans = contract.getContractItems().stream().map(ContractItem::getPricePlan).collect(Collectors.toList());
			List<PricePlanMatrixVersion> pricePlanVersions = pricePlanMatrixVersionService.findByPricePlans(pricePlans);
			if (pricePlanVersions.isEmpty()) {
				log.error("At any given time during the duration of the framework agreement, a price should be applicable, please check your price version dates");
			}
			List<PricePlanMatrixVersion> draftPricePlanVersions = pricePlanVersions.stream().filter(pricePlanMatrixVersion -> VersionStatusEnum.DRAFT.equals(pricePlanMatrixVersion.getStatus())).collect(Collectors.toList());
			if (!draftPricePlanVersions.isEmpty()){
				log.error("All contract lines should have all price versions published to activate the framework agreement");
				throw new BusinessApiException("All contract lines should have all price versions published to activate the framework agreement");
			}
			List<PricePlanMatrixVersion> endDatePricePlanVersions = pricePlanVersions.stream().filter(pricePlanMatrixVersion -> pricePlanMatrixVersion.getValidity().getTo() == null).collect(Collectors.toList());
			if (endDatePricePlanVersions.isEmpty() && !pricePlanVersions.isEmpty()){
				pricePlanVersions.sort(Comparator.comparing(PricePlanMatrixVersion::getValidity));
				PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanVersions.get(0);
				if (pricePlanMatrixVersion.getValidity().getFrom().compareTo(contract.getBeginDate()) != 0){
					log.error("At any given time during the duration of the framework agreement, a price should be applicable, please check your price version dates");
					throw new BusinessApiException(
							"At any given time during the duration of the framework agreement, a price should be applicable, please check your price version dates");

				}
				pricePlanVersions.sort((p1,p2) -> p1.getValidity().compareFieldTo(p2.getValidity()));
				pricePlanMatrixVersion = pricePlanVersions.get(pricePlanVersions.size()-1);
				if (pricePlanMatrixVersion.getValidity().getTo().compareTo(contract.getEndDate()) != 0){
					log.error("At any given time during the duration of the framework agreement, a price should be applicable, please check your price version dates");
					throw new BusinessApiException(
							"At any given time during the duration of the framework agreement, a price should be applicable, please check your price version dates");

				}
			}
			else {
			    pricePlanVersions.sort(Comparator.comparing(PricePlanMatrixVersion::getValidity));
			    if(pricePlanVersions.size() > 0) {
			        for (int i=0; i < pricePlanVersions.size() - 1; i++) {
	                    Date ppvValidityTo = pricePlanVersions.get(i).getValidity().getTo();
	                    Date ppvNextValidityFrom = pricePlanVersions.get(i+1).getValidity().getFrom();
	                    Date ppvValidityToNextDay = DateUtils.addDaysToDate(ppvValidityTo, 1);
	                    if (ppvValidityTo.compareTo(ppvNextValidityFrom) != 0 && ppvValidityToNextDay.compareTo(ppvNextValidityFrom) != 0){
	                        log.error("At any given time during the duration of the framework agreement, a price should be applicable, please check your price version dates");
	                        throw new BusinessApiException("At any given time during the duration of the framework agreement, a price should be applicable, please check your price version dates");
	                    }
	                }
			    }                
            }
		}

		if (ContractStatusEnum.ACTIVE.equals(contract.getStatus())
				&& (ContractStatusEnum.ACTIVE.equals(status) || ContractStatusEnum.DRAFT.equals(status))) {
			 throw new BusinessApiException(String.format(CONTRACT_CAN_NOT_CHANGE_THE_STATUS_ACTIVE_TO, status));
		}


		contract.setStatus(status);
		return  update(contract);
	}
	
	@SuppressWarnings("unchecked")
	public List<Contract> findByBillingAccountLevel(ContractAccountLevel accountLevel, String accountLevelCode){
		switch(accountLevel) {
			case BILLING_ACCOUNT :
				return this.getEntityManager().createNamedQuery("Contract.findBillingAccount").setParameter("codeBillingAccount", accountLevelCode).getResultList();
			case CUSTOMER_ACCOUNT : 
				return this.getEntityManager().createNamedQuery("Contract.findCustomerAccount").setParameter("codeCustomerAccount", accountLevelCode).getResultList();
			case CUSTOMER : 
				return this.getEntityManager().createNamedQuery("Contract.findCustomer").setParameter("codeCustomer", accountLevelCode).getResultList();
			default : throw new BusinessException("Account level is missing for code : " + accountLevelCode);
		}
		
	}

	 public List<Contract> getContractByAccount(Customer customer, BillingAccount billingAccount, CustomerAccount customerAccount) {
	    	try {
				return getEntityManager().createNamedQuery("Contract.findByAccounts")
						.setParameter("customerId", customer.getId()).setParameter("billingAccountId", billingAccount.getId())
						.setParameter("customerAccountId",customerAccount.getId()).setFlushMode(FlushModeType.COMMIT).getResultList();
	    	} catch (NoResultException e) {
	            return null;
	        }
	    }
}