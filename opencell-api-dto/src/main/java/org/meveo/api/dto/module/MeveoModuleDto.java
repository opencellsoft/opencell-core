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

package org.meveo.api.dto.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.*;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerAccountsDto;
import org.meveo.api.dto.catalog.*;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.notification.ScriptNotificationDto;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.payment.DDRequestBuilderDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.mapper.ModuleItemListDeserializer;
import org.meveo.model.IEntity;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.ModuleLicenseEnum;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class MeveoModuleDto.
 * 
 * @author andrius
 */
@XmlRootElement(name = "Module")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoModuleDto extends EnableBusinessDto implements IEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The license. */
    @XmlAttribute(required = true)
    private ModuleLicenseEnum license;

    /** The logo picture. */
    private String logoPicture;
    
    /** The logo picture file. */
    private byte[] logoPictureFile;

    /** The script. */
    private ScriptInstanceDto script;

    /** The module items. */
    private ModuleItemsDto moduleItems;

    /**
     * Instantiates a new meveo module dto.
     */
    public MeveoModuleDto() {
    }

    /**
     * Instantiates a new meveo module dto.
     *
     * @param meveoModule the meveo module
     */
    public MeveoModuleDto(MeveoModule meveoModule) {
        super(meveoModule);
        this.license = meveoModule.getLicense();
        this.logoPicture = meveoModule.getLogoPicture();
        this.moduleItems = new ModuleItemsDto(new ArrayList<>());
        if (meveoModule.getScript() != null) {
            this.setScript(new ScriptInstanceDto(meveoModule.getScript()));
        }
    }

    /**
     * Gets the license.
     *
     * @return the license
     */
    public ModuleLicenseEnum getLicense() {
        return license;
    }

    /**
     * Sets the license.
     *
     * @param license the new license
     */
    public void setLicense(ModuleLicenseEnum license) {
        this.license = license;
    }

    /**
     * Gets the logo picture.
     *
     * @return the logo picture
     */
    public String getLogoPicture() {
        return logoPicture;
    }

    /**
     * Sets the logo picture.
     *
     * @param logoPicture the new logo picture
     */
    public void setLogoPicture(String logoPicture) {
        this.logoPicture = logoPicture;
    }

    /**
     * Gets the logo picture file.
     *
     * @return the logo picture file
     */
    public byte[] getLogoPictureFile() {
        return logoPictureFile;
    }

    /**
     * Sets the logo picture file.
     *
     * @param logoPictureFile the new logo picture file
     */
    public void setLogoPictureFile(byte[] logoPictureFile) {
        this.logoPictureFile = logoPictureFile;
    }

    /**
     * Gets the module items.
     *
     * @return the module items
     */
    public ModuleItemsDto getModuleItems() {
        return moduleItems;
    }

    /**
     * Sets the module items.
     *
     * @param moduleItems the new module items
     */
    public void setModuleItems(ModuleItemsDto moduleItems) {
        this.moduleItems = moduleItems;
    }

    /**
     * Adds the module item.
     *
     * @param item the item
     */
    public void addModuleItem(BaseEntityDto item) {
        if (!moduleItems.getModuleItems().contains(item)) {
            moduleItems.getModuleItems().add(item);
        }
    }

    /* (non-Javadoc)
     * @see org.meveo.model.IEntity#isTransient()
     */
    @Override
    public boolean isTransient() {
        return true;
    }

    /**
     * Gets the script.
     *
     * @return the script
     */
    public ScriptInstanceDto getScript() {
        return script;
    }

    /**
     * Sets the script.
     *
     * @param script the new script
     */
    public void setScript(ScriptInstanceDto script) {
        this.script = script;
    }

    /**
     * Checks if is code only.
     *
     * @return true, if is code only
     */
    public boolean isCodeOnly() {
        return StringUtils.isBlank(getDescription()) && license == null && StringUtils.isBlank(logoPicture) && logoPictureFile == null && script == null
                && (moduleItems.getModuleItems() == null || moduleItems.getModuleItems().isEmpty());
    }
    
    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("ModuleDto [code=%s, license=%s, description=%s, logoPicture=%s, logoPictureFile=%s, moduleItems=%s, script=%s]", getCode(), license, getDescription(),
            logoPicture, logoPictureFile, moduleItems != null ? moduleItems.getModuleItems().subList(0, Math.min(moduleItems.getModuleItems().size(), maxLen)) : null, script);
    }
}