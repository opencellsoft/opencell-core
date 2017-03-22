package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.model.module.MeveoModule;

@XmlRootElement(name = "BusinessServiceModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessServiceModelDto extends MeveoModuleDto {

    private static final long serialVersionUID = -7023791262640948222L;

    @NotNull
    @XmlElement(required = true)
    private ServiceTemplateDto serviceTemplate;

    private boolean duplicateService;

    private boolean duplicatePricePlan;

    public BusinessServiceModelDto() {
    }

    public BusinessServiceModelDto(MeveoModule module) {
        super(module);
    }

    public void setServiceTemplate(ServiceTemplateDto serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    public ServiceTemplateDto getServiceTemplate() {
        return serviceTemplate;
    }

    public boolean isDuplicateService() {
        return duplicateService;
    }

    public void setDuplicateService(boolean duplicateService) {
        this.duplicateService = duplicateService;
    }

    public boolean isDuplicatePricePlan() {
        return duplicatePricePlan;
    }

    public void setDuplicatePricePlan(boolean duplicatePricePlan) {
        this.duplicatePricePlan = duplicatePricePlan;
    }

    @Override
    public String toString() {
        return "BusinessServiceModelDto [serviceTemplate=" + serviceTemplate + ", duplicateService=" + duplicateService + ", duplicatePricePlan=" + duplicatePricePlan
                + ", toString()=" + super.toString() + "]";
    }

}
