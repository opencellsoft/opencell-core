package org.meveo.api.dto.account;

import org.meveo.api.dto.module.ModuleDto;
import org.meveo.model.crm.BusinessAccountModel;

/**
 * @author Edward P. Legaspi
 **/
public class BusinessAccountModelDto extends ModuleDto {

	private static final long serialVersionUID = 2264963153183287690L;

	private String scriptCode;
	private String type;

	public BusinessAccountModelDto() {

	}

	public BusinessAccountModelDto(BusinessAccountModel e) {
		setCode(e.getCode());
		setDescription(e.getDescription());
		if (e.getType() != null) {
			setType(e.getType().name());
		}
		if (e.getScript() != null) {
			setScriptCode(e.getScript().getCode());
		}
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
