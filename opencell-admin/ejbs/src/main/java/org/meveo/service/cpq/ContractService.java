package org.meveo.service.cpq;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.admin.Seller;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.exception.ContractException;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tarik FAKHOURI.
 * @version 10.0
 */
@Stateless
public class ContractService extends PersistenceService<Contract>  {

	private final static Logger LOGGER = LoggerFactory.getLogger(ContractService.class);
	
	private final static String CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE = "status of the contract (%s) is %s, it can not be updated nor removed";
	private final static String CONTRACT_UNKWON = "contract (%s) unkwon!";
	private final static String SELLER_UNKNOW = "SELLER (%s) for contract code (%s) is unkonw!";
	private final static String CONTRACT_CAN_NOT_CHANGE_THE_STATUS = "contract (%s) can not change the status beacause it not draft";
	
	@Inject
	private SellerService sellerService;
	@Inject
	private CustomerAccountService customerAccountService;
	@Inject
	private CustomerService customerService;
	
	public ContractService() {
		
	}
	

	/**
	 * update contract
	 * @param contract
	 * @return update DRAFT contract
	 * @throws ContractException throw this exception if the contract status is ACTIVE or CLOSED
	 */
	public Contract updateContract(Contract contract) throws ContractException{
		LOGGER.info("updating contract {}", contract.getCode());
		
		if(contract.getStatus().equals(ProductStatusEnum.ACTIVE)) {
			LOGGER.warn("the contract {} can not be updated, because of its status => {}", contract.getCode(), contract.getStatus().toString());
			throw new ContractException(String.format(CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, contract.getCode(), contract.getStatus().toString()));
		}
		if(!contract.getStatus().equals(ProductStatusEnum.DRAFT)) {
			LOGGER.warn("the contract {} can not be updated, because of its status => {}", contract.getCode(), contract.getStatus().toString());
			throw new ContractException(String.format(CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, contract.getCode(), contract.getStatus().toString()));
		}
		
		update(contract);
		
		LOGGER.info("the contract ({}) updated successfully", contract.getCode());
		return contract;
	}
	
	/**
	 * delete contract by its id
	 * @param id
	 * @throws ContractException when : 
	 * 	<ul>
	 * 		<li>can't find any contract by id</li>
	 * 		<li>the status of contract is active</li>
	 * 	</ul>
	 */
	public void deleteContractById(Long id) throws ContractException{
		LOGGER.info("contract({}) to be deleted", id);
		
		final Contract contratToDelete = findById(id);
		if(contratToDelete == null) {
			throw new ContractException(String.format(CONTRACT_UNKWON, id));
		}
		if(contratToDelete.getStatus().equals(ProductStatusEnum.ACTIVE)) {
			LOGGER.warn("contract({}) can not be removed, because its status is active", contratToDelete.getCode());
			throw new ContractException(String.format(CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, contratToDelete.getCode(), contratToDelete.getStatus().toString()));
		}
		getEntityManager().remove(contratToDelete);
		LOGGER.info("contract({}) is deleted successfully", contratToDelete.getCode());
	}
	
	/**
	 *  cration  of the new contract
	 * @param codeContract
	 * @param codeCustomerAccount
	 * @param codeCustomer
	 * @param codeSeller
	 * @param dateContract
	 * @param beginDate
	 * @param endDate
	 * @throws ContractException
	 */
	public void createNewContract(String codeContract, String codeCustomerAccount, 
										String codeCustomer, String codeSeller, 
										Date contractDate, Date dateBegin,
										Date dateEnd) throws ContractException {
		final Contract newContract = new Contract();
		
		final CustomerAccount customerAccount = customerAccountService.findByCode(codeCustomerAccount);
		if(customerAccount != null && customerAccount.getId() != null) {
			newContract.setCustomerAccount(customerAccount);
		}
		
		final Customer customer = customerService.findByCode(codeCustomer);
		if(customer != null && customer.getId() != null) {
			newContract.setCustomer(customer);
			newContract.setSeller(customer.getSeller());
		}

		final Seller seller = sellerService.findByCode(codeSeller);
		
		if(seller != null && seller.getId() != null) {
			newContract.setSeller(seller);
		}
		
		if(newContract.getSeller() == null) {
			LOGGER.warn("the seller is requied for creation of the contract ({})", codeContract);
			throw new ContractException(String.format(SELLER_UNKNOW, seller, codeContract));
		}
		
		newContract.setContractDate(contractDate);
		newContract.setBeginDate(dateBegin);
		newContract.setEndDate(dateEnd);
		newContract.setCode(codeContract);
		
		this.create(newContract);
	}
	
	/**
	 * @param contract
	 * @param status
	 * @return
	 * @throws ContractException
	 */
	public Contract updateStatus(Contract contract, ProductStatusEnum status) throws ContractException{
		if(contract.getStatus().equals(ProductStatusEnum.DRAFT)) {
			contract.setStatus(status);
			return  update(contract);
		}else if (ProductStatusEnum.ACTIVE.equals(contract.getStatus())) {
			contract.setStatus(ProductStatusEnum.CLOSED);
			return  update(contract);
		}
		throw new ContractException(String.format(CONTRACT_CAN_NOT_CHANGE_THE_STATUS, contract.getCode()));
	}
	
}
