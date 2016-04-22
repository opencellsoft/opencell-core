package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.ModuleDto;
import org.meveo.model.catalog.BusinessServiceModel;

@XmlRootElement(name = "BusinessServiceModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessServiceModelDto extends ModuleDto {

	private static final long serialVersionUID = -7023791262640948222L;

	@NotNull
	@XmlElement(required = true)
	private String serviceTemplateCode;

	private String scriptCode;

	private boolean duplicateService;

	private boolean duplicatePricePlan;
	
	public BusinessServiceModelDto() {
		
	}

	public BusinessServiceModelDto(BusinessServiceModel e) {
		super(e);

		if (e.getServiceTemplate() != null) {
			serviceTemplateCode = e.getServiceTemplate().getCode();
		}
		if (e.getScript() != null) {
			scriptCode = e.getScript().getCode();
		}
		duplicateService = e.isDuplicateService();
		duplicatePricePlan = e.isDuplicatePricePlan();
	}

	public String getServiceTemplateCode() {
		return serviceTemplateCode;
	}

	public void setServiceTemplateCode(String serviceTemplateCode) {
		this.serviceTemplateCode = serviceTemplateCode;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
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

	/**
	 * Convert BusinessServiceModelDto to a BusinessServiceModel instance.
	 * 
	 * @param dto
	 *            BusinessServiceModelDto object to convert
	 * @param bsmToUpdate
	 *            BusinessServiceModel to update with values from dto, or if
	 *            null create a new one
	 * @return A new or updated BusinessServiceModel instance
	 */
	public static BusinessServiceModel fromDTO(BusinessServiceModelDto dto, BusinessServiceModel bsmToUpdate) {
		BusinessServiceModel bsm = new BusinessServiceModel();
		if (bsmToUpdate != null) {
			bsm = bsmToUpdate;
		}
		bsm.setCode(dto.getCode());
		bsm.setDescription(dto.getDescription());
		bsm.setDuplicatePricePlan(dto.isDuplicatePricePlan());
		bsm.setDuplicateService(dto.isDuplicateService());

		return bsm;
	}

	@Override
	public String toString() {
		return "BusinessServiceModelDto [serviceTemplateCode=" + serviceTemplateCode + ", scriptCode=" + scriptCode + ", duplicateService=" + duplicateService
				+ ", duplicatePricePlan=" + duplicatePricePlan + ", toString()=" + super.toString() + "]";
	}

}
