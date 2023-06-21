package org.meveo.apiv2.catalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import org.hibernate.collection.spi.PersistentCollection;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.exception.*;
import org.meveo.apiv2.catalog.resource.pricelist.PriceListMapper;
import org.meveo.commons.utils.ListUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Country;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.model.report.query.SortOrderEnum;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.shared.RegexUtils;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.catalog.impl.PriceListCriteria;
import org.meveo.service.catalog.impl.PriceListLineService;
import org.meveo.service.catalog.impl.PriceListService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

@Stateless
public class PriceListApiService extends BaseApi {

    @Inject
    private PriceListService priceListService;

    @Inject
    private PriceListLineService priceListLineService;

    @Inject
    private CustomerBrandService customerBrandService;
    
    @Inject
    private CustomerCategoryService customerCategoryService;
    
    @Inject
    private CountryService countryService;
    
    @Inject
    private CreditCategoryService creditCategoryService;
    
    @Inject 
    private CurrencyService currencyService;
    
    @Inject
    private TitleService titleService;
    
    @Inject
    private SellerService sellerService;
    
    @Inject
	private FinanceSettingsService financeSettingsService;
    
    @Inject
    private BillingAccountService billingAccountService;
    
    private PriceListMapper mapper = new PriceListMapper();

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    /**
     * Create a price list
     * @param priceList {@link PriceList}
     * @return {@link PriceList}
     */
    public PriceList create(PriceList priceList, CustomFieldsDto customFields) {
        if (priceListService.findByCode(priceList.getCode()) != null) {
            throw new EntityAlreadyExistsException(PriceList.class, priceList.getCode());
        }
        
        setDefaultValues(priceList);
        validateMandatoryFields(priceList);
        validateApplicationRules(priceList);

        try {
            populateCustomFields(customFields, priceList, true);
            priceListService.create(priceList);
        } catch(BusinessException e) {
            throw new MeveoApiException(e);
        }

        return priceList;
    }

    /**
     * Update a price list
     *
     * @param priceList     {@link PriceList}
     * @param priceListCode Price List Code
     * @param customFields
     * @return {@link PriceList}
     */
    public Optional<PriceList> update(PriceList priceList, String priceListCode, CustomFieldsDto customFields) {
    	PriceList priceListToUpdate = Optional.ofNullable(priceListService.findByCode(priceListCode)).orElseThrow(() -> new EntityDoesNotExistsException(PriceList.class, priceListCode));    	
    	if(!PriceListStatusEnum.DRAFT.equals(priceListToUpdate.getStatus())) {
             throw new BusinessApiException("Updating a PriceList other than DRAFT is not allowed");
        }
    	setDefaultValues(priceList);
    	validateMandatoryFields(priceList);
    	validateApplicationRules(priceList);
    	updatePriceListFields(priceList, priceListToUpdate);
    	updatePriceListFieldLists(priceList, priceListToUpdate);

        try {
            populateCustomFields(customFields, priceListToUpdate, true);
            priceListService.update(priceListToUpdate);
        } catch(BusinessException e) {
            throw new MeveoApiException(e);
        }

        return Optional.ofNullable(priceListToUpdate);
    }

    /**
     * Delete a Price List
     * @param priceListCode Price List Id
     * @return {@link PriceList}
     */
    public Optional<PriceList> delete(String priceListCode) {
    	PriceList priceList = Optional.ofNullable(priceListService.findByCode(priceListCode)).orElseThrow(() -> new EntityDoesNotExistsException(PriceList.class, priceListCode));
    	priceListService.remove(priceList);
        return Optional.ofNullable(priceList);
    }

