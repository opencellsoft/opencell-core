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
package org.meveo.admin.action.generic.wf;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.Auditable;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.generic.wf.GWFTransitionService;
import org.meveo.service.generic.wf.GenericWorkflowService;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Standard backing bean for {@link GenericWorkflow} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components .
 *
 * @author Amine Ben Aicha
 * @author Mounir Bahije
 * @lastModifiedVersion 7.0
 */

@Named
@ViewScoped
@ViewBean
public class GenericWorkflowBean extends CustomFieldBean<GenericWorkflow> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link GenericWorkflow} service. Extends {@link PersistenceService}.
     */
    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Inject
    private GWFTransitionService gWFTransitionService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    private WFStatus selectedWFStatus;

    private transient GWFTransition gWFTransition = new GWFTransition();

    private boolean editWFStatus = false;

    private boolean showDetailPage = false;

    private Filter selectedFilter;

    public GenericWorkflowBean() {
        super(GenericWorkflow.class);
    }

    @Override
    protected IPersistenceService<GenericWorkflow> getPersistenceService() {
        return genericWorkflowService;
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("transitions");
    }

    @Inject
    private FilterService filterService;

    @Override
    public GenericWorkflow initEntity() {
        super.initEntity();
        selectedFilter = entity.getFilter();
        return entity;
    }

    /**
     * Autocomplete method for class filter field - search entity type classes with @WorkflowedEntity annotation
     *
     * @param query A partial class name (including a package)
     * @return A list of classnames
     */
    public List<String> autocompleteClassNames(String query) {
        List<Class<?>> allWFClass = genericWorkflowService.getAllWorkflowedClazz();
        List<String> classNames = new ArrayList<>();
        for (Class<?> clazz : allWFClass) {
            if (StringUtils.isBlank(query) || clazz.getName().toLowerCase().contains(query.toLowerCase())) {
                classNames.add(clazz.getName());
            }
        }
        Collections.sort(classNames);
        return classNames;
    }


    /**
     * list filters that are linked to targetEntityClass
     * @return A list of filters
     */
    public List<Filter>  listFilterTargetEntity() {
        if (StringUtils.isBlank(this.entity.getTargetEntityClass())) {
            return null;
        }
        List<Filter> allWFFilterClass = filterService.findByPrimaryTargetClass(this.entity.getTargetEntityClass());
        return allWFFilterClass;
    }

    /**
     * Autocomplete method for selecting a custom entity template
     *
     * @param query Partial value entered
     * @return A list of matching values
     */
    public List<String> autocompleteCET(String query) {
        List<String> customEntities = new ArrayList<>();

        List<CustomEntityTemplate> customEntityTemplates = customEntityTemplateService.search(query, false);

        for (CustomEntityTemplate customEntityTemplate : customEntityTemplates) {
            customEntities.add(customEntityTemplate.getCode());
        }

        return customEntities;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        entity.setFilter(selectedFilter);
        super.saveOrUpdate(killConversation);
        return null;
    }

    public Map<String, String> getTransitionStatuses() {
        Map<String, String> statusMap = new TreeMap<>();

        for (WFStatus wfStatus : entity.getStatuses()) {
            statusMap.put(wfStatus.getCode(), wfStatus.getCode());
        }

        return statusMap;
    }

    @ActionMethod
    public void duplicateWfTransition(GWFTransition gWFTransition) {
        try {
            this.gWFTransition = gWFTransitionService.duplicate(gWFTransition, entity);

            // Set max priority +1
            int priority = 1;
            if (entity.getTransitions().size() > 0) {
                for (GWFTransition gWFTransitionInList : entity.getTransitions()) {
                    if (GWFTransitionBean.CATCH_ALL_PRIORITY != gWFTransitionInList.getPriority() && priority <= gWFTransitionInList.getPriority()) {
                        priority = gWFTransitionInList.getPriority() + 1;
                    }
                }
            }

            this.gWFTransition.setPriority(priority);
            editgWFTransition(this.gWFTransition);

        } catch (Exception e) {
            log.error("Failed to duplicate WF transition!", e);
            messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
        }
    }

    @ActionMethod
    public void editgWFTransition(GWFTransition transitionToEdit) {
        this.gWFTransition = transitionToEdit;
        showDetailPage = true;
    }

    @ActionMethod
    public void deleteWfTransition(GWFTransition transitionToDelete) {
        try {
            gWFTransitionService.remove(transitionToDelete.getId());
            entity = genericWorkflowService.refreshOrRetrieve(entity);
            messages.info(new BundleKey("messages", "delete.successful"));
        } catch (Exception e) {
            log.info("Failed to delete!", e);
            messages.error(new BundleKey("messages", "error.delete.unexpected"));
        }
    }

    public void moveUpTransition(GWFTransition selectedWfTransition) throws BusinessException {
        cancelTransitionDetail();

        int index = entity.getTransitions().indexOf(selectedWfTransition);
        if (index > 0) {
            GWFTransition upWfTransition = entity.getTransitions().get(index);
            int priorityUp = upWfTransition.getPriority();
            GWFTransition downWfTransition = entity.getTransitions().get(index - 1);
            GWFTransition needUpdate = gWFTransitionService.refreshOrRetrieve(upWfTransition);
            needUpdate.setPriority(downWfTransition.getPriority());
            gWFTransitionService.update(needUpdate);
            needUpdate = gWFTransitionService.refreshOrRetrieve(downWfTransition);
            needUpdate.setPriority(priorityUp);
            gWFTransitionService.update(needUpdate);
            entity.getTransitions().get(index).setPriority(downWfTransition.getPriority());
            entity.getTransitions().get(index - 1).setPriority(priorityUp);
            Collections.swap(entity.getTransitions(), index, index - 1);
            messages.info(new BundleKey("messages", "update.successful"));
        }
    }

    public void moveDownTransition(GWFTransition selectedWfTransition) throws BusinessException {
        cancelTransitionDetail();

        int index = entity.getTransitions().indexOf(selectedWfTransition);
        if (index < entity.getTransitions().size() - 1) {
            GWFTransition upWfTransition = entity.getTransitions().get(index);
            int priorityUp = upWfTransition.getPriority();
            GWFTransition downWfTransition = entity.getTransitions().get(index + 1);
            GWFTransition needUpdate = gWFTransitionService.findById(upWfTransition.getId(), true);
            needUpdate.setPriority(downWfTransition.getPriority());
            gWFTransitionService.update(needUpdate);
            needUpdate = gWFTransitionService.findById(downWfTransition.getId(), true);
            needUpdate.setPriority(priorityUp);
            gWFTransitionService.update(needUpdate);
            entity.getTransitions().get(index).setPriority(downWfTransition.getPriority());
            entity.getTransitions().get(index + 1).setPriority(priorityUp);
            Collections.swap(entity.getTransitions(), index, index + 1);
            messages.info(new BundleKey("messages", "update.successful"));
        }
    }

    public void cancelTransitionDetail() {
        this.gWFTransition = new GWFTransition();
        showDetailPage = false;
    }

    public void saveWfTransition() throws BusinessException, IOException {
        if (gWFTransition.getId() != null) {
            GWFTransition wfTrs = gWFTransitionService.findById(gWFTransition.getId());
            wfTrs.setFromStatus(gWFTransition.getFromStatus());
            wfTrs.setToStatus(gWFTransition.getToStatus());
            wfTrs.setConditionEl(gWFTransition.getConditionEl());
            wfTrs.setDescription(gWFTransition.getDescription());
            wfTrs.setActionScript(gWFTransition.getActionScript());

            gWFTransitionService.update(wfTrs);

            messages.info(new BundleKey("messages", "update.successful"));
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("/opencell/pages/admin/workflow/actions.xhtml?wfCode="
                            + entity.getCode() + "&transition=" + gWFTransition.getUuid());
        } else {

            gWFTransition.setGenericWorkflow(entity);
            gWFTransitionService.create(gWFTransition);

            entity.getTransitions().add(gWFTransition);
            messages.info(new BundleKey("messages", "save.successful"));
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("/opencell/pages/admin/workflow/actions.xhtml?wfCode=" + entity.getCode()
                            + "&transition=" + gWFTransition.getUuid());
        }

        cancelTransitionDetail();
    }

    public void newTransition() {
        showDetailPage = true;
        List<GWFTransition> wfTransitionList = entity.getTransitions();
        if (CollectionUtils.isNotEmpty(wfTransitionList)) {
            GWFTransition lastWFTransition = wfTransitionList.get(wfTransitionList.size() - 1);
            gWFTransition.setPriority(lastWFTransition.getPriority() + 1);
        } else {
            gWFTransition.setPriority(1);
        }
    }

    public void saveOrUpdateWFStatus() throws BusinessException {
        if (!editWFStatus) {
            if (entity.getStatuses().contains(selectedWFStatus)) {
                messages.error(new BundleKey("messages", "generic.wf.unique"));
                facesContext.validationFailed();
                return;
            }

            if (entity.getStatuses().isEmpty()) {
                entity.setInitStatus(selectedWFStatus.getCode());
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

    public void showTransitionActions(String wfCode, String transitionUuid) throws IOException {
        facesContext.getCurrentInstance().getExternalContext()
                .redirect("/opencell/pages/admin/workflow/actions.xhtml?wfCode=" + wfCode
                        + "&transition=" + transitionUuid);

    }

    public boolean isEditWFStatus() {
        return editWFStatus;
    }

    public void setEditWFStatus(boolean editWFStatus) {
        this.editWFStatus = editWFStatus;
    }

    public GWFTransition getgWFTransition() {
        return gWFTransition;
    }

    public void setgWFTransition(GWFTransition gWFTransition) {
        this.gWFTransition = gWFTransition;
    }

    public boolean isShowDetailPage() {
        return showDetailPage;
    }

    public void setShowDetailPage(boolean showDetailPage) {
        this.showDetailPage = showDetailPage;
    }

    public Filter getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(Filter selectedFilter) {
        this.selectedFilter = selectedFilter;
    }
}