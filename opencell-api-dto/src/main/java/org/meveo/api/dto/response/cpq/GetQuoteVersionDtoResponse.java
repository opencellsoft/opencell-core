package org.meveo.api.dto.response.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.PriceDTO;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.cpq.QuoteVersionDto;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteVersion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetProductDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetQuoteVersionDtoResponse extends QuoteVersionDto{

	
	private List<QuoteOfferDTO> quoteItems = new ArrayList<QuoteOfferDTO>();

	/**
	 * List of quote prices
	 */
	private List<PriceDTO> prices;
	
	private ActionStatus actionStatus = new ActionStatus();
  
	public GetQuoteVersionDtoResponse(QuoteVersion q, boolean loadQuoteOffers, boolean loadQuoteProduct,
			boolean loadQuoteAttributes) {
		super(q);
		if (loadQuoteOffers) {
			for (QuoteOffer quoteOffer : q.getQuoteOffers()) {
				quoteItems.add(new QuoteOfferDTO(quoteOffer, loadQuoteProduct, loadQuoteAttributes));
			}
		}

	}
	
	public GetQuoteVersionDtoResponse() {
		super();
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the actionStatus
	 */
	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	/**
	 * @param actionStatus the actionStatus to set
	 */
	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}


	
	
	
}
