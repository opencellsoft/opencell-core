package org.meveo.api.dto.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.AccountModelScript;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "AccountModelScript")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountModelScriptDto extends CustomScriptDto {

	private static final long serialVersionUID = 6835117350196983132L;

	public AccountModelScriptDto() {

	}

	public AccountModelScriptDto(AccountModelScript e) {
		super(e.getCode(), e.getDescription(), e.getSourceTypeEnum(), e.getScript());
	}

}
