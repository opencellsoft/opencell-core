package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BankingDateStatusDto;

/**
 * The Class BankingDateStatusResponse.
 *
 * @author hznibar
 */
@XmlRootElement(name = "BankingDateStatusResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BankingDateStatusResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2550428385118895687L;

    /** The calendar. */
    private BankingDateStatusDto bankingDateStatus;
    
       

    /**
     * Gets the banking date status.
     *
     * @return the banking date status
     */
    public BankingDateStatusDto getBankingDateStatus() {
        return bankingDateStatus;
    }



    /**
     * Sets the banking date status.
     *
     * @param bankingDateStatus the new banking date status
     */
    public void setBankingDateStatus(BankingDateStatusDto bankingDateStatus) {
        this.bankingDateStatus = bankingDateStatus;
    }





    @Override
    public String toString() {
        return "BankingDateStatusResponse [bankingDateStatus=" + bankingDateStatus + ", toString()=" + super.toString() + "]";
    }

}
