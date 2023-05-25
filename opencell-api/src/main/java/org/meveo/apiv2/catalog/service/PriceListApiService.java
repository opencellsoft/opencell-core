package org.meveo.apiv2.catalog.service;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import org.hibernate.collection.spi.PersistentCollection;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ListUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Country;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.PriceListService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.payments.impl.CreditCategoryService;

@Stateless
public class PriceListApiService extends BaseApi {

    @Inject
    private PriceListService priceListService; 
      
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

    /**
     * Create a price list
     * @param priceList {@link PriceList}
     * @param paymentMethods A list of {@link PaymentMethod}
     * @return {@link PriceList}
     */
    public PriceList create(PriceList priceList) {
        if (priceListService.findByCode(priceList.getCode()) != null) {
            throw new EntityAlreadyExistsException(PriceList.class, priceList.getCode());
        }
        
        setDefaultValues(priceList);
        validateMandatoryFields(priceList);
        validateApplicationRules(priceList);
        priceListService.create(priceList);
        return priceList;
    }
    
    /**
     * Update a price list
     * @param priceList {@link PriceList}
     * @param id Price List id to update
     * @param paymentMethods A list of {@link PaymentMethod}
     * @return {@link PriceList}
     */
    public Optional<PriceList> update(PriceList priceList, String priceListCode) {
    	PriceList priceListToUpdate = Optional.ofNullable(priceListService.findByCode(priceListCode)).orElseThrow(() -> new EntityDoesNotExistsException(PriceList.class, priceListCode));    	
    	if(!PriceListStatusEnum.DRAFT.equals(priceListToUpdate.getStatus())) {
             throw new BusinessApiException("Updating a PriceList other than DRAFT is not allowed");
        }
    	setDefaultValues(priceList);
    	validateMandatoryFields(priceList);
    	validateApplicationRules(priceList);
    	updatePriceListFields(priceList, priceListToUpdate);
    	updatePriceListFieldLists(priceList, priceListToUpdate);
        priceListService.update(priceListToUpdate);
        return Optional.ofNullable(priceList);
    }

    /**
     * Delete a Price List
     * @param id Price List Id
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
                                 .filter(pll -> pll.getRate() != null || (pll.getPricePlan()!= null && !pll.getPricePlan().getVersions().isEmpty()  && pll.getPricePlan()
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
     * @param paymentMethods A list of {@link PaymentMethod}
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
}