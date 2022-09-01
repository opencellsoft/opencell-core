package org.meveo.api.dto.response.utilities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Imported Data
 * 
 * INTRD-6782
 * 
 * @author are
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportResultDto {

	private String code;

	private String name;

	private String status;

	private List<String> parentCode;

	public ImportResultDto(String code, String name, String status, List<String> parentCode) {
		super();
		this.code = code;
		this.name = name;
		this.status = status;
		this.parentCode = parentCode;
	}

	public ImportResultDto() {
		super();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getParentCode() {
		return parentCode;
	}

	public void setParentCode(List<String> parentCode) {
		this.parentCode = parentCode;
	}

	
}
