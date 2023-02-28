package org.meveo.service.cpq;

import java.util.Comparator;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.WalletOperation;
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
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tarik FAKHOURI.
 * @version 10.0
 */
@Stateless
public class ContractService extends BusinessService<Contract>  {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractService.class);

	@Inject
	private PricePlanMatrixVersionService pricePlanMatrixVersionService;

	private static final String CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE = "status of the contract (%s) is %s, it can not be updated nor removed";
	private static final String CONTRACT_CAN_NOT_CHANGE_THE_STATUS_ACTIVE_TO = "Status transition from ACTIVE to %s is not allowed";
	
	
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
			LOGGER.warn("the contract {} can not be updated, because of its status => {}", contract.getCode(), contract.getStatus());
			throw new BusinessException(String.format(CONTRACT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, contract.getCode(), contract.getStatus().toString()));
		}
		if(!contract.getStatus().equals(ProductStatusEnum.DRAFT)) {
			LOGGER.warn("the contract {} can not be updated, because of its status => {}", contract.getCode(), contract.getStatus());
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
	public Contract updateStatus(Contract contract, String status){
		if(ContractStatusEnum.DRAFT.toString().equals(contract.getStatus())
				&& ContractStatusEnum.ACTIVE.toString().equals(status) && !contract.getContractItems().isEmpty()) {
			List<PricePlanMatrix> pricePlans = contract.getContractItems().stream().map(ContractItem::getPricePlan).collect(Collectors.toList());
			List<PricePlanMatrixVersion> pricePlanVersions = pricePlanMatrixVersionService.findByPricePlans(pricePlans);
			if (pricePlanVersions.isEmpty()) {
				log.error("At any given time during the duration of the framework agreement, a price should be applicable, please check your price versions");
			}
			List<PricePlanMatrixVersion> draftPricePlanVersions = pricePlanVersions.stream().filter(pricePlanMatrixVersion -> VersionStatusEnum.DRAFT.equals(pricePlanMatrixVersion.getStatus())).collect(Collectors.toList());
			if (!draftPricePlanVersions.isEmpty()){
				log.error("All contract lines should have all price versions published to activate the framework agreement");
				throw new BusinessApiException("All contract lines should have all price versions published to activate the framework agreement");
			}
			List<PricePlanMatrixVersion> endDatePricePlanVersions = pricePlanVersions.stream().
					filter(pricePlanMatrixVersion -> pricePlanMatrixVersion.getValidity() != null && pricePlanMatrixVersion.getValidity().getTo() == null).collect(Collectors.toList());
			
			if (endDatePricePlanVersions.isEmpty() && !pricePlanVersions.isEmpty()){
				pricePlanVersions.sort(comparing(PricePlanMatrixVersion::getValidity, nullsLast(naturalOrder())));
				PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanVersions.get(0);
				if (pricePlanMatrixVersion.getValidity() != null && pricePlanMatrixVersion.getValidity().getFrom().compareTo(contract.getBeginDate()) < 0){
			//NOTE 2		
					log.error("Start date of the price version id {} should not be prior to the Start date of the contract",pricePlanMatrixVersion.getId());
					throw new BusinessApiException(
							"Start date of a price version should not be prior to the Start date of the contract.");

				}
			//NOTE 3
				pricePlanMatrixVersion = pricePlanVersions.get(pricePlanVersions.size()-1);
				if (pricePlanMatrixVersion.getValidity() != null && pricePlanMatrixVersion.getValidity().getTo().compareTo(contract.getEndDate()) > 0){
					log.error("End date of of the price version id {} should not be after the End date of a contract",pricePlanMatrixVersion.getId());
					throw new BusinessApiException(
							"Start date of a price version should not be prior to the Start date of the contract.");
				}
			}
		}

		if (ContractStatusEnum.ACTIVE.toString().equals(contract.getStatus())
				&& (ContractStatusEnum.ACTIVE.toString().equals(status) || ContractStatusEnum.DRAFT.toString().equals(status))) {
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

	public List<Contract> getContractByAccount(Customer customer, BillingAccount billingAccount, CustomerAccount customerAccount, WalletOperation bareWalletOperation) {
		try {
			List<Contract> contracts = getEntityManager().createNamedQuery("Contract.findByAccounts")
					.setParameter("customerId", customer.getId()).setParameter("billingAccountId", billingAccount.getId())
					.setParameter("customerAccountId",customerAccount.getId()).setFlushMode(FlushModeType.COMMIT).getResultList();
			
			return contracts.stream()
					.filter(c -> StringUtils.isBlank(c.getApplicationEl()) || ValueExpressionWrapper.evaluateExpression(c.getApplicationEl(), Boolean.class, bareWalletOperation, c))
					.collect(Collectors.toList());
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Get contract by list of customer's id
	 * @param ids Customer id's
	 * @param billingAccount {@link BillingAccount}
	 * @param customerAccount {@link CustomerAccount}
	 * @return List of {@link Contract}
	 */
	public List<Contract> getContractByListOfCustomers(List<Long> ids , BillingAccount billingAccount, CustomerAccount customerAccount) {
		try {
			return getEntityManager().createNamedQuery("Contract.findByCustomersBillingAccountCustomerAccount")
					.setParameter("customersId", ids).setParameter("billingAccountId", billingAccount.getId())
					.setParameter("customerAccountId",customerAccount.getId()).getResultList();
    	} catch (NoResultException e) {
            return null;
        }
	}
}