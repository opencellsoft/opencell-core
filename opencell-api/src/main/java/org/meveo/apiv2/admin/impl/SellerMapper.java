package org.meveo.apiv2.admin.impl;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.meveo.api.dto.account.RegistrationNumberDto;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.admin.ImmutableAddress;
import org.meveo.apiv2.admin.ImmutableContactInformation;
import org.meveo.apiv2.admin.ImmutableCustomerSequence;
import org.meveo.apiv2.admin.ImmutableInvoiceTypeSellerSequence;
import org.meveo.apiv2.admin.ImmutableSeller;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.RegistrationNumber;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.IsoIcd;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.cpq.Media;
import org.meveo.model.crm.CustomerSequence;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.RegistrationNumberService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceSequenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.IsoIcdService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.CustomerSequenceService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.cpq.MediaService;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

public class SellerMapper extends ResourceMapper<org.meveo.apiv2.admin.Seller, Seller> {
	
	private final TradingCurrencyService tradingCurrencyService = (TradingCurrencyService) getServiceInterface(TradingCurrencyService.class.getSimpleName());
	private final TradingCountryService tradingCountryService = (TradingCountryService) getServiceInterface(TradingCountryService.class.getSimpleName());
	private final TradingLanguageService tradingLanguageService = (TradingLanguageService) getServiceInterface(TradingLanguageService.class.getSimpleName());
	private final MediaService mediaService = (MediaService) getServiceInterface(MediaService.class.getSimpleName());
	private final CountryService countryService = (CountryService) getServiceInterface(CountryService.class.getSimpleName());
	private final InvoiceSequenceService invoiceSequenceService = (InvoiceSequenceService) getServiceInterface(InvoiceSequenceService.class.getSimpleName());
	private final InvoiceTypeService invoiceTypeService = (InvoiceTypeService) getServiceInterface(InvoiceTypeService.class.getSimpleName());
	private final CustomerSequenceService customerSequenceService = (CustomerSequenceService) getServiceInterface(CustomerSequenceService.class.getSimpleName());
	private final TitleService titleService = (TitleService) getServiceInterface(TitleService.class.getSimpleName());
	private final RegistrationNumberService registrationNumberService = (RegistrationNumberService) getServiceInterface(RegistrationNumberService.class.getSimpleName());
	private final IsoIcdService isoIcdService = (IsoIcdService) getServiceInterface(IsoIcdService.class.getSimpleName());
	
	private final SellerService sellerService = (SellerService) getServiceInterface(SellerService.class.getSimpleName());
	protected org.meveo.apiv2.admin.Seller toResource(Seller entity) {
		return ImmutableSeller.builder()
				.id(entity.getId())
				.code(entity.getCode())
				.description(entity.getDescription())
				.vatNumber(entity.getVatNo())
				.registrationNumbers(entity.getRegistrationNumbers().stream().map(RegistrationNumberDto::new).collect(Collectors.toSet()))
				.legalType(entity.getLegalEntityType() != null ? getTitle(entity.getLegalEntityType()) : null)
				.countryCode(entity.getTradingCountry() != null ? entity.getTradingCountry().getCode() : null)
				.currencyCode(entity.getTradingCurrency() != null ? entity.getTradingCurrency().getCurrencyCode() : null)
				.languageCode(entity.getTradingLanguage() != null ? entity.getTradingLanguage().getLanguageCode() : null)
				.address(getAddress(entity))
				.contactInformation(getContactInfo(entity.getContactInformation()))
				.mediaCodes(CollectionUtils.isNotEmpty(entity.getMedias()) ? entity.getMedias().stream().map(Media::getCode).collect(Collectors.toList()) : Collections.emptyList())
				.invoiceTypeSellerSequence(getInvoiceTypeSellerSequences(entity.getInvoiceTypeSequence()))
				.customerSequence(getCustomerSequence(entity.getCustomerSequences()))
				.build();
	}
	private TitleDto getTitle(Title title) {
		TitleDto titleDto = new TitleDto();
		titleDto.setDescription(title.getDescription());
		titleDto.setIsCompany(title.getIsCompany());
		titleDto.setCode(title.getCode());
		return titleDto;
	}
	
	private org.meveo.apiv2.admin.Address getAddress(Seller seller){
		if(seller.getAddress() != null){
			var address = seller.getAddress();
			return ImmutableAddress.builder()
						.address1(address.getAddress1())
						.address2(address.getAddress2())
						.address3(address.getAddress3())
						.zipCode(address.getZipCode())
						.city(address.getCity())
						.country(address.getCountry() != null ? address.getCountry().getCode() : null)
						.state(address.getState())
						.build();
					
		}
		return null;
	}
	
	private org.meveo.apiv2.admin.ContactInformation getContactInfo(ContactInformation contactInformation) {
		if(contactInformation != null) {
			return ImmutableContactInformation.builder()
					.email(contactInformation.getEmail())
					.phone(contactInformation.getPhone())
					.mobile(contactInformation.getMobile())
					.fax(contactInformation.getFax())
					.build();
		}
		return null;
	}
	
