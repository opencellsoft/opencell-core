package org.meveo.api.dto.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.ModuleLicenseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "Module")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoModuleDto extends BaseDataModelDto {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute(required = true)
    private ModuleLicenseEnum license;

    @XmlAttribute
    private String description;

    private String logoPicture;
    private byte[] logoPictureFile;

    private ScriptInstanceDto script;

    @XmlElementWrapper(name = "moduleItems")
    @XmlElements({ @XmlElement(name = "customEntityTemplate", type = CustomEntityTemplateDto.class),
            @XmlElement(name = "customFieldTemplate", type = CustomFieldTemplateDto.class), @XmlElement(name = "filter", type = FilterDto.class),
            @XmlElement(name = "jobInstance", type = JobInstanceDto.class), @XmlElement(name = "script", type = ScriptInstanceDto.class),
            @XmlElement(name = "notification", type = NotificationDto.class), @XmlElement(name = "timerEntity", type = TimerEntityDto.class),
            @XmlElement(name = "emailNotif", type = EmailNotificationDto.class), @XmlElement(name = "jobTrigger", type = JobTriggerDto.class),
            @XmlElement(name = "webhookNotif", type = WebHookDto.class), @XmlElement(name = "counter", type = CounterTemplateDto.class),
            @XmlElement(name = "businessAccountModel", type = BusinessAccountModelDto.class), @XmlElement(name = "businessServiceModel", type = BusinessServiceModelDto.class),
            @XmlElement(name = "businessOfferModel", type = BusinessOfferModelDto.class), @XmlElement(name = "subModule", type = MeveoModuleDto.class),
            @XmlElement(name = "measurableQuantity", type = MeasurableQuantityDto.class), @XmlElement(name = "pieChart", type = PieChartDto.class),
            @XmlElement(name = "lineChart", type = LineChartDto.class), @XmlElement(name = "barChart", type = BarChartDto.class),
            @XmlElement(name = "recurringChargeTemplate", type = RecurringChargeTemplateDto.class), @XmlElement(name = "usageChargeTemplate", type = UsageChargeTemplateDto.class),
            @XmlElement(name = "oneShotChargeTemplate", type = OneShotChargeTemplateDto.class), @XmlElement(name = "productChargeTemplate", type = ProductChargeTemplateDto.class),
            @XmlElement(name = "counterTemplate", type = CounterTemplateDto.class), @XmlElement(name = "pricePlanMatrix", type = PricePlanMatrixDto.class),
            @XmlElement(name = "entityCustomAction", type = EntityCustomActionDto.class), @XmlElement(name = "workflow", type = WorkflowDto.class),
            @XmlElement(name = "offerTemplate", type = OfferTemplateDto.class), @XmlElement(name = "productTemplate", type = ProductTemplateDto.class),
            @XmlElement(name = "bundleTemplate", type = BundleTemplateDto.class), @XmlElement(name = "serviceTemplate", type = ServiceTemplateDto.class) })
    private List<BaseDto> moduleItems;

    public MeveoModuleDto() {
    }

    public MeveoModuleDto(MeveoModule meveoModule) {
        this.code = meveoModule.getCode();
        this.description = meveoModule.getDescription();
        this.license = meveoModule.getLicense();
        this.logoPicture = meveoModule.getLogoPicture();
        this.moduleItems = new ArrayList<BaseDto>();
        if (meveoModule.getScript() != null) {
            this.setScript(new ScriptInstanceDto(meveoModule.getScript()));
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public byte[] getLogoPictureFile() {
        return logoPictureFile;
    }

    public void setLogoPictureFile(byte[] logoPictureFile) {
        this.logoPictureFile = logoPictureFile;
    }

    @Override
    public Serializable getId() {
        return this.code.hashCode();
    }

    @Override
    public void setId(Long id) {

    }

    public List<BaseDto> getModuleItems() {
        return moduleItems;
    }

    public void setModuleItems(List<BaseDto> moduleItems) {
        this.moduleItems = moduleItems;
    }

    @SuppressWarnings("unused")
    public void addModuleItem(BaseDto item) {
        if (item instanceof ScriptInstanceDto) {
            Logger log = LoggerFactory.getLogger(getClass());
        }

        if (!moduleItems.contains(item)) {
            moduleItems.add(item);
        }
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    public ScriptInstanceDto getScript() {
        return script;
    }

    public void setScript(ScriptInstanceDto script) {
        this.script = script;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("ModuleDto [code=%s, license=%s, description=%s, logoPicture=%s, logoPictureFile=%s, moduleItems=%s, script=%s]", code, license, description,
            logoPicture, logoPictureFile, moduleItems != null ? moduleItems.subList(0, Math.min(moduleItems.size(), maxLen)) : null, script);
    }

    public boolean isCodeOnly() {
        return StringUtils.isBlank(description) && license == null && StringUtils.isBlank(logoPicture) && logoPictureFile == null && script == null
                && (moduleItems == null || moduleItems.isEmpty());
    }
}