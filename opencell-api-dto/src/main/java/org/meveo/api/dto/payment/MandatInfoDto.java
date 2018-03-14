/**
 * 
 */
package org.meveo.api.dto.payment;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.payments.MandatStateEnum;

/**
 * @author anasseh
 *
 */

@XmlRootElement(name = "MandatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MandatInfoDto extends BaseResponse {
    /**
     * 
     */
    private static final long serialVersionUID = -8669878897612306520L;
    private String id;
    private String reference;
    private MandatStateEnum state;
    private String standard;
    private int initialScore;
    private Date dateCreated;
    private Date dateSigned;
    private String paymentScheme;
    private String bic;
    private String iban;
    private String bankName;


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @return the state
     */
    public MandatStateEnum getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(MandatStateEnum state) {
        this.state = state;
    }

    /**
     * @return the standard
     */
    public String getStandard() {
        return standard;
    }

    /**
     * @param standard the standard to set
     */
    public void setStandard(String standard) {
        this.standard = standard;
    }

    /**
     * @return the initialScore
     */
    public int getInitialScore() {
        return initialScore;
    }

    /**
     * @param initialScore the initialScore to set
     */
    public void setInitialScore(int initialScore) {
        this.initialScore = initialScore;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the dateSigned
     */
    public Date getDateSigned() {
        return dateSigned;
    }

    /**
     * @param dateSigned the dateSigned to set
     */
    public void setDateSigned(Date dateSigned) {
        this.dateSigned = dateSigned;
    }

    /**
     * @return the paymentScheme
     */
    public String getPaymentScheme() {
        return paymentScheme;
    }

    /**
     * @param paymentScheme the paymentScheme to set
     */
    public void setPaymentScheme(String paymentScheme) {
        this.paymentScheme = paymentScheme;
    }
    

    /**
     * @return the bic
     */
    public String getBic() {
        return bic;
    }

    /**
     * @param bic the bic to set
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * @return the iban
     */
    public String getIban() {
        return iban;
    }

    /**
     * @param iban the iban to set
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    
    /**
     * @return the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * @param bankName the bankName to set
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return "MandatInfoDto [id=" + id + ", reference=" + reference + ", state=" + state + ", standard=" + standard + ", initialScore=" + initialScore + ", dateCreated="
                + dateCreated + ", dateSigned=" + dateSigned + ", paymentScheme=" + paymentScheme + ", bic=" + bic + ", iban=" + iban + ", bankName=" + bankName + "]";
    }
}
