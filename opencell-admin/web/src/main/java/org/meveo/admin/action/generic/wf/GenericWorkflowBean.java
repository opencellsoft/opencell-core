/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.generic.wf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.generic.wf.GenericWorkflowService;

/**
 * Standard backing bean for {@link GenericWorkflow} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components .
 */
@Named
@ViewScoped
@ViewBean
public class GenericWorkflowBean extends BaseBean<GenericWorkflow> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link GenericWorkflow} service. Extends {@link PersistenceService}.
     */
    @Inject
    private GenericWorkflowService genericWorkflowService;

    private WFStatus selectedWFStatus;

    private boolean editWFStatus = false;

    @Override
    protected IPersistenceService<GenericWorkflow> getPersistenceService() {
        return genericWorkflowService;
    }

    public GenericWorkflowBean() {
        super(GenericWorkflow.class);
    }

    /**
     * Autocomplete method for class filter field - search entity type classes with @WorkflowedEntity annotation
     * 
     * @param query A partial class name (including a package)
     * @return A list of classnames
     */
    public List<String> autocompleteClassNames(String query) {
        List<Class<?>> allWFClass = genericWorkflowService.getAllWorkflowedClass();
        List<String> classNames = new ArrayList<>();
        for (Class<?> clazz : allWFClass) {
            if (StringUtils.isBlank(query) || clazz.getName().toLowerCase().contains(query.toLowerCase())) {
                classNames.add(clazz.getName());
            }
        }
        Collections.sort(classNames);
        return classNames;
    }

    public void saveOrUpdateWFStatus() throws BusinessException {
        if (!editWFStatus) {
            if (entity.getStatuses().contains(selectedWFStatus)) {
                messages.error(new BundleKey("messages", "generic.wf.unique"));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
            entity.getStatuses().add(selectedWFStatus);
            messages.info(new BundleKey("messages", "generic.wf.saved"));
        } else {
            Auditable auditable = selectedWFStatus.getAuditable();
            auditable.setUpdated(new Date());
            auditable.setUpdater(currentUser.getUserName());
            selectedWFStatus.setAuditable(auditable);
        }

        resetWFStatus();
    }

    public void resetWFStatus() {
        this.selectedWFStatus = null;
        this.editWFStatus = false;
    }

    public void newWFStatus() {
        this.selectedWFStatus = new WFStatus();
        this.selectedWFStatus.setAuditable(new Auditable(currentUser));
        this.selectedWFStatus.setGenericWorkflow(getEntity());
        this.editWFStatus = false;
    }

    public void deleteWFStatus(WFStatus wfStatus) throws BusinessException {
        entity.getStatuses().remove(wfStatus);
        messages.info(new BundleKey("messages", "generic.wf.deleted"));
    }

    public void selectWFStatus(WFStatus wfStatus) {
        this.selectedWFStatus = wfStatus;
        this.editWFStatus = true;
    }

    public WFStatus getSelectedWFStatus() {
        return selectedWFStatus;
    }

    public void setSelectedWFStatus(WFStatus selectedWFStatus) {
        this.selectedWFStatus = selectedWFStatus;
        this.editWFStatus = true;
    }

    public boolean isEditWFStatus() {
        return editWFStatus;
    }

    public void setEditWFStatus(boolean editWFStatus) {
        this.editWFStatus = editWFStatus;
    }
}