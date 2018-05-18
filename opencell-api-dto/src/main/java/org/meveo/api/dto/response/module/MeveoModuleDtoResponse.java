package org.meveo.api.dto.response.module;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class MeveoModuleDtoResponse.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 */
@XmlRootElement(name = "MeveoModuleDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoModuleDtoResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The module. */
    private MeveoModuleDto module;

    /**
     * Gets the module.
     *
     * @return the module
     */
    public MeveoModuleDto getModule() {
        return module;
    }

    /**
     * Sets the module.
     *
     * @param module the new module
     */
    public void setModule(MeveoModuleDto module) {
        this.module = module;
    }

    @Override
    public String toString() {
        return "MeveoModuleDtoResponse [module=" + module + "]";
    }
}