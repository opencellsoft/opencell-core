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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.omnifaces.cdi.Param;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link Role} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@Named
@ViewScoped
public class UserRoleBean extends CustomFieldBean<Role> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link Role} service. Extends {@link PersistenceService}. */
    @Inject
    protected RoleService userRoleService;

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    @Inject
    @Any
    private Instance<AccountBean<?>> accountBeans;

    @Inject
    @Named
    private SellerBean sellerBean;

    /** Role to lookup */
    @Inject
    @Param()
    private String rolename;

    private DualListModel<Role> rolesDM;
    private String securedEntityType;
    private Map<String, String> securedEntityTypes;
    private Map<String, BaseBean<? extends BusinessEntity>> accountBeanMap;
    private BusinessEntity selectedEntity;
    private BaseBean<?> selectedAccountBean;

    private Map<String, String> securedEntityFilter = new HashMap<>();

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UserRoleBean() {
        super(Role.class);
    }

    @PostConstruct
    public void init() {

        initSelectionOptions();
    }

    @Override
    public Role initEntity() {
        if (rolename != null) {
            entity = userRoleService.findByName(rolename, true, true);

        } else {
            entity = new Role();
        }

        return entity;
    }

    /**
     * This will initialize the dropdown values for selecting the entity types (Seller, Customer, etc) and the map of managed beans associated to each entity type.
     */
    private void initSelectionOptions() {
        log.debug("initSelectionOptions...");
        log.debug("this.securedEntityTypes: {}", this.securedEntityTypes);
        log.debug("this.accountBeanMap: {}", this.accountBeanMap);

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

    public DualListModel<Role> getRoleListModel() {
        if (rolesDM == null) {
            List<Role> perksSource = userRoleService.list((PaginationConfiguration) null);
            perksSource.remove(getEntity());
            List<Role> perksTarget = new ArrayList<Role>();
            if (getEntity().getRoles() != null) {
                perksTarget.addAll(getEntity().getRoles());
            }
            perksSource.removeAll(perksTarget);
            rolesDM = new DualListModel<Role>(perksSource, perksTarget);
        }
        return rolesDM;
    }

    public void setRoleListModel(DualListModel<Role> perks) {
        this.rolesDM = perks;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

//        // Update roles
//        getEntity().getRoles().clear();
//        getEntity().getRoles().addAll(rolesDM.getTarget());

        return super.saveOrUpdate(killConversation);
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

    public List<DetailedSecuredEntity> getSelectedSecuredEntities() {

        List<DetailedSecuredEntity> detailedSecuredEntities = new ArrayList<>();
        DetailedSecuredEntity detailedSecuredEntity = null;
        BusinessEntity businessEntity = null;
        if (entity != null) {
            List<SecuredEntity> securedEntities = securedBusinessEntityService.getSecuredEntitiesForRole(rolename);
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

    @ActionMethod
    public void enableOrDisable(SecuredEntity selectedSecuredEntity, boolean disable) throws BusinessException {
        if (disable) {
            securedBusinessEntityService.disable(selectedSecuredEntity.getId());

        } else {
            securedBusinessEntityService.enable(selectedSecuredEntity.getId());
        }
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
            securedBusinessEntityService.addSecuredEntityForRole(securedEntity, rolename);

            messages.info(new BundleKey("messages", "securedEntity.created"));
        }
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Role> getPersistenceService() {
        return userRoleService;
    }

    /**
     * Returns the type of secured entity
     * 
     * @return type of secured entity
     */
    public String getSecuredEntityType() {
        return securedEntityType;
    }

    /**
     * Sets the type of secured entity
     */
    public void setSecuredEntityType(String securedEntityType) {
        this.securedEntityType = securedEntityType;
    }

    /**
     * Returns a map of secured entity types. From seller down to ua
     * 
     * @return map of secured entity types
     */
    public Map<String, String> getSecuredEntityTypes() {
        return securedEntityTypes;
    }

    /**
     * Sets the type of secured entity types
     */
    public void setSecuredEntityTypes(Map<String, String> securedEntityTypes) {
        this.securedEntityTypes = securedEntityTypes;
    }

    /**
     * Returns the list of account beans: sellerBean, userAccountBean, etc
     * 
     * @return list of account bean
     */
    public Map<String, BaseBean<? extends BusinessEntity>> getAccountBeanMap() {
        return accountBeanMap;
    }

    /**
     * Sets the list of account bean
     */
    public void setAccountBeanMap(Map<String, BaseBean<? extends BusinessEntity>> accountBeanMap) {
        this.accountBeanMap = accountBeanMap;
    }

    /**
     * Returns the selected account bean
     * 
     * @return account bean
     */
    public BaseBean<?> getSelectedAccountBean() {
        return selectedAccountBean;
    }

    public void setSelectedAccountBean(BaseBean<?> selectedAccountBean) {
        this.selectedAccountBean = selectedAccountBean;
    }

    /**
     * Returns the selected entity type
     * 
     * @return selectd entity type
     */
    public BusinessEntity getSelectedEntity() {
        return selectedEntity;
    }

    /**
     * Sets the selected entity type
     */
    public void setSelectedEntity(BusinessEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
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