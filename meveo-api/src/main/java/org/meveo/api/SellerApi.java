package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.SellersDto;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.crm.impl.SellerApiService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class SellerApi{

	@Inject
	private SellerApiService sellerApiService;

	
	
	public void create(SellerDto postData, User currentUser) throws MeveoApiException {
		sellerApiService.create(postData, currentUser);
	}

	public void update(SellerDto postData, User currentUser) throws MeveoApiException {
		sellerApiService.update(postData, currentUser);
	}

	public SellerDto find(String sellerCode, Provider provider) throws MeveoApiException {
		return sellerApiService.find(sellerCode, provider);
	}

	public void remove(String sellerCode, Provider provider) throws MeveoApiException {
		sellerApiService.remove(sellerCode, provider);
	}

	public SellersDto list(Provider provider) {
		return sellerApiService.list(provider);
	}

	public SellerCodesResponseDto listSellerCodes(Provider provider) {		
		return sellerApiService.listSellerCodes(provider);
	}
	
	/**
	 * creates or updates seller based on the seller code. If seller is not existing based
	 * on the seller code, it will be created else, will be updated.
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(SellerDto postData, User currentUser) throws MeveoApiException {
		sellerApiService.createOrUpdate(postData, currentUser);
	}
}
