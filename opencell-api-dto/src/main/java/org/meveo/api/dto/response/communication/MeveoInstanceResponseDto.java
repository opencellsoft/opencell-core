package org.meveo.api.dto.response.communication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class MeveoInstanceResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 7:08:28 AM
 */
@XmlRootElement(name = "MeveoInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoInstanceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9151837082569910954L;
    
    /** The meveo instance. */
    private MeveoInstanceDto meveoInstance;

    /**
     * Gets the meveo instance.
     *
     * @return the meveo instance
     */
    public MeveoInstanceDto getMeveoInstance() {
        return meveoInstance;
    }

    /**
     * Sets the meveo instance.
     *
     * @param meveoInstance the new meveo instance
     */
    public void setMeveoInstance(MeveoInstanceDto meveoInstance) {
        this.meveoInstance = meveoInstance;
    }
}
