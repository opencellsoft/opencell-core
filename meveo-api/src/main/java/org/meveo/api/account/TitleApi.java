package org.meveo.api.account;

import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.TitleService;

public class TitleApi extends BaseApi {
	
	@Inject
    private TitleService titleService;
	
	/**
     * Creates a new Title entity.
     * @param postData
     * @throws MeveoApiException
     */
    public void createTitle(TitleDto postData, User currentUser) throws MeveoApiException {
    	
    	String titleCode = postData.getCode();
    	
    	if ( !StringUtils.isBlank(titleCode) ) {
    		Title title = titleService.findByCode(currentUser.getProvider(), titleCode);
    		
    		if (title != null) {
    			throw new EntityAlreadyExistsException(Title.class, titleCode);
    		}
    		
    		Title newTitle = new Title();
    		newTitle.setCode(titleCode);
    		newTitle.setDescription(postData.getDescription());
    		newTitle.setIsCompany(postData.getIsCompany());
    		
    		titleService.create(newTitle, currentUser, currentUser.getProvider());
    	} else {
   			missingParameters.add("titleCode");
    		throw new MissingParameterException(getMissingParametersExceptionMessage());
    	}
    }
    
    /**
     * Returns TitleDto based on title code.
     * @param titleCode
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public TitleDto findTitle(String titleCode, Provider provider) throws MeveoApiException {
    	if (!StringUtils.isBlank(titleCode)) {
    		Title title = titleService.findByCode(provider, titleCode);
    		if (title != null) {
    			TitleDto titleDto = new TitleDto();
    			titleDto.setCode(title.getCode());
    			titleDto.setDescription(title.getDescription());
    			titleDto.setIsCompany(title.getIsCompany());
    			return titleDto;
    		}
    		throw new EntityDoesNotExistsException(Title.class, titleCode);
    	} else {
    		missingParameters.add("titleCode");
    		throw new MeveoApiException(getMissingParametersExceptionMessage());
    	}
    }
    
    /**
     * Updates a Title Entity based on title code.
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     */
    public void updateTitle(TitleDto postData, User currentUser) throws MeveoApiException {
    	String titleCode = postData.getCode();
    	if (!StringUtils.isBlank(titleCode)) {
    		Title title = titleService.findByCode(currentUser.getProvider(), titleCode);
    		if (title != null) {
    			title.setDescription(postData.getDescription());
    			title.setIsCompany(postData.getIsCompany());
    			titleService.update(title, currentUser);
    		} else {
    			throw new EntityDoesNotExistsException(Title.class, titleCode);
    		}    		
    	} else {
    		missingParameters.add("titleCode");
    		throw new MeveoApiException(getMissingParametersExceptionMessage());
    	}
    }
    
    /**
     * Removes a title based on title code.
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     */
    public void removeTitle(String titleCode, User currentUser) throws MeveoApiException {
    	
    	if (!StringUtils.isBlank(titleCode)) {
    		Title title = titleService.findByCode(currentUser.getProvider(), titleCode);
    		if (title != null) {
    			titleService.remove(title);
    		} else {
    			throw new EntityDoesNotExistsException(Title.class, titleCode);
    		}  
    	} else {
    		missingParameters.add("titleCode");
    		throw new MeveoApiException(getMissingParametersExceptionMessage());
    	}
    }
}
