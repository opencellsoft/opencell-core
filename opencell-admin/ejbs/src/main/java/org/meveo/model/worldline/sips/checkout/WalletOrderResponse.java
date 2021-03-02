package org.meveo.model.worldline.sips.checkout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.meveo.model.worldline.sips.BaseResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletOrderResponse extends BaseResponse {
    private String acquirerResponseCode;
    private String authorisationId;
    private String cardScheme;
    private String maskedPan;
    private String responseCode;
    private String transactionDateTime;
    private String transactionPlatform;

    public String getAcquirerResponseCode() {
        return acquirerResponseCode;
    }

    public void setAcquirerResponseCode(String acquirerResponseCode) {
        this.acquirerResponseCode = acquirerResponseCode;
    }

    public String getAuthorisationId() {
        return authorisationId;
    }

    public void setAuthorisationId(String authorisationId) {
        this.authorisationId = authorisationId;
    }

    public String getCardScheme() {
        return cardScheme;
    }

    public void setCardScheme(String cardScheme) {
        this.cardScheme = cardScheme;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(String transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public String getTransactionPlatform() {
        return transactionPlatform;
    }

    public void setTransactionPlatform(String transactionPlatform) {
        this.transactionPlatform = transactionPlatform;
    }
}