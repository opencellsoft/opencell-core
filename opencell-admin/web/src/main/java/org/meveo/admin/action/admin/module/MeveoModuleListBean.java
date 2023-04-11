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
package org.meveo.admin.action.admin.module;

import java.util.ArrayList;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.util.pagination.EntityListDataModelPF;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.module.MeveoModuleApi;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named

public class MeveoModuleListBean extends MeveoModuleBean {

    private static final long serialVersionUID = 1L;

    @Inject
    private MeveoModuleApi moduleApi;
    
    @Inject
    private MeveoModuleService meveoModuleService;

    private MeveoModuleDto selectedModuleDto;

    private TreeNode selectedModuleItems;

    private EntityListDataModelPF<MeveoModuleDto> moduleDtos = null;

    public EntityListDataModelPF<MeveoModuleDto> getModuleDtos() {
        return moduleDtos;
    }

    public MeveoModuleDto getSelectedModuleDto() {
        return selectedModuleDto;
    }

    public void setSelectedModuleDto(MeveoModuleDto selectedModuleDto) {
        this.selectedModuleDto = selectedModuleDto;
        selectedModuleItems = new DefaultTreeNode("Root");
        if (selectedModuleDto == null) {
            return;
        }

        if (selectedModuleDto.getModuleItems() != null) {
            for (BaseEntityDto item : selectedModuleDto.getModuleItems()) {

                TreeNode classNode = getOrCreateNodeByClass(item.getClass().getSimpleName());
                new DefaultTreeNode("item", item, classNode);
            }
        }
    }

    public TreeNode getSelectedModuleItems() {

        return selectedModuleItems;
    }

    public void loadModulesFromInstance() {
        log.debug("start loadModulesFromInstance {}", meveoInstance.getUrl());
        try {
            moduleDtos = new EntityListDataModelPF<MeveoModuleDto>(new ArrayList<MeveoModuleDto>());
            moduleDtos.addAll(meveoModuleService.downloadModulesFromMeveoInstance(meveoInstance));

        } catch (Exception e) {
            log.error("Error when retrieve modules from {}. Reason {}", meveoInstance.getCode(), e.getMessage(), e);
            messages.error(new BundleKey("messages", "meveoModule.retrieveRemoteMeveoInstanceException"), meveoInstance.getCode(), e.getMessage());
            this.moduleDtos = null;
        }
    }

    public void downloadModule() {
        if (selectedModuleDto != null) {
            try {
                moduleApi.createOrUpdate(selectedModuleDto);
                messages.info(new BundleKey("messages", "meveoModule.downloadSuccess"), selectedModuleDto.getCode());

            } catch (ActionForbiddenException e) {
                if (e.getReason() != null) {
                    messages.error(e.getReason());
                } else {
                    messages.error(new BundleKey("messages", "meveoModule.downloadFailed"), selectedModuleDto.getCode(),
                        (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
                }

            } catch (Exception e) {
                log.error("Failed to download meveo module {} from meveoInstance {}", selectedModuleDto.getCode(), meveoInstance.getCode(), e);
                messages.error(new BundleKey("messages", "meveoModule.downloadFailed"), selectedModuleDto.getCode(),
                    (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
            }
        }
    }

    public void downloadAndInstallModule() {
        if (selectedModuleDto != null) {
            try {
                moduleApi.install(selectedModuleDto);
                messages.info(new BundleKey("messages", "meveoModule.installSuccess"), selectedModuleDto.getCode());

            } catch (ActionForbiddenException e) {
                if (e.getReason() != null) {
                    messages.error(e.getReason());
                } else {
                    messages.error(new BundleKey("messages", "meveoModule.downloadFailed"), selectedModuleDto.getCode(),
                        (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
                }

            } catch (Exception e) {
                log.error("Failed to download and install meveo module {} ", selectedModuleDto.getCode(), e);
                messages.error(new BundleKey("messages", "meveoModule.installFailed"), selectedModuleDto.getCode(),
                    (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
            }
        }
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    private TreeNode getOrCreateNodeByClass(String classname) {

        classname = classname.replaceAll("Dto", "");
        classname = classname.replaceAll("DTO", "");
        for (TreeNode node : selectedModuleItems.getChildren()) {
            if (classname.equals(node.getType())) {
                return node;
            }
        }

        TreeNode node = new DefaultTreeNode(classname, ReflectionUtils.getHumanClassName(classname), selectedModuleItems);
        node.setExpanded(true);
        return node;
    }
}