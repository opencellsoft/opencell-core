package org.meveo.model.worldline.sips.checkout;

import org.meveo.model.worldline.sips.BaseRequest;

public class WalletOrderRequest extends BaseRequest {
    private String amount;
    private String currencyCode;
    private String merchantWalletId;
    private String orderChannel;
    private String paymentMeanId;
    private String transactionReference;

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getMerchantWalletId() {
        return merchantWalletId;
    }

    public void setMerchantWalletId(String merchantWalletId) {
        this.merchantWalletId = merchantWalletId;
    }

    public String getOrderChannel() {
        return orderChannel;
    }

    public void setOrderChannel(String orderChannel) {
        this.orderChannel = orderChannel;
    }

    public String getPaymentMeanId() {
        return paymentMeanId;
    }

    public void setPaymentMeanId(String paymentMeanId) {
        this.paymentMeanId = paymentMeanId;
    }
}