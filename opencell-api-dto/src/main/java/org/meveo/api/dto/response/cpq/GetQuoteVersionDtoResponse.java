package org.meveo.api.dto.response.cpq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.cpq.QuoteVersionDto;
import org.meveo.api.dto.cpq.xml.TaxPricesDto;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteVersion;



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
	private List<TaxPricesDto> prices;
	
	private ActionStatus actionStatus = new ActionStatus();
  
	public GetQuoteVersionDtoResponse(QuoteVersion q, boolean loadQuoteOffers, boolean loadQuoteProduct,boolean loadQuoteAttributes,boolean loadOfferAttributes) {
		super(q);
		if (loadQuoteOffers) {
			for (QuoteOffer quoteOffer : q.getQuoteOffers()) {
				quoteItems.add(new QuoteOfferDTO(quoteOffer, loadQuoteProduct, loadQuoteAttributes,loadOfferAttributes, new HashMap<>()));
			}
		}
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}
	public GetQuoteVersionDtoResponse(QuoteVersion q) {
		super(q);
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}
	public GetQuoteVersionDtoResponse(QuoteVersion q, CustomFieldsDto customFieldsDto) {
		this(q);
		this.customFields = customFieldsDto;
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
	/**
	 * @return the prices
	 */
	public List<TaxPricesDto> getPrices() {
		return prices;
	}
	/**
	 * @param prices the prices to set
	 */
	public void setPrices(List<TaxPricesDto> prices) {
		this.prices = prices;
	}


	
	
	
}
