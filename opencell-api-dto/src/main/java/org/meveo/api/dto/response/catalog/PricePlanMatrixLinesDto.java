package org.meveo.api.dto.response.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "PricePlanMatrixLinesDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixLinesDto extends BaseEntityDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5865367313960284545L;

    @Schema(description = "The price plan matrix code")
	private String pricePlanMatrixCode;

    @Schema(description = "The price plan matrix version")
	private int pricePlanMatrixVersion;
	
	private List<PricePlanMatrixLineDto> pricePlanMatrixLines;



    public void setPricePlanMatrixLineDto(PricePlanMatrixLineDto pricePlanMatrixLineDto) {
        this.pricePlanMatrixLines = List.of(pricePlanMatrixLineDto);
    }

    public List<PricePlanMatrixLineDto> getPricePlanMatrixLines() {
        return pricePlanMatrixLines;
    }

    public void setPricePlanMatrixLines(List<PricePlanMatrixLineDto> pricePlanMatrixLines) {
        this.pricePlanMatrixLines = pricePlanMatrixLines;
    }

	/**
	 * @return the pricePlanMatrixCode
	 */
	public String getPricePlanMatrixCode() {
		return pricePlanMatrixCode;
	}

	/**
	 * @param pricePlanMatrixCode the pricePlanMatrixCode to set
	 */
	public void setPricePlanMatrixCode(String pricePlanMatrixCode) {
		this.pricePlanMatrixCode = pricePlanMatrixCode;
	}

	/**
	 * @return the pricePlanMatrixVersion
	 */
	public int getPricePlanMatrixVersion() {
		return pricePlanMatrixVersion;
	}

	/**
	 * @param pricePlanMatrixVersion the pricePlanMatrixVersion to set
	 */
	public void setPricePlanMatrixVersion(int pricePlanMatrixVersion) {
		this.pricePlanMatrixVersion = pricePlanMatrixVersion;
	}
    
}
