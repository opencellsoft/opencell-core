package org.meveo.api.account;

import java.util.List;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.dto.response.TitlesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
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
     * 
     * @param postData
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void create(TitleDto postData, User currentUser) throws MeveoApiException, BusinessException {

        String titleCode = postData.getCode();

        if (!StringUtils.isBlank(titleCode)) {
            Title title = titleService.findByCode(titleCode, currentUser.getProvider());

            if (title != null) {
                throw new EntityAlreadyExistsException(Title.class, titleCode);
            }

            Title newTitle = new Title();
            newTitle.setCode(titleCode);
            newTitle.setDescription(postData.getDescription());
            newTitle.setIsCompany(postData.getIsCompany());

            titleService.create(newTitle, currentUser);
        } else {
            missingParameters.add("titleCode");
            handleMissingParameters();
        }
    }

    /**
     * Returns TitleDto based on title code.
     * 
     * @param titleCode
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public TitleDto find(String titleCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(titleCode)) {
            missingParameters.add("titleCode");
        }
        handleMissingParameters();

        Title title = titleService.findByCode(titleCode, provider);
        if (title != null) {
            TitleDto titleDto = new TitleDto();
            titleDto.setCode(title.getCode());
            titleDto.setDescription(title.getDescription());
            titleDto.setIsCompany(title.getIsCompany());
            return titleDto;
        }
        throw new EntityDoesNotExistsException(Title.class, titleCode);
    }

    /**
     * Updates a Title Entity based on title code.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void update(TitleDto postData, User currentUser) throws MeveoApiException, BusinessException {
        String titleCode = postData.getCode();
        if (StringUtils.isBlank(titleCode)) {
            missingParameters.add("titleCode");
        }
        handleMissingParameters();

        Title title = titleService.findByCode(titleCode, currentUser.getProvider());
        if (title != null) {
            title.setDescription(postData.getDescription());
            title.setIsCompany(postData.getIsCompany());
            titleService.update(title, currentUser);
        } else {
            throw new EntityDoesNotExistsException(Title.class, titleCode);
        }
    }

    /**
     * Removes a title based on title code.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void remove(String titleCode, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(titleCode)) {
            missingParameters.add("titleCode");
        }

        handleMissingParameters();

        Title title = titleService.findByCode(titleCode, currentUser.getProvider());
        if (title != null) {
            titleService.remove(title, currentUser);
        } else {
            throw new EntityDoesNotExistsException(Title.class, titleCode);
        }
    }

    public void createOrUpdate(TitleDto postData, User currentUser) throws MeveoApiException, BusinessException {
        Title title = titleService.findByCode(postData.getCode(), currentUser.getProvider());

        if (title == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
            update(postData, currentUser);
        }
    }

    public TitlesDto list(Provider provider) throws MeveoApiException {
        TitlesDto titlesDto = new TitlesDto();
        List<Title> titles = titleService.list(provider, true);

        if (titles != null) {
            for (Title title : titles) {
                TitleDto titleDto = new TitleDto();
                titleDto.setCode(title.getCode());
                titleDto.setDescription(title.getDescription());
                titleDto.setIsCompany(title.getIsCompany());
                titlesDto.getTitle().add(titleDto);
            }
        }

        return titlesDto;
    }
}