	private List<org.meveo.apiv2.admin.InvoiceTypeSellerSequence> getInvoiceTypeSellerSequences(List<InvoiceTypeSellerSequence> invoiceTypeSellerSequences) {
		if(CollectionUtils.isEmpty(invoiceTypeSellerSequences)) return Collections.emptyList();
		var invoiceTypeSellerSeqs = new ArrayList<org.meveo.apiv2.admin.InvoiceTypeSellerSequence>();
		invoiceTypeSellerSequences.forEach(invTypeSellerSeq -> {
			invoiceTypeSellerSeqs.add(
																	ImmutableInvoiceTypeSellerSequence.builder()
																			.id((Long)invTypeSellerSeq.getId())
																			.invoiceSequenceId(invTypeSellerSeq.getInvoiceSequence() != null ? invTypeSellerSeq.getInvoiceSequence().getId() :  null)
																			.invoiceTypeId(invTypeSellerSeq.getInvoiceType() != null ? invTypeSellerSeq.getInvoiceType().getId() : null)
																			.prefixEL(invTypeSellerSeq.getPrefixEL())
																			.build()
			);
						
		});
		return  invoiceTypeSellerSeqs;
	}
	
	private List<org.meveo.apiv2.admin.CustomerSequence> getCustomerSequence(Set<CustomerSequence> customerSequences) {
		if(CollectionUtils.isEmpty(customerSequences)) return Collections.emptyList();
		var customerSequenceResult = new ArrayList<org.meveo.apiv2.admin.CustomerSequence>();
		customerSequences.forEach(customerSequence -> {
			customerSequenceResult.add(
					ImmutableCustomerSequence.builder()
							.id(customerSequence.getId())
							.code(customerSequence.getCode())
							.prefix(customerSequence.getGenericSequence() != null ? customerSequence.getGenericSequence().getPrefix() : null)
							.sequenceSize(customerSequence.getGenericSequence() != null ? customerSequence.getGenericSequence().getSequenceSize() : null)
							.currentSequenceNb(customerSequence.getGenericSequence() != null ? customerSequence.getGenericSequence().getCurrentSequenceNb() : null)
							.description(customerSequence.getDescription())
							.build()
			);
		});
		
		return customerSequenceResult;
	}
	@Override
	protected Seller toEntity(org.meveo.apiv2.admin.Seller resource) {
		Seller seller = new Seller();
		seller.setCode(resource.getCode());
		seller.setId(resource.getId());
		seller.setDescription(resource.getDescription());
		seller.setVatNo(resource.getVatNumber());
		if(CollectionUtils.isNotEmpty(resource.getRegistrationNumbers())) {
			resource.getRegistrationNumbers().forEach(registrationNumberDto -> {
				RegistrationNumber registrationNumber = registrationNumberService.findByRegistrationNo(registrationNumberDto.getRegistrationNo());
				IsoIcd isoIcd = null;
				
				if(registrationNumber == null) {
					registrationNumber = new RegistrationNumber(registrationNumberDto.getRegistrationNo(), isoIcd, seller);
				}else{
					registrationNumber.setAccountEntity(seller);
				}
				
				if(StringUtils.isNotBlank(registrationNumberDto.getIsoIcdCode())){
					isoIcd = isoIcdService.findByCode(registrationNumberDto.getIsoIcdCode());
					if(isoIcd == null) {
						throw new EntityDoesNotExistsException(IsoIcd.class, registrationNumberDto.getIsoIcdCode());
					}
					registrationNumber.setIsoIcd(isoIcd);
				}
				seller.getRegistrationNumbers().add(registrationNumber);
			});
		}
		if(resource.getLegalType() != null && StringUtils.isNotBlank(resource.getLegalType().getCode())) {
			var legalEntityType = titleService.findByCode(resource.getLegalType().getCode());
			if(legalEntityType == null) {
				throw new EntityDoesNotExistsException(Title.class, resource.getLegalType().getCode());
			}
			seller.setLegalEntityType(legalEntityType);
		}
		if(StringUtils.isNotBlank(resource.getCurrencyCode())){
			var currency = tradingCurrencyService.findByTradingCurrencyCode(resource.getCurrencyCode());
			if(currency == null){
				throw  new EntityDoesNotExistsException(TradingCurrency.class, resource.getCurrencyCode());
			}
			
			seller.setTradingCurrency((TradingCurrency) Hibernate.unproxy(currency));
		}
		if(StringUtils.isNotBlank(resource.getCountryCode())){
			
			var country = tradingCountryService.findByCode(resource.getCountryCode());
			if(country == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class, resource.getCountryCode());
			}
			seller.setTradingCountry((TradingCountry) Hibernate.unproxy(country));
		}
		if(StringUtils.isNotBlank(resource.getLanguageCode())){
			var language = tradingLanguageService.findByTradingLanguageCode(resource.getLanguageCode());
			if(language == null) {
				throw new EntityDoesNotExistsException(TradingLanguage.class, resource.getLanguageCode());
			}
			seller.setTradingLanguage((TradingLanguage) Hibernate.unproxy(language));
		}
		
