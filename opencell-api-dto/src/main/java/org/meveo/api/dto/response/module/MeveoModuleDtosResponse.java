package org.meveo.api.dto.response.module;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class MeveoModuleDtosResponse.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Edward Legaspi(edward.legaspi@manaty.net)
 **/
@XmlRootElement(name = "MeveoModuleDtosResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoModuleDtosResponse extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The modules. */
    @XmlElementWrapper(name = "modules")
    @XmlElements({ 
                  @XmlElement(name = "businessServiceModel", type = BusinessServiceModelDto.class), 
                  @XmlElement(name = "businessOfferModel", type = BusinessOfferModelDto.class),
                  @XmlElement(name = "businessAccountModel", type = BusinessAccountModelDto.class), 
                  @XmlElement(name = "module", type = MeveoModuleDto.class) })
    private List<MeveoModuleDto> modules;

    /**
     * Instantiates a new meveo module dtos response.
     */
    public MeveoModuleDtosResponse() {
        super();
    }

    /**
     * Gets the modules.
     *
     * @return the modules
     */
    public List<MeveoModuleDto> getModules() {
        if(modules == null) {
            return new ArrayList<>();
        }
        return modules;
    }

    /**
     * Sets the modules.
     *
     * @param modules the new modules
     */
    public void setModules(List<MeveoModuleDto> modules) {
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "MeveoModuleDtosResponse [modules=" + modules + "]";
    }

}