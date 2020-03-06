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

package org.meveo.model.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;
import org.meveo.model.scripts.ScriptInstance;

/**
 * Opencell data/configuration deployment module contains Custom entity, Custom field, Charge, Counter, Service templates, Offer template categories, Product offerings, Price
 * plans, Custom entity actions, Charts, Scripts, Filters, Jobs, Notifications, Timers, Workflow and others.
 * 
 * Module can be exported to another Opencell instance.
 * 
 * @author Tyshan Shi(tyshanchn@manaty.net)
 */
@Entity
@ObservableEntity
@Cacheable
@ModuleItem
@ExportIdentifier({ "code" })
@Table(name = "meveo_module", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "meveo_module_seq"), })
@Inheritance(strategy = InheritanceType.JOINED)
public class MeveoModule extends EnableBusinessEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Module items making up the module
     */
    @OneToMany(mappedBy = "meveoModule", cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MeveoModuleItem> moduleItems = new ArrayList<>();

    /**
     * Licensing terms
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "module_license", nullable = false)
    @NotNull
    private ModuleLicenseEnum license = ModuleLicenseEnum.GPL;

    /**
     * Module image/logo
     */
    @Column(name = "logo_picture", length = 255)
    @Size(max = 255)
    private String logoPicture;

    /**
     * Is module installed
     */
    @Type(type = "numeric_boolean")
    @Column(name = "installed")
    private boolean installed;

    /**
     * Module source in serialized XML fromat
     */
    @Column(name = "module_source", nullable = false, columnDefinition = "TEXT")
    private String moduleSource;

    /**
     * Script of type ModuleScript to execute at module installation or deinstallation
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance script;

    public List<MeveoModuleItem> getModuleItems() {
        return moduleItems;
    }

    public void setModuleItems(List<MeveoModuleItem> moduleItems) {
        this.moduleItems = moduleItems;
    }

    public void addModuleItem(MeveoModuleItem moduleItem) {
        this.moduleItems.add(moduleItem);
        moduleItem.setMeveoModule(this);
    }

    public void removeItem(MeveoModuleItem item) {
        this.moduleItems.remove(item);
        item.setMeveoModule(null);
    }

    public ModuleLicenseEnum getLicense() {
        return license;
    }

    public void setLicense(ModuleLicenseEnum license) {
        this.license = license;
    }

    public String getLogoPicture() {
        return logoPicture;
    }

    public void setLogoPicture(String logoPicture) {
        this.logoPicture = logoPicture;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public void setScript(ScriptInstance script) {
        this.script = script;
    }

    public ScriptInstance getScript() {
        return script;
    }

    public void setModuleSource(String moduleSource) {
        this.moduleSource = moduleSource;
    }

    public String getModuleSource() {
        return moduleSource;
    }

    public boolean isDownloaded() {
        return !StringUtils.isBlank(moduleSource);
    }
}