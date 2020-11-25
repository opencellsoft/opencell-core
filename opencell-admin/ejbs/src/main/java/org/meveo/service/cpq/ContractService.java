package org.meveo.service.cpq;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.ContractAccountLevel;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.service.base.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tarik FAKHOURI.
 * @version 10.0
 */
@Stateless
public class ContractService extends BusinessService<Contract>  {

	private final static Logger LOGGER = LoggerFactory.getLogger(ContractService.class);
	
	private final static String CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE = "status of the contract (%s) is %s, it can not be updated nor removed";
	private final static String CONTRACT_CAN_NOT_CHANGE_THE_STATUS = "contract (%s) can not change the status beacause it not draft";
	
	
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
	public Contract updateStatus(Contract contract, ProductStatusEnum status){
		if(contract.getStatus().equals(ProductStatusEnum.DRAFT)) {
			contract.setStatus(status);
			return  update(contract);
		}else if (ProductStatusEnum.ACTIVE.equals(contract.getStatus())) {
			contract.setStatus(ProductStatusEnum.CLOSED);
			return  update(contract);
		}
		throw new BusinessException(String.format(CONTRACT_CAN_NOT_CHANGE_THE_STATUS, contract.getCode()));
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
	
	
}
