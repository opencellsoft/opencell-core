package org.meveo.service.cpq;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.exception.ContractException;
import org.meveo.service.cpq.exception.ContractItemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tarik FAKHOURI.
 * @version 10.0
 * 
 * Commercial Offer type service implementation.
 */

@Stateless
public class ContractItemService extends
		PersistenceService<ContractItem> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContractItemService.class);

	private static final String PRODUCT_ATTRIBUTE_IS_REQUIRED = "attribute (%d) is missing for creation a new contract item attribute";	
	private static final String CONTRACT_STATUS_NOT_DRAFT = "can not add the new contract of item, cause the current contract it not DRAFT";	
	private final static String CONTRACT_ITEM_STATUS_NOT_DRAFT_CAN_NOT_REMOVED_OR_UPDATE = "item contract (%d) can not be update nor delete because its status is %s";
	private final static String CONTRACT_ITEM_UNKNOWN = "Unkwon contract item for id = (%s)";
	
	@Inject
	private ContractService contractService;
	@Inject
	private OfferTemplateService offerTemplateService;
	@Inject
	private ProductService productService;
	@Inject
	private ChargeTemplateServiceAll chargeTemplateServiceAll;
	@Inject
	private ServiceTemplateService serviceTemplateService;
	
	
	
	/**
	 * @param contractItem
	 * @throws ContractException 
	 */
	public void updateContractItem(ContractItem contractItem) throws ContractItemException {
		LOGGER.info("updating contract item code  {}", contractItem.getId());
		final Contract contract = contractItem.getContract();
		if(contract == null || contract.getId() == null) {
			throw new ContractItemException("");
		}
		if(contract.getStatus().equals(ProductStatusEnum.DRAFT)) {
			update(contractItem);
			LOGGER.info("Updating item contract ({}) successfuly", contractItem.getId());
		}
		throw new ContractItemException(String.format(CONTRACT_ITEM_STATUS_NOT_DRAFT_CAN_NOT_REMOVED_OR_UPDATE, contractItem.getId(), contract.getStatus().toString()));
	}

	/**
	 * @param id
	 * @throws ContractException
	 */
	public void deleteContractItem(Long id) throws ContractItemException {
		LOGGER.info("deleting item of contract ({})", id);
		final ContractItem item = findById(id);
		if(item == null || item.getId() == null) {
			throw new ContractItemException(String.format(CONTRACT_ITEM_UNKNOWN,id));
		}
		if(item.getContract() != null && !item.getContract().getStatus().equals(ProductStatusEnum.DRAFT)) {
			throw new ContractItemException(String.format(CONTRACT_ITEM_STATUS_NOT_DRAFT_CAN_NOT_REMOVED_OR_UPDATE, id, item.getContract().getStatus().toString()));
		}
		LOGGER.info("contract item ({}) successfully deleted", id);
		remove(item);
		
	}
	
	public void createNewContractItem(Long idContract, Long idCommercialOffer, Long idProduct,
											Long idProductAttribute, Long idPricePlan, Long idChargeTemplate,
											Long idServiceTemplate, int rate, BigDecimal amountWithoutTax) throws ContractItemException {
		final ContractItem item = new ContractItem();
		final Contract contract = contractService.findById(idContract);
		// TODO: a confirmer avec Rachid
		if(contract != null && contract.getStatus().equals(ProductStatusEnum.DRAFT)) {
			item.setContract(contract);
		}else if(!contract.getStatus().equals(ProductStatusEnum.DRAFT)) {
			throw new ContractItemException(CONTRACT_STATUS_NOT_DRAFT);
		}
		final OfferTemplate commercialOffer = offerTemplateService.findById(idCommercialOffer);
		item.setOfferTemplate(commercialOffer);
		
		final Product product = productService.findById(idProduct);
		item.setProduct(product);
		
		//TODO : get price plan for contract item : (ask rachid for more information)
		
		//TODO : diff between ChargeTemplateServiceAll & ChargeTemplateService ?
		final ChargeTemplate chargeTemplate = chargeTemplateServiceAll.findById(idChargeTemplate);
		item.setChargeTemplate(chargeTemplate);
		
		final ServiceTemplate serviceTemplate = serviceTemplateService.findById(idServiceTemplate);
		item.setServiceTemplate(serviceTemplate);
		item.setRate(rate);
		item.setAmountWithoutTax(amountWithoutTax);
		
		this.create(item);
		
		
		
		
	}
}