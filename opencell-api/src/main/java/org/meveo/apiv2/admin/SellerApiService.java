package org.meveo.apiv2.admin;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.service.admin.impl.InvoiceTypeSequenceService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.validation.ValidationByNumberCountryService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class SellerApiService {

	@Inject
	private SellerService sellerService;
	@Inject
	private ValidationByNumberCountryService validationByNumberCountryService;
	@Inject
	private InvoiceTypeSequenceService invoiceTypeSequenceService;
	
	public void create(Seller seller) {
		handleMissingParameters(seller);
		if(sellerService.findByCode(seller.getCode()) != null) {
			throw new EntityAlreadyExistsException(Seller.class, seller.getCode());
		}
		if(seller.getSeller() != null){
			var parentSeller = sellerService.findByCode(seller.getSeller().getCode());
			if(parentSeller == null) {
				throw new EntityDoesNotExistsException(Seller.class, seller.getSeller().getCode());
			}
			seller.setSeller(parentSeller);
		}
		checkVatNum(seller.getVatNo(), seller.getTradingCountry().getCountryCode());
		
		if(CollectionUtils.isNotEmpty(seller.getInvoiceTypeSequence())){
			var invoiceTypeIds = seller.getInvoiceTypeSequence().stream()
											.filter(invoiceTypeSellerSequence -> invoiceTypeSellerSequence.getInvoiceType() != null)
											.map(invoiceTypeSellerSequence -> invoiceTypeSellerSequence.getInvoiceType().getId()).collect(Collectors.toList());
			var missingParam = new ArrayList<String>();
			int index = 0;
			for(InvoiceTypeSellerSequence inv: seller.getInvoiceTypeSequence()){
				if(inv.getInvoiceType() == null) {
					missingParam.add("invoiceTypeSellerSequence["+index+"].invoiceType");
				}
				if(inv.getInvoiceSequence() == null) {
					missingParam.add("invoiceTypeSellerSequence["+index+"].invoiceSequence");
				}
				if(StringUtils.isEmpty(inv.getPrefixEL())){
					missingParam.add("invoiceTypeSellerSequence["+index+"].prefixEL");
				}
				if(CollectionUtils.isNotEmpty(missingParam)){
					throw new MissingParameterException(missingParam);
				}
				++index;
				var frequency = Collections.frequency(invoiceTypeIds, inv.getInvoiceType().getId());
				if(frequency > 1) {
					throw new BusinessApiException("Invoice numbering for a given invoice type is already specified");
				}
				inv.setSeller(seller);
			}
		}
		sellerService.create(seller);
	}
	
	public void update(Seller postSeller) {
		handleMissingParameters(postSeller);
		if(postSeller.getId() == null) {
			throw new MissingParameterException(List.of("id"));
		}
		var seller = Optional.ofNullable(sellerService.findById(postSeller.getId())).orElseThrow(NotFoundException::new);
		
		seller.setDescription(postSeller.getDescription());
		seller.setVatNo(postSeller.getVatNo());
		seller.setTradingLanguage(postSeller.getTradingLanguage());
		seller.setTradingCountry(postSeller.getTradingCountry());
		seller.setTradingCurrency(postSeller.getTradingCurrency());
		seller.setContactInformation(postSeller.getContactInformation());
		seller.setAddress(postSeller.getAddress());
		if(CollectionUtils.isNotEmpty(postSeller.getMedias())){
			seller.getMedias().clear();
			seller.getMedias().addAll(postSeller.getMedias());
		}
		if(postSeller.getSeller() != null) {
			seller.setSeller(postSeller.getSeller());
		}
		checkVatNum(seller.getVatNo(), seller.getTradingCountry().getCountryCode());
		sellerService.update(seller);
	}
	
	
	public void createOrUpdate(Seller postData) {
		if(postData.getId() == null){
			create(postData);
		}else{
			update(postData);
		}
	}
	
	private void checkVatNum(String vatNo, String countryCode) {
		
		try{
			boolean valExist = validationByNumberCountryService.getValByValNbCountryCode(vatNo, countryCode);
			if(!valExist){
				throw new BusinessException("The Val Number : " + vatNo + " is incorrect !");
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}
	
	private void checkField(String fieldData, String fieldError, List<String> missingParameters) {
		if(StringUtils.isEmpty(fieldData)){
			missingParameters.add(fieldError);
		}
	}
	private void checkField(Object fieldData, String fieldError, List<String> missingParameters) {
		if(fieldData == null){
			missingParameters.add(fieldError);
		}
	}
	private void handleMissingParameters(Seller seller){
		var paramMissing = new ArrayList<String>();
		checkField(seller.getCode(), "code", paramMissing);
		checkField(seller.getDescription(), "description", paramMissing);
		checkField(seller.getVatNo(), "vatNumber", paramMissing);
		checkField(seller.getTradingLanguage(), "languageCode", paramMissing);
		checkField(seller.getTradingCountry(), "countryCode", paramMissing);
		checkField(seller.getTradingCurrency(), "currencyCode", paramMissing);
		if(seller.getContactInformation() == null) {
			paramMissing.add("contactInformation");
		}else if(StringUtils.isEmpty(seller.getContactInformation().getEmail())){
			paramMissing.add("contactInformation.email");
		}
		if(seller.getAddress() == null){
			paramMissing.add("address");
		}else if(StringUtils.isEmpty(seller.getAddress().getAddress1()) && StringUtils.isEmpty(seller.getAddress().getAddress2()) && StringUtils.isEmpty(seller.getAddress().getAddress3())){
			paramMissing.add("address");
		}
		
		if(CollectionUtils.isNotEmpty(paramMissing)) {
			throw new MissingParameterException(paramMissing);
		}
	}
	
}
