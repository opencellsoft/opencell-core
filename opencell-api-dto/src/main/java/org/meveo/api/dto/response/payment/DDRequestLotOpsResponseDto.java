package org.meveo.api.dto.response.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class DDRequestLotOpsResponseDto.
 *
 * @author TyshanaShi(tyshan@manaty.net)
 */
@XmlRootElement(name = "DDRequestLotOpsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestLotOpsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 317999006133708067L;

    /** The ddrequest lot ops. */
    @XmlElementWrapper(name = "ddrequestLotOps")
    @XmlElement(name = "ddrequestLotOp")
    private List<DDRequestLotOpDto> ddrequestLotOps;

    /**
     * Gets the ddrequest lot ops.
     *
     * @return the ddrequest lot ops
     */
    public List<DDRequestLotOpDto> getDdrequestLotOps() {
        return ddrequestLotOps;
    }

    /**
     * Sets the ddrequest lot ops.
     *
     * @param ddrequestLotOps the new ddrequest lot ops
     */
    public void setDdrequestLotOps(List<DDRequestLotOpDto> ddrequestLotOps) {
        this.ddrequestLotOps = ddrequestLotOps;
    }

}
