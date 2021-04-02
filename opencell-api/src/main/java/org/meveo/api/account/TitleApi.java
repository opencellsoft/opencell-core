/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.account;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.dto.response.account.TitlesResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.TitleService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.function.BiFunction;

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

    public TitlesResponseDto list(PagingAndFiltering pagingAndFiltering) {
        TitlesResponseDto result = new TitlesResponseDto();
        result.setPaging( pagingAndFiltering );

        List<Title> titles = titleService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (titles != null) {
            for (Title title : titles) {
                result.getTitles().getTitle().add(new TitleDto(title, null));
            }
        }

        return result;
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
    protected BiFunction<Title, CustomFieldsDto, TitleDto> getEntityToDtoFunction() {
        return TitleDto::new;
    }
}