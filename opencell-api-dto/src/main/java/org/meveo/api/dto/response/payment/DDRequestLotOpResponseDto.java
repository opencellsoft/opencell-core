package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class DDRequestLotOpResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jul 11, 2016 7:23:52 PM
 */
@XmlRootElement(name = "DDRequestLotOpResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestLotOpResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1633154547155351550L;
    
    /** The ddrequest lot op dto. */
    private DDRequestLotOpDto ddrequestLotOpDto;

    /**
     * Gets the ddrequest lot op dto.
     *
     * @return the ddrequest lot op dto
     */
    public DDRequestLotOpDto getDdrequestLotOpDto() {
        return ddrequestLotOpDto;
    }

    /**
     * Sets the ddrequest lot op dto.
     *
     * @param ddrequestLotOpDto the new ddrequest lot op dto
     */
    public void setDdrequestLotOpDto(DDRequestLotOpDto ddrequestLotOpDto) {
        this.ddrequestLotOpDto = ddrequestLotOpDto;
    }
}
