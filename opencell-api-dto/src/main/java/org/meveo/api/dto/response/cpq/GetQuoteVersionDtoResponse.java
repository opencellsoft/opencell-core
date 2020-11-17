package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.QuoteVersionDto;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.quote.QuoteVersion;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetProductDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetQuoteVersionDtoResponse extends BaseResponse{

	private QuoteVersionDto quoteVersionDto;
	
    
    
    public GetQuoteVersionDtoResponse(QuoteVersion q) {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		quoteVersionDto = new QuoteVersionDto();
		quoteVersionDto.setBillingPlanCode(q.getBillingPlanCode());
		quoteVersionDto.setCurrentVersion(q.getQuoteVersion());
		quoteVersionDto.setEndDate(q.getEndDate());
		quoteVersionDto.setQuoteCode(q.getQuote().getCode());
		quoteVersionDto.setShortDescription(q.getShortDescription());
		quoteVersionDto.setStartDate(q.getStartDate());
		quoteVersionDto.setStatus(q.getStatus());
	}
	
	public GetQuoteVersionDtoResponse() {
		super();
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	
	
	
}
