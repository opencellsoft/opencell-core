package org.meveo.apiv2.catalog.resource.pricelist;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.meveo.apiv2.catalog.ImmutablePriceList;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Country;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.shared.Title;

public class PriceListMapper extends ResourceMapper<org.meveo.apiv2.catalog.PriceList, PriceList> {

	@Override
	protected org.meveo.apiv2.catalog.PriceList toResource(PriceList entity) {
		return ImmutablePriceList.builder()
				.id(entity.getId())
				.code(entity.getCode())
				.description(entity.getDescription())
				.validFrom(entity.getValidFrom())
				.validUntil(entity.getValidUntil())
				.applicationStartDate(entity.getApplicationStartDate())
				.applicationEndDate(entity.getApplicationEndDate())
				.status(entity.getStatus())
				.brands(getBrandsCodes(entity))
				.customerCategories(getCustomerCategoriesCodes(entity))
				.creditCategories(getCreditCategoriesCodes(entity))
				.countries(getCountriesCodes(entity))
				.currencies(getCurrenciesCodes(entity))
				.legalEntities(getLegalEntitiesCodes(entity))
				.sellers(getSellersCodes(entity))
				.build();	
    }
	
	/**
	 * Get codes from a set of Brands
	 * @param entity Price List
	 * @return a list of brands codes
	 */
	private List<String> getBrandsCodes(PriceList entity) {
		if (entity == null || entity.getBrands() == null) {
			return null;
		}
		return entity.getBrands().stream().map(brand -> brand.getCode()).collect(Collectors.toList());
	}
	
	/**
	 * Get codes from a set of Customer Categories
	 * @param entity Price List
	 * @return a list of customer categories
	 */
	private List<String> getCustomerCategoriesCodes(PriceList entity) {
		if (entity == null || entity.getCustomerCategories() == null) {
			return null;
		}
		return entity.getCustomerCategories().stream().map(customerCategory -> customerCategory.getCode()).collect(Collectors.toList());
	}
	
	/**
	 * Get codes from a set of Credit Categories
	 * @param entity Price List
	 * @return a list of Credit Categories
	 */
	private List<String> getCreditCategoriesCodes(PriceList entity) {
		if (entity == null || entity.getCreditCategories() == null) {
			return null;
		}
		return entity.getCreditCategories().stream().map(creditCategory -> creditCategory.getCode()).collect(Collectors.toList());
	}
	
	/**
	 * Get codes from a set of Countries
	 * @param entity Price List
	 * @return a list of Countries
	 */
	private List<String> getCountriesCodes(PriceList entity) {
		if (entity == null || entity.getCountries() == null) {
			return null;
		}
		return entity.getCountries().stream().map(country -> country.getCode()).collect(Collectors.toList());
	}
	
	/**
	 * Get codes from a set of Currencies
	 * @param entity Price List
	 * @return a list of Currencies
	 */
	private List<String> getCurrenciesCodes(PriceList entity) {
		if (entity == null || entity.getCurrencies() == null) {
			return null;
		}
		return entity.getCurrencies().stream().map(currency -> currency.getCurrencyCode()).collect(Collectors.toList());
	}
	
	/**
	 * Get codes from a set of Legal Entities
	 * @param entity Price List
	 * @return a list of Legal Entities
	 */
	private List<String> getLegalEntitiesCodes(PriceList entity) {
		if (entity == null || entity.getLegalEntities() == null) {
			return null;
		}
		return entity.getLegalEntities().stream().map(legalEntity -> legalEntity.getCode()).collect(Collectors.toList());
	}
	
	/**
	 * Get codes from a set of Sellers
	 * @param entity Price List
	 * @return a list of Sellers
	 */
	private List<String> getSellersCodes(PriceList entity) {
		if (entity == null || entity.getSellers() == null) {
			return null;
		}
		return entity.getSellers().stream().map(seller -> seller.getCode()).collect(Collectors.toList());
	}

