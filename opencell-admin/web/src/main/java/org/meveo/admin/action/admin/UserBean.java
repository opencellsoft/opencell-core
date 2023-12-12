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
package org.meveo.admin.action.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.AccountBean;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.security.UserGroup;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.omnifaces.cdi.Param;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

/**
 * Standard backing bean for {@link User} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 *
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Named
@ViewScoped
public class UserBean extends CustomFieldBean<User> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link User} service. Extends {@link PersistenceService}. */
    @Inject
    protected UserService userService;

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    /** User to lookup */
    @Inject
    @Param()
    private String username;

    @Inject
    @Any
    private Instance<AccountBean<?>> accountBeans;

    @Inject
    @Named
    private SellerBean sellerBean;

    @Inject
    private RoleService roleService;

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    private DualListModel<String> rolesDM;
    private TreeNode userGroupRootNode;

    private TreeNode userGroupSelectedNode;
    private String securedEntityType;
    private Map<String, String> securedEntityTypes;
    private Map<String, BaseBean<? extends BusinessEntity>> accountBeanMap;
    private BusinessEntity selectedEntity;
    private BaseBean<?> selectedAccountBean;

    private Map<String, String> securedEntityFilter = new HashMap<>();

    /**
     * Password
     */
    private String password;

    /**
     * Repeated password to check if it matches another entered password and user did not make a mistake.
     */
    private String repeatedPassword;

    /**
     * For showing change password panel
     */
    private boolean showPassword = false;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UserBean() {
        super(User.class);
    }

    @PostConstruct
    public void init() {

        initSelectionOptions();
    }

    @Override
    public User initEntity() {
        if (username != null) {
            entity = userService.findByUsername(username, true, true);

        } else {
            entity = new User();
            entity.setName(new Name());
        }

        return entity;
    }

    /**
     * Get user group selection data model
     * 
     * @return A tree representation of user groups
     */
    public TreeNode getUserGroupRootNode() {
        if (userGroupRootNode == null) {
            userGroupRootNode = new DefaultTreeNode("Root", null);
            List<UserGroup> roots = new ArrayList<UserGroup>(userHierarchyLevelService.list(null));
            if (!roots.isEmpty()) {
                Collections.sort(roots);
                for (UserGroup userGroup : roots) {
                    createTree(userGroup, userGroupRootNode, getEntity().getUserLevel());
                }
            }
        }
        return userGroupRootNode;
    }

    /**
     * Recursive function to create tree with node checked if selected
     * 
     * @param userGroup User group to add
     * @param rootNode A parent node to add to
     * @param selectedUserGroupName A node that should be marked as selected
     * @return A tree representation of user groups
     */
    private TreeNode createTree(UserGroup userGroup, TreeNode rootNode, String selectedUserGroupName) {
        TreeNode newNode = new DefaultTreeNode(userGroup, rootNode);
        newNode.setExpanded(true);
        if (selectedUserGroupName != null && selectedUserGroupName.equals(userGroup.getName())) {
            newNode.setSelected(true);
        }
        if (userGroup.getChildGroups() != null && !userGroup.getChildGroups().isEmpty()) {
            List<UserGroup> childGroups = new ArrayList<UserGroup>(userGroup.getChildGroups());
            Collections.sort(childGroups);
            for (UserGroup childGroup : childGroups) {
                createTree(childGroup, newNode, selectedUserGroupName);
            }
        }
        return newNode;
    }

    public void setUserGroupRootNode(TreeNode rootNode) {
        this.userGroupRootNode = rootNode;
    }

    public TreeNode getUserGroupSelectedNode() {
        return userGroupSelectedNode;
    }

    public void setUserGroupSelectedNode(TreeNode selectedNode) {
        this.userGroupSelectedNode = selectedNode;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        boolean passwordsDoNotMatch = password != null && !password.equals(repeatedPassword);

        if (passwordsDoNotMatch) {
            messages.error(new BundleKey("messages", "save.passwordsDoNotMatch"));
            return null;
        }

        if (!StringUtils.isBlank(password)) {
            entity.setPassword(password);
        }

        if (this.getUserGroupSelectedNode() != null) {
            UserGroup userGroup = (UserGroup) this.getUserGroupSelectedNode().getData();
            getEntity().setUserLevel(userGroup.getName());
        }

        getEntity().getUserRoles().clear();
        getEntity().getUserRoles().addAll(rolesDM.getTarget().stream().map(codeRole -> roleService.findByName(codeRole)).collect(Collectors.toList()));

        return super.saveOrUpdate(killConversation);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<User> getPersistenceService() {
        return userService;
    }

    /**
     * Standard method for custom component with listType="pickList".
     * 
     * @return DualListModel of Role
     */
    public DualListModel<String> getDualListModel() {
        if (rolesDM == null) {
            List<String> perksSource = new ArrayList<String>(roleService.listRoleNames((PaginationConfiguration) null));
            List<String> perksTarget = new ArrayList<String>();
            if (getEntity().getUserRoles() != null) {
                perksTarget.addAll(getEntity().getUserRoles().stream().map(Role::getName).collect(Collectors.toList()));
            }
            perksSource.removeAll(perksTarget);
            rolesDM = new DualListModel<String>(perksSource, perksTarget);
        }
        return rolesDM;
    }

    public void setDualListModel(DualListModel<String> rolesDM) {
        this.rolesDM = rolesDM;
    }

    @Override
    protected String getDefaultSort() {
        return null;// "userName";
    }

    public String getSecuredEntityType() {
        return this.securedEntityType;
    }

    public void setSecuredEntityType(String securedEntityType) {
        this.securedEntityType = securedEntityType;
    }

    public BusinessEntity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(BusinessEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public BaseBean<?> getSelectedAccountBean() {
        return selectedAccountBean;
    }

    public void setSelectedAccountBean(BaseBean<?> selectedAccountBean) {
        this.selectedAccountBean = selectedAccountBean;
    }

    public Map<String, String> getSecuredEntityTypes() {
        return this.securedEntityTypes;
    }

    public void setSecuredEntityTypes(Map<String, String> securedEntityTypes) {
        this.securedEntityTypes = securedEntityTypes;
    }

    public List<DetailedSecuredEntity> getSelectedSecuredEntities() {

        List<DetailedSecuredEntity> detailedSecuredEntities = new ArrayList<>();
        DetailedSecuredEntity detailedSecuredEntity = null;
        BusinessEntity businessEntity = null;
        if (entity != null) {
            List<SecuredEntity> securedEntities = securedBusinessEntityService.getSecuredEntitiesForUser(username);
            if (securedEntities != null) {
                for (SecuredEntity securedEntity : securedEntities) {
                    detailedSecuredEntity = new DetailedSecuredEntity(securedEntity);
                    businessEntity = securedBusinessEntityService.getEntityByCode(securedEntity.getEntityClass(), securedEntity.getEntityCode());
                    if (businessEntity != null) {
                        detailedSecuredEntity.setDescription(businessEntity.getDescription());
                    }
                    detailedSecuredEntities.add(detailedSecuredEntity);
                }
            }
        }
        return detailedSecuredEntities;
    }

    /**
     * This will allow the chosen secured entity to be removed from the user's securedEntities list.
     * 
     * @param selectedSecuredEntity The chosen securedEntity
     * @throws BusinessException General business exception
     */
    @ActionMethod
    public void deleteSecuredEntity(SecuredEntity selectedSecuredEntity) throws BusinessException {
        securedBusinessEntityService.remove(selectedSecuredEntity.getId());
        messages.info(new BundleKey("messages", "securedEntity.deleted"));
    }

    /**
     * This will set the correct account bean based on the selected type(Seller, Customer, etc.)
     */
    public void updateSelectedAccountBean() {
        if (!StringUtils.isBlank(getSecuredEntityType())) {
            setSelectedAccountBean(accountBeanMap.get(getSecuredEntityType()));
        }
        securedEntityFilter.clear();
    }

    /**
     * This will add the selected business entity to the user's securedEntities list.
     * 
     * @param event Faces select event
     * @throws BusinessException General business exception
     */
    @ActionMethod
    public void saveSecuredEntity(SelectEvent event) throws BusinessException {
        if (this.selectedEntity != null) {

            SecuredEntity securedEntity = new SecuredEntity();
            securedEntity.setEntityId(selectedEntity.getId());
            securedEntity.setEntityCode(selectedEntity.getCode());
            securedEntity.setEntityClass(securedEntityType.substring(securedEntityType.lastIndexOf('.') + 1));
            securedBusinessEntityService.addSecuredEntityForUser(securedEntity, username);

            messages.info(new BundleKey("messages", "securedEntity.created"));
        }
    }

    @ActionMethod
    public void enableOrDisable(SecuredEntity selectedSecuredEntity, boolean disable) throws BusinessException {
        if (disable) {
            securedBusinessEntityService.disable(selectedSecuredEntity.getId());

        } else {
            securedBusinessEntityService.enable(selectedSecuredEntity.getId());
        }
    }

    /**
     * This will initialize the dropdown values for selecting the entity types (Seller, Customer, etc) and the map of managed beans associated to each entity type.
     */
    private void initSelectionOptions() {

        if (accountBeanMap == null || accountBeanMap.isEmpty()) {
            accountBeanMap = new HashMap<>();
            securedEntityTypes = new HashMap<>();
            String key = ReflectionUtils.getHumanClassName(sellerBean.getClazz().getSimpleName());
            String value = ReflectionUtils.getCleanClassName(sellerBean.getClazz().getName());
            securedEntityTypes.put(key, value);
            accountBeanMap.put(value, sellerBean);
            for (AccountBean<?> accountBean : accountBeans) {
                key = ReflectionUtils.getHumanClassName(accountBean.getClazz().getSimpleName());
                value = ReflectionUtils.getCleanClassName(accountBean.getClazz().getName());
                securedEntityTypes.put(key, value);
                accountBeanMap.put(value, accountBean);
            }
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }

    public boolean isShowPassword() {
        return showPassword;
    }

    public void setShow(boolean showPassword) {
        this.showPassword = showPassword;
    }

    public void showHidePassword() {
        this.showPassword = !this.showPassword;
    }

    public Map<String, String> getSecuredEntityFilter() {
        return securedEntityFilter;
    }

    public void setSecuredEntityFilter(Map<String, String> securedEntityFilter) {
        this.securedEntityFilter = securedEntityFilter;
    }

    public void filterAccounts() {
        selectedAccountBean.getFilters().clear();
        selectedAccountBean.getFilters().putAll(securedEntityFilter);
        selectedAccountBean.search();
    }

    public void cleanAccountsFilter() {
        securedEntityFilter.clear();
        selectedAccountBean.getFilters().clear();
    }
}