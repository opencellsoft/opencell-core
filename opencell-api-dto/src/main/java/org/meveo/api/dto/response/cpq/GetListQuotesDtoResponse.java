package org.meveo.api.dto.response.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.quote.Quote;



/**
 * @author Rachid.AITYAAZZA.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetQuoteDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListQuotesDtoResponse extends BaseResponse{

	/**
	 * Quote data
	 */
	private List<QuoteDTO> quoteDto;
	

	
	public GetListQuotesDtoResponse() {
		super();
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}



	/**
	 * @return the quoteDto
	 */
	public List<QuoteDTO> getQuoteDto() {
		if(quoteDto == null)
			quoteDto = new ArrayList<>();
		return quoteDto;
	}



	/**
	 * @param quoteDto the quoteDto to set
	 */
	public void setQuoteDto(List<QuoteDTO> quoteDto) {
		this.quoteDto = quoteDto;
	}

	
}
