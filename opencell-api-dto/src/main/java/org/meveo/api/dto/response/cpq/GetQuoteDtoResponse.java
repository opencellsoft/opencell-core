package org.meveo.api.dto.response.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.quote.Quote;



/**
 * @author Rachid.AITYAAZZA.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetQuoteDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetQuoteDtoResponse extends BaseResponse{

	/**
	 * Quote data
	 */
	private QuoteDTO quoteDto;
	
	   
    private List<QuoteOfferDTO> quoteItems = new ArrayList<QuoteOfferDTO>();
	
    
    
    public GetQuoteDtoResponse(Quote q) {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		quoteDto = new QuoteDTO();
	}
	
	public GetQuoteDtoResponse() {
		super();
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the quoteDto
	 */
	public QuoteDTO getQuoteDto() {
		return quoteDto;
	}

	/**
	 * @param quoteDto the quoteDto to set
	 */
	public void setQuoteDto(QuoteDTO quoteDto) {
		this.quoteDto = quoteDto;
	}

	/**
	 * @return the quoteItems
	 */
	public List<QuoteOfferDTO> getQuoteItems() {
		return quoteItems;
	}

	/**
	 * @param quoteItems the quoteItems to set
	 */
	public void setQuoteItems(List<QuoteOfferDTO> quoteItems) {
		this.quoteItems = quoteItems;
	}
	
	

	
	
	
}
