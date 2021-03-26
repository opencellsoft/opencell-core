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

package org.meveo.api.payment;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.payment.CreditCategoriesResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CreditCategory;
import org.meveo.service.payments.impl.CreditCategoryService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The CRUD Api for CreditCategory Entity.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @since 22 Aug 2017
 * @lastModifiedVersion 7.0
 */
@Stateless
public class CreditCategoryApi extends BaseApi {

	@Inject
	private CreditCategoryService creditCategoryService;

	private static CreditCategory dtoToEntity(CreditCategoryDto dto, CreditCategory entity) {
		CreditCategory e = entity;
		if (entity == null) {
			e = new CreditCategory();
			e.setCode(dto.getCode());
		}
		e.setDescription(dto.getDescription());

		return e;
	}

	private static CreditCategoryDto entityToDto(CreditCategory e) {
		CreditCategoryDto dto = new CreditCategoryDto();
		dto.setCode(e.getCode());
		dto.setDescription(e.getDescription());

		return dto;
	}

	public CreditCategory create(CreditCategoryDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        if (creditCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CreditCategory.class, postData.getCode());
        }

        CreditCategory creditCategory = dtoToEntity(postData, null);
		creditCategoryService.create(creditCategory);

		return creditCategory;
	}

	public CreditCategory update(CreditCategoryDto postData) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}

		CreditCategory creditCategory = creditCategoryService.findByCode(postData.getCode());
		if (creditCategory == null) {
            throw new EntityDoesNotExistsException(CreditCategory.class, postData.getCode());
		}

		creditCategory = dtoToEntity(postData, creditCategory);
		creditCategory = creditCategoryService.update(creditCategory);

		handleMissingParameters();
		return creditCategory;
	}

	public CreditCategory createOrUpdate(CreditCategoryDto postData) throws MeveoApiException, BusinessException {
		CreditCategory creditCategory = creditCategoryService.findByCode(postData.getCode());
		if (creditCategory == null) {
			creditCategory = create(postData);
		} else {
			creditCategory = update(postData);
		}
		return creditCategory;
	}

	public CreditCategoryDto find(String creditCategoryCode) throws MeveoApiException {
		CreditCategory creditCategory = creditCategoryService.findByCode(creditCategoryCode);
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(CreditCategory.class, creditCategoryCode);
		}

		return entityToDto(creditCategory);
	}

	public List<CreditCategoryDto> list() throws MeveoApiException {
		List<CreditCategoryDto> result = new ArrayList<>();

		List<CreditCategory> creditCategories = creditCategoryService.listActive();
		if (creditCategories != null && !creditCategories.isEmpty()) {
			result = creditCategories.stream().map(c -> entityToDto(c)).collect(Collectors.toList());
		}

		return result;
	}

	public CreditCategoriesResponseDto list(PagingAndFiltering pagingAndFiltering) {
		CreditCategoriesResponseDto result = new CreditCategoriesResponseDto();
		result.setPaging( pagingAndFiltering );

		List<CreditCategory> creditCategories = creditCategoryService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
		if (creditCategories != null) {
			for (CreditCategory creditCategory : creditCategories) {
				result.getCreditCategories().add(new CreditCategoryDto(creditCategory));
			}
		}

		return result;
	}
	
	public void remove(String creditCategoryCode) throws MeveoApiException, BusinessException {
		CreditCategory creditCategory = creditCategoryService.findByCode(creditCategoryCode);
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(CreditCategory.class, creditCategoryCode);
		}

		creditCategoryService.remove(creditCategory);
	}

}
