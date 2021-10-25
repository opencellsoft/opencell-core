package org.meveo.api.dto.cpq;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.DatePeriod;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 
 * @author Mbarek-Ay
 * @version 11.0
 */ 

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "duplicatePricePlanVersionRequestDto")
public class DuplicatePricePlanVersionRequestDto extends BaseEntityDto{
    
     /** The Constant serialVersionUID. */
    protected static final long serialVersionUID = -7824004884683019688L;  
    
    
    @NotNull
    @Schema(description = "The price plan matrix code")
    private String pricePlanMatrixCode;
    
    @NotNull
    @Schema(description = "the current version of price plan matrix")
    protected int pricePlanMatrixVersion;
    
    
    protected DatePeriod validity = new DatePeriod();


	public String getPricePlanMatrixCode() {
		return pricePlanMatrixCode;
	}


	public void setPricePlanMatrixCode(String pricePlanMatrixCode) {
		this.pricePlanMatrixCode = pricePlanMatrixCode;
	}
 
	public int getPricePlanMatrixVersion() {
		return pricePlanMatrixVersion;
	}


	public void setPricePlanMatrixVersion(int pricePlanMatrixVersion) {
		this.pricePlanMatrixVersion = pricePlanMatrixVersion;
	}


	public DatePeriod getValidity() {
		return validity;
	}


	public void setValidity(DatePeriod validity) {
		this.validity = validity;
	}
    
    

	
}