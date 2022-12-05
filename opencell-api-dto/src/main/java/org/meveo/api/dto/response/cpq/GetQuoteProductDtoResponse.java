package org.meveo.api.dto.response.cpq;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.cpq.QuoteProductDTO;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteProduct;



/**
 * @author Rachid.AITYAAZZA.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetQuoteProductDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetQuoteProductDtoResponse extends BaseResponse{

	/**
	 * Quote data
	 */
	private QuoteProductDTO quoteProductDTO;
	
	
	public GetQuoteProductDtoResponse() {
		super();
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the quoteProductDTO
	 */
	public QuoteProductDTO getQuoteProductDTO() {
		return quoteProductDTO;
	}

	/**
	 * @param quoteProductDTO the quoteProductDTO to set
	 */
	public void setQuoteProductDTO(QuoteProductDTO quoteProductDTO) {
		this.quoteProductDTO = quoteProductDTO;
	}

	
	
	
}
