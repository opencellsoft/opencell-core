package org.meveo.api.dto.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.account.CustomerAccountsDto;
import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessProductModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
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
import org.meveo.api.dto.notification.ScriptNotificationDto;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.payment.DDRequestBuilderDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.mapper.ModuleItemListDeserializer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "ModuleItemsDto")
public class ModuleItemsDto {

    /** The module items. */
    @JsonDeserialize(using = ModuleItemListDeserializer.class)
    @XmlElements({ @XmlElement(name = "customEntityTemplate", type = CustomEntityTemplateDto.class), @XmlElement(name = "customFieldTemplate", type = CustomFieldTemplateDto.class),
            @XmlElement(name = "filter", type = FilterDto.class), @XmlElement(name = "jobInstance", type = JobInstanceDto.class),
            @XmlElement(name = "script", type = ScriptInstanceDto.class), @XmlElement(name = "notification", type = ScriptNotificationDto.class),
            @XmlElement(name = "timerEntity", type = TimerEntityDto.class), @XmlElement(name = "emailNotif", type = EmailNotificationDto.class),
            @XmlElement(name = "jobTrigger", type = JobTriggerDto.class), @XmlElement(name = "webhookNotif", type = WebHookDto.class),
            @XmlElement(name = "counter", type = CounterTemplateDto.class), @XmlElement(name = "businessAccountModel", type = BusinessAccountModelDto.class),
            @XmlElement(name = "businessServiceModel", type = BusinessServiceModelDto.class), @XmlElement(name = "businessProductModel", type = BusinessProductModelDto.class),
            @XmlElement(name = "businessOfferModel", type = BusinessOfferModelDto.class), @XmlElement(name = "subModule", type = MeveoModuleDto.class),
            @XmlElement(name = "measurableQuantity", type = MeasurableQuantityDto.class), @XmlElement(name = "pieChart", type = PieChartDto.class),
            @XmlElement(name = "lineChart", type = LineChartDto.class), @XmlElement(name = "barChart", type = BarChartDto.class),
            @XmlElement(name = "recurringChargeTemplate", type = RecurringChargeTemplateDto.class), @XmlElement(name = "usageChargeTemplate", type = UsageChargeTemplateDto.class),
            @XmlElement(name = "oneShotChargeTemplate", type = OneShotChargeTemplateDto.class), @XmlElement(name = "productChargeTemplate", type = ProductChargeTemplateDto.class),
            @XmlElement(name = "counterTemplate", type = CounterTemplateDto.class), @XmlElement(name = "pricePlanMatrix", type = PricePlanMatrixDto.class),
            @XmlElement(name = "entityCustomAction", type = EntityCustomActionDto.class), @XmlElement(name = "workflow", type = WorkflowDto.class),
            @XmlElement(name = "offerTemplate", type = OfferTemplateDto.class), @XmlElement(name = "productTemplate", type = ProductTemplateDto.class),
            @XmlElement(name = "bundleTemplate", type = BundleTemplateDto.class), @XmlElement(name = "serviceTemplate", type = ServiceTemplateDto.class),
            @XmlElement(name = "offerTemplateCategory", type = OfferTemplateCategoryDto.class), @XmlElement(name = "paymentGateway", type = PaymentGatewayDto.class),
            @XmlElement(name = "ddRequestBuilder", type = DDRequestBuilderDto.class),})
    private List<BaseEntityDto> moduleItems;

    public ModuleItemsDto(List<BaseEntityDto> moduleItems) {
        this.moduleItems = moduleItems;
    }

    public ModuleItemsDto(){}

    /**
     * Gets the moduleItems.
     *
     * @return the moduleItems
     */
    public List<BaseEntityDto> getModuleItems() {
        if (moduleItems == null) {
            moduleItems = new ArrayList<>();
        }

        return moduleItems;
    }

    /**
     * Sets the moduleItems.
     *
     * @param moduleItems the moduleItems
     */
    public void setModuleItems(List<BaseEntityDto> moduleItems) {
        this.moduleItems = moduleItems;
    }

}
