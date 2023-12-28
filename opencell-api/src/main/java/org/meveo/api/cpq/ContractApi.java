package org.meveo.api.cpq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.api.dto.cpq.ContractItemDto;
import org.meveo.api.dto.cpq.ContractListResponsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.apiv2.cpq.contracts.BillingRuleDto;
import org.meveo.apiv2.cpq.mapper.BillingRuleMapper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.contract.BillingRule;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.contract.ContractRateTypeEnum;
import org.meveo.model.cpq.enums.ContractAccountLevel;
import org.meveo.model.cpq.enums.ContractStatusEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.BillingRuleService;
import org.meveo.service.cpq.ContractItemService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author Tarik F.
 * @version 10.0
 *
 */
@Stateless
public class ContractApi extends BaseApi{

	@Inject
	private ContractService contractService;
	@Inject
	private SellerService sellerService;
	@Inject
	private BillingAccountService billingAccountService;
	@Inject
	private CustomerAccountService customerAccountService;
	@Inject 
	private CustomerService customerService;
	@Inject
	private ContractItemService contractItemService;
	@Inject
	private ServiceTemplateService serviceTemplateService;
	@Inject
	private OfferTemplateService offerTemplateService;
	@Inject
	private ProductService productService;
	@Inject
	private PricePlanMatrixService pricePlanMatrixService;
	@Inject
	private ChargeTemplateService<ChargeTemplate> chargeTemplateService;
	@Inject
	private BillingRuleService billingRuleService;
	@Inject
	private PricePlanMatrixVersionService pricePlanMatrixVersionService;
	
	private BillingRuleMapper billingRuleMapper = new BillingRuleMapper();
	
	private static final String CONTRACT_DATE_END_GREAT_THAN_DATE_BEGIN = "Date end (%s) must be great than date begin (%s)";
	private static final String CONTRACT_STATUS_CLOSED = "Closed status of contract cannot be edited";

	private static final String DEFAULT_SORT_ORDER_ID = "id";


