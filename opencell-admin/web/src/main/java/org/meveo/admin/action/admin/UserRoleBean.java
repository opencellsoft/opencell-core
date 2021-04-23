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

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.AccountBean;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.DetailedSecuredEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link Role} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@Named
@ViewScoped
public class UserRoleBean extends BaseBean<Role> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link Role} service. Extends {@link PersistenceService}. */
    @Inject
    private RoleService userRoleService;
    
    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    @Inject
    @Any
    private Instance<AccountBean<?>> accountBeans;

    @Inject
    @Named
    private SellerBean sellerBean;

    private DualListModel<Permission> permissionsDM;
    private DualListModel<Role> rolesDM;
    private BusinessEntity selectedEntity;
    private String securedEntityType;
    private Map<String, String> securedEntityTypes;
    private Map<String, BaseBean<? extends BusinessEntity>> accountBeanMap;
    private BaseBean<?> selectedAccountBean;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UserRoleBean() {
        super(Role.class);
    }
    
    @Override
    public Role initEntity() {
    	Role entity = super.initEntity();
    	
    	initSelectionOptions();
    	
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
        log.debug("this.securedEntityTypes: {}", this.securedEntityTypes);
        log.debug("this.accountBeanMap: {}", this.accountBeanMap);
        log.debug("initSelectionOptions done.");
    }

    public DualListModel<Permission> getPermissionListModel() {
        if (permissionsDM == null) {
            List<Permission> perksSource = permissionService.list();
            List<Permission> perksTarget = new ArrayList<Permission>();
            if (getEntity().getPermissions() != null) {
                perksTarget.addAll(getEntity().getPermissions());
            }
            perksSource.removeAll(perksTarget);
            permissionsDM = new DualListModel<Permission>(perksSource, perksTarget);
        }
        return permissionsDM;
    }

    public void setPermissionListModel(DualListModel<Permission> perks) {
        this.permissionsDM = perks;
    }

    public DualListModel<Role> getRoleListModel() {
        if (rolesDM == null) {
            List<Role> perksSource = userRoleService.listActive();
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

        // Update permissions
        getEntity().getPermissions().clear();
        getEntity().getPermissions().addAll(permissionService.refreshOrRetrieve(permissionsDM.getTarget()));

        // Update roles
        getEntity().getRoles().clear();
        getEntity().getRoles().addAll(userRoleService.refreshOrRetrieve(rolesDM.getTarget()));

        return super.saveOrUpdate(killConversation);
    }

    /**
     * This will set the correct account bean based on the selected type(Seller, Customer, etc.)
     */
    public void updateSelectedAccountBean() {
        if (!StringUtils.isBlank(getSecuredEntityType())) {
            setSelectedAccountBean(accountBeanMap.get(getSecuredEntityType()));
        }
    }

    /**
     * Returns a list of secured entities.
     */
    public List<DetailedSecuredEntity> getSelectedSecuredEntities() {
        List<DetailedSecuredEntity> detailedSecuredEntities = new ArrayList<>();
        DetailedSecuredEntity detailedSecuredEntity = null;
        BusinessEntity businessEntity = null;
        if (entity != null && entity.getSecuredEntities() != null) {
            for (SecuredEntity securedEntity : entity.getSecuredEntities()) {
                detailedSecuredEntity = new DetailedSecuredEntity(securedEntity);
                businessEntity = securedBusinessEntityService.getEntityByCode(securedEntity.getEntityClass(), securedEntity.getCode());
                detailedSecuredEntity.setDescription(businessEntity.getDescription());
                detailedSecuredEntities.add(detailedSecuredEntity);
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
        for (SecuredEntity securedEntity : entity.getSecuredEntities()) {
            if (securedEntity.equals(selectedSecuredEntity)) {
                entity.getSecuredEntities().remove(selectedSecuredEntity);
                break;
            }
        }
        super.saveOrUpdate(false);
    }
    
    @ActionMethod
    public void enableOrDisable(SecuredEntity selectedSecuredEntity, boolean disable) throws BusinessException {
        for (SecuredEntity securedEntity : entity.getSecuredEntities()) {
            if (securedEntity.equals(selectedSecuredEntity)) {
            	securedEntity.setDisabled(disable);
                break;
            }
        }
        super.saveOrUpdate(false);
    }
    
    /**
     * This will add the selected business entity to the user's securedEntities list.
     * 
     * @param event Faces select event
     * @throws BusinessException General business exception
     */
    @ActionMethod
    public void saveSecuredEntity(SelectEvent event) throws BusinessException {
        log.debug("saveSecuredEntity: {}", this.selectedEntity);
        if (this.selectedEntity != null) {
            List<SecuredEntity> securedEntities = getEntity().getSecuredEntities();
            for (SecuredEntity securedEntity : securedEntities) {
                if (securedEntity.equals(this.selectedEntity)) {
                    messages.warn(new BundleKey("messages", "commons.uniqueField.code"));
                    return;
                }
            }
            getEntity().getSecuredEntities().add(new SecuredEntity(this.selectedEntity));
            super.saveOrUpdate(false);
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
}
