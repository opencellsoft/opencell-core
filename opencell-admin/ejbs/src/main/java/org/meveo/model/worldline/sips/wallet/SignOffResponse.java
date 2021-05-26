package org.meveo.model.worldline.sips.wallet;

import org.meveo.model.worldline.sips.BaseResponse;

public class SignOffResponse extends BaseResponse {
    private String walletActionDateTime;
    private String walletResponseCode;

    public String getWalletActionDateTime() {
        return walletActionDateTime;
    }

    public void setWalletActionDateTime(String walletActionDateTime) {
        this.walletActionDateTime = walletActionDateTime;
    }

    public String getWalletResponseCode() {
        return walletResponseCode;
    }

    public void setWalletResponseCode(String walletResponseCode) {
        this.walletResponseCode = walletResponseCode;
    }
}