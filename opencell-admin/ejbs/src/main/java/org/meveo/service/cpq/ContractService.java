package org.meveo.service.cpq;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
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
		if (contratToDelete == null) {
			throw new EntityDoesNotExistsException(Contract.class, contractCode);
		}
		if (ContractStatusEnum.ACTIVE.getValue().equals(contratToDelete.getStatus())) {
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
				if (pricePlanMatrixVersion.getValidity() != null && pricePlanMatrixVersion.getValidity().getFrom() != null
						&& pricePlanMatrixVersion.getValidity().getFrom().compareTo(contract.getBeginDate()) < 0) {
			//NOTE 2
					log.error("Start date of the price version id {} should not be prior to the Start date of the contract",pricePlanMatrixVersion.getId());
				}
			//NOTE 3
				pricePlanMatrixVersion = pricePlanVersions.get(pricePlanVersions.size()-1);
				if (pricePlanMatrixVersion.getValidity() != null && pricePlanMatrixVersion.getValidity().getTo() != null &&
						pricePlanMatrixVersion.getValidity().getTo().compareTo(contract.getEndDate()) > 0) {
					log.error("End date of of the price version id {} should not be after the End date of a contract",pricePlanMatrixVersion.getId());
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

	public List<Contract> getContractByAccount(Customer customer, BillingAccount billingAccount, CustomerAccount customerAccount,Seller seller, WalletOperation bareWalletOperation) {
		return this.getContractByAccount(Arrays.asList(customer.getId()), billingAccount, customerAccount,Arrays.asList(seller.getId()), bareWalletOperation, null);
	}

	public List<Contract> getContractByAccount(List<Long> customersID, BillingAccount billingAccount, CustomerAccount customerAccount,List<Long> sellersId, WalletOperation bareWalletOperation, Date operationDate) {
		try {
			Date updateOD = bareWalletOperation != null ? bareWalletOperation.getOperationDate() : operationDate;
			if(operationDate != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(operationDate);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				updateOD = calendar.getTime();

			}
			List<Contract> contracts = getEntityManager().createNamedQuery("Contract.findByAccounts")
					.setParameter("customerIds", customersID).setParameter("billingAccountId", billingAccount.getId())
					.setParameter("customerAccountId",customerAccount.getId())
					.setParameter("operationDate", updateOD)
					.setParameter("sellerIds",sellersId).getResultList();


			return contracts.stream()
					.filter(c -> {
						try {
							return StringUtils.isBlank(c.getApplicationEl()) || ValueExpressionWrapper.evaluateExpression(c.getApplicationEl(), Boolean.class, bareWalletOperation, c);
						} catch (Exception e) {
							throw new BusinessException("Error evaluating the contract’s application EL [contract_id="+c.getId()+",  “"+c.getApplicationEl()+"“]: "+e.getMessage(), e);
						}
					})
					.collect(Collectors.toList());
		} catch (NoResultException e) {
			return null;
		}
	}


	public Contract lookupSuitableContract(List<Customer> customers, List<Contract> contracts, boolean withRules) {
		Contract contract = null;
		if(contracts != null && !contracts.isEmpty()) {
			// Prioritize BA Contract then CA Contract then Customer Hierarchy Contract then Seller Contract
			Optional<Contract> contractLookup = contracts.stream().filter(c -> c.getBillingAccount() != null && (!withRules || (c.getBillingRules()!=null && !c.getBillingRules().isEmpty()))).findFirst()
					.or(() -> contracts.stream().filter(c -> c.getCustomerAccount() != null && (!withRules || (c.getBillingRules()!=null && !c.getBillingRules().isEmpty()))).findFirst());
			if(contractLookup.isEmpty()) {
				for (Customer iCustomer : customers) {
					contractLookup = contracts.stream().filter(c -> c.getCustomer() != null && c.getCustomer().getId().equals(iCustomer.getId()) && (!withRules || (c.getBillingRules()!=null && !c.getBillingRules().isEmpty()))).findFirst();
					if(contractLookup.isPresent()) {
						break;
					}
				}
			}
			contract = contractLookup.orElseGet(() -> contracts.get(0));
		}
		return contract;
	}
}