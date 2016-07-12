package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jul 11, 2016 7:26:47 PM
 **/
@XmlRootElement(name="DDRequestLotOps")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestLotOpsDto implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3718911071630119539L;
	
	@XmlElement(name="ddrequestLotOp")
	private List<DDRequestLotOpDto> ddrequestLotOps;

	public List<DDRequestLotOpDto> getDdrequestLotOps() {
		if(ddrequestLotOps==null){
			ddrequestLotOps=new ArrayList<DDRequestLotOpDto>();
		}
		return ddrequestLotOps;
	}

	public void setDdrequestLotOps(List<DDRequestLotOpDto> ddrequestLotOps) {
		this.ddrequestLotOps = ddrequestLotOps;
	}
	
}