	@Override
	protected PriceList toEntity(org.meveo.apiv2.catalog.PriceList resource) {
		var entity = new PriceList();		
		entity.setId(resource.getId());
		entity.setCode(resource.getName());
        entity.setDescription(resource.getDescription());
        entity.setValidFrom(resource.getValidFrom());
        entity.setValidUntil(resource.getValidUntil());
        entity.setApplicationStartDate(resource.getApplicationStartDate());
        entity.setApplicationEndDate(resource.getApplicationEndDate());
        entity.setStatus(resource.getStatus());
        entity.setBrands(getBrandsFromCodes(resource.getBrands()));
        entity.setCustomerCategories(getCustomerCategoriesFromCodes(resource.getCustomerCategories()));
        entity.setCountries(getCountriesFromCodes(resource.getCountries()));
        entity.setCreditCategories(getCreditCategoriesFromCodes(resource.getCreditCategories()));
        entity.setCurrencies(getCurrenciesFromCodes(resource.getCurrencies()));
        entity.setLegalEntities(getLegalEntitiesFromCodes(resource.getLegalEntities()));
        entity.setSellers(getSellersFromCodes(resource.getSellers()));
        return entity;
	}
	
	/**
	 * Get a set of {@link CustomerBrand} from codes
	 * @param customerBrandCodes customer brand codes
	 * @return Set of {@link CustomerBrand}
	 */
	private Set<CustomerBrand> getBrandsFromCodes(List<String> customerBrandCodes) {
		if (customerBrandCodes == null) {
			return null;
		}
		
		return customerBrandCodes.stream().map(code -> {
			CustomerBrand customerBrand = new CustomerBrand();
			customerBrand.setCode(code);
			return customerBrand;
		}).collect(Collectors.toSet());
	}
	
	/**
	 * Get a set of {@link CustomerCategory} from codes
	 * @param customerCategoryCodes customer category codes
	 * @return Set of {@link CustomerCategory}
	 */
	private Set<CustomerCategory> getCustomerCategoriesFromCodes(List<String> customerCategoryCodes) {
		if (customerCategoryCodes == null) {
			return null;
		}

		return customerCategoryCodes.stream().map(code -> {
			CustomerCategory customerCategory = new CustomerCategory();
			customerCategory.setCode(code);
			return customerCategory;
		}).collect(Collectors.toSet());
	}
	
	/**
	 * Get a set of {@link Country} from codes
	 * @param countryCodes country category codes
	 * @return Set of {@link Country}
	 */
	private Set<Country> getCountriesFromCodes(List<String> countryCodes) {
		if (countryCodes == null) {
			return null;
		}

		return countryCodes.stream().map(code -> {
			Country country = new Country();
			country.setCountryCode(code);
			country.setCode(code);
			return country;
		}).collect(Collectors.toSet());
	}
	
	/**
	 * Get a set of {@link CreditCategory} from codes
	 * @param creditCategoriesCodes credit category codes
	 * @return Set of {@link CreditCategory}
	 */
	private Set<CreditCategory> getCreditCategoriesFromCodes(List<String> creditCategoriesCodes) {
		if (creditCategoriesCodes == null) {
			return null;
		}

		return creditCategoriesCodes.stream().map(code -> {
			CreditCategory creditCategory = new CreditCategory();
			creditCategory.setCode(code);
			return creditCategory;
		}).collect(Collectors.toSet());
	}
	
	/**
	 * Get a set of {@link Currency} from codes
	 * @param currenciesCodes currency codes
	 * @return Set of {@link Currency}
	 */
	private Set<Currency> getCurrenciesFromCodes(List<String> currenciesCodes) {
		if (currenciesCodes == null) {
			return null;
		}

		return currenciesCodes.stream().map(code -> {
			Currency currency = new Currency();
			currency.setCurrencyCode(code);
			return currency;
		}).collect(Collectors.toSet());
	}
	
	/**
	 * Get a set of {@link Title} from codes
	 * @param legalEntitiesCodes legal entity codes
	 * @return Set of {@link Title}
	 */
	private Set<Title> getLegalEntitiesFromCodes(List<String> legalEntitiesCodes) {
		if (legalEntitiesCodes == null) {
			return null;
		}

		return legalEntitiesCodes.stream().map(code -> {
			Title title = new Title();
			title.setCode(code);
			return title;
		}).collect(Collectors.toSet());
	}
		
	/**
	 * Get a set of {@link Seller} from codes
	 * @param sellersCodes seller codes
	 * @return Set of {@link Seller}
	 */
	private Set<Seller> getSellersFromCodes(List<String> sellersCodes) {
		if (sellersCodes == null) {
			return null;
		}
	
		return sellersCodes.stream().map(code -> {
			Seller seller = new Seller();
			seller.setCode(code);
			return seller;
		}).collect(Collectors.toSet());
	}
}
