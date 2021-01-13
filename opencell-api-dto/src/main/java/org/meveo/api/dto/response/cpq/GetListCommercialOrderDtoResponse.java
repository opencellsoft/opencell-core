package org.meveo.api.dto.response.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.dto.response.SearchResponse;



/**
 * @author TARIK FA.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetListCommercialOrderDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListCommercialOrderDtoResponse extends SearchResponse{

    @XmlElementWrapper(name = "commercialOrderDtos")
    @XmlElement(name = "commercialOrderDtos")
	private List<CommercialOrderDto> commercialOrderDtos;

	/**
	 * @return the commercialOrderDto
	 */
	public List<CommercialOrderDto> getCommercialOrderDtos() {
		if(commercialOrderDtos == null)
			commercialOrderDtos = new ArrayList<CommercialOrderDto>();
		return commercialOrderDtos;
	}

	/**
	 * @param commercialOrderDto the commercialOrderDto to set
	 */
	public void setCommercialOrderDtos(List<CommercialOrderDto> commercialOrderDto) {
		this.commercialOrderDtos = commercialOrderDto;
	}


	
	
	
}