	public Long CreateContract(ContractDto dto) {
		// check mandatory param
		checkParams(dto);
		//check if date end great than date begin
		if(dto.getEndDate().compareTo(dto.getBeginDate()) < 0) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			throw new BusinessApiException(String.format(CONTRACT_DATE_END_GREAT_THAN_DATE_BEGIN, format.format(dto.getEndDate()), format.format(dto.getBeginDate())));
		}
		// check if contract already exist
		Contract contract = contractService.findByCode(dto.getCode());
		if(contract != null)
			throw new EntityAlreadyExistsException(Contract.class, dto.getCode());
		// create new contract
		contract = new Contract();
		contract.setCode(dto.getCode());
		contract.setBeginDate(dto.getBeginDate());
		contract.setEndDate(dto.getEndDate());
		contract.setContractDate(dto.getContractDate());
		contract.setRenewal(dto.isRenewal());
		contract.setContractDuration(dto.getContractDuration());
		contract.setDescription(dto.getDescription());
		changeAccountLevel(dto, contract);
		try {
			populateCustomFields(dto.getCustomFields(), contract, true);
			contractService.create(contract);
			// add billing rules
            List<BillingRule> lstBillingRule = new ArrayList<BillingRule>();
            if (dto.getBillingRules() != null) {
                for (BillingRuleDto brDto : dto.getBillingRules()) {
                    BillingRule br = billingRuleMapper.toEntity(brDto);
                    br.setContract(contract);
                    billingRuleService.create(br);
                    lstBillingRule.add(br);            
                } 
                contract.setBillingRules(lstBillingRule);
            }            
		}catch(BusinessException e) {
			throw new MeveoApiException(e);
		}
		return contract.getId();
	}
	
	private void changeAccountLevel(ContractDto dto, Contract contract) {
		switch (dto.getContractAccountLevel()) {
			case SELLER:
					final Seller seller = sellerService.findByCode(dto.getAccountCode());
					if(seller == null)
						throw new EntityDoesNotExistsException(Seller.class, dto.getAccountCode());
					contract.setSeller(seller);
				break;
			case CUSTOMER : 
					final Customer customer = customerService.findByCode(dto.getAccountCode());
					if(customer == null)
						throw new EntityDoesNotExistsException(Customer.class, dto.getAccountCode());
					contract.setCustomer(customer);
				break;
			case BILLING_ACCOUNT : 
				final BillingAccount billingAccount = billingAccountService.findByCode(dto.getAccountCode());
				if(billingAccount == null)
					throw new EntityDoesNotExistsException(BillingAccount.class, dto.getAccountCode());
				contract.setBillingAccount(billingAccount);
				break;
			case CUSTOMER_ACCOUNT : 
				final CustomerAccount cusotmerAccount = customerAccountService.findByCode(dto.getAccountCode());
				if(cusotmerAccount == null)
					throw new EntityDoesNotExistsException(CustomerAccount.class, dto.getAccountCode());
				contract.setCustomerAccount(cusotmerAccount);
				break;
			default:
				break;
		}
	}
	public void updateContract(ContractDto dto) {
		// check mandatory param
		checkParams(dto);
		//check if date end great than date begin
		if(dto.getEndDate().compareTo(dto.getBeginDate()) < 0) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			throw new BusinessApiException(String.format(CONTRACT_DATE_END_GREAT_THAN_DATE_BEGIN, format.format(dto.getEndDate()), format.format(dto.getBeginDate())));
		}
		//get the current contract
		Contract contract = contractService.findByCode(dto.getCode());
		if (contract == null) {
			throw new EntityDoesNotExistsException(Contract.class, dto.getCode());
		}		
		//check the status of the contract
		if (ContractStatusEnum.CLOSED.equals(contract.getStatus())) {
			throw new MeveoApiException(CONTRACT_STATUS_CLOSED);
		} else if (ContractStatusEnum.ACTIVE.equals(contract.getStatus())) {
			contract.setEndDate(dto.getEndDate());
		} else {
			contract.setStatus(dto.getStatus());
			contract.setCode(dto.getCode());
			contract.setBeginDate(dto.getBeginDate());
			contract.setEndDate(dto.getEndDate());
			contract.setContractDate(dto.getContractDate());
			contract.setRenewal(dto.isRenewal());
			contract.setContractDuration(dto.getContractDuration());
			contract.setDescription(dto.getDescription());

			contract.setBillingAccount(null);
			contract.setSeller(null);
			contract.setCustomer(null);
			contract.setCustomerAccount(null);
			changeAccountLevel(dto, contract);

			// update billing rules
			if (dto.getBillingRules() != null) {
				contract.getBillingRules().clear();
				for (BillingRuleDto brDto : dto.getBillingRules()) {
					BillingRule br = billingRuleMapper.toEntity(brDto);
					br.setContract(contract);
					billingRuleService.create(br);
					contract.getBillingRules().add(br);
				}
			}
		}

		try {
			populateCustomFields(dto.getCustomFields(), contract, false);
			contractService.update(contract);
		}catch(BusinessException e) {
			throw new MeveoApiException(e);
		}
	}

	public void deleteContract(String codeContract) {
		try {
			contractService.deleteContractByCode(codeContract);
		}catch(BusinessException e) {
			throw new MeveoApiException(e);
		}
	}
	
	public ContractDto findContract(String contractCode) {
		final Contract contract = contractService.findByCode(contractCode);
		if(contract == null)
			throw new EntityDoesNotExistsException(Contract.class, contractCode);
		ContractDto dto = new ContractDto(contract);
		dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(contract));
		return dto;
	}
	
	public List<ContractDto> findContractAccountLevel(ContractAccountLevel contractAccountLevel, String accountCode) {
		if(contractAccountLevel == null)
			missingParameters.add("contractAccountLevel");
		if(Strings.isEmpty(accountCode))
			missingParameters.add("accountCode");
		handleMissingParameters();
		return contractService.findByBillingAccountLevel(contractAccountLevel, accountCode)
													.stream().map(c -> {
														ContractDto dto = new ContractDto(c);
														dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(c));
														return dto;
													}
													).collect(Collectors.toList());
													
	}

	public void updateStatus(String contractCode, ContractStatusEnum contractStatus){
		try {
			Contract contract = loadEntityByCode(contractService, contractCode, Contract.class);
			contractService.updateStatus(contract, contractStatus);
		} catch (Exception e){
			log.error(e.getMessage(),e);
			throw new MeveoApiException(e);
		}
	}

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "contracts.contract", 
    			itemPropertiesToFilter = { 
    							@FilterProperty(property = "sellerCode", entityClass = Seller.class),
    							@FilterProperty(property = "billingAccountCode", entityClass = BillingAccount.class),
    							@FilterProperty(property = "customerAccountCode", entityClass = CustomerAccount.class),
    							@FilterProperty(property = "customerCode", entityClass = Customer.class) }, totalRecords = "contract.listSize")
    public ContractListResponsDto list(PagingAndFiltering pagingAndFiltering) throws MeveoApiException {

        String sortBy = DEFAULT_SORT_ORDER_ID;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }
		 var filters = new HashedMap<String, Object>();
		 pagingAndFiltering.getFilters().forEach( (key, value) -> {
			 String newKey = key.replace("accountCode", "billingAccount.code");
			 filters.put(key.replace(key, newKey), value);
		 });
		 pagingAndFiltering.getFilters().clear();
		 pagingAndFiltering.getFilters().putAll(filters);
		 List<String> fields = Arrays.asList("billingAccount");
        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, PagingAndFiltering.SortOrder.ASCENDING, fields, pagingAndFiltering, Contract.class);

        Long totalCount = contractService.count(paginationConfiguration);
        ContractListResponsDto result = new ContractListResponsDto();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        
        if(totalCount > 0) {
        	contractService.list(paginationConfiguration).stream().forEach( c -> {
        		result.getContracts().getContracts().add(new ContractDto(c));
        	});
        }
    	return result;
    }
    
    public Long createContractLine(ContractItemDto contractItemDto) {
    	checkParams(contractItemDto);
    	ContractItem item = contractItemService.findByCode(contractItemDto.getCode());
    	if(item != null)
    		throw new EntityAlreadyExistsException(ContractItem.class, contractItemDto.getCode());
    	item = new ContractItem();
    	final Contract contract = contractService.findByCode(contractItemDto.getContractCode());
    	if(contract == null)
    		throw new EntityDoesNotExistsException(Contract.class, contractItemDto.getContractCode());
    	if(!Strings.isEmpty(contractItemDto.getServiceTemplateCode())) {
        	item.setServiceTemplate(serviceTemplateService.findByCode(contractItemDto.getServiceTemplateCode()));
    	}
    	if(!Strings.isEmpty(contractItemDto.getPricePlanCode())) {
    		item.setPricePlan(pricePlanMatrixService.findByCode(contractItemDto.getPricePlanCode()));
    	}
    	
    	item.setCode(contractItemDto.getCode());
    	item.setContract(contract);
		
    	if(!Strings.isEmpty(contractItemDto.getServiceTemplateCode()))
    		item.setOfferTemplate(offerTemplateService.findByCode(contractItemDto.getServiceTemplateCode()));
    	if(!Strings.isEmpty(contractItemDto.getProductCode()))
    		item.setProduct(productService.findByCode(contractItemDto.getProductCode()));
    	if(!Strings.isEmpty(contractItemDto.getPricePlanCode()))
    		item.setPricePlan(pricePlanMatrixService.findByCode(contractItemDto.getPricePlanCode()));
    	if(!Strings.isEmpty(contractItemDto.getChargeTemplateCode()))
    		item.setChargeTemplate(chargeTemplateService.findByCode(contractItemDto.getChargeTemplateCode()));
		if(!Strings.isEmpty(contractItemDto.getOfferTemplateCode()))
			item.setOfferTemplate(loadEntityByCode(offerTemplateService, contractItemDto.getOfferTemplateCode(), OfferTemplate.class));
    	item.setRate(contractItemDto.getRate());
    	item.setAmountWithoutTax(contractItemDto.getAmountWithoutTax());
    	item.setDescription(contractItemDto.getDescription());

    	if(contractItemDto.getAmountWithoutTax() != null) {
    		item.setContractRateType(ContractRateTypeEnum.FIXED);
    	}else {
        	item.setContractRateType(contractItemDto.getContractRateType());
    	}
    	
    	if(contractItemDto.getSeperateDiscountLine()!=null) {
    		item.setSeparateDiscount(contractItemDto.getSeperateDiscountLine()); 
    	}
    	if(ContractRateTypeEnum.FIXED.equals(item.getContractRateType()) && Boolean.TRUE.equals(contractItemDto.getSeperateDiscountLine())){
    		throw new InvalidParameterException("generate separate discount line is valable only for the types 'Global discount' and 'Custom discount grid'");
    	}
    	
    	try {
    		populateCustomFields(contractItemDto.getCustomFields(), item, true);
    		contractItemService.create(item);
    		return item.getId();
    	}catch(BusinessException e) {
    		throw new MeveoApiException(e);
    	}
    }
    
    public void updateContractLine(ContractItemDto contractItemDto) {

		checkParams(contractItemDto);
    	final ContractItem item = contractItemService.findByCode(contractItemDto.getCode());
    	if(item == null)
    		throw new EntityDoesNotExistsException(ContractItem.class, contractItemDto.getCode());
    	
    	if(!Strings.isEmpty(contractItemDto.getContractCode())) {
	    	final Contract contract = contractService.findByCode(contractItemDto.getContractCode());
	    	if(contract == null)
	    		throw new EntityDoesNotExistsException(Contract.class, contractItemDto.getContractCode());
	    	item.setContract(contract);
    	}
    	if(!Strings.isEmpty(contractItemDto.getServiceTemplateCode())) {
	    	item.setServiceTemplate(serviceTemplateService.findByCode(contractItemDto.getServiceTemplateCode()));
    	}
    	if(!Strings.isEmpty(contractItemDto.getPricePlanCode())) {
			item.setPricePlan(pricePlanMatrixService.findByCode(contractItemDto.getPricePlanCode()));
    	} else {
			item.setPricePlan(null);
		}

		
    	if(!Strings.isEmpty(contractItemDto.getProductCode()))
    		item.setProduct(productService.findByCode(contractItemDto.getProductCode()));
    	if(!Strings.isEmpty(contractItemDto.getChargeTemplateCode()))
    		item.setChargeTemplate(chargeTemplateService.findByCode(contractItemDto.getChargeTemplateCode()));
    	item.setRate(contractItemDto.getRate());
    	item.setAmountWithoutTax(contractItemDto.getAmountWithoutTax());

    	if(contractItemDto.getAmountWithoutTax() != null) {
    		item.setContractRateType(ContractRateTypeEnum.FIXED);
    	}else {
        	item.setContractRateType(contractItemDto.getContractRateType());
    	}
    	
    	if(contractItemDto.getSeperateDiscountLine()!=null) {
    		item.setSeparateDiscount(contractItemDto.getSeperateDiscountLine()); 
    	}
    	if(ContractRateTypeEnum.FIXED.equals(item.getContractRateType()) && item.isSeparateDiscount()){
    		throw new InvalidParameterException("generate separate discount line is valable only for the types 'Global discount' and 'Custom discount grid'");
    	}
    	
    	try {
    		populateCustomFields(contractItemDto.getCustomFields(), item, false);
    		contractItemService.updateContractItem(item);
    	}catch(BusinessException e) {
    		throw new MeveoApiException(e);
    	}
    	
    }

	private void checkPricePlanPeriod(ContractItemDto contractItemDto) {
		if (contractItemDto.getPricePlanCode() == null){
			return;
		}
		PricePlanMatrix pricePlan = pricePlanMatrixService.findByCode(contractItemDto.getPricePlanCode());
		List<PricePlanMatrixVersion> pricePlanMatrixVersions = pricePlanMatrixVersionService.findByPricePlan(pricePlan);
		Contract contract = contractService.findByCode(contractItemDto.getContractCode());
		if (pricePlanMatrixVersions == null || pricePlanMatrixVersions.isEmpty()){
			return;
		}
	}

	public void deleteContractLine(String contractItemCode) {
    	if(Strings.isEmpty(contractItemCode))
    		missingParameters.add("contractCode");
    	handleMissingParameters();
    	contractItemService.deleteContractItem(contractItemCode);
    }
    
    public ContractItemDto getContractLines(String contractItemCode) {
    	final ContractItem item = contractItemService.findByCode(contractItemCode);
    	if(item == null)
    		throw new EntityDoesNotExistsException(ContractItem.class, contractItemCode);
    	ContractItemDto dto = new ContractItemDto(item);
    	dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(item));
    	return dto;
    }
    
	
	
	private void checkParams(ContractItemDto contractItemDto) {
    	if(Strings.isEmpty(contractItemDto.getContractCode()))
    		missingParameters.add("contractCode");
    	if(Strings.isEmpty(contractItemDto.getCode()))
			missingParameters.add("code");
		if(Strings.isEmpty(contractItemDto.getChargeTemplateCode()))
			missingParameters.add("chargeTemplateCode");
		checkPricePlanPeriod(contractItemDto);
    	handleMissingParameters();
	}
	
	
	private void checkParams(ContractDto dto) {
		if(Strings.isEmpty(dto.getCode()))
			missingParameters.add("code");
		if(dto.getContractAccountLevel() == null)
			missingParameters.add("contractAccountLevel");
		if(Strings.isEmpty(dto.getAccountCode()))
			missingParameters.add("accountCode");
		if(dto.getBeginDate() == null)
			missingParameters.add("beginDate");
		if(dto.getEndDate() == null)
			missingParameters.add("endDate");
		
		Set<String> brMessages = new HashSet<>();
		if(dto.getBillingRules() != null) {
			for (BillingRuleDto brDto : dto.getBillingRules()) {
				if(brDto.getPriority() == null) {
					brMessages.add("billingRules.priority");
				}
				
				if(StringUtils.isBlank(brDto.getCriteriaEL())) {
					brMessages.add("billingRules.criteriaEL");
				}
				
				if(StringUtils.isBlank(brDto.getInvoicedBACodeEL())) {
					brMessages.add("billingRules.invoicedBACodeEL");
				}
			}
		}
		
		missingParameters.addAll(new ArrayList<>(brMessages));
		
		handleMissingParameters();
		
	}
}
