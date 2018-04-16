package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.module.MeveoModule;

/**
 * The Class BusinessAccountModelDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "BusinessAccountModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessAccountModelDto extends MeveoModuleDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2264963153183287690L;

    /** The hierarchy type. */
    private AccountHierarchyTypeEnum hierarchyType;

    /**
     * Instantiates a new business account model dto.
     */
    public BusinessAccountModelDto() {

    }

    /**
     * Instantiates a new business account model dto.
     *
     * @param meveoModule the MeveoModule entity
     */
    public BusinessAccountModelDto(MeveoModule meveoModule) {
        super(meveoModule);
        if (meveoModule instanceof BusinessAccountModel) {
            BusinessAccountModel businessAccountModel = (BusinessAccountModel) meveoModule;
            this.hierarchyType = businessAccountModel.getHierarchyType();
        }
    }

    /**
     * Gets the hierarchy type.
     *
     * @return the hierarchy type
     */
    public AccountHierarchyTypeEnum getHierarchyType() {
        return hierarchyType;
    }

    /**
     * Sets the hierarchy type.
     *
     * @param hierarchyType the new hierarchy type
     */
    public void setHierarchyType(AccountHierarchyTypeEnum hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

}
