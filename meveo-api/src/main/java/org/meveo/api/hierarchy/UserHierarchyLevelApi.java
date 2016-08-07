package org.meveo.api.hierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;

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
    public void create(UserHierarchyLevelDto postData, User currentUser) throws MeveoApiException, BusinessException {

        String levelCode = postData.getCode();
        String parentLevelCode = postData.getParentLevel();

        if (StringUtils.isBlank(levelCode)) {
            missingParameters.add("levelCode");
        }

        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }

        handleMissingParameters();

        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(levelCode, currentUser.getProvider());

        if (userHierarchyLevel != null) {
            throw new EntityAlreadyExistsException(UserHierarchyLevel.class, levelCode);
        }

        if (!StringUtils.isBlank(postData.getParentLevel())) {
            UserHierarchyLevel parentLevel = userHierarchyLevelService.findByCode(parentLevelCode, currentUser.getProvider());
            if (parentLevel == null) {
                throw new EntityAlreadyExistsException(UserHierarchyLevel.class, parentLevelCode);
            }
            userHierarchyLevel.setParentLevel(parentLevel);
        } else {
            userHierarchyLevel.setParentLevel(null);
        }

        userHierarchyLevel = new UserHierarchyLevel();
        userHierarchyLevel.setCode(levelCode);
        userHierarchyLevel.setDescription(postData.getDescription());
        userHierarchyLevel.setOrderLevel(postData.getOrderLevel());

        userHierarchyLevelService.create(userHierarchyLevel, currentUser);

    }

    /**
     * Updates a User Hierarchy Level Entity based on title code.
     *
     * @param postData
     * @param currentUser
     * @throws org.meveo.api.exception.MeveoApiException
     * @throws org.meveo.admin.exception.BusinessException
     */
    public void update(UserHierarchyLevelDto postData, User currentUser) throws MeveoApiException, BusinessException {
        String levelCode = postData.getCode();
        String parentLevelCode = postData.getParentLevel();

        if (StringUtils.isBlank(levelCode)) {
            missingParameters.add("levelCode");
        }

        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }

        handleMissingParameters();

        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(levelCode, currentUser.getProvider());
        if (userHierarchyLevel != null) {
            if (!StringUtils.isBlank(postData.getParentLevel())) {
                UserHierarchyLevel parentLevel = userHierarchyLevelService.findByCode(parentLevelCode, currentUser.getProvider());
                if (parentLevel == null) {
                    throw new EntityAlreadyExistsException(UserHierarchyLevel.class, parentLevelCode);
                }
                userHierarchyLevel.setParentLevel(parentLevel);
            } else {
                userHierarchyLevel.setParentLevel(null);
            }
            userHierarchyLevel.setDescription(postData.getDescription());
            userHierarchyLevel.setOrderLevel(postData.getOrderLevel());
            userHierarchyLevelService.update(userHierarchyLevel, currentUser);
        } else {
            throw new EntityDoesNotExistsException(UserHierarchyLevel.class, levelCode);
        }
    }

    /**
     * Returns UserHierarchyLevelDto based on user group level code.
     *
     * @param levelCode
     * @param provider
     * @return
     * @throws org.meveo.api.exception.MeveoApiException
     */
    public UserHierarchyLevelDto find(String levelCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(levelCode)) {
            missingParameters.add("levelCode");
        }
        handleMissingParameters();
        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(levelCode, provider, Arrays.asList("parentLevel", "childLevels"));
        if (userHierarchyLevel != null) {
            UserHierarchyLevelDto userHierarchyLevelDto = new UserHierarchyLevelDto();
            userHierarchyLevelDto.setCode(userHierarchyLevel.getCode());
            userHierarchyLevelDto.setDescription(userHierarchyLevel.getDescription());
            userHierarchyLevelDto.setOrderLevel(userHierarchyLevel.getOrderLevel());
            if (userHierarchyLevel.getParentLevel() != null) {
                userHierarchyLevelDto.setParentLevel(userHierarchyLevel.getParentLevel().getCode());
            }
            userHierarchyLevelDto.setChildLevels(convertToUserHierarchyLevelDto(userHierarchyLevel.getChildLevels()));
            return userHierarchyLevelDto;
        }
        throw new EntityDoesNotExistsException(UserHierarchyLevel.class, levelCode);
    }

    /**
     * Removes a User Hierarchy Level based on user hierarchy level code.
     *
     * @param levelCode
     * @param currentUser
     * @throws org.meveo.api.exception.MeveoApiException
     */
    public void remove(String levelCode, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(levelCode)) {
            missingParameters.add("levelCode");
        }

        handleMissingParameters();

        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(levelCode, currentUser.getProvider());
        if (userHierarchyLevel != null) {
            if (!userHierarchyLevelService.canDeleteUserHierarchyLevel(userHierarchyLevel.getId())) {
                throw new DeleteReferencedEntityException(UserHierarchyLevel.class, levelCode);
            }
            userHierarchyLevelService.remove(userHierarchyLevel);

        } else {
            throw new EntityDoesNotExistsException(UserHierarchyLevel.class, levelCode);
        }
    }

    private List<UserHierarchyLevelDto> convertToUserHierarchyLevelDto(Set<HierarchyLevel> userHierarchyLevels) {
        List<UserHierarchyLevelDto> userHierarchyLevelDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userHierarchyLevels)) {
            for (HierarchyLevel userHierarchyLevel : userHierarchyLevels) {
                UserHierarchyLevelDto userHierarchyLevelDto = new UserHierarchyLevelDto();
                userHierarchyLevelDto.setCode(userHierarchyLevel.getCode());
                userHierarchyLevelDto.setDescription(userHierarchyLevel.getDescription());
                userHierarchyLevelDto.setOrderLevel(userHierarchyLevel.getOrderLevel());
                userHierarchyLevelDtos.add(userHierarchyLevelDto);
            }
        }
        return userHierarchyLevelDtos;
    }
}