		if(CollectionUtils.isNotEmpty(resource.getMediaCodes())) {
			resource.getMediaCodes().forEach(mediaCode -> {
				var media = mediaService.findByCode(mediaCode);
				if(media == null) {
					throw new EntityDoesNotExistsException(Media.class, mediaCode);
				}
				seller.getMedias().add((Media) Hibernate.unproxy(media));
			});
		}
		if(resource.getAddress() != null) {
			var address = new Address();
			var postAddress = resource.getAddress();
			address.setAddress1(postAddress.getAddress1());
			address.setAddress2(postAddress.getAddress2());
			address.setAddress3(postAddress.getAddress3());
			address.setZipCode(postAddress.getZipCode());
			address.setCity(postAddress.getCity());
			if(StringUtils.isNotBlank(postAddress.getCountry())) {
				var country = countryService.findByCode(postAddress.getCountry());
				if(country == null) {
					throw new EntityDoesNotExistsException(Country.class, postAddress.getCountry());
				}
				country.setCode(postAddress.getCountry());
				address.setCountry(country);
			}
			address.setState(postAddress.getState());
			seller.setAddress(address);
		}
		
		if(resource.getContactInformation() != null){
			var postContact = resource.getContactInformation();
			var contactInfo = new ContactInformation();
			contactInfo.setEmail(postContact.getEmail());
			contactInfo.setPhone(postContact.getPhone());
			contactInfo.setMobile(postContact.getMobile());
			contactInfo.setFax(postContact.getFax());
			seller.setContactInformation(contactInfo);
		}
		if(StringUtils.isNotBlank(resource.getParentSeller())){
			var parentSeller = sellerService.findByCode(resource.getParentSeller());
			if(parentSeller == null) {
				throw new EntityDoesNotExistsException(Seller.class, resource.getParentSeller());
			}
			parentSeller.setCode(resource.getParentSeller());
			seller.setSeller(parentSeller);
		}else{
			seller.setSeller(null);
		}
		if(CollectionUtils.isNotEmpty(resource.getInvoiceTypeSellerSequence())){
			resource.getInvoiceTypeSellerSequence().forEach(invTypSelSeq -> {
				InvoiceTypeSellerSequence invoiceTypeSellerSequence = new InvoiceTypeSellerSequence();
				invoiceTypeSellerSequence.setId(invTypSelSeq.getId());
				invoiceTypeSellerSequence.setPrefixEL(invTypSelSeq.getPrefixEL());
				if(invTypSelSeq.getInvoiceTypeId() != null){
					var invoiceType = invoiceTypeService.findById(invTypSelSeq.getInvoiceTypeId());
					if(invoiceType == null){
						throw new EntityDoesNotExistsException(InvoiceType.class, invTypSelSeq.getInvoiceTypeId());
					}
					invoiceTypeSellerSequence.setInvoiceType(invoiceType);
				}
				if(invTypSelSeq.getInvoiceSequenceId() != null){
					var invoiceSequence = invoiceSequenceService.findById(invTypSelSeq.getInvoiceSequenceId());
					if(invoiceSequence == null){
						throw new EntityDoesNotExistsException(InvoiceSequence.class, invTypSelSeq.getInvoiceSequenceId());
					}
					invoiceTypeSellerSequence.setInvoiceSequence(invoiceSequence);
				}
				seller.getInvoiceTypeSequence().add(invoiceTypeSellerSequence);
			});
			
		}
		if(CollectionUtils.isNotEmpty(resource.getCustomerSequence())){
			resource.getCustomerSequence().forEach(cusSeq -> {
				CustomerSequence customerSequence = null;
				if(StringUtils.isBlank(cusSeq.getCode())){
					throw new MissingParameterException("customerSequence.code");
				}
				if(cusSeq.getId() != null ){
					customerSequence = customerSequenceService.findById(cusSeq.getId());
					if(customerSequence == null){
						throw new EntityDoesNotExistsException(CustomerSequence.class, cusSeq.getId());
					}
				}
				if(customerSequence == null) {
					customerSequence = customerSequenceService.findByCode(cusSeq.getCode());
				}
				if(customerSequence == null){
					customerSequence = new CustomerSequence();
				}
				customerSequence.setSeller(seller);
				customerSequence.setDescription(cusSeq.getDescription());
				GenericSequence genericSequence = new GenericSequence();
				genericSequence.setCurrentSequenceNb(cusSeq.getCurrentSequenceNb());
				genericSequence.setPrefix(cusSeq.getPrefix());
				genericSequence.setSequenceSize(cusSeq.getSequenceSize());
				customerSequence.setGenericSequence(genericSequence);
				customerSequence.setCode(cusSeq.getCode());
				seller.getCustomerSequences().add(customerSequence);
			});
		}
		return seller;
	}
}