    @TransactionAttribute
    public void updateStatus(String priceListCode, PriceListStatusEnum newStatus) {

        PriceList priceListToUpdate = priceListService.findByCode(priceListCode);
        if(priceListToUpdate == null) {
            throw new EntityDoesNotExistsException(PriceList.class, priceListCode);
        }

        switch (newStatus) {
            case ACTIVE:
                if(priceListToUpdate.getStatus() != PriceListStatusEnum.DRAFT) {
                    throw new BusinessApiException("Only DRAFT PriceList are eligible to ACTIVE status");
                }

                if(ListUtils.isEmtyCollection(priceListToUpdate.getLines())) {
                    throw new BusinessApiException("Cannot activate PriceList without lines");
                }

                priceListToUpdate.getLines()
                                 .stream()
                                 .filter(pll -> pll.getRate() != null || pll.getAmount() != null || (pll.getPricePlan()!= null && !pll.getPricePlan().getVersions().isEmpty()  && pll.getPricePlan()
                                                                                                          .getVersions()
                                                                                                          .stream()
                                                                                                          .anyMatch(ppv -> ppv.getStatus().equals(VersionStatusEnum.PUBLISHED))))
                                 .findAny()
                                 .orElseThrow(() -> new BusinessApiException("Cannot activate PriceList without lines having a price or active PricePlan"));
                if (ListUtils.isEmtyCollection(priceListToUpdate.getBrands())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getCustomerCategories())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getCreditCategories())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getCountries())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getCurrencies())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getLegalEntities())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getPaymentMethods())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getSellers())
                ) {
                    throw new BusinessApiException("Cannot activate PriceList without application rules");
                }
                break;
            case CLOSED:
                if(priceListToUpdate.getStatus() != PriceListStatusEnum.ACTIVE) {
                    throw new BusinessApiException("Only ACTIVE PriceList are eligible to CLOSED status");
                }
                break;
            case ARCHIVED:
                if(priceListToUpdate.getStatus() != PriceListStatusEnum.DRAFT) {
                    throw new BusinessApiException("Only DRAFT PriceList are eligible to ARCHIVED status");
                }
                break;

            default:
                throw new BusinessApiException("Unsupported status");

        }

        priceListToUpdate.setStatus(newStatus);
        priceListService.update(priceListToUpdate);

    }
    
    /**
     * Set default values
     * @param priceList {@link PriceList}
     */
    private void setDefaultValues(PriceList priceList) {
    	if(priceList.getStatus() == null) {
    		priceList.setStatus(PriceListStatusEnum.DRAFT);
    	}
    }
    
    /**
     * Validate Mandatory fields
     * @param priceList {@link PriceList}
     */
    private void validateMandatoryFields(PriceList priceList) {
    	if (priceList.getCode() == null) {
			throw new BusinessApiException("The code field is mandatory");
    	} else if(priceList.getCode().length() > 50) {
    		throw new BusinessApiException("The code must be 50 characters or less");
    	}

        if (!StringUtils.isBlank(priceList.getCode()) && !RegexUtils.checkCode(priceList.getCode())) {
            throw new BusinessApiException("PriceList code should not contain special characters");
        }
    	
    	if(priceList.getDescription() != null && !priceList.getDescription().isEmpty() && priceList.getDescription().length() > 255) {
    		throw new BusinessApiException("The description must be 255 characters or less");
    	}
    	
    	if (priceList.getValidFrom() == null) {
			throw new BusinessApiException("The validFrom field is mandatory");
    	}
    	
    	if (priceList.getValidUntil() == null) {
			throw new BusinessApiException("The validUntil field is mandatory");
    	}
    	
    	if (priceList.getApplicationStartDate() == null) {
			throw new BusinessApiException("The applicationStartDate field is mandatory");
    	}
    	
    	if (priceList.getApplicationEndDate() == null) {
			throw new BusinessApiException("The applicationEndDate field is mandatory");
    	}
    	
    	if (priceList.getApplicationStartDate().before(priceList.getValidFrom())) {
			throw new BusinessApiException("The applicationStartDate should be greater or equal the validFrom Date");
    	}
    	
    	if (priceList.getApplicationEndDate().after(priceList.getValidUntil())) {
			throw new BusinessApiException("The applicationEndDate should be lower or equal the validUntil Date");
    	}
    	
    	if (priceList.getValidFrom().after(priceList.getValidUntil())) {
			throw new BusinessApiException("The validFrom date should be lower than the validUntil Date");
    	}
    	
    	if (priceList.getApplicationStartDate().after(priceList.getApplicationEndDate())) {
			throw new BusinessApiException("The applicationStartDate should be lower than the applicationEndDate Date");
    	}
    }
    
    /**
     * Validate Application rules
     * @param priceList {@link PriceList}
     */
    private void validateApplicationRules(PriceList priceList) {
    	if (priceList.getBrands() != null && !(priceList.getBrands() instanceof PersistentCollection)) {
            Optional<CustomerBrand> unfoundCustomerBrand = priceList.getBrands().stream().filter(customerBrand -> customerBrandService.findByCode(customerBrand.getCode()) == null).findFirst();
            
            if (unfoundCustomerBrand.isPresent()) {
                throw new EntityDoesNotExistsException(CustomerBrand.class, unfoundCustomerBrand.get().getCode());
            }
            
            priceList.setBrands(priceList.getBrands().stream().map(customerBrand -> customerBrandService.findByCode(customerBrand.getCode())).collect(Collectors.toSet()));
        }
    	
    	if (priceList.getCustomerCategories() != null && !(priceList.getCustomerCategories() instanceof PersistentCollection)) {
            Optional<CustomerCategory> unfoundCustomerCategories = priceList.getCustomerCategories().stream().filter(customerCategory -> customerCategoryService.findByCode(customerCategory.getCode()) == null).findFirst();
            
            if (unfoundCustomerCategories.isPresent()) {
                throw new EntityDoesNotExistsException(CustomerCategory.class, unfoundCustomerCategories.get().getCode());
            }
            
            priceList.setCustomerCategories(priceList.getCustomerCategories().stream().map(customerCategory -> customerCategoryService.findByCode(customerCategory.getCode())).collect(Collectors.toSet()));
        }
    	
    	if (priceList.getCountries() != null && !(priceList.getCountries() instanceof PersistentCollection)) {
            Optional<Country> unfoundCountries = priceList.getCountries().stream().filter(country -> countryService.findByCode(country.getCode()) == null).findFirst();
            
            if (unfoundCountries.isPresent()) {
                throw new EntityDoesNotExistsException(Country.class, unfoundCountries.get().getCode());
            }
            
            priceList.setCountries(priceList.getCountries().stream().map(country -> countryService.findByCode(country.getCode())).collect(Collectors.toSet()));
        }
    	
    	if (priceList.getCreditCategories() != null && !(priceList.getCreditCategories() instanceof PersistentCollection)) {
            Optional<CreditCategory> unfoundCreditCategories = priceList.getCreditCategories().stream().filter(creditCategory -> creditCategoryService.findByCode(creditCategory.getCode()) == null).findFirst();
            
            if (unfoundCreditCategories.isPresent()) {
                throw new EntityDoesNotExistsException(CreditCategory.class, unfoundCreditCategories.get().getCode());
            }
            
            priceList.setCreditCategories(priceList.getCreditCategories().stream().map(creditCategory -> creditCategoryService.findByCode(creditCategory.getCode())).collect(Collectors.toSet()));
        }
    	
    	if (priceList.getCurrencies() != null && !(priceList.getCurrencies() instanceof PersistentCollection)) {
            Optional<Currency> unfoundCurrency = priceList.getCurrencies().stream().filter(action -> currencyService.findByCode(action.getCurrencyCode()) == null).findFirst();
            
            if (unfoundCurrency.isPresent()) {
                throw new EntityDoesNotExistsException(Currency.class, unfoundCurrency.get().getCurrencyCode());
            }
            
            priceList.setCurrencies(priceList.getCurrencies().stream().map(currency -> currencyService.findByCode(currency.getCurrencyCode())).collect(Collectors.toSet()));
        }
    	
    	if (priceList.getLegalEntities() != null && !(priceList.getLegalEntities() instanceof PersistentCollection)) {
            Optional<Title> unfoundLegalEntities = priceList.getLegalEntities().stream().filter(legalEntity -> titleService.findByCode(legalEntity.getCode()) == null).findFirst();
            
            if (unfoundLegalEntities.isPresent()) {
                throw new EntityDoesNotExistsException(Title.class, unfoundLegalEntities.get().getCode());
            }
            
            priceList.setLegalEntities(priceList.getLegalEntities().stream().map(legalEntity -> titleService.findByCode(legalEntity.getCode())).collect(Collectors.toSet()));
        }
    	
    	if (priceList.getSellers() != null && !(priceList.getSellers() instanceof PersistentCollection)) {
            Optional<Seller> unfoundSellers = priceList.getSellers().stream().filter(seller -> sellerService.findByCode(seller.getCode()) == null).findFirst();
            
            if (unfoundSellers.isPresent()) {
                throw new EntityDoesNotExistsException(Seller.class, unfoundSellers.get().getCode());
            }
            
            priceList.setSellers(priceList.getSellers().stream().map(seller -> sellerService.findByCode(seller.getCode())).collect(Collectors.toSet()));
        }
    }
    
    /**
     * Update Price List Fields
     * @param priceList New Price List
     * @param priceListToUpdate Price List to update
     */
    private void updatePriceListFields(PriceList priceList, PriceList priceListToUpdate) {
    	if (StringUtils.isNotBlank(priceList.getCode()) && !priceList.getCode().equals(priceListToUpdate.getCode())) {
            if (priceListService.findByCode(priceList.getCode()) != null) {
                throw new EntityAlreadyExistsException(PriceList.class, priceList.getCode());
            }

            priceListToUpdate.setCode(priceList.getCode());
        }
    	
    	if (priceList.getDescription() != null) {
    		priceListToUpdate.setDescription(priceList.getDescription());
        }
    	
    	if (priceList.getValidFrom() != null) {
    		priceListToUpdate.setValidFrom(priceList.getValidFrom());
        }
    	
    	if (priceList.getValidUntil() != null) {
    		priceListToUpdate.setValidUntil(priceList.getValidUntil());
        }
    	
    	if (priceList.getApplicationStartDate() != null) {
    		priceListToUpdate.setApplicationStartDate(priceList.getApplicationStartDate());
        }
    	
    	if (priceList.getApplicationEndDate() != null) {
    		priceListToUpdate.setApplicationEndDate(priceList.getApplicationEndDate());
        }
    	
    	if (priceList.getStatus() != null) {
    		priceListToUpdate.setStatus(priceList.getStatus());
        }
    	
    	if (priceList.getBrands() != null) {
    		priceListToUpdate.setBrands(priceList.getBrands());
        }
    }
    
    /**
     * Update Price List Field (List fields)
     * @param priceList New Price List
     * @param priceListToUpdate Price List to update
     */
    private void updatePriceListFieldLists(PriceList priceList, PriceList priceListToUpdate) {
    	if (priceList.getBrands() != null) {
    		priceListToUpdate.setBrands(priceList.getBrands());
        }
    	
    	if (priceList.getCustomerCategories() != null) {
    		priceListToUpdate.setCustomerCategories(priceList.getCustomerCategories());
        }
    	
    	if (priceList.getCountries() != null) {
    		priceListToUpdate.setCountries(priceList.getCountries());
        }
    	
    	if (priceList.getCreditCategories() != null) {
    		priceListToUpdate.setCreditCategories(priceList.getCreditCategories());
        }
    	
    	if (priceList.getCurrencies() != null) {
    		priceListToUpdate.setCurrencies(priceList.getCurrencies());
        }
    	
    	if (priceList.getLegalEntities() != null) {
    		priceListToUpdate.setLegalEntities(priceList.getLegalEntities());
        }
    	
    	if (priceList.getSellers() != null) {
    		priceListToUpdate.setSellers(priceList.getSellers());
        }
    	
    	if (priceList.getPaymentMethods() != null) {
    		priceListToUpdate.setPaymentMethods(priceList.getPaymentMethods());
        }
    }
    
    /**
     * Get Price List with criteria
     * @param pOffset Offset
     * @param pLimit Limit
     * @param pSortOrder SortOrder
     * @param pSortBy SortBy
     * @param pBillingAccountCode Billing Account Code
     * @return A list of {@link PriceList}
     */
    public List<org.meveo.apiv2.catalog.PriceList> getPriceList(Long pOffset, Long pLimit, String pSortOrder, String pSortBy, String pBillingAccountCode) {
    	//Check if the PriceList is enabled or not
    	getAndCheckPriceListActivation();
    	
    	if(pSortBy != null && pSortBy.isBlank()) {
    		pSortBy = null;
    	}
    	
    	checkSortByField(pSortBy);
    	
    	//Get and check the existence of BA
    	BillingAccount lBillingAccount = getBillingAccount(pBillingAccountCode);
    	
    	//Get SortOrder
    	pSortOrder = pSortOrder == null ? SortOrderEnum.DESCENDING.getLabel() : SortOrderEnum.valueOf(pSortOrder).getLabel();
    	
    	//Get PriceList
    	List<PriceList> priceList = new ArrayList<>();
    	priceList.addAll(priceListService.getPriceList(buildPriceListCriteria(pOffset, pLimit, pSortOrder, pSortBy, getCustomerBrandId(lBillingAccount), getCustomerCategoryId(lBillingAccount), 
    			getCreditCategoryId(lBillingAccount), getTradingCountryId(lBillingAccount), getTradingCurrencyId(lBillingAccount), getLegalEntityTypeId(lBillingAccount),
    			getPaymentMethodId(lBillingAccount), getSellerId(lBillingAccount), lBillingAccount.getPriceList() != null ? lBillingAccount.getPriceList().getId() : null)));
    	
    	//Convert PriceList
    	return priceList.stream().map(s -> mapper.toResource(s, false)).collect(Collectors.toList());
    }
    
    /**
	 * Build Price List using criteria
	 * @param pOffset Offset
	 * @param pLimit Limit
	 * @param pSortOrder SortOrder
	 * @param pSortBy Sort By
	 * @param pBrandId Customer Brand Id
	 * @param pCustomerCategoryId Customer Category Id
	 * @param pCreditCategoryId Credit Category Id
	 * @param pCountryId Country Id
	 * @param pCurrencyId Currency Id
	 * @param pTitleId Legal Entity Type Id
	 * @param pPaymentMethodEnum Payment Method 
	 * @param pSellerId Seller id
	 * @param pAttachedPriceListId Attached Price List to Billing Account 
	 * @return List of {@link PriceList}
	 */
    private PriceListCriteria buildPriceListCriteria(Long pOffset, Long pLimit, String pSortOrder, String pSortBy, Long pBrandId, Long pCustomerCategoryId, Long pCreditCategoryId, 
			Long pCountryId, Long pCurrencyId, Long pTitleId, PaymentMethodEnum pPaymentMethodEnum, Long pSellerId, Long pAttachedPriceListId) {
    	PriceListCriteria lPriceListCriteria = new PriceListCriteria();
    	lPriceListCriteria.setOffset(pOffset);
    	lPriceListCriteria.setLimit(pLimit);
    	lPriceListCriteria.setSortOrder(pSortOrder);
    	lPriceListCriteria.setSortBy(pSortBy);
    	lPriceListCriteria.setBrandId(pBrandId);
    	lPriceListCriteria.setCustomerCategoryId(pCustomerCategoryId);
    	lPriceListCriteria.setCreditCategoryId(pCreditCategoryId);
    	lPriceListCriteria.setCountryId(pCountryId);
    	lPriceListCriteria.setCurrencyId(pCurrencyId);
    	lPriceListCriteria.setTitleId(pTitleId);
    	lPriceListCriteria.setPaymentMethodEnum(pPaymentMethodEnum);
    	lPriceListCriteria.setSellerId(pSellerId);
    	lPriceListCriteria.setAttachedPriceListId(pAttachedPriceListId);
    	return lPriceListCriteria;
	}

    /**
     * Check sort by field
     * @param pSortBy
     */
	private void checkSortByField(String pSortBy) {
        List<String> lFieldNames = new ArrayList<>();
        lFieldNames.add("code");
        lFieldNames.add("validFrom");
        lFieldNames.add("validUntil");
        lFieldNames.add("applicationStartDate");
		lFieldNames.add("applicationEndDate");
		lFieldNames.add("status");
        
        if(pSortBy != null && !pSortBy.isEmpty() && !lFieldNames.contains(pSortBy)) {
        	throw new BusinessApiException("The field (" + pSortBy + ") is not allowed in sortBy, only the following fields are authorized: " + lFieldNames.toString());
        }		
	}

	/**
     * Count Price List by Criteria
     * @param pSortOrder SortOrder
     * @param pOrderBy OrderBy
     * @param pBillingAccountCode BillingAccountCode
     * @return Count of Price List
     */
    public Long count(String pSortOrder, String pOrderBy, String pBillingAccountCode) {
    	//Get SortOrder
    	pSortOrder = pSortOrder == null ? SortOrderEnum.DESCENDING.getLabel() : SortOrderEnum.valueOf(pSortOrder).getLabel();

    	//Get and check the existence of BA
    	BillingAccount lBillingAccount = getBillingAccount(pBillingAccountCode);
    	
    	//Count PriceList
    	return priceListService.count(buildPriceListCriteria(null, null, pSortOrder, pOrderBy, getCustomerBrandId(lBillingAccount), getCustomerCategoryId(lBillingAccount), 
    			getCreditCategoryId(lBillingAccount), getTradingCountryId(lBillingAccount), getTradingCurrencyId(lBillingAccount), getLegalEntityTypeId(lBillingAccount),
    			getPaymentMethodId(lBillingAccount), getSellerId(lBillingAccount), lBillingAccount.getPriceList() != null ? lBillingAccount.getPriceList().getId() : null));
    }
	
    /**
     * Check if PriceList mode is activated
     */
	private void getAndCheckPriceListActivation() {
    	//Get FinanceSettings t
    	FinanceSettings lFinanceSettings = financeSettingsService.getFinanceSetting();
    	
    	//Check if the PLi is activated or not
    	if(!lFinanceSettings.isEnablePriceList()) {
    		throw new BusinessApiException("The PriceList is not enabled");
    	}
    }
	
	/**
	 * Get Billing account
	 * @param pBillingAccountCode Billing Account Code
	 * @return {@link BillingAccount}
	 */
    private BillingAccount getBillingAccount(String pBillingAccountCode) {
		BillingAccount billingAccount = billingAccountService.findByCode(pBillingAccountCode);
		
		if(billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, pBillingAccountCode);
		} else {
			return billingAccount;
		}
	}
    
    /**
     * Get Customer Brand Id
     * @param pBillingAccountCode Billing Account Code
     * @return Customer Brand Id
     */
    private Long getCustomerBrandId(BillingAccount pBillingAccountCode) {
    	if(pBillingAccountCode.getCustomerAccount() != null && pBillingAccountCode.getCustomerAccount().getCustomer() != null && pBillingAccountCode.getCustomerAccount().getCustomer().getCustomerBrand() != null) {
    		return pBillingAccountCode.getCustomerAccount().getCustomer().getCustomerBrand().getId();		
    	} else {
    		return null;
    	}
    }
    
    /**
     * Get Customer Category Id
     * @param pBillingAccountCode Billing Account Code
     * @return Customer Category Id
     */
    private Long getCustomerCategoryId(BillingAccount pBillingAccountCode) {
    	if(pBillingAccountCode.getCustomerAccount() != null && pBillingAccountCode.getCustomerAccount().getCustomer() != null && pBillingAccountCode.getCustomerAccount().getCustomer().getCustomerCategory() != null) {
    		return pBillingAccountCode.getCustomerAccount().getCustomer().getCustomerCategory().getId();
    	} else {
    		return null;
    	}
    }
    
    /**
     * Get Credit Category Id
     * @param pBillingAccountCode Billing Account Code
     * @return Customer Category Id
     */
    private Long getCreditCategoryId(BillingAccount pBillingAccountCode) {
    	if(pBillingAccountCode.getCustomerAccount() != null && pBillingAccountCode.getCustomerAccount().getCreditCategory() != null) {
    		return pBillingAccountCode.getCustomerAccount().getCreditCategory().getId();
    	} else {
    		return null;
    	}
    }
    
    /**
     * Get Trading Country Id
     * @param pBillingAccountCode Billing Account Code
     * @return Trading Country Id
     */
    private Long getTradingCountryId(BillingAccount pBillingAccountCode) {
    	if(pBillingAccountCode.getTradingCountry() != null && pBillingAccountCode.getTradingCountry().getCountry() != null) {
    		return pBillingAccountCode.getTradingCountry().getCountry().getId();
    	} else {
    		return null;
    	}
    }
    
    /**
     * Get Trading Currency Id
     * @param pBillingAccountCode Billing Account Code
     * @return Trading Currency Id
     */
    private Long getTradingCurrencyId(BillingAccount pBillingAccountCode) {
    	if(pBillingAccountCode.getTradingCurrency() != null && pBillingAccountCode.getTradingCurrency().getCurrency() != null) {
    		return pBillingAccountCode.getTradingCurrency().getCurrency().getId();
    	} else {
    		return null;
    	}
    }
    
    /**
     * Get Legal Entity Type Id
     * @param pBillingAccountCode Billing Account Code 
     * @return Legal Entity Type Id
     */
    private Long getLegalEntityTypeId(BillingAccount pBillingAccountCode) {
    	if(pBillingAccountCode.getLegalEntityType() != null) {
    		return pBillingAccountCode.getLegalEntityType().getId();
    	} else {
    		return null;
    	}
    }
    
    /**
     * Get Payment Method 
     * @param pBillingAccountCode Billing Account Code
     * @return Payment Method
     */
    private PaymentMethodEnum getPaymentMethodId(BillingAccount pBillingAccountCode) {
    	if(pBillingAccountCode.getPaymentMethod() != null) {
    		return pBillingAccountCode.getPaymentMethod().getPaymentType();
    	} else {
    		return null;
    	}
    }
    
    /**
     * Get Seller Id
     * @param pBillingAccountCode Billing Account Code
     * @return Seller Id
     */
    private Long getSellerId(BillingAccount pBillingAccountCode) {
    	if(pBillingAccountCode.getCustomerAccount() != null && pBillingAccountCode.getCustomerAccount().getCustomer() != null && pBillingAccountCode.getCustomerAccount().getCustomer().getSeller() != null) {
    		return pBillingAccountCode.getCustomerAccount().getCustomer().getSeller().getId();
    	} else {
    		return null;
    	}
    }

    @TransactionAttribute
    public PriceList duplicate(String priceListCode) {
        if(StringUtils.isBlank(priceListCode)) {
            throw new MissingParameterException("priceListCode");
        }

        PriceList priceList = priceListService.findByCode(priceListCode);
        if(priceList == null) {
            throw new EntityDoesNotExistsException(PriceList.class, priceListCode);
        }

        PriceList duplicatedPriceList = new PriceList();

        duplicatedPriceList.setCode(priceListService.findDuplicateCode(priceList, "-COPY"));
        duplicatedPriceList.setDescription(priceList.getDescription());
        duplicatedPriceList.setApplicationStartDate(priceList.getApplicationStartDate());
        duplicatedPriceList.setApplicationEndDate(priceList.getApplicationEndDate());
        duplicatedPriceList.setValidFrom(priceList.getValidFrom());
        duplicatedPriceList.setValidUntil(priceList.getValidUntil());

        duplicatedPriceList.setCurrencies(new HashSet<>(priceList.getCurrencies()));
        duplicatedPriceList.setCountries(new HashSet<>(priceList.getCountries()));
        duplicatedPriceList.setBrands(new HashSet<>(priceList.getBrands()));
        duplicatedPriceList.setLegalEntities(new HashSet<>(priceList.getLegalEntities()));
        duplicatedPriceList.setSellers(new HashSet<>(priceList.getSellers()));
        duplicatedPriceList.setCreditCategories(new HashSet<>(priceList.getCreditCategories()));
        duplicatedPriceList.setCustomerCategories(new HashSet<>(priceList.getCustomerCategories()));
        duplicatedPriceList.setPaymentMethods(new HashSet<>(priceList.getPaymentMethods()));

        duplicatedPriceList.setStatus(PriceListStatusEnum.DRAFT);
        duplicatedPriceList.setLines(new HashSet<>());

        if (priceList.getLines() != null) {
            for (PriceListLine line : priceList.getLines()) {
                PriceListLine duplicatedLine = new PriceListLine();
                duplicatedLine.setCode(line.getCode() + "-COPY");
                duplicatedLine.setPriceList(duplicatedPriceList);
                duplicatedLine.setOfferCategory(line.getOfferCategory());
                duplicatedLine.setOfferTemplate(line.getOfferTemplate());
                duplicatedLine.setProductCategory(line.getProductCategory());
                duplicatedLine.setProduct(line.getProduct());
                duplicatedLine.setChargeTemplate(line.getChargeTemplate());
                duplicatedLine.setPriceListType(line.getPriceListType());
                duplicatedLine.setRate(line.getRate());
                duplicatedLine.setAmount(line.getAmount());
                duplicatedLine.setApplicationEl(line.getApplicationEl());

                if(line.getPricePlan() != null) {
                    duplicatedLine.setPricePlan(duplicatePricePlan(line.getPricePlan()));
                }
                priceListLineService.create(duplicatedLine);
                duplicatedPriceList.getLines().add(duplicatedLine);
            }
        }

        duplicatedPriceList.setCfValues(priceList.getCFValuesCopy());

        priceListService.create(duplicatedPriceList);
        return duplicatedPriceList;
    }

    private PricePlanMatrix duplicatePricePlan(PricePlanMatrix pricePlanMatrix) {
        PricePlanMatrix duplicate = new PricePlanMatrix(pricePlanMatrix);
        duplicate.setCode(pricePlanMatrixService.findDuplicateCode(pricePlanMatrix));
        duplicate.setVersion(0);
        duplicate.setVersions(new ArrayList<>());

        pricePlanMatrixService.create(duplicate);

        pricePlanMatrix.getVersions().forEach(ppv -> {
            PricePlanMatrixVersion duplicatedPPMV = pricePlanMatrixVersionService.duplicate(ppv, duplicate, ppv.getValidity(), VersionStatusEnum.DRAFT, ppv.getPriceVersionType(), false, ppv.getCurrentVersion());
            duplicate.getVersions().add(duplicatedPPMV);
        });

        return duplicate;
    }
}