package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.model.module.MeveoModule;

/**
 * The Class BusinessServiceModelDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "BusinessServiceModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessServiceModelDto extends MeveoModuleDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7023791262640948222L;

    /** The service template. */
    @NotNull
    @XmlElement(required = true)
    private ServiceTemplateDto serviceTemplate;

    /** The duplicate service. */
    private boolean duplicateService;

    /** The duplicate price plan. */
    private boolean duplicatePricePlan;

    /**
     * Instantiates a new business service model dto.
     */
    public BusinessServiceModelDto() {
    }

    /**
     * Instantiates a new business service model dto.
     *
     * @param module the module
     */
    public BusinessServiceModelDto(MeveoModule module) {
        super(module);
    }

    /**
     * Sets the service template.
     *
     * @param serviceTemplate the new service template
     */
    public void setServiceTemplate(ServiceTemplateDto serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    /**
     * Gets the service template.
     *
     * @return the service template
     */
    public ServiceTemplateDto getServiceTemplate() {
        return serviceTemplate;
    }

    /**
     * Checks if is duplicate service.
     *
     * @return true, if is duplicate service
     */
    public boolean isDuplicateService() {
        return duplicateService;
    }

    /**
     * Sets the duplicate service.
     *
     * @param duplicateService the new duplicate service
     */
    public void setDuplicateService(boolean duplicateService) {
        this.duplicateService = duplicateService;
    }

    /**
     * Checks if is duplicate price plan.
     *
     * @return true, if is duplicate price plan
     */
    public boolean isDuplicatePricePlan() {
        return duplicatePricePlan;
    }

    /**
     * Sets the duplicate price plan.
     *
     * @param duplicatePricePlan the new duplicate price plan
     */
    public void setDuplicatePricePlan(boolean duplicatePricePlan) {
        this.duplicatePricePlan = duplicatePricePlan;
    }

    @Override
    public String toString() {
        return "BusinessServiceModelDto [serviceTemplate=" + serviceTemplate + ", duplicateService=" + duplicateService + ", duplicatePricePlan=" + duplicatePricePlan
                + ", toString()=" + super.toString() + "]";
    }
}