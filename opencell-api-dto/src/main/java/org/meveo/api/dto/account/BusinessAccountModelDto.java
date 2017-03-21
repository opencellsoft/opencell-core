package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.module.MeveoModule;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BusinessAccountModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessAccountModelDto extends MeveoModuleDto {

    private static final long serialVersionUID = 2264963153183287690L;

    private AccountHierarchyTypeEnum hierarchyType;

    public BusinessAccountModelDto() {

    }

    public BusinessAccountModelDto(MeveoModule module) {
        super(module);
        if(module instanceof BusinessAccountModel){
            BusinessAccountModel businessAccountModel = (BusinessAccountModel) module;
            this.hierarchyType = businessAccountModel.getHierarchyType();
        }
    }

    public AccountHierarchyTypeEnum getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(AccountHierarchyTypeEnum hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

}
