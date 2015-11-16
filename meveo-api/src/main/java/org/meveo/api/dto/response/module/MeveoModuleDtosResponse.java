package org.meveo.api.dto.response.module;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
**/
@XmlRootElement(name="MeveoModuleDtosResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoModuleDtosResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ModuleDto> moduleDtoList;

	public MeveoModuleDtosResponse(){
		super();
	}

	public List<ModuleDto> getModuleDtoList() {
		return moduleDtoList;
	}

	public void setModuleDtoList(List<ModuleDto> moduleDtoList) {
		this.moduleDtoList = moduleDtoList;
	}

	@Override
	public String toString() {
		return "MeveoModuleDtosResponse [moduleDtoList=" + moduleDtoList + "]";
	}
	
}
