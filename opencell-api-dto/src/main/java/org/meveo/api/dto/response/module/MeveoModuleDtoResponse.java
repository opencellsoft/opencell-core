package org.meveo.api.dto.response.module;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 * 
 **/
@XmlRootElement(name = "MeveoModuleDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoModuleDtoResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;
    private MeveoModuleDto module;

    public MeveoModuleDto getModule() {
        return module;
    }

    public void setModule(MeveoModuleDto module) {
        this.module = module;
    }

    @Override
    public String toString() {
        return "MeveoModuleDtoResponse [module=" + module + "]";
    }

}
