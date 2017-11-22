package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.script.CustomScriptDto;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.security.Role;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ScriptInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptInstanceDto extends CustomScriptDto {

    private static final long serialVersionUID = 4555037251902559699L;

    private List<RoleDto> executionRoles = new ArrayList<RoleDto>();
    private List<RoleDto> sourcingRoles = new ArrayList<RoleDto>();

    public ScriptInstanceDto() {
        super();
    }

    public ScriptInstanceDto(ScriptInstance e) {
        super(e.getCode(), e.getDescription(), e.getSourceTypeEnum(), e.getScript());

        if (e.getExecutionRoles() != null) {
            for (Role role : e.getExecutionRoles()) {
                executionRoles.add(new RoleDto(role, true, true));
            }
        }
        if (e.getSourcingRoles() != null) {
            for (Role role : e.getSourcingRoles()) {
                sourcingRoles.add(new RoleDto(role, true, true));
            }
        }
    }

    @Override
    public String toString() {
        return "ScriptInstanceDto [code=" + getCode() + ", description=" + getDescription() + ", type=" + getType() + ", script=" + getScript() + ", executionRoles="
                + executionRoles + ", sourcingRoles=" + sourcingRoles + "]";
    }

    /**
     * @return the executionRoles
     */
    public List<RoleDto> getExecutionRoles() {
        return executionRoles;
    }

    /**
     * @param executionRoles the executionRoles to set
     */
    public void setExecutionRoles(List<RoleDto> executionRoles) {
        this.executionRoles = executionRoles;
    }

    /**
     * @return the sourcingRoles
     */
    public List<RoleDto> getSourcingRoles() {
        return sourcingRoles;
    }

    /**
     * @param sourcingRoles the sourcingRoles to set
     */
    public void setSourcingRoles(List<RoleDto> sourcingRoles) {
        this.sourcingRoles = sourcingRoles;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof ScriptInstanceDto)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        ScriptInstanceDto other = (ScriptInstanceDto) obj;

        if (getCode() == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!getCode().equals(other.getCode())) {
            return false;
        }
        return true;
    }
}