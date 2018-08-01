package org.meveo.api.dto.response.communication;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class MeveoInstancesResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 */
@XmlRootElement(name = "MeveoInstancesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoInstancesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5630363416438814136L;

    /** The meveo instances. */
    @XmlElementWrapper(name = "meveoInstances")
    @XmlElement(name = "meveoInstance")
    private List<MeveoInstanceDto> meveoInstances;

    /**
     * Gets the meveo instances.
     *
     * @return the meveo instances
     */
    public List<MeveoInstanceDto> getMeveoInstances() {
        return meveoInstances;
    }

    /**
     * Sets the meveo instances.
     *
     * @param meveoInstances the new meveo instances
     */
    public void setMeveoInstances(List<MeveoInstanceDto> meveoInstances) {
        this.meveoInstances = meveoInstances;
    }

}
