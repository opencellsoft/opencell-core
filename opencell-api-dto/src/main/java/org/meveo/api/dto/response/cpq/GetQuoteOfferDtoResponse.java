package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.quote.Quote;



/**
 * @author Rachid.AITYAAZZA.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetQuoteOfferDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetQuoteOfferDtoResponse extends BaseResponse{

	/**
	 * Quote data
	 */
	private QuoteOfferDTO quoteOfferDto;
	
	
    
    
    public GetQuoteOfferDtoResponse(Quote q) {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		quoteOfferDto = new QuoteOfferDTO();
	}
	
	public GetQuoteOfferDtoResponse() {
		super();
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the quoteOfferDto
	 */
	public QuoteOfferDTO getQuoteOfferDto() {
		return quoteOfferDto;
	}

	/**
	 * @param quoteOfferDto the quoteOfferDto to set
	 */
	public void setQuoteOfferDto(QuoteOfferDTO quoteOfferDto) {
		this.quoteOfferDto = quoteOfferDto;
	}

	
	
	
	
}
