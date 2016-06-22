package org.meveo.admin.action.crm;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.admin.module.GenericModuleBean;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class BusinessAccountModelBean extends GenericModuleBean<BusinessAccountModel> {

    private static final long serialVersionUID = -3508425903046756219L;

    @Inject
    private BusinessAccountModelService businessAccountModelService;

    public BusinessAccountModelBean() {
        super(BusinessAccountModel.class);
    }

    @Override
    protected IPersistenceService<BusinessAccountModel> getPersistenceService() {
        return businessAccountModelService;
    }
}