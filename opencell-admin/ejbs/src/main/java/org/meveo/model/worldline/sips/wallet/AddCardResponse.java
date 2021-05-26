package org.meveo.model.worldline.sips.wallet;

public class AddCardResponse extends WalletResponse {
    private String paymentMeanId;
    private String maskedPan;

    public String getPaymentMeanId() {
        return paymentMeanId;
    }

    public void setPaymentMeanId(String paymentMeanId) {
        this.paymentMeanId = paymentMeanId;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }
}