package org.meveo.api.dto.response.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.BaseQuoteDTO;
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
public class GetQuoteDtoResponse extends BaseResponse{

	/**
	 * Quote data
	 */
	private BaseQuoteDTO quoteDto;
	private GetQuoteVersionDtoResponse currentVersion;
	
    private List<GetQuoteVersionDtoResponse> allQuoteVersions = new ArrayList<GetQuoteVersionDtoResponse>();
	
    
    
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
	public BaseQuoteDTO getQuoteDto() {
		return quoteDto;
	}

	/**
	 * @param quoteDto the quoteDto to set
	 */
	public void setQuoteDto(BaseQuoteDTO quoteDto) {
		this.quoteDto = quoteDto;
	}

	/**
	 * @return the allQuoteVersions
	 */
	public List<GetQuoteVersionDtoResponse> getAllQuoteVersions() {
		return allQuoteVersions;
	}

	/**
	 * @param allQuoteVersions the allQuoteVersions to set
	 */
	public void setAllQuoteVersions(List<GetQuoteVersionDtoResponse> allQuoteVersions) {
		this.allQuoteVersions = allQuoteVersions;
	}


	
	/**
	 * @param allQuoteVersions the allQuoteVersions to set
	 */
	public void addQuoteVersion(GetQuoteVersionDtoResponse quoteVersion) {
		 if(allQuoteVersions==null) {
			 allQuoteVersions=new ArrayList<GetQuoteVersionDtoResponse>();
		 }
		 allQuoteVersions.add(quoteVersion);
	}

	/**
	 * @return the currentVersion
	 */
	public GetQuoteVersionDtoResponse getCurrentVersion() {
		return currentVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(GetQuoteVersionDtoResponse currentVersion) {
		this.currentVersion = currentVersion;
	}



	
	
	
}
