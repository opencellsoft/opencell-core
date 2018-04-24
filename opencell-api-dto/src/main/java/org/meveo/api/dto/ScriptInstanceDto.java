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
 * The Class ScriptInstanceDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ScriptInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptInstanceDto extends CustomScriptDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4555037251902559699L;

    /** The execution roles. */
    private List<RoleDto> executionRoles = new ArrayList<RoleDto>();
    
    /** The sourcing roles. */
    private List<RoleDto> sourcingRoles = new ArrayList<RoleDto>();

    /**
     * Instantiates a new script instance dto.
     */
    public ScriptInstanceDto() {
        super();
    }

    /**
     * Instantiates a new script instance dto.
     *
     * @param scriptInstance the ScriptInstance entity
     */
    public ScriptInstanceDto(ScriptInstance scriptInstance) {
        super(scriptInstance.getCode(), scriptInstance.getDescription(), scriptInstance.getSourceTypeEnum(), scriptInstance.getScript());

        if (scriptInstance.getExecutionRoles() != null) {
            for (Role role : scriptInstance.getExecutionRoles()) {
                executionRoles.add(new RoleDto(role, true, true));
            }
        }
        if (scriptInstance.getSourcingRoles() != null) {
            for (Role role : scriptInstance.getSourcingRoles()) {
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
     * Gets the execution roles.
     *
     * @return the executionRoles
     */
    public List<RoleDto> getExecutionRoles() {
        return executionRoles;
    }

    /**
     * Sets the execution roles.
     *
     * @param executionRoles the executionRoles to set
     */
    public void setExecutionRoles(List<RoleDto> executionRoles) {
        this.executionRoles = executionRoles;
    }

    /**
     * Gets the sourcing roles.
     *
     * @return the sourcingRoles
     */
    public List<RoleDto> getSourcingRoles() {
        return sourcingRoles;
    }

    /**
     * Sets the sourcing roles.
     *
     * @param sourcingRoles the sourcingRoles to set
     */
    public void setSourcingRoles(List<RoleDto> sourcingRoles) {
        this.sourcingRoles = sourcingRoles;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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