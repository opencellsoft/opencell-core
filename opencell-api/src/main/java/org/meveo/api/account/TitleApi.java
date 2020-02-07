package org.meveo.api.account;

import java.util.function.Function;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.TitleService;

/**
 * CRUD API for {@link Title}.
 * 
 * @author Andrius Karpavicius
 *
 */
@Stateless
public class TitleApi extends BaseCrudApi<Title, TitleDto> {

    @Inject
    private TitleService titleService;

    /**
     * Creates a new Title entity.
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public Title create(TitleDto postData) throws MeveoApiException, BusinessException {

        String titleCode = postData.getCode();

        if (StringUtils.isBlank(titleCode)) {
            missingParameters.add("titleCode");
        }

        handleMissingParametersAndValidate(postData);

        Title title = titleService.findByCode(titleCode);

        if (title != null) {
            throw new EntityAlreadyExistsException(Title.class, titleCode);
        }

        title = new Title();
        title.setCode(titleCode);
        title.setDescription(postData.getDescription());
        title.setIsCompany(postData.getIsCompany());
        title.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        titleService.create(title);

        return title;
    }

    /**
     * Updates a Title Entity based on title code.
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public Title update(TitleDto postData) throws MeveoApiException, BusinessException {
        String titleCode = postData.getCode();
        if (StringUtils.isBlank(titleCode)) {
            missingParameters.add("titleCode");
        }

        handleMissingParametersAndValidate(postData);

        Title title = titleService.findByCode(titleCode);
        if (title == null) {
            throw new EntityDoesNotExistsException(Title.class, titleCode);
        }

        title.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        title.setDescription(postData.getDescription());
        title.setIsCompany(postData.getIsCompany());
        if (postData.getLanguageDescriptions() != null) {
            title.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), title.getDescriptionI18n()));
        }

        title = titleService.update(title);

        return title;
    }

    @Override
    protected Function<Title, TitleDto> getEntityToDtoFunction() {
        return TitleDto::new;
    }
}