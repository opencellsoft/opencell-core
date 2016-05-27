package org.meveo.api.dto.account;

import org.meveo.api.dto.module.ModuleDto;
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.module.MeveoModule;

/**
 * @author Edward P. Legaspi
 **/
public class BusinessAccountModelDto extends ModuleDto {

    private static final long serialVersionUID = 2264963153183287690L;

    private AccountHierarchyTypeEnum type;

    public BusinessAccountModelDto() {

    }

    public BusinessAccountModelDto(MeveoModule module) {
        super(module);
    }

    public AccountHierarchyTypeEnum getType() {
        return type;
    }

    public void setType(AccountHierarchyTypeEnum type) {
        this.type = type;
    }

}
