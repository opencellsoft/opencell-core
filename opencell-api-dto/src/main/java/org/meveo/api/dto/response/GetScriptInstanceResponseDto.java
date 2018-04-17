package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ScriptInstanceDto;

/**
 * The Class GetScriptInstanceResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetScriptInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetScriptInstanceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 962231443399621051L;

    /** The script instance. */
    private ScriptInstanceDto scriptInstance;

    /**
     * Gets the script instance.
     *
     * @return the script instance
     */
    public ScriptInstanceDto getScriptInstance() {
        return scriptInstance;
    }

    /**
     * Sets the script instance.
     *
     * @param scriptInstance the new script instance
     */
    public void setScriptInstance(ScriptInstanceDto scriptInstance) {
        this.scriptInstance = scriptInstance;
    }
}