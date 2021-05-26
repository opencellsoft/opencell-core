package org.meveo.model.worldline.sips.wallet;

public class AddCardRequest extends WalletRequest {
    private String cardNumber;
    private String cardExpiryDate;
    private String paymentMeanAlias;
    private String paymentMeanBrand;
    private String intermediateServiceProviderId;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpiryDate() {
        return cardExpiryDate;
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    public String getPaymentMeanAlias() {
        return paymentMeanAlias;
    }

    public void setPaymentMeanAlias(String paymentMeanAlias) {
        this.paymentMeanAlias = paymentMeanAlias;
    }

    public String getPaymentMeanBrand() {
        return paymentMeanBrand;
    }

    public void setPaymentMeanBrand(String paymentMeanBrand) {
        this.paymentMeanBrand = paymentMeanBrand;
    }

    public String getIntermediateServiceProviderId() {
        return intermediateServiceProviderId;
    }

    public void setIntermediateServiceProviderId(String intermediateServiceProviderId) {
        this.intermediateServiceProviderId = intermediateServiceProviderId;
    }
}