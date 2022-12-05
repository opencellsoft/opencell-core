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

package org.meveo.api.hierarchy;

import java.util.ArrayList;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.security.UserGroup;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;

@Stateless
//@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class UserHierarchyLevelApi extends BaseApi {

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    /**
     * Returns UserHierarchyLevelDto based on hierarchy Level Code.
     *
     * @param hierarchyLevelCode hierarchy level code
     * @return user hierarchy level
     * @throws org.meveo.api.exception.MeveoApiException meveo api exception
     */
//    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = UserHierarchyLevel.class))
    public UserHierarchyLevelDto find(String hierarchyLevelCode) throws MeveoApiException {
        if (StringUtils.isBlank(hierarchyLevelCode)) {
            missingParameters.add("hierarchyLevelCode");
        }
        handleMissingParameters();

        UserGroup userHierarchyLevel = userHierarchyLevelService.findByCode(hierarchyLevelCode);
        if (userHierarchyLevel == null) {
            throw new EntityDoesNotExistsException(UserGroup.class, hierarchyLevelCode);
        }

        UserHierarchyLevelDto userHierarchyLevelDto = convertToUserHierarchyLevelDto(userHierarchyLevel, true);
        return userHierarchyLevelDto;
    }

    private UserHierarchyLevelDto convertToUserHierarchyLevelDto(UserGroup userHierarchyLevel, boolean recursive) {
        UserHierarchyLevelDto userHierarchyLevelDto = new UserHierarchyLevelDto(userHierarchyLevel);

        if (recursive && userHierarchyLevel.getChildGroups() != null && !userHierarchyLevel.getChildGroups().isEmpty()) {
            List<UserHierarchyLevelDto> childLevelDtos = new ArrayList<>();
            for (UserGroup childGroup : userHierarchyLevel.getChildGroups()) {
                childLevelDtos.add(convertToUserHierarchyLevelDto(childGroup, recursive));
            }
            userHierarchyLevelDto.setChildLevels(childLevelDtos);
        }
        return userHierarchyLevelDto;
    }

    /**
     * List user hierarchy levels matching filtering and query criteria.
     * 
     * @param pagingAndFiltering Paging and filtering criteria. Specify "childLevels" in fields to include the child levels of user hierarchy level.
     * @return A list of user hierarchy levels
     * @throws ActionForbiddenException action forbidden exception
     * @throws InvalidParameterException invalid parameter exception
     */
//    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
//    @FilterResults(propertyToFilter = "userHierarchyLevels", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = UserHierarchyLevel.class) })
    public UserHierarchyLevelsDto list(PagingAndFiltering pagingAndFiltering) throws ActionForbiddenException, InvalidParameterException {

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, (Class) null);

        UserHierarchyLevelsDto result = new UserHierarchyLevelsDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());

        List<UserGroup> levels = userHierarchyLevelService.list(paginationConfig);
        result.getPaging().setTotalNumberOfRecords(levels.size());
        for (UserGroup level : levels) {
            result.getUserHierarchyLevels().add(convertToUserHierarchyLevelDto(level, pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("childLevels")));
        }

        return result;
    }
}
