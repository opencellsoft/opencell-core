package org.meveo.api.hierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
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
import org.meveo.api.security.parameter.ObjectPropertyParser;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.primefaces.model.SortOrder;

@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
@Stateless
public class UserHierarchyLevelApi extends BaseApi {

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    /**
     * Creates a new User Hierarchy Level entity.
     *
     * @param postData posted data to API
     * @throws org.meveo.api.exception.MeveoApiException meveo api exception
     * @throws org.meveo.admin.exception.BusinessException business exception
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "parentLevel", entityClass = UserHierarchyLevel.class, parser = ObjectPropertyParser.class))
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

        userHierarchyLevel = fromDto(postData, userHierarchyLevel);
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
     * @param postData posted data
     * 
     * @throws org.meveo.api.exception.MeveoApiException meveo api exception
     * @throws org.meveo.admin.exception.BusinessException business exception
     */
    @SecuredBusinessEntityMethod(validate = { @SecureMethodParameter(property = "code", entityClass = UserHierarchyLevel.class, parser = ObjectPropertyParser.class),
            @SecureMethodParameter(property = "parentLevel", entityClass = UserHierarchyLevel.class, parser = ObjectPropertyParser.class) })
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
     * @param hierarchyLevelCode hierarchy level code
     * @return user hierarchy level
     * @throws org.meveo.api.exception.MeveoApiException meveo api exception
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = UserHierarchyLevel.class))
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
     * @param hierarchyLevelCode hierarchy level code
     * 
     * @throws org.meveo.api.exception.MeveoApiException emveo api exception
     * @throws BusinessException business exception
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = UserHierarchyLevel.class))
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
     * @param postData posted data to API
     * 
     * @throws org.meveo.api.exception.MeveoApiException meveo api exception
     * @throws org.meveo.admin.exception.BusinessException business exception
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "parentLevel", entityClass = UserHierarchyLevel.class, parser = ObjectPropertyParser.class))
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

    protected UserHierarchyLevel fromDto(UserHierarchyLevelDto userHierarchyLevelDto, UserHierarchyLevel hierarchyLevelUpdate) throws EntityDoesNotExistsException {
        UserHierarchyLevel userHierarchyLevel = hierarchyLevelUpdate;
        if (hierarchyLevelUpdate == null) {
            userHierarchyLevel = new UserHierarchyLevel();
            userHierarchyLevel.setCode(userHierarchyLevelDto.getCode());
        }

        if (userHierarchyLevelDto.getParentLevel() != null) {
            if (StringUtils.isBlank(userHierarchyLevelDto.getParentLevel())) {
                userHierarchyLevel.setParentLevel(null);
            } else {
                UserHierarchyLevel parentLevel = userHierarchyLevelService.findByCode(userHierarchyLevelDto.getParentLevel());
                if (parentLevel == null) {
                    throw new EntityDoesNotExistsException(UserHierarchyLevel.class, userHierarchyLevelDto.getParentLevel());
                }
                userHierarchyLevel.setParentLevel(parentLevel);
            }
        }

        if (userHierarchyLevelDto.getDescription() != null) {
            userHierarchyLevel.setDescription(userHierarchyLevelDto.getDescription());
        }
        if (userHierarchyLevelDto.getOrderLevel() != null) {
            userHierarchyLevel.setOrderLevel(userHierarchyLevelDto.getOrderLevel());
        }

        return userHierarchyLevel;
    }

    /**
     * List user hierarchy levels matching filtering and query criteria.
     * 
     * @param pagingAndFiltering Paging and filtering criteria. Specify "childLevels" in fields to include the child levels of user hierarchy level.
     * @return A list of user hierarchy levels
     * @throws ActionForbiddenException action forbidden exception
     * @throws InvalidParameterException invalid parameter exception
     */
    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "userHierarchyLevels", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = UserHierarchyLevel.class) })
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
