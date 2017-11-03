package org.meveo.api.hierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.primefaces.model.SortOrder;

@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class UserHierarchyLevelApi extends BaseApi {

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    /**
     * Creates a new User Hierarchy Level entity.
     *
     * @param postData
     * @throws org.meveo.api.exception.MeveoApiException
     * @throws org.meveo.admin.exception.BusinessException
     */
    public void create(UserHierarchyLevelDto postData) throws MeveoApiException, BusinessException {

        String hierarchyLevelCode = postData.getCode();

        if (StringUtils.isBlank(hierarchyLevelCode)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(hierarchyLevelCode);

        if (userHierarchyLevel != null) {
            throw new EntityAlreadyExistsException(UserHierarchyLevel.class, hierarchyLevelCode);
        }

        createOrUpdateHierarchyTree(postData);
    }

    private void createOrUpdateHierarchyTree(UserHierarchyLevelDto postData) throws EntityDoesNotExistsException, BusinessException {

        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(postData.getCode());
        boolean create = userHierarchyLevel == null;

        UserHierarchyLevel parentLevel = null;
        if (!StringUtils.isBlank(postData.getParentLevel())) {
            parentLevel = userHierarchyLevelService.findByCode(postData.getParentLevel());
            if (parentLevel == null) {
                throw new EntityDoesNotExistsException(UserHierarchyLevel.class, postData.getParentLevel());
            }
        }

        userHierarchyLevel = fromDto(postData, parentLevel, userHierarchyLevel);
        if (create) {
            userHierarchyLevelService.create(userHierarchyLevel);
        } else {
            userHierarchyLevel = userHierarchyLevelService.update(userHierarchyLevel);
        }

        if (postData.getChildLevels() != null && !postData.getChildLevels().isEmpty()) {
            for (UserHierarchyLevelDto childDto : postData.getChildLevels()) {
                childDto.setParentLevel(userHierarchyLevel.getCode());
                createOrUpdateHierarchyTree(childDto);
            }
        }
    }

    /**
     * Updates a User Hierarchy Level Entity based on title code.
     *
     * @param postData
     * 
     * @throws org.meveo.api.exception.MeveoApiException
     * @throws org.meveo.admin.exception.BusinessException
     */
    public void update(UserHierarchyLevelDto postData) throws MeveoApiException, BusinessException {
        String hierarchyLevelCode = postData.getCode();

        if (StringUtils.isBlank(hierarchyLevelCode)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(hierarchyLevelCode);
        if (userHierarchyLevel == null) {
            throw new EntityDoesNotExistsException(UserHierarchyLevel.class, hierarchyLevelCode);
        }

        createOrUpdateHierarchyTree(postData);
    }

    /**
     * Returns UserHierarchyLevelDto based on hierarchy Level Code.
     *
     * @param hierarchyLevelCode
     * @return
     * @throws org.meveo.api.exception.MeveoApiException
     */
    public UserHierarchyLevelDto find(String hierarchyLevelCode) throws MeveoApiException {
        if (StringUtils.isBlank(hierarchyLevelCode)) {
            missingParameters.add("hierarchyLevelCode");
        }
        handleMissingParameters();

        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(hierarchyLevelCode, Arrays.asList("parentLevel", "childLevels"));
        if (userHierarchyLevel == null) {
            throw new EntityDoesNotExistsException(UserHierarchyLevel.class, hierarchyLevelCode);
        }

        UserHierarchyLevelDto userHierarchyLevelDto = convertToUserHierarchyLevelDto(userHierarchyLevel, true);
        return userHierarchyLevelDto;
    }

    /**
     * Removes a User Hierarchy Level based on user hierarchy level code.
     *
     * @param hierarchyLevelCode
     * 
     * @throws org.meveo.api.exception.MeveoApiException
     * @throws BusinessException
     */
    public void remove(String hierarchyLevelCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(hierarchyLevelCode)) {
            missingParameters.add("hierarchyLevelCode");
        }

        handleMissingParameters();

        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(hierarchyLevelCode);
        if (userHierarchyLevel != null) {
            if (!userHierarchyLevelService.canDeleteUserHierarchyLevel(userHierarchyLevel.getId())) {
                throw new DeleteReferencedEntityException(UserHierarchyLevel.class, hierarchyLevelCode);
            }
            userHierarchyLevelService.remove(userHierarchyLevel);

        } else {
            throw new EntityDoesNotExistsException(UserHierarchyLevel.class, hierarchyLevelCode);
        }
    }

    /**
     * Create or Update a User Hierarchy Level Entity based on title code.
     *
     * @param postData
     * 
     * @throws org.meveo.api.exception.MeveoApiException
     * @throws org.meveo.admin.exception.BusinessException
     */
    public void createOrUpdate(UserHierarchyLevelDto postData) throws MeveoApiException, BusinessException {
        String hierarchyLevelCode = postData.getCode();
        if (StringUtils.isBlank(hierarchyLevelCode)) {
            missingParameters.add("hierarchyLevelCode");
        }
        handleMissingParameters();
        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(hierarchyLevelCode);
        if (userHierarchyLevel != null) {
            update(postData);
        } else {
            create(postData);
        }
    }

    @SuppressWarnings("rawtypes")
    private UserHierarchyLevelDto convertToUserHierarchyLevelDto(UserHierarchyLevel userHierarchyLevel, boolean recursive) {
        UserHierarchyLevelDto userHierarchyLevelDto = new UserHierarchyLevelDto(userHierarchyLevel);

        if (recursive && userHierarchyLevel.getChildLevels() != null && !userHierarchyLevel.getChildLevels().isEmpty()) {
            List<UserHierarchyLevelDto> childLevelDtos = new ArrayList<>();
            for (HierarchyLevel childLevel : userHierarchyLevel.getChildLevels()) {
                childLevelDtos.add(convertToUserHierarchyLevelDto((UserHierarchyLevel) childLevel, recursive));
            }
            userHierarchyLevelDto.setChildLevels(childLevelDtos);
        }
        return userHierarchyLevelDto;
    }

    protected UserHierarchyLevel fromDto(UserHierarchyLevelDto userHierarchyLevelDto, UserHierarchyLevel parentLevel, UserHierarchyLevel hierarchyLevelUpdate) {
        UserHierarchyLevel userHierarchyLevel = new UserHierarchyLevel();
        if (hierarchyLevelUpdate != null) {
            userHierarchyLevel = hierarchyLevelUpdate;
        }
        userHierarchyLevel.setCode(userHierarchyLevelDto.getCode());
        userHierarchyLevel.setDescription(userHierarchyLevelDto.getDescription());
        userHierarchyLevel.setOrderLevel(userHierarchyLevelDto.getOrderLevel());
        userHierarchyLevel.setParentLevel(parentLevel);
        return userHierarchyLevel;
    }

    /**
     * List user hierarchy levels matching filtering and query criteria
     * 
     * @param pagingAndFiltering Paging and filtering criteria. Specify "childLevels" in fields to include the child levels of user hierarchy level.
     * @return A list of user hierarchy levels
     * @throws ActionForbiddenException
     * @throws InvalidParameterException
     */
    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    public UserHierarchyLevelsDto list(PagingAndFiltering pagingAndFiltering) throws ActionForbiddenException, InvalidParameterException {

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, UserHierarchyLevel.class);

        Long totalCount = userHierarchyLevelService.count(paginationConfig);

        UserHierarchyLevelsDto result = new UserHierarchyLevelsDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<UserHierarchyLevel> levels = userHierarchyLevelService.list(paginationConfig);
            for (UserHierarchyLevel level : levels) {
                result.getUserHierarchyLevels().add(convertToUserHierarchyLevelDto(level, pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("childLevels")));
            }
        }

        return result;
    }
}
