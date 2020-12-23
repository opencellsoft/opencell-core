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

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.toList;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.generic.wf.ActionTypesEnum;
import org.meveo.model.BaseEntity;
import org.meveo.model.generic.wf.Action;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.generic.wf.ActionsService;
import org.meveo.service.generic.wf.GWFTransitionService;
import org.meveo.service.generic.wf.GenericWorkflowService;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Named
@ViewScoped
@ViewBean
public class ActionBean extends BaseBean<Action> {

    private static final long serialVersionUID = 1L;

    @Inject
    private GWFTransitionService gWFTransitionService;

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Inject
    private ActionsService actionsService;

    private List<String> actionTypes = stream(ActionTypesEnum.values()).map(ActionTypesEnum::name).collect(toList());
    private List<String> logLevel = asList("DEBUG", "TRACE", "INFO");
    private List<String> fields = new ArrayList<>();
    private GWFTransition transition;
    private GenericWorkflow genericWorkflow;

    private List<Action> actions = new ArrayList<>();

    public ActionBean() {
        super(Action.class);
    }

    @Override
    protected IPersistenceService<Action> getPersistenceService() {
        return getPersistenceService();
    }

    @Override
    public Action initEntity() {
        super.initEntity();
        String uuid = extractPathParam("transition");
        String wfCode = extractPathParam("wfCode");
        this.genericWorkflow = genericWorkflowService.findByCode(wfCode);
        this.transition = gWFTransitionService.findWFTransitionByUUID(uuid);
        if (uuid != null && wfCode != null) {
            this.fields = fieldsName();
        }
        return entity;
    }

    private String extractPathParam(String parameter) {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();
        return request.getParameter(parameter);
    }

    @Override
    public String back() {
        return "genericWorkflows";
    }

    @ActionMethod
    public void saveAction() throws IOException {
        addAction();
        FacesContext.getCurrentInstance().getExternalContext()
                .redirect("actions.xhtml?wfCode="
                + genericWorkflow.getCode() + "&transition=" + transition.getUuid());
    }

    private void addAction() {
        String uuid = UUID.randomUUID().toString();
        entity.setUuid(uuid);
        entity.setTransition(transition);
        actionsService.create(entity);
        actions.add(actionsService.findWFActionByUUID(uuid));
        transition = gWFTransitionService.refreshOrRetrieve(transition);
        transition.setActions(actions);
        gWFTransitionService.update(transition);
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        try {
            addAction();
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("/opencell/pages/admin/workflow/genericWorkflows.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ActionMethod
    public void deleteAction(Action action) {
        try {
            actionsService.remove(action.getId());
            messages.info(new BundleKey("messages", "delete.successful"));
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("actions.xhtml?wfCode="
                            + genericWorkflow.getCode() + "&transition=" + transition.getUuid());
        } catch (Exception e) {
            log.info("Failed to delete!", e);
            messages.error(new BundleKey("messages", "error.delete.unexpected"));
        }
    }

    @ActionMethod
    public void moveUpAction(Action selectedAction) throws BusinessException {
        Action needUpdate = actionsService.refreshOrRetrieve(selectedAction);
        if (needUpdate.getPriority() > 0) {
            needUpdate.setPriority(needUpdate.getPriority() - 1);
            actionsService.update(needUpdate);
        }
    }

    @ActionMethod
    public void moveDownAction(Action selectedAction) throws BusinessException {
        Action needUpdate = actionsService.refreshOrRetrieve(selectedAction);
        needUpdate.setPriority(needUpdate.getPriority() + 1);
        actionsService.update(needUpdate);
    }

    public Map<String, String> getTypes() {
        Map<String, String> actionTypesMap = new TreeMap<>();
        for (String type : actionTypes) {
            actionTypesMap.put(type, type);
        }
        return actionTypesMap;
    }

    public Map<String, String> getLogLevel() {
        Map<String, String> logLevelMap = new TreeMap<>();
        for (String level : logLevel) {
            logLevelMap.put(level, level);
        }
        return logLevelMap;
    }

    public void setLogLevel(List<String> logLevel) {
        this.logLevel = logLevel;
    }

    public Map<String, String> getFields() {
        Map<String, String> fieldsMap = new TreeMap<>();
        for (String field : fields) {
            fieldsMap.put(field, field);
        }
        return fieldsMap;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    private List<String> fieldsName() {
        try {
            Class<?> current = Class.forName(genericWorkflow.getTargetEntityClass());
            List<Field> fields = getFields(current);
            return fields.stream()
                    .filter(field -> !isStatic(field.getModifiers()))
                    .map(field -> current.getSimpleName() + "." + field.getName())
                    .collect(toList());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return EMPTY_LIST;
    }

    private List<Field> getFields(Class<?> current) {
        List<Field> fields = new ArrayList<>();
        do {
            fields.addAll(asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        } while(current != BaseEntity.class);
        return fields;
    }

    public GWFTransition getTransition() {
        return transition;
    }

    public void setTransition(GWFTransition transition) {
        this.transition = transition;
    }
}